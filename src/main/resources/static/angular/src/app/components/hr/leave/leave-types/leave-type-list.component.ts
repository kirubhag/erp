import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LeaveService, LeaveType } from '../../../../services/leave.service';

@Component({
    selector: 'app-leave-type-list',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="container-fluid p-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Leave Policies</h2>
         <!-- Generic Create Button could go here -->
      </div>
      <div class="card">
        <div class="card-body p-0">
          <table class="table table-hover mb-0">
            <thead class="bg-light">
              <tr>
                <th>Name</th>
                <th>Code</th>
                <th>Days Allowed</th>
                <th>Carry Forward</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let type of types">
                <td>{{type.name}}</td>
                <td><span class="badge bg-secondary">{{type.code}}</span></td>
                <td>{{type.daysAllowed}}</td>
                <td>
                    <i class="fas" [ngClass]="type.isCarryForward ? 'fa-check text-success' : 'fa-times text-muted'"></i>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class LeaveTypeListComponent implements OnInit {
    types: LeaveType[] = [];

    constructor(private leaveService: LeaveService) { }

    ngOnInit() {
        this.leaveService.getLeaveTypes().subscribe(data => this.types = data);
    }
}
