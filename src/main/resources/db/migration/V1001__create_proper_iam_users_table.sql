-- Migration: Create proper iam_users table matching User entity

-- Create iam_users table with all required columns
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

-- Create user_roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES iam_users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insert test users if they don't exist
INSERT IGNORE INTO iam_users (username, password_hash, email, first_name, last_name, user_type, enabled, account_non_expired, credentials_non_expired, account_non_locked, created_date, last_modified_date, created_by, last_modified_by)
VALUES 
('admin', '$2a$10$dXJ3SW6G7P50eS6DtJV8Ue8LlYpTLj4h4z4W3K1e3L9K4J6M9P2Tu', 'admin@school.edu', 'Admin', 'User', 'ADMIN', true, true, true, true, NOW(), NOW(), 'system', 'system'),
('student', '$2a$10$wZ3MmW7Z5K3B2L9N8Q1R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0', 'student@school.edu', 'Student', 'User', 'STUDENT', true, true, true, true, NOW(), NOW(), 'system', 'system'),
('teacher', '$2a$10$zV4NnX8L6J2M9P1Q5R6S7T8U9V0W1X2Y3Z4A5B6C7D8E9F0G1H2I3', 'teacher@school.edu', 'Teacher', 'User', 'STAFF', true, true, true, true, NOW(), NOW(), 'system', 'system');
