import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

export interface Profile {
  id: number;
  name: string;
  description: string;
  usersCount: number;
  createdDate: string;
  lastModified: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SettingsSidebarComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  searchTerm = '';
  profiles: Profile[] = [];

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadProfiles();
  }

  loadProfiles() {
    // Mock data for profiles
    this.profiles = [
      {
        id: 1,
        name: 'Administrator',
        description: 'Full system access with all permissions',
        usersCount: 2,
        createdDate: '2024-01-15',
        lastModified: '2024-11-01'
      },
      {
        id: 2,
        name: 'Manager',
        description: 'Can manage users and view reports',
        usersCount: 5,
        createdDate: '2024-02-10',
        lastModified: '2024-10-20'
      },
      {
        id: 3,
        name: 'Supervisor',
        description: 'Can view and manage assigned records',
        usersCount: 8,
        createdDate: '2024-03-05',
        lastModified: '2024-11-05'
      },
      {
        id: 4,
        name: 'User',
        description: 'Basic user with limited access',
        usersCount: 45,
        createdDate: '2024-01-20',
        lastModified: '2024-10-30'
      },
      {
        id: 5,
        name: 'Guest',
        description: 'Read-only access to specific modules',
        usersCount: 12,
        createdDate: '2024-04-10',
        lastModified: '2024-11-02'
      }
    ];
  }

  viewProfile(profile: Profile) {
    this.router.navigate(['/setup/profile-detail', profile.id]);
  }

  createProfile() {
    this.router.navigate(['/setup/profile-detail', 'new']);
  }

  deleteProfile(profile: Profile) {
    if (confirm(`Are you sure you want to delete "${profile.name}" profile?`)) {
      this.profiles = this.profiles.filter(p => p.id !== profile.id);
    }
  }

  get filteredProfiles(): Profile[] {
    if (!this.searchTerm) {
      return this.profiles;
    }
    return this.profiles.filter(profile =>
      profile.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      profile.description.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }
}
