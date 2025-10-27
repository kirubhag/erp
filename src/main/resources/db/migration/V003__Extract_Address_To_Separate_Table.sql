-- Migration to extract address information into separate table
-- This improves database normalization and allows reusability across entities
--
-- Key Features:
-- 1. Creates addresses table with all BaseEntity audit columns (created_by, modified_by, created_time, modified_time, owner_id, is_active)
-- 2. Migrates existing address data from students, parents, and staff tables to addresses table
-- 3. Adds address_id foreign key column to each entity table
-- 4. Updates address_id with the migrated address IDs BEFORE dropping old columns
-- 5. Drops redundant address columns from entity tables after successful migration
--
-- Migration Order (Safe):
-- Step 1: Create addresses table
-- Steps 2-4: Copy address data to addresses table (preserves all audit fields)
-- Steps 5-7: Add address_id FK columns and update with address IDs
-- Steps 8-10: Drop old address columns (only after FK relationships established)

-- Step 1: Create the addresses table
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    address_line1 VARCHAR(100),
    address_line2 VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(50),
    is_primary BOOLEAN DEFAULT TRUE NOT NULL,
    address_type VARCHAR(20) DEFAULT 'RESIDENTIAL',
    created_time DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    modified_time DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    modified_by VARCHAR(100),
    owner_id BIGINT,
    is_active INT DEFAULT 1,
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_primary (entity_type, entity_id, is_primary)
);

-- Step 2: Migrate existing address data from students table
INSERT INTO addresses (entity_type, entity_id, address_line1, address_line2, city, state, postal_code, country, is_primary, address_type, created_time, modified_time, created_by, modified_by, owner_id, is_active)
SELECT 
    'STUDENT' as entity_type,
    id as entity_id,
    address_line1,
    address_line2,
    city,
    state,
    postal_code,
    country,
    TRUE as is_primary,
    'RESIDENTIAL' as address_type,
    created_time,
    modified_time,
    created_by,
    modified_by,
    owner_id,
    is_active
FROM students
WHERE address_line1 IS NOT NULL 
   OR address_line2 IS NOT NULL 
   OR city IS NOT NULL 
   OR state IS NOT NULL 
   OR postal_code IS NOT NULL 
   OR country IS NOT NULL;

-- Step 3: Migrate existing address data from parents table
INSERT INTO addresses (entity_type, entity_id, address_line1, address_line2, city, state, postal_code, country, is_primary, address_type, created_time, modified_time, created_by, modified_by, owner_id, is_active)
SELECT 
    'PARENT' as entity_type,
    id as entity_id,
    address_line1,
    address_line2,
    city,
    state,
    postal_code,
    country,
    TRUE as is_primary,
    'RESIDENTIAL' as address_type,
    created_time,
    modified_time,
    created_by,
    modified_by,
    owner_id,
    is_active
FROM parents
WHERE address_line1 IS NOT NULL 
   OR address_line2 IS NOT NULL 
   OR city IS NOT NULL 
   OR state IS NOT NULL 
   OR postal_code IS NOT NULL 
   OR country IS NOT NULL;

-- Step 4: Migrate existing address data from staff table
INSERT INTO addresses (entity_type, entity_id, address_line1, address_line2, city, state, postal_code, country, is_primary, address_type, created_time, modified_time, created_by, modified_by, owner_id, is_active)
SELECT 
    'STAFF' as entity_type,
    id as entity_id,
    address_line1,
    address_line2,
    city,
    state,
    postal_code,
    country,
    TRUE as is_primary,
    'RESIDENTIAL' as address_type,
    created_time,
    modified_time,
    created_by,
    modified_by,
    owner_id,
    is_active
FROM staff
WHERE address_line1 IS NOT NULL 
   OR address_line2 IS NOT NULL 
   OR city IS NOT NULL 
   OR state IS NOT NULL 
   OR postal_code IS NOT NULL 
   OR country IS NOT NULL;

-- Step 5: Add address_id column to students table
ALTER TABLE students ADD COLUMN address_id BIGINT;
ALTER TABLE students ADD CONSTRAINT fk_student_address FOREIGN KEY (address_id) REFERENCES addresses(id);

-- Update students with their address_id
UPDATE students s
INNER JOIN addresses a ON a.entity_type = 'STUDENT' AND a.entity_id = s.id
SET s.address_id = a.id
WHERE a.is_primary = TRUE;

-- Step 6: Add address_id column to parents table
ALTER TABLE parents ADD COLUMN address_id BIGINT;
ALTER TABLE parents ADD CONSTRAINT fk_parent_address FOREIGN KEY (address_id) REFERENCES addresses(id);

-- Update parents with their address_id
UPDATE parents p
INNER JOIN addresses a ON a.entity_type = 'PARENT' AND a.entity_id = p.id
SET p.address_id = a.id
WHERE a.is_primary = TRUE;

-- Step 7: Add address_id column to staff table
ALTER TABLE staff ADD COLUMN address_id BIGINT;
ALTER TABLE staff ADD CONSTRAINT fk_staff_address FOREIGN KEY (address_id) REFERENCES addresses(id);

-- Update staff with their address_id
UPDATE staff s
INNER JOIN addresses a ON a.entity_type = 'STAFF' AND a.entity_id = s.id
SET s.address_id = a.id
WHERE a.is_primary = TRUE;

-- Step 8: Drop old address columns from students table
ALTER TABLE students 
    DROP COLUMN address_line1,
    DROP COLUMN address_line2,
    DROP COLUMN city,
    DROP COLUMN state,
    DROP COLUMN postal_code,
    DROP COLUMN country;

-- Step 9: Drop old address columns from parents table
ALTER TABLE parents 
    DROP COLUMN address_line1,
    DROP COLUMN address_line2,
    DROP COLUMN city,
    DROP COLUMN state,
    DROP COLUMN postal_code,
    DROP COLUMN country;

-- Step 10: Drop old address columns from staff table
ALTER TABLE staff 
    DROP COLUMN address_line1,
    DROP COLUMN address_line2,
    DROP COLUMN city,
    DROP COLUMN state,
    DROP COLUMN postal_code,
    DROP COLUMN country;

-- Migration completed successfully
-- Address information is now normalized in the addresses table
-- Each entity (Student, Parent, Staff) references their address via address_id foreign key
