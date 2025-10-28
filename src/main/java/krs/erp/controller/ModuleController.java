package krs.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.dto.SequenceUpdateDTO;
import krs.erp.model.ErpEntity;
import krs.erp.service.ErpEntityService;

/**
 * REST Controller for module/menu management.
 * Provides endpoints for managing menu items and their ordering.
 */
@RestController
@RequestMapping("/api/module")
public class ModuleController {

    @Autowired
    private ErpEntityService erpEntityService;

    /**
     * Get active menu items ordered by sequence
     * GET /api/module/menu-items
     */
    @GetMapping("/menu-items")
    public ResponseEntity<List<ErpEntity>> getMenuItems() {
        List<ErpEntity> menuItems = erpEntityService.getActiveMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Update menu item sequences
     * PUT /api/module/update-sequence
     * 
     * Accepts an array of objects with {id, sequence} to reorder menu items
     * Note: Dashboard must always remain at sequence 1
     */
    @PutMapping("/update-sequence")
    public ResponseEntity<Map<String, Object>> updateSequence(@RequestBody List<SequenceUpdateDTO> updates) {
        try {
            // Validate that Dashboard is at sequence 1
            boolean dashboardFound = false;
            for (SequenceUpdateDTO update : updates) {
                ErpEntity entity = erpEntityService.getEntityById(update.getId())
                        .orElseThrow(() -> new RuntimeException("Entity not found: " + update.getId()));
                
                if ("Dashboard".equals(entity.getName())) {
                    dashboardFound = true;
                    if (update.getSequence() != 1) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("message", "Dashboard must always be at sequence 1");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                    }
                }
            }
            
            // Update sequences
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
}
