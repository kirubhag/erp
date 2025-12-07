-- Complete migration for existing tenant 'erp_database'
USE IAM_MasterDB;

-- 1. Get the existing tenant_id
SELECT @existing_tenant_id := tenant_id
FROM erp_tenants
WHERE
    db_name = 'erp_database'
LIMIT 1;

-- 2. Update tenant_id in existing tenant database
USE erp_database;

UPDATE iam_users
SET
    tenant_id = @existing_tenant_id
WHERE
    tenant_id IS NULL;

-- 3. Migrate Users from existing database to IAM Master
USE IAM_MasterDB;

INSERT INTO
    iam_users (
        tenant_id,
        username,
        email,
        password_hash,
        first_name,
        last_name,
        phone,
        user_type,
        enabled,
        account_non_expired,
        credentials_non_expired,
        account_non_locked,
        last_login_date,
        password_change_date,
        avatar_url,
        organization_id,
        created_at
    )
SELECT
    @existing_tenant_id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    phone,
    user_type,
    enabled,
    account_non_expired,
    credentials_non_expired,
    account_non_locked,
    last_login_date,
    password_change_date,
    avatar_url,
    organization_id,
    NOW()
FROM erp_database.iam_users
ON DUPLICATE KEY UPDATE
    updated_at = NOW();

SELECT * FROM iam_users WHERE tenant_id = @existing_tenant_id;