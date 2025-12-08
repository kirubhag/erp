import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
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
];

