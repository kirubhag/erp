import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { UserService } from '../../services/user.service';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

export interface User {
  id: number;
  username: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  userType?: string;
  enabled: boolean;
  accountNonExpired?: boolean;
  credentialsNonExpired?: boolean;
  accountNonLocked?: boolean;
  lastLoginDate?: string;
  passwordChangeDate?: string;
  roles?: any[];
  isPrimaryUser?: boolean;
}

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, HttpClientModule, SettingsSidebarComponent],
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
  providers: [UserService]
})
export class UserComponent implements OnInit {
  users: User[] = [];
  selectedUser: User | null = null;
  searchQuery = '';
  loading = false;
  errorMessage = '';
  successMessage = '';
  showUserForm = false;
  isEditMode = false;

  userForm: FormGroup;

  constructor(private userService: UserService, private formBuilder: FormBuilder) {
    this.userForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      firstName: [''],
      lastName: [''],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      userType: ['STAFF'],
      enabled: [true]
    });
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  /**
   * Load all active users from the database
   */
  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';

    this.userService.getAllUsers().subscribe({
      next: (users: User[]) => {
        this.users = users;
        this.loading = false;
        if (this.users.length > 0) {
          this.selectUser(this.users[0]);
        }
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = 'Failed to load users. Please try again.';
        console.error('Error loading users:', error);
      }
    });
  }

  /**
   * Select a user to display details
   */
  selectUser(user: User): void {
    this.selectedUser = user;
    this.showUserForm = false;
  }

  /**
   * Open form to add new user
   */
  openAddUserForm(): void {
    this.isEditMode = false;
    this.showUserForm = true;
    this.userForm.reset({ enabled: true, userType: 'STAFF' });
  }

  /**
   * Open form to edit selected user
   */
  openEditUserForm(): void {
    if (!this.selectedUser) {
      return;
    }

    this.isEditMode = true;
    this.showUserForm = true;

    // Populate form with selected user data
    this.userForm.patchValue({
      username: this.selectedUser.username,
      firstName: this.selectedUser.firstName,
      lastName: this.selectedUser.lastName,
      email: this.selectedUser.email,
      phone: this.selectedUser.phone,
      userType: this.selectedUser.userType,
      enabled: this.selectedUser.enabled
    });
  }

  /**
   * Close user form
   */
  closeUserForm(): void {
    this.showUserForm = false;
    this.userForm.reset();
    this.errorMessage = '';
    this.successMessage = '';
  }

  /**
   * Save user (create new or update existing)
   */
  saveUser(): void {
    if (!this.userForm.valid) {
      this.errorMessage = 'Please fill all required fields correctly';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.userForm.value;

    // Build request payload (no password - user will set via invitation)
    const userPayload: any = {
      username: formValue.username,
      firstName: formValue.firstName || '',
      lastName: formValue.lastName || '',
      email: formValue.email,
      phone: formValue.phone || '',
      userType: formValue.userType || 'STAFF',
      enabled: formValue.enabled
    };

    const operation = this.isEditMode && this.selectedUser
      ? this.userService.updateUser(this.selectedUser.id, userPayload)
      : this.userService.createUser(userPayload);

    operation.subscribe({
      next: (response: any) => {
        this.loading = false;
        if (this.isEditMode) {
          this.successMessage = 'User updated successfully!';
        } else {
          this.successMessage = 'User created! An invitation email has been sent.';
        }
        this.closeUserForm();
        this.loadUsers();
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Failed to save user. Please try again.';
        console.error('Error saving user:', error);
      }
    });
  }

  /**
   * Activate user
   */
  activateUser(): void {
    if (!this.selectedUser) {
      return;
    }

    this.loading = true;
    const updatedUser = { ...this.selectedUser, enabled: true };

    this.userService.updateUser(this.selectedUser.id, updatedUser).subscribe({
      next: (response: User) => {
        this.loading = false;
        this.successMessage = 'User activated successfully!';
        this.selectedUser = response;
        this.loadUsers();
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = 'Failed to activate user. Please try again.';
        console.error('Error activating user:', error);
      }
    });
  }

  /**
   * Deactivate user
   */
  deactivateUser(): void {
    if (!this.selectedUser) {
      return;
    }

    if (confirm('Are you sure you want to deactivate this user?')) {
      this.loading = true;
      const updatedUser = { ...this.selectedUser, enabled: false };

      this.userService.updateUser(this.selectedUser.id, updatedUser).subscribe({
        next: (response: User) => {
          this.loading = false;
          this.successMessage = 'User deactivated successfully!';
          this.selectedUser = response;
          this.loadUsers();
        },
        error: (error: any) => {
          this.loading = false;
          this.errorMessage = 'Failed to deactivate user. Please try again.';
          console.error('Error deactivating user:', error);
        }
      });
    }
  }

  /**
   * Re-invite user
   */
  reinviteUser(): void {
    if (!this.selectedUser) {
      return;
    }

    if (confirm('Send invitation email again to this user?')) {
      this.loading = true;
      this.userService.reinviteUser(this.selectedUser.id).subscribe({
        next: () => {
          this.loading = false;
          this.successMessage = 'Invitation email sent successfully!';
        },
        error: (error: any) => {
          this.loading = false;
          this.errorMessage = 'Failed to send invitation. Please try again.';
          console.error('Error re-inviting user:', error);
        }
      });
    }
  }

  /**
   * Delete user
   */
  deleteUser(): void {
    if (!this.selectedUser) {
      return;
    }

    if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
      this.loading = true;

      this.userService.deleteUser(this.selectedUser.id).subscribe({
        next: () => {
          this.loading = false;
          this.successMessage = 'User deleted successfully!';
          this.selectedUser = null;
          this.loadUsers();
        },
        error: (error: any) => {
          this.loading = false;
          this.errorMessage = 'Failed to delete user. Please try again.';
          console.error('Error deleting user:', error);
        }
      });
    }
  }

  /**
   * Search users by name or email
   */
  searchUsers(): void {
    if (!this.searchQuery.trim()) {
      this.loadUsers();
      return;
    }

    this.loading = true;
    this.userService.searchUsers(this.searchQuery).subscribe({
      next: (users: User[]) => {
        this.users = users;
        this.loading = false;
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = 'Failed to search users.';
        console.error('Error searching users:', error);
      }
    });
  }

  /**
   * Filter users by status
   */
  get filteredUsers(): User[] {
    return this.users;
  }

  /**
   * Get full name of user
   */
  getFullName(user: User): string {
    const firstName = user.firstName || '';
    const lastName = user.lastName || '';
    return `${firstName} ${lastName}`.trim() || user.username;
  }
}
