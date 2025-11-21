package krs.erp.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import krs.erp.dto.CustomViewDTO;
import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;
import krs.erp.service.CustomViewService;

@RestController
@RequestMapping("/api/custom-views")
public class CustomViewController {
    
    @Autowired
    private CustomViewService customViewService;
    
    /**
     * Get all custom views for a specific entity type
     */
    @GetMapping
    public ResponseEntity<List<CustomViewDTO>> getViewsByEntityType(
            @RequestParam("entityType") String entityTypeStr,
            @RequestParam(value = "userId", required = false) Long userId) {
        
        try {
            EntityType entityType = EntityType.fromValue(entityTypeStr);
            List<CustomView> views;
            
            if (userId != null) {
                views = customViewService.getAccessibleViews(entityType, userId);
            } else {
                views = customViewService.getPublicViews(entityType);
            }
            
            List<CustomViewDTO> viewDTOs = views.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
            return ResponseEntity.ok(viewDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get custom view by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomViewDTO> getViewById(@PathVariable Long id) {
        Optional<CustomView> viewOpt = customViewService.getViewById(id);
        if (viewOpt.isPresent()) {
            return ResponseEntity.ok(convertToDTO(viewOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get default custom view for an entity type
     */
    @GetMapping("/default")
    public ResponseEntity<CustomViewDTO> getDefaultView(@RequestParam("entityType") String entityTypeStr) {
        try {
            EntityType entityType = EntityType.fromValue(entityTypeStr);
            Optional<CustomView> defaultView = customViewService.getDefaultView(entityType);
            
            if (defaultView.isPresent()) {
                return ResponseEntity.ok(convertToDTO(defaultView.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get user's custom views for an entity type
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CustomViewDTO>> getUserViews(
            @PathVariable Long userId,
            @RequestParam("entityType") String entityTypeStr) {
        
        try {
            EntityType entityType = EntityType.fromValue(entityTypeStr);
            List<CustomView> userViews = customViewService.getUserViews(entityType, userId);
            
            List<CustomViewDTO> viewDTOs = userViews.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
            return ResponseEntity.ok(viewDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Create a new custom view
     */
    @PostMapping
    public ResponseEntity<CustomViewDTO> createView(
            @Valid @RequestBody CustomViewDTO customViewDTO,
            @RequestParam("userId") Long userId) {
        
        try {
            // Check if view name already exists for the entity type
            if (customViewService.viewNameExists(customViewDTO.getViewName(), customViewDTO.getEntityType())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            CustomView customView = convertToEntity(customViewDTO);
            CustomView savedView = customViewService.createView(customView, userId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedView));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update an existing custom view
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomViewDTO> updateView(
            @PathVariable Long id,
            @Valid @RequestBody CustomViewDTO customViewDTO,
            @RequestParam("userId") Long userId) {
        
        try {
            // Check if view name already exists for the entity type (excluding current view)
            if (customViewService.viewNameExists(customViewDTO.getViewName(), 
                    customViewDTO.getEntityType(), id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            CustomView customView = convertToEntity(customViewDTO);
            CustomView updatedView = customViewService.updateView(id, customView, userId);
            
            return ResponseEntity.ok(convertToDTO(updatedView));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete a custom view
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteView(
            @PathVariable Long id,
            @RequestParam("userId") Long userId) {
        
        try {
            customViewService.deleteView(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Clone a custom view
     */
    @PostMapping("/{id}/clone")
    public ResponseEntity<CustomViewDTO> cloneView(
            @PathVariable Long id,
            @RequestParam("newViewName") String newViewName,
            @RequestParam("userId") Long userId) {
        
        try {
            CustomView clonedView = customViewService.cloneView(id, newViewName, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(clonedView));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get recent views for an entity type
     */
    @GetMapping("/recent")
    public ResponseEntity<List<CustomViewDTO>> getRecentViews(
            @RequestParam("entityType") String entityTypeStr,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        
        try {
            EntityType entityType = EntityType.fromValue(entityTypeStr);
            List<CustomView> recentViews = customViewService.getRecentViews(entityType, limit);
            
            List<CustomViewDTO> viewDTOs = recentViews.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
            return ResponseEntity.ok(viewDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get view count for an entity type
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getViewCount(@RequestParam("entityType") String entityTypeStr) {
        try {
            EntityType entityType = EntityType.fromValue(entityTypeStr);
            long count = customViewService.getViewCount(entityType);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all available entity types
     */
    @GetMapping("/entity-types")
    public ResponseEntity<List<EntityType>> getEntityTypes() {
        return ResponseEntity.ok(List.of(EntityType.values()));
    }
    
    // Helper methods for conversion
    private CustomViewDTO convertToDTO(CustomView customView) {
        return new CustomViewDTO(
                customView.getId(),
                customView.getViewName(),
                customView.getDescription(),
                customView.getEntityType(),
                customView.getSelectedFields(),
                customView.getIsDefault(),
                customView.getIsPublic(),
                customView.getCreatedBy(),
                customView.getCreatedTime(),
                customView.getModifiedBy(),
                customView.getModifiedTime()
        );
    }
    
    private CustomView convertToEntity(CustomViewDTO dto) {
        CustomView customView = new CustomView();
        customView.setId(dto.getId());
        customView.setViewName(dto.getViewName());
        customView.setDescription(dto.getDescription());
        customView.setEntityType(dto.getEntityType());
        customView.setSelectedFields(dto.getSelectedFields());
        customView.setIsDefault(dto.getIsDefault() != null && dto.getIsDefault());
        customView.setIsPublic(dto.getIsPublic() != null && dto.getIsPublic());
        return customView;
    }
}