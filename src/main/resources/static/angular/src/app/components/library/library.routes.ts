import { Routes } from '@angular/router';
import { RouteDataEntityComponent } from '../route-data-entity/route-data-entity.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';

export const LIBRARY_ROUTES: Routes = [
    // Authors
    { path: 'authors', component: RouteDataEntityComponent, data: { entityType: 'AUTHOR' } },
    { path: 'authors/:id', component: EntityDetailComponent, data: { entityType: 'AUTHOR' } },

    // Publishers
    { path: 'publishers', component: RouteDataEntityComponent, data: { entityType: 'PUBLISHER' } },
    { path: 'publishers/:id', component: EntityDetailComponent, data: { entityType: 'PUBLISHER' } },

    // Library Resources
    { path: 'resources', component: RouteDataEntityComponent, data: { entityType: 'LIBRARY_RESOURCE' } },
    { path: 'resources/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_RESOURCE' } },

    // Resource Items
    { path: 'items', component: RouteDataEntityComponent, data: { entityType: 'RESOURCE_ITEM' } },
    { path: 'items/:id', component: EntityDetailComponent, data: { entityType: 'RESOURCE_ITEM' } },

    // Library Loans
    { path: 'loans', component: RouteDataEntityComponent, data: { entityType: 'LIBRARY_LOAN' } },
    { path: 'loans/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_LOAN' } },

    // Library Holds
    { path: 'holds', component: RouteDataEntityComponent, data: { entityType: 'LIBRARY_HOLD' } },
    { path: 'holds/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_HOLD' } },

    // Library Policies
    { path: 'policies', component: RouteDataEntityComponent, data: { entityType: 'LIBRARY_POLICY' } },
    { path: 'policies/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_POLICY' } },

    // Library Purchase Requests
    { path: 'purchase-requests', component: RouteDataEntityComponent, data: { entityType: 'LIBRARY_PURCHASE_REQUEST' } },
    { path: 'purchase-requests/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_PURCHASE_REQUEST' } },

    // Library Purchase Orders
    { path: 'pos', component: RouteDataEntityComponent, data: { entityType: 'LIBRARY_PO' } },
    { path: 'pos/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_PO' } }
];
