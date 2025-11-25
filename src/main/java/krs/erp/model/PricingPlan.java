package krs.erp.model;

import jakarta.persistence.*;
import krs.erp.model.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity for pricing plans
 */
@Entity
@Table(name = "pricing_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_name", nullable = false, unique = true, length = 50)
    private String planName;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 20)
    private PlanType planType;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price_monthly", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceMonthly = BigDecimal.ZERO;

    @Column(name = "price_yearly", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceYearly = BigDecimal.ZERO;

    @Column(name = "max_users")
    private Integer maxUsers; // NULL = unlimited

    @Column(name = "max_storage_gb")
    private Integer maxStorageGb; // NULL = unlimited

    @Column(name = "features", columnDefinition = "JSON")
    private String features; // JSON array of features

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_trial_eligible")
    private Boolean isTrialEligible = false;

    @Column(name = "trial_days")
    private Integer trialDays = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isFree() {
        return planType == PlanType.FREE;
    }

    public boolean isPremium() {
        return planType == PlanType.PREMIUM;
    }

    public boolean hasUserLimit() {
        return maxUsers != null;
    }

    public boolean hasStorageLimit() {
        return maxStorageGb != null;
    }

    public BigDecimal getPrice(String billingCycle) {
        return "YEARLY".equalsIgnoreCase(billingCycle) ? priceYearly : priceMonthly;
    }
}
