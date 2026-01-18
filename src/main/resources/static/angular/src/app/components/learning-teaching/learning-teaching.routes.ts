import { Routes } from '@angular/router';
import { EntityListComponent } from '../entity-list/entity-list.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';

export const LEARNING_TEACHING_ROUTES: Routes = [
    // LMS Module entities
    { path: 'modules', component: EntityListComponent, data: { entityType: 'LMS_MODULE' } },
    { path: 'modules/:id', component: EntityDetailComponent, data: { entityType: 'LMS_MODULE' } },

    { path: 'lessons', component: EntityListComponent, data: { entityType: 'LESSON' } },
    { path: 'lessons/:id', component: EntityDetailComponent, data: { entityType: 'LESSON' } },

    { path: 'topics', component: EntityListComponent, data: { entityType: 'LMS_TOPIC' } },
    { path: 'topics/:id', component: EntityDetailComponent, data: { entityType: 'LMS_TOPIC' } },

    { path: 'contents', component: EntityListComponent, data: { entityType: 'LMS_CONTENT' } },
    { path: 'contents/:id', component: EntityDetailComponent, data: { entityType: 'LMS_CONTENT' } },

    // Quiz & Assessment
    { path: 'quizzes', component: EntityListComponent, data: { entityType: 'LMS_QUIZ' } },
    { path: 'quizzes/:id', component: EntityDetailComponent, data: { entityType: 'LMS_QUIZ' } },

    { path: 'question-bank', component: EntityListComponent, data: { entityType: 'LMS_QUESTION_BANK' } },
    { path: 'question-bank/:id', component: EntityDetailComponent, data: { entityType: 'LMS_QUESTION_BANK' } },

    { path: 'submissions', component: EntityListComponent, data: { entityType: 'LMS_SUBMISSION' } },
    { path: 'submissions/:id', component: EntityDetailComponent, data: { entityType: 'LMS_SUBMISSION' } },

    { path: 'rubrics', component: EntityListComponent, data: { entityType: 'LMS_RUBRIC' } },
    { path: 'rubrics/:id', component: EntityDetailComponent, data: { entityType: 'LMS_RUBRIC' } },

    // Virtual Classroom
    { path: 'virtual-sessions', component: EntityListComponent, data: { entityType: 'VIRTUAL_SESSION' } },
    { path: 'virtual-sessions/:id', component: EntityDetailComponent, data: { entityType: 'VIRTUAL_SESSION' } },

    { path: 'virtual-attendance', component: EntityListComponent, data: { entityType: 'VIRTUAL_ATTENDANCE' } },
    { path: 'virtual-attendance/:id', component: EntityDetailComponent, data: { entityType: 'VIRTUAL_ATTENDANCE' } },

    // Progress & Gamification
    { path: 'progress', component: EntityListComponent, data: { entityType: 'STUDENT_PROGRESS' } },
    { path: 'progress/:id', component: EntityDetailComponent, data: { entityType: 'STUDENT_PROGRESS' } },

    { path: 'badges', component: EntityListComponent, data: { entityType: 'LMS_BADGE' } },
    { path: 'badges/:id', component: EntityDetailComponent, data: { entityType: 'LMS_BADGE' } },

    { path: 'points', component: EntityListComponent, data: { entityType: 'LMS_POINT_LOG' } },
    { path: 'points/:id', component: EntityDetailComponent, data: { entityType: 'LMS_POINT_LOG' } },

    // Discussion Forums
    { path: 'forums', component: EntityListComponent, data: { entityType: 'LMS_FORUM' } },
    { path: 'forums/:id', component: EntityDetailComponent, data: { entityType: 'LMS_FORUM' } },

    { path: 'posts', component: EntityListComponent, data: { entityType: 'LMS_FORUM_POST' } },
    { path: 'posts/:id', component: EntityDetailComponent, data: { entityType: 'LMS_FORUM_POST' } },

    { path: 'peer-reviews', component: EntityListComponent, data: { entityType: 'LMS_PEER_REVIEW' } },
    { path: 'peer-reviews/:id', component: EntityDetailComponent, data: { entityType: 'LMS_PEER_REVIEW' } },

    // TPD - Teacher Professional Development
    { path: 'competencies', component: EntityListComponent, data: { entityType: 'COMPETENCY' } },
    { path: 'competencies/:id', component: EntityDetailComponent, data: { entityType: 'COMPETENCY' } },

    { path: 'assessments', component: EntityListComponent, data: { entityType: 'SKILL_ASSESSMENT' } },
    { path: 'assessments/:id', component: EntityDetailComponent, data: { entityType: 'SKILL_ASSESSMENT' } },

    { path: 'events', component: EntityListComponent, data: { entityType: 'TRAINING_EVENT' } },
    { path: 'events/:id', component: EntityDetailComponent, data: { entityType: 'TRAINING_EVENT' } },

    { path: 'ledger', component: EntityListComponent, data: { entityType: 'CPD_LEDGER' } },
    { path: 'ledger/:id', component: EntityDetailComponent, data: { entityType: 'CPD_LEDGER' } },

    { path: 'portfolios', component: EntityListComponent, data: { entityType: 'PROFESSIONAL_PORTFOLIO' } },
    { path: 'portfolios/:id', component: EntityDetailComponent, data: { entityType: 'PROFESSIONAL_PORTFOLIO' } },

    // Default redirect to modules
    { path: '', redirectTo: 'modules', pathMatch: 'full' }
];
