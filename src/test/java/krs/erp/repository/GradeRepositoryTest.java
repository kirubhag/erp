package krs.erp.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import krs.erp.model.Grade;

/**
 * Test class for GradeRepository
 */
@DataJpaTest
@ActiveProfiles("test")
public class GradeRepositoryTest {

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
        testGrade1.setCourseName("Advanced Mathematics");
        testGrade1.setExamType("Midterm");
        testGrade1.setMarksObtained(new BigDecimal("85.00"));
        testGrade1.setTotalMarks(new BigDecimal("100.00"));
        testGrade1.setExamDate(LocalDate.of(2024, 11, 15));
        testGrade1.setSemester("Fall 2024");
        testGrade1.setAcademicYear("2024-2025");
        testGrade1.setRemarks("Excellent performance");
        testGrade1.setTeacherId("T001");
        testGrade1.setTeacherName("Dr. Smith");
        testGrade1.setOrganizationId(1L);
        testGrade1.markAsActive();
        testGrade1 = gradeRepository.save(testGrade1);

        // Create test grade 2 - Student 1001, Fall 2024, Final
        testGrade2 = new Grade();
        testGrade2.setStudentId(1001L);
        testGrade2.setStudentName("John Doe");
        testGrade2.setGradeLevel("Grade 10");
        testGrade2.setCourseCode("MATH101");
        testGrade2.setCourseName("Advanced Mathematics");
        testGrade2.setExamType("Final");
        testGrade2.setMarksObtained(new BigDecimal("92.00"));
        testGrade2.setTotalMarks(new BigDecimal("100.00"));
        testGrade2.setExamDate(LocalDate.of(2024, 12, 20));
        testGrade2.setSemester("Fall 2024");
        testGrade2.setAcademicYear("2024-2025");
        testGrade2.setRemarks("Outstanding improvement");
        testGrade2.setTeacherId("T001");
        testGrade2.setTeacherName("Dr. Smith");
        testGrade2.setOrganizationId(1L);
        testGrade2.markAsActive();
        testGrade2 = gradeRepository.save(testGrade2);

