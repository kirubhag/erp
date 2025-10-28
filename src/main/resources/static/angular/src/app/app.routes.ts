import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  // Add other routes as needed
  // { path: 'students', component: StudentsComponent },
  // { path: 'staff', component: StaffComponent },
  // etc.
];

