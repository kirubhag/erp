import { Routes } from '@angular/router';
import { RouteDataEntityComponent } from '../route-data-entity/route-data-entity.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';

export const LEAVE_MANAGEMENT_ROUTES: Routes = [
    // Leave Types
    { path: 'leave-types', component: RouteDataEntityComponent, data: { entityType: 'LEAVE_TYPE' } },
    { path: 'leave-types/:id', component: EntityDetailComponent, data: { entityType: 'LEAVE_TYPE' } },

    // Leave Requests
    { path: 'leaves', component: RouteDataEntityComponent, data: { entityType: 'LEAVE_REQUEST' } },
    { path: 'leaves/:id', component: EntityDetailComponent, data: { entityType: 'LEAVE_REQUEST' } },

    // Leave Balances
    { path: 'leave-balances', component: RouteDataEntityComponent, data: { entityType: 'LEAVE_BALANCE' } },
    { path: 'leave-balances/:id', component: EntityDetailComponent, data: { entityType: 'LEAVE_BALANCE' } }
];
