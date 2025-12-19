package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
    /**
     * Find organization by code (unique identifier)
     */
    @Query("SELECT o FROM Organization o WHERE o.code = :code AND o.isActive = 1")
    Optional<Organization> findByCodeAndIsActiveTrue(@Param("code") String code);
    
    /**
     * Find organization by name (any status)
     */
    Optional<Organization> findByName(String name);
    
    /**
     * Find organization by name (active only)
     */
    @Query("SELECT o FROM Organization o WHERE o.name = :name AND o.isActive = 1")
    Optional<Organization> findByNameAndIsActiveTrue(@Param("name") String name);
    
    /**
     * Find organizations by type
     */
    @Query("SELECT o FROM Organization o WHERE o.type = :type AND o.isActive = 1 ORDER BY o.name ASC")
    List<Organization> findByTypeAndIsActiveTrueOrderByNameAsc(@Param("type") String type);
    
    /**
     * Find all active organizations
     */
    @Query("SELECT o FROM Organization o WHERE o.isActive = 1 ORDER BY o.name ASC")
    Page<Organization> findByIsActiveTrueOrderByNameAsc(Pageable pageable);
    
    /**
     * Search organizations by name or code
     */
    @Query("SELECT o FROM Organization o WHERE o.isActive = 1 AND " +
           "(LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Organization> searchByNameOrCode(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find organizations by city
     */
    @Query("SELECT o FROM Organization o WHERE o.city = :city AND o.isActive = 1 ORDER BY o.name ASC")
    List<Organization> findByCityAndIsActiveTrueOrderByNameAsc(@Param("city") String city);
    
    /**
     * Find organizations by country
     */
    @Query("SELECT o FROM Organization o WHERE o.country = :country AND o.isActive = 1 ORDER BY o.name ASC")
    List<Organization> findByCountryAndIsActiveTrueOrderByNameAsc(@Param("country") String country);
    
    /**
     * Check if organization code exists (for validation)
     */
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Organization o WHERE o.code = :code AND o.isActive = 1")
    boolean existsByCodeAndIsActiveTrue(@Param("code") String code);
    
    /**
     * Check if organization name exists (for validation)
     */
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Organization o WHERE o.name = :name AND o.isActive = 1")
    boolean existsByNameAndIsActiveTrue(@Param("name") String name);
    
    /**
     * Get organization types
     */
    @Query("SELECT DISTINCT o.type FROM Organization o WHERE o.isActive = 1 ORDER BY o.type")
    List<String> findDistinctTypes();
}