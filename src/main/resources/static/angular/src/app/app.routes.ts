import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { AuthGuard } from './guards/auth.guard';
import { NoAuthGuard } from './guards/no-auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent, canActivate: [NoAuthGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [NoAuthGuard] },
  {
    path: 'confirm-account',
    loadComponent: () => import('./components/confirm-account/confirm-account.component').then(m => m.ConfirmAccountComponent)
  },
  {
    path: 'accept-invitation',
    loadComponent: () => import('./components/accept-invitation/accept-invitation.component').then(m => m.AcceptInvitationComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'onboarding',
    loadComponent: () => import('./components/onboarding/onboarding.component').then(m => m.OnboardingComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'students',
    loadComponent: () => import('./components/student-list/student-list.component').then(m => m.StudentListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'staff',
    loadComponent: () => import('./components/entity-management/entity-management.component').then(m => m.EntityManagementComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'attendance',
    loadComponent: () => import('./components/entity-management/entity-management.component').then(m => m.EntityManagementComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'parents',
    loadComponent: () => import('./components/entity-management/entity-management.component').then(m => m.EntityManagementComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'subjects',
    loadComponent: () => import('./components/entity-management/entity-management.component').then(m => m.EntityManagementComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'rooms',
    loadComponent: () => import('./components/entity-management/entity-management.component').then(m => m.EntityManagementComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'timetables',
    loadComponent: () => import('./components/timetable-manager/timetable-manager.component').then(m => m.TimetableManagerComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'grades',
    loadComponent: () => import('./components/entity-management/entity-management.component').then(m => m.EntityManagementComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'assignments',
    loadComponent: () => import('./components/entity-management/entity-management.component').then(m => m.EntityManagementComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'exams',
    loadComponent: () => import('./components/entity-management/entity-management.component').then(m => m.EntityManagementComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'courses',
    loadComponent: () => import('./components/entity-management/entity-management.component').then(m => m.EntityManagementComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'entity-detail/:entityType/:id',
    loadComponent: () => import('./components/entity-detail/entity-detail.component').then(m => m.EntityDetailComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'entity-create/:entityType',
    loadComponent: () => import('./components/entity-create/entity-create.component').then(m => m.EntityCreateComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'entity-edit/:entityType/:id',
    loadComponent: () => import('./components/entity-create/entity-create.component').then(m => m.EntityCreateComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'import-wizard',
    loadComponent: () => import('./components/import-wizard/import-wizard.component').then(m => m.ImportWizardComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup',
    loadComponent: () => import('./components/setup/setup.component').then(m => m.SetupComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/personal-settings',
    loadComponent: () => import('./components/personal-settings/personal-settings.component').then(m => m.PersonalSettingsComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/users',
    loadComponent: () => import('./components/user/user.component').then(m => m.UserComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/export',
    loadComponent: () => import('./components/setup/export-data/export-data.component').then(m => m.ExportDataComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/login-history',
    redirectTo: 'setup/users',
    pathMatch: 'full'
  },
  {
    path: 'setup/profiles',
    loadComponent: () => import('./components/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/profile-detail/:id',
    loadComponent: () => import('./components/profile-detail/profile-detail.component').then(m => m.ProfileDetailComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/modules-fields',
    loadComponent: () => import('./components/modules/modules.component').then(m => m.ModulesComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/module-builder/:id',
    loadComponent: () => import('./components/module-builder/module-builder.component').then(m => m.ModuleBuilderComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/tab-groups',
    loadComponent: () => import('./components/setup/tab-groups/tab-group-list.component').then(m => m.TabGroupListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/organisation-settings',
    loadComponent: () => import('./components/company-settings/company-settings.component').then(m => m.CompanySettingsComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/academic',
    redirectTo: 'setup/academic/academic-year',
    pathMatch: 'full'
  },
  {
    path: 'setup/academic/:section',
    loadComponent: () => import('./components/academic-settings/academic-settings.component').then(m => m.AcademicSettingsComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'student-lifecycle/admission',
    loadChildren: () => import('./components/admission/admission.routes').then(m => m.ADMISSION_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'human-resources',
    loadChildren: () => import('./components/hr/hr.routes').then(m => m.HR_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'assets-supplies/inventory',
    loadChildren: () => import('./components/inventory/inventory.routes').then(m => m.INVENTORY_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'administration/reporting',
    loadChildren: () => import('./components/reporting/reporting.routes').then(m => m.REPORTING_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'administration/documents',
    loadChildren: () => import('./components/documents/documents.routes').then(m => m.DOCUMENT_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/administrative/:section',
    loadComponent: () => import('./components/common/under-construction/under-construction.component').then(m => m.UnderConstructionComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/resource/:section',
    loadComponent: () => import('./components/common/under-construction/under-construction.component').then(m => m.UnderConstructionComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/import-history',
    loadComponent: () => import('./components/import-history/import-history.component').then(m => m.ImportHistoryComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/roles-sharing',
    loadComponent: () => import('./components/roles-sharing/roles-sharing.component').then(m => m.RolesSharingComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/subscription',
    loadComponent: () => import('./components/subscription/subscription.component').then(m => m.SubscriptionComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/file-storage',
    loadComponent: () => import('./components/file-storage/file-storage.component').then(m => m.FileStorageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/record-storage',
    loadComponent: () => import('./components/record-storage/record-storage.component').then(m => m.RecordStorageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'settings/account/close',
    loadComponent: () => import('./components/account-closure/account-closure.component').then(m => m.AccountClosureComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'promotions',
    loadComponent: () => import('./components/promotion-list/promotion-list.component').then(m => m.PromotionListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'promotions/create',
    loadComponent: () => import('./components/promotion-create/promotion-create.component').then(m => m.PromotionCreateComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'promotions/:id',
    loadComponent: () => import('./components/promotion-details/promotion-details.component').then(m => m.PromotionDetailsComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/inventory',
    loadChildren: () => import('./components/inventory/inventory.routes').then(m => m.INVENTORY_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'assets-supplies/maintenance',
    loadChildren: () => import('./components/maintenance/maintenance.routes').then(m => m.MAINTENANCE_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/reporting',
    loadChildren: () => import('./components/reporting/reporting.routes').then(m => m.REPORTING_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'setup/documents',
    loadChildren: () => import('./components/documents/documents.routes').then(m => m.DOCUMENT_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'administration/calendar',
    loadChildren: () => import('./components/calendar/calendar.routes').then(m => m.CALENDAR_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'communication',
    loadChildren: () => import('./components/communication/communication.routes').then(m => m.COMMUNICATION_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'library-management',
    loadChildren: () => import('./components/library/library.routes').then(m => m.LIBRARY_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'leave-management',
    loadChildren: () => import('./components/leave-management/leave-management.routes').then(m => m.LEAVE_MANAGEMENT_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'learning-teaching',
    loadChildren: () => import('./components/learning-teaching/learning-teaching.routes').then(m => m.LEARNING_TEACHING_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'student-lifecycle/alumni',
    loadChildren: () => import('./components/alumni/alumni.routes').then(m => m.ALUMNI_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: 'finance',
    loadChildren: () => import('./components/finance/finance.routes').then(m => m.FINANCE_ROUTES),
    canActivate: [AuthGuard]
  },
  // Dynamic Tab Group Dashboard Route
  {
    path: ':tabGroupRoute',
    loadComponent: () => import('./components/dashboards/tab-group-dashboard.component').then(m => m.TabGroupDashboardComponent),
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  }
];
