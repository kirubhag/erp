import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SampleDataModalComponent } from '../sample-data-modal/sample-data-modal.component';

export interface SetupItem {
  title: string;
  description: string;
  icon: string;
  iconColor: string;
  completed: boolean;
  route?: string;
}

export interface SetupSection {
  title: string;
  description: string;
  items: SetupItem[];
}

@Component({
  selector: 'app-onboarding',
  standalone: true,
  imports: [CommonModule, SampleDataModalComponent],
  templateUrl: './onboarding.component.html',
  styleUrls: ['./onboarding.component.css']
})
export class OnboardingComponent implements OnInit {
  activeTab = 'system-setup';
  progressPercentage = 30;
  showSampleDataModal = false;
  
  setupSections: SetupSection[] = [
    {
      title: 'Basic Configuration',
      description: 'Configure essential settings to align the ERP system with your business operations.',
      items: [
        {
          title: 'Company Information',
          description: 'Set up your company profile, logo, and tax details.',
          icon: 'fas fa-building',
          iconColor: 'success',
          completed: true,
          route: '/setup/organisation-settings'
        },
        {
          title: 'User Management',
          description: 'Invite team members and assign roles and permissions.',
          icon: 'fas fa-users',
          iconColor: 'success',
          completed: true,
          route: '/setup/users'
        },
        {
          title: 'Data Import',
          description: 'Import existing data from spreadsheets or other systems.',
          icon: 'fas fa-file-import',
          iconColor: 'primary',
          completed: false,
          route: '/setup/import-history'
        },
        {
          title: 'Chart of Accounts',
          description: 'Configure your accounting structure and GL codes.',
          icon: 'fas fa-university',
          iconColor: 'primary',
          completed: false
        },
        {
          title: 'Warehouse Setup',
          description: 'Configure warehouses, locations, and inventory settings.',
          icon: 'fas fa-warehouse',
          iconColor: 'primary',
          completed: false
        },
        {
          title: 'Payment Gateway',
          description: 'Integrate payment processors for online transactions.',
          icon: 'fas fa-money-bill-wave',
          iconColor: 'primary',
          completed: false
        }
      ]
    },
    {
      title: 'Advanced Configuration',
      description: 'Customize workflows and automate business processes to maximize efficiency.',
      items: [
        {
          title: 'Workflow Automation',
          description: 'Set up automated workflows for approvals and notifications.',
          icon: 'fas fa-project-diagram',
          iconColor: 'success',
          completed: true,
          route: '/setup/workflow-rules'
        },
        {
          title: 'Third-Party Integrations',
          description: 'Connect with CRM, e-commerce, and other platforms.',
          icon: 'fas fa-plug',
          iconColor: 'primary',
          completed: false
        },
        {
          title: 'Custom Reports',
          description: 'Create custom dashboards and reporting templates.',
          icon: 'fas fa-chart-bar',
          iconColor: 'primary',
          completed: false
        },
        {
          title: 'Security Settings',
          description: 'Configure 2FA, IP restrictions, and audit logs.',
          icon: 'fas fa-shield-alt',
          iconColor: 'primary',
          completed: false,
          route: '/setup/profiles'
        }
      ]
    }
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.calculateProgress();
  }

  calculateProgress(): void {
    const allItems = this.setupSections.flatMap(section => section.items);
    const completedItems = allItems.filter(item => item.completed).length;
    this.progressPercentage = Math.round((completedItems / allItems.length) * 100);
  }

  onSetupItemClick(item: SetupItem): void {
    if (item.route) {
      this.router.navigate([item.route]);
    }
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  openSampleDataModal(): void {
    this.showSampleDataModal = true;
  }

  closeSampleDataModal(): void {
    this.showSampleDataModal = false;
  }

  onSampleDataImported(): void {
    this.showSampleDataModal = false;
    // Optionally refresh the page or show success message
    console.log('Sample data imported successfully');
  }
}
