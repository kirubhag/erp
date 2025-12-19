import { Routes } from '@angular/router';

export const INVENTORY_ROUTES: Routes = [
    {
        path: 'assets',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'ASSET' }
    },
    {
        path: 'consumables',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'CONSUMABLE' }
    },
    {
        path: 'vendors',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'VENDOR' }
    },
    {
        path: 'purchase-orders',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'PURCHASE_ORDER' }
    }
];
