SET FOREIGN_KEY_CHECKS = 0;

-- Table from SQL file: academic_settings
CREATE TABLE IF NOT EXISTS academic_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL UNIQUE,
    enable_attendance_tracking BOOLEAN DEFAULT true,
    attendance_calculation_method VARCHAR(20) DEFAULT 'percentage',
    minimum_attendance_percentage DOUBLE DEFAULT 75.0,
    allow_late_marking BOOLEAN DEFAULT true,
    late_marking_cutoff_minutes INT DEFAULT 30,
    enable_biometric_integration BOOLEAN DEFAULT false,
    default_exam_duration INT DEFAULT 60,
    allow_makeup_exams BOOLEAN DEFAULT true,
    makeup_exam_deadline_days INT DEFAULT 7,
    passing_percentage DOUBLE DEFAULT 40.0,
    enable_grade_moderation BOOLEAN DEFAULT false,
    auto_calculate_grades BOOLEAN DEFAULT true,
    publish_results_immediately BOOLEAN DEFAULT false,
    auto_promote_students BOOLEAN DEFAULT false,
    minimum_attendance_for_promotion DOUBLE DEFAULT 75.0,
    minimum_grade_for_promotion DOUBLE DEFAULT 40.0,
    allow_grace_marks BOOLEAN DEFAULT true,
    grace_marks_limit DOUBLE DEFAULT 5.0,
    require_all_subjects_pass BOOLEAN DEFAULT true,
    allow_compartment_exams BOOLEAN DEFAULT true,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table from SQL file: academic_terms
