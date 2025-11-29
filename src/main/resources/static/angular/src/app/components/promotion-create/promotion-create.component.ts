import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { StudentPromotionService } from '../../services/student-promotion.service';
import {
  StudentPromotionRequest,
  StudentEligible,
  GradeStatistics
} from '../../models/student-promotion.model';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

@Component({
  selector: 'app-promotion-create',
  standalone: true,
  imports: [CommonModule, FormsModule, SettingsSidebarComponent],
  templateUrl: './promotion-create.component.html',
  styleUrls: ['./promotion-create.component.css']
})
export class PromotionCreateComponent implements OnInit, OnDestroy {
  // Form data
  batchName = '';
  academicYearFrom = '';
  academicYearTo = '';
  promotionDate = '';
  notes = '';
  autoAssignSections = true;

  // Grade selection
  selectedGrade = '';
  selectedSection = '';
  searchTerm = '';
  targetGrade = '';
  targetSection = '';
  gradeLevels: { [key: string]: string } = {};
  sections = ['A', 'B', 'C'];

  // Student data
  eligibleStudents: StudentEligible[] = [];
  selectedStudents: StudentEligible[] = [];
  allSelected = false;

  // UI state
  loading = false;
  loadingStudents = false;
  processing = false;
  showProgressModal = false;
  showConfirmModal = false;
  progressPercentage = 0;
  currentPhase = '';
  processingBatchId: number | null = null;
  error: string | null = null;
  success = false;
  statistics: GradeStatistics | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 50;

  // Progress polling
  private progressInterval: any;

  constructor(
    private promotionService: StudentPromotionService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadGradeLevels();
    this.loadStatistics();
    this.setDefaultDates();
  }

  setDefaultDates(): void {
    const today = new Date();
    this.promotionDate = today.toISOString().split('T')[0];
    this.academicYearFrom = `${today.getFullYear()}-${today.getFullYear() + 1}`;
    this.academicYearTo = `${today.getFullYear() + 1}-${today.getFullYear() + 2}`;
  }

  loadGradeLevels(): void {
    this.promotionService.getGradeLevels().subscribe({
      next: (levels) => {
        this.gradeLevels = levels;
      },
      error: (error) => {
        console.error('Error loading grade levels:', error);
      }
    });
  }

  loadStatistics(): void {
    this.promotionService.getStatistics().subscribe({
      next: (stats) => {
        this.statistics = stats;
      },
      error: (error) => {
        console.error('Error loading statistics:', error);
      }
    });
  }

  loadStudents(): void {
    this.loadingStudents = true;
    this.error = null;

    this.promotionService.getEligibleStudents(
      this.selectedGrade || undefined,
      this.selectedSection || undefined,
      this.searchTerm || undefined,
      this.currentPage,
      this.pageSize
    ).subscribe({
      next: (response) => {
        this.eligibleStudents = (response.students || []).map((student: any) => ({
          studentId: student.studentId,
          firstName: student.firstName,
          lastName: student.lastName,
          gradeLevel: student.gradeLevel,
          section: student.section,
          admissionNumber: student.admissionNumber,
          selected: false,
          targetGrade: this.getNextGrade(student.gradeLevel),
          targetSection: student.section
        }));
        this.loadingStudents = false;
      },
      error: (error) => {
        this.error = 'Failed to load students';
        this.loadingStudents = false;
        console.error('Error loading students:', error);
      }
    });
  }

  onSearchChange(): void {
    // Debounce search
    if (this.searchTerm.length >= 2 || this.searchTerm.length === 0) {
      this.loadStudents();
    }
  }

  getNextGrade(currentGrade: string): string {
    const gradeKeys = Object.keys(this.gradeLevels);
    const currentIndex = gradeKeys.indexOf(currentGrade);
    if (currentIndex >= 0 && currentIndex < gradeKeys.length - 1) {
      return gradeKeys[currentIndex + 1];
    }
    return currentGrade;
  }

  toggleSelectAll(): void {
    this.allSelected = !this.allSelected;
    this.eligibleStudents.forEach(student => {
      student.selected = this.allSelected;
    });
    this.updateSelectedStudents();
  }

