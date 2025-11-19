package krs.erp.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for import result
 */
public class ImportResultDTO {
    
    private Long id;
    private Integer rowNumber;
    private String recordId;
    private String status;
    private String data;
    private List<String> errors;
    private LocalDateTime createdAt;
    
    // Constructors
    public ImportResultDTO() {
    }
    
    public ImportResultDTO(Integer rowNumber, String recordId, String status) {
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
