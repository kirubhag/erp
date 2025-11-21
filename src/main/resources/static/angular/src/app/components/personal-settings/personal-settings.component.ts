import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService, UserDetails } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

@Component({
  selector: 'app-personal-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, SettingsSidebarComponent],
  templateUrl: './personal-settings.component.html',
  styleUrls: ['./personal-settings.component.css']
})
export class PersonalSettingsComponent implements OnInit {
  currentUser: UserDetails | null = null;
  isEditing = false;
  isSaving = false;
  editForm: Partial<UserDetails> = {};
  successMessage = '';
  errorMessage = '';
  isUploadingAvatar = false;
  avatarUrl: string | null = null;
  hasAvatar = false;
  avatarLoadError = false;

  // Locale Information
  localeInfo = {
    language: 'English (United States)',
    countryLocale: 'United States',
    dateFormat: 'YYYY-MM-DD',
    timeFormat: '12 Hours',
    timeZone: '(GMT 5:30) India Standard Time (Asia/Kolkata)',
    numberFormat: '123,456.789'
  };

  // Display Name Format
  nameFormats = [
    'First Name, Last Name',
    'Last Name, First Name',
    'First Name Last Name'
  ];
  selectedNameFormat = 'First Name, Last Name';

  // Available Themes - now loaded from ThemeService
  themes: { name: string; color: string }[] = [];
  _selectedTheme = '#0099cc'; // Default to cyan

  get selectedTheme(): string {
    return this._selectedTheme;
  }

  set selectedTheme(theme: string) {
    this._selectedTheme = theme;
    this.saveTheme(theme);
  }

  constructor(
    private authService: AuthService,
    private themeService: ThemeService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadThemeFromService();
    this.themes = this.themeService.getAvailableThemes();
  }

  /**
   * Load theme from ThemeService with database persistence
   */
  loadThemeFromService(): void {
    this._selectedTheme = this.themeService.getTheme();
    
    // Load from database if user context is available
    if (this.currentUser?.id && this.currentUser?.organizationId) {
      this.themeService.loadThemeFromDatabase(
        this.currentUser.id,
        this.currentUser.organizationId
      );
    }
  }

  /**
   * Save theme using ThemeService (will persist to database)
   */
  saveTheme(theme: string): void {
    this.themeService.setTheme(
      theme,
      this.currentUser?.id,
      this.currentUser?.organizationId
    );
  }

