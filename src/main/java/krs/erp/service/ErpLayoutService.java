package krs.erp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.dto.LayoutDTO;
import krs.erp.dto.LayoutDTO.FieldDTO;
import krs.erp.dto.LayoutDTO.SectionDTO;
import krs.erp.enums.EntityType;
import krs.erp.model.ErpEntity;
import krs.erp.model.ErpField;
import krs.erp.model.ErpLayout;
import krs.erp.model.ErpLayoutSectionRel;
import krs.erp.model.ErpSection;
import krs.erp.model.ErpSectionFieldRel;
import krs.erp.model.FieldType;
import krs.erp.repository.ErpEntityRepository;
import krs.erp.repository.ErpFieldRepository;
import krs.erp.repository.ErpLayoutRepository;
import krs.erp.repository.ErpLayoutSectionRelRepository;
import krs.erp.repository.ErpSectionFieldRelRepository;
import krs.erp.repository.ErpSectionRepository;

/**
 * Service for managing ERP layouts and the hierarchical structure:
 * Layout -> Sections -> Fields
 */
@Service
@Transactional
public class ErpLayoutService {

    @Autowired
    private ErpLayoutRepository layoutRepository;

    @Autowired
    private ErpLayoutSectionRelRepository layoutSectionRelRepository;

    @Autowired
    private ErpSectionRepository sectionRepository;

    @Autowired
    private ErpSectionFieldRelRepository sectionFieldRelRepository;

    @Autowired
    private ErpFieldRepository fieldRepository;

    @Autowired
    private ErpEntityRepository entityRepository;

    /**
     * Get all layouts for an entity type
     */
    public List<ErpLayout> getLayoutsByEntityType(String entityType) {
        EntityType type = EntityType.fromValue(entityType);
        if (type == null) {
            return List.of();
        }
        
        // Find the entity by singular name
        Optional<ErpEntity> entityOpt = entityRepository.findBySingularName(entityType);
        if (entityOpt.isEmpty()) {
            return List.of();
        }
        
        return layoutRepository.findByErpEntityIdAndIsActiveTrue(entityOpt.get().getId());
    }

    /**
     * Get the default layout for an entity type
     */
    public Optional<ErpLayout> getDefaultLayout(String entityType) {
        Optional<ErpEntity> entityOpt = entityRepository.findBySingularName(entityType);
        if (entityOpt.isEmpty()) {
            return Optional.empty();
        }
        
        return layoutRepository.findDefaultByErpEntityId(entityOpt.get().getId());
    }

    /**
     * Get layout by ID
     */
    public Optional<ErpLayout> getLayoutById(Long layoutId) {
        return layoutRepository.findById(layoutId);
    }

    /**
     * Get the complete layout structure with sections and fields
     * This is the main API for retrieving the hierarchical structure
     */
    public LayoutDTO getLayoutWithSectionsAndFields(String entityType, String layoutName) {
        Optional<ErpEntity> entityOpt = entityRepository.findBySingularName(entityType);
        if (entityOpt.isEmpty()) {
            return null;
        }

        ErpEntity entity = entityOpt.get();

        // Get layout
        Optional<ErpLayout> layoutOpt = layoutRepository.findByErpEntityIdAndLayoutName(entity.getId(), layoutName);
        if (layoutOpt.isEmpty()) {
            // Try to get default layout
            layoutOpt = layoutRepository.findDefaultByErpEntityId(entity.getId());
        }
        
        if (layoutOpt.isEmpty()) {
            return null;
        }

        ErpLayout layout = layoutOpt.get();
        return buildLayoutDTO(layout, entityType);
    }

    /**
     * Get the default layout with sections and fields for an entity type
     */
    public LayoutDTO getDefaultLayoutWithSectionsAndFields(String entityType) {
        return getLayoutWithSectionsAndFields(entityType, "System");
    }

    /**
     * Get layout by ID with sections and fields
     */
    public LayoutDTO getLayoutWithSectionsAndFieldsById(Long layoutId) {
        Optional<ErpLayout> layoutOpt = layoutRepository.findById(layoutId);
        if (layoutOpt.isEmpty()) {
            return null;
        }

        ErpLayout layout = layoutOpt.get();
        String entityType = layout.getErpEntity() != null ? layout.getErpEntity().getSingularName() : null;
        return buildLayoutDTO(layout, entityType);
    }

