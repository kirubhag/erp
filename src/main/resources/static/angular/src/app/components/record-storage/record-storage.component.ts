import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

interface ModuleRecord {
  module: string;
  recordCount: number;
}

@Component({
  selector: 'app-record-storage',
  standalone: true,
  imports: [CommonModule, RouterModule, SettingsSidebarComponent, HttpClientModule],
  templateUrl: './record-storage.component.html',
  styleUrls: ['./record-storage.component.css']
})
export class RecordStorageComponent implements OnInit {
  activeTab: 'file' | 'record' = 'record';

  // Storage statistics
  recordsUsed = 669;
  isUnlimited = true;
  lastUpdated = new Date();

  // Module record data
  moduleRecordData: ModuleRecord[] = [];

  filteredModuleData: ModuleRecord[] = [];
  searchTerm = '';

  constructor(private http: import('@angular/common/http').HttpClient) { }

  ngOnInit(): void {
    this.loadRecordData();
  }

  loadRecordData() {
    this.http.get<ModuleRecord[]>('/api/storage/records').subscribe({
      next: (data) => {
        this.moduleRecordData = data;
        this.filteredModuleData = [...this.moduleRecordData];
        this.recordsUsed = this.moduleRecordData.reduce((sum, item) => sum + item.recordCount, 0);
        this.lastUpdated = new Date();
      },
      error: (err) => {
        console.error('Failed to load storage records', err);
        // Fallback or empty state could be handled here
      }
    });
  }

  setActiveTab(tab: 'file' | 'record'): void {
    this.activeTab = tab;
  }

  onSearch(event: any): void {
    this.searchTerm = event.target.value.toLowerCase();
    this.filteredModuleData = this.moduleRecordData.filter(item =>
      item.module.toLowerCase().includes(this.searchTerm)
    );
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString('en-US', {
      month: '2-digit',
      day: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  }
}
