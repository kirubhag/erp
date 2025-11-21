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
  fieldCategory?: string;
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
  @Output() fieldFilterChange = new EventEmitter<FieldFilter[]>();
  
  fields: EntityField[] = [];
  fieldFilters: FieldFilter[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    if (this.entityType) {
      this.loadEntityFields();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['entityType'] && !changes['entityType'].firstChange) {
      this.loadEntityFields();
    }
  }

  loadEntityFields(): void {
    if (!this.entityType) return;

    this.loading = true;
    this.error = '';

    this.http.get<EntityField[]>(`/api/fields/${this.entityType}`)
      .subscribe({
        next: (fields) => {
          this.fields = fields
            .filter(f => f.isActive === 1)
            .sort((a, b) => a.displayOrder - b.displayOrder);
          
          // Initialize field filters
          this.fieldFilters = this.fields.map(field => ({
            fieldName: field.fieldName,
            fieldLabel: field.fieldLabel,
            selected: false
          }));
          
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading entity fields:', err);
          this.error = 'Failed to load fields';
          this.loading = false;
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
