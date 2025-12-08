package krs.erp.enums;

/**
 * Enum representing the different entity types that can have custom views
 */
public enum EntityType {
    STUDENT("Student"),
    PARENT("Parent"), 
    ATTENDANCE("Attendance"),
    HEALTH("Health"),
    SUBJECT("Subject"),
    TIMETABLE("Timetable"),
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
    EXAM("Exam"),
    // Additional entity types
    ADDRESS("Address"),
    STUDENT_GUARDIAN("StudentGuardian"),
    STUDENT_MEDICAL("StudentMedical");
    
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
     * Handles both singular and plural forms (e.g., "student" or "students")
     */
    public static EntityType fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        String upperValue = value.toUpperCase();
        
        try {
            // Try exact match first
            return EntityType.valueOf(upperValue);
        } catch (IllegalArgumentException e) {
            // Try removing trailing 'S' for plural forms
            if (upperValue.endsWith("S") && upperValue.length() > 1) {
                try {
                    return EntityType.valueOf(upperValue.substring(0, upperValue.length() - 1));
                } catch (IllegalArgumentException e2) {
                    // Ignore and return null
                }
            }
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