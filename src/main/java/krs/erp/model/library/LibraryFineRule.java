package krs.erp.model.library;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "erp_library_param_fine_rules", uniqueConstraints = {
        @UniqueConstraint(columnNames = "member_type")
})
@EqualsAndHashCode(callSuper = true)
public class LibraryFineRule extends BaseEntity {

    @Column(name = "member_type", nullable = false)
    private String memberType; // STUDENT, STAFF

    @Column(name = "daily_fine_amount", nullable = false)
    private BigDecimal dailyFineAmount = BigDecimal.ZERO;

    @Column(name = "max_fine_amount")
    private BigDecimal maxFineAmount;

    @Column(name = "grace_period_days")
    private Integer gracePeriodDays = 0;

    private String description;
}
