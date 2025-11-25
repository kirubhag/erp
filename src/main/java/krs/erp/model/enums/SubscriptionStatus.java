package krs.erp.model.enums;

/**
 * Enum for subscription status
 */
public enum SubscriptionStatus {
    ACTIVE("Active - Full access to features"),
    TRIAL("Trial Period - Temporary premium access"),
    EXPIRED("Expired - Subscription ended"),
    CANCELLED("Cancelled - User terminated subscription"),
    SUSPENDED("Suspended - Payment failed or policy violation");

    private final String description;

    SubscriptionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == TRIAL;
    }
}
