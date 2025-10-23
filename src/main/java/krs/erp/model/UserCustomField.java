package krs.erp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Custom fields for User entity
 * Provides 250 custom fields specifically for users
 */
@Entity
@Table(name = "users_custom_field")
public class UserCustomField extends BaseCustomField {
    
    // Inherits all 250 custom fields from BaseCustomField
    // and uses users_custom_field table
    
    public UserCustomField() {
        super();
    }
    
    public UserCustomField(Long userId) {
        super();
        setEntityId(userId);
    }
}