package krs.erp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.ErpEntity;
import krs.erp.model.ErpEntityRoleRelation;
import krs.erp.model.Role;
import krs.erp.repository.ErpEntityRepository;
import krs.erp.repository.ErpEntityRoleRelationRepository;

/**
 * Service class for managing ERP entities and their role mappings.
 * Handles business logic for entity visibility and access control.
 */
@Service
@Transactional
@SuppressWarnings("unused")
public class ErpEntityService {

    @Autowired
    private ErpEntityRepository erpEntityRepository;

    @Autowired
    private ErpEntityRoleRelationRepository relationRepository;

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    /**
     * Get all ERP entities
     */
    @Transactional(readOnly = true)
    public List<ErpEntity> getAllEntities() {
        return erpEntityRepository.findAll();
    }

    /**
     * Get all active ERP entities
     */
    @Transactional(readOnly = true)
    public List<ErpEntity> getActiveEntities() {
        return erpEntityRepository.findByIsActiveTrue();
    }

    /**
     * Get all active menu items ordered by sequence.
     * Falls back to master database if tenant database is empty.
     */
    @Transactional(readOnly = true)
    public List<ErpEntity> getActiveMenuItems() {
        List<ErpEntity> items = erpEntityRepository.findActiveMenuItems();

        // If no items found in tenant DB, fallback to master DB
        if (items == null || items.isEmpty()) {
            items = getMenuItemsFromMasterDb();
        }

        return items;
    }

