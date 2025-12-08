package krs.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.service.MigrationService;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    @Autowired
    private MigrationService migrationService;

    /**
     * Trigger upgrade for all tenant databases.
     * This endpoint should be secured and only accessible by admins.
     */
    @PostMapping("/upgrade-tenants")
    // @PreAuthorize("hasRole('ADMIN')") - Temporarily disabled for easier debugging
    // if needed
    public ResponseEntity<String> upgradeTenants() {
        String result = migrationService.upgradeAllTenants();
        return ResponseEntity.ok(result);
    }
}
