-- Pricing Plan Tables Migration
-- Version: 1.0.4
-- Date: 2025-11-25

-- Create pricing_plans table
CREATE TABLE IF NOT EXISTS pricing_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(50) NOT NULL UNIQUE,
    plan_type VARCHAR(20) NOT NULL COMMENT 'FREE, BASIC, STANDARD, PREMIUM',
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    price_monthly DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    price_yearly DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    max_users INT DEFAULT NULL COMMENT 'NULL means unlimited',
    max_storage_gb INT DEFAULT NULL COMMENT 'NULL means unlimited',
    features JSON COMMENT 'List of features included in this plan',
    is_active BOOLEAN DEFAULT TRUE,
    is_trial_eligible BOOLEAN DEFAULT FALSE,
    trial_days INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plan_type (plan_type),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Master table for pricing plans';

-- Create user_subscriptions table
CREATE TABLE IF NOT EXISTS user_subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    subscription_status VARCHAR(20) NOT NULL COMMENT 'ACTIVE, TRIAL, EXPIRED, CANCELLED, SUSPENDED',
    billing_cycle VARCHAR(20) COMMENT 'MONTHLY, YEARLY, LIFETIME',
    trial_start_date DATE,
    trial_end_date DATE,
    subscription_start_date DATE NOT NULL,
    subscription_end_date DATE,
    next_billing_date DATE,
    is_auto_renew BOOLEAN DEFAULT TRUE,
    payment_status VARCHAR(20) COMMENT 'PENDING, PAID, FAILED, REFUNDED',
    amount_paid DECIMAL(10, 2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    FOREIGN KEY (user_id) REFERENCES iam_users(id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES pricing_plans(id),
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_subscription_status (subscription_status),
    INDEX idx_trial_end_date (trial_end_date),
    INDEX idx_next_billing_date (next_billing_date),
    UNIQUE KEY unique_active_subscription (user_id, organization_id, subscription_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User subscription records';

-- Create payment_transactions table
CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    transaction_id VARCHAR(100) UNIQUE NOT NULL COMMENT 'External payment gateway transaction ID',
    transaction_type VARCHAR(20) NOT NULL COMMENT 'SUBSCRIPTION, UPGRADE, DOWNGRADE, REFUND',
    payment_method VARCHAR(50) COMMENT 'CREDIT_CARD, DEBIT_CARD, PAYPAL, STRIPE, etc',
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    transaction_status VARCHAR(20) NOT NULL COMMENT 'PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED',
    payment_gateway VARCHAR(50) COMMENT 'STRIPE, PAYPAL, RAZORPAY, etc',
    gateway_response JSON COMMENT 'Full response from payment gateway',
    error_message TEXT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    refunded_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES iam_users(id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    INDEX idx_subscription_id (subscription_id),
    INDEX idx_transaction_status (transaction_status),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Payment transaction records';

-- Create subscription_history table for audit trail
CREATE TABLE IF NOT EXISTS subscription_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    previous_plan_id BIGINT,
    new_plan_id BIGINT NOT NULL,
    change_type VARCHAR(20) NOT NULL COMMENT 'UPGRADE, DOWNGRADE, TRIAL_START, TRIAL_END, CANCELLATION, RENEWAL',
    change_reason TEXT,
    effective_date DATE NOT NULL,
    changed_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES iam_users(id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    FOREIGN KEY (previous_plan_id) REFERENCES pricing_plans(id),
    FOREIGN KEY (new_plan_id) REFERENCES pricing_plans(id),
    INDEX idx_subscription_id (subscription_id),
    INDEX idx_user_id (user_id),
    INDEX idx_change_type (change_type),
    INDEX idx_effective_date (effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Subscription change history for auditing';

-- Insert default pricing plans (Prices in Indian Rupees)
INSERT INTO pricing_plans (plan_name, plan_type, display_name, description, price_monthly, price_yearly, max_users, max_storage_gb, features, is_active, is_trial_eligible, trial_days) VALUES
('free', 'FREE', 'Free Plan', 'Perfect for getting started with basic features', 0.00, 0.00, 5, 1, 
 JSON_ARRAY('5 Users', '1 GB Storage', 'Basic Reports', 'Email Support', 'Core Modules Access'), 
 TRUE, FALSE, 0),

('basic', 'BASIC', 'Basic Plan', 'Great for small teams looking for more features', 3500.00, 35000.00, 25, 10, 
 JSON_ARRAY('25 Users', '10 GB Storage', 'Advanced Reports', 'Priority Email Support', 'All Core Modules', 'Custom Fields', 'API Access'), 
 TRUE, TRUE, 15),

('standard', 'STANDARD', 'Standard Plan', 'Ideal for growing businesses with advanced needs', 5000.00, 50000.00, 100, 50, 
 JSON_ARRAY('100 Users', '50 GB Storage', 'Premium Reports & Analytics', '24/7 Support', 'All Premium Modules', 'Advanced Customization', 'API Access', 'Webhooks', 'SSO Integration', 'Data Export'), 
 TRUE, TRUE, 15),

('premium', 'PREMIUM', 'Premium Plan', 'Complete solution for large enterprises', 7500.00, 75000.00, NULL, NULL, 
 JSON_ARRAY('Unlimited Users', 'Unlimited Storage', 'Enterprise Reports & BI', 'Dedicated Account Manager', 'All Enterprise Modules', 'White Label', 'Advanced API Access', 'Custom Integrations', 'SSO & SAML', 'Data Export & Import', 'Custom Training', 'SLA Guarantee'), 
 TRUE, TRUE, 15);

-- Add plan_type column to organizations table if not exists
ALTER TABLE organizations 
ADD COLUMN IF NOT EXISTS current_plan_id BIGINT DEFAULT NULL,
ADD COLUMN IF NOT EXISTS subscription_status VARCHAR(20) DEFAULT 'FREE',
ADD CONSTRAINT fk_org_plan FOREIGN KEY (current_plan_id) REFERENCES pricing_plans(id);

-- Update existing organizations to Free plan
UPDATE organizations SET current_plan_id = (SELECT id FROM pricing_plans WHERE plan_type = 'FREE' LIMIT 1) WHERE current_plan_id IS NULL;
