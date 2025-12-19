import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'app-journal-entry-form',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './journal-entry-form.component.html',
    styleUrls: ['./journal-entry-form.component.css']
})
export class JournalEntryFormComponent implements OnInit {
    entryForm: FormGroup;
    accounts: any[] = [];
    loading = false;
    saving = false;
    error: string | null = null;
    id: number | null = null;

    constructor(
        private fb: FormBuilder,
        private http: HttpClient,
        public router: Router,
        private route: ActivatedRoute
    ) {
        this.entryForm = this.fb.group({
            entryNumber: ['', Validators.required],
            date: [new Date().toISOString().substring(0, 10), Validators.required],
            reference: [''],
            description: [''],
            items: this.fb.array([])
        });
    }

    ngOnInit(): void {
        this.loadAccounts();
        this.id = this.route.snapshot.params['id'];
        if (this.id) {
            this.loadEntry(this.id);
        } else {
            this.addItem();
            this.addItem();
        }
    }

    get items() {
        return this.entryForm.get('items') as FormArray;
    }

    loadAccounts(): void {
        this.http.get<any[]>('/api/entities/CHART_OF_ACCOUNT/list?pageSize=100').subscribe({
            next: (data: any) => {
                this.accounts = data.records || data;
            }
        });
    }

    loadEntry(id: number): void {
        this.loading = true;
        this.http.get<any>(`/api/entities/JOURNAL_ENTRY/${id}`).subscribe({
            next: (data) => {
                this.entryForm.patchValue({
                    entryNumber: data.entryNumber,
                    date: data.date,
                    reference: data.reference,
                    description: data.description
                });

                const itemArray = this.entryForm.get('items') as FormArray;
                itemArray.clear();
                data.items.forEach((item: any) => {
                    itemArray.push(this.fb.group({
                        accountId: [item.account.id, Validators.required],
                        label: [item.label],
                        debit: [item.debit],
                        credit: [item.credit]
                    }));
                });
                this.loading = false;
            }
        });
    }

    addItem(): void {
        this.items.push(this.fb.group({
            accountId: [null, Validators.required],
            label: [''],
            debit: [0],
            credit: [0]
        }));
    }

    removeItem(index: number): void {
        this.items.removeAt(index);
    }

    getTotalDebit(): number {
        return this.items.controls.reduce((sum, ctrl) => sum + (Number(ctrl.get('debit')?.value) || 0), 0);
    }

    getTotalCredit(): number {
        return this.items.controls.reduce((sum, ctrl) => sum + (Number(ctrl.get('credit')?.value) || 0), 0);
    }

    isBalanced(): boolean {
        return Math.abs(this.getTotalDebit() - this.getTotalCredit()) < 0.01;
    }

    onSubmit(): void {
        if (this.entryForm.invalid) return;
        if (!this.isBalanced()) {
            this.error = 'Journal entry must be balanced (Debits = Credits).';
            return;
        }

        this.saving = true;
        const val = this.entryForm.value;
        // Map items to backend structure if needed
        const payload = {
            ...val,
            items: val.items.map((item: any) => ({
                account: { id: item.accountId },
                label: item.label,
                debit: item.debit,
                credit: item.credit
            }))
        };

        const request = this.id
            ? this.http.put(`/api/entities/JOURNAL_ENTRY/${this.id}`, payload)
            : this.http.post('/api/finance/journal-entry', payload);

        request.subscribe({
            next: () => {
                this.router.navigate(['/setup/finance/journal-entries']);
                this.saving = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Failed to save journal entry';
                this.saving = false;
            }
        });
    }
}
