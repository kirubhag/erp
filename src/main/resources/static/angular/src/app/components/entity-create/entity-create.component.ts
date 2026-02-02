import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { UIFieldTypeService } from '../../services/ui-field-type.service';
import { LookupModalComponent } from '../lookup-modal/lookup-modal.component';

export interface FieldDefinition {
  fieldName: string;
  displayLabel: string;
  uiType: number;
  dataType: string;
  isRequired: boolean;
  isReadonly: boolean;
  isAutoNumber?: boolean;
  autoNumberPrefix?: string;
  autoNumberSuffix?: string;
  autoNumberPreview?: string;
  defaultValue?: any;
  picklistValues?: string[];
  maxLength?: number;
  minValue?: number | null;
  maxValue?: number | null;
  decimalPlaces?: number;
  section?: string;
  step?: number | null;
  placeholder?: string;
}

export interface SectionFields {
  sectionName: string;
  fields: FieldDefinition[];
}

@Component({
  selector: 'app-entity-create',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, LookupModalComponent],
  templateUrl: './entity-create.component.html',
  styleUrls: ['./entity-create.component.css']
})
export class EntityCreateComponent implements OnInit {
  entityType: string = '';
  entityId: string | null = null; // For edit mode
  entityName: string = '';
  entityNamePlural: string = '';
  isEditMode: boolean = false;

  fieldDefinitions: FieldDefinition[] = [];
  sectionsWithFields: SectionFields[] = [];
  autoNumberPreviews: { [key: string]: string } = {};

  entityForm: FormGroup = new FormGroup({});
  originalEntityData: any = null; // Store original data for edit mode
  loading = false;
  saving = false;
  error: string | null = null;
  success = false;
  showCancelModal = false; // For Bootstrap cancel confirmation modal

  // Image upload
  imagePreview: string | null = null;
  selectedImageFile: File | null = null;