    /**
     * Build the complete LayoutDTO from an ErpLayout
     */
    private LayoutDTO buildLayoutDTO(ErpLayout layout, String entityType) {
        LayoutDTO dto = new LayoutDTO();
        dto.setId(layout.getId());
        dto.setLayoutName(layout.getLayoutName());
        dto.setLayoutType(layout.getLayoutType());
        dto.setLayoutColumns(layout.getLayoutColumns());
        dto.setIsDefault(layout.getIsDefault());
        // ErpLayout doesn't have description, leaving it null
        dto.setErpEntityId(layout.getErpEntity() != null ? layout.getErpEntity().getId() : null);
        dto.setEntityType(entityType);

        // Get sections for this layout
        List<ErpLayoutSectionRel> layoutSectionRels = layoutSectionRelRepository.findByLayoutId(layout.getId());
        
        List<SectionDTO> sectionDTOs = new ArrayList<>();
        
        for (ErpLayoutSectionRel rel : layoutSectionRels) {
            Optional<ErpSection> sectionOpt = sectionRepository.findById(rel.getId().getErpSectionId());
            if (sectionOpt.isPresent()) {
                ErpSection section = sectionOpt.get();
                SectionDTO sectionDTO = buildSectionDTO(section, rel.getSectionOrder());
                sectionDTOs.add(sectionDTO);
            }
        }

        // Sort sections by display order
        sectionDTOs.sort(Comparator.comparing(SectionDTO::getDisplayOrder));
        dto.setSections(sectionDTOs);

        return dto;
    }

    /**
     * Build a SectionDTO with its fields
     */
    private SectionDTO buildSectionDTO(ErpSection section, Integer sectionOrder) {
        SectionDTO dto = new SectionDTO();
        dto.setId(section.getId());
        dto.setSectionName(section.getSectionName());
        dto.setSectionLabel(section.getSectionLabel());
        dto.setLayoutType(section.getLayoutType() != null ? section.getLayoutType().name() : null);
        dto.setDisplayOrder(sectionOrder != null ? sectionOrder : section.getDisplayOrder());
        dto.setIsCollapsible(section.getIsCollapsible());
        dto.setIsCollapsedByDefault(section.getIsCollapsedByDefault());
        dto.setShowInCreate(section.getShowInCreate());
        dto.setShowInEdit(section.getShowInEdit());
        dto.setShowInDetail(section.getShowInDetail());
        dto.setSectionIcon(section.getSectionIcon());
        dto.setSectionColor(section.getSectionColor());
        dto.setCssClass(section.getCssClass());
        dto.setDescription(section.getDescription());
        dto.setHelpText(section.getHelpText());

        // Get fields for this section
        List<ErpSectionFieldRel> fieldRels = sectionFieldRelRepository.findBySectionId(section.getId());
        
        List<FieldDTO> fieldDTOs = new ArrayList<>();
        
        for (ErpSectionFieldRel rel : fieldRels) {
            Optional<ErpField> fieldOpt = fieldRepository.findById(rel.getId().getErpFieldId());
            if (fieldOpt.isPresent()) {
                ErpField field = fieldOpt.get();
                FieldDTO fieldDTO = buildFieldDTO(field, rel.getFieldOrder());
                fieldDTOs.add(fieldDTO);
            }
        }

        // Sort fields by field order
        fieldDTOs.sort(Comparator.comparing(FieldDTO::getFieldOrder, Comparator.nullsLast(Comparator.naturalOrder())));
        dto.setFields(fieldDTOs);

        return dto;
    }

    /**
     * Build a FieldDTO
     */
    private FieldDTO buildFieldDTO(ErpField field, Integer fieldOrder) {
        FieldDTO dto = new FieldDTO();
        dto.setId(field.getId());
        dto.setFieldName(field.getFieldName());
        dto.setFieldLabel(field.getFieldLabel());
        dto.setFieldType(field.getFieldType() != null ? field.getFieldType().name() : null);
        dto.setUiType(field.getUiType());
        dto.setIsRequired(field.getIsRequired());
        dto.setIsSearchable(field.getIsSearchable());
        dto.setIsSortable(field.getIsSortable());
        dto.setDisplayOrder(field.getDisplayOrder());
        dto.setFieldDescription(field.getFieldDescription());
        dto.setDefaultWidth(field.getDefaultWidth());
        dto.setMaxLength(field.getMaxLength());
        dto.setValidationPattern(field.getValidationPattern());
        dto.setPicklistOptions(field.getPicklistOptions());
        dto.setDecimalPlaces(field.getDecimalPlaces());
        dto.setIsUnique(field.getIsUnique());
        dto.setShowInList(field.getShowInList());
        dto.setShowInForm(field.getShowInForm());
        dto.setShowType(field.getShowType());
        dto.setFieldProperties(field.getFieldProperties());
        dto.setFieldOrder(fieldOrder != null ? fieldOrder : field.getDisplayOrder());
        return dto;
    }

    /**
     * Create a new layout
     */
    public ErpLayout createLayout(ErpLayout layout) {
        layout.setCreatedTime(LocalDateTime.now());
        layout.setModifiedTime(LocalDateTime.now());
        layout.setIsActive(1);
        return layoutRepository.save(layout);
    }

