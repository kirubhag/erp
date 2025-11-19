package krs.erp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.entity.ImportResult;
import krs.erp.entity.ImportResultStatus;

/**
 * Repository for ImportResult entities
 */
@Repository
public interface ImportResultRepository extends JpaRepository<ImportResult, Long> {
    
    /**
     * Find import results by session ID
     */
    List<ImportResult> findByImportSessionIdOrderByRowNumber(String sessionId);
    
    /**
     * Find import results by session ID with pagination
     */
    Page<ImportResult> findByImportSessionIdOrderByRowNumber(String sessionId, Pageable pageable);
    
    /**
     * Find import results by session ID and status
     */
    List<ImportResult> findByImportSessionIdAndStatusOrderByRowNumber(String sessionId, ImportResultStatus status);
    
    /**
     * Count results by session ID and status
     */
    Long countByImportSessionIdAndStatus(String sessionId, ImportResultStatus status);
    
    /**
     * Find failed results for a session
     */
    @Query("SELECT r FROM ImportResult r WHERE r.importSession.id = :sessionId AND r.status = 'FAILED' ORDER BY r.rowNumber")
    List<ImportResult> findFailedResults(@Param("sessionId") String sessionId);
    
    /**
     * Delete results for a session
     */
    void deleteByImportSessionId(String sessionId);
}
