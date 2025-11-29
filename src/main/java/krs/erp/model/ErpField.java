package krs.erp.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import krs.erp.enums.EntityType;
import krs.erp.enums.UIFieldType;

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

    @Column(name = "ui_type")
    private Integer uiType; // New UI Field Type (100-119 range)

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

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "validation_pattern", length = 500)
    private String validationPattern;

    @Column(name = "picklist_options", columnDefinition = "TEXT")
    private String picklistOptions; // JSON string for picklist options

    @Column(name = "decimal_places")
    private Integer decimalPlaces;

    @Column(name = "is_unique")
    private Boolean isUnique = false;

    @Column(name = "show_in_list")
    private Boolean showInList = true;

    @Column(name = "show_in_form")
    private Boolean showInForm = true;

    @Column(name = "column_width", length = 50)
    private String columnWidth = "medium";

    // Section relationship and positioning
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ErpSection section;

    @Column(name = "row_position")
    private Integer rowPosition = 0;

    @Column(name = "column_position")
    private Integer columnPosition = 0;

    // Constructors
    public ErpField() {
    }

    public ErpField(EntityType entityType, String fieldName, String fieldLabel, FieldType fieldType) {
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.fieldType = fieldType;
        setIsActive(1);
        setCreatedTime(LocalDateTime.now());
        setModifiedTime(LocalDateTime.now());
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

    public Integer getUiType() {
        return uiType;
    }

    public ErpField setUiType(Integer uiType) {
        this.uiType = uiType;
        return this;
    }

    public UIFieldType getUIFieldType() {
        return uiType != null ? UIFieldType.getById(uiType) : null;
    }

    public ErpField setUIFieldType(UIFieldType uiFieldType) {
        this.uiType = uiFieldType != null ? uiFieldType.getTypeId() : null;
        return this;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public ErpField setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public String getValidationPattern() {
        return validationPattern;
    }

    public ErpField setValidationPattern(String validationPattern) {
        this.validationPattern = validationPattern;
        return this;
    }

    public String getPicklistOptions() {
        return picklistOptions;
    }

    public ErpField setPicklistOptions(String picklistOptions) {
        this.picklistOptions = picklistOptions;
        return this;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public ErpField setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
        return this;
    }

    public Boolean getIsUnique() {
        return isUnique;
    }

    public ErpField setIsUnique(Boolean isUnique) {
        this.isUnique = isUnique;
        return this;
    }

    public Boolean getShowInList() {
        return showInList;
    }

    public ErpField setShowInList(Boolean showInList) {
        this.showInList = showInList;
        return this;
    }

    public Boolean getShowInForm() {
        return showInForm;
    }

    public ErpField setShowInForm(Boolean showInForm) {
        this.showInForm = showInForm;
        return this;
    }

    public String getColumnWidth() {
        return columnWidth;
    }

    public ErpField setColumnWidth(String columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }

    public ErpSection getSection() {
        return section;
    }

    public ErpField setSection(ErpSection section) {
        this.section = section;
        return this;
    }

    public Integer getRowPosition() {
        return rowPosition;
    }

    public ErpField setRowPosition(Integer rowPosition) {
        this.rowPosition = rowPosition;
        return this;
    }

    public Integer getColumnPosition() {
        return columnPosition;
    }

    public ErpField setColumnPosition(Integer columnPosition) {
        this.columnPosition = columnPosition;
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
                '}';
    }
}