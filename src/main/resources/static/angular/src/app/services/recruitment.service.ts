import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface JobPosting {
    id?: number;
    title: string;
    department: string;
    description?: string;
    requirements?: string;
    employmentType: 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'TEMPORARY' | 'INTERN';
    postedDate: string;
    closingDate?: string;
    status: 'DRAFT' | 'OPEN' | 'CLOSED' | 'ON_HOLD';
}

export interface JobApplication {
    id?: number;
    jobPostingId: number;
    candidateName: string;
    candidateEmail: string;
    candidatePhone?: string;
    resumeUrl?: string;
    appliedDate: string;
    status: 'NEW' | 'SCREENING' | 'INTERVIEW' | 'OFFER_EXTENDED' | 'HIRED' | 'REJECTED';
}

@Injectable({
    providedIn: 'root'
})
export class RecruitmentService {
    private apiUrl = '/api/hr/recruitment';

    constructor(private http: HttpClient) { }

    // Jobs
    getJobs(onlyOpen: boolean = false): Observable<JobPosting[]> {
        const params = new HttpParams().set('onlyOpen', onlyOpen);
        return this.http.get<JobPosting[]>(`${this.apiUrl}/jobs`, { params });
    }

    createJob(job: JobPosting): Observable<JobPosting> {
        return this.http.post<JobPosting>(`${this.apiUrl}/jobs`, job);
    }

    updateJobStatus(id: number, status: string): Observable<JobPosting> {
        const params = new HttpParams().set('status', status);
        return this.http.put<JobPosting>(`${this.apiUrl}/jobs/${id}/status`, {}, { params });
    }

    // Applications
    getApplications(jobId?: number): Observable<JobApplication[]> {
        let params = new HttpParams();
        if (jobId) params = params.set('jobId', jobId);
        return this.http.get<JobApplication[]>(`${this.apiUrl}/applications`, { params });
    }

    createApplication(app: JobApplication): Observable<JobApplication> {
        return this.http.post<JobApplication>(`${this.apiUrl}/applications`, app);
    }

    updateApplicationStatus(id: number, status: string): Observable<JobApplication> {
        const params = new HttpParams().set('status', status);
        return this.http.put<JobApplication>(`${this.apiUrl}/applications/${id}/status`, {}, { params });
    }
}
