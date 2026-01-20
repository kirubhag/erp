import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { EntityListComponent, EntityColumn, PaginationInfo } from '../entity-list/entity-list.component';
import { HttpClient } from '@angular/common/http';
import { Subject, takeUntil } from 'rxjs';

interface ApiResponse {
  content?: any[];
  data?: any[];
  records?: any[];
  _embedded?: { [key: string]: any[] };
  totalElements?: number;
  total?: number;
  size?: number;
  number?: number;
  totalPages?: number;
}

@Component({
  selector: 'app-route-data-entity',
  standalone: true,
  imports: [CommonModule, EntityListComponent],
  template: `
    <app-entity-list
      [title]="title"
      [entityName]="entityName"
      [entityNamePlural]="entityNamePlural"
      [entityType]="fieldsApiEntityType"
      [columns]="columns"
      [data]="data"
      [pagination]="pagination"
      [loading]="loading"
      [userId]="userId"
      [organizationId]="organizationId"
      [fieldMetadata]="fieldMetadata"
      (pageChange)="onPageChange($event)"
      (searchChange)="onSearch($event)"
      (rowClick)="onRowClick($event)"
      (actionClick)="onActionClick($event)"
    ></app-entity-list>
  `,
  styles: []
})
export class RouteDataEntityComponent implements OnInit, OnDestroy {
  entityType: string = '';
  fieldsApiEntityType: string = ''; // Kebab-case version for /api/fields/ endpoint
  title: string = '';
  entityName: string = '';
  entityNamePlural: string = '';
  columns: EntityColumn[] = [];
  data: any[] = [];
  fieldMetadata: any[] = []; // Store field metadata to pass to sidebar
  loading: boolean = false;
  userId: number | undefined;
  organizationId: number | undefined;
  pagination: PaginationInfo = {
    currentPage: 1,
    itemsPerPage: 50,
    totalItems: 0,
    totalPages: 1
  };

  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    console.log('RouteDataEntityComponent initialized');
    
