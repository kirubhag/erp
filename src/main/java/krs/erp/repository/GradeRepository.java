package krs.erp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import krs.erp.model.Grade;
import krs.erp.model.Student;
import krs.erp.model.Subject;
import krs.erp.model.Staff;

/**
 * Repository interface for Grade entity
 * Updated to use JPA relationships instead of denormalized fields
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Find by student
    @Query("SELECT g FROM Grade g WHERE g.student = :student AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByStudent(@Param("student") Student student);

    // Find by student ID (convenience method)
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);

    // Find by student and academic year
    @Query("SELECT g FROM Grade g WHERE g.student = :student AND g.academicYear = :academicYear AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByStudentAndAcademicYear(@Param("student") Student student,
            @Param("academicYear") String academicYear);

    // Find by student ID and academic year
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.academicYear = :academicYear AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByStudentIdAndAcademicYear(@Param("studentId") Long studentId,
            @Param("academicYear") String academicYear);

    // Find by student and semester
    @Query("SELECT g FROM Grade g WHERE g.student = :student AND g.semester = :semester AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByStudentAndSemester(@Param("student") Student student, @Param("semester") String semester);

    // Find by student ID and semester
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.semester = :semester AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    // Find by subject
    @Query("SELECT g FROM Grade g WHERE g.subject = :subject AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findBySubject(@Param("subject") Subject subject);

    // Find by subject ID
    @Query("SELECT g FROM Grade g WHERE g.subject.id = :subjectId AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findBySubjectId(@Param("subjectId") Long subjectId);

    // Find by subject code
    @Query("SELECT g FROM Grade g WHERE g.subject.subjectCode = :subjectCode AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findBySubjectCode(@Param("subjectCode") String subjectCode);

    // Find by grade level (from student)
    @Query("SELECT g FROM Grade g WHERE g.student.gradeLevel = :gradeLevel AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByGradeLevel(@Param("gradeLevel") String gradeLevel);

    // Find by grade level and academic year
    @Query("SELECT g FROM Grade g WHERE g.student.gradeLevel = :gradeLevel AND g.academicYear = :academicYear AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByGradeLevelAndAcademicYear(@Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear);

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

    // Search grades - updated to use relationships
    @Query("SELECT g FROM Grade g WHERE g.isActive = 1 AND " +
            "(LOWER(g.student.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(g.student.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(g.subject.subjectCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(g.subject.subjectName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(g.examType) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(g.letterGrade) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY g.examDate DESC")
    Page<Grade> searchGrades(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find by teacher
    @Query("SELECT g FROM Grade g WHERE g.teacher = :teacher AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByTeacher(@Param("teacher") Staff teacher);

    // Find by teacher ID
    @Query("SELECT g FROM Grade g WHERE g.teacher.id = :teacherId AND g.isActive = 1 ORDER BY g.examDate DESC")
    List<Grade> findByTeacherId(@Param("teacherId") Long teacherId);

    // Count by student and academic year
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.student = :student AND g.academicYear = :academicYear AND g.isActive = 1")
    long countByStudentAndAcademicYear(@Param("student") Student student, @Param("academicYear") String academicYear);

    // Count by student ID and academic year
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.student.id = :studentId AND g.academicYear = :academicYear AND g.isActive = 1")
    long countByStudentIdAndAcademicYear(@Param("studentId") Long studentId,
            @Param("academicYear") String academicYear);

    // Find by student, subject, exam type and semester (unique combination)
    @Query("SELECT g FROM Grade g WHERE g.student = :student AND g.subject = :subject AND g.examType = :examType AND g.semester = :semester AND g.isActive = 1")
    Optional<Grade> findByStudentAndSubjectAndExamTypeAndSemester(
            @Param("student") Student student,
            @Param("subject") Subject subject,
            @Param("examType") String examType,
            @Param("semester") String semester);

    // Check if grade exists for student, subject and exam type
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Grade g " +
            "WHERE g.student = :student AND g.subject = :subject AND " +
            "g.examType = :examType AND g.semester = :semester AND g.isActive = 1")
    boolean existsByStudentAndSubjectAndExamTypeAndSemester(
            @Param("student") Student student,
            @Param("subject") Subject subject,
            @Param("examType") String examType,
            @Param("semester") String semester);

    // Find all grades (override to add @NonNull)
    @NonNull
    @Override
    List<Grade> findAll();
}
