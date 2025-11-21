import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

interface Role {
  id: number;
  name: string;
  description: string;
  users: number;
  isSystemRole: boolean;
  permissions: string[];
  children?: Role[];
  isExpanded?: boolean;
}

@Component({
  selector: 'app-roles-sharing',
  standalone: true,
  imports: [CommonModule, FormsModule, SettingsSidebarComponent],
  templateUrl: './roles-sharing.component.html',
  styles: [`
    .roles-sharing-page { display: flex; min-height: 100vh; }
    .roles-sidebar-wrapper { width: 280px; background-color: white; border-right: 1px solid var(--app-border, #E5E7EB); overflow-y: auto; overflow-x: hidden; flex-shrink: 0; }
    .settings-content-container { flex: 1; overflow-y: auto; overflow-x: hidden; padding-top: 60px; }
    .roles-sharing-wrapper { min-height: 100vh; background-color: #f5f5f5; }
    .tabs-section { background: white; border-bottom: 1px solid var(--app-border, #E5E7EB); }
    .tabs { max-width: 1400px; margin: 0 auto; padding: 0 30px; display: flex; gap: 30px; }
    .tab-button { padding: 15px 0; background: none; border: none; color: #6c757d; cursor: pointer; font-size: 1rem; font-weight: 500; border-bottom: 3px solid transparent; transition: all 0.3s ease; }
    .tab-button.active { color: var(--app-primary, #0891B2); border-bottom-color: var(--app-primary, #0891B2); }
    .content-section { max-width: 1400px; margin: 0 auto; padding: 30px; }
    .roles-main { flex: 1; padding: 30px; width: 100%; }
    .description-section { margin-bottom: 30px; }
    .description { display: flex; align-items: center; padding: 12px; background-color: var(--app-primary-light, #CFFAFE); border-left: 4px solid var(--app-primary, #0891B2); border-radius: 6px; color: #155E75; margin-bottom: 20px; }
    .action-buttons { display: flex; gap: 12px; flex-wrap: wrap; }
    .btn { padding: 10px 20px; border-radius: 6px; border: none; cursor: pointer; font-weight: 500; transition: all 0.3s ease; }
    .btn-primary { background-color: var(--app-primary, #0891B2); color: white; }
    .btn-primary:hover { background-color: var(--app-primary-dark, #0369A1); transform: translateY(-2px); box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15); }
    .btn-link { background: none; color: var(--app-primary, #0891B2); border: none; padding: 10px 0; text-decoration: none; }
    .btn-link:hover { text-decoration: underline; color: var(--app-primary-dark, #0369A1); }
    .btn-sm { padding: 6px 12px; font-size: 0.85rem; }
    .btn-danger { background-color: var(--app-danger, #EF4444); color: white; }
    .btn-danger:hover { background-color: #DC2626; }
    .btn-outline-secondary { background-color: white; color: #333; border: 1px solid #ddd; }
    .btn-outline-secondary:hover { background-color: #f0f9fc; border-color: var(--app-primary, #0891B2); color: var(--app-primary, #0891B2); }
    .role-hierarchy { margin-top: 20px; }
    .tree-container { font-family: 'Courier New', monospace; font-size: 0.95rem; }
    .tree-node { display: flex; align-items: flex-start; gap: 8px; padding: 8px 0; margin-left: 0; }
    .tree-node.level-1 { margin-left: 20px; }
    .tree-node.level-2 { margin-left: 40px; }
    .tree-node.level-3 { margin-left: 60px; }
    .tree-node.level-4 { margin-left: 80px; }
    .tree-connector { display: flex; flex-direction: column; width: 20px; align-items: center; color: #ccc; flex-shrink: 0; }
    .tree-connector-line { width: 1px; height: 24px; background-color: #ccc; }
    .tree-connector-branch { display: flex; align-items: center; height: 20px; }
    .tree-connector-branch::before { content: ''; width: 12px; height: 1px; background-color: #ccc; }
    .tree-content { flex: 1; }
    .tree-item { display: flex; align-items: center; gap: 10px; padding: 10px 12px; background-color: white; border: 1px solid var(--app-border, #E5E7EB); border-radius: 6px; cursor: pointer; transition: all 0.2s ease; }
    .tree-item:hover { background-color: #f0f9fc; border-color: var(--app-primary, #0891B2); }
    .tree-item.selected { background-color: var(--app-primary-light, #CFFAFE); border-color: var(--app-primary, #0891B2); }
    .tree-item.parent { font-weight: 600; }
    .expand-btn { background: none; border: none; padding: 0; cursor: pointer; color: var(--app-primary, #0891B2); width: 20px; height: 20px; display: flex; align-items: center; justify-content: center; font-size: 0.85rem; }
    .expand-placeholder { width: 20px; }
    .tree-icon { color: var(--app-primary, #0891B2); font-size: 0.95rem; }
    .role-name { color: #333; font-weight: 500; }
    .role-badge { background-color: var(--app-danger, #EF4444); color: white; padding: 2px 8px; border-radius: 4px; font-size: 0.7rem; font-weight: 600; margin-left: 8px; }
    .user-count { color: #6c757d; font-size: 0.85rem; margin-left: auto; padding-left: 10px; white-space: nowrap; }
    .details-panel { margin-top: 20px; padding: 20px; background-color: white; border: 1px solid var(--app-border, #E5E7EB); border-radius: 8px; }
    .details-panel h4 { margin: 0 0 12px 0; color: #333; }
    .details-panel p { margin: 8px 0; color: #6c757d; }
    .role-actions { display: flex; gap: 10px; margin-top: 15px; }
    .empty-state { text-align: center; padding: 60px 20px; color: #6c757d; }
    .empty-state i { font-size: 3rem; color: #ddd; margin-bottom: 20px; }
    .empty-state h3 { font-size: 1.3rem; font-weight: 600; margin-bottom: 10px; }
    .modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 1000; }
    .modal { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; border-radius: 12px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2); z-index: 1001; max-width: 500px; width: 90%; }
    .modal-header { padding: 20px; border-bottom: 1px solid var(--app-border, #E5E7EB); display: flex; justify-content: space-between; align-items: center; }
    .modal-header h2 { margin: 0; font-size: 1.3rem; color: #333; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #6c757d; transition: color 0.3s ease; }
    .close-btn:hover { color: var(--app-primary, #0891B2); }
    .modal-body { padding: 20px; }
    .form-group { margin-bottom: 20px; }
    .form-group label { display: block; font-weight: 500; margin-bottom: 8px; color: #333; }
    .required { color: var(--app-danger, #EF4444); }
    .form-control { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 0.95rem; font-family: inherit; transition: all 0.3s ease; }
    .form-control:focus { outline: none; border-color: var(--app-primary, #0891B2); box-shadow: 0 0 0 3px rgba(8, 145, 178, 0.1); }
    .modal-footer { padding: 20px; border-top: 1px solid var(--app-border, #E5E7EB); display: flex; gap: 12px; justify-content: flex-end; }
    @media (max-width: 768px) {
      .roles-main { padding: 15px; }
      .tree-node.level-1 { margin-left: 10px; }
      .tree-node.level-2 { margin-left: 20px; }
      .tree-node.level-3 { margin-left: 30px; }
      .btn { font-size: 0.9rem; padding: 8px 16px; }
      .modal { width: 95%; }
    }
  `]
})
export class RolesSharingComponent implements OnInit {
  roles: Role[] = [
    {
      id: 1,
      name: 'CEO',
      description: 'Chief Executive Officer - Full system access',
      users: 1,
      isSystemRole: true,
      permissions: ['READ', 'WRITE', 'DELETE', 'ADMIN'],
      isExpanded: true,
      children: [
        {
          id: 2,
          name: 'Manager',
          description: 'Department Manager',
          users: 3,
          isSystemRole: false,
          permissions: ['READ', 'WRITE', 'APPROVE'],
          isExpanded: false
        }
      ]
    },
    {
      id: 3,
      name: 'Principal',
      description: 'School Principal',
      users: 1,
      isSystemRole: true,
      permissions: ['READ', 'WRITE', 'APPROVE', 'ADMIN'],
      isExpanded: false,
      children: [
        {
          id: 4,
          name: 'Teacher',
          description: 'Teaching Staff',
          users: 12,
          isSystemRole: false,
          permissions: ['READ', 'WRITE'],
          isExpanded: false
        },
        {
          id: 5,
          name: 'Staff',
          description: 'Support Staff',
          users: 5,
          isSystemRole: false,
          permissions: ['READ'],
          isExpanded: false
        }
      ]
    },
    {
      id: 6,
      name: 'Student',
      description: 'Student Role',
      users: 250,
      isSystemRole: false,
      permissions: ['READ'],
      isExpanded: false
    }
  ];

