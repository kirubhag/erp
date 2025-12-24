#!/usr/bin/env python3
"""
Script to fix XML file formats for ERP system.
Converts old format section files to use <sectionName> and <sectionLabel>.
Converts old format field files to use <fieldName>, <fieldLabel> etc. as child elements.
"""

import os
import re
import xml.etree.ElementTree as ET
from pathlib import Path

DATA_DIR = Path(__file__).parent.parent / "src" / "main" / "resources" / "data"

def convert_name_to_section_format(name_value):
    """Convert display name to section_name format (snake_case)"""
    # Convert to lowercase and replace spaces/special chars with underscores
    section_name = re.sub(r'[^a-zA-Z0-9]+', '_', name_value.lower()).strip('_')
    return section_name

def derive_entity_type_from_section_filename(file_path):
    """Derive entity type from filename like 'library_loan_sections.xml' -> 'LIBRARY_LOAN'"""
    filename = file_path.stem  # e.g., 'library_loan_sections'
    # Remove '_sections' suffix
    entity_name = filename.replace('_sections', '')
    # Convert to uppercase
    return entity_name.upper()

def fix_section_file(file_path):
    """Fix a sections XML file to use sectionName and sectionLabel"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Check if file already uses correct format
        if '<sectionName>' in content and '<entitySections' in content:
            print(f"  SKIP (already correct): {file_path.name}")
            return False
        
        # Check if this is a <sections> root element with <title> (needs full conversion)
        if '<sections>' in content and '<title>' in content:
            return fix_section_file_title_format(file_path)
        
        # Check if this is a <sections> root element without proper format
        if '<sections>' in content and '<entitySections' not in content:
            return fix_section_file_sections_root(file_path)
        
        # Check if file uses old format with <name>
        if '<name>' not in content:
            print(f"  SKIP (no <name> tag): {file_path.name}")
            return False
        
        # Parse the XML
        tree = ET.parse(file_path)
        root = tree.getroot()
        
        # Process each section
        for section in root.findall('.//section'):
            name_elem = section.find('name')
            if name_elem is not None:
                name_value = name_elem.text or ''
                section_name = convert_name_to_section_format(name_value)
                section_label = name_value
                
                # Create new elements
                section_name_elem = ET.Element('sectionName')
                section_name_elem.text = section_name
                
                section_label_elem = ET.Element('sectionLabel')
                section_label_elem.text = section_label
                
                # Find the index of <name> and replace it
                name_idx = list(section).index(name_elem)
                section.remove(name_elem)
                section.insert(name_idx, section_label_elem)
                section.insert(name_idx, section_name_elem)
        
        # Write the fixed content
        tree.write(file_path, encoding='unicode', xml_declaration=True)
        
        # Re-read and format the XML nicely
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Fix formatting (add proper indentation)
        content = content.replace('><', '>\n<')
        lines = content.split('\n')
        formatted_lines = []
        indent = 0
        for line in lines:
            line = line.strip()
            if not line:
                continue
            if line.startswith('</'):
                indent -= 1
            formatted_lines.append('    ' * indent + line)
            if line.startswith('<') and not line.startswith('</') and not line.startswith('<?') and not '/>' in line and not '</' in line:
                indent += 1
            if '/>' in line or ('</' in line and not line.startswith('</')):
                pass  # Don't change indent for self-closing or inline closing tags
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(formatted_lines))
        
        print(f"  FIXED: {file_path.name}")
        return True
    except Exception as e:
        print(f"  ERROR: {file_path.name} - {e}")
        return False

def fix_section_file_title_format(file_path):
    """Fix sections files that use <sections> root with <title> instead of <sectionName>/<sectionLabel>"""
    try:
        tree = ET.parse(file_path)
        root = tree.getroot()
        
        if root.tag != 'sections':
            return False
        
        # Derive entity type from filename
        entity_type = derive_entity_type_from_section_filename(file_path)
        
        lines = ['<?xml version="1.0" encoding="UTF-8"?>']
        lines.append(f'<entitySections type="{entity_type}">')
        
        display_order = 1
        for section in root.findall('section'):
            title_elem = section.find('title')
            fields_elem = section.find('fields')
            
            if title_elem is not None and title_elem.text:
                section_label = title_elem.text
                section_name = convert_name_to_section_format(section_label)
                
                lines.append('    <section>')
                lines.append(f'        <sectionName>{section_name}</sectionName>')
                lines.append(f'        <sectionLabel>{section_label}</sectionLabel>')
                lines.append(f'        <displayOrder>{display_order}</displayOrder>')
                lines.append('        <isCollapsible>true</isCollapsible>')
                lines.append('        <isDefaultExpanded>true</isDefaultExpanded>')
                
                if fields_elem is not None and fields_elem.text:
                    lines.append('        <fields>')
                    for field in fields_elem.text.split(','):
                        field = field.strip()
                        if field:
                            lines.append(f'            <field>{field}</field>')
                    lines.append('        </fields>')
                
                lines.append('    </section>')
                display_order += 1
        
        lines.append('</entitySections>')
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(lines))
        
        print(f"  FIXED (title format): {file_path.name} -> type={entity_type}")
        return True
    except Exception as e:
        print(f"  ERROR: {file_path.name} - {e}")
        return False

def fix_section_file_sections_root(file_path):
    """Fix sections files that use <sections> root but may have different element names"""
    try:
        tree = ET.parse(file_path)
        root = tree.getroot()
        
        if root.tag != 'sections':
            return False
        
        # Derive entity type from filename
        entity_type = derive_entity_type_from_section_filename(file_path)
        
        lines = ['<?xml version="1.0" encoding="UTF-8"?>']
        lines.append(f'<entitySections type="{entity_type}">')
        
        display_order = 1
        for section in root.findall('section'):
            # Try to find section name/label from various possible element names
            name_elem = section.find('name')
            title_elem = section.find('title')
            section_name_elem = section.find('sectionName')
            section_label_elem = section.find('sectionLabel')
            
            if section_name_elem is not None:
                section_name = section_name_elem.text or ''
                section_label = section_label_elem.text if section_label_elem is not None else section_name
            elif name_elem is not None and name_elem.text:
                section_label = name_elem.text
                section_name = convert_name_to_section_format(section_label)
            elif title_elem is not None and title_elem.text:
                section_label = title_elem.text
                section_name = convert_name_to_section_format(section_label)
            else:
                continue  # Skip if no name/title found
            
            # Get other attributes
            display_order_elem = section.find('displayOrder')
            sequence_elem = section.find('sequence')
            order = display_order
            if display_order_elem is not None and display_order_elem.text:
                try:
                    order = int(display_order_elem.text)
                except:
                    pass
            elif sequence_elem is not None and sequence_elem.text:
                try:
                    order = int(sequence_elem.text)
                except:
                    pass
            
            lines.append('    <section>')
            lines.append(f'        <sectionName>{section_name}</sectionName>')
            lines.append(f'        <sectionLabel>{section_label}</sectionLabel>')
            lines.append(f'        <displayOrder>{order}</displayOrder>')
            lines.append('        <isCollapsible>true</isCollapsible>')
            lines.append('        <isDefaultExpanded>true</isDefaultExpanded>')
            lines.append('    </section>')
            display_order += 1
        
        lines.append('</entitySections>')
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(lines))
        
        print(f"  FIXED (sections root): {file_path.name} -> type={entity_type}")
        return True
    except Exception as e:
        print(f"  ERROR: {file_path.name} - {e}")
        return False

def fix_field_file_entity_fields_attributes(file_path):
    """Fix field files that use attribute format like <field name="x" label="y"/>"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Check if file uses entity-fields with attributes
        if '<entity-fields' not in content:
            return False
        
        # Parse the entity type
        entity_type_match = re.search(r'<entity-fields\s+type="([^"]+)"', content)
        if not entity_type_match:
            print(f"  SKIP (no entity type): {file_path.name}")
            return False
        
        entity_type = entity_type_match.group(1)
        
        # Parse all fields
        fields = []
        for match in re.finditer(r'<field\s+([^>]+)/>', content):
            attrs_str = match.group(1)
            attrs = {}
            for attr_match in re.finditer(r'(\w+)="([^"]*)"', attrs_str):
                attrs[attr_match.group(1)] = attr_match.group(2)
            fields.append(attrs)
        
        # Also check for <field ...>...</field> format with options
        for match in re.finditer(r'<field\s+([^>]+)>(.*?)</field>', content, re.DOTALL):
            attrs_str = match.group(1)
            inner_content = match.group(2)
            attrs = {}
            for attr_match in re.finditer(r'(\w+)="([^"]*)"', attrs_str):
                attrs[attr_match.group(1)] = attr_match.group(2)
            # Parse options if present
            options = []
            for opt_match in re.finditer(r'<option\s+value="([^"]*)"\s+label="([^"]*)"\s*/>', inner_content):
                options.append({'value': opt_match.group(1), 'label': opt_match.group(2)})
            if options:
                attrs['options'] = options
            fields.append(attrs)
        
        if not fields:
            print(f"  SKIP (no fields found): {file_path.name}")
            return False
        
        # Generate new format
        lines = ['<?xml version="1.0" encoding="UTF-8"?>']
        lines.append(f'<entityFields type="{entity_type}">')
        
        display_order = 1
        for field in fields:
            field_name = field.get('name', '')
            field_label = field.get('label', field_name)
            field_type = field.get('type', 'text').upper()
            if field_type == 'TEXT':
                field_type = 'STRING'
            elif field_type == 'NUMBER':
                field_type = 'LONG'
            elif field_type == 'DATE':
                field_type = 'DATE'
            elif field_type == 'DATETIME':
                field_type = 'DATETIME'
            elif field_type == 'BOOLEAN':
                field_type = 'BOOLEAN'
            elif field_type == 'SELECT':
                field_type = 'ENUM'
            elif field_type == 'RELATION':
                field_type = 'REFERENCE'
            
            is_required = field.get('required', 'false').lower() == 'true'
            is_searchable = field.get('searchable', 'false').lower() == 'true'
            is_readonly = field.get('readOnly', 'false').lower() == 'true'
            is_hidden = field.get('hidden', 'false').lower() == 'true'
            
            lines.append('    <field>')
            lines.append(f'        <fieldName>{field_name}</fieldName>')
            lines.append(f'        <fieldLabel>{field_label}</fieldLabel>')
            lines.append(f'        <fieldType>{field_type}</fieldType>')
            lines.append(f'        <displayOrder>{display_order}</displayOrder>')
            lines.append(f'        <isRequired>{str(is_required).lower()}</isRequired>')
            lines.append(f'        <isSearchable>{str(is_searchable).lower()}</isSearchable>')
            lines.append(f'        <isSortable>true</isSortable>')
            lines.append(f'        <showInList>{str(not is_hidden).lower()}</showInList>')
            lines.append(f'        <showInForm>{str(not is_readonly).lower()}</showInForm>')
            lines.append('    </field>')
            display_order += 1
        
        lines.append('</entityFields>')
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(lines))
        
        print(f"  FIXED (attribute format): {file_path.name}")
        return True
    except Exception as e:
        print(f"  ERROR: {file_path.name} - {e}")
        return False

