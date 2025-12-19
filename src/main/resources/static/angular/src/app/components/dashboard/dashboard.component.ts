import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { DashboardService, DashboardStats } from '../../services/dashboard.service';
import { SampleDataModalComponent } from '../sample-data-modal/sample-data-modal.component';
import { OrganizationCreationModalComponent } from '../organization-creation-modal/organization-creation-modal.component';
import { AuthService } from '../../services/auth.service';
import { OrganizationService } from '../../services/organization.service';

interface QuickAction {
  icon: string;
  title: string;
  description: string;
  route?: string;
}

interface Activity {
  message: string;
  timeAgo: string;
  icon: string;
  iconClass: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, SampleDataModalComponent, OrganizationCreationModalComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalStudents: 378,
    presentToday: 0,
    absentToday: 0,
    registeredParents: 0,
    attendanceRate: 0,
    totalAttendance: 0,
    totalStaff: 0,
    totalAssets: 0,
    pendingWorkOrders: 0,
    monthlyRevenue: 0,
    monthlyExpense: 0
  };

  showSampleDataModal = false;
  showOrganizationModal = false;

  quickActions: QuickAction[] = [
    {
      icon: 'fas fa-user-plus',
      title: 'Add New Student',
      description: 'Register a new student in the system',
      route: '/students/new'
    },
    {
      icon: 'fas fa-calendar-check',
      title: 'Mark Attendance',
      description: 'Record daily attendance for classes',
      route: '/attendance'
    },
    {
      icon: 'fas fa-users',
      title: 'Parent Registration',
      description: 'Register new parent for portal access',
      route: '/parents/new'
    },
    {
      icon: 'fas fa-heartbeat',
      title: 'Health Records',
      description: 'Manage student health information',
      route: '/health'
    }
  ];

  recentActivities: Activity[] = [
    {
      message: 'New student John Doe enrolled in 5th grade',
      timeAgo: '2 hours ago',
      icon: 'fas fa-user-graduate',
      iconClass: 'text-success'
    }
  ];

  systemStatus = 'Online';
  currentDate = new Date();

  constructor(
    private dashboardService: DashboardService,
    private route: ActivatedRoute,
    private authService: AuthService,
    private organizationService: OrganizationService
  ) { }

  ngOnInit() {
    // Check for loadSampleData query parameter
    this.route.queryParams.subscribe(params => {
      if (params['loadSampleData'] === 'true') {
        this.showSampleDataModal = true;
      }
    });

    // Show default stats immediately
    console.log('Dashboard initialized with default stats');

    // Check if user has organization
    this.checkOrganizationStatus();

    // Load data in background (non-blocking)
    // Commented out for now to debug infinite loading
    // setTimeout(() => this.loadDashboardStats(), 100);
  }

  currentOrganization: any = null;

  checkOrganizationStatus() {
    const user = this.authService.getCurrentUser();

    if (user && user.organizationId) {
      // User has an organization ID, fetch details to check if it's complete
      this.organizationService.getOrganizationById(user.organizationId).subscribe({
        next: (org) => {
          this.currentOrganization = org;
          // Check if organization has minimal required details (e.g. city, country)
          // If not, show modal to complete setup
          if (!org.city || !org.country) {
            console.log('Organization details incomplete, showing setup modal');
            this.showOrganizationModal = true;
          }
        },
        error: (err) => {
          console.error('Failed to fetch organization details', err);
        }
      });
    } else if (user && !user.organizationId) {
      // No organization linked at all
      this.showOrganizationModal = true;
    }
  }

  onOrganizationCreated(organization: any) {
    this.showOrganizationModal = false;
    // Update user with new organization ID if needed
    const user = this.authService.getCurrentUser();
    if (user && !user.organizationId) {
      user.organizationId = organization.id;
      this.authService.setCurrentUser(user);
    }
    // Reload dashboard
    this.loadDashboardStats();
  }

  loadDashboardStats() {
    this.dashboardService.getDashboardStats().subscribe({
      next: (data: DashboardStats) => {
        this.stats = data;
        console.log('Dashboard stats loaded');
      },
      error: (error: any) => {
        console.error('Error loading dashboard stats:', error);
      }
    });
  }

  refresh() {
    this.loadDashboardStats();
  }
}
