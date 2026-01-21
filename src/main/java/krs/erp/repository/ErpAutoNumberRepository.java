package krs.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import krs.erp.enums.EntityType;
import krs.erp.model.ErpAutoNumber;

/**
 * Repository for managing ErpAutoNumber entities.
 * Provides methods to retrieve and update auto-number sequences with proper locking.
 */
@Repository
public interface ErpAutoNumberRepository extends JpaRepository<ErpAutoNumber, Long> {

    /**
     * Find auto-number configuration by entity type and field name.
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @return Optional containing the auto-number config if found
     */
    Optional<ErpAutoNumber> findByEntityTypeAndFieldName(EntityType entityType, String fieldName);

    /**
     * Find auto-number configuration by entity type and field name with pessimistic write lock.
     * Use this for generating numbers to prevent race conditions.
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @return Optional containing the auto-number config if found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM ErpAutoNumber a WHERE a.entityType = :entityType AND a.fieldName = :fieldName")
    Optional<ErpAutoNumber> findByEntityTypeAndFieldNameWithLock(
            @Param("entityType") EntityType entityType, 
            @Param("fieldName") String fieldName);

    /**
     * Find all auto-number configurations for a specific entity type.
     * 
     * @param entityType The entity type
     * @return List of auto-number configurations
     */
    java.util.List<ErpAutoNumber> findByEntityType(EntityType entityType);

    /**
     * Check if an auto-number configuration exists for the given entity type and field name.
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @return true if configuration exists
     */
    boolean existsByEntityTypeAndFieldName(EntityType entityType, String fieldName);
}
