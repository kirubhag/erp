import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecruitmentService, JobPosting } from '../../../../services/recruitment.service';

@Component({
    selector: 'app-job-posting-list',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './job-posting-list.component.html',
    styleUrls: ['./job-posting-list.component.css']
})
export class JobPostingListComponent implements OnInit {
    jobs: JobPosting[] = [];
    isLoading = false;
    showCreateModal = false;

    newJob: JobPosting = {
        title: '',
        department: '',
        employmentType: 'FULL_TIME',
        postedDate: new Date().toISOString().split('T')[0],
        status: 'OPEN'
    };

    constructor(private recruitmentService: RecruitmentService) { }

    ngOnInit() {
        this.loadJobs();
    }

    loadJobs() {
        this.isLoading = true;
        this.recruitmentService.getJobs().subscribe({
            next: (data) => {
                this.jobs = data;
                this.isLoading = false;
            },
            error: (err) => {
                console.error(err);
                this.isLoading = false;
            }
        });
    }

    saveJob() {
        this.recruitmentService.createJob(this.newJob).subscribe(() => {
            this.loadJobs();
            this.showCreateModal = false;
            // Reset
            this.newJob = {
                title: '',
                department: '',
                employmentType: 'FULL_TIME',
                postedDate: new Date().toISOString().split('T')[0],
                status: 'OPEN'
            };
        });
    }

    updateStatus(id: number | undefined, status: string) {
        if (!id) return;
        this.recruitmentService.updateJobStatus(id, status).subscribe(() => {
            this.loadJobs();
        });
    }
}
