package krs.erp.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.dto.StudentCustomViewDTO;
import krs.erp.dto.StudentFieldDTO;
import krs.erp.model.StudentCustomView;
import krs.erp.repository.StudentCustomViewRepository;

/**
 * Service class for managing Student Custom Views
 */
@Service
public class StudentCustomViewService {
    
    @Autowired
    private StudentCustomViewRepository customViewRepository;
    
    /**
     * Get all available fields that can be used in custom views
     */
    public List<StudentFieldDTO> getAvailableFields() {
        return StudentFieldDTO.getAllAvailableFields();
    }
    
    /**
     * Get default visible fields
     */
    public List<String> getDefaultVisibleFields() {
        return StudentFieldDTO.getDefaultVisibleFields();
    }
    
    /**
     * Create a new custom view
     */
    @Transactional
    public StudentCustomViewDTO createCustomView(StudentCustomViewDTO viewDTO, String currentUser) {
        // Validate view name uniqueness
        if (customViewRepository.existsByViewNameIgnoreCase(viewDTO.getViewName())) {
            throw new IllegalArgumentException("A custom view with this name already exists");
        }
        
        // If setting as default, unset all other default views
        if (Boolean.TRUE.equals(viewDTO.getIsDefault())) {
            customViewRepository.unsetAllDefaultViews();
        }
        
        StudentCustomView customView = convertToEntity(viewDTO);
        customView.setCreatedByUser(currentUser);
        customView.setCreatedBy(currentUser);
        
        StudentCustomView savedView = customViewRepository.save(customView);
        return convertToDTO(savedView);
    }
    
    /**
     * Update an existing custom view
     */
    @Transactional
    public StudentCustomViewDTO updateCustomView(Long viewId, StudentCustomViewDTO viewDTO, String currentUser) {
        StudentCustomView existingView = customViewRepository.findById(viewId)
            .orElseThrow(() -> new IllegalArgumentException("Custom view not found with id: " + viewId));
        
        // Validate view name uniqueness (excluding current view)
        if (customViewRepository.existsByViewNameIgnoreCaseAndIdNot(viewDTO.getViewName(), viewId)) {
            throw new IllegalArgumentException("A custom view with this name already exists");
        }
        
        // If setting as default, unset all other default views
        if (Boolean.TRUE.equals(viewDTO.getIsDefault()) && !Boolean.TRUE.equals(existingView.getIsDefault())) {
            customViewRepository.unsetAllDefaultViews();
        }
        
        // Update fields
        existingView.setViewName(viewDTO.getViewName());
        existingView.setDescription(viewDTO.getDescription());
        existingView.setSelectedFields(viewDTO.getSelectedFields());
        existingView.setIsDefault(viewDTO.getIsDefault());
        existingView.setIsPublic(viewDTO.getIsPublic());
        existingView.setUpdatedBy(currentUser);
        
        StudentCustomView updatedView = customViewRepository.save(existingView);
        return convertToDTO(updatedView);
    }
    
    /**
     * Delete a custom view
     */
    @Transactional
    public void deleteCustomView(Long viewId, String currentUser) {
        StudentCustomView existingView = customViewRepository.findById(viewId)
            .orElseThrow(() -> new IllegalArgumentException("Custom view not found with id: " + viewId));
        
        // Check if user has permission to delete (owner or admin)
        if (!existingView.getCreatedByUser().equals(currentUser)) {
            throw new IllegalArgumentException("You don't have permission to delete this custom view");
        }
        
        // Soft delete by setting isActive to false
        existingView.setIsActive(false);
        existingView.setUpdatedBy(currentUser);
        customViewRepository.save(existingView);
    }
    
    /**
     * Get custom view by ID
     */
    public Optional<StudentCustomViewDTO> getCustomView(Long viewId) {
        return customViewRepository.findById(viewId)
            .filter(view -> Boolean.TRUE.equals(view.getIsActive()))
            .map(this::convertToDTO);
    }
    
    /**
     * Get all custom views accessible to a user
     */
    public List<StudentCustomViewDTO> getAccessibleViews(String currentUser) {
        List<StudentCustomView> views = customViewRepository.findAccessibleViews(currentUser);
        return views.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get custom views with pagination
     */
    public Page<StudentCustomViewDTO> getAccessibleViews(String currentUser, Pageable pageable) {
        Page<StudentCustomView> viewsPage = customViewRepository.findAccessibleViews(currentUser, pageable);
        List<StudentCustomViewDTO> dtoList = viewsPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, viewsPage.getTotalElements());
    }
    
