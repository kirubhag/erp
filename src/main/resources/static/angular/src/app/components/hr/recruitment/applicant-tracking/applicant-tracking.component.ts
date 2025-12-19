import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecruitmentService, JobApplication, JobPosting } from '../../../../services/recruitment.service';

@Component({
    selector: 'app-applicant-tracking',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './applicant-tracking.component.html',
    styleUrls: ['./applicant-tracking.component.css']
})
export class ApplicantTrackingComponent implements OnInit {
    applications: JobApplication[] = [];
    jobs: JobPosting[] = [];
    isLoading = false;
    selectedJobId: number | null = null;
    selectedApplication: JobApplication | null = null;

    constructor(private recruitmentService: RecruitmentService) { }

    ngOnInit() {
        this.loadJobs();
        this.loadApplications();
    }

    loadJobs() {
        this.recruitmentService.getJobs().subscribe(data => this.jobs = data);
    }

    loadApplications() {
        this.isLoading = true;
        this.recruitmentService.getApplications(this.selectedJobId || undefined).subscribe({
            next: (data) => {
                this.applications = data;
                this.isLoading = false;
            },
            error: (err) => {
                console.error(err);
                this.isLoading = false;
            }
        });
    }

    onJobFilterChange() {
        this.loadApplications();
    }

    updateStatus(id: number | undefined, status: string) {
        if (!id) return;
        this.recruitmentService.updateApplicationStatus(id, status).subscribe(() => {
            this.loadApplications();
        });
    }

    viewApplication(app: JobApplication) {
        this.selectedApplication = app;
    }
}
