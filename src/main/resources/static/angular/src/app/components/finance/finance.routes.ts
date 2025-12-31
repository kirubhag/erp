import { Routes } from '@angular/router';
import { RouteDataEntityComponent } from '../route-data-entity/route-data-entity.component';
import { EntityDetailComponent } from '../entity-detail/entity-detail.component';
import { TrialBalanceComponent } from './reports/trial-balance/trial-balance.component';
import { ProfitLossComponent } from './reports/profit-loss/profit-loss.component';
import { JournalEntryFormComponent } from './journal-entry-form/journal-entry-form.component';

export const FINANCE_ROUTES: Routes = [
    // Fee Types
    { path: 'fee-types', component: RouteDataEntityComponent, data: { entityType: 'FEE_TYPE' } },
    { path: 'fee-types/:id', component: EntityDetailComponent, data: { entityType: 'FEE_TYPE' } },
    
    // Fee Structures
    { path: 'fee-structures', component: RouteDataEntityComponent, data: { entityType: 'FEE_STRUCTURE' } },
    { path: 'fee-structures/:id', component: EntityDetailComponent, data: { entityType: 'FEE_STRUCTURE' } },
    
    // Discounts
    { path: 'discounts', component: RouteDataEntityComponent, data: { entityType: 'FEE_DISCOUNT_RULE' } },
    { path: 'discounts/:id', component: EntityDetailComponent, data: { entityType: 'FEE_DISCOUNT_RULE' } },
    
    // Fine Categories
    { path: 'fine-categories', component: RouteDataEntityComponent, data: { entityType: 'FINE_CATEGORY' } },
    { path: 'fine-categories/:id', component: EntityDetailComponent, data: { entityType: 'FINE_CATEGORY' } },
    
    // Fine Management
    { path: 'fine-management', component: RouteDataEntityComponent, data: { entityType: 'FINE_CONFIGURATION' } },
    { path: 'fine-management/:id', component: EntityDetailComponent, data: { entityType: 'FINE_CONFIGURATION' } },
    
    // Payments
    { path: 'payments', component: RouteDataEntityComponent, data: { entityType: 'FEE_PAYMENT' } },
    { path: 'payments/:id', component: EntityDetailComponent, data: { entityType: 'FEE_PAYMENT' } },
    
    // Fine Ledger
    { path: 'fine-ledger', component: RouteDataEntityComponent, data: { entityType: 'FINE_LEDGER' } },
    { path: 'fine-ledger/:id', component: EntityDetailComponent, data: { entityType: 'FINE_LEDGER' } },
    
    // Incidents
    { path: 'incidents', component: RouteDataEntityComponent, data: { entityType: 'DISCIPLINARY_INCIDENT' } },
    { path: 'incidents/:id', component: EntityDetailComponent, data: { entityType: 'DISCIPLINARY_INCIDENT' } },
    
    // Waivers
    { path: 'waivers', component: RouteDataEntityComponent, data: { entityType: 'FINE_WAIVER_REQUEST' } },
    { path: 'waivers/:id', component: EntityDetailComponent, data: { entityType: 'FINE_WAIVER_REQUEST' } },
    
    // Invoices
    { path: 'invoices', component: RouteDataEntityComponent, data: { entityType: 'INVOICE' } },
    { path: 'invoices/:id', component: EntityDetailComponent, data: { entityType: 'INVOICE' } },
    
    // Transactions
    { path: 'transactions', component: RouteDataEntityComponent, data: { entityType: 'TRANSACTION' } },
    { path: 'transactions/:id', component: EntityDetailComponent, data: { entityType: 'TRANSACTION' } },

    // General Ledger - Accounts
    { path: 'accounts', component: RouteDataEntityComponent, data: { entityType: 'CHART_OF_ACCOUNT' } },
    { path: 'accounts/:id', component: EntityDetailComponent, data: { entityType: 'CHART_OF_ACCOUNT' } },
    
    // Journal Entries
    { path: 'journal-entries', component: RouteDataEntityComponent, data: { entityType: 'JOURNAL_ENTRY' } },
    { path: 'journal-entries/new', component: JournalEntryFormComponent },
    { path: 'journal-entries/:id/edit', component: JournalEntryFormComponent },
    { path: 'journal-entries/:id', component: EntityDetailComponent, data: { entityType: 'JOURNAL_ENTRY' } },
    
    // Accounting Periods
    { path: 'accounting-periods', component: RouteDataEntityComponent, data: { entityType: 'ACCOUNTING_PERIOD' } },
    { path: 'accounting-periods/:id', component: EntityDetailComponent, data: { entityType: 'ACCOUNTING_PERIOD' } },
    
    // Reports
    { path: 'trial-balance', component: TrialBalanceComponent },
    { path: 'profit-loss', component: ProfitLossComponent },

    // Budgeting
    { path: 'budgets', component: RouteDataEntityComponent, data: { entityType: 'BUDGET' } },
    { path: 'budgets/:id', component: EntityDetailComponent, data: { entityType: 'BUDGET' } },
    
    // Bank Statements
    { path: 'bank-statements', component: RouteDataEntityComponent, data: { entityType: 'BANK_STATEMENT' } },
    { path: 'bank-statements/:id', component: EntityDetailComponent, data: { entityType: 'BANK_STATEMENT' } },

    // Scholarship Categories
    { path: 'scholarship-categories', component: RouteDataEntityComponent, data: { entityType: 'SCHOLARSHIP_CATEGORY' } },
    { path: 'scholarship-categories/:id', component: EntityDetailComponent, data: { entityType: 'SCHOLARSHIP_CATEGORY' } },
    
    // Scholarship Applications
    { path: 'scholarship-applications', component: RouteDataEntityComponent, data: { entityType: 'SCHOLARSHIP_APPLICATION' } },
    { path: 'scholarship-applications/:id', component: EntityDetailComponent, data: { entityType: 'SCHOLARSHIP_APPLICATION' } }
];
