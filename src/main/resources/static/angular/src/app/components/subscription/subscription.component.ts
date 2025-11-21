import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SettingsSidebarComponent } from '../settings-sidebar/settings-sidebar.component';

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
  imports: [CommonModule, RouterModule, SettingsSidebarComponent],
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {
  activeTab: 'onetime' | 'daily' = 'onetime';

  // Plan details
  profileId = 'ORG_ERP_001';
  currentPlan = 'Enterprise';
  supportPlan = 'Premium';
  billingCycle = 'Yearly';
  nextRenewal = new Date('2026-05-15');

  // Users limit
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

  constructor() { }

  ngOnInit(): void {
  }

  setActiveTab(tab: 'onetime' | 'daily'): void {
    this.activeTab = tab;
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString('en-US', {
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
