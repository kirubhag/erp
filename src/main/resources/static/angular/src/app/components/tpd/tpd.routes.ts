import { Routes } from '@angular/router';
import { EntityListComponent } from '../entity-list/entity-list.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';

export const TPD_ROUTES: Routes = [
    { path: 'competencies', component: EntityListComponent, data: { entityType: 'COMPETENCY' } },
    { path: 'competencies/:id', component: EntityDetailComponent, data: { entityType: 'COMPETENCY' } },

    { path: 'assessments', component: EntityListComponent, data: { entityType: 'SKILL_ASSESSMENT' } },
    { path: 'assessments/:id', component: EntityDetailComponent, data: { entityType: 'SKILL_ASSESSMENT' } },

    { path: 'events', component: EntityListComponent, data: { entityType: 'TRAINING_EVENT' } },
    { path: 'events/:id', component: EntityDetailComponent, data: { entityType: 'TRAINING_EVENT' } },

    { path: 'ledger', component: EntityListComponent, data: { entityType: 'CPD_LEDGER' } },
    { path: 'ledger/:id', component: EntityDetailComponent, data: { entityType: 'CPD_LEDGER' } },

    { path: 'portfolios', component: EntityListComponent, data: { entityType: 'PROFESSIONAL_PORTFOLIO' } },
    { path: 'portfolios/:id', component: EntityDetailComponent, data: { entityType: 'PROFESSIONAL_PORTFOLIO' } }
];
