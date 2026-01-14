-- ============================================================================
-- Migration V002: Add Organization and User Configuration Tables
-- Purpose: Store organization-wide and user-specific settings
-- Date: 2025-11-19
-- ============================================================================

-- Organization Settings Table - Store organization-wide settings
CREATE TABLE IF NOT EXISTS organization_settings (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    
    -- Theme and Display Settings
    default_theme VARCHAR(50) DEFAULT 'light' COMMENT 'light, dark, auto',
    theme_primary_color VARCHAR(7) DEFAULT '#1976D2' COMMENT 'Primary color in hex',
    theme_secondary_color VARCHAR(7) DEFAULT '#E91E63' COMMENT 'Secondary color in hex',
    theme_accent_color VARCHAR(7) DEFAULT '#FFC107' COMMENT 'Accent color in hex',
    
    -- Localization Settings
    default_language VARCHAR(10) DEFAULT 'en' COMMENT 'Language code: en, es, fr, etc.',
    default_timezone VARCHAR(50) DEFAULT 'UTC' COMMENT 'Timezone identifier',
    default_date_format VARCHAR(20) DEFAULT 'DD/MM/YYYY' COMMENT 'Date format pattern',
    default_time_format VARCHAR(20) DEFAULT '24h' COMMENT '12h or 24h',
    default_currency VARCHAR(3) DEFAULT 'USD' COMMENT 'ISO 4217 currency code',
    
    -- UI Behavior Settings
    items_per_page INT DEFAULT 25 COMMENT 'Default records per page',
    default_list_view VARCHAR(20) DEFAULT 'table' COMMENT 'table or card',
    enable_smart_filters BOOLEAN DEFAULT true COMMENT 'Enable advanced filtering',
    enable_column_customization BOOLEAN DEFAULT true COMMENT 'Allow users to customize columns',
    enable_bulk_operations BOOLEAN DEFAULT true COMMENT 'Enable bulk select/delete',
    
    -- Data Settings
    enable_audit_logging BOOLEAN DEFAULT true COMMENT 'Log all data changes',
    data_retention_days INT DEFAULT 365 COMMENT 'Days to retain deleted records',
    max_file_upload_mb INT DEFAULT 50 COMMENT 'Maximum file upload size',
    
    -- Email Settings
    smtp_server VARCHAR(255),
    smtp_port INT DEFAULT 587,
    smtp_username VARCHAR(100),
    smtp_password VARCHAR(255),
    smtp_from_email VARCHAR(100),
    smtp_from_name VARCHAR(100),
    email_templates_enabled BOOLEAN DEFAULT true,
    
    -- System Settings
    enable_two_factor_auth BOOLEAN DEFAULT false COMMENT 'Require 2FA for all users',
    session_timeout_minutes INT DEFAULT 30 COMMENT 'Session timeout duration',
    password_expiry_days INT DEFAULT 90 COMMENT 'Days until password expires, 0 = never',
    min_password_length INT DEFAULT 8,
    require_special_characters BOOLEAN DEFAULT true,
    
    -- Notification Settings
    enable_email_notifications BOOLEAN DEFAULT true,
    enable_sms_notifications BOOLEAN DEFAULT false,
    enable_in_app_notifications BOOLEAN DEFAULT true,
    notification_sound_enabled BOOLEAN DEFAULT true,
    
    -- API and Integration Settings
    api_rate_limit_per_minute INT DEFAULT 100,
    enable_api_documentation BOOLEAN DEFAULT true,
    enable_webhooks BOOLEAN DEFAULT false,
    
    -- Metadata
    created_by BIGINT NOT NULL,
    modified_by BIGINT,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    
    UNIQUE KEY uq_organization (organization_id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    INDEX idx_organization_id (organization_id),
    INDEX idx_is_active (is_active),
    INDEX idx_created_time (created_time)
) COMMENT='Stores organization-wide configuration and settings';

-- User Settings Table - Store user-specific settings
CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    
    -- Theme and Display Preferences
    theme VARCHAR(50) COMMENT 'light, dark, auto - user override',
    theme_custom_colors JSON COMMENT 'User custom color overrides {primary, secondary, accent}',
    
    -- Localization Preferences
    language VARCHAR(10) COMMENT 'User preferred language',
    timezone VARCHAR(50) COMMENT 'User preferred timezone',
    date_format VARCHAR(20) COMMENT 'User preferred date format',
    time_format VARCHAR(20) COMMENT 'User preferred time format (12h or 24h)',
    
    -- List and Grid View Settings
    default_list_view VARCHAR(20) DEFAULT 'table' COMMENT 'User default: table or card view',
    records_per_page INT DEFAULT 25 COMMENT 'User preferred pagination size',
    enable_smart_filters BOOLEAN DEFAULT true COMMENT 'User filter preferences',
    saved_filters JSON COMMENT 'User saved filter configurations',
    
    -- Column Customization
    grid_columns JSON COMMENT 'Saved column configurations per module {module: [columns]}',
    column_widths JSON COMMENT 'User column width preferences',
    hidden_columns JSON COMMENT 'User hidden column preferences',
    
    -- Module and Navigation Preferences
    default_module VARCHAR(100) COMMENT 'Default module on login',
    favorite_modules JSON COMMENT 'Pinned/favorite modules list',
    module_order JSON COMMENT 'Custom module menu order',
    collapsed_menu_sections JSON COMMENT 'Collapsed menu sections',
    
    -- Notification Settings
    email_notifications_enabled BOOLEAN DEFAULT true,
    sms_notifications_enabled BOOLEAN DEFAULT false,
    in_app_notifications_enabled BOOLEAN DEFAULT true,
    notification_sound_enabled BOOLEAN DEFAULT true,
    notification_settings JSON COMMENT 'Per-module notification preferences',
    
    -- Dashboard and Widget Settings
    dashboard_layout JSON COMMENT 'Custom dashboard widget configuration',
    widget_preferences JSON COMMENT 'Widget size and position settings',
    dashboard_refresh_interval INT DEFAULT 300 COMMENT 'Auto-refresh interval in seconds',
    
    -- Search and Quick Access
    recent_searches JSON COMMENT 'User recent searches array',
    quick_access_shortcuts JSON COMMENT 'User custom quick links',
    
    -- Accessibility Settings
    enable_accessibility_mode BOOLEAN DEFAULT false,
    font_size VARCHAR(20) DEFAULT 'normal' COMMENT 'small, normal, large, extra-large',
    high_contrast_enabled BOOLEAN DEFAULT false,
    reduce_animations BOOLEAN DEFAULT false,
    
    -- Data Preferences
    show_deleted_records BOOLEAN DEFAULT false COMMENT 'Include soft-deleted records in views',
    auto_save_drafts BOOLEAN DEFAULT true COMMENT 'Auto-save form drafts',
    confirm_before_delete BOOLEAN DEFAULT true COMMENT 'Require confirmation on delete',
    
    -- Print and Export Settings
    export_format VARCHAR(20) DEFAULT 'csv' COMMENT 'Preferred export format',
    print_landscape BOOLEAN DEFAULT false,
    include_hidden_columns_in_export BOOLEAN DEFAULT false,
    
    -- Security and Privacy
    two_factor_auth_enabled BOOLEAN DEFAULT false,
    last_password_change DATETIME,
    failed_login_attempts INT DEFAULT 0,
    account_locked_until DATETIME,
    
    -- Session and Activity
    last_activity DATETIME,
    current_session_id VARCHAR(100),
    remember_me_enabled BOOLEAN DEFAULT false,
    
    -- Metadata
    created_by BIGINT NOT NULL,
    modified_by BIGINT,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    
    UNIQUE KEY uq_user_organization (user_id, organization_id),
    FOREIGN KEY (user_id) REFERENCES iam_users(id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_is_active (is_active),
    INDEX idx_created_time (created_time),
    INDEX idx_last_activity (last_activity)
) COMMENT='Stores user-specific settings, preferences, and customizations';

-- Add initial organization settings for existing organizations (if any)
INSERT IGNORE INTO organization_settings (
    organization_id,
    created_by,
    modified_by,
    is_active
)
SELECT 
    id,
    'SYSTEM',
    'SYSTEM',
    1
FROM organizations
WHERE is_active = 1
    AND id NOT IN (SELECT DISTINCT organization_id FROM organization_settings);

-- Add initial user settings for existing users (if any)
-- Note: This assumes users are assigned to organization_id 1 by default or via a separate assignment
INSERT IGNORE INTO user_settings (
    user_id,
    organization_id,
    created_by,
    modified_by,
    is_active
)
SELECT 
    u.id,
    1,
    'SYSTEM',
    'SYSTEM',
    1
FROM iam_users u
WHERE u.is_active = 1
    AND NOT EXISTS (SELECT 1 FROM user_settings us WHERE us.user_id = u.id AND us.organization_id = 1);

-- ============================================================================
-- Migration Complete
-- ============================================================================
