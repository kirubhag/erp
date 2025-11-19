package krs.erp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * UserSettings Entity - Maps to user_settings table
 * Stores user-specific configuration preferences for theme, display, and behavior
 */
@Entity
@Table(name = "user_settings")
public class UserSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    
    @Column(name = "default_list_view", length = 20)
    private String defaultListView = "table";
    
    @Column(name = "records_per_page")
    private Integer recordsPerPage = 25;
    
    @Column(name = "theme", length = 50)
    private String theme = "#0099cc";
    
    @Column(name = "theme_primary_color", length = 50)
    private String themePrimaryColor = "#0099cc";
    
    @Column(name = "theme_secondary_color", length = 50)
    private String themeSecondaryColor;
    
    @Column(name = "theme_accent_color", length = 50)
    private String themeAccentColor;
    
    @Column(name = "theme_custom_colors", columnDefinition = "JSON")
    private String themeCustomColors;
    
    @Column(name = "grid_columns", columnDefinition = "JSON")
    private String gridColumns;
    
    @Column(name = "column_widths", columnDefinition = "JSON")
    private String columnWidths;
    
    @Column(name = "hidden_columns", columnDefinition = "JSON")
    private String hiddenColumns;
    
    @Column(name = "list_sidebar_expanded")
    private Boolean listSidebarExpanded = true;
    
    @Column(name = "saved_filters", columnDefinition = "JSON")
    private String savedFilters;
    
    @Column(name = "saved_views", columnDefinition = "JSON")
    private String savedViews;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Constructors
    public UserSettings() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public UserSettings(Long userId, Long organizationId) {
        this.userId = userId;
        this.organizationId = organizationId;
        this.lastUpdated = LocalDateTime.now();
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
    
    public Long getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    public String getDefaultListView() {
        return defaultListView;
    }
    
    public void setDefaultListView(String defaultListView) {
        this.defaultListView = defaultListView;
    }
    
    public Integer getRecordsPerPage() {
        return recordsPerPage;
    }
    
    public void setRecordsPerPage(Integer recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public String getThemePrimaryColor() {
        return themePrimaryColor;
    }
    
    public void setThemePrimaryColor(String themePrimaryColor) {
        this.themePrimaryColor = themePrimaryColor;
    }
    
    public String getThemeSecondaryColor() {
        return themeSecondaryColor;
    }
    
    public void setThemeSecondaryColor(String themeSecondaryColor) {
        this.themeSecondaryColor = themeSecondaryColor;
    }
    
    public String getThemeAccentColor() {
        return themeAccentColor;
    }
    
    public void setThemeAccentColor(String themeAccentColor) {
        this.themeAccentColor = themeAccentColor;
    }
    
    public String getThemeCustomColors() {
        return themeCustomColors;
    }
    
    public void setThemeCustomColors(String themeCustomColors) {
        this.themeCustomColors = themeCustomColors;
    }
    
    public String getGridColumns() {
        return gridColumns;
    }
    
    public void setGridColumns(String gridColumns) {
        this.gridColumns = gridColumns;
    }
    
    public String getColumnWidths() {
        return columnWidths;
    }
    
    public void setColumnWidths(String columnWidths) {
        this.columnWidths = columnWidths;
    }
    
    public String getHiddenColumns() {
        return hiddenColumns;
    }
    
    public void setHiddenColumns(String hiddenColumns) {
        this.hiddenColumns = hiddenColumns;
    }
    
    public Boolean getListSidebarExpanded() {
        return listSidebarExpanded;
    }
    
    public void setListSidebarExpanded(Boolean listSidebarExpanded) {
        this.listSidebarExpanded = listSidebarExpanded;
    }
    
    public String getSavedFilters() {
        return savedFilters;
    }
    
    public void setSavedFilters(String savedFilters) {
        this.savedFilters = savedFilters;
    }
    
    public String getSavedViews() {
        return savedViews;
    }
    
    public void setSavedViews(String savedViews) {
        this.savedViews = savedViews;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    @Override
    public String toString() {
        return "UserSettings{" +
                "id=" + id +
                ", userId=" + userId +
                ", organizationId=" + organizationId +
                ", defaultListView='" + defaultListView + '\'' +
                ", recordsPerPage=" + recordsPerPage +
                ", theme='" + theme + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", isActive=" + isActive +
                '}';
    }
}
