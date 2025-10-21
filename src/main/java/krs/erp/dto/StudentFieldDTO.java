package krs.erp.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for available Student fields that can be selected in custom views
 */
public class StudentFieldDTO {
    
    private String fieldName;
    private String displayName;
    private String fieldType;
    private String category;
    private boolean required;
    private String description;
    
    // Constructors
    public StudentFieldDTO() {}
    
    public StudentFieldDTO(String fieldName, String displayName, String fieldType, String category, boolean required, String description) {
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.fieldType = fieldType;
        this.category = category;
        this.required = required;
        this.description = description;
    }
    
    // Static method to get all available fields
    public static List<StudentFieldDTO> getAllAvailableFields() {
        List<StudentFieldDTO> fields = new ArrayList<>();
        
        // Personal Information
        fields.add(new StudentFieldDTO("id", "ID", "number", "Personal", true, "Student database ID"));
        fields.add(new StudentFieldDTO("studentId", "Student ID", "text", "Personal", true, "Unique student identifier"));
        fields.add(new StudentFieldDTO("firstName", "First Name", "text", "Personal", true, "Student's first name"));
        fields.add(new StudentFieldDTO("middleName", "Middle Name", "text", "Personal", false, "Student's middle name"));
        fields.add(new StudentFieldDTO("lastName", "Last Name", "text", "Personal", true, "Student's last name"));
        fields.add(new StudentFieldDTO("fullName", "Full Name", "text", "Personal", false, "Complete name (computed)"));
        fields.add(new StudentFieldDTO("email", "Email", "email", "Personal", false, "Student's email address"));
        fields.add(new StudentFieldDTO("phone", "Phone Number", "phone", "Personal", false, "Student's phone number"));
        fields.add(new StudentFieldDTO("dateOfBirth", "Date of Birth", "date", "Personal", true, "Student's birth date"));
        fields.add(new StudentFieldDTO("age", "Age", "number", "Personal", false, "Student's current age (computed)"));
        fields.add(new StudentFieldDTO("gender", "Gender", "enum", "Personal", false, "Student's gender"));
        
        // Academic Information
        fields.add(new StudentFieldDTO("gradeLevel", "Grade Level", "enum", "Academic", true, "Current grade level"));
        fields.add(new StudentFieldDTO("enrollmentDate", "Enrollment Date", "date", "Academic", true, "Date of enrollment"));
        fields.add(new StudentFieldDTO("enrollmentStatus", "Status", "enum", "Academic", true, "Current enrollment status"));
        
        // Address Information
        fields.add(new StudentFieldDTO("addressLine1", "Address Line 1", "text", "Address", false, "Primary address line"));
        fields.add(new StudentFieldDTO("addressLine2", "Address Line 2", "text", "Address", false, "Secondary address line"));
        fields.add(new StudentFieldDTO("city", "City", "text", "Address", false, "City name"));
        fields.add(new StudentFieldDTO("state", "State", "text", "Address", false, "State/Province"));
        fields.add(new StudentFieldDTO("postalCode", "Postal Code", "text", "Address", false, "ZIP/Postal code"));
        fields.add(new StudentFieldDTO("country", "Country", "text", "Address", false, "Country name"));
        fields.add(new StudentFieldDTO("fullAddress", "Full Address", "text", "Address", false, "Complete address (computed)"));
        
        // Emergency Contact Information
        fields.add(new StudentFieldDTO("emergencyContactName", "Emergency Contact Name", "text", "Emergency", false, "Emergency contact person"));
        fields.add(new StudentFieldDTO("emergencyContactPhone", "Emergency Contact Phone", "phone", "Emergency", false, "Emergency contact phone"));
        fields.add(new StudentFieldDTO("emergencyContactRelation", "Emergency Contact Relation", "text", "Emergency", false, "Relationship to student"));
        
        // System Information
        fields.add(new StudentFieldDTO("createdAt", "Created At", "datetime", "System", false, "Record creation timestamp"));
        fields.add(new StudentFieldDTO("updatedAt", "Updated At", "datetime", "System", false, "Last update timestamp"));
        fields.add(new StudentFieldDTO("createdBy", "Created By", "text", "System", false, "User who created the record"));
        fields.add(new StudentFieldDTO("updatedBy", "Updated By", "text", "System", false, "User who last updated the record"));
        fields.add(new StudentFieldDTO("isActive", "Is Active", "boolean", "System", false, "Record active status"));
        
        return fields;
    }
    
    // Static method to get default visible fields
    public static List<String> getDefaultVisibleFields() {
        List<String> defaultFields = new ArrayList<>();
        defaultFields.add("studentId");
        defaultFields.add("firstName");
        defaultFields.add("lastName");
        defaultFields.add("email");
        defaultFields.add("gradeLevel");
        defaultFields.add("enrollmentStatus");
        defaultFields.add("enrollmentDate");
        defaultFields.add("age");
        return defaultFields;
    }
    
    // Getters and Setters
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getFieldType() {
        return fieldType;
    }
    
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}