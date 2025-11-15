import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { OrganizationService, OrganizationRegistrationRequest } from '../../services/organization.service';

@Component({
  selector: 'app-organization-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './organization-registration.component.html',
  styles: [`
    .organization-registration-wrapper { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 20px; }
    .registration-header { text-align: center; color: white; margin-bottom: 40px; }
    .registration-header h1 { font-size: 2.5rem; font-weight: 700; margin-bottom: 10px; }
    .registration-header p { font-size: 1.1rem; opacity: 0.9; }
    .registration-container { max-width: 700px; margin: 0 auto; background: white; border-radius: 12px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1); padding: 40px; }
    .progress-steps { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; }
    .step { display: flex; flex-direction: column; align-items: center; flex: 1; }
    .step-number { width: 40px; height: 40px; border-radius: 50%; background-color: #e9ecef; color: #495057; display: flex; align-items: center; justify-content: center; font-weight: 600; margin-bottom: 8px; transition: all 0.3s ease; }
    .step.active .step-number { background-color: #667eea; color: white; }
    .step.completed .step-number { background-color: #28a745; color: white; }
    .step-label { font-size: 0.85rem; color: #6c757d; text-align: center; }
    .step.active .step-label { color: #667eea; font-weight: 600; }
    .step-divider { flex: 1; height: 2px; background-color: #e9ecef; margin: 0 20px 24px 20px; transition: all 0.3s ease; }
    .step-divider.completed { background-color: #28a745; }
    .form-step { animation: fadeIn 0.3s ease-in; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
    .step-title { font-size: 1.5rem; font-weight: 600; margin-bottom: 25px; color: #333; }
    .form-group { margin-bottom: 20px; }
    .form-group label { font-weight: 500; color: #333; margin-bottom: 8px; display: block; }
    .required { color: #dc3545; }
    .form-control { padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 0.95rem; transition: all 0.3s ease; }
    .form-control:focus { border-color: #667eea; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1); }
    .form-control.is-invalid { border-color: #dc3545; }
    .invalid-feedback { color: #dc3545; font-size: 0.85rem; margin-top: 5px; display: block; }
    .sample-data-section { margin-top: 35px; padding: 20px; background-color: #f8f9fa; border-radius: 8px; border-left: 4px solid #667eea; }
    .section-subtitle { font-size: 1.1rem; font-weight: 600; color: #333; margin-bottom: 10px; }
    .section-description { color: #6c757d; font-size: 0.9rem; margin-bottom: 15px; }
    .sample-data-checkbox { margin-bottom: 15px; }
    .form-check-input { width: 20px; height: 20px; margin-top: 3px; cursor: pointer; }
    .form-check-label { margin-left: 8px; cursor: pointer; font-weight: 500; color: #333; }
    .sample-data-info { padding: 10px; background-color: #e7f3ff; border-radius: 6px; color: #004085; font-size: 0.9rem; }
    .form-navigation { display: flex; gap: 12px; margin-top: 30px; justify-content: space-between; }
    .btn { padding: 10px 24px; border-radius: 6px; font-weight: 500; transition: all 0.3s ease; cursor: pointer; border: none; }
    .btn-primary { background-color: #667eea; color: white; }
    .btn-primary:hover:not(:disabled) { background-color: #5568d3; transform: translateY(-2px); }
    .btn-success { background-color: #28a745; color: white; }
    .btn-success:hover:not(:disabled) { background-color: #218838; transform: translateY(-2px); }
    .btn-outline-secondary { border: 1px solid #ddd; background-color: white; color: #333; }
    .btn-outline-secondary:hover:not(:disabled) { background-color: #f8f9fa; }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .alert { padding: 15px; border-radius: 6px; display: flex; align-items: center; }
    .alert-danger { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    .alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
    .registration-footer { text-align: center; margin-top: 25px; padding-top: 20px; border-top: 1px solid #e9ecef; color: #6c757d; }
    .registration-footer a { color: #667eea; text-decoration: none; font-weight: 600; }
    .registration-footer a:hover { text-decoration: underline; }
    @media (max-width: 768px) {
      .registration-container { padding: 25px; }
      .registration-header h1 { font-size: 1.8rem; }
      .progress-steps { margin-bottom: 25px; }
      .step-divider { margin: 0 10px 24px 10px; }
      .form-navigation { flex-direction: column; }
      .btn { width: 100%; }
    }
  `]
})
export class OrganizationRegistrationComponent implements OnInit {
  registrationForm!: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';
  currentStep = 1; // Step 1: Basic Info, Step 2: Address, Step 3: Additional
  loadSampleData = true;
  currentYear = new Date().getFullYear();

