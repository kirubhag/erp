#!/usr/bin/env python3
"""
Schema FK Fix Script
Automatically fixes foreign key references in tenant_schema.sql
Replaces REFERENCES table(id) with REFERENCES table(table_id)
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
    "erp_sections": "erp_section_id",
    "erp_fields": "erp_field_id",
    "erp_entities": "erp_entity_id"
}

def fix_schema_fk(schema_file):
    """Fix foreign key references in schema file"""
    
    try:
        with open(schema_file, 'r') as f:
            content = f.read()
    except FileNotFoundError:
        print(f"Error: {schema_file} not found")
        return False
    
    original_content = content
    changes_made = []
    
    # Pattern to match REFERENCES table (id)
    # We capture the table name
    pattern = r"REFERENCES\s+(\w+)\s*\(\s*id\s*\)"
    
    def replacer(match):
        table_name = match.group(1)
        if table_name in TABLE_PK_MAPPING:
            correct_pk = TABLE_PK_MAPPING[table_name]
            changes_made.append(f"REFERENCES {table_name}(id) -> REFERENCES {table_name}({correct_pk})")
            return f"REFERENCES {table_name} ({correct_pk})"
        else:
            return match.group(0) # No change if table not in mapping
            
    content = re.sub(pattern, replacer, content, flags=re.IGNORECASE)
    
    if content == original_content:
        print("No changes needed - schema FKs are already correct")
        return True
    
    # Write the fixed content
    backup_file = str(schema_file) + ".fk_backup"
    with open(backup_file, 'w') as f:
        f.write(original_content)
    print(f"✅ Created backup: {backup_file}")
    
    with open(schema_file, 'w') as f:
        f.write(content)
    
    print(f"\n✅ Fixed {len(changes_made)} FK references")
    # Print distinct changes
    for change in sorted(set(changes_made)):
        print(f"   - {change}")
    
    return True

def main():
    schema_file = Path("src/main/resources/schema.sql")
    
    if not schema_file.exists():
        print(f"Error: {schema_file} not found")
        return 1
    
    print("=== Fixing FKs in tenant_schema.sql ===\n")
    
    if fix_schema_fk(schema_file):
        print("\n✅ Schema FK fix completed successfully!")
        return 0
    else:
        print("\n❌ Schema FK fix failed")
        return 1

if __name__ == "__main__":
    sys.exit(main())
