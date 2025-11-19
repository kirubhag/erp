package krs.erp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.entity.ImportSession;
import krs.erp.entity.ImportStatus;

/**
 * Repository for ImportSession entities
 */
@Repository
public interface ImportSessionRepository extends JpaRepository<ImportSession, String> {
    
    /**
     * Find import sessions by user ID
     */
    List<ImportSession> findByUserIdOrderByUploadedAtDesc(Long userId);
    
    /**
     * Find import sessions by user ID and entity type
     */
    Page<ImportSession> findByUserIdAndEntityTypeOrderByUploadedAtDesc(Long userId, String entityType, Pageable pageable);
    
    /**
     * Find import sessions by organization ID
     */
    List<ImportSession> findByOrganizationIdOrderByUploadedAtDesc(Long organizationId);
    
    /**
     * Find import sessions by status
     */
    List<ImportSession> findByStatusOrderByUploadedAtDesc(ImportStatus status);
    
    /**
     * Find in-progress or pending sessions
     */
    @Query("SELECT i FROM ImportSession i WHERE i.status IN (krs.erp.entity.ImportStatus.IN_PROGRESS, krs.erp.entity.ImportStatus.PENDING)")
    List<ImportSession> findActiveImports();
    
    /**
     * Find sessions for a user within date range
     */
    @Query("SELECT i FROM ImportSession i WHERE i.userId = :userId AND i.uploadedAt BETWEEN :startDate AND :endDate")
    List<ImportSession> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count import sessions by entity type
     */
    Long countByUserIdAndEntityType(Long userId, String entityType);
}
