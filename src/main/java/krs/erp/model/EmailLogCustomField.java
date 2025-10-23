package krs.erp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Custom fields for EmailLog entity
 * Provides 250 custom fields specifically for email logs
 */
@Entity
@Table(name = "email_logs_custom_field")
public class EmailLogCustomField extends BaseCustomField {
    
    // Inherits all 250 custom fields from BaseCustomField
    // and uses email_logs_custom_field table
    
    public EmailLogCustomField() {
        super();
    }
    
    public EmailLogCustomField(Long emailLogId) {
        super();
        setEntityId(emailLogId);
    }
}