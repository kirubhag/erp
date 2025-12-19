package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_fee_discount_rules")
@Data
@EqualsAndHashCode(callSuper = true)
public class FeeDiscountRule extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DiscountType type; // PERCENTAGE, FIXED

    @Column(name = "value")
    private Double value;

    @Column(name = "condition_type")
    private String conditionType; // SIBLING, MERIT, etc.

    public enum DiscountType {
        PERCENTAGE, FIXED
    }
}
