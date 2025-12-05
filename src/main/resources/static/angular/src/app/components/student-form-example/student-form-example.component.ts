import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FieldService } from '../../services/field.service';
import { ErpField, FieldsGroupedBySection } from '../../models/erp-field.model';
import { DynamicFieldRendererComponent } from '../dynamic-field-renderer/dynamic-field-renderer.component';

/**
 * Example Student Form using Dynamic Field Renderer
 * This demonstrates how to integrate the dynamic field renderer into a create/edit form
 */
@Component({
    selector: 'app-student-form-example',
    standalone: true,
    imports: [CommonModule, DynamicFieldRendererComponent],
    template: `
        <div class="container mt-4">
            <div class="card">
                <div class="card-header">
                    <h3>{{ isEditMode ? 'Edit Student' : 'Create New Student' }}</h3>
                </div>
                <div class="card-body">
                    <!-- Loading State -->
                    <div *ngIf="loading" class="text-center">
                        <div class="spinner-border" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                    </div>

                    <!-- Error State -->
                    <div *ngIf="error" class="alert alert-danger">
                        {{ error }}
                    </div>

                    <!-- Form -->
                    <form *ngIf="!loading && !error" (ngSubmit)="onSubmit()">
                        <!-- Render fields grouped by section -->
                        <div *ngFor="let section of getSectionNames()" class="mb-4">
                            <h5 class="section-header">{{ section }}</h5>
                            <div class="row">
                                <div *ngFor="let field of fieldsGrouped[section]" 
                                     [ngClass]="getFieldColumnClass(field)"
                                     class="mb-3">
                                    <app-dynamic-field-renderer
                                        [field]="field"
                                        [value]="formData[field.fieldName]"
                                        [mode]="isEditMode ? 'edit' : 'create'"
                                        [disabled]="submitting"
                                        (valueChange)="onFieldChange(field.fieldName, $event)">
                                    </app-dynamic-field-renderer>
                                </div>
                            </div>
                        </div>

                        <!-- Form Actions -->
                        <div class="form-actions mt-4">
                            <button type="submit" 
                                    class="btn btn-primary" 
                                    [disabled]="submitting || !isFormValid()">
                                <span *ngIf="submitting" class="spinner-border spinner-border-sm me-2"></span>
                                {{ isEditMode ? 'Update Student' : 'Create Student' }}
                            </button>
                            <button type="button" 
                                    class="btn btn-secondary ms-2" 
                                    (click)="onCancel()"
                                    [disabled]="submitting">
                                Cancel
                            </button>
                        </div>

                        <!-- Validation Summary -->
                        <div *ngIf="validationErrors.length > 0" class="alert alert-danger mt-3">
                            <h6>Please fix the following errors:</h6>
                            <ul>
                                <li *ngFor="let error of validationErrors">{{ error }}</li>
                            </ul>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .section-header {
            border-bottom: 2px solid #007bff;
            padding-bottom: 0.5rem;
            margin-bottom: 1rem;
            color: #007bff;
        }

        .form-actions {
            border-top: 1px solid #dee2e6;
            padding-top: 1rem;
        }

        .card {
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        }
    `]
})
export class StudentFormExampleComponent implements OnInit {
    // Component state
    loading: boolean = true;
    error: string = '';
    submitting: boolean = false;
    isEditMode: boolean = false;

    // Field configuration
    fieldsGrouped: FieldsGroupedBySection = {};
    formData: any = {};
    validationErrors: string[] = [];

    constructor(
        private fieldService: FieldService,
        private http: HttpClient,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadFields();

        // Check if we're in edit mode (you would get this from route params)
        // this.isEditMode = this.route.snapshot.params['id'] !== undefined;
        // if (this.isEditMode) {
        //     this.loadStudentData(this.route.snapshot.params['id']);
        // }
    }

    /**
     * Load field configuration from backend
     */
    loadFields(): void {
        this.loading = true;
        this.fieldService.getFieldsGroupedBySection('STUDENT').subscribe({
            next: (grouped) => {
                this.fieldsGrouped = grouped;
                this.initializeFormData();
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading fields:', err);
                this.error = 'Failed to load form fields';
                this.loading = false;
            }
        });
    }

    /**
     * Initialize form data with default values
     */
    initializeFormData(): void {
        Object.values(this.fieldsGrouped).forEach(fields => {
            fields.forEach(field => {
                if (field.uiType === 114) { // Checkbox
                    this.formData[field.fieldName] = false;
                } else {
                    this.formData[field.fieldName] = null;
                }
            });
        });
    }

    /**
     * Handle field value change
     */
    onFieldChange(fieldName: string, value: any): void {
        this.formData[fieldName] = value;
        // Clear validation errors when user makes changes
        this.validationErrors = [];
    }

    /**
     * Get section names in order
     */
    getSectionNames(): string[] {
        return Object.keys(this.fieldsGrouped);
    }

    /**
     * Get Bootstrap column class based on field width
     */
    getFieldColumnClass(field: ErpField): string {
        const width = field.columnWidth || 'medium';
        const classMap: { [key: string]: string } = {
            'small': 'col-md-3',
            'medium': 'col-md-6',
            'large': 'col-md-12'
        };
        return classMap[width] || 'col-md-6';
    }

    /**
     * Validate form before submission
     */
    isFormValid(): boolean {
        this.validationErrors = [];

        Object.values(this.fieldsGrouped).forEach(fields => {
            fields.forEach(field => {
                if (field.isRequired && !this.formData[field.fieldName]) {
                    this.validationErrors.push(`${field.fieldLabel} is required`);
                }
            });
        });

        return this.validationErrors.length === 0;
    }

    /**
     * Submit form
     */
    onSubmit(): void {
        if (!this.isFormValid()) {
            return;
        }

        this.submitting = true;
        const endpoint = this.isEditMode ?
            `/api/students/${this.formData.id}` :
            '/api/students';

        const method = this.isEditMode ? 'put' : 'post';

        this.http[method](endpoint, this.formData).subscribe({
            next: (response) => {
                console.log('Student saved successfully:', response);
                this.router.navigate(['/students']);
            },
            error: (err) => {
                console.error('Error saving student:', err);
                this.error = 'Failed to save student. Please try again.';
                this.submitting = false;
            }
        });
    }

    /**
     * Cancel and go back
     */
    onCancel(): void {
        this.router.navigate(['/students']);
    }
}
