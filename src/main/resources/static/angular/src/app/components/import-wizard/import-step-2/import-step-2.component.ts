import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImportService } from '../../../services/import.service';
import { ImportSession, FieldMapping, FieldMappingTemplate } from '../../../models/import.model';

/**
 * Import Step 2: Field Mapping
 * Allows user to map CSV columns to entity fields
 */
@Component({
  selector: 'app-import-step-2',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="step-2-container">
      <div class="content-section">
        <div class="map-fields-header">Map Fields:</div>
        <p class="instruction-text">
          Map the {{entityName}} field names to the columns of your imported source files.
        </p>

        <!-- Mapped Fields -->
        <div *ngFor="let mapping of fieldMappings" class="section">
          <div class="section-title">{{ getSectionTitle(mapping.targetField) }}</div>
          
          <div class="field-mapping-row">
            <div class="field-group">
              <label class="field-label" [class.required]="mapping.isRequired">
                {{ mapping.targetFieldLabel }}:
              </label>
              <select 
                class="form-select"
                [(ngModel)]="mapping.sourceColumn"
                [compareWith]="compareColumns">
                <option [value]="null">-- Not Mapped --</option>
                <option *ngFor="let col of availableColumns" [value]="col">
                  {{ col }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <!-- Auto-Detect Button -->
        <div class="auto-detect-section">
          <button 
            class="btn btn-sm btn-outline-primary"
            (click)="autoDetectMappings()"
            [disabled]="isAutoDetecting">
            <i class="fas fa-magic me-2"></i>
            <span *ngIf="isAutoDetecting">Auto-detecting...</span>
            <span *ngIf="!isAutoDetecting">Auto-Detect Mappings</span>
          </button>
        </div>
      </div>

      <!-- Footer Controls -->
      <div class="footer-controls">
        <button class="btn btn-outline-secondary" (click)="onPrevious()">Previous</button>
        <button class="btn btn-outline-secondary" (click)="onCancel()">Cancel</button>
        <button 
          class="btn btn-primary"
          [disabled]="!isValidMapping() || isSaving"
          (click)="onNext()">
          <span *ngIf="isSaving">
            <i class="fas fa-spinner fa-spin me-2"></i>Loading...
          </span>
          <span *ngIf="!isSaving">Next</span>
        </button>
      </div>

      <!-- Error Banner -->
      <div class="error-banner" *ngIf="errorMessage">
        <i class="fas fa-exclamation-circle me-2"></i>
        {{ errorMessage }}
        <button class="btn-close" (click)="errorMessage = ''"></button>
      </div>
    </div>
  `,
  styles: [`
    .step-2-container {
      position: relative;
    }

    .content-section {
      padding-bottom: 2rem;
      border-bottom: 1px solid #dee2e6;
    }

    .map-fields-header {
      font-weight: 600;
      margin-bottom: 0.5rem;
      font-size: 1rem;
      color: #333;
    }

    .instruction-text {
      color: #6c757d;
      font-size: 0.9rem;
      margin-bottom: 2rem;
    }

    .section {
      margin-bottom: 2rem;
    }

    .section-title {
      font-size: 1rem;
      font-weight: 600;
      color: #333;
      margin-bottom: 1rem;
      padding-bottom: 0.75rem;
      border-bottom: 1px solid #e9ecef;
    }

    .field-mapping-row {
      display: flex;
      gap: 2rem;
      margin-bottom: 1rem;
    }

    .field-group {
      flex: 1;
      display: flex;
      align-items: flex-start;
      gap: 1rem;
    }

    .field-label {
      min-width: 180px;
      text-align: right;
      font-size: 0.9rem;
      color: #333;
      margin-top: 0.375rem;
    }

    .field-label.required::before {
      content: "* ";
      color: #dc3545;
      font-weight: bold;
    }

    .form-select {
      font-size: 0.9rem;
      flex: 1;
    }

    .auto-detect-section {
      margin-top: 2rem;
      padding-top: 1rem;
      border-top: 1px solid #dee2e6;
    }

    .footer-controls {
      display: flex;
      gap: 0.5rem;
      padding-top: 1.5rem;
      border-top: 1px solid #dee2e6;
      justify-content: flex-end;
    }

    .error-banner {
      margin-top: 1rem;
      padding: 1rem;
      background-color: #f8d7da;
      border: 1px solid #f5c6cb;
      border-radius: 4px;
      color: #721c24;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .btn-close {
      background: none;
      border: none;
      color: #721c24;
      cursor: pointer;
      font-size: 1.25rem;
    }
  `]
})
export class ImportStep2Component implements OnInit {
  @Input() session: ImportSession | null = null;
  @Output() previousStep = new EventEmitter<number>();
  @Output() nextStep = new EventEmitter<ImportSession>();

  fieldMappings: FieldMapping[] = [];
  availableColumns: string[] = [];
  fieldMappingTemplate: FieldMappingTemplate | null = null;
  isAutoDetecting = false;
  isSaving = false;
  errorMessage = '';
  entityName = 'Student';

  constructor(private importService: ImportService) {}

  ngOnInit() {
    if (this.session) {
      this.initializeMapping();
    }
  }

  initializeMapping() {
    if (!this.session) return;

    // Get available columns from session
    this.availableColumns = this.session.fieldMappings
      .map(m => m.sourceColumn)
      .filter((v, i, a) => a.indexOf(v) === i);

    // Load field mapping template
    // For now, use existing mappings from session
    this.fieldMappings = [...this.session.fieldMappings];
    this.entityName = this.session.importType === 'personal' ? 'My Students' : 'Organization Students';
  }

  getSectionTitle(fieldKey: string): string {
    const sections: { [key: string]: string } = {
      'fullName': 'Basic Information',
      'studentId': 'Basic Information',
      'email': 'Contact Information',
      'phone': 'Contact Information',
      'grade': 'Academic Information',
      'status': 'Status Information',
      'enrollmentDate': 'Enrollment Information'
    };
    return sections[fieldKey] || 'Other Information';
  }

  compareColumns(a: any, b: any): boolean {
    return a === b;
  }

  isValidMapping(): boolean {
    // Check if all required fields are mapped
    return this.fieldMappings.every(m => !m.isRequired || m.sourceColumn);
  }

  autoDetectMappings() {
    if (!this.session) return;

    this.isAutoDetecting = true;
    this.errorMessage = '';

    this.importService.autoDetectMappings(this.session.id).subscribe({
      next: (mappings: FieldMapping[]) => {
        this.isAutoDetecting = false;
        this.fieldMappings = mappings;
      },
      error: (error) => {
        this.isAutoDetecting = false;
        this.errorMessage = 'Could not auto-detect mappings. Please map fields manually.';
        console.error('Error auto-detecting mappings:', error);
      }
    });
  }

  onPrevious() {
    this.previousStep.emit(1);
  }

  onNext() {
    if (!this.session || !this.isValidMapping()) {
      this.errorMessage = 'Please ensure all required fields are mapped.';
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';

    this.importService.saveFieldMappings(this.session.id, this.fieldMappings).subscribe({
      next: (updatedSession: ImportSession) => {
        this.isSaving = false;
        this.nextStep.emit(updatedSession);
      },
      error: (error) => {
        this.isSaving = false;
        this.errorMessage = error.error?.message || 'Error saving field mappings.';
        console.error('Error saving mappings:', error);
      }
    });
  }

  onCancel() {
    if (confirm('Are you sure you want to cancel the import? All progress will be lost.')) {
      this.importService.cancelSession(this.session?.id || '').subscribe({
        next: () => window.history.back(),
        error: () => window.history.back()
      });
    }
  }
}
