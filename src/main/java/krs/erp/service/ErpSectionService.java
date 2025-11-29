package krs.erp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.ErpSection;
import krs.erp.repository.ErpSectionRepository;

@Service
@Transactional
public class ErpSectionService {

    @Autowired
    private ErpSectionRepository sectionRepository;

    /**
     * Get all active sections for an entity type
     */
    public List<ErpSection> getSectionsByEntityType(EntityType entityType) {
        return sectionRepository.findByEntityTypeAndIsActiveTrue(entityType);
    }

    /**
     * Get all active sections for an entity type and organization
     */
    public List<ErpSection> getSectionsByEntityTypeAndOrganization(EntityType entityType, Long organizationId) {
        return sectionRepository.findByEntityTypeAndOrganizationIdAndIsActiveTrue(entityType, organizationId);
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
    public boolean sectionExists(EntityType entityType, String sectionName, Long organizationId) {
        return sectionRepository.existsByEntityTypeAndSectionNameAndOrganizationId(
                entityType, sectionName, organizationId);
    }
}
