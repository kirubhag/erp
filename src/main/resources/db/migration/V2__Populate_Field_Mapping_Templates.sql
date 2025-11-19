-- V2__Populate_Field_Mapping_Templates.sql
-- Populates the field mapping templates for each entity type

-- Student entity type templates
INSERT INTO field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, suggestions, section, display_order) VALUES
('students', 'studentId', 'Student ID', 1, 'string', 'id,student_id,studentid', 'Basic Info', 1),
('students', 'fullName', 'Full Name', 1, 'string', 'name,full_name,firstname lastname,student_name', 'Basic Info', 2),
('students', 'firstName', 'First Name', 0, 'string', 'first_name,firstname', 'Basic Info', 3),
('students', 'lastName', 'Last Name', 0, 'string', 'last_name,lastname', 'Basic Info', 4),
('students', 'email', 'Email', 1, 'email', 'email,email_address,student_email', 'Contact Info', 5),
('students', 'phone', 'Phone', 0, 'phone', 'phone,phone_number,mobile,contact', 'Contact Info', 6),
('students', 'grade', 'Grade', 0, 'string', 'grade,class,level,standard', 'Academic Info', 7),
('students', 'section', 'Section', 0, 'string', 'section,class_section', 'Academic Info', 8),
('students', 'dateOfBirth', 'Date of Birth', 0, 'date', 'dob,date_of_birth,birthdate', 'Personal Info', 9),
('students', 'address', 'Address', 0, 'string', 'address,street_address', 'Address Info', 10);

-- Candidate entity type templates
INSERT INTO field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, suggestions, section, display_order) VALUES
('candidates', 'candidateId', 'Candidate ID', 1, 'string', 'id,candidate_id,candidateid', 'Basic Info', 1),
('candidates', 'fullName', 'Full Name', 1, 'string', 'name,full_name,candidate_name', 'Basic Info', 2),
('candidates', 'email', 'Email', 1, 'email', 'email,email_address,candidate_email', 'Contact Info', 3),
('candidates', 'phone', 'Phone', 1, 'phone', 'phone,phone_number,mobile,contact', 'Contact Info', 4),
('candidates', 'position', 'Applied Position', 0, 'string', 'position,job_position,applied_position', 'Job Info', 5),
('candidates', 'experience', 'Experience', 0, 'string', 'experience,years_experience,exp', 'Job Info', 6),
('candidates', 'status', 'Application Status', 0, 'string', 'status,application_status', 'Job Info', 7),
('candidates', 'appliedDate', 'Applied Date', 0, 'date', 'applied_date,application_date', 'Job Info', 8);

-- Contact entity type templates
INSERT INTO field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, suggestions, section, display_order) VALUES
('contacts', 'contactId', 'Contact ID', 1, 'string', 'id,contact_id,contactid', 'Basic Info', 1),
('contacts', 'fullName', 'Full Name', 1, 'string', 'name,full_name,contact_name', 'Basic Info', 2),
('contacts', 'email', 'Email', 1, 'email', 'email,email_address', 'Contact Info', 3),
('contacts', 'phone', 'Phone', 1, 'phone', 'phone,phone_number,mobile', 'Contact Info', 4),
('contacts', 'company', 'Company', 0, 'string', 'company,organization,company_name', 'Business Info', 5),
('contacts', 'designation', 'Designation', 0, 'string', 'designation,title,position', 'Business Info', 6),
('contacts', 'address', 'Address', 0, 'string', 'address,street_address', 'Address Info', 7);

-- User entity type templates
INSERT INTO field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, suggestions, section, display_order) VALUES
('users', 'userId', 'User ID', 1, 'string', 'id,user_id,userid,username', 'Basic Info', 1),
('users', 'email', 'Email', 1, 'email', 'email,email_address', 'Contact Info', 2),
('users', 'firstName', 'First Name', 0, 'string', 'first_name,firstname', 'Basic Info', 3),
('users', 'lastName', 'Last Name', 0, 'string', 'last_name,lastname', 'Basic Info', 4),
('users', 'phone', 'Phone', 0, 'phone', 'phone,phone_number,mobile', 'Contact Info', 5),
('users', 'role', 'Role', 0, 'string', 'role,user_role', 'Access Info', 6),
('users', 'status', 'Status', 0, 'string', 'status,user_status,active', 'Access Info', 7);
