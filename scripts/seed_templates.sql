INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'firstName', 'First Name', 1, 'string', 'Basic Info', 0, 'First Name,Given Name,Student First Name');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'lastName', 'Last Name', 1, 'string', 'Basic Info', 1, 'Last Name,Surname,Family Name,Student Last Name');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'middleName', 'Middle Name', 0, 'string', 'Basic Info', 2, 'Middle Name,Middle Initial');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'email', 'Email', 0, 'email', 'Basic Info', 3, 'Email,Email Address,E-mail,Student Email');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'phone', 'Phone', 0, 'phone', 'Basic Info', 4, 'Phone,Phone Number,Mobile,Contact Number,Cell');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'dateOfBirth', 'Date of Birth', 0, 'date', 'Basic Info', 5, 'Date of Birth,DOB,Birth Date,Birthday');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'gender', 'Gender', 0, 'enum', 'Basic Info', 6, 'Gender,Sex');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'gradeLevel', 'Grade Level', 1, 'enum', 'Academic Info', 7, 'Grade,Grade Level,Class,Year,Standard');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'section', 'Section', 0, 'string', 'Academic Info', 8, 'Section,Division,Class Section');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'enrollmentDate', 'Enrollment Date', 0, 'date', 'Academic Info', 9, 'Enrollment Date,Admission Date,Join Date,Start Date');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'enrollmentStatus', 'Enrollment Status', 0, 'enum', 'Academic Info', 10, 'Status,Enrollment Status,Current Status');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'admissionNumber', 'Admission Number', 0, 'string', 'Academic Info', 11, 'Admission Number,Admission No,Admission ID,Registration Number');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'emergencyContactName', 'Emergency Contact Name', 0, 'string', 'Emergency Contact', 12, 'Emergency Contact,Guardian Name,Parent Name,Emergency Name');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'emergencyContactPhone', 'Emergency Contact Phone', 0, 'phone', 'Emergency Contact', 13, 'Emergency Phone,Guardian Phone,Parent Phone,Emergency Number');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'emergencyContactRelation', 'Emergency Contact Relation', 0, 'string', 'Emergency Contact', 14, 'Relation,Relationship,Guardian Relation,Contact Relation');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'nationality', 'Nationality', 0, 'string', 'Additional Info', 15, 'Nationality,Country,Citizenship');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('students', 'bloodGroup', 'Blood Group', 0, 'string', 'Additional Info', 16, 'Blood Group,Blood Type');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'subjectCode', 'Subject Code', 1, 'string', 'Basic Info', 0, 'Subject Code,Code,Subject ID,Course Code');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'subjectName', 'Subject Name', 1, 'string', 'Basic Info', 1, 'Subject Name,Name,Subject,Course Name,Title');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'description', 'Description', 0, 'string', 'Basic Info', 2, 'Description,Details,Subject Description,Course Description');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'gradeLevel', 'Grade Level', 1, 'string', 'Basic Info', 3, 'Grade Level,Grade,Class,Year,Standard,Level');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'category', 'Category', 0, 'string', 'Basic Info', 4, 'Category,Subject Category,Type,Department');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'credits', 'Credits', 0, 'integer', 'Academic Details', 5, 'Credits,Credit Hours,Units');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'hoursPerWeek', 'Hours Per Week', 0, 'integer', 'Academic Details', 6, 'Hours Per Week,Weekly Hours,Hours,Periods');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'prerequisites', 'Prerequisites', 0, 'string', 'Academic Details', 7, 'Prerequisites,Pre-requisites,Required Subjects');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'difficultyLevel', 'Difficulty Level', 0, 'string', 'Academic Details', 8, 'Difficulty Level,Difficulty,Level');
INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES
('subjects', 'isMandatory', 'Is Mandatory', 0, 'boolean', 'Academic Details', 9, 'Is Mandatory,Mandatory,Required,Compulsory');