        // Create test grade 3 - Student 1002, Spring 2025, Midterm
        testGrade3 = new Grade();
        testGrade3.setStudentId(1002L);
        testGrade3.setStudentName("Jane Smith");
        testGrade3.setGradeLevel("Grade 11");
        testGrade3.setCourseCode("ENG101");
        testGrade3.setCourseName("English Literature");
        testGrade3.setExamType("Midterm");
        testGrade3.setMarksObtained(new BigDecimal("78.00"));
        testGrade3.setTotalMarks(new BigDecimal("100.00"));
        testGrade3.setExamDate(LocalDate.of(2025, 3, 15));
        testGrade3.setSemester("Spring 2025");
        testGrade3.setAcademicYear("2024-2025");
        testGrade3.setRemarks("Good analytical skills");
        testGrade3.setTeacherId("T002");
        testGrade3.setTeacherName("Prof. Johnson");
        testGrade3.setOrganizationId(1L);
        testGrade3.markAsActive();
        testGrade3 = gradeRepository.save(testGrade3);
    }

    @Test
    void testFindByStudentId() {
        List<Grade> grades = gradeRepository.findByStudentId(1001L);
        assertThat(grades).hasSize(2);
        assertThat(grades).extracting(Grade::getStudentId).containsOnly(1001L);
    }

    @Test
    void testFindByStudentIdAndAcademicYear() {
        List<Grade> grades = gradeRepository.findByStudentIdAndAcademicYear(1001L, "2024-2025");
        assertThat(grades).hasSize(2);
        assertThat(grades).allMatch(g -> g.getStudentId().equals(1001L) && "2024-2025".equals(g.getAcademicYear()));
    }

    @Test
    void testFindByStudentIdAndSemester() {
        List<Grade> grades = gradeRepository.findByStudentIdAndSemester(1001L, "Fall 2024");
        assertThat(grades).hasSize(2);
        assertThat(grades).allMatch(g -> g.getStudentId().equals(1001L) && "Fall 2024".equals(g.getSemester()));
    }

    @Test
    void testFindByCourseCode() {
        List<Grade> grades = gradeRepository.findByCourseCode("MATH101");
        assertThat(grades).hasSize(2);
        assertThat(grades).extracting(Grade::getCourseCode).containsOnly("MATH101");
    }

    @Test
    void testFindByGradeLevel() {
        List<Grade> grades = gradeRepository.findByGradeLevel("Grade 10");
        assertThat(grades).hasSize(2);
        assertThat(grades).extracting(Grade::getGradeLevel).containsOnly("Grade 10");
    }

    @Test
    void testFindByGradeLevelAndAcademicYear() {
        List<Grade> grades = gradeRepository.findByGradeLevelAndAcademicYear("Grade 10", "2024-2025");
        assertThat(grades).hasSize(2);
        assertThat(grades).allMatch(g -> "Grade 10".equals(g.getGradeLevel()) && "2024-2025".equals(g.getAcademicYear()));
    }

    @Test
    void testFindByExamType() {
        List<Grade> grades = gradeRepository.findByExamType("Midterm");
        assertThat(grades).hasSize(2);
        assertThat(grades).extracting(Grade::getExamType).containsOnly("Midterm");
    }

    @Test
    void testFindByAcademicYear() {
        List<Grade> grades = gradeRepository.findByAcademicYear("2024-2025");
        assertThat(grades).hasSize(3);
        assertThat(grades).extracting(Grade::getAcademicYear).containsOnly("2024-2025");
    }

    @Test
    void testFindBySemester() {
        List<Grade> grades = gradeRepository.findBySemester("Fall 2024");
        assertThat(grades).hasSize(2);
        assertThat(grades).extracting(Grade::getSemester).containsOnly("Fall 2024");
    }

    @Test
    void testFindByExamDateBetween() {
        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        List<Grade> grades = gradeRepository.findByExamDateBetween(startDate, endDate);
        assertThat(grades).hasSize(2);
        assertThat(grades).allMatch(g -> 
            !g.getExamDate().isBefore(startDate) && !g.getExamDate().isAfter(endDate));
    }

    @Test
    void testFindAllActive() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Grade> grades = gradeRepository.findAllActive(pageable);
        assertThat(grades.getContent()).hasSize(3);
        assertThat(grades.getTotalElements()).isEqualTo(3);
    }

    @Test
    void testSearchGrades() {
        Pageable pageable = PageRequest.of(0, 10);
        
        // Search by student name
        Page<Grade> gradesByName = gradeRepository.searchGrades("John", pageable);
        assertThat(gradesByName.getContent()).hasSize(2);
        
        // Search by course code
        Page<Grade> gradesByCode = gradeRepository.searchGrades("MATH", pageable);
        assertThat(gradesByCode.getContent()).hasSize(2);
        
        // Search by exam type
        Page<Grade> gradesByExam = gradeRepository.searchGrades("Midterm", pageable);
        assertThat(gradesByExam.getContent()).hasSize(2);
    }

    @Test
    void testFindByTeacherId() {
        List<Grade> grades = gradeRepository.findByTeacherId("T001");
        assertThat(grades).hasSize(2);
        assertThat(grades).extracting(Grade::getTeacherId).containsOnly("T001");
    }

    @Test
    void testCountByStudentIdAndAcademicYear() {
        long count = gradeRepository.countByStudentIdAndAcademicYear(1001L, "2024-2025");
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testFindByStudentIdAndCourseCodeAndExamTypeAndSemester() {
        Optional<Grade> gradeOpt = gradeRepository.findByStudentIdAndCourseCodeAndExamTypeAndSemester(1001L, "MATH101", "Midterm", "Fall 2024");
        assertThat(gradeOpt).isPresent();
        Grade grade = gradeOpt.get();
        assertThat(grade.getStudentId()).isEqualTo(1001L);
        assertThat(grade.getCourseCode()).isEqualTo("MATH101");
        assertThat(grade.getExamType()).isEqualTo("Midterm");
        assertThat(grade.getSemester()).isEqualTo("Fall 2024");
    }

    @Test
    void testExistsByStudentIdAndCourseCodeAndExamTypeAndSemester() {
        // Should exist
        boolean exists1 = gradeRepository.existsByStudentIdAndCourseCodeAndExamTypeAndSemester(
                1001L, "MATH101", "Midterm", "Fall 2024");
        assertThat(exists1).isTrue();

        // Should not exist
        boolean exists2 = gradeRepository.existsByStudentIdAndCourseCodeAndExamTypeAndSemester(
                1001L, "MATH101", "Quiz", "Fall 2024");
        assertThat(exists2).isFalse();
    }

    @Test
    void testAutoCalculation() {
        // Verify auto-calculation of percentage, letterGrade, and gradePoint
        assertThat(testGrade1.getPercentage()).isEqualByComparingTo(new BigDecimal("85.00"));
        assertThat(testGrade1.getLetterGrade()).isEqualTo("A");
        assertThat(testGrade1.getGradePoint()).isEqualByComparingTo(new BigDecimal("9.00"));

        assertThat(testGrade2.getPercentage()).isEqualByComparingTo(new BigDecimal("92.00"));
        assertThat(testGrade2.getLetterGrade()).isEqualTo("A+");
        assertThat(testGrade2.getGradePoint()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void testSoftDelete() {
        // Mark as deleted
        testGrade1.markAsDeleted();
        gradeRepository.save(testGrade1);

        // Should not be found in active queries
        List<Grade> activeGrades = gradeRepository.findByStudentId(1001L);
        assertThat(activeGrades).hasSize(1); // Only testGrade2 should be found
        assertThat(activeGrades.get(0).getId()).isEqualTo(testGrade2.getId());
    }
}
