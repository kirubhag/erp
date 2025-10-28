package krs.erp.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.model.Grade;
import krs.erp.repository.GradeRepository;

@WebMvcTest(GradeController.class)
@ActiveProfiles("test")
@WithMockUser
public class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GradeRepository gradeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Grade createTestGrade(Long id, Long studentId, String courseCode, String examType, String semester) {
        Grade grade = new Grade();
        grade.setId(id);
        grade.setStudentId(studentId);
        grade.setStudentName("Test Student");
        grade.setGradeLevel("Grade 10");
        grade.setCourseCode(courseCode);
        grade.setCourseName("Test Course");
        grade.setExamType(examType);
        grade.setMarksObtained(new BigDecimal("85.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));
        grade.setExamDate(LocalDate.now());
        grade.setSemester(semester);
        grade.setAcademicYear("2024-2025");
        grade.setRemarks("Test remarks");
        grade.setTeacherId("T001");
        grade.setTeacherName("Test Teacher");
        grade.setOrganizationId(1L);
        grade.markAsActive();
        return grade;
    }

    @Test
    public void testGetAllGrades() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        Grade grade2 = createTestGrade(2L, 1002L, "ENG101", "Final", "Spring 2025");
        Page<Grade> page = new PageImpl<>(Arrays.asList(grade1, grade2));

        when(gradeRepository.findAllActive(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/grades")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].studentId", is(1001)))
                .andExpect(jsonPath("$.content[1].studentId", is(1002)));
    }

    @Test
    public void testGetGradeById() throws Exception {
        Grade grade = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        mockMvc.perform(get("/api/grades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.courseCode", is("MATH101")));
    }

    @Test
    public void testGetGradeByIdNotFound() throws Exception {
        when(gradeRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/grades/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetGradeByIdInactive() throws Exception {
        Grade grade = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        grade.markAsDeleted();
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        mockMvc.perform(get("/api/grades/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetActiveGrades() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        Page<Grade> page = new PageImpl<>(Arrays.asList(grade1));

        when(gradeRepository.findAllActive(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/grades/active")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void testGetGradesByStudentId() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        Grade grade2 = createTestGrade(2L, 1001L, "ENG101", "Final", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1, grade2);

        when(gradeRepository.findByStudentId(1001L)).thenReturn(grades);

        mockMvc.perform(get("/api/grades/student/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentId", is(1001)))
                .andExpect(jsonPath("$[1].studentId", is(1001)));
    }

    @Test
    public void testGetGradesByStudentIdAndAcademicYear() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findByStudentIdAndAcademicYear(1001L, "2024-2025")).thenReturn(grades);

        mockMvc.perform(get("/api/grades/student/1001/year/2024-2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].academicYear", is("2024-2025")));
    }

    @Test
    public void testGetGradesByStudentIdAndSemester() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findByStudentIdAndSemester(1001L, "Fall 2024")).thenReturn(grades);

        mockMvc.perform(get("/api/grades/student/1001/semester/Fall 2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].semester", is("Fall 2024")));
    }

    @Test
    public void testGetGradesByCourseCode() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findByCourseCode("MATH101")).thenReturn(grades);

        mockMvc.perform(get("/api/grades/course/MATH101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseCode", is("MATH101")));
    }

    @Test
    public void testGetGradesByGradeLevel() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findByGradeLevel("Grade 10")).thenReturn(grades);

        mockMvc.perform(get("/api/grades/grade-level/Grade 10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gradeLevel", is("Grade 10")));
    }

    @Test
    public void testGetGradesByGradeLevelAndAcademicYear() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findByGradeLevelAndAcademicYear("Grade 10", "2024-2025")).thenReturn(grades);

        mockMvc.perform(get("/api/grades/grade-level/Grade 10/year/2024-2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetGradesByExamType() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findByExamType("Midterm")).thenReturn(grades);

        mockMvc.perform(get("/api/grades/exam-type/Midterm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].examType", is("Midterm")));
    }

    @Test
    public void testGetGradesByAcademicYear() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findByAcademicYear("2024-2025")).thenReturn(grades);

        mockMvc.perform(get("/api/grades/year/2024-2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].academicYear", is("2024-2025")));
    }

    @Test
    public void testGetGradesBySemester() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findBySemester("Fall 2024")).thenReturn(grades);

        mockMvc.perform(get("/api/grades/semester/Fall 2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].semester", is("Fall 2024")));
    }

    @Test
    public void testGetGradesByExamDateRange() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findByExamDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(grades);

        mockMvc.perform(get("/api/grades/date-range")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetGradesByTeacherId() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        List<Grade> grades = Arrays.asList(grade1);

        when(gradeRepository.findByTeacherId("T001")).thenReturn(grades);

        mockMvc.perform(get("/api/grades/teacher/T001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].teacherId", is("T001")));
    }

    @Test
    public void testSearchGrades() throws Exception {
        Grade grade1 = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        Page<Grade> page = new PageImpl<>(Arrays.asList(grade1));

        when(gradeRepository.searchGrades(Mockito.anyString(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/grades/search")
                .param("q", "MATH")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void testCreateGrade() throws Exception {
        Grade grade = createTestGrade(null, 1001L, "MATH101", "Midterm", "Fall 2024");
        Grade savedGrade = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");

        when(gradeRepository.existsByStudentIdAndCourseCodeAndExamTypeAndSemester(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(false);
        when(gradeRepository.save(any(Grade.class))).thenReturn(savedGrade);

        mockMvc.perform(post("/api/grades")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(grade)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.courseCode", is("MATH101")));
    }

    @Test
    public void testCreateGradeDuplicate() throws Exception {
        Grade grade = createTestGrade(null, 1001L, "MATH101", "Midterm", "Fall 2024");

        when(gradeRepository.existsByStudentIdAndCourseCodeAndExamTypeAndSemester(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);

        mockMvc.perform(post("/api/grades")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(grade)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateGrade() throws Exception {
        Grade existingGrade = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        Grade updatedGrade = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        updatedGrade.setRemarks("Updated remarks");

        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(updatedGrade);

        mockMvc.perform(put("/api/grades/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void testUpdateGradeNotFound() throws Exception {
        Grade grade = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");

        when(gradeRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/grades/999")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(grade)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateGradeInactive() throws Exception {
        Grade grade = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        grade.markAsDeleted();

        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        Grade updateData = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        mockMvc.perform(put("/api/grades/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteGrade() throws Exception {
        Grade grade = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");

        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(grade);

        mockMvc.perform(delete("/api/grades/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteGradeNotFound() throws Exception {
        when(gradeRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/grades/999")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteGradeAlreadyInactive() throws Exception {
        Grade grade = createTestGrade(1L, 1001L, "MATH101", "Midterm", "Fall 2024");
        grade.markAsDeleted();

        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        mockMvc.perform(delete("/api/grades/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCountGradesByStudentIdAndAcademicYear() throws Exception {
        when(gradeRepository.countByStudentIdAndAcademicYear(1001L, "2024-2025")).thenReturn(5L);

        mockMvc.perform(get("/api/grades/count/student/1001/year/2024-2025"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}
