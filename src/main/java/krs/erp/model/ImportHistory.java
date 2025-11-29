package krs.erp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ImportHistory tracks data population events for audit and idempotency.
 * Used to prevent duplicate data loading and maintain audit trail of imports.
 */
@Entity
@Table(name = "import_history")
public class ImportHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "import_history_id")
    private Long id;
    
    @NotBlank(message = "Entity name is required")
    @Size(max = 100, message = "Entity name cannot exceed 100 characters")
    @Column(name = "entity_name", nullable = false, length = 100)
    private String entityName; // e.g., "PERMISSIONS", "ROLES", "STUDENTS", etc.
    
    @NotNull(message = "Import type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "import_type", nullable = false, length = 50)
    private ImportType importType; // e.g., SAMPLE_DATA, MANUAL_IMPORT, REGISTRATION
    
    @NotNull(message = "Record count is required")
    @Column(name = "record_count", nullable = false)
    private Integer recordCount = 0; // Number of records imported
    
    @Column(name = "source", length = 255)
    private String source; // e.g., "sample-students.xml", "CSV upload", "Form submission"
    
    @NotNull(message = "Imported by is required")
    @Size(max = 100, message = "Imported by cannot exceed 100 characters")
    @Column(name = "imported_by", nullable = false, length = 100)
    private String importedBy; // User who initiated the import (SYSTEM for auto-initialization)
    
    @Column(name = "import_start_time", nullable = false)
    private LocalDateTime importStartTime;
    
    @Column(name = "import_end_time")
    private LocalDateTime importEndTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "import_status", nullable = false, length = 20)
    private ImportStatus importStatus; // SUCCESS, FAILED, PARTIAL
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage; // Error details if import failed
    
    @Column(name = "notes", length = 500)
    private String notes; // Additional notes about the import
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Enums
    public enum ImportType {
        SAMPLE_DATA,      // Loading sample data from XML
        MANUAL_IMPORT,    // Manual CSV/Excel import by user
        REGISTRATION,     // Data from organization/user registration
        MIGRATION,        // Data migration from other system
        SYNC              // Data synchronization
    }
    
    public enum ImportStatus {
        PENDING,    // Import in progress
        SUCCESS,    // Import completed successfully
        FAILED,     // Import failed
        PARTIAL     // Some records imported, some failed
    }
    
    // Constructors
    public ImportHistory() {
        this.createdAt = LocalDateTime.now();
        this.importStartTime = LocalDateTime.now();
        this.importStatus = ImportStatus.PENDING;
    }
    
    public ImportHistory(String entityName, ImportType importType, String importedBy, String source) {
        this();
        this.entityName = entityName;
        this.importType = importType;
        this.importedBy = importedBy;
        this.source = source;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEntityName() {
        return entityName;
    }
    
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
    public ImportType getImportType() {
        return importType;
    }
    
    public void setImportType(ImportType importType) {
        this.importType = importType;
    }
    
    public Integer getRecordCount() {
        return recordCount;
    }
    
    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getImportedBy() {
        return importedBy;
    }
    
    public void setImportedBy(String importedBy) {
        this.importedBy = importedBy;
    }
    
    public LocalDateTime getImportStartTime() {
        return importStartTime;
    }
    
    public void setImportStartTime(LocalDateTime importStartTime) {
        this.importStartTime = importStartTime;
    }
    
    public LocalDateTime getImportEndTime() {
        return importEndTime;
    }
    
    public void setImportEndTime(LocalDateTime importEndTime) {
        this.importEndTime = importEndTime;
    }
    
    public ImportStatus getImportStatus() {
        return importStatus;
    }
    
    public void setImportStatus(ImportStatus importStatus) {
        this.importStatus = importStatus;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    // Helper methods
    public void markAsCompleted(int recordCount) {
        this.recordCount = recordCount;
        this.importEndTime = LocalDateTime.now();
        this.importStatus = ImportStatus.SUCCESS;
    }
    
    public void markAsFailed(String errorMessage) {
        this.importEndTime = LocalDateTime.now();
        this.importStatus = ImportStatus.FAILED;
        this.errorMessage = errorMessage;
    }
    
    public void markAsPartial(int recordCount, String errorMessage) {
        this.recordCount = recordCount;
        this.importEndTime = LocalDateTime.now();
        this.importStatus = ImportStatus.PARTIAL;
        this.errorMessage = errorMessage;
    }
    
    public long getDurationInSeconds() {
        if (importEndTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.SECONDS.between(importStartTime, importEndTime);
    }
    
    @Override
    public String toString() {
        return "ImportHistory{" +
                "id=" + id +
                ", entityName='" + entityName + '\'' +
                ", importType=" + importType +
                ", recordCount=" + recordCount +
                ", source='" + source + '\'' +
                ", importedBy='" + importedBy + '\'' +
                ", importStatus=" + importStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}
