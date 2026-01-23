package krs.erp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.dto.LayoutSaveDTO;
import krs.erp.enums.EntityType;
import krs.erp.model.ErpField;
import krs.erp.model.ErpSection;
import krs.erp.model.ErpSectionFieldRel;
import krs.erp.repository.ErpFieldRepository;
import krs.erp.repository.ErpSectionFieldRelRepository;
import krs.erp.repository.ErpSectionRepository;

@Service
@Transactional
public class ErpSectionService {

    @Autowired
    private ErpSectionRepository sectionRepository;

    @Autowired
    private ErpSectionFieldRelRepository sectionFieldRelRepository;

    /**
     * Get all active sections for an entity type
     */
    public List<ErpSection> getSectionsByEntityType(EntityType entityType) {
        return sectionRepository.findByEntityTypeAndIsActiveTrue(entityType);
    }

    /**
     * Get all active sections for an erp_entity_id
     */
    public List<ErpSection> getSectionsByErpEntityId(Long erpEntityId) {
        return sectionRepository.findByErpEntityIdAndIsActiveTrue(erpEntityId);
    }

    /**
     * Get section by ID
     */
    public Optional<ErpSection> getSectionById(Long id) {
        return sectionRepository.findById(id);
    }

    /**
     * Create a new section
     */
    public ErpSection createSection(ErpSection section) {
        // Set default display order if not provided
        if (section.getDisplayOrder() == null || section.getDisplayOrder() == 0) {
            Long count = sectionRepository.countByEntityTypeAndIsActiveTrue(section.getEntityType());
            section.setDisplayOrder(count.intValue() + 1);
        }

        // Ensure active status
        if (section.getIsActive() == null) {
            section.setIsActive(1);
        }

        return sectionRepository.save(section);
    }

    /**
     * Update an existing section
     */
    public ErpSection updateSection(Long id, ErpSection sectionData) {
        Optional<ErpSection> existingOpt = sectionRepository.findById(id);

        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Section not found with id: " + id);
        }

        ErpSection existing = existingOpt.get();

        // Update fields
        if (sectionData.getSectionLabel() != null) {
            existing.setSectionLabel(sectionData.getSectionLabel());
        }
        if (sectionData.getLayoutType() != null) {
            existing.setLayoutType(sectionData.getLayoutType());
        }
        if (sectionData.getDisplayOrder() != null) {
            existing.setDisplayOrder(sectionData.getDisplayOrder());
        }
        if (sectionData.getIsCollapsible() != null) {
            existing.setIsCollapsible(sectionData.getIsCollapsible());
        }
        if (sectionData.getIsCollapsedByDefault() != null) {
            existing.setIsCollapsedByDefault(sectionData.getIsCollapsedByDefault());
        }
        if (sectionData.getShowInCreate() != null) {
            existing.setShowInCreate(sectionData.getShowInCreate());
        }
        if (sectionData.getShowInEdit() != null) {
            existing.setShowInEdit(sectionData.getShowInEdit());
        }
        if (sectionData.getShowInDetail() != null) {
            existing.setShowInDetail(sectionData.getShowInDetail());
        }
        if (sectionData.getSectionIcon() != null) {
            existing.setSectionIcon(sectionData.getSectionIcon());
        }
        if (sectionData.getSectionColor() != null) {
            existing.setSectionColor(sectionData.getSectionColor());
        }
        if (sectionData.getCssClass() != null) {
            existing.setCssClass(sectionData.getCssClass());
        }
        if (sectionData.getDescription() != null) {
            existing.setDescription(sectionData.getDescription());
        }
        if (sectionData.getHelpText() != null) {
            existing.setHelpText(sectionData.getHelpText());
        }

