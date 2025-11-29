import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';
import { MenuService, MenuItem } from '../../services/menu.service';

export interface Module {
  id: number;
  systemName: string;
  pluralName: string;
  singularName: string;
  icon: string;
  route: string;
  sequence: number;
  isActive: boolean;
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
  isLoading = false;
  errorMessage = '';

  constructor(
    private router: Router,
    private menuService: MenuService
  ) { }

  ngOnInit() {
    this.loadModules();
  }

  loadModules() {
    this.isLoading = true;
    this.errorMessage = '';

    this.menuService.getMenuItems().subscribe({
      next: (items: MenuItem[]) => {
        // Transform MenuItem to Module format
        this.modules = items.map(item => ({
          id: item.id,
          systemName: item.systemName,
          pluralName: item.pluralName,
          singularName: item.singularName,
          icon: item.icon,
          route: item.route,
          sequence: item.sequence,
          isActive: item.isActive
        }));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading modules:', error);
        this.errorMessage = 'Failed to load modules. Please try again.';
        this.isLoading = false;
        // Set empty array on error
        this.modules = [];
      }
    });
  }

  editModule(module: Module) {
    this.router.navigate(['/setup/module-builder', module.systemName]);
  }

  createModule() {
    this.router.navigate(['/setup/module-builder', 'new']);
  }

  deleteModule(module: Module) {
    if (confirm(`Are you sure you want to delete "${module.pluralName}" module?`)) {
      this.modules = this.modules.filter(m => m.id !== module.id);
    }
  }

  toggleModuleStatus(module: Module) {
    module.isActive = !module.isActive;
  }

  get filteredModules(): Module[] {
    if (!this.searchTerm) {
      return this.modules;
    }
    return this.modules.filter(module =>
      module.pluralName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      module.singularName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      module.systemName.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }
}
