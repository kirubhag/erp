package krs.erp.model;

import jakarta.persistence.*;
import krs.erp.model.enums.BillingCycle;
import krs.erp.model.enums.PaymentStatus;
import krs.erp.model.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for user subscriptions
 */
@Entity
@Table(name = "user_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", insertable = false, updatable = false)
    private PricingPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false, length = 20)
    private SubscriptionStatus subscriptionStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", length = 20)
    private BillingCycle billingCycle;

    @Column(name = "trial_start_date")
    private LocalDate trialStartDate;

    @Column(name = "trial_end_date")
    private LocalDate trialEndDate;

    @Column(name = "subscription_start_date", nullable = false)
    private LocalDate subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "is_auto_renew")
    private Boolean isAutoRenew = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private PaymentStatus paymentStatus;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

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
    public boolean isActive() {
        return subscriptionStatus != null && subscriptionStatus.isActive();
    }

    public boolean isTrial() {
        return subscriptionStatus == SubscriptionStatus.TRIAL;
    }

    public boolean isTrialExpiring() {
        return isTrial() && trialEndDate != null && 
               !LocalDate.now().isAfter(trialEndDate);
    }

    public boolean isTrialExpired() {
        return isTrial() && trialEndDate != null && 
               LocalDate.now().isAfter(trialEndDate);
    }

    public long getDaysUntilTrialExpires() {
        if (trialEndDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), trialEndDate);
    }
}
