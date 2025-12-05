-- Migration: Add show_type column for field visibility control
-- Version: V003_3
-- Description: Add show_type column to control field visibility in Create/Edit/View pages
--              0 = Show everywhere (default)
--              1 = View only (system fields)
--              2 = Hidden (internal only)

-- Add show_type column with default value 0
ALTER TABLE erp_fields
ADD COLUMN show_type INT DEFAULT 0 COMMENT '0=show everywhere, 1=view only, 2=hidden';

-- Update system fields to view-only (show_type = 1)
UPDATE erp_fields
SET
    show_type = 1
WHERE
    field_name IN (
        'id',
        'createdAt',
        'created_at',
        'created_time',
        'updatedAt',
        'updated_at',
        'modified_time',
        'createdBy',
        'created_by',
        'modifiedBy',
        'modified_by',
        'ownerId',
        'owner_id'
    )
    AND is_active = 1;

-- Ensure all other fields default to show everywhere (show_type = 0)
UPDATE erp_fields SET show_type = 0 WHERE show_type IS NULL;

-- Add index for performance
CREATE INDEX idx_show_type ON erp_fields (show_type);

-- Verify the update
SELECT
    show_type,
    COUNT(*) as field_count,
    GROUP_CONCAT(
        DISTINCT field_name SEPARATOR ', '
    ) as sample_fields
FROM erp_fields
WHERE
    is_active = 1
GROUP BY
    show_type
ORDER BY show_type;