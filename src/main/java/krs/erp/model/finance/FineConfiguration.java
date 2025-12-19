package krs.erp.model.finance;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_fee_fine_configs")
@Data
@EqualsAndHashCode(callSuper = true)
public class FineConfiguration extends BaseEntity {

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "calc_logic")
    private CalculationLogic calcLogic; // FLAT, PERCENTAGE

    @Column(name = "base_amount")
    private Double baseAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private FineFrequency frequency; // ONE_TIME, DAILY, WEEKLY

    @Column(name = "grace_period_days")
    private Integer gracePeriodDays = 0;

    @Column(name = "is_auto_post")
    private Boolean isAutoPost = true;

    public enum CalculationLogic {
        FLAT, PERCENTAGE
    }

    public enum FineFrequency {
        ONE_TIME, DAILY, WEEKLY
    }
}
