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

import krs.erp.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByStudentId(String studentId);
    
    Optional<Student> findByEmail(String email);
    
    List<Student> findByEnrollmentStatus(Student.EnrollmentStatus enrollmentStatus);
    
    List<Student> findByGradeLevel(Student.GradeLevel gradeLevel);
    
    @Query("SELECT s FROM Student s WHERE s.enrollmentStatus = 'ACTIVE'")
    List<Student> findActiveStudents();
    
    @Query("SELECT s FROM Student s WHERE s.enrollmentDate BETWEEN :startDate AND :endDate")
    List<Student> findByEnrollmentDateBetween(@Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s FROM Student s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Student> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT s FROM Student s WHERE s.gradeLevel = :gradeLevel AND s.enrollmentStatus = 'ACTIVE'")
    List<Student> findActiveStudentsByGradeLevel(@Param("gradeLevel") Student.GradeLevel gradeLevel);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.enrollmentStatus = 'ACTIVE'")
    Long countActiveStudents();
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.gradeLevel = :gradeLevel AND s.enrollmentStatus = 'ACTIVE'")
    Long countActiveStudentsByGradeLevel(@Param("gradeLevel") Student.GradeLevel gradeLevel);
    
    Page<Student> findByEnrollmentStatus(Student.EnrollmentStatus enrollmentStatus, Pageable pageable);
    
    @Query("SELECT s FROM Student s WHERE s.address.city = :city AND s.enrollmentStatus = 'ACTIVE'")
    List<Student> findActiveStudentsByCity(@Param("city") String city);
    
    @Query("SELECT s FROM Student s JOIN s.parentRelations pr WHERE pr.parent.id = :parentId")
    List<Student> findStudentsByParentId(@Param("parentId") Long parentId);
    
    // Additional methods for pagination and combined filtering
    Page<Student> findByGradeLevel(Student.GradeLevel gradeLevel, Pageable pageable);
    
    @Query("SELECT s FROM Student s WHERE s.enrollmentStatus = :status AND s.gradeLevel = :gradeLevel")
    Page<Student> findByEnrollmentStatusAndGradeLevel(
        @Param("status") Student.EnrollmentStatus status, 
        @Param("gradeLevel") Student.GradeLevel gradeLevel, 
        Pageable pageable
    );
    
    @Query("SELECT s FROM Student s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Student> findByNameContaining(@Param("name") String name, Pageable pageable);
    
    // Methods to support soft delete functionality
    List<Student> findByIsActive(Integer isActive);
    
    @Query("SELECT s FROM Student s WHERE s.isActive = 1")
    List<Student> findAllActive();
    
    Page<Student> findByIsActive(Integer isActive, Pageable pageable);
    
    Long countByIsActive(Integer isActive);
    
    // Methods for student promotion filtering
    Page<Student> findByGradeLevelAndEnrollmentStatus(
        Student.GradeLevel gradeLevel,
        Student.EnrollmentStatus enrollmentStatus,
        Pageable pageable
    );
    
    Page<Student> findByGradeLevelAndSectionAndEnrollmentStatus(
        Student.GradeLevel gradeLevel,
        String section,
        Student.EnrollmentStatus enrollmentStatus,
        Pageable pageable
    );
    
    Long countByGradeLevelAndEnrollmentStatus(
        Student.GradeLevel gradeLevel,
        Student.EnrollmentStatus enrollmentStatus
    );
    
    Long countByEnrollmentStatus(Student.EnrollmentStatus enrollmentStatus);
}