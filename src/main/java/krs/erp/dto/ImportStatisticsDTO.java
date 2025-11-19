package krs.erp.dto;

/**
 * DTO for import statistics
 */
public class ImportStatisticsDTO {
    
    private Integer totalRecords;
    private Integer addedRecords;
    private Integer updatedRecords;
    private Integer skippedRecords;
    private Integer failedRecords;
    private Double successRate;
    
    // Constructors
    public ImportStatisticsDTO() {
    }
    
    public ImportStatisticsDTO(Integer totalRecords, Integer addedRecords, Integer updatedRecords, 
                               Integer skippedRecords, Integer failedRecords, Double successRate) {
        this.totalRecords = totalRecords;
        this.addedRecords = addedRecords;
        this.updatedRecords = updatedRecords;
        this.skippedRecords = skippedRecords;
        this.failedRecords = failedRecords;
        this.successRate = successRate;
    }
    
    // Getters and Setters
    public Integer getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public Integer getAddedRecords() {
        return addedRecords;
    }
    
    public void setAddedRecords(Integer addedRecords) {
        this.addedRecords = addedRecords;
    }
    
    public Integer getUpdatedRecords() {
        return updatedRecords;
    }
    
    public void setUpdatedRecords(Integer updatedRecords) {
        this.updatedRecords = updatedRecords;
    }
    
    public Integer getSkippedRecords() {
        return skippedRecords;
    }
    
    public void setSkippedRecords(Integer skippedRecords) {
        this.skippedRecords = skippedRecords;
    }
    
    public Integer getFailedRecords() {
        return failedRecords;
    }
    
    public void setFailedRecords(Integer failedRecords) {
        this.failedRecords = failedRecords;
    }
    
    public Double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }
}
