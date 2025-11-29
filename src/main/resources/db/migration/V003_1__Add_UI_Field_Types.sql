-- Database migration script for UI Field Types implementation
-- Add new columns to erp_fields table for enhanced field management

-- Add ui_type column (check if not exists)
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND COLUMN_NAME = 'ui_type';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_fields ADD COLUMN ui_type INT NULL COMMENT ''UI Field Type ID (100-119 range)''',
    'SELECT ''Column ui_type already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add max_length column (check if not exists)
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND COLUMN_NAME = 'max_length';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_fields ADD COLUMN max_length INT NULL COMMENT ''Maximum field length for text fields''',
    'SELECT ''Column max_length already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add validation_pattern column (check if not exists)
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND COLUMN_NAME = 'validation_pattern';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_fields ADD COLUMN validation_pattern VARCHAR(500) NULL COMMENT ''Regular expression pattern for field validation''',
    'SELECT ''Column validation_pattern already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add picklist_options column (check if not exists)
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND COLUMN_NAME = 'picklist_options';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_fields ADD COLUMN picklist_options TEXT NULL COMMENT ''JSON string containing picklist options''',
    'SELECT ''Column picklist_options already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add decimal_places column (check if not exists)
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND COLUMN_NAME = 'decimal_places';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_fields ADD COLUMN decimal_places INT NULL COMMENT ''Number of decimal places for numeric fields''',
    'SELECT ''Column decimal_places already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add is_unique column (check if not exists)
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND COLUMN_NAME = 'is_unique';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_fields ADD COLUMN is_unique BOOLEAN DEFAULT FALSE COMMENT ''Whether field values must be unique''',
    'SELECT ''Column is_unique already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add show_in_list column (check if not exists)
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND COLUMN_NAME = 'show_in_list';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_fields ADD COLUMN show_in_list BOOLEAN DEFAULT TRUE COMMENT ''Whether field should appear in list views''',
    'SELECT ''Column show_in_list already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add show_in_form column (check if not exists)
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND COLUMN_NAME = 'show_in_form';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_fields ADD COLUMN show_in_form BOOLEAN DEFAULT TRUE COMMENT ''Whether field should appear in forms''',
    'SELECT ''Column show_in_form already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add column_width column (check if not exists)
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND COLUMN_NAME = 'column_width';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_fields ADD COLUMN column_width VARCHAR(50) DEFAULT ''medium'' COMMENT ''Column width for list view (small, medium, large, etc.)''',
    'SELECT ''Column column_width already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add index on ui_type for better query performance (check if not exists)
SET @index_exists = 0;
SELECT COUNT(*) INTO @index_exists
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'erp_fields'
  AND INDEX_NAME = 'idx_erp_fields_ui_type';

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_erp_fields_ui_type ON erp_fields(ui_type)',
    'SELECT ''Index idx_erp_fields_ui_type already exists'' AS note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update existing fields with appropriate UI types based on field_type
-- This maps existing FieldType enum values to new UIFieldType values

-- TEXT and STRING fields -> Single Line Text (100)
UPDATE erp_fields 
SET ui_type = 100 
WHERE field_type IN ('TEXT', 'STRING') 
AND ui_type IS NULL;

-- TEXTAREA and RICH_TEXT fields -> Multi Line Text (101)
UPDATE erp_fields 
SET ui_type = 101 
WHERE field_type IN ('TEXTAREA', 'RICH_TEXT') 
AND ui_type IS NULL;

-- EMAIL fields -> Email (102)
UPDATE erp_fields 
SET ui_type = 102 
WHERE field_type = 'EMAIL' 
AND ui_type IS NULL;

-- PHONE fields -> Phone (103)
UPDATE erp_fields 
SET ui_type = 103 
WHERE field_type = 'PHONE' 
AND ui_type IS NULL;

-- SELECT and ENUM fields -> Pick List (104)
UPDATE erp_fields 
SET ui_type = 104 
WHERE field_type IN ('SELECT', 'ENUM') 
AND ui_type IS NULL;

-- MULTI_SELECT fields -> Multi-Select Picklist (105)
UPDATE erp_fields 
SET ui_type = 105 
WHERE field_type = 'MULTI_SELECT' 
AND ui_type IS NULL;

-- DATE fields -> Date (106)
UPDATE erp_fields 
SET ui_type = 106 
WHERE field_type = 'DATE' 
AND ui_type IS NULL;

-- DATETIME fields -> Date/Time (107)
UPDATE erp_fields 
SET ui_type = 107 
WHERE field_type IN ('DATETIME', 'TIME') 
AND ui_type IS NULL;

-- INTEGER and NUMERIC fields -> Number (108)
UPDATE erp_fields 
SET ui_type = 108 
WHERE field_type IN ('INTEGER', 'NUMERIC') 
AND ui_type IS NULL;

-- LONG fields -> Long Integer (113)
UPDATE erp_fields 
SET ui_type = 113 
WHERE field_type = 'LONG' 
AND ui_type IS NULL;

-- DECIMAL fields -> Decimal (111)
UPDATE erp_fields 
SET ui_type = 111 
WHERE field_type = 'DECIMAL' 
AND ui_type IS NULL;

-- BOOLEAN and CHECKBOX fields -> Checkbox (114)
UPDATE erp_fields 
SET ui_type = 114 
WHERE field_type IN ('BOOLEAN', 'CHECKBOX') 
AND ui_type IS NULL;

-- RADIO fields -> Radio Button (116)
UPDATE erp_fields 
SET ui_type = 116 
WHERE field_type = 'RADIO' 
AND ui_type IS NULL;

-- FILE fields -> File Upload (117)
UPDATE erp_fields 
SET ui_type = 117 
WHERE field_type = 'FILE' 
AND ui_type IS NULL;

-- IMAGE fields -> Image Upload (118)
UPDATE erp_fields 
SET ui_type = 118 
WHERE field_type = 'IMAGE' 
AND ui_type IS NULL;

-- URL fields -> URL (119)
UPDATE erp_fields 
SET ui_type = 119 
WHERE field_type = 'URL' 
AND ui_type IS NULL;

-- Set default max_length for text fields
UPDATE erp_fields 
SET max_length = 255 
WHERE ui_type IN (100, 102, 103, 104, 116, 119) 
AND max_length IS NULL;

-- Set max_length for file upload fields
UPDATE erp_fields 
SET max_length = 500 
WHERE ui_type IN (117, 118) 
AND max_length IS NULL;

-- Set default decimal places for decimal and currency fields
UPDATE erp_fields 
SET decimal_places = 2 
WHERE ui_type IN (110, 111, 112) 
AND decimal_places IS NULL;

-- Set validation patterns for email and phone fields
UPDATE erp_fields 
SET validation_pattern = '^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$' 
WHERE ui_type = 102 
AND validation_pattern IS NULL;

UPDATE erp_fields 
SET validation_pattern = '^[+]?[0-9\\-\\s\\(\\)]{7,20}$' 
WHERE ui_type = 103 
AND validation_pattern IS NULL;

UPDATE erp_fields 
SET validation_pattern = '^https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)$' 
WHERE ui_type = 119 
AND validation_pattern IS NULL;

-- Commit the changes
COMMIT;