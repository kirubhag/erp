#!/usr/bin/env python3
"""
Script to generate comprehensive XML files for:
1. erp-section-field-relations.xml - Maps all fields to their sections
2. erp-layout-section-relations.xml - Maps all sections to System layouts
"""

import os
import xml.etree.ElementTree as ET
from collections import defaultdict
import glob

DATA_DIR = "../src/main/resources/data"

def get_entity_type_from_file(filepath):
    """Extract entity type from the XML file's root element 'type' attribute"""
    try:
        tree = ET.parse(filepath)
        root = tree.getroot()
        return root.get('type')
    except Exception as e:
        print(f"Error reading {filepath}: {e}")
        return None

def get_fields_with_sections(filepath):
    """Extract all fields and their sectionName from a fields XML file"""
    fields = []
    try:
        tree = ET.parse(filepath)
        root = tree.getroot()
        entity_type = root.get('type')
        
        for field in root.findall('.//field'):
            field_name = field.findtext('fieldName')
            section_name = field.findtext('sectionName')
            display_order = field.findtext('displayOrder', '1')
            
            if field_name:
                fields.append({
                    'entityType': entity_type,
                    'fieldName': field_name,
                    'sectionName': section_name,
                    'displayOrder': int(display_order) if display_order else 1
                })
    except Exception as e:
        print(f"Error parsing {filepath}: {e}")
    return fields

def get_sections(filepath):
    """Extract all sections from a sections XML file"""
    sections = []
    try:
        tree = ET.parse(filepath)
        root = tree.getroot()
        entity_type = root.get('type')
        
        for section in root.findall('.//section'):
            section_name = section.findtext('sectionName')
            display_order = section.findtext('displayOrder', '1')
            
            if section_name:
                sections.append({
                    'entityType': entity_type,
                    'sectionName': section_name,
                    'displayOrder': int(display_order) if display_order else 1
                })
    except Exception as e:
        print(f"Error parsing {filepath}: {e}")
    return sections

def generate_section_field_relations_xml():
    """Generate the comprehensive erp-section-field-relations.xml"""
    all_relations = []
    fields_without_sections = []
    entity_first_sections = {}  # Track first section per entity for default assignment
    
    # First, collect all sections to know what sections exist per entity
    for sections_file in glob.glob(os.path.join(DATA_DIR, "**/*_sections.xml"), recursive=True):
        sections = get_sections(sections_file)
        for section in sections:
            entity_type = section['entityType']
            if entity_type not in entity_first_sections:
                entity_first_sections[entity_type] = section['sectionName']
            elif section['displayOrder'] < entity_first_sections.get(entity_type + '_order', 999):
                entity_first_sections[entity_type] = section['sectionName']
                entity_first_sections[entity_type + '_order'] = section['displayOrder']
    
    # Now process all fields
    for fields_file in glob.glob(os.path.join(DATA_DIR, "**/*_fields.xml"), recursive=True):
        fields = get_fields_with_sections(fields_file)
        for field in fields:
            entity_type = field['entityType']
            field_name = field['fieldName']
            section_name = field['sectionName']
            display_order = field['displayOrder']
            
            # Skip system fields (id, createdTime, modifiedTime, etc.)
            if field_name in ['id', 'createdTime', 'modifiedTime', 'createdBy', 'modifiedBy', 'isActive', 'version']:
                continue
                
            if section_name:
                all_relations.append({
                    'entityType': entity_type,
                    'sectionName': section_name,
                    'fieldName': field_name,
                    'fieldOrder': display_order
                })
            else:
                # Assign to first section if no section specified
                default_section = entity_first_sections.get(entity_type)
                if default_section:
                    all_relations.append({
                        'entityType': entity_type,
                        'sectionName': default_section,
                        'fieldName': field_name,
                        'fieldOrder': display_order
                    })
                    fields_without_sections.append(f"{entity_type}/{field_name} -> {default_section}")
    
    # Sort relations by entity type, section name, then field order
    all_relations.sort(key=lambda x: (x['entityType'], x['sectionName'], x['fieldOrder']))
    
    # Generate XML
    xml_lines = [
        '<?xml version="1.0" encoding="UTF-8"?>',
        '<!--',
        '  ERP Section-Field Relations',
        '  Maps fields to their sections for form layout organization',
        '  Auto-generated from *_fields.xml files',
        '-->',
        '<sectionFieldRelations>'
    ]
    
    current_entity = None
    for rel in all_relations:
        if rel['entityType'] != current_entity:
            if current_entity:
                xml_lines.append('')
            xml_lines.append(f'    <!-- {rel["entityType"]} -->')
            current_entity = rel['entityType']
        
        xml_lines.append('    <relation>')
        xml_lines.append(f'        <entityType>{rel["entityType"]}</entityType>')
        xml_lines.append(f'        <sectionName>{rel["sectionName"]}</sectionName>')
        xml_lines.append(f'        <fieldName>{rel["fieldName"]}</fieldName>')
        xml_lines.append(f'        <fieldOrder>{rel["fieldOrder"]}</fieldOrder>')
        xml_lines.append('    </relation>')
    
    xml_lines.append('</sectionFieldRelations>')
    
    output_path = os.path.join(DATA_DIR, "erp-section-field-relations.xml")
    with open(output_path, 'w') as f:
        f.write('\n'.join(xml_lines))
    
    print(f"Generated {output_path} with {len(all_relations)} relations")
    if fields_without_sections:
        print(f"  - {len(fields_without_sections)} fields assigned to default sections")
    
    return len(all_relations)

