import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OrganizationService, OrganizationRegistrationRequest } from '../../services/organization.service';

@Component({
    selector: 'app-organization-creation-modal',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, FormsModule],
    templateUrl: './organization-creation-modal.component.html',
    styleUrls: ['./organization-creation-modal.component.css']
})
export class OrganizationCreationModalComponent implements OnInit {
    @Output() organizationCreated = new EventEmitter<any>();
    @Input() existingOrganization: any = null;

    registrationForm!: FormGroup;
    loading = false;
    errorMessage = '';
    successMessage = '';
    currentStep = 1;
    isUpdateMode = false;

    constructor(
        private fb: FormBuilder,
        private organizationService: OrganizationService
    ) { }

    ngOnInit(): void {
        this.initializeForm();

        if (this.existingOrganization) {
            this.isUpdateMode = true;
            this.patchFormWithExistingData();
        }
    }

    private initializeForm(): void {
        this.registrationForm = this.fb.group({
            // Basic Information
            name: ['', [Validators.required, Validators.minLength(2)]],
            type: ['School', Validators.required],
            code: ['', [Validators.required, Validators.minLength(2)]],
            description: [''],

            // Contact Information
            email: ['', [Validators.required, Validators.email]],
            phone: ['', [Validators.required, Validators.pattern(/^\+?[0-9]{10,}$/)]],

            // Address Information
            city: ['', Validators.required],
            country: ['', Validators.required],

            // Sample Data
            loadSampleData: [true]
        });
    }

    private patchFormWithExistingData(): void {
        if (!this.existingOrganization) return;

        this.registrationForm.patchValue({
            name: this.existingOrganization.name,
            type: this.existingOrganization.type || 'School',
            code: this.existingOrganization.code,
            description: this.existingOrganization.description,
            email: this.existingOrganization.email,
            phone: this.existingOrganization.phone,
            city: this.existingOrganization.city,
            country: this.existingOrganization.country
        });

        // If we have data, we can assume sample data isn't needed or is already there
        this.registrationForm.get('loadSampleData')?.setValue(false);
    }

    goToStep(step: number): void {
        if (step < this.currentStep) {
            this.currentStep = step;
        } else if (this.isStepValid(this.currentStep)) {
            this.currentStep = step;
        } else {
            this.errorMessage = 'Please complete the current step before proceeding';
        }
    }

    private isStepValid(step: number): boolean {
        const form = this.registrationForm;
        if (step === 1) {
            // In update mode, we might want to be more lenient or check specific fields
            // But for now, same validation applies
            return !!(form.get('name')?.valid && form.get('type')?.valid &&
                form.get('code')?.valid && form.get('email')?.valid &&
                form.get('phone')?.valid);
        }
        return true;
    }

    onRegister(): void {
        if (!this.registrationForm.valid) {
            this.errorMessage = 'Please fill in all required fields correctly';
            return;
        }

        this.loading = true;
        this.errorMessage = '';
        this.successMessage = '';

        const formValue = this.registrationForm.value;

        if (this.isUpdateMode && this.existingOrganization) {
            // Update existing organization
            const updateRequest = { ...this.existingOrganization, ...formValue };

            this.organizationService.updateOrganization(this.existingOrganization.id, updateRequest).subscribe({
                next: (organization) => {
                    this.loading = false;
                    this.successMessage = `Organization "${organization.name}" updated successfully!`;

                    setTimeout(() => {
                        this.organizationCreated.emit(organization);
                    }, 1500);
                },
                error: (error) => {
                    this.loading = false;
                    const errorMsg = error.error?.message || error.message || 'Update failed. Please try again.';
                    this.errorMessage = errorMsg;
                    console.error('Organization update error:', error);
                }
            });
        } else {
            // Create new organization
            const request: OrganizationRegistrationRequest = formValue;

            this.organizationService.registerOrganization(request).subscribe({
                next: (organization) => {
                    this.loading = false;
                    this.successMessage = `Organization "${organization.name}" created successfully!`;

                    setTimeout(() => {
                        this.organizationCreated.emit(organization);
                    }, 1500);
                },
                error: (error) => {
                    this.loading = false;
                    const errorMsg = error.error?.message || error.message || 'Creation failed. Please try again.';
                    this.errorMessage = errorMsg;
                    console.error('Organization creation error:', error);
                }
            });
        }
    }

    get organizationTypes(): string[] {
        return ['School', 'College', 'University', 'Training Center', 'Other'];
    }

    get countries(): string[] {
        return ['USA', 'Canada', 'UK', 'India', 'Australia', 'Germany', 'France', 'Other'];
    }

    isFieldInvalid(fieldName: string): boolean {
        const control = this.registrationForm.get(fieldName);
        return !!(control && control.invalid && (control.dirty || control.touched));
    }
}
