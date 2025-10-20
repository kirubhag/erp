package krs.erp.integration;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.model.Student;
import krs.erp.repository.StudentRepository;

/**
 * Integration tests for Student management
 */
@SpringBootTest
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void testCompleteStudentWorkflow() throws Exception {
        // 1. Create a student
        Student newStudent = createTestStudent();
        
        String studentJson = objectMapper.writeValueAsString(newStudent);
        
        String result = mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Student createdStudent = objectMapper.readValue(result, Student.class);
        assertNotNull(createdStudent.getId());

        // 2. Retrieve the student
        mockMvc.perform(get("/api/students/" + createdStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.studentId").value("STU001"));

        // 3. Update the student
        createdStudent.setLastName("Smith");
        String updatedJson = objectMapper.writeValueAsString(createdStudent);

        mockMvc.perform(put("/api/students/" + createdStudent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"));

        // 4. Search for the student
        mockMvc.perform(get("/api/students/search")
                .param("name", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("John"));

        // 5. Delete the student
        mockMvc.perform(delete("/api/students/" + createdStudent.getId()))
                .andExpect(status().isNoContent());

        // 6. Verify deletion
        mockMvc.perform(get("/api/students/" + createdStudent.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testStudentsByGradeLevel() throws Exception {
        // Create students with different grade levels
        Student grade5Student1 = createTestStudent();
        grade5Student1.setStudentId("STU001");
        grade5Student1.setGradeLevel(Student.GradeLevel.GRADE_5);
        
        Student grade5Student2 = createTestStudent();
        grade5Student2.setStudentId("STU002");
        grade5Student2.setFirstName("Jane");
        grade5Student2.setGradeLevel(Student.GradeLevel.GRADE_5);
        
        Student grade6Student = createTestStudent();
        grade6Student.setStudentId("STU003");
        grade6Student.setFirstName("Bob");
        grade6Student.setGradeLevel(Student.GradeLevel.GRADE_6);

        // Save students
        studentRepository.save(grade5Student1);
        studentRepository.save(grade5Student2);
        studentRepository.save(grade6Student);

        // Test getting students by grade level
        mockMvc.perform(get("/api/students/grade/GRADE_5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/students/grade/GRADE_6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Bob"));
    }

    @Test
    @WithMockUser
    void testActiveStudents() throws Exception {
        // Create active and inactive students
        Student activeStudent1 = createTestStudent();
        activeStudent1.setStudentId("STU001");
        activeStudent1.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        
        Student activeStudent2 = createTestStudent();
        activeStudent2.setStudentId("STU002");
        activeStudent2.setFirstName("Jane");
        activeStudent2.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        
        Student inactiveStudent = createTestStudent();
        inactiveStudent.setStudentId("STU003");
        inactiveStudent.setFirstName("Bob");
        inactiveStudent.setEnrollmentStatus(Student.EnrollmentStatus.INACTIVE);

        // Save students
        studentRepository.save(activeStudent1);
        studentRepository.save(activeStudent2);
        studentRepository.save(inactiveStudent);

        // Test getting active students
        mockMvc.perform(get("/api/students/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        // Test student count
        mockMvc.perform(get("/api/students/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    @WithMockUser
    void testStudentValidation() throws Exception {
        // Test creating student with missing required fields
        Student invalidStudent = new Student();
        invalidStudent.setFirstName(""); // Empty first name
        
        String invalidJson = objectMapper.writeValueAsString(invalidStudent);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testStudentEnrollmentDateRange() throws Exception {
        // Create students with different enrollment dates
        Student student2024 = createTestStudent();
        student2024.setStudentId("STU001");
        student2024.setEnrollmentDate(LocalDate.of(2024, 6, 15));
        
        Student student2023 = createTestStudent();
        student2023.setStudentId("STU002");
        student2023.setFirstName("Jane");
        student2023.setEnrollmentDate(LocalDate.of(2023, 9, 1));

        // Save students
        studentRepository.save(student2024);
        studentRepository.save(student2023);

        // Test getting students by enrollment date range
        mockMvc.perform(get("/api/students/enrollment")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].studentId").value("STU001"));
    }

    @Test
    @WithMockUser 
    void testPagination() throws Exception {
        // Create multiple students
        for (int i = 1; i <= 15; i++) {
            Student student = createTestStudent();
            student.setStudentId("STU" + String.format("%03d", i));
            student.setFirstName("Student" + i);
            studentRepository.save(student);
        }

        // Test pagination
        mockMvc.perform(get("/api/students")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(get("/api/students")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5));
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
}