package krs.erp.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.dto.UserSettingsDTO;
import krs.erp.model.User;
import krs.erp.model.UserSettings;
import krs.erp.repository.UserRepository;
import krs.erp.repository.UserSettingsRepository;
import krs.erp.service.UserSettingsService;

/**
 * UserSettingsServiceImpl - Implementation of UserSettingsService
 * 
 * Handles:
 * - CRUD operations for user settings
 * - Theme and preference updates
 * - Widget state management
 * - Validation and error handling
 */
@Service
@Transactional
public class UserSettingsServiceImpl implements UserSettingsService {
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Valid items per page values matching frontend
    private static final Integer[] VALID_ITEMS_PER_PAGE = {25, 50, 100, 200};
    
    @Override
    public UserSettingsDTO getSettingsByUserId(Long userId) {
        return findSettingsByUserId(userId)
            .orElseGet(() -> createDefaultSettings(userId));
    }
    
    @Override
    public Optional<UserSettingsDTO> findSettingsByUserId(Long userId) {
        return userSettingsRepository.findByUserId(userId)
            .map(UserSettingsDTO::new);
    }
    
    @Override
    public UserSettingsDTO saveSettings(UserSettingsDTO settingsDTO) {
        User user = userRepository.findById(settingsDTO.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + settingsDTO.getUserId()));
        
        UserSettings settings = settingsDTO.toEntity();
        settings.setUser(user);
        
        UserSettings saved = userSettingsRepository.save(settings);
        return new UserSettingsDTO(saved);
    }
    
    @Override
    public UserSettingsDTO updateThemePreference(Long userId, String themePreference) {
        if (!isValidThemePreference(themePreference)) {
            throw new IllegalArgumentException("Invalid theme preference: " + themePreference);
        }
        
        UserSettings settings = getUserSettingsOrCreate(userId);
        settings.setThemePreference(UserSettings.ThemePreference.valueOf(themePreference.toUpperCase()));
        
        UserSettings updated = userSettingsRepository.save(settings);
        return new UserSettingsDTO(updated);
    }
    
    @Override
    public UserSettingsDTO updatePrimaryColor(Long userId, String primaryColor) {
        if (!isValidHexColor(primaryColor)) {
            throw new IllegalArgumentException("Invalid hex color code: " + primaryColor);
        }
        
        UserSettings settings = getUserSettingsOrCreate(userId);
        settings.setPrimaryColor(primaryColor);
        
        UserSettings updated = userSettingsRepository.save(settings);
        return new UserSettingsDTO(updated);
    }
    
    @Override
    public UserSettingsDTO updateListSidebarState(Long userId, Boolean expanded) {
        UserSettings settings = getUserSettingsOrCreate(userId);
        settings.setListSidebarExpanded(expanded);
        
        UserSettings updated = userSettingsRepository.save(settings);
        return new UserSettingsDTO(updated);
    }
    
    @Override
    public UserSettingsDTO updateListItemsPerPage(Long userId, Integer itemsPerPage) {
        if (!isValidItemsPerPage(itemsPerPage)) {
            throw new IllegalArgumentException("Invalid items per page value: " + itemsPerPage + 
                ". Valid values are: 25, 50, 100, 200");
        }
        
        UserSettings settings = getUserSettingsOrCreate(userId);
        settings.setListItemsPerPage(itemsPerPage);
        
        UserSettings updated = userSettingsRepository.save(settings);
        return new UserSettingsDTO(updated);
    }
    
    @Override
    public UserSettingsDTO setWidgetExpanded(Long userId, String widgetKey, boolean expanded) {
        UserSettings settings = getUserSettingsOrCreate(userId);
        settings.setWidgetExpanded(widgetKey, expanded);
        
        UserSettings updated = userSettingsRepository.save(settings);
        return new UserSettingsDTO(updated);
    }
    
    @Override
    public boolean isWidgetExpanded(Long userId, String widgetKey) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            return settings.get().isWidgetExpanded(widgetKey);
        }
        
        // Default to expanded if no settings found
        return true;
    }
    
    @Override
    public UserSettingsDTO toggleWidgetState(Long userId, String widgetKey) {
        UserSettings settings = getUserSettingsOrCreate(userId);
        settings.toggleWidgetState(widgetKey);
        
        UserSettings updated = userSettingsRepository.save(settings);
        return new UserSettingsDTO(updated);
    }
    
    @Override
    public UserSettingsDTO setPreference(Long userId, String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Preference key cannot be empty");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Preference value cannot be empty");
        }
        
        UserSettings settings = getUserSettingsOrCreate(userId);
        settings.setPreference(key, value);
        
        UserSettings updated = userSettingsRepository.save(settings);
        return new UserSettingsDTO(updated);
    }
    
    @Override
    public String getPreference(Long userId, String key, String defaultValue) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            return settings.get().getPreference(key, defaultValue);
        }
        
        return defaultValue;
    }
    
    @Override
    public UserSettingsDTO removePreference(Long userId, String key) {
        UserSettings settings = getUserSettingsOrCreate(userId);
        settings.removePreference(key);
        
        UserSettings updated = userSettingsRepository.save(settings);
        return new UserSettingsDTO(updated);
    }
    
    @Override
    public void deleteSettings(Long userId) {
        userSettingsRepository.deleteByUserId(userId);
    }
    
    @Override
    public UserSettingsDTO createDefaultSettings(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        // Check if settings already exist
        if (userSettingsRepository.existsByUserId(userId)) {
            return new UserSettingsDTO(userSettingsRepository.findByUserId(userId).get());
        }
        
        UserSettings settings = new UserSettings();
        settings.setUser(user);
        settings.setThemePreference(UserSettings.ThemePreference.LIGHT);
        settings.setPrimaryColor("#0099cc");
        settings.setListSidebarExpanded(true);
        settings.setListItemsPerPage(50);
        
        UserSettings created = userSettingsRepository.save(settings);
        return new UserSettingsDTO(created);
    }
    
    @Override
    public Integer[] getValidItemsPerPageValues() {
        return VALID_ITEMS_PER_PAGE.clone();
    }
    
    @Override
    public boolean isValidThemePreference(String themePreference) {
        if (themePreference == null) {
            return false;
        }
        try {
            UserSettings.ThemePreference.valueOf(themePreference.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public boolean isValidItemsPerPage(Integer itemsPerPage) {
        if (itemsPerPage == null) {
            return false;
        }
        for (Integer valid : VALID_ITEMS_PER_PAGE) {
            if (valid.equals(itemsPerPage)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Helper method to get or create settings
     * @param userId User ID
     * @return UserSettings entity
     */
    private UserSettings getUserSettingsOrCreate(Long userId) {
        return userSettingsRepository.findByUserId(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
                
                UserSettings newSettings = new UserSettings();
                newSettings.setUser(user);
                newSettings.setThemePreference(UserSettings.ThemePreference.LIGHT);
                newSettings.setPrimaryColor("#0099cc");
                newSettings.setListSidebarExpanded(true);
                newSettings.setListItemsPerPage(50);
                
                return userSettingsRepository.save(newSettings);
            });
    }
    
    /**
     * Validate hex color code format
     * @param hexColor Hex color code
     * @return true if valid
     */
    private boolean isValidHexColor(String hexColor) {
        if (hexColor == null) {
            return false;
        }
        // Match #RRGGBB or #RRGGBBAA format
        return hexColor.matches("^#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");
    }
}
