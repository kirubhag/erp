import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { StudentListComponent } from './components/student-list/student-list.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'students', component: StudentListComponent },
  // Add other routes as needed
  // { path: 'staff', component: StaffComponent },
  // { path: 'attendance', component: AttendanceComponent },
  // { path: 'parents', component: ParentsComponent },
  // { path: 'subjects', component: SubjectsComponent },
  // { path: 'timetables', component: TimetablesComponent },
  // { path: 'health', component: HealthRecordsComponent },
];

