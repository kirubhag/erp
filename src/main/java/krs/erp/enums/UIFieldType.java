package krs.erp.enums;

/**
 * Enum representing UI Field Types for the ERP system (100-119 range)
 * These are Lightning/Salesforce-style UI field types for comprehensive field management
 */
public enum UIFieldType {
    // Basic Input Types (100-106)
    SINGLE_LINE_TEXT(100, "Single Line Text", "VARCHAR", "Basic Input", "text", 255),
    MULTI_LINE_TEXT(101, "Multi Line Text", "TEXT", "Basic Input", "textarea", null),
    EMAIL(102, "Email", "VARCHAR", "Contact Information", "email", 255),
    PHONE(103, "Phone Number", "VARCHAR", "Contact Information", "tel", 20),
    PICKLIST(104, "Pick List", "VARCHAR", "Selection Types", "select", 255),
    MULTI_SELECT(105, "Multi-Select Picklist", "JSON", "Selection Types", "multi-select", null),
    DATE(106, "Date", "DATE", "Date & Time", "date", null),
    DATETIME(107, "Date/Time", "DATETIME", "Date & Time", "datetime-local", null),
    NUMBER(108, "Number", "INT", "Basic Input", "number", null),
    AUTO_NUMBER(109, "Auto Number", "INT", "Basic Input", "number", null),
    CURRENCY(110, "Currency", "DECIMAL", "Basic Input", "number", null),
    DECIMAL(111, "Decimal", "DECIMAL", "Basic Input", "number", null),
    PERCENT(112, "Percent", "DECIMAL", "Basic Input", "number", null),
    LONG_INTEGER(113, "Long Integer", "BIGINT", "Basic Input", "number", null),
    CHECKBOX(114, "Checkbox", "BOOLEAN", "Selection Types", "checkbox", null),
    LOOKUP(115, "Lookup", "BIGINT", "Relationship", "select", null),
    RADIO(116, "Radio Button", "VARCHAR", "Selection Types", "radio", 255),
    FILE_UPLOAD(117, "File Upload", "VARCHAR", "File & Media", "file", 500),
    IMAGE_UPLOAD(118, "Image Upload", "VARCHAR", "File & Media", "file", 500),
    URL(119, "URL", "VARCHAR", "Contact Information", "url", 500);

    private final int typeId;
    private final String displayName;
    private final String dataType;
    private final String category;
    private final String htmlInputType;
    private final Integer maxLength;

    UIFieldType(int typeId, String displayName, String dataType, String category, String htmlInputType, Integer maxLength) {
        this.typeId = typeId;
        this.displayName = displayName;
        this.dataType = dataType;
        this.category = category;
        this.htmlInputType = htmlInputType;
        this.maxLength = maxLength;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getCategory() {
        return category;
    }

    public String getHtmlInputType() {
        return htmlInputType;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * Get UIFieldType by ID
     */
    public static UIFieldType getById(int typeId) {
        for (UIFieldType type : UIFieldType.values()) {
            if (type.getTypeId() == typeId) {
                return type;
            }
        }
        return null;
    }

    /**
     * Check if the UI field type supports options (picklist, radio, etc.)
     */
    public boolean hasOptions() {
        return this == PICKLIST || this == MULTI_SELECT || this == RADIO;
    }

    /**
     * Check if the UI field type supports multiple selections
     */
    public boolean isMultiSelect() {
        return this == MULTI_SELECT;
    }

    /**
     * Check if the UI field type is a relationship field
     */
    public boolean isRelationship() {
        return this == LOOKUP;
    }

    /**
     * Check if the UI field type is a file upload field
     */
    public boolean isFileUpload() {
        return this == FILE_UPLOAD || this == IMAGE_UPLOAD;
    }

    /**
     * Check if the UI field type is numeric
     */
    public boolean isNumeric() {
        return this == NUMBER || this == AUTO_NUMBER || this == CURRENCY || 
               this == DECIMAL || this == PERCENT || this == LONG_INTEGER;
    }

    /**
     * Check if the UI field type is date/time related
     */
    public boolean isDateTime() {
        return this == DATE || this == DATETIME;
    }

    /**
     * Get suggested validation pattern for the UI field type
     */
    public String getValidationPattern() {
        switch (this) {
            case EMAIL:
                return "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            case PHONE:
                return "^[+]?[0-9\\-\\s\\(\\)]{7,20}$";
            case URL:
                return "^https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)$";
            default:
                return null;
        }
    }

    /**
     * Get default field configuration for the UI type
     */
    public FieldConfiguration getDefaultConfiguration() {
        FieldConfiguration config = new FieldConfiguration();
        config.setMaxLength(this.maxLength);
        config.setValidationPattern(getValidationPattern());
        config.setHasOptions(hasOptions());
        config.setIsMultiSelect(isMultiSelect());
        config.setIsRelationship(isRelationship());
        config.setIsFileUpload(isFileUpload());
        
        // Set default decimal places for numeric types
        if (this == CURRENCY || this == DECIMAL || this == PERCENT) {
            config.setDecimalPlaces(2);
        }
        
        return config;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Helper class to hold field configuration
     */
    public static class FieldConfiguration {
        private Integer maxLength;
        private String validationPattern;
        private boolean hasOptions;
        private boolean isMultiSelect;
        private boolean isRelationship;
        private boolean isFileUpload;
        private Integer decimalPlaces;

        // Getters and setters
        public Integer getMaxLength() { return maxLength; }
        public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
        
        public String getValidationPattern() { return validationPattern; }
        public void setValidationPattern(String validationPattern) { this.validationPattern = validationPattern; }
        
        public boolean isHasOptions() { return hasOptions; }
        public void setHasOptions(boolean hasOptions) { this.hasOptions = hasOptions; }
        
        public boolean isMultiSelect() { return isMultiSelect; }
        public void setIsMultiSelect(boolean isMultiSelect) { this.isMultiSelect = isMultiSelect; }
        
        public boolean isRelationship() { return isRelationship; }
        public void setIsRelationship(boolean isRelationship) { this.isRelationship = isRelationship; }
        
        public boolean isFileUpload() { return isFileUpload; }
        public void setIsFileUpload(boolean isFileUpload) { this.isFileUpload = isFileUpload; }
        
        public Integer getDecimalPlaces() { return decimalPlaces; }
        public void setDecimalPlaces(Integer decimalPlaces) { this.decimalPlaces = decimalPlaces; }
    }
}