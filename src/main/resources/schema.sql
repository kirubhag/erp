-- Drop and create tables without problematic custom field tables

-- Organizations table
CREATE TABLE IF NOT EXISTS organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) UNIQUE NOT NULL,
    address TEXT,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    website VARCHAR(255),
    logo_url VARCHAR(500),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE
);

-- Users IAM table
CREATE TABLE IF NOT EXISTS iam_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_date TIMESTAMP NULL,
    password_change_date TIMESTAMP NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100)
);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    organization_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- Permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100)
);

-- User roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES iam_users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Role permissions junction table
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Addresses table (must be created before students, staff, and parents tables)
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    address_line1 VARCHAR(100),
    address_line2 VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(50),
    is_primary BOOLEAN DEFAULT TRUE NOT NULL,
    address_type VARCHAR(20) DEFAULT 'RESIDENTIAL',
    created_time DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    modified_time DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_primary (entity_type, entity_id, is_primary)
);

-- Students table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    email VARCHAR(255),
    phone_number VARCHAR(20),
    address TEXT,
    enrollment_date DATE NOT NULL,
    graduation_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    grade_level VARCHAR(20),
    class_section VARCHAR(50),
    guardian_name VARCHAR(200),
    guardian_phone VARCHAR(20),
    guardian_email VARCHAR(255),
    guardian_relation VARCHAR(50),
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(20),
    blood_group VARCHAR(10),
    allergies TEXT,
    medical_conditions TEXT,
    transportation_mode VARCHAR(50),
    organization_id BIGINT,
    address_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    FOREIGN KEY (organization_id) REFERENCES organizations(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- Staff table
CREATE TABLE IF NOT EXISTS staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    hire_date DATE NOT NULL,
    job_title VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    salary DECIMAL(15,2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    date_of_birth DATE,
    gender VARCHAR(10),
    address TEXT,
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(20),
    qualification TEXT,
    experience_years INT DEFAULT 0,
    organization_id BIGINT,
    user_id BIGINT UNIQUE,
    address_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    FOREIGN KEY (organization_id) REFERENCES organizations(id),
    FOREIGN KEY (user_id) REFERENCES iam_users(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- Parents table
CREATE TABLE IF NOT EXISTS parents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20),
    address TEXT,
    occupation VARCHAR(100),
    relationship_to_student VARCHAR(50),
    is_primary_contact BOOLEAN DEFAULT FALSE,
    is_emergency_contact BOOLEAN DEFAULT FALSE,
    organization_id BIGINT,
    address_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    FOREIGN KEY (organization_id) REFERENCES organizations(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- Student parent relationships
CREATE TABLE IF NOT EXISTS student_parent_relationships (
    student_id BIGINT NOT NULL,
    parent_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, parent_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE CASCADE
);

-- Attendance table (without custom fields to avoid row size issues)
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    staff_id BIGINT,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    check_in_time TIME,
    check_out_time TIME,
    attendance_type VARCHAR(20) NOT NULL,
    remarks TEXT,
    excused BOOLEAN DEFAULT FALSE,
    recorded_by BIGINT,
    organization_id BIGINT,
    created_time DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    modified_time DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    UNIQUE KEY unique_student_date (student_id, attendance_date),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES iam_users(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- Health records table
CREATE TABLE IF NOT EXISTS health_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    record_type VARCHAR(50) NOT NULL,
    description TEXT,
    treatment TEXT,
    medication TEXT,
    height_cm DECIMAL(5,2),
    weight_kg DECIMAL(5,2),
    temperature_celsius DECIMAL(4,2),
    blood_pressure VARCHAR(20),
    notes TEXT,
    recorded_by VARCHAR(100),
    organization_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- ERP Fields table for field management system
CREATE TABLE IF NOT EXISTS erp_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_label VARCHAR(255) NOT NULL,
    field_type VARCHAR(50) NOT NULL,
    ui_field_type INT DEFAULT 100,
    is_required BOOLEAN DEFAULT FALSE,
    is_searchable BOOLEAN DEFAULT TRUE,
    is_sortable BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    field_group VARCHAR(100),
    placeholder_text VARCHAR(255),
    help_text VARCHAR(500),
    validation_pattern VARCHAR(500),
    default_value TEXT,
    options_json TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    organization_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    UNIQUE KEY unique_entity_field (entity_type, field_name, organization_id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- Custom Views table
CREATE TABLE IF NOT EXISTS custom_views (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    entity_type VARCHAR(100) NOT NULL,
    view_type VARCHAR(50) DEFAULT 'LIST',
    columns_config JSON,
    filters_config JSON,
    sort_config JSON,
    is_public BOOLEAN DEFAULT FALSE,
    is_default BOOLEAN DEFAULT FALSE,
    organization_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- Email Templates table
CREATE TABLE IF NOT EXISTS email_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,
    template_type VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    organization_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- Insert basic permissions
INSERT IGNORE INTO permissions (name, description, resource, action) VALUES
('VIEW_STUDENTS', 'View students list and details', 'STUDENT', 'VIEW'),
('CREATE_STUDENTS', 'Create new students', 'STUDENT', 'CREATE'),
('EDIT_STUDENTS', 'Edit student information', 'STUDENT', 'EDIT'),
('DELETE_STUDENTS', 'Delete students', 'STUDENT', 'DELETE'),
('VIEW_ATTENDANCE', 'View attendance records', 'ATTENDANCE', 'VIEW'),
('MANAGE_ATTENDANCE', 'Mark and manage attendance', 'ATTENDANCE', 'MANAGE'),
('VIEW_STAFF', 'View staff list and details', 'STAFF', 'VIEW'),
('MANAGE_STAFF', 'Manage staff information', 'STAFF', 'MANAGE'),
('VIEW_REPORTS', 'View system reports', 'REPORTS', 'VIEW'),
('MANAGE_SYSTEM', 'System administration', 'SYSTEM', 'MANAGE');
-- Subjects table
CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(20) NOT NULL UNIQUE,
    subject_name VARCHAR(100) NOT NULL,
    description TEXT,
    grade_level VARCHAR(50) NOT NULL,
    category VARCHAR(50),
    credits INT,
    hours_per_week INT,
    prerequisites VARCHAR(200),
    difficulty_level VARCHAR(20),
    is_mandatory BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_subject_code (subject_code),
    INDEX idx_grade_level (grade_level),
    INDEX idx_is_active (is_active)
);

-- Timetables table
CREATE TABLE IF NOT EXISTS timetables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timetable_code VARCHAR(50) NOT NULL UNIQUE,
    class_name VARCHAR(100) NOT NULL,
    grade_level VARCHAR(50) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    semester VARCHAR(50),
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    subject_name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    teacher_name VARCHAR(200) NOT NULL,
    teacher_id VARCHAR(50) NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    building VARCHAR(100),
    period_number INT,
    notes TEXT,
    is_lab_session BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_timetable_code (timetable_code),
    INDEX idx_grade_level (grade_level),
    INDEX idx_day_of_week (day_of_week),
    INDEX idx_subject_code (subject_code),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_room_number (room_number),
    INDEX idx_is_active (is_active)
);

-- Grades table
CREATE TABLE IF NOT EXISTS grades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    student_name VARCHAR(200),
    grade_level VARCHAR(50),
    course_code VARCHAR(20),
    course_name VARCHAR(200),
    exam_type VARCHAR(50),
    marks_obtained DECIMAL(5, 2),
    total_marks DECIMAL(5, 2),
    percentage DECIMAL(5, 2),
    letter_grade VARCHAR(5),
    grade_point DECIMAL(4, 2),
    exam_date DATE NOT NULL,
    semester VARCHAR(20),
    academic_year VARCHAR(20),
    remarks TEXT,
    teacher_id VARCHAR(50),
    teacher_name VARCHAR(200),
    organization_id BIGINT,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_student_id (student_id),
    INDEX idx_course_code (course_code),
    INDEX idx_grade_level (grade_level),
    INDEX idx_exam_date (exam_date),
    INDEX idx_semester (semester),
    INDEX idx_academic_year (academic_year),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_is_active (is_active),
    UNIQUE KEY unique_grade_entry (student_id, course_code, exam_type, semester)
);

-- Insert test users for authentication
INSERT IGNORE INTO iam_users (username, password_hash, email, first_name, last_name, user_type, enabled, account_non_expired, credentials_non_expired, account_non_locked, created_date, last_modified_date, created_by, last_modified_by)
VALUES 
('admin', '$2a$10$dXJ3SW6G7P50eS6DtJV8Ue8LlYpTLj4h4z4W3K1e3L9K4J6M9P2Tu', 'admin@school.edu', 'Admin', 'User', 'ADMIN', true, true, true, true, NOW(), NOW(), 'system', 'system'),
('student', '$2a$10$wZ3MmW7Z5K3B2L9N8Q1R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0', 'student@school.edu', 'John', 'Student', 'STUDENT', true, true, true, true, NOW(), NOW(), 'system', 'system'),
('teacher', '$2a$10$zV4NnX8L6J2M9P1Q5R6S7T8U9V0W1X2Y3Z4A5B6C7D8E9F0G1H2I3', 'teacher@school.edu', 'Jane', 'Teacher', 'STAFF', true, true, true, true, NOW(), NOW(), 'system', 'system');
