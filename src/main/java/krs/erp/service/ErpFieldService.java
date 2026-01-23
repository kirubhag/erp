package krs.erp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.enums.UIFieldType;
import krs.erp.model.ErpField;
import krs.erp.repository.ErpFieldRepository;

@Service
@Transactional
public class ErpFieldService {

    @Autowired
    private ErpFieldRepository erpFieldRepository;

    /**
     * Get all active fields for a specific entity type
     */
    public List<ErpField> getFieldsByEntityType(EntityType entityType) {
        return erpFieldRepository.findByEntityTypeAndIsActiveTrue(entityType);
    }

    /**
     * Get all active fields for a specific entity type with pagination
     */
    public Page<ErpField> getFieldsByEntityType(EntityType entityType, Pageable pageable) {
        return erpFieldRepository.findByEntityTypeAndIsActiveTrue(entityType, pageable);
    }

    /**
     * Get fields grouped by category for a specific entity type
     * Note: Returns all fields under "General" since section info is now in erp_sections_field_rel table
     */
    public Map<String, List<ErpField>> getFieldsGroupedByCategory(EntityType entityType) {
        List<ErpField> fields = erpFieldRepository.findByEntityTypeAndIsActiveTrue(entityType);
        Map<String, List<ErpField>> result = new HashMap<>();
        result.put("General", fields);
        return result;
    }

    /**
     * Get searchable fields for a specific entity type
     */
    public List<ErpField> getSearchableFields(EntityType entityType) {
        return erpFieldRepository.findSearchableFieldsByEntityType(entityType);
    }

    /**
     * Get sortable fields for a specific entity type
     */
    public List<ErpField> getSortableFields(EntityType entityType) {
        return erpFieldRepository.findSortableFieldsByEntityType(entityType);
    }

    /**
     * Create or update a field
     */
    public ErpField saveField(ErpField field) {
        if (field.getId() == null) {
            field.setCreatedTime(LocalDateTime.now());
        }
        field.setModifiedTime(LocalDateTime.now());
        return erpFieldRepository.save(field);
    }

    /**
     * Create multiple fields for an entity
     */
    public List<ErpField> saveFields(List<ErpField> fields) {
        LocalDateTime now = LocalDateTime.now();
        fields.forEach(field -> {
            if (field.getId() == null) {
                field.setCreatedTime(now);
            }
            field.setModifiedTime(now);
        });
        return erpFieldRepository.saveAll(fields);
    }

    /**
     * Get field by entity type and field name
     */
    public ErpField getFieldByName(EntityType entityType, String fieldName) {
        return erpFieldRepository.findByEntityTypeAndFieldNameAndIsActiveTrue(entityType, fieldName);
    }

    /**
     * Check if a field exists
     */
    public boolean fieldExists(EntityType entityType, String fieldName) {
        return erpFieldRepository.existsByEntityTypeAndFieldNameAndIsActiveTrue(entityType, fieldName);
    }

    /**
     * Get field by ID
     */
    public ErpField getFieldById(Long fieldId) {
        return erpFieldRepository.findById(fieldId).orElse(null);
    }

    /**
     * Delete field (soft delete by setting isActive to 0)
     */
    public void deleteField(Long fieldId) {
        ErpField field = erpFieldRepository.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Field not found"));
        field.setIsActive(0);
        field.setModifiedTime(LocalDateTime.now());
        erpFieldRepository.save(field);
    }

    /**
     * Get total field count for an entity
     */
    public Long getFieldCount(EntityType entityType) {
        return erpFieldRepository.countByEntityTypeAndIsActiveTrue(entityType);
    }

    /**
     * Get all fields for entity type including UI type information
     */
    public List<ErpField> getAllFieldsWithUIType(EntityType entityType) {
        return erpFieldRepository.findByEntityTypeAndIsActiveTrue(entityType);
    }

    /**
     * Get fields by UI field type
     */
    public List<ErpField> getFieldsByUIType(EntityType entityType, int uiType) {
        return erpFieldRepository.findByEntityTypeAndUiTypeAndIsActiveTrue(entityType, uiType);
    }

    /**
     * Validate field configuration for UI type
     */
    public Map<String, Object> validateFieldConfiguration(ErpField field) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Get UI field type
        UIFieldType uiFieldType = field.getUIFieldType();
        if (uiFieldType == null) {
            errors.add("UI Field Type is required");
            result.put("valid", false);
            result.put("errors", errors);
            return result;
        }

        // Validate field name
        if (field.getFieldName() == null || field.getFieldName().trim().isEmpty()) {
            errors.add("Field name is required");
        } else if (!field.getFieldName().matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            errors.add("Field name must start with a letter and contain only letters, numbers, and underscores");
        }

        // Validate field label
        if (field.getFieldLabel() == null || field.getFieldLabel().trim().isEmpty()) {
            errors.add("Field label is required");
        }

        // Validate max length for text fields
        if (uiFieldType.getMaxLength() != null && field.getMaxLength() != null) {
            if (field.getMaxLength() > uiFieldType.getMaxLength()) {
                errors.add("Maximum length cannot exceed " + uiFieldType.getMaxLength() + " for "
                        + uiFieldType.getDisplayName());
            }
        }

        // Validate picklist options
        if (uiFieldType.hasOptions()) {
            if (field.getPicklistOptions() == null || field.getPicklistOptions().trim().isEmpty()) {
                errors.add(uiFieldType.getDisplayName() + " requires at least one option");
            }
        }

        // Validate decimal places
        if (uiFieldType.isNumeric() && field.getDecimalPlaces() != null) {
            if (field.getDecimalPlaces() < 0 || field.getDecimalPlaces() > 10) {
                errors.add("Decimal places must be between 0 and 10");
            }
        }

        // Validate validation pattern
        if (field.getValidationPattern() != null && !field.getValidationPattern().trim().isEmpty()) {
            try {
                java.util.regex.Pattern.compile(field.getValidationPattern());
            } catch (Exception e) {
                errors.add("Invalid validation pattern: " + e.getMessage());
            }
        }

        // Check for duplicate field names
        if (field.getId() == null || field.getId() == 0) {
            if (fieldExists(field.getEntityType(), field.getFieldName())) {
                errors.add("A field with this name already exists for this entity");
            }
        }

        // Add warnings for potential issues
        if (field.getIsRequired() && field.getUIFieldType() == UIFieldType.CHECKBOX) {
            warnings.add("Required checkbox fields may cause confusion for users");
        }

        if (field.getIsUnique() && uiFieldType.hasOptions()) {
            warnings.add("Unique constraint on picklist fields may limit reusability");
        }

        result.put("valid", errors.isEmpty());
        result.put("errors", errors);
        result.put("warnings", warnings);
        result.put("uiFieldType", uiFieldType);

        return result;
    }

}