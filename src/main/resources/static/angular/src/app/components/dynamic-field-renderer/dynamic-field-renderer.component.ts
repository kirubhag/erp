import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ErpField } from '../../models/erp-field.model';
import { UIFieldTypeId } from '../../models/ui-field-type.model';
import { UIFieldTypeService } from '../../services/ui-field-type.service';
import { MultiSelectFieldComponent } from '../multi-select-field/multi-select-field.component';
import { RichTextFieldComponent } from '../rich-text-field/rich-text-field.component';
import { FileUploadFieldComponent } from '../file-upload-field/file-upload-field.component';
import { ImageUploadFieldComponent } from '../image-upload-field/image-upload-field.component';
import { LookupFieldComponent } from '../lookup-field/lookup-field.component';

/**
 * Dynamic Field Renderer Component
 * Renders form fields dynamically based on UI field type configuration
 * 
 * Usage:
 * <app-dynamic-field-renderer
 *   [field]="field"
 *   [value]="value"
 *   [mode]="'edit'"
 *   (valueChange)="onValueChange($event)">
 * </app-dynamic-field-renderer>
 */
@Component({
    selector: 'app-dynamic-field-renderer',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MultiSelectFieldComponent,
        RichTextFieldComponent,
        FileUploadFieldComponent,
        ImageUploadFieldComponent,
        LookupFieldComponent
    ],
    templateUrl: './dynamic-field-renderer.component.html',
    styleUrls: ['./dynamic-field-renderer.component.css']
})
export class DynamicFieldRendererComponent implements OnInit {
    @Input() field!: ErpField;
    @Input() value: any;
    @Input() mode: 'create' | 'edit' | 'view' = 'edit';
    @Input() disabled: boolean = false;

    @Output() valueChange = new EventEmitter<any>();

    // Expose UIFieldTypeId enum to template
    UIFieldTypeId = UIFieldTypeId;

    // Validation state
    validationErrors: string[] = [];
    isValid: boolean = true;

    // Picklist options (parsed from field configuration)
    picklistOptions: string[] = [];

    constructor(private uiFieldTypeService: UIFieldTypeService) { }

    ngOnInit(): void {
        // Parse picklist options if field has them
        if (this.field.picklistOptions) {
            this.picklistOptions = this.field.picklistOptions.split(',').map(opt => opt.trim());
        }

        // Validate initial value if in edit mode
        if (this.mode !== 'view' && this.value !== null && this.value !== undefined) {
            this.validateValue();
        }
    }

    /**
     * Handle value change from input
     */
    onValueChanged(newValue: any): void {
        this.value = newValue;
        this.valueChange.emit(newValue);

        // Validate the new value
        if (this.mode !== 'view') {
            this.validateValue();
        }
    }

    /**
     * Validate the current value
     */
    validateValue(): void {
        const result = this.uiFieldTypeService.validateFieldValueLocal(this.field, this.value);
        this.isValid = result.isValid;
        this.validationErrors = result.errors;
    }

    /**
     * Get formatted display value for view mode
     */
    getDisplayValue(): string {
        return this.uiFieldTypeService.formatValueForDisplay(this.value, this.field.uiType);
    }

    /**
     * Check if field should be rendered as textarea
     */
    isTextArea(): boolean {
        return this.field.uiType === UIFieldTypeId.MULTI_LINE_TEXT;
    }

    /**
     * Check if field should be rendered as select/dropdown
     */
    isSelect(): boolean {
        return this.field.uiType === UIFieldTypeId.PICKLIST;
    }

    /**
     * Check if field should be rendered as checkbox
     */
    isCheckbox(): boolean {
        return this.field.uiType === UIFieldTypeId.CHECKBOX;
    }

    /**
     * Check if field should be rendered as date input
     */
    isDate(): boolean {
        return this.field.uiType === UIFieldTypeId.DATE;
    }

    /**
     * Check if field should be rendered as datetime input
     */
    isDateTime(): boolean {
        return this.field.uiType === UIFieldTypeId.DATETIME;
    }

    /**
     * Check if field should be rendered as number input
     */
    isNumber(): boolean {
        return this.field.uiType === UIFieldTypeId.NUMBER ||
            this.field.uiType === UIFieldTypeId.LONG_INTEGER ||
            this.field.uiType === UIFieldTypeId.CURRENCY ||
            this.field.uiType === UIFieldTypeId.DECIMAL ||
            this.field.uiType === UIFieldTypeId.PERCENT;
    }

    /**
     * Get HTML input type for the field
     */
    getInputType(): string {
        if (!this.field.uiType) {
            return 'text';
        }

        const typeMap: { [key: number]: string } = {
            [UIFieldTypeId.SINGLE_LINE_TEXT]: 'text',
            [UIFieldTypeId.EMAIL]: 'email',
            [UIFieldTypeId.PHONE]: 'tel',
            [UIFieldTypeId.URL]: 'url',
            [UIFieldTypeId.NUMBER]: 'number',
            [UIFieldTypeId.LONG_INTEGER]: 'number',
            [UIFieldTypeId.CURRENCY]: 'number',
            [UIFieldTypeId.DECIMAL]: 'number',
            [UIFieldTypeId.PERCENT]: 'number',
            [UIFieldTypeId.DATE]: 'date',
            [UIFieldTypeId.DATETIME]: 'datetime-local'
        };

        return typeMap[this.field.uiType] || 'text';
    }

    /**
     * Get step value for number inputs
     */
    getNumberStep(): string {
        if (this.field.uiType === UIFieldTypeId.DECIMAL ||
            this.field.uiType === UIFieldTypeId.CURRENCY) {
            return '0.01';
        }
        if (this.field.uiType === UIFieldTypeId.PERCENT) {
            return '0.1';
        }
        return '1';
    }

    /**
     * Get min/max values for number inputs
     */
    getNumberMin(): number | undefined {
        if (this.field.uiType === UIFieldTypeId.PERCENT) {
            return 0;
        }
        return undefined;
    }

    getNumberMax(): number | undefined {
        if (this.field.uiType === UIFieldTypeId.PERCENT) {
            return 100;
        }
        return undefined;
    }
}
