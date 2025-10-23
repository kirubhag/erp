package krs.erp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Custom fields for EmailTemplate entity
 * Provides 250 custom fields specifically for email templates
 */
@Entity
@Table(name = "email_templates_custom_field")
public class EmailTemplateCustomField extends BaseCustomField {
    
    // Inherits all 250 custom fields from BaseCustomField
    // and uses email_templates_custom_field table
    
    public EmailTemplateCustomField() {
        super();
    }
    
    public EmailTemplateCustomField(Long emailTemplateId) {
        super();
        setEntityId(emailTemplateId);
    }
}