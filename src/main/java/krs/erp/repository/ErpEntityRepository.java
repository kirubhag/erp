package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpEntity;

/**
 * Repository interface for ErpEntity entity.
 * Provides data access methods for ERP entity/module management.
 */
@Repository
public interface ErpEntityRepository extends JpaRepository<ErpEntity, Long> {

       /**
        * Find an ERP entity by its singular name
        */
       Optional<ErpEntity> findBySingularName(String singularName);

       /**
        * Find all active ERP entities
        */
       List<ErpEntity> findByIsActiveTrue();

       /**
        * Find all ERP entities accessible by a specific role
        */
       @Query("SELECT DISTINCT e FROM ErpEntity e " +
                     "JOIN e.roleRelations r " +
                     "WHERE r.role.id = :roleId AND e.isActive = true")
       List<ErpEntity> findActiveEntitiesByRoleId(@Param("roleId") Long roleId);

       /**
        * Find all ERP entities accessible by a list of roles
        */
       @Query("SELECT DISTINCT e FROM ErpEntity e " +
                     "JOIN e.roleRelations r " +
                     "WHERE r.role.id IN :roleIds AND e.isActive = true " +
                     "ORDER BY e.singularName")
       List<ErpEntity> findActiveEntitiesByRoleIds(@Param("roleIds") List<Long> roleIds);

       /**
        * Check if an entity is accessible by a specific role
        */
       @Query("SELECT COUNT(e) > 0 FROM ErpEntity e " +
                     "JOIN e.roleRelations r " +
                     "WHERE e.id = :entityId AND r.role.id = :roleId AND e.isActive = true")
       boolean isEntityAccessibleByRole(@Param("entityId") Long entityId, @Param("roleId") Long roleId);

       /**
        * Find an ERP entity by its system name
        */
       Optional<ErpEntity> findBySystemName(String systemName);

       Optional<ErpEntity> findBySingularName(String singularName);

       /**
        * Find all active menu items with presence ordered by sequence
        */
       @Query("SELECT e FROM ErpEntity e " +
                     "WHERE e.isActive = true AND e.presence = true " +
                     "ORDER BY e.sequence ASC")
       List<ErpEntity> findActiveMenuItems();

       /**
        * Find active menu items accessible by role, ordered by sequence
        */
       @Query("SELECT DISTINCT e FROM ErpEntity e " +
                     "JOIN e.roleRelations r " +
                     "WHERE r.role.id IN :roleIds AND e.isActive = true AND e.presence = true " +
                     "ORDER BY e.sequence ASC")
       List<ErpEntity> findActiveMenuItemsByRoleIds(@Param("roleIds") List<Long> roleIds);

       /**
        * Find an ERP entity by table name
        */
       Optional<ErpEntity> findByTableName(String tableName);

       /**
        * Find all ERP entities that have related child tables
        */
       List<ErpEntity> findByHasRelTableTrue();
}
