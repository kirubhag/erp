package krs.erp.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Student;

/**
 * Unit tests for StudentRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private Student student1;
    private Student student2;
    private Student student3;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();

        // Create student 1 - Active, Grade 10
        student1 = new Student();
        student1.setStudentId("STU001");
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john.doe@school.com");
        student1.setPhone("1234567890");
        student1.setDateOfBirth(LocalDate.of(2008, 5, 15));
        student1.setGender(Student.Gender.MALE);
        student1.setGradeLevel(Student.GradeLevel.GRADE_10);
        student1.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        student1.setEnrollmentDate(LocalDate.of(2023, 9, 1));
        student1.setIsActive(1);
        student1.setCreatedTime(LocalDateTime.now());
        student1 = studentRepository.save(student1);

        // Create student 2 - Active, Grade 11
        student2 = new Student();
        student2.setStudentId("STU002");
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setEmail("jane.smith@school.com");
        student2.setDateOfBirth(LocalDate.of(2007, 8, 20));
        student2.setGender(Student.Gender.FEMALE);
        student2.setGradeLevel(Student.GradeLevel.GRADE_11);
        student2.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        student2.setEnrollmentDate(LocalDate.of(2022, 9, 1));
        student2.setIsActive(1);
        student2.setCreatedTime(LocalDateTime.now());
        student2 = studentRepository.save(student2);

        // Create student 3 - Withdrawn, Grade 10
        student3 = new Student();
        student3.setStudentId("STU003");
        student3.setFirstName("Bob");
        student3.setLastName("Johnson");
        student3.setEmail("bob.johnson@school.com");
        student3.setDateOfBirth(LocalDate.of(2008, 3, 10));
        student3.setGender(Student.Gender.MALE);
        student3.setGradeLevel(Student.GradeLevel.GRADE_10);
        student3.setEnrollmentStatus(Student.EnrollmentStatus.TRANSFERRED);
        student3.setEnrollmentDate(LocalDate.of(2023, 9, 1));
        student3.setIsActive(0);
        student3.setCreatedTime(LocalDateTime.now());
        student3 = studentRepository.save(student3);
    }

    @Test
    void testFindByStudentId() {
        Optional<Student> found = studentRepository.findByStudentId("STU001");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void testFindByEmail() {
        Optional<Student> found = studentRepository.findByEmail("jane.smith@school.com");
        assertThat(found).isPresent();
        assertThat(found.get().getLastName()).isEqualTo("Smith");
    }

    @Test
    void testFindByEnrollmentStatus() {
        List<Student> activeStudents = studentRepository.findByEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        assertThat(activeStudents).hasSize(2);
        
        List<Student> transferredStudents = studentRepository.findByEnrollmentStatus(Student.EnrollmentStatus.TRANSFERRED);
        assertThat(transferredStudents).hasSize(1);
    }

    @Test
    void testFindByGradeLevel() {
        List<Student> grade10Students = studentRepository.findByGradeLevel(Student.GradeLevel.GRADE_10);
        assertThat(grade10Students).hasSize(2);
        
        List<Student> grade11Students = studentRepository.findByGradeLevel(Student.GradeLevel.GRADE_11);
        assertThat(grade11Students).hasSize(1);
    }

    @Test
    void testFindActiveStudents() {
        List<Student> activeStudents = studentRepository.findActiveStudents();
        assertThat(activeStudents).hasSize(2);
        assertThat(activeStudents).allMatch(s -> s.getEnrollmentStatus() == Student.EnrollmentStatus.ACTIVE);
    }

    @Test
    void testFindByEnrollmentDateBetween() {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        
        List<Student> students = studentRepository.findByEnrollmentDateBetween(startDate, endDate);
        assertThat(students).hasSize(3);
    }

    @Test
    void testFindByNameContaining() {
        List<Student> students = studentRepository.findByNameContaining("John");
        assertThat(students).hasSize(2); // John and Johnson
        
        students = studentRepository.findByNameContaining("jane");
        assertThat(students).hasSize(1);
    }

    @Test
    void testFindActiveStudentsByGradeLevel() {
        List<Student> activeGrade10 = studentRepository.findActiveStudentsByGradeLevel(Student.GradeLevel.GRADE_10);
        assertThat(activeGrade10).hasSize(1);
        assertThat(activeGrade10.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void testCountActiveStudents() {
        Long count = studentRepository.countActiveStudents();
        assertThat(count).isEqualTo(2L);
    }

    @Test
    void testCountActiveStudentsByGradeLevel() {
        Long count = studentRepository.countActiveStudentsByGradeLevel(Student.GradeLevel.GRADE_10);
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void testFindByEnrollmentStatusWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = studentRepository.findByEnrollmentStatus(Student.EnrollmentStatus.ACTIVE, pageable);
        
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFindByGradeLevelWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = studentRepository.findByGradeLevel(Student.GradeLevel.GRADE_10, pageable);
        
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void testFindByIsActive() {
        List<Student> activeStudents = studentRepository.findByIsActive(1);
        assertThat(activeStudents).hasSize(2);
        
        List<Student> inactiveStudents = studentRepository.findByIsActive(0);
        assertThat(inactiveStudents).hasSize(1);
    }

    @Test
    void testFindAllActive() {
        List<Student> activeStudents = studentRepository.findAllActive();
        assertThat(activeStudents).hasSize(2);
    }

    @Test
    void testCountByIsActive() {
        Long activeCount = studentRepository.countByIsActive(1);
        assertThat(activeCount).isEqualTo(2L);
        
        Long inactiveCount = studentRepository.countByIsActive(0);
        assertThat(inactiveCount).isEqualTo(1L);
    }

    @Test
    void testSaveStudent() {
        Student newStudent = new Student();
        newStudent.setStudentId("STU004");
        newStudent.setFirstName("Alice");
        newStudent.setLastName("Brown");
        newStudent.setEmail("alice.brown@school.com");
        newStudent.setDateOfBirth(LocalDate.of(2009, 1, 5));
        newStudent.setGender(Student.Gender.FEMALE);
        newStudent.setGradeLevel(Student.GradeLevel.GRADE_9);
        newStudent.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        newStudent.setEnrollmentDate(LocalDate.now());
        newStudent.setIsActive(1);
        newStudent.setCreatedTime(LocalDateTime.now());
        
        Student saved = studentRepository.save(newStudent);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStudentId()).isEqualTo("STU004");
    }

    @Test
    void testUpdateStudent() {
        student1.setPhone("9876543210");
        student1.setModifiedTime(LocalDateTime.now());
        
        Student updated = studentRepository.save(student1);
        assertThat(updated.getPhone()).isEqualTo("9876543210");
    }

    @Test
    void testDeleteStudent() {
        Long studentId = student1.getId();
        studentRepository.delete(student1);
        
        assertThat(studentRepository.findById(studentId)).isEmpty();
    }
}