    /**
     * Get menu items directly from master database (fallback)
     */
    private List<ErpEntity> getMenuItemsFromMasterDb() {
        try {
            JdbcTemplate masterJdbc = new JdbcTemplate(masterDataSource);
            String sql = "SELECT erp_entity_id, singular_name, plural_name, description, is_active, " +
                    "sequence, system_name, presence, icon, route, table_name, pkid, display_column, " +
                    "has_rel_table, created_date, last_modified_date, created_by, last_modified_by " +
                    "FROM erp_entities WHERE is_active = true AND presence = true ORDER BY sequence ASC";

            return masterJdbc.query(sql, (rs, rowNum) -> {
                ErpEntity entity = new ErpEntity();
                entity.setId(rs.getLong("erp_entity_id"));
                entity.setSingularName(rs.getString("singular_name"));
                entity.setPluralName(rs.getString("plural_name"));
                entity.setDescription(rs.getString("description"));
                entity.setIsActive(rs.getBoolean("is_active"));
                entity.setSequence(rs.getInt("sequence"));
                entity.setSystemName(rs.getString("system_name"));
                entity.setPresence(rs.getBoolean("presence"));
                entity.setIcon(rs.getString("icon"));
                entity.setRoute(rs.getString("route"));
                entity.setTableName(rs.getString("table_name"));
                entity.setPkid(rs.getString("pkid"));
                entity.setDisplayColumn(rs.getString("display_column"));
                entity.setHasRelTable(rs.getBoolean("has_rel_table"));
                return entity;
            });
        } catch (Exception e) {
            // Log error but don't fail - return empty list
            System.err.println("Failed to get menu items from master DB: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get active menu items accessible by specific roles
     */
    @Transactional(readOnly = true)
    public List<ErpEntity> getActiveMenuItemsByRoles(List<Long> roleIds) {
        return erpEntityRepository.findActiveMenuItemsByRoleIds(roleIds);
    }

    /**
     * Get entity by ID
     */
    @Transactional(readOnly = true)
    public Optional<ErpEntity> getEntityById(Long id) {
        return erpEntityRepository.findById(id);
    }

    /**
     * Get entity by singular name
     */
    @Transactional(readOnly = true)
    public Optional<ErpEntity> getEntityBySingularName(String singularName) {
        return erpEntityRepository.findBySingularName(singularName);
    }

    /**
     * Create a new ERP entity
     */
    public ErpEntity createEntity(ErpEntity entity) {
        entity.setCreatedBy(null); // System created - no specific user
        entity.setLastModifiedBy(null);
        return erpEntityRepository.save(entity);
    }

    /**
     * Update an existing ERP entity
     */
    public ErpEntity updateEntity(Long id, ErpEntity updatedEntity) {
        ErpEntity entity = erpEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found with id: " + id));

        entity.setSingularName(updatedEntity.getSingularName());
        entity.setPluralName(updatedEntity.getPluralName());
        entity.setDescription(updatedEntity.getDescription());
        entity.setIsActive(updatedEntity.getIsActive());
        entity.setSequence(updatedEntity.getSequence());
        entity.setSystemName(updatedEntity.getSystemName());
        entity.setPresence(updatedEntity.getPresence());
        entity.setIcon(updatedEntity.getIcon());
        entity.setRoute(updatedEntity.getRoute());
        entity.setLastModifiedBy(null); // System modified - no specific user

        return erpEntityRepository.save(entity);
    }

    /**
     * Delete an ERP entity
     */
    public void deleteEntity(Long id) {
        erpEntityRepository.deleteById(id);
    }

    /**
     * Get all entities accessible by a specific role
     */
    @Transactional(readOnly = true)
    public List<ErpEntity> getEntitiesByRole(Long roleId) {
        return erpEntityRepository.findActiveEntitiesByRoleId(roleId);
    }

    /**
     * Get all entities accessible by multiple roles
     */
    @Transactional(readOnly = true)
    public List<ErpEntity> getEntitiesByRoles(List<Long> roleIds) {
        return erpEntityRepository.findActiveEntitiesByRoleIds(roleIds);
    }

    /**
     * Check if an entity is accessible by a role
     */
    @Transactional(readOnly = true)
    public boolean isEntityAccessibleByRole(Long entityId, Long roleId) {
        return erpEntityRepository.isEntityAccessibleByRole(entityId, roleId);
    }

    /**
     * Add a role to an entity (grant access)
     */
    public ErpEntityRoleRelation addRoleToEntity(Long entityId, Role role) {
        ErpEntity entity = erpEntityRepository.findById(entityId)
                .orElseThrow(() -> new RuntimeException("Entity not found with id: " + entityId));

        // Check if relation already exists
        Optional<ErpEntityRoleRelation> existingRelation = relationRepository.findByErpEntityIdAndRoleId(entityId,
                role.getId());

        if (existingRelation.isPresent()) {
            return existingRelation.get();
        }

        ErpEntityRoleRelation relation = new ErpEntityRoleRelation(entity, role);
        relation.setCreatedBy(null); // System created - no specific user
        relation.setLastModifiedBy(null);

        return relationRepository.save(relation);
    }

    /**
     * Remove a role from an entity (revoke access)
     */
    public void removeRoleFromEntity(Long entityId, Long roleId) {
        Optional<ErpEntityRoleRelation> relation = relationRepository.findByErpEntityIdAndRoleId(entityId, roleId);

        relation.ifPresent(r -> relationRepository.delete(r));
    }

    @Autowired
    private krs.erp.repository.ErpTabGroupRepository tabGroupRepository;

    @Autowired
    private krs.erp.repository.ErpTabGroupEntityMappingRepository tabGroupMappingRepository;

    /**
     * Get all active Tab Groups with their mapped Entities
     */
    @Transactional(readOnly = true)
    public List<krs.erp.dto.TabGroupDTO> getActiveTabGroups() {
        List<krs.erp.model.ErpTabGroup> groups = tabGroupRepository.findByIsActiveOrderBySequenceAsc(1);
        List<krs.erp.dto.TabGroupDTO> result = new ArrayList<>();

        for (krs.erp.model.ErpTabGroup group : groups) {
            krs.erp.dto.TabGroupDTO dto = new krs.erp.dto.TabGroupDTO();
            dto.setId(group.getId());
            dto.setName(group.getName());
            dto.setCode(group.getCode());
            dto.setIcon(group.getIcon());
            dto.setRoutePath(group.getRoutePath());
            dto.setSequence(group.getSequence());
            dto.setDescription(group.getDescription());

            // Fetch mapped entities
            List<krs.erp.model.ErpTabGroupEntityMapping> mappings = tabGroupMappingRepository
                    .findByTabGroupIdOrderBySequenceAsc(group.getId());

            List<ErpEntity> entities = new ArrayList<>();
            for (krs.erp.model.ErpTabGroupEntityMapping mapping : mappings) {
                erpEntityRepository.findById(mapping.getEntityId()).ifPresent(entities::add);
            }
            dto.setEntities(entities);

            result.add(dto);
        }

        return result;
    }

    /**
     * Get all role relations for an entity
     */
    @Transactional(readOnly = true)
    public List<ErpEntityRoleRelation> getEntityRoleRelations(Long entityId) {
        return relationRepository.findByErpEntityId(entityId);
    }

    /**
     * Get all entity relations for a role
     */
    @Transactional(readOnly = true)
    public List<ErpEntityRoleRelation> getRoleEntityRelations(Long roleId) {
        return relationRepository.findByRoleId(roleId);
    }
}