def generate_layout_section_relations_xml():
    """Generate the comprehensive erp-layout-section-relations.xml"""
    all_relations = []
    
    # Process all sections files
    for sections_file in glob.glob(os.path.join(DATA_DIR, "**/*_sections.xml"), recursive=True):
        sections = get_sections(sections_file)
        for section in sections:
            all_relations.append({
                'entityType': section['entityType'],
                'layoutName': 'System',  # Default layout name
                'sectionName': section['sectionName'],
                'sectionOrder': section['displayOrder']
            })
    
    # Sort by entity type and section order
    all_relations.sort(key=lambda x: (x['entityType'], x['sectionOrder']))
    
    # Generate XML
    xml_lines = [
        '<?xml version="1.0" encoding="UTF-8"?>',
        '<!--',
        '  ERP Layout-Section Relations',
        '  Maps sections to their layouts (System layout by default)',
        '  Auto-generated from *_sections.xml files',
        '-->',
        '<layoutSectionRelations>'
    ]
    
    current_entity = None
    for rel in all_relations:
        if rel['entityType'] != current_entity:
            if current_entity:
                xml_lines.append('')
            xml_lines.append(f'    <!-- {rel["entityType"]} -->')
            current_entity = rel['entityType']
        
        xml_lines.append('    <relation>')
        xml_lines.append(f'        <entityType>{rel["entityType"]}</entityType>')
        xml_lines.append(f'        <layoutName>{rel["layoutName"]}</layoutName>')
        xml_lines.append(f'        <sectionName>{rel["sectionName"]}</sectionName>')
        xml_lines.append(f'        <sectionOrder>{rel["sectionOrder"]}</sectionOrder>')
        xml_lines.append('    </relation>')
    
    xml_lines.append('</layoutSectionRelations>')
    
    output_path = os.path.join(DATA_DIR, "erp-layout-section-relations.xml")
    with open(output_path, 'w') as f:
        f.write('\n'.join(xml_lines))
    
    print(f"Generated {output_path} with {len(all_relations)} relations")
    
    return len(all_relations)

if __name__ == "__main__":
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    
    print("=" * 60)
    print("Generating ERP Relation XML Files")
    print("=" * 60)
    
    section_field_count = generate_section_field_relations_xml()
    layout_section_count = generate_layout_section_relations_xml()
    
    print("=" * 60)
    print(f"Total: {section_field_count} section-field relations")
    print(f"Total: {layout_section_count} layout-section relations")
    print("=" * 60)
