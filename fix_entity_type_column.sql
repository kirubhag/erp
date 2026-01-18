-- Fix entity_type column length issue
-- Run this script on your existing database to increase VARCHAR(50) to VARCHAR(100)
-- This fixes the "Data truncated for column 'entity_type'" error for entities like PROFESSIONAL_PORTFOLIO

USE IAM_MasterDB;

-- Fix VARCHAR(50) columns in IAM_MasterDB
ALTER TABLE erp_fields MODIFY COLUMN entity_type VARCHAR(100) NOT NULL;
ALTER TABLE erp_sections MODIFY COLUMN entity_type VARCHAR(100) NOT NULL;

-- Show updated schema
DESCRIBE erp_fields;
DESCRIBE erp_sections;

-- Now fix tenant database tables (replace YOUR_TENANT_DB with actual tenant database name)
-- USE YOUR_TENANT_DB;

-- ALTER TABLE erp_addresses MODIFY COLUMN entity_type VARCHAR(100) NOT NULL;
-- ALTER TABLE erp_custom_views MODIFY COLUMN entity_type VARCHAR(100) NOT NULL;
-- ALTER TABLE erp_field_mapping_templates MODIFY COLUMN entity_type VARCHAR(100) NOT NULL;
-- ALTER TABLE erp_import_sessions MODIFY COLUMN entity_type VARCHAR(100) NOT NULL;
