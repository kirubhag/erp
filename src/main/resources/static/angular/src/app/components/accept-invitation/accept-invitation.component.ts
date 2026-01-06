import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-accept-invitation',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './accept-invitation.component.html',
  styleUrls: ['./accept-invitation.component.css']
})
export class AcceptInvitationComponent implements OnInit {
  token: string = '';
  loading = true;
  validating = true;
  submitting = false;
  
  tokenValid = false;
  tokenError = '';
  
  userEmail = '';
  userFirstName = '';
  userLastName = '';
  
  passwordForm: FormGroup;
  successMessage = '';
  errorMessage = '';
  
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder
  ) {
    this.passwordForm = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
      
      if (!this.token) {
        this.tokenError = 'No invitation token provided.';
        this.loading = false;
        this.validating = false;
        return;
      }
      
      this.validateToken();
    });
  }

  validateToken(): void {
    this.validating = true;
    this.http.get<any>(`/api/iam/validate-invitation/${this.token}`).subscribe({
      next: (response) => {
        this.validating = false;
        this.loading = false;
        
        if (response.valid) {
          this.tokenValid = true;
          this.userEmail = response.email;
          this.userFirstName = response.firstName || '';
          this.userLastName = response.lastName || '';
        } else {
          this.tokenValid = false;
          this.tokenError = response.message || 'Invalid invitation token.';
        }
      },
      error: (error) => {
        this.validating = false;
        this.loading = false;
        this.tokenValid = false;
        this.tokenError = error.error?.message || 'Invalid or expired invitation token.';
      }
    });
  }

  passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password');
    const confirmPassword = group.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.errorMessage = '';

    const payload = {
      token: this.token,
      password: this.passwordForm.get('password')?.value
    };

    this.http.post<any>('/api/iam/accept-invitation', payload).subscribe({
      next: (response) => {
        this.submitting = false;
        this.successMessage = response.message || 'Password set successfully!';
        
        // Redirect to login after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/login'], { 
            queryParams: { message: 'Your account is ready. Please login.' }
          });
        }, 2000);
      },
      error: (error) => {
        this.submitting = false;
        this.errorMessage = error.error?.message || 'Failed to set password. Please try again.';
      }
    });
  }

  getPasswordStrength(): { label: string; class: string; width: string } {
    const password = this.passwordForm.get('password')?.value || '';
    
    if (password.length === 0) {
      return { label: '', class: '', width: '0%' };
    }
    
    let strength = 0;
    
    // Length check
    if (password.length >= 6) strength += 1;
    if (password.length >= 8) strength += 1;
    if (password.length >= 12) strength += 1;
    
    // Character variety
    if (/[a-z]/.test(password)) strength += 1;
    if (/[A-Z]/.test(password)) strength += 1;
    if (/[0-9]/.test(password)) strength += 1;
    if (/[^a-zA-Z0-9]/.test(password)) strength += 1;
    
    if (strength <= 2) {
      return { label: 'Weak', class: 'bg-danger', width: '33%' };
    } else if (strength <= 4) {
      return { label: 'Medium', class: 'bg-warning', width: '66%' };
    } else {
      return { label: 'Strong', class: 'bg-success', width: '100%' };
    }
  }

  // Helper methods for template - Angular templates don't support regex literals
  hasMinLength(): boolean {
    const password = this.passwordForm.get('password')?.value;
    return password && password.length >= 6;
  }

  hasUppercase(): boolean {
    const password = this.passwordForm.get('password')?.value;
    return password && /[A-Z]/.test(password);
  }

  hasNumber(): boolean {
    const password = this.passwordForm.get('password')?.value;
    return password && /[0-9]/.test(password);
  }
}
