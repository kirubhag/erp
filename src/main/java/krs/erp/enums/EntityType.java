package krs.erp.enums;

/**
 * Enum representing the different entity types that can have custom views
 */
public enum EntityType {
    STUDENT("Student"),
    PARENT("Parent"), 
    ATTENDANCE("Attendance"),
    HEALTH("Health"),
    USER("User"),
    STAFF("Staff"),
    ORGANIZATION("Organization"),
    EMAIL_TEMPLATE("EmailTemplate"),
    EMAIL_LOG("EmailLog"),
    PERMISSION("Permission"),
    ROLE("Role"),
    CUSTOM_VIEW("CustomView"),
    ERP_FIELD("ErpField"),
    PARENT_STUDENT_RELATION("ParentStudentRelation"),
    RECYCLE_BIN("RecycleBin"),
    // From EmailTemplate - additional types
    GENERAL("General"),
    NOTIFICATION("Notification"),
    // Legacy types for compatibility
    TEACHER("Teacher"),
    COURSE("Course"),
    GRADE("Grade"),
    ASSIGNMENT("Assignment"),
    EXAM("Exam");
    
    private final String displayName;
    
    EntityType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getValue() {
        return this.name();
    }
    
    /**
     * Get EntityType from string value
     */
    public static EntityType fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return EntityType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Check if the given string is a valid entity type
     */
    public static boolean isValid(String value) {
        return fromValue(value) != null;
    }
}