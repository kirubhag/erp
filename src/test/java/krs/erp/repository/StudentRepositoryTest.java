package krs.erp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import krs.erp.model.Student;

/**
 * Integration tests for StudentRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testFindByStudentId() {
        // Given
        Student student = createTestStudent();
        student.setStudentId("STU001");
        entityManager.persistAndFlush(student);

        // When
        Optional<Student> found = studentRepository.findByStudentId("STU001");

        // Then
        assertTrue(found.isPresent());
        assertEquals("STU001", found.get().getStudentId());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    void testFindByEmail() {
        // Given
        Student student = createTestStudent();
        student.setEmail("john.doe@example.com");
        entityManager.persistAndFlush(student);

        // When
        Optional<Student> found = studentRepository.findByEmail("john.doe@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("john.doe@example.com", found.get().getEmail());
    }

    @Test
    void testFindByGradeLevel() {
        // Given
        Student student1 = createTestStudent();
        student1.setStudentId("STU001");
        student1.setGradeLevel(Student.GradeLevel.GRADE_5);
        
        Student student2 = createTestStudent();
        student2.setStudentId("STU002");
        student2.setGradeLevel(Student.GradeLevel.GRADE_6);
        
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // When
        List<Student> grade5Students = studentRepository.findByGradeLevel(Student.GradeLevel.GRADE_5);

        // Then
        assertEquals(1, grade5Students.size());
        assertEquals(Student.GradeLevel.GRADE_5, grade5Students.get(0).getGradeLevel());
    }

    @Test
    void testFindByEnrollmentStatus() {
        // Given
        Student activeStudent = createTestStudent();
        activeStudent.setStudentId("STU001");
        activeStudent.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        
        Student inactiveStudent = createTestStudent();
        inactiveStudent.setStudentId("STU002");
        inactiveStudent.setEnrollmentStatus(Student.EnrollmentStatus.INACTIVE);
        
        entityManager.persistAndFlush(activeStudent);
        entityManager.persistAndFlush(inactiveStudent);

        // When
        List<Student> activeStudents = studentRepository.findByEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);

        // Then
        assertEquals(1, activeStudents.size());
        assertEquals(Student.EnrollmentStatus.ACTIVE, activeStudents.get(0).getEnrollmentStatus());
    }

    @Test
    void testFindByEnrollmentDateBetween() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        
        Student student1 = createTestStudent();
        student1.setStudentId("STU001");
        student1.setEnrollmentDate(LocalDate.of(2024, 6, 15));
        
        Student student2 = createTestStudent();
        student2.setStudentId("STU002");
        student2.setEnrollmentDate(LocalDate.of(2023, 6, 15)); // Outside range
        
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // When
        List<Student> studentsInRange = studentRepository.findByEnrollmentDateBetween(startDate, endDate);

        // Then
        assertEquals(1, studentsInRange.size());
        assertEquals("STU001", studentsInRange.get(0).getStudentId());
    }

    @Test
    void testCountActiveStudents() {
        // Given
        Student activeStudent1 = createTestStudent();
        activeStudent1.setStudentId("STU001");
        activeStudent1.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        
        Student activeStudent2 = createTestStudent();
        activeStudent2.setStudentId("STU002");
        activeStudent2.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        
        Student inactiveStudent = createTestStudent();
        inactiveStudent.setStudentId("STU003");
        inactiveStudent.setEnrollmentStatus(Student.EnrollmentStatus.INACTIVE);
        
        entityManager.persistAndFlush(activeStudent1);
        entityManager.persistAndFlush(activeStudent2);
        entityManager.persistAndFlush(inactiveStudent);

        // When
        Long activeCount = studentRepository.countActiveStudents();

        // Then
        assertEquals(2, activeCount);
    }

    @Test
    void testSearchByName() {
        // Given
        Student johnDoe = createTestStudent();
        johnDoe.setStudentId("STU001");
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        
        Student janeSmith = createTestStudent();
        janeSmith.setStudentId("STU002");
        janeSmith.setFirstName("Jane");
        janeSmith.setLastName("Smith");
        
        entityManager.persistAndFlush(johnDoe);
        entityManager.persistAndFlush(janeSmith);

        // When
        List<Student> johnResults = studentRepository.findByNameContaining("john");

        // Then
        assertEquals(1, johnResults.size());
        assertEquals("John", johnResults.get(0).getFirstName());
    }

    @Test
    void testFindActiveStudentsByCity() {
        // Given
        Student student1 = createTestStudent();
        student1.setStudentId("STU001");
        student1.setCity("New York");
        student1.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        
        Student student2 = createTestStudent();
        student2.setStudentId("STU002");
        student2.setCity("Los Angeles");
        student2.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);

        // When
        List<Student> newYorkStudents = studentRepository.findActiveStudentsByCity("New York");

        // Then
        assertEquals(1, newYorkStudents.size());
        assertEquals("New York", newYorkStudents.get(0).getCity());
    }

    private Student createTestStudent() {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setStudentId("STU_TEST_" + System.currentTimeMillis());
        student.setEmail("test." + System.currentTimeMillis() + "@example.com");
        student.setDateOfBirth(LocalDate.of(2010, 1, 1));
        student.setEnrollmentDate(LocalDate.now());
        student.setGradeLevel(Student.GradeLevel.GRADE_5);
        student.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        return student;
    }
}