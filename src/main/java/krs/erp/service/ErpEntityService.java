package krs.erp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ErpEntityService {

    @Autowired
    private ErpEntityRepository erpEntityRepository;

    @Autowired
    private ErpEntityRoleRelationRepository relationRepository;

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
     * Get all active menu items ordered by sequence
     */
    @Transactional(readOnly = true)
    public List<ErpEntity> getActiveMenuItems() {
        return erpEntityRepository.findActiveMenuItems();
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
        entity.setCreatedBy("system");
        entity.setLastModifiedBy("system");
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
        entity.setLastModifiedBy("system");
        
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
        Optional<ErpEntityRoleRelation> existingRelation = 
                relationRepository.findByErpEntityIdAndRoleId(entityId, role.getId());
        
        if (existingRelation.isPresent()) {
            return existingRelation.get();
        }
        
        ErpEntityRoleRelation relation = new ErpEntityRoleRelation(entity, role);
        relation.setCreatedBy("system");
        relation.setLastModifiedBy("system");
        
        return relationRepository.save(relation);
    }

    /**
     * Remove a role from an entity (revoke access)
     */
    public void removeRoleFromEntity(Long entityId, Long roleId) {
        Optional<ErpEntityRoleRelation> relation = 
                relationRepository.findByErpEntityIdAndRoleId(entityId, roleId);
        
        relation.ifPresent(r -> relationRepository.delete(r));
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
