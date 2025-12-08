#!/usr/bin/env python3
"""
Audit Missing Defaults Script
Scans schema files for columns that are NOT NULL but missing DEFAULT values.
Focuses on BOOLEAN and DATETIME columns which commonly cause runtime errors.
"""

import re
import sys

def audit_defaults(file_path):
    print(f"\n🔍 Auditing {file_path} for missing defaults...")
    
    try:
        with open(file_path, 'r') as f:
            content = f.read()
    except FileNotFoundError:
        print(f"❌ File not found: {file_path}")
        return False

    # Regex to find CREATE TABLE blocks
    table_pattern = re.compile(r"CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?(\w+)\s*\((.*?)\);", re.DOTALL | re.IGNORECASE)
    
    tables = table_pattern.findall(content)
    
    issues_found = 0
    
    for table_name, body in tables:
        lines = [line.strip() for line in body.split(',')]
        
        for line in lines:
            # Skip comments and keys
            if line.startswith('--') or line.upper().startswith(('PRIMARY', 'FOREIGN', 'UNIQUE', 'KEY', 'INDEX', 'CONSTRAINT')):
                continue
                
            # Check for BOOLEAN or DATETIME columns
            is_boolean = re.search(r'\b(BOOLEAN|TINYINT\(1\))\b', line, re.IGNORECASE)
            is_datetime = re.search(r'\b(DATETIME|TIMESTAMP)\b', line, re.IGNORECASE)
            
            if is_boolean or is_datetime:
                # Check if NOT NULL is present
                is_not_null = re.search(r'\bNOT\s+NULL\b', line, re.IGNORECASE)
                
                # Check if DEFAULT is present
                has_default = re.search(r'\bDEFAULT\b', line, re.IGNORECASE)
                
                if is_not_null and not has_default:
                    # Exclude created_time/created_at if they are handled by code (but usually they should have DEFAULT CURRENT_TIMESTAMP)
                    # Exclude primary keys (though we filtered keys, inline PKs might be here)
                    if 'PRIMARY KEY' in line.upper():
                        continue
                        
                    print(f"   ⚠️  Table '{table_name}': Column '{line.split()[0]}' is NOT NULL but missing DEFAULT")
                    issues_found += 1

    if issues_found == 0:
        print(f"   ✅ No missing defaults found in {file_path}")
        return True
    else:
        print(f"   ❌ Found {issues_found} potential missing defaults in {file_path}")
        return False

def main():
    files = [
        "src/main/resources/schema.sql",
        "src/main/resources/scripts/tenant_schema.sql"
    ]
    
    for f in files:
        audit_defaults(f)

if __name__ == "__main__":
    main()
