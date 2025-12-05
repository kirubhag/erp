-- IAM Master Database Seed Data
USE IAM_MasterDB;

-- Insert Sample Tenants
INSERT INTO
    erp_tenants (
        tenant_name,
        db_host,
        db_name,
        status
    )
VALUES (
        'Acme Corp',
        'localhost',
        'erp_acme',
        'Active'
    ),
    (
        'Globex Inc',
        'localhost',
        'erp_globex',
        'Active'
    ),
    (
        'Initech',
        'localhost',
        'erp_initech',
        'Suspended'
    );

-- Insert Sample Users
-- Passwords should be hashed in production. These are placeholders.
INSERT INTO
    erp_users (
        tenant_id,
        email,
        password_hash,
        global_roles
    )
VALUES (
        1,
        'admin@acme.com',
        'hashed_secret_123',
        '["System Admin"]'
    ),
    (
        1,
        'support@acme.com',
        'hashed_secret_123',
        '["Client Support"]'
    ),
    (
        2,
        'admin@globex.com',
        'hashed_secret_456',
        '["System Admin"]'
    ),
    (
        3,
        'admin@initech.com',
        'hashed_secret_789',
        '["System Admin"]'
    );