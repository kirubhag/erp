import { Routes } from '@angular/router';

export const REPORTING_ROUTES: Routes = [
    {
        path: 'reports',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'MIS_REPORT' }
    }
];
