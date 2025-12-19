import { Routes } from '@angular/router';
import { EntityListComponent } from '../shared/entity-list/entity-list.component';

export const ALUMNI_ROUTES: Routes = [
    {
        path: 'directory',
        component: EntityListComponent,
        data: { entityType: 'ALUMNI_PROFILE' }
    },
    {
        path: 'contributions',
        component: EntityListComponent,
        data: { entityType: 'ALUMNI_CONTRIBUTION' }
    }
];
