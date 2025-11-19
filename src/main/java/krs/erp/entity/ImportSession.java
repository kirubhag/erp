package krs.erp.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Entity representing an import session for bulk data import operations.
 * Tracks file upload, field mappings, import settings, and results.
 */
@Entity
@Table(name = "import_sessions")
public class ImportSession implements Serializable {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "organization_id")
    private Long organizationId;
    
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // students, candidates, contacts, users
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_format", length = 20)
    private String fileFormat; // csv, xlsx, xls, vcf
    
    @Column(name = "total_records")
    private Integer totalRecords;
    
    @Column(name = "file_size")
    private Long fileSize; // in bytes
    
    @Column(name = "import_type", length = 20)
    @Enumerated(EnumType.STRING)
    private ImportType importType; // PERSONAL, ORGANIZATION
    
    @Column(name = "duplicate_action", length = 20)
    @Enumerated(EnumType.STRING)
    private DuplicateAction duplicateAction; // SKIP, OVERWRITE, CLONE
    
    @Column(name = "find_duplicates_by", length = 50)
    private String findDuplicatesBy; // email, phone, name, id
    
    @Column(name = "enable_manual_approval")
    private Boolean enableManualApproval = false;
    
    @Column(name = "skip_empty_fields")
    private Boolean skipEmptyFields = false;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private ImportStatus status; // PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
    
    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    
    @Column(name = "imported_at")
    private LocalDateTime importedAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Statistics
    @Column(name = "added_records")
    private Integer addedRecords = 0;
    
    @Column(name = "updated_records")
    private Integer updatedRecords = 0;
    
    @Column(name = "skipped_records")
    private Integer skippedRecords = 0;
    
    @Column(name = "failed_records")
    private Integer failedRecords = 0;
    
    @Column(name = "success_rate")
    private Double successRate = 0.0;
    
    // Relationships
    @OneToMany(mappedBy = "importSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FieldMapping> fieldMappings = new ArrayList<>();
    
    @OneToMany(mappedBy = "importSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ImportResult> importedRecords = new ArrayList<>();
    
    @Transient
    private String[] headerRow;
    
    @Transient
    private String[] unmappedColumns;
    
    // Constructors
    public ImportSession() {
    }
    
    public ImportSession(String id, Long userId, Long organizationId, String entityType, String fileName, 
                        String fileFormat, Integer totalRecords, Long fileSize, ImportType importType, 
                        DuplicateAction duplicateAction, String findDuplicatesBy, Boolean enableManualApproval,
                        Boolean skipEmptyFields, ImportStatus status) {
        this.id = id;
        this.userId = userId;
        this.organizationId = organizationId;
        this.entityType = entityType;
        this.fileName = fileName;
        this.fileFormat = fileFormat;
        this.totalRecords = totalRecords;
        this.fileSize = fileSize;
        this.importType = importType;
        this.duplicateAction = duplicateAction;
        this.findDuplicatesBy = findDuplicatesBy;
        this.enableManualApproval = enableManualApproval;
        this.skipEmptyFields = skipEmptyFields;
        this.status = status;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileFormat() {
        return fileFormat;
    }
    
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
    
    public Integer getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public ImportType getImportType() {
        return importType;
    }
    
    public void setImportType(ImportType importType) {
        this.importType = importType;
    }
    
    public DuplicateAction getDuplicateAction() {
        return duplicateAction;
    }
    
    public void setDuplicateAction(DuplicateAction duplicateAction) {
        this.duplicateAction = duplicateAction;
    }
    
    public String getFindDuplicatesBy() {
        return findDuplicatesBy;
    }
    
    public void setFindDuplicatesBy(String findDuplicatesBy) {
        this.findDuplicatesBy = findDuplicatesBy;
    }
    
    public Boolean getEnableManualApproval() {
        return enableManualApproval;
    }
    
    public void setEnableManualApproval(Boolean enableManualApproval) {
        this.enableManualApproval = enableManualApproval;
    }
    
    public Boolean getSkipEmptyFields() {
        return skipEmptyFields;
    }
    
    public void setSkipEmptyFields(Boolean skipEmptyFields) {
        this.skipEmptyFields = skipEmptyFields;
    }
    
    public ImportStatus getStatus() {
        return status;
    }
    
    public void setStatus(ImportStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public LocalDateTime getImportedAt() {
        return importedAt;
    }
    
    public void setImportedAt(LocalDateTime importedAt) {
        this.importedAt = importedAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Integer getAddedRecords() {
        return addedRecords != null ? addedRecords : 0;
    }
    
    public void setAddedRecords(Integer addedRecords) {
        this.addedRecords = addedRecords;
    }
    
    public Integer getUpdatedRecords() {
        return updatedRecords != null ? updatedRecords : 0;
    }
    
    public void setUpdatedRecords(Integer updatedRecords) {
        this.updatedRecords = updatedRecords;
    }
    
    public Integer getSkippedRecords() {
        return skippedRecords != null ? skippedRecords : 0;
    }
    
    public void setSkippedRecords(Integer skippedRecords) {
        this.skippedRecords = skippedRecords;
    }
    
    public Integer getFailedRecords() {
        return failedRecords != null ? failedRecords : 0;
    }
    
    public void setFailedRecords(Integer failedRecords) {
        this.failedRecords = failedRecords;
    }
    
    public Double getSuccessRate() {
        return successRate != null ? successRate : 0.0;
    }
    
    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }
    
    public List<FieldMapping> getFieldMappings() {
        return fieldMappings;
    }
    
    public void setFieldMappings(List<FieldMapping> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }
    
    public List<ImportResult> getImportedRecords() {
        return importedRecords;
    }
    
    public void setImportedRecords(List<ImportResult> importedRecords) {
        this.importedRecords = importedRecords;
    }
    
    public String[] getHeaderRow() {
        return headerRow;
    }
    
    public void setHeaderRow(String[] headerRow) {
        this.headerRow = headerRow;
    }
    
    public String[] getUnmappedColumns() {
        return unmappedColumns;
    }
    
    public void setUnmappedColumns(String[] unmappedColumns) {
        this.unmappedColumns = unmappedColumns;
    }
    
    /**
     * Calculate success rate based on import statistics
     */
    public void calculateSuccessRate() {
        if (totalRecords == null || totalRecords == 0) {
            this.successRate = 0.0;
        } else {
            int successCount = (addedRecords != null ? addedRecords : 0) + 
                               (updatedRecords != null ? updatedRecords : 0);
            this.successRate = (double) successCount / totalRecords * 100;
        }
    }
    
    /**
     * Update statistics from imported records
     */
    public void updateStatisticsFromResults() {
        this.addedRecords = 0;
        this.updatedRecords = 0;
        this.skippedRecords = 0;
        this.failedRecords = 0;
        
        if (importedRecords != null) {
            for (ImportResult result : importedRecords) {
                switch (result.getStatus()) {
                    case ADDED -> this.addedRecords++;
                    case UPDATED -> this.updatedRecords++;
                    case SKIPPED -> this.skippedRecords++;
                    case FAILED -> this.failedRecords++;
                }
            }
        }
        
        calculateSuccessRate();
    }
    
    /**
     * Check if all required fields are mapped
     */
    public boolean areAllRequiredFieldsMapped() {
        if (fieldMappings == null || fieldMappings.isEmpty()) {
            return false;
        }
        
        return fieldMappings.stream()
                .filter(FieldMapping::getIsRequired)
                .allMatch(fm -> fm.getTargetField() != null && !fm.getTargetField().isEmpty());
    }
    
    /**
     * Get unmapped columns from field mappings as list
     */
    public List<String> getUnmappedColumnsList() {
        if (fieldMappings == null) {
            return new ArrayList<>();
        }
        
        return fieldMappings.stream()
                .filter(fm -> fm.getTargetField() == null || fm.getTargetField().isEmpty())
                .map(FieldMapping::getSourceColumn)
                .toList();
    }
}
