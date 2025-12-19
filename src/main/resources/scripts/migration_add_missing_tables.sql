-- ============================================================================
-- Migration Script: Add Missing Tables to Existing Tenant Database
-- Purpose: Add 7 tables that were missing from tenant_schema.sql
-- Date: Migration to sync tenant schema with master schema
-- Target: erpdbea9fbd46 (and any other existing tenant databases)
-- ============================================================================
-- Tables Added:
-- 1. login_history
-- 2. student_promotion_batch
-- 3. student_promotion_record
-- 4. student_promotion_audit_log
-- 5. courses
-- 6. exams
-- 7. grading_scales
-- ============================================================================

-- Login History Table
CREATE TABLE IF NOT EXISTS login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    browser VARCHAR(200),
    status VARCHAR(20) DEFAULT 'SUCCESS',
    failure_reason VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES iam_users (user_id) ON DELETE CASCADE,
    INDEX idx_user_history (user_id, login_time),
    INDEX idx_login_time (login_time)
) COMMENT = 'Tracks user login history and attempts';

-- Student Promotion Batch
CREATE TABLE IF NOT EXISTS student_promotion_batch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_name VARCHAR(255) NOT NULL,
    from_academic_year VARCHAR(50) NOT NULL,
    to_academic_year VARCHAR(50) NOT NULL,
    from_grade_level VARCHAR(50) NOT NULL,
    to_grade_level VARCHAR(50) NOT NULL,
    promotion_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    total_students INT DEFAULT 0,
    promoted_students INT DEFAULT 0,
    failed_students INT DEFAULT 0,
    notes TEXT,
    created_by BIGINT,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by BIGINT,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    organization_id BIGINT NOT NULL,
    INDEX idx_promotion_org (organization_id),
    INDEX idx_promotion_status (status),
    INDEX idx_promotion_date (promotion_date)
) COMMENT = 'Student promotion batches';

-- Student Promotion Record
CREATE TABLE IF NOT EXISTS student_promotion_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    from_class_id BIGINT,
    to_class_id BIGINT,
    from_section VARCHAR(50),
    to_section VARCHAR(50),
    promotion_status VARCHAR(50) DEFAULT 'PENDING',
    reason VARCHAR(500),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES student_promotion_batch(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    INDEX idx_promotion_record_batch (batch_id),
    INDEX idx_promotion_record_student (student_id),
    INDEX idx_promotion_record_status (promotion_status)
) COMMENT = 'Individual student promotion records';

-- Student Promotion Audit Log
CREATE TABLE IF NOT EXISTS student_promotion_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id BIGINT,
    batch_id BIGINT,
    action VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    performed_by BIGINT,
    performed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    notes TEXT,
    INDEX idx_audit_record (record_id),
    INDEX idx_audit_batch (batch_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_time (performed_at)
) COMMENT = 'Audit trail for student promotions';

-- Courses Table
CREATE TABLE IF NOT EXISTS courses (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(50) NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    description TEXT,
    credits INT,
    duration_hours INT,
    department VARCHAR(100),
    prerequisites TEXT,
    learning_objectives TEXT,
    assessment_methods TEXT,
    textbooks TEXT,
    is_active TINYINT(1) DEFAULT 1,
    organization_id BIGINT NOT NULL,
    owner_id BIGINT,
    created_by BIGINT,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by BIGINT,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_course_code_org (course_code, organization_id),
    INDEX idx_course_org (organization_id),
    INDEX idx_course_active (is_active),
    INDEX idx_course_department (department)
) COMMENT = 'Course catalog';

-- Exams Table
CREATE TABLE IF NOT EXISTS exams (
    exam_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_name VARCHAR(255) NOT NULL,
    exam_code VARCHAR(50),
    exam_type VARCHAR(50),
    description TEXT,
    course_id BIGINT,
    class_id BIGINT,
    subject_id BIGINT,
    academic_year VARCHAR(50),
    term VARCHAR(50),
    exam_date DATE,
    start_time TIME,
    end_time TIME,
    duration_minutes INT,
    total_marks DECIMAL(10,2),
    passing_marks DECIMAL(10,2),
    room VARCHAR(100),
    instructions TEXT,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    is_published TINYINT(1) DEFAULT 0,
    organization_id BIGINT NOT NULL,
    owner_id BIGINT,
    created_by BIGINT,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by BIGINT,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE SET NULL,
    INDEX idx_exam_org (organization_id),
    INDEX idx_exam_date (exam_date),
    INDEX idx_exam_status (status),
    INDEX idx_exam_course (course_id),
    INDEX idx_exam_class (class_id),
    INDEX idx_exam_subject (subject_id)
) COMMENT = 'Exam schedules and details';

-- Grading Scales Table
CREATE TABLE IF NOT EXISTS grading_scales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    letter_grade VARCHAR(10) NOT NULL,
    min_percentage DECIMAL(5,2) NOT NULL,
    max_percentage DECIMAL(5,2) NOT NULL,
    grade_point DECIMAL(3,2),
    description VARCHAR(255),
    organization_id BIGINT NOT NULL,
    created_by BIGINT,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by BIGINT,
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_grading_org (organization_id),
    INDEX idx_grading_range (min_percentage, max_percentage),
    UNIQUE KEY unique_grading_letter_org (letter_grade, organization_id)
) COMMENT = 'Grading scale definitions';

-- ============================================================================
-- Migration Complete - 7 Tables Added
-- ============================================================================
-- Next Steps:
-- 1. Verify table creation: 
--    SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'erpdbea9fbd46';
--    Expected: 53 tables (was 46)
--
-- 2. Test API endpoints:
--    - GET /api/academic/grading-scales
--    - GET /api/promotions/batches
--    - GET /api/courses
--    - GET /api/exams
--
-- 3. Check application logs for any errors
-- ============================================================================
