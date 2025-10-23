#!/bin/bash

# Script to generate complete custom field table creation SQL
# This generates the full 250-column structure for each entity

cat > complete_custom_field_migration.sql << 'EOF'
-- Complete Custom Field Tables Migration
-- This script creates all custom field tables with 250 columns each

EOF

# Function to generate custom field table
generate_custom_field_table() {
    local table_name=$1
    local foreign_table=$2
    local foreign_key_name=$3
    
    echo "-- ${table_name} custom field table" >> complete_custom_field_migration.sql
    echo "CREATE TABLE IF NOT EXISTS ${table_name} (" >> complete_custom_field_migration.sql
    echo "    id BIGINT AUTO_INCREMENT PRIMARY KEY," >> complete_custom_field_migration.sql
    echo "    entity_id BIGINT NOT NULL," >> complete_custom_field_migration.sql
    echo "" >> complete_custom_field_migration.sql
    echo "    -- String custom fields (1-100)" >> complete_custom_field_migration.sql
    
    # Generate 100 string fields
    for i in {1..100}; do
        if [ $i -lt 100 ]; then
            echo "    custom_field_${i} TEXT(2000)," >> complete_custom_field_migration.sql
        else
            echo "    custom_field_${i} TEXT(2000)," >> complete_custom_field_migration.sql
        fi
    done
    
    echo "" >> complete_custom_field_migration.sql
    echo "    -- Numeric custom fields (101-150)" >> complete_custom_field_migration.sql
    
    # Generate 50 numeric fields
    for i in {1..50}; do
        if [ $i -lt 50 ]; then
            echo "    custom_numeric_${i} DOUBLE," >> complete_custom_field_migration.sql
        else
            echo "    custom_numeric_${i} DOUBLE," >> complete_custom_field_migration.sql
        fi
    done
    
    echo "" >> complete_custom_field_migration.sql
    echo "    -- Date custom fields (151-200)" >> complete_custom_field_migration.sql
    
    # Generate 50 date fields
    for i in {1..50}; do
        if [ $i -lt 50 ]; then
            echo "    custom_date_${i} DATE," >> complete_custom_field_migration.sql
        else
            echo "    custom_date_${i} DATE," >> complete_custom_field_migration.sql
        fi
    done
    
    echo "" >> complete_custom_field_migration.sql
    echo "    -- Boolean custom fields (201-250)" >> complete_custom_field_migration.sql
    
    # Generate 50 boolean fields
    for i in {1..50}; do
        if [ $i -lt 50 ]; then
            echo "    custom_boolean_${i} BOOLEAN," >> complete_custom_field_migration.sql
        else
            echo "    custom_boolean_${i} BOOLEAN," >> complete_custom_field_migration.sql
        fi
    done
    
    echo "" >> complete_custom_field_migration.sql
    echo "    FOREIGN KEY (entity_id) REFERENCES ${foreign_table}(id) ON DELETE CASCADE," >> complete_custom_field_migration.sql
    echo "    UNIQUE KEY ${foreign_key_name} (entity_id)" >> complete_custom_field_migration.sql
    echo ");" >> complete_custom_field_migration.sql
    echo "" >> complete_custom_field_migration.sql
}

# Generate all custom field tables
generate_custom_field_table "students_custom_field" "students" "unique_student_custom"
generate_custom_field_table "parents_custom_field" "parents" "unique_parent_custom"
generate_custom_field_table "staff_custom_field" "staff" "unique_staff_custom"
generate_custom_field_table "health_records_custom_field" "health_records" "unique_health_record_custom"
generate_custom_field_table "attendance_custom_field" "attendance" "unique_attendance_custom"
generate_custom_field_table "organizations_custom_field" "organizations" "unique_organization_custom"
generate_custom_field_table "users_custom_field" "users" "unique_user_custom"
generate_custom_field_table "email_templates_custom_field" "email_templates" "unique_email_template_custom"
generate_custom_field_table "email_logs_custom_field" "email_logs" "unique_email_log_custom"

# Add indexes
cat >> complete_custom_field_migration.sql << 'EOF'
-- Create indexes for better performance
CREATE INDEX idx_students_custom_entity ON students_custom_field(entity_id);
CREATE INDEX idx_parents_custom_entity ON parents_custom_field(entity_id);
CREATE INDEX idx_staff_custom_entity ON staff_custom_field(entity_id);
CREATE INDEX idx_health_records_custom_entity ON health_records_custom_field(entity_id);
CREATE INDEX idx_attendance_custom_entity ON attendance_custom_field(entity_id);
CREATE INDEX idx_organizations_custom_entity ON organizations_custom_field(entity_id);
CREATE INDEX idx_users_custom_entity ON users_custom_field(entity_id);
CREATE INDEX idx_email_templates_custom_entity ON email_templates_custom_field(entity_id);
CREATE INDEX idx_email_logs_custom_entity ON email_logs_custom_field(entity_id);
EOF

echo "Generated complete_custom_field_migration.sql with all 250-column custom field tables"