  // Lookup modal
  showLookupModal = false;
  currentLookupField: FieldDefinition | null = null;
  lookupFieldLabel: string = '';
  lookupEntityType: string = '';
  lookupDisplayNames: { [key: string]: string } = {}; // Store display names for selected lookup values

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder,
    private uiFieldTypeService: UIFieldTypeService
  ) { }

  ngOnInit(): void {
    // Get entity type and id from route params or query params
    this.route.paramMap.subscribe(params => {
      const newEntityType = params.get('entityType') || '';
      const newEntityId = params.get('id') || null;

      // Only reload if entity type or ID changed
      if (newEntityType !== this.entityType || newEntityId !== this.entityId) {
        this.entityType = newEntityType;
        this.entityId = newEntityId;
        this.isEditMode = !!this.entityId;

        if (this.entityType) {
          // Reset internal state
          this.fieldDefinitions = [];
          this.sectionsWithFields = [];
          this.entityForm = new FormGroup({});
          this.originalEntityData = null;

          this.loadEntityMetadata();
        }
      }
    });
  }


  /**
   * Load entity metadata including field definitions
   */
  loadEntityMetadata(): void {
    this.loading = true;
    this.error = null;

    this.http.get<any>(`/api/module/${this.entityType}/metadata`).subscribe({
      next: (metadata) => {
        console.log('Received metadata:', metadata);
        this.entityName = metadata.entityName || this.entityType;
        this.entityNamePlural = metadata.entityNamePlural || this.entityType + 's';
        this.fieldDefinitions = metadata.fields || [];
        this.autoNumberPreviews = metadata.autoNumberPreviews || {};
        console.log('Field definitions:', this.fieldDefinitions);
        console.log('Auto-number previews:', this.autoNumberPreviews);

        // Group fields by section
        this.groupFieldsBySection();
        console.log('Sections with fields:', this.sectionsWithFields);

        // Build form
        this.buildForm();

        // If edit mode, load existing entity data
        if (this.isEditMode && this.entityId) {
          this.loadEntityData();
        } else {
          this.loading = false;
        }
      },
      error: (err) => {
        console.error('Error loading entity metadata:', err);
        this.error = 'Failed to load entity information';
        this.loading = false;
      }
    });
  }

  /**
   * Load existing entity data for edit mode
   */
  loadEntityData(): void {
    const endpoint = this.getApiEndpoint();
    if (!endpoint) {
      this.error = `No API endpoint configured for entity type: ${this.entityType}`;
      this.loading = false;
      return;
    }

    this.http.get<any>(endpoint).subscribe({
      next: (data) => {
        console.log('Loaded entity data for edit:', data);
        // Store original data to merge with form changes later
        this.originalEntityData = { ...data };
        // Patch the form with the existing data
        this.entityForm.patchValue(data);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading entity data:', err);
        this.error = 'Failed to load entity data for editing';
        this.loading = false;
      }
    });
  }

  /**
   * Get API endpoint for the entity type (for GET/PUT operations)
   */
  private getApiEndpoint(): string {
    const endpoints: { [key: string]: string } = {
      // Original entity types (lowercase)
      students: `/api/students/${this.entityId}`,
      staff: `/api/staff/${this.entityId}`,
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
      LIBRARY_PO: `/api/library/pos/${this.entityId}`,

      // HR module entity types
      STAFF: `/api/staff/${this.entityId}`,
      Staff: `/api/staff/${this.entityId}`,
      PAYROLL_RUN: `/api/hr/payroll-runs/${this.entityId}`,
      JOB_POSTING: `/api/hr/job-postings/${this.entityId}`,
      JOB_APPLICATION: `/api/hr/job-applications/${this.entityId}`,
      PERFORMANCE_CYCLE: `/api/hr/performance-cycles/${this.entityId}`,
      PERFORMANCE_CRITERIA: `/api/hr/performance-criteria/${this.entityId}`,
      PERFORMANCE_REVIEW: `/api/hr/performance-reviews/${this.entityId}`,
      STAFF_SALARY: `/api/hr/staff-salaries/${this.entityId}`,
      PAYSLIP: `/api/hr/payslips/${this.entityId}`,
      DEPARTMENT: `/api/hr/departments/${this.entityId}`,
      DESIGNATION: `/api/hr/designations/${this.entityId}`,

      // Admission module entity types
      ADMISSION_INQUIRY: `/api/admissions/inquiries/${this.entityId}`,
      ADMISSION_APPLICATION: `/api/admissions/applications/${this.entityId}`,
      ADMISSION_CYCLE: `/api/admissions/cycles/${this.entityId}`,
      ADMISSION_SEAT_ALLOCATION: `/api/admissions/seat-allocations/${this.entityId}`,
      STUDENT_REGISTRATION: `/api/admissions/registrations/${this.entityId}`,

      // Academic module entity types
      STUDENT: `/api/students/${this.entityId}`,
      Student: `/api/students/${this.entityId}`,
      PARENT: `/api/parents/${this.entityId}`,
      Parent: `/api/parents/${this.entityId}`,
      SUBJECT: `/api/subjects/${this.entityId}`,
      Subject: `/api/subjects/${this.entityId}`,
      ROOM: `/api/rooms/${this.entityId}`,
      Room: `/api/rooms/${this.entityId}`,
      ATTENDANCE: `/api/attendance/${this.entityId}`,
      Attendance: `/api/attendance/${this.entityId}`,
      TIMETABLE: `/api/timetables/${this.entityId}`,
      Timetable: `/api/timetables/${this.entityId}`,
      ADDRESS: `/api/addresses/${this.entityId}`,
      ACADEMIC_SETTINGS: `/api/academic/settings/${this.entityId}`,
      ACADEMIC_YEAR: `/api/academic/years/${this.entityId}`,
      GRADING_SCALE: `/api/academic/grading-scales/${this.entityId}`,
      TERM: `/api/academic/terms/${this.entityId}`,
      ERP_CLASS: `/api/classes/${this.entityId}`,

      // Alumni module entity types
      ALUMNI: `/api/alumni/${this.entityId}`,
      ALUMNI_CONTRIBUTION: `/api/alumni/contributions/${this.entityId}`,

      // Calendar module entity types
      CALENDAR_DAY: `/api/calendar/days/${this.entityId}`,
      INSTITUTION_EVENT: `/api/calendar/events/${this.entityId}`,
      HOLIDAY: `/api/calendar/holidays/${this.entityId}`,

      // Document module entity types
      ERP_DOCUMENT: `/api/documents/${this.entityId}`,

      // LMS module entity types
      LMS_MODULE: `/api/lms/modules/${this.entityId}`,
      LESSON: `/api/lms/lessons/${this.entityId}`,
      LMS_TOPIC: `/api/lms/topics/${this.entityId}`,
      LMS_CONTENT: `/api/lms/contents/${this.entityId}`,
      LMS_QUIZ: `/api/lms/quizzes/${this.entityId}`,
      LMS_QUESTION: `/api/lms/questions/${this.entityId}`,
      LMS_ANSWER: `/api/lms/answers/${this.entityId}`,
      LMS_SUBMISSION: `/api/lms/submissions/${this.entityId}`,
      LMS_STUDENT_PROGRESS: `/api/lms/progress/${this.entityId}`,
      VIRTUAL_SESSION: `/api/lms/virtual-sessions/${this.entityId}`,
      LMS_QUESTION_BANK: `/api/lms/question-banks/${this.entityId}`,
      LMS_RUBRIC: `/api/lms/rubrics/${this.entityId}`,
      LMS_BADGE: `/api/lms/badges/${this.entityId}`,
      LMS_POINT_LOG: `/api/lms/point-logs/${this.entityId}`,
      LMS_FORUM: `/api/lms/forums/${this.entityId}`,
      LMS_FORUM_POST: `/api/lms/forum-posts/${this.entityId}`,
      LMS_PEER_REVIEW: `/api/lms/peer-reviews/${this.entityId}`,

      // Facility/Maintenance module entity types
      FACILITY: `/api/maintenance/facilities/${this.entityId}`,
      FACILITY_BOOKING: `/api/maintenance/bookings/${this.entityId}`,
      WORK_ORDER: `/api/maintenance/work-orders/${this.entityId}`,

      // User/System module entity types
      USER: `/api/users/${this.entityId}`,
      ORGANIZATION: `/api/organizations/${this.entityId}`,
      ORGANIZATION_SETTINGS: `/api/organizations/settings/${this.entityId}`,
      NOTIFICATION_LOG: `/api/notifications/logs/${this.entityId}`,
      NOTIFICATION_TEMPLATE: `/api/notifications/templates/${this.entityId}`,
      TICKET_COMMENT: `/api/tickets/comments/${this.entityId}`,

      // Health module entity types
      HEALTH_RECORD: `/api/health/records/${this.entityId}`,
      STUDENT_GUARDIAN: `/api/students/guardians/${this.entityId}`,
      STUDENT_MEDICAL: `/api/students/medical/${this.entityId}`
    };

    return endpoints[this.entityType] || `/api/${this.entityType.toLowerCase()}/${this.entityId}`;
  }

  /**
   * Get base API endpoint for POST operations (without ID)
   */
  private getBaseApiEndpoint(): string {
    const endpoints: { [key: string]: string } = {
      // Original entity types (lowercase)
      students: `/api/students`,
      staff: `/api/staff`,
      attendance: `/api/attendance`,
      parents: `/api/parents`,
      subjects: `/api/subjects`,
      classes: `/api/classes`,

      // Financial module entity types (uppercase)
      FEE_TYPE: `/api/finance/fee-types`,
      FEE_STRUCTURE: `/api/finance/fee-structures`,
      FEE_DISCOUNT_RULE: `/api/finance/discounts`,
      FINE_CATEGORY: `/api/finance/fine-categories`,
      FINE_CONFIGURATION: `/api/finance/fine-management`,
      FEE_PAYMENT: `/api/finance/payments`,
      FINE_LEDGER: `/api/finance/fine-ledger`,
      DISCIPLINARY_INCIDENT: `/api/finance/incidents`,
      FINE_WAIVER_REQUEST: `/api/finance/waivers`,
      INVOICE: `/api/finance/invoices`,
      TRANSACTION: `/api/finance/transactions`,
      INVOICE_ITEM: `/api/finance/invoice-items`,
      SCHOLARSHIP_CATEGORY: `/api/finance/scholarship-categories`,
      SCHOLARSHIP_APPLICATION: `/api/finance/scholarship-applications`,
      CHART_OF_ACCOUNT: `/api/finance/accounts`,
      JOURNAL_ENTRY: `/api/finance/journal-entries`,
      ACCOUNTING_PERIOD: `/api/finance/accounting-periods`,
      BUDGET: `/api/finance/budgets`,
      BANK_STATEMENT: `/api/finance/bank-statements`,

      // Inventory / Assets & Supplies module entity types
      ASSET: `/api/inventory/assets`,
      CONSUMABLE: `/api/inventory/consumables`,
      VENDOR: `/api/inventory/vendors`,
      PURCHASE_ORDER: `/api/inventory/purchase-orders`,

      // Communication module entity types
      MESSAGE: `/api/setup/communication/messages`,
      ANNOUNCEMENT: `/api/setup/communication/announcements`,
      SUPPORT_TICKET: `/api/setup/communication/tickets`,

      // TPD module entity types
      COMPETENCY: `/api/tpd/competencies`,
      SKILL_ASSESSMENT: `/api/tpd/assessments`,
      TRAINING_EVENT: `/api/tpd/events`,
      CPD_LEDGER: `/api/tpd/ledger`,
      PROFESSIONAL_PORTFOLIO: `/api/tpd/portfolios`,

      // Leave Management module entity types
      LEAVE_TYPE: `/api/hr/leave-types`,
      LEAVE_REQUEST: `/api/hr/leave-requests`,
      LEAVE_BALANCE: `/api/hr/leave-balances`,

      // Library Management module entity types
      AUTHOR: `/api/library/authors`,
      PUBLISHER: `/api/library/publishers`,
      LIBRARY_RESOURCE: `/api/library/resources`,
      RESOURCE_ITEM: `/api/library/items`,
      LIBRARY_LOAN: `/api/library/loans`,
      LIBRARY_HOLD: `/api/library/holds`,
      LIBRARY_POLICY: `/api/library/policies`,
      LIBRARY_PURCHASE_REQUEST: `/api/library/purchase-requests`,
      LIBRARY_PO: `/api/library/pos`,

      // HR module entity types
      STAFF: `/api/staff`,
      Staff: `/api/staff`,
      PAYROLL_RUN: `/api/hr/payroll-runs`,
      JOB_POSTING: `/api/hr/job-postings`,
      JOB_APPLICATION: `/api/hr/job-applications`,
      PERFORMANCE_CYCLE: `/api/hr/performance-cycles`,
      PERFORMANCE_CRITERIA: `/api/hr/performance-criteria`,
      PERFORMANCE_REVIEW: `/api/hr/performance-reviews`,
      STAFF_SALARY: `/api/hr/staff-salaries`,
      PAYSLIP: `/api/hr/payslips`,
      DEPARTMENT: `/api/hr/departments`,
      DESIGNATION: `/api/hr/designations`,

      // Admission module entity types
      ADMISSION_INQUIRY: `/api/admissions/inquiries`,
      ADMISSION_APPLICATION: `/api/admissions/applications`,
      ADMISSION_CYCLE: `/api/admissions/cycles`,
      ADMISSION_SEAT_ALLOCATION: `/api/admissions/seat-allocations`,
      STUDENT_REGISTRATION: `/api/admissions/registrations`,

      // Academic module entity types
      STUDENT: `/api/students`,
      Student: `/api/students`,
      PARENT: `/api/parents`,
      Parent: `/api/parents`,
      SUBJECT: `/api/subjects`,
      Subject: `/api/subjects`,
      ROOM: `/api/rooms`,
      Room: `/api/rooms`,
      ATTENDANCE: `/api/attendance`,
      Attendance: `/api/attendance`,
      TIMETABLE: `/api/timetables`,
      Timetable: `/api/timetables`,
      ADDRESS: `/api/addresses`,
      ACADEMIC_SETTINGS: `/api/academic/settings`,
      ACADEMIC_YEAR: `/api/academic/years`,
      GRADING_SCALE: `/api/academic/grading-scales`,
      TERM: `/api/academic/terms`,
      ERP_CLASS: `/api/classes`,

      // Alumni module entity types
      ALUMNI: `/api/alumni`,
      ALUMNI_CONTRIBUTION: `/api/alumni/contributions`,

      // Calendar module entity types
      CALENDAR_DAY: `/api/calendar/days`,
      INSTITUTION_EVENT: `/api/calendar/events`,
      HOLIDAY: `/api/calendar/holidays`,

      // Document module entity types
      ERP_DOCUMENT: `/api/documents`,

      // LMS module entity types
      LMS_MODULE: `/api/lms/modules`,
      LESSON: `/api/lms/lessons`,
      LMS_TOPIC: `/api/lms/topics`,
      LMS_CONTENT: `/api/lms/contents`,
      LMS_QUIZ: `/api/lms/quizzes`,
      LMS_QUESTION: `/api/lms/questions`,
      LMS_ANSWER: `/api/lms/answers`,
      LMS_SUBMISSION: `/api/lms/submissions`,
      LMS_STUDENT_PROGRESS: `/api/lms/progress`,
      VIRTUAL_SESSION: `/api/lms/virtual-sessions`,
      LMS_QUESTION_BANK: `/api/lms/question-banks`,
      LMS_RUBRIC: `/api/lms/rubrics`,
      LMS_BADGE: `/api/lms/badges`,
      LMS_POINT_LOG: `/api/lms/point-logs`,
      LMS_FORUM: `/api/lms/forums`,
      LMS_FORUM_POST: `/api/lms/forum-posts`,
      LMS_PEER_REVIEW: `/api/lms/peer-reviews`,

      // Facility/Maintenance module entity types
      FACILITY: `/api/maintenance/facilities`,
      FACILITY_BOOKING: `/api/maintenance/bookings`,
      WORK_ORDER: `/api/maintenance/work-orders`,

      // User/System module entity types
      USER: `/api/users`,
      ORGANIZATION: `/api/organizations`,
      ORGANIZATION_SETTINGS: `/api/organizations/settings`,
      NOTIFICATION_LOG: `/api/notifications/logs`,
      NOTIFICATION_TEMPLATE: `/api/notifications/templates`,
      TICKET_COMMENT: `/api/tickets/comments`,

      // Health module entity types
      HEALTH_RECORD: `/api/health/records`,
      STUDENT_GUARDIAN: `/api/students/guardians`,
      STUDENT_MEDICAL: `/api/students/medical`
    };

    // Return endpoint or throw error if not configured
    const endpoint = endpoints[this.entityType];
    if (!endpoint) {
      console.warn(`No API endpoint configured for entity type: ${this.entityType}`);
    }
    return endpoint || `/api/${this.entityType.toLowerCase()}`;
  }

  /**
   * Check if field is an auto-number field (uiType 120)
   */
  isAutoNumberField(field: FieldDefinition): boolean {
    return field.uiType === 120 || field.isAutoNumber === true;
  }

  /**
   * Get the auto-number preview for a field
   */
  getAutoNumberPreview(field: FieldDefinition): string {
    if (field.autoNumberPreview) {
      return field.autoNumberPreview;
    }
    if (this.autoNumberPreviews[field.fieldName]) {
      return this.autoNumberPreviews[field.fieldName];
    }
    return 'Auto-generated';
  }

  /**
   * Group fields by section (excluding image fields which are rendered separately)
   */
  groupFieldsBySection(): void {
    const sectionsMap = new Map<string, FieldDefinition[]>();

    // Filter out image fields - they are rendered in the image upload section at the top
    const nonImageFields = this.fieldDefinitions.filter(f => !this.isImageField(f));

    nonImageFields.forEach(field => {
      const section = field.section || 'General Information';
      if (!sectionsMap.has(section)) {
        sectionsMap.set(section, []);
      }
      sectionsMap.get(section)!.push(field);
    });

    this.sectionsWithFields = Array.from(sectionsMap.entries()).map(([sectionName, fields]) => ({
      sectionName,
      fields
    }));
  }

  /**
   * Build reactive form based on field definitions
   */
  buildForm(): void {
    const formControls: { [key: string]: any } = {};

    this.fieldDefinitions.forEach(field => {
      const validators = [];

      if (field.isRequired) {
        validators.push(Validators.required);
      }

      if (field.maxLength) {
        validators.push(Validators.maxLength(field.maxLength));
      }

      const defaultValue = this.getDefaultValue(field);
      formControls[field.fieldName] = [
        { value: defaultValue, disabled: field.isReadonly },
        validators
      ];
    });

    this.entityForm = this.fb.group(formControls);
  }

  /**
   * Get default value for a field
   */
  getDefaultValue(field: FieldDefinition): any {
    if (field.defaultValue !== undefined && field.defaultValue !== null) {
      return field.defaultValue;
    }

    switch (field.uiType) {
      case 100: // Single Line Text
      case 102: // Email
      case 103: // Phone
      case 119: // URL
      case 101: // Multi Line Text
        return '';
      case 104: // Picklist
      case 105: // Multi-Select Picklist
        return null;
      case 106: // Date
      case 107: // Date/Time
        return null;
      case 114: // Checkbox
        return false;
      case 108: // Number
      case 110: // Currency
      case 111: // Decimal
      case 112: // Percent
      case 113: // Long Integer
      case 121: // Slider
        return null;
      case 120: // Auto Number (read-only, auto-generated)
        return '';
      case 115: // Lookup
        return null;
      case 118: // Image Upload
        return null;
      default:
        return null;
    }
  }

  /**
   * Get input type for a field
   */
  getInputType(field: FieldDefinition): string {
    switch (field.uiType) {
      case 100: // Single Line Text
        return 'text';
      case 102: // Email
        return 'email';
      case 103: // Phone
        return 'tel';
      case 106: // Date
        return 'date';
      case 107: // Date/Time
        return 'datetime-local';
      case 119: // URL
        return 'url';
      case 108: // Number
      case 110: // Currency
      case 111: // Decimal
      case 112: // Percent
      case 113: // Long Integer
        return 'number';
      case 120: // Auto Number
        return 'text';
      case 121: // Slider
        return 'range';
      default:
        return 'text';
    }
  }

  /**
   * Check if field should be rendered as input
   */
  /**
   * Check if field should be rendered as input
   */
  isInputField(field: FieldDefinition): boolean {
    return [100, 102, 103, 108, 110, 111, 112, 113, 119].includes(Number(field.uiType)); // Single Line, Email, Phone, Number, Currency, Decimal, Percent, Long Integer, URL
  }

  /**
   * Check if field should be rendered as slider
   */
  isSliderField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 121; // Slider
  }

  /**
   * Check if field should be rendered as textarea
   */
  isTextareaField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 101; // Multi Line Text
  }

  /**
   * Check if field should be rendered as select
   */
  isSelectField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 104; // Picklist
  }

  /**
   * Check if field should be rendered as checkbox
   */
  isCheckboxField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 114; // Checkbox
  }

  /**
   * Check if field should be rendered as date picker
   */
  isDateField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 106; // Date
  }

  /**
   * Check if field should be rendered as datetime picker
   */
  isDateTimeField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 107; // Date/Time
  }

  /**
   * Check if field should be rendered as image upload
   */
  isImageField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 118; // Image Upload
  }

  /**
   * Check if field should be rendered as file upload
   */
  isFileField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 117; // File Upload
  }

  /**
   * Check if any field in the form is an image field
   */
  hasImageField(): boolean {
    return this.fieldDefinitions.some(f => this.isImageField(f));
  }

  /**
   * Get the image field definition
   */
  getImageField(): FieldDefinition | undefined {
    return this.fieldDefinitions.find(f => this.isImageField(f));
  }

  /**
   * Check if field should be rendered as lookup
   */
  isLookupField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 115; // Lookup
  }

  /**
   * Check if field should be rendered as multi-select
   */
  isMultiSelectField(field: FieldDefinition): boolean {
    return Number(field.uiType) === 105; // Multi-Select Picklist
  }

  /**
   * Handle image selection
   */
  onImageSelect(event: any, fieldName: string): void {
    const file = event.target.files?.[0];
    if (file && file.type.startsWith('image/')) {
      this.selectedImageFile = file;

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = e.target.result;
      };
      reader.readAsDataURL(file);

      // Update form value
      this.entityForm.patchValue({
        [fieldName]: file.name
      });
    }
  }

  /**
   * Trigger image upload click
   */
  triggerImageUpload(): void {
    const imageField = this.getImageField();
    const fieldName = imageField?.fieldName || 'photoUrl';

    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.onchange = (e: any) => {
      this.onImageSelect(e, fieldName);
    };
    input.click();
  }

  /**
   * Handle file selection (non-image files)
   */
  onFileSelect(event: any, fieldName: string): void {
    const file = event.target.files?.[0];
    if (file) {
      // Update form value with file name
      this.entityForm.patchValue({
        [fieldName]: file.name
      });
    }
  }

  /**
   * Save the entity
   */
  onSave(): void {
    if (this.entityForm.invalid) {
      this.error = 'Please fill in all required fields';
      this.markFormGroupTouched(this.entityForm);
      return;
    }

    this.saving = true;
    this.error = null;

    const formData = this.prepareFormDataForSubmission();

    // Use PUT for edit mode, POST for create mode
    // Use proper API endpoint based on entity type
    const baseEndpoint = this.getBaseApiEndpoint();
    const request = this.isEditMode
      ? this.http.put(this.getApiEndpoint(), formData)
      : this.http.post(baseEndpoint, formData);

    request.subscribe({
      next: (response: any) => {
        this.success = true;
        this.saving = false;

        // Navigate to detail page or list page
        setTimeout(() => {
          const entityId = this.isEditMode ? this.entityId : response.id;
          if (entityId) {
            this.router.navigate(['/entity-detail', this.entityType, entityId]);
          } else {
            this.router.navigate([`/${this.entityType}`]);
          }
        }, 1000);
      },
      error: (err) => {
        console.error('Error saving entity:', err);
        // Extract error message from various possible response formats
        let errorMessage = 'Failed to save entity';
        if (err.error) {
          if (typeof err.error === 'string') {
            errorMessage = err.error;
          } else if (err.error.error) {
            errorMessage = err.error.error;
          } else if (err.error.message) {
            errorMessage = err.error.message;
          } else if (err.error.errors && Array.isArray(err.error.errors)) {
            errorMessage = err.error.errors.map((e: any) => e.defaultMessage || e.message || e).join(', ');
          }
        } else if (err.message) {
          errorMessage = err.message;
        }
        this.error = errorMessage;
        this.saving = false;
        // Scroll to top to show error
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    });
  }

  /**
   * Prepare form data for API submission
   * Converts enum/picklist values to uppercase for backend compatibility
   * For edit mode, merges original entity data with form changes
   */
  private prepareFormDataForSubmission(): any {
    const formData = this.entityForm.getRawValue();

    // For edit mode, merge original entity data with form changes
    // This preserves fields that aren't in the form (like id, studentId, etc.)
    let mergedData = this.isEditMode && this.originalEntityData
      ? { ...this.originalEntityData, ...formData }
      : formData;

    // Find fields with ENUM type (uiType 104) or picklist
    for (const field of this.fieldDefinitions) {
      const fieldName = field.fieldName;
      const value = mergedData[fieldName];

      if (value && typeof value === 'string') {
        // Convert enum values to uppercase with underscores for spaces
        if (field.uiType === 104 || field.dataType === 'ENUM' || field.picklistValues?.length) {
          mergedData[fieldName] = value.toUpperCase().replace(/ /g, '_');
        }
      }
    }

    // Handle special fields that are objects in the backend but strings in the form
    // Remove address if it's a string (backend expects Address object)
    if (mergedData.address && typeof mergedData.address === 'string') {
      delete mergedData.address;
    }

    // Remove computed/transient fields that shouldn't be sent to backend
    delete mergedData.fullName;
    delete mergedData.fullAddress;
    delete mergedData.age;

    return mergedData;
  }

  /**
   * Save and create new
   */
  onSaveAndNew(): void {
    if (this.entityForm.invalid) {
      this.error = 'Please fill in all required fields';
      this.markFormGroupTouched(this.entityForm);
      return;
    }

    this.saving = true;
    this.error = null;

    const formData = this.prepareFormDataForSubmission();
    const baseEndpoint = this.getBaseApiEndpoint();

    this.http.post(baseEndpoint, formData).subscribe({
      next: () => {
        this.success = true;
        this.saving = false;

        // Reset form for new entry
        this.entityForm.reset();
        this.imagePreview = null;
        this.selectedImageFile = null;

        // Clear success message after a short delay
        setTimeout(() => {
          this.success = false;
        }, 2000);
      },
      error: (err) => {
        console.error('Error saving entity:', err);
        // Extract error message from various possible response formats
        let errorMessage = 'Failed to save entity';
        if (err.error) {
          if (typeof err.error === 'string') {
            errorMessage = err.error;
          } else if (err.error.error) {
            errorMessage = err.error.error;
          } else if (err.error.message) {
            errorMessage = err.error.message;
          } else if (err.error.errors && Array.isArray(err.error.errors)) {
            errorMessage = err.error.errors.map((e: any) => e.defaultMessage || e.message || e).join(', ');
          }
        } else if (err.message) {
          errorMessage = err.message;
        }
        this.error = errorMessage;
        this.saving = false;
        // Scroll to top to show error
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    });
  }

  /**
   * Cancel and navigate back
   */
  onCancel(): void {
    // Check if form has been modified
    if (this.entityForm.dirty) {
      this.showCancelModal = true;
    } else {
      this.navigateBack();
    }
  }

  /**
   * Confirm cancel and navigate back
   */
  confirmCancel(): void {
    this.showCancelModal = false;
    this.navigateBack();
  }

  /**
   * Close cancel modal without navigating
   */
  closeCancelModal(): void {
    this.showCancelModal = false;
  }

  /**
   * Navigate back to entity list
   */
  private navigateBack(): void {
    this.router.navigate([`/${this.entityType}`]);
  }

  /**
   * Mark all form controls as touched
   */
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Check if a field has an error
   */
  hasError(fieldName: string): boolean {
    const control = this.entityForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }

  /**
   * Get error message for a field
   */
  getErrorMessage(fieldName: string): string {
    const control = this.entityForm.get(fieldName);
    if (control?.hasError('required')) {
      return 'This field is required';
    }
    if (control?.hasError('email')) {
      return 'Please enter a valid email';
    }
    if (control?.hasError('maxlength')) {
      return `Maximum length exceeded`;
    }
    return '';
  }

  /**
   * Open lookup modal for a field
   */
  openLookupModal(field: FieldDefinition): void {
    this.currentLookupField = field;
    this.lookupFieldLabel = field.displayLabel;
    this.lookupEntityType = this.getLookupEntityType(field.fieldName);
    this.showLookupModal = true;
  }

  /**
   * Close lookup modal
   */
  closeLookupModal(): void {
    this.showLookupModal = false;
    this.currentLookupField = null;
    this.lookupFieldLabel = '';
    this.lookupEntityType = '';
  }

  /**
   * Handle lookup record selection
   */
  onLookupRecordSelected(record: any): void {
    if (this.currentLookupField) {
      // Update form with selected record ID
      this.entityForm.patchValue({
        [this.currentLookupField.fieldName]: record.id
      });

      // Store display name for showing in the field
      this.lookupDisplayNames[this.currentLookupField.fieldName] = record.displayName;
    }

    this.closeLookupModal();
  }

  /**
   * Get lookup entity type based on field name
   */
  private getLookupEntityType(fieldName: string): string {
    // Map field names to entity types
    const fieldToEntityMap: { [key: string]: string } = {
      'department': 'DEPARTMENT',
      'departmentId': 'DEPARTMENT',
      'student': 'STUDENT',
      'studentId': 'STUDENT',
      'staff': 'STAFF',
      'staffId': 'STAFF',
      'classId': 'CLASS',
      'class': 'CLASS',
      'subject': 'SUBJECT',
      'subjectId': 'SUBJECT',
      'course': 'COURSE',
      'courseId': 'COURSE',
      'feeType': 'FEE_TYPE',
      'feeTypeId': 'FEE_TYPE',
      'vendor': 'VENDOR',
      'vendorId': 'VENDOR',
      'asset': 'ASSET',
      'assetId': 'ASSET',
      'classTeacher': 'STAFF',
      'classTeacherId': 'STAFF',
      // Add more mappings as needed
    };

    return fieldToEntityMap[fieldName] || fieldName.toUpperCase();
  }

  /**
   * Get display name for a lookup field value
   */
  getLookupDisplayName(fieldName: string): string {
    return this.lookupDisplayNames[fieldName] || '';
  }
}
