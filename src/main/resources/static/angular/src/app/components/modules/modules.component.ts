import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

export interface Module {
  id: number;
  name: string;
  displayName: string;
  description: string;
  icon: string;
  isCustom: boolean;
  createdDate: string;
  lastModified: string;
  status: 'active' | 'inactive';
}

@Component({
  selector: 'app-modules',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SettingsSidebarComponent],
  templateUrl: './modules.component.html',
  styleUrls: ['./modules.component.css']
})
export class ModulesComponent implements OnInit {
  searchTerm = '';
  modules: Module[] = [];
  activeTab = 'modules';

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadModules();
  }

  loadModules() {
    this.modules = [
      {
        id: 1,
        name: 'Candidates',
        displayName: 'Candidates',
        description: 'Manage candidate profiles and applications',
        icon: 'fas fa-user-tie',
        isCustom: false,
        createdDate: '2024-01-10',
        lastModified: '2024-11-01',
        status: 'active'
      },
      {
        id: 2,
        name: 'Jobs',
        displayName: 'Job Openings',
        description: 'Create and manage job postings',
        icon: 'fas fa-briefcase',
        isCustom: false,
        createdDate: '2024-01-10',
        lastModified: '2024-10-25',
        status: 'active'
      },
      {
        id: 3,
        name: 'Interviews',
        displayName: 'Interviews',
        description: 'Schedule and track interviews',
        icon: 'fas fa-video',
        isCustom: false,
        createdDate: '2024-02-15',
        lastModified: '2024-11-02',
        status: 'active'
      },
      {
        id: 4,
        name: 'Contacts',
        displayName: 'Contacts',
        description: 'Manage contact information',
        icon: 'fas fa-address-book',
        isCustom: false,
        createdDate: '2024-01-10',
        lastModified: '2024-10-20',
        status: 'active'
      },
      {
        id: 5,
        name: 'CustomModule',
        displayName: 'Custom Module',
        description: 'User-created custom module for tracking',
        icon: 'fas fa-puzzle-piece',
        isCustom: true,
        createdDate: '2024-06-20',
        lastModified: '2024-11-03',
        status: 'active'
      },
      {
        id: 6,
        name: 'Reports',
        displayName: 'Reports',
        description: 'Generate and view reports',
        icon: 'fas fa-chart-bar',
        isCustom: false,
        createdDate: '2024-03-01',
        lastModified: '2024-10-30',
        status: 'active'
      }
    ];
  }

  editModule(module: Module) {
    this.router.navigate(['/setup/module-builder', module.id]);
  }

  createModule() {
    this.router.navigate(['/setup/module-builder', 'new']);
  }

  deleteModule(module: Module) {
    if (confirm(`Are you sure you want to delete "${module.displayName}" module?`)) {
      this.modules = this.modules.filter(m => m.id !== module.id);
    }
  }

  toggleModuleStatus(module: Module) {
    module.status = module.status === 'active' ? 'inactive' : 'active';
  }

  get filteredModules(): Module[] {
    if (!this.searchTerm) {
      return this.modules;
    }
    return this.modules.filter(module =>
      module.displayName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      module.description.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }
}
