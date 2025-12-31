package krs.erp.controller.finance;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.finance.BankStatement;
import krs.erp.model.finance.Budget;
import krs.erp.model.finance.DisciplinaryIncident;
import krs.erp.model.finance.FeeDiscountRule;
import krs.erp.model.finance.FeePayment;
import krs.erp.model.finance.FeeStructure;
import krs.erp.model.finance.FeeType;
import krs.erp.model.finance.FineCategory;
import krs.erp.model.finance.FineConfiguration;
import krs.erp.model.finance.FineWaiverRequest;
import krs.erp.model.finance.Invoice;
import krs.erp.model.finance.JournalEntry;
import krs.erp.model.finance.ScholarshipApplication;
import krs.erp.model.finance.ScholarshipDisbursement;
import krs.erp.model.finance.StudentFineLedger;
import krs.erp.model.finance.Transaction;
import krs.erp.repository.finance.BankStatementRepository;
import krs.erp.repository.finance.BudgetRepository;
import krs.erp.repository.finance.DisciplinaryIncidentRepository;
import krs.erp.repository.finance.FeeDiscountRuleRepository;
import krs.erp.repository.finance.FeePaymentRepository;
import krs.erp.repository.finance.FeeStructureRepository;
import krs.erp.repository.finance.FeeTypeRepository;
import krs.erp.repository.finance.FineCategoryRepository;
import krs.erp.repository.finance.FineConfigurationRepository;
import krs.erp.repository.finance.FineWaiverRequestRepository;
import krs.erp.repository.finance.InvoiceRepository;
import krs.erp.repository.finance.StudentFineLedgerRepository;
import krs.erp.repository.finance.TransactionRepository;
import krs.erp.service.finance.FinanceService;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*")
public class FinanceController {

    @Autowired
    private FinanceService financeService;
    
    @Autowired
    private FeeTypeRepository feeTypeRepository;
    
    @Autowired
    private FeeStructureRepository feeStructureRepository;
    
    @Autowired
    private FeeDiscountRuleRepository feeDiscountRuleRepository;
    
    @Autowired
    private FineCategoryRepository fineCategoryRepository;
    
    @Autowired
    private FineConfigurationRepository fineConfigurationRepository;
    
    @Autowired
    private FeePaymentRepository feePaymentRepository;
    
    @Autowired
    private StudentFineLedgerRepository fineLedgerRepository;
    
    @Autowired
    private DisciplinaryIncidentRepository disciplinaryIncidentRepository;
    
    @Autowired
    private FineWaiverRequestRepository fineWaiverRequestRepository;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private BankStatementRepository bankStatementRepository;

