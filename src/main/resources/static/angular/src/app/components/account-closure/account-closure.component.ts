import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-account-closure',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
    <div class="container mx-auto px-4 py-8 max-w-2xl">
      <div class="bg-white rounded-lg shadow-lg overflow-hidden">
        <div class="bg-red-600 px-6 py-4">
          <h2 class="text-xl font-bold text-white">Close Account</h2>
        </div>
        
        <div class="p-6">
          <!-- Step 1: Warning -->
          <div *ngIf="step === 1">
            <div class="bg-red-50 border-l-4 border-red-500 p-4 mb-6">
              <div class="flex">
                <div class="flex-shrink-0">
                  <svg class="h-5 w-5 text-red-500" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="ml-3">
                  <h3 class="text-sm leading-5 font-medium text-red-800">
                    Warning: This action is permanent
                  </h3>
                  <div class="mt-2 text-sm leading-5 text-red-700">
                    <p>
                      Closing your account will permanently delete all your data, including:
                    </p>
                    <ul class="list-disc list-inside mt-1">
                      <li>All organization data</li>
                      <li>All user accounts</li>
                      <li>All student and staff records</li>
                      <li>All uploaded files</li>
                    </ul>
                    <p class="mt-2 font-bold">
                      This action cannot be undone.
                    </p>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="flex justify-end">
              <button (click)="nextStep()" class="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">
                I Understand, Continue
              </button>
            </div>
          </div>

          <!-- Step 2: Reason -->
          <div *ngIf="step === 2">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Why are you leaving?</h3>
            <div class="mb-4">
              <label class="block text-gray-700 text-sm font-bold mb-2" for="reason">
                Reason for closing account
              </label>
              <textarea 
                [(ngModel)]="reason" 
                id="reason" 
                rows="4" 
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                placeholder="Please tell us why you are closing your account..."></textarea>
            </div>
            
            <div class="flex justify-between">
              <button (click)="prevStep()" class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">
                Back
              </button>
              <button (click)="nextStep()" [disabled]="!reason" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:opacity-50">
                Continue
              </button>
            </div>
          </div>

          <!-- Step 3: Confirmation -->
          <div *ngIf="step === 3">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Confirm Account Closure</h3>
            <p class="text-gray-600 mb-4">
              Please enter your password to confirm that you want to permanently close your account.
            </p>
            
            <div class="mb-6">
              <label class="block text-gray-700 text-sm font-bold mb-2" for="password">
                Password
              </label>
              <input 
                [(ngModel)]="password" 
                type="password" 
                id="password" 
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
                placeholder="Enter your password">
            </div>

            <div *ngIf="errorMessage" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
              <span class="block sm:inline">{{ errorMessage }}</span>
            </div>
            
            <div class="flex justify-between">
              <button (click)="prevStep()" class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">
                Back
              </button>
              <button 
                (click)="closeAccount()" 
                [disabled]="!password || loading" 
                class="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline flex items-center disabled:opacity-50">
                <span *ngIf="loading" class="mr-2">
                  <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                </span>
                Permanently Close Account
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class AccountClosureComponent {
    step = 1;
    reason = '';
    password = '';
    loading = false;
    errorMessage = '';

    constructor(
        private http: HttpClient,
        private router: Router,
        private authService: AuthService
    ) { }

    nextStep() {
        this.step++;
    }

    prevStep() {
        this.step--;
    }

    closeAccount() {
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