    // Get user context from local storage
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.userId = user.userId;
      this.organizationId = user.organizationId;
    }

    // Get entity type from route data
    this.route.data.pipe(takeUntil(this.destroy$)).subscribe(data => {
      this.entityType = data['entityType'];
      console.log('Entity type from route data:', this.entityType);
      
      if (this.entityType) {
        // Initialize the kebab-case version for API calls
        this.fieldsApiEntityType = this.getFieldsApiEntityType();
        console.log('Fields API entity type:', this.fieldsApiEntityType);
        
        this.initializeEntity();
        this.loadDynamicColumns();
        this.loadData();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeEntity(): void {
    // Convert UPPER_SNAKE_CASE to Title Case for display
    const words = this.entityType.split('_').map(word => 
      word.charAt(0) + word.slice(1).toLowerCase()
    );
    
    this.entityName = words.join(' ');
    this.entityNamePlural = this.entityName + 's'; // Simple pluralization
    this.title = this.entityNamePlural;
    
    console.log(`Initialized entity: ${this.entityName} (${this.entityType})`);
  }

  private getFieldsApiEntityType(): string {
    // Map system names to the format expected by /api/fields/ endpoint
    // The backend typically uses simple names without underscores
    const entityTypeMap: { [key: string]: string } = {
      'FEE_TYPE': 'fee-types',
      'FEE_STRUCTURE': 'fee-structures', 
      'FEE_DISCOUNT_RULE': 'discounts',
      'FINE_CATEGORY': 'fine-categories',
      'FINE_CONFIGURATION': 'fine-management',
      'FEE_PAYMENT': 'payments',
      'FINE_LEDGER': 'fine-ledger',
      'DISCIPLINARY_INCIDENT': 'incidents',
      'FINE_WAIVER_REQUEST': 'waivers',
      'INVOICE': 'invoices',
      'TRANSACTION': 'transactions',
      'INVOICE_ITEM': 'invoice-items',
      'SCHOLARSHIP_CATEGORY': 'scholarship-categories',
      'SCHOLARSHIP_APPLICATION': 'scholarship-applications',
      'CHART_OF_ACCOUNT': 'accounts',
      'JOURNAL_ENTRY': 'journal-entries',
      'ACCOUNTING_PERIOD': 'accounting-periods',
      'BUDGET': 'budgets',
      'BANK_STATEMENT': 'bank-statements',
      'MESSAGE': 'messages',
      'ANNOUNCEMENT': 'announcements',
      'SUPPORT_TICKET': 'tickets',
      // Library module
      'AUTHOR': 'author',
      'PUBLISHER': 'publisher',
      'LIBRARY_RESOURCE': 'library-resource',
      'RESOURCE_ITEM': 'resource-item',
      'LIBRARY_LOAN': 'library-loan',
      'LIBRARY_HOLD': 'library-hold',
      'LIBRARY_POLICY': 'library-policy',
      'LIBRARY_PURCHASE_REQUEST': 'library-purchase-request',
      'LIBRARY_PO': 'library-po',
      // Leave Management module
      'LEAVE_TYPE': 'leave_type',
      'LEAVE_REQUEST': 'leave_request',
      'LEAVE_BALANCE': 'leave_balance',
      // Learning & Teaching - LMS entities
      'LMS_MODULE': 'lms-module',
      'LESSON': 'lesson',
      'LMS_TOPIC': 'lms-topic',
      'LMS_CONTENT': 'lms-content',
      'LMS_QUIZ': 'lms-quiz',
      'LMS_QUESTION_BANK': 'lms-question-bank',
      'LMS_SUBMISSION': 'lms-submission',
      'LMS_RUBRIC': 'lms-rubric',
      'VIRTUAL_SESSION': 'virtual-session',
      'VIRTUAL_ATTENDANCE': 'virtual-attendance',
      'STUDENT_PROGRESS': 'student-progress',
      'LMS_BADGE': 'lms-badge',
      'LMS_POINT_LOG': 'lms-point-log',
      'LMS_FORUM': 'lms-forum',
      'LMS_FORUM_POST': 'lms-forum-post',
      'LMS_PEER_REVIEW': 'lms-peer-review',
      // Learning & Teaching - TPD entities
      'COMPETENCY': 'competency',
      'SKILL_ASSESSMENT': 'skill-assessment',
      'TRAINING_EVENT': 'training-event',
      'TRAINING_ATTENDANCE': 'training-attendance',
      'CPD_LEDGER': 'cpd-ledger',
      'PROFESSIONAL_PORTFOLIO': 'professional-portfolio',
      'EVIDENCE': 'evidence',
      'EVALUATION': 'evaluation'
    };

    return entityTypeMap[this.entityType] || this.entityType.toLowerCase().replace(/_/g, '-');
  }

  private loadDynamicColumns(): void {
    const fieldsEntityType = this.getFieldsApiEntityType();
    console.log(`Loading dynamic columns for ${this.entityType} (using ${fieldsEntityType} for fields API)`);

    this.http.get<any[]>(`/api/fields/${fieldsEntityType}`).subscribe({
      next: (fields) => {
        // Store field metadata for sidebar
        this.fieldMetadata = fields || [];
        
        if (fields && fields.length > 0) {
          this.columns = fields
            .filter(f => f.showInList)
            .sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0))
            .map(f => ({
              key: f.fieldName,
              label: f.fieldLabel,
              type: this.mapFieldTypeToColumnType(f.fieldType),
              sortable: f.isSortable || false,
              width: f.defaultWidth ? `${f.defaultWidth}px` : undefined,
              uiType: f.uiType
            }));
          console.log(`Loaded ${this.columns.length} dynamic columns for ${this.entityType}`);
        } else {
          console.warn(`No fields found for ${this.entityType}`);
        }
      },
      error: (err) => {
        console.error(`Failed to load dynamic columns for ${this.entityType}:`, err);
        // Set some default columns as fallback
        this.columns = [
          { key: 'id', label: 'ID', type: 'text', sortable: true }
        ];
      }
    });
  }

  private mapFieldTypeToColumnType(fieldType: string): 'text' | 'email' | 'badge' | 'date' | 'avatar' | 'custom' | 'number' {
    switch (fieldType) {
      case 'EMAIL': return 'email';
      case 'DATE':
      case 'DATETIME': return 'date';
      case 'NUMBER':
      case 'DECIMAL':
      case 'LONG': return 'number';
      case 'ENUM':
      case 'BOOLEAN': return 'badge';
      default: return 'text';
    }
  }

  private getApiEndpoint(): string {
    // Convert entity type to API endpoint
    // FEE_TYPE -> fee-types
    const pathSegment = this.entityType
      .toLowerCase()
      .replace(/_/g, '-')
      .replace(/-([a-z])/g, (_, letter) => letter); // Remove hyphens before letters for compound words
    
    // Try common patterns
    const commonEndpoints: { [key: string]: string } = {
      // Finance module
      'FEE_TYPE': '/api/finance/fee-types',
      'FEE_STRUCTURE': '/api/finance/fee-structures',
      'FEE_DISCOUNT_RULE': '/api/finance/discounts',
      'FINE_CATEGORY': '/api/finance/fine-categories',
      'FINE_CONFIGURATION': '/api/finance/fine-management',
      'FEE_PAYMENT': '/api/finance/payments',
      'FINE_LEDGER': '/api/finance/fine-ledger',
      'DISCIPLINARY_INCIDENT': '/api/finance/incidents',
      'FINE_WAIVER_REQUEST': '/api/finance/waivers',
      'INVOICE': '/api/finance/invoices',
      'TRANSACTION': '/api/finance/transactions',
      'INVOICE_ITEM': '/api/finance/invoice-items',
      'SCHOLARSHIP_CATEGORY': '/api/finance/scholarship-categories',
      'SCHOLARSHIP_APPLICATION': '/api/finance/scholarship-applications',
      'CHART_OF_ACCOUNT': '/api/finance/accounts',
      'JOURNAL_ENTRY': '/api/finance/journal-entries',
      'ACCOUNTING_PERIOD': '/api/finance/accounting-periods',
      'BUDGET': '/api/finance/budgets',
      'BANK_STATEMENT': '/api/finance/bank-statements',
      
      // Inventory / Assets & Supplies module
      'ASSET': '/api/inventory/assets',
      'CONSUMABLE': '/api/inventory/consumables',
      'VENDOR': '/api/inventory/vendors',
      'PURCHASE_ORDER': '/api/inventory/purchase-orders',
      
      // Library module
      'AUTHOR': '/api/library/authors',
      'PUBLISHER': '/api/library/publishers',
      'LIBRARY_RESOURCE': '/api/library/resources',
      'RESOURCE_ITEM': '/api/library/items',
      'LIBRARY_LOAN': '/api/library/loans',
      'LIBRARY_HOLD': '/api/library/holds',
      'LIBRARY_POLICY': '/api/library/policies',
      'LIBRARY_PURCHASE_REQUEST': '/api/library/purchase-requests',
      'LIBRARY_PO': '/api/library/pos',
      
      // Leave Management module
      'LEAVE_TYPE': '/api/hr/leave-types',
      'LEAVE_REQUEST': '/api/hr/leave-requests',
      'LEAVE_BALANCE': '/api/hr/leave-balances',
      
      // Communication module
      'MESSAGE': '/api/setup/communication/messages',
      'ANNOUNCEMENT': '/api/setup/communication/announcements',
      'SUPPORT_TICKET': '/api/setup/communication/tickets',
      
      // Learning & Teaching - LMS entities
      'LMS_MODULE': '/api/lms/modules',
      'LESSON': '/api/lms/lessons',
      'LMS_TOPIC': '/api/lms/topics',
      'LMS_CONTENT': '/api/lms/content',
      'LMS_QUIZ': '/api/lms/quizzes',
      'LMS_QUESTION_BANK': '/api/lms/question-banks',
      'LMS_SUBMISSION': '/api/lms/submissions',
      'LMS_RUBRIC': '/api/lms/rubrics',
      'VIRTUAL_SESSION': '/api/lms/virtual-sessions',
      'VIRTUAL_ATTENDANCE': '/api/lms/virtual-attendance',
      'STUDENT_PROGRESS': '/api/lms/student-progress',
      'LMS_BADGE': '/api/lms/badges',
      'LMS_POINT_LOG': '/api/lms/point-logs',
      'LMS_FORUM': '/api/lms/forums',
      'LMS_FORUM_POST': '/api/lms/forum-posts',
      'LMS_PEER_REVIEW': '/api/lms/peer-reviews',
      
      // Learning & Teaching - TPD entities
      'COMPETENCY': '/api/tpd/competencies',
      'SKILL_ASSESSMENT': '/api/tpd/skill-assessments',
      'TRAINING_EVENT': '/api/tpd/training-events',
      'TRAINING_ATTENDANCE': '/api/tpd/training-attendance',
      'CPD_LEDGER': '/api/tpd/cpd-ledger',
      'PROFESSIONAL_PORTFOLIO': '/api/tpd/portfolios',
      'EVIDENCE': '/api/tpd/evidence',
      'EVALUATION': '/api/tpd/evaluations'
    };

    return commonEndpoints[this.entityType] || `/api/${pathSegment}`;
  }

  loadData(page: number = 1): void {
    console.log(`Loading data for ${this.entityType}, page ${page}`);
    
    this.loading = true;
    const endpoint = this.getApiEndpoint();

    console.log(`Fetching from endpoint: ${endpoint}`);

    const params = {
      page: page - 1,
      size: this.pagination.itemsPerPage
    };

    this.http.get<ApiResponse>(endpoint, { params }).subscribe({
      next: (response) => {
        console.log(`Received response for ${this.entityType}:`, response);
        this.data = this.extractData(response);
        this.pagination.totalItems = this.extractTotalElements(response);
        this.pagination.totalPages = Math.ceil(
          this.pagination.totalItems / this.pagination.itemsPerPage
        );
        this.pagination.currentPage = page;
        this.loading = false;
        console.log(`Loaded ${this.data.length} ${this.entityType} records`);
      },
      error: (error) => {
        console.error(`Error loading ${this.entityType}:`, error);
        this.loading = false;
        this.data = [];
      }
    });
  }

  private extractData(response: ApiResponse): any[] {
    if (Array.isArray(response)) {
      return response;
    }
    if (response.content) {
      return response.content;
    }
    if (response.data) {
      return response.data;
    }
    if (response.records) {
      return response.records;
    }
    if (response._embedded) {
      const key = Object.keys(response._embedded)[0];
      return response._embedded[key] || [];
    }
    return [];
  }

  private extractTotalElements(response: ApiResponse): number {
    return response.totalElements || response.total || 0;
  }

  onPageChange(page: number): void {
    this.loadData(page);
  }

  onSearch(searchTerm: string): void {
    console.log('Searching for:', searchTerm);
    // TODO: Implement search functionality
  }

  onRowClick(event: { entityType: string; item: any }): void {
    const { item } = event;
    const itemId = item.id || item.erp_entity_id;
    if (itemId) {
      this.router.navigate(['/entity-detail', this.entityType, itemId]);
    }
  }

  onActionClick(event: { action: string; item?: any; selectedItems?: any[] }): void {
    switch (event.action) {
      case 'import':
        console.log(`Import ${this.entityType}`);
        this.router.navigate(['/import-wizard'], {
          queryParams: { entityType: this.entityType }
        });
        break;
      case 'export':
        console.log(`Export ${this.entityType}`, event.selectedItems || 'all');
        break;
      case 'delete':
        if (event.item) {
          console.log(`Delete ${this.entityType}`, event.item);
        } else if (event.selectedItems && event.selectedItems.length > 0) {
          console.log(`Delete ${event.selectedItems.length} ${this.entityType} items`);
        }
        break;
      default:
        console.log('Unhandled action:', event.action);
    }
  }
}
