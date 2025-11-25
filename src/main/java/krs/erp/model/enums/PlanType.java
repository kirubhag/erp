package krs.erp.model.enums;

/**
 * Enum for pricing plan types
 */
public enum PlanType {
    FREE("Free Plan - Limited Features"),
    BASIC("Basic Plan - Essential Features"),
    STANDARD("Standard Plan - Advanced Features"),
    PREMIUM("Premium Plan - All Features");

    private final String description;

    PlanType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFree() {
        return this == FREE;
    }

    public boolean isPaid() {
        return this != FREE;
    }
}
