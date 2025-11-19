import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly DEFAULT_THEME = '#0099cc';
  private currentTheme$ = new BehaviorSubject<string>(this.DEFAULT_THEME);
  private savingTheme = false;

  constructor(private http: HttpClient) {
    this.initializeTheme();
  }

  /**
   * Initialize theme on service creation
   */
  private initializeTheme(): void {
    const savedTheme = localStorage.getItem('selectedTheme');
    if (savedTheme) {
      this.currentTheme$.next(savedTheme);
      this.applyTheme(savedTheme);
    } else {
      this.applyTheme(this.DEFAULT_THEME);
    }
  }

  /**
   * Get current theme as Observable
   */
  getTheme$(): Observable<string> {
    return this.currentTheme$.asObservable();
  }

  /**
   * Get current theme value synchronously
   */
  getTheme(): string {
    return this.currentTheme$.value;
  }

  /**
   * Set theme and save to database (if user context available)
   */
  setTheme(theme: string, userId?: number, organizationId?: number): void {
    if (!theme) return;

    // Update local state
    this.currentTheme$.next(theme);
    localStorage.setItem('selectedTheme', theme);
    this.applyTheme(theme);

    // Persist to database if user context is available
    if (userId && organizationId) {
      this.saveThemeToDatabase(theme, userId, organizationId);
    }
  }

  /**
   * Load theme from database for a specific user
   */
  loadThemeFromDatabase(userId: number, organizationId: number): void {
    this.http.get(`/api/user-settings/${userId}/${organizationId}`)
      .subscribe({
        next: (settings: any) => {
          if (settings.theme) {
            // Update from database preference
            const dbTheme = settings.theme;
            this.currentTheme$.next(dbTheme);
            localStorage.setItem('selectedTheme', dbTheme);
            this.applyTheme(dbTheme);
          }
        },
        error: (err) => {
          console.warn('Could not load theme from database:', err);
          // Continue with localStorage value
        }
      });
  }

  /**
   * Save theme to database
   */
  private saveThemeToDatabase(theme: string, userId: number, organizationId: number): void {
    if (this.savingTheme) {
      return;
    }

    this.savingTheme = true;

    this.http.put(
      `/api/user-settings/${userId}/${organizationId}`,
      { 
        theme: theme,
        themePrimaryColor: theme
      }
    ).subscribe({
      next: () => {
        console.log('Theme saved to database:', theme);
        this.savingTheme = false;
      },
      error: (err) => {
        console.error('Failed to save theme to database:', err);
        this.savingTheme = false;
        // Theme is still applied locally via localStorage fallback
      }
    });
  }

  /**
   * Apply theme to document using CSS variables
   */
  private applyTheme(color: string): void {
    // Set the main CSS variables - all components reference these
    document.documentElement.style.setProperty('--app-primary', color);
    document.documentElement.style.setProperty('--primary-color', color);
    document.documentElement.style.setProperty('--app-primary-dark', this.darkenColor(color));
    
    // Update navbar background if it exists
    const navbar = document.querySelector('nav.navbar');
    if (navbar) {
      (navbar as HTMLElement).style.backgroundColor = color;
    }
  }

  /**
   * Darken a color by a percentage
   */
  private darkenColor(color: string, percent: number = 20): string {
    const num = parseInt(color.replace('#', ''), 16);
    const amt = Math.round(2.55 * percent);
    const R = Math.max(0, (num >> 16) - amt);
    const G = Math.max(0, (num >> 8 & 0x00FF) - amt);
    const B = Math.max(0, (num & 0x0000FF) - amt);
    return '#' + (0x1000000 + (R < 255 ? R : 255) * 0x10000 +
      (G < 255 ? G : 255) * 0x100 +
      (B < 255 ? B : 255)).toString(16).slice(1);
  }

  /**
   * Reset to default theme
   */
  resetTheme(): void {
    this.setTheme(this.DEFAULT_THEME);
    localStorage.removeItem('selectedTheme');
  }

  /**
   * Get available themes
   */
  getAvailableThemes(): { name: string; color: string }[] {
    return [
      { name: 'Dark Red', color: '#660000' },
      { name: 'Red', color: '#990000' },
      { name: 'Red Light', color: '#D24143' },
      { name: 'Red Salmon', color: '#DE4F5D' },
      { name: 'Pink', color: '#ea4c88' },
      { name: 'Purple', color: '#993399' },
      { name: 'Purple Dark', color: '#663399' },
      { name: 'Navy', color: '#07385D' },
      { name: 'Blue Dark', color: '#1e5598' },
      { name: 'Blue', color: '#2d72d9' },
      { name: 'Blue Light', color: '#018EE0' },
      { name: 'Cyan', color: '#0099cc' },
      { name: 'Teal', color: '#37a5a5' },
      { name: 'Green', color: '#439454' },
      { name: 'Green Dark', color: '#336600' },
      { name: 'Green Teal', color: '#165151' },
      { name: 'Olive', color: '#999900' },
      { name: 'Orange', color: '#E9A23F' },
      { name: 'Orange Dark', color: '#E77817' },
      { name: 'Brown', color: '#996633' },
      { name: 'Mauve', color: '#553A48' },
      { name: 'Gray', color: '#313949' }
    ];
  }
}
