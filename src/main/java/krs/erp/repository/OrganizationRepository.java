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
    Optional<Organization> findByCodeAndIsActiveTrue(String code);
    
    /**
     * Find organization by name
     */
    Optional<Organization> findByNameAndIsActiveTrue(String name);
    
    /**
     * Find organizations by type
     */
    List<Organization> findByTypeAndIsActiveTrueOrderByNameAsc(String type);
    
    /**
     * Find all active organizations
     */
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
    List<Organization> findByCityAndIsActiveTrueOrderByNameAsc(String city);
    
    /**
     * Find organizations by country
     */
    List<Organization> findByCountryAndIsActiveTrueOrderByNameAsc(String country);
    
    /**
     * Check if organization code exists (for validation)
     */
    boolean existsByCodeAndIsActiveTrue(String code);
    
    /**
     * Check if organization name exists (for validation)
     */
    boolean existsByNameAndIsActiveTrue(String name);
    
    /**
     * Get organization types
     */
    @Query("SELECT DISTINCT o.type FROM Organization o WHERE o.isActive = 1 ORDER BY o.type")
    List<String> findDistinctTypes();
}