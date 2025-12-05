import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { DashboardService, DashboardStats } from '../../services/dashboard.service';
import { SampleDataModalComponent } from '../sample-data-modal/sample-data-modal.component';
import { OrganizationCreationModalComponent } from '../organization-creation-modal/organization-creation-modal.component';
import { AuthService } from '../../services/auth.service';

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
    totalAttendance: 0
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
    private authService: AuthService
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

  checkOrganizationStatus() {
    const user = this.authService.getCurrentUser();
    // If user exists but has no organization ID, show the creation modal.
    // If organizationId is present (i.e., organization table entry exists for this user), the modal remains hidden.
    if (user && !user.organizationId) {
      this.showOrganizationModal = true;
    }
  }

  onOrganizationCreated(organization: any) {
    this.showOrganizationModal = false;
    // Update user with new organization ID
    const user = this.authService.getCurrentUser();
    if (user) {
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
