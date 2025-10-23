package krs.erp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Custom fields for Parent entity
 * Provides 250 custom fields specifically for parents
 */
@Entity
@Table(name = "parents_custom_field")
public class ParentCustomField extends BaseCustomField {
    
    // Inherits all 250 custom fields from BaseCustomField
    // and uses parents_custom_field table
    
    public ParentCustomField() {
        super();
    }
    
    public ParentCustomField(Long parentId) {
        super();
        setEntityId(parentId);
    }
}