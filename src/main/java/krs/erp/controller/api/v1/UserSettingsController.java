package krs.erp.controller.api.v1;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.dto.UserSettingsDTO;
import krs.erp.service.UserSettingsService;

/**
 * UserSettingsController - REST API for user settings management
 * 
 * Endpoints:
 * GET /api/v1/user-settings - Get current user settings
 * PUT /api/v1/user-settings - Update settings
 * PUT /api/v1/user-settings/theme - Update theme preference
 * PUT /api/v1/user-settings/color - Update primary color
 * PUT /api/v1/user-settings/sidebar - Toggle sidebar state
 * PUT /api/v1/user-settings/items-per-page - Update items per page
 * GET /api/v1/user-settings/widget/{widgetKey} - Get widget state
 * PUT /api/v1/user-settings/widget/{widgetKey} - Update widget state
 * DELETE /api/v1/user-settings - Delete user settings
 * GET /api/v1/user-settings/valid-values/items-per-page - Get valid items per page values
 */
@RestController
@RequestMapping("/api/v1/user-settings")
public class UserSettingsController {
    
    @Autowired
    private UserSettingsService userSettingsService;
    
    /**
     * Get current user settings
     * @param authentication Current user authentication
     * @return UserSettingsDTO
     */
    @GetMapping
    public ResponseEntity<UserSettingsDTO> getSettings(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        UserSettingsDTO settings = userSettingsService.getSettingsByUserId(userId);
        return ResponseEntity.ok(settings);
    }
    
