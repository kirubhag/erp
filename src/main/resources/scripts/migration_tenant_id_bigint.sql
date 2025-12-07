-- WARNING: THIS SCRIPT TRUNCATES DATA. USE WITH CAUTION.

USE IAM_MasterDB;

-- 0. Disable Foreign Key Checks
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Truncate tables to remove existing data with incompatible UUIDs
TRUNCATE TABLE IAM_MasterDB.iam_users;

TRUNCATE TABLE erp_tenants;

-- 2. Drop Foreign Key on iam_users
ALTER TABLE IAM_MasterDB.iam_users DROP FOREIGN KEY iam_users_ibfk_1;

-- 3. Modify erp_tenants table
ALTER TABLE erp_tenants MODIFY tenant_id BIGINT;

-- 4. Modify iam_users table
ALTER TABLE IAM_MasterDB.iam_users MODIFY tenant_id BIGINT;

-- 5. Re-add Foreign Key
ALTER TABLE IAM_MasterDB.iam_users
ADD CONSTRAINT iam_users_ibfk_1 FOREIGN KEY (tenant_id) REFERENCES erp_tenants (tenant_id);

-- 6. Re-enable Foreign Key Checks
SET FOREIGN_KEY_CHECKS = 1;