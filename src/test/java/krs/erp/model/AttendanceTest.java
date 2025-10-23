package krs.erp.model;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Attendance model
 */
class AttendanceTest {

    private Attendance attendance;

    @BeforeEach
    void setUp() {
        attendance = new Attendance();
    }

    @Test
    void testAttendanceCreation() {
        LocalDate date = LocalDate.now();
        LocalTime checkIn = LocalTime.of(8, 30);
        LocalTime checkOut = LocalTime.of(17, 0);

        attendance.setAttendanceDate(date);
        attendance.setCheckInTime(checkIn);
        attendance.setCheckOutTime(checkOut);
        attendance.setStatus(Attendance.AttendanceStatus.PRESENT);

        assertEquals(date, attendance.getAttendanceDate());
        assertEquals(checkIn, attendance.getCheckInTime());
        assertEquals(checkOut, attendance.getCheckOutTime());
        assertEquals(Attendance.AttendanceStatus.PRESENT, attendance.getStatus());
    }

    @Test
    void testAttendanceStatus() {
        // Test all attendance statuses
        attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
        assertEquals(Attendance.AttendanceStatus.PRESENT, attendance.getStatus());

        attendance.setStatus(Attendance.AttendanceStatus.ABSENT);
        assertEquals(Attendance.AttendanceStatus.ABSENT, attendance.getStatus());

        attendance.setStatus(Attendance.AttendanceStatus.LATE);
        assertEquals(Attendance.AttendanceStatus.LATE, attendance.getStatus());

        attendance.setStatus(Attendance.AttendanceStatus.EARLY_DEPARTURE);
        assertEquals(Attendance.AttendanceStatus.EARLY_DEPARTURE, attendance.getStatus());

        attendance.setStatus(Attendance.AttendanceStatus.EXCUSED);
        assertEquals(Attendance.AttendanceStatus.EXCUSED, attendance.getStatus());
    }

    @Test
    void testAttendanceRemarks() {
        String remarks = "Medical appointment";
        attendance.setRemarks(remarks);
        assertEquals(remarks, attendance.getRemarks());
    }

    @Test
    void testAttendanceExcused() {
        // Test excused field
        attendance.setExcused(true);
        assertTrue(attendance.getExcused());

        attendance.setExcused(false);
        assertFalse(attendance.getExcused());
    }

    @Test
    void testAttendanceHours() {
        LocalTime checkIn = LocalTime.of(8, 30);
        LocalTime checkOut = LocalTime.of(17, 30);
        
        attendance.setCheckInTime(checkIn);
        attendance.setCheckOutTime(checkOut);
        
        // Test hours calculation (would be implemented in service layer)
        assertNotNull(attendance.getCheckInTime());
        assertNotNull(attendance.getCheckOutTime());
    }

    @Test
    void testAttendanceAuditFields() {
        attendance.setCreatedBy("system");
        attendance.setModifiedBy("admin");
        attendance.setOwnerId(300L);
        attendance.setIsActive(1);

        assertEquals("system", attendance.getCreatedBy());
        assertEquals("admin", attendance.getModifiedBy());
        assertEquals(300L, attendance.getOwnerId());
        assertEquals(1, attendance.getIsActive());
    }

    @Test
    void testAttendanceEquality() {
        Attendance attendance1 = new Attendance();
        Attendance attendance2 = new Attendance();
        
        attendance1.setId(1L);
        attendance2.setId(1L);
        
        assertEquals(attendance1.getId(), attendance2.getId());
    }

    @Test
    void testAttendanceToString() {
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
        
        String toString = attendance.toString();
        assertNotNull(toString);
    }

    @Test
    void testAttendanceValidation() {
        // Test required fields
        LocalDate today = LocalDate.now();
        attendance.setAttendanceDate(today);
        
        assertEquals(today, attendance.getAttendanceDate());
        
        // Date should not be null
        assertNotNull(attendance.getAttendanceDate());
    }
}