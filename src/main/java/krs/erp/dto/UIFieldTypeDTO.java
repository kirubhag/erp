package krs.erp.dto;

import krs.erp.enums.UIFieldType;

/**
 * Data Transfer Object for UI Field Type metadata
 * Provides comprehensive information about field types for frontend rendering
 */
public class UIFieldTypeDTO {

    private int typeId;
    private String displayName;
    private String dataType;
    private String category;
    private String htmlInputType;
    private Integer maxLength;
    private String validationPattern;
    private boolean hasOptions;
    private boolean isMultiSelect;
    private boolean isRelationship;
    private boolean isFileUpload;
    private boolean isNumeric;
    private boolean isDateTime;
    private Integer defaultDecimalPlaces;

    // Constructors
    public UIFieldTypeDTO() {
    }

    /**
     * Create DTO from UIFieldType enum
     */
    public static UIFieldTypeDTO fromEnum(UIFieldType uiFieldType) {
        UIFieldTypeDTO dto = new UIFieldTypeDTO();
        dto.setTypeId(uiFieldType.getTypeId());
        dto.setDisplayName(uiFieldType.getDisplayName());
        dto.setDataType(uiFieldType.getDataType());
        dto.setCategory(uiFieldType.getCategory());
        dto.setHtmlInputType(uiFieldType.getHtmlInputType());
        dto.setMaxLength(uiFieldType.getMaxLength());
        dto.setValidationPattern(uiFieldType.getValidationPattern());
        dto.setHasOptions(uiFieldType.hasOptions());
        dto.setMultiSelect(uiFieldType.isMultiSelect());
        dto.setRelationship(uiFieldType.isRelationship());
        dto.setFileUpload(uiFieldType.isFileUpload());
        dto.setNumeric(uiFieldType.isNumeric());
        dto.setDateTime(uiFieldType.isDateTime());

        // Set default decimal places for numeric types
        if (uiFieldType.isNumeric() &&
                (uiFieldType == UIFieldType.CURRENCY ||
                        uiFieldType == UIFieldType.DECIMAL ||
                        uiFieldType == UIFieldType.PERCENT)) {
            dto.setDefaultDecimalPlaces(2);
        }

        return dto;
    }

    // Getters and Setters
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHtmlInputType() {
        return htmlInputType;
    }

    public void setHtmlInputType(String htmlInputType) {
        this.htmlInputType = htmlInputType;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public String getValidationPattern() {
        return validationPattern;
    }

    public void setValidationPattern(String validationPattern) {
        this.validationPattern = validationPattern;
    }

    public boolean isHasOptions() {
        return hasOptions;
    }

    public void setHasOptions(boolean hasOptions) {
        this.hasOptions = hasOptions;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
    }

    public boolean isRelationship() {
        return isRelationship;
    }

    public void setRelationship(boolean relationship) {
        isRelationship = relationship;
    }

    public boolean isFileUpload() {
        return isFileUpload;
    }

    public void setFileUpload(boolean fileUpload) {
        isFileUpload = fileUpload;
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    public void setNumeric(boolean numeric) {
        isNumeric = numeric;
    }

    public boolean isDateTime() {
        return isDateTime;
    }

    public void setDateTime(boolean dateTime) {
        isDateTime = dateTime;
    }

    public Integer getDefaultDecimalPlaces() {
        return defaultDecimalPlaces;
    }

    public void setDefaultDecimalPlaces(Integer defaultDecimalPlaces) {
        this.defaultDecimalPlaces = defaultDecimalPlaces;
    }
}
