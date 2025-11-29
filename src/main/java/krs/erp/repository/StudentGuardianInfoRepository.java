package krs.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.StudentGuardianInfo;

/**
 * Repository for StudentGuardianInfo entity.
 * Handles database operations for student guardian/parent information.
 */
@Repository
public interface StudentGuardianInfoRepository extends JpaRepository<StudentGuardianInfo, Long> {

    /**
     * Find guardian info by student ID
     */
    @Query("SELECT sgi FROM StudentGuardianInfo sgi WHERE sgi.student.id = :studentId AND sgi.isActive = 1")
    StudentGuardianInfo findByStudentId(@Param("studentId") Long studentId);

    /**
     * Check if guardian info exists for a student
     */
    @Query("SELECT CASE WHEN COUNT(sgi) > 0 THEN true ELSE false END FROM StudentGuardianInfo sgi WHERE sgi.student.id = :studentId AND sgi.isActive = 1")
    boolean existsByStudentId(@Param("studentId") Long studentId);
}
