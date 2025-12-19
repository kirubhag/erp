package krs.erp.model.hr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_hr_leave_balances")
@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveBalance extends BaseEntity {

    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @Column(name = "leave_type_id", nullable = false)
    private Long leaveTypeId;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "total_days")
    private Double totalDays = 0.0;

    @Column(name = "consumed_days")
    private Double consumedDays = 0.0;

    @Column(name = "remaining_days")
    private Double remainingDays = 0.0;
}
