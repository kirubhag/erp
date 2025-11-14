import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'erp-frontend';
  showNavbar = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // Subscribe to user state changes
    this.authService.currentUser$.subscribe(user => {
      this.updateNavbarVisibility();
    });

    // Listen to route changes
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.updateNavbarVisibility();
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

