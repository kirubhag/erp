package krs.erp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
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
     */
    public Map<String, List<ErpField>> getFieldsGroupedByCategory(EntityType entityType) {
        List<ErpField> fields = getFieldsByEntityType(entityType);
        return fields.stream()
                .collect(Collectors.groupingBy(
                    field -> field.getFieldCategory() != null ? field.getFieldCategory() : "General"
                ));
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
     * Get distinct categories for a specific entity type
     */
    public List<String> getFieldCategories(EntityType entityType) {
        return erpFieldRepository.findDistinctFieldCategoriesByEntityType(entityType);
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


}