import { Routes } from '@angular/router';
import { AuthGuard } from '../../guards/auth.guard';

export const ADMISSION_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./admission-dashboard/admission-dashboard.component').then(m => m.AdmissionDashboardComponent),
                canActivate: [AuthGuard]
            },
            {
                path: 'inquiries',
                loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent),
                canActivate: [AuthGuard]
            },
            {
                path: 'applications',
                loadComponent: () => import('../entity-management/entity-management.component').then(m => m.EntityManagementComponent),
                canActivate: [AuthGuard]
            },
            {
                path: 'cycles',
                loadComponent: () => import('./admission-cycle/admission-cycle.component').then(m => m.AdmissionCycleComponent),
                canActivate: [AuthGuard]
            },
            {
                path: 'registrations',
                loadComponent: () => import('./student-registration/student-registration.component').then(m => m.StudentRegistrationComponent),
                canActivate: [AuthGuard]
            }
        ]
    }
];
