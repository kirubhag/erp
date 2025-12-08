#!/bin/bash

# Script to audit primary key column names in tenant_schema.sql
# Compares entity @AttributeOverride with actual schema definitions

echo "=== Schema Audit Report ==="
echo ""

# List of entities with @AttributeOverride and their expected PK column names
declare -A entities=(
    ["students"]="student_id"
    ["staff"]="staff_id"
    ["parents"]="parent_id"
    ["subjects"]="subject_id"
    ["grades"]="grade_id"
    ["timetables"]="timetable_id"
    ["attendance"]="attendance_id"
    ["health_records"]="health_record_id"
    ["erp_sections"]="erp_section_id"
    ["erp_fields"]="erp_field_id"
    ["email_templates"]="email_template_id"
    ["email_logs"]="email_log_id"
    ["roles"]="role_id"
    ["permissions"]="permission_id"
    ["addresses"]="address_id"
    ["iam_users"]="user_id"
    ["organizations"]="organization_id"
    ["erp_entity_relation"]="erp_entity_relation_id"
    ["parent_student_relations"]="parent_student_relation_id"
    ["custom_views"]="custom_view_id"
    ["erp_attachments"]="erp_attachment_id"
)

SCHEMA_FILE="src/main/resources/scripts/tenant_schema.sql"

echo "Checking tenant_schema.sql for primary key mismatches..."
echo ""

for table in "${!entities[@]}"; do
    expected_pk="${entities[$table]}"
    
    # Extract the CREATE TABLE block for this table
    table_def=$(awk "/CREATE TABLE.*$table /,/^);/" "$SCHEMA_FILE" 2>/dev/null)
    
    if [ -z "$table_def" ]; then
        echo "❌ Table '$table' NOT FOUND in schema"
        continue
    fi
    
    # Check if the expected PK column exists
    if echo "$table_def" | grep -q "$expected_pk.*PRIMARY KEY"; then
        echo "✅ $table: Uses correct PK '$expected_pk'"
    elif echo "$table_def" | grep -q "id.*PRIMARY KEY"; then
        echo "❌ $table: Uses 'id' instead of '$expected_pk' - NEEDS FIX"
    else
        echo "⚠️  $table: PK column unclear - manual check needed"
    fi
done

echo ""
echo "=== End of Audit ==="
