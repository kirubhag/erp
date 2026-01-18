import { Routes } from '@angular/router';
import { RouteDataEntityComponent } from '../route-data-entity/route-data-entity.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';

export const INVENTORY_ROUTES: Routes = [
    // Assets
    { path: 'assets', component: RouteDataEntityComponent, data: { entityType: 'ASSET' } },
    { path: 'assets/:id', component: EntityDetailComponent, data: { entityType: 'ASSET' } },
    
    // Consumables
    { path: 'consumables', component: RouteDataEntityComponent, data: { entityType: 'CONSUMABLE' } },
    { path: 'consumables/:id', component: EntityDetailComponent, data: { entityType: 'CONSUMABLE' } },
    
    // Vendors
    { path: 'vendors', component: RouteDataEntityComponent, data: { entityType: 'VENDOR' } },
    { path: 'vendors/:id', component: EntityDetailComponent, data: { entityType: 'VENDOR' } },
    
    // Purchase Orders
    { path: 'purchase-orders', component: RouteDataEntityComponent, data: { entityType: 'PURCHASE_ORDER' } },
    { path: 'purchase-orders/:id', component: EntityDetailComponent, data: { entityType: 'PURCHASE_ORDER' } }
];
