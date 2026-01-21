package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import krs.erp.enums.EntityType;
import krs.erp.model.ErpAutoNumber;
import krs.erp.model.ErpField;

/**
 * Repository for managing ErpAutoNumber entities.
 * Provides methods to retrieve and update auto-number sequences with proper locking.
 */
@Repository
public interface ErpAutoNumberRepository extends JpaRepository<ErpAutoNumber, Long> {

    /**
     * Find auto-number configuration by the linked ErpField.
     * 
     * @param erpField The ErpField entity
     * @return Optional containing the auto-number config if found
     */
    Optional<ErpAutoNumber> findByErpField(ErpField erpField);

    /**
     * Find auto-number configuration by ErpField ID.
     * 
     * @param erpFieldId The ErpField ID
     * @return Optional containing the auto-number config if found
     */
    Optional<ErpAutoNumber> findByErpFieldId(Long erpFieldId);

    /**
     * Find auto-number configuration by entity type and field name (via ErpField relationship).
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @return Optional containing the auto-number config if found
     */
    @Query("SELECT a FROM ErpAutoNumber a WHERE a.erpField.entityType = :entityType AND a.erpField.fieldName = :fieldName")
    Optional<ErpAutoNumber> findByEntityTypeAndFieldName(
            @Param("entityType") EntityType entityType, 
            @Param("fieldName") String fieldName);

    /**
     * Find auto-number configuration by entity type and field name with pessimistic write lock.
     * Use this for generating numbers to prevent race conditions.
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @return Optional containing the auto-number config if found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM ErpAutoNumber a WHERE a.erpField.entityType = :entityType AND a.erpField.fieldName = :fieldName")
    Optional<ErpAutoNumber> findByEntityTypeAndFieldNameWithLock(
            @Param("entityType") EntityType entityType, 
            @Param("fieldName") String fieldName);

    /**
     * Find all auto-number configurations for a specific entity type.
     * 
     * @param entityType The entity type
     * @return List of auto-number configurations
     */
    @Query("SELECT a FROM ErpAutoNumber a WHERE a.erpField.entityType = :entityType")
    List<ErpAutoNumber> findByEntityType(@Param("entityType") EntityType entityType);

    /**
     * Check if an auto-number configuration exists for the given entity type and field name.
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @return true if configuration exists
     */
    @Query("SELECT COUNT(a) > 0 FROM ErpAutoNumber a WHERE a.erpField.entityType = :entityType AND a.erpField.fieldName = :fieldName")
    boolean existsByEntityTypeAndFieldName(
            @Param("entityType") EntityType entityType, 
            @Param("fieldName") String fieldName);

    /**
     * Check if an auto-number configuration exists for the given ErpField.
     * 
     * @param erpField The ErpField entity
     * @return true if configuration exists
     */
    boolean existsByErpField(ErpField erpField);

    /**
     * Check if an auto-number configuration exists for the given ErpField ID.
     * 
     * @param erpFieldId The ErpField ID
     * @return true if configuration exists
     */
    boolean existsByErpFieldId(Long erpFieldId);
}
