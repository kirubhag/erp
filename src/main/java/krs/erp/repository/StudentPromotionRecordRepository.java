package krs.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.entity.StudentPromotionRecord;
import krs.erp.entity.StudentPromotionRecord.PromotionStatus;

@Repository
public interface StudentPromotionRecordRepository extends JpaRepository<StudentPromotionRecord, Long> {

    List<StudentPromotionRecord> findByBatchBatchId(Long batchId);

    List<StudentPromotionRecord> findByStudentId(Long studentId);

    List<StudentPromotionRecord> findByPromotionStatus(PromotionStatus status);

    @Query("SELECT r FROM StudentPromotionRecord r WHERE r.batch.batchId = :batchId AND r.promotionStatus = :status")
    List<StudentPromotionRecord> findByBatchIdAndStatus(@Param("batchId") Long batchId, @Param("status") PromotionStatus status);

    @Query("SELECT r FROM StudentPromotionRecord r WHERE r.studentId = :studentId ORDER BY r.createdAt DESC")
    List<StudentPromotionRecord> findPromotionHistoryByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(r) FROM StudentPromotionRecord r WHERE r.batch.batchId = :batchId AND r.promotionStatus = :status")
    Long countByBatchIdAndStatus(@Param("batchId") Long batchId, @Param("status") PromotionStatus status);

    @Query("SELECT r FROM StudentPromotionRecord r WHERE r.fromGradeLevel = :fromGrade AND r.toGradeLevel = :toGrade")
    List<StudentPromotionRecord> findByGradeTransition(@Param("fromGrade") String fromGrade, @Param("toGrade") String toGrade);
}
