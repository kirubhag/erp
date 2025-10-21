package krs.erp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.enums.EntityType;
import krs.erp.model.ErpField;
import krs.erp.service.ErpFieldService;

/**
 * REST Controller for managing ERP field metadata
 * Provides endpoints for field configuration and selection
 */
@RestController
@RequestMapping("/api/fields")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ErpFieldController {

    @Autowired
    private ErpFieldService erpFieldService;

    /**
     * Get all fields for a specific entity type
     */
    @GetMapping("/{entityType}")
    public ResponseEntity<List<ErpField>> getFieldsByEntityType(@PathVariable EntityType entityType) {
        try {
            List<ErpField> fields = erpFieldService.getFieldsByEntityType(entityType);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get fields grouped by category for a specific entity type
     */
    @GetMapping("/{entityType}/grouped")
    public ResponseEntity<Map<String, List<ErpField>>> getFieldsGroupedByCategory(@PathVariable EntityType entityType) {
        try {
            Map<String, List<ErpField>> groupedFields = erpFieldService.getFieldsGroupedByCategory(entityType);
            return ResponseEntity.ok(groupedFields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get searchable fields for a specific entity type
     */
    @GetMapping("/{entityType}/searchable")
    public ResponseEntity<List<ErpField>> getSearchableFields(@PathVariable EntityType entityType) {
        try {
            List<ErpField> fields = erpFieldService.getSearchableFields(entityType);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get sortable fields for a specific entity type
     */
    @GetMapping("/{entityType}/sortable")
    public ResponseEntity<List<ErpField>> getSortableFields(@PathVariable EntityType entityType) {
        try {
            List<ErpField> fields = erpFieldService.getSortableFields(entityType);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get distinct categories for a specific entity type
     */
    @GetMapping("/{entityType}/categories")
    public ResponseEntity<List<String>> getFieldCategories(@PathVariable EntityType entityType) {
        try {
            List<String> categories = erpFieldService.getFieldCategories(entityType);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific field by entity type and field name
     */
    @GetMapping("/{entityType}/field/{fieldName}")
    public ResponseEntity<ErpField> getFieldByName(@PathVariable EntityType entityType, @PathVariable String fieldName) {
        try {
            ErpField field = erpFieldService.getFieldByName(entityType, fieldName);
            if (field != null) {
                return ResponseEntity.ok(field);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create or update a field
     */
    @PostMapping
    public ResponseEntity<ErpField> saveField(@RequestBody ErpField field) {
        try {
            ErpField savedField = erpFieldService.saveField(field);
            return ResponseEntity.ok(savedField);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create or update multiple fields
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ErpField>> saveFields(@RequestBody List<ErpField> fields) {
        try {
            List<ErpField> savedFields = erpFieldService.saveFields(fields);
            return ResponseEntity.ok(savedFields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update field configuration (enable/disable, change properties)
     */
    @PutMapping("/{fieldId}")
    public ResponseEntity<ErpField> updateField(@PathVariable Long fieldId, @RequestBody ErpField field) {
        try {
            field.setId(fieldId);
            ErpField updatedField = erpFieldService.saveField(field);
            return ResponseEntity.ok(updatedField);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Soft delete a field
     */
    @DeleteMapping("/{fieldId}")
    public ResponseEntity<Void> deleteField(@PathVariable Long fieldId) {
        try {
            erpFieldService.deleteField(fieldId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get field count for an entity type
     */
    @GetMapping("/{entityType}/count")
    public ResponseEntity<Long> getFieldCount(@PathVariable EntityType entityType) {
        try {
            Long count = erpFieldService.getFieldCount(entityType);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check if a field exists
     */
    @GetMapping("/{entityType}/exists/{fieldName}")
    public ResponseEntity<Boolean> fieldExists(@PathVariable EntityType entityType, @PathVariable String fieldName) {
        try {
            boolean exists = erpFieldService.fieldExists(entityType, fieldName);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}