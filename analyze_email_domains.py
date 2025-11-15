#!/usr/bin/env python3
"""
Script to generate a detailed report of all XML files with email fields
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

def analyze_email_fields(file_path):
    """
    Analyze an XML file to find email fields and their domains.
    Returns a dict with file info and email statistics.
    """
    email_pattern = re.compile(r'email="([^"]+)"', re.IGNORECASE)
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        emails = email_pattern.findall(content)
        
        if not emails:
            return None
        
        domains = {}
        for email in emails:
            domain = email.split('@')[1] if '@' in email else 'unknown'
            domains[domain] = domains.get(domain, 0) + 1
        
        return {
            'file': file_path,
            'email_count': len(emails),
            'domains': domains
        }
    except Exception as e:
        return None

def main():
    # Configuration
    data_dir = '/Users/kirubha-2911/Documents/GitHub/erp/src/main/resources/data'
    
    print(f"Analyzing email fields in XML files")
    print(f"Directory: {data_dir}\n")
    print(f"{'='*80}")
    
    # Find all XML files
    xml_files = find_xml_files(data_dir)
    print(f"Found {len(xml_files)} XML file(s)\n")
    
    # Analyze each file
    files_with_emails = []
    total_emails = 0
    all_domains = {}
    
    for xml_file in sorted(xml_files):
        analysis = analyze_email_fields(xml_file)
        if analysis:
            files_with_emails.append(analysis)
            total_emails += analysis['email_count']
            for domain, count in analysis['domains'].items():
                all_domains[domain] = all_domains.get(domain, 0) + count
    
    # Print detailed report
    print(f"FILES WITH EMAIL FIELDS ({len(files_with_emails)} total):\n")
    
    for item in files_with_emails:
        relative_path = item['file'].replace(data_dir, '').lstrip('/')
        print(f"📄 {relative_path}")
        print(f"   Emails: {item['email_count']}")
        print(f"   Domains:")
        for domain, count in sorted(item['domains'].items()):
            print(f"     • {domain}: {count}")
        print()
    
    # Summary statistics
    print(f"{'='*80}")
    print(f"SUMMARY STATISTICS:")
    print(f"{'='*80}")
    print(f"Total files with email fields: {len(files_with_emails)}")
    print(f"Total email records: {total_emails}")
    print(f"\nDomain distribution:")
    for domain in sorted(all_domains.keys()):
        count = all_domains[domain]
        percentage = (count / total_emails) * 100 if total_emails > 0 else 0
        print(f"  • {domain}: {count} ({percentage:.1f}%)")
    
    # Check for old domain
    if 'springfieldcentral.edu' in all_domains:
        print(f"\n⚠️  WARNING: Found {all_domains['springfieldcentral.edu']} 'springfieldcentral.edu' email(s)")
    else:
        print(f"\n✅ No 'springfieldcentral.edu' domain found - all domains are updated!")
    
    print(f"{'='*80}\n")

if __name__ == '__main__':
    main()
