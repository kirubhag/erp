import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

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

  ngOnInit(): void {
    this.initializeCategories();
    this.filterCategories();
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
          { label: 'Personal Settings', icon: 'fas fa-user', route: '/setup/personal-settings', active: true },
          { label: 'Users', icon: 'fas fa-users', route: '/setup/users', active: false },
          { label: 'Company Settings', icon: 'fas fa-building', route: '/setup/company-settings', active: false },
          { label: 'Calendar Booking', icon: 'fas fa-calendar', route: '/setup/calendar-booking', active: false },
          { label: 'Motivator', icon: 'fas fa-heart', route: '/setup/motivator', active: false }
        ]
      },
      {
        title: 'Security Control',
        isExpanded: false,
        items: [
          { label: 'Profiles', icon: 'fas fa-shield-alt', route: '/setup/profiles', active: false },
          { label: 'Roles and Sharing', icon: 'fas fa-lock', route: '/setup/roles', active: false },
          { label: 'Login History', icon: 'fas fa-history', route: '/setup/login-history', active: false },
          { label: 'Audit Log', icon: 'fas fa-clipboard-list', route: '/setup/audit-log', active: false }
        ]
      },
      {
        title: 'Channels',
        isExpanded: false,
        items: [
          { label: 'Email', icon: 'fas fa-envelope', route: '/setup/email', active: false },
          { label: 'SMS', icon: 'fas fa-sms', route: '/setup/sms', active: false },
          { label: 'Social Media', icon: 'fas fa-share-alt', route: '/setup/social', active: false }
        ]
      },
      {
        title: 'Customization',
        isExpanded: false,
        items: [
          { label: 'Modules and Fields', icon: 'fas fa-cubes', route: '/setup/modules-fields', active: false },
          { label: 'Customize Home page', icon: 'fas fa-home', route: '/setup/customize-home', active: false },
          { label: 'Email Templates', icon: 'fas fa-envelope-open-text', route: '/setup/email-templates', active: false }
        ]
      },
      {
        title: 'Automation',
        isExpanded: false,
        items: [
          { label: 'Workflow Rules', icon: 'fas fa-cogs', route: '/setup/workflow', active: false },
          { label: 'Actions', icon: 'fas fa-tasks', route: '/setup/actions', active: false },
          { label: 'Schedules', icon: 'fas fa-clock', route: '/setup/schedules', active: false }
        ]
      },
      {
        title: 'Process Management',
        isExpanded: false,
        items: [
          { label: 'Blueprint', icon: 'fas fa-project-diagram', route: '/setup/blueprint', active: false },
          { label: 'Approval Processes', icon: 'fas fa-check-circle', route: '/setup/approval', active: false },
          { label: 'Review Processes', icon: 'fas fa-eye', route: '/setup/review', active: false }
        ]
      },
      {
        title: 'Data Administration',
        isExpanded: false,
        items: [
          { label: 'Import', icon: 'fas fa-download', route: '/setup/import-history', active: false },
          { label: 'Export', icon: 'fas fa-upload', route: '/setup/export', active: false },
          { label: 'Data Backup', icon: 'fas fa-database', route: '/setup/backup', active: false },
          { label: 'Storage', icon: 'fas fa-server', route: '/setup/storage', active: false },
          { label: 'Recycle Bin', icon: 'fas fa-trash', route: '/setup/recycle-bin', active: false }
        ]
      },
      {
        title: 'Experience Center',
        isExpanded: false,
        items: [
          { label: 'Modules', icon: 'fas fa-th', route: '/setup/experience-modules', active: false }
        ]
      },
      {
        title: 'Marketplace',
        isExpanded: false,
        items: [
          { label: 'Extensions', icon: 'fas fa-puzzle-piece', route: '/setup/marketplace', active: false }
        ]
      },
      {
        title: 'Developer Hub',
        isExpanded: false,
        items: [
          { label: 'API', icon: 'fas fa-code', route: '/setup/api', active: false }
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
    category.isExpanded = !category.isExpanded;
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
