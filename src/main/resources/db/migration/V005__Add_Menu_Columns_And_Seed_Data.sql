-- Migration V005: Add Menu Columns to erp_entities and Seed Menu Data
-- This migration adds menu-specific columns (sequence, system_name, presence, icon, route)
-- and populates the table with all application menu items

-- Add new columns for dynamic menu system
ALTER TABLE erp_entities 
    ADD COLUMN sequence INT DEFAULT 0 AFTER is_active,
    ADD COLUMN system_name VARCHAR(100) AFTER sequence,
    ADD COLUMN presence BOOLEAN DEFAULT TRUE AFTER system_name,
    ADD COLUMN icon VARCHAR(100) AFTER presence,
    ADD COLUMN route VARCHAR(255) AFTER icon,
    ADD INDEX idx_sequence (sequence),
    ADD INDEX idx_presence (presence),
    ADD INDEX idx_system_name (system_name);

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

-- Insert additional menu items
INSERT INTO erp_entities (singular_name, plural_name, description, is_active, sequence, system_name, presence, icon, route, created_by, last_modified_by) 
VALUES 
    ('Dashboard', 'Dashboard', 'Main dashboard for overview and analytics', TRUE, 1, 'dashboard', TRUE, 'fas fa-tachometer-alt', '#!/', 'system', 'system'),
    ('Parent', 'Parents', 'Parent management entity for managing parent/guardian information', TRUE, 4, 'parent', TRUE, 'fas fa-users', '#!/parents', 'system', 'system'),
    ('Subject', 'Subjects', 'Subject management entity for managing academic subjects', TRUE, 5, 'subject', TRUE, 'fas fa-book', '#!/subjects', 'system', 'system'),
    ('Timetable', 'Timetables', 'Timetable management entity for managing class schedules', TRUE, 6, 'timetable', TRUE, 'fas fa-calendar', '#!/timetables', 'system', 'system'),
    ('Health Record', 'Health Records', 'Health record management entity for student health information', TRUE, 7, 'health_record', TRUE, 'fas fa-heartbeat', '#!/health', 'system', 'system'),
    ('Settings', 'Settings', 'Application settings and configuration', TRUE, 99, 'settings', FALSE, 'fas fa-cog', '#!/settings', 'system', 'system');

-- Note: Settings has sequence=99 and presence=FALSE as it's shown separately in the navbar
-- Other menu items are ordered by their sequence value (1-8)
