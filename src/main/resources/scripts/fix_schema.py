#!/usr/bin/env python3
"""
Schema Fix Script
Automatically fixes primary key column names in tenant_schema.sql
"""

import re
import sys
from pathlib import Path

# Mapping of table names to their correct PK column names
TABLE_PK_MAPPING = {
    "addresses": "address_id",
    "attendance": "attendance_id",
    "custom_views": "custom_view_id",
    "email_logs": "email_log_id",
    "email_templates": "email_template_id",
    "erp_entity_relation": "erp_entity_relation_id",
    "grades": "grade_id",
    "health_records": "health_record_id",
    "iam_users": "user_id",
    "organizations": "organization_id",
    "parent_student_relations": "parent_student_relation_id",
    "parents": "parent_id",
    "permissions": "permission_id",
    "roles": "role_id",
    "staff": "staff_id",
    "students": "student_id",
    "subjects": "subject_id",
    "timetables": "timetable_id",
}

def fix_schema(schema_file):
    """Fix primary key column names in schema file"""
    
    try:
        with open(schema_file, 'r') as f:
            content = f.read()
    except FileNotFoundError:
        print(f"Error: {schema_file} not found")
        return False
    
    original_content = content
    changes_made = []
    
    for table, pk_column in TABLE_PK_MAPPING.items():
        # Pattern to match CREATE TABLE block
        # We need to find "id BIGINT ... PRIMARY KEY" and replace with specific column name
        
        # Find the CREATE TABLE statement for this table
        pattern = rf"(CREATE TABLE[^(]*{table}\s*\(\s*)(id\s+BIGINT\s+NOT\s+NULL\s+AUTO_INCREMENT\s+PRIMARY\s+KEY)"
        
        def replacer(match):
            changes_made.append(f"{table}: id -> {pk_column}")
            return match.group(1) + f"{pk_column} BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY"
        
        content = re.sub(pattern, replacer, content, flags=re.IGNORECASE)
    
    if content == original_content:
        print("No changes needed - schema is already correct")
        return True
    
    # Write the fixed content
    backup_file = str(schema_file) + ".backup"
    with open(backup_file, 'w') as f:
        f.write(original_content)
    print(f"✅ Created backup: {backup_file}")
    
    with open(schema_file, 'w') as f:
        f.write(content)
    
    print(f"\n✅ Fixed {len(changes_made)} tables:")
    for change in changes_made:
        print(f"   - {change}")
    
    return True

def main():
    schema_file = Path("src/main/resources/scripts/tenant_schema.sql")
    
    if not schema_file.exists():
        print(f"Error: {schema_file} not found")
        return 1
    
    print("=== Fixing tenant_schema.sql ===\n")
    
    if fix_schema(schema_file):
        print("\n✅ Schema fix completed successfully!")
        return 0
    else:
        print("\n❌ Schema fix failed")
        return 1

if __name__ == "__main__":
    sys.exit(main())
