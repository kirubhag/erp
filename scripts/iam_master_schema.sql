-- IAM Master Database Schema
-- Database: IAM_MasterDB

CREATE DATABASE IF NOT EXISTS IAM_MasterDB;

USE IAM_MasterDB;

-- Table: erp_tenants
-- Stores configuration for each tenant organization.
CREATE TABLE IF NOT EXISTS erp_tenants (
    tenant_id INT AUTO_INCREMENT PRIMARY KEY,
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

-- Table: erp_users
-- Stores global user identities and their association with tenants.
CREATE TABLE IF NOT EXISTS erp_users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id INT NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    global_roles JSON, -- Stores roles like ["System Admin", "Client Support"]
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES erp_tenants (tenant_id) ON DELETE CASCADE,
    UNIQUE KEY uk_email (email)
);

-- Index for faster lookup during login
CREATE INDEX idx_user_email ON erp_users (email);