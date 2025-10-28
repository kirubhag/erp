import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService, DashboardStats } from '../../services/dashboard.service';

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
  imports: [CommonModule],
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

  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.loadDashboardStats();
  }

  loadDashboardStats() {
    this.dashboardService.getDashboardStats().subscribe({
      next: (data: DashboardStats) => {
        this.stats = data;
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

