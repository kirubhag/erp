package krs.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_subscriptions")
@Data
public class ErpSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private ErpPlan plan;

    @Column(name = "razorpay_subscription_id")
    private String razorpaySubscriptionId;

    @Column(name = "razorpay_customer_id")
    private String razorpayCustomerId;

    private String status; // CREATED, AUTHENTICATED, ACTIVE, PAST_DUE, CANCELLED

    @Column(name = "current_period_start")
    private LocalDateTime currentPeriodStart;

    @Column(name = "current_period_end")
    private LocalDateTime currentPeriodEnd;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
