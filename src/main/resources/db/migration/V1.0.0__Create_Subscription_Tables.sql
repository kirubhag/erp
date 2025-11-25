-- =====================================================
-- ERP Pricing Plan System - Database Schema
-- =====================================================

-- Table: subscription_plans
-- Stores the available subscription plans
CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_type VARCHAR(20) NOT NULL UNIQUE,
    plan_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    billing_cycle VARCHAR(20) NOT NULL DEFAULT 'MONTHLY', -- MONTHLY, YEARLY
    max_users INT NOT NULL DEFAULT 1,
    max_storage_gb INT NOT NULL DEFAULT 1,
    features JSON, -- JSON array of features
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    trial_period_days INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plan_type (plan_type),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: user_subscriptions
-- Tracks user's current subscription
CREATE TABLE IF NOT EXISTS user_subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    subscription_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, TRIAL, EXPIRED, CANCELLED
    start_date DATE NOT NULL,
    end_date DATE,
    trial_start_date DATE,
    trial_end_date DATE,
    is_trial BOOLEAN NOT NULL DEFAULT FALSE,
    auto_renew BOOLEAN NOT NULL DEFAULT TRUE,
    next_billing_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    CONSTRAINT fk_user_subscription_user FOREIGN KEY (user_id) REFERENCES iam_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_subscription_plan FOREIGN KEY (plan_id) REFERENCES subscription_plans(id),
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_subscription_status (subscription_status),
    INDEX idx_trial_end_date (trial_end_date),
    INDEX idx_is_trial (is_trial),
    UNIQUE KEY unique_user_org_subscription (user_id, organization_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: subscription_history
-- Audit trail of all subscription changes
CREATE TABLE IF NOT EXISTS subscription_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    from_plan_id BIGINT,
    to_plan_id BIGINT NOT NULL,
    change_type VARCHAR(20) NOT NULL, -- UPGRADE, DOWNGRADE, TRIAL_START, TRIAL_END, RENEWAL, CANCELLATION
    change_reason TEXT,
    effective_date DATE NOT NULL,
    amount_charged DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT fk_subscription_history_user FOREIGN KEY (user_id) REFERENCES iam_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_subscription_history_from_plan FOREIGN KEY (from_plan_id) REFERENCES subscription_plans(id),
    CONSTRAINT fk_subscription_history_to_plan FOREIGN KEY (to_plan_id) REFERENCES subscription_plans(id),
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_change_type (change_type),
    INDEX idx_effective_date (effective_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: payment_transactions
-- Records all payment transactions (including mock payments)
CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    subscription_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    transaction_id VARCHAR(100) UNIQUE NOT NULL, -- External payment gateway transaction ID
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    payment_method VARCHAR(50), -- CREDIT_CARD, DEBIT_CARD, PAYPAL, MOCK
    payment_status VARCHAR(20) NOT NULL, -- PENDING, SUCCESS, FAILED, REFUNDED
    payment_gateway VARCHAR(50) NOT NULL DEFAULT 'MOCK', -- STRIPE, PAYPAL, RAZORPAY, MOCK
    gateway_response JSON, -- Store gateway response
    payment_date TIMESTAMP,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_transaction_user FOREIGN KEY (user_id) REFERENCES iam_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_transaction_subscription FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id),
    CONSTRAINT fk_payment_transaction_plan FOREIGN KEY (plan_id) REFERENCES subscription_plans(id),
    INDEX idx_user_id (user_id),
    INDEX idx_organization_id (organization_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Insert Default Subscription Plans
-- =====================================================

INSERT INTO subscription_plans (plan_type, plan_name, description, price, billing_cycle, max_users, max_storage_gb, features, is_active, trial_period_days) VALUES
('FREE', 'Free Plan', 'Perfect for trying out our platform', 0.00, 'MONTHLY', 1, 1, 
 JSON_ARRAY('1 User', '1GB Storage', 'Basic Features', 'Community Support', 'Email Support'),
 TRUE, 0),

('BASIC', 'Basic Plan', 'Great for small teams', 29.00, 'MONTHLY', 5, 10,
 JSON_ARRAY('5 Users', '10GB Storage', 'Standard Features', 'Email Support', 'Basic Reports', 'API Access'),
 TRUE, 0),

('STANDARD', 'Standard Plan', 'Ideal for growing businesses', 79.00, 'MONTHLY', 15, 50,
 JSON_ARRAY('15 Users', '50GB Storage', 'Advanced Features', 'Priority Support', 'Advanced Reports', 'API Access', 'Custom Integrations', 'Data Export'),
 TRUE, 0),

('PREMIUM', 'Premium Plan', 'For large enterprises', 149.00, 'MONTHLY', -1, -1,
 JSON_ARRAY('Unlimited Users', 'Unlimited Storage', 'All Features', '24/7 Phone Support', 'Advanced Reports', 'API Access', 'Custom Integrations', 'Data Export', 'Dedicated Account Manager', 'Custom Training', 'SLA Guarantee'),
 TRUE, 15);

-- =====================================================
-- Stored Procedure: Process Trial Expiration
-- =====================================================

DELIMITER //

CREATE PROCEDURE IF NOT EXISTS process_trial_expirations()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_user_id BIGINT;
    DECLARE v_subscription_id BIGINT;
    DECLARE v_organization_id BIGINT;
    DECLARE v_current_plan_id BIGINT;
    DECLARE v_free_plan_id BIGINT;
    
    -- Cursor to fetch all users whose trial ends today
    DECLARE trial_cursor CURSOR FOR 
        SELECT us.id, us.user_id, us.organization_id, us.plan_id
        FROM user_subscriptions us
        WHERE us.is_trial = TRUE
        AND us.subscription_status = 'TRIAL'
        AND DATE(us.trial_end_date) = CURDATE();
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- Get Free plan ID
    SELECT id INTO v_free_plan_id FROM subscription_plans WHERE plan_type = 'FREE' LIMIT 1;
    
    OPEN trial_cursor;
    
    read_loop: LOOP
        FETCH trial_cursor INTO v_subscription_id, v_user_id, v_organization_id, v_current_plan_id;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- Update subscription to Free plan
        UPDATE user_subscriptions
        SET plan_id = v_free_plan_id,
            subscription_status = 'ACTIVE',
            is_trial = FALSE,
            trial_end_date = NULL,
            start_date = CURDATE(),
            end_date = NULL,
            next_billing_date = NULL,
            updated_at = NOW()
        WHERE id = v_subscription_id;
        
        -- Log the change in history
        INSERT INTO subscription_history (
            user_id, organization_id, from_plan_id, to_plan_id, 
            change_type, change_reason, effective_date, amount_charged
        ) VALUES (
            v_user_id, v_organization_id, v_current_plan_id, v_free_plan_id,
            'TRIAL_END', 'Trial period expired - auto-downgraded to Free plan', CURDATE(), 0.00
        );
        
    END LOOP;
    
    CLOSE trial_cursor;
END //

DELIMITER ;

-- =====================================================
-- Views for Easy Data Access
-- =====================================================

-- View: Current Active Subscriptions
CREATE OR REPLACE VIEW v_active_subscriptions AS
SELECT 
    us.id AS subscription_id,
    us.user_id,
    u.username,
    u.email,
    us.organization_id,
    sp.plan_type,
    sp.plan_name,
    sp.price,
    us.subscription_status,
    us.is_trial,
    us.trial_end_date,
    us.start_date,
    us.end_date,
    us.next_billing_date,
    CASE 
        WHEN us.is_trial = TRUE THEN DATEDIFF(us.trial_end_date, CURDATE())
        ELSE NULL
    END AS days_until_trial_end
FROM user_subscriptions us
JOIN iam_users u ON us.user_id = u.id
JOIN subscription_plans sp ON us.plan_id = sp.id
WHERE us.subscription_status IN ('ACTIVE', 'TRIAL');

-- View: Trial Expirations This Week
CREATE OR REPLACE VIEW v_expiring_trials AS
SELECT 
    us.id AS subscription_id,
    us.user_id,
    u.username,
    u.email,
    us.organization_id,
    sp.plan_name,
    us.trial_end_date,
    DATEDIFF(us.trial_end_date, CURDATE()) AS days_remaining
FROM user_subscriptions us
JOIN iam_users u ON us.user_id = u.id
JOIN subscription_plans sp ON us.plan_id = sp.id
WHERE us.is_trial = TRUE
AND us.subscription_status = 'TRIAL'
AND us.trial_end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
ORDER BY us.trial_end_date ASC;

-- =====================================================
-- Sample Indexes for Performance
-- =====================================================

-- Add composite indexes for common queries
CREATE INDEX idx_user_org_status ON user_subscriptions(user_id, organization_id, subscription_status);
CREATE INDEX idx_trial_expiry_check ON user_subscriptions(is_trial, trial_end_date, subscription_status);

-- =====================================================
-- End of Schema
-- =====================================================
