import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { filter } from 'rxjs/operators';

export interface SettingCategory {
  title: string;
  items?: SettingItem[];
  isExpanded?: boolean;
}

export interface SettingItem {
  label: string;
  icon: string;
  route: string;
  active?: boolean;
}

@Component({
  selector: 'app-settings-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './settings-sidebar.component.html',
  styleUrls: ['./settings-sidebar.component.css']
})
export class SettingsSidebarComponent implements OnInit {
  searchTerm = '';
  categories: SettingCategory[] = [];
  filteredCategories: SettingCategory[] = [];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.initializeCategories();
    this.filterCategories();
    this.expandActiveCategory();
    
    // Listen to route changes to update expanded category
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.expandActiveCategory();
    });
  }

  /**
   * Expand the category that contains the active route
   */
  private expandActiveCategory(): void {
    const currentUrl = this.router.url;
    
    // Find the category that contains the active route
    for (const category of this.categories) {
      if (category.items) {
        const hasActiveItem = category.items.some(item => currentUrl.startsWith(item.route));
        if (hasActiveItem) {
          // Close all categories first
          this.categories.forEach(cat => cat.isExpanded = false);
          // Open the category with active item
          category.isExpanded = true;
          break;
        }
      }
    }
  }

  /**
   * Initialize all setting categories and items
   */
  initializeCategories(): void {
    this.categories = [
      {
        title: 'General',
        isExpanded: true,
        items: [
          { label: 'Personal Settings', icon: 'fas fa-user-cog', route: '/setup/personal-settings', active: true },
          { label: 'Users', icon: 'fas fa-users', route: '/setup/users', active: false },
          { label: 'Company Settings', icon: 'fas fa-building', route: '/setup/company-settings', active: false },
          { label: 'Subscription', icon: 'fas fa-crown', route: '/setup/subscription', active: false }
        ]
      },
      {
        title: 'Academic Settings',
        isExpanded: false,
        items: [
          { label: 'Academic Year & Terms', icon: 'fas fa-calendar-alt', route: '/setup/academic-settings', active: false },
          { label: 'Student Promotion', icon: 'fas fa-user-graduate', route: '/promotions', active: false },
          { label: 'Promotion Rules', icon: 'fas fa-graduation-cap', route: '/setup/academic-settings?tab=promotion', active: false },
          { label: 'Grading Scale', icon: 'fas fa-star', route: '/setup/academic-settings?tab=grading', active: false },
          { label: 'Attendance Settings', icon: 'fas fa-user-check', route: '/setup/academic-settings?tab=attendance', active: false },
          { label: 'Exam Settings', icon: 'fas fa-file-alt', route: '/setup/academic-settings?tab=exam', active: false }
        ]
      },
      {
        title: 'Security Control',
        isExpanded: false,
        items: [
          { label: 'Profiles', icon: 'fas fa-id-card', route: '/setup/profiles', active: false },
          { label: 'Roles and Sharing', icon: 'fas fa-share-alt', route: '/setup/roles-sharing', active: false },
          { label: 'Login History', icon: 'fas fa-history', route: '/setup/login-history', active: false },
          { label: 'Audit Log', icon: 'fas fa-clipboard-list', route: '/setup/audit-log', active: false }
        ]
      },
      {
        title: 'Customization',
        isExpanded: false,
        items: [
          { label: 'Modules and Fields', icon: 'fas fa-puzzle-piece', route: '/setup/modules-fields', active: false },
          { label: 'Customize Home page', icon: 'fas fa-home', route: '/setup/customize-home', active: false },
          { label: 'Email Templates', icon: 'fas fa-envelope-open-text', route: '/setup/email-templates', active: false },
          { label: 'File Storage', icon: 'fas fa-file-alt', route: '/setup/file-storage', active: false },
          { label: 'Record Storage', icon: 'fas fa-database', route: '/setup/record-storage', active: false }
        ]
      },
      {
        title: 'Automation',
        isExpanded: false,
        items: [
          { label: 'Workflow Rules', icon: 'fas fa-project-diagram', route: '/setup/workflow-rules', active: false },
          { label: 'Actions', icon: 'fas fa-bolt', route: '/setup/actions', active: false },
          { label: 'Schedules', icon: 'fas fa-calendar-alt', route: '/setup/schedules', active: false }
        ]
      },
      {
        title: 'Process Management',
        isExpanded: false,
        items: [
          { label: 'Blueprint', icon: 'fas fa-drafting-compass', route: '/setup/blueprint', active: false },
          { label: 'Approval Processes', icon: 'fas fa-check-circle', route: '/setup/approval-processes', active: false },
          { label: 'Review Processes', icon: 'fas fa-search', route: '/setup/review-processes', active: false }
        ]
      },
      {
        title: 'Data Administration',
        isExpanded: false,
        items: [
          { label: 'Import', icon: 'fas fa-file-import', route: '/setup/import-history', active: false },
          { label: 'Export', icon: 'fas fa-file-export', route: '/setup/export', active: false },
          { label: 'Data Backup', icon: 'fas fa-database', route: '/setup/data-backup', active: false },
          { label: 'Storage', icon: 'fas fa-hdd', route: '/setup/storage', active: false },
          { label: 'Recycle Bin', icon: 'fas fa-trash-restore', route: '/setup/recycle-bin', active: false },
          { label: 'Copy Customization', icon: 'fas fa-copy', route: '/setup/copy-customization', active: false }
        ]
      }
    ];
  }

  /**
   * Filter categories based on search term
   */
  filterCategories(): void {
    if (!this.searchTerm.trim()) {
      this.filteredCategories = this.categories;
      return;
    }

    const searchLower = this.searchTerm.toLowerCase();
    this.filteredCategories = this.categories
      .map(category => ({
        ...category,
        items: category.items?.filter(item =>
          item.label.toLowerCase().includes(searchLower)
        )
      }))
      .filter(category => (category.items && category.items.length > 0) || category.title.toLowerCase().includes(searchLower));
  }

  /**
   * Handle search input
   */
  onSearch(event: any): void {
    this.searchTerm = event.target.value;
    this.filterCategories();
  }

  /**
   * Clear search
   */
  clearSearch(): void {
    this.searchTerm = '';
    this.filterCategories();
  }

  /**
   * Toggle category expansion
   */
  toggleCategory(category: SettingCategory): void {
    const wasExpanded = category.isExpanded;
    // Close all categories first
    this.categories.forEach(cat => cat.isExpanded = false);
    // Toggle the clicked category - open it if it was closed, keep it closed if it was open
    category.isExpanded = !wasExpanded;
  }

  /**
   * Set active item
   */
  setActiveItem(item: SettingItem): void {
    this.categories.forEach(category => {
      if (category.items) {
        category.items.forEach(i => {
          i.active = i === item;
        });
      }
    });
  }

  /**
   * Track category for ngFor
   */
  trackCategory(index: number, category: SettingCategory): string {
    return category.title;
  }

  /**
   * Track item for ngFor
   */
  trackItem(index: number, item: SettingItem): string {
    return item.label;
  }
}
