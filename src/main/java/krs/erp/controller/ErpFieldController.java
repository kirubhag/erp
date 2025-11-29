package krs.erp.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import krs.erp.enums.EntityType;
import krs.erp.enums.UIFieldType;
import krs.erp.model.ErpField;
import krs.erp.service.ErpFieldService;

/**
 * REST Controller for managing ERP field metadata
 * Provides endpoints for field configuration and selection
 */
@RestController
@RequestMapping("/api/fields")
public class ErpFieldController {

    @Autowired
    private ErpFieldService erpFieldService;

    /**
     * Get all fields for a specific entity type
     */
    @GetMapping("/{entityType}")
    public ResponseEntity<List<ErpField>> getFieldsByEntityType(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }
            List<ErpField> fields = erpFieldService.getFieldsByEntityType(type);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get fields grouped by category for a specific entity type
     */
    @GetMapping("/{entityType}/grouped")
    public ResponseEntity<Map<String, List<ErpField>>> getFieldsGroupedByCategory(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }
            Map<String, List<ErpField>> groupedFields = erpFieldService.getFieldsGroupedByCategory(type);
            return ResponseEntity.ok(groupedFields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get searchable fields for a specific entity type
     */
    @GetMapping("/{entityType}/searchable")
    public ResponseEntity<List<ErpField>> getSearchableFields(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }
            List<ErpField> fields = erpFieldService.getSearchableFields(type);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get sortable fields for a specific entity type
     */
    @GetMapping("/{entityType}/sortable")
    public ResponseEntity<List<ErpField>> getSortableFields(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }
            List<ErpField> fields = erpFieldService.getSortableFields(type);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get distinct categories for a specific entity type
     * @deprecated Use ErpSectionController to get sections instead
     * This endpoint is kept for backward compatibility but returns empty list
     */
    @Deprecated
    @GetMapping("/{entityType}/categories")
    public ResponseEntity<List<String>> getFieldCategories(@PathVariable String entityType) {
        // Field categories are deprecated in favor of sections
        // Return empty list for backward compatibility
        return ResponseEntity.ok(List.of());
    }

    /**
     * Get a specific field by entity type and field name
     */
    @GetMapping("/{entityType}/field/{fieldName}")
    public ResponseEntity<ErpField> getFieldByName(@PathVariable String entityType, @PathVariable String fieldName) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }
            ErpField field = erpFieldService.getFieldByName(type, fieldName);
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
    public ResponseEntity<Long> getFieldCount(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }
            Long count = erpFieldService.getFieldCount(type);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check if a field exists
     */
    @GetMapping("/{entityType}/exists/{fieldName}")
    public ResponseEntity<Boolean> fieldExists(@PathVariable String entityType, @PathVariable String fieldName) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }
            boolean exists = erpFieldService.fieldExists(type, fieldName);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all available UI field types
     */
    @GetMapping("/ui-types")
    public ResponseEntity<UIFieldType[]> getUIFieldTypes() {
        try {
            return ResponseEntity.ok(UIFieldType.values());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get UI field type by ID
     */
    @GetMapping("/ui-types/{typeId}")
    public ResponseEntity<UIFieldType> getUIFieldType(@PathVariable int typeId) {
        try {
            UIFieldType uiFieldType = UIFieldType.getById(typeId);
            if (uiFieldType != null) {
                return ResponseEntity.ok(uiFieldType);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all fields for entity type including UI type information
     */
    @GetMapping("/{entityType}/all")
    public ResponseEntity<List<ErpField>> getAllFieldsWithUIType(@PathVariable String entityType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }
            List<ErpField> fields = erpFieldService.getAllFieldsWithUIType(type);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get fields by UI field type
     */
    @GetMapping("/{entityType}/ui-type/{uiType}")
    public ResponseEntity<List<ErpField>> getFieldsByUIType(@PathVariable String entityType, @PathVariable int uiType) {
        try {
            EntityType type = EntityType.fromValue(entityType);
            if (type == null) {
                return ResponseEntity.badRequest().build();
            }
            List<ErpField> fields = erpFieldService.getFieldsByUIType(type, uiType);
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Validate field configuration for UI type
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateField(@RequestBody ErpField field) {
        try {
            Map<String, Object> validation = erpFieldService.validateFieldConfiguration(field);
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}