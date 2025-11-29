package krs.erp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.entity.StudentPromotionBatch;
import krs.erp.entity.StudentPromotionBatch.PromotionBatchStatus;

@Repository
public interface StudentPromotionBatchRepository extends JpaRepository<StudentPromotionBatch, Long> {

    List<StudentPromotionBatch> findByStatus(PromotionBatchStatus status);

    Page<StudentPromotionBatch> findByStatus(PromotionBatchStatus status, Pageable pageable);

    List<StudentPromotionBatch> findByAcademicYearFrom(String academicYearFrom);

    List<StudentPromotionBatch> findByAcademicYearTo(String academicYearTo);

    @Query("SELECT b FROM StudentPromotionBatch b WHERE b.promotionDate BETWEEN :startDate AND :endDate ORDER BY b.promotionDate DESC")
    List<StudentPromotionBatch> findByPromotionDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM StudentPromotionBatch b WHERE b.initiatedBy = :userId ORDER BY b.createdAt DESC")
    List<StudentPromotionBatch> findByInitiatedBy(@Param("userId") Long userId);

    @Query("SELECT b FROM StudentPromotionBatch b WHERE b.academicYearFrom = :yearFrom AND b.academicYearTo = :yearTo ORDER BY b.createdAt DESC")
    List<StudentPromotionBatch> findByAcademicYearTransition(@Param("yearFrom") String yearFrom, @Param("yearTo") String yearTo);

    @Query("SELECT b FROM StudentPromotionBatch b WHERE b.status IN :statuses ORDER BY b.createdAt DESC")
    List<StudentPromotionBatch> findByStatusIn(@Param("statuses") List<PromotionBatchStatus> statuses);

    Page<StudentPromotionBatch> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
