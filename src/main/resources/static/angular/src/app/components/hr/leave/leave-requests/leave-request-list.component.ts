import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LeaveService, LeaveRequest, LeaveType } from '../../../../services/leave.service';
import { AuthService } from '../../../../services/auth.service';

@Component({
    selector: 'app-leave-request-list',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './leave-request-list.component.html'
})
export class LeaveRequestListComponent implements OnInit {
    activeTab: 'my-leaves' | 'pending' = 'my-leaves';
    myLeaves: LeaveRequest[] = [];
    pendingLeaves: LeaveRequest[] = [];
    leaveTypes: LeaveType[] = [];

    showApplyModal = false;
    newRequest: Partial<LeaveRequest> = { staffId: 0, leaveTypeId: 0, reason: '', status: 'PENDING' };

    constructor(
        private leaveService: LeaveService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        const user = this.authService.getCurrentUser();
        if (user && user.staffId) {
            this.newRequest.staffId = user.staffId;
            this.loadMyLeaves(user.staffId);
        }
        this.loadTypes();
    }

    loadMyLeaves(staffId?: number) {
        const id = staffId || this.authService.getCurrentUser()?.staffId;
        if (id) {
            this.leaveService.getMyLeaves(id).subscribe(data => this.myLeaves = data);
        }
    }

    loadPending() {
        this.leaveService.getPendingLeaves().subscribe(data => this.pendingLeaves = data);
    }

    loadTypes() {
        this.leaveService.getLeaveTypes().subscribe(data => this.leaveTypes = data);
    }

    switchTab(tab: 'my-leaves' | 'pending') {
        this.activeTab = tab;
        if (tab === 'pending') this.loadPending();
        else this.loadMyLeaves();
    }

    applyLeave() {
        this.leaveService.applyForLeave(this.newRequest).subscribe({
            next: () => {
                this.showApplyModal = false;
                const user = this.authService.getCurrentUser();
                if (user && user.staffId) {
                    this.loadMyLeaves(user.staffId);
                    this.newRequest = { staffId: user.staffId };
                }
            },
            error: (err) => alert('Error: ' + (err.error?.error || err.message))
        });
    }

    approve(id: number) {
        if (confirm('Approve this leave request?')) {
            this.leaveService.approveLeave(id).subscribe(() => this.loadPending());
        }
    }

    reject(id: number) {
        const reason = prompt('Enter rejection reason:');
        if (reason) {
            this.leaveService.rejectLeave(id, reason).subscribe(() => this.loadPending());
        }
    }

    getTypeName(id: number) {
        return this.leaveTypes.find(t => t.id === id)?.name || id;
    }
}
