import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LeaveService, LeaveRequest, LeaveType } from '../../../../services/leave.service';

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
    newRequest: any = { staffId: 1 }; // Default staff 1 for demo

    constructor(private leaveService: LeaveService) { }

    ngOnInit() {
        this.loadMyLeaves();
        this.loadTypes();
    }

    loadMyLeaves() {
        this.leaveService.getMyLeaves(1).subscribe(data => this.myLeaves = data); // Hardcoded staff 1
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
                this.loadMyLeaves();
                this.newRequest = { staffId: 1 };
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
