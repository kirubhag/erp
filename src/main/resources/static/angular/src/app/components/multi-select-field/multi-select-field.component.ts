import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ErpField } from '../../models/erp-field.model';

/**
 * Multi-Select Picklist Component (ui_type 105)
 * Allows selection of multiple options from a list
 */
@Component({
    selector: 'app-multi-select-field',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
        <div class="multi-select-container">
            <label [for]="field.fieldName" class="field-label">
                {{ field.fieldLabel }}
                <span *ngIf="field.isRequired" class="required">*</span>
            </label>
            
            <!-- View Mode -->
            <div *ngIf="mode === 'view'" class="selected-values-display">
                <span *ngIf="selectedValues.length === 0" class="no-selection">—</span>
                <div *ngIf="selectedValues.length > 0" class="chips-container">
                    <span *ngFor="let value of selectedValues" class="chip">
                        {{ value }}
                    </span>
                </div>
            </div>
            
            <!-- Edit/Create Mode -->
            <div *ngIf="mode !== 'view'" class="multi-select-input">
                <div class="dropdown-container">
                    <button type="button" 
                            class="dropdown-toggle" 
                            (click)="toggleDropdown()"
                            [disabled]="disabled">
                        <span *ngIf="selectedValues.length === 0">Select {{ field.fieldLabel }}</span>
                        <span *ngIf="selectedValues.length > 0">{{ selectedValues.length }} selected</span>
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    
                    <div *ngIf="isOpen" class="dropdown-menu">
                        <div class="search-box">
                            <input type="text" 
                                   [(ngModel)]="searchTerm" 
                                   placeholder="Search..."
                                   class="form-control form-control-sm">
                        </div>
                        
                        <div class="options-list">
                            <label *ngFor="let option of filteredOptions" class="option-item">
                                <input type="checkbox"
                                       [checked]="isSelected(option)"
                                       (change)="toggleOption(option)"
                                       [disabled]="disabled">
                                <span>{{ option }}</span>
                            </label>
                            <div *ngIf="filteredOptions.length === 0" class="no-options">
                                No options found
                            </div>
                        </div>
                        
                        <div class="dropdown-footer">
                            <button type="button" class="btn-sm" (click)="selectAll()">Select All</button>
                            <button type="button" class="btn-sm" (click)="clearAll()">Clear All</button>
                        </div>
                    </div>
                </div>
                
                <!-- Selected chips -->
                <div *ngIf="selectedValues.length > 0" class="selected-chips">
                    <span *ngFor="let value of selectedValues" class="chip">
                        {{ value }}
                        <i class="fas fa-times" (click)="removeValue(value)"></i>
                    </span>
                </div>
            </div>
            
            <small *ngIf="field.fieldDescription" class="help-text">
                {{ field.fieldDescription }}
            </small>
        </div>
    `,
    styles: [`
        .multi-select-container {
            margin-bottom: 1rem;
        }
        
        .field-label {
            display: block;
            font-weight: 500;
            margin-bottom: 0.5rem;
        }
        
        .required {
            color: #dc3545;
        }
        
        .dropdown-container {
            position: relative;
        }
        
        .dropdown-toggle {
            width: 100%;
            padding: 0.5rem 0.75rem;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            background: white;
            text-align: left;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .dropdown-toggle:disabled {
            background: #e9ecef;
            cursor: not-allowed;
        }
        
        .dropdown-menu {
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            background: white;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            margin-top: 0.25rem;
            box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.15);
            z-index: 1000;
            max-height: 300px;
            display: flex;
            flex-direction: column;
        }
        
        .search-box {
            padding: 0.5rem;
            border-bottom: 1px solid #dee2e6;
        }
        
        .options-list {
            overflow-y: auto;
            max-height: 200px;
            padding: 0.25rem 0;
        }
        
        .option-item {
            display: flex;
            align-items: center;
            padding: 0.5rem 0.75rem;
            cursor: pointer;
            margin: 0;
        }
        
        .option-item:hover {
            background: #f8f9fa;
        }
        
        .option-item input {
            margin-right: 0.5rem;
        }
        
        .dropdown-footer {
            border-top: 1px solid #dee2e6;
            padding: 0.5rem;
            display: flex;
            gap: 0.5rem;
        }
        
        .btn-sm {
            padding: 0.25rem 0.5rem;
            font-size: 0.875rem;
            border: 1px solid #ced4da;
            background: white;
            border-radius: 0.25rem;
            cursor: pointer;
        }
        
        .btn-sm:hover {
            background: #f8f9fa;
        }
        
        .selected-chips, .chips-container {
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
            margin-top: 0.5rem;
        }
        
        .chip {
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            padding: 0.25rem 0.5rem;
            background: #007bff;
            color: white;
            border-radius: 1rem;
            font-size: 0.875rem;
        }
        
        .chip i {
            cursor: pointer;
            font-size: 0.75rem;
        }
        
        .chip i:hover {
            opacity: 0.8;
        }
        
        .no-selection {
            color: #6c757d;
        }
        
        .no-options {
            padding: 1rem;
            text-align: center;
            color: #6c757d;
        }
        
        .help-text {
            display: block;
            margin-top: 0.25rem;
            color: #6c757d;
            font-size: 0.875rem;
        }
    `]
})
export class MultiSelectFieldComponent implements OnInit {
    @Input() field!: ErpField;
    @Input() value: string[] | string = [];
    @Input() mode: 'create' | 'edit' | 'view' = 'edit';
    @Input() disabled: boolean = false;

    @Output() valueChange = new EventEmitter<string[]>();

    selectedValues: string[] = [];
    options: string[] = [];
    filteredOptions: string[] = [];
    isOpen: boolean = false;
    searchTerm: string = '';

    ngOnInit(): void {
        // Parse options from field configuration
        if (this.field.picklistOptions) {
            this.options = this.field.picklistOptions.split(',').map(opt => opt.trim());
            this.filteredOptions = [...this.options];
        }

        // Parse initial value
        if (this.value) {
            if (Array.isArray(this.value)) {
                this.selectedValues = [...this.value];
            } else if (typeof this.value === 'string') {
                this.selectedValues = this.value.split(',').map(v => v.trim()).filter(v => v);
            }
        }
    }

    ngOnChanges(): void {
        this.filterOptions();
    }

    toggleDropdown(): void {
        this.isOpen = !this.isOpen;
        if (this.isOpen) {
            this.searchTerm = '';
            this.filterOptions();
        }
    }

    filterOptions(): void {
        if (!this.searchTerm) {
            this.filteredOptions = [...this.options];
        } else {
            const term = this.searchTerm.toLowerCase();
            this.filteredOptions = this.options.filter(opt =>
                opt.toLowerCase().includes(term)
            );
        }
    }

    isSelected(option: string): boolean {
        return this.selectedValues.includes(option);
    }

    toggleOption(option: string): void {
        const index = this.selectedValues.indexOf(option);
        if (index > -1) {
            this.selectedValues.splice(index, 1);
        } else {
            this.selectedValues.push(option);
        }
        this.emitChange();
    }

    removeValue(value: string): void {
        const index = this.selectedValues.indexOf(value);
        if (index > -1) {
            this.selectedValues.splice(index, 1);
            this.emitChange();
        }
    }

    selectAll(): void {
        this.selectedValues = [...this.filteredOptions];
        this.emitChange();
    }

    clearAll(): void {
        this.selectedValues = [];
        this.emitChange();
    }

    private emitChange(): void {
        this.valueChange.emit([...this.selectedValues]);
    }
}
