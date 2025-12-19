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
  queryParams?: { [key: string]: string };
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

  constructor(private router: Router) { }

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
          id: 'users',
          label: 'Users',
          icon: 'fas fa-users',
          route: '/setup/users',
          description: 'Manage system users and their access'
        },
        {
          id: 'organisation-settings',
          label: 'Organisation Settings',
          icon: 'fas fa-building',
          route: '/setup/organisation-settings',
          description: 'Configure organisation details'
        },
        {
          id: 'subscription',
          label: 'Subscription',
          icon: 'fas fa-crown',
          route: '/setup/subscription',
          description: "Displays an overview of your organization's subscription plan and feature usage limits"
        }
      ]
    },
    {
      id: 'administrative',
      title: 'Administrative & Institutional',
      items: [
        {
          id: 'admission-enrollment',
          label: 'Admission & Enrollment',
          icon: 'fas fa-user-plus',
          route: '/setup/admission/dashboard',
          description: 'Manage applications, registration, and waitlists'
        },
        {
          id: 'hr-management',
          label: 'HR Management',
          icon: 'fas fa-users-cog',
          route: '/setup/administrative/hr',
          description: 'Staff recruitment, payroll, and performance evaluation'
        },
        {
          id: 'inventory-assets',
          label: 'Inventory & Assets',
          icon: 'fas fa-boxes',
          route: '/setup/administrative/inventory',
          description: 'Track physical assets and manage consumables'
        },
        {
          id: 'facility-management',
          label: 'Facility Management',
          icon: 'fas fa-building',
          route: '/setup/administrative/facility',
          description: 'Handle maintenance work orders and facility scheduling'
        },
        {
          id: 'reporting-mis',
          label: 'Reporting & Analytics',
          icon: 'fas fa-chart-line',
          route: '/setup/administrative/reports',
          description: 'Centralized dashboard for reports and analytics'
        },
        {
          id: 'document-management',
          label: 'Document Management',
          icon: 'fas fa-file-contract',
          route: '/setup/administrative/documents',
          description: 'Secure storage for official documents and policies'
        }
      ]
    },
    {
      id: 'financial',
      title: 'Financial & Accounting',
      items: [
        {
          id: 'fee-types',
          label: 'Fee Types',
          icon: 'fas fa-money-bill-wave',
          route: '/setup/finance/fee-types',
          description: 'Define institutional fee categories'
        },
        {
          id: 'fee-structures',
          label: 'Fee Structures',
          icon: 'fas fa-receipt',
          route: '/setup/finance/fee-structures',
          description: 'Set fee amounts by grade and year'
        },
        {
          id: 'fine-management',
          label: 'Fine Management',
          icon: 'fas fa-gavel',
          route: '/setup/finance/fine-management',
          description: 'Configure automated fine rules and categories'
        },
        {
          id: 'fee-payments',
          label: 'Fee Collection',
          icon: 'fas fa-file-invoice-dollar',
          route: '/setup/finance/payments',
          description: 'Record and track student fee payments'
        },
        {
          id: 'fine-ledger',
          label: 'Fine Ledger',
          icon: 'fas fa-balance-scale',
          route: '/setup/finance/fine-ledger',
          description: 'View and manage student fine records'
        },
        {
          id: 'disciplinary-incidents',
          label: 'Behavioral Fines',
          icon: 'fas fa-user-shield',
          route: '/setup/finance/incidents',
          description: 'Report incidents and issue disciplinary fines'
        },
        {
          id: 'fine-waivers',
          label: 'Fine Waivers',
          icon: 'fas fa-hand-holding-usd',
          route: '/setup/finance/waivers',
          description: 'Process waiver requests and adjustments'
        },
        {
          id: 'invoices',
          label: 'Invoices',
          icon: 'fas fa-file-invoice',
          route: '/setup/finance/invoices',
          description: 'Manage billing invoices for fees and fines'
        },
        {
          id: 'transactions',
          label: 'Transactions',
          icon: 'fas fa-exchange-alt',
          route: '/setup/finance/transactions',
          description: 'View and track payment transactions'
        },
        {
          id: 'chart-of-accounts',
          label: 'Chart of Accounts',
          icon: 'fas fa-sitemap',
          route: '/setup/finance/accounts',
          description: 'Manage the hierarchy of accounting codes'
        },
        {
          id: 'journal-entries',
          label: 'Journal Entries',
          icon: 'fas fa-book',
          route: '/setup/finance/journal-entries',
          description: 'Record accounting transactions and adjustments'
        },
        {
          id: 'accounting-periods',
          label: 'Accounting Periods',
          icon: 'fas fa-calendar-check',
          route: '/setup/finance/accounting-periods',
          description: 'Manage fiscal years and reporting durations'
        },
        {
          id: 'trial-balance',
          label: 'Trial Balance',
          icon: 'fas fa-balance-scale-left',
          route: '/setup/finance/trial-balance',
          description: 'View the summary of all account balances'
        },
        {
          id: 'profit-loss',
          label: 'Profit & Loss',
          icon: 'fas fa-chart-pie',
          route: '/setup/finance/profit-loss',
          description: 'Analyze institutional revenue and expenses'
        },
        {
          id: 'scholarships',
          label: 'Scholarships',
          icon: 'fas fa-graduation-cap',
          route: '/setup/finance/scholarship-categories',
          description: 'Define aid types and manage applications'
        },
        {
          id: 'budgets',
          label: 'Budgets',
          icon: 'fas fa-money-check-alt',
          route: '/setup/finance/budgets',
          description: 'Manage departmental and institutional budgets'
        },
        {
          id: 'bank-statements',
          label: 'Bank Statements',
          icon: 'fas fa-university',
          route: '/setup/finance/bank-statements',
          description: 'Reconcile ledger entries with bank records'
        }
      ]
    },
    {
      id: 'communication',
      title: 'Communication & Engagement',
      items: [
        {
          id: 'communication-portal',
          label: 'Communication Portal',
          icon: 'fas fa-comments',
          route: '/setup/communication/portal',
          description: 'Internal messaging for staff, students, and parents'
        },
        {
          id: 'announcements',
          label: 'Announcements',
          icon: 'fas fa-bullhorn',
          route: '/setup/communication/announcements',
          description: 'Broadcast notices and circulars'
        },
        {
          id: 'helpdesk',
          label: 'Helpdesk Support',
          icon: 'fas fa-life-ring',
          route: '/setup/communication/helpdesk',
          description: 'Support ticketing and issue resolution'
        },
        {
          id: 'alumni',
          label: 'Alumni Management',
          icon: 'fas fa-user-graduate',
          route: '/setup/communication/alumni',
          description: 'Track and engage with former students'
        }
      ]
    },
    {
      id: 'resource-support',
      title: 'Resource & Academic Support',
      items: [
        {
          id: 'library-management',
          label: 'Library Management',
          icon: 'fas fa-book-reader',
          route: '/setup/resource/library',
          description: 'Manage books, circulation, and digital resources'
        },
        {
          id: 'lms-integration',
          label: 'LMS Integration',
          icon: 'fas fa-laptop-code',
          route: '/setup/resource/lms',
          description: 'Online content, quizzes, and assessments'
        },
        {
          id: 'training-development',
          label: 'Professional Development',
          icon: 'fas fa-chalkboard-teacher',
          route: '/setup/resource/training',
          description: 'Track staff training and certifications'
        }
      ]
    },
    {
      id: 'academic-settings',
      title: 'Academic Settings',
      items: [
        {
          id: 'academic-year',
          label: 'Academic Year',
          icon: 'fas fa-calendar-alt',
          route: '/setup/academic/academic-year',
          description: 'Configure academic years and terms'
        },
        {
          id: 'grading-scale',
          label: 'Grading Scale',
          icon: 'fas fa-star',
          route: '/setup/academic/grading',
          description: 'Set up grading scales and letter grades'
        },
        {
          id: 'attendance-settings',
          label: 'Attendance Settings',
          icon: 'fas fa-user-check',
          route: '/setup/academic/attendance',
          description: 'Configure attendance tracking and calculation'
        },
        {
          id: 'exam-settings',
          label: 'Exam Settings',
          icon: 'fas fa-file-alt',
          route: '/setup/academic/exam',
          description: 'Set up exam defaults and grading policies'
        },
        {
          id: 'student-promotion',
          label: 'Student Promotion',
          icon: 'fas fa-user-graduate',
          route: '/promotions',
          description: 'Manage student promotions between grades'
        },
        {
          id: 'promotion-rules',
          label: 'Promotion Rules',
          icon: 'fas fa-graduation-cap',
          route: '/setup/academic/promotion',
          description: 'Configure promotion criteria and rules'
        }
      ]
    },
    {
      id: 'timetable-scheduling',
      title: 'Timetable & Scheduling',
      items: [
        {
          id: 'rooms',
          label: 'Rooms',
          icon: 'fas fa-door-open',
          route: '/rooms',
          description: 'Manage school rooms and labs'
        },
        {
          id: 'timetables',
          label: 'Timetables',
          icon: 'fas fa-calendar-week',
          route: '/timetables',
          description: 'Create and view class schedules'
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
    },
    {
      id: 'communication',
      title: 'Communication & Engagement',
      items: [
        {
          id: 'messages',
          label: 'Messages',
          icon: 'fas fa-envelope',
          route: '/setup/communication/messages',
          description: 'Internal peer-to-peer messaging'
        },
        {
          id: 'announcements',
          label: 'Announcements',
          icon: 'fas fa-bullhorn',
          route: '/setup/communication/announcements',
          description: 'Targeted broadcasts and circulars'
        },
        {
          id: 'tickets',
          label: 'Support Tickets',
          icon: 'fas fa-ticket-alt',
          route: '/setup/communication/tickets',
          description: 'Helpdesk and issue tracking'
        }
      ]
    },
    {
      id: 'library',
      title: 'Library Management',
      items: [
        {
          id: 'resources',
          label: 'Resource Catalog',
          icon: 'fas fa-book',
          route: '/setup/library/resources',
          description: 'Cataloged books, journals, and e-resources'
        },
        {
          id: 'items',
          label: 'Physical Items',
          icon: 'fas fa-barcode',
          route: '/setup/library/items',
          description: 'Individual physical copies and stock tracking'
        },
        {
          id: 'loans',
          label: 'Circulation (Loans)',
          icon: 'fas fa-exchange-alt',
          route: '/setup/library/loans',
          description: 'Manage check-outs, returns, and renewals'
        },
        {
          id: 'holds',
          label: 'Reservations (Holds)',
          icon: 'fas fa-hand-paper',
          route: '/setup/library/holds',
          description: 'Manage book reservations'
        },
        {
          id: 'policies',
          label: 'Library Policies',
          icon: 'fas fa-gavel',
          route: '/setup/library/policies',
          description: 'Define borrowing rules and fine rates'
        },
        {
          id: 'procurement',
          label: 'Acquisitions & POs',
          icon: 'fas fa-shopping-cart',
          route: '/setup/library/purchase-requests',
          description: 'Manage purchase requests and orders'
        },
        {
          id: 'authors',
          label: 'Authors',
          icon: 'fas fa-pen-nib',
          route: '/setup/library/authors',
          description: 'Manage book authors'
        },
        {
          id: 'publishers',
          label: 'Publishers',
          icon: 'fas fa-building',
          route: '/setup/library/publishers',
          description: 'Manage resource publishers'
        }
      ]
    },
    {
      id: 'lms',
      title: 'Learning Management (LMS)',
      items: [
        {
          id: 'lms-modules',
          label: 'LMS Modules',
          icon: 'fas fa-layer-group',
          route: '/setup/lms/modules',
          description: 'Organize course modules'
        },
        {
          id: 'lessons',
          label: 'Lessons',
          icon: 'fas fa-chalkboard-teacher',
          route: '/setup/lms/lessons',
          description: 'Manage instructional lessons'
        },
        {
          id: 'lms-topics',
          label: 'Lesson Topics',
          icon: 'fas fa-heading',
          route: '/setup/lms/topics',
          description: 'Granular topic sections'
        },
        {
          id: 'lms-content',
          label: 'Learning Content',
          icon: 'fas fa-file-video',
          route: '/setup/lms/contents',
          description: 'Multimedia materials and resources'
        },
        {
          id: 'lms-quizzes',
          label: 'Quizzes & Exams',
          icon: 'fas fa-question-circle',
          route: '/setup/lms/quizzes',
          description: 'Create and manage online tests'
        },
        {
          id: 'question-bank',
          label: 'Question Bank',
          icon: 'fas fa-university',
          route: '/setup/lms/question-bank',
          description: 'Reusable question repository'
        },
        {
          id: 'lms-submissions',
          label: 'Student Submissions',
          icon: 'fas fa-user-check',
          route: '/setup/lms/submissions',
          description: 'View and grade student attempts'
        },
        {
          id: 'lms-rubrics',
          label: 'Grading Rubrics',
          icon: 'fas fa-tasks',
          route: '/setup/lms/rubrics',
          description: 'Define evaluation criteria'
        },
        {
          id: 'virtual-sessions',
          label: 'Virtual Classrooms',
          icon: 'fas fa-video',
          route: '/setup/lms/virtual-sessions',
          description: 'Schedule live Zoom/Meet sessions'
        },
        {
          id: 'virtual-attendance',
          label: 'Virtual Attendance',
          icon: 'fas fa-user-clock',
          route: '/setup/lms/virtual-attendance',
          description: 'Synchronized live session participation'
        },
        {
          id: 'lms-progress',
          label: 'Student Progress',
          icon: 'fas fa-chart-line',
          route: '/setup/lms/progress',
          description: 'Track lesson completion and time'
        },
        {
          id: 'lms-badges',
          label: 'Achievement Badges',
          icon: 'fas fa-medal',
          route: '/setup/lms/badges',
          description: 'Gamification reward management'
        },
        {
          id: 'lms-forums',
          label: 'Discussion Forums',
          icon: 'fas fa-comments',
          route: '/setup/lms/forums',
          description: 'Social learning and course discussions'
        },
        {
          id: 'lms-peer-reviews',
          label: 'Peer Feedback',
          icon: 'fas fa-user-friends',
          route: '/setup/lms/peer-reviews',
          description: 'Anonymous student feedback workflow'
        }
      ]
    },
    {
      id: 'tpd',
      title: 'Staff Training & Growth (TPD)',
      items: [
        {
          id: 'competencies',
          label: 'Competency Framework',
          icon: 'fas fa-graduation-cap',
          route: '/setup/tpd/competencies',
          description: 'Define and map required staff skills'
        },
        {
          id: 'skill-assessments',
          label: 'Skill Gap Analysis',
          icon: 'fas fa-user-check',
          route: '/setup/tpd/assessments',
          description: 'Self-evaluations and manager reviews'
        },
        {
          id: 'training-events',
          label: 'Workshop Manager',
          icon: 'fas fa-chalkboard',
          route: '/setup/tpd/events',
          description: 'Schedule internal and external trainings'
        },
        {
          id: 'cpd-ledger',
          label: 'CPD Credit Passport',
          icon: 'fas fa-passport',
          route: '/setup/tpd/ledger',
          description: 'Track professional development credits'
        },
        {
          id: 'professional-portfolios',
          label: 'Staff Portfolios',
          icon: 'fas fa-id-card',
          route: '/setup/tpd/portfolios',
          description: 'Career pathing and promotion readiness'
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
      if (item.queryParams) {
        this.router.navigate([item.route], { queryParams: item.queryParams });
      } else {
        this.router.navigate([item.route]);
      }
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