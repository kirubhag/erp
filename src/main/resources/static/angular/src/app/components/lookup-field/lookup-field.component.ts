import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { Subject } from 'rxjs';
import { ErpField } from '../../models/erp-field.model';

/**
 * Lookup/Autocomplete Component (ui_type 115)
 * Provides searchable lookup with autocomplete functionality
 */
@Component({
    selector: 'app-lookup-field',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
        <div class="lookup-container">
            <label [for]="field.fieldName" class="field-label">
                {{ field.fieldLabel }}
                <span *ngIf="field.isRequired" class="required">*</span>
            </label>
            
            <!-- View Mode -->
            <div *ngIf="mode === 'view'" class="lookup-display">
                {{ displayValue || '—' }}
            </div>
            
            <!-- Edit/Create Mode -->
            <div *ngIf="mode !== 'view'" class="lookup-input">
                <div class="input-wrapper">
                    <input type="text"
                           [id]="field.fieldName"
                           [(ngModel)]="searchTerm"
                           (input)="onSearchChange()"
                           (focus)="onFocus()"
                           (blur)="onBlur()"
                           [disabled]="disabled"
                           class="form-control"
                           [placeholder]="'Search ' + field.fieldLabel"
                           autocomplete="off">
                    <i class="fas fa-search search-icon"></i>
                    <i *ngIf="selectedValue && !disabled" 
                       class="fas fa-times clear-icon" 
                       (click)="clearSelection()"></i>
                </div>
                
                <!-- Dropdown Results -->
                <div *ngIf="showDropdown && (results.length > 0 || searching)" class="dropdown-results">
                    <div *ngIf="searching" class="loading">
                        <div class="spinner-border spinner-border-sm"></div>
                        <span>Searching...</span>
                    </div>
                    
                    <div *ngIf="!searching && results.length > 0" class="results-list">
                        <div *ngFor="let result of results" 
                             class="result-item"
                             (mousedown)="selectResult(result)"
                             [class.selected]="result.id === selectedValue">
                            <div class="result-primary">{{ result.displayText }}</div>
                            <div *ngIf="result.secondaryText" class="result-secondary">
                                {{ result.secondaryText }}
                            </div>
                        </div>
                    </div>
                    
                    <div *ngIf="!searching && results.length === 0 && searchTerm" class="no-results">
                        No results found for "{{ searchTerm }}"
                    </div>
                </div>
                
                <small *ngIf="field.fieldDescription" class="help-text">
                    {{ field.fieldDescription }}
                </small>
            </div>
        </div>
    `,
    styles: [`
        .lookup-container {
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
        
        .lookup-input {
            position: relative;
        }
        
        .input-wrapper {
            position: relative;
        }
        
        .form-control {
            width: 100%;
            padding: 0.5rem 2.5rem 0.5rem 2.5rem;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
        }
        
        .form-control:focus {
            border-color: #80bdff;
            outline: 0;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
        }
        
        .search-icon {
            position: absolute;
            left: 0.75rem;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
        }
        
        .clear-icon {
            position: absolute;
            right: 0.75rem;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
            cursor: pointer;
        }
        
        .clear-icon:hover {
            color: #495057;
        }
        
        .dropdown-results {
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
            overflow-y: auto;
        }
        
        .loading {
            padding: 1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            justify-content: center;
            color: #6c757d;
        }
        
        .results-list {
            padding: 0.25rem 0;
        }
        
        .result-item {
            padding: 0.75rem 1rem;
            cursor: pointer;
            border-bottom: 1px solid #f8f9fa;
        }
        
        .result-item:last-child {
            border-bottom: none;
        }
        
        .result-item:hover {
            background: #f8f9fa;
        }
        
        .result-item.selected {
            background: #e7f3ff;
        }
        
        .result-primary {
            font-weight: 500;
            color: #212529;
        }
        
        .result-secondary {
            font-size: 0.875rem;
            color: #6c757d;
            margin-top: 0.25rem;
        }
        
        .no-results {
            padding: 1rem;
            text-align: center;
            color: #6c757d;
        }
        
        .lookup-display {
            padding: 0.5rem 0;
            color: #212529;
        }
        
        .help-text {
            display: block;
            margin-top: 0.25rem;
            color: #6c757d;
            font-size: 0.875rem;
        }
    `]
})
export class LookupFieldComponent implements OnInit {
    @Input() field!: ErpField;
    @Input() value: any;
    @Input() mode: 'create' | 'edit' | 'view' = 'edit';
    @Input() disabled: boolean = false;
    @Input() lookupEntity: string = ''; // e.g., 'STUDENT', 'STAFF'
    @Input() displayField: string = 'name'; // Field to display
    @Input() secondaryField?: string; // Optional secondary field

    @Output() valueChange = new EventEmitter<any>();

    searchTerm: string = '';
    displayValue: string = '';
    selectedValue: any = null;
    results: any[] = [];
    showDropdown: boolean = false;
    searching: boolean = false;

    private searchSubject = new Subject<string>();

    constructor(private http: HttpClient) { }

    ngOnInit(): void {
        // Setup search debouncing
        this.searchSubject.pipe(
            debounceTime(300),
            distinctUntilChanged(),
            switchMap(term => this.searchLookup(term))
        ).subscribe(results => {
            this.results = results as any[];
            this.searching = false;
        });

        // Load initial value if exists
        if (this.value) {
            this.selectedValue = this.value;
            this.loadDisplayValue(this.value);
        }
    }

    onSearchChange(): void {
        if (this.searchTerm.length >= 2) {
            this.searching = true;
            this.showDropdown = true;
            this.searchSubject.next(this.searchTerm);
        } else {
            this.results = [];
            this.showDropdown = false;
        }
    }

    onFocus(): void {
        if (this.searchTerm.length >= 2) {
            this.showDropdown = true;
        }
    }

    onBlur(): void {
        // Delay to allow click on dropdown item
        setTimeout(() => {
            this.showDropdown = false;
        }, 200);
    }

    selectResult(result: any): void {
        this.selectedValue = result.id;
        this.displayValue = result.displayText;
        this.searchTerm = result.displayText;
        this.showDropdown = false;
        this.valueChange.emit(result.id);
    }

    clearSelection(): void {
        this.selectedValue = null;
        this.displayValue = '';
        this.searchTerm = '';
        this.results = [];
        this.valueChange.emit(null);
    }

    private searchLookup(term: string): any {
        if (!this.lookupEntity || !term) {
            return of([]);
        }

        // TODO: Replace with actual API call
        // Example: return this.http.get(`/api/${this.lookupEntity.toLowerCase()}/search?q=${term}`);

        // Mock data for demonstration
        const mockResults = [
            { id: 1, displayText: `${term} Result 1`, secondaryText: 'Additional info 1' },
            { id: 2, displayText: `${term} Result 2`, secondaryText: 'Additional info 2' },
            { id: 3, displayText: `${term} Result 3`, secondaryText: 'Additional info 3' }
        ];

        return of(mockResults);
    }

    private loadDisplayValue(id: any): void {
        if (!this.lookupEntity || !id) return;

        // TODO: Replace with actual API call to get display value
        // this.http.get(`/api/${this.lookupEntity.toLowerCase()}/${id}`).subscribe(
        //   (data: any) => {
        //     this.displayValue = data[this.displayField];
        //     this.searchTerm = this.displayValue;
        //   }
        // );

        // Mock for demonstration
        this.displayValue = `Item ${id}`;
        this.searchTerm = this.displayValue;
    }
}
