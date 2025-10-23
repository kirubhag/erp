package krs.erp.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.enums.EntityType;
import krs.erp.model.RecycleBin;
import krs.erp.service.RecycleBinService;

/**
 * REST Controller for Recycle Bin operations
 * Provides endpoints for viewing, restoring, and permanently deleting soft-deleted entities
 */
@RestController
@RequestMapping("/api/recycle-bin")
@CrossOrigin(origins = "*")
public class RecycleBinController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecycleBinController.class);
    
    @Autowired
    private RecycleBinService recycleBinService;
    
    /**
     * Get all recycle bin records with optional pagination
     * 
     * @param page Page number (optional, default: 0)
     * @param size Page size (optional, default: 20)
     * @return List or Page of RecycleBin records
     */
    @GetMapping
    public ResponseEntity<?> getAllRecycleBinRecords(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        try {
            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page, size);
                Page<RecycleBin> recycleBinPage = recycleBinService.getAllRecycleBinRecords(pageable);
                return ResponseEntity.ok(recycleBinPage);
            } else {
                List<RecycleBin> recycleBinList = recycleBinService.getAllRecycleBinRecords();
                return ResponseEntity.ok(recycleBinList);
            }
        } catch (Exception e) {
            logger.error("Error fetching recycle bin records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch recycle bin records: " + e.getMessage()));
        }
    }
    
    /**
     * Get recycle bin records by entity type
     * 
     * @param entityType Entity type to filter by
     * @param page Page number (optional)
     * @param size Page size (optional)
     * @return List or Page of RecycleBin records for the specified entity type
     */
    @GetMapping("/type/{entityType}")
    public ResponseEntity<?> getRecycleBinRecordsByType(
            @PathVariable String entityType,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid entity type: " + entityType));
            }
            
            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page, size);
                Page<RecycleBin> recycleBinPage = recycleBinService.getRecycleBinRecordsByType(type, pageable);
                return ResponseEntity.ok(recycleBinPage);
            } else {
                List<RecycleBin> recycleBinList = recycleBinService.getRecycleBinRecordsByType(type);
                return ResponseEntity.ok(recycleBinList);
            }
        } catch (Exception e) {
            logger.error("Error fetching recycle bin records by type", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch recycle bin records: " + e.getMessage()));
        }
    }
    
    /**
     * Get a single recycle bin record by ID
     * 
     * @param id RecycleBin ID
     * @return RecycleBin record
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecycleBinRecord(@PathVariable Long id) {
        try {
            return recycleBinService.getRecycleBinRecord(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching recycle bin record with ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch recycle bin record: " + e.getMessage()));
        }
    }
    
    /**
     * Search recycle bin records by entity name
     * 
     * @param searchTerm Search term
     * @param page Page number (optional)
     * @param size Page size (optional)
     * @return List or Page of matching RecycleBin records
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchRecycleBinRecords(
            @RequestParam String searchTerm,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        try {
            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page, size);
                Page<RecycleBin> recycleBinPage = recycleBinService.searchRecycleBinRecords(searchTerm, pageable);
                return ResponseEntity.ok(recycleBinPage);
            } else {
                List<RecycleBin> recycleBinList = recycleBinService.searchRecycleBinRecords(searchTerm);
                return ResponseEntity.ok(recycleBinList);
            }
        } catch (Exception e) {
            logger.error("Error searching recycle bin records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search recycle bin records: " + e.getMessage()));
        }
    }
    
    /**
     * Get recycle bin statistics
     * 
     * @return Statistics including total count, count by entity type, etc.
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getRecycleBinStatistics() {
        try {
            Map<String, Object> stats = recycleBinService.getRecycleBinStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching recycle bin statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Get count of records in recycle bin
     * 
     * @return Total count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getRecycleBinCount() {
        try {
            long count = recycleBinService.getRecycleBinCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            logger.error("Error fetching recycle bin count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch count: " + e.getMessage()));
        }
    }
    
    /**
     * Get count of records by entity type
     * 
     * @param entityType Entity type
     * @return Count for the specified entity type
     */
    @GetMapping("/count/type/{entityType}")
    public ResponseEntity<?> getRecycleBinCountByType(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid entity type: " + entityType));
            }
            
            long count = recycleBinService.getRecycleBinCountByType(type);
            return ResponseEntity.ok(Map.of("count", count, "entityType", entityType));
        } catch (Exception e) {
            logger.error("Error fetching recycle bin count by type", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch count: " + e.getMessage()));
        }
    }
    
    /**
     * Restore a single entity from recycle bin by recycle bin ID
     * 
     * @param id RecycleBin ID
     * @param requestBody Request body containing restoredBy username
     * @return Success or error response
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreEntity(@PathVariable Long id, 
                                          @RequestBody Map<String, String> requestBody) {
        try {
            String restoredBy = requestBody.getOrDefault("restoredBy", "system");
            boolean restored = recycleBinService.restoreEntity(id, restoredBy);
            
            if (restored) {
                return ResponseEntity.ok(Map.of("message", "Entity restored successfully", 
                                              "recycleBinId", id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Failed to restore entity. Record may not exist or already restored."));
            }
        } catch (Exception e) {
            logger.error("Error restoring entity from recycle bin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to restore entity: " + e.getMessage()));
        }
    }
    
    /**
     * Restore entity by entity ID and type
     * 
     * @param entityId Entity ID
     * @param entityType Entity type
     * @param requestBody Request body containing restoredBy username
     * @return Success or error response
     */
    @PostMapping("/restore/entity/{entityType}/{entityId}")
    public ResponseEntity<?> restoreEntityByIdAndType(
            @PathVariable Long entityId,
            @PathVariable String entityType,
            @RequestBody Map<String, String> requestBody) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid entity type: " + entityType));
            }
            
            String restoredBy = requestBody.getOrDefault("restoredBy", "system");
            boolean restored = recycleBinService.restoreEntityByIdAndType(entityId, type, restoredBy);
            
            if (restored) {
                return ResponseEntity.ok(Map.of("message", "Entity restored successfully", 
                                              "entityId", entityId,
                                              "entityType", entityType));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Failed to restore entity. Record may not exist or already restored."));
            }
        } catch (Exception e) {
            logger.error("Error restoring entity by ID and type", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to restore entity: " + e.getMessage()));
        }
    }
    
    /**
     * Restore all entities of a specific type
     * 
     * @param entityType Entity type
     * @param requestBody Request body containing restoredBy username
     * @return Count of restored entities
     */
    @PostMapping("/restore/type/{entityType}")
    public ResponseEntity<?> restoreAllEntitiesByType(
            @PathVariable String entityType,
            @RequestBody Map<String, String> requestBody) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid entity type: " + entityType));
            }
            
            String restoredBy = requestBody.getOrDefault("restoredBy", "system");
            int restoredCount = recycleBinService.restoreAllEntitiesByType(type, restoredBy);
            
            return ResponseEntity.ok(Map.of("message", "Entities restored successfully", 
                                          "restoredCount", restoredCount,
                                          "entityType", entityType));
        } catch (Exception e) {
            logger.error("Error restoring all entities by type", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to restore entities: " + e.getMessage()));
        }
    }
    
    /**
     * Restore all entities in recycle bin
     * 
     * @param requestBody Request body containing restoredBy username
     * @return Count of restored entities
     */
    @PostMapping("/restore/all")
    public ResponseEntity<?> restoreAllEntities(@RequestBody Map<String, String> requestBody) {
        try {
            String restoredBy = requestBody.getOrDefault("restoredBy", "system");
            int restoredCount = recycleBinService.restoreAllEntities(restoredBy);
            
            return ResponseEntity.ok(Map.of("message", "All entities restored successfully", 
                                          "restoredCount", restoredCount));
        } catch (Exception e) {
            logger.error("Error restoring all entities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to restore all entities: " + e.getMessage()));
        }
    }
    
    /**
     * Permanently delete a single recycle bin record
     * 
     * @param id RecycleBin ID
     * @return Success or error response
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentlyDeleteRecord(@PathVariable Long id) {
        try {
            boolean deleted = recycleBinService.permanentlyDelete(id);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Record permanently deleted successfully", 
                                              "recycleBinId", id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Record not found"));
            }
        } catch (Exception e) {
            logger.error("Error permanently deleting recycle bin record", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete record: " + e.getMessage()));
        }
    }
    
    /**
     * Permanently delete old records (older than specified days)
     * 
     * @param days Number of days to use as cutoff (default: 30)
     * @return Count of permanently deleted records
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanupOldRecords(@RequestParam(value = "days", defaultValue = "30") int days) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
            int deletedCount = recycleBinService.permanentlyDeleteOldRecords(cutoffDate);
            
            return ResponseEntity.ok(Map.of("message", "Old records cleaned up successfully", 
                                          "deletedCount", deletedCount,
                                          "cutoffDays", days));
        } catch (Exception e) {
            logger.error("Error cleaning up old recycle bin records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cleanup records: " + e.getMessage()));
        }
    }
    
    /**
     * Empty entire recycle bin (permanently delete all records)
     * 
     * @return Count of permanently deleted records
     */
    @DeleteMapping("/empty")
    public ResponseEntity<?> emptyRecycleBin() {
        try {
            int deletedCount = recycleBinService.emptyRecycleBin();
            
            return ResponseEntity.ok(Map.of("message", "Recycle bin emptied successfully", 
                                          "deletedCount", deletedCount));
        } catch (Exception e) {
            logger.error("Error emptying recycle bin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to empty recycle bin: " + e.getMessage()));
        }
    }
    
    /**
     * Check if an entity exists in recycle bin
     * 
     * @param entityId Entity ID
     * @param entityType Entity type
     * @return Existence status
     */
    @GetMapping("/exists/{entityType}/{entityId}")
    public ResponseEntity<?> existsInRecycleBin(
            @PathVariable Long entityId,
            @PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid entity type: " + entityType));
            }
            
            boolean exists = recycleBinService.existsInRecycleBin(entityId, type);
            return ResponseEntity.ok(Map.of("exists", exists, 
                                          "entityId", entityId,
                                          "entityType", entityType));
        } catch (Exception e) {
            logger.error("Error checking entity existence in recycle bin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check existence: " + e.getMessage()));
        }
    }
    
    /**
     * Get list of all available entity types
     * 
     * @return List of entity types
     */
    @GetMapping("/entity-types")
    public ResponseEntity<?> getEntityTypes() {
        try {
            List<Map<String, String>> entityTypes = java.util.Arrays.stream(EntityType.values())
                    .map(type -> {
                        Map<String, String> typeInfo = new HashMap<>();
                        typeInfo.put("value", type.name());
                        typeInfo.put("displayName", type.getDisplayName());
                        return typeInfo;
                    })
                    .toList();
            
            return ResponseEntity.ok(entityTypes);
        } catch (Exception e) {
            logger.error("Error fetching entity types", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch entity types: " + e.getMessage()));
        }
    }
}
