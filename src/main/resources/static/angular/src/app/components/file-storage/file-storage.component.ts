import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

interface ModuleStorage {
  module: string;
  attachmentCount: number;
  storageUsed: string;
}

@Component({
  selector: 'app-file-storage',
  standalone: true,
  imports: [CommonModule, RouterModule, SettingsSidebarComponent],
  templateUrl: './file-storage.component.html',
  styleUrls: ['./file-storage.component.css']
})
export class FileStorageComponent implements OnInit {
  activeTab: 'file' | 'record' = 'file';
  activeView: 'module' | 'user' = 'module';

  // Storage statistics
  totalStorage = '10.00 GB';
  fileAttachments = '935.00 Bytes';
  documents = '0 Bytes';
  templates = '0 Bytes';
  available = '10.00 GB';

  // Storage breakdown
  defaultStorage = '3.00 GB';
  complimentaryStorage = '7.00 GB';
  complimentaryDetails = '1 GB per user (7 x 1 GB)';
  purchasedStorage = '0 Bytes';

  // Storage usage percentage
  fileAttachmentsPercentage = 9.35;
  documentsPercentage = 0;
  templatesPercentage = 0;

  // Module storage data
  moduleStorageData: ModuleStorage[] = [
    { module: 'Interviews', attachmentCount: 2, storageUsed: '935.00 Bytes' },
    { module: 'Candidates', attachmentCount: 0, storageUsed: '0 Bytes' },
    { module: 'Clients', attachmentCount: 0, storageUsed: '0 Bytes' },
    { module: 'Contacts', attachmentCount: 0, storageUsed: '0 Bytes' },
    { module: 'Job Openings', attachmentCount: 0, storageUsed: '0 Bytes' },
    { module: 'Notes', attachmentCount: 0, storageUsed: '0 Bytes' },
    { module: 'To-Dos', attachmentCount: 0, storageUsed: '0 Bytes' },
    { module: 'Emails', attachmentCount: 0, storageUsed: '0 Bytes' },
    { module: 'Vendors', attachmentCount: 0, storageUsed: '0 Bytes' },
    { module: 'Applications', attachmentCount: 0, storageUsed: '0 Bytes' }
  ];

  filteredModuleData: ModuleStorage[] = [];
  searchTerm = '';

  constructor() { }

  ngOnInit(): void {
    this.filteredModuleData = [...this.moduleStorageData];
  }

  setActiveTab(tab: 'file' | 'record'): void {
    this.activeTab = tab;
  }

  setActiveView(view: 'module' | 'user'): void {
    this.activeView = view;
  }

  onSearch(event: any): void {
    this.searchTerm = event.target.value.toLowerCase();
    this.filteredModuleData = this.moduleStorageData.filter(item =>
      item.module.toLowerCase().includes(this.searchTerm)
    );
  }

  navigateToRecordStorage(): void {
    // Will be implemented with routing
  }
}
