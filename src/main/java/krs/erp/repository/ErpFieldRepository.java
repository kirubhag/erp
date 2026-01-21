package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.enums.EntityType;
import krs.erp.model.ErpField;

@Repository
public interface ErpFieldRepository extends JpaRepository<ErpField, Long> {

    /**
     * Find all active fields for a specific entity type
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.isActive = 1 ORDER BY ef.displayOrder ASC, ef.fieldLabel ASC")
    List<ErpField> findByEntityTypeAndIsActiveTrue(@Param("entityType") EntityType entityType);

    /**
     * Find all active fields for a specific entity type with pagination
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.isActive = 1 ORDER BY ef.displayOrder ASC, ef.fieldLabel ASC")
    Page<ErpField> findByEntityTypeAndIsActiveTrue(@Param("entityType") EntityType entityType, Pageable pageable);

    /**
     * Find fields by section
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.section.id = :sectionId AND ef.isActive = 1 ORDER BY ef.rowPosition, ef.columnPosition")
    List<ErpField> findBySectionIdAndIsActiveTrue(@Param("sectionId") Long sectionId);

    /**
     * Find searchable fields for a specific entity type
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.isSearchable = true AND ef.isActive = 1 ORDER BY ef.displayOrder ASC")
    List<ErpField> findSearchableFieldsByEntityType(@Param("entityType") EntityType entityType);

    /**
     * Find field by entity type and field name (returns Optional)
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.fieldName = :fieldName AND ef.isActive = 1")
    Optional<ErpField> findByEntityTypeAndFieldName(@Param("entityType") EntityType entityType,
            @Param("fieldName") String fieldName);

    /**
     * Find sortable fields for a specific entity type
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.isSortable = true AND ef.isActive = 1 ORDER BY ef.displayOrder ASC")
    List<ErpField> findSortableFieldsByEntityType(@Param("entityType") EntityType entityType);

    /**
     * Find field by entity type and field name
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.fieldName = :fieldName AND ef.isActive = 1")
    ErpField findByEntityTypeAndFieldNameAndIsActiveTrue(@Param("entityType") EntityType entityType,
            @Param("fieldName") String fieldName);

    /**
     * Check if a field exists for an entity
     */
    @Query("SELECT CASE WHEN COUNT(ef) > 0 THEN true ELSE false END FROM ErpField ef WHERE ef.entityType = :entityType AND ef.fieldName = :fieldName AND ef.isActive = 1")
    boolean existsByEntityTypeAndFieldNameAndIsActiveTrue(@Param("entityType") EntityType entityType,
            @Param("fieldName") String fieldName);

    /**
     * Count active fields for an entity type
     */
    @Query("SELECT COUNT(ef) FROM ErpField ef WHERE ef.entityType = :entityType AND ef.isActive = 1")
    Long countByEntityTypeAndIsActiveTrue(@Param("entityType") EntityType entityType);

    /**
     * Find fields by entity type and UI type
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.uiType = :uiType AND ef.isActive = 1 ORDER BY ef.displayOrder ASC")
    List<ErpField> findByEntityTypeAndUiTypeAndIsActiveTrue(@Param("entityType") EntityType entityType,
            @Param("uiType") int uiType);
}