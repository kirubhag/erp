-- Database migration script to populate missing UI types
-- This migration ensures all fields have appropriate ui_type values

-- Update any remaining NULL ui_type values based on field_type
-- This handles fields that may have been added after the initial migration

-- PICKLIST fields that don't have ui_type yet
UPDATE erp_fields
SET
    ui_type = 104
WHERE
    field_type = 'PICKLIST'
    AND ui_type IS NULL;

-- AUTO_NUMBER fields -> Auto Number (109)
UPDATE erp_fields
SET
    ui_type = 109
WHERE
    field_type = 'AUTO_NUMBER'
    AND ui_type IS NULL;

-- LOOKUP fields -> Lookup (115)
UPDATE erp_fields
SET
    ui_type = 115
WHERE
    field_type = 'LOOKUP'
    AND ui_type IS NULL;

-- Set default picklist options for gender fields if not already set
UPDATE erp_fields
SET
    picklist_options = 'Male,Female,Other,Prefer not to say'
WHERE
    field_name IN ('gender', 'Gender')
    AND ui_type = 104
    AND (
        picklist_options IS NULL
        OR picklist_options = ''
    );

-- Set default picklist options for status fields if not already set
UPDATE erp_fields
SET
    picklist_options = 'ACTIVE,INACTIVE,PENDING,SUSPENDED'
WHERE
    field_name IN (
        'status',
        'enrollmentStatus',
        'employmentStatus'
    )
    AND ui_type = 104
    AND (
        picklist_options IS NULL
        OR picklist_options = ''
    );

-- Ensure email fields have proper validation pattern
UPDATE erp_fields
SET
    validation_pattern = '^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'
WHERE
    ui_type = 102
    AND (
        validation_pattern IS NULL
        OR validation_pattern = ''
    );

-- Ensure phone fields have proper validation pattern
UPDATE erp_fields
SET
    validation_pattern = '^[+]?[0-9\\-\\s\\(\\)]{7,20}$'
WHERE
    ui_type = 103
    AND (
        validation_pattern IS NULL
        OR validation_pattern = ''
    );

-- Ensure URL fields have proper validation pattern
UPDATE erp_fields
SET
    validation_pattern = '^https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)$'
WHERE
    ui_type = 119
    AND (
        validation_pattern IS NULL
        OR validation_pattern = ''
    );

-- Set decimal places for currency fields
UPDATE erp_fields
SET
    decimal_places = 2
WHERE
    ui_type = 110
    AND decimal_places IS NULL;

-- Set decimal places for decimal fields
UPDATE erp_fields
SET
    decimal_places = 2
WHERE
    ui_type = 111
    AND decimal_places IS NULL;

-- Set decimal places for percent fields
UPDATE erp_fields
SET
    decimal_places = 2
WHERE
    ui_type = 112
    AND decimal_places IS NULL;

-- Ensure show_in_form is true for all active fields (unless explicitly set to false)
UPDATE erp_fields
SET
    show_in_form = true
WHERE
    show_in_form IS NULL
    AND is_active = 1;

-- Ensure show_in_list has sensible defaults
UPDATE erp_fields
SET
    show_in_list = true
WHERE
    show_in_list IS NULL
    AND field_name NOT IN(
        'password',
        'photoUrl',
        'notes',
        'description',
        'comments'
    )
    AND is_active = 1;

-- Hide sensitive fields from list view
UPDATE erp_fields
SET
    show_in_list = false
WHERE
    field_name IN (
        'password',
        'passwordHash',
        'salt'
    )
    AND ui_type IS NOT NULL;

-- Set column width for common field types
UPDATE erp_fields
SET
    column_width = 'small'
WHERE
    ui_type IN (114, 121) -- Checkbox, Toggle
    AND (
        column_width IS NULL
        OR column_width = 'medium'
    );

UPDATE erp_fields
SET
    column_width = 'large'
WHERE
    ui_type IN (101, 102, 119) -- Multi-line text, Email, URL
    AND (
        column_width IS NULL
        OR column_width = 'medium'
    );

UPDATE erp_fields
SET
    column_width = 'medium'
WHERE
    ui_type IN (100, 103, 104, 106, 107, 108) -- Single line, Phone, Picklist, Date, DateTime, Number
    AND column_width IS NULL;

-- Verify all fields have ui_type (log any that don't)
-- This is for monitoring purposes
SELECT
    entity_type,
    field_name,
    field_type,
    'Missing ui_type' as issue
FROM erp_fields
WHERE
    ui_type IS NULL
    AND is_active = 1;

COMMIT;