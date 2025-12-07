-- ============================================================================
-- Comprehensive ERP Database Schema - MySQL 9.x Compatible
-- Table creation order optimized to resolve foreign key constraints
-- Generated from all 23 entity definitions in krs.erp.model package
-- ============================================================================

-- =============================================================================
-- Phase 1: Base IAM Tables (No FK Dependencies)
-- =============================================================================

-- Organizations table - Created first as it's referenced by other tables
CREATE TABLE IF NOT EXISTS organizations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    code VARCHAR(20) UNIQUE,
    description VARCHAR(500),
    email VARCHAR(100),
    phone VARCHAR(20),
    fax VARCHAR(20),
    website VARCHAR(100),
    street_address VARCHAR(200),
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(10),
    country VARCHAR(50),
    registration_number VARCHAR(50),
    tax_id VARCHAR(20),
    established_year INT,
    accreditation VARCHAR(50),
    academic_year_format VARCHAR(20),
    default_language VARCHAR(10) DEFAULT 'en',
    default_currency VARCHAR(10) DEFAULT 'USD',
    timezone VARCHAR(50) DEFAULT 'UTC',
    logo_url VARCHAR(500),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_code (code),
    INDEX idx_name (name),
    INDEX idx_type (type),
    INDEX idx_is_active (is_active)
);

-- Permissions table - Created before Roles
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    resource VARCHAR(50),
    action VARCHAR(50),
    system_permission BOOLEAN NOT NULL DEFAULT false,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_name (name),
    INDEX idx_resource_action (resource, action),
    INDEX idx_is_active (is_active)
);

-- Roles table - Created before Users
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    system_role BOOLEAN NOT NULL DEFAULT false,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_name (name),
    INDEX idx_is_active (is_active)
);

-- Role-Permission relationship
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE,
    INDEX idx_permission_id (permission_id)
);

-- Address table - Created early as it's used polymorphically
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    address_line1 VARCHAR(100),
    address_line2 VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(50),
    is_primary BOOLEAN NOT NULL DEFAULT true,
    address_type VARCHAR(20),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_is_primary (is_primary),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 2: Users Table (Depends on nothing - FK independent)
-- =============================================================================

-- Users table - Base for all user types
CREATE TABLE IF NOT EXISTS iam_users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(50) NOT NULL,
    tenant_id VARCHAR(36),
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    last_login_date DATETIME,
    password_change_date DATETIME,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_user_type (user_type),
    INDEX idx_is_active (is_active)
);

-- User-Role relationship
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES iam_users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    INDEX idx_role_id (role_id)
);

-- =============================================================================
-- Phase 3: Student Table (Depends on addresses and iam_users)
-- =============================================================================

CREATE TABLE IF NOT EXISTS students (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    student_id VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20),
    enrollment_date DATE NOT NULL,
    grade_level VARCHAR(50) NOT NULL,
    enrollment_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    address_id BIGINT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(50),
    user_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    UNIQUE KEY unique_user_id (user_id),
    FOREIGN KEY (address_id) REFERENCES addresses (id),
    FOREIGN KEY (user_id) REFERENCES iam_users (id),
    INDEX idx_student_id (student_id),
    INDEX idx_email (email),
    INDEX idx_grade_level (grade_level),
    INDEX idx_enrollment_status (enrollment_status),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 4: Staff Table (Depends on addresses and iam_users)
-- =============================================================================

CREATE TABLE IF NOT EXISTS staff (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    staff_id VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20),
    hire_date DATE NOT NULL,
    termination_date DATE,
    employment_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    staff_type VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    position VARCHAR(100),
    qualification VARCHAR(255),
    experience_years INT,
    salary DOUBLE,
    address_id BIGINT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(50),
    user_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    UNIQUE KEY unique_user_id (user_id),
    FOREIGN KEY (address_id) REFERENCES addresses (id),
    FOREIGN KEY (user_id) REFERENCES iam_users (id),
    INDEX idx_staff_id (staff_id),
    INDEX idx_email (email),
    INDEX idx_employment_status (employment_status),
    INDEX idx_staff_type (staff_type),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 5: Parent Table (Depends on addresses and iam_users)
