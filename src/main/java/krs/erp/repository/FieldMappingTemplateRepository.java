package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.entity.FieldMappingTemplate;

/**
 * Repository for FieldMappingTemplate entities
 */
@Repository
public interface FieldMappingTemplateRepository extends JpaRepository<FieldMappingTemplate, Long> {
    
    /**
     * Find templates by entity type ordered by display order
     */
    List<FieldMappingTemplate> findByEntityTypeOrderByDisplayOrder(String entityType);
    
    /**
     * Find templates by entity type and section
     */
    List<FieldMappingTemplate> findByEntityTypeAndSectionOrderByDisplayOrder(String entityType, String section);
    
    /**
     * Find a specific template by entity type and field name
     */
    Optional<FieldMappingTemplate> findByEntityTypeAndFieldName(String entityType, String fieldName);
    
    /**
     * Get distinct sections for an entity type
     */
    List<String> findDistinctSectionsByEntityType(String entityType);
}
