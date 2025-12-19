import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PayrollRun {
    id?: number;
    month: number;
    year: number;
    processedDate?: string;
    status: 'DRAFT' | 'PROCESSED' | 'COMPLETED' | 'CANCELLED';
    totalPayout?: number;
}

export interface Payslip {
    id?: number;
    payrollRunId: number;
    staffId: number;
    staffName: string;
    department: string;
    basicSalary: number;
    hra: number;
    da: number;
    allowances: number;
    pfDeduction: number;
    taxDeduction: number;
    otherDeductions: number;
    grossSalary: number;
    totalDeductions: number;
    netSalary: number;
}

@Injectable({
    providedIn: 'root'
})
export class PayrollService {
    private apiUrl = '/api/hr/payroll';

    constructor(private http: HttpClient) { }

    getRuns(): Observable<PayrollRun[]> {
        return this.http.get<PayrollRun[]>(`${this.apiUrl}/runs`);
    }

    initiateRun(month: number, year: number): Observable<PayrollRun> {
        const params = new HttpParams().set('month', month).set('year', year);
        return this.http.post<PayrollRun>(`${this.apiUrl}/runs`, {}, { params });
    }

    executeRun(id: number): Observable<PayrollRun> {
        return this.http.post<PayrollRun>(`${this.apiUrl}/runs/${id}/execute`, {});
    }

    getPayslips(runId: number): Observable<Payslip[]> {
        return this.http.get<Payslip[]>(`${this.apiUrl}/runs/${runId}/payslips`);
    }
}
