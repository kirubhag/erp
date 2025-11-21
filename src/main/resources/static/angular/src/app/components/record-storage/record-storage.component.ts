import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

interface ModuleRecord {
  module: string;
  recordCount: number;
}

@Component({
  selector: 'app-record-storage',
  standalone: true,
  imports: [CommonModule, RouterModule, SettingsSidebarComponent],
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
  moduleRecordData: ModuleRecord[] = [
    { module: 'Candidates', recordCount: 457 },
    { module: 'Emails', recordCount: 110 },
    { module: 'Job Openings', recordCount: 36 },
    { module: 'Contacts', recordCount: 34 },
    { module: 'Onboarding', recordCount: 17 },
    { module: 'Clients', recordCount: 6 },
    { module: 'Assessments', recordCount: 4 },
    { module: 'Attachments', recordCount: 2 },
    { module: 'Interviews', recordCount: 2 },
    { module: 'To-Dos', recordCount: 1 },
    { module: 'Notes', recordCount: 0 },
    { module: 'Vendors', recordCount: 0 }
  ];

  filteredModuleData: ModuleRecord[] = [];
  searchTerm = '';

  constructor() { }

  ngOnInit(): void {
    this.filteredModuleData = [...this.moduleRecordData];
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
