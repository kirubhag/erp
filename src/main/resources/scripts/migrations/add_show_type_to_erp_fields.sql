-- Migration script to add show_type column to existing tenant databases
-- Run this script on each tenant database that was created before this migration

-- Add show_type column to erp_fields table if it doesn't exist
ALTER TABLE erp_fields
ADD COLUMN IF NOT EXISTS show_type INT DEFAULT 0 AFTER column_width;

-- Update existing records to have default value
UPDATE erp_fields SET show_type = 0 WHERE show_type IS NULL;