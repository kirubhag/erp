#!/usr/bin/env python3
"""
Fix Missing Defaults Script
Automatically adds DEFAULT CURRENT_TIMESTAMP to DATETIME/TIMESTAMP columns that are NOT NULL but missing defaults.
"""

import re
import sys
from pathlib import Path

def fix_defaults(file_path):
    print(f"\n🔧 Fixing defaults in {file_path}...")
    
    try:
        with open(file_path, 'r') as f:
            content = f.read()
    except FileNotFoundError:
        print(f"❌ File not found: {file_path}")
        return False
        
    original_content = content
    lines = content.split('\n')
    new_lines = []
    fixed_count = 0
    
    for line in lines:
        # Check for DATETIME/TIMESTAMP NOT NULL without DEFAULT
        # We need to be careful not to match lines that already have DEFAULT
        
        # Regex explanation:
        # \b(DATETIME|TIMESTAMP)\b : Match type
        # .* : Match anything in between (e.g. precision)
        # \bNOT\s+NULL\b : Match NOT NULL
        # (?!.*\bDEFAULT\b) : Negative lookahead ensuring DEFAULT is NOT present later in the line
        
        # Note: This simple regex approach works because we process line by line and schema files usually have one column per line.
        
        if (re.search(r'\b(DATETIME|TIMESTAMP)\b', line, re.IGNORECASE) and 
            re.search(r'\bNOT\s+NULL\b', line, re.IGNORECASE) and 
            not re.search(r'\bDEFAULT\b', line, re.IGNORECASE)):
            
            # Exclude PRIMARY KEY lines just in case
            if 'PRIMARY KEY' in line.upper():
                new_lines.append(line)
                continue
                
            # Add DEFAULT CURRENT_TIMESTAMP
            # We append it before the trailing comma if it exists
            if line.strip().endswith(','):
                new_line = line.rsplit(',', 1)[0] + " DEFAULT CURRENT_TIMESTAMP,"
            else:
                new_line = line + " DEFAULT CURRENT_TIMESTAMP"
                
            print(f"   Fixed: {line.strip()} -> {new_line.strip()}")
            new_lines.append(new_line)
            fixed_count += 1
        else:
            new_lines.append(line)
            
    if fixed_count > 0:
        # Create backup
        backup_path = str(file_path) + ".defaults_bak"
        with open(backup_path, 'w') as f:
            f.write(original_content)
        print(f"   💾 Backup created: {backup_path}")
        
        # Write fixes
        with open(file_path, 'w') as f:
            f.write('\n'.join(new_lines))
        print(f"   ✅ Fixed {fixed_count} columns in {file_path}")
        return True
    else:
        print(f"   ✅ No fixes needed for {file_path}")
        return True

def main():
    files = [
        "src/main/resources/schema.sql",
        "src/main/resources/scripts/tenant_schema.sql"
    ]
    
    for f in files:
        fix_defaults(f)

if __name__ == "__main__":
    main()