    /**
     * Update an existing layout
     */
    public ErpLayout updateLayout(Long layoutId, ErpLayout layoutData) {
        Optional<ErpLayout> existingOpt = layoutRepository.findById(layoutId);
        if (existingOpt.isEmpty()) {
            return null;
        }

        ErpLayout existing = existingOpt.get();
        existing.setLayoutName(layoutData.getLayoutName());
        existing.setLayoutType(layoutData.getLayoutType());
        existing.setLayoutColumns(layoutData.getLayoutColumns());
        existing.setIsDefault(layoutData.getIsDefault());
        existing.setModifiedTime(LocalDateTime.now());

        return layoutRepository.save(existing);
    }

    /**
     * Delete a layout (soft delete)
     */
    public void deleteLayout(Long layoutId) {
        Optional<ErpLayout> existingOpt = layoutRepository.findById(layoutId);
        if (existingOpt.isPresent()) {
            ErpLayout existing = existingOpt.get();
            existing.setIsActive(0);
            existing.setModifiedTime(LocalDateTime.now());
            layoutRepository.save(existing);
        }
    }

    /**
     * Add a section to a layout
     */
    public void addSectionToLayout(Long layoutId, Long sectionId, Integer sectionOrder) {
        ErpLayoutSectionRel.ErpLayoutSectionRelId id = new ErpLayoutSectionRel.ErpLayoutSectionRelId();
        id.setErpLayoutId(layoutId);
        id.setErpSectionId(sectionId);

        ErpLayoutSectionRel rel = new ErpLayoutSectionRel();
        rel.setId(id);
        rel.setSectionOrder(sectionOrder);
        rel.setCreatedTime(LocalDateTime.now());
        rel.setModifiedTime(LocalDateTime.now());

        layoutSectionRelRepository.save(rel);
    }

    /**
     * Remove a section from a layout
     */
    public void removeSectionFromLayout(Long layoutId, Long sectionId) {
        ErpLayoutSectionRel.ErpLayoutSectionRelId id = new ErpLayoutSectionRel.ErpLayoutSectionRelId();
        id.setErpLayoutId(layoutId);
        id.setErpSectionId(sectionId);

        layoutSectionRelRepository.deleteById(id);
    }

    /**
     * Update section order in a layout
     */
    public void updateSectionOrder(Long layoutId, List<Long> sectionIds) {
        // Delete existing relations
        layoutSectionRelRepository.deleteByLayoutId(layoutId);

        // Create new relations with updated order
        int order = 1;
        for (Long sectionId : sectionIds) {
            addSectionToLayout(layoutId, sectionId, order++);
        }
    }

    /**
     * Get all fields for an entity grouped by section (for backward compatibility)
     */
    public Map<String, List<ErpField>> getFieldsGroupedBySection(String entityType) {
        LayoutDTO layout = getDefaultLayoutWithSectionsAndFields(entityType);
        if (layout == null || layout.getSections() == null) {
            return new HashMap<>();
        }

        Map<String, List<ErpField>> result = new HashMap<>();
        
        for (SectionDTO section : layout.getSections()) {
            if (section.getFields() != null && !section.getFields().isEmpty()) {
                List<ErpField> fields = section.getFields().stream()
                        .map(this::convertFieldDTOToEntity)
                        .collect(Collectors.toList());
                result.put(section.getSectionLabel(), fields);
            }
        }

        return result;
    }

    /**
     * Convert FieldDTO back to ErpField (for backward compatibility)
     */
    private ErpField convertFieldDTOToEntity(FieldDTO dto) {
        ErpField field = new ErpField();
        field.setId(dto.getId());
        field.setFieldName(dto.getFieldName());
        field.setFieldLabel(dto.getFieldLabel());
        if (dto.getFieldType() != null) {
            try {
                field.setFieldType(FieldType.valueOf(dto.getFieldType()));
            } catch (IllegalArgumentException e) {
                // Ignore invalid field type
            }
        }
        field.setUiType(dto.getUiType());
        field.setIsRequired(dto.getIsRequired());
        field.setIsSearchable(dto.getIsSearchable());
        field.setIsSortable(dto.getIsSortable());
        field.setDisplayOrder(dto.getDisplayOrder());
        field.setFieldDescription(dto.getFieldDescription());
        field.setDefaultWidth(dto.getDefaultWidth());
        field.setMaxLength(dto.getMaxLength());
        field.setValidationPattern(dto.getValidationPattern());
        field.setPicklistOptions(dto.getPicklistOptions());
        field.setDecimalPlaces(dto.getDecimalPlaces());
        field.setIsUnique(dto.getIsUnique());
        field.setShowInList(dto.getShowInList());
        field.setShowInForm(dto.getShowInForm());
        field.setShowType(dto.getShowType());
        field.setFieldProperties(dto.getFieldProperties());
        return field;
    }
}
