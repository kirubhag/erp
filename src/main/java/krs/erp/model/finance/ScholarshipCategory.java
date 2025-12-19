package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Defines a type of scholarship or financial aid.
 */
@Data
@Entity
@Table(name = "fin_scholarship_categories")
@EqualsAndHashCode(callSuper = true)
public class ScholarshipCategory extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AidType type; // FIXED, PERCENTAGE

    @Column(precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    private boolean isNeedBased;
    private boolean isMeritBased;

    public enum AidType {
        FIXED, PERCENTAGE
    }
}
