package krs.erp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    
    /**
     * Backward compatibility endpoint for PUT /api/user-settings/{userId}/{organizationId}
     * Updates user settings based on the provided partial settings map
     */
    @PutMapping("/{userId}/{organizationId}")
    public ResponseEntity<UserSettingsDTO> updateSettings(
            @PathVariable Long userId,
            @PathVariable Long organizationId,
            @RequestBody Map<String, Object> partialSettings) {
        
        // Get current settings
        UserSettingsDTO currentSettings = userSettingsService.getSettingsByUserId(userId);
        
        // Apply partial updates
        if (partialSettings.containsKey("defaultListView")) {
            currentSettings.setDefaultListView((String) partialSettings.get("defaultListView"));
        }
        if (partialSettings.containsKey("recordsPerPage")) {
            currentSettings.setRecordsPerPage(((Number) partialSettings.get("recordsPerPage")).intValue());
        }
        if (partialSettings.containsKey("listSidebarExpanded")) {
            currentSettings.setListSidebarExpanded((Boolean) partialSettings.get("listSidebarExpanded"));
        }
        
        // Save updated settings
        UserSettingsDTO updated = userSettingsService.saveSettings(currentSettings);
        return ResponseEntity.ok(updated);
    }
}
