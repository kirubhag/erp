package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.enums.EntityType;
import krs.erp.model.ErpAutoNumber;
import krs.erp.model.ErpField;
import krs.erp.service.AutoNumberService;
import krs.erp.service.ErpFieldService;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Entity Metadata operations
 * Provides field definitions and metadata for dynamic form generation
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EntityMetadataController {

    private final ErpFieldService erpFieldService;
    private final AutoNumberService autoNumberService;

    /**
     * Get entity metadata including field definitions for the entity create form
     * 
     * @param entityType The entity type (e.g., "students", "staff", "attendance")
     * @return Entity metadata with field definitions
     */
    @GetMapping("/api/module/{entityType}/metadata")
    public ResponseEntity<Map<String, Object>> getEntityMetadata(@PathVariable String entityType) {
        try {
            // Convert entity type string to EntityType enum
            EntityType entityTypeEnum = convertToEntityType(entityType);

            // Get fields for the entity
            List<ErpField> fields = erpFieldService.getFieldsByEntityType(entityTypeEnum);

            // Get auto-number configurations for this entity
            List<ErpAutoNumber> autoNumbers = autoNumberService.getAutoNumbersForEntity(entityTypeEnum);
            Map<String, ErpAutoNumber> autoNumberMap = new HashMap<>();
            for (ErpAutoNumber an : autoNumbers) {
                autoNumberMap.put(an.getFieldName(), an);
            }

            // Build metadata response
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("entityType", entityType);
            metadata.put("entityName", entityTypeEnum.getDisplayName());
            metadata.put("entityNamePlural", toTitleCase(entityType));

            // Convert fields to field definitions, enriching auto-number fields with preview
            List<Map<String, Object>> fieldDefinitions = fields.stream()
                    .filter(field -> field.getShowInForm() == null || field.getShowInForm()) // Only show fields marked for forms
                    .map(field -> convertToFieldDefinition(field, autoNumberMap))
                    .collect(Collectors.toList());

            metadata.put("fields", fieldDefinitions);

            // Include auto-number previews for the form
            Map<String, String> autoNumberPreviews = new HashMap<>();
            for (ErpAutoNumber an : autoNumbers) {
                autoNumberPreviews.put(an.getFieldName(), an.previewNextNumber());
            }
            metadata.put("autoNumberPreviews", autoNumberPreviews);

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
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /**
     * Convert ERPField to field definition map for frontend (with auto-number info)
     */
    private Map<String, Object> convertToFieldDefinition(ErpField field, Map<String, ErpAutoNumber> autoNumberMap) {
        Map<String, Object> definition = new HashMap<>();

        definition.put("fieldName", field.getFieldName());
        definition.put("displayLabel", field.getFieldLabel());
        definition.put("uiType", field.getUiType());
        definition.put("dataType", field.getFieldType() != null ? field.getFieldType().name() : "STRING");
        definition.put("isRequired", field.getIsRequired() != null ? field.getIsRequired() : false);

        // Auto Number (120) is always read-only and should not appear in forms
        boolean isAutoNumber = field.getUiType() != null && field.getUiType() == 120;
        boolean isReadonly = isAutoNumber;
        definition.put("isReadonly", isReadonly);
        definition.put("isAutoNumber", isAutoNumber);

        // If this is an auto-number field, get the configuration
        if (isAutoNumber && autoNumberMap.containsKey(field.getFieldName())) {
            ErpAutoNumber autoNumber = autoNumberMap.get(field.getFieldName());
            definition.put("autoNumberPrefix", autoNumber.getPrefix());
            definition.put("autoNumberSuffix", autoNumber.getSuffix());
            definition.put("autoNumberPreview", autoNumber.previewNextNumber());
            definition.put("placeholder", "Will be: " + autoNumber.previewNextNumber());
        }

        definition.put("defaultValue", null); // Can be extended if needed
        definition.put("maxLength", field.getMaxLength());
        definition.put("decimalPlaces", field.getDecimalPlaces());
        definition.put("displayOrder", field.getDisplayOrder() != null ? field.getDisplayOrder() : 0);
        definition.put("rowPosition", field.getRowPosition() != null ? field.getRowPosition() : 0);
        definition.put("columnPosition", field.getColumnPosition() != null ? field.getColumnPosition() : 0);

        // Parse field properties for advanced configuration (Slider, Auto Number)
        if (field.getFieldProperties() != null && !field.getFieldProperties().isEmpty()) {
            try {
                Map<String, Object> props = objectMapper.readValue(field.getFieldProperties(), Map.class);

                if (props.containsKey("minValue"))
                    definition.put("minValue", props.get("minValue"));
                if (props.containsKey("maxValue"))
                    definition.put("maxValue", props.get("maxValue"));
                if (props.containsKey("step"))
                    definition.put("step", props.get("step"));
                if (props.containsKey("defaultValue"))
                    definition.put("defaultValue", props.get("defaultValue"));

            } catch (Exception e) {
                // Ignore JSON parsing errors, fall back to defaults
                System.err.println(
                        "Error parsing field properties for field " + field.getFieldName() + ": " + e.getMessage());
            }
        } else {
            definition.put("minValue", null);
            definition.put("maxValue", null);
        }

        // Section name will be set from the relationship data, default to "General Information"
        definition.put("section", "General Information");

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
