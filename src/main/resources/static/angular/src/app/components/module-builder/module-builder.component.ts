import { Component, OnInit } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FieldService } from '../../services/field.service';
import { SectionService } from '../../services/section.service';
import { ErpField, FieldsGroupedBySection } from '../../models/erp-field.model';

export interface LayoutField {
  id: string;
  label: string;
  type: string;
  required: boolean;
  prefix?: string;
}

export interface LayoutSection {
  id: string;
  name: string;
  rows: LayoutField[][];
  warning?: string;
}

export interface FieldType {
  id: string;
  label: string;
  icon: string;
  type: string;
}

@Component({
  selector: 'app-module-builder',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, TitleCasePipe],
  templateUrl: './module-builder.component.html',
  styleUrls: ['./module-builder.component.css']
})
export class ModuleBuilderComponent implements OnInit {
  moduleId: string | null = null;
  activeTab = 'layouts';
  currentModule = 'STUDENT'; // Changed to EntityType format
  selectedLayout = 'Standard';
  activeSection: string | null = null;
  activeField: string | null = null;

  // Loading and error states
  isLoadingFields = false;
  fieldsError: string = '';

  // Drag and drop state
  draggedItem: any = null;
  draggedItemType: 'new' | 'unused' | null = null;

  newFieldTypes: FieldType[] = [
    { id: 'singleLine', label: 'Single Li...', icon: 'fas fa-minus', type: 'Single Line' },
    { id: 'multiLine', label: 'Multi-Line', icon: 'fas fa-align-left', type: 'Multi-Line' },
    { id: 'email', label: 'Email', icon: 'fas fa-envelope', type: 'Email' },
    { id: 'phone', label: 'Phone', icon: 'fas fa-phone', type: 'Phone' },
    { id: 'pickList', label: 'Pick List', icon: 'fas fa-list', type: 'Pick List' },
    { id: 'multiSelect', label: 'Multi-Se...', icon: 'fas fa-list-ul', type: 'Multi-Select' },
    { id: 'date', label: 'Date', icon: 'fas fa-calendar', type: 'Date' },
    { id: 'dateTime', label: 'Date/Time', icon: 'far fa-clock', type: 'Date/Time' },
    { id: 'number', label: 'Number', icon: 'fas fa-hashtag', type: 'Number' },
    { id: 'autoNumber', label: 'Auto-Nu...', icon: 'fas fa-sort-numeric-up', type: 'Auto-Number' },
    { id: 'currency', label: 'Currency', icon: 'fas fa-dollar-sign', type: 'Currency' },
    { id: 'decimal', label: 'Decimal', icon: 'fas fa-circle', type: 'Decimal' },
    { id: 'percent', label: 'Percent', icon: 'fas fa-percent', type: 'Percent' },
    { id: 'longInt', label: 'Long Int...', icon: 'fas fa-text-width', type: 'Long Integer' },
    { id: 'checkbox', label: 'Checkbox', icon: 'far fa-check-square', type: 'Checkbox' },
    { id: 'terms', label: 'Terms o...', icon: 'far fa-file-alt', type: 'Terms of Service' },
    { id: 'url', label: 'URL', icon: 'fas fa-link', type: 'URL' },
    { id: 'formula', label: 'Formula', icon: 'fas fa-function', type: 'Formula' },
    { id: 'lookup', label: 'Lookup', icon: 'fas fa-search', type: 'Lookup' },
    { id: 'tabular', label: 'Tabular', icon: 'fas fa-table', type: 'Tabular' },
    { id: 'user', label: 'User', icon: 'fas fa-user', type: 'User' }
  ];

  unusedFields: FieldType[] = [
    { id: 'sample', label: 'Sample Field', icon: 'fas fa-minus', type: 'Single Line' }
  ];

  sections: LayoutSection[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fieldService: FieldService,
    private sectionService: SectionService
  ) { }

  ngOnInit() {
    // Load fields for default module
    this.loadFieldsForModule(this.currentModule);

    this.route.paramMap.subscribe(params => {
      this.moduleId = params.get('id');
      if (this.moduleId && this.moduleId !== 'new') {
        this.loadModuleData(this.moduleId);
      }
    });
  }

