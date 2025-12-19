import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
      [entityType]="entityType"
      [columns]="columns"
      [data]="data"
      [pagination]="pagination"
      [loading]="loading"
      (pageChange)="onPageChange($event)"
      (searchChange)="onSearch($event)"
      (rowClick)="onRowClick($event)"
      (actionClick)="onActionClick($event)"
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
    ],
    rooms: [
      { key: 'id', label: 'ID', type: 'text' },
      { key: 'roomName', label: 'Room Name', type: 'text', sortable: true },
      { key: 'roomType', label: 'Type', type: 'badge', sortable: true },
      { key: 'capacity', label: 'Capacity', type: 'number', sortable: true },
      { key: 'building', label: 'Building', type: 'text', sortable: true },
      { key: 'description', label: 'Description', type: 'text' },
      { key: 'isActive', label: 'Active', type: 'badge' }
    ],
    exams: [
      { key: 'id', label: 'ID', type: 'text' },
      { key: 'examName', label: 'Exam Name', type: 'text', sortable: true },
      { key: 'academicYear', label: 'Academic Year', type: 'text', sortable: true },
      { key: 'term', label: 'Term', type: 'text', sortable: true },
      { key: 'startDate', label: 'Start Date', type: 'date', sortable: true },
      { key: 'endDate', label: 'End Date', type: 'date', sortable: true },
      { key: 'status', label: 'Status', type: 'badge' }
    ],
    courses: [
      { key: 'id', label: 'ID', type: 'text' },
      { key: 'courseName', label: 'Course Name', type: 'text', sortable: true },
      { key: 'courseCode', label: 'Code', type: 'text', sortable: true },
      { key: 'credits', label: 'Credits', type: 'number', sortable: true },
      { key: 'department', label: 'Department', type: 'text', sortable: true },
      { key: 'description', label: 'Description', type: 'text' },
      { key: 'isActive', label: 'Active', type: 'badge' }
    ],
    assignments: [
      { key: 'id', label: 'ID', type: 'text' },
      { key: 'studentName', label: 'Student', type: 'text', sortable: true },
      { key: 'subjectName', label: 'Subject', type: 'text', sortable: true },
      { key: 'examType', label: 'Exam Type', type: 'text', sortable: true },
      { key: 'marksObtained', label: 'Marks', type: 'number', sortable: true },
      { key: 'totalMarks', label: 'Total', type: 'number' },
      { key: 'percentage', label: 'Percentage', type: 'number', sortable: true },
      { key: 'letterGrade', label: 'Grade', type: 'badge' }
    ],
    grades: [
      { key: 'id', label: 'ID', type: 'text' },
      { key: 'studentName', label: 'Student', type: 'text', sortable: true },
      { key: 'subjectName', label: 'Subject', type: 'text', sortable: true },
      { key: 'examType', label: 'Exam Type', type: 'text', sortable: true },
      { key: 'marksObtained', label: 'Marks', type: 'number', sortable: true },
      { key: 'totalMarks', label: 'Total', type: 'number' },
      { key: 'percentage', label: 'Percentage', type: 'number', sortable: true },
      { key: 'letterGrade', label: 'Grade', type: 'badge' }
    ]
  };

  private apiEndpoints: { [key: string]: string } = {
    staff: '/api/v1/staff',
    attendance: '/api/attendance',
    parents: '/api/parents',
    subjects: '/api/subjects',
    rooms: '/api/rooms',
    exams: '/api/exams',
    courses: '/api/courses',
    assignments: '/api/assignments',
    grades: '/api/grades'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    // Get the entity type from the route path
    const routePath = this.route.snapshot.url[0]?.path || '';
    this.entityType = routePath;
    
    console.log(`========================================`);
    console.log(`EntityManagementComponent ngOnInit`);
    console.log(`Route Path: ${routePath}`);
    console.log(`Entity Type: ${this.entityType}`);
    console.log(`========================================`);
    
    this.initializeEntityType();
    this.loadData();
    
    // Also subscribe to route changes for navigation between entity types
    this.route.paramMap.subscribe(params => {
      const newRoutePath = this.route.snapshot.url[0]?.path || '';
      if (newRoutePath !== this.entityType) {
        console.log(`Route changed from ${this.entityType} to ${newRoutePath}`);
        this.entityType = newRoutePath;
        this.initializeEntityType();
        this.loadData();
      }
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
      case 'rooms':
        this.title = 'Rooms';
        this.entityName = 'Room';
        this.entityNamePlural = 'Rooms';
        this.columns = this.columnMappings['rooms'];
        break;
      case 'exams':
        this.title = 'Exams';
        this.entityName = 'Exam';
        this.entityNamePlural = 'Exams';
        this.columns = this.columnMappings['exams'];
        break;
      case 'courses':
        this.title = 'Courses';
        this.entityName = 'Course';
        this.entityNamePlural = 'Courses';
        this.columns = this.columnMappings['courses'];
        break;
      case 'assignments':
        this.title = 'Assignments';
        this.entityName = 'Assignment';
        this.entityNamePlural = 'Assignments';
        this.columns = this.columnMappings['assignments'];
        break;
      case 'grades':
        this.title = 'Grades';
        this.entityName = 'Grade';
        this.entityNamePlural = 'Grades';
        this.columns = this.columnMappings['grades'];
        break;
      default:
        this.title = 'Entity Management';
        this.entityName = 'Item';
        this.entityNamePlural = 'Items';
    }
  }

  // Force recompile - loadData method with detailed logging
  loadData(page: number = 1): void {
    console.log(`=== LOADDATA CALLED ===`);
    console.log(`Entity Type: ${this.entityType}`);
    console.log(`All API Endpoints:`, this.apiEndpoints);
    
    this.loading = true;
    const endpoint = this.apiEndpoints[this.entityType];
    
    console.log(`Resolved endpoint for '${this.entityType}': ${endpoint}`);
    
    if (!endpoint) {
      console.error(`❌ No API endpoint configured for entity type: ${this.entityType}`);
      console.error(`Available endpoints:`, Object.keys(this.apiEndpoints));
      this.loading = false;
      return;
    }

    const params = {
      page: page - 1,
      size: this.pagination.itemsPerPage
    };

    console.log(`🚀 Making HTTP GET request to: ${endpoint}`, params);

    // Use relative URL - request goes to the same origin as the page
    this.http.get<ApiResponse>(endpoint, { params }).subscribe({
      next: (response) => {
        console.log(`✅ Received response for ${this.entityType}:`, response);
        this.data = this.extractData(response);
        this.pagination.totalItems = this.extractTotalElements(response);
        this.pagination.totalPages = Math.ceil(
          this.pagination.totalItems / this.pagination.itemsPerPage
        );
        this.pagination.currentPage = page;
        this.loading = false;
        console.log(`✅ Loaded ${this.data.length} ${this.entityType} records`);
      },
      error: (error) => {
        console.error(`❌ Error loading ${this.entityType}:`, error);
        console.error(`Error details:`, {
          status: error.status,
          statusText: error.statusText,
          url: error.url,
          message: error.message
        });
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

  onRowClick(event: { entityType: string; item: any }): void {
    const { entityType, item } = event;
    const itemId = item.id;
    if (itemId) {
      this.router.navigate(['/entity-detail', entityType, itemId]);
    }
  }

  onActionClick(event: { action: string; item?: any; selectedItems?: any[] }): void {
    switch (event.action) {
      case 'import':
        console.log(`Import ${this.entityType} - navigating to import-wizard route`);
        this.router.navigate(['/import-wizard'], { 
          queryParams: { entityType: this.entityType } 
        }).then(success => {
          console.log('Navigation:', success ? 'SUCCESS' : 'FAILED');
        });
        break;
      case 'export':
        console.log(`Export ${this.entityType}`, event.selectedItems || 'all');
        // Implement export logic if needed
        break;
      case 'delete':
        if (event.item) {
          console.log(`Delete ${this.entityType}`, event.item);
          // Implement delete logic for single item
        } else if (event.selectedItems && event.selectedItems.length > 0) {
          console.log(`Delete ${event.selectedItems.length} ${this.entityType} items`, event.selectedItems);
          // Implement bulk delete logic
        }
        break;
      default:
        console.log('Unhandled action:', event.action);
    }
  }
}
