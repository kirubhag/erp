package krs.erp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;
import krs.erp.service.DefaultCustomViewService;

/**
 * REST Controller for managing default custom views
 */
@RestController
@RequestMapping("/api/default-views")
@CrossOrigin(origins = "*")
public class DefaultCustomViewController {

    @Autowired
    private DefaultCustomViewService defaultCustomViewService;

    /**
     * Initialize default views for all entity types
     */
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeDefaultViews() {
        try {
            defaultCustomViewService.initializeDefaultViews();
            return ResponseEntity.ok("✅ Default views initialized successfully for all entity types!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("❌ Error initializing default views: " + e.getMessage());
        }
    }

    /**
     * Reset and recreate all default views
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetDefaultViews() {
        try {
            defaultCustomViewService.resetDefaultViews();
            return ResponseEntity.ok("✅ Default views reset and recreated successfully!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("❌ Error resetting default views: " + e.getMessage());
        }
    }

    /**
     * Get all default views grouped by entity type
     */
    @GetMapping("/all")
    public ResponseEntity<Map<EntityType, CustomView>> getAllDefaultViews() {
        try {
            Map<EntityType, CustomView> defaultViews = defaultCustomViewService.getAllDefaultViews();
            return ResponseEntity.ok(defaultViews);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get default view for a specific entity type
     */
    @GetMapping("/{entityType}")
    public ResponseEntity<CustomView> getDefaultViewForEntity(@PathVariable EntityType entityType) {
        try {
            Map<EntityType, CustomView> defaultViews = defaultCustomViewService.getAllDefaultViews();
            CustomView defaultView = defaultViews.get(entityType);
            
            if (defaultView != null) {
                return ResponseEntity.ok(defaultView);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get list of all available entity types
     */
    @GetMapping("/entity-types")
    public ResponseEntity<EntityType[]> getAllEntityTypes() {
        return ResponseEntity.ok(EntityType.values());
    }
}