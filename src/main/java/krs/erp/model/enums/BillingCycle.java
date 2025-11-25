package krs.erp.model.enums;

/**
 * Enum for billing cycle
 */
public enum BillingCycle {
    MONTHLY("Monthly Billing"),
    YEARLY("Yearly Billing - Save up to 20%"),
    LIFETIME("One-time Payment - Lifetime Access");

    private final String description;

    BillingCycle(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
