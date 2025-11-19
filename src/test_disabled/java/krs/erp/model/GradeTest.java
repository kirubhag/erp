package krs.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Grade model
 */
class GradeTest {

    private Grade grade;

    @BeforeEach
    void setUp() {
        grade = new Grade();
    }

    @Test
    void testGradeCreation() {
        // Test basic grade creation
        grade.setStudentId(1L);
        grade.setStudentName("John Doe");
        grade.setGradeLevel("Grade 10");
        grade.setCourseCode("MATH101");
        grade.setCourseName("Mathematics");
        grade.setExamType("Final Exam");
        grade.setMarksObtained(new BigDecimal("85.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));
        grade.setExamDate(LocalDate.of(2024, 6, 15));
        grade.setSemester("Fall 2024");
        grade.setAcademicYear("2024-2025");

        assertEquals(1L, grade.getStudentId());
        assertEquals("John Doe", grade.getStudentName());
        assertEquals("Grade 10", grade.getGradeLevel());
        assertEquals("MATH101", grade.getCourseCode());
        assertEquals("Mathematics", grade.getCourseName());
        assertEquals("Final Exam", grade.getExamType());
        assertEquals(new BigDecimal("85.00"), grade.getMarksObtained());
        assertEquals(new BigDecimal("100.00"), grade.getTotalMarks());
        assertEquals(LocalDate.of(2024, 6, 15), grade.getExamDate());
        assertEquals("Fall 2024", grade.getSemester());
        assertEquals("2024-2025", grade.getAcademicYear());
    }

    @Test
    void testPercentageCalculation() {
        // Test percentage calculation when marks are set
        grade.setMarksObtained(new BigDecimal("85.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertNotNull(grade.getPercentage());
        assertEquals(new BigDecimal("85.00"), grade.getPercentage());
    }

    @Test
    void testPercentageCalculationWithDecimal() {
        // Test percentage calculation with decimal values
        grade.setMarksObtained(new BigDecimal("47.5"));
        grade.setTotalMarks(new BigDecimal("50.0"));

        assertNotNull(grade.getPercentage());
        assertEquals(new BigDecimal("95.00"), grade.getPercentage());
    }

    @Test
    void testLetterGradeAPlusCalculation() {
        // Test A+ grade (90% and above)
        grade.setMarksObtained(new BigDecimal("95.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("A+", grade.getLetterGrade());
        assertEquals(new BigDecimal("10.0"), grade.getGradePoint());
    }

    @Test
    void testLetterGradeACalculation() {
        // Test A grade (80-89%)
        grade.setMarksObtained(new BigDecimal("85.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("A", grade.getLetterGrade());
        assertEquals(new BigDecimal("9.0"), grade.getGradePoint());
    }

    @Test
    void testLetterGradeBPlusCalculation() {
        // Test B+ grade (70-79%)
        grade.setMarksObtained(new BigDecimal("75.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("B+", grade.getLetterGrade());
        assertEquals(new BigDecimal("8.0"), grade.getGradePoint());
    }

    @Test
    void testLetterGradeBCalculation() {
        // Test B grade (60-69%)
        grade.setMarksObtained(new BigDecimal("65.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("B", grade.getLetterGrade());
        assertEquals(new BigDecimal("7.0"), grade.getGradePoint());
    }

    @Test
    void testLetterGradeCCalculation() {
        // Test C grade (50-59%)
        grade.setMarksObtained(new BigDecimal("55.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("C", grade.getLetterGrade());
        assertEquals(new BigDecimal("6.0"), grade.getGradePoint());
    }

    @Test
    void testLetterGradeDCalculation() {
        // Test D grade (40-49%)
        grade.setMarksObtained(new BigDecimal("45.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("D", grade.getLetterGrade());
        assertEquals(new BigDecimal("5.0"), grade.getGradePoint());
    }

    @Test
    void testLetterGradeFCalculation() {
        // Test F grade (below 40%)
        grade.setMarksObtained(new BigDecimal("35.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("F", grade.getLetterGrade());
        assertEquals(new BigDecimal("0.0"), grade.getGradePoint());
    }

    @Test
    void testBoundaryGradeAt90Percent() {
        // Test boundary at exactly 90%
        grade.setMarksObtained(new BigDecimal("90.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("A+", grade.getLetterGrade());
        assertEquals(new BigDecimal("10.0"), grade.getGradePoint());
    }

    @Test
    void testBoundaryGradeAt80Percent() {
        // Test boundary at exactly 80%
        grade.setMarksObtained(new BigDecimal("80.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("A", grade.getLetterGrade());
        assertEquals(new BigDecimal("9.0"), grade.getGradePoint());
    }

    @Test
    void testBoundaryGradeAt70Percent() {
        // Test boundary at exactly 70%
        grade.setMarksObtained(new BigDecimal("70.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals("B+", grade.getLetterGrade());
        assertEquals(new BigDecimal("8.0"), grade.getGradePoint());
    }

    @Test
    void testGradePointCalculationUpdatesOnMarksChange() {
        // Test that grade point updates when marks change
        grade.setMarksObtained(new BigDecimal("95.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("10.0"), grade.getGradePoint());

        // Change marks
        grade.setMarksObtained(new BigDecimal("75.00"));
        assertEquals(new BigDecimal("8.0"), grade.getGradePoint());
    }

    @Test
    void testPercentageWithZeroTotalMarks() {
        // Test that percentage doesn't get calculated with zero total marks
        grade.setMarksObtained(new BigDecimal("50.00"));
        grade.setTotalMarks(BigDecimal.ZERO);

        // Percentage should remain null or not cause error
        // The calculation method should handle this gracefully
    }

    @Test
    void testTeacherInformation() {
        // Test teacher information fields
        grade.setTeacherId("T12345");
        grade.setTeacherName("Dr. Smith");

        assertEquals("T12345", grade.getTeacherId());
        assertEquals("Dr. Smith", grade.getTeacherName());
    }

    @Test
    void testRemarks() {
        // Test remarks field
        String remarks = "Excellent performance in mathematics. Shows strong analytical skills.";
        grade.setRemarks(remarks);

        assertEquals(remarks, grade.getRemarks());
    }

    @Test
    void testOrganizationId() {
        // Test organization ID
        grade.setOrganizationId(100L);
        assertEquals(100L, grade.getOrganizationId());
    }

    @Test
    void testCompleteGradeRecord() {
        // Test creating a complete grade record
        grade.setStudentId(1L);
        grade.setStudentName("Alice Johnson");
        grade.setGradeLevel("Grade 12");
        grade.setCourseCode("PHY201");
        grade.setCourseName("Physics Advanced");
        grade.setExamType("Midterm");
        grade.setMarksObtained(new BigDecimal("92.50"));
        grade.setTotalMarks(new BigDecimal("100.00"));
        grade.setExamDate(LocalDate.of(2024, 11, 20));
        grade.setSemester("Fall 2024");
        grade.setAcademicYear("2024-2025");
        grade.setTeacherId("T5678");
        grade.setTeacherName("Prof. Anderson");
        grade.setRemarks("Outstanding work");
        grade.setOrganizationId(1L);

        // Verify all fields
        assertEquals(1L, grade.getStudentId());
        assertEquals("Alice Johnson", grade.getStudentName());
        assertEquals("Grade 12", grade.getGradeLevel());
        assertEquals("PHY201", grade.getCourseCode());
        assertEquals("Physics Advanced", grade.getCourseName());
        assertEquals("Midterm", grade.getExamType());
        assertEquals(new BigDecimal("92.50"), grade.getMarksObtained());
        assertEquals(new BigDecimal("100.00"), grade.getTotalMarks());
        assertEquals(new BigDecimal("92.50"), grade.getPercentage());
        assertEquals("A+", grade.getLetterGrade());
        assertEquals(new BigDecimal("10.0"), grade.getGradePoint());
        assertEquals(LocalDate.of(2024, 11, 20), grade.getExamDate());
        assertEquals("Fall 2024", grade.getSemester());
        assertEquals("2024-2025", grade.getAcademicYear());
        assertEquals("T5678", grade.getTeacherId());
        assertEquals("Prof. Anderson", grade.getTeacherName());
        assertEquals("Outstanding work", grade.getRemarks());
        assertEquals(1L, grade.getOrganizationId());
    }

    @Test
    void testToString() {
        // Test toString method
        grade.setStudentId(1L);
        grade.setStudentName("Test Student");
        grade.setGradeLevel("Grade 9");
        grade.setCourseCode("ENG101");
        grade.setCourseName("English");
        grade.setExamType("Quiz");
        grade.setMarksObtained(new BigDecimal("80.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));
        grade.setExamDate(LocalDate.of(2024, 10, 1));
        grade.setSemester("Fall 2024");
        grade.setAcademicYear("2024-2025");

        String gradeString = grade.toString();
        
        assertNotNull(gradeString);
        assertTrue(gradeString.contains("studentId=1"));
        assertTrue(gradeString.contains("studentName='Test Student'"));
        assertTrue(gradeString.contains("courseCode='ENG101'"));
        assertTrue(gradeString.contains("marksObtained=80.00"));
    }

    @Test
    void testPerfectScore() {
        // Test perfect score (100%)
        grade.setMarksObtained(new BigDecimal("100.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), grade.getPercentage());
        assertEquals("A+", grade.getLetterGrade());
        assertEquals(new BigDecimal("10.0"), grade.getGradePoint());
    }

    @Test
    void testZeroScore() {
        // Test zero score
        grade.setMarksObtained(BigDecimal.ZERO);
        grade.setTotalMarks(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("0.00"), grade.getPercentage());
        assertEquals("F", grade.getLetterGrade());
        assertEquals(new BigDecimal("0.0"), grade.getGradePoint());
    }

    @Test
    void testPartialMarks() {
        // Test with partial marks (different total marks)
        grade.setMarksObtained(new BigDecimal("30.00"));
        grade.setTotalMarks(new BigDecimal("50.00"));

        assertEquals(new BigDecimal("60.00"), grade.getPercentage());
        assertEquals("B", grade.getLetterGrade());
        assertEquals(new BigDecimal("7.0"), grade.getGradePoint());
    }

    @Test
    void testMultipleSemestersAndYears() {
        // Test setting different semesters
        grade.setSemester("Spring 2024");
        assertEquals("Spring 2024", grade.getSemester());

        grade.setSemester("Fall 2023");
        assertEquals("Fall 2023", grade.getSemester());

        // Test academic years
        grade.setAcademicYear("2023-2024");
        assertEquals("2023-2024", grade.getAcademicYear());
    }

    @Test
    void testExamTypes() {
        // Test different exam types
        String[] examTypes = {"Midterm", "Final", "Quiz", "Assignment", "Project"};
        
        for (String examType : examTypes) {
            grade.setExamType(examType);
            assertEquals(examType, grade.getExamType());
        }
    }

    @Test
    void testGradeLevels() {
        // Test different grade levels
        String[] gradeLevels = {"Grade 1", "Grade 5", "Grade 10", "Grade 12"};
        
        for (String gradeLevel : gradeLevels) {
            grade.setGradeLevel(gradeLevel);
            assertEquals(gradeLevel, grade.getGradeLevel());
        }
    }

    @Test
    void testNullMarksHandling() {
        // Test that null marks don't cause errors
        grade.setMarksObtained(null);
        grade.setTotalMarks(null);

        // Should not throw exception
        assertNull(grade.getPercentage());
    }

    @Test
    void testRecalculationOnTotalMarksChange() {
        // Set initial marks
        grade.setMarksObtained(new BigDecimal("80.00"));
        grade.setTotalMarks(new BigDecimal("100.00"));
        
        assertEquals(new BigDecimal("80.00"), grade.getPercentage());
        assertEquals("A", grade.getLetterGrade());

        // Change total marks - percentage should recalculate
        grade.setTotalMarks(new BigDecimal("200.00"));
        
        assertEquals(new BigDecimal("40.00"), grade.getPercentage());
        assertEquals("D", grade.getLetterGrade());
    }
}
