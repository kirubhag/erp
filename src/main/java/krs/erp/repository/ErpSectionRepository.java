package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.enums.EntityType;
import krs.erp.model.ErpSection;

@Repository
public interface ErpSectionRepository extends JpaRepository<ErpSection, Long> {

    /**
     * Find all active sections for a specific entity type, ordered by display order
     */
    @Query("SELECT es FROM ErpSection es WHERE es.entityType = :entityType AND es.isActive = 1 ORDER BY es.displayOrder ASC, es.sectionLabel ASC")
    List<ErpSection> findByEntityTypeAndIsActiveTrue(@Param("entityType") EntityType entityType);

    /**
     * Find all active sections for a specific entity type and organization
     */
    @Query("SELECT es FROM ErpSection es WHERE es.entityType = :entityType AND es.organizationId = :organizationId AND es.isActive = 1 ORDER BY es.displayOrder ASC")
    List<ErpSection> findByEntityTypeAndOrganizationIdAndIsActiveTrue(
            @Param("entityType") EntityType entityType,
            @Param("organizationId") Long organizationId);

    /**
     * Find section by entity type, section name, and organization
     */
    @Query("SELECT es FROM ErpSection es WHERE es.entityType = :entityType AND es.sectionName = :sectionName AND es.organizationId = :organizationId AND es.isActive = 1")
    Optional<ErpSection> findByEntityTypeAndSectionNameAndOrganizationId(
            @Param("entityType") EntityType entityType,
            @Param("sectionName") String sectionName,
            @Param("organizationId") Long organizationId);

    /**
     * Count active sections for an entity type
     */
    @Query("SELECT COUNT(es) FROM ErpSection es WHERE es.entityType = :entityType AND es.isActive = 1")
    Long countByEntityTypeAndIsActiveTrue(@Param("entityType") EntityType entityType);

    /**
     * Check if a section name exists for an entity type and organization
     */
    @Query("SELECT CASE WHEN COUNT(es) > 0 THEN true ELSE false END FROM ErpSection es WHERE es.entityType = :entityType AND es.sectionName = :sectionName AND es.organizationId = :organizationId AND es.isActive = 1")
    boolean existsByEntityTypeAndSectionNameAndOrganizationId(
            @Param("entityType") EntityType entityType,
            @Param("sectionName") String sectionName,
            @Param("organizationId") Long organizationId);

    /**
     * Find sections visible in create view
     */
    @Query("SELECT es FROM ErpSection es WHERE es.entityType = :entityType AND es.showInCreate = true AND es.isActive = 1 ORDER BY es.displayOrder ASC")
    List<ErpSection> findVisibleInCreate(@Param("entityType") EntityType entityType);

    /**
     * Find sections visible in edit view
     */
    @Query("SELECT es FROM ErpSection es WHERE es.entityType = :entityType AND es.showInEdit = true AND es.isActive = 1 ORDER BY es.displayOrder ASC")
    List<ErpSection> findVisibleInEdit(@Param("entityType") EntityType entityType);

    /**
     * Find sections visible in detail view
     */
    @Query("SELECT es FROM ErpSection es WHERE es.entityType = :entityType AND es.showInDetail = true AND es.isActive = 1 ORDER BY es.displayOrder ASC")
    List<ErpSection> findVisibleInDetail(@Param("entityType") EntityType entityType);

    /**
     * Check if a section exists by entity type and section name (for XML loading)
     */
    @Query("SELECT CASE WHEN COUNT(es) > 0 THEN true ELSE false END FROM ErpSection es WHERE es.entityType = :entityType AND es.sectionName = :sectionName AND es.isActive = 1")
    boolean existsByEntityTypeAndSectionNameAndIsActiveTrue(
            @Param("entityType") EntityType entityType,
            @Param("sectionName") String sectionName);
}