  loadModuleData(id: string) {
    // Load module-specific data if needed
    console.log('Loading module:', id);
  }

  /**
   * Load fields from backend for the specified module/entity type
   */
  loadFieldsForModule(entityType: string) {
    this.isLoadingFields = true;
    this.fieldsError = '';

    this.fieldService.getFieldsGroupedBySection(entityType).subscribe({
      next: (groupedFields: FieldsGroupedBySection) => {
        this.sections = this.transformFieldsToSections(groupedFields);
        this.isLoadingFields = false;
      },
      error: (error) => {
        console.error('Error loading fields:', error);
        this.fieldsError = 'Failed to load fields. Please try again.';
        this.isLoadingFields = false;
        // Fallback to empty sections
        this.sections = [];
      }
    });
  }

  /**
   * Transform grouped ErpField data to LayoutSection format
   * Fields are now grouped by section labels instead of categories
   */
  transformFieldsToSections(groupedFields: FieldsGroupedBySection): LayoutSection[] {
    const sections: LayoutSection[] = [];

    // Convert each category to a section
    Object.keys(groupedFields).forEach(category => {
      const fields = groupedFields[category];

      // Group fields into rows (2 fields per row)
      const rows: LayoutField[][] = [];
      for (let i = 0; i < fields.length; i += 2) {
        const row: LayoutField[] = [];

        // Add first field in row
        row.push(this.convertErpFieldToLayoutField(fields[i]));

        // Add second field if exists
        if (i + 1 < fields.length) {
          row.push(this.convertErpFieldToLayoutField(fields[i + 1]));
        }

        rows.push(row);
      }

      sections.push({
        id: category.toLowerCase().replace(/\s+/g, '_'),
        name: category,
        rows: rows
      });
    });

    return sections;
  }

  /**
   * Convert ErpField to LayoutField format
   */
  convertErpFieldToLayoutField(field: ErpField): LayoutField {
    return {
      id: field.fieldName,
      label: field.fieldLabel,
      type: this.getDisplayFieldType(field.fieldType),
      required: field.isRequired
    };
  }

  /**
   * Get display-friendly field type name
   */
  getDisplayFieldType(fieldType: string): string {
    const typeMap: { [key: string]: string } = {
      'TEXT': 'Single Line',
      'TEXTAREA': 'Multi-Line',
      'EMAIL': 'Email',
      'PHONE': 'Phone',
      'PICKLIST': 'Pick List',
      'MULTISELECT': 'Multi-Select',
      'ENUM': 'Pick List',
      'DATE': 'Date',
      'DATETIME': 'Date/Time',
      'NUMBER': 'Number',
      'AUTONUMBER': 'Auto-Number',
      'CURRENCY': 'Currency',
      'DECIMAL': 'Decimal',
      'PERCENT': 'Percent',
      'BOOLEAN': 'Checkbox',
      'URL': 'URL',
      'LOOKUP': 'Lookup',
      'USER': 'User'
    };

    return typeMap[fieldType] || fieldType;
  }

  selectModule(module: string) {
    this.currentModule = module;
    // Load fields for the newly selected module
    this.loadFieldsForModule(module);
  }

