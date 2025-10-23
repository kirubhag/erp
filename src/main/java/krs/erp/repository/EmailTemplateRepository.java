package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.EmailTemplate;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    
    // Find by template name
    Optional<EmailTemplate> findByTemplateName(String templateName);
    
    // Find by template name ignoring case
    Optional<EmailTemplate> findByTemplateNameIgnoreCase(String templateName);
    
    // Find by entity type
    List<EmailTemplate> findByEntityType(EmailTemplate.EntityType entityType);
    
    // Find by entity type with pagination
    Page<EmailTemplate> findByEntityType(EmailTemplate.EntityType entityType, Pageable pageable);
    
    // Find active templates
    @Query("SELECT t FROM EmailTemplate t WHERE t.isActive = true ORDER BY t.templateName ASC")
    List<EmailTemplate> findAllActiveTemplates();
    
    // Find active templates by entity type
    @Query("SELECT t FROM EmailTemplate t WHERE t.entityType = :entityType AND t.isActive = true ORDER BY t.templateName ASC")
    List<EmailTemplate> findActiveTemplatesByEntityType(@Param("entityType") EmailTemplate.EntityType entityType);
    
    // Find templates by created by
    List<EmailTemplate> findByCreatedByIgnoreCase(String createdBy);
    
    // Search templates by name or description
    @Query("SELECT t FROM EmailTemplate t WHERE LOWER(t.templateName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<EmailTemplate> searchTemplates(@Param("searchTerm") String searchTerm);
    
    // Find templates with usage count greater than specified value
    @Query("SELECT t FROM EmailTemplate t WHERE t.usageCount > :minUsage ORDER BY t.usageCount DESC")
    List<EmailTemplate> findPopularTemplates(@Param("minUsage") Integer minUsage);
    
    // Find most used templates
    @Query("SELECT t FROM EmailTemplate t WHERE t.usageCount > 0 ORDER BY t.usageCount DESC")
    List<EmailTemplate> findMostUsedTemplates();
    
    // Count templates by entity type
    @Query("SELECT COUNT(t) FROM EmailTemplate t WHERE t.entityType = :entityType AND t.isActive = true")
    Long countActiveTemplatesByEntityType(@Param("entityType") EmailTemplate.EntityType entityType);
    
    // Find templates that haven't been used recently
    @Query("SELECT t FROM EmailTemplate t WHERE t.lastUsed IS NULL OR t.lastUsed < :cutoffDate ORDER BY t.createdTime DESC")
    List<EmailTemplate> findUnusedTemplates(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
    
    // Check if template name exists (for validation)
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM EmailTemplate t WHERE LOWER(t.templateName) = LOWER(:templateName) AND (:id IS NULL OR t.id != :id)")
    boolean existsByTemplateNameIgnoreCase(@Param("templateName") String templateName, @Param("id") Long id);
}