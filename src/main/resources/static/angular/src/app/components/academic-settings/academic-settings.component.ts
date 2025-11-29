import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';
import { HttpClient } from '@angular/common/http';

interface AcademicYear {
  id?: number;
  name: string;
  startDate: string;
  endDate: string;
  isActive: boolean;
}

interface Term {
  id?: number;
  name: string;
  startDate: string;
  endDate: string;
  academicYearId?: number;
}

interface GradingScale {
  id?: number;
  name: string;
  minPercentage: number;
  maxPercentage: number;
  gradePoint: number;
  letterGrade: string;
}

interface AttendanceSettings {
  enableAttendanceTracking: boolean;
  attendanceCalculationMethod: string;
  minimumAttendancePercentage: number;
  allowLateMarking: boolean;
  lateMarkingCutoffMinutes: number;
  enableBiometricIntegration: boolean;
}

@Component({
  selector: 'app-academic-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, SettingsSidebarComponent],
  templateUrl: './academic-settings.component.html',
  styleUrls: ['./academic-settings.component.css']
})
export class AcademicSettingsComponent implements OnInit {
  activeTab = 'academic-year';

  // Forms
  academicYearForm: FormGroup;
  termForm: FormGroup;
  gradingForm: FormGroup;
  attendanceForm: FormGroup;
  examForm: FormGroup;
  promotionForm: FormGroup;

  // Data
  academicYears: AcademicYear[] = [];
  terms: Term[] = [];
  gradingScales: GradingScale[] = [];

  // UI States
  // UI States
  isEditingAcademicYear = false;
  isEditingTerm = false;
  isEditingGrading = false;
  showAcademicYearModal = false;
  showTermModal = false;
  showGradingModal = false;
  selectedAcademicYear: AcademicYear | null = null;
  selectedTerm: Term | null = null;

  successMessage = '';
  errorMessage = '';

  // Header Info
  // pageTitle and pageDescription removed as we are using static header

  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {
    // ... (forms initialization)
    // Academic Year Form
    this.academicYearForm = this.formBuilder.group({
      name: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      isActive: [false]
    });

    // Term Form
    this.termForm = this.formBuilder.group({
      name: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      academicYearId: ['', Validators.required]
    });

    // Grading Scale Form
    this.gradingForm = this.formBuilder.group({
      name: ['', Validators.required],
      minPercentage: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      maxPercentage: [100, [Validators.required, Validators.min(0), Validators.max(100)]],
      gradePoint: [0, [Validators.required, Validators.min(0)]],
      letterGrade: ['', Validators.required]
    });

    // Attendance Settings Form
    this.attendanceForm = this.formBuilder.group({
      enableAttendanceTracking: [true],
      attendanceCalculationMethod: ['percentage'],
      minimumAttendancePercentage: [75, [Validators.min(0), Validators.max(100)]],
      allowLateMarking: [true],
      lateMarkingCutoffMinutes: [30, Validators.min(0)],
      enableBiometricIntegration: [false]
    });

    // Exam Settings Form
    this.examForm = this.formBuilder.group({
      defaultExamDuration: [60, Validators.min(1)],
      allowMakeupExams: [true],
      makeupExamDeadlineDays: [7, Validators.min(0)],
      passingPercentage: [40, [Validators.min(0), Validators.max(100)]],
      enableGradeModeration: [false],
      autoCalculateGrades: [true],
      publishResultsImmediately: [false]
    });

    // Promotion Rules Form
    this.promotionForm = this.formBuilder.group({
      autoPromoteStudents: [false],
      minimumAttendanceForPromotion: [75, [Validators.min(0), Validators.max(100)]],
      minimumGradeForPromotion: [40, [Validators.min(0), Validators.max(100)]],
      allowGraceMarks: [true],
      graceMarksLimit: [5, Validators.min(0)],
      requireAllSubjectsPass: [true],
      allowCompartmentExams: [true]
    });
  }

  ngOnInit() {
    // Handle tab selection from route params
    this.route.params.subscribe(params => {
      if (params['section']) {
        this.activeTab = params['section'];
      }
    });

    this.loadAcademicYears();
    this.loadTerms();
    this.loadGradingScales();
    this.loadAttendanceSettings();
    this.loadExamSettings();
    this.loadPromotionSettings();
  }

  // updateHeaderInfo removed

  // Helper method to get academic year name
  getAcademicYearName(yearId?: number): string {
    if (!yearId) return 'N/A';
    const year = this.academicYears.find(y => y.id === yearId);
    return year?.name || 'N/A';
  }

  // Academic Year Methods
  loadAcademicYears() {
    this.http.get<AcademicYear[]>('/api/academic/years').subscribe({
      next: (data) => this.academicYears = data,
      error: (err) => this.showError('Failed to load academic years')
    });
  }

  openAcademicYearModal(year?: AcademicYear) {
    this.showAcademicYearModal = true;
    if (year) {
      this.isEditingAcademicYear = true;
      this.selectedAcademicYear = year;
      this.academicYearForm.patchValue(year);
    } else {
      this.isEditingAcademicYear = false;
      this.selectedAcademicYear = null;
      this.academicYearForm.reset({ isActive: false });
    }
  }

  closeAcademicYearModal() {
    this.showAcademicYearModal = false;
    this.academicYearForm.reset();
    this.selectedAcademicYear = null;
  }

  saveAcademicYear() {
    if (this.academicYearForm.invalid) return;

    const yearData = this.academicYearForm.value;
    const request = this.isEditingAcademicYear && this.selectedAcademicYear?.id
      ? this.http.put(`/api/academic/years/${this.selectedAcademicYear.id}`, yearData)
      : this.http.post('/api/academic/years', yearData);

    request.subscribe({
      next: () => {
        this.showSuccess('Academic year saved successfully');
        this.closeAcademicYearModal();
        this.loadAcademicYears();
      },
      error: (err) => this.showError('Failed to save academic year')
    });
  }

