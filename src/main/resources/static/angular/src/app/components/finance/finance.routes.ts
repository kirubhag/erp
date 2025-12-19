import { Routes } from '@angular/router';
import { EntityListComponent } from '../entity-list/entity-list.component';
import { TrialBalanceComponent } from './reports/trial-balance/trial-balance.component';
import { ProfitLossComponent } from './reports/profit-loss/profit-loss.component';
import { JournalEntryFormComponent } from './journal-entry-form/journal-entry-form.component';

export const FINANCE_ROUTES: Routes = [
    { path: 'fee-types', component: EntityListComponent, data: { entityType: 'FEE_TYPE' } },
    { path: 'fee-structures', component: EntityListComponent, data: { entityType: 'FEE_STRUCTURE' } },
    { path: 'discounts', component: EntityListComponent, data: { entityType: 'FEE_DISCOUNT_RULE' } },
    { path: 'fine-categories', component: EntityListComponent, data: { entityType: 'FINE_CATEGORY' } },
    { path: 'fine-management', component: EntityListComponent, data: { entityType: 'FINE_CONFIGURATION' } },
    { path: 'payments', component: EntityListComponent, data: { entityType: 'FEE_PAYMENT' } },
    { path: 'fine-ledger', component: EntityListComponent, data: { entityType: 'FINE_LEDGER' } },
    { path: 'incidents', component: EntityListComponent, data: { entityType: 'DISCIPLINARY_INCIDENT' } },
    { path: 'waivers', component: EntityListComponent, data: { entityType: 'FINE_WAIVER_REQUEST' } },
    { path: 'invoices', component: EntityListComponent, data: { entityType: 'INVOICE' } },
    { path: 'transactions', component: EntityListComponent, data: { entityType: 'TRANSACTION' } },

    // General Ledger
    { path: 'accounts', component: EntityListComponent, data: { entityType: 'CHART_OF_ACCOUNT' } },
    { path: 'journal-entries', component: EntityListComponent, data: { entityType: 'JOURNAL_ENTRY' } },
    { path: 'journal-entries/new', component: JournalEntryFormComponent },
    { path: 'journal-entries/:id/edit', component: JournalEntryFormComponent },
    { path: 'accounting-periods', component: EntityListComponent, data: { entityType: 'ACCOUNTING_PERIOD' } },
    { path: 'trial-balance', component: TrialBalanceComponent },
    { path: 'profit-loss', component: ProfitLossComponent },

    // Budgeting & Bank Reconciliation
    { path: 'budgets', component: EntityListComponent, data: { entityType: 'BUDGET' } },
    { path: 'bank-statements', component: EntityListComponent, data: { entityType: 'BANK_STATEMENT' } },

    // Scholarships
    { path: 'scholarship-categories', component: EntityListComponent, data: { entityType: 'SCHOLARSHIP_CATEGORY' } },
    { path: 'scholarship-applications', component: EntityListComponent, data: { entityType: 'SCHOLARSHIP_APPLICATION' } }
];
