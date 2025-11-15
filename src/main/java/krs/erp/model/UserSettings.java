package krs.erp.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * UserSettings Entity - Stores user UI preferences and settings
 * 
 * This entity provides a scalable architecture for storing:
 * - Theme preferences (light/dark)
 * - UI state (sidebar collapse, widget states)
 * - Display preferences (items per page, etc.)
 * - Custom preferences (stored as key-value pairs)
 */
@Entity
@Table(name = "user_settings")
public class UserSettings extends BaseEntity {
    
    /**
     * One-to-One relationship with User
     * Cascade delete ensures settings are removed when user is deleted
     */
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    /**
     * Theme Preference - LIGHT or DARK
     * Default: LIGHT
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "theme_preference", nullable = false)
    private ThemePreference themePreference = ThemePreference.LIGHT;
    
    /**
     * Primary Color Code (Hex)
     * Stores the user's selected theme color
     * Default: Cyan (#0099cc)
     */
    @Column(name = "primary_color", length = 7)
    private String primaryColor = "#0099cc";
    
    /**
     * List Page Sidebar State
     * true = expanded, false = collapsed
     * Default: true (expanded)
     */
    @Column(name = "list_sidebar_expanded", nullable = false)
    private Boolean listSidebarExpanded = true;
    
    /**
     * List Page Items Per Page Count
     * Stores the user's preferred pagination size
     * Supported values: 25, 50, 100, 200
     * Default: 50
     */
    @Column(name = "list_items_per_page", nullable = false)
    private Integer listItemsPerPage = 50;
    
    /**
     * Dashboard Widget State
     * JSON-like storage for widget collapse/expand states
     * Stored as a flexible key-value map for scalability
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_settings_widget_states",
        joinColumns = @JoinColumn(name = "user_settings_id")
    )
    @MapKeyColumn(name = "widget_key")
    @Column(name = "widget_value")
    private Map<String, String> widgetStates = new HashMap<>();
    
    /**
     * Custom Preferences - Generic key-value storage
     * Allows for future extensibility without schema changes
     * Examples:
     * - "default_view_mode": "table"
     * - "sort_by_field": "name"
     * - "filter_defaults": "active_only"
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_settings_preferences",
        joinColumns = @JoinColumn(name = "user_settings_id")
    )
    @MapKeyColumn(name = "preference_key")
    @Column(name = "preference_value")
    private Map<String, String> preferences = new HashMap<>();
    
    /**
     * Last Updated Time
     * Tracks when settings were last modified
     */
    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    /**
     * Theme Preference Enum
     */
    public enum ThemePreference {
        LIGHT("Light Theme"),
        DARK("Dark Theme");
        
        private final String displayName;
        
        ThemePreference(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public UserSettings() {
        this.themePreference = ThemePreference.LIGHT;
        this.primaryColor = "#0099cc";
        this.listSidebarExpanded = true;
        this.listItemsPerPage = 50;
        this.widgetStates = new HashMap<>();
        this.preferences = new HashMap<>();
    }
    
    public UserSettings(User user) {
        this();
        this.user = user;
    }
    
    // Getters and Setters
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public ThemePreference getThemePreference() {
        return themePreference;
    }
    
    public void setThemePreference(ThemePreference themePreference) {
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
        // Validate against allowed values
        if (listItemsPerPage == null || !isValidItemsPerPage(listItemsPerPage)) {
            this.listItemsPerPage = 50; // Default
        } else {
            this.listItemsPerPage = listItemsPerPage;
        }
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
    
    // Widget State Methods
    /**
     * Get widget state (collapsed/expanded)
     * @param widgetKey Widget identifier
     * @return "true" if expanded, "false" if collapsed, "true" if not found (default expanded)
     */
    public String getWidgetState(String widgetKey) {
        return widgetStates.getOrDefault(widgetKey, "true");
    }
    
    /**
     * Check if widget is expanded
     * @param widgetKey Widget identifier
     * @return true if expanded, false if collapsed
     */
    public boolean isWidgetExpanded(String widgetKey) {
        return Boolean.parseBoolean(getWidgetState(widgetKey));
    }
    
    /**
     * Toggle widget state
     * @param widgetKey Widget identifier
     */
    public void toggleWidgetState(String widgetKey) {
        boolean current = isWidgetExpanded(widgetKey);
        widgetStates.put(widgetKey, String.valueOf(!current));
    }
    
    /**
     * Set widget expanded state
     * @param widgetKey Widget identifier
     * @param expanded true to expand, false to collapse
     */
    public void setWidgetExpanded(String widgetKey, boolean expanded) {
        widgetStates.put(widgetKey, String.valueOf(expanded));
    }
    
    // Preferences Methods
    /**
     * Get preference value
     * @param key Preference key
     * @param defaultValue Default value if key not found
     * @return Preference value or default
     */
    public String getPreference(String key, String defaultValue) {
        return preferences.getOrDefault(key, defaultValue);
    }
    
    /**
     * Get preference value
     * @param key Preference key
     * @return Preference value or null if not found
     */
    public String getPreference(String key) {
        return getPreference(key, null);
    }
    
    /**
     * Set preference
     * @param key Preference key
     * @param value Preference value
     */
    public void setPreference(String key, String value) {
        if (key != null) {
            preferences.put(key, value);
        }
    }
    
    /**
     * Remove preference
     * @param key Preference key
     */
    public void removePreference(String key) {
        preferences.remove(key);
    }
    
    // Validation Methods
    /**
     * Validate items per page value
     * @param itemsPerPage Items per page value
     * @return true if valid, false otherwise
     */
    private static boolean isValidItemsPerPage(Integer itemsPerPage) {
        return itemsPerPage != null && (itemsPerPage == 25 || itemsPerPage == 50 || 
               itemsPerPage == 100 || itemsPerPage == 200);
    }
    
    /**
     * Get list of valid items per page values
     * @return Array of valid values
     */
    public static Integer[] getValidItemsPerPageValues() {
        return new Integer[]{25, 50, 100, 200};
    }
}
