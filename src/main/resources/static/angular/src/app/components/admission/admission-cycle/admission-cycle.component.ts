import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdmissionService, AdmissionCycle, AdmissionSeatAllocation } from '../../../services/admission.service';

@Component({
    selector: 'app-admission-cycle',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './admission-cycle.component.html',
    styleUrls: ['./admission-cycle.component.css']
})
export class AdmissionCycleComponent implements OnInit {
    cycles: AdmissionCycle[] = [];
    newCycle: AdmissionCycle = {
        name: '',
        startDate: '',
        endDate: '',
        academicYearId: 1 // Default to 1 for now or fetch existing academic years
    };

    gradeLevels = ['GRADE_1', 'GRADE_2', 'GRADE_3', 'GRADE_4', 'GRADE_5']; // Sample grades
    selectedCycleId: number | undefined = undefined;
    newAllocation: AdmissionSeatAllocation = {
        admissionCycleId: 0,
        gradeLevel: 'GRADE_1',
        totalSeats: 0,
        occupiedSeats: 0,
        waitlistedSeats: 0
    };

    constructor(private admissionService: AdmissionService) { }

    ngOnInit() {
        this.loadCycles();
    }

    loadCycles() {
        this.admissionService.getAllCycles().subscribe(data => {
            this.cycles = data;
        });
    }

    saveCycle() {
        this.admissionService.createCycle(this.newCycle).subscribe(cycle => {
            this.loadCycles();
            this.newCycle = { name: '', startDate: '', endDate: '', academicYearId: 1 }; // Reset
        });
    }

    saveAllocation() {
        if (this.selectedCycleId) {
            this.newAllocation.admissionCycleId = this.selectedCycleId;
            this.admissionService.createAllocation(this.newAllocation).subscribe(() => {
                alert('Allocation saved!');
                // Reset allocation form?
            });
        }
    }
}
