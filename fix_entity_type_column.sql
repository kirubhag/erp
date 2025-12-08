-- Fix entity_type column length in erp_fields and erp_sections tables
USE IAM_MasterDB;

ALTER TABLE erp_fields MODIFY COLUMN entity_type VARCHAR(50) NOT NULL;
ALTER TABLE erp_sections MODIFY COLUMN entity_type VARCHAR(50) NOT NULL;

-- Show updated schema
DESCRIBE erp_fields;
DESCRIBE erp_sections;