    /**
     * Update entire user settings
     * @param settingsDTO Settings to update
     * @param authentication Current user authentication
     * @return Updated UserSettingsDTO
     */
    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateSettings(
            @RequestBody UserSettingsDTO settingsDTO,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        // Ensure userId matches current user
        settingsDTO.setUserId(userId);
        
        UserSettingsDTO updated = userSettingsService.saveSettings(settingsDTO);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Update theme preference only
     * @param request Map containing "themePreference" key with LIGHT or DARK value
     * @param authentication Current user authentication
     * @return Updated UserSettingsDTO
     */
    @PutMapping("/theme")
    public ResponseEntity<UserSettingsDTO> updateTheme(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        String themePreference = request.get("themePreference");
        if (themePreference == null || themePreference.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        UserSettingsDTO updated = userSettingsService.updateThemePreference(userId, themePreference);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Update primary color only
     * @param request Map containing "primaryColor" key with hex color value
     * @param authentication Current user authentication
     * @return Updated UserSettingsDTO
     */
    @PutMapping("/color")
    public ResponseEntity<UserSettingsDTO> updateColor(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        String primaryColor = request.get("primaryColor");
        if (primaryColor == null || primaryColor.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        UserSettingsDTO updated = userSettingsService.updatePrimaryColor(userId, primaryColor);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Update list sidebar expanded state
     * @param request Map containing "expanded" key with boolean value
     * @param authentication Current user authentication
     * @return Updated UserSettingsDTO
     */
    @PutMapping("/sidebar")
    public ResponseEntity<UserSettingsDTO> updateSidebarState(
            @RequestBody Map<String, Boolean> request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        Boolean expanded = request.get("expanded");
        if (expanded == null) {
            return ResponseEntity.badRequest().build();
        }
        
        UserSettingsDTO updated = userSettingsService.updateListSidebarState(userId, expanded);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Update list items per page
     * @param request Map containing "itemsPerPage" key with integer value
     * @param authentication Current user authentication
     * @return Updated UserSettingsDTO
     */
    @PutMapping("/items-per-page")
    public ResponseEntity<UserSettingsDTO> updateItemsPerPage(
            @RequestBody Map<String, Integer> request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        Integer itemsPerPage = request.get("itemsPerPage");
        if (itemsPerPage == null) {
            return ResponseEntity.badRequest().build();
        }
        
        UserSettingsDTO updated = userSettingsService.updateListItemsPerPage(userId, itemsPerPage);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Get widget expanded state
     * @param widgetKey Widget identifier
     * @param authentication Current user authentication
     * @return Map with widget key and expanded boolean
     */
    @GetMapping("/widget/{widgetKey}")
    public ResponseEntity<Map<String, Object>> getWidgetState(
            @PathVariable String widgetKey,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        boolean expanded = userSettingsService.isWidgetExpanded(userId, widgetKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("widgetKey", widgetKey);
        response.put("expanded", expanded);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update widget expanded state
     * @param widgetKey Widget identifier
     * @param request Map containing "expanded" key with boolean value
     * @param authentication Current user authentication
     * @return Updated UserSettingsDTO
     */
    @PutMapping("/widget/{widgetKey}")
    public ResponseEntity<UserSettingsDTO> updateWidgetState(
            @PathVariable String widgetKey,
            @RequestBody Map<String, Boolean> request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        Boolean expanded = request.get("expanded");
        if (expanded == null) {
            return ResponseEntity.badRequest().build();
        }
        
        UserSettingsDTO updated = userSettingsService.setWidgetExpanded(userId, widgetKey, expanded);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Toggle widget expanded state
     * @param widgetKey Widget identifier
     * @param authentication Current user authentication
     * @return Updated UserSettingsDTO
     */
    @PutMapping("/widget/{widgetKey}/toggle")
    public ResponseEntity<UserSettingsDTO> toggleWidgetState(
            @PathVariable String widgetKey,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        UserSettingsDTO updated = userSettingsService.toggleWidgetState(userId, widgetKey);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Set custom preference
     * @param request Map containing "key" and "value"
     * @param authentication Current user authentication
     * @return Updated UserSettingsDTO
     */
    @PostMapping("/preference")
    public ResponseEntity<UserSettingsDTO> setPreference(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        String key = request.get("key");
        String value = request.get("value");
        
        if (key == null || key.trim().isEmpty() || 
            value == null || value.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        UserSettingsDTO updated = userSettingsService.setPreference(userId, key, value);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Get custom preference
     * @param key Preference key
     * @param authentication Current user authentication
     * @return Map with preference value or null if not found
     */
    @GetMapping("/preference/{key}")
    public ResponseEntity<Map<String, String>> getPreference(
            @PathVariable String key,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        String value = userSettingsService.getPreference(userId, key, null);
        
        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Remove custom preference
     * @param key Preference key
     * @param authentication Current user authentication
     * @return Updated UserSettingsDTO
     */
    @DeleteMapping("/preference/{key}")
    public ResponseEntity<UserSettingsDTO> removePreference(
            @PathVariable String key,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        UserSettingsDTO updated = userSettingsService.removePreference(userId, key);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Delete user settings
     * @param authentication Current user authentication
     * @return No content response
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteSettings(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        userSettingsService.deleteSettings(userId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get valid items per page values
     * @return Array of valid values
     */
    @GetMapping("/valid-values/items-per-page")
    public ResponseEntity<Map<String, Object>> getValidItemsPerPageValues() {
        
        Integer[] validValues = userSettingsService.getValidItemsPerPageValues();
        
        Map<String, Object> response = new HashMap<>();
        response.put("validValues", validValues);
        response.put("default", 50);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get valid theme preferences
     * @return List of valid theme preferences
     */
    @GetMapping("/valid-values/themes")
    public ResponseEntity<Map<String, Object>> getValidThemes() {
        
        Map<String, Object> response = new HashMap<>();
        response.put("themes", new String[]{"LIGHT", "DARK"});
        response.put("default", "LIGHT");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reset settings to defaults
     * @param authentication Current user authentication
     * @return Restored UserSettingsDTO
     */
    @PostMapping("/reset")
    public ResponseEntity<UserSettingsDTO> resetSettings(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        // Delete existing and create new defaults
        userSettingsService.deleteSettings(userId);
        UserSettingsDTO restored = userSettingsService.createDefaultSettings(userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(restored);
    }
    
    /**
     * Helper method to extract current user ID from authentication
     * @param authentication Spring Authentication object
     * @return User ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        // This assumes your authentication principal is a custom user object
        // Adjust based on your actual authentication implementation
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("User not authenticated");
        }
        
        // If using UserDetails or custom user object with getId()
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // You may need to retrieve the User entity or store userId in principal
            // This is a placeholder - adjust based on your security configuration
            throw new IllegalStateException("Extract user ID from authentication principal");
        }
        
        throw new IllegalStateException("Cannot extract user ID from authentication");
    }
}
