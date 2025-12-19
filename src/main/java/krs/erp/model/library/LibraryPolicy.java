package krs.erp.model.library;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import krs.erp.model.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "erp_library_policies")
@EqualsAndHashCode(callSuper = true)
public class LibraryPolicy extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "max_books", nullable = false)
    private Integer maxBooks;

    @Column(name = "loan_period_days", nullable = false)
    private Integer loanPeriodDays;

    @Column(name = "max_renewals", nullable = false)
    private Integer maxRenewals;

    @Column(name = "fine_per_day", nullable = false, precision = 19, scale = 4)
    private java.math.BigDecimal finePerDay = java.math.BigDecimal.ZERO;
}
