import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService, UserDetails } from '../../../services/auth.service';
import { LeaveService, LeaveRequest } from '../../../services/leave.service';

@Component({
    selector: 'app-employee-portal',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './employee-portal.component.html',
    styleUrls: ['./employee-portal.component.css']
})
export class EmployeePortalComponent implements OnInit {
    currentUser: UserDetails | null = null;
    myLeaves: LeaveRequest[] = [];
    stats = {
        pendingLeaves: 0,
        attendanceRate: 95, // Mock data
        totalHours: 160 // Mock data
    };

    constructor(
        private authService: AuthService,
        private leaveService: LeaveService
    ) { }

    ngOnInit(): void {
        this.currentUser = this.authService.getCurrentUser();
        if (this.currentUser?.staffId) {
            this.loadMyLeaves(this.currentUser.staffId);
        }
    }

    loadMyLeaves(staffId: number): void {
        this.leaveService.getMyLeaves(staffId).subscribe({
            next: (leaves) => {
                this.myLeaves = leaves;
                this.stats.pendingLeaves = leaves.filter(l => l.status === 'PENDING').length;
            },
            error: (err) => console.error('Error loading leaves', err)
        });
    }

    getStatusClass(status: string): string {
        switch (status) {
            case 'APPROVED': return 'badge bg-success';
            case 'PENDING': return 'badge bg-warning text-dark';
            case 'REJECTED': return 'badge bg-danger';
            default: return 'badge bg-secondary';
        }
    }
}
