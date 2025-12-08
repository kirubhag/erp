package krs.erp.model;

/**
 * Enum representing different field data types in the ERP system
 * Used for field metadata and UI rendering configuration
 */
public enum FieldType {
    // Basic data types
    TEXT("Text", "text"),
    STRING("String", "string"),
    NUMERIC("Numeric", "number"),
    INTEGER("Integer", "number"),
    LONG("Long", "number"),
    DECIMAL("Decimal", "number"),
    BOOLEAN("Boolean", "checkbox"),

    // Date and time types
    DATE("Date", "date"),
    TIME("Time", "time"),
    DATETIME("Date Time", "datetime-local"),

    // Contact types
    EMAIL("Email", "email"),
    PHONE("Phone", "tel"),
    URL("URL", "url"),

    // Selection types
    SELECT("Select", "select"),
    PICKLIST("Picklist", "select"),
    MULTI_SELECT("Multi Select", "multi-select"),
    RADIO("Radio", "radio"),
    CHECKBOX("Checkbox", "checkbox"),

    // Text types
    TEXTAREA("Text Area", "textarea"),
    RICH_TEXT("Rich Text", "rich-text"),

    // File types
    FILE("File", "file"),
    IMAGE("Image", "image"),

    // Special types
    PASSWORD("Password", "password"),
    HIDDEN("Hidden", "hidden"),
    ENUM("Enum", "select");

    private final String displayName;
    private final String htmlInputType;

    FieldType(String displayName, String htmlInputType) {
        this.displayName = displayName;
        this.htmlInputType = htmlInputType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHtmlInputType() {
        return htmlInputType;
    }

    @Override
    public String toString() {
        return displayName;
    }
}