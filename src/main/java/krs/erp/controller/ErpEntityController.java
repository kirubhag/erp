package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.dto.SequenceUpdateDTO;
import krs.erp.model.ErpEntity;
import krs.erp.model.ErpEntityRoleRelation;
import krs.erp.model.Role;
import krs.erp.service.ErpEntityService;

/**
 * REST Controller for managing ERP entities and their role mappings.
 * Provides endpoints for entity CRUD operations and role-based access control.
 */
@RestController
@RequestMapping("/api/erp-entities")
public class ErpEntityController {

    @Autowired
    private ErpEntityService erpEntityService;

    /**
     * Get all ERP entities
     * GET /api/erp-entities
     */
    @GetMapping
    public ResponseEntity<List<ErpEntity>> getAllEntities(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<ErpEntity> entities = activeOnly ? 
                erpEntityService.getActiveEntities() : 
                erpEntityService.getAllEntities();
        return ResponseEntity.ok(entities);
    }

    /**
     * Get active menu items ordered by sequence
     * GET /api/erp-entities/menu-items
     */
    @GetMapping("/menu-items")
    public ResponseEntity<List<ErpEntity>> getMenuItems() {
        List<ErpEntity> menuItems = erpEntityService.getActiveMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get menu items accessible by specific roles
     * POST /api/erp-entities/menu-items/by-roles
     */
    @PostMapping("/menu-items/by-roles")
    public ResponseEntity<List<ErpEntity>> getMenuItemsByRoles(@RequestBody List<Long> roleIds) {
        List<ErpEntity> menuItems = erpEntityService.getActiveMenuItemsByRoles(roleIds);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Get entity by ID
     * GET /api/erp-entities/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ErpEntity> getEntityById(@PathVariable Long id) {
        return erpEntityService.getEntityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get entity by singular name
     * GET /api/erp-entities/by-name/{name}
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<ErpEntity> getEntityByName(@PathVariable String name) {
        return erpEntityService.getEntityBySingularName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new ERP entity
     * POST /api/erp-entities
     */
    @PostMapping
    public ResponseEntity<ErpEntity> createEntity(@RequestBody ErpEntity entity) {
        ErpEntity createdEntity = erpEntityService.createEntity(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntity);
    }

    /**
     * Update an existing ERP entity
     * PUT /api/erp-entities/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ErpEntity> updateEntity(
            @PathVariable Long id, 
            @RequestBody ErpEntity entity) {
        try {
            ErpEntity updatedEntity = erpEntityService.updateEntity(id, entity);
            return ResponseEntity.ok(updatedEntity);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update menu item sequences
     * PUT /api/erp-entities/update-sequence
     * 
     * Accepts an array of objects with {id, sequence} to reorder menu items
     */
    @PutMapping("/update-sequence")
    public ResponseEntity<Map<String, Object>> updateSequence(@RequestBody List<SequenceUpdateDTO> updates) {
        try {
            int updatedCount = 0;
            for (SequenceUpdateDTO update : updates) {
                ErpEntity entity = erpEntityService.getEntityById(update.getId())
                        .orElseThrow(() -> new RuntimeException("Entity not found: " + update.getId()));
                
                entity.setSequence(update.getSequence());
                erpEntityService.updateEntity(entity.getId(), entity);
                updatedCount++;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully updated " + updatedCount + " menu items");
            response.put("count", updatedCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update sequences: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Delete an ERP entity
     * DELETE /api/erp-entities/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable Long id) {
        erpEntityService.deleteEntity(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get entities accessible by a specific role
     * GET /api/erp-entities/by-role/{roleId}
     */
    @GetMapping("/by-role/{roleId}")
    public ResponseEntity<List<ErpEntity>> getEntitiesByRole(@PathVariable Long roleId) {
        List<ErpEntity> entities = erpEntityService.getEntitiesByRole(roleId);
        return ResponseEntity.ok(entities);
    }

    /**
     * Get entities accessible by multiple roles
     * POST /api/erp-entities/by-roles
     */
    @PostMapping("/by-roles")
    public ResponseEntity<List<ErpEntity>> getEntitiesByRoles(@RequestBody List<Long> roleIds) {
        List<ErpEntity> entities = erpEntityService.getEntitiesByRoles(roleIds);
        return ResponseEntity.ok(entities);
    }

    /**
     * Check if entity is accessible by role
     * GET /api/erp-entities/{entityId}/accessible/{roleId}
     */
    @GetMapping("/{entityId}/accessible/{roleId}")
    public ResponseEntity<Boolean> isEntityAccessible(
            @PathVariable Long entityId, 
            @PathVariable Long roleId) {
        boolean accessible = erpEntityService.isEntityAccessibleByRole(entityId, roleId);
        return ResponseEntity.ok(accessible);
    }

    /**
     * Add a role to an entity (grant access)
     * POST /api/erp-entities/{entityId}/roles
     */
    @PostMapping("/{entityId}/roles")
    public ResponseEntity<ErpEntityRoleRelation> addRoleToEntity(
            @PathVariable Long entityId,
            @RequestBody Role role) {
        try {
            ErpEntityRoleRelation relation = erpEntityService.addRoleToEntity(entityId, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(relation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remove a role from an entity (revoke access)
     * DELETE /api/erp-entities/{entityId}/roles/{roleId}
     */
    @DeleteMapping("/{entityId}/roles/{roleId}")
    public ResponseEntity<Void> removeRoleFromEntity(
            @PathVariable Long entityId,
            @PathVariable Long roleId) {
        erpEntityService.removeRoleFromEntity(entityId, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all role relations for an entity
     * GET /api/erp-entities/{entityId}/roles
     */
    @GetMapping("/{entityId}/roles")
    public ResponseEntity<List<Role>> getEntityRoles(@PathVariable Long entityId) {
        List<ErpEntityRoleRelation> relations = erpEntityService.getEntityRoleRelations(entityId);
        List<Role> roles = relations.stream()
                .map(ErpEntityRoleRelation::getRole)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    /**
     * Get all entity relations for a role
     * GET /api/erp-entities/roles/{roleId}/entities
     */
    @GetMapping("/roles/{roleId}/entities")
    public ResponseEntity<List<ErpEntity>> getRoleEntities(@PathVariable Long roleId) {
        List<ErpEntityRoleRelation> relations = erpEntityService.getRoleEntityRelations(roleId);
        List<ErpEntity> entities = relations.stream()
                .map(ErpEntityRoleRelation::getErpEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(entities);
    }
}
