import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

export interface Field {
  id: string;
  name: string;
  displayName: string;
  type: string;
  required: boolean;
  visible: boolean;
}

@Component({
  selector: 'app-module-builder',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, SettingsSidebarComponent],
  templateUrl: './module-builder.component.html',
  styleUrls: ['./module-builder.component.css']
})
export class ModuleBuilderComponent implements OnInit {
  moduleForm: FormGroup;
  moduleId: string | null = null;
  activeTab = 'layout';
  fields: Field[] = [];
  
  availableFieldTypes = [
    { id: 'text', name: 'Text' },
    { id: 'email', name: 'Email' },
    { id: 'phone', name: 'Phone' },
    { id: 'number', name: 'Number' },
    { id: 'date', name: 'Date' },
    { id: 'dropdown', name: 'Dropdown' },
    { id: 'checkbox', name: 'Checkbox' },
    { id: 'textarea', name: 'Text Area' },
    { id: 'file', name: 'File Upload' }
  ];

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.moduleForm = this.formBuilder.group({
      name: ['', Validators.required],
      displayName: ['', Validators.required],
      description: [''],
      singularLabel: [''],
      pluralLabel: [''],
      icon: ['']
    });
  }

  ngOnInit() {
    this.loadFields();
    this.route.paramMap.subscribe(params => {
      this.moduleId = params.get('id');
      if (this.moduleId && this.moduleId !== 'new') {
        this.loadModuleData(this.moduleId);
      }
    });
  }

  loadModuleData(id: string) {
    const mockModules: any = {
      '1': { 
        name: 'Candidates',
        displayName: 'Candidates',
        description: 'Manage candidate profiles',
        singularLabel: 'Candidate',
        pluralLabel: 'Candidates',
        icon: 'fas fa-user-tie'
      }
    };

    if (mockModules[id]) {
      this.moduleForm.patchValue(mockModules[id]);
    }
  }

  loadFields() {
    this.fields = [
      { id: 'first_name', name: 'firstName', displayName: 'First Name', type: 'text', required: true, visible: true },
      { id: 'last_name', name: 'lastName', displayName: 'Last Name', type: 'text', required: true, visible: true },
      { id: 'email', name: 'email', displayName: 'Email', type: 'email', required: false, visible: true },
      { id: 'phone', name: 'phone', displayName: 'Phone', type: 'phone', required: false, visible: true },
      { id: 'experience', name: 'experience', displayName: 'Experience', type: 'number', required: false, visible: true }
    ];
  }

  addField() {
    const newField: Field = {
      id: 'field_' + Date.now(),
      name: '',
      displayName: '',
      type: 'text',
      required: false,
      visible: true
    };
    this.fields.push(newField);
  }

  removeField(field: Field) {
    this.fields = this.fields.filter(f => f.id !== field.id);
  }

  toggleFieldVisibility(field: Field) {
    field.visible = !field.visible;
  }

  toggleFieldRequired(field: Field) {
    field.required = !field.required;
  }

  saveModule() {
    if (this.moduleForm.valid) {
      console.log('Saving module:', this.moduleForm.value);
      console.log('Fields:', this.fields);
      alert('Module saved successfully!');
      this.router.navigate(['/setup/modules-fields']);
    }
  }

  cancel() {
    this.router.navigate(['/setup/modules-fields']);
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }
}
