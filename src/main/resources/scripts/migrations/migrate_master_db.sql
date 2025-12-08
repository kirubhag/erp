-- Migration script for IAM_MasterDB
-- Adds missing columns and tables only if they don't exist

-- Add show_type column to erp_fields if it doesn't exist
SET @dbname = DATABASE();

SET @tablename = 'erp_fields';

SET @columnname = 'show_type';

SET
    @preparedStatement = (
        SELECT IF(
                (
                    SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE (table_name = @tablename)
                        AND (table_schema = @dbname)
                        AND (column_name = @columnname)
                ) > 0, 'SELECT 1', 'ALTER TABLE erp_fields ADD COLUMN show_type INT DEFAULT 0 AFTER column_width'
            )
    );

PREPARE alterIfNotExists FROM @preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

-- Create student_guardian_info table if it doesn't exist
CREATE TABLE IF NOT EXISTS student_guardian_info (
    student_guardian_info_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_is_active (is_active)
);

-- Create student_medical_info table if it doesn't exist
CREATE TABLE IF NOT EXISTS student_medical_info (
    student_medical_info_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
    created_time DATETIME NOT NULL,
    modified_time DATETIME,
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id),
    INDEX idx_is_active (is_active)
);

-- Create erp_attachments table if it doesn't exist
CREATE TABLE IF NOT EXISTS erp_attachments (
    erp_attachment_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL UNIQUE,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    attachment_type VARCHAR(50) NOT NULL,
    organization_id BIGINT NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    description VARCHAR(500),
    uploaded_by BIGINT,
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