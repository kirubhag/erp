-- Register existing 'erp_database' as a tenant
USE IAM_MasterDB;

-- 1. Insert Tenant Record
SET @new_tenant_id = UUID();

INSERT INTO
    erp_tenants (
        tenant_id,
        tenant_name,
        db_host,
        db_name,
        status
    )
SELECT @new_tenant_id, COALESCE(
        MAX(name), 'Default Organization'
    ), 'localhost', 'erp_database', 'Active'
FROM erp_database.organizations;

-- 2. Add tenant_id column to existing tenant database if not exists
-- Note: This requires the user to run this against the tenant DB as well, or we do it here if we have access.
-- Since we are in IAM_MasterDB context, we can try cross-database update if permissions allow.
USE erp_database;

ALTER TABLE iam_users ADD COLUMN tenant_id VARCHAR(36);

UPDATE iam_users
SET
    tenant_id = @new_tenant_id
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
    @new_tenant_id,
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

SELECT * FROM erp_tenants WHERE db_name = 'erp_database';

SELECT * FROM iam_users WHERE tenant_id = @new_tenant_id;