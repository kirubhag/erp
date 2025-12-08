-- =============================================================================
-- Phase 8.5: Student Extended Information Tables (Depends on students)
-- =============================================================================

-- Student Guardian Information Table
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
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_is_active (is_active)
);

-- Student Medical Information Table
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
insurance_provider VARCHAR(100),
insurance_policy_number VARCHAR(50),

-- Audit fields from BaseEntity
created_by VARCHAR(100),
    modified_by VARCHAR(100),
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_is_active (is_active)
);

-- ERP Attachments Table (For file uploads - avatars, documents, etc.)
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
    created_time DATETIME NOT NULL,
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