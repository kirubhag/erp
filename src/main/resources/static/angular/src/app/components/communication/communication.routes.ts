import { Routes } from '@angular/router';
import { EntityListComponent } from '../entity-list/entity-list.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';

export const COMMUNICATION_ROUTES: Routes = [
    { path: 'messages', component: EntityListComponent, data: { entityType: 'MESSAGE' } },
    { path: 'messages/:id', component: EntityDetailComponent, data: { entityType: 'MESSAGE' } },
    { path: 'announcements', component: EntityListComponent, data: { entityType: 'ANNOUNCEMENT' } },
    { path: 'announcements/:id', component: EntityDetailComponent, data: { entityType: 'ANNOUNCEMENT' } },
    { path: 'tickets', component: EntityListComponent, data: { entityType: 'SUPPORT_TICKET' } },
    { path: 'tickets/:id', component: EntityDetailComponent, data: { entityType: 'SUPPORT_TICKET' } }
];
