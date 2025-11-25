package krs.erp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for payment transactions
 */
@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 100)
    private String transactionId; // External gateway transaction ID

    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType; // SUBSCRIPTION, UPGRADE, DOWNGRADE, REFUND

    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, PAYPAL, etc.

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @Column(name = "transaction_status", nullable = false, length = 20)
    private String transactionStatus; // PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway; // STRIPE, PAYPAL, RAZORPAY, etc.

    @Column(name = "gateway_response", columnDefinition = "JSON")
    private String gatewayResponse; // Full response from gateway

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }

    // Helper methods
    public boolean isSuccessful() {
        return "SUCCESS".equals(transactionStatus);
    }

    public boolean isPending() {
        return "PENDING".equals(transactionStatus);
    }

    public boolean isFailed() {
        return "FAILED".equals(transactionStatus);
    }
}
