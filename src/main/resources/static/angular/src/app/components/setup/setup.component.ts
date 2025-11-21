import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

export interface SetupSection {
  id: string;
  title: string;
  items: SetupItem[];
}

export interface SetupItem {
  id: string;
  label: string;
  icon?: string;
  route?: string;
  description?: string;
}

@Component({
  selector: 'app-setup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './setup.component.html',
  styleUrls: ['./setup.component.css']
})
export class SetupComponent implements OnInit {
  
  constructor(private router: Router) {}

  setupSections: SetupSection[] = [
    {
      id: 'general',
      title: 'General',
      items: [
        {
          id: 'personal-settings',
          label: 'Personal Settings',
          icon: 'fas fa-user-cog',
          route: '/setup/personal-settings',
          description: 'Manage your personal preferences and account settings'
        },
        {
          id: 'academic-settings',
          label: 'Academic Settings',
          icon: 'fas fa-graduation-cap',
          route: '/setup/academic-settings',
          description: 'Configure academic year, terms, grading scales, and educational settings'
        },
        {
          id: 'users',
          label: 'Users',
          icon: 'fas fa-users',
          route: '/setup/users',
          description: 'Manage system users and their access'
        },
        {
          id: 'company-settings',
          label: 'Company Settings',
          icon: 'fas fa-building',
          route: '/setup/company-settings',
          description: 'Configure company-wide settings and preferences'
        }
      ]
    },
    {
      id: 'security',
      title: 'Security Control',
      items: [
        {
          id: 'profiles',
          label: 'Profiles',
          icon: 'fas fa-id-card',
          route: '/setup/profiles',
          description: 'Manage user profiles and permissions'
        },
        {
          id: 'roles-sharing',
          label: 'Roles and Sharing',
          icon: 'fas fa-share-alt',
          route: '/setup/roles-sharing',
          description: 'Configure roles and data sharing settings'
        },
        {
          id: 'login-history',
          label: 'Login History',
          icon: 'fas fa-history',
          route: '/setup/login-history',
          description: 'View user login activities and history'
        },
        {
          id: 'audit-log',
          label: 'Audit Log',
          icon: 'fas fa-clipboard-list',
          route: '/setup/audit-log',
          description: 'Track system changes and user activities'
        }
      ]
    },
    {
      id: 'customization',
      title: 'Customization',
      items: [
        {
          id: 'modules-fields',
          label: 'Modules and Fields',
          icon: 'fas fa-puzzle-piece',
          route: '/setup/modules-fields',
          description: 'Customize modules and field configurations'
        },
        {
          id: 'home-page',
          label: 'Customize Home page',
          icon: 'fas fa-home',
          route: '/setup/customize-home',
          description: 'Personalize your home page layout and widgets'
        },
        {
          id: 'email-templates',
          label: 'Email Templates',
          icon: 'fas fa-envelope-open-text',
          route: '/setup/email-templates',
          description: 'Create and manage email templates'
        }
      ]
    },
    {
      id: 'automation',
      title: 'Automation',
      items: [
        {
          id: 'workflow-rules',
          label: 'Workflow Rules',
          icon: 'fas fa-project-diagram',
          route: '/setup/workflow-rules',
          description: 'Set up automated workflow rules and triggers'
        },
        {
          id: 'actions',
          label: 'Actions',
          icon: 'fas fa-bolt',
          route: '/setup/actions',
          description: 'Configure automated actions and responses'
        },
        {
          id: 'schedules',
          label: 'Schedules',
          icon: 'fas fa-calendar-alt',
          route: '/setup/schedules',
          description: 'Manage scheduled tasks and recurring events'
        }
      ]
    },
    {
      id: 'process-management',
      title: 'Process Management',
      items: [
        {
          id: 'blueprint',
          label: 'Blueprint',
          icon: 'fas fa-drafting-compass',
          route: '/setup/blueprint',
          description: 'Design and manage business process blueprints'
        },
        {
          id: 'approval-processes',
          label: 'Approval Processes',
          icon: 'fas fa-check-circle',
          route: '/setup/approval-processes',
          description: 'Set up approval workflows and processes'
        },
        {
          id: 'review-processes',
          label: 'Review Processes',
          icon: 'fas fa-search',
          route: '/setup/review-processes',
          description: 'Configure review and validation processes'
        }
      ]
    },
    {
      id: 'data-administration',
      title: 'Data Administration',
      items: [
        {
          id: 'import',
          label: 'Import',
          icon: 'fas fa-file-import',
          route: '/setup/import-history',
          description: 'Import data from external sources'
        },
        {
          id: 'export',
          label: 'Export',
          icon: 'fas fa-file-export',
          route: '/setup/export',
          description: 'Export data to various formats'
        },
        {
          id: 'data-backup',
          label: 'Data Backup',
          icon: 'fas fa-database',
          route: '/setup/data-backup',
          description: 'Manage data backup and restore operations'
        },
        {
          id: 'file-storage',
          label: 'File Storage',
          icon: 'fas fa-file-alt',
          route: '/setup/file-storage',
          description: 'Monitor file storage usage and attachments'
        },
        {
          id: 'record-storage',
          label: 'Record Storage',
          icon: 'fas fa-hdd',
          route: '/setup/record-storage',
          description: 'View record storage usage across modules'
        },
        {
          id: 'recycle-bin',
          label: 'Recycle Bin',
          icon: 'fas fa-trash-restore',
          route: '/setup/recycle-bin',
          description: 'View and restore deleted items'
        },
        {
          id: 'copy-customization',
          label: 'Copy Customization',
          icon: 'fas fa-copy',
          route: '/setup/copy-customization',
          description: 'Copy customizations between environments'
        }
      ]
    }
  ];

  searchTerm: string = '';
  filteredSections: SetupSection[] = [];

  ngOnInit() {
    this.filteredSections = [...this.setupSections];
  }

  onSearch(event: Event) {
    const target = event.target as HTMLInputElement;
    this.searchTerm = target.value.toLowerCase();
    
    if (!this.searchTerm) {
      this.filteredSections = [...this.setupSections];
      return;
    }

    this.filteredSections = this.setupSections.map(section => ({
      ...section,
      items: section.items.filter(item => 
        item.label.toLowerCase().includes(this.searchTerm) ||
        item.description?.toLowerCase().includes(this.searchTerm)
      )
    })).filter(section => section.items.length > 0);
  }

  onItemClick(item: SetupItem) {
    if (item.route) {
      this.router.navigate([item.route]);
    }
  }

  // Handle clicks on whole section cards (template uses this)
  onSectionClick(section: SetupSection) {
    // Find first item in section and navigate
    if (section.items.length > 0) {
      const firstItem = section.items[0];
      this.onItemClick(firstItem);
    }
  }

  clearSearch() {
    this.searchTerm = '';
    this.filteredSections = [...this.setupSections];
  }

  trackSection(index: number, section: SetupSection): string {
    return section.id;
  }

  trackItem(index: number, item: SetupItem): string {
    return item.id;
  }
}