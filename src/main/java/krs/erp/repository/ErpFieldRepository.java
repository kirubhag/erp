package krs.erp.repository;

import java.util.List;

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
     * Find fields by entity type and category
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.fieldCategory = :category AND ef.isActive = 1 ORDER BY ef.displayOrder ASC")
    List<ErpField> findByEntityTypeAndFieldCategoryAndIsActiveTrue(@Param("entityType") EntityType entityType, @Param("category") String category);
    
    /**
     * Find searchable fields for a specific entity type
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.isSearchable = true AND ef.isActive = 1 ORDER BY ef.displayOrder ASC")
    List<ErpField> findSearchableFieldsByEntityType(@Param("entityType") EntityType entityType);
    
    /**
     * Find sortable fields for a specific entity type
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.isSortable = true AND ef.isActive = 1 ORDER BY ef.displayOrder ASC")
    List<ErpField> findSortableFieldsByEntityType(@Param("entityType") EntityType entityType);
    
    /**
     * Find field by entity type and field name
     */
    @Query("SELECT ef FROM ErpField ef WHERE ef.entityType = :entityType AND ef.fieldName = :fieldName AND ef.isActive = 1")
    ErpField findByEntityTypeAndFieldNameAndIsActiveTrue(@Param("entityType") EntityType entityType, @Param("fieldName") String fieldName);
    
    /**
     * Check if a field exists for an entity
     */
    boolean existsByEntityTypeAndFieldNameAndIsActiveTrue(EntityType entityType, String fieldName);
    
    /**
     * Get distinct field categories for an entity type
     */
    @Query("SELECT DISTINCT ef.fieldCategory FROM ErpField ef WHERE ef.entityType = :entityType AND ef.isActive = 1 AND ef.fieldCategory IS NOT NULL ORDER BY ef.fieldCategory")
    List<String> findDistinctFieldCategoriesByEntityType(@Param("entityType") EntityType entityType);
    
    /**
     * Count active fields for an entity type
     */
    @Query("SELECT COUNT(ef) FROM ErpField ef WHERE ef.entityType = :entityType AND ef.isActive = 1")
    Long countByEntityTypeAndIsActiveTrue(@Param("entityType") EntityType entityType);
}