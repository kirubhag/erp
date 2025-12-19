package krs.erp.model.hr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_hr_leave_types")
@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveType extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "days_allowed")
    private Integer daysAllowed;

    @Column(name = "is_carry_forward")
    private Boolean isCarryForward = false;

    @Column(name = "description")
    private String description;
}
