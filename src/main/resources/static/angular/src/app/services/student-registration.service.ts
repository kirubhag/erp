import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Student {
    id: number;
    firstName?: string;
    lastName?: string;
    studentId?: string;
}

export interface AcademicYear {
    id: number;
    name?: string;
}

export interface ErpClass {
    id: number;
    className?: string;
    section?: string;
}

export interface StudentRegistration {
    id?: number;
    student?: Student;
    academicYear?: AcademicYear;
    erpClass?: ErpClass;
    registrationDate?: string;
    status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED';
    remarks?: string;
}

@Injectable({
    providedIn: 'root'
})
export class StudentRegistrationService {
    private apiUrl = '/api/admission/registrations';

    constructor(private http: HttpClient) { }

    getAllRegistrations(): Observable<StudentRegistration[]> {
        return this.http.get<StudentRegistration[]>(this.apiUrl);
    }

    getRegistrationById(id: number): Observable<StudentRegistration> {
        return this.http.get<StudentRegistration>(`${this.apiUrl}/${id}`);
    }

    getRegistrationsByStudent(studentId: number): Observable<StudentRegistration[]> {
        return this.http.get<StudentRegistration[]>(`${this.apiUrl}/student/${studentId}`);
    }

    createRegistration(registration: StudentRegistration): Observable<StudentRegistration> {
        return this.http.post<StudentRegistration>(this.apiUrl, registration);
    }

    updateRegistration(id: number, registration: StudentRegistration): Observable<StudentRegistration> {
        return this.http.put<StudentRegistration>(`${this.apiUrl}/${id}`, registration);
    }

    deleteRegistration(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
