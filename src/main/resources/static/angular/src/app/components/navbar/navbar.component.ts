import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MenuService } from '../../services/menu.service';
import { AuthService, UserDetails } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';

export interface MenuItem {
  id: number;
  systemName: string;
  pluralName: string;
  singularName: string;
  icon: string;
  route: string;
  sequence: number;
  isActive: boolean;
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  menuItems: MenuItem[] = [];
  visibleMenuItems: MenuItem[] = [];
  overflowMenuItems: MenuItem[] = [];
  hasOverflow = false;
  organizationName = 'Zylker';
  currentUser: UserDetails | null = null;
  selectedTheme = '#0056b3';

  constructor(
    private menuService: MenuService,
    private authService: AuthService,
    private themeService: ThemeService,
    private router: Router
  ) {}

  ngOnInit() {
    // Set default menu items immediately (synchronous)
    this.setDefaultMenuItems();
    console.log('Navbar initialized with default menu items');
    
    // Load current user
    this.loadCurrentUser();
    
    // Load theme from service (will load from localStorage + database if available)
    this.loadTheme();
  }

  /**
   * Load current user from AuthService
   */
  loadCurrentUser(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUser = user;
        // Reload theme from database with user context
        if (user.id && user.organizationId) {
          this.themeService.loadThemeFromDatabase(user.id, user.organizationId);
        }
      }
    });
  }

  /**
   * Load theme using ThemeService
   */
  loadTheme(): void {
    // Get current theme from service
    this.selectedTheme = this.themeService.getTheme();
    
    // Subscribe to theme changes
    this.themeService.getTheme$().subscribe(theme => {
      this.selectedTheme = theme;
    });
  }

  loadMenuItemsAsync(): void {
    setTimeout(() => {
      this.loadMenuItems();
    }, 0);
  }

  loadMenuItems(): void {
    this.menuService.getMenuItems().subscribe({
      next: (items) => {
        this.menuItems = items.map(item => ({
          ...item,
          route: this.convertRoute(item.route)
        }));
        
        this.visibleMenuItems = this.menuItems.filter(item => item.isActive);
        console.log('Menu items loaded from API:', items.length);
      },
      error: (error) => {
        console.error('Error loading menu items:', error);
        // Default already set in ngOnInit
      }
    });
  }

  trackByMenuItem(index: number, item: MenuItem): number {
    return item.id;
  }

  convertRoute(angularJsRoute: string): string {
    // Convert AngularJS routes (#!/path) to Angular routes (/path)
    if (angularJsRoute.startsWith('#!/')) {
      const path = angularJsRoute.substring(2); // Remove '#!'
      return path === '/' ? '/dashboard' : path;
    }
    return angularJsRoute;
  }

  setDefaultMenuItems() {
    this.menuItems = [
      { id: 1, systemName: 'dashboard', pluralName: 'Dashboard', singularName: 'Dashboard', icon: 'fas fa-home', route: '/dashboard', sequence: 1, isActive: true },
      { id: 2, systemName: 'students', pluralName: 'Students', singularName: 'Student', icon: 'fas fa-user-graduate', route: '/students', sequence: 2, isActive: true },
      { id: 3, systemName: 'staff', pluralName: 'Staff', singularName: 'Staff', icon: 'fas fa-chalkboard-teacher', route: '/staff', sequence: 3, isActive: true },
      { id: 4, systemName: 'attendance', pluralName: 'Attendance', singularName: 'Attendance', icon: 'fas fa-calendar-check', route: '/attendance', sequence: 4, isActive: true },
      { id: 5, systemName: 'parents', pluralName: 'Parents', singularName: 'Parent', icon: 'fas fa-users', route: '/parents', sequence: 5, isActive: true },
      { id: 6, systemName: 'subjects', pluralName: 'Subjects', singularName: 'Subject', icon: 'fas fa-book', route: '/subjects', sequence: 6, isActive: true }
    ];
    this.visibleMenuItems = this.menuItems;
  }

  isMenuItemActive(menuItem: MenuItem): boolean {
    // TODO: Implement active menu detection based on current route
    return false;
  }

  /**
   * Logout - clears theme and session
   */
  logout() {
    // Call backend logout endpoint to clear server session
    this.authService.logout().subscribe({
      next: (response) => {
        console.log('Logout successful:', response);
        // Don't clear theme on logout - let it persist for next login
        // But you can reset to default if you prefer
        // this.themeService.resetTheme();
      },
      error: (error) => {
        console.error('Logout error:', error);
        // Error handler - state already cleared by AuthService
      },
      complete: () => {
        console.log('Logout completed, navigating to home');
        // Navigate to home AFTER logout is complete
        // Use a small delay to ensure all state is cleared
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 100);
      }
    });
  }
}
