import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
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
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      userType: [''],
      enabled: [true]
    }, { validators: this.passwordMatchValidator });
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
    this.userForm.reset({ enabled: true });
    this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
    this.userForm.get('confirmPassword')?.setValidators([Validators.required]);
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
    
    // Password is optional when editing
    this.userForm.get('password')?.setValidators([Validators.minLength(6)]);
    this.userForm.get('confirmPassword')?.setValidators([]);
    
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
    
    // Clear password fields
    this.userForm.get('password')?.reset();
    this.userForm.get('confirmPassword')?.reset();
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
    
    // Remove confirmPassword from request payload
    const userPayload: any = {
      username: formValue.username,
      firstName: formValue.firstName || '',
      lastName: formValue.lastName || '',
      email: formValue.email,
      phone: formValue.phone || '',
      userType: formValue.userType || '',
      enabled: formValue.enabled
    };

    if (formValue.password) {
      userPayload.password = formValue.password;
    }

    const operation = this.isEditMode && this.selectedUser 
      ? this.userService.updateUser(this.selectedUser.id, userPayload)
      : this.userService.createUser(userPayload);

    operation.subscribe({
      next: (response: any) => {
        this.loading = false;
        this.successMessage = this.isEditMode ? 'User updated successfully!' : 'User created successfully!';
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

  /**
   * Password match validator
   */
  passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password');
    const confirmPassword = group.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }
}
