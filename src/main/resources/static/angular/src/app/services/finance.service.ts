import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TrialBalance {
    [accountName: string]: number;
}

export interface ProfitAndLoss {
    [key: string]: number;
    TOTAL_REVENUE: number;
    TOTAL_EXPENSE: number;
    NET_PROFIT: number;
}

@Injectable({
    providedIn: 'root'
})
export class FinanceService {
    private apiUrl = '/api/finance';

    constructor(private http: HttpClient) { }

    getTrialBalance(): Observable<TrialBalance> {
        return this.http.get<TrialBalance>(`${this.apiUrl}/trial-balance`);
    }

    getProfitAndLoss(): Observable<ProfitAndLoss> {
        return this.http.get<ProfitAndLoss>(`${this.apiUrl}/profit-loss`);
    }

    postJournalEntry(id: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/journal-entry/${id}/post`, {});
    }
}