        return sectionRepository.save(existing);
    }

    /**
     * Delete a section (soft delete)
     */
    public void deleteSection(Long id) {
        Optional<ErpSection> sectionOpt = sectionRepository.findById(id);

        if (sectionOpt.isPresent()) {
            ErpSection section = sectionOpt.get();
            section.setIsActive(-1); // Soft delete
            sectionRepository.save(section);
        }
    }

    /**
     * Reorder sections for an entity type
     */
    public void reorderSections(EntityType entityType, List<Long> sectionIds) {
        for (int i = 0; i < sectionIds.size(); i++) {
            Long sectionId = sectionIds.get(i);
            Optional<ErpSection> sectionOpt = sectionRepository.findById(sectionId);

            if (sectionOpt.isPresent()) {
                ErpSection section = sectionOpt.get();
                section.setDisplayOrder(i + 1);
                sectionRepository.save(section);
            }
        }
    }

    /**
     * Get sections visible in create view
     */
    public List<ErpSection> getSectionsForCreateView(EntityType entityType) {
        return sectionRepository.findVisibleInCreate(entityType);
    }

    /**
     * Get sections visible in edit view
     */
    public List<ErpSection> getSectionsForEditView(EntityType entityType) {
        return sectionRepository.findVisibleInEdit(entityType);
    }

    /**
     * Get sections visible in detail view
     */
    public List<ErpSection> getSectionsForDetailView(EntityType entityType) {
        return sectionRepository.findVisibleInDetail(entityType);
    }

    /**
     * Check if section name exists
     */
    public boolean sectionExists(EntityType entityType, String sectionName) {
        return sectionRepository.existsByEntityTypeAndSectionNameAndIsActiveTrue(
                entityType, sectionName);
    }

    /**
     * Save complete module layout including sections and field positions
     */
    @Transactional
    public Map<String, Object> saveModuleLayout(LayoutSaveDTO layoutData, ErpFieldRepository fieldRepository) {
        Map<String, Object> result = new HashMap<>();
        int sectionsUpdated = 0;
        int fieldsUpdated = 0;
        List<String> errors = new ArrayList<>();

        try {
            // Parse entity type
            EntityType entityType = EntityType.valueOf(layoutData.getEntityType().toUpperCase());

            // Process each section
            for (LayoutSaveDTO.SectionLayoutDTO sectionData : layoutData.getSections()) {
                try {
                    // Find section by entity type and section name
                    ErpSection section = null;
                    List<ErpSection> allSections = sectionRepository.findByEntityTypeAndIsActiveTrue(entityType);
                    for (ErpSection s : allSections) {
                        if (s.getSectionName().equals(sectionData.getSectionName())) {
                            section = s;
                            break;
                        }
                    }

                    if (section != null) {
                        // Update existing section
                        section.setDisplayOrder(sectionData.getDisplayOrder());
                        section.setSectionLabel(sectionData.getSectionLabel());
                        sectionRepository.save(section);
                        sectionsUpdated++;
                    } else {
                        // Create new section if doesn't exist
                        section = new ErpSection(entityType, sectionData.getSectionName(),
                                sectionData.getSectionLabel());
                        section.setDisplayOrder(sectionData.getDisplayOrder());
                        section.setIsActive(1);
                        sectionRepository.save(section);
                        sectionsUpdated++;
                    }

                    // Update field positions using the relationship table
                    int fieldOrderCounter = 0;
                    for (LayoutSaveDTO.FieldPositionDTO fieldPos : sectionData.getFields()) {
                        ErpField field = fieldRepository.findByEntityTypeAndFieldNameAndIsActiveTrue(
                                entityType, fieldPos.getFieldName());

                        if (field != null) {
                            // Create or update section-field relationship
                            ErpSectionFieldRel.ErpSectionFieldRelId relId = 
                                new ErpSectionFieldRel.ErpSectionFieldRelId(section.getId(), field.getId());
                            Optional<ErpSectionFieldRel> existingRel = sectionFieldRelRepository.findById(relId);
                            
                            ErpSectionFieldRel rel;
                            if (existingRel.isPresent()) {
                                rel = existingRel.get();
                                rel.setFieldOrder(fieldOrderCounter++);
                            } else {
                                rel = new ErpSectionFieldRel(section, field, fieldOrderCounter++);
                            }
                            sectionFieldRelRepository.save(rel);

                            // Update field row/column positions
                            field.setRowPosition(fieldPos.getRowPosition());
                            field.setColumnPosition(fieldPos.getColumnPosition());

                            // Update field properties if provided
                            if (fieldPos.getFieldProperties() != null) {
                                field.setFieldProperties(fieldPos.getFieldProperties());
                            }

                            fieldRepository.save(field);
                            fieldsUpdated++;
                        } else {
                            errors.add("Field not found: " + fieldPos.getFieldName());
                        }
                    }
                } catch (Exception e) {
                    errors.add("Error processing section " + sectionData.getSectionName() + ": " + e.getMessage());
                }
            }

            result.put("success", true);
            result.put("sectionsUpdated", sectionsUpdated);
            result.put("fieldsUpdated", fieldsUpdated);
            result.put("errors", errors);
            result.put("message",
                    String.format("Successfully updated %d sections and %d fields", sectionsUpdated, fieldsUpdated));

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error saving layout: " + e.getMessage());
            result.put("errors", errors);
        }

        return result;
    }
}
