import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { StudentPromotionService } from '../../services/student-promotion.service';
import { StudentPromotionResponse } from '../../models/student-promotion.model';

@Component({
  selector: 'app-promotion-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './promotion-list.component.html',
  styleUrls: ['./promotion-list.component.css']
})
export class PromotionListComponent implements OnInit {
  batches: StudentPromotionResponse[] = [];
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  
  filterStatus = '';
  searchTerm = '';
  loading = false;
  error: string | null = null;
  
  // Expose Math to template
  Math = Math;

  constructor(
    private promotionService: StudentPromotionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadBatches();
  }

  loadBatches(): void {
    this.loading = true;
    this.error = null;

    if (this.filterStatus) {
      this.promotionService.getBatchesByStatus(this.filterStatus).subscribe({
        next: (batches) => {
          this.batches = batches;
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Failed to load batches';
          this.loading = false;
          console.error('Error loading batches:', error);
        }
      });
    } else {
      this.promotionService.getAllBatches(this.currentPage, this.pageSize).subscribe({
        next: (response) => {
          this.batches = response.content || [];
          this.totalPages = response.totalPages || 0;
          this.totalElements = response.totalElements || 0;
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Failed to load batches';
          this.loading = false;
          console.error('Error loading batches:', error);
        }
      });
    }
  }

  filterByStatus(): void {
    this.currentPage = 0;
    this.loadBatches();
  }

  viewDetails(batchId: number): void {
    this.router.navigate(['/promotions/details', batchId]);
  }

  createNewPromotion(): void {
    this.router.navigate(['/promotions/create']);
  }

  rollbackBatch(batch: StudentPromotionResponse): void {
    if (batch.status !== 'COMPLETED') {
      alert('Only completed batches can be rolled back');
      return;
    }

    if (confirm(`Are you sure you want to rollback batch "${batch.batchName}"? This will restore all students to their previous grade and section.`)) {
      this.loading = true;
      this.promotionService.rollbackBatch(batch.batchId).subscribe({
        next: () => {
          alert('Batch rolled back successfully');
          this.loadBatches();
        },
        error: (error) => {
          this.error = 'Failed to rollback batch';
          this.loading = false;
          console.error('Error rolling back batch:', error);
        }
      });
    }
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
      default:
        return 'badge bg-light text-dark';
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadBatches();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadBatches();
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadBatches();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
