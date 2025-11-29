# Student Promotion Module - Angular Routing Setup

## Add Routes to app.routes.ts

Add these routes to your Angular routing configuration:

```typescript
import { Routes } from '@angular/router';
import { PromotionListComponent } from './components/promotion-list/promotion-list.component';
import { PromotionCreateComponent } from './components/promotion-create/promotion-create.component';
import { PromotionDetailsComponent } from './components/promotion-details/promotion-details.component';

export const routes: Routes = [
  // ... your existing routes
  
  // Student Promotion Routes
  {
    path: 'promotions',
    component: PromotionListComponent,
    title: 'Student Promotions'
  },
  {
    path: 'promotions/create',
    component: PromotionCreateComponent,
    title: 'Create Promotion'
  },
  {
    path: 'promotions/details/:id',
    component: PromotionDetailsComponent,
    title: 'Promotion Details'
  },
  
  // ... rest of your routes
];
```

## Add Navigation Menu Item

Add this to your main navigation menu (e.g., in app.component.html or your navigation component):

```html
<li class="nav-item">
  <a class="nav-link" routerLink="/promotions" routerLinkActive="active">
    <i class="bi bi-arrow-up-circle"></i> Student Promotions
  </a>
</li>
```

## Ensure Bootstrap Icons

Make sure Bootstrap Icons are included in your index.html:

```html
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
```

## Quick Start

1. **Navigate to Promotion List**: http://localhost:8080/promotions
2. **Create New Promotion**: Click "Create New Promotion" button
3. **View Details**: Click eye icon on any batch

## Component Dependencies

All components are **standalone** with the following imports:
- `CommonModule` (for *ngIf, *ngFor, pipes)
- `FormsModule` (for [(ngModel)] two-way binding)
- `Router` / `ActivatedRoute` (for navigation)

No module declarations needed - components can be used directly in routes!
