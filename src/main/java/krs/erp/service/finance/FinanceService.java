package krs.erp.service.finance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.AccountType;
import krs.erp.model.finance.BankStatement;
import krs.erp.model.finance.BankStatementLine;
import krs.erp.model.finance.Budget;
import krs.erp.model.finance.BudgetLine;
import krs.erp.model.finance.ChartOfAccount;
import krs.erp.model.finance.DisciplinaryIncident;
import krs.erp.model.finance.FeeDiscountRule;
import krs.erp.model.finance.FeePayment;
import krs.erp.model.finance.FeeStructure;
import krs.erp.model.finance.FineWaiverRequest;
import krs.erp.model.finance.Invoice;
import krs.erp.model.finance.InvoiceItem;
import krs.erp.model.finance.JournalEntry;
import krs.erp.model.finance.JournalItem;
import krs.erp.model.finance.ScholarshipApplication;
import krs.erp.model.finance.ScholarshipCategory;
import krs.erp.model.finance.ScholarshipDisbursement;
import krs.erp.model.finance.StudentFineLedger;
import krs.erp.model.finance.Transaction;
import krs.erp.repository.ParentStudentRelationRepository;
import krs.erp.repository.finance.AccountingPeriodRepository;
import krs.erp.repository.finance.BankStatementLineRepository;
import krs.erp.repository.finance.BankStatementRepository;
import krs.erp.repository.finance.BudgetLineRepository;
import krs.erp.repository.finance.BudgetRepository;
import krs.erp.repository.finance.ChartOfAccountRepository;
import krs.erp.repository.finance.DisciplinaryIncidentRepository;
import krs.erp.repository.finance.FeeDiscountRuleRepository;
import krs.erp.repository.finance.FeePaymentRepository;
import krs.erp.repository.finance.FeeStructureRepository;
import krs.erp.repository.finance.FeeTypeRepository;
import krs.erp.repository.finance.FineWaiverRequestRepository;
import krs.erp.repository.finance.InvoiceItemRepository;
import krs.erp.repository.finance.InvoiceRepository;
import krs.erp.repository.finance.JournalEntryRepository;
import krs.erp.repository.finance.JournalItemRepository;
import krs.erp.repository.finance.ScholarshipApplicationRepository;
import krs.erp.repository.finance.ScholarshipCategoryRepository;
import krs.erp.repository.finance.ScholarshipDisbursementRepository;
import krs.erp.repository.finance.StudentFineLedgerRepository;
import krs.erp.repository.finance.TransactionRepository;

@Service
public class FinanceService {

    @Autowired
    private FeeTypeRepository feeTypeRepository;

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Autowired
    private FeePaymentRepository feePaymentRepository;

    @Autowired
    private StudentFineLedgerRepository fineLedgerRepository;

    @Autowired
    private FeeDiscountRuleRepository discountRuleRepository;

    @Autowired
    private ParentStudentRelationRepository parentStudentRelationRepository;

    @Autowired
    private DisciplinaryIncidentRepository incidentRepository;

    @Autowired
    private FineWaiverRequestRepository waiverRepository;

    @Autowired
    private ScholarshipCategoryRepository scholarshipCategoryRepository;

    @Autowired
    private ScholarshipApplicationRepository scholarshipApplicationRepository;

    @Autowired
    private ScholarshipDisbursementRepository scholarshipDisbursementRepository;

    @Autowired
    private ChartOfAccountRepository chartOfAccountRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private JournalItemRepository journalItemRepository;

    @Autowired
    private AccountingPeriodRepository accountingPeriodRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BudgetLineRepository budgetLineRepository;

    @Autowired
    private BankStatementRepository bankStatementRepository;

    @Autowired
    private BankStatementLineRepository bankStatementLineRepository;

    @Transactional
    public FeePayment recordPayment(FeePayment payment) {
        // Apply sibling discount if applicable
        if (isSibling(payment.getStudentId())) {
            Optional<FeeDiscountRule> siblingRule = discountRuleRepository.findAll().stream()
                    .filter(r -> "SIBLING".equalsIgnoreCase(r.getConditionType()))
                    .findFirst();

            if (siblingRule.isPresent()) {
                FeeDiscountRule rule = siblingRule.get();
                double discount = 0;
                if (rule.getType() == FeeDiscountRule.DiscountType.PERCENTAGE) {
                    discount = payment.getBaseAmount() * (rule.getValue() / 100);
                } else {
                    discount = rule.getValue();
                }
                payment.setDiscountAmount(discount);
            }
        }

        // Calculate net amount
        double net = payment.getBaseAmount() - payment.getDiscountAmount() + payment.getFineAmount();
        payment.setNetAmount(net);
        payment.setPaymentDate(LocalDateTime.now());

        return feePaymentRepository.save(payment);
    }

