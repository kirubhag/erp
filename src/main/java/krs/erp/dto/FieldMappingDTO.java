package krs.erp.dto;

/**
 * DTO for field mapping
 */
public class FieldMappingDTO {
    
    private Long id;
    private String sourceColumn;
    private Integer sourceIndex;
    private String targetField;
    private String targetFieldLabel;
    private Boolean isRequired;
    private String dataType;
    
    // Constructors
    public FieldMappingDTO() {
    }
    
    public FieldMappingDTO(String sourceColumn, String targetField) {
        this.sourceColumn = sourceColumn;
        this.targetField = targetField;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSourceColumn() {
        return sourceColumn;
    }
    
    public void setSourceColumn(String sourceColumn) {
        this.sourceColumn = sourceColumn;
    }
    
    public Integer getSourceIndex() {
        return sourceIndex;
    }
    
    public void setSourceIndex(Integer sourceIndex) {
        this.sourceIndex = sourceIndex;
    }
    
    public String getTargetField() {
        return targetField;
    }
    
    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }
    
    public String getTargetFieldLabel() {
        return targetFieldLabel;
    }
    
    public void setTargetFieldLabel(String targetFieldLabel) {
        this.targetFieldLabel = targetFieldLabel;
    }
    
    public Boolean getIsRequired() {
        return isRequired;
    }
    
    public void setIsRequired(Boolean required) {
        isRequired = required;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
