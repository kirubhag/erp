import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImportService } from '../../../services/import.service';
import { ImportSession, ImportSettings } from '../../../models/import.model';

/**
 * Import Step 1: File Upload & Settings
 * Allows user to upload file and configure import settings
 */
@Component({
  selector: 'app-import-step-1',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="step-1-container">
      <div class="content-wrapper">
        <!-- Left Section: Upload & Settings -->
        <div class="left-section">
          <h6 class="mb-3">Select a file to upload</h6>
          
          <!-- File Input -->
          <div class="file-input-wrapper">
            <div class="mb-3">
              <input 
                type="file"
                #fileInput
                class="form-control"
                accept=".xlsx,.xls,.csv,.vcf"
                (change)="onFileSelected($event)"
                hidden>
              <button 
                class="btn btn-outline-secondary btn-sm"
                (click)="fileInput.click()">
                <i class="fas fa-folder-open me-2"></i>Browse
              </button>
            </div>
            <div class="mb-2" *ngIf="selectedFile">
              <input 
                type="text" 
                class="form-control" 
                [value]="selectedFile.name" 
                readonly>
            </div>
            <small class="help-text">
              Files may be up to 50 MB, and .xls, .xlsx, .csv or .vcf formats.
            </small>
          </div>

          <!-- Import Type -->
          <div class="mb-4">
            <h6 class="section-title mb-3">Import Type</h6>
            <div class="form-check mb-2">
              <input 
                class="form-check-input" 
                type="radio" 
                name="importType" 
                id="myCandidate"
                [(ngModel)]="settings.importType"
                value="personal"
                checked>
              <label class="form-check-label" for="myCandidate">
                Import My {{entityName}}
              </label>
            </div>
            <div class="form-check">
              <input 
                class="form-check-input" 
                type="radio" 
                name="importType" 
                id="orgCandidate"
                [(ngModel)]="settings.importType"
                value="organization">
              <label class="form-check-label" for="orgCandidate">
                Import My Organization {{entityNamePlural}}
              </label>
            </div>
          </div>

          <!-- Manual Approval -->
          <div class="mb-4">
            <div class="form-check">
              <input 
                class="form-check-input" 
                type="checkbox" 
                id="manualApproval"
                [(ngModel)]="settings.enableManualApproval">
              <label class="form-check-label" for="manualApproval">
                Enable Manual {{entityName}} Approval
              </label>
            </div>
          </div>

          <!-- Duplicate Handling -->
          <div class="mb-4">
            <h6 class="section-title mb-3">In case of duplicate records found</h6>
            <div class="form-check mb-2">
              <input 
                class="form-check-input" 
                type="radio" 
                name="duplicateAction" 
                id="skip"
                [(ngModel)]="settings.duplicateAction"
                value="skip">
              <label class="form-check-label" for="skip">
                Skip
              </label>
            </div>
            <div class="form-check mb-2">
              <input 
                class="form-check-input" 
                type="radio" 
                name="duplicateAction" 
                id="overwrite"
                [(ngModel)]="settings.duplicateAction"
                value="overwrite"
                checked>
              <label class="form-check-label" for="overwrite">
                Overwrite
              </label>
            </div>
            <div class="form-check mb-3">
              <input 
                class="form-check-input" 
                type="radio" 
                name="duplicateAction" 
                id="clone"
                [(ngModel)]="settings.duplicateAction"
                value="clone">
              <label class="form-check-label" for="clone">
                Clone
              </label>
            </div>
            
            <div class="form-check mb-3">
              <input 
                class="form-check-input" 
                type="checkbox" 
                id="skipEmpty"
                [(ngModel)]="settings.skipEmptyFields">
              <label class="form-check-label" for="skipEmpty">
                Skip empty fields during import
                <i class="fas fa-info-circle info-icon ms-1"></i>
              </label>
            </div>
          </div>

          <!-- Advanced Options -->
          <div class="mb-4">
            <a href="#" class="text-primary text-decoration-none advanced-toggle" (click)="toggleAdvanced($event)">
              Advanced Options <i class="fas" [ngClass]="showAdvanced ? 'fa-chevron-up' : 'fa-chevron-down'"></i>
            </a>
            
            <div class="mt-3" *ngIf="showAdvanced">
              <label class="form-label">Find duplicates using:</label>
              <select class="form-select" [(ngModel)]="settings.findDuplicatesBy">
                <option value="email">Email</option>
                <option value="phone">Phone</option>
                <option value="name">Name</option>
                <option value="id">ID</option>
              </select>
              <i class="fas fa-question-circle info-icon mt-2"></i>
            </div>
          </div>
        </div>

        <!-- Right Section: Info Panel -->
        <div class="right-section">
          <h6 class="mb-3">Supported formats</h6>
          <p class="small mb-4">
            You can use .xls .xlsx .vcf or .csv format and import up to 1500 records. 
            For more than 1500 records, only .csv format is supported.
          </p>

          <h6 class="mb-3">Maximum limits</h6>
          <ul class="small mb-4">
            <li class="mb-2">Enterprise - 20000 per batch.</li>
            <li class="mb-2">Skip/Overwrite can be performed with a maximum of 5000 records.</li>
          </ul>

          <h6 class="mb-3">Important notes</h6>
          <ul class="small">
            <li class="mb-2">Duplicates will be verified based on "{{ settings.findDuplicatesBy | titlecase }}" field.</li>
            <li class="mb-2">First row of the given file will be treated as field names.</li>
            <li class="mb-2">Please ensure your file size does not exceed 50 MB.</li>
            <li class="mb-2">Before importing the data all duplicate records will be ignored from the XLS/CSV file.</li>
            <li class="mb-2">While checking for duplicate records, you can overwrite, clone, or skip the duplicates.</li>
            <li class="mb-2">In case of XLS file, only MS Excel 97 - 2003 formats are supported.</li>
            <li class="mb-2">Unexpected errors may occur if the XLS file contains any special controls like combo filters or images embedded within it.</li>
          </ul>
        </div>
      </div>

      <!-- Footer Controls -->
      <div class="footer-controls">
        <button class="btn btn-outline-secondary" (click)="onCancel()">Cancel</button>
        <button 
          class="btn btn-primary" 
          [disabled]="!selectedFile || isLoading"
          (click)="onNext()">
          <span *ngIf="isLoading">
            <i class="fas fa-spinner fa-spin me-2"></i>Loading...
          </span>
          <span *ngIf="!isLoading">Next</span>
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
    .step-1-container {
      position: relative;
    }

    .content-wrapper {
      display: flex;
      gap: 2rem;
      margin-bottom: 2rem;
    }

    .left-section {
      flex: 1;
      max-width: 600px;
    }

    .right-section {
      flex: 1;
      background-color: #f8f9fa;
      padding: 1.5rem;
      border-radius: 8px;
      height: fit-content;
    }

    .file-input-wrapper {
      border: 2px dashed #dee2e6;
      border-radius: 4px;
      padding: 1rem;
      margin-bottom: 1.5rem;
      background-color: #fafafa;
    }

    .help-text {
      color: #6c757d;
      font-size: 0.875rem;
    }

    .section-title {
      font-size: 0.95rem;
      color: #333;
      font-weight: 600;
      margin-bottom: 1rem;
    }

    .info-icon {
      color: #6c757d;
      cursor: help;
    }

    .advanced-toggle {
      text-decoration: none !important;
    }

    .footer-controls {
      display: flex;
      gap: 0.5rem;
      padding-top: 1.5rem;
      border-top: 1px solid #dee2e6;
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

    @media (max-width: 1024px) {
      .content-wrapper {
        flex-direction: column;
      }

      .right-section {
        max-width: 100%;
      }
    }
  `]
})
export class ImportStep1Component implements OnInit {
  @Input() entityType: string = 'student';
  @Output() nextStep = new EventEmitter<ImportSession>();

  selectedFile: File | null = null;
  isLoading = false;
  errorMessage = '';
  showAdvanced = false;

  entityName = 'Student';
  entityNamePlural = 'Students';

  settings: ImportSettings = {
    importType: 'personal',
    enableManualApproval: false,
    duplicateAction: 'overwrite',
    skipEmptyFields: false,
    findDuplicatesBy: 'email'
  };

  constructor(private importService: ImportService) {
    this.updateEntityNames();
  }

  ngOnInit() {
    // Any initialization if needed
  }

  updateEntityNames() {
    const names: { [key: string]: { singular: string; plural: string } } = {
      'student': { singular: 'Student', plural: 'Students' },
      'candidate': { singular: 'Candidate', plural: 'Candidates' },
      'contact': { singular: 'Contact', plural: 'Contacts' },
      'user': { singular: 'User', plural: 'Users' }
    };

    const names_entry = names[this.entityType.toLowerCase()] || names['student'];
    this.entityName = names_entry.singular;
    this.entityNamePlural = names_entry.plural;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const format = this.importService.detectFileFormat(file.name);
      
      if (!this.importService.isSupportedFormat(format)) {
        this.errorMessage = `File format '.${format}' is not supported. Please use .xlsx, .xls, .csv, or .vcf`;
        return;
      }

      if (!this.importService.isValidFileSize(file.size)) {
        this.errorMessage = `File size exceeds 50 MB limit. Current size: ${this.importService.formatFileSize(file.size)}`;
        return;
      }

      this.selectedFile = file;
      this.errorMessage = '';
    }
  }

  toggleAdvanced(event: any) {
    event.preventDefault();
    this.showAdvanced = !this.showAdvanced;
  }

  onNext() {
    if (!this.selectedFile) {
      this.errorMessage = 'Please select a file to continue.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.importService.createImportSession(
      this.selectedFile,
      this.entityType,
      this.settings
    ).subscribe({
      next: (session: ImportSession) => {
        this.isLoading = false;
        this.nextStep.emit(session);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Error uploading file. Please try again.';
        console.error('Error creating import session:', error);
      }
    });
  }

  onCancel() {
    if (confirm('Are you sure you want to cancel the import? All progress will be lost.')) {
      window.history.back();
    }
  }
}
