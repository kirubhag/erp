import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

@Component({
    selector: 'app-account-closure',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule, SettingsSidebarComponent],
    templateUrl: './account-closure.component.html',
    styleUrls: ['./account-closure.component.css']
})
export class AccountClosureComponent {
    step = 1;
    reason = '';
    password = '';
    loading = false;
    errorMessage = '';
    showPassword = false;
    confirmDelete = false;

    constructor(
        private http: HttpClient,
        private router: Router,
        private authService: AuthService
    ) { }

    nextStep() {
        if (this.step < 3) {
            this.step++;
            this.errorMessage = '';
        }
    }

    prevStep() {
        if (this.step > 1) {
            this.step--;
            this.errorMessage = '';
        }
    }

    selectReason(reason: string) {
        this.reason = reason;
    }

    closeAccount() {
        if (!this.password || !this.confirmDelete) {
            return;
        }

        this.loading = true;
        this.errorMessage = '';

        this.http.post('/api/account/close', {
            password: this.password,
            reason: this.reason
        }).subscribe({
            next: () => {
                this.loading = false;
                // Logout and redirect
                this.authService.logout().subscribe(() => {
                    this.router.navigate(['/login']);
                });
            },
            error: (error) => {
                this.loading = false;
                this.errorMessage = error.error?.message || 'Failed to close account. Please check your password and try again.';
            }
        });
    }
}
