package krs.erp.dto;

import java.util.List;

/**
 * DTO for file preview
 */
public class ImportPreviewDTO {
    
    private Integer totalRows;
    private String[] headerRow;
    private List<String[]> sampleRows;
    private String detectedFormat;
    private Long fileSize;
    
    // Constructors
    public ImportPreviewDTO() {
    }
    
    public ImportPreviewDTO(Integer totalRows, String[] headerRow, String detectedFormat) {
        this.totalRows = totalRows;
        this.headerRow = headerRow;
        this.detectedFormat = detectedFormat;
    }
    
    // Getters and Setters
    public Integer getTotalRows() {
        return totalRows;
    }
    
    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }
    
    public String[] getHeaderRow() {
        return headerRow;
    }
    
    public void setHeaderRow(String[] headerRow) {
        this.headerRow = headerRow;
    }
    
    public List<String[]> getSampleRows() {
        return sampleRows;
    }
    
    public void setSampleRows(List<String[]> sampleRows) {
        this.sampleRows = sampleRows;
    }
    
    public String getDetectedFormat() {
        return detectedFormat;
    }
    
    public void setDetectedFormat(String detectedFormat) {
        this.detectedFormat = detectedFormat;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
