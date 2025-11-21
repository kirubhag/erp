package krs.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.service.CustomViewMigrationService;
import krs.erp.service.CustomViewMigrationService.CustomViewStats;
import krs.erp.service.CustomViewMigrationService.ValidationResult;

@RestController
@RequestMapping("/api/admin/custom-view-utils")
public class CustomViewMigrationController {
    
    @Autowired
    private CustomViewMigrationService migrationService;
    
    /**
     * Get statistics about custom views in the system
     */
    @GetMapping("/stats")
    public ResponseEntity<CustomViewStats> getCustomViewStats() {
        try {
            CustomViewStats stats = migrationService.getCustomViewStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Validate custom view integrity
     */
    @GetMapping("/validate")
    public ResponseEntity<ValidationResult> validateCustomViews() {
        try {
            ValidationResult result = migrationService.validateCustomViews();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}