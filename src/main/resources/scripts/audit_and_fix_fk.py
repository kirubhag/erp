#!/usr/bin/env python3
"""
Robust Schema FK Audit & Fix Script
1. Parses CREATE TABLE statements to find the ACTUAL Primary Key for each table.
2. Checks all FOREIGN KEY references to ensure they point to the correct PK.
3. Fixes mismatches automatically.
"""

import re
import sys
from pathlib import Path

def parse_primary_keys(content):
    """
    Parses SQL content and returns a dictionary {table_name: pk_column_name}
    """
    pk_map = {}
    
    # Regex to find CREATE TABLE blocks
    table_pattern = re.compile(r"CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?(\w+)\s*\((.*?)\);", re.DOTALL | re.IGNORECASE)
    
    tables = table_pattern.findall(content)
    
    for table_name, body in tables:
        # Method 1: Look for "col_name TYPE ... PRIMARY KEY"
        # We split by comma, but need to be careful about commas in parens (like DECIMAL(10,2))
        # For simplicity, let's try a regex on the body first
        
        # Check for inline PRIMARY KEY
        inline_pk_match = re.search(r"^\s*(\w+)\s+[^,]*PRIMARY KEY", body, re.MULTILINE | re.IGNORECASE)
        if inline_pk_match:
            pk_map[table_name] = inline_pk_match.group(1)
            continue
            
        # Check for PRIMARY KEY (col) constraint at the end
        constraint_pk_match = re.search(r"PRIMARY\s+KEY\s*\(\s*(\w+)\s*\)", body, re.IGNORECASE)
        if constraint_pk_match:
            pk_map[table_name] = constraint_pk_match.group(1)
            continue
            
    return pk_map

def audit_and_fix(file_path):
    print(f"\n🔍 Auditing {file_path}...")
    
    try:
        with open(file_path, 'r') as f:
            content = f.read()
    except FileNotFoundError:
        print(f"❌ File not found: {file_path}")
        return False

    # 1. Build the Truth Map (Table -> Actual PK)
    pk_map = parse_primary_keys(content)
    print(f"   Found {len(pk_map)} table definitions.")
    
    # 2. Find and Fix FKs
    # Pattern: FOREIGN KEY (local_col) REFERENCES ref_table (ref_col)
    # We use a callback to check and fix
    
    fixed_count = 0
    errors = []
    
    def fk_replacer(match):
        nonlocal fixed_count
        full_match = match.group(0)
        local_col = match.group(1)
        ref_table = match.group(2)
        ref_col = match.group(3)
        
        if ref_table in pk_map:
            actual_pk = pk_map[ref_table]
            if ref_col != actual_pk:
                print(f"   ⚠️  Mismatch in {ref_table}: References '{ref_col}', but PK is '{actual_pk}'")
                fixed_count += 1
                return f"FOREIGN KEY ({local_col}) REFERENCES {ref_table} ({actual_pk})"
        else:
            # Referenced table might not be in this file (e.g. cross-schema? unlikely here)
            # Or regex failed to parse the table definition
            pass
            
        return full_match

    # Regex for FOREIGN KEY constraint
    # FOREIGN KEY (col) REFERENCES table (col)
    fk_pattern = re.compile(r"FOREIGN\s+KEY\s*\(\s*(\w+)\s*\)\s*REFERENCES\s+(\w+)\s*\(\s*(\w+)\s*\)", re.IGNORECASE)
    
    new_content = fk_pattern.sub(fk_replacer, content)
    
    if fixed_count > 0:
        # Create backup
        backup_path = str(file_path) + ".bak"
        with open(backup_path, 'w') as f:
            f.write(content)
        print(f"   💾 Backup created: {backup_path}")
        
        # Write fixes
        with open(file_path, 'w') as f:
            f.write(new_content)
        print(f"   ✅ Fixed {fixed_count} mismatches in {file_path}")
        return True
    else:
        print(f"   ✅ No FK mismatches found in {file_path}")
        return True

def main():
    files = [
        "src/main/resources/schema.sql",
        "src/main/resources/scripts/tenant_schema.sql"
    ]
    
    for f in files:
        audit_and_fix(f)

if __name__ == "__main__":
    main()
