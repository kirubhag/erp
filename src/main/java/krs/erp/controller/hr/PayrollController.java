package krs.erp.controller.hr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.hr.PayrollRun;
import krs.erp.model.hr.Payslip;
import krs.erp.model.hr.StaffSalary;
import krs.erp.service.hr.PayrollService;

@RestController
@RequestMapping("/api/hr/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    // Runs
    @PostMapping("/runs")
    public ResponseEntity<PayrollRun> initiateRun(@RequestParam Integer month, @RequestParam Integer year) {
        return ResponseEntity.ok(payrollService.initiatePayrollRun(month, year));
    }

    @PostMapping("/runs/{id}/execute")
    public ResponseEntity<PayrollRun> executeRun(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.executePayrollRun(id));
    }

    @GetMapping("/runs")
    public ResponseEntity<List<PayrollRun>> getAllRuns() {
        return ResponseEntity.ok(payrollService.getAllRuns());
    }

    // Payslips
    @GetMapping("/runs/{id}/payslips")
    public ResponseEntity<List<Payslip>> getPayslips(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.getPayslipsForRun(id));
    }

    // Staff Salary Structure
    @GetMapping("/salary/{staffId}")
    public ResponseEntity<StaffSalary> getSalaryStructure(@PathVariable Long staffId) {
        return ResponseEntity.ok(payrollService.getSalaryStructure(staffId));
    }

    @PostMapping("/salary")
    public ResponseEntity<StaffSalary> updateSalaryStructure(@RequestBody StaffSalary salary) {
        return ResponseEntity.ok(payrollService.updateSalaryStructure(salary));
    }
}
