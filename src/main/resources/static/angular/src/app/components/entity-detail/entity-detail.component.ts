import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-entity-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './entity-detail.component.html',
  styleUrls: ['./entity-detail.component.css']
})
export class EntityDetailComponent implements OnInit {
  entityType: string = '';
  entityId: string = '';
  entityData: any = {};
  loading: boolean = true;
  error: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    // Get entity type and ID from route params
    this.route.paramMap.subscribe(params => {
      this.entityType = params.get('entityType') || '';
      this.entityId = params.get('id') || '';
      
      if (this.entityType && this.entityId) {
        this.loadEntityData();
      } else {
        this.error = 'Invalid entity type or ID';
        this.loading = false;
      }
    });
  }

  private loadEntityData(): void {
    this.loading = true;
    const endpoint = this.getApiEndpoint();
    
    if (!endpoint) {
      this.error = `No API endpoint configured for entity type: ${this.entityType}`;
      this.loading = false;
      return;
    }

    const backendUrl = `http://localhost:8081${endpoint}`;

    this.http.get<any>(backendUrl).subscribe({
      next: (response) => {
        this.entityData = response;
        this.loading = false;
      },
      error: (error) => {
        console.error(`Error loading ${this.entityType} detail:`, error);
        this.error = `Failed to load ${this.entityType} details`;
        this.loading = false;
      }
    });
  }

  private getApiEndpoint(): string {
    const endpoints: { [key: string]: string } = {
      students: `/api/students/${this.entityId}`,
      staff: `/api/v1/staff/${this.entityId}`,
      attendance: `/api/attendance/${this.entityId}`,
      parents: `/api/parents/${this.entityId}`,
      subjects: `/api/subjects/${this.entityId}`
    };

    return endpoints[this.entityType] || '';
  }

  goBack(): void {
    // Map entity types to their correct routes
    const routeMap: { [key: string]: string } = {
      students: '/students',
      staff: '/staff',
      attendance: '/attendance',
      parents: '/parents',
      subjects: '/subjects'
    };
    
    const route = routeMap[this.entityType] || '/dashboard';
    this.router.navigate([route]);
  }

  getPageTitle(): string {
    const titles: { [key: string]: string } = {
      students: 'Student Details',
      staff: 'Staff Member',
      attendance: 'Attendance Record',
      parents: 'Parent',
      subjects: 'Subject'
    };
    return titles[this.entityType] || 'Entity Details';
  }

  getEntityLabel(key: string): string {
    // Convert camelCase to Title Case
    return key
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, str => str.toUpperCase())
      .trim();
  }

  getDisplayValue(value: any): string {
    if (value === null || value === undefined) return '—';
    if (typeof value === 'boolean') return value ? 'Yes' : 'No';
    if (value instanceof Date) return value.toLocaleDateString();
    return String(value);
  }

  getEntityKeys(): string[] {
    if (!this.entityData) return [];
    return Object.keys(this.entityData).filter(key => 
      !key.startsWith('_') && typeof this.entityData[key] !== 'object'
    );
  }

  getEntityIconClass(): string {
    const icons: { [key: string]: string } = {
      students: 'fas fa-user-graduate',
      staff: 'fas fa-users-cog',
      attendance: 'fas fa-clipboard-check',
      parents: 'fas fa-home',
      subjects: 'fas fa-book'
    };
    return icons[this.entityType] || 'fas fa-file';
  }

  getStatusBadge(): string {
    if (this.entityData.isActive === true || this.entityData.status === 'ACTIVE') {
      return 'Active';
    }
    if (this.entityData.isActive === false || this.entityData.status === 'INACTIVE') {
      return 'Inactive';
    }
    return this.entityData.status || 'Active';
  }

  toggleDetailsVisibility(event: any): void {
    const header = event.currentTarget;
    const icon = header.querySelector('i');
    const details = header.nextElementSibling;
    
    if (icon.classList.contains('fa-chevron-up')) {
      icon.classList.remove('fa-chevron-up');
      icon.classList.add('fa-chevron-down');
      header.querySelector('span').textContent = 'Show Details';
      if (details) {
        details.style.display = 'none';
      }
    } else {
      icon.classList.remove('fa-chevron-down');
      icon.classList.add('fa-chevron-up');
      header.querySelector('span').textContent = 'Hide Details';
      if (details) {
        details.style.display = 'block';
      }
    }
  }
}
