package krs.erp.service;

import java.util.Optional;

import krs.erp.dto.UserSettingsDTO;

/**
 * UserSettingsService - Business logic for user settings management
 * 
 * Responsibilities:
 * - CRUD operations for user settings
 * - Getting/setting specific preferences
 * - Widget state management
 * - Preference updates
 * - Validation and defaults
 */
public interface UserSettingsService {
    
    /**
     * Get settings for a user by user ID
     * Creates default settings if they don't exist
     * @param userId User ID
     * @return UserSettingsDTO
     */
    UserSettingsDTO getSettingsByUserId(Long userId);
    
    /**
     * Get settings optionally
     * @param userId User ID
     * @return Optional containing UserSettingsDTO
     */
    Optional<UserSettingsDTO> findSettingsByUserId(Long userId);
    
    /**
     * Save or update user settings
     * @param settings UserSettingsDTO
     * @return Updated UserSettingsDTO
     */
    UserSettingsDTO saveSettings(UserSettingsDTO settings);
    
    /**
     * Update theme preference
     * @param userId User ID
     * @param themePreference LIGHT or DARK
     * @return Updated UserSettingsDTO
     */
    UserSettingsDTO updateThemePreference(Long userId, String themePreference);
    
    /**
     * Update primary color
     * @param userId User ID
     * @param primaryColor Hex color code (e.g., #0099cc)
     * @return Updated UserSettingsDTO
     */
    UserSettingsDTO updatePrimaryColor(Long userId, String primaryColor);
    
    /**
     * Update list sidebar state
     * @param userId User ID
     * @param expanded true if expanded, false if collapsed
     * @return Updated UserSettingsDTO
     */
    UserSettingsDTO updateListSidebarState(Long userId, Boolean expanded);
    
    /**
     * Update list items per page
     * @param userId User ID
     * @param itemsPerPage Items per page count (25, 50, 100, 200)
     * @return Updated UserSettingsDTO
     */
    UserSettingsDTO updateListItemsPerPage(Long userId, Integer itemsPerPage);
    
    /**
     * Set widget expanded state
     * @param userId User ID
     * @param widgetKey Widget identifier
     * @param expanded true if expanded, false if collapsed
     * @return Updated UserSettingsDTO
     */
    UserSettingsDTO setWidgetExpanded(Long userId, String widgetKey, boolean expanded);
    
    /**
     * Get widget state
     * @param userId User ID
     * @param widgetKey Widget identifier
     * @return true if expanded, false if collapsed, true if not found (default)
     */
    boolean isWidgetExpanded(Long userId, String widgetKey);
    
    /**
     * Toggle widget state
     * @param userId User ID
     * @param widgetKey Widget identifier
     * @return Updated UserSettingsDTO
     */
    UserSettingsDTO toggleWidgetState(Long userId, String widgetKey);
    
    /**
     * Set custom preference
     * @param userId User ID
     * @param key Preference key
     * @param value Preference value
     * @return Updated UserSettingsDTO
     */
    UserSettingsDTO setPreference(Long userId, String key, String value);
    
    /**
     * Get custom preference
     * @param userId User ID
     * @param key Preference key
     * @param defaultValue Default value if not found
     * @return Preference value or default
     */
    String getPreference(Long userId, String key, String defaultValue);
    
    /**
     * Remove custom preference
     * @param userId User ID
     * @param key Preference key
     * @return Updated UserSettingsDTO
     */
    UserSettingsDTO removePreference(Long userId, String key);
    
    /**
     * Delete all settings for a user
     * @param userId User ID
     */
    void deleteSettings(Long userId);
    
    /**
     * Create default settings for a new user
     * @param userId User ID
     * @return Created UserSettingsDTO
     */
    UserSettingsDTO createDefaultSettings(Long userId);
    
    /**
     * Get valid items per page values
     * @return Array of valid values
     */
    Integer[] getValidItemsPerPageValues();
    
    /**
     * Validate theme preference
     * @param themePreference Theme preference string
     * @return true if valid
     */
    boolean isValidThemePreference(String themePreference);
    
    /**
     * Validate items per page value
     * @param itemsPerPage Items per page value
     * @return true if valid
     */
    boolean isValidItemsPerPage(Integer itemsPerPage);
}