  selectedRole: Role | null = null;
  showNewRoleForm = false;
  newRoleName = '';
  newRoleDescription = '';
  searchTerm = '';
  filteredRoles: Role[] = [];
  expandedRoleIds: Set<number> = new Set();

  ngOnInit(): void {
    this.filteredRoles = [...this.roles];
  }

  expandAll(): void {
    this.expandedRoleIds.clear();
    this.roles.forEach(role => this.expandAllRecursive(role));
  }

  collapseAll(): void {
    this.expandedRoleIds.clear();
  }

  private expandAllRecursive(role: Role): void {
    this.expandedRoleIds.add(role.id);
    if (role.children) {
      role.children.forEach(child => this.expandAllRecursive(child));
    }
  }

  toggleRole(role: Role): void {
    if (this.expandedRoleIds.has(role.id)) {
      this.expandedRoleIds.delete(role.id);
    } else {
      this.expandedRoleIds.add(role.id);
    }
  }

  isRoleExpanded(roleId: number): boolean {
    return this.expandedRoleIds.has(roleId);
  }

  selectRole(role: Role): void {
    this.selectedRole = role;
  }

  searchRoles(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchTerm = target.value.toLowerCase();
    this.filterRoles();
  }

  filterRoles(): void {
    if (!this.searchTerm) {
      this.filteredRoles = [...this.roles];
    } else {
      this.filteredRoles = this.filterRolesRecursive(this.roles);
    }
  }

