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
    
    @Query("SELECT s FROM Student s WHERE s.city = :city AND s.enrollmentStatus = 'ACTIVE'")
    List<Student> findActiveStudentsByCity(@Param("city") String city);
    
    @Query("SELECT s FROM Student s JOIN s.parentRelations pr WHERE pr.parent.id = :parentId")
    List<Student> findStudentsByParentId(@Param("parentId") Long parentId);
}