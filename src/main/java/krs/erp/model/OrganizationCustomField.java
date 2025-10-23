package krs.erp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Custom fields for Organization entity
 * Provides 250 custom fields specifically for organizations
 */
@Entity
@Table(name = "organizations_custom_field")
public class OrganizationCustomField extends BaseCustomField {
    
    // Inherits all 250 custom fields from BaseCustomField
    // and uses organizations_custom_field table
    
    public OrganizationCustomField() {
        super();
    }
    
    public OrganizationCustomField(Long organizationId) {
        super();
        setEntityId(organizationId);
    }
}