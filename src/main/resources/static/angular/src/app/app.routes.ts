import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { StudentListComponent } from './components/student-list/student-list.component';
import { SetupComponent } from './components/setup/setup.component';
import { PersonalSettingsComponent } from './components/personal-settings/personal-settings.component';
import { UserComponent } from './components/user/user.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'students', component: StudentListComponent, canActivate: [AuthGuard] },
  { path: 'setup', component: SetupComponent, canActivate: [AuthGuard] },
  { path: 'setup/personal-settings', component: PersonalSettingsComponent, canActivate: [AuthGuard] },
  { path: 'setup/users', component: UserComponent, canActivate: [AuthGuard] },
  // Add other routes as needed
  // { path: 'staff', component: StaffComponent },
  // { path: 'attendance', component: AttendanceComponent },
  // { path: 'parents', component: ParentsComponent },
  // { path: 'subjects', component: SubjectsComponent },
  // { path: 'timetables', component: TimetablesComponent },
  // { path: 'health', component: HealthRecordsComponent },
];

