package krs.erp.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import krs.erp.model.UserSettings;

/**
 * UserSettingsDTO - Data Transfer Object for UserSettings
 * 
 * Used for:
 * - Returning settings to frontend
 * - Accepting settings updates from frontend
 * - Hiding sensitive information
 * - Serializing/Deserializing settings
 */
public class UserSettingsDTO {
    
    private Long id;
    
    private Long userId;
    
    /**
     * Theme Preference - LIGHT or DARK
     */
    private String themePreference;
    
    /**
     * Primary Color Hex Code
     */
    private String primaryColor;
    
    /**
     * List page sidebar state
     * true = expanded, false = collapsed
     */
    private Boolean listSidebarExpanded;
    
    /**
     * List page items per page count
     */
    private Integer listItemsPerPage;
    
    /**
     * Dashboard widget states
     */
    private Map<String, String> widgetStates;
    
    /**
     * Custom preferences for future extensibility
     */
    private Map<String, String> preferences;
    
    /**
     * Last updated timestamp
     */
    private LocalDateTime lastUpdated;
    
    // Constructors
    public UserSettingsDTO() {
        this.themePreference = "LIGHT";
        this.primaryColor = "#0099cc";
        this.listSidebarExpanded = true;
        this.listItemsPerPage = 50;
        this.widgetStates = new HashMap<>();
        this.preferences = new HashMap<>();
    }
    
    /**
     * Convert from entity to DTO
     * @param settings UserSettings entity
     */
    public UserSettingsDTO(UserSettings settings) {
        this();
        if (settings != null) {
            this.id = settings.getId();
            this.userId = settings.getUser() != null ? settings.getUser().getId() : null;
            this.themePreference = settings.getThemePreference().name();
            this.primaryColor = settings.getPrimaryColor();
            this.listSidebarExpanded = settings.getListSidebarExpanded();
            this.listItemsPerPage = settings.getListItemsPerPage();
            this.widgetStates = new HashMap<>(settings.getWidgetStates());
            this.preferences = new HashMap<>(settings.getPreferences());
            this.lastUpdated = settings.getLastUpdated();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getThemePreference() {
        return themePreference;
    }
    
    public void setThemePreference(String themePreference) {
        this.themePreference = themePreference;
    }
    
    public String getPrimaryColor() {
        return primaryColor;
    }
    
    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }
    
    public Boolean getListSidebarExpanded() {
        return listSidebarExpanded;
    }
    
    public void setListSidebarExpanded(Boolean listSidebarExpanded) {
        this.listSidebarExpanded = listSidebarExpanded;
    }
    
    public Integer getListItemsPerPage() {
        return listItemsPerPage;
    }
    
    public void setListItemsPerPage(Integer listItemsPerPage) {
        this.listItemsPerPage = listItemsPerPage;
    }
    
    public Map<String, String> getWidgetStates() {
        return widgetStates;
    }
    
    public void setWidgetStates(Map<String, String> widgetStates) {
        this.widgetStates = widgetStates != null ? widgetStates : new HashMap<>();
    }
    
    public Map<String, String> getPreferences() {
        return preferences;
    }
    
    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences != null ? preferences : new HashMap<>();
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    /**
     * Convert DTO back to entity
     * @return UserSettings entity
     */
    public UserSettings toEntity() {
        UserSettings settings = new UserSettings();
        settings.setId(this.id);
        if (this.themePreference != null) {
            try {
                settings.setThemePreference(UserSettings.ThemePreference.valueOf(this.themePreference));
            } catch (IllegalArgumentException e) {
                settings.setThemePreference(UserSettings.ThemePreference.LIGHT);
            }
        }
        settings.setPrimaryColor(this.primaryColor);
        settings.setListSidebarExpanded(this.listSidebarExpanded);
        settings.setListItemsPerPage(this.listItemsPerPage);
        settings.setWidgetStates(this.widgetStates);
        settings.setPreferences(this.preferences);
        settings.setLastUpdated(this.lastUpdated);
        return settings;
    }
}
