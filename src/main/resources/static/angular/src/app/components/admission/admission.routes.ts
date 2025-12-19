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
                loadComponent: () => import('./inquiry-list/inquiry-list.component').then(m => m.InquiryListComponent),
                canActivate: [AuthGuard]
            },
            {
                path: 'applications',
                loadComponent: () => import('./application-list/application-list.component').then(m => m.ApplicationListComponent),
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
