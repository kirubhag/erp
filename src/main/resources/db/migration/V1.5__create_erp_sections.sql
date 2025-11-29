-- =====================================================
-- ERP Sections Table Migration Script (Enhanced)
-- =====================================================
-- Description: Creates erp_sections table and migrates existing
--              field_category data to sections
-- Author: System
-- Date: 2025-11-29
-- =====================================================

-- Create erp_sections table
CREATE TABLE IF NOT EXISTS erp_sections (
    -- Primary Key
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

-- Section Identification
entity_type VARCHAR(50) NOT NULL COMMENT 'Entity type this section belongs to',
section_name VARCHAR(100) NOT NULL COMMENT 'Unique system name for the section',
section_label VARCHAR(200) NOT NULL COMMENT 'Display label for the section',

-- Layout Configuration
layout_type VARCHAR(20) NOT NULL DEFAULT 'TWO_COLUMN' COMMENT 'Column layout: SINGLE_COLUMN, TWO_COLUMN, THREE_COLUMN, FOUR_COLUMN',

-- Display Properties
display_order INT NOT NULL DEFAULT 0 COMMENT 'Order in which sections appear',
is_collapsible TINYINT(1) DEFAULT 0 COMMENT 'Whether section can be collapsed',
is_collapsed_by_default TINYINT(1) DEFAULT 0 COMMENT 'Whether section is collapsed by default',

-- Visibility Controls
show_in_create TINYINT(1) DEFAULT 1 COMMENT 'Show in create/new form',
show_in_edit TINYINT(1) DEFAULT 1 COMMENT 'Show in edit form',
show_in_detail TINYINT(1) DEFAULT 1 COMMENT 'Show in detail view',

-- Styling
section_icon VARCHAR(100) COMMENT 'FontAwesome icon class',
section_color VARCHAR(50) COMMENT 'Section color (hex or named)',
css_class VARCHAR(100) COMMENT 'Custom CSS class',

-- Description
description TEXT COMMENT 'Section description',
help_text TEXT COMMENT 'Help text for users',

-- Activity Tracking (from BaseEntity)
is_active TINYINT(1) DEFAULT 1 COMMENT '1=Active, 0=Inactive, -1=Deleted',
created_by VARCHAR(255) COMMENT 'User who created this section',
created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
modified_by VARCHAR(255) COMMENT 'User who last modified this section',
modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last modification timestamp',
owner_id BIGINT COMMENT 'Owner user ID',

-- Organization Context
organization_id BIGINT COMMENT 'Organization this section belongs to',

-- Indexes for performance
INDEX idx_entity_type (entity_type),
INDEX idx_organization (organization_id),
INDEX idx_display_order (display_order),
INDEX idx_active (is_active),
INDEX idx_entity_org (entity_type, organization_id),

-- Unique constraint to prevent duplicate sections


UNIQUE KEY uk_section (entity_type, section_name, organization_id)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
COMMENT='Stores module section configurations for dynamic layout management';

-- Add section relationship columns to erp_fields table
-- Check if columns exist before adding
SET @dbname = DATABASE();

SET @tablename = 'erp_fields';

-- Add section_id column if it doesn't exist
SET
    @preparedStatement = (
        SELECT IF(
                (
                    SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE
                        TABLE_SCHEMA = @dbname
                        AND TABLE_NAME = @tablename
                        AND COLUMN_NAME = 'section_id'
                ) > 0, 'SELECT 1', 'ALTER TABLE erp_fields ADD COLUMN section_id BIGINT COMMENT ''Reference to erp_sections table'''
            )
    );

PREPARE alterIfNotExists FROM @preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

-- Add row_position column if it doesn't exist
SET
    @preparedStatement = (
        SELECT IF(
                (
                    SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE
                        TABLE_SCHEMA = @dbname
                        AND TABLE_NAME = @tablename
                        AND COLUMN_NAME = 'row_position'
                ) > 0, 'SELECT 1', 'ALTER TABLE erp_fields ADD COLUMN row_position INT DEFAULT 0 COMMENT ''Row position within section'''
            )
    );

PREPARE alterIfNotExists FROM @preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

-- Add column_position column if it doesn't exist
SET
    @preparedStatement = (
        SELECT IF(
                (
                    SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE
                        TABLE_SCHEMA = @dbname
                        AND TABLE_NAME = @tablename
                        AND COLUMN_NAME = 'column_position'
                ) > 0, 'SELECT 1', 'ALTER TABLE erp_fields ADD COLUMN column_position INT DEFAULT 0 COMMENT ''Column position within row'''
            )
    );

PREPARE alterIfNotExists FROM @preparedStatement;

EXECUTE alterIfNotExists;

DEALLOCATE PREPARE alterIfNotExists;

-- Add indexes for the new columns (drop first if exists to avoid errors)
SET
    @preparedStatement = (
        SELECT IF(
                (
                    SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.STATISTICS
                    WHERE
                        TABLE_SCHEMA = @dbname
                        AND TABLE_NAME = @tablename
                        AND INDEX_NAME = 'idx_section'
                ) > 0, 'ALTER TABLE erp_fields DROP INDEX idx_section', 'SELECT 1'
            )
    );

PREPARE dropIndexIfExists FROM @preparedStatement;

EXECUTE dropIndexIfExists;

DEALLOCATE PREPARE dropIndexIfExists;

ALTER TABLE erp_fields ADD INDEX idx_section (section_id);

SET
    @preparedStatement = (
        SELECT IF(
                (
                    SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.STATISTICS
                    WHERE
                        TABLE_SCHEMA = @dbname
                        AND TABLE_NAME = @tablename
                        AND INDEX_NAME = 'idx_position'
                ) > 0, 'ALTER TABLE erp_fields DROP INDEX idx_position', 'SELECT 1'
            )
    );

PREPARE dropIndexIfExists FROM @preparedStatement;

EXECUTE dropIndexIfExists;

DEALLOCATE PREPARE dropIndexIfExists;

ALTER TABLE erp_fields
ADD INDEX idx_position (
    section_id,
    row_position,
    column_position
);

-- Add foreign key for section relationship (drop first if exists)
SET
    @preparedStatement = (
        SELECT IF(
                (
                    SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
                    WHERE
                        TABLE_SCHEMA = @dbname
                        AND TABLE_NAME = @tablename
                        AND CONSTRAINT_NAME = 'fk_field_section'
                ) > 0, 'ALTER TABLE erp_fields DROP FOREIGN KEY fk_field_section', 'SELECT 1'
            )
    );

PREPARE dropFKIfExists FROM @preparedStatement;

EXECUTE dropFKIfExists;

DEALLOCATE PREPARE dropFKIfExists;

ALTER TABLE erp_fields
ADD CONSTRAINT fk_field_section FOREIGN KEY (section_id) REFERENCES erp_sections (id) ON DELETE SET NULL;

-- =====================================================
-- Migrate Existing field_category to erp_sections
-- =====================================================
-- This dynamically creates sections from existing field categories

-- Create sections from distinct field_category values
INSERT INTO
    erp_sections (
        entity_type,
        section_name,
        section_label,
        layout_type,
        display_order,
        is_collapsible,
        organization_id,
        is_active,
        created_by
    )
SELECT DISTINCT
    ef.entity_type,
    LOWER(
        REPLACE (
                COALESCE(
                    ef.field_category COLLATE utf8mb4_unicode_ci,
                    'general'
                ),
                ' ',
                '_'
            )
    ) AS section_name,
    UPPER(
        COALESCE(
            ef.field_category COLLATE utf8mb4_unicode_ci,
            'GENERAL INFORMATION'
        )
    ) AS section_label,
    'TWO_COLUMN' AS layout_type,
    -- Assign display order based on common category ordering
    CASE LOWER(
            ef.field_category COLLATE utf8mb4_unicode_ci
        )
        WHEN 'personal' THEN 1
        WHEN 'contact' THEN 2
        WHEN 'academic' THEN 3
        WHEN 'employment' THEN 3
        WHEN 'guardian' THEN 4
        WHEN 'qualification' THEN 4
        WHEN 'occupation' THEN 4
        WHEN 'address' THEN 5
        WHEN 'account' THEN 6
        WHEN 'preferences' THEN 7
        WHEN 'additional' THEN 10
        ELSE 99
    END AS display_order,
    -- Make less common sections collapsible
    CASE LOWER(
            ef.field_category COLLATE utf8mb4_unicode_ci
        )
        WHEN 'personal' THEN 0
        WHEN 'contact' THEN 0
        WHEN 'academic' THEN 0
        WHEN 'employment' THEN 0
        ELSE 1
    END AS is_collapsible,
    1 AS organization_id,
    1 AS is_active,
    'migration' AS created_by
FROM erp_fields ef
WHERE
    ef.field_category IS NOT NULL
    AND ef.is_active = 1
    AND NOT EXISTS (
        SELECT 1
        FROM erp_sections es
        WHERE
            es.entity_type = ef.entity_type
            AND es.section_name = LOWER(
                REPLACE (
                        ef.field_category COLLATE utf8mb4_unicode_ci,
                        ' ',
                        '_'
                    )
            )
            AND es.organization_id = 1
    )
ORDER BY ef.entity_type, display_order;

-- =====================================================
-- Link Existing Fields to Their Sections
-- =====================================================
-- Map all fields to their corresponding sections based on field_category

UPDATE erp_fields ef
INNER JOIN erp_sections es ON ef.entity_type = es.entity_type
AND es.section_name = LOWER(
    REPLACE (
            COALESCE(ef.field_category, 'general') COLLATE utf8mb4_unicode_ci,
            ' ',
            '_'
        )
)
AND es.organization_id = 1
SET
    ef.section_id = es.id
WHERE
    ef.field_category IS NOT NULL
    AND ef.is_active = 1
    AND ef.section_id IS NULL;

-- Handle fields without a category - assign to 'general' section
-- First, create 'general' sections for entity types that have uncategorized fields
INSERT INTO
    erp_sections (
        entity_type,
        section_name,
        section_label,
        layout_type,
        display_order,
        is_collapsible,
        organization_id,
        is_active,
        created_by
    )
SELECT DISTINCT
    ef.entity_type,
    'general' AS section_name,
    'GENERAL INFORMATION' AS section_label,
    'TWO_COLUMN' AS layout_type,
    0 AS display_order,
    0 AS is_collapsible,
    1 AS organization_id,
    1 AS is_active,
    'migration' AS created_by
FROM erp_fields ef
WHERE (
        ef.field_category IS NULL
        OR ef.field_category = ''
    )
    AND ef.is_active = 1
    AND NOT EXISTS (
        SELECT 1
        FROM erp_sections es
        WHERE
            es.entity_type = ef.entity_type
            AND es.section_name = 'general'
            AND es.organization_id = 1
    )
GROUP BY
    ef.entity_type;

-- Link uncategorized fields to 'general' section
UPDATE erp_fields ef
INNER JOIN erp_sections es ON ef.entity_type = es.entity_type
AND es.section_name = 'general'
AND es.organization_id = 1
SET
    ef.section_id = es.id
WHERE (
        ef.field_category IS NULL
        OR ef.field_category = ''
    )
    AND ef.is_active = 1
    AND ef.section_id IS NULL;

-- =====================================================
-- Set Row and Column Positions
-- =====================================================
-- Assign positions based on display_order within each section

-- Create temporary table to calculate positions
CREATE TEMPORARY TABLE IF NOT EXISTS temp_field_positions AS
SELECT
    ef.id,
    ef.section_id,
    ef.display_order,
    @row_num := IF(
        @section_id = ef.section_id,
        @row_num + 1,
        1
    ) AS position,
    @section_id := ef.section_id AS current_section,
    FLOOR((@row_num - 1) / 2) AS row_pos,
    (@row_num - 1) % 2 AS col_pos
FROM erp_fields ef
    CROSS JOIN (
        SELECT @row_num := 0, @section_id := NULL
    ) AS vars
WHERE
    ef.section_id IS NOT NULL
    AND ef.is_active = 1
ORDER BY ef.section_id, ef.display_order, ef.id;

-- Update row and column positions
UPDATE erp_fields ef
INNER JOIN temp_field_positions tfp ON ef.id = tfp.id
SET
    ef.row_position = tfp.row_pos,
    ef.column_position = tfp.col_pos;

-- Clean up temporary table
DROP TEMPORARY TABLE IF EXISTS temp_field_positions;

-- =====================================================
-- Verification Queries
-- =====================================================
-- Run these to verify the migration

-- Check created sections
SELECT
    entity_type,
    section_name,
    section_label,
    layout_type,
    display_order,
    (
        SELECT COUNT(*)
        FROM erp_fields
        WHERE
            section_id = es.id
    ) AS field_count
FROM erp_sections es
WHERE
    is_active = 1
ORDER BY entity_type, display_order;

-- Check fields linked to sections
SELECT ef.entity_type, es.section_label, COUNT(*) as field_count, GROUP_CONCAT(
        ef.field_label
        ORDER BY ef.row_position, ef.column_position SEPARATOR ', '
    ) AS fields
FROM
    erp_fields ef
    LEFT JOIN erp_sections es ON ef.section_id = es.id
WHERE
    ef.is_active = 1
GROUP BY
    ef.entity_type,
    es.section_label,
    es.display_order
ORDER BY ef.entity_type, es.display_order;

-- Check for any fields not linked to sections
SELECT
    entity_type,
    field_name,
    field_label,
    field_category
FROM erp_fields
WHERE
    section_id IS NULL
    AND is_active = 1;

-- =====================================================
-- Rollback Script (if needed)
-- =====================================================
-- Uncomment to rollback changes

-- UPDATE erp_fields SET section_id = NULL, row_position = 0, column_position = 0;
-- ALTER TABLE erp_fields DROP FOREIGN KEY IF EXISTS fk_field_section;
-- ALTER TABLE erp_fields DROP INDEX IF EXISTS idx_section;
-- ALTER TABLE erp_fields DROP INDEX IF EXISTS idx_position;
-- ALTER TABLE erp_fields DROP COLUMN IF EXISTS section_id;
-- ALTER TABLE erp_fields DROP COLUMN IF EXISTS row_position;
-- ALTER TABLE erp_fields DROP COLUMN IF EXISTS column_position;
-- DROP TABLE IF EXISTS erp_sections;