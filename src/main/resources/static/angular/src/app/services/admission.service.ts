import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdmissionInquiry {
    id?: number;
    firstName: string;
    lastName: string;
    email?: string;
    phone?: string;
    gradeInterested: string;
    status: 'NEW' | 'CONTACTED' | 'VISITED' | 'APPLICATION_FORM_ISSUED' | 'APPROVED' | 'REJECTED' | 'CLOSED';
    inquiryDate: string;
    source?: string;
    notes?: string;
}

export interface AdmissionApplication {
    id?: number;
    applicationNumber: string;
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    gradeApplied: string;
    status: 'SUBMITTED' | 'UNDER_REVIEW' | 'INTERVIEW_SCHEDULED' | 'SELECTED' | 'REJECTED' | 'ADMITTED';
    applicationDate: string;
    parentName?: string;
    parentEmail?: string;
    parentPhone?: string;
}

export interface AdmissionCycle {
    id?: number;
    name: string;
    startDate: string;
    endDate: string;
    academicYearId: number;
    description?: string;
}

export interface AdmissionSeatAllocation {
    id?: number;
    admissionCycleId: number;
    gradeLevel: string;
    totalSeats: number;
    occupiedSeats: number;
    waitlistedSeats: number;
}

@Injectable({
    providedIn: 'root'
})
export class AdmissionService {
    private apiUrl = '/api/admission';

    constructor(private http: HttpClient) { }

    // Inquiries
    getInquiries(): Observable<AdmissionInquiry[]> {
        return this.http.get<AdmissionInquiry[]>(`${this.apiUrl}/inquiries`);
    }

    createInquiry(inquiry: AdmissionInquiry): Observable<AdmissionInquiry> {
        return this.http.post<AdmissionInquiry>(`${this.apiUrl}/inquiries`, inquiry);
    }

    updateInquiryStatus(id: number, status: string): Observable<AdmissionInquiry> {
        const params = new HttpParams().set('status', status);
        return this.http.put<AdmissionInquiry>(`${this.apiUrl}/inquiries/${id}/status`, {}, { params });
    }

    // Applications
    getApplications(): Observable<AdmissionApplication[]> {
        return this.http.get<AdmissionApplication[]>(`${this.apiUrl}/applications`);
    }

    createApplication(application: AdmissionApplication): Observable<AdmissionApplication> {
        return this.http.post<AdmissionApplication>(`${this.apiUrl}/applications`, application);
    }

    updateApplicationStatus(id: number, status: string): Observable<AdmissionApplication> {
        const params = new HttpParams().set('status', status);
        return this.http.put<AdmissionApplication>(`${this.apiUrl}/applications/${id}/status`, {}, { params });
    }

    // Cycles
    createCycle(cycle: AdmissionCycle): Observable<AdmissionCycle> {
        return this.http.post<AdmissionCycle>(`${this.apiUrl}/cycles`, cycle);
    }

    getAllCycles(): Observable<AdmissionCycle[]> {
        return this.http.get<AdmissionCycle[]>(`${this.apiUrl}/cycles`);
    }

    // Allocations
    createAllocation(allocation: AdmissionSeatAllocation): Observable<AdmissionSeatAllocation> {
        return this.http.post<AdmissionSeatAllocation>(`${this.apiUrl}/allocations`, allocation);
    }
}
