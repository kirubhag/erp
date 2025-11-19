-- V1_Import_System_Initial_Schema.sql
-- Creates tables for the import system

-- ImportSession table
CREATE TABLE IF NOT EXISTS import_sessions (
    id VARCHAR(50) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT,
    entity_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_format VARCHAR(20),
    total_records INT,
    file_size BIGINT,
    import_type VARCHAR(20),
    duplicate_action VARCHAR(20),
    find_duplicates_by VARCHAR(50),
    enable_manual_approval BOOLEAN DEFAULT false,
    skip_empty_fields BOOLEAN DEFAULT false,
    status VARCHAR(20) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    imported_at TIMESTAMP,
    updated_at TIMESTAMP,
    added_records INT DEFAULT 0,
    updated_records INT DEFAULT 0,
    skipped_records INT DEFAULT 0,
    failed_records INT DEFAULT 0,
    success_rate DOUBLE DEFAULT 0.0,
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_entity_type (entity_type),
    INDEX idx_status (status),
    INDEX idx_uploaded_at (uploaded_at)
);

-- FieldMapping table
CREATE TABLE IF NOT EXISTS field_mappings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_session_id VARCHAR(50) NOT NULL,
    source_column VARCHAR(255),
    source_index INT,
    target_field VARCHAR(255),
    target_field_label VARCHAR(255),
    is_required BOOLEAN DEFAULT false,
    data_type VARCHAR(50),
    FOREIGN KEY (import_session_id) REFERENCES import_sessions(id) ON DELETE CASCADE,
    INDEX idx_session_id (import_session_id)
);

-- ImportResult table
CREATE TABLE IF NOT EXISTS import_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_session_id VARCHAR(50) NOT NULL,
    row_num INT,
    record_id VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    data LONGTEXT,
    errors LONGTEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (import_session_id) REFERENCES import_sessions(id) ON DELETE CASCADE,
    INDEX idx_session_id (import_session_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- FieldMappingTemplate table
CREATE TABLE IF NOT EXISTS field_mapping_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_label VARCHAR(255) NOT NULL,
    is_required BOOLEAN DEFAULT false,
    data_type VARCHAR(50),
    suggestions LONGTEXT,
    section VARCHAR(100),
    display_order INT DEFAULT 0,
    UNIQUE KEY unique_entity_field (entity_type, field_name),
    INDEX idx_entity_type (entity_type),
    INDEX idx_field_name (field_name)
);

-- Create indexes for performance
CREATE INDEX idx_import_sessions_user_entity ON import_sessions(user_id, entity_type);
CREATE INDEX idx_import_results_session_status ON import_results(import_session_id, status);