    private boolean isSibling(Long studentId) {
        // Logic: find parents of this student, then find other students of those
        // parents
        List<krs.erp.model.ParentStudentRelation> parents = parentStudentRelationRepository.findByStudentId(studentId);
        for (krs.erp.model.ParentStudentRelation rel : parents) {
            List<krs.erp.model.ParentStudentRelation> siblings = parentStudentRelationRepository
                    .findByParentId(rel.getParent().getId());
            if (siblings.size() > 1) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void calculateLateFees() {
        // Automation logic for overdue fee structures
        List<FeeStructure> structures = feeStructureRepository.findAll();
        LocalDate now = LocalDate.now();

        for (FeeStructure fs : structures) {
            if (fs.getDueDate() != null && fs.getDueDate().isBefore(now)) {
                // Find students who haven't paid for this structure
                // Simplified for now: just record pending fines based on frequency
            }
        }
    }

    @Transactional
    public void approveIncident(Long incidentId, Long staffId) {
        Optional<DisciplinaryIncident> incident = incidentRepository.findById(incidentId);
        if (incident.isPresent()) {
            DisciplinaryIncident di = incident.get();
            di.setApprovalStatus(DisciplinaryIncident.ApprovalStatus.APPROVED);
            incidentRepository.save(di);

            // Post to ledger
            StudentFineLedger ledger = new StudentFineLedger();
            ledger.setStudentId(di.getStudentId());
            ledger.setBaseAmount(di.getFineAmount());
            ledger.setAccruedAmount(di.getFineAmount());
            ledger.setStatus(StudentFineLedger.FineStatus.PENDING);
            fineLedgerRepository.save(ledger);
        }
    }

    @Transactional
    public void processWaiver(Long waiverId, Long staffId, WaiverAction action, Double adjustedAmount) {
        Optional<FineWaiverRequest> request = waiverRepository.findById(waiverId);
        if (request.isPresent()) {
            FineWaiverRequest wr = request.get();
            wr.setApprovedById(staffId);
            wr.setStatus(action == WaiverAction.APPROVE ? FineWaiverRequest.WaiverStatus.APPROVED
                    : FineWaiverRequest.WaiverStatus.REJECTED);
            wr.setAdjustmentAmount(adjustedAmount);
            waiverRepository.save(wr);

            if (action == WaiverAction.APPROVE) {
                Optional<StudentFineLedger> ledger = fineLedgerRepository.findById(wr.getFineLedgerId());
                if (ledger.isPresent()) {
                    StudentFineLedger sl = ledger.get();
                    sl.setAccruedAmount(sl.getAccruedAmount() - adjustedAmount);
                    if (sl.getAccruedAmount() <= 0) {
                        sl.setStatus(StudentFineLedger.FineStatus.WAIVED);
                    }
                    fineLedgerRepository.save(sl);
                }
            }
        }
    }

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Invoice generateInvoiceForFee(Long studentId, Long feeStructureId) {
        Optional<FeeStructure> fsOpt = feeStructureRepository.findById(feeStructureId);
        if (fsOpt.isPresent()) {
            FeeStructure fs = fsOpt.get();
            Invoice invoice = new Invoice();
            invoice.setStudentId(studentId);
            invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
            invoice.setIssueDate(LocalDate.now());
            invoice.setDueDate(fs.getDueDate());
            invoice.setTotalAmount(fs.getAmount());
            invoice.setNetAmount(fs.getAmount());
            invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
            Invoice savedInvoice = invoiceRepository.save(invoice);

            InvoiceItem item = new InvoiceItem();
            item.setInvoiceId(savedInvoice.getId());
            item.setDescription("Tuition Fee - " + fs.getAcademicYearId());
            item.setAmount(fs.getAmount());
            item.setItemType(InvoiceItem.ItemType.FEE);
            invoiceItemRepository.save(item);

            return savedInvoice;
        }
        return null;
    }

    @Transactional
    public Transaction recordTransaction(Transaction transaction) {
        transaction.setPaymentDate(LocalDateTime.now());
        Transaction savedTxn = transactionRepository.save(transaction);

        if (savedTxn.getStatus() == Transaction.TransactionStatus.SUCCESS) {
            updateInvoiceStatus(savedTxn.getInvoiceId());
        }
        return savedTxn;
    }

    private void updateInvoiceStatus(Long invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            List<Transaction> txns = transactionRepository.findByInvoiceId(invoiceId);
            double totalPaid = txns.stream()
                    .filter(t -> t.getStatus() == Transaction.TransactionStatus.SUCCESS)
                    .mapToDouble(Transaction::getAmountPaid)
                    .sum();

            if (totalPaid >= invoice.getNetAmount()) {
                invoice.setStatus(Invoice.InvoiceStatus.PAID);
            } else if (totalPaid > 0) {
                invoice.setStatus(Invoice.InvoiceStatus.PARTIALLY_PAID);
            }
            invoiceRepository.save(invoice);
        }
    }

    @Transactional
    public ScholarshipApplication applyForScholarship(ScholarshipApplication application) {
        application.setApplicationDate(LocalDate.now());
        application.setStatus(ScholarshipApplication.ApplicationStatus.SUBMITTED);
        return scholarshipApplicationRepository.save(application);
    }

    @Transactional
    public ScholarshipApplication approveScholarship(Long applicationId, String approvedBy) {
        Optional<ScholarshipApplication> appOpt = scholarshipApplicationRepository.findById(applicationId);
        if (appOpt.isPresent()) {
            ScholarshipApplication app = appOpt.get();
            app.setStatus(ScholarshipApplication.ApplicationStatus.APPROVED);
            app.setApprovedBy(approvedBy);
            app.setApprovalDate(LocalDate.now());
            return scholarshipApplicationRepository.save(app);
        }
        return null;
    }

    @Transactional
    public ScholarshipDisbursement disburseScholarship(Long applicationId, Long invoiceId) {
        Optional<ScholarshipApplication> appOpt = scholarshipApplicationRepository.findById(applicationId);
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);

        if (appOpt.isPresent() && invoiceOpt.isPresent()) {
            ScholarshipApplication app = appOpt.get();
            Invoice invoice = invoiceOpt.get();
            ScholarshipCategory category = app.getCategory();

            java.math.BigDecimal amount = java.math.BigDecimal.ZERO;
            java.math.BigDecimal totalAmount = java.math.BigDecimal.valueOf(invoice.getTotalAmount());

            if (category.getType() == ScholarshipCategory.AidType.FIXED) {
                amount = category.getAmount();
            } else if (category.getType() == ScholarshipCategory.AidType.PERCENTAGE) {
                amount = totalAmount.multiply(category.getPercentage()).divide(new java.math.BigDecimal(100));
            }

            ScholarshipDisbursement disbursement = new ScholarshipDisbursement();
            disbursement.setApplication(app);
            disbursement.setInvoice(invoice);
            disbursement.setDisbursementAmount(amount);
            disbursement.setDisbursementDate(LocalDate.now());
            disbursement.setStatus("APPLIED");

            ScholarshipDisbursement saved = scholarshipDisbursementRepository.save(disbursement);

            // Deduct from invoice
            double netAmount = invoice.getNetAmount() - amount.doubleValue();
            invoice.setNetAmount(netAmount);
            invoiceRepository.save(invoice);

            return saved;
        }
        return null;
    }

