package krs.erp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;
import krs.erp.repository.CustomViewRepository;

@Service
@Transactional
public class CustomViewService {
    
    @Autowired
    private CustomViewRepository customViewRepository;
    
    /**
     * Get all custom views for a specific entity type
     */
    public List<CustomView> getViewsByEntityType(EntityType entityType) {
        return customViewRepository.findByEntityTypeOrderByViewNameAsc(entityType);
    }
    
    /**
     * Get public custom views for a specific entity type
     */
    public List<CustomView> getPublicViews(EntityType entityType) {
        return customViewRepository.findByEntityTypeAndIsPublicTrue(entityType);
    }
    
    /**
     * Get custom views accessible to a user (public + user's own)
     */
    public List<CustomView> getAccessibleViews(EntityType entityType, Long userId) {
        return customViewRepository.findAccessibleViews(entityType, userId);
    }
    
    /**
     * Get user's custom views for an entity type
     */
    public List<CustomView> getUserViews(EntityType entityType, Long userId) {
        return customViewRepository.findByEntityTypeAndCreatedBy(entityType, userId);
    }
    
    /**
     * Get default custom view for an entity type
     */
    public Optional<CustomView> getDefaultView(EntityType entityType) {
        return customViewRepository.findByEntityTypeAndIsDefaultTrue(entityType);
    }
    
    /**
     * Get custom view by ID
     */
    public Optional<CustomView> getViewById(Long viewId) {
        return customViewRepository.findById(viewId);
    }
    
    /**
     * Get custom view by name and entity type
     */
    public Optional<CustomView> getViewByName(String viewName, EntityType entityType) {
        return customViewRepository.findByViewNameAndEntityType(viewName, entityType);
    }
    
    /**
     * Create a new custom view
     */
    public CustomView createView(CustomView customView, Long userId) {
        // Set audit fields
        customView.setCreatedBy(userId.toString());
        customView.setCreatedTime(LocalDateTime.now());
        customView.setModifiedBy(userId.toString());
        customView.setModifiedTime(LocalDateTime.now());
        
        // Ensure only one default view per entity type
        if (customView.getIsDefault()) {
            clearDefaultView(customView.getEntityType());
        }
        
        return customViewRepository.save(customView);
    }
    
    /**
     * Update an existing custom view
     */
    public CustomView updateView(Long viewId, CustomView updatedView, Long userId) {
        Optional<CustomView> existingViewOpt = customViewRepository.findById(viewId);
        if (existingViewOpt.isEmpty()) {
            throw new RuntimeException("Custom view not found with id: " + viewId);
        }
        
        CustomView existingView = existingViewOpt.get();
        
        // Update fields
        existingView.setViewName(updatedView.getViewName());
        existingView.setDescription(updatedView.getDescription());
        existingView.setSelectedFields(updatedView.getSelectedFields());
        existingView.setIsPublic(updatedView.getIsPublic());
        existingView.setModifiedBy(userId.toString());
        existingView.setModifiedTime(LocalDateTime.now());
        
        // Ensure only one default view per entity type
        if (updatedView.getIsDefault()) {
            clearDefaultViewExcept(existingView.getEntityType(), viewId);
            existingView.setIsDefault(true);
        } else {
            existingView.setIsDefault(false);
        }
        
        return customViewRepository.save(existingView);
    }
    
    /**
     * Delete a custom view
     */
    public void deleteView(Long viewId, Long userId) {
        Optional<CustomView> viewOpt = customViewRepository.findById(viewId);
        if (viewOpt.isEmpty()) {
            throw new RuntimeException("Custom view not found with id: " + viewId);
        }
        
        CustomView view = viewOpt.get();
        
        // Check if user owns the view or is admin
        if (!view.getCreatedBy().equals(userId.toString())) {
            throw new RuntimeException("User not authorized to delete this view");
        }
        
        customViewRepository.deleteById(viewId);
    }
    
    /**
     * Check if view name already exists for the entity type
     */
    public boolean viewNameExists(String viewName, EntityType entityType) {
        return customViewRepository.existsByViewNameAndEntityType(viewName, entityType);
    }
    
    /**
     * Check if view name exists for entity type, excluding a specific view ID
     */
    public boolean viewNameExists(String viewName, EntityType entityType, Long excludeViewId) {
        Optional<CustomView> existingView = customViewRepository.findByViewNameAndEntityType(viewName, entityType);
        return existingView.isPresent() && !existingView.get().getId().equals(excludeViewId);
    }
    
    /**
     * Clear any existing default view for the entity type
     */
    private void clearDefaultView(EntityType entityType) {
        Optional<CustomView> existingDefault = customViewRepository.findByEntityTypeAndIsDefaultTrue(entityType);
        if (existingDefault.isPresent()) {
            CustomView defaultView = existingDefault.get();
            defaultView.setIsDefault(false);
            customViewRepository.save(defaultView);
        }
    }
    
    private void clearDefaultViewExcept(EntityType entityType, Long excludeViewId) {
        Optional<CustomView> existingDefault = customViewRepository.findByEntityTypeAndIsDefaultTrue(entityType);
        if (existingDefault.isPresent()) {
            CustomView defaultView = existingDefault.get();
            // Only clear if it's not the view we're currently updating
            if (!defaultView.getId().equals(excludeViewId)) {
                defaultView.setIsDefault(false);
                customViewRepository.save(defaultView);
            }
        }
    }
    
    /**
     * Get recent views for an entity type
     */
    public List<CustomView> getRecentViews(EntityType entityType, int limit) {
        return customViewRepository.findRecentViews(entityType, PageRequest.of(0, limit));
    }
    
    /**
     * Get view count for an entity type
     */
    public long getViewCount(EntityType entityType) {
        return customViewRepository.countByEntityType(entityType);
    }
    
    /**
     * Clone a custom view
     */
    public CustomView cloneView(Long viewId, String newViewName, Long userId) {
        Optional<CustomView> originalViewOpt = customViewRepository.findById(viewId);
        if (originalViewOpt.isEmpty()) {
            throw new RuntimeException("Custom view not found with id: " + viewId);
        }
        
        CustomView originalView = originalViewOpt.get();
        
        // Check if new name already exists
        if (viewNameExists(newViewName, originalView.getEntityType())) {
            throw new RuntimeException("View name already exists: " + newViewName);
        }
        
        // Create new view
        CustomView clonedView = new CustomView();
        clonedView.setViewName(newViewName);
        clonedView.setDescription("Copy of " + originalView.getDescription());
        clonedView.setEntityType(originalView.getEntityType());
        clonedView.setSelectedFields(originalView.getSelectedFields());
        clonedView.setIsDefault(false); // Cloned views are never default
        clonedView.setIsPublic(false);  // Cloned views are private by default
        
        return createView(clonedView, userId);
    }
}