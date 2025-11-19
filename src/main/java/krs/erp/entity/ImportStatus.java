package krs.erp.entity;

/**
 * Enum representing the status of an import session
 */
public enum ImportStatus {
    PENDING,      // Session created, waiting for user to start
    IN_PROGRESS,  // Import currently running
    COMPLETED,    // Import finished successfully
    FAILED,       // Import failed
    CANCELLED     // Import was cancelled by user
}
