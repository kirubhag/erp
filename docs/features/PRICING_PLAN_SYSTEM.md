# ERP Pricing Plan System

## Overview
A comprehensive subscription management system with 4 pricing tiers, automatic trial management, and payment integration.

## Features
- **4 Pricing Plans**: Free, Basic, Standard, Premium
- **15-Day Premium Trial**: Automatic for new users
- **Auto-Downgrade**: Trial users automatically downgraded to Free after 15 days
- **Daily Scheduler**: Checks and processes trial expirations
- **Plan Upgrade UI**: User-friendly interface for plan upgrades
- **Payment Integration**: Mock payment gateway for testing

## Database Schema

### Tables Created:
1. **subscription_plans** - Defines available plans
2. **user_subscriptions** - Tracks user subscriptions
3. **subscription_history** - Audit trail of plan changes
4. **payment_transactions** - Payment records (mock)

## Architecture

### Backend (Spring Boot)
- **Entities**: SubscriptionPlan, UserSubscription, SubscriptionHistory, PaymentTransaction
- **Enums**: PlanType, SubscriptionStatus, PaymentStatus
- **Services**: SubscriptionService, PaymentService, TrialExpirationScheduler
- **Controllers**: SubscriptionController, PaymentController

### Frontend (Angular)
- **Components**: 
  - pricing-plans (displays all plans)
  - plan-upgrade-modal (upgrade flow)
  - subscription-status (user's current plan)
- **Services**: SubscriptionService, PaymentService

## Plan Features Matrix

| Feature | Free | Basic | Standard | Premium |
|---------|------|-------|----------|---------|
| Users | 1 | 5 | 15 | Unlimited |
| Storage | 1GB | 10GB | 50GB | Unlimited |
| Support | Community | Email | Priority | 24/7 Phone |
| Price | $0 | $29/mo | $79/mo | $149/mo |

## Implementation Steps

1. ✅ Database schema creation
2. ✅ Backend entities and repositories
3. ✅ Service layer implementation
4. ✅ Scheduler for trial expiration
5. ✅ REST API endpoints
6. ✅ Frontend components
7. ✅ Payment gateway integration
8. ✅ User registration integration

## API Endpoints

### Subscription Management
- `GET /api/subscriptions/plans` - Get all available plans
- `GET /api/subscriptions/current` - Get user's current subscription
- `POST /api/subscriptions/upgrade` - Upgrade to a new plan
- `POST /api/subscriptions/cancel` - Cancel subscription
- `GET /api/subscriptions/history` - Get subscription history

### Payment
- `POST /api/payments/process` - Process payment (mock)
- `GET /api/payments/history` - Get payment history

## Scheduler Configuration
- **Cron Expression**: `0 0 2 * * *` (Runs daily at 2 AM)
- **Function**: Checks trials ending today and downgrades to Free
- **Notification**: Sends email notification to affected users

## Security Considerations
- Plan features validated on backend
- Payment information encrypted
- Subscription status checked on every request
- Audit trail maintained

## Testing
- Unit tests for all services
- Integration tests for scheduler
- Mock payment gateway for testing
- Test data seeding script included
