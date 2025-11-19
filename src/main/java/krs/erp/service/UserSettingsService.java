package krs.erp.service;

import krs.erp.model.UserSettings;
import krs.erp.dto.UserSettingsDTO;
import java.util.Optional;

/**
 * UserSettingsService - Interface for user settings management
 */
public interface UserSettingsService {
    
    /**
     * Get user settings by userId and organizationId
     */
    Optional<UserSettings> getUserSettings(Long userId, Long organizationId);
    
    /**
     * Save or update user settings
     */
    UserSettings saveUserSettings(UserSettings userSettings);
    
    /**
     * Create user settings DTO from entity
     */
    UserSettingsDTO convertToDTO(UserSettings userSettings);
    
    /**
     * Update user theme preference
     */
    void updateThemePreference(Long userId, Long organizationId, String theme);
    
    /**
     * Update records per page preference
     */
    void updateListItemsPerPage(Long userId, Long organizationId, Integer itemsPerPage);
    
    /**
     * Update list view mode (table/card)
     */
    void updateListViewMode(Long userId, Long organizationId, String viewMode);
    
    /**
     * Update list sidebar expansion state
     */
    void updateListSidebarState(Long userId, Long organizationId, Boolean expanded);
    
    /**
     * Delete user settings
     */
    void deleteUserSettings(Long userId, Long organizationId);
    
    // Backward-compatible methods for controller
    
    /**
     * Get settings by userId only (assumes organizationId = 1)
     */
    UserSettingsDTO getSettingsByUserId(Long userId);
    
    /**
     * Save settings from DTO (userId extracted from DTO, organizationId = 1)
     */
    UserSettingsDTO saveSettings(UserSettingsDTO settingsDTO);
    
    /**
     * Update theme preference by userId only
     */
    UserSettingsDTO updateThemePreference(Long userId, String themePreference);
    
    /**
     * Update primary color by userId only
     */
    UserSettingsDTO updatePrimaryColor(Long userId, String primaryColor);
    
    /**
     * Update sidebar state by userId only
     */
    UserSettingsDTO updateListSidebarState(Long userId, Boolean expanded);
    
    /**
     * Update items per page by userId only
     */
    UserSettingsDTO updateListItemsPerPage(Long userId, Integer itemsPerPage);
    
    /**
     * Check if widget is expanded
     */
    boolean isWidgetExpanded(Long userId, String widgetKey);
    
    /**
     * Set widget expanded state
     */
    UserSettingsDTO setWidgetExpanded(Long userId, String widgetKey, Boolean expanded);
    
    /**
     * Toggle widget state
     */
    UserSettingsDTO toggleWidgetState(Long userId, String widgetKey);
    
    /**
     * Set custom preference
     */
    UserSettingsDTO setPreference(Long userId, String key, String value);
    
    /**
     * Get custom preference
     */
    String getPreference(Long userId, String key, String defaultValue);
    
    /**
     * Remove preference
     */
    UserSettingsDTO removePreference(Long userId, String key);
    
    /**
     * Delete settings by userId
     */
    void deleteSettings(Long userId);
    
    /**
     * Get valid items per page values
     */
    Integer[] getValidItemsPerPageValues();
    
    /**
     * Create default settings
     */
    UserSettingsDTO createDefaultSettings(Long userId);
}

