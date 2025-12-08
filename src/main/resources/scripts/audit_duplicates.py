#!/usr/bin/env python3
"""
Schema Audit Script
Checks for duplicate column definitions in CREATE TABLE statements
"""

import re
import sys
from pathlib import Path

def audit_schema_duplicates(schema_file):
    """Check for duplicate column names in schema file"""
    
    try:
        with open(schema_file, 'r') as f:
            content = f.read()
    except FileNotFoundError:
        print(f"Error: {schema_file} not found")
        return False
    
    print(f"Auditing {schema_file}...")
    
    # Regex to capture CREATE TABLE blocks
    # Matches "CREATE TABLE [IF NOT EXISTS] table_name ( ... );"
    table_pattern = re.compile(r"CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?(\w+)\s*\((.*?)\);", re.DOTALL | re.IGNORECASE)
    
    tables = table_pattern.findall(content)
    
    issues_found = 0
    
    for table_name, body in tables:
        # Extract column names
        # Simple regex to match "column_name TYPE"
        # We assume column definition starts with a word (column name) followed by whitespace and another word (type)
        # We need to filter out KEY, PRIMARY KEY, FOREIGN KEY, INDEX, CONSTRAINT lines
        
        lines = [line.strip() for line in body.split(',')]
        columns = []
        
        for line in lines:
            # Clean up line
            line = line.strip()
            if not line:
                continue
                
            # Skip comments
            if line.startswith('--'):
                continue
                
            # Skip constraints and indexes
            upper_line = line.upper()
            if (upper_line.startswith('PRIMARY KEY') or 
                upper_line.startswith('FOREIGN KEY') or 
                upper_line.startswith('UNIQUE KEY') or 
                upper_line.startswith('KEY') or 
                upper_line.startswith('INDEX') or 
                upper_line.startswith('CONSTRAINT') or
                upper_line.startswith('CHECK')):
                continue
            
            # Extract first word as column name
            match = re.match(r'`?(\w+)`?', line)
            if match:
                col_name = match.group(1)
                columns.append(col_name)
        
        # Check for duplicates
        seen = set()
        duplicates = set()
        for col in columns:
            if col in seen:
                duplicates.add(col)
            seen.add(col)
            
        if duplicates:
            print(f"❌ Table '{table_name}' has duplicate columns: {', '.join(duplicates)}")
            issues_found += 1
        
        # Check for potential PK collision (e.g. table_id vs id)
        # If table has 'id' AND 'table_name_id' (singular), it might be a collision if not handled
        # But here we are specifically looking for the "duplicate column name" error which means EXACT match
            
    if issues_found == 0:
        print(f"✅ No duplicate columns found in {len(tables)} tables.")
        return True
    else:
        print(f"❌ Found issues in {issues_found} tables.")
        return False

def main():
    files_to_check = [
        "src/main/resources/scripts/tenant_schema.sql",
        "src/main/resources/schema.sql"
    ]
    
    has_error = False
    for file_path in files_to_check:
        if not audit_schema_duplicates(file_path):
            has_error = True
        print("-" * 40)
            
    return 1 if has_error else 0

if __name__ == "__main__":
    sys.exit(main())
