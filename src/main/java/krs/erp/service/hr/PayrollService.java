package krs.erp.service.hr;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Staff;
import krs.erp.model.Staff.EmploymentStatus;
import krs.erp.model.hr.PayrollRun;
import krs.erp.model.hr.PayrollRun.RunStatus;
import krs.erp.model.hr.Payslip;
import krs.erp.model.hr.StaffSalary;
import krs.erp.repository.StaffRepository;
import krs.erp.repository.hr.PayrollRunRepository;
import krs.erp.repository.hr.PayslipRepository;
import krs.erp.repository.hr.StaffSalaryRepository;

@Service
@Transactional
public class PayrollService {

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private StaffSalaryRepository staffSalaryRepository;

    @Autowired
    private StaffRepository staffRepository;

    // 1. Initiate a Payroll Run for a Month/Year
    public PayrollRun initiatePayrollRun(Integer month, Integer year) {
        if (payrollRunRepository.existsByMonthAndYear(month, year)) {
            throw new RuntimeException("Payroll for this month/year already exists!");
        }

        PayrollRun run = new PayrollRun();
        run.setMonth(month);
        run.setYear(year);
        run.setStatus(RunStatus.DRAFT);
        // Do NOT populate processed_date yet
        return payrollRunRepository.save(run);
    }

    // 2. Process Calculation for All Active Staff
    @SuppressWarnings("null")
    public PayrollRun executePayrollRun(Long runId) {
        PayrollRun run = payrollRunRepository.findById(runId)
                .orElseThrow(() -> new RuntimeException("Payroll Run not found"));

        if (run.getStatus() == RunStatus.COMPLETED) {
            throw new RuntimeException("Payroll Run is already completed.");
        }

        List<Staff> activeStaff = staffRepository.findByEmploymentStatus(EmploymentStatus.ACTIVE);
        double totalPayout = 0;
        List<Payslip> payslips = new ArrayList<>();

        // Clear existing drafts if any (optional, for simple re-run logic)
        List<Payslip> existing = payslipRepository.findByPayrollRunId(runId);
        payslipRepository.deleteAll(existing);

        for (Staff staff : activeStaff) {
            Optional<StaffSalary> salaryOpt = staffSalaryRepository.findByStaffId(staff.getId());
            if (salaryOpt.isPresent()) {
                StaffSalary salary = salaryOpt.get();
                Payslip payslip = calculatePayslip(run, staff, salary);
                payslips.add(payslip);
                totalPayout += payslip.getNetSalary();
            }
        }

        payslipRepository.saveAll(payslips);

        run.setTotalPayout(totalPayout);
        run.setStatus(RunStatus.PROCESSED);
        run.setProcessedDate(LocalDate.now());

        return payrollRunRepository.save(run);
    }

    private Payslip calculatePayslip(PayrollRun run, Staff staff, StaffSalary salary) {
        Payslip p = new Payslip();
        p.setPayrollRunId(run.getId());
        p.setStaffId(staff.getId());
        p.setStaffName(staff.getFullName());
        p.setDepartment(staff.getDepartment());

        // Earnings
        double basic = salary.getBasicSalary() != null ? salary.getBasicSalary() : 0;
        double hra = salary.getHra() != null ? salary.getHra() : 0;
        double da = salary.getDa() != null ? salary.getDa() : 0;
        double special = salary.getSpecialAllowance() != null ? salary.getSpecialAllowance() : 0;

        p.setBasicSalary(basic);
        p.setHra(hra);
        p.setDa(da);
        p.setAllowances(special);

        double gross = basic + hra + da + special;
        p.setGrossSalary(gross);

        // Deductions
        // Logic: PF is 12% of Basic + DA (Indian Rule typically)
        // If isPfEnabled is true
        double pf = 0;
        if (Boolean.TRUE.equals(salary.getIsPfEnabled())) {
            pf = (basic + da) * 0.12;
        }
        p.setPfDeduction(pf);

        double tax = salary.getTaxDeduction() != null ? salary.getTaxDeduction() : 0;
        p.setTaxDeduction(tax);

        double totalDeductions = pf + tax;
        p.setTotalDeductions(totalDeductions);

        // Net
        p.setNetSalary(gross - totalDeductions);

        return p;
    }

    // CRUD for Salary Structure
    public StaffSalary updateSalaryStructure(StaffSalary salaryStructure) {
        Optional<StaffSalary> existing = staffSalaryRepository.findByStaffId(salaryStructure.getStaffId());
        if (existing.isPresent()) {
            StaffSalary s = existing.get();
            s.setBasicSalary(salaryStructure.getBasicSalary());
            s.setHra(salaryStructure.getHra());
            s.setDa(salaryStructure.getDa());
            s.setSpecialAllowance(salaryStructure.getSpecialAllowance());
            s.setIsPfEnabled(salaryStructure.getIsPfEnabled());
            s.setPfAccountNumber(salaryStructure.getPfAccountNumber());
            s.setTaxDeduction(salaryStructure.getTaxDeduction());
            s.setPanNumber(salaryStructure.getPanNumber());
            return staffSalaryRepository.save(s);
        } else {
            return staffSalaryRepository.save(salaryStructure);
        }
    }

    public StaffSalary getSalaryStructure(Long staffId) {
        return staffSalaryRepository.findByStaffId(staffId).orElse(new StaffSalary());
    }

    // View Methods
    public List<PayrollRun> getAllRuns() {
        return payrollRunRepository.findAll();
    }

    public List<Payslip> getPayslipsForRun(Long runId) {
        return payslipRepository.findByPayrollRunId(runId);
    }
}
