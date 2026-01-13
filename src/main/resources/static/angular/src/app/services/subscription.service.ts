import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface PricingPlan {
  id: number;
  name: string;           // Backend field: name (e.g., "Free", "Starter")
  planName: string;       // Mapped from name for compatibility
  planType: string;       // Backend field: type (e.g., "MONTHLY")
  displayName: string;    // Computed from name for UI display
  description: string;    // Backend field: description
  amount: number;         // Backend field: amount
  currency: string;       // Backend field: currency
  priceMonthly: number;   // Computed from amount
  priceYearly: number;    // Computed from amount * 10 (with discount)
  maxUsers: number;       // Default based on plan tier
  maxStorageGb: number;   // Default based on plan tier
  features: string[];     // Default features based on plan tier
  isActive: boolean;      // Backend field: isActive
  isTrialEligible: boolean; // Default true for non-free plans
  trialDays: number;      // Default 14 days
}

export interface UserSubscription {
  id: number;
  userId: number;
  organizationId: number;
  plan: PricingPlan;
  subscriptionStatus: string; // Keep for compat if needed, or mapped
  status: string; // Add this to match backend
  billingCycle: string;
  trialStartDate?: string;
  trialEndDate?: string;
  subscriptionStartDate: string;
  nextBillingDate?: string;
  currentPeriodEnd?: string; // Add this
  paymentStatus: string;
  amountPaid: number;
  createdAt: string;
  updatedAt: string;
}

export interface PaymentTransaction {
  id: number;
  subscriptionId?: number;
  userId: number;
  organizationId: number;
  transactionId: string;
  transactionType: string;
  paymentMethod: string;
  amount: number;
  currency: string;
  transactionStatus: string;
  paymentGateway: string;
  gatewayResponse: any;
  errorMessage?: string;
  processedAt: string;
  createdAt: string;
}

export interface SubscriptionHistory {
  id: number;
  subscriptionId: number;
  userId: number;
  organizationId: number;
  previousPlanId?: number;
  newPlanId: number;
  changeType: string;
  changeReason?: string;
  effectiveDate: string;
  changedBy: number;
  createdAt: string;
}

export interface PaymentRequest {
  userId: number;
  organizationId: number;
  planType: string;
  billingCycle: string;
  cardNumber: string;
  cardHolderName: string;
  expiryDate: string;
  cvv: string;
}

export interface UpgradeRequest {
  userId: number;
  organizationId: number;
  targetPlan: string;
  billingCycle: string;
  paymentMethod: string;
  cardNumber: string;
  cardHolderName: string;
  cardExpiry: string;
  cardCvv: string;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = '/api/subscriptions';

  constructor(private http: HttpClient) { }

  /**
   * Get all available pricing plans
   */
  getPlans(): Observable<PricingPlan[]> {
    return this.http.get<any[]>(`${this.apiUrl}/plans`).pipe(
      map(plans => plans.map(plan => this.transformPlan(plan)))
    );
  }

  /**
   * Get current subscription for user
   */
  /**
   * Get current subscription for user (uses auth context)
   */
  getCurrentSubscription(userId: number, organizationId: number): Observable<UserSubscription> {
    // Backend gets context from Auth
    return this.http.get<any>(`${this.apiUrl}/current`).pipe(
      map(response => {
        // If response is just the subscription object or wrapper
        return this.transformSubscription(response);
      })
    );
  }

