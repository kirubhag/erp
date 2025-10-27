package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpEntityRoleRelation;

/**
 * Repository interface for ErpEntityRoleRelation entity.
 * Provides data access methods for managing entity-role mappings.
 */
@Repository
public interface ErpEntityRoleRelationRepository extends JpaRepository<ErpEntityRoleRelation, Long> {

    /**
     * Find all role relations for a specific entity
     */
    List<ErpEntityRoleRelation> findByErpEntityId(Long entityId);

    /**
     * Find all role relations for a specific role
     */
    List<ErpEntityRoleRelation> findByRoleId(Long roleId);

    /**
     * Find a specific entity-role relation
     */
    Optional<ErpEntityRoleRelation> findByErpEntityIdAndRoleId(Long entityId, Long roleId);

    /**
     * Delete all relations for a specific entity
     */
    void deleteByErpEntityId(Long entityId);

    /**
     * Delete all relations for a specific role
     */
    void deleteByRoleId(Long roleId);

    /**
     * Check if a relation exists between entity and role
     */
    boolean existsByErpEntityIdAndRoleId(Long entityId, Long roleId);

    /**
     * Count relations for a specific entity
     */
    long countByErpEntityId(Long entityId);

    /**
     * Count relations for a specific role
     */
    long countByRoleId(Long roleId);
}
