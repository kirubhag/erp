package krs.erp.model.enums;

/**
 * Enum for payment transaction status
 */
public enum PaymentStatus {
    PENDING("Payment pending processing"),
    PAID("Payment successful"),
    FAILED("Payment failed"),
    REFUNDED("Payment refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSuccessful() {
        return this == PAID;
    }
}
