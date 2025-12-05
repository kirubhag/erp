-- Populate ui_type values for erp_fields table
-- Maps existing field_type values to new UIFieldType enum (100-119 range)

USE erp_database;

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

-- Show summary of updates
SELECT 
    ui_type,
    field_type,
    COUNT(*) as field_count
FROM erp_fields
WHERE ui_type IS NOT NULL
GROUP BY ui_type, field_type
ORDER BY ui_type;
