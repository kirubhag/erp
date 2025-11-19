package krs.erp.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.dto.UserSettingsDTO;
import krs.erp.model.UserSettings;
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
    
    // Valid items per page values matching frontend
    private static final Integer[] VALID_ITEMS_PER_PAGE = {25, 50, 100, 200};
    
    @Override
    public Optional<UserSettings> getUserSettings(Long userId, Long organizationId) {
        return userSettingsRepository.findByUserIdAndOrganizationId(userId, organizationId);
    }
    
    @Override
    public UserSettings saveUserSettings(UserSettings userSettings) {
        userSettings.setLastUpdated(LocalDateTime.now());
        return userSettingsRepository.save(userSettings);
    }
    
    @Override
    public UserSettingsDTO convertToDTO(UserSettings userSettings) {
        if (userSettings == null) {
            return null;
        }
        return new UserSettingsDTO(userSettings);
    }
    
    @Override
    public void updateThemePreference(Long userId, Long organizationId, String theme) {
        if (theme == null || theme.trim().isEmpty()) {
            return;
        }
        
        Optional<UserSettings> settings = getUserSettings(userId, organizationId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setTheme(theme);
            userSettings.setThemePrimaryColor(theme);
            userSettings.setLastUpdated(LocalDateTime.now());
            userSettingsRepository.save(userSettings);
        } else {
            // Create new settings if not exists
            UserSettings newSettings = new UserSettings(userId, organizationId);
            newSettings.setTheme(theme);
            newSettings.setThemePrimaryColor(theme);
            userSettingsRepository.save(newSettings);
        }
    }
    
    @Override
    public void updateListItemsPerPage(Long userId, Long organizationId, Integer itemsPerPage) {
        if (itemsPerPage == null || itemsPerPage <= 0) {
            return;
        }
        
        // Validate against allowed values
        boolean isValid = false;
        for (Integer valid : VALID_ITEMS_PER_PAGE) {
            if (valid.equals(itemsPerPage)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            itemsPerPage = 25; // Default fallback
        }
        
        Optional<UserSettings> settings = getUserSettings(userId, organizationId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setRecordsPerPage(itemsPerPage);
            userSettings.setLastUpdated(LocalDateTime.now());
            userSettingsRepository.save(userSettings);
        } else {
            // Create new settings if not exists
            UserSettings newSettings = new UserSettings(userId, organizationId);
            newSettings.setRecordsPerPage(itemsPerPage);
            userSettingsRepository.save(newSettings);
        }
    }
    
    @Override
    public void updateListViewMode(Long userId, Long organizationId, String viewMode) {
        if (viewMode == null || (!viewMode.equals("table") && !viewMode.equals("card"))) {
            return;
        }
        
        Optional<UserSettings> settings = getUserSettings(userId, organizationId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setDefaultListView(viewMode);
            userSettings.setLastUpdated(LocalDateTime.now());
            userSettingsRepository.save(userSettings);
        } else {
            // Create new settings if not exists
            UserSettings newSettings = new UserSettings(userId, organizationId);
            newSettings.setDefaultListView(viewMode);
            userSettingsRepository.save(newSettings);
        }
    }
    
    @Override
    public void updateListSidebarState(Long userId, Long organizationId, Boolean expanded) {
        if (expanded == null) {
            return;
        }
        
        Optional<UserSettings> settings = getUserSettings(userId, organizationId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setListSidebarExpanded(expanded);
            userSettings.setLastUpdated(LocalDateTime.now());
            userSettingsRepository.save(userSettings);
        } else {
            // Create new settings if not exists
            UserSettings newSettings = new UserSettings(userId, organizationId);
            newSettings.setListSidebarExpanded(expanded);
            userSettingsRepository.save(newSettings);
        }
    }
    
    @Override
    public void deleteUserSettings(Long userId, Long organizationId) {
        Optional<UserSettings> settings = getUserSettings(userId, organizationId);
        settings.ifPresent(userSettingsRepository::delete);
    }
    
    // Backward-compatible methods for controller
    
    @Override
    public UserSettingsDTO getSettingsByUserId(Long userId) {
        Optional<UserSettings> settings = getUserSettings(userId, 1L);
        if (settings.isPresent()) {
            return convertToDTO(settings.get());
        }
        // Return default DTO if not found
        return new UserSettingsDTO();
    }
    
    @Override
    public UserSettingsDTO saveSettings(UserSettingsDTO settingsDTO) {
        if (settingsDTO == null || settingsDTO.getUserId() == null) {
            return settingsDTO;
        }
        
        Long userId = settingsDTO.getUserId();
        Long organizationId = 1L; // Default organization
        
        Optional<UserSettings> existing = getUserSettings(userId, organizationId);
        UserSettings settings;
        
        if (existing.isPresent()) {
            settings = existing.get();
            // Update fields from DTO
            if (settingsDTO.getThemePreference() != null) {
                settings.setTheme(settingsDTO.getThemePreference());
            }
            if (settingsDTO.getPrimaryColor() != null) {
                settings.setThemePrimaryColor(settingsDTO.getPrimaryColor());
            }
            if (settingsDTO.getListSidebarExpanded() != null) {
                settings.setListSidebarExpanded(settingsDTO.getListSidebarExpanded());
            }
            if (settingsDTO.getListItemsPerPage() != null) {
                settings.setRecordsPerPage(settingsDTO.getListItemsPerPage());
            }
        } else {
            settings = new UserSettings(userId, organizationId);
            if (settingsDTO.getThemePreference() != null) {
                settings.setTheme(settingsDTO.getThemePreference());
            }
            if (settingsDTO.getPrimaryColor() != null) {
                settings.setThemePrimaryColor(settingsDTO.getPrimaryColor());
            }
            if (settingsDTO.getListSidebarExpanded() != null) {
                settings.setListSidebarExpanded(settingsDTO.getListSidebarExpanded());
            }
            if (settingsDTO.getListItemsPerPage() != null) {
                settings.setRecordsPerPage(settingsDTO.getListItemsPerPage());
            }
        }
        
        UserSettings saved = saveUserSettings(settings);
        return convertToDTO(saved);
    }
    
    @Override
    public UserSettingsDTO updateThemePreference(Long userId, String themePreference) {
        updateThemePreference(userId, 1L, themePreference);
        Optional<UserSettings> settings = getUserSettings(userId, 1L);
        return settings.map(this::convertToDTO).orElse(new UserSettingsDTO());
    }
    
    @Override
    public UserSettingsDTO updatePrimaryColor(Long userId, String primaryColor) {
        Long organizationId = 1L;
        if (primaryColor == null || primaryColor.trim().isEmpty()) {
            return getSettingsByUserId(userId);
        }
        
        Optional<UserSettings> settings = getUserSettings(userId, organizationId);
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setThemePrimaryColor(primaryColor);
            userSettings.setLastUpdated(LocalDateTime.now());
            userSettingsRepository.save(userSettings);
        } else {
            UserSettings newSettings = new UserSettings(userId, organizationId);
            newSettings.setThemePrimaryColor(primaryColor);
            userSettingsRepository.save(newSettings);
        }
        
        return getSettingsByUserId(userId);
    }
    
    @Override
    public UserSettingsDTO updateListSidebarState(Long userId, Boolean expanded) {
        updateListSidebarState(userId, 1L, expanded);
        Optional<UserSettings> settings = getUserSettings(userId, 1L);
        return settings.map(this::convertToDTO).orElse(new UserSettingsDTO());
    }
    
    @Override
    public UserSettingsDTO updateListItemsPerPage(Long userId, Integer itemsPerPage) {
        updateListItemsPerPage(userId, 1L, itemsPerPage);
        Optional<UserSettings> settings = getUserSettings(userId, 1L);
        return settings.map(this::convertToDTO).orElse(new UserSettingsDTO());
    }
    
    @Override
    public boolean isWidgetExpanded(Long userId, String widgetKey) {
        Optional<UserSettings> settings = getUserSettings(userId, 1L);
        return settings.isPresent(); // Simplified - assume expanded if exists
    }
    
    @Override
    public UserSettingsDTO setWidgetExpanded(Long userId, String widgetKey, Boolean expanded) {
        // Widget state management - simplified version
        Long organizationId = 1L;
        Optional<UserSettings> settings = getUserSettings(userId, organizationId);
        
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setLastUpdated(LocalDateTime.now());
            userSettingsRepository.save(userSettings);
        } else {
            UserSettings newSettings = new UserSettings(userId, organizationId);
            userSettingsRepository.save(newSettings);
        }
        
        return getSettingsByUserId(userId);
    }
    
    @Override
    public UserSettingsDTO toggleWidgetState(Long userId, String widgetKey) {
        // Toggle widget state - simplified version
        return setWidgetExpanded(userId, widgetKey, !isWidgetExpanded(userId, widgetKey));
    }
    
    @Override
    public UserSettingsDTO setPreference(Long userId, String key, String value) {
        // Custom preferences - simplified version
        Long organizationId = 1L;
        Optional<UserSettings> settings = getUserSettings(userId, organizationId);
        
        if (settings.isPresent()) {
            UserSettings userSettings = settings.get();
            userSettings.setLastUpdated(LocalDateTime.now());
            userSettingsRepository.save(userSettings);
        } else {
            UserSettings newSettings = new UserSettings(userId, organizationId);
            userSettingsRepository.save(newSettings);
        }
        
        return getSettingsByUserId(userId);
    }
    
    @Override
    public String getPreference(Long userId, String key, String defaultValue) {
        // Get custom preference - simplified version
        return defaultValue;
    }
    
    @Override
    public UserSettingsDTO removePreference(Long userId, String key) {
        // Remove preference - simplified version
        return getSettingsByUserId(userId);
    }
    
    @Override
    public void deleteSettings(Long userId) {
        deleteUserSettings(userId, 1L);
    }
    
    @Override
    public Integer[] getValidItemsPerPageValues() {
        return VALID_ITEMS_PER_PAGE;
    }
    
    @Override
    public UserSettingsDTO createDefaultSettings(Long userId) {
        Long organizationId = 1L;
        
        // Delete existing first
        deleteSettings(userId);
        
        // Create new default settings
        UserSettings newSettings = new UserSettings(userId, organizationId);
        newSettings.setTheme("LIGHT");
        newSettings.setThemePrimaryColor("#0099cc");
        newSettings.setDefaultListView("table");
        newSettings.setRecordsPerPage(50);
        newSettings.setListSidebarExpanded(true);
        newSettings.setIsActive(true);
        
        UserSettings saved = saveUserSettings(newSettings);
        return convertToDTO(saved);
    }
}

