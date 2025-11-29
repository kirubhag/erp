// Student Promotion Models

export interface StudentPromotionItem {
  studentId: number;
  toGradeLevel: string;
  toSection?: string;
}

export interface StudentPromotionRequest {
  batchName: string;
  academicYearFrom: string;
  academicYearTo: string;
  promotionDate: string;
  students: StudentPromotionItem[];
  notes?: string;
  autoAssignSections: boolean;
}

export interface PromotionRecordSummary {
  recordId: number;
  studentId: number;
  studentName: string;
  fromGradeLevel: string;
  toGradeLevel: string;
  fromSection?: string;
  toSection?: string;
  promotionStatus: string;
  failureReason?: string;
  promotedAt?: string;
}

export interface StudentPromotionResponse {
  batchId: number;
  batchName: string;
  academicYearFrom: string;
  academicYearTo: string;
  promotionDate: string;
  status: string;
  totalStudents: number;
  successfulPromotions: number;
  failedPromotions: number;
  processedStudents: number;
  progressPercentage: number;
  currentPhase?: string;
  successRate: number;
  initiatedBy: number;
  createdAt: string;
  completedAt?: string;
  startedAt?: string;
  notes?: string;
  promotionRecords: PromotionRecordSummary[];
}

export interface StudentEligible {
  studentId: number;
  firstName: string;
  lastName: string;
  gradeLevel: string;
  section?: string;
  admissionNumber?: string;
  selected?: boolean;
  targetGrade?: string;
  targetSection?: string;
}

export interface GradeStatistics {
  [gradeLevel: string]: number;
}
