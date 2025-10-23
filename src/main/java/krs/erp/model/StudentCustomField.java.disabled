package krs.erp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Custom fields for Student entity
 * Provides 250 custom fields specifically for students
 */
@Entity
@Table(name = "students_custom_field")
public class StudentCustomField extends BaseCustomField {
    
    // Inherits all 250 custom fields from BaseCustomField
    // and uses students_custom_field table
    
    public StudentCustomField() {
        super();
    }
    
    public StudentCustomField(Long studentId) {
        super();
        setEntityId(studentId);
    }
}