import { Routes } from '@angular/router';
import { RouteDataEntityComponent } from '../route-data-entity/route-data-entity.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';

export const COMMUNICATION_ROUTES: Routes = [
    { path: 'messages', component: RouteDataEntityComponent, data: { entityType: 'MESSAGE' } },
    { path: 'messages/:id', component: EntityDetailComponent, data: { entityType: 'MESSAGE' } },
    { path: 'announcements', component: RouteDataEntityComponent, data: { entityType: 'ANNOUNCEMENT' } },
    { path: 'announcements/:id', component: EntityDetailComponent, data: { entityType: 'ANNOUNCEMENT' } },
    { path: 'tickets', component: RouteDataEntityComponent, data: { entityType: 'SUPPORT_TICKET' } },
    { path: 'tickets/:id', component: EntityDetailComponent, data: { entityType: 'SUPPORT_TICKET' } }
];
