package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;

@Repository
public interface CustomViewRepository extends JpaRepository<CustomView, Long> {
    
    /**
     * Find all custom views for a specific entity type
     */
    List<CustomView> findByEntityType(EntityType entityType);
    
    /**
     * Find all custom views for a specific entity type ordered by name
     */
    List<CustomView> findByEntityTypeOrderByViewNameAsc(EntityType entityType);
    
    /**
     * Find public custom views for a specific entity type
     */
    List<CustomView> findByEntityTypeAndIsPublicTrue(EntityType entityType);
    
    /**
     * Find custom views created by a specific user for an entity type
     */
    @Query("SELECT cv FROM CustomView cv WHERE cv.entityType = :entityType AND cv.createdBy = :userId")
    List<CustomView> findByEntityTypeAndCreatedBy(@Param("entityType") EntityType entityType, 
                                                  @Param("userId") Long userId);
    
    /**
     * Find default custom view for a specific entity type
     */
    Optional<CustomView> findByEntityTypeAndIsDefaultTrue(EntityType entityType);
    
    /**
     * Find custom view by name and entity type
     */
    Optional<CustomView> findByViewNameAndEntityType(String viewName, EntityType entityType);
    
    /**
     * Check if view name already exists for the entity type
     */
    boolean existsByViewNameAndEntityType(String viewName, EntityType entityType);
    
    /**
     * Find views accessible to a user (public views + user's own views)
     */
    @Query("SELECT cv FROM CustomView cv WHERE cv.entityType = :entityType " +
           "AND (cv.isPublic = true OR cv.createdBy = :userId)")
    List<CustomView> findAccessibleViews(@Param("entityType") EntityType entityType, 
                                        @Param("userId") Long userId);
    
    /**
     * Count views for a specific entity type
     */
    long countByEntityType(EntityType entityType);
    
    /**
     * Find recently created views for an entity type
     */
    @Query("SELECT cv FROM CustomView cv WHERE cv.entityType = :entityType " +
           "ORDER BY cv.createdTime DESC")
    List<CustomView> findRecentViews(@Param("entityType") EntityType entityType, 
                                    org.springframework.data.domain.Pageable pageable);
    
    /**
     * Find all default views
     */
    List<CustomView> findByIsDefaultTrue();
}