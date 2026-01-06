import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';
  showPassword = false;
  showConfirmPassword = false;
  passwordStrength = 0;
  passwordStrengthLabel = '';
  registrationComplete = false;
  registeredEmail = '';

  // Field touched states for better UX
  fieldTouched = {
    organizationName: false,
    firstName: false,
    lastName: false,
    email: false,
    phone: false,
    password: false,
    confirmPassword: false
  };

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.initializeForm();

    // Don't auto-redirect - let users complete registration or navigate manually
    // The route guard will handle protection if needed

    // Watch password changes for strength meter
    this.registerForm.get('password')?.valueChanges.subscribe(password => {
      this.calculatePasswordStrength(password);
    });
  }

  initializeForm(): void {
    this.registerForm = this.formBuilder.group({
      organizationName: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100)
      ]],
      firstName: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z\s'-]+$/)
      ]],
      lastName: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z\s'-]+$/)
      ]],
      email: ['', [
        Validators.required,
        Validators.email,
        Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
      ]],
      phone: ['', [
        Validators.required,
        Validators.pattern(/^[0-9]{10,15}$/)
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        this.passwordStrengthValidator()
      ]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator()
    });
  }

  // Custom validator for password strength
  passwordStrengthValidator() {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;

      const hasUpperCase = /[A-Z]/.test(value);
      const hasLowerCase = /[a-z]/.test(value);
      const hasNumeric = /[0-9]/.test(value);
      const hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value);

      const strengthChecks = [hasUpperCase, hasLowerCase, hasNumeric, hasSpecialChar];
      const passedChecks = strengthChecks.filter(check => check).length;

      if (passedChecks < 3) {
        return { weakPassword: true };
      }

      return null;
    };
  }

  // Custom validator for password match
  passwordMatchValidator() {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const password = formGroup.get('password')?.value;
      const confirmPassword = formGroup.get('confirmPassword')?.value;

      if (!password || !confirmPassword) return null;

      return password === confirmPassword ? null : { passwordMismatch: true };
    };
  }

  calculatePasswordStrength(password: string): void {
    if (!password) {
      this.passwordStrength = 0;
      this.passwordStrengthLabel = '';
      return;
    }

    let strength = 0;

    // Length check
    if (password.length >= 8) strength += 20;
    if (password.length >= 12) strength += 10;

    // Character variety checks
    if (/[a-z]/.test(password)) strength += 20;
    if (/[A-Z]/.test(password)) strength += 20;
    if (/[0-9]/.test(password)) strength += 15;
    if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) strength += 15;

    this.passwordStrength = Math.min(strength, 100);

    if (this.passwordStrength < 40) {
      this.passwordStrengthLabel = 'Weak';
    } else if (this.passwordStrength < 70) {
      this.passwordStrengthLabel = 'Medium';
    } else {
      this.passwordStrengthLabel = 'Strong';
    }
  }

  togglePasswordVisibility(field: 'password' | 'confirmPassword'): void {
    if (field === 'password') {
      this.showPassword = !this.showPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  markFieldTouched(field: keyof typeof this.fieldTouched): void {
    this.fieldTouched[field] = true;
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched || this.fieldTouched[fieldName as keyof typeof this.fieldTouched]));
  }

  getFieldError(fieldName: string): string {
    const field = this.registerForm.get(fieldName);
    if (!field || !field.errors) return '';

    if (field.errors['required']) return `${this.getFieldLabel(fieldName)} is required`;
    if (field.errors['minlength']) return `${this.getFieldLabel(fieldName)} must be at least ${field.errors['minlength'].requiredLength} characters`;
    if (field.errors['maxlength']) return `${this.getFieldLabel(fieldName)} cannot exceed ${field.errors['maxlength'].requiredLength} characters`;
    if (field.errors['email']) return 'Please enter a valid email address';
    if (field.errors['pattern']) {
      if (fieldName === 'firstName' || fieldName === 'lastName') {
        return 'Only letters, spaces, hyphens and apostrophes allowed';
      }
      return 'Invalid format';
    }
    if (field.errors['weakPassword']) return 'Password must include uppercase, lowercase, number, and special character';

    return 'Invalid input';
  }

  getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      organizationName: 'Organization name',
      firstName: 'First name',
      lastName: 'Last name',
      email: 'Email',
      phone: 'Phone number',
      password: 'Password',
      confirmPassword: 'Confirm password'
    };
    return labels[fieldName] || fieldName;
  }

  getPasswordMismatchError(): string {
    return this.registerForm.errors?.['passwordMismatch'] ? 'Passwords do not match' : '';
  }

  // Helper methods for password requirements
  hasMinLength(): boolean {
    return this.registerForm.get('password')?.value?.length >= 8;
  }

  hasUpperCase(): boolean {
    return /[A-Z]/.test(this.registerForm.get('password')?.value || '');
  }

  hasLowerCase(): boolean {
    return /[a-z]/.test(this.registerForm.get('password')?.value || '');
  }

  hasNumber(): boolean {
    return /[0-9]/.test(this.registerForm.get('password')?.value || '');
  }

  hasSpecialChar(): boolean {
    return /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(this.registerForm.get('password')?.value || '');
  }

  onRegister(): void {
    // Mark all fields as touched to show validation errors
    Object.keys(this.fieldTouched).forEach(key => {
      this.fieldTouched[key as keyof typeof this.fieldTouched] = true;
    });
    this.registerForm.markAllAsTouched();

    if (this.registerForm.invalid) {
      this.errorMessage = 'Please correct the errors in the form';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.registerForm.value;
    const registrationData = {
      organizationName: formValue.organizationName.trim(),
      adminFirstName: formValue.firstName.trim(),
      adminLastName: formValue.lastName.trim(),
      adminEmail: formValue.email.trim().toLowerCase(),
      adminPassword: formValue.password,
      adminPhone: formValue.phone.trim()
    };

    this.authService.register(registrationData).subscribe({
      next: () => {
        this.loading = false;
        this.registrationComplete = true;
        this.registeredEmail = formValue.email.trim().toLowerCase();
        this.successMessage = 'Registration successful! A confirmation email has been sent to ' + this.registeredEmail + '. Please check your inbox and click the confirmation link to activate your account.';
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
        console.error('Registration error:', error);
      }
    });
  }
}
