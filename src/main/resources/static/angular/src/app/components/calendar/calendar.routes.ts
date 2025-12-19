import { Routes } from '@angular/router';
import { EntityListComponent } from '../entity-list/entity-list.component';

export const CALENDAR_ROUTES: Routes = [
    {
        path: 'days',
        component: EntityListComponent,
        data: { entityType: 'CALENDAR_DAY' }
    },
    {
        path: 'events',
        component: EntityListComponent,
        data: { entityType: 'INSTITUTION_EVENT' }
    }
];
