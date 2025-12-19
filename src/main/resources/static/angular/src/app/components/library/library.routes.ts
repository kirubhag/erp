import { Routes } from '@angular/router';
import { EntityListComponent } from '../entity-list/entity-list.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';

export const LIBRARY_ROUTES: Routes = [
    { path: 'resources', component: EntityListComponent, data: { entityType: 'LIBRARY_RESOURCE' } },
    { path: 'resources/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_RESOURCE' } },

    { path: 'items', component: EntityListComponent, data: { entityType: 'RESOURCE_ITEM' } },
    { path: 'items/:id', component: EntityDetailComponent, data: { entityType: 'RESOURCE_ITEM' } },

    { path: 'loans', component: EntityListComponent, data: { entityType: 'LIBRARY_LOAN' } },
    { path: 'loans/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_LOAN' } },

    { path: 'holds', component: EntityListComponent, data: { entityType: 'LIBRARY_HOLD' } },
    { path: 'holds/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_HOLD' } },

    { path: 'policies', component: EntityListComponent, data: { entityType: 'LIBRARY_POLICY' } },
    { path: 'policies/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_POLICY' } },

    { path: 'authors', component: EntityListComponent, data: { entityType: 'AUTHOR' } },
    { path: 'authors/:id', component: EntityDetailComponent, data: { entityType: 'AUTHOR' } },

    { path: 'publishers', component: EntityListComponent, data: { entityType: 'PUBLISHER' } },
    { path: 'publishers/:id', component: EntityDetailComponent, data: { entityType: 'PUBLISHER' } },

    { path: 'purchase-requests', component: EntityListComponent, data: { entityType: 'LIBRARY_PURCHASE_REQUEST' } },
    { path: 'purchase-requests/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_PURCHASE_REQUEST' } },

    { path: 'pos', component: EntityListComponent, data: { entityType: 'LIBRARY_PO' } },
    { path: 'pos/:id', component: EntityDetailComponent, data: { entityType: 'LIBRARY_PO' } }
];
