import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdmissionService, AdmissionInquiry } from '../../../services/admission.service';

@Component({
    selector: 'app-inquiry-list',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './inquiry-list.component.html',
    styleUrls: ['./inquiry-list.component.css']
})
export class InquiryListComponent implements OnInit {
    inquiries: AdmissionInquiry[] = [];
    isLoading = false;

    searchTerm: string = '';
    filteredInquiries: AdmissionInquiry[] = [];

    constructor(private admissionService: AdmissionService) { }

    ngOnInit() {
        this.loadInquiries();
    }

    loadInquiries() {
        this.isLoading = true;
        this.admissionService.getInquiries().subscribe({
            next: (data) => {
                this.inquiries = data;
                this.filteredInquiries = data;
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Failed to load inquiries', err);
                this.isLoading = false;
            }
        });
    }

    updateStatus(id: number | undefined, status: string) {
        if (!id) return;
        this.admissionService.updateInquiryStatus(id, status).subscribe(() => {
            this.loadInquiries();
        });
    }

    onSearch() {
        if (!this.searchTerm) {
            this.filteredInquiries = this.inquiries;
        } else {
            const term = this.searchTerm.toLowerCase();
            this.filteredInquiries = this.inquiries.filter(i =>
                i.firstName.toLowerCase().includes(term) ||
                i.lastName.toLowerCase().includes(term) ||
                i.email?.toLowerCase().includes(term)
            );
        }
    }
}
