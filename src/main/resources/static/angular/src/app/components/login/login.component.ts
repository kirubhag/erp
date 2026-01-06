import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  username = '';
  password = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  // Forgot Password
  showForgotPasswordModal = false;
  resetEmail = '';
  forgotPasswordSuccess = '';
  forgotPasswordError = '';

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    // Don't auto-redirect - allow users to access login page
    // Successful login will redirect via onLogin method
  }

  onLogin(): void {
    if (!this.username || !this.password) {
      this.errorMessage = 'Please fill in all fields';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Try to login with username/email and password
    this.authService.login(this.username, this.password).subscribe({
      next: (response: any) => {
        this.successMessage = 'Login successful! Redirecting...';
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1000);
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Login failed. Please check your credentials.';
        console.error('Login error:', error);
      }
    });
  }

  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.onLogin();
    }
  }

  // Forgot Password Methods

  closeForgotPasswordModal(): void {
    this.showForgotPasswordModal = false;
    this.resetEmail = '';
    this.forgotPasswordSuccess = '';
    this.forgotPasswordError = '';
  }

  onForgotPassword(): void {
    if (!this.resetEmail) {
      this.forgotPasswordError = 'Please enter your email';
      return;
    }

    this.loading = true; // Use shared loading or separate
    this.forgotPasswordError = '';
    this.forgotPasswordSuccess = '';

    this.authService.forgotPassword(this.resetEmail).subscribe({
      next: (response: any) => {
        this.loading = false;
        this.forgotPasswordSuccess = response.message || 'Reset link sent to your email';
      },
      error: (error: any) => {
        this.loading = false;
        this.forgotPasswordError = error.error?.message || 'Failed to send reset link';
        console.error('Forgot password error:', error);
      }
    });
  }
}