    public enum WaiverAction {
        APPROVE, REJECT
    }

    // --- General Ledger Logic ---

    @Transactional
    public JournalEntry createJournalEntry(JournalEntry entry) {
        if (entry.getEntryNumber() == null) {
            entry.setEntryNumber("JE-" + System.currentTimeMillis());
        }
        entry.setStatus(JournalEntry.EntryStatus.DRAFT);
        return journalEntryRepository.save(entry);
    }

    @Transactional
    public JournalEntry postJournalEntry(Long entryId) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Journal Entry not found"));

        if (entry.getStatus() == JournalEntry.EntryStatus.POSTED) {
            throw new RuntimeException("Entry is already posted");
        }

        // Validate debits == credits
        BigDecimal totalDebit = entry.getItems().stream()
                .map(JournalItem::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = entry.getItems().stream()
                .map(JournalItem::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new RuntimeException(
                    "Journal entry is not balanced. Debits: " + totalDebit + ", Credits: " + totalCredit);
        }

        entry.setStatus(JournalEntry.EntryStatus.POSTED);
        return journalEntryRepository.save(entry);
    }

    public Map<String, BigDecimal> getTrialBalance() {
        List<JournalItem> postedItems = journalItemRepository.findAll().stream()
                .filter(item -> item.getJournalEntry().getStatus() == JournalEntry.EntryStatus.POSTED)
                .collect(Collectors.toList());

        Map<String, BigDecimal> balances = new HashMap<>();

        for (JournalItem item : postedItems) {
            String accountName = item.getAccount().getName();
            BigDecimal current = balances.getOrDefault(accountName, BigDecimal.ZERO);

            // Asset/Expense: Debit increases, Credit decreases
            // Liability/Equity/Revenue: Credit increases, Debit decreases
            AccountType type = item.getAccount().getType();
            if (type == AccountType.ASSET || type == AccountType.EXPENSE) {
                balances.put(accountName, current.add(item.getDebit()).subtract(item.getCredit()));
            } else {
                balances.put(accountName, current.add(item.getCredit()).subtract(item.getDebit()));
            }
        }

        return balances;
    }