-- =============================================================================

CREATE TABLE IF NOT EXISTS parents (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    alternate_phone VARCHAR(20),
    gender VARCHAR(20),
    occupation VARCHAR(100),
    workplace VARCHAR(100),
    work_phone VARCHAR(20),
    emergency_contact BOOLEAN NOT NULL DEFAULT false,
    authorized_pickup BOOLEAN NOT NULL DEFAULT true,
    receive_notifications BOOLEAN NOT NULL DEFAULT true,
    address_id BIGINT,
    user_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    UNIQUE KEY unique_user_id (user_id),
    FOREIGN KEY (address_id) REFERENCES addresses (id),
    FOREIGN KEY (user_id) REFERENCES iam_users (id),
    INDEX idx_email (email),
    INDEX idx_is_active (is_active)
);

-- Parent-Student relationship (Depends on parents and students)
CREATE TABLE IF NOT EXISTS parent_student_relations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    relationship_type VARCHAR(50) NOT NULL,
    primary_contact BOOLEAN NOT NULL DEFAULT false,
    custody_rights BOOLEAN NOT NULL DEFAULT true,
    emergency_contact BOOLEAN NOT NULL DEFAULT false,
    authorized_pickup BOOLEAN NOT NULL DEFAULT true,
    receive_communications BOOLEAN NOT NULL DEFAULT true,
    notes VARCHAR(500),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (parent_id) REFERENCES parents (id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    INDEX idx_parent_id (parent_id),
    INDEX idx_student_id (student_id),
    INDEX idx_relationship_type (relationship_type),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 6: Academic Tables (Subjects, Grades, Timetables)
-- =============================================================================

CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(20) NOT NULL UNIQUE,
    subject_name VARCHAR(100) NOT NULL,
    description TEXT,
    grade_level VARCHAR(50) NOT NULL,
    category VARCHAR(50),
    credits INT,
    hours_per_week INT,
    prerequisites VARCHAR(200),
    difficulty_level VARCHAR(20),
    is_mandatory BOOLEAN DEFAULT true,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_subject_code (subject_code),
    INDEX idx_grade_level (grade_level),
    INDEX idx_category (category),
    INDEX idx_is_active (is_active)
);

CREATE TABLE IF NOT EXISTS grades (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_name VARCHAR(200) NOT NULL,
    grade_level VARCHAR(50) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    course_name VARCHAR(200) NOT NULL,
    exam_type VARCHAR(50) NOT NULL,
    marks_obtained DECIMAL(5, 2) NOT NULL,
    total_marks DECIMAL(5, 2) NOT NULL,
    percentage DECIMAL(5, 2),
    letter_grade VARCHAR(5),
    grade_point DECIMAL(4, 2),
    exam_date DATE NOT NULL,
    semester VARCHAR(20) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    remarks TEXT,
    teacher_id VARCHAR(50),
    teacher_name VARCHAR(200),
    organization_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_student_id (student_id),
    INDEX idx_exam_date (exam_date),
    INDEX idx_academic_year (academic_year),
    INDEX idx_is_active (is_active)
);

