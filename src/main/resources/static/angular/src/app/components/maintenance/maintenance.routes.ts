import { Routes } from '@angular/router';

export const MAINTENANCE_ROUTES: Routes = [
    {
        path: 'work-orders',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'WORK_ORDER' }
    },
    {
        path: 'facilities',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'FACILITY' }
    },
    {
        path: 'bookings',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'FACILITY_BOOKING' }
    }
];
