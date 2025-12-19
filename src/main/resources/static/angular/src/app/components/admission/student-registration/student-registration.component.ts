import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { StudentRegistrationService, StudentRegistration, Student, AcademicYear, ErpClass } from '../../../services/student-registration.service';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-student-registration',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
    templateUrl: './student-registration.component.html',
    styleUrls: ['./student-registration.component.css']
})
export class StudentRegistrationComponent implements OnInit {
    registrationForm: FormGroup;
    registrations: StudentRegistration[] = [];
    students: Student[] = [];
    academicYears: AcademicYear[] = [];
    classes: ErpClass[] = [];

    showModal = false;
    isEditing = false;
    selectedId: number | null = null;

    successMessage = '';
    errorMessage = '';

    constructor(
        private fb: FormBuilder,
        private registrationService: StudentRegistrationService,
        private http: HttpClient
    ) {
        this.registrationForm = this.fb.group({
            studentId: ['', Validators.required],
            academicYearId: ['', Validators.required],
            classId: ['', Validators.required],
            registrationDate: [new Date().toISOString().split('T')[0], Validators.required],
            status: ['PENDING', Validators.required],
            remarks: ['']
        });
    }

    ngOnInit(): void {
        this.loadRegistrations();
        this.loadStudents();
        this.loadAcademicYears();
        this.loadClasses();
    }

    loadRegistrations(): void {
        this.registrationService.getAllRegistrations().subscribe({
            next: (data) => this.registrations = data,
            error: (err) => this.showError('Failed to load registrations')
        });
    }

    loadStudents(): void {
        this.http.get<Student[]>('/api/students/active').subscribe({
            next: (data) => this.students = data,
            error: (err) => this.showError('Failed to load students')
        });
    }

    loadAcademicYears(): void {
        this.http.get<AcademicYear[]>('/api/academic/years').subscribe({
            next: (data) => this.academicYears = data,
            error: (err) => this.showError('Failed to load academic years')
        });
    }

    loadClasses(): void {
        this.http.get<any>('/api/classes').subscribe({
            next: (data) => this.classes = data.content || data,
            error: (err) => this.showError('Failed to load classes')
        });
    }

    openModal(registration?: StudentRegistration): void {
        this.showModal = true;
        if (registration) {
            this.isEditing = true;
            this.selectedId = registration.id || null;
            this.registrationForm.patchValue({
                studentId: registration.student?.id,
                academicYearId: registration.academicYear?.id,
                classId: registration.erpClass?.id,
                registrationDate: registration.registrationDate,
                status: registration.status,
                remarks: registration.remarks
            });
        } else {
            this.isEditing = false;
            this.selectedId = null;
            this.registrationForm.reset({
                registrationDate: new Date().toISOString().split('T')[0],
                status: 'PENDING'
            });
        }
    }

    closeModal(): void {
        this.showModal = false;
    }

    saveRegistration(): void {
        if (this.registrationForm.invalid) return;

        const formData = this.registrationForm.value;
        const registration: StudentRegistration = {
            student: { id: Number(formData.studentId) },
            academicYear: { id: Number(formData.academicYearId) },
            erpClass: { id: Number(formData.classId) },
            registrationDate: formData.registrationDate,
            status: formData.status,
            remarks: formData.remarks
        };

        if (this.isEditing && this.selectedId) {
            this.registrationService.updateRegistration(this.selectedId, registration).subscribe({
                next: () => {
                    this.showSuccess('Registration updated successfully');
                    this.loadRegistrations();
                    this.closeModal();
                },
                error: () => this.showError('Failed to update registration')
            });
        } else {
            this.registrationService.createRegistration(registration).subscribe({
                next: () => {
                    this.showSuccess('Registration created successfully');
                    this.loadRegistrations();
                    this.closeModal();
                },
                error: () => this.showError('Failed to create registration')
            });
        }
    }

    deleteRegistration(id: number): void {
        if (confirm('Are you sure you want to delete this registration?')) {
            this.registrationService.deleteRegistration(id).subscribe({
                next: () => {
                    this.showSuccess('Registration deleted successfully');
                    this.loadRegistrations();
                },
                error: () => this.showError('Failed to delete registration')
            });
        }
    }

    showSuccess(msg: string): void {
        this.successMessage = msg;
        setTimeout(() => this.successMessage = '', 3000);
    }

    showError(msg: string): void {
        this.errorMessage = msg;
        setTimeout(() => this.errorMessage = '', 3000);
    }
}
