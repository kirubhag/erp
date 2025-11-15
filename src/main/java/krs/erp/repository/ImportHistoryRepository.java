package krs.erp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.ImportHistory;
import krs.erp.model.ImportHistory.ImportStatus;
import krs.erp.model.ImportHistory.ImportType;

/**
 * Repository for ImportHistory entities
 * Provides methods to track and query data import/population history
 */
@Repository
public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {
    
    /**
     * Find all imports for a specific entity
     */
    List<ImportHistory> findByEntityNameOrderByCreatedAtDesc(String entityName);
    
    /**
     * Find the most recent successful import for an entity
     */
    Optional<ImportHistory> findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
            String entityName, ImportStatus importStatus);
    
    /**
     * Check if an entity has been imported (any successful imports)
     */
    @Query("SELECT COUNT(h) > 0 FROM ImportHistory h WHERE h.entityName = :entityName " +
           "AND h.importStatus = 'SUCCESS' AND h.isActive = true")
    boolean hasEntityBeenImported(@Param("entityName") String entityName);
    
    /**
     * Get all imports by type
     */
    List<ImportHistory> findByImportTypeOrderByCreatedAtDesc(ImportType importType);
    
    /**
     * Get all successful imports
     */
    List<ImportHistory> findByImportStatusOrderByCreatedAtDesc(ImportStatus importStatus);
    
    /**
     * Get imports within a date range
     */
    @Query("SELECT h FROM ImportHistory h WHERE h.createdAt BETWEEN :startDate AND :endDate " +
           "AND h.isActive = true ORDER BY h.createdAt DESC")
    List<ImportHistory> findImportsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get paginated import history
     */
    Page<ImportHistory> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Get import history for a specific entity with pagination
     */
    Page<ImportHistory> findByEntityNameAndIsActiveTrueOrderByCreatedAtDesc(
            String entityName, Pageable pageable);
    
    /**
     * Get imports by status with pagination
     */
    Page<ImportHistory> findByImportStatusAndIsActiveTrueOrderByCreatedAtDesc(
            ImportStatus importStatus, Pageable pageable);
    
    /**
     * Get total records imported for an entity
     */
    @Query("SELECT COALESCE(SUM(h.recordCount), 0) FROM ImportHistory h " +
           "WHERE h.entityName = :entityName AND h.importStatus = 'SUCCESS' AND h.isActive = true")
    Integer getTotalRecordsImportedForEntity(@Param("entityName") String entityName);
    
    /**
     * Get recent imports (last 10)
     */
    @Query("SELECT h FROM ImportHistory h WHERE h.isActive = true ORDER BY h.createdAt DESC")
    List<ImportHistory> getRecentImports(Pageable pageable);
    
    /**
     * Count successful imports by entity
     */
    @Query("SELECT COUNT(h) FROM ImportHistory h WHERE h.entityName = :entityName " +
           "AND h.importStatus = 'SUCCESS' AND h.isActive = true")
    long countSuccessfulImportsForEntity(@Param("entityName") String entityName);
    
    /**
     * Get all entities that have been imported
     */
    @Query("SELECT DISTINCT h.entityName FROM ImportHistory h " +
           "WHERE h.importStatus = 'SUCCESS' AND h.isActive = true ORDER BY h.entityName")
    List<String> getImportedEntities();
}
