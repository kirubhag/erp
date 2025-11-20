package krs.erp.repository;

import java.math.BigDecimal;
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

import krs.erp.model.Grade;

/**
 * Test class for GradeRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GradeRepositoryTest {

    @Autowired
    private GradeRepository gradeRepository;

    private Grade testGrade1;
    private Grade testGrade2;
    private Grade testGrade3;

    @BeforeEach
    void setUp() {
        gradeRepository.deleteAll();

        // Create test grade 1 - Student 1001, Fall 2024, Midterm
        testGrade1 = new Grade();
        testGrade1.setStudentId(1001L);
        testGrade1.setStudentName("John Doe");
        testGrade1.setGradeLevel("Grade 10");
        testGrade1.setCourseCode("MATH101");
        testGrade1.setCourseName("Algebra I");
        testGrade1.setExamType("Midterm");
        testGrade1.setMarksObtained(new BigDecimal("85.00"));
        testGrade1.setTotalMarks(new BigDecimal("100.00"));
        testGrade1.setPercentage(new BigDecimal("85.00"));
        testGrade1.setLetterGrade("B");
        testGrade1.setGradePoint(new BigDecimal("3.0"));
        testGrade1.setExamDate(LocalDate.of(2024, 10, 15));
        testGrade1.setSemester("Fall 2024");
        testGrade1.setAcademicYear("2024-2025");
        testGrade1.setTeacherId("T001");
        testGrade1.setTeacherName("Prof. Smith");
        testGrade1.setIsActive(1);
        testGrade1.setCreatedTime(LocalDateTime.now());
        testGrade1 = gradeRepository.save(testGrade1);

        // Create test grade 2 - Student 1001, Fall 2024, Final
        testGrade2 = new Grade();
        testGrade2.setStudentId(1001L);
        testGrade2.setStudentName("John Doe");
        testGrade2.setGradeLevel("Grade 10");
        testGrade2.setCourseCode("MATH101");
        testGrade2.setCourseName("Algebra I");
        testGrade2.setExamType("Final");
        testGrade2.setMarksObtained(new BigDecimal("92.00"));
        testGrade2.setTotalMarks(new BigDecimal("100.00"));
        testGrade2.setPercentage(new BigDecimal("92.00"));
        testGrade2.setLetterGrade("A");
        testGrade2.setGradePoint(new BigDecimal("4.0"));
        testGrade2.setExamDate(LocalDate.of(2024, 12, 20));
        testGrade2.setSemester("Fall 2024");
        testGrade2.setAcademicYear("2024-2025");
        testGrade2.setTeacherId("T001");
        testGrade2.setTeacherName("Prof. Smith");
        testGrade2.setIsActive(1);
        testGrade2.setCreatedTime(LocalDateTime.now());
        testGrade2 = gradeRepository.save(testGrade2);

        // Create test grade 3 - Student 1002, Fall 2024, Midterm
        testGrade3 = new Grade();
        testGrade3.setStudentId(1002L);
        testGrade3.setStudentName("Jane Smith");
        testGrade3.setGradeLevel("Grade 11");
        testGrade3.setCourseCode("ENG201");
        testGrade3.setCourseName("English Literature");
        testGrade3.setExamType("Midterm");
        testGrade3.setMarksObtained(new BigDecimal("78.00"));
        testGrade3.setTotalMarks(new BigDecimal("100.00"));
        testGrade3.setPercentage(new BigDecimal("78.00"));
        testGrade3.setLetterGrade("C+");
        testGrade3.setGradePoint(new BigDecimal("2.5"));
        testGrade3.setExamDate(LocalDate.of(2024, 10, 20));
        testGrade3.setSemester("Fall 2024");
        testGrade3.setAcademicYear("2024-2025");
        testGrade3.setTeacherId("T002");
        testGrade3.setTeacherName("Prof. Johnson");
        testGrade3.setIsActive(1);
        testGrade3.setCreatedTime(LocalDateTime.now());
        testGrade3 = gradeRepository.save(testGrade3);
    }

    @Test
    void testFindByStudentId() {
        List<Grade> grades = gradeRepository.findByStudentId(1001L);
        assertThat(grades).hasSize(2);
        assertThat(grades.get(0).getExamDate()).isAfter(grades.get(1).getExamDate()); // Ordered by exam date DESC
    }

    @Test
    void testFindByStudentIdAndAcademicYear() {
        List<Grade> grades = gradeRepository.findByStudentIdAndAcademicYear(1001L, "2024-2025");
        assertThat(grades).hasSize(2);
        assertThat(grades).allMatch(g -> g.getAcademicYear().equals("2024-2025"));
    }

    @Test
    void testFindByStudentIdAndSemester() {
        List<Grade> grades = gradeRepository.findByStudentIdAndSemester(1001L, "Fall 2024");
        assertThat(grades).hasSize(2);
        assertThat(grades).allMatch(g -> g.getSemester().equals("Fall 2024"));
    }

    @Test
    void testFindByCourseCode() {
        List<Grade> grades = gradeRepository.findByCourseCode("MATH101");
        assertThat(grades).hasSize(2);
        assertThat(grades).allMatch(g -> g.getCourseCode().equals("MATH101"));
    }

    @Test
    void testFindByGradeLevel() {
        List<Grade> grades = gradeRepository.findByGradeLevel("Grade 10");
        assertThat(grades).hasSize(2);
        
        grades = gradeRepository.findByGradeLevel("Grade 11");
        assertThat(grades).hasSize(1);
    }

    @Test
    void testFindByGradeLevelAndAcademicYear() {
        List<Grade> grades = gradeRepository.findByGradeLevelAndAcademicYear("Grade 10", "2024-2025");
        assertThat(grades).hasSize(2);
    }

    @Test
    void testFindByExamType() {
        List<Grade> midterms = gradeRepository.findByExamType("Midterm");
        assertThat(midterms).hasSize(2);
        
        List<Grade> finals = gradeRepository.findByExamType("Final");
        assertThat(finals).hasSize(1);
    }

    @Test
    void testFindByAcademicYear() {
        List<Grade> grades = gradeRepository.findByAcademicYear("2024-2025");
        assertThat(grades).hasSize(3);
    }

    @Test
    void testFindBySemester() {
        List<Grade> grades = gradeRepository.findBySemester("Fall 2024");
        assertThat(grades).hasSize(3);
    }

    @Test
    void testFindByExamDateBetween() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);
        
        List<Grade> grades = gradeRepository.findByExamDateBetween(startDate, endDate);
        assertThat(grades).hasSize(2); // Two midterm exams in October
    }

    @Test
    void testFindAllActive() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Grade> page = gradeRepository.findAllActive(pageable);
        
        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void testSearchGrades() {
        Pageable pageable = PageRequest.of(0, 10);
        
        // Search by student name
        Page<Grade> page = gradeRepository.searchGrades("John", pageable);
        assertThat(page.getContent()).hasSize(2);
        
        // Search by course code
        page = gradeRepository.searchGrades("MATH", pageable);
        assertThat(page.getContent()).hasSize(2);
        
        // Search by letter grade (at least 1 match expected - our test data plus any from initializers)
        page = gradeRepository.searchGrades("A", pageable);
        assertThat(page.getContent().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testFindByTeacherId() {
        List<Grade> grades = gradeRepository.findByTeacherId("T001");
        assertThat(grades).hasSize(2);
        
        grades = gradeRepository.findByTeacherId("T002");
        assertThat(grades).hasSize(1);
    }

    @Test
    void testCountByStudentIdAndAcademicYear() {
        long count = gradeRepository.countByStudentIdAndAcademicYear(1001L, "2024-2025");
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testFindByStudentIdAndCourseCodeAndExamTypeAndSemester() {
        Optional<Grade> grade = gradeRepository.findByStudentIdAndCourseCodeAndExamTypeAndSemester(
            1001L, "MATH101", "Midterm", "Fall 2024"
        );
        
        assertThat(grade).isPresent();
        assertThat(grade.get().getMarksObtained()).isEqualByComparingTo(new BigDecimal("85.00"));
    }

    @Test
    void testExistsByStudentIdAndCourseCodeAndExamTypeAndSemester() {
        boolean exists = gradeRepository.existsByStudentIdAndCourseCodeAndExamTypeAndSemester(
            1001L, "MATH101", "Midterm", "Fall 2024"
        );
        assertThat(exists).isTrue();
        
        exists = gradeRepository.existsByStudentIdAndCourseCodeAndExamTypeAndSemester(
            1001L, "MATH101", "Midterm", "Spring 2025"
        );
        assertThat(exists).isFalse();
    }

    @Test
    void testSaveGrade() {
        Grade newGrade = new Grade();
        newGrade.setStudentId(1003L);
        newGrade.setStudentName("Bob Wilson");
        newGrade.setGradeLevel("Grade 12");
        newGrade.setCourseCode("PHY301");
        newGrade.setCourseName("Physics");
        newGrade.setExamType("Quiz");
        newGrade.setMarksObtained(new BigDecimal("18.00"));
        newGrade.setTotalMarks(new BigDecimal("20.00"));
        newGrade.setPercentage(new BigDecimal("90.00"));
        newGrade.setLetterGrade("A");
        newGrade.setGradePoint(new BigDecimal("4.0"));
        newGrade.setExamDate(LocalDate.now());
        newGrade.setSemester("Fall 2024");
        newGrade.setAcademicYear("2024-2025");
        newGrade.setIsActive(1);
        newGrade.setCreatedTime(LocalDateTime.now());
        
        Grade saved = gradeRepository.save(newGrade);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCourseCode()).isEqualTo("PHY301");
    }

    @Test
    void testUpdateGrade() {
        testGrade1.setRemarks("Excellent improvement");
        testGrade1.setModifiedTime(LocalDateTime.now());
        
        Grade updated = gradeRepository.save(testGrade1);
        assertThat(updated.getRemarks()).isEqualTo("Excellent improvement");
    }

    @Test
    void testDeleteGrade() {
        Long gradeId = testGrade1.getId();
        gradeRepository.delete(testGrade1);
        
        assertThat(gradeRepository.findById(gradeId)).isEmpty();
    }

    @Test
    void testSoftDelete() {
        testGrade1.setIsActive(0);
        gradeRepository.save(testGrade1);
        
        // Should not appear in active queries
        List<Grade> activeGrades = gradeRepository.findByStudentId(1001L);
        assertThat(activeGrades).hasSize(1); // Only the Final exam grade
    }
}
