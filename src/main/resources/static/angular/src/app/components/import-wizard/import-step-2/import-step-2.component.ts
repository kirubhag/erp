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
        <div class="map-fields-header">Map Fields</div>
        <p class="instruction-text">
          Map the {{entityName}} field names to the columns of your imported source files.
        </p>

        <!-- Mapping Progress -->
        <div class="mapping-progress" *ngIf="fieldMappings.length > 0">
          <div class="progress-stats">
            <span class="stat-item">
              <i class="fas fa-check-circle text-success"></i>
              Mapped: <strong>{{ getMappedCount() }}</strong> ({{ getMappedPercentage() }}%)
            </span>
            <span class="stat-item">
              <i class="fas fa-times-circle text-muted"></i>
              Unmapped: <strong>{{ getUnmappedCount() }}</strong> ({{ getUnmappedPercentage() }}%)
            </span>
          </div>
          <div class="progress" style="height: 8px; margin-top: 0.5rem;">
            <div class="progress-bar bg-success" [style.width.%]="getMappedPercentage()"></div>
          </div>
        </div>

        <!-- 4-Column Grid Layout -->
        <div class="field-mapping-container">
          <div class="mapping-grid">
            <!-- Header Row -->
            <div class="grid-row header-row">
              <div class="grid-cell header">Entity Field</div>
              <div class="grid-cell header">CSV Column</div>
              <div class="grid-cell header">Entity Field</div>
              <div class="grid-cell header">CSV Column</div>
            </div>
            
            <!-- Data Rows -->
            <ng-container *ngFor="let mapping of fieldMappings; let i = index">
              <!-- Only render on even indices for left column -->
              <div *ngIf="i % 2 === 0" class="grid-row data-row">
                <!-- Left Pair -->
                <div class="grid-cell field-label" [class.required]="mapping.isRequired">
                  {{ mapping.targetFieldLabel }}
                </div>
                <div class="grid-cell field-dropdown">
                  <select 
                    [(ngModel)]="mapping.sourceColumn" 
                    class="form-select"
                    [compareWith]="compareColumns">
                    <option [value]="null">-- Not Mapped --</option>
                    <option *ngFor="let col of availableColumns" [value]="col">
                      {{ col }}
                    </option>
                  </select>
                </div>
                
                <!-- Right Pair (if exists) -->
                <ng-container *ngIf="fieldMappings[i + 1]">
                  <div class="grid-cell field-label" [class.required]="fieldMappings[i + 1].isRequired">
                    {{ fieldMappings[i + 1].targetFieldLabel }}
                  </div>
                  <div class="grid-cell field-dropdown">
                    <select 
                      [(ngModel)]="fieldMappings[i + 1].sourceColumn" 
                      class="form-select"
                      [compareWith]="compareColumns">
                      <option [value]="null">-- Not Mapped --</option>
                      <option *ngFor="let col of availableColumns" [value]="col">
                        {{ col }}
                      </option>
                    </select>
                  </div>
                </ng-container>
                
                <!-- Empty cells if odd number of fields -->
                <ng-container *ngIf="!fieldMappings[i + 1]">
                  <div class="grid-cell empty"></div>
                  <div class="grid-cell empty"></div>
                </ng-container>
              </div>
            </ng-container>
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
      padding-bottom: 100px;
    }

    .content-section {
      padding: 1.5rem;
    }

    .map-fields-header {
      font-weight: 600;
      margin-bottom: 0.5rem;
      font-size: 1.25rem;
      color: #262626;
    }

    .instruction-text {
      color: #8c8c8c;
      font-size: 0.9rem;
      margin-bottom: 2rem;
      line-height: 1.5;
    }

    /* 4-Column Grid Layout */
    .field-mapping-container {
      padding: 1.5rem 0;
    }

    .mapping-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 0;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      overflow: hidden;
      background: white;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }

    .grid-row {
      display: contents;
    }

    .grid-cell {
      padding: 14px 16px;
      border-bottom: 1px solid #f0f0f0;
      border-right: 1px solid #f0f0f0;
      display: flex;
      align-items: center;
      transition: background-color 0.2s ease;
    }

    .grid-cell:nth-child(4n) {
      border-right: none;
    }

    .header-row .grid-cell {
      background: #fafafa;
      font-weight: 600;
      font-size: 0.8rem;
      text-transform: uppercase;
      color: #595959;
      letter-spacing: 0.5px;
      padding: 12px 16px;
      border-bottom: 2px solid #e0e0e0;
    }

    .data-row:hover .grid-cell:not(.empty) {
      background: #f8f9fc;
    }

    .field-label {
      font-size: 0.9rem;
      color: #262626;
      font-weight: 500;
    }

    .field-label.required::before {
      content: "* ";
      color: #ff4d4f;
      font-weight: bold;
      margin-right: 4px;
    }

    .field-dropdown {
      width: 100%;
    }

    .form-select {
      width: 100%;
      font-size: 0.875rem;
      padding: 8px 12px;
      border: 1px solid #d9d9d9;
      border-radius: 4px;
      background-color: white;
      transition: all 0.2s ease;
      cursor: pointer;
    }

    .form-select:hover {
      border-color: #40a9ff;
    }

    .form-select:focus {
      outline: none;
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }

    .grid-cell.empty {
      background: #fafafa;
      border-color: #f5f5f5;
    }

    .mapping-progress {
      margin-bottom: 1.5rem;
      padding: 1rem;
      background: #f8f9fa;
      border-radius: 6px;
      border: 1px solid #e9ecef;
    }

    .progress-stats {
      display: flex;
      gap: 2rem;
      margin-bottom: 0.5rem;
    }

    .stat-item {
      font-size: 0.9rem;
      color: #495057;
    }

    .stat-item i {
      margin-right: 0.25rem;
    }

    .auto-detect-section {
      margin-top: 2rem;
      padding-top: 1.5rem;
      border-top: 1px solid #e8e8e8;
      display: flex;
      justify-content: center;
    }

    .btn {
      padding: 8px 20px;
      border-radius: 4px;
      font-size: 0.9rem;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s ease;
      border: none;
    }

    .btn-outline-primary {
      background: white;
      border: 1px solid #1890ff;
      color: #1890ff;
    }

    .btn-outline-primary:hover:not(:disabled) {
      background: #1890ff;
      color: white;
    }

    .btn-outline-primary:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .footer-controls {
      display: flex;
      gap: 0.75rem;
      padding: 1.5rem;
      border-top: 1px solid #e8e8e8;
      justify-content: flex-end;
      background-color: white;
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      z-index: 1000;
      box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
    }

    .btn-outline-secondary {
      background: white;
      border: 1px solid #d9d9d9;
      color: #595959;
    }

    .btn-outline-secondary:hover {
      border-color: #1890ff;
      color: #1890ff;
    }

    .btn-primary {
      background: #1890ff;
      color: white;
      border: none;
    }

    .btn-primary:hover:not(:disabled) {
      background: #096dd9;
    }

    .btn-primary:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .error-banner {
      margin: 1.5rem;
      padding: 1rem 1.5rem;
      background-color: #fff1f0;
      border: 1px solid #ffccc7;
      border-radius: 4px;
      color: #ff4d4f;
      display: flex;
      align-items: center;
      justify-content: space-between;
      animation: slideDown 0.3s ease;
    }

    @keyframes slideDown {
      from {
        opacity: 0;
        transform: translateY(-10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .btn-close {
      background: none;
      border: none;
      color: #ff4d4f;
      cursor: pointer;
      font-size: 1.25rem;
      padding: 0;
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .btn-close::before {
      content: "×";
    }

    /* Responsive Design */
    @media (max-width: 992px) {
      .mapping-grid {
        grid-template-columns: repeat(2, 1fr);
      }
      
      .header-row .grid-cell:nth-child(n+3),
      .data-row .grid-cell:nth-child(n+3) {
        display: none;
      }
    }

    @media (max-width: 576px) {
      .mapping-grid {
        grid-template-columns: 1fr;
      }
      
      .header-row .grid-cell:nth-child(even),
      .data-row .grid-cell:nth-child(even) {
        display: none;
      }

      .grid-cell {
        padding: 12px;
      }

      .field-label {
        font-size: 0.85rem;
      }

      .form-select {
        font-size: 0.8rem;
        padding: 6px 10px;
      }
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

  constructor(private importService: ImportService) { }

  ngOnInit() {
    // If session is not provided via @Input, get it from the service
    if (!this.session) {
      this.session = this.importService.getCurrentSession();
    }

    if (this.session) {
      this.initializeMapping();
    } else {
      this.errorMessage = 'No import session found. Please start from step 1.';
    }
  }



  initializeMapping() {
    if (!this.session) {
      console.log('No session in initializeMapping');
      return;
    }

    console.log('Initializing mapping with session:', this.session);

    // Extract available columns from session's field mappings
    if (this.session.fieldMappings && this.session.fieldMappings.length > 0) {
      this.availableColumns = this.session.fieldMappings
        .map(m => m.sourceColumn)
        .filter((v, i, a) => v && a.indexOf(v) === i) as string[];
      console.log('Available columns extracted:', this.availableColumns);
    } else {
      console.log('No field mappings in session');
    }

    // Load field mapping template for the entity type
    this.loadFieldMappingTemplate();
    
    this.entityName = this.session.importType === 'personal' ? 'My Students' : 'Organization Students';
  }

  loadFieldMappingTemplate() {
    if (!this.session) {
      console.log('No session available');
      return;
    }

    const entityType = this.session.entityType || 'students';
    console.log('Loading field mapping template for entity type:', entityType);
    
    this.importService.getFieldMappingTemplate(entityType).subscribe({
      next: (templates: FieldMappingTemplate[]) => {
        console.log('Received templates:', templates);
        // If session already has complete mappings (with target fields), use them
        if (this.session?.fieldMappings && 
            this.session.fieldMappings.length > 0 && 
            this.session.fieldMappings[0].targetField) {
          console.log('Using existing field mappings from session');
          this.fieldMappings = [...this.session.fieldMappings];
        } else {
          // Otherwise, initialize from template
          console.log('Initializing field mappings from template');
          this.fieldMappings = templates.map(template => ({
            sourceColumn: null,
            sourceIndex: null,
            targetField: template.targetField,
            targetFieldLabel: template.targetFieldLabel,
            isRequired: template.isRequired,
            dataType: template.dataType
          }));
          console.log('Field mappings initialized:', this.fieldMappings);
          
          // Auto-detect mappings after initialization
          this.autoDetectMappings();
        }
      },
      error: (error) => {
        console.error('Error loading field mapping template:', error);
        this.errorMessage = 'Could not load field mapping template. Please try again.';
      }
    });
  }

  getMappedCount(): number {
    return this.fieldMappings.filter(m => m.sourceColumn !== null).length;
  }

  getUnmappedCount(): number {
    return this.fieldMappings.filter(m => m.sourceColumn === null).length;
  }

  getMappedPercentage(): number {
    if (this.fieldMappings.length === 0) return 0;
    return Math.round((this.getMappedCount() / this.fieldMappings.length) * 100);
  }

  getUnmappedPercentage(): number {
    if (this.fieldMappings.length === 0) return 0;
    return Math.round((this.getUnmappedCount() / this.fieldMappings.length) * 100);
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

    // Calculate sourceIndex for each mapping based on availableColumns
    const mappingsToSave = this.fieldMappings.map(mapping => ({
      ...mapping,
      sourceIndex: mapping.sourceColumn ? this.availableColumns.indexOf(mapping.sourceColumn) : null
    }));

    this.importService.saveFieldMappings(this.session.id, mappingsToSave).subscribe({
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
