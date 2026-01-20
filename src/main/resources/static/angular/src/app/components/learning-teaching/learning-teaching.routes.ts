import { Routes } from '@angular/router';
import { RouteDataEntityComponent } from '../route-data-entity/route-data-entity.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';
import { TabGroupDashboardComponent } from '../dashboards/tab-group-dashboard.component';

export const LEARNING_TEACHING_ROUTES: Routes = [
    // Dashboard
    { path: 'dashboard', component: TabGroupDashboardComponent },
    
    // LMS Module entities
    { path: 'modules', component: RouteDataEntityComponent, data: { entityType: 'LMS_MODULE' } },
    { path: 'modules/:id', component: EntityDetailComponent, data: { entityType: 'LMS_MODULE' } },

    { path: 'lessons', component: RouteDataEntityComponent, data: { entityType: 'LESSON' } },
    { path: 'lessons/:id', component: EntityDetailComponent, data: { entityType: 'LESSON' } },

    { path: 'topics', component: RouteDataEntityComponent, data: { entityType: 'LMS_TOPIC' } },
    { path: 'topics/:id', component: EntityDetailComponent, data: { entityType: 'LMS_TOPIC' } },

    { path: 'contents', component: RouteDataEntityComponent, data: { entityType: 'LMS_CONTENT' } },
    { path: 'contents/:id', component: EntityDetailComponent, data: { entityType: 'LMS_CONTENT' } },

    // Quiz & Assessment
    { path: 'quizzes', component: RouteDataEntityComponent, data: { entityType: 'LMS_QUIZ' } },
    { path: 'quizzes/:id', component: EntityDetailComponent, data: { entityType: 'LMS_QUIZ' } },

    { path: 'question-bank', component: RouteDataEntityComponent, data: { entityType: 'LMS_QUESTION_BANK' } },
    { path: 'question-bank/:id', component: EntityDetailComponent, data: { entityType: 'LMS_QUESTION_BANK' } },

    { path: 'submissions', component: RouteDataEntityComponent, data: { entityType: 'LMS_SUBMISSION' } },
    { path: 'submissions/:id', component: EntityDetailComponent, data: { entityType: 'LMS_SUBMISSION' } },

    { path: 'rubrics', component: RouteDataEntityComponent, data: { entityType: 'LMS_RUBRIC' } },
    { path: 'rubrics/:id', component: EntityDetailComponent, data: { entityType: 'LMS_RUBRIC' } },

    // Virtual Classroom
    { path: 'virtual-sessions', component: RouteDataEntityComponent, data: { entityType: 'VIRTUAL_SESSION' } },
    { path: 'virtual-sessions/:id', component: EntityDetailComponent, data: { entityType: 'VIRTUAL_SESSION' } },

    { path: 'virtual-attendance', component: RouteDataEntityComponent, data: { entityType: 'VIRTUAL_ATTENDANCE' } },
    { path: 'virtual-attendance/:id', component: EntityDetailComponent, data: { entityType: 'VIRTUAL_ATTENDANCE' } },

    // Progress & Gamification
    { path: 'progress', component: RouteDataEntityComponent, data: { entityType: 'STUDENT_PROGRESS' } },
    { path: 'progress/:id', component: EntityDetailComponent, data: { entityType: 'STUDENT_PROGRESS' } },

    { path: 'badges', component: RouteDataEntityComponent, data: { entityType: 'LMS_BADGE' } },
    { path: 'badges/:id', component: EntityDetailComponent, data: { entityType: 'LMS_BADGE' } },

    { path: 'points', component: RouteDataEntityComponent, data: { entityType: 'LMS_POINT_LOG' } },
    { path: 'points/:id', component: EntityDetailComponent, data: { entityType: 'LMS_POINT_LOG' } },

    // Discussion Forums
    { path: 'forums', component: RouteDataEntityComponent, data: { entityType: 'LMS_FORUM' } },
    { path: 'forums/:id', component: EntityDetailComponent, data: { entityType: 'LMS_FORUM' } },

    { path: 'posts', component: RouteDataEntityComponent, data: { entityType: 'LMS_FORUM_POST' } },
    { path: 'posts/:id', component: EntityDetailComponent, data: { entityType: 'LMS_FORUM_POST' } },

    { path: 'peer-reviews', component: RouteDataEntityComponent, data: { entityType: 'LMS_PEER_REVIEW' } },
    { path: 'peer-reviews/:id', component: EntityDetailComponent, data: { entityType: 'LMS_PEER_REVIEW' } },

    // TPD - Teacher Professional Development
    { path: 'competencies', component: RouteDataEntityComponent, data: { entityType: 'COMPETENCY' } },
    { path: 'competencies/:id', component: EntityDetailComponent, data: { entityType: 'COMPETENCY' } },

    { path: 'assessments', component: RouteDataEntityComponent, data: { entityType: 'SKILL_ASSESSMENT' } },
    { path: 'assessments/:id', component: EntityDetailComponent, data: { entityType: 'SKILL_ASSESSMENT' } },

    { path: 'events', component: RouteDataEntityComponent, data: { entityType: 'TRAINING_EVENT' } },
    { path: 'events/:id', component: EntityDetailComponent, data: { entityType: 'TRAINING_EVENT' } },

    { path: 'ledger', component: RouteDataEntityComponent, data: { entityType: 'CPD_LEDGER' } },
    { path: 'ledger/:id', component: EntityDetailComponent, data: { entityType: 'CPD_LEDGER' } },

    { path: 'portfolios', component: RouteDataEntityComponent, data: { entityType: 'PROFESSIONAL_PORTFOLIO' } },
    { path: 'portfolios/:id', component: EntityDetailComponent, data: { entityType: 'PROFESSIONAL_PORTFOLIO' } },

    { path: 'evidence', component: RouteDataEntityComponent, data: { entityType: 'EVIDENCE' } },
    { path: 'evidence/:id', component: EntityDetailComponent, data: { entityType: 'EVIDENCE' } },

    { path: 'training-attendance', component: RouteDataEntityComponent, data: { entityType: 'TRAINING_ATTENDANCE' } },
    { path: 'training-attendance/:id', component: EntityDetailComponent, data: { entityType: 'TRAINING_ATTENDANCE' } },

    { path: 'evaluations', component: RouteDataEntityComponent, data: { entityType: 'EVALUATION' } },
    { path: 'evaluations/:id', component: EntityDetailComponent, data: { entityType: 'EVALUATION' } },

    // Default redirect to dashboard
    { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
];