CREATE TABLE IF NOT EXISTS academic_terms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    academic_year_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table from SQL file: academic_years
CREATE TABLE IF NOT EXISTS academic_years (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT false,
    organization_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table from SQL file: addresses
CREATE TABLE IF NOT EXISTS addresses (
    address_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_is_primary (is_primary),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: attendance
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE SET NULL,
    FOREIGN KEY (staff_id) REFERENCES staff (staff_id) ON DELETE SET NULL,
    FOREIGN KEY (recorded_by) REFERENCES iam_users (user_id) ON DELETE SET NULL,
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_student_id (student_id),
    INDEX idx_staff_id (staff_id),
    INDEX idx_status (status),
    INDEX idx_attendance_type (attendance_type),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: courses
CREATE TABLE IF NOT EXISTS courses (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    credits INT,
    department VARCHAR(100),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: custom_view_fields
CREATE TABLE IF NOT EXISTS custom_view_fields (
    custom_view_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (custom_view_id) REFERENCES custom_views (custom_view_id) ON DELETE CASCADE,
    INDEX idx_custom_view_id (custom_view_id)
);

-- Table from SQL file: custom_views
CREATE TABLE IF NOT EXISTS custom_views (
    custom_view_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    view_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    entity_type VARCHAR(50) NOT NULL,
    is_default BOOLEAN DEFAULT false,
    created_by_user VARCHAR(100),
    is_public BOOLEAN DEFAULT false,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_view_name (view_name),
    INDEX idx_entity_type (entity_type),
    INDEX idx_is_default (is_default),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: email_logs
CREATE TABLE IF NOT EXISTS email_logs (
    email_log_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (template_id) REFERENCES email_templates (email_template_id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_recipient_email (recipient_email),
    INDEX idx_status (status),
    INDEX idx_sent_at (sent_at),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: email_templates
CREATE TABLE IF NOT EXISTS email_templates (
    email_template_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_template_name (template_name),
    INDEX idx_entity_type (entity_type),
    INDEX idx_is_active (is_active)
);

-- Table generated from JPA: erp_accounting_periods
CREATE TABLE IF NOT EXISTS erp_accounting_periods (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_closed BOOLEAN,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_admission_applications
CREATE TABLE IF NOT EXISTS erp_admission_applications (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    application_number VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255),
    date_of_birth DATE NOT NULL,
    gender VARCHAR(255) NOT NULL,
    grade_applied VARCHAR(255) NOT NULL,
    academic_year_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    application_date DATE NOT NULL,
    previous_school VARCHAR(255),
    parent_name VARCHAR(255),
    parent_email VARCHAR(255),
    parent_phone VARCHAR(255),
    address_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_admission_cycles
CREATE TABLE IF NOT EXISTS erp_admission_cycles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    academic_year_id BIGINT NOT NULL,
    description VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_admission_inquiries
CREATE TABLE IF NOT EXISTS erp_admission_inquiries (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(255),
    grade_interested VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    inquiry_date DATE NOT NULL,
    follow_up_date DATE,
    notes VARCHAR(255),
    source VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_admission_seat_allocations
CREATE TABLE IF NOT EXISTS erp_admission_seat_allocations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    admission_cycle_id BIGINT NOT NULL,
    grade_level VARCHAR(255) NOT NULL,
    total_seats INT NOT NULL,
    occupied_seats INT NOT NULL,
    waitlisted_seats INT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_alumni_contributions
CREATE TABLE IF NOT EXISTS erp_alumni_contributions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    alumni_id BIGINT NOT NULL,
    amount DECIMAL(19,2),
    contribution_date DATE,
    type VARCHAR(255),
    description VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_alumni_profiles
CREATE TABLE IF NOT EXISTS erp_alumni_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    graduation_year INT,
    degree VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(255),
    occupation VARCHAR(255),
    company VARCHAR(255),
    linkedin_profile VARCHAR(255),
    status VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_announcements
CREATE TABLE IF NOT EXISTS erp_announcements (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    target_audience VARCHAR(255) NOT NULL,
    published_at DATETIME,
    expires_at DATETIME,
    author_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: erp_attachments
CREATE TABLE IF NOT EXISTS erp_attachments (
    erp_attachment_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

-- File Information
original_filename VARCHAR(255) NOT NULL,
stored_filename VARCHAR(255) NOT NULL UNIQUE,
file_path VARCHAR(500) NOT NULL,
file_size BIGINT,
mime_type VARCHAR(100),

-- Attachment Type and Context
attachment_type VARCHAR(50) NOT NULL, -- AVATAR, DOCUMENT, IMAGE, VIDEO, AUDIO, OTHER
organization_id BIGINT NOT NULL,
entity_type VARCHAR(100), -- e.g., "USER", "STUDENT", "STAFF"
entity_id BIGINT, -- ID of the related entity
description VARCHAR(500),
uploaded_by BIGINT,

-- Audit fields from BaseEntity


created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    
    INDEX idx_stored_filename (stored_filename),
    INDEX idx_entity_type_id (entity_type, entity_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_uploaded_by (uploaded_by),
    INDEX idx_attachment_type (attachment_type),
    INDEX idx_is_active (is_active)
);

-- Table generated from JPA: erp_bank_statement_lines
CREATE TABLE IF NOT EXISTS erp_bank_statement_lines (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    is_reconciled BOOLEAN,
    matched_journal_item_id BIGINT,
    bank_statement_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_bank_statements
CREATE TABLE IF NOT EXISTS erp_bank_statements (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    bank_name VARCHAR(255) NOT NULL,
    account_number VARCHAR(255) NOT NULL,
    statement_date DATE NOT NULL,
    opening_balance DECIMAL(19,2) NOT NULL,
    closing_balance DECIMAL(19,2) NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_budget_lines
CREATE TABLE IF NOT EXISTS erp_budget_lines (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    allocated_amount DECIMAL(19,2) NOT NULL,
    actual_amount DECIMAL(19,2) NOT NULL,
    budget_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_budgets
CREATE TABLE IF NOT EXISTS erp_budgets (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    total_amount DECIMAL(19,2) NOT NULL,
    accounting_period_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_cal_calendar_days
CREATE TABLE IF NOT EXISTS erp_cal_calendar_days (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    calendar_date DATE NOT NULL,
    day_type VARCHAR(255),
    description VARCHAR(255),
    is_holiday BOOLEAN,
    event_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_cal_events
CREATE TABLE IF NOT EXISTS erp_cal_events (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    start_date DATETIME,
    end_date DATETIME,
    location VARCHAR(255),
    category VARCHAR(255),
    status VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_chart_of_accounts
CREATE TABLE IF NOT EXISTS erp_chart_of_accounts (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME,
    updated_at DATETIME,
    parent_account_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: erp_class
CREATE TABLE IF NOT EXISTS erp_class (
    class_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    class_code VARCHAR(50) NOT NULL UNIQUE,
    class_name VARCHAR(100) NOT NULL,
    grade_level VARCHAR(50) NOT NULL,
    section VARCHAR(20),
    academic_year VARCHAR(20) NOT NULL,
    capacity INT DEFAULT 40,
    room_number VARCHAR(20),
    class_teacher_id BIGINT,
    description TEXT,
    organization_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (class_teacher_id) REFERENCES staff (staff_id) ON DELETE SET NULL,
    FOREIGN KEY (organization_id) REFERENCES organizations (organization_id) ON DELETE CASCADE,
    INDEX idx_class_code (class_code),
    INDEX idx_grade_level (grade_level),
    INDEX idx_academic_year (academic_year),
    INDEX idx_is_active (is_active),
    UNIQUE KEY unique_class (
        grade_level,
        section,
        academic_year
    )
);

-- Table from SQL file: erp_class_student
CREATE TABLE IF NOT EXISTS erp_class_student (
    class_student_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    enrollment_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    roll_number VARCHAR(20),
    created_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES erp_class (class_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    UNIQUE KEY unique_class_student (class_id, student_id),
    INDEX idx_class_id (class_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
);

-- Table from SQL file: erp_class_subject
CREATE TABLE IF NOT EXISTS erp_class_subject (
    class_subject_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    hours_per_week INT,
    created_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES erp_class (class_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects (subject_id) ON DELETE CASCADE,
    UNIQUE KEY unique_class_subject (class_id, subject_id),
    INDEX idx_class_id (class_id),
    INDEX idx_subject_id (subject_id)
);

-- Table from SQL file: erp_class_teacher
CREATE TABLE IF NOT EXISTS erp_class_teacher (
    class_teacher_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    subject_id BIGINT,
    assignment_date DATE,
    is_primary BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES erp_class (class_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES staff (staff_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects (subject_id) ON DELETE SET NULL,
    UNIQUE KEY unique_class_teacher_subject (
        class_id,
        teacher_id,
        subject_id
    ),
    INDEX idx_class_id (class_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_subject_id (subject_id)
);

-- Table generated from JPA: erp_comm_logs
CREATE TABLE IF NOT EXISTS erp_comm_logs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    recipient_type VARCHAR(255),
    channel VARCHAR(255),
    subject VARCHAR(255),
    content VARCHAR(255),
    status VARCHAR(255),
    sent_at DATETIME,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_comm_templates
CREATE TABLE IF NOT EXISTS erp_comm_templates (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    channel VARCHAR(255),
    subject VARCHAR(255),
    content VARCHAR(255),
    placeholders VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_docs_documents
CREATE TABLE IF NOT EXISTS erp_docs_documents (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(255),
    file_type VARCHAR(255),
    file_url VARCHAR(255),
    status VARCHAR(255),
    upload_date DATETIME,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: erp_entities
CREATE TABLE IF NOT EXISTS erp_entities (
    erp_entity_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    INDEX idx_singular_name (singular_name),
    INDEX idx_is_active (is_active),
    INDEX idx_table_name (table_name)
);

-- Table from SQL file: erp_entities_role_relation
CREATE TABLE IF NOT EXISTS erp_entities_role_relation (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    UNIQUE KEY unique_entity_role (entity_id, role_id),
    FOREIGN KEY (entity_id) REFERENCES erp_entities (erp_entity_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,
    INDEX idx_entity_id (entity_id),
    INDEX idx_role_id (role_id)
);

-- Table from SQL file: erp_entity_relation
CREATE TABLE IF NOT EXISTS erp_entity_relation (
    erp_entity_relation_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_p_table_name (p_table_name),
    INDEX idx_c_table_name (c_table_name),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: erp_entity_relations
CREATE TABLE IF NOT EXISTS erp_entity_relations (
    erp_entity_relation_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    parent_entity_id BIGINT NOT NULL,
    child_entity_id BIGINT NOT NULL,
    relation_type VARCHAR(50) NOT NULL,
    relation_name VARCHAR(100),
    foreign_key_column VARCHAR(100),
    is_mandatory BOOLEAN DEFAULT false,
    cascade_delete BOOLEAN DEFAULT false,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    INDEX idx_parent_entity (parent_entity_id),
    INDEX idx_child_entity (child_entity_id),
    INDEX idx_relation_type (relation_type),
    INDEX idx_is_active (is_active)
);

-- Table generated from JPA: erp_fee_disciplinary_incidents
CREATE TABLE IF NOT EXISTS erp_fee_disciplinary_incidents (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    reported_by_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    description VARCHAR(255),
    evidence_url VARCHAR(255),
    fine_amount DECIMAL(19,2),
    approval_status VARCHAR(255),
    incident_date DATETIME,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fee_discount_rules
CREATE TABLE IF NOT EXISTS erp_fee_discount_rules (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    value DECIMAL(19,2),
    condition_type VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fee_fine_categories
CREATE TABLE IF NOT EXISTS erp_fee_fine_categories (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fee_fine_configs
CREATE TABLE IF NOT EXISTS erp_fee_fine_configs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    calc_logic VARCHAR(255),
    base_amount DECIMAL(19,2),
    frequency VARCHAR(255),
    grace_period_days INT,
    is_auto_post BOOLEAN,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fee_fine_ledger
CREATE TABLE IF NOT EXISTS erp_fee_fine_ledger (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    fine_config_id BIGINT NOT NULL,
    base_amount DECIMAL(19,2),
    accrued_amount DECIMAL(19,2),
    status VARCHAR(255),
    issued_at DATETIME,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fee_fine_waivers
CREATE TABLE IF NOT EXISTS erp_fee_fine_waivers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fine_ledger_id BIGINT NOT NULL,
    requested_by_id BIGINT NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(255),
    approved_by_id BIGINT,
    adjustment_amount DECIMAL(19,2),
    request_date DATETIME,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fee_payments
CREATE TABLE IF NOT EXISTS erp_fee_payments (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    fee_structure_id BIGINT,
    base_amount DECIMAL(19,2),
    discount_amount DECIMAL(19,2),
    fine_amount DECIMAL(19,2),
    net_amount DECIMAL(19,2),
    payment_date DATETIME,
    payment_mode VARCHAR(255),
    receipt_number VARCHAR(255),
    remarks VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fee_structures
CREATE TABLE IF NOT EXISTS erp_fee_structures (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fee_type_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    grade_id BIGINT,
    amount DECIMAL(19,2) NOT NULL,
    due_date DATE,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fee_types
CREATE TABLE IF NOT EXISTS erp_fee_types (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255),
    description VARCHAR(255),
    is_recurring BOOLEAN,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: erp_fields
CREATE TABLE IF NOT EXISTS erp_fields (
    erp_field_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(250) NOT NULL,
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
    show_type INT DEFAULT 0,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (section_id) REFERENCES erp_sections (erp_section_id) ON DELETE SET NULL,
    INDEX idx_entity_type (entity_type),
    INDEX idx_field_name (field_name),
    INDEX idx_section_id (section_id),
    INDEX idx_show_type (show_type),
    INDEX idx_is_active (is_active)
);

-- Table generated from JPA: erp_fin_invoice_items
CREATE TABLE IF NOT EXISTS erp_fin_invoice_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(19,2),
    item_type VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fin_invoices
CREATE TABLE IF NOT EXISTS erp_fin_invoices (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(255) NOT NULL,
    student_id BIGINT,
    issue_date DATE,
    due_date DATE,
    total_amount DECIMAL(19,2),
    discount_amount DECIMAL(19,2),
    tax_amount DECIMAL(19,2),
    net_amount DECIMAL(19,2),
    status VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_fin_transactions
CREATE TABLE IF NOT EXISTS erp_fin_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    transaction_id_ext VARCHAR(255),
    invoice_id BIGINT,
    payment_date DATETIME,
    amount_paid DECIMAL(19,2),
    payment_mode VARCHAR(255),
    reference_number VARCHAR(255),
    status VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: erp_grade
CREATE TABLE IF NOT EXISTS erp_grade (
    grade_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    teacher_id BIGINT,
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
    organization_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects (subject_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES staff (staff_id) ON DELETE SET NULL,
    FOREIGN KEY (organization_id) REFERENCES organizations (organization_id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_subject_id (subject_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_exam_date (exam_date),
    INDEX idx_academic_year (academic_year),
    INDEX idx_semester (semester),
    INDEX idx_is_active (is_active),
    UNIQUE KEY unique_grade (
        student_id,
        subject_id,
        exam_type,
        semester,
        academic_year
    )
);

-- Table generated from JPA: erp_hr_job_applications
CREATE TABLE IF NOT EXISTS erp_hr_job_applications (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    job_posting_id BIGINT NOT NULL,
    candidate_name VARCHAR(255) NOT NULL,
    candidate_email VARCHAR(255) NOT NULL,
    candidate_phone VARCHAR(255),
    resume_url VARCHAR(255),
    applied_date DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_hr_job_postings
CREATE TABLE IF NOT EXISTS erp_hr_job_postings (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    requirements VARCHAR(255),
    employment_type VARCHAR(255) NOT NULL,
    posted_date DATE NOT NULL,
    closing_date DATE,
    status VARCHAR(255) NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_hr_leave_balances
CREATE TABLE IF NOT EXISTS erp_hr_leave_balances (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    academic_year VARCHAR(255) NOT NULL,
    total_days DECIMAL(19,2),
    consumed_days DECIMAL(19,2),
    remaining_days DECIMAL(19,2),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_hr_leave_requests
CREATE TABLE IF NOT EXISTS erp_hr_leave_requests (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(255),
    rejection_reason VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_hr_leave_types
CREATE TABLE IF NOT EXISTS erp_hr_leave_types (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    days_allowed INT,
    is_carry_forward BOOLEAN,
    description VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_hr_payroll_runs
CREATE TABLE IF NOT EXISTS erp_hr_payroll_runs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    month INT NOT NULL,
    year INT NOT NULL,
    processed_date DATE,
    status VARCHAR(255) NOT NULL,
    total_payout DECIMAL(19,2),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_hr_payslips
CREATE TABLE IF NOT EXISTS erp_hr_payslips (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    payroll_run_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    staff_name VARCHAR(255),
    department VARCHAR(255),
    basic_salary DECIMAL(19,2),
    hra DECIMAL(19,2),
    da DECIMAL(19,2),
    allowances DECIMAL(19,2),
    pf_deduction DECIMAL(19,2),
    tax_deduction DECIMAL(19,2),
    other_deductions DECIMAL(19,2),
    gross_salary DECIMAL(19,2),
    total_deductions DECIMAL(19,2),
    net_salary DECIMAL(19,2),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_hr_staff_salaries
CREATE TABLE IF NOT EXISTS erp_hr_staff_salaries (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    basic_salary DECIMAL(19,2),
    hra DECIMAL(19,2),
    da DECIMAL(19,2),
    special_allowance DECIMAL(19,2),
    is_pf_enabled BOOLEAN,
    pf_account_number VARCHAR(255),
    tax_deduction DECIMAL(19,2),
    pan_number VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_inventory_assets
CREATE TABLE IF NOT EXISTS erp_inventory_assets (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    asset_tag VARCHAR(255),
    serial_number VARCHAR(255),
    type VARCHAR(255),
    purchase_date DATE,
    status VARCHAR(255),
    location VARCHAR(255),
    assigned_staff_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_inventory_consumables
CREATE TABLE IF NOT EXISTS erp_inventory_consumables (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255),
    category VARCHAR(255),
    unit VARCHAR(255),
    reorder_level INT,
    current_stock INT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_inventory_purchase_orders
CREATE TABLE IF NOT EXISTS erp_inventory_purchase_orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    po_number VARCHAR(255) NOT NULL,
    vendor_id BIGINT NOT NULL,
    order_date DATE,
    total_amount DECIMAL(19,2),
    status VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_inventory_vendors
CREATE TABLE IF NOT EXISTS erp_inventory_vendors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    tin_gstin VARCHAR(255),
    address VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_journal_entries
CREATE TABLE IF NOT EXISTS erp_journal_entries (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entry_number VARCHAR(255) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_journal_items
CREATE TABLE IF NOT EXISTS erp_journal_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    journal_entry_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_library_authors
CREATE TABLE IF NOT EXISTS erp_library_authors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_library_holds
CREATE TABLE IF NOT EXISTS erp_library_holds (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    request_date DATETIME NOT NULL,
    resource_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_library_items
CREATE TABLE IF NOT EXISTS erp_library_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    accession_number VARCHAR(255) NOT NULL,
    audit_status VARCHAR(255),
    resource_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_library_loans
CREATE TABLE IF NOT EXISTS erp_library_loans (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    loan_date DATETIME NOT NULL,
    due_date DATETIME NOT NULL,
    return_date DATETIME,
    renewal_count INT,
    item_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_library_policies
CREATE TABLE IF NOT EXISTS erp_library_policies (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    grade_level VARCHAR(255),
    max_books INT NOT NULL,
    loan_period_days INT NOT NULL,
    max_renewals INT NOT NULL,
    fine_per_day VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_library_pos
CREATE TABLE IF NOT EXISTS erp_library_pos (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_date DATE NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    vendor_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_library_publishers
CREATE TABLE IF NOT EXISTS erp_library_publishers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    contact_info VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_library_purchase_requests
CREATE TABLE IF NOT EXISTS erp_library_purchase_requests (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    requested_by BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_library_resources
CREATE TABLE IF NOT EXISTS erp_library_resources (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    isbn_issn VARCHAR(255),
    digital_path VARCHAR(255),
    author_id BIGINT,
    publisher_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_answers
CREATE TABLE IF NOT EXISTS erp_lms_answers (
    answer_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_badges
CREATE TABLE IF NOT EXISTS erp_lms_badges (
    badge_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_content
CREATE TABLE IF NOT EXISTS erp_lms_content (
    content_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_forum_posts
CREATE TABLE IF NOT EXISTS erp_lms_forum_posts (
    post_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    forum_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    parent_post_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_forums
CREATE TABLE IF NOT EXISTS erp_lms_forums (
    forum_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT,
    lesson_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_lessons
CREATE TABLE IF NOT EXISTS erp_lms_lessons (
    lesson_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_modules
CREATE TABLE IF NOT EXISTS erp_lms_modules (
    module_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_peer_reviews
CREATE TABLE IF NOT EXISTS erp_lms_peer_reviews (
    review_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_point_logs
CREATE TABLE IF NOT EXISTS erp_lms_point_logs (
    log_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    badge_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_question_bank
CREATE TABLE IF NOT EXISTS erp_lms_question_bank (
    qbank_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_questions
CREATE TABLE IF NOT EXISTS erp_lms_questions (
    question_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_quizzes
CREATE TABLE IF NOT EXISTS erp_lms_quizzes (
    quiz_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    lesson_id BIGINT,
    subject_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_rubrics
CREATE TABLE IF NOT EXISTS erp_lms_rubrics (
    rubric_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_student_progress
CREATE TABLE IF NOT EXISTS erp_lms_student_progress (
    progress_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_submissions
CREATE TABLE IF NOT EXISTS erp_lms_submissions (
    submission_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_topics
CREATE TABLE IF NOT EXISTS erp_lms_topics (
    topic_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_virtual_attendance
CREATE TABLE IF NOT EXISTS erp_lms_virtual_attendance (
    attendance_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_lms_virtual_sessions
CREATE TABLE IF NOT EXISTS erp_lms_virtual_sessions (
    session_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_maint_facilities
CREATE TABLE IF NOT EXISTS erp_maint_facilities (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    capacity INT,
    is_bookable BOOLEAN,
    description VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_maint_facility_bookings
CREATE TABLE IF NOT EXISTS erp_maint_facility_bookings (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    facility_id BIGINT NOT NULL,
    booked_by_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    purpose VARCHAR(255),
    status VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_maint_work_orders
CREATE TABLE IF NOT EXISTS erp_maint_work_orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    priority VARCHAR(255),
    status VARCHAR(255),
    request_date DATETIME,
    assigned_technician_id BIGINT,
    asset_id BIGINT,
    completion_date DATETIME,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_messages
CREATE TABLE IF NOT EXISTS erp_messages (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sent_at DATETIME,
    is_read BOOLEAN,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_performance_criteria
CREATE TABLE IF NOT EXISTS erp_performance_criteria (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_performance_cycles
CREATE TABLE IF NOT EXISTS erp_performance_cycles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_performance_review_details
CREATE TABLE IF NOT EXISTS erp_performance_review_details (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    criteria_id BIGINT NOT NULL,
    rating INT,
    comments VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_performance_reviews
CREATE TABLE IF NOT EXISTS erp_performance_reviews (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    cycle_id BIGINT NOT NULL,
    reviewer_id BIGINT,
    review_date DATE,
    overall_rating DECIMAL(19,2),
    comments VARCHAR(255),
    status VARCHAR(255),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_reporting_mis_reports
CREATE TABLE IF NOT EXISTS erp_reporting_mis_reports (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    description VARCHAR(255),
    last_run_date DATETIME,
    report_data TEXT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: erp_rooms
CREATE TABLE IF NOT EXISTS erp_rooms (
    room_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    room_type VARCHAR(50) NOT NULL, -- CLASSROOM, LAB, COMPUTER_LAB, HALL
    building VARCHAR(100),
    description TEXT,
    organization_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_room_name (room_name),
    INDEX idx_room_type (room_type),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: erp_sections
CREATE TABLE IF NOT EXISTS erp_sections (
    erp_section_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(250) NOT NULL,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_entity_type (entity_type),
    INDEX idx_section_name (section_name),
    INDEX idx_display_order (display_order),
    INDEX idx_is_active (is_active),
    UNIQUE KEY unique_entity_section (entity_type, section_name)
);

-- Table generated from JPA: erp_student_registrations
CREATE TABLE IF NOT EXISTS erp_student_registrations (
    registration_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_support_tickets
CREATE TABLE IF NOT EXISTS erp_support_tickets (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    author_id BIGINT NOT NULL,
    assigned_to_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: erp_tenants
CREATE TABLE IF NOT EXISTS erp_tenants (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE,
    tenant_name VARCHAR(100) NOT NULL,
    db_host VARCHAR(100) NOT NULL,
    db_name VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'Active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table generated from JPA: erp_ticket_comments
CREATE TABLE IF NOT EXISTS erp_ticket_comments (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME,
    ticket_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: erp_timetables
CREATE TABLE IF NOT EXISTS erp_timetables (
    timetable_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    room_id BIGINT,
    day_of_week VARCHAR(20) NOT NULL,
    period_number INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    organization_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (class_id) REFERENCES erp_class (class_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES staff (staff_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects (subject_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES erp_rooms (room_id) ON DELETE SET NULL,
    INDEX idx_class_id (class_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_day_of_week (day_of_week),
    INDEX idx_academic_year (academic_year),
    INDEX idx_is_active (is_active)
);

-- Table generated from JPA: erp_tpd_competencies
CREATE TABLE IF NOT EXISTS erp_tpd_competencies (
    competency_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_tpd_cpd_ledger
CREATE TABLE IF NOT EXISTS erp_tpd_cpd_ledger (
    ledger_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_tpd_evaluations
CREATE TABLE IF NOT EXISTS erp_tpd_evaluations (
    evaluation_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_tpd_evidence
CREATE TABLE IF NOT EXISTS erp_tpd_evidence (
    evidence_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    approved_by BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_tpd_portfolios
CREATE TABLE IF NOT EXISTS erp_tpd_portfolios (
    portfolio_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_tpd_skill_assessments
CREATE TABLE IF NOT EXISTS erp_tpd_skill_assessments (
    assessment_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    competency_id BIGINT NOT NULL,
    manager_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_tpd_training_attendance
CREATE TABLE IF NOT EXISTS erp_tpd_training_attendance (
    attendance_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: erp_tpd_training_events
CREATE TABLE IF NOT EXISTS erp_tpd_training_events (
    event_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    venue_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: exams
CREATE TABLE IF NOT EXISTS exams (
    exam_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_name VARCHAR(255) NOT NULL,
    exam_code VARCHAR(50),
    exam_type VARCHAR(50),
    exam_date DATE,
    start_time TIME,
    end_time TIME,
    duration_minutes INT,
    total_marks DECIMAL(10,2),
    passing_marks DECIMAL(10,2),
    subject_id BIGINT,
    class_id BIGINT,
    academic_year VARCHAR(50),
    semester VARCHAR(50),
    instructions TEXT,
    organization_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: field_mapping_templates
CREATE TABLE IF NOT EXISTS field_mapping_templates (
    field_mapping_template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_label VARCHAR(255) NOT NULL,
    is_required BOOLEAN DEFAULT false,
    data_type VARCHAR(50),
    suggestions TEXT,
    section VARCHAR(100),
    display_order INT DEFAULT 0
);

-- Table from SQL file: field_mappings
CREATE TABLE IF NOT EXISTS field_mappings (
    field_mapping_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_session_id VARCHAR(50) NOT NULL,
    source_column VARCHAR(255),
    source_index INT,
    target_field VARCHAR(255),
    target_field_label VARCHAR(255),
    is_required BOOLEAN DEFAULT false,
    data_type VARCHAR(50)
);

-- Table generated from JPA: fin_scholarship_applications
CREATE TABLE IF NOT EXISTS fin_scholarship_applications (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    approved_by VARCHAR(255),
    approval_date DATE,
    student_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: fin_scholarship_categories
CREATE TABLE IF NOT EXISTS fin_scholarship_categories (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table generated from JPA: fin_scholarship_disbursements
CREATE TABLE IF NOT EXISTS fin_scholarship_disbursements (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    invoice_id BIGINT,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    owner_id BIGINT,
    is_active INT DEFAULT 1
);

-- Table from SQL file: grading_scales
CREATE TABLE IF NOT EXISTS grading_scales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    letter_grade VARCHAR(10) NOT NULL,
    min_percentage DOUBLE NOT NULL,
    max_percentage DOUBLE NOT NULL,
    grade_point DOUBLE NOT NULL,
    organization_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table from SQL file: health_records
CREATE TABLE IF NOT EXISTS health_records (
    health_record_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES iam_users (user_id) ON DELETE SET NULL,
    INDEX idx_student_id (student_id),
    INDEX idx_record_type (record_type),
    INDEX idx_requires_attention (requires_attention),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: iam_users
CREATE TABLE IF NOT EXISTS iam_users (
    user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(50) NOT NULL,
    tenant_id VARCHAR(36),
    organization_id BIGINT,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    last_login_date DATETIME,
    password_change_date DATETIME,
    avatar_url VARCHAR(500),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_user_type (user_type),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: import_history
CREATE TABLE IF NOT EXISTS import_history (
    import_history_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_name VARCHAR(100) NOT NULL,
    import_type VARCHAR(50) NOT NULL,
    record_count INT NOT NULL DEFAULT 0,
    source VARCHAR(255),
    imported_by VARCHAR(100) NOT NULL,
    import_start_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    import_end_time DATETIME,
    import_status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000),
    notes VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    INDEX idx_entity_name (entity_name),
    INDEX idx_import_type (import_type),
    INDEX idx_import_status (import_status),
    INDEX idx_created_at (created_at),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: import_results
CREATE TABLE IF NOT EXISTS import_results (
    import_result_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_session_id VARCHAR(50) NOT NULL,
    row_num INT,
    record_id VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    data TEXT,
    errors TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table from SQL file: import_sessions
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
    status VARCHAR(20),
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    imported_at DATETIME,
    updated_at DATETIME,
    added_records INT DEFAULT 0,
    updated_records INT DEFAULT 0,
    skipped_records INT DEFAULT 0,
    failed_records INT DEFAULT 0,
    success_rate DOUBLE DEFAULT 0.0
);

-- Table from SQL file: login_history
CREATE TABLE IF NOT EXISTS login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    login_time DATETIME NOT NULL,
    ip_address VARCHAR(50),
    browser VARCHAR(255),
    status VARCHAR(20),
    failure_reason VARCHAR(500)
);

-- Table from SQL file: organization_settings
CREATE TABLE IF NOT EXISTS organization_settings (
    organization_settings_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL UNIQUE,
    default_theme VARCHAR(50),
    theme_primary_color VARCHAR(7),
    theme_secondary_color VARCHAR(7),
    theme_accent_color VARCHAR(7),
    default_language VARCHAR(10),
    default_timezone VARCHAR(50),
    default_date_format VARCHAR(20),
    default_time_format VARCHAR(20),
    default_currency VARCHAR(3),
    items_per_page INT,
    default_list_view VARCHAR(20),
    enable_smart_filters BOOLEAN,
    enable_column_customization BOOLEAN,
    enable_bulk_operations BOOLEAN,
    enable_audit_logging BOOLEAN,
    data_retention_days INT,
    max_file_upload_mb INT,
    smtp_server VARCHAR(255),
    smtp_port INT,
    smtp_username VARCHAR(100),
    smtp_password VARCHAR(255),
    smtp_from_email VARCHAR(100),
    smtp_from_name VARCHAR(100),
    email_templates_enabled BOOLEAN,
    enable_two_factor_auth BOOLEAN,
    session_timeout_minutes INT,
    password_expiry_days INT,
    min_password_length INT,
    require_special_characters BOOLEAN,
    enable_email_notifications BOOLEAN,
    enable_sms_notifications BOOLEAN,
    enable_in_app_notifications BOOLEAN,
    notification_sound_enabled BOOLEAN,
    api_rate_limit_per_minute INT,
    enable_api_documentation BOOLEAN,
    enable_webhooks BOOLEAN,
    created_by VARCHAR(100) NOT NULL,
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME
);

-- Table from SQL file: organizations
CREATE TABLE IF NOT EXISTS organizations (
    organization_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_code (code),
    INDEX idx_name (name),
    INDEX idx_type (type),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: parent_student_relations
CREATE TABLE IF NOT EXISTS parent_student_relations (
    parent_student_relation_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (parent_id) REFERENCES parents (parent_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    INDEX idx_parent_id (parent_id),
    INDEX idx_student_id (student_id),
    INDEX idx_relationship_type (relationship_type),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: parents
CREATE TABLE IF NOT EXISTS parents (
    parent_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    UNIQUE KEY unique_user_id (user_id),
    FOREIGN KEY (address_id) REFERENCES addresses (address_id),
    FOREIGN KEY (user_id) REFERENCES iam_users (user_id),
    INDEX idx_email (email),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: payment_transactions
CREATE TABLE IF NOT EXISTS payment_transactions (
    payment_transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    transaction_type VARCHAR(20) NOT NULL,
    payment_method VARCHAR(50),
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    transaction_status VARCHAR(20) NOT NULL,
    payment_gateway VARCHAR(50),
    gateway_response JSON,
    error_message TEXT,
    transaction_date DATETIME NOT NULL,
    processed_at DATETIME,
    refunded_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_subscription_id (subscription_id),
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_transaction_status (transaction_status),
    INDEX idx_transaction_date (transaction_date)
);

-- Table from SQL file: permissions
CREATE TABLE IF NOT EXISTS permissions (
    permission_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    resource VARCHAR(50),
    action VARCHAR(50),
    system_permission BOOLEAN NOT NULL DEFAULT false,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_name (name),
    INDEX idx_resource_action (resource, action),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: pricing_plans
CREATE TABLE IF NOT EXISTS pricing_plans (
    pricing_plan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(50) NOT NULL UNIQUE,
    plan_type VARCHAR(20) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    price_monthly DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    price_yearly DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    max_users INT,
    max_storage_gb INT,
    features JSON,
    is_active BOOLEAN DEFAULT TRUE,
    is_trial_eligible BOOLEAN DEFAULT FALSE,
    trial_days INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    INDEX idx_plan_name (plan_name),
    INDEX idx_plan_type (plan_type),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: recycle_bin
CREATE TABLE IF NOT EXISTS recycle_bin (
    recycle_bin_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    deleted_by VARCHAR(100) NOT NULL,
    deleted_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deletion_reason VARCHAR(500),
    entity_data TEXT,
    related_entity_count INT DEFAULT 0,
    INDEX idx_entity_type (entity_type),
    INDEX idx_deleted_time (deleted_time),
    INDEX idx_deleted_by (deleted_by)
);

-- Table from SQL file: role_permissions
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (permission_id) ON DELETE CASCADE,
    INDEX idx_permission_id (permission_id)
);

-- Table from SQL file: roles
CREATE TABLE IF NOT EXISTS roles (
    role_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    system_role BOOLEAN NOT NULL DEFAULT false,
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_name (name),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: staff
CREATE TABLE IF NOT EXISTS staff (
    staff_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    staff_identifier VARCHAR(20) NOT NULL UNIQUE,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    UNIQUE KEY unique_user_id (user_id),
    FOREIGN KEY (address_id) REFERENCES addresses (address_id),
    FOREIGN KEY (user_id) REFERENCES iam_users (user_id),
    INDEX idx_staff_id (staff_id),
    INDEX idx_email (email),
    INDEX idx_employment_status (employment_status),
    INDEX idx_staff_type (staff_type),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: student_guardian_info
CREATE TABLE IF NOT EXISTS student_guardian_info (
    student_guardian_info_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,

-- Father Information
father_name VARCHAR(100),
father_occupation VARCHAR(100),
father_phone VARCHAR(20),
father_email VARCHAR(100),

-- Mother Information
mother_name VARCHAR(100),
mother_occupation VARCHAR(100),
mother_phone VARCHAR(20),
mother_email VARCHAR(100),

-- Guardian Information (if different from parents)
guardian_name VARCHAR(100),
guardian_relation VARCHAR(50),
guardian_phone VARCHAR(20),
guardian_email VARCHAR(100),
guardian_address VARCHAR(300),

-- Audit fields from BaseEntity


created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: student_medical_info
CREATE TABLE IF NOT EXISTS student_medical_info (
    student_medical_info_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,

-- Critical Health Information
allergies TEXT, medical_conditions TEXT, medications TEXT,

-- Medical Contact Information
doctor_name VARCHAR(100),
doctor_phone VARCHAR(20),
hospital_preference VARCHAR(200),

-- Insurance Information
insurance_provider VARCHAR(100), insurance_policy_number VARCHAR(50),

-- Audit fields from BaseEntity


created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: student_promotion_audit_log
CREATE TABLE IF NOT EXISTS student_promotion_audit_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id BIGINT,
    record_id BIGINT,
    action_type VARCHAR(50) NOT NULL,
    performed_by BIGINT NOT NULL,
    target_student_id BIGINT,
    old_value VARCHAR(500),
    new_value VARCHAR(500),
    details TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_batch_id (batch_id),
    INDEX idx_record_id (record_id),
    INDEX idx_action_type (action_type),
    INDEX idx_performed_by (performed_by),
    INDEX idx_created_at (created_at)
);

-- Table from SQL file: student_promotion_batch
CREATE TABLE IF NOT EXISTS student_promotion_batch (
    batch_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_name VARCHAR(200) NOT NULL,
    academic_year_from VARCHAR(20) NOT NULL,
    academic_year_to VARCHAR(20) NOT NULL,
    promotion_date DATE NOT NULL,
    initiated_by BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_students INT DEFAULT 0,
    successful_promotions INT DEFAULT 0,
    failed_promotions INT DEFAULT 0,
    processed_students INT DEFAULT 0,
    progress_percentage DOUBLE DEFAULT 0.0,
    current_phase VARCHAR(50),
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    completed_at DATETIME,
    started_at DATETIME,
    INDEX idx_status (status),
    INDEX idx_initiated_by (initiated_by),
    INDEX idx_promotion_date (promotion_date),
    INDEX idx_created_at (created_at)
);

-- Table from SQL file: student_promotion_record
CREATE TABLE IF NOT EXISTS student_promotion_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    from_grade_level VARCHAR(20) NOT NULL,
    to_grade_level VARCHAR(20) NOT NULL,
    from_section VARCHAR(20),
    to_section VARCHAR(20),
    promotion_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    failure_reason VARCHAR(500),
    promoted_at DATETIME,
    rolled_back_at DATETIME,
    student_snapshot JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    INDEX idx_batch_id (batch_id),
    INDEX idx_student_id (student_id),
    INDEX idx_promotion_status (promotion_status),
    FOREIGN KEY (batch_id) REFERENCES student_promotion_batch(batch_id) ON DELETE CASCADE
);

-- Table from SQL file: students
CREATE TABLE IF NOT EXISTS students (
    student_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    student_identifier VARCHAR(20) NOT NULL UNIQUE,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    UNIQUE KEY unique_user_id (user_id),
    FOREIGN KEY (address_id) REFERENCES addresses (address_id),
    FOREIGN KEY (user_id) REFERENCES iam_users (user_id),
    INDEX idx_student_id (student_id),
    INDEX idx_email (email),
    INDEX idx_grade_level (grade_level),
    INDEX idx_enrollment_status (enrollment_status),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: subjects
CREATE TABLE IF NOT EXISTS subjects (
    subject_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_subject_code (subject_code),
    INDEX idx_grade_level (grade_level),
    INDEX idx_category (category),
    INDEX idx_is_active (is_active)
);

-- Table from SQL file: subscription_history
CREATE TABLE IF NOT EXISTS subscription_history (
    subscription_change_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    previous_plan_id BIGINT,
    new_plan_id BIGINT NOT NULL,
    change_type VARCHAR(20) NOT NULL,
    change_reason TEXT,
    previous_status VARCHAR(20),
    new_status VARCHAR(20),
    effective_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    INDEX idx_subscription_id (subscription_id),
    INDEX idx_user_id (user_id),
    INDEX idx_change_type (change_type),
    INDEX idx_effective_date (effective_date)
);

-- Table from SQL file: user_roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES iam_users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,
    INDEX idx_role_id (role_id)
);

-- Table from SQL file: user_settings
CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    default_list_view VARCHAR(20) DEFAULT 'table',
    records_per_page INT DEFAULT 25,
    theme VARCHAR(50) DEFAULT '#0099cc',
    theme_primary_color VARCHAR(50) DEFAULT '#0099cc',
    theme_secondary_color VARCHAR(50),
    theme_accent_color VARCHAR(50),
    theme_custom_colors JSON,
    grid_columns JSON,
    column_widths JSON,
    hidden_columns JSON,
    list_sidebar_expanded BOOLEAN DEFAULT TRUE,
    saved_filters JSON,
    saved_views JSON,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_is_active (is_active),
    UNIQUE KEY uk_user_org (user_id, organization_id)
);

-- Table from SQL file: user_subscriptions
CREATE TABLE IF NOT EXISTS user_subscriptions (
    user_subscription_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    subscription_status VARCHAR(20) NOT NULL,
    billing_cycle VARCHAR(20),
    trial_start_date DATE,
    trial_end_date DATE,
    subscription_start_date DATE NOT NULL,
    subscription_end_date DATE,
    next_billing_date DATE,
    is_auto_renew BOOLEAN DEFAULT TRUE,
    payment_status VARCHAR(20),
    amount_paid DECIMAL(10, 2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_plan_id (plan_id),
    INDEX idx_subscription_status (subscription_status),
    INDEX idx_next_billing_date (next_billing_date)
);

SET FOREIGN_KEY_CHECKS = 1;