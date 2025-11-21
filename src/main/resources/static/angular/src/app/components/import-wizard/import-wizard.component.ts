import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ImportService } from '../../services/import.service';
import { ImportSession } from '../../models/import.model';
import { ImportStep1Component } from './import-step-1/import-step-1.component';
import { ImportStep2Component } from './import-step-2/import-step-2.component';
import { ImportStep3Component } from './import-step-3/import-step-3.component';
import { ImportStep4Component } from './import-step-4/import-step-4.component';

/**
 * Import Wizard Main Component
 * Manages the multi-step import workflow with validation and progress tracking
 */
@Component({
  selector: 'app-import-wizard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ImportStep1Component,
    ImportStep2Component,
    ImportStep3Component,
    ImportStep4Component
  ],
  template: `
    <div class="import-wizard-container">
      <!-- Wizard Header -->
      <div class="wizard-header">
        <div class="wizard-title">
          <h4>Import Wizard</h4>
          <span class="step-indicator">Step {{ currentStep }} of 4</span>
        </div>
      </div>

      <!-- Progress Bar -->
      <div class="progress-section">
        <div class="progress" role="progressbar">
          <div class="progress-bar" [style.width.%]="(currentStep / 4) * 100"></div>
        </div>
      </div>

      <!-- Wizard Steps -->
      <div class="steps-container">
        <!-- Step 1: File Upload -->
        <app-import-step-1 
          *ngIf="currentStep === 1"
          [entityType]="entityType"
          (nextStep)="onNextStep($event)">
        </app-import-step-1>

        <!-- Step 2: Field Mapping -->
        <app-import-step-2 
          *ngIf="currentStep === 2"
          [session]="currentSession"
          (previousStep)="currentStep = $event"
          (nextStep)="onNextStep($event)">
        </app-import-step-2>

        <!-- Step 3: Confirmation -->
        <app-import-step-3 
          *ngIf="currentStep === 3"
          [session]="currentSession"
          (previousStep)="currentStep = $event"
          (importStart)="onImportStart($event)">
        </app-import-step-3>

        <!-- Step 4: Summary -->
        <app-import-step-4 
          *ngIf="currentStep === 4"
          [session]="currentSession"
          (done)="onImportDone()">
        </app-import-step-4>
      </div>

      <!-- Error Message -->
      <div class="error-banner" *ngIf="errorMessage">
        <i class="fas fa-exclamation-circle me-2"></i>
        {{ errorMessage }}
        <button class="btn-close" (click)="errorMessage = ''"></button>
      </div>
    </div>
  `,
  styles: [`
    .import-wizard-container {
      width: 100%;
      margin: 60px 0 0 0;
      padding: 2rem;
      background: white;
      min-height: calc(100vh - 60px);
      position: relative;
      z-index: 1;
    }

    .wizard-header {
      margin-bottom: 2rem;
      padding-bottom: 1rem;
      border-bottom: 1px solid #dee2e6;
    }

    .wizard-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .wizard-title h4 {
      margin: 0;
      font-size: 1.5rem;
      color: #333;
    }

    .step-indicator {
      color: #6c757d;
      font-size: 0.9rem;
    }

    .progress-section {
      margin-bottom: 2rem;
    }

    .progress {
      height: 6px;
      border-radius: 3px;
      background-color: #e9ecef;
    }

    .progress-bar {
      background-color: var(--app-primary, #0099cc);
      transition: width 0.3s ease;
      height: 100%;
      border-radius: 3px;
    }

    .steps-container {
      min-height: 500px;
    }

    .error-banner {
      margin-top: 2rem;
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
export class ImportWizardComponent implements OnInit, OnDestroy {
  currentStep = 1;
  currentSession: ImportSession | null = null;
  entityType: string = '';
  errorMessage = '';
  private destroy$ = new Subject<void>();

  constructor(
    private importService: ImportService,
    private router: Router
  ) {
    // Get entity type from route params or use default
    this.entityType = 'students';
  }

  ngOnInit() {
    this.importService.currentStep$
      .pipe(takeUntil(this.destroy$))
      .subscribe(step => {
        this.currentStep = step;
      });

    this.importService.currentSession$
      .pipe(takeUntil(this.destroy$))
      .subscribe(session => {
        this.currentSession = session;
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onNextStep(session: ImportSession) {
    this.currentSession = session;
    this.importService.setCurrentSession(session);
    this.importService.setCurrentStep(this.currentStep + 1);
  }

  onImportStart(session: ImportSession) {
    this.currentSession = session;
    this.importService.setCurrentSession(session);
    this.importService.setCurrentStep(4);
  }

  onImportDone() {
    this.router.navigate([`/${this.entityType}`]);
  }
}
