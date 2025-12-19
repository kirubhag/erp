package krs.erp.controller.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import krs.erp.model.finance.*;
import java.math.BigDecimal;
import java.util.Map;
import krs.erp.service.finance.FinanceService;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

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
