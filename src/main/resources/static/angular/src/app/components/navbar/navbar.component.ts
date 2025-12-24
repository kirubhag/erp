import { Component, OnInit, HostListener, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MenuService } from '../../services/menu.service';
import { AuthService, UserDetails } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { ClickOutsideDirective } from '../../directives/click-outside.directive';

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
  imports: [CommonModule, RouterModule, ClickOutsideDirective],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, AfterViewInit {
  @ViewChild('menuContainer', { read: ElementRef }) menuContainer!: ElementRef;
  
  menuItems: MenuItem[] = [];
  visibleMenuItems: MenuItem[] = [];
  overflowMenuItems: MenuItem[] = [];
  hasOverflow = false;
  organizationName = 'Zylker';
  currentUser: UserDetails | null = null;
  selectedTheme = '#0056b3';
  isProfileDropdownOpen = false;
  hasAvatar = false;
  avatarLoadError = false;
  isOverflowDropdownOpen = false;
  isNavbarCollapsed = true; // For mobile menu toggle
  
  // Navbar width constraints - dynamically calculated based on screen size
  private readonly ITEM_WIDTH = 140; // Estimated width per menu item including margins
  private readonly MORE_BUTTON_WIDTH = 60; // Width of the "..." button
  private readonly BRAND_WIDTH = 200; // Approximate width of brand/logo area
  private readonly PROFILE_WIDTH = 150; // Approximate width of profile dropdown area
  private readonly PADDING_BUFFER = 50; // Extra buffer for padding and margins

  constructor(
    private menuService: MenuService,
    private authService: AuthService,
    private themeService: ThemeService,
    private router: Router
  ) { }

  ngOnInit() {
    // Set default menu items immediately (synchronous)
    this.setDefaultMenuItems();
    console.log('Navbar initialized with default menu items');

    // Load current user
    this.loadCurrentUser();

    // Load theme from service (will load from localStorage + database if available)
    this.loadTheme();

    // Load menu items from API
    this.loadMenuItems();
  }

  ngAfterViewInit() {
    // Calculate overflow after view is initialized
    setTimeout(() => {
      this.calculateMenuOverflow();
    }, 100);
  }

  @HostListener('window:resize')
  onResize() {
    this.calculateMenuOverflow();
  }

  /**
   * Load current user from AuthService
   */
  loadCurrentUser(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUser = user;
        // Update avatar state
        this.hasAvatar = !!(user.avatarUrl);
        this.avatarLoadError = false;
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

        // Filter only active items and sort by sequence
        const activeItems = this.menuItems.filter(item => item.isActive).sort((a, b) => a.sequence - b.sequence);
        
        console.log('Menu items loaded from API:', activeItems.length);
        console.log('Loaded menu items:', activeItems.map(i => ({ 
          name: i.pluralName, 
          sequence: i.sequence, 
          icon: i.icon, 
          route: i.route,
          active: i.isActive 
        })));
        
        // Debug: Check specific items for icon issues
        const roomsItem = activeItems.find(i => i.systemName === 'ROOM');
        const timetableItem = activeItems.find(i => i.systemName === 'TIMETABLE');
        if (roomsItem) console.log('Rooms icon:', roomsItem.icon, '| Full item:', roomsItem);
        if (timetableItem) console.log('Timetable icon:', timetableItem.icon, '| Full item:', timetableItem);
        
        // Calculate overflow based on available space
        this.calculateMenuOverflow(activeItems);
      },
      error: (error) => {
        // Silently fail and use default menu items
        // Only log if it's not a timeout error
        if (error.name !== 'TimeoutError') {
          console.warn('Error fetching menu items, using defaults:', error.message);
        }
        // Default menu items already set in ngOnInit via setDefaultMenuItems()
        this.calculateMenuOverflow(this.menuItems);
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
    // Calculate overflow for default items
    this.calculateMenuOverflow(this.menuItems);
  }

  /**
   * Calculate which menu items should be visible vs overflow
   * Dynamically based on available screen width
   */
  calculateMenuOverflow(items?: MenuItem[]): void {
    const menuItems = items || this.menuItems.filter(item => item.isActive).sort((a, b) => a.sequence - b.sequence);
    
    // Get current window width
    const screenWidth = window.innerWidth;
    
    // Calculate available width for menu items
    // Subtract brand width, profile width, padding, and buffer
    const availableWidth = screenWidth - this.BRAND_WIDTH - this.PROFILE_WIDTH - this.PADDING_BUFFER;
    
    // Calculate how many items can fit based on available width
    const maxItems = Math.floor(availableWidth / this.ITEM_WIDTH);
    
    // Apply responsive breakpoints
    let effectiveMaxItems = maxItems;
    
    if (screenWidth < 768) {
      // Mobile: collapse to hamburger menu (Bootstrap handles this)
      effectiveMaxItems = menuItems.length; // Show all in collapsed menu
    } else if (screenWidth < 992) {
      // Tablet: limit to prevent overcrowding
      effectiveMaxItems = Math.min(maxItems, 4);
    } else if (screenWidth < 1200) {
      // Small laptop
      effectiveMaxItems = Math.min(maxItems, 6);
    } else if (screenWidth < 1600) {
      // Standard laptop/desktop
      effectiveMaxItems = Math.min(maxItems, 8);
    }
    // Large screens (1600+): use calculated maxItems as-is
    
    if (menuItems.length <= effectiveMaxItems) {
      // All items fit, no overflow
      this.visibleMenuItems = menuItems;
      this.overflowMenuItems = [];
      this.hasOverflow = false;
    } else {
      // Split items: visible vs overflow
      // Reserve space for "More" button by reducing visible items by 1
      const visibleCount = effectiveMaxItems - 1;
      this.visibleMenuItems = menuItems.slice(0, visibleCount);
      this.overflowMenuItems = menuItems.slice(visibleCount);
      this.hasOverflow = true;
      
      console.log(`Menu overflow (${screenWidth}px): ${this.visibleMenuItems.length} visible, ${this.overflowMenuItems.length} in dropdown`);
    }
  }

  isMenuItemActive(menuItem: MenuItem): boolean {
    const currentRoute = this.router.url;
    // Check if current route starts with menu item route (handles detail pages)
    return currentRoute.startsWith(menuItem.route);
  }

  /**
   * Toggle profile dropdown
   */
  toggleProfileDropdown(event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.isProfileDropdownOpen = !this.isProfileDropdownOpen;
    this.isOverflowDropdownOpen = false; // Close overflow when opening profile
  }

  /**
   * Close profile dropdown when clicking outside
   */
  closeProfileDropdown(): void {
    this.isProfileDropdownOpen = false;
  }

  /**
   * Toggle navbar collapsed state (for mobile)
   */
  toggleNavbar(): void {
    this.isNavbarCollapsed = !this.isNavbarCollapsed;
  }

  /**
   * Close navbar when a link is clicked (mobile)
   */
  closeNavbar(): void {
    this.isNavbarCollapsed = true;
  }

  /**
   * Toggle overflow menu dropdown
   */
  toggleOverflowDropdown(event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    this.isOverflowDropdownOpen = !this.isOverflowDropdownOpen;
    this.isProfileDropdownOpen = false; // Close profile when opening overflow
  }

  /**
   * Close overflow dropdown when clicking outside
   */
  closeOverflowDropdown(): void {
    this.isOverflowDropdownOpen = false;
  }

  /**
   * Navigate to a route and close dropdown
   */
  navigateAndClose(route: string): void {
    this.router.navigate([route]);
    this.closeProfileDropdown();
  }

  /**
   * Get user initials for avatar
   */
  getUserInitials(): string {
    if (!this.currentUser) return '?';
    const first = this.currentUser.firstName?.charAt(0) || this.currentUser.name?.charAt(0) || '';
    const last = this.currentUser.lastName?.charAt(0) || '';
    return (first + last).toUpperCase() || this.currentUser.username?.charAt(0).toUpperCase() || '?';
  }

  /**
   * Get avatar URL
   */
  getAvatarUrl(): string {
    if (this.currentUser?.avatarUrl) {
      return this.currentUser.avatarUrl;
    }
    if (this.currentUser?.id) {
      return `/api/attachments/avatar/${this.currentUser.id}`;
    }
    return '';
  }

  /**
   * Handle avatar load error
   */
  onAvatarError(): void {
    this.avatarLoadError = true;
    this.hasAvatar = false;
  }

  /**
   * Logout - clears theme and session
   */
  logout() {
    this.closeProfileDropdown();
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
