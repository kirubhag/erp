package krs.erp.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.model.Student;
import krs.erp.repository.StudentRepository;

/**
 * Unit tests for StudentController
 */
@WebMvcTest(StudentController.class)
@ActiveProfiles("test")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testGetAllStudents() throws Exception {
        // Given
        List<Student> students = Arrays.asList(
            createTestStudent(1L, "John", "Doe", "STU001"),
            createTestStudent(2L, "Jane", "Smith", "STU002")
        );
        Page<Student> studentPage = new PageImpl<>(students, PageRequest.of(0, 10), 2);
        when(studentRepository.findAll(any(PageRequest.class))).thenReturn(studentPage);

        // When & Then
        mockMvc.perform(get("/api/students")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[1].firstName").value("Jane"));

        verify(studentRepository).findAll(any(PageRequest.class));
    }

    @Test
    @WithMockUser
    void testGetStudentById() throws Exception {
        // Given
        Student student = createTestStudent(1L, "John", "Doe", "STU001");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // When & Then
        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.studentId").value("STU001"));

        verify(studentRepository).findById(1L);
    }

    @Test
    @WithMockUser
    void testGetStudentByIdNotFound() throws Exception {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/students/999"))
                .andExpect(status().isNotFound());

        verify(studentRepository).findById(999L);
    }

    @Test
    @WithMockUser
    void testCreateStudent() throws Exception {
        // Given
        Student newStudent = createTestStudent(null, "Alice", "Johnson", "STU003");
        Student savedStudent = createTestStudent(3L, "Alice", "Johnson", "STU003");
        
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // When & Then
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStudent))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("Alice"));

        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @WithMockUser
    void testUpdateStudent() throws Exception {
        // Given
        Student existingStudent = createTestStudent(1L, "John", "Doe", "STU001");
        Student updatedStudent = createTestStudent(1L, "John", "Smith", "STU001");
        
        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // When & Then
        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudent))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"));

        verify(studentRepository).findById(1L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @WithMockUser
    void testDeleteStudent() throws Exception {
        // Given
        Student student = createTestStudent(1L, "John", "Doe", "STU001");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // When & Then
        mockMvc.perform(delete("/api/students/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(studentRepository).findById(1L);
        verify(studentRepository).delete(student);
    }

    @Test
    @WithMockUser
    void testGetStudentsByGradeLevel() throws Exception {
        // Given
        List<Student> grade5Students = Arrays.asList(
            createTestStudent(1L, "John", "Doe", "STU001"),
            createTestStudent(2L, "Jane", "Smith", "STU002")
        );
        when(studentRepository.findByGradeLevel(Student.GradeLevel.GRADE_5)).thenReturn(grade5Students);

        // When & Then
        mockMvc.perform(get("/api/students/grade/GRADE_5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(studentRepository).findByGradeLevel(Student.GradeLevel.GRADE_5);
    }

    @Test
    @WithMockUser
    void testGetActiveStudents() throws Exception {
        // Given
        List<Student> activeStudents = Arrays.asList(
            createTestStudent(1L, "John", "Doe", "STU001")
        );
        when(studentRepository.findActiveStudents()).thenReturn(activeStudents);

        // When & Then
        mockMvc.perform(get("/api/students/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(studentRepository).findActiveStudents();
    }

    @Test
    @WithMockUser
    void testSearchStudents() throws Exception {
        // Given
        List<Student> searchResults = Arrays.asList(
            createTestStudent(1L, "John", "Doe", "STU001")
        );
        when(studentRepository.findByNameContaining("john")).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/students/search")
                .param("name", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(studentRepository).findByNameContaining("john");
    }

    @Test
    @WithMockUser
    void testGetStudentCount() throws Exception {
        // Given
        when(studentRepository.countActiveStudents()).thenReturn(25L);

        // When & Then
        mockMvc.perform(get("/api/students/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));

        verify(studentRepository).countActiveStudents();
    }

    private Student createTestStudent(Long id, String firstName, String lastName, String studentId) {
        Student student = new Student();
        student.setId(id);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setStudentId(studentId);
        student.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
        student.setDateOfBirth(LocalDate.of(2010, 1, 1));
        student.setEnrollmentDate(LocalDate.now());
        student.setGradeLevel(Student.GradeLevel.GRADE_5);
        student.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        return student;
    }
}