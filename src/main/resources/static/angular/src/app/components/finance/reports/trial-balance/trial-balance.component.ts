import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FinanceService, TrialBalance } from '../../../../services/finance.service';

@Component({
    selector: 'app-trial-balance',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './trial-balance.component.html',
    styleUrls: ['./trial-balance.component.css']
})
export class TrialBalanceComponent implements OnInit {
    trialBalance: TrialBalance = {};
    accounts: string[] = [];
    loading = true;
    error: string | null = null;

    constructor(private financeService: FinanceService) { }

    ngOnInit(): void {
        this.loadTrialBalance();
    }

    loadTrialBalance(): void {
        this.loading = true;
        this.financeService.getTrialBalance().subscribe({
            next: (data) => {
                this.trialBalance = data;
                this.accounts = Object.keys(data);
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading trial balance:', err);
                this.error = 'Failed to load trial balance. Please ensure data is posted.';
                this.loading = false;
            }
        });
    }

    getTotal(): number {
        // In a balanced TB, sum of debits - sum of credits should be handled by the service
        // Here we just show the net balance per account
        return Object.values(this.trialBalance).reduce((sum, val) => sum + val, 0);
    }
}
