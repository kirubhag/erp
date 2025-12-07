import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  private userSubscription: Subscription | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // Check if user is logged in - but don't auto-redirect from home page
    // Let users explicitly choose to login or register
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      // Get the isLoggedOut flag state from AuthService
      const isLoggingOut = (this.authService as any)['isLoggedOut'];
      
      if (user && user.id && !isLoggingOut) {
        // User is logged in
        this.isLoggedIn = true;
        // Don't auto-redirect - let them navigate manually or use the dashboard link
      } else {
        // User is not logged in or is logging out, stay on home page
        this.isLoggedIn = false;
      }
    });
  }

  ngOnDestroy(): void {
    // Clean up subscription
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  scrollToSection(sectionId: string): void {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }
}
