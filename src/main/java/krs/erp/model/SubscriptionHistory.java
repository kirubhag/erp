package krs.erp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for subscription history (audit trail)
 */
@Entity
@Table(name = "subscription_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_history_id")
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "previous_plan_id")
    private Long previousPlanId;

    @Column(name = "new_plan_id", nullable = false)
    private Long newPlanId;

    @Column(name = "change_type", nullable = false, length = 20)
    private String changeType; // UPGRADE, DOWNGRADE, TRIAL_START, TRIAL_END, CANCELLATION, RENEWAL

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "changed_by")
    private Long changedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (effectiveDate == null) {
            effectiveDate = LocalDate.now();
        }
    }
}
