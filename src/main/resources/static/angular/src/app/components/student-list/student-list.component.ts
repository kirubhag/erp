import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { EntityListComponent, EntityColumn, EntityFilter, PaginationInfo } from '../entity-list/entity-list.component';
import { StudentService } from '../../services/student.service';
import { AuthService } from '../../services/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

export interface Student {
  id: number;
  studentId: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  phone?: string;
  grade: string;
  status: 'ACTIVE' | 'INACTIVE' | 'PENDING';
  enrollmentDate: string;
}

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonModule, EntityListComponent],
  template: `
    <app-entity-list
      title="Students"
      entityName="Student"
      entityNamePlural="Students"
      entityType="students"
      [columns]="columns"
      [data]="students"
      [filters]="filters"
      [pagination]="pagination"
      [loading]="loading"
      [showFilters]="showFilters"
      [userId]="userId"
      [organizationId]="organizationId"
      (searchChange)="onSearch($event)"
      (filterChange)="onFilterChange($event)"
      (sortChange)="onSort($event)"
      (pageChange)="onPageChange($event)"
      (actionClick)="onAction($event)"
      (selectionChange)="onSelectionChange($event)"
      (rowClick)="onRowClick($event)">
    </app-entity-list>
  `
})
export class StudentListComponent implements OnInit, OnDestroy {
  students: Student[] = [];
  loading = false;
  showFilters = true; // Show filters by default
  userId?: number;
  organizationId?: number;
  private destroy$ = new Subject<void>();

