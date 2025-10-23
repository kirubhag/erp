-- Test Data for ERP Application Unit Tests
-- This inserts basic test data for unit testing

-- Insert test organizations
INSERT INTO organizations (name, code, description, is_active, created_by, created_time) VALUES 
('Test School', 'TS001', 'A test school for unit testing', true, 'system', CURRENT_TIMESTAMP),
('Another School', 'AS002', 'Another test school', true, 'system', CURRENT_TIMESTAMP);

-- Insert test roles
INSERT INTO roles (name, description, is_active, created_by, created_time) VALUES 
('ADMIN', 'Administrator role', true, 'system', CURRENT_TIMESTAMP),
('TEACHER', 'Teacher role', true, 'system', CURRENT_TIMESTAMP),
('STUDENT', 'Student role', true, 'system', CURRENT_TIMESTAMP),
('PARENT', 'Parent role', true, 'system', CURRENT_TIMESTAMP);

-- Insert test permissions
INSERT INTO permissions (name, description, resource, action, is_active, created_by, created_time) VALUES 
('USER_READ', 'Read user information', 'user', 'read', true, 'system', CURRENT_TIMESTAMP),
('USER_WRITE', 'Write user information', 'user', 'write', true, 'system', CURRENT_TIMESTAMP),
('STAFF_READ', 'Read staff information', 'staff', 'read', true, 'system', CURRENT_TIMESTAMP),
('STAFF_WRITE', 'Write staff information', 'staff', 'write', true, 'system', CURRENT_TIMESTAMP);

-- Insert test users
INSERT INTO users (username, email, password, first_name, last_name, is_active, role_id, organization_id, created_by, created_time) VALUES 
('testadmin', 'admin@test.com', '$2a$10$test.hash', 'Test', 'Admin', true, 1, 1, 'system', CURRENT_TIMESTAMP),
('testteacher', 'teacher@test.com', '$2a$10$test.hash', 'Test', 'Teacher', true, 2, 1, 'system', CURRENT_TIMESTAMP),
('teststudent', 'student@test.com', '$2a$10$test.hash', 'Test', 'Student', true, 3, 1, 'system', CURRENT_TIMESTAMP),
('testparent', 'parent@test.com', '$2a$10$test.hash', 'Test', 'Parent', true, 4, 1, 'system', CURRENT_TIMESTAMP);

-- Insert test staff
INSERT INTO staff (employee_id, user_id, organization_id, department, position, hire_date, salary, is_active, created_by, created_time) VALUES 
('EMP001', 2, 1, 'Mathematics', 'Senior Teacher', '2024-01-01', 50000.00, true, 'system', CURRENT_TIMESTAMP);

-- Insert test students
INSERT INTO students (student_id, user_id, organization_id, grade_level, enrollment_date, is_active, created_by, created_time) VALUES 
('STU001', 3, 1, '10th Grade', '2024-01-01', true, 'system', CURRENT_TIMESTAMP);

-- Insert test parents
INSERT INTO parents (user_id, relationship_type, occupation, emergency_contact, is_active, created_by, created_time) VALUES 
(4, 'FATHER', 'Engineer', true, true, 'system', CURRENT_TIMESTAMP);

-- Insert test parent-student relations
INSERT INTO parent_student_relations (parent_id, student_id, relationship_type, is_primary, is_active, created_by, created_time) VALUES 
(1, 1, 'FATHER', true, true, 'system', CURRENT_TIMESTAMP);

-- Insert test attendance records
INSERT INTO attendance (user_id, attendance_date, check_in_time, check_out_time, status, organization_id, is_active, created_by, created_time) VALUES 
(3, '2024-10-23', '08:00:00', '15:00:00', 'PRESENT', 1, true, 'system', CURRENT_TIMESTAMP);

-- Insert test health records
INSERT INTO health_records (student_id, record_date, record_type, description, is_active, created_by, created_time) VALUES 
(1, '2024-10-23', 'CHECKUP', 'Regular health checkup', true, 'system', CURRENT_TIMESTAMP);

-- Insert test email templates
INSERT INTO email_templates (name, subject, content, template_type, organization_id, is_active, created_by, created_time) VALUES 
('Welcome Email', 'Welcome to Test School', 'Welcome to our school!', 'WELCOME', 1, true, 'system', CURRENT_TIMESTAMP);

-- Insert test email logs
INSERT INTO email_logs (recipient_email, sender_email, subject, content, status, template_id, organization_id, is_active, created_by, created_time) VALUES 
('student@test.com', 'admin@test.com', 'Welcome to Test School', 'Welcome to our school!', 'SENT', 1, 1, true, 'system', CURRENT_TIMESTAMP);

-- Insert test custom views
INSERT INTO custom_views (view_name, entity_type, description, is_default, is_public, created_by_user, organization_id, is_active, created_by, created_time) VALUES 
('Default User View', 'USER', 'Default view for users', true, true, 'system', 1, true, 'system', CURRENT_TIMESTAMP),
('Staff View', 'STAFF', 'View for staff members', false, false, 'system', 1, true, 'system', CURRENT_TIMESTAMP);