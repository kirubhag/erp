import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { StudentPromotionService } from '../../services/student-promotion.service';
import { StudentPromotionResponse } from '../../models/student-promotion.model';

@Component({
  selector: 'app-promotion-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './promotion-details.component.html',
  styleUrls: ['./promotion-details.component.css']
})
export class PromotionDetailsComponent implements OnInit {
  batchId!: number;
  batch: StudentPromotionResponse | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private promotionService: StudentPromotionService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.batchId = +params['id'];
      this.loadBatchDetails();
    });
  }

  loadBatchDetails(): void {
    this.loading = true;
    this.error = null;

    this.promotionService.getBatchDetails(this.batchId).subscribe({
      next: (response) => {
        this.batch = response;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load batch details';
        this.loading = false;
        console.error('Error loading batch details:', error);
      }
    });
  }

  rollbackBatch(): void {
    if (!this.batch || this.batch.status !== 'COMPLETED') {
      alert('Only completed batches can be rolled back');
      return;
    }

    if (confirm(`Are you sure you want to rollback batch "${this.batch.batchName}"? This will restore all students to their previous grade and section.`)) {
      this.loading = true;
      this.promotionService.rollbackBatch(this.batchId).subscribe({
        next: () => {
          alert('Batch rolled back successfully');
          this.loadBatchDetails();
        },
        error: (error) => {
          this.error = 'Failed to rollback batch';
          this.loading = false;
          console.error('Error rolling back batch:', error);
        }
      });
    }
  }

  exportToCSV(): void {
    if (!this.batch) return;

    const headers = ['Student ID', 'Student Name', 'From Grade', 'To Grade', 'From Section', 'To Section', 'Status', 'Failure Reason', 'Promoted At'];
    const rows = this.batch.promotionRecords.map(record => [
      record.studentId,
      record.studentName,
      record.fromGradeLevel,
      record.toGradeLevel,
      record.fromSection || 'N/A',
      record.toSection || 'N/A',
      record.promotionStatus,
      record.failureReason || 'N/A',
      record.promotedAt || 'N/A'
    ]);

    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `promotion-batch-${this.batchId}.csv`;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  goBack(): void {
    this.router.navigate(['/promotions']);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'COMPLETED':
        return 'badge bg-success';
      case 'IN_PROGRESS':
        return 'badge bg-info';
      case 'PENDING':
        return 'badge bg-warning';
      case 'FAILED':
        return 'badge bg-danger';
      case 'ROLLED_BACK':
        return 'badge bg-secondary';
      case 'SUCCESS':
        return 'badge bg-success';
      default:
        return 'badge bg-light text-dark';
    }
  }

  getProgressBarClass(successRate: number): string {
    if (successRate >= 90) return 'bg-success';
    if (successRate >= 70) return 'bg-warning';
    return 'bg-danger';
  }
}
