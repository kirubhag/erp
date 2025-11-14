import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

export interface Permission {
  id: string;
  name: string;
  description: string;
  enabled: boolean;
}

@Component({
  selector: 'app-profile-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, SettingsSidebarComponent],
  templateUrl: './profile-detail.component.html',
  styleUrls: ['./profile-detail.component.css']
})
export class ProfileDetailComponent implements OnInit {
  profileForm: FormGroup;
  profileId: string | null = null;
  activeTab = 'basic';
  permissions: Permission[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.profileForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: [''],
      enabled: [true]
    });
  }

  ngOnInit() {
    this.loadPermissions();
    this.route.paramMap.subscribe(params => {
      this.profileId = params.get('id');
      if (this.profileId && this.profileId !== 'new') {
        this.loadProfileData(this.profileId);
      }
    });
  }

  loadProfileData(id: string) {
    // Mock data loading
    const mockProfiles: any = {
      '1': { name: 'Administrator', description: 'Full system access with all permissions', enabled: true },
      '2': { name: 'Manager', description: 'Can manage users and view reports', enabled: true },
      '3': { name: 'Supervisor', description: 'Can view and manage assigned records', enabled: true },
      '4': { name: 'User', description: 'Basic user with limited access', enabled: true },
      '5': { name: 'Guest', description: 'Read-only access to specific modules', enabled: true }
    };

    if (mockProfiles[id]) {
      this.profileForm.patchValue(mockProfiles[id]);
      // For admin, enable all permissions
      if (id === '1') {
        this.permissions = this.permissions.map(p => ({ ...p, enabled: true }));
      }
    }
  }

  loadPermissions() {
    this.permissions = [
      { id: 'create', name: 'Create Records', description: 'Can create new records', enabled: false },
      { id: 'read', name: 'Read Records', description: 'Can view records', enabled: true },
      { id: 'update', name: 'Update Records', description: 'Can edit existing records', enabled: false },
      { id: 'delete', name: 'Delete Records', description: 'Can delete records', enabled: false },
      { id: 'export', name: 'Export Data', description: 'Can export records to files', enabled: false },
      { id: 'import', name: 'Import Data', description: 'Can import records from files', enabled: false },
      { id: 'manage_users', name: 'Manage Users', description: 'Can manage user accounts', enabled: false },
      { id: 'view_reports', name: 'View Reports', description: 'Can access reports and analytics', enabled: true },
      { id: 'manage_settings', name: 'Manage Settings', description: 'Can modify system settings', enabled: false },
      { id: 'audit_log', name: 'View Audit Log', description: 'Can view audit logs', enabled: false }
    ];
  }

  togglePermission(permission: Permission) {
    permission.enabled = !permission.enabled;
  }

  saveProfile() {
    if (this.profileForm.valid) {
      console.log('Saving profile:', this.profileForm.value);
      alert('Profile saved successfully!');
      this.router.navigate(['/setup/profiles']);
    }
  }

  cancel() {
    this.router.navigate(['/setup/profiles']);
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }
}
