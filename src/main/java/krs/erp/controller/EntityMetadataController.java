package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.enums.EntityType;
import krs.erp.model.ErpField;
import krs.erp.service.ErpFieldService;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Entity Metadata operations
 * Provides field definitions and metadata for dynamic form generation
 */
@RestController
@RequestMapping("/api/entities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EntityMetadataController {

    private final ErpFieldService erpFieldService;

    /**
     * Get entity metadata including field definitions for the entity create form
     * 
     * @param entityType The entity type (e.g., "students", "staff", "attendance")
     * @return Entity metadata with field definitions
     */
    @GetMapping("/{entityType}/metadata")
    public ResponseEntity<Map<String, Object>> getEntityMetadata(@PathVariable String entityType) {
        try {
            // Convert entity type string to EntityType enum
            EntityType entityTypeEnum = convertToEntityType(entityType);
            
            // Get fields for the entity
            List<ErpField> fields = erpFieldService.getFieldsByEntityType(entityTypeEnum);
            
            // Build metadata response
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("entityType", entityType);
            metadata.put("entityName", entityTypeEnum.getDisplayName());
            metadata.put("entityNamePlural", toTitleCase(entityType));
            
            // Convert fields to field definitions
            List<Map<String, Object>> fieldDefinitions = fields.stream()
                .filter(field -> field.getShowInForm() == null || field.getShowInForm()) // Only show fields marked for forms
                .map(this::convertToFieldDefinition)
                .collect(Collectors.toList());
            
            metadata.put("fields", fieldDefinitions);
            
            return ResponseEntity.ok(metadata);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid entity type: " + entityType);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to load entity metadata: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Create a new entity record
     * 
     * @param entityType The entity type
     * @param data The entity data
     * @return Created entity response
     */
    @PostMapping("/{entityType}")
    public ResponseEntity<Map<String, Object>> createEntity(
            @PathVariable String entityType,
            @RequestBody Map<String, Object> data) {
        try {
            // TODO: Implement entity creation logic based on entity type
            // This is a placeholder that returns success with a mock ID
            Map<String, Object> response = new HashMap<>();
            response.put("id", System.currentTimeMillis());
            response.put("message", "Entity created successfully");
            response.put("entityType", entityType);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create entity: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Convert entity type string to EntityType enum
     * Examples: "students" -> STUDENT, "staff" -> STAFF
     */
    private EntityType convertToEntityType(String entityType) {
        // Remove trailing 's' for plural forms
        String singular = entityType.endsWith("s") && !entityType.equals("class") 
            ? entityType.substring(0, entityType.length() - 1) 
            : entityType;
        
        // Convert to uppercase for enum lookup
        try {
            return EntityType.valueOf(singular.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }

    /**
     * Convert string to title case
     */
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    /**
     * Convert ERPField to field definition map for frontend
     */
    private Map<String, Object> convertToFieldDefinition(ErpField field) {
        Map<String, Object> definition = new HashMap<>();
        
        definition.put("fieldName", field.getFieldName());
        definition.put("displayLabel", field.getFieldLabel());
        definition.put("uiType", field.getUiType());
        definition.put("dataType", field.getFieldType() != null ? field.getFieldType().name() : "STRING");
        definition.put("isRequired", field.getIsRequired() != null ? field.getIsRequired() : false);
        definition.put("isReadonly", false); // Can be extended if needed
        definition.put("defaultValue", null); // Can be extended if needed
        definition.put("maxLength", field.getMaxLength());
        definition.put("minValue", null); // Can be extended if needed
        definition.put("maxValue", null); // Can be extended if needed
        definition.put("decimalPlaces", field.getDecimalPlaces());
        definition.put("displayOrder", field.getDisplayOrder() != null ? field.getDisplayOrder() : 0);
        definition.put("rowPosition", field.getRowPosition() != null ? field.getRowPosition() : 0);
        definition.put("columnPosition", field.getColumnPosition() != null ? field.getColumnPosition() : 0);
        
        // Group fields by section (if available, otherwise use default)
        String sectionName = field.getSection() != null && field.getSection().getSectionLabel() != null 
            ? field.getSection().getSectionLabel() 
            : "General Information";
        definition.put("section", sectionName);
        
        // Convert picklist options if available
        if (field.getPicklistOptions() != null && !field.getPicklistOptions().isEmpty()) {
            definition.put("picklistValues", parsePicklistOptions(field.getPicklistOptions()));
        }
        
        return definition;
    }
    
    /**
     * Parse picklist options from JSON string or comma-separated values
     */
    private List<String> parsePicklistOptions(String picklistOptions) {
        if (picklistOptions == null || picklistOptions.isEmpty()) {
            return List.of();
        }
        
        // If it's a simple comma-separated list
        if (!picklistOptions.trim().startsWith("[") && !picklistOptions.trim().startsWith("{")) {
            return List.of(picklistOptions.split(","));
        }
        
        // For now, just split by comma - can be enhanced to parse JSON later
        return List.of(picklistOptions.split(","));
    }
}