    /**
     * Search custom views by criteria
     */
    public Page<StudentCustomViewDTO> searchCustomViews(String viewName, Boolean isPublic, String username, Pageable pageable) {
        Page<StudentCustomView> viewsPage = customViewRepository.findByCriteria(viewName, isPublic, username, pageable);
        List<StudentCustomViewDTO> dtoList = viewsPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, viewsPage.getTotalElements());
    }
    
    /**
     * Get default custom view
     */
    public Optional<StudentCustomViewDTO> getDefaultView() {
        return customViewRepository.findDefaultView()
            .map(this::convertToDTO);
    }
    
    /**
     * Set a custom view as default
     */
    @Transactional
    public void setAsDefault(Long viewId, String currentUser) {
        StudentCustomView view = customViewRepository.findById(viewId)
            .orElseThrow(() -> new IllegalArgumentException("Custom view not found with id: " + viewId));
        
        // Unset all default views first
        customViewRepository.unsetAllDefaultViews();
        
        // Set the specified view as default
        view.setIsDefault(true);
        view.setUpdatedBy(currentUser);
        customViewRepository.save(view);
    }
    
    /**
     * Get custom views created by a specific user
     */
    public List<StudentCustomViewDTO> getViewsByUser(String username) {
        List<StudentCustomView> views = customViewRepository.findByCreatedByUser(username);
        return views.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Duplicate an existing custom view
     */
    @Transactional
    public StudentCustomViewDTO duplicateCustomView(Long viewId, String newViewName, String currentUser) {
        StudentCustomView originalView = customViewRepository.findById(viewId)
            .orElseThrow(() -> new IllegalArgumentException("Custom view not found with id: " + viewId));
        
        // Validate new view name uniqueness
        if (customViewRepository.existsByViewNameIgnoreCase(newViewName)) {
            throw new IllegalArgumentException("A custom view with this name already exists");
        }
        
        // Create new view based on original
        StudentCustomView newView = new StudentCustomView();
        newView.setViewName(newViewName);
        newView.setDescription("Copy of " + originalView.getDescription());
        newView.setSelectedFields(originalView.getSelectedFields());
        newView.setIsDefault(false); // Never set duplicate as default
        newView.setIsPublic(false); // Always private for duplicates
        newView.setCreatedByUser(currentUser);
        newView.setCreatedBy(currentUser);
        
        StudentCustomView savedView = customViewRepository.save(newView);
        return convertToDTO(savedView);
    }
    
    /**
     * Validate custom view data
     */
    public void validateCustomView(StudentCustomViewDTO viewDTO) {
        if (viewDTO.getSelectedFields() == null || viewDTO.getSelectedFields().isEmpty()) {
            throw new IllegalArgumentException("At least one field must be selected");
        }
        
        // Validate that selected fields are valid
        List<String> availableFields = getAvailableFields().stream()
            .map(StudentFieldDTO::getFieldName)
            .collect(Collectors.toList());
        
        for (String field : viewDTO.getSelectedFields()) {
            if (!availableFields.contains(field)) {
                throw new IllegalArgumentException("Invalid field: " + field);
            }
        }
    }
    
    /**
     * Get statistics about custom views
     */
    public Object getCustomViewStatistics(String currentUser) {
        long totalViewsCount = customViewRepository.countActiveViews();
        long userViewsCount = customViewRepository.countByCreatedByUser(currentUser);
        long publicViewsCount = customViewRepository.findPublicViews().size();
        
        return new Object() {
            public final long totalViews = totalViewsCount;
            public final long userViews = userViewsCount;
            public final long publicViews = publicViewsCount;
            public final boolean hasDefaultView = customViewRepository.findDefaultView().isPresent();
        };
    }
    
    // Helper methods for conversion
    private StudentCustomViewDTO convertToDTO(StudentCustomView entity) {
        StudentCustomViewDTO dto = new StudentCustomViewDTO();
        dto.setId(entity.getId());
        dto.setViewName(entity.getViewName());
        dto.setDescription(entity.getDescription());
        dto.setSelectedFields(entity.getSelectedFields());
        dto.setIsDefault(entity.getIsDefault());
        dto.setCreatedByUser(entity.getCreatedByUser());
        dto.setIsPublic(entity.getIsPublic());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }
    
    private StudentCustomView convertToEntity(StudentCustomViewDTO dto) {
        StudentCustomView entity = new StudentCustomView();
        entity.setViewName(dto.getViewName());
        entity.setDescription(dto.getDescription());
        entity.setSelectedFields(dto.getSelectedFields());
        entity.setIsDefault(dto.getIsDefault());
        entity.setIsPublic(dto.getIsPublic());
        return entity;
    }
}