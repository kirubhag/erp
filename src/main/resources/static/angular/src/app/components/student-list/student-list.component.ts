import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EntityListComponent, EntityColumn, EntityFilter, PaginationInfo } from '../entity-list/entity-list.component';

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
      [columns]="columns"
      [data]="students"
      [filters]="filters"
      [pagination]="pagination"
      [loading]="loading"
      [showFilters]="showFilters"
      (searchChange)="onSearch($event)"
      (filterChange)="onFilterChange($event)"
      (sortChange)="onSort($event)"
      (pageChange)="onPageChange($event)"
      (actionClick)="onAction($event)"
      (selectionChange)="onSelectionChange($event)">
    </app-entity-list>
  `
})
export class StudentListComponent implements OnInit {
  students: Student[] = [];
  loading = false;
  showFilters = true; // Show filters by default

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
      label: 'Active',
      type: 'checkbox',
      value: true
    },
    {
      key: 'createdAt',
      label: 'Created At',
      type: 'checkbox',
      value: false
    },
    {
      key: 'updatedAt',
      label: 'Updated At',
      type: 'checkbox',
      value: false
    },
    {
      key: 'firstName',
      label: 'First Name',
      type: 'checkbox',
      value: false
    },
    {
      key: 'lastName',
      label: 'Last Name',
      type: 'checkbox',
      value: false
    },
    {
      key: 'middleName',
      label: 'Middle Name',
      type: 'checkbox',
      value: false
    },
    {
      key: 'dateOfBirth',
      label: 'Date of Birth',
      type: 'checkbox',
      value: false
    },
    {
      key: 'gender',
      label: 'Gender',
      type: 'checkbox',
      value: false
    },
    {
      key: 'studentId',
      label: 'Student ID',
      type: 'checkbox',
      value: false
    }
  ];

  pagination: PaginationInfo = {
    currentPage: 1,
    itemsPerPage: 100,
    totalItems: 358,
    totalPages: 4
  };

  ngOnInit() {
    this.loadStudents();
  }

  loadStudents() {
    this.loading = true;
    
    // Simulate API call
    setTimeout(() => {
      this.students = [
        {
          id: 1,
          studentId: 'KGSTU016',
          firstName: 'Patrick',
          lastName: 'Parker',
          fullName: 'Patrick Parker',
          email: 'patrick.parker@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        },
        {
          id: 2,
          studentId: 'KGSTU017',
          firstName: 'Queenie',
          lastName: 'Quinn',
          fullName: 'Queenie Quinn',
          email: 'queenie.quinn@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        },
        {
          id: 3,
          studentId: 'KGSTU018',
          firstName: 'Roman',
          lastName: 'Roberts',
          fullName: 'Roman Roberts',
          email: 'roman.roberts@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        },
        {
          id: 4,
          studentId: 'KGSTU019',
          firstName: 'Sophia',
          lastName: 'Smith',
          fullName: 'Sophia Smith',
          email: 'sophia.smith@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        },
        {
          id: 5,
          studentId: 'KGSTU020',
          firstName: 'Theodore',
          lastName: 'Taylor',
          fullName: 'Theodore Taylor',
          email: 'theodore.taylor@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        },
        {
          id: 6,
          studentId: 'KGSTU021',
          firstName: 'Uma',
          lastName: 'Turner',
          fullName: 'Uma Turner',
          email: 'uma.turner@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        },
        {
          id: 7,
          studentId: 'KGSTU022',
          firstName: 'Vincent',
          lastName: 'White',
          fullName: 'Vincent White',
          email: 'vincent.white@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        },
        {
          id: 8,
          studentId: 'KGSTU023',
          firstName: 'Willa',
          lastName: 'Wilson',
          fullName: 'Willa Wilson',
          email: 'willa.wilson@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        },
        {
          id: 9,
          studentId: 'KGSTU024',
          firstName: 'Xavier',
          lastName: 'Young',
          fullName: 'Xavier Young',
          email: 'xavier.young@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        },
        {
          id: 10,
          studentId: 'KGSTU025',
          firstName: 'Yelena',
          lastName: 'Adams',
          fullName: 'Yelena Adams',
          email: 'yelena.adams@student.school.edu',
          phone: '',
          grade: 'KINDERGARTEN',
          status: 'ACTIVE',
          enrollmentDate: '2024-08-15'
        }
      ];
      this.loading = false;
    }, 1000);
  }

  onSearch(searchTerm: string) {
    console.log('Search:', searchTerm);
    // Implement search logic
  }

  onFilterChange(filters: { [key: string]: any }) {
    console.log('Filters:', filters);
    // Implement filter logic
  }

  onSort(sort: { column: string; direction: 'asc' | 'desc' }) {
    console.log('Sort:', sort);
    // Implement sort logic
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
      case 'edit':
        console.log('Edit student:', event.item);
        break;
      case 'delete':
        console.log('Delete students:', event.selectedItems);
        break;
    }
  }

  onSelectionChange(selectedItems: any[]) {
    console.log('Selected items:', selectedItems);
  }
}