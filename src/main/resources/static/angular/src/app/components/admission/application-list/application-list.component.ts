import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdmissionService, AdmissionApplication } from '../../../services/admission.service';

@Component({
    selector: 'app-application-list',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './application-list.component.html',
    styleUrls: ['./application-list.component.css']
})
export class ApplicationListComponent implements OnInit {
    applications: AdmissionApplication[] = [];
    filteredApplications: AdmissionApplication[] = [];
    isLoading = false;
    searchTerm: string = '';
    selectedApplication: AdmissionApplication | null = null;

    constructor(private admissionService: AdmissionService) { }

    ngOnInit() {
        this.loadApplications();
    }

    loadApplications() {
        this.isLoading = true;
        this.admissionService.getApplications().subscribe({
            next: (data) => {
                this.applications = data;
                this.filteredApplications = data;
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Failed to load applications', err);
                this.isLoading = false;
            }
        });
    }

    onSearch() {
        if (!this.searchTerm) {
            this.filteredApplications = this.applications;
        } else {
            const term = this.searchTerm.toLowerCase();
            this.filteredApplications = this.applications.filter(app =>
                app.applicationNumber.toLowerCase().includes(term) ||
                app.firstName.toLowerCase().includes(term) ||
                app.lastName.toLowerCase().includes(term) ||
                app.gradeApplied.toLowerCase().includes(term)
            );
        }
    }

    openQuickView(app: AdmissionApplication) {
        this.selectedApplication = app;
    }

    closeQuickView() {
        this.selectedApplication = null;
    }
}
