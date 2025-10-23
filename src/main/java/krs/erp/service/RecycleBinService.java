package krs.erp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import krs.erp.enums.EntityType;
import krs.erp.model.RecycleBin;

/**
 * Service interface for RecycleBin operations
 */
public interface RecycleBinService {
    
    /**
     * Add an entity to recycle bin (soft delete)
     */
    RecycleBin addToRecycleBin(Long entityId, String entityName, EntityType entityType, 
                              String deletedBy, String deletionReason);
    
    /**
     * Add an entity to recycle bin with JSON data
     */
    RecycleBin addToRecycleBin(Long entityId, String entityName, EntityType entityType, 
                              String deletedBy, String deletionReason, String entityData);
    
    /**
     * Soft delete an entity and its related entities
     */
    void softDeleteEntity(Long entityId, EntityType entityType, String deletedBy, String deletionReason);
    
    /**
     * Restore a single entity from recycle bin
     */
    boolean restoreEntity(Long recycleBinId, String restoredBy);
    
    /**
     * Restore an entity by entity ID and type
     */
    boolean restoreEntityByIdAndType(Long entityId, EntityType entityType, String restoredBy);
    
    /**
     * Restore all entities of a specific type
     */
    int restoreAllEntitiesByType(EntityType entityType, String restoredBy);
    
    /**
     * Restore all entities in recycle bin
     */
    int restoreAllEntities(String restoredBy);
    
    /**
     * Get all recycle bin records
     */
    List<RecycleBin> getAllRecycleBinRecords();
    
    /**
     * Get all recycle bin records with pagination
     */
    Page<RecycleBin> getAllRecycleBinRecords(Pageable pageable);
    
    /**
     * Get recycle bin records by entity type
     */
    List<RecycleBin> getRecycleBinRecordsByType(EntityType entityType);
    
    /**
     * Get recycle bin records by entity type with pagination
     */
    Page<RecycleBin> getRecycleBinRecordsByType(EntityType entityType, Pageable pageable);
    
    /**
     * Get recycle bin records by deleted by user
     */
    List<RecycleBin> getRecycleBinRecordsByDeletedBy(String deletedBy);
    
    /**
     * Get recycle bin record by ID
     */
    Optional<RecycleBin> getRecycleBinRecord(Long recycleBinId);
    
    /**
     * Search recycle bin records by entity name
     */
    List<RecycleBin> searchRecycleBinRecords(String searchTerm);
    
    /**
     * Search recycle bin records by entity name with pagination
     */
    Page<RecycleBin> searchRecycleBinRecords(String searchTerm, Pageable pageable);
    
    /**
     * Get recycle bin statistics
     */
    Map<String, Object> getRecycleBinStatistics();
    
    /**
     * Get recycle bin statistics by entity type
     */
    Map<EntityType, Long> getRecycleBinStatsByEntityType();
    
    /**
     * Permanently delete a record from recycle bin
     */
    boolean permanentlyDelete(Long recycleBinId);
    
    /**
     * Permanently delete old records (cleanup)
     */
    int permanentlyDeleteOldRecords(LocalDateTime cutoffDate);
    
    /**
     * Empty recycle bin (permanently delete all records)
     */
    int emptyRecycleBin();
    
    /**
     * Check if an entity exists in recycle bin
     */
    boolean existsInRecycleBin(Long entityId, EntityType entityType);
    
    /**
     * Get count of records in recycle bin
     */
    long getRecycleBinCount();
    
    /**
     * Get count of records by entity type
     */
    long getRecycleBinCountByType(EntityType entityType);
}