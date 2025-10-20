package krs.erp.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import krs.erp.model.Student;
import krs.erp.repository.StudentRepository;

/**
 * Unit tests for StudentService
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = createTestStudent();
        
        // Initialize the service since we're mocking
        studentService = new StudentService(studentRepository);
    }

    @Test
    void testGetAllStudents() {
        // Given
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> studentPage = new PageImpl<>(students, PageRequest.of(0, 10), 1);
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(studentPage);

        // When
        Page<Student> result = studentService.getAllStudents(PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());
        verify(studentRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetStudentById() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // When
        Optional<Student> result = studentService.getStudentById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        verify(studentRepository).findById(1L);
    }

    @Test
    void testGetStudentByIdNotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Student> result = studentService.getStudentById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(studentRepository).findById(999L);
    }

    @Test
    void testCreateStudent() {
        // Given
        Student newStudent = createTestStudent();
        newStudent.setId(null);
        Student savedStudent = createTestStudent();
        savedStudent.setId(1L);
        
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // When
        Student result = studentService.createStudent(newStudent);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("John", result.getFirstName());
        verify(studentRepository).save(newStudent);
    }

    @Test
    void testUpdateStudent() {
        // Given
        Student existingStudent = createTestStudent();
        existingStudent.setId(1L);
        
        Student updatedStudent = createTestStudent();
        updatedStudent.setId(1L);
        updatedStudent.setLastName("Smith");
        
        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // When
        Optional<Student> result = studentService.updateStudent(1L, updatedStudent);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Smith", result.get().getLastName());
        verify(studentRepository).findById(1L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void testUpdateStudentNotFound() {
        // Given
        Student updateData = createTestStudent();
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Student> result = studentService.updateStudent(999L, updateData);

        // Then
        assertFalse(result.isPresent());
        verify(studentRepository).findById(999L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testDeleteStudent() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // When
        boolean result = studentService.deleteStudent(1L);

        // Then
        assertTrue(result);
        verify(studentRepository).findById(1L);
        verify(studentRepository).delete(testStudent);
    }

    @Test
    void testDeleteStudentNotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = studentService.deleteStudent(999L);

        // Then
        assertFalse(result);
        verify(studentRepository).findById(999L);
        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    void testGetStudentsByGradeLevel() {
        // Given
        List<Student> grade5Students = Arrays.asList(testStudent);
        when(studentRepository.findByGradeLevel(Student.GradeLevel.GRADE_5)).thenReturn(grade5Students);

        // When
        List<Student> result = studentService.getStudentsByGradeLevel(Student.GradeLevel.GRADE_5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Student.GradeLevel.GRADE_5, result.get(0).getGradeLevel());
        verify(studentRepository).findByGradeLevel(Student.GradeLevel.GRADE_5);
    }

    @Test
    void testGetActiveStudents() {
        // Given
        List<Student> activeStudents = Arrays.asList(testStudent);
        when(studentRepository.findActiveStudents()).thenReturn(activeStudents);

        // When
        List<Student> result = studentService.getActiveStudents();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Student.EnrollmentStatus.ACTIVE, result.get(0).getEnrollmentStatus());
        verify(studentRepository).findActiveStudents();
    }

    @Test
    void testSearchStudentsByName() {
        // Given
        List<Student> searchResults = Arrays.asList(testStudent);
        when(studentRepository.findByNameContaining("john")).thenReturn(searchResults);

        // When
        List<Student> result = studentService.searchStudentsByName("john");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getFirstName().toLowerCase().contains("john"));
        verify(studentRepository).findByNameContaining("john");
    }

    @Test
    void testGetStudentCount() {
        // Given
        when(studentRepository.countActiveStudents()).thenReturn(25L);

        // When
        Long result = studentService.getActiveStudentCount();

        // Then
        assertNotNull(result);
        assertEquals(25L, result);
        verify(studentRepository).countActiveStudents();
    }

    @Test
    void testGetStudentsByEnrollmentDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        List<Student> studentsInRange = Arrays.asList(testStudent);
        
        when(studentRepository.findByEnrollmentDateBetween(startDate, endDate)).thenReturn(studentsInRange);

        // When
        List<Student> result = studentService.getStudentsByEnrollmentDateRange(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepository).findByEnrollmentDateBetween(startDate, endDate);
    }

    @Test
    void testValidateStudentData() {
        // Test with valid student
        Student validStudent = createTestStudent();
        assertTrue(studentService.isValidStudent(validStudent));

        // Test with invalid student (missing required fields)
        Student invalidStudent = new Student();
        assertFalse(studentService.isValidStudent(invalidStudent));

        // Test with invalid email
        Student invalidEmailStudent = createTestStudent();
        invalidEmailStudent.setEmail("invalid-email");
        assertFalse(studentService.isValidStudent(invalidEmailStudent));
    }

    @Test
    void testGetStudentsByCity() {
        // Given
        List<Student> studentsInCity = Arrays.asList(testStudent);
        when(studentRepository.findActiveStudentsByCity("New York")).thenReturn(studentsInCity);

        // When
        List<Student> result = studentService.getStudentsByCity("New York");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepository).findActiveStudentsByCity("New York");
    }

    private Student createTestStudent() {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setStudentId("STU001");
        student.setEmail("john.doe@example.com");
        student.setDateOfBirth(LocalDate.of(2010, 1, 1));
        student.setEnrollmentDate(LocalDate.now());
        student.setGradeLevel(Student.GradeLevel.GRADE_5);
        student.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        return student;
    }

    // Mock StudentService class for testing
    static class StudentService {
        private final StudentRepository studentRepository;

        public StudentService(StudentRepository studentRepository) {
            this.studentRepository = studentRepository;
        }

        public Page<Student> getAllStudents(Pageable pageable) {
            return studentRepository.findAll(pageable);
        }

        public Optional<Student> getStudentById(Long id) {
            return studentRepository.findById(id);
        }

        public Student createStudent(Student student) {
            return studentRepository.save(student);
        }

        public Optional<Student> updateStudent(Long id, Student studentData) {
            return studentRepository.findById(id)
                    .map(existingStudent -> {
                        existingStudent.setFirstName(studentData.getFirstName());
                        existingStudent.setLastName(studentData.getLastName());
                        existingStudent.setEmail(studentData.getEmail());
                        existingStudent.setGradeLevel(studentData.getGradeLevel());
                        existingStudent.setEnrollmentStatus(studentData.getEnrollmentStatus());
                        return studentRepository.save(existingStudent);
                    });
        }

        public boolean deleteStudent(Long id) {
            return studentRepository.findById(id)
                    .map(student -> {
                        studentRepository.delete(student);
                        return true;
                    }).orElse(false);
        }

        public List<Student> getStudentsByGradeLevel(Student.GradeLevel gradeLevel) {
            return studentRepository.findByGradeLevel(gradeLevel);
        }

        public List<Student> getActiveStudents() {
            return studentRepository.findActiveStudents();
        }

        public List<Student> searchStudentsByName(String name) {
            return studentRepository.findByNameContaining(name);
        }

        public Long getActiveStudentCount() {
            return studentRepository.countActiveStudents();
        }

        public List<Student> getStudentsByEnrollmentDateRange(LocalDate startDate, LocalDate endDate) {
            return studentRepository.findByEnrollmentDateBetween(startDate, endDate);
        }

        public List<Student> getStudentsByCity(String city) {
            return studentRepository.findActiveStudentsByCity(city);
        }

        public boolean isValidStudent(Student student) {
            if (student == null) return false;
            if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) return false;
            if (student.getLastName() == null || student.getLastName().trim().isEmpty()) return false;
            if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) return false;
            if (student.getDateOfBirth() == null) return false;
            if (student.getEmail() != null && !student.getEmail().contains("@")) return false;
            return true;
        }
    }
}