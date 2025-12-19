package krs.erp.model.hr;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import krs.erp.model.BaseEntity;

@Entity
@Table(name = "erp_hr_payroll_runs")
public class PayrollRun extends BaseEntity {

    @NotNull
    @Column(name = "month", nullable = false)
    private Integer month; // 1-12

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "processed_date")
    private LocalDate processedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RunStatus status = RunStatus.DRAFT;

    @Column(name = "total_payout")
    private Double totalPayout = 0.0;

    public enum RunStatus {
        DRAFT, PROCESSED, COMPLETED, CANCELLED
    }

    // Getters and Setters
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalDate getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDate processedDate) {
        this.processedDate = processedDate;
    }

    public RunStatus getStatus() {
        return status;
    }

    public void setStatus(RunStatus status) {
        this.status = status;
    }

    public Double getTotalPayout() {
        return totalPayout;
    }

    public void setTotalPayout(Double totalPayout) {
        this.totalPayout = totalPayout;
    }
}
