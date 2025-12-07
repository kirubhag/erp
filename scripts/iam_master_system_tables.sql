-- IAM Master Database - System Tables Schema
-- These tables store system-wide configuration shared across all tenants
-- Database: IAM_MasterDB

USE IAM_MasterDB;

-- =============================================================================
-- System Permissions Table
-- =============================================================================
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    system_permission BOOLEAN DEFAULT false,
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    INDEX idx_resource (resource),
    INDEX idx_action (action),
    INDEX idx_system_permission (system_permission)
);

-- =============================================================================
-- System Roles Table
-- =============================================================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    system_role BOOLEAN DEFAULT false,
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    INDEX idx_name (name),
    INDEX idx_system_role (system_role)
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

-- =============================================================================
-- ERP Sections Table (System-wide section definitions)
-- =============================================================================
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
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    is_active INT DEFAULT 1,
    INDEX idx_entity_type (entity_type),
    INDEX idx_section_name (section_name),
    INDEX idx_display_order (display_order),
    INDEX idx_is_active (is_active),
    UNIQUE KEY unique_entity_section (entity_type, section_name)
);

-- =============================================================================
-- ERP Fields Table (System-wide field definitions)
-- =============================================================================
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
    is_active INT DEFAULT 1,
    FOREIGN KEY (section_id) REFERENCES erp_sections (id) ON DELETE SET NULL,
    INDEX idx_entity_type (entity_type),
    INDEX idx_field_name (field_name),
    INDEX idx_section_id (section_id),
    INDEX idx_is_active (is_active),
    UNIQUE KEY unique_entity_field (entity_type, field_name)
);

-- =============================================================================
-- ERP Entities Table (System-wide entity definitions)
-- =============================================================================
CREATE TABLE IF NOT EXISTS erp_entities (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    singular_name VARCHAR(100) NOT NULL,
    plural_name VARCHAR(100) NOT NULL,
    system_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    table_name VARCHAR(100),
    pkid VARCHAR(100),
    display_column VARCHAR(100),
    has_rel_table BOOLEAN DEFAULT false,
    icon VARCHAR(100),
    route VARCHAR(200),
    sequence INT DEFAULT 0,
    presence BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    INDEX idx_system_name (system_name),
    INDEX idx_sequence (sequence),
    INDEX idx_is_active (is_active)
);

-- =============================================================================
-- ERP Entity Relations Table (System-wide entity relationship definitions)
-- NOTE: This table is created after erp_entities to satisfy foreign key constraints
-- =============================================================================
CREATE TABLE IF NOT EXISTS erp_entity_relations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    parent_entity_id BIGINT NOT NULL,
    child_entity_id BIGINT NOT NULL,
    relation_type VARCHAR(50) NOT NULL,
    relation_name VARCHAR(100),
    foreign_key_column VARCHAR(100),
    is_mandatory BOOLEAN DEFAULT false,
    cascade_delete BOOLEAN DEFAULT false,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    INDEX idx_parent_entity (parent_entity_id),
    INDEX idx_child_entity (child_entity_id),
    INDEX idx_relation_type (relation_type),
    INDEX idx_is_active (is_active)
);

-- Add foreign keys after both tables exist
ALTER TABLE erp_entity_relations
    ADD CONSTRAINT fk_parent_entity FOREIGN KEY (parent_entity_id) REFERENCES erp_entities (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_child_entity FOREIGN KEY (child_entity_id) REFERENCES erp_entities (id) ON DELETE CASCADE;

-- =============================================================================
-- ERP Entity-Role Relations (Which roles can access which entities)
-- =============================================================================
CREATE TABLE IF NOT EXISTS erp_entity_role_relations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    can_create BOOLEAN DEFAULT false,
    can_read BOOLEAN DEFAULT true,
    can_update BOOLEAN DEFAULT false,
    can_delete BOOLEAN DEFAULT false,
    can_export BOOLEAN DEFAULT false,
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    UNIQUE KEY unique_entity_role (entity_id, role_id),
    INDEX idx_role_id (role_id),
    INDEX idx_entity_id (entity_id)
);

-- Add foreign keys after all referenced tables exist
ALTER TABLE erp_entity_role_relations
    ADD CONSTRAINT fk_entity_role_entity FOREIGN KEY (entity_id) REFERENCES erp_entities (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_entity_role_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE;

-- =============================================================================
-- Profile/User Type Configuration (Optional - if you have profile concept)
-- =============================================================================
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    profile_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    default_role_id BIGINT,
    is_system_profile BOOLEAN DEFAULT false,
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    is_active BOOLEAN DEFAULT true,
    INDEX idx_profile_name (profile_name),
    INDEX idx_is_active (is_active),
    INDEX idx_default_role (default_role_id)
);

-- Add foreign key after roles table exists
ALTER TABLE user_profiles
    ADD CONSTRAINT fk_profile_default_role FOREIGN KEY (default_role_id) REFERENCES roles (id) ON DELETE SET NULL;

-- Profile-Role mapping (profiles can have multiple roles)
CREATE TABLE IF NOT EXISTS profile_roles (
    profile_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (profile_id, role_id),
    INDEX idx_role_id (role_id),
    INDEX idx_profile_id (profile_id)
);

-- Add foreign keys after all tables exist
ALTER TABLE profile_roles
    ADD CONSTRAINT fk_profile_role_profile FOREIGN KEY (profile_id) REFERENCES user_profiles (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_profile_role_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE;
