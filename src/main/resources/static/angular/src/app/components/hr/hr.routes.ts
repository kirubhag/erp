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
        loadComponent: () => import('./recruitment/job-posting-list/job-posting-list.component').then(m => m.JobPostingListComponent)
    },
    {
        path: 'job-applications',
        loadComponent: () => import('./recruitment/applicant-tracking/applicant-tracking.component').then(m => m.ApplicantTrackingComponent)
    },
    {
        path: 'payroll-runs',
        loadComponent: () => import('./payroll/payroll-dashboard/payroll-dashboard.component').then(m => m.PayrollDashboardComponent)
    },
    {
        path: 'payroll-runs/:id/payslips',
        loadComponent: () => import('./payroll/payslip-view/payslip-view.component').then(m => m.PayslipViewComponent)
    },
    {
        path: 'leave-types',
        loadComponent: () => import('./leave/leave-types/leave-type-list.component').then(m => m.LeaveTypeListComponent)
    },
    {
        path: 'leaves',
        loadComponent: () => import('./leave/leave-requests/leave-request-list.component').then(m => m.LeaveRequestListComponent)
    },
    {
        path: 'performance-cycles',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'PERFORMANCE_CYCLE' }
    },
    {
        path: 'performance-criteria',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'PERFORMANCE_CRITERIA' }
    },
    {
        path: 'performance-reviews',
        loadComponent: () => import('../entity-list/entity-list.component').then(m => m.EntityListComponent),
        data: { entityName: 'PERFORMANCE_REVIEW' }
    },
    {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
    }
];
