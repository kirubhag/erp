package krs.erp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Grade;

/**
 * Repository interface for Grade entity
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Find by student ID
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);

    // Find by student ID and academic year
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.academicYear = :academicYear AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByStudentIdAndAcademicYear(@Param("studentId") Long studentId, @Param("academicYear") String academicYear);

    // Find by student ID and semester
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.semester = :semester AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    // Find by course code
    @Query("SELECT g FROM Grade g WHERE g.courseCode = :courseCode AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByCourseCode(@Param("courseCode") String courseCode);

    // Find by grade level
    @Query("SELECT g FROM Grade g WHERE g.gradeLevel = :gradeLevel AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByGradeLevel(@Param("gradeLevel") String gradeLevel);

    // Find by grade level and academic year
    @Query("SELECT g FROM Grade g WHERE g.gradeLevel = :gradeLevel AND g.academicYear = :academicYear AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByGradeLevelAndAcademicYear(@Param("gradeLevel") String gradeLevel, @Param("academicYear") String academicYear);

    // Find by exam type
    @Query("SELECT g FROM Grade g WHERE g.examType = :examType AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByExamType(@Param("examType") String examType);

    // Find by academic year
    @Query("SELECT g FROM Grade g WHERE g.academicYear = :academicYear AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByAcademicYear(@Param("academicYear") String academicYear);

    // Find by semester
    @Query("SELECT g FROM Grade g WHERE g.semester = :semester AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findBySemester(@Param("semester") String semester);

    // Find by exam date range
    @Query("SELECT g FROM Grade g WHERE g.examDate BETWEEN :startDate AND :endDate AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByExamDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find all active grades with pagination
    @Query("SELECT g FROM Grade g WHERE g.isActive = 1 ORDER BY g.examDate DESC")
    Page<Grade> findAllActive(Pageable pageable);

    // Search grades
    @Query("SELECT g FROM Grade g WHERE g.isActive = 1 AND " +
           "(LOWER(g.studentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(g.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(g.courseName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(g.examType) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(g.gradeLevel) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(g.letterGrade) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY g.examDate DESC")
    Page<Grade> searchGrades(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find by teacher ID
    @Query("SELECT g FROM Grade g WHERE g.teacherId = :teacherId AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByTeacherId(@Param("teacherId") String teacherId);

    // Count by student ID and academic year
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.studentId = :studentId AND g.academicYear = :academicYear AND g.isActive = 1")
    long countByStudentIdAndAcademicYear(@Param("studentId") Long studentId, @Param("academicYear") String academicYear);

    // Find by student ID, course code, exam type and semester (unique combination)
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.courseCode = :courseCode AND g.examType = :examType AND g.semester = :semester AND g.isActive = 1")
    Optional<Grade> findByStudentIdAndCourseCodeAndExamTypeAndSemester(
        @Param("studentId") Long studentId,
        @Param("courseCode") String courseCode,
        @Param("examType") String examType,
        @Param("semester") String semester
    );

    // Check if grade exists for student, course and exam type
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Grade g " +
           "WHERE g.studentId = :studentId AND g.courseCode = :courseCode AND " +
           "g.examType = :examType AND g.semester = :semester AND g.isActive = 1")
    boolean existsByStudentIdAndCourseCodeAndExamTypeAndSemester(
        @Param("studentId") Long studentId,
        @Param("courseCode") String courseCode,
        @Param("examType") String examType,
        @Param("semester") String semester
    );
}
