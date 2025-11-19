package krs.erp.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * OrganizationSettings Entity - Stores organization-wide configuration
 */
@Entity
@Table(name = "organization_settings")
public class OrganizationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false, unique = true)
    private Long organizationId;

    // Theme and Display Settings
    @Column(name = "default_theme", length = 50)
    private String defaultTheme;

    @Column(name = "theme_primary_color", length = 7)
    private String themePrimaryColor;

    @Column(name = "theme_secondary_color", length = 7)
    private String themeSecondaryColor;

    @Column(name = "theme_accent_color", length = 7)
    private String themeAccentColor;

    // Localization Settings
    @Column(name = "default_language", length = 10)
    private String defaultLanguage;

    @Column(name = "default_timezone", length = 50)
    private String defaultTimezone;

    @Column(name = "default_date_format", length = 20)
    private String defaultDateFormat;

    @Column(name = "default_time_format", length = 20)
    private String defaultTimeFormat;

    @Column(name = "default_currency", length = 3)
    private String defaultCurrency;

    // UI Behavior Settings
    @Column(name = "items_per_page")
    private Integer itemsPerPage;

    @Column(name = "default_list_view", length = 20)
    private String defaultListView;

    @Column(name = "enable_smart_filters")
    private Boolean enableSmartFilters;

    @Column(name = "enable_column_customization")
    private Boolean enableColumnCustomization;

    @Column(name = "enable_bulk_operations")
    private Boolean enableBulkOperations;

    // Data Settings
    @Column(name = "enable_audit_logging")
    private Boolean enableAuditLogging;

    @Column(name = "data_retention_days")
    private Integer dataRetentionDays;

    @Column(name = "max_file_upload_mb")
    private Integer maxFileUploadMb;

    // Email Settings
    @Column(name = "smtp_server", length = 255)
    private String smtpServer;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "smtp_username", length = 100)
    private String smtpUsername;

    @Column(name = "smtp_password", length = 255)
    private String smtpPassword;

    @Column(name = "smtp_from_email", length = 100)
    private String smtpFromEmail;

    @Column(name = "smtp_from_name", length = 100)
    private String smtpFromName;

    @Column(name = "email_templates_enabled")
    private Boolean emailTemplatesEnabled;

    // System Settings
    @Column(name = "enable_two_factor_auth")
    private Boolean enableTwoFactorAuth;

    @Column(name = "session_timeout_minutes")
    private Integer sessionTimeoutMinutes;

    @Column(name = "password_expiry_days")
    private Integer passwordExpiryDays;

    @Column(name = "min_password_length")
    private Integer minPasswordLength;

    @Column(name = "require_special_characters")
    private Boolean requireSpecialCharacters;

    // Notification Settings
    @Column(name = "enable_email_notifications")
    private Boolean enableEmailNotifications;

    @Column(name = "enable_sms_notifications")
    private Boolean enableSmsNotifications;

    @Column(name = "enable_in_app_notifications")
    private Boolean enableInAppNotifications;

    @Column(name = "notification_sound_enabled")
    private Boolean notificationSoundEnabled;

    // API and Integration Settings
    @Column(name = "api_rate_limit_per_minute")
    private Integer apiRateLimitPerMinute;

    @Column(name = "enable_api_documentation")
    private Boolean enableApiDocumentation;

    @Column(name = "enable_webhooks")
    private Boolean enableWebhooks;

    // Metadata
    @Column(name = "created_by", length = 100, nullable = false)
    private String createdBy;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    @Column(name = "is_active")
    private Boolean isActive;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Organization organization;

    // Constructors
    public OrganizationSettings() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public String getDefaultTheme() { return defaultTheme; }
    public void setDefaultTheme(String defaultTheme) { this.defaultTheme = defaultTheme; }

    public String getThemePrimaryColor() { return themePrimaryColor; }
    public void setThemePrimaryColor(String themePrimaryColor) { this.themePrimaryColor = themePrimaryColor; }

    public String getThemeSecondaryColor() { return themeSecondaryColor; }
    public void setThemeSecondaryColor(String themeSecondaryColor) { this.themeSecondaryColor = themeSecondaryColor; }

    public String getThemeAccentColor() { return themeAccentColor; }
    public void setThemeAccentColor(String themeAccentColor) { this.themeAccentColor = themeAccentColor; }

    public String getDefaultLanguage() { return defaultLanguage; }
    public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }

    public String getDefaultTimezone() { return defaultTimezone; }
    public void setDefaultTimezone(String defaultTimezone) { this.defaultTimezone = defaultTimezone; }

    public String getDefaultDateFormat() { return defaultDateFormat; }
    public void setDefaultDateFormat(String defaultDateFormat) { this.defaultDateFormat = defaultDateFormat; }

    public String getDefaultTimeFormat() { return defaultTimeFormat; }
    public void setDefaultTimeFormat(String defaultTimeFormat) { this.defaultTimeFormat = defaultTimeFormat; }

    public String getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }

    public Integer getItemsPerPage() { return itemsPerPage; }
    public void setItemsPerPage(Integer itemsPerPage) { this.itemsPerPage = itemsPerPage; }

    public String getDefaultListView() { return defaultListView; }
    public void setDefaultListView(String defaultListView) { this.defaultListView = defaultListView; }

    public Boolean getEnableSmartFilters() { return enableSmartFilters; }
    public void setEnableSmartFilters(Boolean enableSmartFilters) { this.enableSmartFilters = enableSmartFilters; }

    public Boolean getEnableColumnCustomization() { return enableColumnCustomization; }
    public void setEnableColumnCustomization(Boolean enableColumnCustomization) { this.enableColumnCustomization = enableColumnCustomization; }

    public Boolean getEnableBulkOperations() { return enableBulkOperations; }
    public void setEnableBulkOperations(Boolean enableBulkOperations) { this.enableBulkOperations = enableBulkOperations; }

    public Boolean getEnableAuditLogging() { return enableAuditLogging; }
    public void setEnableAuditLogging(Boolean enableAuditLogging) { this.enableAuditLogging = enableAuditLogging; }

    public Integer getDataRetentionDays() { return dataRetentionDays; }
    public void setDataRetentionDays(Integer dataRetentionDays) { this.dataRetentionDays = dataRetentionDays; }

    public Integer getMaxFileUploadMb() { return maxFileUploadMb; }
    public void setMaxFileUploadMb(Integer maxFileUploadMb) { this.maxFileUploadMb = maxFileUploadMb; }

    public String getSmtpServer() { return smtpServer; }
    public void setSmtpServer(String smtpServer) { this.smtpServer = smtpServer; }

    public Integer getSmtpPort() { return smtpPort; }
    public void setSmtpPort(Integer smtpPort) { this.smtpPort = smtpPort; }

    public String getSmtpUsername() { return smtpUsername; }
    public void setSmtpUsername(String smtpUsername) { this.smtpUsername = smtpUsername; }

    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String smtpPassword) { this.smtpPassword = smtpPassword; }

    public String getSmtpFromEmail() { return smtpFromEmail; }
    public void setSmtpFromEmail(String smtpFromEmail) { this.smtpFromEmail = smtpFromEmail; }

    public String getSmtpFromName() { return smtpFromName; }
    public void setSmtpFromName(String smtpFromName) { this.smtpFromName = smtpFromName; }

    public Boolean getEmailTemplatesEnabled() { return emailTemplatesEnabled; }
    public void setEmailTemplatesEnabled(Boolean emailTemplatesEnabled) { this.emailTemplatesEnabled = emailTemplatesEnabled; }

    public Boolean getEnableTwoFactorAuth() { return enableTwoFactorAuth; }
    public void setEnableTwoFactorAuth(Boolean enableTwoFactorAuth) { this.enableTwoFactorAuth = enableTwoFactorAuth; }

    public Integer getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
    public void setSessionTimeoutMinutes(Integer sessionTimeoutMinutes) { this.sessionTimeoutMinutes = sessionTimeoutMinutes; }

    public Integer getPasswordExpiryDays() { return passwordExpiryDays; }
    public void setPasswordExpiryDays(Integer passwordExpiryDays) { this.passwordExpiryDays = passwordExpiryDays; }

    public Integer getMinPasswordLength() { return minPasswordLength; }
    public void setMinPasswordLength(Integer minPasswordLength) { this.minPasswordLength = minPasswordLength; }

    public Boolean getRequireSpecialCharacters() { return requireSpecialCharacters; }
    public void setRequireSpecialCharacters(Boolean requireSpecialCharacters) { this.requireSpecialCharacters = requireSpecialCharacters; }

    public Boolean getEnableEmailNotifications() { return enableEmailNotifications; }
    public void setEnableEmailNotifications(Boolean enableEmailNotifications) { this.enableEmailNotifications = enableEmailNotifications; }

    public Boolean getEnableSmsNotifications() { return enableSmsNotifications; }
    public void setEnableSmsNotifications(Boolean enableSmsNotifications) { this.enableSmsNotifications = enableSmsNotifications; }

    public Boolean getEnableInAppNotifications() { return enableInAppNotifications; }
    public void setEnableInAppNotifications(Boolean enableInAppNotifications) { this.enableInAppNotifications = enableInAppNotifications; }

    public Boolean getNotificationSoundEnabled() { return notificationSoundEnabled; }
    public void setNotificationSoundEnabled(Boolean notificationSoundEnabled) { this.notificationSoundEnabled = notificationSoundEnabled; }

    public Integer getApiRateLimitPerMinute() { return apiRateLimitPerMinute; }
    public void setApiRateLimitPerMinute(Integer apiRateLimitPerMinute) { this.apiRateLimitPerMinute = apiRateLimitPerMinute; }

    public Boolean getEnableApiDocumentation() { return enableApiDocumentation; }
    public void setEnableApiDocumentation(Boolean enableApiDocumentation) { this.enableApiDocumentation = enableApiDocumentation; }

    public Boolean getEnableWebhooks() { return enableWebhooks; }
    public void setEnableWebhooks(Boolean enableWebhooks) { this.enableWebhooks = enableWebhooks; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

    public LocalDateTime getModifiedTime() { return modifiedTime; }
    public void setModifiedTime(LocalDateTime modifiedTime) { this.modifiedTime = modifiedTime; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }
}
