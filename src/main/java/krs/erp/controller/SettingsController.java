package krs.erp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingsController {
    
    // In-memory settings storage (for demo purposes)
    // In a real application, this would be stored in a database
    private Map<String, Object> userSettings = new HashMap<>();
    
    public SettingsController() {
        // Initialize with default settings
        initializeDefaultSettings();
    }
    
    /**
     * Get user settings
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserSettings() {
        return ResponseEntity.ok(userSettings);
    }
    
    /**
     * Update user settings
     */
    @PutMapping("/user")
    public ResponseEntity<Map<String, Object>> updateUserSettings(@RequestBody Map<String, Object> settings) {
        try {
            // Merge the new settings with existing ones
            userSettings.putAll(settings);
            
            return ResponseEntity.ok(userSettings);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update settings");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get system settings (read-only)
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemSettings() {
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("applicationName", "Student Information System");
        systemSettings.put("applicationVersion", "1.0.0");
        systemSettings.put("maxUploadSize", "10MB");
        systemSettings.put("sessionTimeout", 30);
        systemSettings.put("supportedLanguages", new String[]{"en", "es", "fr", "de", "it", "pt"});
        systemSettings.put("defaultLanguage", "en");
        systemSettings.put("defaultTheme", "light");
        systemSettings.put("defaultTimezone", "UTC");
        systemSettings.put("defaultCurrency", "USD");
        
        return ResponseEntity.ok(systemSettings);
    }
    
    /**
     * Reset user settings to defaults
     */
    @PutMapping("/user/reset")
    public ResponseEntity<Map<String, Object>> resetUserSettings() {
        initializeDefaultSettings();
        return ResponseEntity.ok(userSettings);
    }
    
    /**
     * Initialize default settings
     */
    private void initializeDefaultSettings() {
        userSettings.clear();
        
        // General settings
        userSettings.put("language", "en");
        userSettings.put("theme", "light");
        userSettings.put("timezone", "UTC");
        userSettings.put("dateFormat", "YYYY-MM-DD");
        userSettings.put("currency", "USD");
        
        // Notification settings
        Map<String, Object> notifications = new HashMap<>();
        notifications.put("email", true);
        notifications.put("sms", false);
        userSettings.put("notifications", notifications);
        
        // UI preferences
        userSettings.put("compactMode", false);
        userSettings.put("showAnimations", true);
        userSettings.put("itemsPerPage", 10);
        
        // Custom setting
        userSettings.put("customSetting", "");
    }
}