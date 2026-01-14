package krs.erp.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Entity representing available fields for each entity type during import
 */
@Entity
@Table(name = "erp_field_mapping_templates")
public class FieldMappingTemplate implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_mapping_template_id")
    private Long id;
    
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // students, candidates, contacts, users
    
    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;
    
    @Column(name = "field_label", nullable = false)
    private String fieldLabel;
    
    @Column(name = "is_required")
    private Boolean isRequired = false;
    
    @Column(name = "data_type", length = 50)
    private String dataType; // string, email, phone, integer, date, etc.
    
    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions; // JSON array or comma-separated
    
    @Column(name = "section", length = 100)
    private String section; // Basic Info, Contact Info, etc.
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    // Constructors
    public FieldMappingTemplate() {
    }
    
    public FieldMappingTemplate(String entityType, String fieldName, String fieldLabel, Boolean isRequired, String dataType) {
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
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
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getFieldLabel() {
        return fieldLabel;
    }
    
    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
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
    
    public String getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }
    
    public String getSection() {
        return section;
    }
    
    public void setSection(String section) {
        this.section = section;
    }
    
    public Integer getDisplayOrder() {
        Integer order = displayOrder;
        return order != null ? order : 0;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    /**
     * Get suggestions as a list
     */
    @Transient
    public List<String> getSuggestionsList() {
        if (suggestions == null || suggestions.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(suggestions.split(","));
    }
    
    /**
     * Set suggestions from a list
     */
    public void setSuggestionsList(List<String> suggestionsList) {
        if (suggestionsList == null || suggestionsList.isEmpty()) {
            this.suggestions = null;
        } else {
            this.suggestions = String.join(",", suggestionsList);
        }
    }
}
