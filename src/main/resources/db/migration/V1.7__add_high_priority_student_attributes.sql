-- ============================================================================
-- Migration V1.7: Add High-Priority Student Attributes (Normalized Structure)
-- ============================================================================
-- This migration adds comprehensive student attributes using normalized tables
-- to avoid bloating the main students table with 40+ columns.
--
-- Table Structure:
-- 1. students - Add only essential direct fields (section, nationality, blood_group, etc.)
-- 2. student_guardian_info - Separate table for guardian/parent information (FK to students)
-- 3. student_medical_info - Separate table for medical/health information (FK to students)
-- ============================================================================

-- ============================================================================
-- Part 1: Add Essential Fields Directly to Students Table
-- ============================================================================

-- Add high-priority personal/demographic fields
ALTER TABLE students
ADD COLUMN nationality VARCHAR(50) COMMENT 'Student nationality/citizenship',
ADD COLUMN blood_group VARCHAR(10) COMMENT 'Blood type (A+, B+, O+, AB+, etc.)',
ADD COLUMN photo_url VARCHAR(500) COMMENT 'Profile photo path';

-- Add critical academic fields (section was in XML but missing from entity!)
ALTER TABLE students
ADD COLUMN section VARCHAR(20) COMMENT 'Class section (A, B, C, etc.)',
ADD COLUMN admission_number VARCHAR(50) UNIQUE COMMENT 'Admission/registration number',
ADD COLUMN admission_date DATE COMMENT 'Date of admission';

-- Add indexes for frequently queried fields
ALTER TABLE students
ADD INDEX idx_section (section),
ADD INDEX idx_admission_number (admission_number),
ADD INDEX idx_blood_group (blood_group),
ADD INDEX idx_nationality (nationality);

-- ============================================================================
-- Part 2: Create Student Guardian Information Table
-- ============================================================================
-- This table stores parent/guardian details with FK pointing to students table
-- One-to-One relationship: Each student has one guardian info record

CREATE TABLE IF NOT EXISTS student_guardian_info (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE COMMENT 'FK to students table',

-- Father Information
father_name VARCHAR(100) COMMENT 'Father''s full name',
father_occupation VARCHAR(100) COMMENT 'Father''s occupation',
father_phone VARCHAR(20) COMMENT 'Father''s contact number',
father_email VARCHAR(100) COMMENT 'Father''s email address',

-- Mother Information
mother_name VARCHAR(100) COMMENT 'Mother''s full name',
mother_occupation VARCHAR(100) COMMENT 'Mother''s occupation',
mother_phone VARCHAR(20) COMMENT 'Mother''s contact number',
mother_email VARCHAR(100) COMMENT 'Mother''s email address',

-- Guardian Information (if different from parents)
guardian_name VARCHAR(100) COMMENT 'Legal guardian''s name',
guardian_relation VARCHAR(50) COMMENT 'Relationship to student',
guardian_phone VARCHAR(20) COMMENT 'Guardian''s contact number',
guardian_email VARCHAR(100) COMMENT 'Guardian''s email address',
guardian_address VARCHAR(300) COMMENT 'Guardian address if different from student',

-- Audit fields
created_by BIGINT,
modified_by BIGINT,
created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
is_active INT DEFAULT 1,

-- Foreign key constraint
CONSTRAINT fk_guardian_student FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,

-- Indexes
INDEX idx_student_id (student_id),
    INDEX idx_father_phone (father_phone),
    INDEX idx_mother_phone (mother_phone),
    INDEX idx_guardian_phone (guardian_phone),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
COMMENT='Stores guardian/parent information for students';

-- ============================================================================
-- Part 3: Create Student Medical Information Table
-- ============================================================================
-- This table stores medical/health details with FK pointing to students table
-- One-to-One relationship: Each student has one medical info record

CREATE TABLE IF NOT EXISTS student_medical_info (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE COMMENT 'FK to students table',

-- Critical Health Information
allergies TEXT COMMENT 'Known allergies (comma-separated or detailed)',
medical_conditions TEXT COMMENT 'Chronic medical conditions',
medications TEXT COMMENT 'Current medications being taken',

-- Medical Contact Information
doctor_name VARCHAR(100) COMMENT 'Family doctor name',
doctor_phone VARCHAR(20) COMMENT 'Doctor contact number',
hospital_preference VARCHAR(200) COMMENT 'Preferred hospital for emergencies',

-- Insurance Information
insurance_provider VARCHAR(100) COMMENT 'Health insurance provider',
insurance_policy_number VARCHAR(50) COMMENT 'Insurance policy number',

-- Audit fields
created_by BIGINT,
modified_by BIGINT,
created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
is_active INT DEFAULT 1,

-- Foreign key constraint
CONSTRAINT fk_medical_student FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,

-- Indexes
INDEX idx_student_id (student_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
COMMENT='Stores medical and health information for students';

-- ============================================================================
-- Part 4: Data Migration (if needed)
-- ============================================================================
-- If there are existing students, create default guardian and medical records

-- Create empty guardian info records for existing students
INSERT INTO
    student_guardian_info (
        student_id,
        created_by,
        created_time,
        is_active
    )
SELECT id, 'migration', NOW(), 1
FROM students
WHERE
    id NOT IN(
        SELECT student_id
        FROM student_guardian_info
    )
    AND is_active = 1;

-- Create empty medical info records for existing students
INSERT INTO
    student_medical_info (
        student_id,
        created_by,
        created_time,
        is_active
    )
SELECT id, 'migration', NOW(), 1
FROM students
WHERE
    id NOT IN(
        SELECT student_id
        FROM student_medical_info
    )
    AND is_active = 1;

-- ============================================================================
-- Part 5: Update Existing Data (if applicable)
-- ============================================================================
-- Set default academic year for existing students
UPDATE students
SET
    admission_date = enrollment_date
WHERE
    admission_date IS NULL
    AND enrollment_date IS NOT NULL;

-- ============================================================================
-- Rollback Script (commented out - uncomment if rollback needed)
-- ============================================================================
/*
-- Drop tables in reverse order
DROP TABLE IF EXISTS student_medical_info;
DROP TABLE IF EXISTS student_guardian_info;

-- Remove columns from students table
ALTER TABLE students
DROP COLUMN nationality,
DROP COLUMN blood_group,
DROP COLUMN photo_url,
DROP COLUMN section,
DROP COLUMN admission_number,
DROP COLUMN admission_date,
DROP INDEX idx_section,
DROP INDEX idx_admission_number,
DROP INDEX idx_blood_group,
DROP INDEX idx_nationality;
*/

-- ============================================================================
-- End of Migration V1.7
-- ============================================================================