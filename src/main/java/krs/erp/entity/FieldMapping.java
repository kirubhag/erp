package krs.erp.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Entity representing the mapping between a CSV column and an entity field
 */
@Entity
@Table(name = "erp_field_mappings")
public class FieldMapping implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_mapping_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_session_id", nullable = false)
    private ImportSession importSession;
    
    @Column(name = "source_column")
    private String sourceColumn; // Column name from CSV header
    
    @Column(name = "source_index")
    private Integer sourceIndex; // Column index (0-based)
    
    @Column(name = "target_field")
    private String targetField; // Entity field name
    
    @Column(name = "target_field_label")
    private String targetFieldLabel; // Display label
    
    @Column(name = "is_required")
    private Boolean isRequired = false;
    
    @Column(name = "data_type", length = 50)
    private String dataType; // string, email, phone, integer, etc.
    
    // Constructors
    public FieldMapping() {
    }
    
    public FieldMapping(String sourceColumn, Integer sourceIndex, String targetField, String targetFieldLabel, Boolean isRequired, String dataType) {
        this.sourceColumn = sourceColumn;
        this.sourceIndex = sourceIndex;
        this.targetField = targetField;
        this.targetFieldLabel = targetFieldLabel;
        this.isRequired = isRequired;
        this.dataType = dataType;
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
        return isRequired != null && isRequired;
    }
    
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    /**
     * Check if mapping is valid (target field is set for required fields)
     */
    @Transient
    public boolean isMappingValid() {
        if (Boolean.TRUE.equals(isRequired)) {
            return targetField != null && !targetField.trim().isEmpty();
        }
        return true;
    }
}