  /**
   * Load current user details
   */
  loadCurrentUser(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUser = user;
        // Check if user has avatar URL in their profile
        this.hasAvatar = !!(user.avatarUrl);
        this.initializeEditForm();
        // Reload theme from database after user is loaded
        if (user.id && user.organizationId) {
          this.themeService.loadThemeFromDatabase(user.id, user.organizationId);
        }
      } else {
        // Try to fetch from a default user or show message
        this.currentUser = {
          username: 'admin',
          firstName: 'Admin',
          lastName: 'User',
          email: 'admin@example.com',
          phone: '9876543210',
          userType: 'ADMIN'
        };
        this.hasAvatar = false;
        this.initializeEditForm();
      }
    });
  }

  /**
   * Initialize edit form with current user data
   */
  initializeEditForm(): void {
    if (this.currentUser) {
      this.editForm = { ...this.currentUser };
    }
  }

  /**
   * Start editing mode
   */
  startEdit(): void {
    this.isEditing = true;
    this.successMessage = '';
    this.errorMessage = '';
    this.initializeEditForm();
  }

  /**
   * Cancel editing
   */
  cancelEdit(): void {
    this.isEditing = false;
    this.successMessage = '';
    this.errorMessage = '';
    this.initializeEditForm();
  }

  /**
   * Save user profile changes
   */
  saveProfile(): void {
    if (!this.currentUser?.id) {
      this.errorMessage = 'User ID not found';
      return;
    }

    this.isSaving = true;
    this.authService.updateUserProfile(this.currentUser.id, this.editForm).subscribe({
      next: (updatedUser) => {
        this.currentUser = updatedUser;
        this.isEditing = false;
        this.isSaving = false;
        this.successMessage = 'Profile updated successfully!';
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (err) => {
        this.isSaving = false;
        this.errorMessage = err.error?.message || 'Failed to update profile. Please try again.';
        console.error('Error updating profile:', err);
      }
    });
  }

  /**
   * Get full name
   */
  getFullName(): string {
    if (!this.currentUser) return '';
    switch (this.selectedNameFormat) {
      case 'Last Name, First Name':
        return `${this.currentUser.lastName}, ${this.currentUser.firstName}`;
      case 'First Name Last Name':
        return `${this.currentUser.firstName} ${this.currentUser.lastName}`;
      default:
        return `${this.currentUser.firstName} ${this.currentUser.lastName}`;
    }
  }

  /**
   * Get user initials for avatar
   */
  getUserInitials(): string {
    if (!this.currentUser) return '?';
    const first = this.currentUser.firstName?.charAt(0) || '';
    const last = this.currentUser.lastName?.charAt(0) || '';
    return (first + last).toUpperCase();
  }

  /**
   * Get user type badge color
   */
  getUserTypeBadgeColor(): string {
    switch (this.currentUser?.userType) {
      case 'ADMIN':
        return 'danger';
      case 'STAFF':
        return 'primary';
      case 'STUDENT':
        return 'info';
      case 'PARENT':
        return 'success';
      default:
        return 'secondary';
    }
  }

  /**
   * Open specific tab
   */
  openTab(tabName: string): void {
    // Hide all tab contents
    const tabContents = document.querySelectorAll('.personal-settings-tab, .accessibility-tab');
    tabContents.forEach(content => {
      (content as HTMLElement).style.display = 'none';
    });

    // Remove active class from all tab links
    const tabLinks = document.querySelectorAll('.nav-link');
    tabLinks.forEach(link => {
      link.classList.remove('active');
    });

    // Show selected tab content
    if (tabName === 'personal-settings') {
      const personalTab = document.querySelector('.personal-settings-tab') as HTMLElement;
      if (personalTab) personalTab.style.display = 'block';
    } else if (tabName === 'accessibility') {
      const accessibilityTab = document.querySelector('.accessibility-tab') as HTMLElement;
      if (accessibilityTab) accessibilityTab.style.display = 'block';
    }

    // Add active class to clicked tab link
    const activeLink = Array.from(tabLinks).find(link => {
      const button = link as HTMLElement;
      return button.textContent?.toLowerCase().includes(tabName.split('-')[0]);
    });
    if (activeLink) {
      activeLink.classList.add('active');
    }
  }

  /**
   * Handle avatar file selection
   */
  onAvatarSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];
    
    // Validate file type
    if (!file.type.startsWith('image/')) {
      this.errorMessage = 'Please select an image file';
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      this.errorMessage = 'Image size must be less than 5MB';
      return;
    }

    this.uploadAvatar(file);
  }

  /**
   * Upload avatar to server
   */
  uploadAvatar(file: File): void {
    if (!this.currentUser?.id || !this.currentUser?.organizationId) {
      this.errorMessage = 'User information not available';
      return;
    }

    this.isUploadingAvatar = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', this.currentUser.id.toString());
    formData.append('organizationId', this.currentUser.organizationId.toString());

    this.http.post<any>('/api/attachments/avatar/upload', formData).subscribe({
      next: (response) => {
        this.isUploadingAvatar = false;
        this.successMessage = 'Avatar uploaded successfully!';
        // Update avatar URL with cache-busting timestamp
        const timestamp = Date.now();
        this.avatarUrl = response.attachment.url + '?t=' + timestamp;
        this.hasAvatar = true;
        this.avatarLoadError = false;
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (err) => {
        this.isUploadingAvatar = false;
        this.errorMessage = err.error?.message || 'Failed to upload avatar';
        console.error('Error uploading avatar:', err);
      }
    });
  }

  /**
   * Get avatar URL - returns stable URL to avoid change detection errors
   */
  getAvatarUrl(): string {
    if (this.avatarUrl) {
      return this.avatarUrl;
    }
    
    if (this.currentUser?.id && this.hasAvatar && !this.avatarLoadError) {
      // Use a stable URL without timestamp to avoid change detection errors
      return `/api/attachments/avatar/${this.currentUser.id}`;
    }
    
    // Default placeholder with user initials
    const initials = this.getUserInitials();
    return `https://placehold.co/80x80/E8F0FE/333?text=${initials}`;
  }

  /**
   * Handle avatar load error
   */
  onAvatarError(): void {
    this.avatarLoadError = true;
    this.hasAvatar = false;
  }
}
