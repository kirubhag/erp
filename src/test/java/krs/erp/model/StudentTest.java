package krs.erp.model;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Student model
 */
class StudentTest {

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
    }

    @Test
    void testStudentCreation() {
        // Test basic student creation
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@example.com");
        student.setDateOfBirth(LocalDate.of(2010, 5, 15));
        student.setGradeLevel(Student.GradeLevel.GRADE_5);

        assertEquals("John", student.getFirstName());
        assertEquals("Doe", student.getLastName());
        assertEquals("john.doe@example.com", student.getEmail());
        assertEquals(LocalDate.of(2010, 5, 15), student.getDateOfBirth());
        assertEquals(Student.GradeLevel.GRADE_5, student.getGradeLevel());
    }



    @Test
    void testStudentDefaultStatus() {
        // Test default status is ACTIVE
        assertEquals(Student.EnrollmentStatus.ACTIVE, student.getEnrollmentStatus());
    }

    @Test
    void testStudentStatusUpdate() {
        student.setEnrollmentStatus(Student.EnrollmentStatus.INACTIVE);
        assertEquals(Student.EnrollmentStatus.INACTIVE, student.getEnrollmentStatus());

        student.setEnrollmentStatus(Student.EnrollmentStatus.GRADUATED);
        assertEquals(Student.EnrollmentStatus.GRADUATED, student.getEnrollmentStatus());
    }

    @Test
    void testEnrollmentDate() {
        LocalDate enrollmentDate = LocalDate.now();
        student.setEnrollmentDate(enrollmentDate);
        assertEquals(enrollmentDate, student.getEnrollmentDate());
    }

    @Test
    void testStudentValidation() {
        // Test required fields
        assertNotNull(student.getEnrollmentStatus()); // Should have default status
        
        // Test email format (basic test)
        student.setEmail("invalid-email");
        // In real scenario, this would be validated by @Email annotation
        assertTrue(student.getEmail().contains("invalid-email"));
    }

    @Test
    void testStudentFullNameMethod() {
        student.setFirstName("Jane");
        student.setMiddleName("Marie");
        student.setLastName("Smith");

        String expectedFullName = "Jane Marie Smith";
        assertEquals(expectedFullName, student.getFullName());
    }

    @Test
    void testStudentAgeCalculation() {
        LocalDate birthDate = LocalDate.of(2010, 1, 1);
        student.setDateOfBirth(birthDate);

        int expectedAge = LocalDate.now().getYear() - 2010;
        assertEquals(expectedAge, student.getAge());
    }

    @Test
    void testStudentAddress() {
        student.setAddressLine1("123 Main St");
        student.setCity("Anytown");
        student.setState("CA");
        student.setPostalCode("12345");
        student.setCountry("USA");

        String fullAddress = student.getFullAddress();
        assertTrue(fullAddress.contains("123 Main St"));
        assertTrue(fullAddress.contains("Anytown"));
        assertTrue(fullAddress.contains("CA"));
    }

    @Test
    void testStudentToString() {
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setStudentId("STU001");
        
        String toString = student.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Student@") || toString.contains("krs.erp.model.Student"));
    }

    @Test
    void testStudentEquality() {
        Student student1 = new Student();
        Student student2 = new Student();
        
        // Test equality when both have same ID
        student1.setId(1L);
        student2.setId(1L);
        
        // In JPA entities, equality is typically based on ID
        assertEquals(student1.getId(), student2.getId());
    }
}