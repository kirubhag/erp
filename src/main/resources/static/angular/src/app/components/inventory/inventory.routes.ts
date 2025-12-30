import { Routes } from '@angular/router';

export const INVENTORY_ROUTES: Routes = [
    {
        path: 'assets',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'consumables',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'vendors',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'purchase-orders',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    }
];
