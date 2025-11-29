package krs.erp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.entity.StudentPromotionAuditLog;
import krs.erp.entity.StudentPromotionAuditLog.ActionType;

@Repository
public interface StudentPromotionAuditLogRepository extends JpaRepository<StudentPromotionAuditLog, Long> {

    List<StudentPromotionAuditLog> findByBatchId(Long batchId);

    List<StudentPromotionAuditLog> findByRecordId(Long recordId);

    List<StudentPromotionAuditLog> findByTargetStudentId(Long studentId);

    List<StudentPromotionAuditLog> findByPerformedBy(Long userId);

    List<StudentPromotionAuditLog> findByActionType(ActionType actionType);

    @Query("SELECT l FROM StudentPromotionAuditLog l WHERE l.batchId = :batchId ORDER BY l.createdAt DESC")
    List<StudentPromotionAuditLog> findBatchAuditTrail(@Param("batchId") Long batchId);

    @Query("SELECT l FROM StudentPromotionAuditLog l WHERE l.targetStudentId = :studentId ORDER BY l.createdAt DESC")
    List<StudentPromotionAuditLog> findStudentAuditTrail(@Param("studentId") Long studentId);

    @Query("SELECT l FROM StudentPromotionAuditLog l WHERE l.createdAt BETWEEN :startDate AND :endDate ORDER BY l.createdAt DESC")
    List<StudentPromotionAuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Page<StudentPromotionAuditLog> findByBatchIdOrderByCreatedAtDesc(Long batchId, Pageable pageable);
}
