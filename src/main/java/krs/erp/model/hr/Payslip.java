package krs.erp.model.hr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import krs.erp.model.BaseEntity;

@Entity
@Table(name = "erp_hr_payslips")
public class Payslip extends BaseEntity {

    @NotNull
    @Column(name = "payroll_run_id", nullable = false)
    private Long payrollRunId;

    @NotNull
    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @Column(name = "staff_name")
    private String staffName;

    @Column(name = "department")
    private String department;

    // Earnings
    @Column(name = "basic_salary")
    private Double basicSalary = 0.0;

    @Column(name = "hra")
    private Double hra = 0.0;

    @Column(name = "da")
    private Double da = 0.0;

    @Column(name = "allowances")
    private Double allowances = 0.0;

    // Deductions
    @Column(name = "pf_deduction")
    private Double pfDeduction = 0.0;

    @Column(name = "tax_deduction")
    private Double taxDeduction = 0.0;

    @Column(name = "other_deductions")
    private Double otherDeductions = 0.0;

    // Totals
    @Column(name = "gross_salary")
    private Double grossSalary = 0.0;

    @Column(name = "total_deductions")
    private Double totalDeductions = 0.0;

    @Column(name = "net_salary")
    private Double netSalary = 0.0;

    // Getters and Setters
    public Long getPayrollRunId() {
        return payrollRunId;
    }

    public void setPayrollRunId(Long payrollRunId) {
        this.payrollRunId = payrollRunId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public Double getHra() {
        return hra;
    }

    public void setHra(Double hra) {
        this.hra = hra;
    }

    public Double getDa() {
        return da;
    }

    public void setDa(Double da) {
        this.da = da;
    }

    public Double getAllowances() {
        return allowances;
    }

    public void setAllowances(Double allowances) {
        this.allowances = allowances;
    }

    public Double getPfDeduction() {
        return pfDeduction;
    }

    public void setPfDeduction(Double pfDeduction) {
        this.pfDeduction = pfDeduction;
    }

    public Double getTaxDeduction() {
        return taxDeduction;
    }

    public void setTaxDeduction(Double taxDeduction) {
        this.taxDeduction = taxDeduction;
    }

    public Double getOtherDeductions() {
        return otherDeductions;
    }

    public void setOtherDeductions(Double otherDeductions) {
        this.otherDeductions = otherDeductions;
    }

    public Double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(Double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public Double getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(Double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public Double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
    }
}