CREATE TABLE IF NOT EXISTS timetables (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    timetable_code VARCHAR(50) NOT NULL UNIQUE,
    class_name VARCHAR(100) NOT NULL,
    grade_level VARCHAR(50) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    semester VARCHAR(20),
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    subject_name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(50),
    teacher_name VARCHAR(100),
    teacher_id VARCHAR(50),
    room_number VARCHAR(20),
    building VARCHAR(50),
    period_number INT,
    notes VARCHAR(500),
    is_lab_session BOOLEAN DEFAULT false,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_timetable_code (timetable_code),
    INDEX idx_day_of_week (day_of_week),
    INDEX idx_grade_level (grade_level),
    INDEX idx_academic_year (academic_year),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 7: Attendance Table (Depends on students, staff, iam_users)
-- =============================================================================

CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    attendance_date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    status VARCHAR(50) NOT NULL,
    attendance_type VARCHAR(50) NOT NULL,
    remarks VARCHAR(500),
    excused BOOLEAN DEFAULT false,
    student_id BIGINT,
    staff_id BIGINT,
    recorded_by BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE SET NULL,
    FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE SET NULL,
    FOREIGN KEY (recorded_by) REFERENCES iam_users (id) ON DELETE SET NULL,
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_student_id (student_id),
    INDEX idx_staff_id (staff_id),
    INDEX idx_status (status),
    INDEX idx_attendance_type (attendance_type),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 8: Health Records Table (Depends on students and iam_users)
-- =============================================================================

CREATE TABLE IF NOT EXISTS health_records (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    record_type VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    record_date DATE,
    expiry_date DATE,
    provider VARCHAR(100),
    provider_contact VARCHAR(100),
    severity VARCHAR(50),
    medication VARCHAR(255),
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    special_instructions TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    requires_attention BOOLEAN NOT NULL DEFAULT false,
    document_path VARCHAR(255),
    recorded_by BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES iam_users (id) ON DELETE SET NULL,
    INDEX idx_student_id (student_id),
    INDEX idx_record_type (record_type),
    INDEX idx_requires_attention (requires_attention),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 9: ERP Section and Field Configuration Tables
-- =============================================================================

-- ERP Sections table (must be created before erp_fields)
CREATE TABLE IF NOT EXISTS erp_sections (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    section_name VARCHAR(100) NOT NULL,
    section_label VARCHAR(200) NOT NULL,
    layout_type VARCHAR(20) NOT NULL DEFAULT 'TWO_COLUMN',
    display_order INT DEFAULT 0,
    is_collapsible BOOLEAN DEFAULT false,
    is_collapsed_by_default BOOLEAN DEFAULT false,
    show_in_create BOOLEAN DEFAULT true,
    show_in_edit BOOLEAN DEFAULT true,
    show_in_detail BOOLEAN DEFAULT true,
    section_icon VARCHAR(100),
    section_color VARCHAR(50),
    css_class VARCHAR(100),
    description TEXT,
    help_text TEXT,
    organization_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_entity_type (entity_type),
    INDEX idx_section_name (section_name),
    INDEX idx_display_order (display_order),
    INDEX idx_is_active (is_active),
    UNIQUE KEY unique_entity_section (entity_type, section_name)
);

-- ERP Fields table (depends on erp_sections)
CREATE TABLE IF NOT EXISTS erp_fields (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_label VARCHAR(200) NOT NULL,
    field_type VARCHAR(50) NOT NULL,
    ui_type INT,
    section_id BIGINT,
    row_position INT DEFAULT 0,
    column_position INT DEFAULT 0,
    is_required BOOLEAN DEFAULT false,
    is_searchable BOOLEAN DEFAULT true,
    is_sortable BOOLEAN DEFAULT true,
    display_order INT DEFAULT 0,
    field_description VARCHAR(500),
    default_width INT DEFAULT 150,
    max_length INT,
    validation_pattern VARCHAR(500),
    picklist_options TEXT,
    decimal_places INT,
    is_unique BOOLEAN DEFAULT false,
    show_in_list BOOLEAN DEFAULT true,
    show_in_form BOOLEAN DEFAULT true,
    column_width VARCHAR(50) DEFAULT 'medium',
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (section_id) REFERENCES erp_sections (id) ON DELETE SET NULL,
    INDEX idx_entity_type (entity_type),
    INDEX idx_field_name (field_name),
    INDEX idx_section_id (section_id),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 10: Email Configuration Tables (Depends on nothing directly)
-- =============================================================================

CREATE TABLE IF NOT EXISTS email_templates (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    last_used DATETIME,
    usage_count INT NOT NULL DEFAULT 0,
    available_variables TEXT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_template_name (template_name),
    INDEX idx_entity_type (entity_type),
    INDEX idx_is_active (is_active)
);

CREATE TABLE IF NOT EXISTS email_logs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    recipient_name VARCHAR(200),
    subject VARCHAR(500) NOT NULL,
    body TEXT,
    status VARCHAR(50) NOT NULL,
    sent_at DATETIME,
    delivered_at DATETIME,
    opened_at DATETIME,
    failed_at DATETIME,
    error_message VARCHAR(1000),
    sent_by VARCHAR(100),
    retry_count INT NOT NULL DEFAULT 0,
    priority INT NOT NULL DEFAULT 1,
    email_provider VARCHAR(50),
    message_id VARCHAR(255),
    metadata TEXT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (template_id) REFERENCES email_templates (id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_recipient_email (recipient_email),
    INDEX idx_status (status),
    INDEX idx_sent_at (sent_at),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 11: ERP Entity Management Tables (No FK dependencies initially)
-- =============================================================================

CREATE TABLE IF NOT EXISTS erp_entities (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    singular_name VARCHAR(100) NOT NULL UNIQUE,
    plural_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    sequence INT NOT NULL DEFAULT 0,
    system_name VARCHAR(100),
    presence BOOLEAN NOT NULL DEFAULT true,
    icon VARCHAR(100),
    route VARCHAR(255),
    table_name VARCHAR(100),
    pkid VARCHAR(100),
    display_column VARCHAR(100),
    has_rel_table BOOLEAN NOT NULL DEFAULT false,
    created_date DATETIME NOT NULL,
    last_modified_date DATETIME,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    INDEX idx_singular_name (singular_name),
    INDEX idx_is_active (is_active),
    INDEX idx_table_name (table_name)
);

CREATE TABLE IF NOT EXISTS erp_entities_role_relation (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_date DATETIME NOT NULL,
    last_modified_date DATETIME,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    UNIQUE KEY unique_entity_role (entity_id, role_id),
    FOREIGN KEY (entity_id) REFERENCES erp_entities (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    INDEX idx_entity_id (entity_id),
    INDEX idx_role_id (role_id)
);

-- ERP Entity Relationship Tracking Table
CREATE TABLE IF NOT EXISTS erp_entity_relation (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    p_table_name VARCHAR(100) NOT NULL,
    p_pkid VARCHAR(100) NOT NULL,
    p_display_column VARCHAR(100),
    c_table_name VARCHAR(100) NOT NULL,
    c_pkid VARCHAR(100) NOT NULL,
    c_display_column VARCHAR(100),
    fk_column VARCHAR(100) NOT NULL,
    description TEXT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_p_table_name (p_table_name),
    INDEX idx_c_table_name (c_table_name),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 12: Custom View Tables (No FK dependencies)
-- =============================================================================

CREATE TABLE IF NOT EXISTS custom_views (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    view_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    entity_type VARCHAR(50) NOT NULL,
    is_default BOOLEAN DEFAULT false,
    created_by_user VARCHAR(100),
    is_public BOOLEAN DEFAULT false,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_view_name (view_name),
    INDEX idx_entity_type (entity_type),
    INDEX idx_is_default (is_default),
    INDEX idx_is_active (is_active)
);

CREATE TABLE IF NOT EXISTS custom_view_fields (
    custom_view_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (custom_view_id) REFERENCES custom_views (id) ON DELETE CASCADE,
    INDEX idx_custom_view_id (custom_view_id)
);

-- =============================================================================
-- Phase 13: Recycle Bin and Import History Tables (No FK dependencies)
-- =============================================================================

CREATE TABLE IF NOT EXISTS recycle_bin (
    recycle_bin_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    deleted_by VARCHAR(100) NOT NULL,
    deleted_time DATETIME NOT NULL,
    deletion_reason VARCHAR(500),
    entity_data TEXT,
    related_entity_count INT DEFAULT 0,
    INDEX idx_entity_type (entity_type),
    INDEX idx_deleted_time (deleted_time),
    INDEX idx_deleted_by (deleted_by)
);

-- Import History Table - Tracks data population events for audit and idempotency
CREATE TABLE IF NOT EXISTS import_history (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_name VARCHAR(100) NOT NULL,
    import_type VARCHAR(50) NOT NULL,
    record_count INT NOT NULL DEFAULT 0,
    source VARCHAR(255),
    imported_by VARCHAR(100) NOT NULL,
    import_start_time DATETIME NOT NULL,
    import_end_time DATETIME,
    import_status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000),
    notes VARCHAR(500),
    created_at DATETIME NOT NULL,
    is_active BOOLEAN DEFAULT true,
    INDEX idx_entity_name (entity_name),
    INDEX idx_import_type (import_type),
    INDEX idx_import_status (import_status),
    INDEX idx_created_at (created_at),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- Phase 7: Configuration and Settings Tables
-- =============================================================================

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
created_by VARCHAR(100) NOT NULL,
    modified_by VARCHAR(100),
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
theme_primary_color VARCHAR(50),
theme_secondary_color VARCHAR(50),
theme_accent_color VARCHAR(50),
theme_custom_colors JSON COMMENT 'User custom color overrides {primary, secondary, accent}',

-- Localization Preferences
language VARCHAR(10) COMMENT 'User preferred language',
timezone VARCHAR(50) COMMENT 'User preferred timezone',
date_format VARCHAR(20) COMMENT 'User preferred date format',
time_format VARCHAR(20) COMMENT 'User preferred time format (12h or 24h)',

-- List and Grid View Settings
default_list_view VARCHAR(20) DEFAULT 'table' COMMENT 'User default: table or card view',
records_per_page INT DEFAULT 25 COMMENT 'User preferred pagination size',
list_sidebar_expanded BOOLEAN DEFAULT true,
enable_smart_filters BOOLEAN DEFAULT true COMMENT 'User filter preferences',
saved_filters JSON COMMENT 'User saved filter configurations',
saved_views JSON COMMENT 'User saved view configurations',

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
last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

-- Metadata
created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
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

-- ============================================================================
-- Import System Tables (Added for bulk entity import functionality)
-- ============================================================================

-- ImportSession table - Tracks bulk import operations
CREATE TABLE IF NOT EXISTS import_sessions (
    id VARCHAR(50) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT,
    entity_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_format VARCHAR(20),
    total_records INT,
    file_size BIGINT,
    import_type VARCHAR(20),
    duplicate_action VARCHAR(20),
    find_duplicates_by VARCHAR(50),
    enable_manual_approval BOOLEAN DEFAULT false,
    skip_empty_fields BOOLEAN DEFAULT false,
    status VARCHAR(20) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    imported_at TIMESTAMP,
    updated_at TIMESTAMP,
    added_records INT DEFAULT 0,
    updated_records INT DEFAULT 0,
    skipped_records INT DEFAULT 0,
    failed_records INT DEFAULT 0,
    success_rate DOUBLE DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES iam_users (id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_entity_type (entity_type),
    INDEX idx_status (status),
    INDEX idx_uploaded_at (uploaded_at),
    INDEX idx_import_sessions_user_entity (user_id, entity_type)
) COMMENT = 'Tracks bulk import sessions and their status';

-- FieldMapping table - Maps CSV columns to entity fields
CREATE TABLE IF NOT EXISTS field_mappings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_session_id VARCHAR(50) NOT NULL,
    source_column VARCHAR(255),
    source_index INT,
    target_field VARCHAR(255),
    target_field_label VARCHAR(255),
    is_required BOOLEAN DEFAULT false,
    data_type VARCHAR(50),
    FOREIGN KEY (import_session_id) REFERENCES import_sessions (id) ON DELETE CASCADE,
    INDEX idx_session_id (import_session_id)
) COMMENT = 'Maps source columns from import file to target entity fields';

-- ImportResult table - Stores results of individual record imports
CREATE TABLE IF NOT EXISTS import_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_session_id VARCHAR(50) NOT NULL,
    row_num INT,
    record_id VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    data LONGTEXT,
    errors LONGTEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (import_session_id) REFERENCES import_sessions (id) ON DELETE CASCADE,
    INDEX idx_session_id (import_session_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_import_results_session_status (import_session_id, status)
) COMMENT = 'Stores result and error details for each imported record';

-- FieldMappingTemplate table - Defines available fields for each entity type
CREATE TABLE IF NOT EXISTS field_mapping_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_label VARCHAR(255) NOT NULL,
    is_required BOOLEAN DEFAULT false,
    data_type VARCHAR(50),
    suggestions LONGTEXT,
    section VARCHAR(100),
    display_order INT DEFAULT 0,
    UNIQUE KEY unique_entity_field (entity_type, field_name),
    INDEX idx_entity_type (entity_type),
    INDEX idx_field_name (field_name)
) COMMENT = 'Pre-configured field templates for auto-detection during import mapping';

-- ============================================================================
-- Academic Management Tables
-- ============================================================================

-- Academic Years table
CREATE TABLE IF NOT EXISTS academic_years (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT false,
    organization_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
    INDEX idx_org_active (organization_id, is_active),
    INDEX idx_dates (start_date, end_date)
) COMMENT = 'Academic/School years for the organization';

-- Academic Terms/Semesters table
CREATE TABLE IF NOT EXISTS academic_terms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    academic_year_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years (id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
    INDEX idx_academic_year (academic_year_id),
    INDEX idx_org_dates (organization_id, start_date)
) COMMENT = 'Terms/Semesters within academic years';

-- Grading Scales table
CREATE TABLE IF NOT EXISTS grading_scales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    letter_grade VARCHAR(10) NOT NULL,
    min_percentage DOUBLE NOT NULL,
    max_percentage DOUBLE NOT NULL,
    grade_point DOUBLE NOT NULL,
    organization_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
    INDEX idx_org_percentage (
        organization_id,
        min_percentage
    )
) COMMENT = 'Grading scales and grade points for the organization';

-- Academic Settings table
CREATE TABLE IF NOT EXISTS academic_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL UNIQUE,

-- Attendance Settings
enable_attendance_tracking BOOLEAN DEFAULT true,
attendance_calculation_method VARCHAR(20) DEFAULT 'percentage',
minimum_attendance_percentage DOUBLE DEFAULT 75.0,
allow_late_marking BOOLEAN DEFAULT true,
late_marking_cutoff_minutes INT DEFAULT 30,
enable_biometric_integration BOOLEAN DEFAULT false,

-- Exam Settings
default_exam_duration INT DEFAULT 60,
allow_makeup_exams BOOLEAN DEFAULT true,
makeup_exam_deadline_days INT DEFAULT 7,
passing_percentage DOUBLE DEFAULT 40.0,
enable_grade_moderation BOOLEAN DEFAULT false,
auto_calculate_grades BOOLEAN DEFAULT true,
publish_results_immediately BOOLEAN DEFAULT false,

-- Promotion Rules
auto_promote_students BOOLEAN DEFAULT false,
    minimum_attendance_for_promotion DOUBLE DEFAULT 75.0,
    minimum_grade_for_promotion DOUBLE DEFAULT 40.0,
    allow_grace_marks BOOLEAN DEFAULT true,
    grace_marks_limit DOUBLE DEFAULT 5.0,
    require_all_subjects_pass BOOLEAN DEFAULT true,
    allow_compartment_exams BOOLEAN DEFAULT true,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
) COMMENT='Academic settings for attendance, exams, and promotion rules';

-- ============================================================================
-- End of Schema - All tables created with proper FK ordering
-- ============================================================================