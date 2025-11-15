#!/usr/bin/env python3
"""
Script to update email domains in all XML files from springfieldcentral.edu to school.edu
"""

import os
import xml.etree.ElementTree as ET
from pathlib import Path
import re

def find_xml_files(data_dir):
    """Find all XML files in the data directory and subdirectories."""
    xml_files = []
    for root, dirs, files in os.walk(data_dir):
        for file in files:
            if file.endswith('.xml'):
                xml_files.append(os.path.join(root, file))
    return xml_files

def update_email_domains(file_path, old_domain, new_domain):
    """
    Update email domains in an XML file.
    Returns the count of replacements made.
    """
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Count occurrences before
        occurrences_before = content.count(old_domain)
        
        if occurrences_before == 0:
            return 0, file_path
        
        # Replace all occurrences of the old domain with new domain
        updated_content = content.replace(old_domain, new_domain)
        
        # Write back to file
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(updated_content)
        
        return occurrences_before, file_path
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return 0, file_path

def main():
    # Configuration
    data_dir = '/Users/kirubha-2911/Documents/GitHub/erp/src/main/resources/data'
    old_domain = 'springfieldcentral.edu'
    new_domain = 'school.edu'
    
    print(f"Scanning XML files in: {data_dir}")
    print(f"Replacing '{old_domain}' with '{new_domain}'\n")
    
    # Find all XML files
    xml_files = find_xml_files(data_dir)
    print(f"Found {len(xml_files)} XML file(s)\n")
    
    # Process each file
    total_replacements = 0
    updated_files = []
    
    for xml_file in sorted(xml_files):
        count, file_path = update_email_domains(xml_file, old_domain, new_domain)
        if count > 0:
            relative_path = xml_file.replace(data_dir, '').lstrip('/')
            updated_files.append((relative_path, count))
            total_replacements += count
            print(f"✓ {relative_path}")
            print(f"  Updated {count} email domain(s)")
    
    # Summary
    print(f"\n{'='*60}")
    print(f"SUMMARY:")
    print(f"{'='*60}")
    print(f"Total files updated: {len(updated_files)}")
    print(f"Total email domains replaced: {total_replacements}")
    
    if updated_files:
        print(f"\nUpdated files:")
        for file_name, count in updated_files:
            print(f"  • {file_name}: {count} replacement(s)")
    else:
        print("\nNo email domains to update - all files already have correct domains!")
    
    print(f"{'='*60}\n")

if __name__ == '__main__':
    main()
