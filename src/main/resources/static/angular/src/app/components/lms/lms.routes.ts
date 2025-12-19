import { Routes } from '@angular/router';
import { EntityListComponent } from '../entity-list/entity-list.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';

export const LMS_ROUTES: Routes = [
    { path: 'modules', component: EntityListComponent, data: { entityType: 'LMS_MODULE' } },
    { path: 'modules/:id', component: EntityDetailComponent, data: { entityType: 'LMS_MODULE' } },

    { path: 'lessons', component: EntityListComponent, data: { entityType: 'LESSON' } },
    { path: 'lessons/:id', component: EntityDetailComponent, data: { entityType: 'LESSON' } },

    { path: 'topics', component: EntityListComponent, data: { entityType: 'LMS_TOPIC' } },
    { path: 'topics/:id', component: EntityDetailComponent, data: { entityType: 'LMS_TOPIC' } },

    { path: 'contents', component: EntityListComponent, data: { entityType: 'LMS_CONTENT' } },
    { path: 'contents/:id', component: EntityDetailComponent, data: { entityType: 'LMS_CONTENT' } },

    { path: 'quizzes', component: EntityListComponent, data: { entityType: 'LMS_QUIZ' } },
    { path: 'quizzes/:id', component: EntityDetailComponent, data: { entityType: 'LMS_QUIZ' } },

    { path: 'question-bank', component: EntityListComponent, data: { entityType: 'LMS_QUESTION_BANK' } },
    { path: 'question-bank/:id', component: EntityDetailComponent, data: { entityType: 'LMS_QUESTION_BANK' } },

    { path: 'submissions', component: EntityListComponent, data: { entityType: 'LMS_SUBMISSION' } },
    { path: 'submissions/:id', component: EntityDetailComponent, data: { entityType: 'LMS_SUBMISSION' } },

    { path: 'rubrics', component: EntityListComponent, data: { entityType: 'LMS_RUBRIC' } },
    { path: 'rubrics/:id', component: EntityDetailComponent, data: { entityType: 'LMS_RUBRIC' } },

    { path: 'virtual-sessions', component: EntityListComponent, data: { entityType: 'VIRTUAL_SESSION' } },
    { path: 'virtual-sessions/:id', component: EntityDetailComponent, data: { entityType: 'VIRTUAL_SESSION' } },

    { path: 'virtual-attendance', component: EntityListComponent, data: { entityType: 'VIRTUAL_ATTENDANCE' } },
    { path: 'virtual-attendance/:id', component: EntityDetailComponent, data: { entityType: 'VIRTUAL_ATTENDANCE' } },

    { path: 'progress', component: EntityListComponent, data: { entityType: 'STUDENT_PROGRESS' } },
    { path: 'progress/:id', component: EntityDetailComponent, data: { entityType: 'STUDENT_PROGRESS' } },

    { path: 'badges', component: EntityListComponent, data: { entityType: 'LMS_BADGE' } },
    { path: 'badges/:id', component: EntityDetailComponent, data: { entityType: 'LMS_BADGE' } },

    { path: 'points', component: EntityListComponent, data: { entityType: 'LMS_POINT_LOG' } },
    { path: 'points/:id', component: EntityDetailComponent, data: { entityType: 'LMS_POINT_LOG' } },

    { path: 'forums', component: EntityListComponent, data: { entityType: 'LMS_FORUM' } },
    { path: 'forums/:id', component: EntityDetailComponent, data: { entityType: 'LMS_FORUM' } },

    { path: 'peer-reviews', component: EntityListComponent, data: { entityType: 'LMS_PEER_REVIEW' } },
    { path: 'peer-reviews/:id', component: EntityDetailComponent, data: { entityType: 'LMS_PEER_REVIEW' } }
];
