-- Migration V005: Add Menu Columns to erp_entities and Seed Menu Data
-- This migration adds menu-specific columns (sequence, system_name, presence, icon, route)
-- and populates the table with all application menu items

-- Check and add sequence column
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'erp_entities' AND COLUMN_NAME = 'sequence';
SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE erp_entities ADD COLUMN sequence INT DEFAULT 0 AFTER is_active',
    'SELECT ''Column sequence already exists'' AS Note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add system_name column
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'erp_entities' AND COLUMN_NAME = 'system_name';
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_entities ADD COLUMN system_name VARCHAR(100) AFTER sequence',
    'SELECT ''Column system_name already exists'' AS Note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add presence column
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'erp_entities' AND COLUMN_NAME = 'presence';
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_entities ADD COLUMN presence BOOLEAN DEFAULT TRUE AFTER system_name',
    'SELECT ''Column presence already exists'' AS Note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add icon column
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'erp_entities' AND COLUMN_NAME = 'icon';
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_entities ADD COLUMN icon VARCHAR(100) AFTER presence',
    'SELECT ''Column icon already exists'' AS Note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add route column
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'erp_entities' AND COLUMN_NAME = 'route';
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE erp_entities ADD COLUMN route VARCHAR(255) AFTER icon',
    'SELECT ''Column route already exists'' AS Note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add idx_sequence index
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'erp_entities' AND INDEX_NAME = 'idx_sequence';
SET @sql = IF(@idx_exists = 0,
    'CREATE INDEX idx_sequence ON erp_entities(sequence)',
    'SELECT ''Index idx_sequence already exists'' AS Note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add idx_presence index
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'erp_entities' AND INDEX_NAME = 'idx_presence';
SET @sql = IF(@idx_exists = 0,
    'CREATE INDEX idx_presence ON erp_entities(presence)',
    'SELECT ''Index idx_presence already exists'' AS Note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add idx_system_name index
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'erp_entities' AND INDEX_NAME = 'idx_system_name';
SET @sql = IF(@idx_exists = 0,
    'CREATE INDEX idx_system_name ON erp_entities(system_name)',
    'SELECT ''Index idx_system_name already exists'' AS Note');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update existing entities with menu information
UPDATE erp_entities SET 
    sequence = 2,
    system_name = 'student',
    presence = TRUE,
    icon = 'fas fa-user-graduate',
    route = '#!/students'
WHERE singular_name = 'Student';

UPDATE erp_entities SET 
    sequence = 8,
    system_name = 'staff',
    presence = TRUE,
    icon = 'fas fa-user-tie',
    route = '#!/staff'
WHERE singular_name = 'Staff';

UPDATE erp_entities SET 
    sequence = 3,
    system_name = 'attendance',
    presence = TRUE,
    icon = 'fas fa-calendar-check',
    route = '#!/attendance'
WHERE singular_name = 'Attendance';

UPDATE erp_entities SET 
    sequence = 5,
    system_name = 'subject',
    presence = TRUE,
    icon = 'fas fa-book',
    route = '#!/subjects'
WHERE singular_name = 'Subject';

UPDATE erp_entities SET 
    sequence = 6,
    system_name = 'timetable',
    presence = TRUE,
    icon = 'fas fa-calendar',
    route = '#!/timetables'
WHERE singular_name = 'Timetable';

-- Insert new menu items using INSERT IGNORE (skip if already exists)
INSERT IGNORE INTO erp_entities (singular_name, plural_name, description, is_active, sequence, system_name, presence, icon, route, created_by, last_modified_by) 
VALUES 
    ('Dashboard', 'Dashboard', 'Main dashboard for overview and analytics', TRUE, 1, 'dashboard', TRUE, 'fas fa-tachometer-alt', '#!/', 'system', 'system'),
    ('Parent', 'Parents', 'Parent management entity for managing parent/guardian information', TRUE, 4, 'parent', TRUE, 'fas fa-users', '#!/parents', 'system', 'system'),
    ('Health Record', 'Health Records', 'Health record management entity for student health information', TRUE, 7, 'health_record', TRUE, 'fas fa-heartbeat', '#!/health', 'system', 'system'),
    ('Settings', 'Settings', 'Application settings and configuration', TRUE, 99, 'settings', FALSE, 'fas fa-cog', '#!/settings', 'system', 'system');

-- Note: Settings has sequence=99 and presence=FALSE as it's shown separately in the navbar
-- Other menu items are ordered by their sequence value (1-8)
