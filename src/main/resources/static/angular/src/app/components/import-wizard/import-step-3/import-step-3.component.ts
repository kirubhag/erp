import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImportService } from '../../../services/import.service';
import { ImportSession } from '../../../models/import.model';

/**
 * Import Step 3: Confirmation
 * Review unmapped columns and confirm import settings
 */
@Component({
  selector: 'app-import-step-3',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="step-3-container">
      <div class="content-section">
        <div class="confirm-mapping-title">Confirm Mapping:</div>
        
        <div class="mapping-description">
          The following columns in your import file are not mapped with any of the {{entityName}} fields. 
          If you continue, the data in these columns will not be imported. 
          Click the <strong>Previous</strong> button to go back and map these fields. 
          If you wish to continue, click the <strong>Import</strong> button.
        </div>

        <!-- Unmapped Columns List -->
        <div *ngIf="unmappedColumns.length > 0">
          <ul class="unmapped-columns">
            <li *ngFor="let col of unmappedColumns">
              {{ col }}
            </li>
          </ul>
        </div>

        <!-- No Unmapped Columns -->
        <div *ngIf="unmappedColumns.length === 0" class="alert alert-success">
          <i class="fas fa-check-circle me-2"></i>
          All columns have been successfully mapped!
        </div>

        <!-- Import Summary -->
        <div class="summary-info mt-4">
          <h6 class="mb-3">Import Summary</h6>
          <div class="info-row">
            <span class="info-label">File:</span>
            <span class="info-value">{{ session?.fileName }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Total Records:</span>
            <span class="info-value">{{ session?.totalRecords }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Import Type:</span>
            <span class="info-value">{{ getImportType(session?.importType) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Duplicate Action:</span>
            <span class="info-value">{{ session?.duplicateAction | titlecase }}</span>
          </div>
        </div>
      </div>

      <!-- Footer Controls -->
      <div class="footer-controls">
        <button class="btn btn-outline-secondary" (click)="onPrevious()">Previous</button>
        <button class="btn btn-outline-secondary" (click)="onCancel()">Cancel</button>
        <button 
          class="btn btn-primary"
          [disabled]="isImporting"
          (click)="onImport()">
          <span *ngIf="isImporting">
            <i class="fas fa-spinner fa-spin me-2"></i>Importing...
          </span>
          <span *ngIf="!isImporting">Import</span>
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
    .step-3-container {
      position: relative;
    }

    .content-section {
      padding-bottom: 2rem;
      border-bottom: 1px solid #dee2e6;
    }

    .confirm-mapping-title {
      font-size: 1rem;
      font-weight: 600;
      color: #333;
      margin-bottom: 1rem;
    }

    .mapping-description {
      font-size: 0.9rem;
      color: #595959;
      line-height: 1.6;
      margin-bottom: 1.5rem;
    }

    .mapping-description strong {
      font-weight: 600;
      color: #262626;
    }

    .unmapped-columns {
      list-style: none;
      padding-left: 20px;
      margin: 0;
    }

    .unmapped-columns li {
      font-size: 0.9rem;
      color: #595959;
      padding: 4px 0;
      line-height: 1.8;
    }

    .unmapped-columns li::before {
      content: "• ";
      color: #dc3545;
      margin-right: 8px;
    }

    .alert-success {
      padding: 1rem;
      background-color: #d4edda;
      border: 1px solid #c3e6cb;
      color: #155724;
      border-radius: 4px;
      margin: 0;
    }

    .summary-info {
      background-color: #f8f9fa;
      padding: 1rem;
      border-radius: 4px;
      border-left: 4px solid #0077cc;
    }

    .summary-info h6 {
      color: #333;
      font-weight: 600;
      margin-bottom: 1rem;
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      font-size: 0.9rem;
      padding: 0.5rem 0;
      border-bottom: 1px solid #e9ecef;
    }

    .info-row:last-child {
      border-bottom: none;
    }

    .info-label {
      color: #6c757d;
      font-weight: 500;
    }

    .info-value {
      color: #333;
      font-weight: 600;
    }

    .footer-controls {
      display: flex;
      gap: 0.5rem;
      padding: 1.5rem;
      border-top: 1px solid #dee2e6;
      justify-content: flex-end;
      background-color: white;
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      z-index: 1000;
      box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
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
export class ImportStep3Component implements OnInit {
  @Input() session: ImportSession | null = null;
  @Output() previousStep = new EventEmitter<number>();
  @Output() importStart = new EventEmitter<ImportSession>();

  unmappedColumns: string[] = [];
  isImporting = false;
  errorMessage = '';
  entityName = 'Student';

  constructor(private importService: ImportService) {}

  ngOnInit() {
    if (this.session) {
      this.unmappedColumns = this.session.unmappedColumns || [];
      this.entityName = this.session.importType === 'personal' ? 'My Students' : 'Organization Students';
    }
  }

  getImportType(type?: string): string {
    return type === 'personal' ? 'Personal' : 'Organization';
  }

  onPrevious() {
    this.previousStep.emit(2);
  }

  onImport() {
    if (!this.session) {
      this.errorMessage = 'Invalid import session. Please start over.';
      return;
    }

    // Validate mappings before import
    this.isImporting = true;
    this.errorMessage = '';

    this.importService.validateMappings(this.session.id).subscribe({
      next: (result) => {
        if (result.valid) {
          // Start the actual import
          this.startImport();
        } else {
          this.isImporting = false;
          this.errorMessage = result.errors?.join(', ') || 'Invalid field mappings.';
        }
      },
      error: (error) => {
        this.isImporting = false;
        this.errorMessage = 'Error validating mappings. Please try again.';
        console.error('Error validating mappings:', error);
      }
    });
  }

  private startImport() {
    if (!this.session) return;

    this.importService.startImport(this.session.id).subscribe({
      next: (event: any) => {
        if (event.type === 4) { // HttpResponse
          this.isImporting = false;
          if (this.session) {
            this.importStart.emit(this.session);
          }
        }
      },
      error: (error) => {
        this.isImporting = false;
        this.errorMessage = error.error?.message || 'Error starting import. Please try again.';
        console.error('Error starting import:', error);
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
