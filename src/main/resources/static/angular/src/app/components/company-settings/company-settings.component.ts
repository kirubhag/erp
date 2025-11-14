import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

@Component({
  selector: 'app-company-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, SettingsSidebarComponent],
  templateUrl: './company-settings.component.html',
  styleUrls: ['./company-settings.component.css']
})
export class CompanySettingsComponent implements OnInit {
  companyForm: FormGroup;
  activeTab = 'general';
  logoPreviewUrl = 'https://via.placeholder.com/100';
  isEditing = false;

  constructor(private formBuilder: FormBuilder) {
    this.companyForm = this.formBuilder.group({
      companyName: ['', Validators.required],
      companyId: ['ORG123', Validators.required],
      website: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', Validators.required],
      country: ['', Validators.required],
      industry: ['', Validators.required],
      employeeCount: ['', Validators.required],
      fiscalYearStart: ['01-01'],
      timezone: ['UTC'],
      language: ['en'],
      currency: ['USD'],
      dateFormat: ['MM/DD/YYYY'],
      timeFormat: ['12h']
    });
  }

  ngOnInit() {
    this.loadCompanyData();
  }

  loadCompanyData() {
    // Mock company data
    this.companyForm.patchValue({
      companyName: 'Edu ERP Solutions',
      companyId: 'ERP_ORG_001',
      website: 'https://www.eduerp.com',
      email: 'support@eduerp.com',
      phone: '+1 (555) 123-4567',
      address: '123 Business Street, Suite 100',
      city: 'New York',
      state: 'NY',
      zipCode: '10001',
      country: 'United States',
      industry: 'Education Technology',
      employeeCount: '50-100'
    });
  }

  editCompanyInfo() {
    this.isEditing = true;
  }

  saveCompanyInfo() {
    if (this.companyForm.valid) {
      console.log('Saving company settings:', this.companyForm.value);
      alert('Company settings saved successfully!');
      this.isEditing = false;
    }
  }

  cancelEdit() {
    this.isEditing = false;
    this.loadCompanyData();
  }

  onLogoUpload(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.logoPreviewUrl = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }
}
