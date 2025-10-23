package krs.erp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Custom fields for Staff entity
 * Provides 250 custom fields specifically for staff members
 */
@Entity
@Table(name = "staff_custom_field")
public class StaffCustomField extends BaseCustomField {
    
    // Inherits all 250 custom fields from BaseCustomField
    // and uses staff_custom_field table
    
    public StaffCustomField() {
        super();
    }
    
    public StaffCustomField(Long staffId) {
        super();
        setEntityId(staffId);
    }
}