def fix_field_file_old_format(file_path):
    """Fix field files that use old format like <name>, <display_name>, etc."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Check if file uses old format with <fields> root and <name>/<display_name>
        if '<fields type=' not in content or '<display_name>' not in content:
            return False
        
        # Parse entity type
        entity_type_match = re.search(r'<fields\s+type="([^"]+)"', content)
        if not entity_type_match:
            print(f"  SKIP (no entity type): {file_path.name}")
            return False
        
        entity_type = entity_type_match.group(1)
        
        # Parse fields using regex
        fields = []
        for field_match in re.finditer(r'<field>(.*?)</field>', content, re.DOTALL):
            field_content = field_match.group(1)
            field = {}
            
            name_match = re.search(r'<name>([^<]*)</name>', field_content)
            if name_match:
                field['name'] = name_match.group(1)
            
            display_name_match = re.search(r'<display_name>([^<]*)</display_name>', field_content)
            if display_name_match:
                field['display_name'] = display_name_match.group(1)
            
            type_match = re.search(r'<type>([^<]*)</type>', field_content)
            if type_match:
                field['type'] = type_match.group(1)
            
            required_match = re.search(r'<required>([^<]*)</required>', field_content)
            if required_match:
                field['required'] = required_match.group(1).lower() == 'true'
            
            grid_visible_match = re.search(r'<grid_visible>([^<]*)</grid_visible>', field_content)
            if grid_visible_match:
                field['grid_visible'] = grid_visible_match.group(1).lower() == 'true'
            
            fields.append(field)
        
        if not fields:
            print(f"  SKIP (no fields found): {file_path.name}")
            return False
        
        # Generate new format
        lines = ['<?xml version="1.0" encoding="UTF-8"?>']
        lines.append(f'<entityFields type="{entity_type}">')
        
        display_order = 1
        for field in fields:
            field_name = field.get('name', '')
            field_label = field.get('display_name', field_name)
            field_type = field.get('type', 'text').upper()
            if field_type == 'TEXT':
                field_type = 'STRING'
            elif field_type == 'NUMBER':
                field_type = 'DECIMAL'
            elif field_type == 'DATE':
                field_type = 'DATE'
            elif field_type == 'DATETIME':
                field_type = 'DATETIME'
            elif field_type == 'BOOLEAN':
                field_type = 'BOOLEAN'
            elif field_type == 'SELECT':
                field_type = 'ENUM'
            elif field_type == 'TEXTAREA':
                field_type = 'TEXT'
            elif field_type == 'RELATION':
                field_type = 'REFERENCE'
            
            is_required = field.get('required', False)
            is_visible = field.get('grid_visible', True)
            
            lines.append('    <field>')
            lines.append(f'        <fieldName>{field_name}</fieldName>')
            lines.append(f'        <fieldLabel>{field_label}</fieldLabel>')
            lines.append(f'        <fieldType>{field_type}</fieldType>')
            lines.append(f'        <displayOrder>{display_order}</displayOrder>')
            lines.append(f'        <isRequired>{str(is_required).lower()}</isRequired>')
            lines.append(f'        <isSearchable>true</isSearchable>')
            lines.append(f'        <isSortable>true</isSortable>')
            lines.append(f'        <showInList>{str(is_visible).lower()}</showInList>')
            lines.append(f'        <showInForm>true</showInForm>')
            lines.append('    </field>')
            display_order += 1
        
        lines.append('</entityFields>')
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(lines))
        
        print(f"  FIXED (old format): {file_path.name}")
        return True
    except Exception as e:
        print(f"  ERROR: {file_path.name} - {e}")
        return False

def main():
    print("=" * 60)
    print("ERP XML Format Fixer")
    print("=" * 60)
    
    # Find all section and field files
    section_files = list(DATA_DIR.rglob('*_sections.xml'))
    field_files = list(DATA_DIR.rglob('*_fields.xml'))
    
    print(f"\nFound {len(section_files)} section files and {len(field_files)} field files")
    
    # Fix section files
    print("\n--- Fixing Section Files ---")
    sections_fixed = 0
    for file_path in sorted(section_files):
        if fix_section_file(file_path):
            sections_fixed += 1
    
    # Fix field files with <fields> root element (need to add entity type and fix format)
    print("\n--- Fixing Field Files (fields root element) ---")
    fields_root_fixed = 0
    for file_path in sorted(field_files):
        if fix_field_file_fields_root(file_path):
            fields_root_fixed += 1
    
    # Fix field files with attribute format (entity-fields)
    print("\n--- Fixing Field Files (attribute format) ---")
    fields_attr_fixed = 0
    for file_path in sorted(field_files):
        if fix_field_file_entity_fields_attributes(file_path):
            fields_attr_fixed += 1
    
    # Fix field files with old format (<name>/<display_name>)
    print("\n--- Fixing Field Files (old format) ---")
    fields_old_fixed = 0
    for file_path in sorted(field_files):
        if fix_field_file_old_format(file_path):
            fields_old_fixed += 1
    
    print("\n" + "=" * 60)
    print(f"Summary:")
    print(f"  Section files fixed: {sections_fixed}")
    print(f"  Field files fixed (fields root): {fields_root_fixed}")
    print(f"  Field files fixed (attribute format): {fields_attr_fixed}")
    print(f"  Field files fixed (old format): {fields_old_fixed}")
    print("=" * 60)

def derive_entity_type_from_filename(file_path):
    """Derive entity type from filename like 'alumni_contribution_fields.xml' -> 'ALUMNI_CONTRIBUTION'"""
    filename = file_path.stem  # e.g., 'alumni_contribution_fields'
    # Remove '_fields' suffix
    entity_name = filename.replace('_fields', '')
    # Convert to uppercase
    return entity_name.upper()

def fix_field_file_fields_root(file_path):
    """Fix field files that have <fields> root element - add entity type and fix format"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Check if file uses <fields> root element (old format)
        if '<fields>' not in content or '<entityFields' in content:
            return False
        
        # Parse the XML
        tree = ET.parse(file_path)
        root = tree.getroot()
        
        if root.tag != 'fields':
            return False
        
        # Derive entity type from filename
        entity_type = derive_entity_type_from_filename(file_path)
        
        # Build new XML content
        lines = ['<?xml version="1.0" encoding="UTF-8"?>']
        lines.append(f'<entityFields type="{entity_type}">')
        
        display_order = 1
        for field in root.findall('field'):
            # Get old format values
            name_elem = field.find('name')
            label_elem = field.find('label')
            type_elem = field.find('type')
            required_elem = field.find('required')
            show_in_list_elem = field.find('show_in_list')
            sequence_elem = field.find('sequence')
            
            field_name = name_elem.text if name_elem is not None and name_elem.text else ''
            field_label = label_elem.text if label_elem is not None and label_elem.text else field_name
            field_type_raw = type_elem.text if type_elem is not None and type_elem.text else 'text'
            
            # Convert field type
            field_type = field_type_raw.upper()
            type_mapping = {
                'TEXT': 'STRING',
                'NUMBER': 'LONG',
                'STRING': 'STRING',
                'SELECT': 'ENUM',
                'BOOLEAN': 'BOOLEAN',
                'DATE': 'DATE',
                'DATETIME': 'DATETIME',
                'TEXTAREA': 'TEXT',
            }
            field_type = type_mapping.get(field_type, field_type)
            
            is_required = required_elem.text.lower() == 'true' if required_elem is not None and required_elem.text else False
            show_in_list = show_in_list_elem.text.lower() == 'true' if show_in_list_elem is not None and show_in_list_elem.text else True
            
            # Use sequence if available, otherwise use display_order counter
            if sequence_elem is not None and sequence_elem.text:
                try:
                    order = int(sequence_elem.text)
                except:
                    order = display_order
            else:
                order = display_order
            
            lines.append('    <field>')
            lines.append(f'        <fieldName>{field_name}</fieldName>')
            lines.append(f'        <fieldLabel>{field_label}</fieldLabel>')
            lines.append(f'        <fieldType>{field_type}</fieldType>')
            lines.append(f'        <displayOrder>{order}</displayOrder>')
            lines.append(f'        <isRequired>{str(is_required).lower()}</isRequired>')
            lines.append(f'        <isSearchable>true</isSearchable>')
            lines.append(f'        <isSortable>true</isSortable>')
            lines.append(f'        <showInList>{str(show_in_list).lower()}</showInList>')
            lines.append(f'        <showInForm>true</showInForm>')
            lines.append('    </field>')
            display_order += 1
        
        lines.append('</entityFields>')
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(lines))
        
        print(f"  FIXED (fields root): {file_path.name} -> type={entity_type}")
        return True
    except Exception as e:
        print(f"  ERROR: {file_path.name} - {e}")
        return False

if __name__ == "__main__":
    main()
