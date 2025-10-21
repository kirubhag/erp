package krs.erp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;
import krs.erp.repository.CustomViewRepository;

/**
 * Service for custom view utilities and data management operations.
 * Legacy migration functionality has been removed as the system now uses
 * the generic custom view system exclusively.
 */
@Service
@Transactional
public class CustomViewMigrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomViewMigrationService.class);
    
    @Autowired
    private CustomViewRepository customViewRepository;
    
    /**
     * Get statistics about custom views in the system
     */
    public CustomViewStats getCustomViewStats() {
        logger.info("Getting custom view statistics");
        
        CustomViewStats stats = new CustomViewStats();
        
        try {
            // Count views by entity type
            for (EntityType entityType : EntityType.values()) {
                long count = customViewRepository.countByEntityType(entityType);
                stats.addEntityTypeCount(entityType, count);
            }
            
            // Count total views
            long totalViews = customViewRepository.count();
            stats.setTotalViews(totalViews);
            
            // Count default views
            long defaultViews = customViewRepository.findByIsDefaultTrue().size();
            stats.setDefaultViews(defaultViews);
            
            // Count public views manually
            long publicViews = 0;
            for (EntityType entityType : EntityType.values()) {
                publicViews += customViewRepository.findByEntityTypeAndIsPublicTrue(entityType).size();
            }
            stats.setPublicViews(publicViews);
            
            logger.info("Custom view statistics collected successfully");
            
        } catch (Exception e) {
            logger.error("Failed to collect custom view statistics", e);
            stats.setError(e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Validate the integrity of custom views in the system
     */
    public ValidationResult validateCustomViews() {
        logger.info("Validating custom view integrity");
        
        ValidationResult result = new ValidationResult();
        
        try {
            // Find views with duplicate names within the same entity type
            for (EntityType entityType : EntityType.values()) {
                var views = customViewRepository.findByEntityType(entityType);
                var viewNames = new java.util.HashMap<String, Long>();
                
                for (CustomView view : views) {
                    viewNames.merge(view.getViewName(), 1L, Long::sum);
                }
                
                for (var entry : viewNames.entrySet()) {
                    if (entry.getValue() > 1) {
                        result.addDuplicateViewName(entityType, entry.getKey());
                    }
                }
            }
            
            // Check for multiple default views per entity type
            for (EntityType entityType : EntityType.values()) {
                var allDefaultViews = customViewRepository.findByIsDefaultTrue();
                var entityDefaultViews = allDefaultViews.stream()
                    .filter(view -> view.getEntityType() == entityType)
                    .count();
                if (entityDefaultViews > 1) {
                    result.addMultipleDefaultViews(entityType, (int) entityDefaultViews);
                }
            }
            
            logger.info("Custom view validation completed");
            
        } catch (Exception e) {
            logger.error("Failed to validate custom views", e);
            result.setError(e.getMessage());
        }
        
        return result;
    }
    
    // Inner classes for results
    public static class CustomViewStats {
        private final java.util.Map<EntityType, Long> entityTypeCounts = new java.util.HashMap<>();
        private long totalViews;
        private long defaultViews;
        private long publicViews;
        private String error;
        
        public void addEntityTypeCount(EntityType entityType, long count) { 
            entityTypeCounts.put(entityType, count); 
        }
        
        public java.util.Map<EntityType, Long> getEntityTypeCounts() { return entityTypeCounts; }
        
        public long getTotalViews() { return totalViews; }
        public void setTotalViews(long totalViews) { this.totalViews = totalViews; }
        
        public long getDefaultViews() { return defaultViews; }
        public void setDefaultViews(long defaultViews) { this.defaultViews = defaultViews; }
        
        public long getPublicViews() { return publicViews; }
        public void setPublicViews(long publicViews) { this.publicViews = publicViews; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public boolean isValid() { return error == null; }
    }
    
    public static class ValidationResult {
        private final java.util.Map<EntityType, java.util.List<String>> duplicateViewNames = new java.util.HashMap<>();
        private final java.util.Map<EntityType, Integer> multipleDefaultViews = new java.util.HashMap<>();
        private String error;
        
        public void addDuplicateViewName(EntityType entityType, String viewName) {
            duplicateViewNames.computeIfAbsent(entityType, k -> new java.util.ArrayList<>()).add(viewName);
        }
        
        public void addMultipleDefaultViews(EntityType entityType, int count) {
            multipleDefaultViews.put(entityType, count);
        }
        
        public java.util.Map<EntityType, java.util.List<String>> getDuplicateViewNames() { return duplicateViewNames; }
        public java.util.Map<EntityType, Integer> getMultipleDefaultViews() { return multipleDefaultViews; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public boolean isValid() { 
            return error == null && duplicateViewNames.isEmpty() && multipleDefaultViews.isEmpty(); 
        }
        
        public boolean hasIssues() {
            return !duplicateViewNames.isEmpty() || !multipleDefaultViews.isEmpty();
        }
    }
}