  selectLayout(layout: string) {
    this.selectedLayout = layout;
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  setActiveSection(sectionId: string) {
    this.activeSection = sectionId;
    this.activeField = null;
  }

  updateSectionName(section: LayoutSection, event: Event) {
    const target = event.target as HTMLElement;
    const newName = target.textContent?.trim();
    if (newName && newName !== section.name) {
      section.name = newName;
      console.log('Section name updated:', section.name);
    }
  }

  setActiveField(fieldId: string, event: Event) {
    event.stopPropagation();
    this.activeField = fieldId;

    // Find and set active section
    for (const section of this.sections) {
      for (const row of section.rows) {
        if (row.some(field => field.id === fieldId)) {
          this.activeSection = section.id;
          break;
        }
      }
    }
  }

  addNewField(fieldType: FieldType) {
    console.log('Adding new field:', fieldType);
    // Add logic to add field to the active section
  }

  addNewSection() {
    const newSection: LayoutSection = {
      id: 'section_' + Date.now(),
      name: 'NEW SECTION',
      rows: []
    };
    this.sections.push(newSection);
  }

  editField(fieldId: string, event: Event) {
    event.stopPropagation();
    console.log('Editing field:', fieldId);
    // TODO: Open field edit modal/panel with field configuration options
    alert(`Field settings for: ${fieldId}\n\nThis will open a configuration panel to edit field properties like:\n- Display label\n- Required/Optional\n- Validation rules\n- Default values\n- Help text`);
  }

  deleteField(fieldId: string, event: Event) {
    event.stopPropagation();
    console.log('Deleting field:', fieldId);

    // Remove field from sections
    this.sections = this.sections.map(section => ({
      ...section,
      rows: section.rows.map(row => row.filter(field => field.id !== fieldId)).filter(row => row.length > 0)
    }));

    this.activeField = null;
  }

  /**
   * Drag and Drop Handlers
   */
  onDragStart(event: DragEvent, item: any, type: 'new' | 'unused') {
    this.draggedItem = item;
    this.draggedItemType = type;
    
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'copy';
      event.dataTransfer.setData('text/plain', JSON.stringify(item));
    }
    
    console.log('Drag started:', item, type);
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = 'copy';
    }
  }

  onDrop(event: DragEvent, section: LayoutSection) {
    event.preventDefault();
    event.stopPropagation();
    
    if (!this.draggedItem) {
      return;
    }

    console.log('Dropped item on section:', section.name);
    
    // Create a new field from the dragged item
    let newField: LayoutField;
    
    if (this.draggedItemType === 'new') {
      // Create a new field with unique ID
      const timestamp = Date.now();
      newField = {
        id: `${this.draggedItem.id}_${timestamp}`,
        label: `New ${this.draggedItem.label}`,
        type: this.draggedItem.type,
        required: false
      };
    } else {
      // Reuse unused field
      newField = this.draggedItem;
    }
    
    // Add field to the section (create new row or add to last row)
    const lastRow = section.rows[section.rows.length - 1];
    
    if (!lastRow || lastRow.length >= 2) {
      // Create new row if last row doesn't exist or is full
      section.rows.push([newField]);
    } else {
      // Add to existing row
      lastRow.push(newField);
    }
    
    console.log('Field added to section:', newField);
    
    // Clear drag state
    this.draggedItem = null;
    this.draggedItemType = null;
  }

  saveLayout() {
    console.log('=== SAVE LAYOUT CALLED ===');
    console.log('Current Module:', this.currentModule);
    console.log('Sections count:', this.sections?.length || 0);
    console.log('Sections data:', this.sections);
    
    if (!this.sections || this.sections.length === 0) {
      alert('No sections to save. Please add some fields first.');
      return;
    }
    
    const layoutData = {
      entityType: this.currentModule,
      sections: this.sections.map((section, index) => ({
        sectionName: section.id,
        sectionLabel: section.name,
        displayOrder: index + 1,
        fields: section.rows.flatMap((row, rowIndex) => 
          row.map((field, colIndex) => ({
            fieldName: field.id,
            rowPosition: rowIndex,
            columnPosition: colIndex
          }))
        )
      }))
    };
    
    console.log('Layout data prepared:', JSON.stringify(layoutData, null, 2));
    
    // Call backend API to save layout
    this.sectionService.saveModuleLayout(this.currentModule, layoutData).subscribe({
      next: (response) => {
        console.log('Save response:', response);
        
        const totalFields = this.sections.reduce((sum, s) => sum + s.rows.flat().length, 0);
        let message = `Layout saved successfully!\n\n`;
        message += `Saved ${response.sectionsUpdated || this.sections.length} sections with ${response.fieldsUpdated || totalFields} fields.`;
        
        if (response.errors && response.errors.length > 0) {
          message += `\n\nWarnings:\n` + response.errors.join('\n');
        }
        
        alert(message);
      },
      error: (error) => {
        console.error('Error saving layout:', error);
        alert(`Error saving layout: ${error.error?.message || error.message || 'Unknown error'}`);
      }
    });
  }

  cancel() {
    this.router.navigate(['/setup/modules-fields']);
  }
}
