package krs.erp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Custom fields for Attendance entity
 * Provides 250 custom fields specifically for attendance records
 */
@Entity
@Table(name = "attendance_custom_field")
public class AttendanceCustomField extends BaseCustomField {
    
    // Inherits all 250 custom fields from BaseCustomField
    // and uses attendance_custom_field table
    
    public AttendanceCustomField() {
        super();
    }
    
    public AttendanceCustomField(Long attendanceId) {
        super();
        setEntityId(attendanceId);
    }
}