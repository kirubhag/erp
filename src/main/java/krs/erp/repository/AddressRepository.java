package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Address;
import krs.erp.model.Address.EntityType;

/**
 * Repository interface for Address entity
 * Provides methods to find addresses by entity type and entity ID
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * Find all addresses for a specific entity (Student, Parent, Staff, etc.)
     * 
     * @param entityType The type of entity (STUDENT, PARENT, STAFF, etc.)
     * @param entityId The ID of the entity
     * @return List of addresses for the entity
     */
    List<Address> findByEntityTypeAndEntityId(EntityType entityType, Long entityId);
    
    /**
     * Find the primary address for a specific entity
     * 
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     * @return Optional containing the primary address if found
     */
    @Query("SELECT a FROM Address a WHERE a.entityType = :entityType " +
           "AND a.entityId = :entityId AND a.isPrimary = true")
    Optional<Address> findPrimaryAddress(@Param("entityType") EntityType entityType, 
                                         @Param("entityId") Long entityId);
    
    /**
     * Find all addresses of a specific type for an entity
     * 
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     * @param addressType The type of address (RESIDENTIAL, MAILING, etc.)
     * @return List of addresses matching the criteria
     */
    @Query("SELECT a FROM Address a WHERE a.entityType = :entityType " +
           "AND a.entityId = :entityId AND a.addressType = :addressType")
    List<Address> findByEntityAndAddressType(@Param("entityType") EntityType entityType,
                                             @Param("entityId") Long entityId,
                                             @Param("addressType") Address.AddressType addressType);
    
    /**
     * Delete all addresses for a specific entity
     * 
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     */
    void deleteByEntityTypeAndEntityId(EntityType entityType, Long entityId);
    
    /**
     * Check if an entity has any addresses
     * 
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     * @return true if the entity has at least one address
     */
    boolean existsByEntityTypeAndEntityId(EntityType entityType, Long entityId);
}