  constructor(
    private studentService: StudentService,
    private router: Router,
    private authService: AuthService
  ) {
    // Get current user info for preferences
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.userId = currentUser.id;
      this.organizationId = currentUser.organizationId;
    }
  }

  columns: EntityColumn[] = [
    {
      key: 'fullName',
      label: 'STUDENT NAME',
      type: 'avatar',
      sortable: true,
      width: '250px'
    },
    {
      key: 'studentId',
      label: 'STUDENT ID',
      type: 'text',
      sortable: true,
      width: '120px'
    },
    {
      key: 'email',
      label: 'EMAIL',
      type: 'email',
      sortable: true,
      width: '200px'
    },
    {
      key: 'phone',
      label: 'PHONE',
      type: 'text',
      sortable: false,
      width: '120px'
    },
    {
      key: 'grade',
      label: 'GRADE',
      type: 'text',
      sortable: true,
      width: '100px'
    },
    {
      key: 'status',
      label: 'STATUS',
      type: 'badge',
      sortable: true,
      width: '100px'
    },
    {
      key: 'enrollmentDate',
      label: 'ENROLLMENT DATE',
      type: 'date',
      sortable: true,
      width: '150px'
    }
  ];

  filters: EntityFilter[] = [
    {
      key: 'status',
      label: 'Status',
      type: 'select',
      options: [
        { value: 'ACTIVE', label: 'Active' },
        { value: 'INACTIVE', label: 'Inactive' },
        { value: 'PENDING', label: 'Pending' }
      ],
      value: ''
    },
    {
      key: 'grade',
      label: 'Grade',
      type: 'select',
      options: [
        { value: 'KINDERGARTEN', label: 'Kindergarten' },
        { value: 'GRADE_1', label: 'Grade 1' },
        { value: 'GRADE_2', label: 'Grade 2' },
        { value: 'GRADE_3', label: 'Grade 3' }
      ],
      value: ''
    }
  ];

  pagination: PaginationInfo = {
    currentPage: 1,
    itemsPerPage: 10,
    totalItems: 0,
    totalPages: 0
  };

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnInit() {
    // Subscribe to user changes to update userId/organizationId if needed
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        if (user) {
          this.userId = user.id;
          this.organizationId = user.organizationId;
          console.log('Updated userId/organizationId from auth service:', { userId: this.userId, organizationId: this.organizationId });
        }
      });

    this.loadStudents();
  }

  loadStudents() {
    this.loading = true;
    const page = this.pagination.currentPage - 1; // Convert to 0-based for backend
    
    this.studentService.getStudents(page, this.pagination.itemsPerPage)
      .subscribe({
        next: (response) => {
          this.students = response.content;
          this.pagination.totalItems = response.totalElements;
          this.pagination.totalPages = response.totalPages;
        },
        error: (error) => {
          console.error('Error loading students:', error);
          // Handle error appropriately
        },
        complete: () => {
          this.loading = false;
        }
      });
  }

  onSearch(searchTerm: string) {
    if (searchTerm && searchTerm.trim() !== '') {
      this.loading = true;
      const page = this.pagination.currentPage - 1;
      
      this.studentService.searchStudents(searchTerm, page, this.pagination.itemsPerPage)
        .subscribe({
          next: (response) => {
            this.students = response.content;
            this.pagination.totalItems = response.totalElements;
            this.pagination.totalPages = response.totalPages;
          },
          error: (error) => {
            console.error('Error searching students:', error);
          },
          complete: () => {
            this.loading = false;
          }
        });
    } else {
      this.loadStudents();
    }
  }

  onFilterChange(filters: { [key: string]: any }) {
    // Handle status filter
    if (filters['status']) {
      this.loading = true;
      const page = this.pagination.currentPage - 1;
      
      this.studentService.getStudentsByStatus(filters['status'], page, this.pagination.itemsPerPage)
        .subscribe({
          next: (response) => {
            this.students = response.content;
            this.pagination.totalItems = response.totalElements;
            this.pagination.totalPages = response.totalPages;
          },
          error: (error) => {
            console.error('Error filtering students by status:', error);
          },
          complete: () => {
            this.loading = false;
          }
        });
    }
    
    // Handle grade filter
    if (filters['grade']) {
      this.loading = true;
      const page = this.pagination.currentPage - 1;
      
      this.studentService.getStudentsByGrade(filters['grade'], page, this.pagination.itemsPerPage)
        .subscribe({
          next: (response) => {
            this.students = response.content;
            this.pagination.totalItems = response.totalElements;
            this.pagination.totalPages = response.totalPages;
          },
          error: (error) => {
            console.error('Error filtering students by grade:', error);
          },
          complete: () => {
            this.loading = false;
          }
        });
    }
  }

  onSort(sort: { column: string; direction: 'asc' | 'desc' }) {
    const sortQuery = `${sort.column},${sort.direction}`;
    const page = this.pagination.currentPage - 1;
    
    this.loading = true;
    this.studentService.getStudents(page, this.pagination.itemsPerPage, sortQuery)
      .subscribe({
        next: (response) => {
          this.students = response.content;
          this.pagination.totalItems = response.totalElements;
          this.pagination.totalPages = response.totalPages;
        },
        error: (error) => {
          console.error('Error sorting students:', error);
        },
        complete: () => {
          this.loading = false;
        }
      });
  }

  onPageChange(page: number) {
    console.log('Page:', page);
    this.pagination.currentPage = page;
    this.loadStudents();
  }

  onAction(event: { action: string; item?: any; selectedItems?: any[] }) {
    console.log('Action:', event);
    switch (event.action) {
      case 'add':
        console.log('Add new student');
        break;
      case 'import':
        console.log('Import students - navigating to import-wizard route');
        console.log('Available routes should include: /import-wizard');
        this.router.navigate(['/import-wizard'], { 
          queryParams: { entityType: 'students' } 
        }).then(success => {
          console.log('Navigation to import-wizard:', success ? 'SUCCESS' : 'FAILED');
        }).catch(error => {
          console.error('Navigation error:', error);
        });
        break;
      case 'edit':
        console.log('Edit student:', event.item);
        break;
      case 'delete':
        console.log('Delete students:', event.selectedItems);
        break;
    }
  }

  onRowClick(event: { entityType: string; item: any }): void {
    const { entityType, item } = event;
    const itemId = item.id;
    if (itemId) {
      this.router.navigate(['/entity-detail', entityType, itemId]);
    }
  }

  onSelectionChange(selectedItems: any[]) {
    console.log('Selected items:', selectedItems);
  }
}