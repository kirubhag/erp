# Schema Verification Report

**Date:** 2024
**Purpose:** Verify master_schema.sql and tenant_schema.sql have all required columns before database recreation

---

## Summary

✅ **tenant_schema.sql** - FIXED (added missing student columns)
✅ **master_schema.sql** - COMPLETE (all IAM and system tables present)

---

## Detailed Findings

### 1. Addresses Table ✅ COMPLETE

**Location:** Both master_schema.sql (line 56) and tenant_schema.sql (line 56)

**Required Columns (from Address.java entity):**
- ✅ `address_id` (BIGINT PRIMARY KEY)
- ✅ `entity_type` (VARCHAR(50) NOT NULL)
- ✅ `entity_id` (BIGINT NOT NULL)
- ✅ `address_line1` (VARCHAR(100))
- ✅ `address_line2` (VARCHAR(100))
- ✅ `city` (VARCHAR(50))
- ✅ `state` (VARCHAR(50))
- ✅ `postal_code` (VARCHAR(20))
- ✅ `country` (VARCHAR(50))
- ✅ `is_primary` (BOOLEAN NOT NULL DEFAULT true)
- ✅ `address_type` (VARCHAR(20))
- ✅ `created_by`, `modified_by`, `created_time`, `modified_time`, `owner_id`, `is_active`

**Indexes:**
- ✅ `idx_entity (entity_type, entity_id)` - Critical for polymorphic lookups
- ✅ `idx_is_primary (is_primary)`
- ✅ `idx_is_active (is_active)`

---

### 2. Students Table ✅ FIXED

**Location:** tenant_schema.sql (line 2655)

**Missing Columns ADDED:**
1. ✅ `nationality` VARCHAR(50) - Student.java line 95
2. ✅ `blood_group` VARCHAR(10) - Student.java line 98
3. ✅ `photo_url` VARCHAR(500) - Student.java line 101
4. ✅ `section` VARCHAR(20) - Student.java line 104
5. ✅ `admission_number` VARCHAR(50) UNIQUE - Student.java line 108
6. ✅ `admission_date` DATE - Student.java line 111

**Existing Required Columns:**
- ✅ `student_id` (BIGINT PRIMARY KEY)
- ✅ `first_name`, `last_name`, `middle_name`
- ✅ `student_identifier` (VARCHAR(20) UNIQUE)
- ✅ `email`, `phone`
- ✅ `date_of_birth`, `gender`
- ✅ `enrollment_date`, `grade_level`, `enrollment_status`
- ✅ `address_id` (BIGINT FK to addresses)
- ✅ `emergency_contact_name`, `emergency_contact_phone`, `emergency_contact_relation`
- ✅ `user_id` (BIGINT FK to iam_users)

**Indexes:**
- ✅ `idx_student_id`
- ✅ `idx_email`
- ✅ `idx_grade_level`
- ✅ `idx_enrollment_status`
- ✅ `idx_admission_number` (ADDED)
- ✅ `idx_is_active`

**Foreign Keys:**
- ✅ `address_id` → `addresses(address_id)`
- ✅ `user_id` → `iam_users(user_id)`

---

### 3. Staff Table ✅ COMPLETE

**Location:** tenant_schema.sql (line 2475)

**Key Columns:**
- ✅ `staff_id` (BIGINT PRIMARY KEY)
- ✅ `first_name`, `last_name`, `middle_name`
- ✅ `staff_identifier` (VARCHAR(20) UNIQUE)
- ✅ `email`, `phone`
- ✅ `date_of_birth`, `gender`
- ✅ `hire_date`, `termination_date`, `employment_status`
- ✅ `staff_type`, `department`, `position`
- ✅ `qualification`, `experience_years`, `salary`
- ✅ `address_id` (BIGINT FK to addresses)
- ✅ `emergency_contact_name`, `emergency_contact_phone`, `emergency_contact_relation`
- ✅ `user_id` (BIGINT FK to iam_users)

**Foreign Keys:**
- ✅ `address_id` → `addresses(address_id)`
- ✅ `user_id` → `iam_users(user_id)`

---

### 4. Parents Table ✅ COMPLETE

**Location:** tenant_schema.sql (line 2335)

**Key Columns:**
- ✅ `parent_id` (BIGINT PRIMARY KEY)
- ✅ `first_name`, `last_name`, `middle_name`
- ✅ `email`, `phone`, `alternate_phone`
- ✅ `gender`, `occupation`, `workplace`, `work_phone`
- ✅ `emergency_contact`, `authorized_pickup`, `receive_notifications`
- ✅ `address_id` (BIGINT FK to addresses)
- ✅ `user_id` (BIGINT FK to iam_users)

**Foreign Keys:**
- ✅ `address_id` → `addresses(address_id)`
- ✅ `user_id` → `iam_users(user_id)`

---

### 5. Master Database Tables ✅ COMPLETE

**Location:** master_schema.sql

**Critical IAM Tables:**

1. ✅ **erp_tenants** (line 1828)
   - `id`, `tenant_id`, `tenant_name`
   - `db_host`, `db_name`
   - `status`, `created_at`, `updated_at`

2. ✅ **iam_users** (line 2123)
   - `user_id`, `username`, `password_hash`
   - `email`, `first_name`, `last_name`, `phone`
   - `user_type`, `tenant_id`, `organization_id`
   - `enabled`, `account_non_expired`, `credentials_non_expired`, `account_non_locked`
   - `last_login_date`, `password_change_date`, `avatar_url`

---

## Schema File Changes Made

### File: tenant_schema.sql

**Students table (line 2655-2687) - UPDATED**