    public Map<String, BigDecimal> getProfitAndLoss() {
        Map<String, BigDecimal> trialBalance = getTrialBalance();
        Map<String, BigDecimal> pl = new HashMap<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        List<ChartOfAccount> accounts = chartOfAccountRepository.findAll();
        for (ChartOfAccount acc : accounts) {
            BigDecimal balance = trialBalance.getOrDefault(acc.getName(), BigDecimal.ZERO);
            if (acc.getType() == AccountType.REVENUE) {
                totalRevenue = totalRevenue.add(balance);
                pl.put("Revenue: " + acc.getName(), balance);
            } else if (acc.getType() == AccountType.EXPENSE) {
                totalExpense = totalExpense.add(balance);
                pl.put("Expense: " + acc.getName(), balance);
            }
        }

        pl.put("TOTAL_REVENUE", totalRevenue);
        pl.put("TOTAL_EXPENSE", totalExpense);
        pl.put("NET_PROFIT", totalRevenue.subtract(totalExpense));

        return pl;
    }

    // --- Budgeting Logic ---

    @Transactional
    public void updateBudgetActuals(Long accountId, BigDecimal amount) {
        List<BudgetLine> lines = budgetLineRepository.findAll().stream()
                .filter(l -> l.getAccount().getId().equals(accountId))
                .filter(l -> l.getBudget().getStatus() == Budget.BudgetStatus.APPROVED)
                .collect(Collectors.toList());

        for (BudgetLine line : lines) {
            line.setActualAmount(line.getActualAmount().add(amount));
            budgetLineRepository.save(line);
        }
    }

    public List<Map<String, Object>> getBudgetUtilization(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        return budget.getLines().stream().map(line -> {
            Map<String, Object> map = new HashMap<>();
            map.put("account", line.getAccount().getName());
            map.put("allocated", line.getAllocatedAmount());
            map.put("actual", line.getActualAmount());
            map.put("remaining", line.getAllocatedAmount().subtract(line.getActualAmount()));
            BigDecimal percent = line.getAllocatedAmount().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : line.getActualAmount().multiply(new BigDecimal(100)).divide(line.getAllocatedAmount(), 2,
                            RoundingMode.HALF_UP);
            map.put("utilizationPercent", percent);
            return map;
        }).collect(Collectors.toList());
    }

    // --- Bank Reconciliation Logic ---

    @Transactional
    public void reconcileBankLine(Long statementLineId, Long journalItemId) {
        BankStatementLine line = bankStatementLineRepository.findById(statementLineId)
                .orElseThrow(() -> new RuntimeException("Statement line not found"));

        JournalItem item = journalItemRepository.findById(journalItemId)
                .orElseThrow(() -> new RuntimeException("Journal item not found"));

        // Basic validation: amounts should match (absolute value)
        BigDecimal itemAmount = item.getDebit().subtract(item.getCredit());
        if (line.getAmount().abs().compareTo(itemAmount.abs()) != 0) {
            throw new RuntimeException(
                    "Amounts do not match. Statement: " + line.getAmount() + ", Ledger: " + itemAmount);
        }

        line.setReconciled(true);
        line.setMatchedJournalItemId(journalItemId);
        bankStatementLineRepository.save(line);

        // Note: BankStatement status tracking removed - use line reconciliation status
        BankStatement statement = line.getBankStatement();
        bankStatementRepository.save(statement);
    }
}
