package krs.erp.controller;

import java.util.HashMap;
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

import krs.erp.enums.EntityType;
import krs.erp.model.ErpSection;
import krs.erp.service.ErpSectionService;

/**
 * REST Controller for managing ERP sections
 * Provides endpoints for section configuration and management
 */
@RestController
@RequestMapping("/api/sections")
public class ErpSectionController {

    @Autowired
    private ErpSectionService sectionService;

    /**
     * Get all sections for a specific entity type
     */
    @GetMapping("/{entityType}")
    public ResponseEntity<List<ErpSection>> getSections(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }

            List<ErpSection> sections = sectionService.getSectionsByEntityType(type);
            return ResponseEntity.ok(sections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get sections for a specific entity type and organization
     */
    @GetMapping("/{entityType}/organization/{organizationId}")
    public ResponseEntity<List<ErpSection>> getSectionsByOrganization(
            @PathVariable String entityType,
            @PathVariable Long organizationId) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }

            List<ErpSection> sections = sectionService.getSectionsByEntityTypeAndOrganization(type, organizationId);
            return ResponseEntity.ok(sections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get sections for create view
     */
    @GetMapping("/{entityType}/create")
    public ResponseEntity<List<ErpSection>> getSectionsForCreate(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }

            List<ErpSection> sections = sectionService.getSectionsForCreateView(type);
            return ResponseEntity.ok(sections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get sections for edit view
     */
    @GetMapping("/{entityType}/edit")
    public ResponseEntity<List<ErpSection>> getSectionsForEdit(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }

            List<ErpSection> sections = sectionService.getSectionsForEditView(type);
            return ResponseEntity.ok(sections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get sections for detail view
     */
    @GetMapping("/{entityType}/detail")
    public ResponseEntity<List<ErpSection>> getSectionsForDetail(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }

            List<ErpSection> sections = sectionService.getSectionsForDetailView(type);
            return ResponseEntity.ok(sections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific section by ID
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<ErpSection> getSectionById(@PathVariable Long id) {
        try {
            Optional<ErpSection> section = sectionService.getSectionById(id);
            return section.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new section
     */
    @PostMapping
    public ResponseEntity<ErpSection> createSection(@RequestBody ErpSection section) {
        try {
            ErpSection created = sectionService.createSection(section);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing section
     */
    @PutMapping("/{id}")
    public ResponseEntity<ErpSection> updateSection(
            @PathVariable Long id,
            @RequestBody ErpSection section) {
        try {
            ErpSection updated = sectionService.updateSection(id, section);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a section (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSection(@PathVariable Long id) {
        try {
            sectionService.deleteSection(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Section deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Reorder sections for an entity type
     */
    @PostMapping("/{entityType}/reorder")
    public ResponseEntity<Map<String, String>> reorderSections(
            @PathVariable String entityType,
            @RequestBody List<Long> sectionIds) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }

            sectionService.reorderSections(type, sectionIds);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sections reordered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check if section name exists
     */
    @GetMapping("/{entityType}/exists")
    public ResponseEntity<Map<String, Boolean>> checkSectionExists(
            @PathVariable String entityType,
            @RequestParam String sectionName,
            @RequestParam Long organizationId) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }

            boolean exists = sectionService.sectionExists(type, sectionName, organizationId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
