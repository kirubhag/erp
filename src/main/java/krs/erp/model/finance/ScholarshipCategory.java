package krs.erp.model.finance;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Defines a type of scholarship or financial aid.
 */
@Data
@Entity
@Table(name = "erp_fin_scholarship_categories")
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
