import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, UserDetails } from '../../services/auth.service';
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

  // Available Themes
  themes = [
    { name: 'Dark Red', color: '#660000' },
    { name: 'Red', color: '#990000' },
    { name: 'Red Light', color: '#D24143' },
    { name: 'Red Salmon', color: '#DE4F5D' },
    { name: 'Pink', color: '#ea4c88' },
    { name: 'Purple', color: '#993399' },
    { name: 'Purple Dark', color: '#663399' },
    { name: 'Navy', color: '#07385D' },
    { name: 'Blue Dark', color: '#1e5598' },
    { name: 'Blue', color: '#2d72d9' },
    { name: 'Blue Light', color: '#018EE0' },
    { name: 'Cyan', color: '#0099cc' },
    { name: 'Teal', color: '#37a5a5' },
    { name: 'Green', color: '#439454' },
    { name: 'Green Dark', color: '#336600' },
    { name: 'Green Teal', color: '#165151' },
    { name: 'Olive', color: '#999900' },
    { name: 'Orange', color: '#E9A23F' },
    { name: 'Orange Dark', color: '#E77817' },
    { name: 'Brown', color: '#996633' },
    { name: 'Mauve', color: '#553A48' },
    { name: 'Gray', color: '#313949' }
  ];
  _selectedTheme = '#0099cc'; // Default to cyan

  get selectedTheme(): string {
    return this._selectedTheme;
  }

  set selectedTheme(theme: string) {
    this._selectedTheme = theme;
    this.saveTheme(theme);
  }

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadThemeFromStorage();
  }

  /**
   * Load theme from localStorage
   */
  loadThemeFromStorage(): void {
    const savedTheme = localStorage.getItem('selectedTheme');
    if (savedTheme) {
      this._selectedTheme = savedTheme;
      this.applyTheme(savedTheme);
    } else {
      // Apply default cyan theme
      this.applyTheme(this._selectedTheme);
    }
  }

  /**
   * Save theme to localStorage and apply it
   */
  saveTheme(theme: string): void {
    localStorage.setItem('selectedTheme', theme);
    this.applyTheme(theme);
  }

  /**
   * Apply theme to document using CSS variables
   */
  applyTheme(color: string): void {
    // Set the main CSS variable - all components reference this
    document.documentElement.style.setProperty('--app-primary', color);
    // All themed elements will automatically update through CSS cascade
  }

  /**
   * Load current user details
   */
  loadCurrentUser(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUser = user;
        this.initializeEditForm();
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
}
