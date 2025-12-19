import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FinanceService, ProfitAndLoss } from '../../../../services/finance.service';

@Component({
    selector: 'app-profit-loss',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './profit-loss.component.html',
    styleUrls: ['./profit-loss.component.css']
})
export class ProfitLossComponent implements OnInit {
    profitAndLoss: ProfitAndLoss | null = null;
    revenueCategories: { name: string, amount: number }[] = [];
    expenseCategories: { name: string, amount: number }[] = [];
    loading = true;
    error: string | null = null;

    constructor(private financeService: FinanceService) { }

    ngOnInit(): void {
        this.loadProfitAndLoss();
    }

    loadProfitAndLoss(): void {
        this.loading = true;
        this.financeService.getProfitAndLoss().subscribe({
            next: (data) => {
                this.profitAndLoss = data;
                this.processCategories(data);
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading P&L:', err);
                this.error = 'Failed to load Profit & Loss. Please ensure revenue and expense accounts have posted entries.';
                this.loading = false;
            }
        });
    }

    processCategories(data: ProfitAndLoss): void {
        this.revenueCategories = [];
        this.expenseCategories = [];

        Object.keys(data).forEach(key => {
            if (key !== 'TOTAL_REVENUE' && key !== 'TOTAL_EXPENSE' && key !== 'NET_PROFIT') {
                // Simple logic: if it's not a total, it's a category
                // In a real app we'd get metadata about account types
                const val = data[key];
                if (key.toLowerCase().includes('income') || key.toLowerCase().includes('revenue')) {
                    this.revenueCategories.push({ name: key, amount: val });
                } else {
                    this.expenseCategories.push({ name: key, amount: val });
                }
            }
        });
    }
}
