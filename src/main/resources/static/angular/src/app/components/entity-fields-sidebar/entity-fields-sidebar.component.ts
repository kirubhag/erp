import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

export interface EntityField {
  id: number;
  fieldName: string;
  fieldLabel: string;
  fieldType: string;
  isRequired: boolean;
  isActive: number;
  displayOrder: number;
  isSearchable?: boolean;
  isSortable?: boolean;
  // fieldCategory removed - no longer returned by API (deprecated in favor of sections)
}

export interface FieldFilter {
  fieldName: string;
  fieldLabel: string;
  selected: boolean;
}

@Component({
  selector: 'app-entity-fields-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './entity-fields-sidebar.component.html',
  styleUrls: ['./entity-fields-sidebar.component.css']
})
export class EntityFieldsSidebarComponent implements OnInit, OnChanges {
  @Input() entityType: string = '';
  @Input() visible: boolean = true;
  @Input() fields: EntityField[] = []; // Accept fields from parent to avoid duplicate API calls
  @Output() fieldFilterChange = new EventEmitter<FieldFilter[]>();
  
  internalFields: EntityField[] = [];
  fieldFilters: FieldFilter[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    // If fields are provided from parent, use them; otherwise load them
    if (this.fields && this.fields.length > 0) {
      this.initializeFieldFilters(this.fields);
    } else if (this.entityType) {
      this.loadEntityFields();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    // If fields input changed and has values, use them
    if (changes['fields'] && changes['fields'].currentValue && changes['fields'].currentValue.length > 0) {
      this.initializeFieldFilters(changes['fields'].currentValue);
    } else if (changes['entityType'] && !changes['entityType'].firstChange && (!this.fields || this.fields.length === 0)) {
      this.loadEntityFields();
    }
  }

  private initializeFieldFilters(fields: EntityField[]): void {
    this.internalFields = fields
      .filter(f => f.isActive === 1)
      .sort((a, b) => a.displayOrder - b.displayOrder);
    
    // Initialize field filters
    this.fieldFilters = this.internalFields.map(field => ({
      fieldName: field.fieldName,
      fieldLabel: field.fieldLabel,
      selected: false
    }));
  }

  loadEntityFields(): void {
    if (!this.entityType) return;

    this.loading = true;
    this.error = '';

    this.http.get<EntityField[]>(`/api/fields/${this.entityType}`)
      .subscribe({
        next: (fields) => {
          this.initializeFieldFilters(fields);
          this.loading = false;
        },
        error: (err) => {
          console.warn(`No field metadata found for entity type: ${this.entityType}. This is normal for entities without custom field definitions.`);
          // Gracefully handle missing field metadata - don't show error to user
          this.internalFields = [];
          this.fieldFilters = [];
          this.loading = false;
          // Don't set error message - this is expected for some entity types
        }
      });
  }

  onFieldFilterChange(fieldName: string): void {
    const filter = this.fieldFilters.find(f => f.fieldName === fieldName);
    if (filter) {
      filter.selected = !filter.selected;
      this.fieldFilterChange.emit(this.fieldFilters.filter(f => f.selected));
    }
  }

  clearAllFilters(): void {
    this.fieldFilters.forEach(f => f.selected = false);
    this.fieldFilterChange.emit([]);
  }

  getSelectedCount(): number {
    return this.fieldFilters.filter(f => f.selected).length;
  }
}
