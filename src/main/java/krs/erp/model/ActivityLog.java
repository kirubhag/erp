package krs.erp.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotNull;
import krs.erp.enums.ActionType;
import krs.erp.enums.LogSeverity;

/**
 * Entity representing an immutable activity log entry in the audit trail.
 * Captures all entity CRUD operations, system configurations, and sensitive
 * data exports.
 * 
 * IMPORTANT: This entity is IMMUTABLE - no update or delete operations are
 * allowed.
 * Once a log entry is created, it cannot be modified to ensure audit trail
 * integrity.
 */
@Entity
@Table(name = "erp_activity_logs", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_timestamp", columnList = "timestamp"),
        @Index(name = "idx_action_type", columnList = "action_type"),
        @Index(name = "idx_entity_type", columnList = "entity_type"),
        @Index(name = "idx_severity", columnList = "severity"),
        @Index(name = "idx_composite", columnList = "user_id, timestamp, action_type")
})
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== Actor Information ==========

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 255)
    private String username;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    // ========== Action Details ==========

    @NotNull(message = "Action type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ActionType actionType;

    @NotNull(message = "Severity is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private LogSeverity severity;

    // ========== Target Information ==========

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "module_name", length = 100)
    private String moduleName;

    // ========== Change Details (JSON) ==========

    @Column(name = "old_value", columnDefinition = "JSON")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "JSON")
    private String newValue;

    @Column(name = "delta_summary", columnDefinition = "TEXT")
    private String deltaSummary;

    // ========== Additional Context ==========

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    // ========== Timestamp ==========

    @NotNull
    @Column(name = "timestamp", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now(ZoneOffset.UTC);
    }

    // ========== Constructors ==========

    public ActivityLog() {
    }

    /**
     * Constructor for creating a basic activity log entry
     */
    public ActivityLog(Long userId, String username, ActionType actionType, LogSeverity severity) {
        this.userId = userId;
        this.username = username;
        this.actionType = actionType;
        this.severity = severity;
    }

    /**
     * Constructor for creating a complete activity log entry
     */
    public ActivityLog(Long userId, String username, String ipAddress, ActionType actionType,
            LogSeverity severity, String entityType, Long entityId, String description) {
        this.userId = userId;
        this.username = username;
        this.ipAddress = ipAddress;
        this.actionType = actionType;
        this.severity = severity;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
    }

    // ========== Getters and Setters ==========

    public Long getId() {
        return id;
    }

    // Note: No setId() method - ID is auto-generated and immutable

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public LogSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(LogSeverity severity) {
        this.severity = severity;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getDeltaSummary() {
        return deltaSummary;
    }

    public void setDeltaSummary(String deltaSummary) {
        this.deltaSummary = deltaSummary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Note: No setTimestamp() method - timestamp is set automatically on creation

    // ========== Helper Methods ==========

    /**
     * Returns a human-readable string representation of the action type
     */
    public String getActionTypeDisplay() {
        return actionType != null ? actionType.name().replace("_", " ") : "";
    }

    /**
     * Returns a human-readable string representation of the severity
     */
    public String getSeverityDisplay() {
        return severity != null ? severity.name() : "";
    }

    /**
     * Checks if this log entry represents a critical action
     */
    public boolean isCritical() {
        return severity == LogSeverity.CRITICAL;
    }

    /**
     * Checks if this log entry represents a warning
     */
    public boolean isWarning() {
        return severity == LogSeverity.WARNING;
    }

    /**
     * Checks if this log entry is informational
     */
    public boolean isInfo() {
        return severity == LogSeverity.INFO;
    }
}
