import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface PricingPlan {
  id: number;
  planType: string;
  name: string;
  description: string;
  priceMonthly: number;
  priceYearly: number;
  features: any;
  maxUsers: number;
  maxStorageGb: number;
  isActive: boolean;
  trialDays: number;
}

interface Subscription {
  id: number;
  plan: PricingPlan;
  subscriptionStatus: string;
  trialStartDate?: string;
  trialEndDate?: string;
  nextBillingDate?: string;
  billingCycle: string;
}

@Component({
  selector: 'app-pricing',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pricing.component.html',
  styleUrls: ['./pricing.component.css']
})
export class PricingComponent implements OnInit {
  plans: PricingPlan[] = [];
  currentSubscription: Subscription | null = null;
  isLoading = true;
  isTrial = false;
  daysUntilExpiry = 0;
  
  selectedPlan: PricingPlan | null = null;
  billingCycle: 'MONTHLY' | 'YEARLY' = 'MONTHLY';
  showUpgradeModal = false;
  
  paymentForm = {
    cardNumber: '',
    cardHolderName: '',
    cardExpiry: '',
    cardCvv: '',
    paymentMethod: 'CREDIT_CARD'
  };
  
  isProcessing = false;
  errorMessage = '';
  successMessage = '';

  private apiUrl = '/api/subscriptions';
  private userId: number = 1; // TODO: Get from auth service
  private orgId: number = 1; // TODO: Get from auth service

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadPlans();
    this.loadCurrentSubscription();
  }

  loadPlans(): void {
    this.http.get<PricingPlan[]>(`${this.apiUrl}/plans`).subscribe({
      next: (plans) => {
        this.plans = plans.sort((a, b) => a.priceMonthly - b.priceMonthly);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading plans:', err);
        this.isLoading = false;
      }
    });
  }

  loadCurrentSubscription(): void {
    this.http.get<any>(`${this.apiUrl}/current`, {
      params: { userId: this.userId.toString(), organizationId: this.orgId.toString() }
    }).subscribe({
      next: (response) => {
        if (response.hasSubscription) {
          this.currentSubscription = response.subscription;
          this.isTrial = response.isTrial;
          this.daysUntilExpiry = response.daysUntilExpiry;
        }
      },
      error: (err) => {
        console.error('Error loading subscription:', err);
      }
    });
  }

  getPrice(plan: PricingPlan): number {
    return this.billingCycle === 'MONTHLY' ? plan.priceMonthly : plan.priceYearly;
  }

  isCurrentPlan(plan: PricingPlan): boolean {
    return this.currentSubscription?.plan?.planType === plan.planType;
  }

  canUpgrade(plan: PricingPlan): boolean {
    if (!this.currentSubscription) return true;
    
    const planOrder = { FREE: 0, BASIC: 1, STANDARD: 2, PREMIUM: 3 };
    const currentOrder = planOrder[this.currentSubscription.plan.planType as keyof typeof planOrder];
    const targetOrder = planOrder[plan.planType as keyof typeof planOrder];
    
    return targetOrder > currentOrder;
  }

  openUpgradeModal(plan: PricingPlan): void {
    this.selectedPlan = plan;
    this.showUpgradeModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeUpgradeModal(): void {
    this.showUpgradeModal = false;
    this.selectedPlan = null;
    this.paymentForm = {
      cardNumber: '',
      cardHolderName: '',
      cardExpiry: '',
      cardCvv: '',
      paymentMethod: 'CREDIT_CARD'
    };
  }

  processUpgrade(): void {
    if (!this.selectedPlan) return;

    this.isProcessing = true;
    this.errorMessage = '';
    this.successMessage = '';

    const upgradeRequest = {
      userId: this.userId,
      organizationId: this.orgId,
      targetPlan: this.selectedPlan.planType,
      billingCycle: this.billingCycle,
      ...this.paymentForm
    };

    this.http.post<any>(`${this.apiUrl}/upgrade`, upgradeRequest).subscribe({
      next: (response) => {
        this.isProcessing = false;
        if (response.success) {
          this.successMessage = response.message;
          setTimeout(() => {
            this.closeUpgradeModal();
            this.loadCurrentSubscription();
          }, 2000);
        }
      },
      error: (err) => {
        this.isProcessing = false;
        this.errorMessage = err.error?.message || 'Payment failed. Please try again.';
      }
    });
  }

  formatFeatures(features: any): string[] {
    if (typeof features === 'string') {
      try {
        features = JSON.parse(features);
      } catch (e) {
        return [];
      }
    }
    return Array.isArray(features) ? features : [];
  }

  formatCardNumber(event: any): void {
    let value = event.target.value.replace(/\s/g, '');
    let formatted = value.match(/.{1,4}/g)?.join(' ') || value;
    this.paymentForm.cardNumber = formatted;
  }

  formatExpiry(event: any): void {
    let value = event.target.value.replace(/\D/g, '');
    if (value.length >= 2) {
      value = value.substring(0, 2) + '/' + value.substring(2, 4);
    }
    this.paymentForm.cardExpiry = value;
  }
}