    // === Fee Types CRUD ===
    @GetMapping("/fee-types")
    public ResponseEntity<Page<FeeType>> getAllFeeTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(feeTypeRepository.findAll(pageable));
    }
    
    @GetMapping("/fee-types/{id}")
    public ResponseEntity<FeeType> getFeeType(@PathVariable Long id) {
        return feeTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/fee-types")
    public ResponseEntity<FeeType> createFeeType(@RequestBody FeeType feeType) {
        return ResponseEntity.ok(feeTypeRepository.save(feeType));
    }
    
    @PutMapping("/fee-types/{id}")
    public ResponseEntity<FeeType> updateFeeType(@PathVariable Long id, @RequestBody FeeType feeType) {
        if (!feeTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        feeType.setId(id);
        return ResponseEntity.ok(feeTypeRepository.save(feeType));
    }
    
    @DeleteMapping("/fee-types/{id}")
    public ResponseEntity<Void> deleteFeeType(@PathVariable Long id) {
        if (!feeTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        feeTypeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Fee Structures CRUD ===
    @GetMapping("/fee-structures")
    public ResponseEntity<Page<FeeStructure>> getAllFeeStructures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(feeStructureRepository.findAll(pageable));
    }
    
    @GetMapping("/fee-structures/{id}")
    public ResponseEntity<FeeStructure> getFeeStructure(@PathVariable Long id) {
        return feeStructureRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/fee-structures")
    public ResponseEntity<FeeStructure> createFeeStructure(@RequestBody FeeStructure feeStructure) {
        return ResponseEntity.ok(feeStructureRepository.save(feeStructure));
    }
    
    @PutMapping("/fee-structures/{id}")
    public ResponseEntity<FeeStructure> updateFeeStructure(@PathVariable Long id, @RequestBody FeeStructure feeStructure) {
        if (!feeStructureRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        feeStructure.setId(id);
        return ResponseEntity.ok(feeStructureRepository.save(feeStructure));
    }
    
    @DeleteMapping("/fee-structures/{id}")
    public ResponseEntity<Void> deleteFeeStructure(@PathVariable Long id) {
        if (!feeStructureRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        feeStructureRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Fee Discount Rules (Discounts) CRUD ===
    @GetMapping("/discounts")
    public ResponseEntity<Page<FeeDiscountRule>> getAllDiscounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(feeDiscountRuleRepository.findAll(pageable));
    }
    
    @GetMapping("/discounts/{id}")
    public ResponseEntity<FeeDiscountRule> getDiscount(@PathVariable Long id) {
        return feeDiscountRuleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/discounts")
    public ResponseEntity<FeeDiscountRule> createDiscount(@RequestBody FeeDiscountRule discount) {
        return ResponseEntity.ok(feeDiscountRuleRepository.save(discount));
    }
    
    @PutMapping("/discounts/{id}")
    public ResponseEntity<FeeDiscountRule> updateDiscount(@PathVariable Long id, @RequestBody FeeDiscountRule discount) {
        if (!feeDiscountRuleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        discount.setId(id);
        return ResponseEntity.ok(feeDiscountRuleRepository.save(discount));
    }
    
    @DeleteMapping("/discounts/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        if (!feeDiscountRuleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        feeDiscountRuleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Fine Categories CRUD ===
    @GetMapping("/fine-categories")
    public ResponseEntity<Page<FineCategory>> getAllFineCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(fineCategoryRepository.findAll(pageable));
    }
    
    @GetMapping("/fine-categories/{id}")
    public ResponseEntity<FineCategory> getFineCategory(@PathVariable Long id) {
        return fineCategoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/fine-categories")
    public ResponseEntity<FineCategory> createFineCategory(@RequestBody FineCategory fineCategory) {
        return ResponseEntity.ok(fineCategoryRepository.save(fineCategory));
    }
    
    @PutMapping("/fine-categories/{id}")
    public ResponseEntity<FineCategory> updateFineCategory(@PathVariable Long id, @RequestBody FineCategory fineCategory) {
        if (!fineCategoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fineCategory.setId(id);
        return ResponseEntity.ok(fineCategoryRepository.save(fineCategory));
    }
    
    @DeleteMapping("/fine-categories/{id}")
    public ResponseEntity<Void> deleteFineCategory(@PathVariable Long id) {
        if (!fineCategoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fineCategoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Fine Management (Configurations) CRUD ===
    @GetMapping("/fine-management")
    public ResponseEntity<Page<FineConfiguration>> getAllFineConfigurations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(fineConfigurationRepository.findAll(pageable));
    }
    
    @GetMapping("/fine-management/{id}")
    public ResponseEntity<FineConfiguration> getFineConfiguration(@PathVariable Long id) {
        return fineConfigurationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/fine-management")
    public ResponseEntity<FineConfiguration> createFineConfiguration(@RequestBody FineConfiguration config) {
        return ResponseEntity.ok(fineConfigurationRepository.save(config));
    }
    
    @PutMapping("/fine-management/{id}")
    public ResponseEntity<FineConfiguration> updateFineConfiguration(@PathVariable Long id, @RequestBody FineConfiguration config) {
        if (!fineConfigurationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        config.setId(id);
        return ResponseEntity.ok(fineConfigurationRepository.save(config));
    }
    
    @DeleteMapping("/fine-management/{id}")
    public ResponseEntity<Void> deleteFineConfiguration(@PathVariable Long id) {
        if (!fineConfigurationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fineConfigurationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Fee Payments CRUD ===
    @GetMapping("/payments")
    public ResponseEntity<Page<FeePayment>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(feePaymentRepository.findAll(pageable));
    }
    
    @GetMapping("/payments/{id}")
    public ResponseEntity<FeePayment> getPayment(@PathVariable Long id) {
        return feePaymentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/payments/{id}")
    public ResponseEntity<FeePayment> updatePayment(@PathVariable Long id, @RequestBody FeePayment payment) {
        if (!feePaymentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        payment.setId(id);
        return ResponseEntity.ok(feePaymentRepository.save(payment));
    }
    
    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        if (!feePaymentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        feePaymentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Fine Ledger CRUD ===
    @GetMapping("/fine-ledger")
    public ResponseEntity<Page<StudentFineLedger>> getAllFineLedgers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(fineLedgerRepository.findAll(pageable));
    }
    
    @GetMapping("/fine-ledger/{id}")
    public ResponseEntity<StudentFineLedger> getFineLedger(@PathVariable Long id) {
        return fineLedgerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/fine-ledger")
    public ResponseEntity<StudentFineLedger> createFineLedger(@RequestBody StudentFineLedger ledger) {
        return ResponseEntity.ok(fineLedgerRepository.save(ledger));
    }
    
    @PutMapping("/fine-ledger/{id}")
    public ResponseEntity<StudentFineLedger> updateFineLedger(@PathVariable Long id, @RequestBody StudentFineLedger ledger) {
        if (!fineLedgerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ledger.setId(id);
        return ResponseEntity.ok(fineLedgerRepository.save(ledger));
    }
    
    @DeleteMapping("/fine-ledger/{id}")
    public ResponseEntity<Void> deleteFineLedger(@PathVariable Long id) {
        if (!fineLedgerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fineLedgerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Disciplinary Incidents CRUD ===
    @GetMapping("/incidents")
    public ResponseEntity<Page<DisciplinaryIncident>> getAllIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(disciplinaryIncidentRepository.findAll(pageable));
    }
    
    @GetMapping("/incidents/{id}")
    public ResponseEntity<DisciplinaryIncident> getIncident(@PathVariable Long id) {
        return disciplinaryIncidentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/incidents")
    public ResponseEntity<DisciplinaryIncident> createIncident(@RequestBody DisciplinaryIncident incident) {
        return ResponseEntity.ok(disciplinaryIncidentRepository.save(incident));
    }
    
    @PutMapping("/incidents/{id}")
    public ResponseEntity<DisciplinaryIncident> updateIncident(@PathVariable Long id, @RequestBody DisciplinaryIncident incident) {
        if (!disciplinaryIncidentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        incident.setId(id);
        return ResponseEntity.ok(disciplinaryIncidentRepository.save(incident));
    }
    
    @DeleteMapping("/incidents/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        if (!disciplinaryIncidentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        disciplinaryIncidentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Fine Waiver Requests CRUD ===
    @GetMapping("/waivers")
    public ResponseEntity<Page<FineWaiverRequest>> getAllWaivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(fineWaiverRequestRepository.findAll(pageable));
    }
    
    @GetMapping("/waivers/{id}")
    public ResponseEntity<FineWaiverRequest> getWaiver(@PathVariable Long id) {
        return fineWaiverRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/waivers")
    public ResponseEntity<FineWaiverRequest> createWaiver(@RequestBody FineWaiverRequest waiver) {
        return ResponseEntity.ok(fineWaiverRequestRepository.save(waiver));
    }
    
    @PutMapping("/waivers/{id}")
    public ResponseEntity<FineWaiverRequest> updateWaiver(@PathVariable Long id, @RequestBody FineWaiverRequest waiver) {
        if (!fineWaiverRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        waiver.setId(id);
        return ResponseEntity.ok(fineWaiverRequestRepository.save(waiver));
    }
    
    @DeleteMapping("/waivers/{id}")
    public ResponseEntity<Void> deleteWaiver(@PathVariable Long id) {
        if (!fineWaiverRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fineWaiverRequestRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Invoices CRUD ===
    @GetMapping("/invoices")
    public ResponseEntity<Page<Invoice>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(invoiceRepository.findAll(pageable));
    }
    
    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceRepository.save(invoice));
    }
    
    @PutMapping("/invoices/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        if (!invoiceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        invoice.setId(id);
        return ResponseEntity.ok(invoiceRepository.save(invoice));
    }
    
    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        if (!invoiceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        invoiceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Transactions CRUD ===
    @GetMapping("/transactions")
    public ResponseEntity<Page<Transaction>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(transactionRepository.findAll(pageable));
    }
    
    @GetMapping("/transactions/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        return transactionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/transactions/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        if (!transactionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        transaction.setId(id);
        return ResponseEntity.ok(transactionRepository.save(transaction));
    }
    
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (!transactionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        transactionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Budgets CRUD ===
    @GetMapping("/budgets")
    public ResponseEntity<Page<Budget>> getAllBudgets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(budgetRepository.findAll(pageable));
    }
    
    @GetMapping("/budgets/{id}")
    public ResponseEntity<Budget> getBudget(@PathVariable Long id) {
        return budgetRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/budgets")
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        return ResponseEntity.ok(budgetRepository.save(budget));
    }
    
    @PutMapping("/budgets/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budget) {
        if (!budgetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        budget.setId(id);
        return ResponseEntity.ok(budgetRepository.save(budget));
    }
    
    @DeleteMapping("/budgets/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        if (!budgetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        budgetRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    // === Bank Statements CRUD ===
    @GetMapping("/bank-statements")
    public ResponseEntity<Page<BankStatement>> getAllBankStatements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bankStatementRepository.findAll(pageable));
    }
    
    @GetMapping("/bank-statements/{id}")
    public ResponseEntity<BankStatement> getBankStatement(@PathVariable Long id) {
        return bankStatementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/bank-statements")
    public ResponseEntity<BankStatement> createBankStatement(@RequestBody BankStatement statement) {
        return ResponseEntity.ok(bankStatementRepository.save(statement));
    }
    
    @PutMapping("/bank-statements/{id}")
    public ResponseEntity<BankStatement> updateBankStatement(@PathVariable Long id, @RequestBody BankStatement statement) {
        if (!bankStatementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        statement.setId(id);
        return ResponseEntity.ok(bankStatementRepository.save(statement));
    }
    
    @DeleteMapping("/bank-statements/{id}")
    public ResponseEntity<Void> deleteBankStatement(@PathVariable Long id) {
        if (!bankStatementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bankStatementRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Business Logic Endpoints (existing) ===

    @PostMapping("/payments/record")
    public ResponseEntity<FeePayment> recordPayment(@RequestBody FeePayment payment) {
        return ResponseEntity.ok(financeService.recordPayment(payment));
    }

    @PostMapping("/incidents/{id}/approve")
    public ResponseEntity<Void> approveIncident(@PathVariable Long id, @RequestParam Long staffId) {
        financeService.approveIncident(id, staffId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/waivers/{id}/process")
    public ResponseEntity<Void> processWaiver(
            @PathVariable Long id,
            @RequestParam Long staffId,
            @RequestParam FinanceService.WaiverAction action,
            @RequestParam Double amount) {
        financeService.processWaiver(id, staffId, action, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/late-fees/calculate")
    public ResponseEntity<Void> calculateLateFees() {
        financeService.calculateLateFees();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invoices/generate")
    public ResponseEntity<Invoice> generateInvoice(@RequestParam Long studentId, @RequestParam Long feeStructureId) {
        return ResponseEntity.ok(financeService.generateInvoiceForFee(studentId, feeStructureId));
    }

    @PostMapping("/transactions/record")
    public ResponseEntity<Transaction> recordTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(financeService.recordTransaction(transaction));
    }

    @PostMapping("/scholarships/apply")
    public ResponseEntity<ScholarshipApplication> applyScholarship(@RequestBody ScholarshipApplication application) {
        return ResponseEntity.ok(financeService.applyForScholarship(application));
    }

    @PostMapping("/scholarships/{id}/approve")
    public ResponseEntity<ScholarshipApplication> approveScholarship(@PathVariable Long id,
            @RequestParam String approvedBy) {
        return ResponseEntity.ok(financeService.approveScholarship(id, approvedBy));
    }

    @PostMapping("/scholarships/disburse")
    public ResponseEntity<ScholarshipDisbursement> disburseScholarship(@RequestParam Long applicationId,
            @RequestParam Long invoiceId) {
        return ResponseEntity.ok(financeService.disburseScholarship(applicationId, invoiceId));
    }

    // --- General Ledger Endpoints ---

    @PostMapping("/journal-entry")
    public ResponseEntity<JournalEntry> createJournalEntry(@RequestBody JournalEntry entry) {
        return ResponseEntity.ok(financeService.createJournalEntry(entry));
    }

    @PostMapping("/journal-entry/{id}/post")
    public ResponseEntity<JournalEntry> postJournalEntry(@PathVariable Long id) {
        return ResponseEntity.ok(financeService.postJournalEntry(id));
    }

    @GetMapping("/trial-balance")
    public ResponseEntity<Map<String, BigDecimal>> getTrialBalance() {
        return ResponseEntity.ok(financeService.getTrialBalance());
    }

    @GetMapping("/profit-loss")
    public ResponseEntity<Map<String, BigDecimal>> getProfitAndLoss() {
        return ResponseEntity.ok(financeService.getProfitAndLoss());
    }
}
