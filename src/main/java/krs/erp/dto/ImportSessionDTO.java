package krs.erp.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for import session responses
 */
public class ImportSessionDTO {
    
    private String id;
    private String fileName;
    private String fileFormat;
    private Integer totalRecords;
    private Long fileSize;
    private String importType;
    private String status;
    private LocalDateTime uploadedAt;
    private LocalDateTime importedAt;
    private List<FieldMappingDTO> fieldMappings;
    private List<String> unmappedColumns;
    private ImportStatisticsDTO statistics;
    private List<ImportResultDTO> importedRecords;
    
    // Constructors
    public ImportSessionDTO() {
    }
    
    public ImportSessionDTO(String id, String fileName, String fileFormat, Integer totalRecords, String status) {
        this.id = id;
        this.fileName = fileName;
        this.fileFormat = fileFormat;
        this.totalRecords = totalRecords;
        this.status = status;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public String getImportType() {
        return importType;
    }
    
    public void setImportType(String importType) {
        this.importType = importType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
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
    
    public List<FieldMappingDTO> getFieldMappings() {
        return fieldMappings;
    }
    
    public void setFieldMappings(List<FieldMappingDTO> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }
    
    public List<String> getUnmappedColumns() {
        return unmappedColumns;
    }
    
    public void setUnmappedColumns(List<String> unmappedColumns) {
        this.unmappedColumns = unmappedColumns;
    }
    
    public ImportStatisticsDTO getStatistics() {
        return statistics;
    }
    
    public void setStatistics(ImportStatisticsDTO statistics) {
        this.statistics = statistics;
    }
    
    public List<ImportResultDTO> getImportedRecords() {
        return importedRecords;
    }
    
    public void setImportedRecords(List<ImportResultDTO> importedRecords) {
        this.importedRecords = importedRecords;
    }
}
