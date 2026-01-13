import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';
import {
  SubscriptionService,
  PricingPlan,
  UserSubscription,
  UpgradeRequest,
  PaymentTransaction,
  SubscriptionHistory
} from '../../services/subscription.service';

interface FeatureLimit {
  feature: string;
  defaultLimit: string;
  additionalLimit: string;
  totalLimit: string;
  used: string;
  remaining: string;
  hasLink?: boolean;
}

@Component({
  selector: 'app-subscription',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SettingsSidebarComponent],
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {
  // Main section tabs
  activeSection: 'plan-details' | 'available-plans' | 'transaction-history' | 'usage-details' = 'plan-details';
  
  // Usage sub-tabs
  activeTab: 'onetime' | 'daily' = 'onetime';

  // User context (TODO: Get from auth service)
  userId = 2;
  organizationId = 1;

  // Current subscription data
  currentSubscription?: UserSubscription;
  availablePlans: PricingPlan[] = [];
  subscriptionHistory: SubscriptionHistory[] = [];
  paymentHistory: PaymentTransaction[] = [];

  // Loading states
  loading = true;
  loadingPlans = false;
  loadingHistory = false;
  upgrading = false;

  // UI state
  showUpgradeModal = false;
  showHistorySection = false;
  selectedBillingCycle: 'MONTHLY' | 'YEARLY' = 'YEARLY';
  selectedPlan?: PricingPlan;

  // Payment form
  paymentForm = {
    cardNumber: '',
    cardHolderName: '',
    expiryDate: '',
    cvv: ''
  };

  // Error handling
  error: string | null = null;
  paymentError: string | null = null;
  successMessage: string | null = null;

  // Plan details (computed from API or fallback)
  profileId = 'ORG_ERP_001';
  currentPlan = 'Loading...';
  supportPlan = 'Standard';
  billingCycle = 'Monthly';
  nextRenewal = new Date();

  // Users limit (computed from plan data)
  totalUsers = 7;
  usedUsers = 2;
  availableUsers = 5;
  usersPercentage = 28.5;

  // One-time limits data
  oneTimeLimits: FeatureLimit[] = [
    {
      feature: 'File Storage',
      defaultLimit: '10.00 GB',
      additionalLimit: '0',
      totalLimit: '10.00 GB',
      used: '935.00 Bytes',
      remaining: '10.00 GB',
      hasLink: true
    },
    {
      feature: 'Active Jobs',
      defaultLimit: '750',
      additionalLimit: '0',
      totalLimit: '750',
      used: '34',
      remaining: '716',
      hasLink: false
    },
    {
      feature: 'Record Storage',
      defaultLimit: 'Unlimited',
      additionalLimit: 'NA',
      totalLimit: 'Unlimited',
      used: '669',
      remaining: 'Unlimited',
      hasLink: true
    },
    {
      feature: 'Portal Users',
      defaultLimit: '10',
      additionalLimit: '0',
      totalLimit: '10',
      used: '0',
      remaining: '10',
      hasLink: false
    },
    {
      feature: 'Video Interview',
      defaultLimit: '1',
      additionalLimit: '0',
      totalLimit: '1',
      used: '0',
      remaining: '1',
      hasLink: false
    }
  ];

  // Daily limits data (placeholder)
  dailyLimits: FeatureLimit[] = [];

  constructor(private subscriptionService: SubscriptionService) { }

  ngOnInit(): void {
    this.loadSubscriptionData();
    this.loadAvailablePlans();
  }

  /**
   * Load current subscription data from API
   */
  loadSubscriptionData(): void {
    this.loading = true;
    this.error = null;

    this.subscriptionService.getCurrentSubscription(this.userId, this.organizationId).subscribe({
      next: (subscription) => {
        this.currentSubscription = subscription;
        this.updateDisplayData();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading subscription:', err);
        this.error = 'Failed to load subscription details. Using default data.';
        this.loading = false;
        // Keep using hardcoded data as fallback
      }
    });
  }

  /**
   * Load available pricing plans
   */
  loadAvailablePlans(): void {
    this.loadingPlans = true;

    this.subscriptionService.getPlans().subscribe({
      next: (plans) => {
        this.availablePlans = plans;
        this.loadingPlans = false;
      },
      error: (err) => {
        console.error('Error loading plans:', err);
        this.loadingPlans = false;
      }
    });
  }

  /**
   * Load subscription and payment history
   */
  loadHistory(): void {
    if (this.subscriptionHistory.length > 0) {
      this.showHistorySection = !this.showHistorySection;
      return;
    }

    this.loadingHistory = true;

    this.subscriptionService.getSubscriptionHistory(this.userId).subscribe({
      next: (history) => {
        this.subscriptionHistory = history;
      },
      error: (err) => console.error('Error loading subscription history:', err)
    });

    this.subscriptionService.getPaymentHistory(this.userId).subscribe({
      next: (payments) => {
        this.paymentHistory = payments;
        this.loadingHistory = false;
        this.showHistorySection = true;
      },
      error: (err) => {
        console.error('Error loading payment history:', err);
        this.loadingHistory = false;
      }
    });
  }

  /**
   * Update display data from API subscription
   */
  updateDisplayData(): void {
    if (!this.currentSubscription) return;

    const sub = this.currentSubscription;
    // Use plan displayName, fallback to planName, then planType
    this.currentPlan = sub.plan?.displayName || sub.plan?.planName || sub.plan?.planType || 'Free';
    this.billingCycle = sub.billingCycle || 'Monthly';
    this.profileId = `ORG_${sub.organizationId}`;

    if (sub.nextBillingDate) {
      this.nextRenewal = new Date(sub.nextBillingDate);
    }

    // Update users limit from plan
    this.totalUsers = sub.plan.maxUsers;
    // Keep usedUsers as is (would need another API call to get actual usage)
    this.availableUsers = this.totalUsers - this.usedUsers;
    this.usersPercentage = (this.usedUsers / this.totalUsers) * 100;

    // Update feature limits based on plan
    this.updateFeatureLimits(sub.plan);
  }

  /**
   * Update feature limits based on plan data
   */
  updateFeatureLimits(plan: PricingPlan): void {
    this.oneTimeLimits[0].defaultLimit = `${plan.maxStorageGb} GB`;
    this.oneTimeLimits[0].totalLimit = `${plan.maxStorageGb} GB`;
    this.oneTimeLimits[0].remaining = `${plan.maxStorageGb} GB`;

    this.oneTimeLimits[3].defaultLimit = plan.maxUsers.toString();
    this.oneTimeLimits[3].totalLimit = plan.maxUsers.toString();
    this.oneTimeLimits[3].remaining = plan.maxUsers.toString();
  }

  /**
   * Open upgrade modal for selected plan
   */
  openUpgradeModal(plan: PricingPlan): void {
    this.selectedPlan = plan;
    this.showUpgradeModal = true;
    this.paymentError = null;
    this.successMessage = null;
    this.resetPaymentForm();
  }

  /**
   * Close upgrade modal
   */
  closeUpgradeModal(): void {
    this.showUpgradeModal = false;
    this.selectedPlan = undefined;
    this.resetPaymentForm();
  }

  /**
   * Process upgrade payment
   */
  processUpgrade(): void {
    if (!this.selectedPlan) return;

    // Basic validation
    if (!this.paymentForm.cardNumber || !this.paymentForm.cardHolderName ||
      !this.paymentForm.expiryDate || !this.paymentForm.cvv) {
      this.paymentError = 'Please fill all payment details';
      return;
    }

    this.upgrading = true;
    this.paymentError = null;

    const request = {
      userId: this.userId,
      organizationId: this.organizationId,
      targetPlan: this.selectedPlan.planType,
      billingCycle: this.selectedBillingCycle,
      paymentMethod: 'CARD',
      cardNumber: this.paymentForm.cardNumber.replace(/\s/g, ''),
      cardHolderName: this.paymentForm.cardHolderName,
      cardExpiry: this.paymentForm.expiryDate,
      cardCvv: this.paymentForm.cvv
    };

    this.subscriptionService.changePlan(this.selectedPlan.id).subscribe({
      next: () => {
        this.upgrading = false;
        this.successMessage = 'Subscription plan changed successfully!';

        // Close modal and refresh after showing success message
        setTimeout(() => {
          this.closeUpgradeModal();
          this.loadSubscriptionData(); // Reload to get fresh data
        }, 2000);
      },
      error: (err) => {
        console.error('Change plan failed:', err);
        this.paymentError = err.error?.message || 'Failed to change plan. Please try again.';
        this.upgrading = false;
      }
    });
  }

  /**
   * Extend trial period
   */
  extendTrial(): void {
    if (confirm('Extend trial by 1 minute for testing?')) {
      this.subscriptionService.extendTrial(1, 'MINUTES').subscribe({
        next: () => {
          alert('Trial extended!');
          this.loadSubscriptionData();
        },
        error: (err) => alert('Failed to extend trial: ' + err.message)
      });
    }
  }

  get trialRemaining(): string {
    if (this.currentSubscription?.status === 'TRIAL' && this.currentSubscription.currentPeriodEnd) {
      const end = new Date(this.currentSubscription.currentPeriodEnd).getTime();
      const now = new Date().getTime();
      const diff = end - now;
      if (diff <= 0) return 'Expired';

      const days = Math.floor(diff / (1000 * 60 * 60 * 24));
      const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

      if (days > 0) return `${days}d ${hours}h`;
      return `${hours}h ${minutes}m`;
    }
    return '';
  }

  /**
   * Reset payment form
   */
  resetPaymentForm(): void {
    this.paymentForm = {
      cardNumber: '',
      cardHolderName: '',
      expiryDate: '',
      cvv: ''
    };
  }

  /**
   * Format card number with spaces
   */
  formatCardNumber(): void {
    let value = this.paymentForm.cardNumber.replace(/\s/g, '');
    if (value.length > 16) value = value.substr(0, 16);
    this.paymentForm.cardNumber = value.replace(/(\d{4})/g, '$1 ').trim();
  }

  /**
   * Get price for selected plan and billing cycle
   */
  getPlanPrice(plan: PricingPlan): number {
    return this.selectedBillingCycle === 'YEARLY' ? plan.priceYearly : plan.priceMonthly;
  }

  /**
   * Check if plan is current
   */
  isCurrentPlan(plan: PricingPlan): boolean {
    if (!this.currentSubscription?.plan) return false;
    // Match by ID if available, otherwise by name
    if (this.currentSubscription.plan.id && plan.id) {
      return this.currentSubscription.plan.id === plan.id;
    }
    return this.currentSubscription.plan.name === plan.name || 
           this.currentSubscription.plan.displayName === plan.displayName;
  }

  /**
   * Check if plan is higher than current
   */
  canUpgradeTo(plan: PricingPlan): boolean {
    if (!this.currentSubscription?.plan) return true;
    const currentPrice = this.currentSubscription.plan.priceMonthly || this.currentSubscription.plan.amount || 0;
    const targetPrice = plan.priceMonthly || plan.amount || 0;
    return targetPrice > currentPrice;
  }

  setActiveTab(tab: 'onetime' | 'daily'): void {
    this.activeTab = tab;
  }

  formatDate(date: Date | string): string {
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return dateObj.toLocaleDateString('en-US', {
      month: '2-digit',
      day: '2-digit',
      year: 'numeric'
    });
  }

  navigateToStorage(feature: string): void {
    if (feature === 'File Storage') {
      // Navigate to file storage
    } else if (feature === 'Record Storage') {
      // Navigate to record storage
    }
  }
}
