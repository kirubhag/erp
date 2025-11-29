package krs.erp.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Entity representing the result of importing a single record
 */
@Entity
@Table(name = "import_results")
public class ImportResult implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "import_result_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_session_id", nullable = false)
    private ImportSession importSession;
    
    @Column(name = "row_num")
    private Integer rowNumber;
    
    @Column(name = "record_id")
    private String recordId;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImportResultStatus status; // ADDED, UPDATED, SKIPPED, FAILED
    
    @Column(name = "data", columnDefinition = "TEXT")
    private String data; // JSON string of record data
    
    @Column(name = "errors", columnDefinition = "TEXT")
    private String errors; // JSON string of error messages
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public ImportResult() {
    }
    
    public ImportResult(ImportSession importSession, Integer rowNumber, String recordId, ImportResultStatus status) {
        this.importSession = importSession;
        this.rowNumber = rowNumber;
        this.recordId = recordId;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ImportSession getImportSession() {
        return importSession;
    }
    
    public void setImportSession(ImportSession importSession) {
        this.importSession = importSession;
    }
    
    public Integer getRowNumber() {
        return rowNumber;
    }
    
    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }
    
    public String getRecordId() {
        return recordId;
    }
    
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    
    public ImportResultStatus getStatus() {
        return status;
    }
    
    public void setStatus(ImportResultStatus status) {
        this.status = status;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public String getErrors() {
        return errors;
    }
    
    public void setErrors(String errors) {
        this.errors = errors;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Get error messages as a list (from JSON string)
     */
    @Transient
    public List<String> getErrorList() {
        if (errors == null || errors.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(errors.split(","));
    }
    
    /**
     * Add error message
     */
    public void addError(String error) {
        if (this.errors == null || this.errors.isEmpty()) {
            this.errors = error;
        } else {
            this.errors += "," + error;
        }
    }
}
