import { Component, OnInit, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { SettingsSidebarComponent } from '../../settings-sidebar/settings-sidebar.component';

interface ErpEntity {
  id: number;
  singularName: string;
  pluralName: string;
  tableName: string;
}

interface ErpField {
  id: number;
  fieldName: string;
  fieldLabel: string;
  uiType: number;
}

interface ExportRequest {
  entityId: number;
  fieldIds: number[];
  excludedFieldIds?: number[];
  format: string;
  excludeFields: boolean;
  searchQuery?: string;
}

@Component({
  selector: 'app-export-data',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, SettingsSidebarComponent],
  templateUrl: './export-data.component.html',
  styleUrls: ['./export-data.component.css']
})
export class ExportDataComponent implements OnInit {
  modules: ErpEntity[] = [];
  selectedModule: ErpEntity | null = null;
  allFields: ErpField[] = [];
  selectedFields: ErpField[] = [];
  excludedFields: ErpField[] = [];
  excludeFields = false;
  fieldSelectionMode = 'all'; // 'all' or 'selected'
  fileFormat = 'csv';
  charset = 'UTF-8';
  searchTerm = '';
  excludeSearchTerm = '';
  showExcludeDropdown = false;
  loading = false;
  errorMessage = '';

  constructor(private http: HttpClient, private elementRef: ElementRef) { }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    const excludeDropdown = this.elementRef.nativeElement.querySelector('.multiselect-dropdown');
    if (excludeDropdown && !excludeDropdown.contains(target)) {
      this.showExcludeDropdown = false;
    }
  }

  ngOnInit(): void {
    this.loadModules();
  }

  loadModules(): void {
    this.http.get<ErpEntity[]>('/api/module/list').subscribe({
      next: (data) => {
        this.modules = data;
      },
      error: (err) => {
        console.error('Error loading modules:', err);
        this.errorMessage = 'Failed to load modules';
      }
    });
  }

  onModuleChange(): void {
    if (this.selectedModule) {
      this.loadFields(this.selectedModule.singularName.toLowerCase());
    }
  }

  loadFields(entityType: string): void {
    this.http.get<ErpField[]>(`/api/fields/${entityType}`).subscribe({
      next: (data) => {
        this.allFields = data;
        this.selectedFields = [];
      },
      error: (err) => {
        console.error('Error loading fields:', err);
        this.errorMessage = 'Failed to load fields';
      }
    });
  }

  toggleFieldSelection(field: ErpField): void {
    const index = this.selectedFields.findIndex(f => f.id === field.id);
    if (index > -1) {
      this.selectedFields.splice(index, 1);
    } else {
      this.selectedFields.push(field);
    }
  }

  isFieldSelected(field: ErpField): boolean {
    return this.selectedFields.some(f => f.id === field.id);
  }

  removeField(field: ErpField): void {
    const index = this.selectedFields.findIndex(f => f.id === field.id);
    if (index > -1) {
      this.selectedFields.splice(index, 1);
    }
  }

  exportData(): void {
    if (!this.selectedModule) {
      this.errorMessage = 'Please select a module';
      return;
    }

    // Only require selected fields when in 'selected' mode
    if (this.fieldSelectionMode === 'selected' && this.selectedFields.length === 0) {
      this.errorMessage = 'Please select at least one field';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // When 'all' mode, send empty fieldIds to indicate all fields
    const fieldIds = this.fieldSelectionMode === 'all' ? [] : this.selectedFields.map(f => f.id);
    const excludedFieldIds = this.excludeFields ? this.excludedFields.map(f => f.id) : [];

    const request: ExportRequest = {
      entityId: this.selectedModule.id,
      fieldIds: fieldIds,
      format: this.fileFormat,
      excludeFields: this.excludeFields,
      excludedFieldIds: excludedFieldIds,
      searchQuery: this.searchTerm
    };

    this.http.post('/api/export', request, {
      responseType: 'blob',
      observe: 'response'
    }).subscribe({
      next: (response) => {
        const blob = response.body;
        if (blob) {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `export.${this.fileFormat}`;
          link.click();
          window.URL.revokeObjectURL(url);
        }
        this.loading = false;
        this.resetForm();
      },
      error: (err) => {
        console.error('Export error:', err);
        this.errorMessage = 'Export failed. Please try again.';
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.selectedModule = null;
    this.selectedFields = [];
    this.excludedFields = [];
    this.searchTerm = '';
    this.excludeSearchTerm = '';
    this.excludeFields = false;
    this.fieldSelectionMode = 'all';
    this.showExcludeDropdown = false;
    this.errorMessage = '';
  }

  resetForm(): void {
    this.selectedModule = null;
    this.allFields = [];
    this.selectedFields = [];
    this.excludedFields = [];
    this.searchTerm = '';
    this.excludeSearchTerm = '';
    this.excludeFields = false;
    this.fieldSelectionMode = 'all';
    this.fileFormat = 'csv';
    this.showExcludeDropdown = false;
    this.errorMessage = '';
  }

  onFieldSelectionModeChange(): void {
    // Reset excluded fields when mode changes
    this.excludedFields = [];
    this.excludeSearchTerm = '';
  }

  onExcludeFieldsChange(): void {
    if (!this.excludeFields) {
      this.excludedFields = [];
      this.excludeSearchTerm = '';
      this.showExcludeDropdown = false;
    }
  }

  toggleExcludeDropdown(): void {
    this.showExcludeDropdown = !this.showExcludeDropdown;
  }

  toggleExcludedField(field: ErpField): void {
    const index = this.excludedFields.findIndex(f => f.id === field.id);
    if (index > -1) {
      this.excludedFields.splice(index, 1);
    } else {
      this.excludedFields.push(field);
    }
  }

  isFieldExcluded(field: ErpField): boolean {
    return this.excludedFields.some(f => f.id === field.id);
  }

  removeExcludedField(field: ErpField): void {
    const index = this.excludedFields.findIndex(f => f.id === field.id);
    if (index > -1) {
      this.excludedFields.splice(index, 1);
    }
  }

  get filteredFields(): ErpField[] {
    if (!this.searchTerm) {
      return this.allFields;
    }
    const term = this.searchTerm.toLowerCase();
    return this.allFields.filter(f =>
      f.fieldLabel.toLowerCase().includes(term) ||
      f.fieldName.toLowerCase().includes(term)
    );
  }

  get excludeFieldsSource(): ErpField[] {
    // When 'all' mode, show all fields; when 'selected' mode, show only selected fields
    return this.fieldSelectionMode === 'all' ? this.allFields : this.selectedFields;
  }

  get filteredExcludeFields(): ErpField[] {
    const source = this.excludeFieldsSource;
    if (!this.excludeSearchTerm) {
      return source;
    }
    const term = this.excludeSearchTerm.toLowerCase();
    return source.filter(f =>
      f.fieldLabel.toLowerCase().includes(term) ||
      f.fieldName.toLowerCase().includes(term)
    );
  }
}
