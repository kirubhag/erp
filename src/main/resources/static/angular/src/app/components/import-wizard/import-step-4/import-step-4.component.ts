import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImportService } from '../../../services/import.service';
import { ImportSession, ImportResult } from '../../../models/import.model';
import { Subject, interval } from 'rxjs';
import { takeUntil, switchMap } from 'rxjs/operators';

/**
 * Import Step 4: Summary & Results
 * Displays import results and allows undo/navigation
 */
@Component({
  selector: 'app-import-step-4',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="step-4-container">
      <div class="content-section">
        <!-- Loading State -->
        <div *ngIf="isLoading" class="loading-state">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Importing...</span>
          </div>
          <p class="text-muted mt-3">Processing {{ importedCount }} of {{ session?.totalRecords }} records...</p>
          <div class="progress mt-3" style="height: 6px;">
            <div class="progress-bar" [style.width.%]="getProgressPercent()"></div>
          </div>
        </div>

        <!-- Results Summary -->
        <div *ngIf="!isLoading && session">
          <!-- Header -->
          <div class="summary-header">
            <div class="d-flex align-items-center">
              <h5 class="summary-title mb-0">Import Summary {{ session.fileName }}</h5>
              <a href="#" class="undo-link" (click)="onUndoImport($event)">
                <i class="fas fa-undo me-1"></i>Undo Import
              </a>
            </div>
          </div>

          <!-- Statistics -->
          <div class="stats-section">
            <div class="stats-item">
              <span class="stats-label">Total records in file</span>
              <span class="stats-value ms-2">{{ session.statistics?.totalRecords || session.totalRecords }}</span>
            </div>
          </div>

          <div class="stats-section">
            <div class="stats-item" *ngIf="session.statistics?.addedRecords">
              <span class="stats-label status-added">
                <i class="fas fa-plus-circle me-1"></i>Added
              </span>
              <span class="stats-value ms-2">{{ session.statistics.addedRecords }}</span>
            </div>
            <div class="stats-item" *ngIf="session.statistics?.updatedRecords">
              <span class="stats-label status-updated">
                <i class="fas fa-edit me-1"></i>Updated
              </span>
              <span class="stats-value ms-2">{{ session.statistics.updatedRecords }}</span>
            </div>
            <div class="stats-item" *ngIf="session.statistics?.skippedRecords">
              <span class="stats-label status-skipped">
                <i class="fas fa-skip-forward me-1"></i>Skipped
              </span>
              <span class="stats-value ms-2">{{ session.statistics.skippedRecords }}</span>
            </div>
            <div class="stats-item" *ngIf="session.statistics?.failedRecords && session.statistics.failedRecords > 0">
              <span class="stats-label status-failed">
                <i class="fas fa-times-circle me-1"></i>Failed
              </span>
              <span class="stats-value ms-2">{{ session.statistics.failedRecords }}</span>
            </div>
          </div>

          <!-- Success Rate -->
          <div class="success-rate" *ngIf="session.statistics?.successRate">
            <span class="rate-label">Success Rate:</span>
            <div class="rate-bar">
              <div class="rate-fill" [style.width.%]="session.statistics.successRate"></div>
            </div>
            <span class="rate-value">{{ session.statistics.successRate }}%</span>
          </div>

          <!-- Results Table -->
          <div class="table-section" *ngIf="importedResults && importedResults.length > 0">
            <h6 class="table-title">Import Details</h6>
            <div class="table-responsive">
              <table class="table">
                <thead>
                  <tr>
                    <th>Row Number</th>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Errors</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let result of importedResults.slice(0, 10)" [ngClass]="'status-' + result.status">
                    <td>{{ result.rowNumber }}</td>
                    <td>{{ result.recordId }}</td>
                    <td>{{ result.data?.fullName || result.data?.name || '-' }}</td>
                    <td>
                      <span class="badge" [ngClass]="getStatusBadgeClass(result.status)">
                        {{ result.status | titlecase }}
                      </span>
                    </td>
                    <td>
                      <small *ngIf="result.errors && result.errors.length > 0">
                        {{ result.errors[0] }}
                      </small>
                      <small *ngIf="!result.errors || result.errors.length === 0" class="text-muted">-</small>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="text-center text-muted mt-2" *ngIf="importedResults.length > 10">
              <small>Showing 10 of {{ importedResults.length }} records</small>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer Controls -->
      <div class="footer-controls">
        <button class="btn btn-primary" (click)="onDone()">
          <i class="fas fa-check me-2"></i>Done
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
    .step-4-container {
      position: relative;
    }

    .content-section {
      padding-bottom: 2rem;
      border-bottom: 1px solid #dee2e6;
      min-height: 400px;
    }

    .loading-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 400px;
      text-align: center;
    }

    .spinner-border {
      width: 3rem;
      height: 3rem;
    }

    .summary-header {
      margin-bottom: 1.5rem;
    }

    .summary-title {
      margin: 0;
      font-size: 1.1rem;
      color: #333;
    }

    .undo-link {
      color: #dc3545;
      text-decoration: none;
      font-size: 0.9rem;
      margin-left: 1rem;
    }

    .undo-link:hover {
      text-decoration: underline;
    }

    .stats-section {
      margin-bottom: 1.5rem;
      padding-bottom: 1rem;
      border-bottom: 1px solid #dee2e6;
    }

    .stats-item {
      display: inline-block;
      margin-right: 2rem;
      font-size: 0.95rem;
    }

    .stats-label {
      color: #666;
    }

    .stats-value {
      font-weight: 600;
      color: #333;
    }

    .status-added {
      color: #28a745;
    }

    .status-updated {
      color: #0077cc;
    }

    .status-skipped {
      color: #ffc107;
    }

    .status-failed {
      color: #dc3545;
    }

    .success-rate {
      display: flex;
      align-items: center;
      gap: 1rem;
      margin-bottom: 2rem;
      font-size: 0.9rem;
    }

    .rate-label {
      color: #666;
      min-width: 100px;
    }

    .rate-bar {
      flex: 1;
      height: 24px;
      background-color: #e9ecef;
      border-radius: 4px;
      overflow: hidden;
    }

    .rate-fill {
      height: 100%;
      background: linear-gradient(90deg, #28a745, #20c997);
      transition: width 0.3s ease;
    }

    .rate-value {
      color: #333;
      font-weight: 600;
      min-width: 50px;
      text-align: right;
    }

    .table-section {
      margin-top: 2rem;
    }

    .table-title {
      font-size: 1rem;
      font-weight: 600;
      color: #333;
      margin-bottom: 1rem;
    }

    .table-responsive {
      background-color: white;
      border-radius: 4px;
      border: 1px solid #dee2e6;
    }

    .table {
      margin-bottom: 0;
      font-size: 0.9rem;
    }

    .table thead th {
      background-color: #f8f9fa;
      border-bottom: 2px solid #dee2e6;
      font-weight: 600;
      color: #333;
      padding: 0.75rem;
      white-space: nowrap;
    }

    .table tbody td {
      padding: 0.75rem;
      vertical-align: middle;
      border-bottom: 1px solid #dee2e6;
    }

    .table tbody tr:hover {
      background-color: #f8f9fa;
    }

    .status-added {
      background-color: #d4edda;
      color: #155724;
    }

    .status-updated {
      background-color: #cfe2ff;
      color: #084298;
    }

    .status-skipped {
      background-color: #fff3cd;
      color: #664d03;
    }

    .status-failed {
      background-color: #f8d7da;
      color: #842029;
    }

    .badge {
      font-size: 0.75rem;
      padding: 0.35rem 0.65rem;
      border-radius: 12px;
    }

    .badge-added {
      background-color: #d4edda;
      color: #155724;
    }

    .badge-updated {
      background-color: #cfe2ff;
      color: #084298;
    }

    .badge-skipped {
      background-color: #fff3cd;
      color: #664d03;
    }

    .badge-failed {
      background-color: #f8d7da;
      color: #842029;
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
export class ImportStep4Component implements OnInit, OnDestroy {
  @Input() session: ImportSession | null = null;
  @Output() done = new EventEmitter<void>();

  isLoading = true;
  importedCount = 0;
  importedResults: ImportResult[] = [];
  errorMessage = '';
  private destroy$ = new Subject<void>();

  constructor(private importService: ImportService) {}

  ngOnInit() {
    if (this.session) {
      this.loadImportResults();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadImportResults() {
    if (!this.session) return;

    // Poll for results every 2 seconds
    interval(2000)
      .pipe(
        switchMap(() => this.importService.getImportSummary(this.session!.id)),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (updatedSession: ImportSession) => {
          if (updatedSession.importedRecords) {
            this.importedResults = updatedSession.importedRecords;
            this.importedCount = this.importedResults.length;
          }

          if (updatedSession.status === 'completed') {
            this.isLoading = false;
            this.session = updatedSession;
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Error loading import results.';
          console.error('Error loading import results:', error);
        }
      });
  }

  getProgressPercent(): number {
    if (!this.session || this.session.totalRecords === 0) return 0;
    return Math.round((this.importedCount / this.session.totalRecords) * 100);
  }

  getStatusBadgeClass(status: string): string {
    const classes: { [key: string]: string } = {
      'added': 'bg-success',
      'updated': 'bg-info',
      'skipped': 'bg-warning',
      'failed': 'bg-danger'
    };
    return classes[status] || 'bg-secondary';
  }

  onUndoImport(event: any) {
    event.preventDefault();
    if (!this.session) return;

    if (confirm('Are you sure you want to undo this import? This action cannot be reversed.')) {
      this.importService.undoImport(this.session.id).subscribe({
        next: () => {
          this.done.emit();
        },
        error: (error) => {
          this.errorMessage = 'Error undoing import. Please contact support.';
          console.error('Error undoing import:', error);
        }
      });
    }
  }

  onDone() {
    this.done.emit();
  }
}
