package krs.erp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Custom fields for HealthRecord entity
 * Provides 250 custom fields specifically for health records
 */
@Entity
@Table(name = "health_records_custom_field")
public class HealthRecordCustomField extends BaseCustomField {
    
    // Inherits all 250 custom fields from BaseCustomField
    // and uses health_records_custom_field table
    
    public HealthRecordCustomField() {
        super();
    }
    
    public HealthRecordCustomField(Long healthRecordId) {
        super();
        setEntityId(healthRecordId);
    }
}