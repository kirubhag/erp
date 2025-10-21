package krs.erp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import krs.erp.enums.EntityType;

@Entity
@Table(name = "erp_fields")
public class ErpField extends BaseEntity {
    
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;
    
    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;
    
    @Column(name = "field_label", nullable = false, length = 200)
    private String fieldLabel;
    
    @Column(name = "field_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FieldType fieldType;
    
    @Column(name = "field_category", length = 100)
    private String fieldCategory; // PERSONAL, CONTACT, ACADEMIC, etc.
    
    @Column(name = "is_required")
    private Boolean isRequired = false;
    
    @Column(name = "is_searchable")
    private Boolean isSearchable = true;
    
    @Column(name = "is_sortable")
    private Boolean isSortable = true;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @Column(name = "field_description", length = 500)
    private String fieldDescription;
    
    @Column(name = "default_width")
    private Integer defaultWidth = 150;
    
    // Constructors
    public ErpField() {}
    
    public ErpField(EntityType entityType, String fieldName, String fieldLabel, FieldType fieldType) {
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.fieldType = fieldType;
        setIsActive(true);
        setCreatedAt(LocalDateTime.now());
        setUpdatedAt(LocalDateTime.now());
    }
    
    // Getters and Setters
    public EntityType getEntityType() {
        return entityType;
    }
    
    public void setEntityType(EntityType entityType) {
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
    
    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
    
    public String getFieldCategory() {
        return fieldCategory;
    }
    
    public ErpField setFieldCategory(String fieldCategory) {
        this.fieldCategory = fieldCategory;
        return this;
    }
    
    public Boolean getIsRequired() {
        return isRequired;
    }
    
    public ErpField setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
        return this;
    }
    
    public Boolean getIsSearchable() {
        return isSearchable;
    }
    
    public ErpField setIsSearchable(Boolean isSearchable) {
        this.isSearchable = isSearchable;
        return this;
    }
    
    public Boolean getIsSortable() {
        return isSortable;
    }
    
    public ErpField setIsSortable(Boolean isSortable) {
        this.isSortable = isSortable;
        return this;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public ErpField setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
        return this;
    }
    
    public String getFieldDescription() {
        return fieldDescription;
    }
    
    public ErpField setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
        return this;
    }
    
    public Integer getDefaultWidth() {
        return defaultWidth;
    }
    
    public ErpField setDefaultWidth(Integer defaultWidth) {
        this.defaultWidth = defaultWidth;
        return this;
    }
    
    @Override
    public String toString() {
        return "ErpField{" +
                "id=" + getId() +
                ", entityType=" + entityType +
                ", fieldName='" + fieldName + '\'' +
                ", fieldLabel='" + fieldLabel + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", fieldCategory='" + fieldCategory + '\'' +
                '}';
    }
}