import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { UIFieldTypeService } from '../../services/ui-field-type.service';

export interface FieldDefinition {
  fieldName: string;
  displayLabel: string;
  uiType: number;
  dataType: string;
  isRequired: boolean;
  isReadonly: boolean;
  defaultValue?: any;
  picklistValues?: string[];
  maxLength?: number;
  minValue?: number;
  maxValue?: number;
  decimalPlaces?: number;
  section?: string;
}

export interface SectionFields {
  sectionName: string;
  fields: FieldDefinition[];
}

@Component({
  selector: 'app-entity-create',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './entity-create.component.html',
  styleUrls: ['./entity-create.component.css']
})
export class EntityCreateComponent implements OnInit {
  entityType: string = '';
  entityName: string = '';
  entityNamePlural: string = '';

  fieldDefinitions: FieldDefinition[] = [];
  sectionsWithFields: SectionFields[] = [];

  entityForm: FormGroup = new FormGroup({});
  loading = false;
  saving = false;
  error: string | null = null;
  success = false;

  // Image upload
  imagePreview: string | null = null;
  selectedImageFile: File | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder,
    private uiFieldTypeService: UIFieldTypeService
  ) { }

  ngOnInit(): void {
    // Get entity type from route params or query params
    this.route.paramMap.subscribe(params => {
      this.entityType = params.get('entityType') || '';
    });

    this.route.queryParams.subscribe(params => {
      if (params['entityType']) {
        this.entityType = params['entityType'];
      }
    });

    if (this.entityType) {
      this.loadEntityMetadata();
    }
  }

  /**
   * Load entity metadata including field definitions
   */
  loadEntityMetadata(): void {
    this.loading = true;
    this.error = null;

    this.http.get<any>(`/api/entities/${this.entityType}/metadata`).subscribe({
      next: (metadata) => {
        console.log('Received metadata:', metadata);
        this.entityName = metadata.entityName || this.entityType;
        this.entityNamePlural = metadata.entityNamePlural || this.entityType + 's';
        this.fieldDefinitions = metadata.fields || [];
        console.log('Field definitions:', this.fieldDefinitions);

        // Group fields by section
        this.groupFieldsBySection();
        console.log('Sections with fields:', this.sectionsWithFields);

        // Build form
        this.buildForm();

        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading entity metadata:', err);
        this.error = 'Failed to load entity information';
        this.loading = false;
      }
    });
  }

  /**
   * Group fields by section
   */
  groupFieldsBySection(): void {
    const sectionsMap = new Map<string, FieldDefinition[]>();

    this.fieldDefinitions.forEach(field => {
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
      case 1: // Single Line
      case 2: // Email
      case 3: // Phone
      case 7: // URL
      case 19: // Multi Line
        return '';
      case 4: // Picklist
      case 16: // Multi-Select Picklist
        return null;
      case 5: // Date
      case 6: // Date/Time
        return null;
      case 8: // Checkbox
        return false;
      case 9: // Currency
      case 10: // Decimal
      case 11: // Number
      case 12: // Percent
        return null;
      case 13: // Lookup
      case 14: // User
        return null;
      case 17: // Auto Number
        return null;
      case 18: // Image Upload
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
      case 1: // Single Line
        return 'text';
      case 2: // Email
        return 'email';
      case 3: // Phone
        return 'tel';
      case 5: // Date
        return 'date';
      case 6: // Date/Time
        return 'datetime-local';
      case 7: // URL
        return 'url';
      case 9: // Currency
      case 10: // Decimal
      case 11: // Number
      case 12: // Percent
        return 'number';
      default:
        return 'text';
    }
  }

  /**
   * Check if field should be rendered as input
   */
  isInputField(field: FieldDefinition): boolean {
    return [1, 2, 3, 7, 9, 10, 11, 12].includes(field.uiType); // Single Line, Email, Phone, URL, Currency, Decimal, Number, Percent
  }

  /**
   * Check if field should be rendered as textarea
   */
  isTextareaField(field: FieldDefinition): boolean {
    return field.uiType === 19; // Multi Line Text
  }

  /**
   * Check if field should be rendered as select
   */
  isSelectField(field: FieldDefinition): boolean {
    return field.uiType === 4; // Picklist
  }

  /**
   * Check if field should be rendered as checkbox
   */
  isCheckboxField(field: FieldDefinition): boolean {
    return field.uiType === 8; // Checkbox
  }

  /**
   * Check if field should be rendered as date picker
   */
  isDateField(field: FieldDefinition): boolean {
    return field.uiType === 5; // Date
  }

  /**
   * Check if field should be rendered as datetime picker
   */
  isDateTimeField(field: FieldDefinition): boolean {
    return field.uiType === 6; // Date/Time
  }

  /**
   * Check if field should be rendered as image upload
   */
  isImageField(field: FieldDefinition): boolean {
    return field.uiType === 18; // Image Upload
  }

  /**
   * Check if any field in the form is an image field
   */
  hasImageField(): boolean {
    return this.fieldDefinitions.some(f => this.isImageField(f));
  }

  /**
   * Check if field should be rendered as lookup
   */
  isLookupField(field: FieldDefinition): boolean {
    return field.uiType === 13; // Lookup
  }

  /**
   * Check if field should be rendered as multi-select
   */
  isMultiSelectField(field: FieldDefinition): boolean {
    return field.uiType === 16; // Multi-Select Picklist
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
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.onchange = (e: any) => {
      this.onImageSelect(e, 'image');
    };
    input.click();
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

    const formData = this.entityForm.getRawValue();

    this.http.post(`/api/entities/${this.entityType}`, formData).subscribe({
      next: (response: any) => {
        this.success = true;
        this.saving = false;

        // Navigate to detail page or list page
        setTimeout(() => {
          if (response.id) {
            this.router.navigate(['/entity-detail', this.entityType, response.id]);
          } else {
            this.router.navigate([`/${this.entityType}`]);
          }
        }, 1000);
      },
      error: (err) => {
        console.error('Error saving entity:', err);
        this.error = err.error?.message || 'Failed to save entity';
        this.saving = false;
      }
    });
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

    const formData = this.entityForm.getRawValue();

    this.http.post(`/api/entities/${this.entityType}`, formData).subscribe({
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
        this.error = err.error?.message || 'Failed to save entity';
        this.saving = false;
      }
    });
  }

  /**
   * Cancel and navigate back
   */
  onCancel(): void {
    if (confirm('Are you sure you want to cancel? Any unsaved changes will be lost.')) {
      this.router.navigate([`/${this.entityType}`]);
    }
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
}
