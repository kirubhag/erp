import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LeaveType {
    id?: number;
    name: string;
    code: string;
    daysAllowed: number;
    isCarryForward: boolean;
    description?: string;
}

export interface LeaveRequest {
    id?: number;
    staffId: number;
    leaveTypeId: number;
    startDate: string;
    endDate: string;
    reason: string;
    status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
    rejectionReason?: string;
}

@Injectable({
    providedIn: 'root'
})
export class LeaveService {
    private apiUrl = '/api/hr/leaves';
    private typeUrl = '/api/entity/LEAVE_TYPE'; // Using generic entity API for types

    constructor(private http: HttpClient) { }

    getLeaveTypes(): Observable<LeaveType[]> {
        return this.http.get<LeaveType[]>(this.typeUrl);
    }

    getMyLeaves(staffId: number): Observable<LeaveRequest[]> {
        return this.http.get<LeaveRequest[]>(`${this.apiUrl}/staff/${staffId}`);
    }

    getPendingLeaves(): Observable<LeaveRequest[]> {
        return this.http.get<LeaveRequest[]>(`${this.apiUrl}/pending`);
    }

    applyForLeave(data: any): Observable<LeaveRequest> {
        return this.http.post<LeaveRequest>(`${this.apiUrl}/apply`, data);
    }

    approveLeave(id: number): Observable<LeaveRequest> {
        return this.http.post<LeaveRequest>(`${this.apiUrl}/${id}/approve`, {});
    }

    rejectLeave(id: number, reason: string): Observable<LeaveRequest> {
        return this.http.post<LeaveRequest>(`${this.apiUrl}/${id}/reject`, { reason });
    }
}
