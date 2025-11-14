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
    // Subscribe to user state
    this.authService.currentUser$.subscribe(user => {
      this.showNavbar = !!user;
    });

    // Listen to route changes
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        // Hide navbar on home, login, and register pages
        const hiddenRoutes = ['/', '/login', '/register'];
        this.showNavbar = !hiddenRoutes.includes(event.url) && !!this.authService.getCurrentUser();
      });
  }
}
