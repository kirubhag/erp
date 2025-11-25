import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface PricingPlan {
  id: number;
  planName: string;
  planType: string;
  displayName: string;
  description: string;
  priceMonthly: number;
  priceYearly: number;
  maxUsers: number;
  maxStorageGb: number;
  features: string[];
  isActive: boolean;
  isTrialEligible: boolean;
  trialDays: number;
}

export interface UserSubscription {
  id: number;
  userId: number;
  organizationId: number;
  plan: PricingPlan;
  subscriptionStatus: string;
  billingCycle: string;
  trialStartDate?: string;
  trialEndDate?: string;
  subscriptionStartDate: string;
  nextBillingDate?: string;
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

  constructor(private http: HttpClient) {}

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
  getCurrentSubscription(userId: number, organizationId: number): Observable<UserSubscription> {
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('organizationId', organizationId.toString());
    return this.http.get<any>(`${this.apiUrl}/current`, { params }).pipe(
      map(response => this.transformSubscription(response.subscription))
    );
  }

  /**
   * Start premium trial for user
   */
  startTrial(userId: number, organizationId: number): Observable<UserSubscription> {
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('organizationId', organizationId.toString());
    return this.http.post<any>(`${this.apiUrl}/trial/start`, null, { params }).pipe(
      map(data => this.transformSubscription(data))
    );
  }

  /**
   * Upgrade/downgrade subscription plan
   */
  upgradePlan(upgradeRequest: UpgradeRequest): Observable<UserSubscription> {
    return this.http.post<any>(`${this.apiUrl}/upgrade`, upgradeRequest).pipe(
      map(response => this.transformSubscription(response.subscription))
    );
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
   * Transform plan data from backend (parse features JSON string to array)
   */
  private transformPlan(plan: any): PricingPlan {
    let features: string[] = [];
    
    if (typeof plan.features === 'string') {
      try {
        features = JSON.parse(plan.features);
      } catch (e) {
        console.error('Failed to parse features:', plan.features, e);
        features = [];
      }
    } else if (Array.isArray(plan.features)) {
      features = plan.features;
    }

    return {
      ...plan,
      features
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
