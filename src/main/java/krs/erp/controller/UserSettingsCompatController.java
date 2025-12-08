package krs.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.dto.UserSettingsDTO;
import krs.erp.service.UserSettingsService;

/**
 * Backward compatibility controller for old user-settings API path
 * Redirects /api/user-settings to the new /api/v1/user-settings
 */
@RestController
@RequestMapping("/api/user-settings")
public class UserSettingsCompatController {
    
    @Autowired
    private UserSettingsService userSettingsService;
    
    /**
     * Backward compatibility endpoint for GET /api/user-settings/{userId}/{organizationId}
     */
    @GetMapping("/{userId}/{organizationId}")
    public ResponseEntity<UserSettingsDTO> getSettings(
            @PathVariable Long userId,
            @PathVariable Long organizationId) {
        UserSettingsDTO settings = userSettingsService.getSettingsByUserId(userId);
        return ResponseEntity.ok(settings);
    }
}
