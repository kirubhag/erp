import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuService } from '../../services/menu.service';

export interface MenuItem {
  id: number;
  name: string;
  pluralName: string;
  singularName: string;
  icon: string;
  route: string;
  sequence: number;
  visible: boolean;
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  menuItems: MenuItem[] = [];
  visibleMenuItems: MenuItem[] = [];
  overflowMenuItems: MenuItem[] = [];
  hasOverflow = false;
  organizationName = 'Zylker';
  currentUser = { name: 'Administrator' };

  constructor(private menuService: MenuService) {}

  ngOnInit() {
    this.loadMenuItems();
  }

  loadMenuItems() {
    this.menuService.getMenuItems().subscribe({
      next: (items) => {
        this.menuItems = items.sort((a, b) => a.sequence - b.sequence);
        this.visibleMenuItems = this.menuItems.filter(item => item.visible);
        // For now, show all items. Later we can add overflow logic
        this.overflowMenuItems = [];
        this.hasOverflow = false;
      },
      error: (error) => {
        console.error('Error loading menu items:', error);
        // Fallback to default menu items
        this.setDefaultMenuItems();
      }
    });
  }

  setDefaultMenuItems() {
    this.menuItems = [
      { id: 1, name: 'dashboard', pluralName: 'Dashboard', singularName: 'Dashboard', icon: 'fas fa-home', route: '/dashboard', sequence: 1, visible: true },
      { id: 2, name: 'students', pluralName: 'Students', singularName: 'Student', icon: 'fas fa-user-graduate', route: '/students', sequence: 2, visible: true },
      { id: 3, name: 'staff', pluralName: 'Staff', singularName: 'Staff', icon: 'fas fa-chalkboard-teacher', route: '/staff', sequence: 3, visible: true },
      { id: 4, name: 'attendance', pluralName: 'Attendance', singularName: 'Attendance', icon: 'fas fa-calendar-check', route: '/attendance', sequence: 4, visible: true },
      { id: 5, name: 'parents', pluralName: 'Parents', singularName: 'Parent', icon: 'fas fa-users', route: '/parents', sequence: 5, visible: true },
      { id: 6, name: 'subjects', pluralName: 'Subjects', singularName: 'Subject', icon: 'fas fa-book', route: '/subjects', sequence: 6, visible: true }
    ];
    this.visibleMenuItems = this.menuItems;
  }

  isMenuItemActive(menuItem: MenuItem): boolean {
    // TODO: Implement active menu detection based on current route
    return false;
  }

  logout() {
    // TODO: Implement logout functionality
    console.log('Logout clicked');
  }
}
