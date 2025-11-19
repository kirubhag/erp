package krs.erp.entity;

/**
 * Enum representing how to handle duplicate records during import
 */
public enum DuplicateAction {
    SKIP,       // Don't import if duplicate exists
    OVERWRITE,  // Replace existing record with new data
    CLONE       // Create new record even if duplicate exists
}
