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
    entity_type VARCHAR(250) NOT NULL,
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
    -- High-priority additional fields
    nationality VARCHAR(50),
    blood_group VARCHAR(10),
    photo_url VARCHAR(500),
    section VARCHAR(20),
    admission_number VARCHAR(50) UNIQUE,
    admission_date DATE,
    -- Existing fields
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
    INDEX idx_section (section),
    INDEX idx_admission_number (admission_number),
    INDEX idx_blood_group (blood_group),
    INDEX idx_nationality (nationality),
    INDEX idx_is_active (is_active)
);

-- Student Guardian Information Table
CREATE TABLE IF NOT EXISTS student_guardian_info (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,
    father_name VARCHAR(100),
    father_occupation VARCHAR(100),
    father_phone VARCHAR(20),
    father_email VARCHAR(100),
    mother_name VARCHAR(100),
    mother_occupation VARCHAR(100),
    mother_phone VARCHAR(20),
    mother_email VARCHAR(100),
    guardian_name VARCHAR(100),
    guardian_relation VARCHAR(50),
    guardian_phone VARCHAR(20),
    guardian_email VARCHAR(100),
    guardian_address VARCHAR(300),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active INT DEFAULT 1,
    CONSTRAINT fk_guardian_student FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_father_phone (father_phone),
    INDEX idx_mother_phone (mother_phone),
    INDEX idx_guardian_phone (guardian_phone),
    INDEX idx_is_active (is_active)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Student Medical Information Table
CREATE TABLE IF NOT EXISTS student_medical_info (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,
    allergies TEXT,
    medical_conditions TEXT,
    medications TEXT,
    doctor_name VARCHAR(100),
    doctor_phone VARCHAR(20),
    hospital_preference VARCHAR(200),
    insurance_provider VARCHAR(100),
    insurance_policy_number VARCHAR(50),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active INT DEFAULT 1,
    CONSTRAINT fk_medical_student FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_is_active (is_active)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

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
-- Phase 9: ERP Field Configuration Tables (No FK dependencies)
-- =============================================================================

CREATE TABLE IF NOT EXISTS erp_fields (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(250) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_label VARCHAR(200) NOT NULL,
    field_type VARCHAR(50) NOT NULL,
    ui_type INT,
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
    section_id BIGINT,
    row_position INT DEFAULT 0,
    column_position INT DEFAULT 0,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_entity_type (entity_type),
    INDEX idx_field_name (field_name),
    INDEX idx_section (section_id),
    INDEX idx_position (
        section_id,
        row_position,
        column_position
    ),
    INDEX idx_is_active (is_active)
);

-- ERP Sections table - Stores module section configurations
CREATE TABLE IF NOT EXISTS erp_sections (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(250) NOT NULL,
    section_name VARCHAR(100) NOT NULL,
    section_label VARCHAR(200) NOT NULL,
    layout_type VARCHAR(20) NOT NULL DEFAULT 'TWO_COLUMN',
    display_order INT NOT NULL DEFAULT 0,
    is_collapsible TINYINT(1) DEFAULT 0,
    is_collapsed_by_default TINYINT(1) DEFAULT 0,
    show_in_create TINYINT(1) DEFAULT 1,
    show_in_edit TINYINT(1) DEFAULT 1,
    show_in_detail TINYINT(1) DEFAULT 1,
    section_icon VARCHAR(100),
    section_color VARCHAR(50),
    css_class VARCHAR(100),
    description TEXT,
    help_text TEXT,
    is_active TINYINT(1) DEFAULT 1,
    created_by VARCHAR(255),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    organization_id BIGINT,
    UNIQUE KEY uk_section (
        entity_type,
        section_name,
        organization_id
    ),
    INDEX idx_entity_type (entity_type),
    INDEX idx_display_order (display_order),
    INDEX idx_is_active (is_active)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Add foreign key constraint for section relationship
ALTER TABLE erp_fields
ADD CONSTRAINT fk_field_section FOREIGN KEY (section_id) REFERENCES erp_sections (id) ON DELETE SET NULL;

-- =============================================================================
-- Phase 10: Email Configuration Tables (Depends on nothing directly)
-- =============================================================================

CREATE TABLE IF NOT EXISTS email_templates (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    entity_type VARCHAR(250) NOT NULL,
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
    entity_type VARCHAR(250) NOT NULL,
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
    created_date DATETIME NOT NULL,
    last_modified_date DATETIME,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    INDEX idx_singular_name (singular_name),
    INDEX idx_is_active (is_active)
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

-- =============================================================================
-- Phase 12: Custom View Tables (No FK dependencies)
-- =============================================================================

CREATE TABLE IF NOT EXISTS custom_views (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    view_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    entity_type VARCHAR(250) NOT NULL,
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
    entity_type VARCHAR(250) NOT NULL,
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

-- ============================================================================
-- End of Schema - All 25 tables created with proper FK ordering
-- ============================================================================