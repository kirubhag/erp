-- Test Schema for ERP Application
-- This creates the necessary tables for unit testing

-- Base tables that other tables depend on
CREATE TABLE IF NOT EXISTS erp_organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE,
    description TEXT,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(255),
    website VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT
);

CREATE TABLE IF NOT EXISTS erp_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT
);

CREATE TABLE IF NOT EXISTS erp_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    resource VARCHAR(100),
    action VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP,
    password_changed_at TIMESTAMP,
    role_id BIGINT,
    organization_id BIGINT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (role_id) REFERENCES erp_roles(id),
    FOREIGN KEY (organization_id) REFERENCES erp_organizations(id)
);

CREATE TABLE IF NOT EXISTS erp_staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) UNIQUE,
    user_id BIGINT NOT NULL,
    organization_id BIGINT,
    department VARCHAR(100),
    position VARCHAR(100),
    hire_date DATE,
    salary DECIMAL(15,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (organization_id) REFERENCES erp_organizations(id)
);

CREATE TABLE IF NOT EXISTS erp_students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) UNIQUE,
    user_id BIGINT NOT NULL,
    organization_id BIGINT,
    grade_level VARCHAR(20),
    enrollment_date DATE,
    graduation_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (organization_id) REFERENCES erp_organizations(id)
);

CREATE TABLE IF NOT EXISTS erp_parents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    relationship_type VARCHAR(50),
    occupation VARCHAR(100),
    emergency_contact BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS erp_parent_student_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    relationship_type VARCHAR(50),
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (parent_id) REFERENCES erp_parents(id),
    FOREIGN KEY (student_id) REFERENCES erp_students(id),
    UNIQUE KEY unique_parent_student (parent_id, student_id)
);

CREATE TABLE IF NOT EXISTS erp_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    status VARCHAR(20) DEFAULT 'PRESENT',
    notes TEXT,
    organization_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (organization_id) REFERENCES erp_organizations(id)
);

CREATE TABLE IF NOT EXISTS erp_health_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    record_type VARCHAR(50),
    description TEXT,
    medical_conditions TEXT,
    medications TEXT,
    allergies TEXT,
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    doctor_name VARCHAR(255),
    doctor_phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (student_id) REFERENCES erp_students(id)
);

CREATE TABLE IF NOT EXISTS erp_email_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(500),
    content TEXT,
    template_type VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    organization_id BIGINT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (organization_id) REFERENCES erp_organizations(id)
);

CREATE TABLE IF NOT EXISTS erp_email_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    sender_email VARCHAR(255),
    subject VARCHAR(500),
    content TEXT,
    sent_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    error_message TEXT,
    template_id BIGINT,
    organization_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (template_id) REFERENCES email_templates(id),
    FOREIGN KEY (organization_id) REFERENCES erp_organizations(id)
);

CREATE TABLE IF NOT EXISTS erp_custom_views (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    view_name VARCHAR(255) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    description TEXT,
    view_definition TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    is_public BOOLEAN DEFAULT FALSE,
    created_by_user VARCHAR(255),
    organization_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (organization_id) REFERENCES erp_organizations(id)
);

-- Custom field tables for each entity
CREATE TABLE IF NOT EXISTS user_custom_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type VARCHAR(50) DEFAULT 'TEXT',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (entity_id) REFERENCES users(id),
    UNIQUE KEY unique_user_field (entity_id, field_name)
);

CREATE TABLE IF NOT EXISTS erp_staff_custom_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type VARCHAR(50) DEFAULT 'TEXT',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (entity_id) REFERENCES erp_staff(id),
    UNIQUE KEY unique_staff_field (entity_id, field_name)
);

CREATE TABLE IF NOT EXISTS student_custom_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type VARCHAR(50) DEFAULT 'TEXT',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (entity_id) REFERENCES erp_students(id),
    UNIQUE KEY unique_student_field (entity_id, field_name)
);

CREATE TABLE IF NOT EXISTS parent_custom_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type VARCHAR(50) DEFAULT 'TEXT',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (entity_id) REFERENCES erp_parents(id),
    UNIQUE KEY unique_parent_field (entity_id, field_name)
);

CREATE TABLE IF NOT EXISTS organization_custom_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type VARCHAR(50) DEFAULT 'TEXT',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (entity_id) REFERENCES erp_organizations(id),
    UNIQUE KEY unique_org_field (entity_id, field_name)
);

CREATE TABLE IF NOT EXISTS erp_attendance_custom_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type VARCHAR(50) DEFAULT 'TEXT',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (entity_id) REFERENCES erp_attendance(id),
    UNIQUE KEY unique_attendance_field (entity_id, field_name)
);

CREATE TABLE IF NOT EXISTS health_record_custom_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type VARCHAR(50) DEFAULT 'TEXT',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (entity_id) REFERENCES erp_health_records(id),
    UNIQUE KEY unique_health_field (entity_id, field_name)
);

CREATE TABLE IF NOT EXISTS email_template_custom_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type VARCHAR(50) DEFAULT 'TEXT',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (entity_id) REFERENCES email_templates(id),
    UNIQUE KEY unique_email_template_field (entity_id, field_name)
);

CREATE TABLE IF NOT EXISTS email_log_custom_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type VARCHAR(50) DEFAULT 'TEXT',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    owner_id BIGINT,
    FOREIGN KEY (entity_id) REFERENCES email_logs(id),
    UNIQUE KEY unique_email_log_field (entity_id, field_name)
);