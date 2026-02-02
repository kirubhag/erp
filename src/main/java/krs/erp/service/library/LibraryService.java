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

    @Autowired
    private LibraryFineRuleRepository fineRuleRepository;

    @Autowired
    private GoogleBooksService googleBooksService;

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

    public Map<String, Object> fetchMetadataByISBN(String isbn) {
        return googleBooksService.getBookDetailsByIsbn(isbn)
                .map(details -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", details.getTitle());
                    map.put("author",
                            details.getAuthors() != null && !details.getAuthors().isEmpty()
                                    ? String.join(", ", details.getAuthors())
                                    : "Unknown");
                    map.put("publisher", details.getPublisher());
                    map.put("year", details.getPublishedDate());
                    map.put("description", details.getDescription());
                    map.put("thumbnail", details.getThumbnailUrl());
                    return map;
                })
                .orElseGet(() -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("isbn", isbn);
                    map.put("error", "Book not found");
                    return map;
                });
    }

    public krs.erp.dto.library.LibraryDashboardDTO getDashboardStats() {
        krs.erp.dto.library.LibraryDashboardDTO stats = new krs.erp.dto.library.LibraryDashboardDTO();

        long totalItems = itemRepository.count();
        long itemsOnLoan = loanRepository.countByStatus(LibraryLoan.LoanStatus.ACTIVE);
        long itemsOverdue = loanRepository.countByStatus(LibraryLoan.LoanStatus.OVERDUE); // Make sure OVERDUE status is
                                                                                          // managed or calculated via
                                                                                          // query

        stats.setTotalInventory(totalItems);
        stats.setTotalBooksOut(itemsOnLoan);
        stats.setTotalBooksIn(totalItems - itemsOnLoan);
        stats.setOverdueCount(itemsOverdue);

        if (totalItems > 0) {
            stats.setCirculationRate((double) itemsOnLoan / totalItems * 100);
        }

        // Trending Books - Top borrowed books
        List<Object[]> topBorrowedResults = loanRepository.findTopBorrowedBooks();
        List<krs.erp.dto.library.LibraryDashboardDTO.TopBorrowedBook> trendingBooks = new java.util.ArrayList<>();
        int maxTrending = Math.min(5, topBorrowedResults.size());
        for (int i = 0; i < maxTrending; i++) {
            Object[] row = topBorrowedResults.get(i);
            krs.erp.dto.library.LibraryDashboardDTO.TopBorrowedBook book = new krs.erp.dto.library.LibraryDashboardDTO.TopBorrowedBook();
            book.setTitle((String) row[1]);
            book.setBorrowCount(((Number) row[2]).intValue());
            trendingBooks.add(book);
        }
        stats.setTrendingBooks(trendingBooks);

        // Overdue Leaders - Users with most overdue items
        List<Object[]> overdueLeaderResults = loanRepository.findOverdueLeaders();
        List<krs.erp.dto.library.LibraryDashboardDTO.OverdueUser> overdueLeaders = new java.util.ArrayList<>();
        int maxOverdue = Math.min(5, overdueLeaderResults.size());
        for (int i = 0; i < maxOverdue; i++) {
            Object[] row = overdueLeaderResults.get(i);
            krs.erp.dto.library.LibraryDashboardDTO.OverdueUser user = new krs.erp.dto.library.LibraryDashboardDTO.OverdueUser();
            user.setUserId((Long) row[0]);
            String firstName = (String) row[1];
            String lastName = (String) row[2];
            user.setName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
            user.setOverdueItemsCount(((Number) row[3]).intValue());
            overdueLeaders.add(user);
        }
        stats.setOverdueLeaders(overdueLeaders);

        stats.setTodayTraffic(0);

        return stats;
    }

    public List<LibraryResource> searchBooks(String query) {
        // Implementation for elastic/fuzzy search would go here.
        // For now, simple title contains search
        return resourceRepository.findByTitleContainingIgnoreCase(query);
    }

    // Overriding the previous calculateAndPostFine to use LibraryFineRule if
    // available
    private void calculateAndPostFine(LibraryLoan loan) {
        User user = loan.getUser();
        // Determine member type (simplified logic, adjust based on Role names)
        String memberType = "STUDENT"; // Default
        if (user.getRoles().stream().anyMatch(r -> r.getName().contains("STAFF") || r.getName().contains("TEACHER"))) {
            memberType = "STAFF";
        }

        LibraryFineRule fineRule = fineRuleRepository.findByMemberType(memberType).orElse(null);
        BigDecimal finePerDay = BigDecimal.ZERO;

        if (fineRule != null) {
            finePerDay = fineRule.getDailyFineAmount();
        } else {
            // Fallback to old policy logic
            krs.erp.model.Role userRole = user.getRoles().isEmpty() ? null : user.getRoles().iterator().next();
            if (userRole != null) {
                LibraryPolicy policy = policyRepository.findByRoleAndGradeLevel(userRole, null)
                        .orElseGet(() -> policyRepository.findByRole(userRole).orElse(null));
                if (policy != null) {
                    finePerDay = policy.getFinePerDay();
                }
            }
        }

        if (finePerDay.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        long daysOverdue = Duration.between(loan.getDueDate(), LocalDateTime.now()).toDays();
        if (daysOverdue > 0) {
            BigDecimal fineAmount = finePerDay.multiply(BigDecimal.valueOf(daysOverdue));

            // Cap fine if max amount is set
            if (fineRule != null && fineRule.getMaxFineAmount() != null
                    && fineAmount.compareTo(fineRule.getMaxFineAmount()) > 0) {
                fineAmount = fineRule.getMaxFineAmount();
            }

            StudentFineLedger fine = new StudentFineLedger();
            fine.setStudentId(user.getId());
            fine.setFineConfigId(0L);
            fine.setBaseAmount(fineAmount.doubleValue());
            fine.setAccruedAmount(fineAmount.doubleValue());
            fine.setStatus(StudentFineLedger.FineStatus.PENDING);
            fine.setIssuedAt(LocalDateTime.now());
            fineLedgerRepository.save(fine);

            logger.info("Posted library fine of {} for user {} (Loan ID: {})", fineAmount, user.getUsername(),
                    loan.getId());
        }
    }
}
