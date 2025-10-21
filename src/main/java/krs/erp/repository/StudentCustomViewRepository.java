package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.StudentCustomView;

@Repository
public interface StudentCustomViewRepository extends JpaRepository<StudentCustomView, Long> {
    
    /**
     * Find all custom views by view name (case-insensitive)
     */
    List<StudentCustomView> findByViewNameContainingIgnoreCase(String viewName);
    
    /**
     * Find custom view by exact view name
     */
    Optional<StudentCustomView> findByViewName(String viewName);
    
    /**
     * Find all public custom views
     */
    @Query("SELECT cv FROM StudentCustomView cv WHERE cv.isPublic = true AND cv.isActive = true")
    List<StudentCustomView> findPublicViews();
    
    /**
     * Find custom views created by a specific user
     */
    @Query("SELECT cv FROM StudentCustomView cv WHERE cv.createdByUser = :username AND cv.isActive = true")
    List<StudentCustomView> findByCreatedByUser(@Param("username") String username);
    
    /**
     * Find custom views accessible to a user (public views + user's private views)
     */
    @Query("SELECT cv FROM StudentCustomView cv WHERE " +
           "(cv.isPublic = true OR cv.createdByUser = :username) AND cv.isActive = true " +
           "ORDER BY cv.isDefault DESC, cv.viewName ASC")
    List<StudentCustomView> findAccessibleViews(@Param("username") String username);
    
    /**
     * Find custom views accessible to a user with pagination
     */
    @Query("SELECT cv FROM StudentCustomView cv WHERE " +
           "(cv.isPublic = true OR cv.createdByUser = :username) AND cv.isActive = true")
    Page<StudentCustomView> findAccessibleViews(@Param("username") String username, Pageable pageable);
    
    /**
     * Find the default custom view
     */
    @Query("SELECT cv FROM StudentCustomView cv WHERE cv.isDefault = true AND cv.isActive = true")
    Optional<StudentCustomView> findDefaultView();
    
    /**
     * Find custom views containing a specific field
     */
    @Query("SELECT DISTINCT cv FROM StudentCustomView cv JOIN cv.selectedFields f WHERE f = :fieldName AND cv.isActive = true")
    List<StudentCustomView> findViewsContainingField(@Param("fieldName") String fieldName);
    
    /**
     * Check if a view name already exists (case-insensitive)
     */
    @Query("SELECT COUNT(cv) > 0 FROM StudentCustomView cv WHERE LOWER(cv.viewName) = LOWER(:viewName)")
    boolean existsByViewNameIgnoreCase(@Param("viewName") String viewName);
    
    /**
     * Check if a view name already exists for update operation (excluding current view)
     */
    @Query("SELECT COUNT(cv) > 0 FROM StudentCustomView cv WHERE LOWER(cv.viewName) = LOWER(:viewName) AND cv.id != :id")
    boolean existsByViewNameIgnoreCaseAndIdNot(@Param("viewName") String viewName, @Param("id") Long id);
    
    /**
     * Set all views as non-default
     */
    @Modifying
    @Query("UPDATE StudentCustomView cv SET cv.isDefault = false")
    void unsetAllDefaultViews();
    
    /**
     * Set a specific view as default
     */
    @Modifying
    @Query("UPDATE StudentCustomView cv SET cv.isDefault = true WHERE cv.id = :viewId")
    void setAsDefault(@Param("viewId") Long viewId);
    
    /**
     * Count active custom views
     */
    @Query("SELECT COUNT(cv) FROM StudentCustomView cv WHERE cv.isActive = true")
    long countActiveViews();
    
    /**
     * Count custom views created by a specific user
     */
    @Query("SELECT COUNT(cv) FROM StudentCustomView cv WHERE cv.createdByUser = :username AND cv.isActive = true")
    long countByCreatedByUser(@Param("username") String username);
    
    /**
     * Find custom views by multiple criteria
     */
    @Query("SELECT cv FROM StudentCustomView cv WHERE " +
           "(:viewName IS NULL OR LOWER(cv.viewName) LIKE LOWER(CONCAT('%', :viewName, '%'))) AND " +
           "(:isPublic IS NULL OR cv.isPublic = :isPublic) AND " +
           "(:username IS NULL OR cv.createdByUser = :username) AND " +
           "cv.isActive = true")
    Page<StudentCustomView> findByCriteria(@Param("viewName") String viewName, 
                                          @Param("isPublic") Boolean isPublic, 
                                          @Param("username") String username, 
                                          Pageable pageable);
}