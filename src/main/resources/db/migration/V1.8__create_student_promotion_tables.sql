-- Migration V1.8: Create Student Promotion System Tables
-- Description: Tables for tracking student grade promotions, bulk operations, and audit logs

-- Table: student_promotion_batch
-- Purpose: Track bulk promotion operations with metadata
CREATE TABLE student_promotion_batch (
    batch_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_name VARCHAR(200) NOT NULL,
    academic_year_from VARCHAR(20) NOT NULL,
    academic_year_to VARCHAR(20) NOT NULL,
    promotion_date DATE NOT NULL,
    initiated_by BIGINT NOT NULL,
    status VARCHAR(250) DEFAULT 'PENDING',
    total_students INT DEFAULT 0,
    successful_promotions INT DEFAULT 0,
    failed_promotions INT DEFAULT 0,
    processed_students INT DEFAULT 0,
    progress_percentage DECIMAL(5,2) DEFAULT 0.00,
    current_phase VARCHAR(50),
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at DATETIME,
    started_at DATETIME,
    INDEX idx_academic_year (academic_year_from, academic_year_to),
    INDEX idx_status (status),
    INDEX idx_promotion_date (promotion_date),
    INDEX idx_initiated_by (initiated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: student_promotion_record
-- Purpose: Individual student promotion records with detailed tracking
CREATE TABLE student_promotion_record (
    record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    from_grade_level VARCHAR(20) NOT NULL,
    to_grade_level VARCHAR(20) NOT NULL,
    from_section VARCHAR(20),
    to_section VARCHAR(20),
    promotion_status VARCHAR(250) DEFAULT 'PENDING',
    failure_reason VARCHAR(500),
    promoted_at DATETIME,
    rolled_back_at DATETIME,
    student_snapshot JSON COMMENT 'Snapshot of student data before promotion',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES student_promotion_batch(batch_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    INDEX idx_batch_student (batch_id, student_id),
    INDEX idx_student_promotions (student_id, promoted_at),
    INDEX idx_promotion_status (promotion_status),
    INDEX idx_grade_transition (from_grade_level, to_grade_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: student_promotion_validation_rule
-- Purpose: Configurable validation rules for promotions
CREATE TABLE student_promotion_validation_rule (
    rule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(100) NOT NULL UNIQUE,
    rule_type VARCHAR(250) NOT NULL,
    from_grade VARCHAR(20),
    to_grade VARCHAR(20),
    validation_criteria JSON COMMENT 'Flexible validation criteria in JSON',
    is_active BOOLEAN DEFAULT TRUE,
    error_message VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rule_type (rule_type),
    INDEX idx_active_rules (is_active),
    INDEX idx_grade_rules (from_grade, to_grade)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: student_promotion_audit_log
-- Purpose: Comprehensive audit trail for all promotion activities
CREATE TABLE student_promotion_audit_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT,
    record_id BIGINT,
    action_type VARCHAR(250) NOT NULL,
    performed_by BIGINT NOT NULL,
    target_student_id BIGINT,
    old_value VARCHAR(500),
    new_value VARCHAR(500),
    details TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES student_promotion_batch(batch_id) ON DELETE CASCADE,
    FOREIGN KEY (record_id) REFERENCES student_promotion_record(record_id) ON DELETE CASCADE,
    INDEX idx_batch_logs (batch_id, created_at),
    INDEX idx_student_logs (target_student_id, created_at),
    INDEX idx_action_type (action_type),
    INDEX idx_performed_by (performed_by, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: academic_year_config
-- Purpose: Manage academic year configurations and timelines
CREATE TABLE academic_year_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    academic_year VARCHAR(20) NOT NULL UNIQUE,
    year_start_date DATE NOT NULL,
    year_end_date DATE NOT NULL,
    promotion_window_start DATE,
    promotion_window_end DATE,
    is_current BOOLEAN DEFAULT FALSE,
    status VARCHAR(250) DEFAULT 'UPCOMING',
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_current_year (is_current),
    INDEX idx_year_status (status),
    INDEX idx_promotion_window (promotion_window_start, promotion_window_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default validation rules
INSERT INTO student_promotion_validation_rule (rule_name, rule_type, from_grade, to_grade, validation_criteria, error_message) VALUES
('KINDERGARTEN_TO_GRADE1', 'GRADE_TRANSITION', 'KINDERGARTEN', 'GRADE_1', '{"min_age_months": 72, "required_attendance_percentage": 75}', 'Student must be at least 6 years old and have 75% attendance'),
('GRADE1_TO_GRADE2', 'GRADE_TRANSITION', 'GRADE_1', 'GRADE_2', '{"required_attendance_percentage": 80}', 'Student must have at least 80% attendance'),
('GRADE2_TO_GRADE3', 'GRADE_TRANSITION', 'GRADE_2', 'GRADE_3', '{"required_attendance_percentage": 80}', 'Student must have at least 80% attendance'),
('GRADE3_TO_GRADE4', 'GRADE_TRANSITION', 'GRADE_3', 'GRADE_4', '{"required_attendance_percentage": 80}', 'Student must have at least 80% attendance'),
('GRADE4_TO_GRADE5', 'GRADE_TRANSITION', 'GRADE_4', 'GRADE_5', '{"required_attendance_percentage": 80}', 'Student must have at least 80% attendance'),
('GRADE5_TO_GRADE6', 'GRADE_TRANSITION', 'GRADE_5', 'GRADE_6', '{"required_attendance_percentage": 80}', 'Student must have at least 80% attendance'),
('GRADE6_TO_GRADE7', 'GRADE_TRANSITION', 'GRADE_6', 'GRADE_7', '{"required_attendance_percentage": 80}', 'Student must have at least 80% attendance'),
('GRADE7_TO_GRADE8', 'GRADE_TRANSITION', 'GRADE_7', 'GRADE_8', '{"required_attendance_percentage": 80}', 'Student must have at least 80% attendance'),
('GRADE8_TO_GRADE9', 'GRADE_TRANSITION', 'GRADE_8', 'GRADE_9', '{"required_attendance_percentage": 80}', 'Student must have at least 80% attendance'),
('GRADE9_TO_GRADE10', 'GRADE_TRANSITION', 'GRADE_9', 'GRADE_10', '{"required_attendance_percentage": 85}', 'Student must have at least 85% attendance for high school'),
('GRADE10_TO_GRADE11', 'GRADE_TRANSITION', 'GRADE_10', 'GRADE_11', '{"required_attendance_percentage": 85}', 'Student must have at least 85% attendance'),
('GRADE11_TO_GRADE12', 'GRADE_TRANSITION', 'GRADE_11', 'GRADE_12', '{"required_attendance_percentage": 85}', 'Student must have at least 85% attendance'),
('NO_SKIP_GRADES', 'CUSTOM', NULL, NULL, '{"allow_grade_skip": false}', 'Grade skipping is not allowed');

-- Insert current academic year configuration
INSERT INTO academic_year_config (academic_year, year_start_date, year_end_date, promotion_window_start, promotion_window_end, is_current, status) VALUES
('2024-2025', '2024-08-01', '2025-06-30', '2025-05-01', '2025-06-30', TRUE, 'ACTIVE'),
('2025-2026', '2025-08-01', '2026-06-30', '2026-05-01', '2026-06-30', FALSE, 'UPCOMING');

-- Add indexes to students table for promotion queries
CREATE INDEX idx_students_grade_status ON students(grade_level, enrollment_status);
CREATE INDEX idx_students_section_grade ON students(section, grade_level);
