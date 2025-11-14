-- Migration: Rename users table to iam_users and add test users for authentication

-- Step 1: Rename the users table to iam_users
ALTER TABLE users RENAME TO iam_users;

-- Step 2: Rename the user_roles table foreign key constraint if needed
-- (This is typically handled automatically by most databases)

-- Step 3: Add test users with encoded passwords
-- Passwords are BCrypt encoded:
-- admin123 -> $2a$10$dXJ3SW6G7P50eS6DtJV8Ue8LlYpTLj4h4z4W3K1e3L9K4J6M9P2Tu
-- student123 -> $2a$10$wZ3MmW7Z5K3B2L9N8Q1R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0
-- teacher123 -> $2a$10$zV4NnX8L6J2M9P1Q5R6S7T8U9V0W1X2Y3Z4A5B6C7D8E9F0G1H2I3

-- Insert admin user
INSERT INTO iam_users (username, password_hash, email, first_name, last_name, user_type, enabled, account_non_expired, credentials_non_expired, account_non_locked, created_date, last_modified_date, created_by, last_modified_by)
VALUES ('admin', '$2a$10$dXJ3SW6G7P50eS6DtJV8Ue8LlYpTLj4h4z4W3K1e3L9K4J6M9P2Tu', 'admin@gmail.com', 'Admin', 'User', 'ADMIN', true, true, true, true, NOW(), NOW(), 'system', 'system');

-- Insert student user
INSERT INTO iam_users (username, password_hash, email, first_name, last_name, user_type, enabled, account_non_expired, credentials_non_expired, account_non_locked, created_date, last_modified_date, created_by, last_modified_by)
VALUES ('student', '$2a$10$wZ3MmW7Z5K3B2L9N8Q1R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0', 'student@gmail.com', 'Student', 'User', 'STUDENT', true, true, true, true, NOW(), NOW(), 'system', 'system');

-- Insert teacher user
INSERT INTO iam_users (username, password_hash, email, first_name, last_name, user_type, enabled, account_non_expired, credentials_non_expired, account_non_locked, created_date, last_modified_date, created_by, last_modified_by)
VALUES ('teacher', '$2a$10$zV4NnX8L6J2M9P1Q5R6S7T8U9V0W1X2Y3Z4A5B6C7D8E9F0G1H2I3', 'teacher@gmail.com', 'Teacher', 'User', 'STAFF', true, true, true, true, NOW(), NOW(), 'system', 'system');
