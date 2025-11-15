import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { EntityListComponent, EntityColumn, PaginationInfo } from '../entity-list/entity-list.component';
import { HttpClient } from '@angular/common/http';

interface ApiResponse {
  content?: any[];
  data?: any[];
  records?: any[];
  _embedded?: { [key: string]: any[] };
  totalElements?: number;
  total?: number;
  size?: number;
  number?: number;
  totalPages?: number;
}

@Component({
  selector: 'app-entity-management',
  standalone: true,
  imports: [CommonModule, EntityListComponent],
  template: `
    <app-entity-list
      [title]="title"
      [entityName]="entityName"
      [entityNamePlural]="entityNamePlural"
      [columns]="columns"
      [data]="data"
      [pagination]="pagination"
      [loading]="loading"
      (pageChange)="onPageChange($event)"
      (searchChange)="onSearch($event)"
    ></app-entity-list>
  `,
  styles: []
})
export class EntityManagementComponent implements OnInit {
  entityType: string = '';
  title: string = '';
  entityName: string = '';
  entityNamePlural: string = '';
  columns: EntityColumn[] = [];
  data: any[] = [];
  loading: boolean = false;
  pagination: PaginationInfo = {
    currentPage: 1,
    itemsPerPage: 50,
    totalItems: 0,
    totalPages: 1
  };

  private columnMappings: { [key: string]: EntityColumn[] } = {
    staff: [
      { key: 'id', label: 'ID', type: 'text' },
      { key: 'firstName', label: 'First Name', type: 'text', sortable: true },
      { key: 'lastName', label: 'Last Name', type: 'text', sortable: true },
      { key: 'email', label: 'Email', type: 'email', sortable: true },
      { key: 'phone', label: 'Phone', type: 'text' },
      { key: 'staffType', label: 'Type', type: 'badge' },
      { key: 'department', label: 'Department', type: 'text' },
      { key: 'isActive', label: 'Active', type: 'badge' }
    ],
    attendance: [
      { key: 'id', label: 'ID', type: 'text' },
      { key: 'studentId', label: 'Student ID', type: 'text' },
      { key: 'attendanceDate', label: 'Date', type: 'date', sortable: true },
      { key: 'status', label: 'Status', type: 'badge', sortable: true },
      { key: 'remarks', label: 'Remarks', type: 'text' }
    ],
    parents: [
      { key: 'id', label: 'ID', type: 'text' },
      { key: 'firstName', label: 'First Name', type: 'text', sortable: true },
      { key: 'lastName', label: 'Last Name', type: 'text', sortable: true },
      { key: 'email', label: 'Email', type: 'email', sortable: true },
      { key: 'phone', label: 'Phone', type: 'text' },
      { key: 'relation', label: 'Relation', type: 'text' }
    ],
    subjects: [
      { key: 'id', label: 'ID', type: 'text' },
      { key: 'subjectName', label: 'Subject Name', type: 'text', sortable: true },
      { key: 'subjectCode', label: 'Code', type: 'text', sortable: true },
      { key: 'description', label: 'Description', type: 'text' },
      { key: 'isActive', label: 'Active', type: 'badge' }
    ]
  };

  private apiEndpoints: { [key: string]: string } = {
    staff: '/api/v1/staff',
    attendance: '/api/attendance',
    parents: '/api/parents',
    subjects: '/api/subjects'
  };

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const routePath = this.route.snapshot.url[0]?.path || '';
      this.entityType = routePath;
      this.initializeEntityType();
      this.loadData();
    });
  }

  private initializeEntityType(): void {
    switch (this.entityType) {
      case 'staff':
        this.title = 'Staff Members';
        this.entityName = 'Staff';
        this.entityNamePlural = 'Staff';
        this.columns = this.columnMappings['staff'];
        break;
      case 'attendance':
        this.title = 'Attendance';
        this.entityName = 'Attendance Record';
        this.entityNamePlural = 'Attendance Records';
        this.columns = this.columnMappings['attendance'];
        break;
      case 'parents':
        this.title = 'Parents';
        this.entityName = 'Parent';
        this.entityNamePlural = 'Parents';
        this.columns = this.columnMappings['parents'];
        break;
      case 'subjects':
        this.title = 'Subjects';
        this.entityName = 'Subject';
        this.entityNamePlural = 'Subjects';
        this.columns = this.columnMappings['subjects'];
        break;
      default:
        this.title = 'Entity Management';
        this.entityName = 'Item';
        this.entityNamePlural = 'Items';
    }
  }

  loadData(page: number = 1): void {
    this.loading = true;
    const endpoint = this.apiEndpoints[this.entityType];
    
    if (!endpoint) {
      console.warn(`No API endpoint configured for entity type: ${this.entityType}`);
      this.loading = false;
      return;
    }

    const params = {
      page: page - 1,
      size: this.pagination.itemsPerPage
    };

    // Use full backend URL directly
    const backendUrl = `http://localhost:8081${endpoint}`;

    this.http.get<ApiResponse>(backendUrl, { params }).subscribe({
      next: (response) => {
        this.data = this.extractData(response);
        this.pagination.totalItems = this.extractTotalElements(response);
        this.pagination.totalPages = Math.ceil(
          this.pagination.totalItems / this.pagination.itemsPerPage
        );
        this.pagination.currentPage = page;
        this.loading = false;
        console.log(`Loaded ${this.data.length} ${this.entityType} records`);
      },
      error: (error) => {
        console.error(`Error loading ${this.entityType}:`, error);
        this.loading = false;
        // Set empty data on error
        this.data = [];
      }
    });
  }

  private extractData(response: ApiResponse): any[] {
    if (Array.isArray(response)) {
      return response;
    }
    if (response.content) {
      return response.content;
    }
    if (response.data) {
      return response.data;
    }
    if (response.records) {
      return response.records;
    }
    if (response._embedded) {
      const key = Object.keys(response._embedded)[0];
      return response._embedded[key] || [];
    }
    return [];
  }

  private extractTotalElements(response: ApiResponse): number {
    return response.totalElements || response.total || 0;
  }

  onPageChange(page: number): void {
    this.loadData(page);
  }

  onSearch(searchTerm: string): void {
    // Implement search functionality if needed
    console.log('Searching for:', searchTerm);
  }
}
