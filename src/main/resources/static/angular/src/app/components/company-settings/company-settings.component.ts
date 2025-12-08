import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';
import { EntityAvatarComponent } from '../entity-avatar/entity-avatar.component';
import { OrganizationService, Organization } from '../../services/organization.service';

@Component({
  selector: 'app-company-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, SettingsSidebarComponent, EntityAvatarComponent],
  templateUrl: './company-settings.component.html',
  styleUrls: ['./company-settings.component.css']
})
export class CompanySettingsComponent implements OnInit {
  companyForm: FormGroup;
  activeTab = 'general';
  logoPreviewUrl = 'https://via.placeholder.com/100';
  isEditing = false;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Company avatar properties
  companyId: number = 0;
  companyName: string = '';
  currentOrganization: Organization | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private organizationService: OrganizationService
  ) {
    this.companyForm = this.formBuilder.group({
      companyName: ['', Validators.required],
      companyId: [{ value: 'ORG123', disabled: true }, Validators.required],
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
    // Wait for organization service to be ready
    setTimeout(() => {
      this.loadCompanyData();
      this.disableFormControls();
    }, 100);
  }

  disableFormControls() {
    // Disable all controls except companyId (which is always disabled)
    Object.keys(this.companyForm.controls).forEach(key => {
      if (key !== 'companyId') {
        this.companyForm.get(key)?.disable();
      }
    });
  }

  enableFormControls() {
    // Enable all controls except companyId (which stays disabled)
    Object.keys(this.companyForm.controls).forEach(key => {
      if (key !== 'companyId') {
        this.companyForm.get(key)?.enable();
      }
    });
  }

  loadCompanyData() {
    this.isLoading = true;
    this.errorMessage = '';
    
    // Get current organization from service
    this.currentOrganization = this.organizationService.getCurrentOrganization();
    
    if (this.currentOrganization) {
      this.companyId = this.currentOrganization.id;
      this.companyName = this.currentOrganization.name;
      
      // Patch form with organization data
      this.companyForm.patchValue({
        companyName: this.currentOrganization.name,
        companyId: this.currentOrganization.code,
        website: this.currentOrganization.website || '',
        email: this.currentOrganization.email,
        phone: this.currentOrganization.phone,
        address: this.currentOrganization.streetAddress || '',
        city: this.currentOrganization.city,
        state: this.currentOrganization.state || '',
        zipCode: this.currentOrganization.postalCode || '',
        country: this.currentOrganization.country,
        industry: this.currentOrganization.type || '',
        employeeCount: ''
      });
      
      this.isLoading = false;
    } else {
      // Try to reload from server
      const orgIdStr = localStorage.getItem('organizationId');
      if (orgIdStr) {
        const orgId = parseInt(orgIdStr, 10);
        this.organizationService.getOrganizationById(orgId).subscribe({
          next: (org) => {
            this.currentOrganization = org;
            this.companyId = org.id;
            this.companyName = org.name;
            
            this.companyForm.patchValue({
              companyName: org.name,
              companyId: org.code,
              website: org.website || '',
              email: org.email,
              phone: org.phone,
              address: org.streetAddress || '',
              city: org.city,
              state: org.state || '',
              zipCode: org.postalCode || '',
              country: org.country,
              industry: org.type || '',
              employeeCount: ''
            });
            
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error loading organization:', error);
            this.errorMessage = 'Failed to load organization data. Please try again.';
            this.isLoading = false;
          }
        });
      } else {
        this.errorMessage = 'No organization found. Please log in again.';
        this.isLoading = false;
      }
    }
  }

  editCompanyInfo() {
    this.isEditing = true;
    this.enableFormControls();
  }

  saveCompanyInfo() {
    if (this.companyForm.valid && this.currentOrganization) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      
      const formValue = this.companyForm.getRawValue();
      
      // Update organization object with form values
      const updatedOrg: Organization = {
        ...this.currentOrganization,
        name: formValue.companyName,
        website: formValue.website,
        email: formValue.email,
        phone: formValue.phone,
        streetAddress: formValue.address,
        city: formValue.city,
        state: formValue.state,
        postalCode: formValue.zipCode,
        country: formValue.country,
        type: formValue.industry
      };
      
      // Save to server
      this.organizationService.updateOrganization(this.currentOrganization.id, updatedOrg).subscribe({
        next: (savedOrg) => {
          this.currentOrganization = savedOrg;
          this.companyName = savedOrg.name;
          this.successMessage = 'Organization settings saved successfully!';
          this.isEditing = false;
          this.disableFormControls();
          this.isLoading = false;
          
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          console.error('Error saving organization:', error);
          this.errorMessage = 'Failed to save organization settings. Please try again.';
          this.isLoading = false;
        }
      });
    }
  }

  cancelEdit() {
    this.isEditing = false;
    this.loadCompanyData();
    this.disableFormControls();
  }

  /**
   * Handle company logo update
   */
  onCompanyLogoUpdated(url: string) {
    console.log('Company logo updated:', url);
    this.logoPreviewUrl = url;
  }

  /**
   * Handle company logo deletion
   */
  onCompanyLogoDeleted() {
    console.log('Company logo deleted');
    this.logoPreviewUrl = 'https://via.placeholder.com/100';
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }
}
