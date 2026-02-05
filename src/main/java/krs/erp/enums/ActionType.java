package krs.erp.enums;

/**
 * Enum representing different types of actions that can be logged in the
 * activity log.
 * Used for categorizing and filtering audit trail entries.
 */
public enum ActionType {
    // Entity CRUD Operations
    CREATE,
    UPDATE,
    DELETE,

    // Authentication Events
    LOGIN,
    LOGOUT,
    LOGIN_FAILED,

    // System Configuration
    SETTING_CHANGE,
    PERMISSION_CHANGE,

    // Sensitive Operations
    EXPORT,
    BULK_UPDATE,
    BULK_DELETE,

    // Other Operations
    VIEW,
    SEARCH
}
