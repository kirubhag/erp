import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-confirm-account',
    standalone: true,
    imports: [CommonModule, RouterModule],
    template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card shadow-sm">
            <div class="card-body text-center p-5">
              
              <div *ngIf="loading" class="spinner-border text-primary mb-3" role="status">
                <span class="sr-only">Loading...</span>
              </div>

              <h3 *ngIf="loading">Verifying your account...</h3>

              <div *ngIf="!loading && success">
                <i class="fas fa-check-circle text-success fa-3x mb-3"></i>
                <h3 class="text-success mb-3">Account Confirmed!</h3>
                <p class="mb-4">{{ message }}</p>
                <p class="text-muted mb-3">Redirecting to login page...</p>
                <a routerLink="/login" class="btn btn-primary btn-lg">Login Now</a>
              </div>

              <div *ngIf="!loading && !success">
                <i class="fas fa-times-circle text-danger fa-3x mb-3"></i>
                <h3 class="text-danger mb-3">Confirmation Failed</h3>
                <p class="mb-4">{{ message }}</p>
                <a routerLink="/login" class="btn btn-secondary">Back to Login</a>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .card { border-radius: 10px; border: none; }
  `]
})
export class ConfirmAccountComponent implements OnInit {
    loading = true;
    success = false;
    message = '';

    constructor(
        private route: ActivatedRoute,
        private http: HttpClient,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            const token = params['token'];
            if (token) {
                this.confirmAccount(token);
            } else {
                this.loading = false;
                this.success = false;
                this.message = 'No confirmation token found.';
            }
        });
    }

    confirmAccount(token: string): void {
        this.http.get<any>(`/api/auth/confirm-account?token=${token}`).subscribe({
            next: (response) => {
                this.loading = false;
                this.success = true;
                this.message = response.message || 'Your account has been successfully confirmed.';
                // Redirect to login page after 3 seconds
                setTimeout(() => {
                    this.router.navigate(['/login']);
                }, 3000);
            },
            error: (error) => {
                this.loading = false;
                this.success = false;
                this.message = error.error?.message || 'Invalid or expired confirmation token.';
            }
        });
    }
}