  constructor(
    private fb: FormBuilder,
    private organizationService: OrganizationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
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
      fax: [''],
      website: ['', Validators.pattern(/^(https?:\/\/)?.+\..+/i)],

      // Address Information
      streetAddress: [''],
      city: ['', Validators.required],
      state: [''],
      postalCode: [''],
      country: ['', Validators.required],

      // Additional Information
      registrationNumber: [''],
      taxId: [''],
      establishedYear: [''],
      accreditation: [''],

      // Sample Data
      loadSampleData: [true]
    });
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
      return !!(form.get('name')?.valid && form.get('type')?.valid && 
             form.get('code')?.valid && form.get('email')?.valid &&
             form.get('phone')?.valid);
    } else if (step === 2) {
      return !!(form.get('city')?.valid && form.get('country')?.valid);
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

    const request: OrganizationRegistrationRequest = this.registrationForm.value;

    this.organizationService.registerOrganization(request).subscribe({
      next: (organization) => {
        this.loading = false;
        this.successMessage = `Organization "${organization.name}" registered successfully!`;
        
        // Redirect to dashboard after 1.5 seconds
        setTimeout(() => {
          this.router.navigate(['/dashboard'], {
            queryParams: { organizationId: organization.id, loadSampleData: this.loadSampleData }
          });
        }, 1500);
      },
      error: (error) => {
        this.loading = false;
        const errorMsg = error.error?.message || error.message || 'Registration failed. Please try again.';
        this.errorMessage = errorMsg;
        console.error('Registration error:', error);
      }
    });
  }

  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.currentStep === 3) {
      this.onRegister();
    }
  }

  get organizationTypes(): string[] {
    return ['School', 'College', 'University', 'Training Center', 'Other'];
  }

  get countries(): string[] {
    return ['USA', 'Canada', 'UK', 'India', 'Australia', 'Germany', 'France', 'Other'];
  }

  get usStates(): string[] {
    return ['AL', 'AK', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA', 'HI', 'ID', 
            'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MD', 'MA', 'MI', 'MN', 'MS', 
            'MO', 'MT', 'NE', 'NV', 'NH', 'NJ', 'NM', 'NY', 'NC', 'ND', 'OH', 'OK', 
            'OR', 'PA', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VT', 'VA', 'WA', 'WV', 
            'WI', 'WY'];
  }

  getErrorMessage(fieldName: string): string {
    const control = this.registrationForm.get(fieldName);
    if (!control || !control.errors) {
      return '';
    }

    if (control.errors['required']) {
      return `${this.formatFieldName(fieldName)} is required`;
    }
    if (control.errors['minlength']) {
      return `${this.formatFieldName(fieldName)} must be at least ${control.errors['minlength'].requiredLength} characters`;
    }
    if (control.errors['email']) {
      return 'Please enter a valid email address';
    }
    if (control.errors['pattern']) {
      return `Please enter a valid ${this.formatFieldName(fieldName).toLowerCase()}`;
    }
    return '';
  }

  private formatFieldName(fieldName: string): string {
    return fieldName
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, str => str.toUpperCase())
      .trim();
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.registrationForm.get(fieldName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
