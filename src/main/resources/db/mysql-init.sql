-- Minimal schema to allow Hibernate update mode to work with MappedSuperclass

-- Base tables for JPA entities to exist with basic structure
-- Hibernate in update mode will add missing columns from annotations

CREATE TABLE IF NOT EXISTS grades (id BIGINT PRIMARY KEY AUTO_INCREMENT) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS subjects (id BIGINT PRIMARY KEY AUTO_INCREMENT) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS timetables (id BIGINT PRIMARY KEY AUTO_INCREMENT) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS students (id BIGINT PRIMARY KEY AUTO_INCREMENT) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS iam_users (id BIGINT PRIMARY KEY AUTO_INCREMENT) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS roles (id BIGINT PRIMARY KEY AUTO_INCREMENT) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS permissions (id BIGINT PRIMARY KEY AUTO_INCREMENT) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS erp_fields (id BIGINT PRIMARY KEY AUTO_INCREMENT) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS user_roles (user_id BIGINT, role_id BIGINT, PRIMARY KEY (user_id, role_id)) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS role_permissions (role_id BIGINT, permission_id BIGINT, PRIMARY KEY (role_id, permission_id)) ENGINE=InnoDB;

