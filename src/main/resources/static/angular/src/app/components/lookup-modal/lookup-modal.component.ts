import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

export interface LookupRecord {
  id: number;
  displayName: string;
  secondaryInfo?: string;
}

@Component({
  selector: 'app-lookup-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lookup-modal.component.html',
  styleUrls: ['./lookup-modal.component.css']
})
export class LookupModalComponent implements OnInit {
  @Input() fieldLabel: string = 'Lookup';
  @Input() lookupEntity: string = ''; // Entity type to lookup (e.g., 'DEPARTMENT', 'STUDENT')
  @Input() displayField: string = 'name'; // Field to display from records
  @Input() secondaryField?: string; // Optional secondary field to display
  @Output() recordSelected = new EventEmitter<LookupRecord>();
  @Output() modalClosed = new EventEmitter<void>();

  records: LookupRecord[] = [];
  filteredRecords: LookupRecord[] = [];
  loading = false;
  error: string | null = null;
  searchTerm: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadLookupRecords();
  }

  /**
   * Load lookup records from API
   */
  loadLookupRecords(): void {
    if (!this.lookupEntity) {
      this.error = 'No lookup entity specified';
      return;
    }

    this.loading = true;
    this.error = null;

    // Map entity type to API endpoint
    const endpoint = this.getLookupEndpoint(this.lookupEntity);
    if (!endpoint) {
      this.error = `No API endpoint configured for lookup entity: ${this.lookupEntity}`;
      this.loading = false;
      return;
    }

    this.http.get<any[]>(endpoint).subscribe({
      next: (data) => {
        this.records = data.map(record => this.mapToLookupRecord(record));
        this.filteredRecords = [...this.records];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading lookup records:', err);
        this.error = 'Failed to load lookup records';
        this.loading = false;
      }
    });
  }

  /**
   * Get API endpoint for lookup entity type
   */
  private getLookupEndpoint(entityType: string): string {
    const endpoints: { [key: string]: string } = {
      'DEPARTMENT': '/api/departments',
      'STUDENT': '/api/students',
      'STAFF': '/api/staff',
      'CLASS': '/api/classes',
      'SUBJECT': '/api/subjects',
      'COURSE': '/api/courses',
      'FEE_TYPE': '/api/finance/fee-types',
      'ASSET': '/api/inventory/assets',
      'VENDOR': '/api/inventory/vendors',
      // Add more mappings as needed
    };
    return endpoints[entityType.toUpperCase()] || '';
  }

  /**
   * Map API record to LookupRecord interface
   */
  private mapToLookupRecord(record: any): LookupRecord {
    let displayName = record[this.displayField] || record.name || record.title || `Record ${record.id}`;
    let secondaryInfo: string | undefined = undefined;

    if (this.secondaryField && record[this.secondaryField]) {
      secondaryInfo = record[this.secondaryField];
    }

    return {
      id: record.id,
      displayName: displayName,
      secondaryInfo: secondaryInfo
    };
  }

  /**
   * Filter records based on search term
   */
  filterRecords(): void {
    if (!this.searchTerm.trim()) {
      this.filteredRecords = [...this.records];
      return;
    }

    const searchLower = this.searchTerm.toLowerCase();
    this.filteredRecords = this.records.filter(record =>
      record.displayName.toLowerCase().includes(searchLower) ||
      (record.secondaryInfo && record.secondaryInfo.toLowerCase().includes(searchLower))
    );
  }

  /**
   * Handle record selection
   */
  selectRecord(record: LookupRecord): void {
    this.recordSelected.emit(record);
  }

  /**
   * Close modal
   */
  close(): void {
    this.modalClosed.emit();
  }
}
