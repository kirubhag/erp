package krs.erp.service.library;

import krs.erp.model.User;
import krs.erp.model.finance.StudentFineLedger;
import krs.erp.model.library.*;
import krs.erp.repository.UserRepository;
import krs.erp.repository.finance.StudentFineLedgerRepository;
import krs.erp.repository.library.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LibraryService {

    private final Logger logger = LoggerFactory.getLogger(LibraryService.class);

    @Autowired
    private LibraryResourceRepository resourceRepository;

    @Autowired
    private ResourceItemRepository itemRepository;

    @Autowired
    private LibraryLoanRepository loanRepository;

    @Autowired
    private LibraryHoldRepository holdRepository;

    @Autowired
    private LibraryPolicyRepository policyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentFineLedgerRepository fineLedgerRepository;

    @Transactional
    public LibraryLoan checkOut(Long itemId, Long userId) {
        if (itemId == null || userId == null) {
            throw new IllegalArgumentException("Item ID and User ID must not be null");
        }
        ResourceItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (item.getStatus() != ResourceItem.ItemStatus.AVAILABLE) {
            throw new RuntimeException("Item is not available for checkout. Current status: " + item.getStatus());
        }

        // Validate Policy
        // Validate Policy
        krs.erp.model.Role userRole = user.getRoles().isEmpty() ? null : user.getRoles().iterator().next();
        if (userRole == null) {
            throw new RuntimeException("User has no assigned roles.");
        }

        LibraryPolicy policy = policyRepository.findByRoleAndGradeLevel(userRole, null)
                .orElseGet(() -> policyRepository.findByRole(userRole)
                        .orElseThrow(() -> new RuntimeException(
                                "No library policy found for role: " + userRole.getName())));

        List<LibraryLoan> activeLoans = loanRepository.findByUserAndStatus(user, LibraryLoan.LoanStatus.ACTIVE);
        if (activeLoans.size() >= policy.getMaxBooks()) {
            throw new RuntimeException("User has reached the maximum allowed books: " + policy.getMaxBooks());
        }

        // Check for holds
        List<LibraryHold> holds = holdRepository.findByResourceAndStatusOrderByRequestDateAsc(item.getResource(),
                LibraryHold.HoldStatus.WAITING);
        if (!holds.isEmpty() && !holds.get(0).getUser().getId().equals(userId)) {
            throw new RuntimeException("Item is reserved for another user.");
        }

        // Perform Checkout
        item.setStatus(ResourceItem.ItemStatus.LOANED);
        itemRepository.save(item);

        LibraryLoan loan = new LibraryLoan();
        loan.setItem(item);
        loan.setUser(user);
        loan.setLoanDate(LocalDateTime.now());
        loan.setDueDate(LocalDateTime.now().plusDays(policy.getLoanPeriodDays()));
        loan.setStatus(LibraryLoan.LoanStatus.ACTIVE);

        // Fulfill hold if applicable
        if (!holds.isEmpty() && holds.get(0).getUser().getId().equals(userId)) {
            LibraryHold hold = holds.get(0);
            hold.setStatus(LibraryHold.HoldStatus.FULFILLED);
            holdRepository.save(hold);
        }

        return loanRepository.save(loan);
    }

    @Transactional
    public LibraryLoan checkIn(Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID must not be null");
        }
        ResourceItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        LibraryLoan loan = loanRepository.findByItemAndStatus(item, LibraryLoan.LoanStatus.ACTIVE).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active loan found for this item"));

        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus(LibraryLoan.LoanStatus.RETURNED);

        // Calculate and post Fines
        if (LocalDateTime.now().isAfter(loan.getDueDate())) {
            calculateAndPostFine(loan);
        }

        // Update item status
        List<LibraryHold> pendingHolds = holdRepository.findByResourceAndStatusOrderByRequestDateAsc(item.getResource(),
                LibraryHold.HoldStatus.WAITING);
        if (!pendingHolds.isEmpty()) {
            item.setStatus(ResourceItem.ItemStatus.RESERVED);
            logger.info("Item {} is now RESERVED for user {}", item.getAccessionNumber(),
                    pendingHolds.get(0).getUser().getUsername());
        } else {
            item.setStatus(ResourceItem.ItemStatus.AVAILABLE);
        }
        itemRepository.save(item);

        return loanRepository.save(loan);
    }

    private void calculateAndPostFine(LibraryLoan loan) {
        User user = loan.getUser();
        krs.erp.model.Role userRole = user.getRoles().isEmpty() ? null : user.getRoles().iterator().next();
        if (userRole == null)
            return;

        LibraryPolicy policy = policyRepository.findByRoleAndGradeLevel(userRole, null)
                .orElseGet(() -> policyRepository.findByRole(userRole).orElse(null));

        if (policy == null || policy.getFinePerDay().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        long daysOverdue = Duration.between(loan.getDueDate(), LocalDateTime.now()).toDays();
        if (daysOverdue > 0) {
            BigDecimal fineAmount = policy.getFinePerDay().multiply(BigDecimal.valueOf(daysOverdue));

            StudentFineLedger fine = new StudentFineLedger();
            fine.setStudentId(user.getId());
            fine.setFineConfigId(0L); // System/Library default
            fine.setBaseAmount(fineAmount.doubleValue());
            fine.setAccruedAmount(fineAmount.doubleValue());
            fine.setStatus(StudentFineLedger.FineStatus.PENDING);
            fine.setIssuedAt(LocalDateTime.now());
            fineLedgerRepository.save(fine);

            logger.info("Posted library fine of {} for user {} (Loan ID: {})", fineAmount, user.getUsername(),
                    loan.getId());
        }
    }

    @Transactional
    public ResourceItem stockAudit(String barcode, String shelfLocation) {
        ResourceItem item = itemRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Item not found with barcode: " + barcode));

        if (item.getLocation().equalsIgnoreCase(shelfLocation)) {
            item.setAuditStatus(ResourceItem.AuditStatus.MATCHED);
        } else {
            item.setAuditStatus(ResourceItem.AuditStatus.MISPLACED);
            logger.warn("Item {} misplaced. Expected: {}, Found at: {}", item.getAccessionNumber(), item.getLocation(),
                    shelfLocation);
        }

        return itemRepository.save(item);
    }

    public Map<String, String> fetchMetadataByISBN(String isbn) {
        // Mocking an external API call like Open Library
        Map<String, String> metadata = new HashMap<>();
        if ("9780132350884".equals(isbn)) {
            metadata.put("title", "Clean Code");
            metadata.put("author", "Robert C. Martin");
            metadata.put("publisher", "Prentice Hall");
            metadata.put("year", "2008");
        } else {
            metadata.put("title", "Unknown Book");
            metadata.put("isbn", isbn);
        }
        return metadata;
    }
}
