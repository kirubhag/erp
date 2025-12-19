import { Routes } from '@angular/router';
import { EntityListComponent } from '../entity-list/entity-list.component';

export const DOCUMENT_ROUTES: Routes = [
  {
    path: 'list',
    component: EntityListComponent,
    data: { entityType: 'ERP_DOCUMENT' }
  }
];
