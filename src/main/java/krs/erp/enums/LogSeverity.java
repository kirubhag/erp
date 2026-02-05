package krs.erp.enums;

/**
 * Enum representing the severity level of logged activities.
 * Used for color-coding and prioritizing audit trail entries in the UI.
 * 
 * - INFO: Informational events (logins, views) - displayed in blue
 * - WARNING: Modification events (updates, changes) - displayed in yellow
 * - CRITICAL: High-risk events (deletions, exports, permission changes) -
 * displayed in red
 */
public enum LogSeverity {
    INFO, // Blue - Informational events
    WARNING, // Yellow - Modification events
    CRITICAL // Red - High-risk events
}
