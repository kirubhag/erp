import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { FieldService } from '../../services/field.service';
import { ErpField } from '../../models/erp-field.model';
import { DynamicFieldRendererComponent } from '../dynamic-field-renderer/dynamic-field-renderer.component';

@Component({
  selector: 'app-entity-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, DynamicFieldRendererComponent],
  templateUrl: './entity-detail.component.html',
  styleUrls: ['./entity-detail.component.css']
})
export class EntityDetailComponent implements OnInit {
  entityType: string = '';
  entityId: string = '';
  entityData: any = {};
  fields: ErpField[] = [];
  loading: boolean = true;
  error: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private fieldService: FieldService
  ) { }

  ngOnInit(): void {
    // First check route data for entityType (used by module-specific routes like /finance/fee-types/:id)
    const routeData = this.route.snapshot.data;
    if (routeData && routeData['entityType']) {
      this.entityType = routeData['entityType'];
    }

    // Get ID from route params, and entityType if not already set from route data
    this.route.paramMap.subscribe(params => {
      if (!this.entityType) {
        this.entityType = params.get('entityType') || '';
      }
      this.entityId = params.get('id') || '';

      if (this.entityType && this.entityId) {
        this.loadFieldConfiguration();
        this.loadEntityData();
      } else {
        this.error = 'Invalid entity type or ID';
        this.loading = false;
      }
    });
  }

  private loadFieldConfiguration(): void {
    this.fieldService.getFieldsByEntityType(this.entityType.toUpperCase()).subscribe({
      next: (fields) => {
        this.fields = fields.filter(f => f.showInForm);
      },
      error: (error) => {
        console.error('Error loading field configuration:', error);
        // Continue without field configuration - fall back to basic display
      }
    });
  }

  private loadEntityData(): void {
    this.loading = true;
    const endpoint = this.getApiEndpoint();

    if (!endpoint) {
      this.error = `No API endpoint configured for entity type: ${this.entityType}`;
      this.loading = false;
      return;
    }

    const backendUrl = `http://localhost:8081${endpoint}`;

    this.http.get<any>(backendUrl).subscribe({
      next: (response) => {
        this.entityData = response;
        this.loading = false;
      },
      error: (error) => {
        console.error(`Error loading ${this.entityType} detail:`, error);
        this.error = `Failed to load ${this.entityType} details`;
        this.loading = false;
      }
    });
  }

  private getApiEndpoint(): string {
    const endpoints: { [key: string]: string } = {
      // Original entity types (lowercase)
      students: `/api/students/${this.entityId}`,
      staff: `/api/v1/staff/${this.entityId}`,
      attendance: `/api/attendance/${this.entityId}`,
      parents: `/api/parents/${this.entityId}`,
      subjects: `/api/subjects/${this.entityId}`,
      classes: `/api/classes/${this.entityId}`,
      
      // Financial module entity types (uppercase)
      FEE_TYPE: `/api/finance/fee-types/${this.entityId}`,
      FEE_STRUCTURE: `/api/finance/fee-structures/${this.entityId}`,
      FEE_DISCOUNT_RULE: `/api/finance/discounts/${this.entityId}`,
      FINE_CATEGORY: `/api/finance/fine-categories/${this.entityId}`,
      FINE_CONFIGURATION: `/api/finance/fine-management/${this.entityId}`,
      FEE_PAYMENT: `/api/finance/payments/${this.entityId}`,
      FINE_LEDGER: `/api/finance/fine-ledger/${this.entityId}`,
      DISCIPLINARY_INCIDENT: `/api/finance/incidents/${this.entityId}`,
      FINE_WAIVER_REQUEST: `/api/finance/waivers/${this.entityId}`,
      INVOICE: `/api/finance/invoices/${this.entityId}`,
      TRANSACTION: `/api/finance/transactions/${this.entityId}`,
      INVOICE_ITEM: `/api/finance/invoice-items/${this.entityId}`,
      SCHOLARSHIP_CATEGORY: `/api/finance/scholarship-categories/${this.entityId}`,
      SCHOLARSHIP_APPLICATION: `/api/finance/scholarship-applications/${this.entityId}`,
      CHART_OF_ACCOUNT: `/api/finance/accounts/${this.entityId}`,
      JOURNAL_ENTRY: `/api/finance/journal-entries/${this.entityId}`,
      ACCOUNTING_PERIOD: `/api/finance/accounting-periods/${this.entityId}`,
      BUDGET: `/api/finance/budgets/${this.entityId}`,
      BANK_STATEMENT: `/api/finance/bank-statements/${this.entityId}`,
      
      // Inventory / Assets & Supplies module entity types
      ASSET: `/api/inventory/assets/${this.entityId}`,
      CONSUMABLE: `/api/inventory/consumables/${this.entityId}`,
      VENDOR: `/api/inventory/vendors/${this.entityId}`,
      PURCHASE_ORDER: `/api/inventory/purchase-orders/${this.entityId}`,
      
      // Communication module entity types
      MESSAGE: `/api/setup/communication/messages/${this.entityId}`,
      ANNOUNCEMENT: `/api/setup/communication/announcements/${this.entityId}`,
      SUPPORT_TICKET: `/api/setup/communication/tickets/${this.entityId}`,
      
      // TPD module entity types
      COMPETENCY: `/api/tpd/competencies/${this.entityId}`,
      SKILL_ASSESSMENT: `/api/tpd/assessments/${this.entityId}`,
      TRAINING_EVENT: `/api/tpd/events/${this.entityId}`,
      CPD_LEDGER: `/api/tpd/ledger/${this.entityId}`,
      PROFESSIONAL_PORTFOLIO: `/api/tpd/portfolios/${this.entityId}`,
      
      // Leave Management module entity types
      LEAVE_TYPE: `/api/hr/leave-types/${this.entityId}`,
      LEAVE_REQUEST: `/api/hr/leave-requests/${this.entityId}`,
      LEAVE_BALANCE: `/api/hr/leave-balances/${this.entityId}`,
      
      // Library Management module entity types
      AUTHOR: `/api/library/authors/${this.entityId}`,
      PUBLISHER: `/api/library/publishers/${this.entityId}`,
      LIBRARY_RESOURCE: `/api/library/resources/${this.entityId}`,
      RESOURCE_ITEM: `/api/library/items/${this.entityId}`,
      LIBRARY_LOAN: `/api/library/loans/${this.entityId}`,
      LIBRARY_HOLD: `/api/library/holds/${this.entityId}`,
      LIBRARY_POLICY: `/api/library/policies/${this.entityId}`,
      LIBRARY_PURCHASE_REQUEST: `/api/library/purchase-requests/${this.entityId}`,
      LIBRARY_PO: `/api/library/pos/${this.entityId}`
    };

    return endpoints[this.entityType] || '';
  }

  goBack(): void {
    // Map entity types to their correct routes
    const routeMap: { [key: string]: string } = {
      // Original entity types
      students: '/students',
      staff: '/staff',
      attendance: '/attendance',
      parents: '/parents',
      subjects: '/subjects',
      classes: '/classes',
      
      // Financial module entity types
      FEE_TYPE: '/finance/fee-types',
      FEE_STRUCTURE: '/finance/fee-structures',
      FEE_DISCOUNT_RULE: '/finance/discounts',
      FINE_CATEGORY: '/finance/fine-categories',
      FINE_CONFIGURATION: '/finance/fine-management',
      FEE_PAYMENT: '/finance/payments',
      FINE_LEDGER: '/finance/fine-ledger',
      DISCIPLINARY_INCIDENT: '/finance/incidents',
      FINE_WAIVER_REQUEST: '/finance/waivers',
      INVOICE: '/finance/invoices',
      TRANSACTION: '/finance/transactions',
      INVOICE_ITEM: '/finance/invoice-items',
      SCHOLARSHIP_CATEGORY: '/finance/scholarship-categories',
      SCHOLARSHIP_APPLICATION: '/finance/scholarship-applications',
      CHART_OF_ACCOUNT: '/finance/accounts',
      JOURNAL_ENTRY: '/finance/journal-entries',
      ACCOUNTING_PERIOD: '/finance/accounting-periods',
      BUDGET: '/finance/budgets',
      BANK_STATEMENT: '/finance/bank-statements',
      
      // Inventory / Assets & Supplies module entity types
      ASSET: '/assets-supplies/inventory/assets',
      CONSUMABLE: '/assets-supplies/inventory/consumables',
      VENDOR: '/assets-supplies/inventory/vendors',
      PURCHASE_ORDER: '/assets-supplies/inventory/purchase-orders',
      
      // Communication module entity types
      MESSAGE: '/communication/messages',
      ANNOUNCEMENT: '/communication/announcements',
      SUPPORT_TICKET: '/communication/tickets',
      
      // TPD module entity types
      COMPETENCY: '/tpd/competencies',
      SKILL_ASSESSMENT: '/tpd/assessments',
      TRAINING_EVENT: '/tpd/events',
      CPD_LEDGER: '/tpd/ledger',
      PROFESSIONAL_PORTFOLIO: '/tpd/portfolios',
      
      // Leave Management module entity types
      LEAVE_TYPE: '/leave-management/leave-types',
      LEAVE_REQUEST: '/leave-management/leaves',
      LEAVE_BALANCE: '/leave-management/leave-balances',
      
      // Library Management module entity types
      AUTHOR: '/library/authors',
      PUBLISHER: '/library/publishers',
      LIBRARY_RESOURCE: '/library/resources',
      RESOURCE_ITEM: '/library/items',
      LIBRARY_LOAN: '/library/loans',
      LIBRARY_HOLD: '/library/holds',
      LIBRARY_POLICY: '/library/policies',
      LIBRARY_PURCHASE_REQUEST: '/library/purchase-requests',
      LIBRARY_PO: '/library/pos'
    };

    const route = routeMap[this.entityType] || '/dashboard';
    this.router.navigate([route]);
  }

  getPageTitle(): string {
    const titles: { [key: string]: string } = {
      // Original entity types
      students: 'Student Details',
      staff: 'Staff Member',
      attendance: 'Attendance Record',
      parents: 'Parent',
      subjects: 'Subject',
      classes: 'Class Details',
      
      // Financial module entity types
      FEE_TYPE: 'Fee Type Details',
      FEE_STRUCTURE: 'Fee Structure Details',
      FEE_DISCOUNT_RULE: 'Discount Rule Details',
      FINE_CATEGORY: 'Fine Category Details',
      FINE_CONFIGURATION: 'Fine Configuration',
      FEE_PAYMENT: 'Payment Details',
      FINE_LEDGER: 'Fine Ledger Entry',
      DISCIPLINARY_INCIDENT: 'Incident Details',
      FINE_WAIVER_REQUEST: 'Waiver Request',
      INVOICE: 'Invoice Details',
      TRANSACTION: 'Transaction Details',
      INVOICE_ITEM: 'Invoice Item',
      SCHOLARSHIP_CATEGORY: 'Scholarship Category',
      SCHOLARSHIP_APPLICATION: 'Scholarship Application',
      CHART_OF_ACCOUNT: 'Account Details',
      JOURNAL_ENTRY: 'Journal Entry',
      ACCOUNTING_PERIOD: 'Accounting Period',
      BUDGET: 'Budget Details',
      BANK_STATEMENT: 'Bank Statement',
      
      // Inventory / Assets & Supplies module entity types
      ASSET: 'Asset Details',
      CONSUMABLE: 'Consumable Details',
      VENDOR: 'Vendor Details',
      PURCHASE_ORDER: 'Purchase Order Details',
      
      // Communication module entity types
      MESSAGE: 'Message Details',
      ANNOUNCEMENT: 'Announcement Details',
      SUPPORT_TICKET: 'Support Ticket',
      
      // TPD module entity types
      COMPETENCY: 'Competency Details',
      SKILL_ASSESSMENT: 'Skill Assessment',
      TRAINING_EVENT: 'Training Event',
      CPD_LEDGER: 'CPD Ledger Entry',
      PROFESSIONAL_PORTFOLIO: 'Professional Portfolio',
      
      // Leave Management module entity types
      LEAVE_TYPE: 'Leave Type Details',
      LEAVE_REQUEST: 'Leave Request Details',
      LEAVE_BALANCE: 'Leave Balance Details',
      
      // Library Management module entity types
      AUTHOR: 'Author Details',
      PUBLISHER: 'Publisher Details',
      LIBRARY_RESOURCE: 'Library Resource Details',
      RESOURCE_ITEM: 'Resource Item Details',
      LIBRARY_LOAN: 'Library Loan Details',
      LIBRARY_HOLD: 'Library Hold Details',
      LIBRARY_POLICY: 'Library Policy Details',
      LIBRARY_PURCHASE_REQUEST: 'Purchase Request Details',
      LIBRARY_PO: 'Purchase Order Details'
    };
    return titles[this.entityType] || 'Entity Details';
  }

  getEntityLabel(key: string): string {
    // Find field configuration for this key
    const field = this.fields.find(f => f.fieldName === key);
    if (field) {
      return field.fieldLabel;
    }

    // Fallback: Convert camelCase to Title Case
    return key
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, str => str.toUpperCase())
      .trim();
  }

  getDisplayValue(key: string, value: any): string {
    // Find field configuration for this key
    const field = this.fields.find(f => f.fieldName === key);

    if (value === null || value === undefined) return '—';
    if (typeof value === 'boolean') return value ? 'Yes' : 'No';
    if (value instanceof Date) return value.toLocaleDateString();
    return String(value);
  }

  getEntityKeys(): string[] {
    if (!this.entityData) return [];

    // If we have field configuration, use that order
    if (this.fields.length > 0) {
      return this.fields
        .map(f => f.fieldName)
        .filter(name => this.entityData.hasOwnProperty(name));
    }

    // Fallback to all keys
    return Object.keys(this.entityData).filter(key =>
      !key.startsWith('_') && typeof this.entityData[key] !== 'object'
    );
  }

  getEntityIconClass(): string {
    const icons: { [key: string]: string } = {
      // Original entity types
      students: 'fas fa-user-graduate',
      staff: 'fas fa-users-cog',
      attendance: 'fas fa-clipboard-check',
      parents: 'fas fa-home',
      subjects: 'fas fa-book',
      classes: 'fas fa-building',
      
      // Financial module entity types
      FEE_TYPE: 'fas fa-tags',
      FEE_STRUCTURE: 'fas fa-layer-group',
      FEE_DISCOUNT_RULE: 'fas fa-percent',
      FINE_CATEGORY: 'fas fa-exclamation-triangle',
      FINE_CONFIGURATION: 'fas fa-cogs',
      FEE_PAYMENT: 'fas fa-credit-card',
      FINE_LEDGER: 'fas fa-book',
      DISCIPLINARY_INCIDENT: 'fas fa-gavel',
      FINE_WAIVER_REQUEST: 'fas fa-hand-holding-usd',
      INVOICE: 'fas fa-file-invoice-dollar',
      TRANSACTION: 'fas fa-exchange-alt',
      INVOICE_ITEM: 'fas fa-list',
      SCHOLARSHIP_CATEGORY: 'fas fa-graduation-cap',
      SCHOLARSHIP_APPLICATION: 'fas fa-file-alt',
      CHART_OF_ACCOUNT: 'fas fa-sitemap',
      JOURNAL_ENTRY: 'fas fa-pen',
      ACCOUNTING_PERIOD: 'fas fa-calendar-alt',
      BUDGET: 'fas fa-chart-pie',
      BANK_STATEMENT: 'fas fa-university',
      
      // Inventory / Assets & Supplies module entity types
      ASSET: 'fas fa-desktop',
      CONSUMABLE: 'fas fa-boxes',
      VENDOR: 'fas fa-truck',
      PURCHASE_ORDER: 'fas fa-file-invoice',
      
      // Communication module entity types
      MESSAGE: 'fas fa-envelope',
      ANNOUNCEMENT: 'fas fa-bullhorn',
      SUPPORT_TICKET: 'fas fa-ticket-alt',
      
      // TPD module entity types
      COMPETENCY: 'fas fa-award',
      SKILL_ASSESSMENT: 'fas fa-clipboard-list',
      TRAINING_EVENT: 'fas fa-chalkboard-teacher',
      CPD_LEDGER: 'fas fa-book-open',
      PROFESSIONAL_PORTFOLIO: 'fas fa-briefcase',
      
      // Leave Management module entity types
      LEAVE_TYPE: 'fas fa-cog',
      LEAVE_REQUEST: 'fas fa-calendar-minus',
      LEAVE_BALANCE: 'fas fa-chart-pie',
      
      // Library Management module entity types
      AUTHOR: 'fas fa-user-edit',
      PUBLISHER: 'fas fa-building',
      LIBRARY_RESOURCE: 'fas fa-book',
      RESOURCE_ITEM: 'fas fa-barcode',
      LIBRARY_LOAN: 'fas fa-hand-holding',
      LIBRARY_HOLD: 'fas fa-clock',
      LIBRARY_POLICY: 'fas fa-gavel',
      LIBRARY_PURCHASE_REQUEST: 'fas fa-file-alt',
      LIBRARY_PO: 'fas fa-file-invoice'
    };
    return icons[this.entityType] || 'fas fa-file';
  }

  getStatusBadge(): string {
    if (this.entityData.isActive === true || this.entityData.status === 'ACTIVE') {
      return 'Active';
    }
    if (this.entityData.isActive === false || this.entityData.status === 'INACTIVE') {
      return 'Inactive';
    }
    return this.entityData.status || 'Active';
  }

  toggleDetailsVisibility(event: any): void {
    const header = event.currentTarget;
    const icon = header.querySelector('i');
    const details = header.nextElementSibling;

    if (icon.classList.contains('fa-chevron-up')) {
      icon.classList.remove('fa-chevron-up');
      icon.classList.add('fa-chevron-down');
      header.querySelector('span').textContent = 'Show Details';
      if (details) {
        details.style.display = 'none';
      }
    } else {
      icon.classList.remove('fa-chevron-down');
      icon.classList.add('fa-chevron-up');
      header.querySelector('span').textContent = 'Hide Details';
      if (details) {
        details.style.display = 'block';
      }
    }
  }

  getFieldForKey(key: string): ErpField | undefined {
    return this.fields.find(f => f.fieldName === key);
  }
}
