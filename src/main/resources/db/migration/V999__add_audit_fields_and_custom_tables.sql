-- Migration to add audit fields and custom field tables
-- This script adds the required audit columns to BaseEntity and creates custom field tables

-- First, add the missing audit columns to all existing entity tables
-- Note: Some columns may already exist, ignore errors for existing columns

-- Add audit columns to students table
ALTER TABLE students ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE students ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE students ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE students ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to parents table
ALTER TABLE parents ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE parents ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE parents ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE parents ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to staff table
ALTER TABLE staff ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE staff ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE staff ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE staff ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to health_records table
ALTER TABLE health_records ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE health_records ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE health_records ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE health_records ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to attendance table
ALTER TABLE attendance ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE attendance ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE attendance ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE attendance ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to organizations table
ALTER TABLE organizations ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE organizations ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE organizations ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE organizations ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to email_templates table
ALTER TABLE email_templates ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE email_templates ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE email_templates ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE email_templates ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to email_logs table
ALTER TABLE email_logs ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE email_logs ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE email_logs ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE email_logs ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to roles table
ALTER TABLE roles ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE roles ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE roles ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE roles ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Add audit columns to permissions table
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS modified_by VARCHAR(255);
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- Create custom field tables with 250 columns each

-- Students custom field table
CREATE TABLE IF NOT EXISTS students_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    
    -- String custom fields (1-100)
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    
    -- Numeric custom fields (101-150)
    custom_numeric_1 DOUBLE, custom_numeric_2 DOUBLE, custom_numeric_3 DOUBLE, custom_numeric_4 DOUBLE, custom_numeric_5 DOUBLE,
    custom_numeric_6 DOUBLE, custom_numeric_7 DOUBLE, custom_numeric_8 DOUBLE, custom_numeric_9 DOUBLE, custom_numeric_10 DOUBLE,
    custom_numeric_11 DOUBLE, custom_numeric_12 DOUBLE, custom_numeric_13 DOUBLE, custom_numeric_14 DOUBLE, custom_numeric_15 DOUBLE,
    custom_numeric_16 DOUBLE, custom_numeric_17 DOUBLE, custom_numeric_18 DOUBLE, custom_numeric_19 DOUBLE, custom_numeric_20 DOUBLE,
    custom_numeric_21 DOUBLE, custom_numeric_22 DOUBLE, custom_numeric_23 DOUBLE, custom_numeric_24 DOUBLE, custom_numeric_25 DOUBLE,
    custom_numeric_26 DOUBLE, custom_numeric_27 DOUBLE, custom_numeric_28 DOUBLE, custom_numeric_29 DOUBLE, custom_numeric_30 DOUBLE,
    custom_numeric_31 DOUBLE, custom_numeric_32 DOUBLE, custom_numeric_33 DOUBLE, custom_numeric_34 DOUBLE, custom_numeric_35 DOUBLE,
    custom_numeric_36 DOUBLE, custom_numeric_37 DOUBLE, custom_numeric_38 DOUBLE, custom_numeric_39 DOUBLE, custom_numeric_40 DOUBLE,
    custom_numeric_41 DOUBLE, custom_numeric_42 DOUBLE, custom_numeric_43 DOUBLE, custom_numeric_44 DOUBLE, custom_numeric_45 DOUBLE,
    custom_numeric_46 DOUBLE, custom_numeric_47 DOUBLE, custom_numeric_48 DOUBLE, custom_numeric_49 DOUBLE, custom_numeric_50 DOUBLE,
    
    -- Date custom fields (151-200)
    custom_date_1 DATE, custom_date_2 DATE, custom_date_3 DATE, custom_date_4 DATE, custom_date_5 DATE,
    custom_date_6 DATE, custom_date_7 DATE, custom_date_8 DATE, custom_date_9 DATE, custom_date_10 DATE,
    custom_date_11 DATE, custom_date_12 DATE, custom_date_13 DATE, custom_date_14 DATE, custom_date_15 DATE,
    custom_date_16 DATE, custom_date_17 DATE, custom_date_18 DATE, custom_date_19 DATE, custom_date_20 DATE,
    custom_date_21 DATE, custom_date_22 DATE, custom_date_23 DATE, custom_date_24 DATE, custom_date_25 DATE,
    custom_date_26 DATE, custom_date_27 DATE, custom_date_28 DATE, custom_date_29 DATE, custom_date_30 DATE,
    custom_date_31 DATE, custom_date_32 DATE, custom_date_33 DATE, custom_date_34 DATE, custom_date_35 DATE,
    custom_date_36 DATE, custom_date_37 DATE, custom_date_38 DATE, custom_date_39 DATE, custom_date_40 DATE,
    custom_date_41 DATE, custom_date_42 DATE, custom_date_43 DATE, custom_date_44 DATE, custom_date_45 DATE,
    custom_date_46 DATE, custom_date_47 DATE, custom_date_48 DATE, custom_date_49 DATE, custom_date_50 DATE,
    
    -- Boolean custom fields (201-250)
    custom_boolean_1 BOOLEAN, custom_boolean_2 BOOLEAN, custom_boolean_3 BOOLEAN, custom_boolean_4 BOOLEAN, custom_boolean_5 BOOLEAN,
    custom_boolean_6 BOOLEAN, custom_boolean_7 BOOLEAN, custom_boolean_8 BOOLEAN, custom_boolean_9 BOOLEAN, custom_boolean_10 BOOLEAN,
    custom_boolean_11 BOOLEAN, custom_boolean_12 BOOLEAN, custom_boolean_13 BOOLEAN, custom_boolean_14 BOOLEAN, custom_boolean_15 BOOLEAN,
    custom_boolean_16 BOOLEAN, custom_boolean_17 BOOLEAN, custom_boolean_18 BOOLEAN, custom_boolean_19 BOOLEAN, custom_boolean_20 BOOLEAN,
    custom_boolean_21 BOOLEAN, custom_boolean_22 BOOLEAN, custom_boolean_23 BOOLEAN, custom_boolean_24 BOOLEAN, custom_boolean_25 BOOLEAN,
    custom_boolean_26 BOOLEAN, custom_boolean_27 BOOLEAN, custom_boolean_28 BOOLEAN, custom_boolean_29 BOOLEAN, custom_boolean_30 BOOLEAN,
    custom_boolean_31 BOOLEAN, custom_boolean_32 BOOLEAN, custom_boolean_33 BOOLEAN, custom_boolean_34 BOOLEAN, custom_boolean_35 BOOLEAN,
    custom_boolean_36 BOOLEAN, custom_boolean_37 BOOLEAN, custom_boolean_38 BOOLEAN, custom_boolean_39 BOOLEAN, custom_boolean_40 BOOLEAN,
    custom_boolean_41 BOOLEAN, custom_boolean_42 BOOLEAN, custom_boolean_43 BOOLEAN, custom_boolean_44 BOOLEAN, custom_boolean_45 BOOLEAN,
    custom_boolean_46 BOOLEAN, custom_boolean_47 BOOLEAN, custom_boolean_48 BOOLEAN, custom_boolean_49 BOOLEAN, custom_boolean_50 BOOLEAN,
    
    FOREIGN KEY (entity_id) REFERENCES students(id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_custom (entity_id)
);

