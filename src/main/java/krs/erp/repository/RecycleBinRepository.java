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

import krs.erp.enums.EntityType;
import krs.erp.model.RecycleBin;

/**
 * Repository interface for RecycleBin entity operations
 */
@Repository
public interface RecycleBinRepository extends JpaRepository<RecycleBin, Long> {
    
    /**
     * Find all recycle bin records ordered by deletion time (most recent first)
     */
    List<RecycleBin> findAllByOrderByDeletedTimeDesc();
    
    /**
     * Find all recycle bin records with pagination, ordered by deletion time
     */
    Page<RecycleBin> findAllByOrderByDeletedTimeDesc(Pageable pageable);
    
    /**
     * Find recycle bin records by entity type
     */
    List<RecycleBin> findByEntityTypeOrderByDeletedTimeDesc(EntityType entityType);
    
    /**
     * Find recycle bin records by entity type with pagination
     */
    Page<RecycleBin> findByEntityTypeOrderByDeletedTimeDesc(EntityType entityType, Pageable pageable);
    
    /**
     * Find recycle bin records by deleted by user
     */
    List<RecycleBin> findByDeletedByOrderByDeletedTimeDesc(String deletedBy);
    
    /**
     * Find recycle bin record by entity ID and entity type
     */
    Optional<RecycleBin> findByEntityIdAndEntityType(Long entityId, EntityType entityType);
    
    /**
     * Find recycle bin records deleted within a date range
     */
    @Query("SELECT r FROM RecycleBin r WHERE r.deletedTime BETWEEN :startDate AND :endDate ORDER BY r.deletedTime DESC")
    List<RecycleBin> findByDeletedTimeBetween(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count total records in recycle bin
     */
    long count();
    
    /**
     * Count records by entity type
     */
    long countByEntityType(EntityType entityType);
    
    /**
     * Count records by deleted by user
     */
    long countByDeletedBy(String deletedBy);
    
    /**
     * Find recycle bin records older than specified date
     */
    @Query("SELECT r FROM RecycleBin r WHERE r.deletedTime < :cutoffDate ORDER BY r.deletedTime ASC")
    List<RecycleBin> findOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Delete recycle bin records older than specified date (for permanent cleanup)
     */
    void deleteByDeletedTimeBefore(LocalDateTime cutoffDate);
    
    /**
     * Check if an entity exists in recycle bin
     */
    boolean existsByEntityIdAndEntityType(Long entityId, EntityType entityType);
    
    /**
     * Search recycle bin by entity name containing search term
     */
    @Query("SELECT r FROM RecycleBin r WHERE LOWER(r.entityName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY r.deletedTime DESC")
    List<RecycleBin> searchByEntityName(@Param("searchTerm") String searchTerm);
    
    /**
     * Search recycle bin by entity name with pagination
     */
    @Query("SELECT r FROM RecycleBin r WHERE LOWER(r.entityName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY r.deletedTime DESC")
    Page<RecycleBin> searchByEntityName(@Param("searchTerm") String searchTerm, Pageable pageable);
}