  private filterRolesRecursive(roles: Role[]): Role[] {
    return roles
      .filter(role => 
        role.name.toLowerCase().includes(this.searchTerm) ||
        role.description.toLowerCase().includes(this.searchTerm)
      )
      .map(role => ({
        ...role,
        children: role.children ? this.filterRolesRecursive(role.children) : []
      }));
  }

  openNewRoleForm(): void {
    this.showNewRoleForm = true;
    this.newRoleName = '';
    this.newRoleDescription = '';
  }

  closeNewRoleForm(): void {
    this.showNewRoleForm = false;
  }

  createNewRole(): void {
    if (!this.newRoleName.trim()) {
      alert('Please enter a role name');
      return;
    }

    const newRole: Role = {
      id: Math.max(...this.roles.map(r => r.id)) + 1,
      name: this.newRoleName,
      description: this.newRoleDescription,
      users: 0,
      isSystemRole: false,
      permissions: [],
      isExpanded: false
    };

    this.roles.push(newRole);
    this.filteredRoles = [...this.roles];
    this.closeNewRoleForm();
    alert(`Role "${newRole.name}" created successfully!`);
  }

  deleteRole(role: Role): void {
    if (role.isSystemRole) {
      alert('System roles cannot be deleted.');
      return;
    }

    if (confirm(`Are you sure you want to delete the "${role.name}" role?`)) {
      const index = this.roles.indexOf(role);
      if (index > -1) {
        this.roles.splice(index, 1);
      }
      this.filteredRoles = [...this.roles];
      this.selectedRole = null;
      alert(`Role "${role.name}" deleted successfully!`);
    }
  }

  editRole(role: Role): void {
    const newName = prompt('Enter new role name:', role.name);
    if (newName && newName.trim()) {
      role.name = newName.trim();
    }
  }

  getChildRoles(role: Role): Role[] {
    return role.children || [];
  }

  getHierarchyLevel(role: Role): string {
    // This would be calculated based on role's position in hierarchy
    return '■';
  }

  /**
   * Calculate total users including children (recursive count)
   */
  getTotalUserCount(role: Role): number {
    let count = role.users;
    if (role.children && role.children.length > 0) {
      count += role.children.reduce((sum, child) => sum + this.getTotalUserCount(child), 0);
    }
    return count;
  }

  /**
   * Get the depth level of a role in the hierarchy
   */
  getLevel(role: Role, allRoles: Role[] = this.roles, currentLevel: number = 0): number {
    // Find if this role is in the current level array
    if (allRoles.some(r => r.id === role.id)) {
      return currentLevel;
    }
    
    // Search in children
    for (let r of allRoles) {
      if (r.children) {
        const level = this.getLevel(role, r.children, currentLevel + 1);
        if (level !== -1) {
          return level;
        }
      }
    }
    return -1;
  }

  /**
   * Get CSS class for tree node based on level
   */
  getLevelClass(role: Role): string {
    const level = this.getLevel(role);
    return `level-${Math.max(1, level + 1)}`;
  }

  /**
   * Check if a role is a root role (no parent)
   */
  isRootRole(role: Role): boolean {
    return this.roles.some(r => r.id === role.id);
  }

  /**
   * Render all roles in tree structure recursively
   */
  getAllRolesInTree(roles: Role[] = this.filteredRoles): Role[] {
    const result: Role[] = [];
    const traverse = (roleList: Role[]) => {
      for (let role of roleList) {
        result.push(role);
        if (this.isRoleExpanded(role.id) && role.children) {
          traverse(role.children);
        }
      }
    };
    traverse(roles);
    return result;
  }
}