  toggleStudent(student: StudentEligible): void {
    student.selected = !student.selected;
    this.updateSelectedStudents();
  }

  updateSelectedStudents(): void {
    this.selectedStudents = this.eligibleStudents.filter(s => s.selected);
    this.allSelected = this.eligibleStudents.length > 0 &&
      this.selectedStudents.length === this.eligibleStudents.length;
  }

  globalTargetGrade = '';

  applyGlobalTargetGrade(): void {
    if (!this.globalTargetGrade) return;

    this.eligibleStudents.forEach(student => {
      student.targetGrade = this.globalTargetGrade;
    });
  }

  applyBulkGrade(): void {
    if (!this.targetGrade) {
      return;
    }
    this.selectedStudents.forEach(student => {
      student.targetGrade = this.targetGrade;
    });
  }

  applyBulkSection(): void {
    if (!this.targetSection) {
      return;
    }
    this.selectedStudents.forEach(student => {
      student.targetSection = this.targetSection;
    });
  }

  validateForm(): boolean {
    if (!this.batchName.trim()) {
      this.error = 'Please enter a batch name';
      return false;
    }
    if (!this.academicYearFrom || !this.academicYearTo) {
      this.error = 'Please select academic years';
      return false;
    }
    if (!this.promotionDate) {
      this.error = 'Please select promotion date';
      return false;
    }
    if (this.selectedStudents.length === 0) {
      this.error = 'Please select at least one student';
      return false;
    }
    return true;
  }

  showConfirmation(): void {
    if (!this.validateForm()) {
      return;
    }
    this.showConfirmModal = true;
  }

  closeConfirmModal(): void {
    this.showConfirmModal = false;
  }

  submitPromotion(): void {
    this.showConfirmModal = false;

    const request: StudentPromotionRequest = {
      batchName: this.batchName,
      academicYearFrom: this.academicYearFrom,
      academicYearTo: this.academicYearTo,
      promotionDate: this.promotionDate,
      students: this.selectedStudents.map(student => ({
        studentId: student.studentId,
        toGradeLevel: student.targetGrade || this.getNextGrade(student.gradeLevel),
        toSection: this.autoAssignSections ? undefined : student.targetSection
      })),
      notes: this.notes,
      autoAssignSections: this.autoAssignSections
    };

    this.processing = true;
    this.showProgressModal = true;
    this.error = null;

    // Execute promotion (async - returns immediately with batch ID)
    this.promotionService.executePromotion(request).subscribe({
      next: (response) => {
        this.processingBatchId = response.batchId;
        this.currentPhase = 'Initializing...';
        this.startProgressPolling(response.batchId);
      },
      error: (error) => {
        this.error = error.error?.error || 'Failed to execute promotion';
        this.processing = false;
        this.showProgressModal = false;
        console.error('Error executing promotion:', error);
      }
    });
  }

  startProgressPolling(batchId: number): void {
    // Poll progress every 2 seconds
    this.progressInterval = setInterval(() => {
      this.promotionService.getBatchProgress(batchId).subscribe({
        next: (progress) => {
          this.progressPercentage = progress.progressPercentage || 0;
          this.currentPhase = progress.currentPhase || 'Processing...';

          // Check if completed
          if (progress.status === 'COMPLETED' || progress.status === 'FAILED') {
            this.stopProgressPolling();
            this.processing = false;

            if (progress.status === 'COMPLETED') {
              this.success = true;
              setTimeout(() => {
                this.showProgressModal = false;
                this.router.navigate(['/promotions/details', batchId]);
              }, 2000);
            } else {
              this.error = 'Promotion failed. Check batch details for more information.';
              this.showProgressModal = false;
            }
          }
        },
        error: (error) => {
          console.error('Error polling progress:', error);
        }
      });
    }, 2000);
  }

  stopProgressPolling(): void {
    if (this.progressInterval) {
      clearInterval(this.progressInterval);
      this.progressInterval = null;
    }
  }

  ngOnDestroy(): void {
    this.stopProgressPolling();
  }

  cancel(): void {
    this.router.navigate(['/promotions']);
  }

  getStudentCount(grade: string): number {
    return this.statistics ? (this.statistics[grade] || 0) : 0;
  }
}