  /**
   * Extend trial
   */
  extendTrial(duration: number, unit: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/extend-trial`, { duration, unit });
  }

  /**
   * Upgrade/downgrade subscription plan
   */
  changePlan(planId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/change-plan`, { planId });
  }

  // Deprecated/Unused by current backend flow but kept for compatibility if needed
  startTrial(userId: number, organizationId: number): Observable<UserSubscription> {
    return new Observable();
  }

  upgradePlan(upgradeRequest: UpgradeRequest): Observable<UserSubscription> {
    // Map old upgrade request to new change-plan for now, ignoring payment details
    // Need to find planId from targetPlan name or something. 
    // For now, let's assume component calls changePlan directly.
    return new Observable();
  }

  /**
   * Get subscription history for user
   */
  getSubscriptionHistory(userId: number): Observable<SubscriptionHistory[]> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<SubscriptionHistory[]>(`${this.apiUrl}/history`, { params });
  }

  /**
   * Get payment transaction history for user
   */
  getPaymentHistory(userId: number): Observable<PaymentTransaction[]> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<PaymentTransaction[]>(`${this.apiUrl}/payments`, { params });
  }

  /**
   * Get default features based on plan name
   */
  private getDefaultFeatures(planName: string): string[] {
    const name = planName?.toLowerCase() || 'free';
    if (name.includes('enterprise')) {
      return ['Unlimited Users', 'Priority Support', 'Custom Integrations', 'Advanced Analytics', 'API Access', 'Dedicated Account Manager'];
    } else if (name.includes('professional')) {
      return ['Up to 50 Users', 'Email Support', 'Standard Integrations', 'Analytics Dashboard', 'API Access'];
    } else if (name.includes('starter')) {
      return ['Up to 10 Users', 'Email Support', 'Basic Integrations', 'Reports'];
    }
    return ['Up to 5 Users', 'Community Support', 'Basic Features'];
  }

  /**
   * Get default max users based on plan name
   */
  private getDefaultMaxUsers(planName: string): number {
    const name = planName?.toLowerCase() || 'free';
    if (name.includes('enterprise')) return 500;
    if (name.includes('professional')) return 50;
    if (name.includes('starter')) return 10;
    return 5;
  }

  /**
   * Get default storage based on plan name
   */
  private getDefaultStorageGb(planName: string): number {
    const name = planName?.toLowerCase() || 'free';
    if (name.includes('enterprise')) return 500;
    if (name.includes('professional')) return 100;
    if (name.includes('starter')) return 25;
    return 5;
  }

  /**
   * Transform plan data from backend to frontend format
   */
  private transformPlan(plan: any): PricingPlan {
    if (!plan) {
      return this.getDefaultPlan();
    }

    // Parse features if it's a JSON string
    let features: string[] = [];
    if (typeof plan.features === 'string') {
      try {
        features = JSON.parse(plan.features);
      } catch (e) {
        features = this.getDefaultFeatures(plan.name);
      }
    } else if (Array.isArray(plan.features)) {
      features = plan.features;
    } else {
      features = this.getDefaultFeatures(plan.name);
    }

    const amount = plan.amount || 0;
    const planName = plan.name || 'Free';
    const planType = plan.type || 'MONTHLY';

    return {
      id: plan.id,
      name: planName,
      planName: planName,
      planType: planType,
      displayName: planName,
      description: plan.description || '',
      amount: amount,
      currency: plan.currency || 'INR',
      priceMonthly: amount,
      priceYearly: amount * 10, // 10 months for yearly (20% discount)
      maxUsers: plan.maxUsers || this.getDefaultMaxUsers(planName),
      maxStorageGb: plan.maxStorageGb || this.getDefaultStorageGb(planName),
      features: features,
      isActive: plan.isActive !== false,
      isTrialEligible: planName.toLowerCase() !== 'free',
      trialDays: 14
    };
  }

  /**
   * Get a default free plan when no plan data is available
   */
  private getDefaultPlan(): PricingPlan {
    return {
      id: 0,
      name: 'Free',
      planName: 'Free',
      planType: 'FREE',
      displayName: 'Free',
      description: 'Basic features for getting started',
      amount: 0,
      currency: 'INR',
      priceMonthly: 0,
      priceYearly: 0,
      maxUsers: 5,
      maxStorageGb: 5,
      features: ['Up to 5 Users', 'Community Support', 'Basic Features'],
      isActive: true,
      isTrialEligible: false,
      trialDays: 0
    };
  }

  /**
   * Transform subscription data (parse plan features)
   */
  private transformSubscription(data: any): UserSubscription {
    return {
      ...data,
      plan: this.transformPlan(data.plan)
    };
  }
}
