-- IAM Master Database Schema
-- Database: IAM_MasterDB

CREATE DATABASE IF NOT EXISTS IAM_MasterDB;

USE IAM_MasterDB;

-- Table: erp_tenants
-- Stores configuration for each tenant organization.
CREATE TABLE IF NOT EXISTS erp_tenants (
    tenant_id VARCHAR(36) NOT NULL PRIMARY KEY,
    tenant_name VARCHAR(255) NOT NULL,
    db_host VARCHAR(255) NOT NULL,
    db_name VARCHAR(255) NOT NULL,
    status ENUM(
        'Active',
        'Inactive',
        'Suspended'
    ) NOT NULL DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tenant_db (db_host, db_name)
);

-- Table: iam_users
-- Stores global user identities and their association with tenants.
CREATE TABLE IF NOT EXISTS iam_users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    last_login_date DATETIME,
    password_change_date DATETIME,
    avatar_url VARCHAR(500),
    organization_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES erp_tenants (tenant_id) ON DELETE CASCADE,
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_username (username)
);