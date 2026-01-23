package krs.erp.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import krs.erp.dto.LayoutDTO;
import krs.erp.model.ErpField;
import krs.erp.model.ErpLayout;
import krs.erp.service.ErpLayoutService;

/**
 * REST Controller for managing ERP layouts
 * Provides endpoints for layout configuration and the hierarchical layout -> sections -> fields structure
 */
@RestController
@RequestMapping("/api/layouts")
public class ErpLayoutController {

    @Autowired
    private ErpLayoutService layoutService;

    /**
     * Get all layouts for an entity type
     */
    @GetMapping("/entity/{entityType}")
    public ResponseEntity<List<ErpLayout>> getLayoutsByEntityType(@PathVariable String entityType) {
        try {
            List<ErpLayout> layouts = layoutService.getLayoutsByEntityType(entityType);
            return ResponseEntity.ok(layouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get the default layout for an entity type
     */
    @GetMapping("/entity/{entityType}/default")
    public ResponseEntity<ErpLayout> getDefaultLayout(@PathVariable String entityType) {
        try {
            Optional<ErpLayout> layout = layoutService.getDefaultLayout(entityType);
            return layout.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get layout by ID
     */
    @GetMapping("/{layoutId}")
    public ResponseEntity<ErpLayout> getLayoutById(@PathVariable Long layoutId) {
        try {
            Optional<ErpLayout> layout = layoutService.getLayoutById(layoutId);
            return layout.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get the complete layout structure with sections and fields
     * This is the main API for retrieving the hierarchical structure: layout -> sections -> fields
     */
    @GetMapping("/entity/{entityType}/complete")
    public ResponseEntity<LayoutDTO> getCompleteLayout(
            @PathVariable String entityType,
            @RequestParam(required = false, defaultValue = "System") String layoutName) {
        try {
            LayoutDTO layout = layoutService.getLayoutWithSectionsAndFields(entityType, layoutName);
            if (layout == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get the default layout with sections and fields
     */
    @GetMapping("/entity/{entityType}/complete/default")
    public ResponseEntity<LayoutDTO> getDefaultCompleteLayout(@PathVariable String entityType) {
        try {
            LayoutDTO layout = layoutService.getDefaultLayoutWithSectionsAndFields(entityType);
            if (layout == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get layout by ID with sections and fields
     */
    @GetMapping("/{layoutId}/complete")
    public ResponseEntity<LayoutDTO> getCompleteLayoutById(@PathVariable Long layoutId) {
        try {
            LayoutDTO layout = layoutService.getLayoutWithSectionsAndFieldsById(layoutId);
            if (layout == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get fields grouped by section for an entity (backward compatibility)
     */
    @GetMapping("/entity/{entityType}/fields-by-section")
    public ResponseEntity<Map<String, List<ErpField>>> getFieldsGroupedBySection(@PathVariable String entityType) {
        try {
            Map<String, List<ErpField>> fields = layoutService.getFieldsGroupedBySection(entityType);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new layout
     */
    @PostMapping
    public ResponseEntity<ErpLayout> createLayout(@RequestBody ErpLayout layout) {
        try {
            ErpLayout created = layoutService.createLayout(layout);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing layout
     */
    @PutMapping("/{layoutId}")
    public ResponseEntity<ErpLayout> updateLayout(@PathVariable Long layoutId, @RequestBody ErpLayout layout) {
        try {
            ErpLayout updated = layoutService.updateLayout(layoutId, layout);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a layout (soft delete)
     */
    @DeleteMapping("/{layoutId}")
    public ResponseEntity<Void> deleteLayout(@PathVariable Long layoutId) {
        try {
            layoutService.deleteLayout(layoutId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Add a section to a layout
     */
    @PostMapping("/{layoutId}/sections/{sectionId}")
    public ResponseEntity<Void> addSectionToLayout(
            @PathVariable Long layoutId,
            @PathVariable Long sectionId,
            @RequestParam(required = false, defaultValue = "0") Integer sectionOrder) {
        try {
            layoutService.addSectionToLayout(layoutId, sectionId, sectionOrder);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove a section from a layout
     */
    @DeleteMapping("/{layoutId}/sections/{sectionId}")
    public ResponseEntity<Void> removeSectionFromLayout(
            @PathVariable Long layoutId,
            @PathVariable Long sectionId) {
        try {
            layoutService.removeSectionFromLayout(layoutId, sectionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update section order in a layout
     */
    @PutMapping("/{layoutId}/sections/reorder")
    public ResponseEntity<Void> updateSectionOrder(
            @PathVariable Long layoutId,
            @RequestBody List<Long> sectionIds) {
        try {
            layoutService.updateSectionOrder(layoutId, sectionIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
