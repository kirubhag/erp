package krs.erp.entity;

/**
 * Enum representing the result status of a single imported record
 */
public enum ImportResultStatus {
    ADDED,      // New record was created
    UPDATED,    // Existing record was updated
    SKIPPED,    // Record was skipped (duplicate or validation error)
    FAILED      // Record import failed with error
}