Added 6 missing columns:
```sql
-- Additional student fields
nationality VARCHAR(50),
blood_group VARCHAR(10),
photo_url VARCHAR(500),
section VARCHAR(20),
admission_number VARCHAR(50) UNIQUE,
admission_date DATE,
```

Added index:
```sql
INDEX idx_admission_number (admission_number),
```

---

## Database Recreation Steps

Now that schema files are verified and corrected, follow these steps:

### Step 1: Drop Existing Databases
```sql
DROP DATABASE IF EXISTS IAM_MasterDB;
DROP DATABASE IF EXISTS erpdb5e44ad91;
```

### Step 2: Create Master Database
```sql
CREATE DATABASE IAM_MasterDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE IAM_MasterDB;
SOURCE /Users/kirubha-2911/Documents/GitHub/erp/src/main/resources/master_schema.sql;
```

### Step 3: Register Tenant
```sql
INSERT INTO erp_tenants (tenant_id, tenant_name, db_host, db_name, status)
VALUES (UNIX_TIMESTAMP(NOW()) * 1000, 'Zoho Corp', 'localhost:3307', 'erpdb_GENERATED_ID', 'Active');
```

### Step 4: Create Tenant Database
```sql
CREATE DATABASE erpdb_GENERATED_ID CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE erpdb_GENERATED_ID;
SOURCE /Users/kirubha-2911/Documents/GitHub/erp/src/main/resources/scripts/tenant_schema.sql;
```

### Step 5: Load Sample Data (in order)
```bash
# Set tenant ID from step 3
TENANT_ID="<generated_tenant_id>"

# 1. Load addresses FIRST (prerequisite for students/staff/parents)
curl -X POST http://localhost:8081/api/v1/sample-data/populate \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -d '{"entityNames": ["addresses"]}'

# Verify: SELECT COUNT(*) FROM addresses; -- Should be 596

# 2. Load students (depends on addresses)
curl -X POST http://localhost:8081/api/v1/sample-data/populate \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -d '{"entityNames": ["STUDENTS"]}'

# Verify: SELECT COUNT(*) FROM students; -- Should be 585+

# 3. Load staff (depends on addresses)
curl -X POST http://localhost:8081/api/v1/sample-data/populate \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -d '{"entityNames": ["STAFF"]}'

# 4. Load parents (depends on addresses)
curl -X POST http://localhost:8081/api/v1/sample-data/populate \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -d '{"entityNames": ["PARENTS"]}'

# 5. Load other entities
curl -X POST http://localhost:8081/api/v1/sample-data/populate \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -d '{"entityNames": ["subjects", "rooms", "classes", "timetables"]}'
```

---

## Dependency Order (CRITICAL)

Sample data MUST be loaded in this order:

1. **addresses** (no dependencies)
2. **students, staff, parents** (depend on addresses)
3. **subjects, rooms** (no dependencies)
4. **classes** (depend on staff, rooms)
5. **timetables** (depend on classes, subjects, staff, rooms)

This order is enforced in `SampleDataController.DEPENDENCY_ORDER` (lines 37-80).

---

## Verification Queries

After loading sample data, run these queries to verify:

```sql
-- Check addresses
SELECT entity_type, COUNT(*) as count 
FROM addresses 
GROUP BY entity_type;
-- Expected: STUDENT (~200), STAFF (~8), PARENT (~20), ORGANIZATION (~368)

-- Check students with addresses
SELECT 
    COUNT(*) as total_students,
    COUNT(address_id) as students_with_address,
    COUNT(admission_date) as students_with_admission_date
FROM students;
-- Expected: ~585 total, all with address_id, all with admission_date

-- Check staff with addresses
SELECT 
    COUNT(*) as total_staff,
    COUNT(address_id) as staff_with_address
FROM staff;
-- Expected: ~8 total, all with address_id

-- Check parents with addresses
SELECT 
    COUNT(*) as total_parents,
    COUNT(address_id) as parents_with_address
FROM parents;
-- Expected: ~20 total, all with address_id

-- Check FK integrity
SELECT s.student_id, s.first_name, s.last_name, a.city, a.state
FROM students s
LEFT JOIN addresses a ON s.address_id = a.address_id
WHERE s.address_id IS NOT NULL
LIMIT 10;
-- Expected: All joins should succeed with address data
```

---

## Critical Fixes Applied

### Transaction Commit Fix (TenantFilter.java)
The most critical fix was changing when tenant context is cleared:

**BEFORE (BROKEN):**
```java
finally {
    TenantContext.clear(); // Cleared BEFORE transaction commits!
}
```

**AFTER (WORKING):**
```java
TransactionSynchronizationManager.registerSynchronization(
    new TransactionSynchronization() {
        @Override
        public void afterCommit() {
            TenantContext.clear(); // Cleared AFTER transaction commits
        }
    }
);
```

This ensures tenant context remains active during transaction commit, allowing Hibernate to correctly route INSERT statements to the tenant database.

**Verified Working:** 596 addresses successfully imported and persisted on 2024-12-25.

---

## Next Steps After Database Recreation

1. ✅ Schema files verified and corrected
2. ⏳ Drop and recreate databases using corrected schema files
3. ⏳ Load sample data in correct dependency order
4. ⏳ Verify FK relationships and data integrity
5. ⏳ Test application functionality with sample data

---

## Notes

- **No Flyway migrations needed:** Schema files are complete and can be sourced directly
- **Transaction fix is production-critical:** Affects all multi-tenant data persistence
- **Dependency order enforced:** SampleDataController will load in correct order
- **All entity-schema mismatches resolved:** Students table now has all required columns
