import { Routes } from '@angular/router';

export const HR_ROUTES: Routes = [
    {
        path: 'dashboard',
        loadComponent: () => import('./hr-dashboard/hr-dashboard.component').then(m => m.HrDashboardComponent)
    },
    {
        path: 'self-service',
        loadComponent: () => import('./employee-portal/employee-portal.component').then(m => m.EmployeePortalComponent)
    },
    {
        path: 'job-postings',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'job-applications',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'payroll-runs',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'payroll-runs/:id/payslips',
        loadComponent: () => import('./payroll/payslip-view/payslip-view.component').then(m => m.PayslipViewComponent)
    },
    {
        path: 'salaries',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'leave-types',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'leaves',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'leave-balances',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'performance-cycles',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'performance-criteria',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: 'performance-reviews',
        loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent)
    },
    {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
    }
];