  deleteAcademicYear(id: number) {
    if (!confirm('Are you sure you want to delete this academic year?')) return;

    this.http.delete(`/api/academic/years/${id}`).subscribe({
      next: () => {
        this.showSuccess('Academic year deleted successfully');
        this.loadAcademicYears();
      },
      error: (err) => this.showError('Failed to delete academic year')
    });
  }

  setActiveAcademicYear(id: number) {
    this.http.put(`/api/academic/years/${id}/activate`, {}).subscribe({
      next: () => {
        this.showSuccess('Academic year activated successfully');
        this.loadAcademicYears();
      },
      error: (err) => this.showError('Failed to activate academic year')
    });
  }

  // Term Methods
  loadTerms() {
    this.http.get<Term[]>('/api/academic/terms').subscribe({
      next: (data) => this.terms = data,
      error: (err) => this.showError('Failed to load terms')
    });
  }

  openTermModal(term?: Term) {
    this.showTermModal = true;
    if (term) {
      this.isEditingTerm = true;
      this.selectedTerm = term;
      this.termForm.patchValue(term);
    } else {
      this.isEditingTerm = false;
      this.selectedTerm = null;
      this.termForm.reset();
    }
  }

  closeTermModal() {
    this.showTermModal = false;
    this.termForm.reset();
    this.selectedTerm = null;
  }

  saveTerm() {
    if (this.termForm.invalid) return;

    const termData = this.termForm.value;
    const request = this.isEditingTerm && this.selectedTerm?.id
      ? this.http.put(`/api/academic/terms/${this.selectedTerm.id}`, termData)
      : this.http.post('/api/academic/terms', termData);

    request.subscribe({
      next: () => {
        this.showSuccess('Term saved successfully');
        this.closeTermModal();
        this.loadTerms();
      },
      error: (err) => this.showError('Failed to save term')
    });
  }

  deleteTerm(id: number) {
    if (!confirm('Are you sure you want to delete this term?')) return;

    this.http.delete(`/api/academic/terms/${id}`).subscribe({
      next: () => {
        this.showSuccess('Term deleted successfully');
        this.loadTerms();
      },
      error: (err) => this.showError('Failed to delete term')
    });
  }

  // Grading Scale Methods
  loadGradingScales() {
    this.http.get<GradingScale[]>('/api/academic/grading-scales').subscribe({
      next: (data) => this.gradingScales = data,
      error: (err) => this.showError('Failed to load grading scales')
    });
  }

  openGradingModal(scale?: GradingScale) {
    this.showGradingModal = true;
    if (scale) {
      this.isEditingGrading = true;
      this.gradingForm.patchValue(scale);
    } else {
      this.isEditingGrading = false;
      this.gradingForm.reset();
    }
  }

  closeGradingModal() {
    this.showGradingModal = false;
    this.gradingForm.reset();
  }

  saveGradingScale() {
    if (this.gradingForm.invalid) return;

    const scaleData = this.gradingForm.value;
    this.http.post('/api/academic/grading-scales', scaleData).subscribe({
      next: () => {
        this.showSuccess('Grading scale saved successfully');
        this.closeGradingModal();
        this.loadGradingScales();
      },
      error: (err) => this.showError('Failed to save grading scale')
    });
  }

  deleteGradingScale(id: number) {
    if (!confirm('Are you sure you want to delete this grading scale?')) return;

    this.http.delete(`/api/academic/grading-scales/${id}`).subscribe({
      next: () => {
        this.showSuccess('Grading scale deleted successfully');
        this.loadGradingScales();
      },
      error: (err) => this.showError('Failed to delete grading scale')
    });
  }

  // Settings Methods
  loadAttendanceSettings() {
    this.http.get<AttendanceSettings>('/api/academic/settings/attendance').subscribe({
      next: (data) => this.attendanceForm.patchValue(data),
      error: (err) => this.showError('Failed to load attendance settings')
    });
  }

  saveAttendanceSettings() {
    if (this.attendanceForm.invalid) return;

    this.http.put('/api/academic/settings/attendance', this.attendanceForm.value).subscribe({
      next: () => this.showSuccess('Attendance settings saved successfully'),
      error: (err) => this.showError('Failed to save attendance settings')
    });
  }

  loadExamSettings() {
    this.http.get('/api/academic/settings/exam').subscribe({
      next: (data) => this.examForm.patchValue(data),
      error: (err) => this.showError('Failed to load exam settings')
    });
  }

  saveExamSettings() {
    if (this.examForm.invalid) return;

    this.http.put('/api/academic/settings/exam', this.examForm.value).subscribe({
      next: () => this.showSuccess('Exam settings saved successfully'),
      error: (err) => this.showError('Failed to save exam settings')
    });
  }

  loadPromotionSettings() {
    this.http.get('/api/academic/settings/promotion').subscribe({
      next: (data) => this.promotionForm.patchValue(data),
      error: (err) => this.showError('Failed to load promotion settings')
    });
  }

  savePromotionSettings() {
    if (this.promotionForm.invalid) return;

    this.http.put('/api/academic/settings/promotion', this.promotionForm.value).subscribe({
      next: () => this.showSuccess('Promotion settings saved successfully'),
      error: (err) => this.showError('Failed to save promotion settings')
    });
  }

  // Utility Methods
  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  showSuccess(message: string) {
    this.successMessage = message;
    this.errorMessage = '';
    setTimeout(() => this.successMessage = '', 3000);
  }

  showError(message: string) {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => this.errorMessage = '', 3000);
  }
}
