import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { LoadingService } from './services/loading.service';

import { SidebarComponent } from './components/layout/sidebar/sidebar.component';
import { LayoutService } from './services/layout.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, SidebarComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'erp-frontend';
  showNavbar = false;
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private loadingService: LoadingService,
    public layoutService: LayoutService
  ) { }

  ngOnInit(): void {
    // Subscribe to user state changes
    this.authService.currentUser$.subscribe(user => {
      this.updateNavbarVisibility();
    });

    // Subscribe to loading state from HTTP requests
    this.loadingService.loading$.subscribe(loading => {
      this.isLoading = loading;
    });

    // Listen to route changes for navbar visibility
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateNavbarVisibility();
      }
    });
  }

  private updateNavbarVisibility(): void {
    // Hide navbar on home, login, and register pages regardless of user state
    const hiddenRoutes = ['/', '/login', '/register'];
    const currentUser = this.authService.getCurrentUser();
    const currentUrl = this.router.url;

    // Show navbar only if:
    // 1. User is logged in (has currentUser with id)
    // 2. Current URL is NOT a hidden route
    this.showNavbar = !!currentUser && !!currentUser.id && !hiddenRoutes.includes(currentUrl);
  }
}