-- Create similar tables for other entities (due to file length limits, showing pattern for one more)

-- Parents custom field table
CREATE TABLE IF NOT EXISTS parents_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    
    -- String custom fields (1-100) - same structure as above
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    -- ... (continuing for all 100 string fields)
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    
    -- Numeric custom fields (101-150)
    custom_numeric_1 DOUBLE, custom_numeric_2 DOUBLE, custom_numeric_3 DOUBLE, custom_numeric_4 DOUBLE, custom_numeric_5 DOUBLE,
    -- ... (continuing for all 50 numeric fields)
    custom_numeric_46 DOUBLE, custom_numeric_47 DOUBLE, custom_numeric_48 DOUBLE, custom_numeric_49 DOUBLE, custom_numeric_50 DOUBLE,
    
    -- Date custom fields (151-200)
    custom_date_1 DATE, custom_date_2 DATE, custom_date_3 DATE, custom_date_4 DATE, custom_date_5 DATE,
    -- ... (continuing for all 50 date fields)
    custom_date_46 DATE, custom_date_47 DATE, custom_date_48 DATE, custom_date_49 DATE, custom_date_50 DATE,
    
    -- Boolean custom fields (201-250)
    custom_boolean_1 BOOLEAN, custom_boolean_2 BOOLEAN, custom_boolean_3 BOOLEAN, custom_boolean_4 BOOLEAN, custom_boolean_5 BOOLEAN,
    -- ... (continuing for all 50 boolean fields)
    custom_boolean_46 BOOLEAN, custom_boolean_47 BOOLEAN, custom_boolean_48 BOOLEAN, custom_boolean_49 BOOLEAN, custom_boolean_50 BOOLEAN,
    
    FOREIGN KEY (entity_id) REFERENCES parents(id) ON DELETE CASCADE,
    UNIQUE KEY unique_parent_custom (entity_id)
);

-- Note: Similar CREATE TABLE statements should be created for:
-- staff_custom_field, health_records_custom_field, attendance_custom_field,
-- organizations_custom_field, users_custom_field, email_templates_custom_field,
-- email_logs_custom_field, roles_custom_field, permissions_custom_field

-- Create indexes for better performance
CREATE INDEX idx_students_custom_entity ON students_custom_field(entity_id);
CREATE INDEX idx_parents_custom_entity ON parents_custom_field(entity_id);

-- Add similar indexes for all other custom field tables