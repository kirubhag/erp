package krs.erp.model.hr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import krs.erp.model.BaseEntity;

@Entity
@Table(name = "erp_hr_staff_salaries")
public class StaffSalary extends BaseEntity {

    @NotNull
    @Column(name = "staff_id", nullable = false, unique = true)
    private Long staffId;

    @Column(name = "basic_salary")
    private Double basicSalary = 0.0;

    @Column(name = "hra")
    private Double hra = 0.0;

    @Column(name = "da")
    private Double da = 0.0;

    @Column(name = "special_allowance")
    private Double specialAllowance = 0.0;

    @Column(name = "is_pf_enabled")
    private Boolean isPfEnabled = false;

    @Column(name = "pf_account_number", length = 50)
    private String pfAccountNumber;

    @Column(name = "tax_deduction")
    private Double taxDeduction = 0.0; // TDS per month

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    // Getters and Setters
    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
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

    public Double getSpecialAllowance() {
        return specialAllowance;
    }

    public void setSpecialAllowance(Double specialAllowance) {
        this.specialAllowance = specialAllowance;
    }

    public Boolean getIsPfEnabled() {
        return isPfEnabled;
    }

    public void setIsPfEnabled(Boolean isPfEnabled) {
        this.isPfEnabled = isPfEnabled;
    }

    public String getPfAccountNumber() {
        return pfAccountNumber;
    }

    public void setPfAccountNumber(String pfAccountNumber) {
        this.pfAccountNumber = pfAccountNumber;
    }

    public Double getTaxDeduction() {
        return taxDeduction;
    }

    public void setTaxDeduction(Double taxDeduction) {
        this.taxDeduction = taxDeduction;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public Double getGrossSalary() {
        return (basicSalary != null ? basicSalary : 0) +
                (hra != null ? hra : 0) +
                (da != null ? da : 0) +
                (specialAllowance != null ? specialAllowance : 0);
    }
}
