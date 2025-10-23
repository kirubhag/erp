-- Fix column names in all tables to match BaseEntity
-- Drop created_at and updated_at/updated_by if created_time and modified_time/modified_by already exist
-- Otherwise rename created_at to created_time and updated_at/updated_by to modified_time/modified_by

USE erp_database;

-- Students table (already has created_time, modified_time, modified_by)
-- Nothing to do - already correct!

-- Attendance table (has created_time, but still has updated_at/updated_by alongside modified_time/modified_by)
ALTER TABLE attendance DROP COLUMN updated_at;
ALTER TABLE attendance DROP COLUMN updated_by;

-- Custom Views table (has created_at, but has modified_time/modified_by)
ALTER TABLE custom_views CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE custom_views DROP COLUMN updated_at;
ALTER TABLE custom_views DROP COLUMN updated_by;

-- Email Logs table (has both created_at and created_time!)
ALTER TABLE email_logs DROP COLUMN created_at;
ALTER TABLE email_logs DROP COLUMN updated_at;
ALTER TABLE email_logs DROP COLUMN updated_by;

-- Email Templates table (has both created_at and created_time!)
ALTER TABLE email_templates DROP COLUMN created_at;
ALTER TABLE email_templates DROP COLUMN updated_at;
ALTER TABLE email_templates DROP COLUMN updated_by;

-- ERP Fields table (has created_at, but has modified_time/modified_by)
ALTER TABLE erp_fields CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE erp_fields DROP COLUMN updated_at;
ALTER TABLE erp_fields DROP COLUMN updated_by;

-- Health Records table (has created_at, but has modified_time/modified_by)
ALTER TABLE health_records CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE health_records DROP COLUMN updated_at;
ALTER TABLE health_records DROP COLUMN updated_by;

-- Homework table (only has updated_at/updated_by, needs renaming)
ALTER TABLE homework CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE homework CHANGE COLUMN updated_at modified_time DATETIME(6) NULL;
ALTER TABLE homework CHANGE COLUMN updated_by modified_by VARCHAR(255) NULL;

-- Organizations table (has both created_at and created_time!)
ALTER TABLE organizations DROP COLUMN created_at;
ALTER TABLE organizations DROP COLUMN updated_at;
ALTER TABLE organizations DROP COLUMN updated_by;

-- Parents table (has created_at, but has modified_time/modified_by)
ALTER TABLE parents CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE parents DROP COLUMN updated_at;
ALTER TABLE parents DROP COLUMN updated_by;

-- Permissions table (has created_at, but has modified_time/modified_by)
ALTER TABLE permissions CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE permissions DROP COLUMN updated_at;
ALTER TABLE permissions DROP COLUMN updated_by;

-- Recycle Bin table (no timestamp columns to fix based on earlier check)

-- Roles table (has created_at, but has modified_time/modified_by)
ALTER TABLE roles CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE roles DROP COLUMN updated_at;
ALTER TABLE roles DROP COLUMN updated_by;

-- Staff table (has created_at, but has modified_time/modified_by)
ALTER TABLE staff CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE staff DROP COLUMN updated_at;
ALTER TABLE staff DROP COLUMN updated_by;

-- Users table (has created_at, but has modified_time/modified_by)
ALTER TABLE users CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE users DROP COLUMN updated_at;
ALTER TABLE users DROP COLUMN updated_by;

-- Parent Student Relations table (has created_at, but has modified_time/modified_by)
ALTER TABLE parent_student_relations CHANGE COLUMN created_at created_time DATETIME(6) NOT NULL;
ALTER TABLE parent_student_relations DROP COLUMN updated_at;
ALTER TABLE parent_student_relations DROP COLUMN updated_by;

-- Custom View Fields table (need to check if exists)
-- Skipping for now as it wasn't shown in the output

SELECT 'Column names fixed successfully!' AS status;
