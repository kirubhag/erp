import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
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

  private userSubscription: Subscription | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    // Check for error messages from query params (e.g., session expired, tenant DB not found)
    this.route.queryParams.subscribe(params => {
      if (params['error']) {
        this.errorMessage = params['message'] || this.getErrorMessageByCode(params['error']);
      }
    });

    // If user is already logged in, redirect to dashboard
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      if (user && user.id) {
        // User is already logged in, redirect to dashboard
        console.log('User already logged in, redirecting to dashboard');
        this.router.navigate(['/dashboard']);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  /**
   * Get user-friendly error message from error code
   */
  private getErrorMessageByCode(errorCode: string): string {
    const errorMessages: { [key: string]: string } = {
      'tenant_db_not_found': 'Your session has expired or your account is no longer active. Please login again.',
      'session_expired': 'Your session has expired. Please login again.',
      'unauthorized': 'You are not authorized to access this resource. Please login.',
      'account_disabled': 'Your account has been disabled. Please contact support.'
    };
    return errorMessages[errorCode] || 'An error occurred. Please try again.';
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
