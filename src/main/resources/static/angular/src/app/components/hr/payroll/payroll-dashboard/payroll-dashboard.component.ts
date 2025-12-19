import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PayrollService, PayrollRun } from '../../../../services/payroll.service';

@Component({
    selector: 'app-payroll-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './payroll-dashboard.component.html',
    styleUrls: ['./payroll-dashboard.component.css']
})
export class PayrollDashboardComponent implements OnInit {
    runs: PayrollRun[] = [];
    isLoading = false;
    showCreateModal = false;

    newRunMonth: number;
    newRunYear: number;
    months = [
        { val: 1, name: 'January' }, { val: 2, name: 'February' }, { val: 3, name: 'March' },
        { val: 4, name: 'April' }, { val: 5, name: 'May' }, { val: 6, name: 'June' },
        { val: 7, name: 'July' }, { val: 8, name: 'August' }, { val: 9, name: 'September' },
        { val: 10, name: 'October' }, { val: 11, name: 'November' }, { val: 12, name: 'December' }
    ];

    constructor(private payrollService: PayrollService) {
        const today = new Date();
        this.newRunMonth = today.getMonth() + 1;
        this.newRunYear = today.getFullYear();
    }

    ngOnInit() {
        this.loadRuns();
    }

    loadRuns() {
        this.isLoading = true;
        this.payrollService.getRuns().subscribe({
            next: (data) => {
                this.runs = data.sort((a, b) => (b.year * 100 + b.month) - (a.year * 100 + a.month)); // Descending sort
                this.isLoading = false;
            },
            error: (err) => {
                console.error(err);
                this.isLoading = false;
            }
        });
    }

    createRun() {
        this.payrollService.initiateRun(this.newRunMonth, this.newRunYear).subscribe({
            next: (run) => {
                this.showCreateModal = false;
                this.loadRuns();
            },
            error: (err) => {
                alert('Error creating run: ' + (err.error?.message || err.message));
            }
        });
    }

    executeRun(run: PayrollRun) {
        if (confirm(`Process payroll for ${this.getMonthName(run.month)} ${run.year}?`)) {
            this.payrollService.executeRun(run.id!).subscribe(() => {
                this.loadRuns();
            });
        }
    }

    getMonthName(month: number): string {
        return this.months.find(m => m.val === month)?.name || 'Unknown';
    }
}
