import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { PayrollService, Payslip } from '../../../../services/payroll.service';

@Component({
    selector: 'app-payslip-view',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './payslip-view.component.html',
    styleUrls: ['./payslip-view.component.css']
})
export class PayslipViewComponent implements OnInit {
    payslips: Payslip[] = [];
    runId: number | null = null;
    isLoading = false;
    selectedPayslip: Payslip | null = null;

    constructor(private route: ActivatedRoute, private payrollService: PayrollService) { }

    ngOnInit() {
        this.route.paramMap.subscribe(params => {
            const id = params.get('id');
            if (id) {
                this.runId = +id;
                this.loadPayslips();
            }
        });
    }

    loadPayslips() {
        if (!this.runId) return;
        this.isLoading = true;
        this.payrollService.getPayslips(this.runId).subscribe({
            next: (data) => {
                this.payslips = data;
                this.isLoading = false;
            },
            error: (err) => {
                console.error(err);
                this.isLoading = false;
            }
        });
    }

    viewPayslip(p: Payslip) {
        this.selectedPayslip = p;
    }

    printPayslip() {
        window.print();
    }
}
