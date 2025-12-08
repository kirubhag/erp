#!/usr/bin/env python3
"""
Compare master schema.sql with tenant_schema.sql
Find discrepancies in table definitions
"""

import re
import sys

def extract_tables(filename):
    """Extract table names from SQL file"""
    with open(filename, 'r') as f:
        content = f.read()
    
    # Find all CREATE TABLE statements
    pattern = r'CREATE TABLE[^(]*?(\w+)\s*\('
    tables = re.findall(pattern, content, re.IGNORECASE)
    return set(tables)

def extract_table_definition(filename, table_name):
    """Extract full table definition"""
    with open(filename, 'r') as f:
        content = f.read()
    
    # Find the CREATE TABLE block for this table
    pattern = rf'CREATE TABLE[^(]*{table_name}\s*\((.*?)\);'
    match = re.search(pattern, content, re.DOTALL | re.IGNORECASE)
    
    if match:
        return match.group(1).strip()
    return None

def extract_primary_key(table_def):
    """Extract primary key column name from table definition"""
    if not table_def:
        return None
    
    # Look for PRIMARY KEY
    pk_pattern = r'(\w+)\s+\w+[^,]*PRIMARY KEY'
    match = re.search(pk_pattern, table_def, re.IGNORECASE)
    
    if match:
        return match.group(1)
    return None

def main():
    master_file = "src/main/resources/schema.sql"
    tenant_file = "src/main/resources/scripts/tenant_schema.sql"
    
    print("=== Schema Comparison Report ===\n")
    
    # Extract tables from both files
    master_tables = extract_tables(master_file)
    tenant_tables = extract_tables(tenant_file)
    
    print(f"Master schema tables: {len(master_tables)}")
    print(f"Tenant schema tables: {len(tenant_tables)}\n")
    
    # Find differences
    only_in_master = master_tables - tenant_tables
    only_in_tenant = tenant_tables - master_tables
    common_tables = master_tables & tenant_tables
    
    if only_in_master:
        print(f"❌ Tables only in MASTER ({len(only_in_master)}):")
        for table in sorted(only_in_master):
            print(f"   - {table}")
        print()
    
    if only_in_tenant:
        print(f"❌ Tables only in TENANT ({len(only_in_tenant)}):")
        for table in sorted(only_in_tenant):
            print(f"   - {table}")
        print()
    
    # Check primary keys for common tables
    print(f"Checking primary keys for {len(common_tables)} common tables...\n")
    pk_mismatches = []
    
    for table in sorted(common_tables):
        master_def = extract_table_definition(master_file, table)
        tenant_def = extract_table_definition(tenant_file, table)
        
        master_pk = extract_primary_key(master_def)
        tenant_pk = extract_primary_key(tenant_def)
        
        if master_pk != tenant_pk:
            pk_mismatches.append((table, master_pk, tenant_pk))
            print(f"❌ {table}: MASTER uses '{master_pk}' vs TENANT uses '{tenant_pk}'")
    
    if not pk_mismatches:
        print("✅ All common tables have matching primary keys")
    
    print(f"\n=== Summary ===")
    print(f"Tables only in master: {len(only_in_master)}")
    print(f"Tables only in tenant: {len(only_in_tenant)}")
    print(f"Primary key mismatches: {len(pk_mismatches)}")
    
    return len(only_in_master) + len(only_in_tenant) + len(pk_mismatches)

if __name__ == "__main__":
    sys.exit(main())
