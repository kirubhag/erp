package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpClass;

/**
 * Repository interface for ErpClass entity
 */
@Repository
public interface ErpClassRepository extends JpaRepository<ErpClass, Long> {

    // Find by class code
    Optional<ErpClass> findByClassCodeAndIsActive(String classCode, Integer isActive);

    // Find by grade level
    @Query("SELECT c FROM ErpClass c WHERE c.gradeLevel = :gradeLevel AND c.isActive = 1 ORDER BY c.section")
    List<ErpClass> findByGradeLevel(@Param("gradeLevel") String gradeLevel);

    // Find by academic year
    @Query("SELECT c FROM ErpClass c WHERE c.academicYear = :academicYear AND c.isActive = 1 ORDER BY c.gradeLevel, c.section")
    List<ErpClass> findByAcademicYear(@Param("academicYear") String academicYear);

    // Find by grade level and academic year
    @Query("SELECT c FROM ErpClass c WHERE c.gradeLevel = :gradeLevel AND c.academicYear = :academicYear AND c.isActive = 1 ORDER BY c.section")
    List<ErpClass> findByGradeLevelAndAcademicYear(
            @Param("gradeLevel") String gradeLevel,
            @Param("academicYear") String academicYear);

    // Find by grade level, section, and academic year (unique combination)
    @Query("SELECT c FROM ErpClass c WHERE c.gradeLevel = :gradeLevel AND c.section = :section AND c.academicYear = :academicYear AND c.isActive = 1")
    Optional<ErpClass> findByGradeLevelAndSectionAndAcademicYear(
            @Param("gradeLevel") String gradeLevel,
            @Param("section") String section,
            @Param("academicYear") String academicYear);

    // Check if class exists
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ErpClass c " +
            "WHERE c.gradeLevel = :gradeLevel AND c.section = :section AND c.academicYear = :academicYear AND c.isActive = 1")
    boolean existsByGradeLevelAndSectionAndAcademicYear(
            @Param("gradeLevel") String gradeLevel,
            @Param("section") String section,
            @Param("academicYear") String academicYear);

    // Find all active classes with pagination
    @Query("SELECT c FROM ErpClass c WHERE c.isActive = 1 ORDER BY c.academicYear DESC, c.gradeLevel, c.section")
    Page<ErpClass> findAllActive(Pageable pageable);

    // Search classes
    @Query("SELECT c FROM ErpClass c WHERE c.isActive = 1 AND " +
            "(LOWER(c.classCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.className) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.gradeLevel) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.section) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.roomNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY c.academicYear DESC, c.gradeLevel, c.section")
    Page<ErpClass> searchClasses(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find classes by class teacher
    @Query("SELECT c FROM ErpClass c WHERE c.classTeacher.id = :teacherId AND c.isActive = 1 ORDER BY c.academicYear DESC")
    List<ErpClass> findByClassTeacherId(@Param("teacherId") Long teacherId);

    // Find classes with a specific student
    @Query("SELECT c FROM ErpClass c JOIN c.students s WHERE s.id = :studentId AND c.isActive = 1 ORDER BY c.academicYear DESC")
    List<ErpClass> findByStudentId(@Param("studentId") Long studentId);

    // Find classes with a specific teacher
    @Query("SELECT c FROM ErpClass c JOIN c.teachers t WHERE t.id = :teacherId AND c.isActive = 1 ORDER BY c.academicYear DESC")
    List<ErpClass> findByTeacherId(@Param("teacherId") Long teacherId);

    // Find classes with a specific subject
    @Query("SELECT c FROM ErpClass c JOIN c.subjects s WHERE s.id = :subjectId AND c.isActive = 1 ORDER BY c.academicYear DESC")
    List<ErpClass> findBySubjectId(@Param("subjectId") Long subjectId);

    // Count classes by academic year
    @Query("SELECT COUNT(c) FROM ErpClass c WHERE c.academicYear = :academicYear AND c.isActive = 1")
    long countByAcademicYear(@Param("academicYear") String academicYear);

    // Count classes by grade level
    @Query("SELECT COUNT(c) FROM ErpClass c WHERE c.gradeLevel = :gradeLevel AND c.isActive = 1")
    long countByGradeLevel(@Param("gradeLevel") String gradeLevel);
}
