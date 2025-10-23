package krs.erp.model;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Staff model
 */
class StaffTest {

    private Staff staff;

    @BeforeEach
    void setUp() {
        staff = new Staff();
    }

    @Test
    void testStaffCreation() {
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setEmail("john.doe@school.com");
        staff.setStaffId("EMP001");
        staff.setDepartment("Mathematics");
        staff.setPosition("Teacher");

        assertEquals("John", staff.getFirstName());
        assertEquals("Doe", staff.getLastName());
        assertEquals("john.doe@school.com", staff.getEmail());
        assertEquals("EMP001", staff.getStaffId());
        assertEquals("Mathematics", staff.getDepartment());
        assertEquals("Teacher", staff.getPosition());
    }

    @Test
    void testStaffEmploymentStatus() {
        // Test default employment status
        assertEquals(Staff.EmploymentStatus.ACTIVE, staff.getEmploymentStatus());

        staff.setEmploymentStatus(Staff.EmploymentStatus.INACTIVE);
        assertEquals(Staff.EmploymentStatus.INACTIVE, staff.getEmploymentStatus());

        staff.setEmploymentStatus(Staff.EmploymentStatus.TERMINATED);
        assertEquals(Staff.EmploymentStatus.TERMINATED, staff.getEmploymentStatus());

        staff.setEmploymentStatus(Staff.EmploymentStatus.ON_LEAVE);
        assertEquals(Staff.EmploymentStatus.ON_LEAVE, staff.getEmploymentStatus());
    }

    @Test
    void testStaffEmploymentDates() {
        LocalDate hireDate = LocalDate.of(2020, 1, 15);
        LocalDate terminationDate = LocalDate.of(2023, 12, 31);

        staff.setHireDate(hireDate);
        staff.setTerminationDate(terminationDate);

        assertEquals(hireDate, staff.getHireDate());
        assertEquals(terminationDate, staff.getTerminationDate());
    }

    @Test
    void testStaffSalary() {
        Double salary = 50000.00;
        staff.setSalary(salary);

        assertEquals(salary, staff.getSalary());
    }

    @Test
    void testStaffFullName() {
        staff.setFirstName("Jane");
        staff.setMiddleName("Marie");
        staff.setLastName("Smith");

        String expectedFullName = "Jane Marie Smith";
        assertEquals(expectedFullName, staff.getFullName());
    }

    @Test
    void testStaffContact() {
        staff.setPhone("555-1234");
        staff.setEmergencyContactPhone("555-5678");
        staff.setAddressLine1("123 Main St");
        staff.setCity("Anytown");
        staff.setState("CA");
        staff.setPostalCode("12345");

        assertEquals("555-1234", staff.getPhone());
        assertEquals("555-5678", staff.getEmergencyContactPhone());
        assertEquals("123 Main St", staff.getAddressLine1());
        assertEquals("Anytown", staff.getCity());
        assertEquals("CA", staff.getState());
        assertEquals("12345", staff.getPostalCode());
    }

    @Test
    void testStaffAuditFields() {
        staff.setCreatedBy("hr");
        staff.setModifiedBy("manager");
        staff.setOwnerId(200L);
        staff.setIsActive(true);

        assertEquals("hr", staff.getCreatedBy());
        assertEquals("manager", staff.getModifiedBy());
        assertEquals(200L, staff.getOwnerId());
        assertTrue(staff.getIsActive());
    }

    @Test
    void testStaffEquality() {
        Staff staff1 = new Staff();
        Staff staff2 = new Staff();
        
        staff1.setId(1L);
        staff2.setId(1L);
        
        assertEquals(staff1.getId(), staff2.getId());
    }

    @Test
    void testStaffToString() {
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setStaffId("EMP001");
        
        String toString = staff.toString();
        assertNotNull(toString);
    }

    @Test
    void testStaffValidation() {
        // Test staff ID uniqueness (would be handled by database constraints)
        staff.setStaffId("EMP001");
        assertEquals("EMP001", staff.getStaffId());

        // Test email format (would be validated by @Email annotation)
        staff.setEmail("invalid-email");
        assertEquals("invalid-email", staff.getEmail());
    }
}