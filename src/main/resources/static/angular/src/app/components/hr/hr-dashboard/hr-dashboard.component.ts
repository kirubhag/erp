import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-hr-dashboard',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './hr-dashboard.component.html',
    styleUrls: ['./hr-dashboard.component.css']
})
export class HrDashboardComponent implements OnInit {
    stats = {
        totalEmployees: 48,
        activeRecruitments: 5,
        pendingLeaves: 12,
        upcomingPayrolls: 1
    };

    quickLinks = [
        { label: 'Staff Directory', icon: 'fas fa-id-card', route: '/staff', color: 'bg-primary' },
        { label: 'Employee Self-Service', icon: 'fas fa-user-shield', route: '/setup/hr/self-service', color: 'bg-indigo' },
        { label: 'Leave Requests', icon: 'fas fa-calendar-minus', route: '/setup/hr/leave/requests', color: 'bg-warning' },
        { label: 'Payroll Management', icon: 'fas fa-file-invoice-dollar', route: '/setup/hr/payroll/list', color: 'bg-success' },
        { label: 'Recruitment', icon: 'fas fa-user-tie', route: '/setup/hr/recruitment/postings', color: 'bg-info' }
    ];

    constructor() { }

    ngOnInit(): void {
    }
}
