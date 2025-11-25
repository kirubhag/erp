import { Component, OnInit } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';

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
  currentModule = 'Candidates';
  selectedLayout = 'Standard';
  activeSection: string | null = null;
  activeField: string | null = null;
  
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
    private router: Router
  ) {}

  ngOnInit() {
    this.loadLayoutData();
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

  loadLayoutData() {
    this.sections = [
      {
        id: 'userFields',
        name: 'USER FIELDS',
        rows: [
          [
            { id: 'user1', label: 'User 1', type: 'User', required: false },
            { id: 'user2', label: 'User 2', type: 'User', required: false }
          ],
          [
            { id: 'users', label: 'Users', type: 'Multi User', required: false }
          ]
        ]
      },
      {
        id: 'basicInfo',
        name: 'BASIC INFO',
        rows: [
          [
            { id: 'candidateId', label: 'Candidates ID', type: 'Auto-Number', required: false },
            { id: 'firstName', label: 'First Name', type: 'Single Line', required: false, prefix: 'Mr.' }
          ],
          [
            { id: 'lastName', label: 'Last Name', type: 'Single Line', required: true },
            { id: 'email', label: 'Email', type: 'Email (Unique)', required: false }
          ],
          [
            { id: 'secondaryEmail', label: 'Secondary Email', type: 'Email', required: false },
            { id: 'phone', label: 'Phone', type: 'Phone', required: false }
          ],
          [
            { id: 'mobile', label: 'Mobile', type: 'Phone', required: false },
            { id: 'fax', label: 'Fax', type: 'Phone', required: false }
          ],
          [
            { id: 'website', label: 'Website', type: 'URL', required: false },
            { id: 'dontList', label: 'Don\'t List in Client Portal', type: 'Checkbox', required: false }
          ]
        ],
        warning: 'This field will be hidden during creation/editing.'
      }
    ];
  }

  selectModule(module: string) {
    this.currentModule = module;
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
    // Open field edit modal/panel
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

  saveLayout() {
    console.log('Saving layout:', this.sections);
    alert('Layout saved successfully!');
  }

  cancel() {
    this.router.navigate(['/setup/modules-fields']);
  }
}
