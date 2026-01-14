-- Migration V004: Create ERP Entities and Role Mapping Tables
-- This migration creates the erp_entities table to manage menu visibility
-- and the erp_entities_role_relation table to map entities to user roles

-- Create erp_entities table
CREATE TABLE IF NOT EXISTS erp_entities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    singular_name VARCHAR(100) NOT NULL UNIQUE,
    plural_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    last_modified_by BIGINT,
    INDEX idx_singular_name (singular_name),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create erp_entities_role_relation table to map entities with roles
CREATE TABLE IF NOT EXISTS erp_entities_role_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    last_modified_by BIGINT,
    UNIQUE KEY unique_entity_role (entity_id, role_id),
    FOREIGN KEY (entity_id) REFERENCES erp_entities(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    INDEX idx_entity_id (entity_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert initial entities (Student, Staff, Attendance)
INSERT INTO erp_entities (singular_name, plural_name, description, is_active, created_by, last_modified_by) 
VALUES 
    ('Student', 'Students', 'Student management entity for managing student information', TRUE, 'system', 'system'),
    ('Staff', 'Staff', 'Staff management entity for managing staff/employee information', TRUE, 'system', 'system'),
    ('Attendance', 'Attendance', 'Attendance management entity for tracking student and staff attendance', TRUE, 'system', 'system');
