#!/usr/bin/env python3
"""
Schema Audit Script
Compares JPA entity @AttributeOverride annotations with tenant_schema.sql
"""

import re
import sys

# Entities with @AttributeOverride and their expected PK column names
ENTITIES = {
    "students": "student_id",
    "staff": "staff_id",
    "parents": "parent_id",
    "subjects": "subject_id",
    "grades": "grade_id",
    "timetables": "timetable_id",
    "attendance": "attendance_id",
    "health_records": "health_record_id",
    "erp_sections": "erp_section_id",
    "erp_fields": "erp_field_id",
    "email_templates": "email_template_id",
    "email_logs": "email_log_id",
    "roles": "role_id",
    "permissions": "permission_id",
    "addresses": "address_id",
    "iam_users": "user_id",
    "organizations": "organization_id",
    "erp_entity_relation": "erp_entity_relation_id",
    "parent_student_relations": "parent_student_relation_id",
    "custom_views": "custom_view_id",
    "erp_attachments": "erp_attachment_id",
    "student_guardian_info": "student_guardian_info_id",
    "student_medical_info": "student_medical_info_id",
}

def main():
    schema_file = "src/main/resources/scripts/tenant_schema.sql"
    
    try:
        with open(schema_file, 'r') as f:
            schema_content = f.read()
    except FileNotFoundError:
        print(f"Error: {schema_file} not found")
        sys.exit(1)
    
    print("=== Schema Audit Report ===\n")
    print("Checking tenant_schema.sql for primary key mismatches...\n")
    
    issues = []
    
    for table, expected_pk in sorted(ENTITIES.items()):
        # Find the CREATE TABLE block
        pattern = rf"CREATE TABLE[^(]*{table}\s*\((.*?)\);"
        match = re.search(pattern, schema_content, re.DOTALL | re.IGNORECASE)
        
        if not match:
            print(f"❌ Table '{table}' NOT FOUND in schema")
            issues.append(f"{table}: Table not found")
            continue
        
        table_def = match.group(1)
        
        # Check for the expected PK column
        if re.search(rf"{expected_pk}.*PRIMARY KEY", table_def, re.IGNORECASE):
            print(f"✅ {table}: Uses correct PK '{expected_pk}'")
        elif re.search(r"\bid\s+BIGINT.*PRIMARY KEY", table_def, re.IGNORECASE):
            print(f"❌ {table}: Uses 'id' instead of '{expected_pk}' - NEEDS FIX")
            issues.append(f"{table}: id -> {expected_pk}")
        else:
            print(f"⚠️  {table}: PK column unclear - manual check needed")
            issues.append(f"{table}: Manual check needed")
    
    print(f"\n=== Summary ===")
    print(f"Total entities checked: {len(ENTITIES)}")
    print(f"Issues found: {len(issues)}")
    
    if issues:
        print("\nTables needing fixes:")
        for issue in issues:
            print(f"  - {issue}")
    
    return len(issues)

if __name__ == "__main__":
    sys.exit(main())
