package krs.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.StudentMedicalInfo;

/**
 * Repository for StudentMedicalInfo entity.
 * Handles database operations for student medical/health information.
 */
@Repository
public interface StudentMedicalInfoRepository extends JpaRepository<StudentMedicalInfo, Long> {

    /**
     * Find medical info by student ID
     */
    @Query("SELECT smi FROM StudentMedicalInfo smi WHERE smi.student.id = :studentId AND smi.isActive = 1")
    StudentMedicalInfo findByStudentId(@Param("studentId") Long studentId);

    /**
     * Check if medical info exists for a student
     */
    @Query("SELECT CASE WHEN COUNT(smi) > 0 THEN true ELSE false END FROM StudentMedicalInfo smi WHERE smi.student.id = :studentId AND smi.isActive = 1")
    boolean existsByStudentId(@Param("studentId") Long studentId);
}
