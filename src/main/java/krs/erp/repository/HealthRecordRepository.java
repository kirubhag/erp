package krs.erp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.HealthRecord;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    
    List<HealthRecord> findByStudentId(Long studentId);
    
    List<HealthRecord> findByStudentIdAndRecordType(Long studentId, HealthRecord.RecordType recordType);
    
    List<HealthRecord> findByStudentIdAndActiveTrue(Long studentId);
    
    List<HealthRecord> findByRecordType(HealthRecord.RecordType recordType);
    
    @Query("SELECT h FROM HealthRecord h WHERE h.student.id = :studentId AND h.active = true " +
           "AND h.recordType = :recordType ORDER BY h.recordDate DESC")
    List<HealthRecord> findActiveHealthRecordsByStudentAndType(@Param("studentId") Long studentId,
                                                              @Param("recordType") HealthRecord.RecordType recordType);
    
    @Query("SELECT h FROM HealthRecord h WHERE h.expiryDate IS NOT NULL " +
           "AND h.expiryDate BETWEEN :startDate AND :endDate AND h.active = true")
    List<HealthRecord> findRecordsExpiringBetween(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT h FROM HealthRecord h WHERE h.expiryDate IS NOT NULL " +
           "AND h.expiryDate < CURRENT_DATE AND h.active = true")
    List<HealthRecord> findExpiredRecords();
    
    @Query("SELECT h FROM HealthRecord h WHERE h.requiresAttention = true AND h.active = true")
    List<HealthRecord> findRecordsRequiringAttention();
    
    @Query("SELECT h FROM HealthRecord h WHERE h.severity IN ('HIGH', 'CRITICAL') AND h.active = true")
    List<HealthRecord> findCriticalHealthRecords();
    
    @Query("SELECT h FROM HealthRecord h WHERE h.student.id = :studentId " +
           "AND h.recordType = 'ALLERGY' AND h.active = true")
    List<HealthRecord> findActiveAllergiesByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT h FROM HealthRecord h WHERE h.student.id = :studentId " +
           "AND h.recordType = 'MEDICATION' AND h.active = true")
    List<HealthRecord> findActiveMedicationsByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT h FROM HealthRecord h WHERE h.student.id = :studentId " +
           "AND h.recordType = 'IMMUNIZATION' ORDER BY h.recordDate DESC")
    List<HealthRecord> findImmunizationsByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(h) FROM HealthRecord h WHERE h.student.id = :studentId AND h.active = true")
    Long countActiveHealthRecordsByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT h FROM HealthRecord h WHERE h.recordDate BETWEEN :startDate AND :endDate")
    List<HealthRecord> findByRecordDateBetween(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}