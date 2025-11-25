package krs.erp.service;

import krs.erp.model.*;
import krs.erp.model.enums.BillingCycle;
import krs.erp.model.enums.PlanType;
import krs.erp.model.enums.SubscriptionStatus;
import krs.erp.repository.*;
import krs.erp.service.payment.MockPaymentGatewayService;
import krs.erp.service.payment.PaymentRequest;
import krs.erp.service.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing user subscriptions and plan changes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;
    private final PricingPlanRepository pricingPlanRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final SubscriptionHistoryRepository historyRepository;
    private final MockPaymentGatewayService paymentGatewayService;

    /**
     * Start a 15-day premium trial for new user signup
     */
    @Transactional
    public UserSubscription startPremiumTrial(Long userId, Long organizationId, Long createdBy) {
        log.info("Starting premium trial for user: {}, organization: {}", userId, organizationId);

        // Check if user already has an active subscription
        Optional<UserSubscription> existing = subscriptionRepository
                .findActiveSubscription(userId, organizationId);
        
        if (existing.isPresent()) {
            log.warn("User {} already has an active subscription", userId);
            throw new IllegalStateException("User already has an active subscription");
        }

        // Get Premium plan
        PricingPlan premiumPlan = pricingPlanRepository.findActivePlanByType(PlanType.PREMIUM)
                .orElseThrow(() -> new IllegalStateException("Premium plan not found"));

        // Create trial subscription
        UserSubscription subscription = new UserSubscription();
        subscription.setUserId(userId);
        subscription.setOrganizationId(organizationId);
        subscription.setPlanId(premiumPlan.getId());
        subscription.setSubscriptionStatus(SubscriptionStatus.TRIAL);
        subscription.setTrialStartDate(LocalDate.now());
        subscription.setTrialEndDate(LocalDate.now().plusDays(15));
        subscription.setSubscriptionStartDate(LocalDate.now());
        subscription.setPaymentStatus(krs.erp.model.enums.PaymentStatus.PAID); // Trial is "paid"
        subscription.setAmountPaid(BigDecimal.ZERO);
        subscription.setCreatedBy(createdBy);

        subscription = subscriptionRepository.save(subscription);

        // Record history
        recordSubscriptionHistory(subscription, null, premiumPlan.getId(), 
                                 "TRIAL_START", "15-day premium trial started", createdBy);

        log.info("Premium trial started successfully for user: {}", userId);
        return subscription;
    }

    /**
     * Downgrade trial to free plan when trial expires
     */
    @Transactional
    public UserSubscription downgradeTrialToFree(Long subscriptionId) {
        log.info("Downgrading trial subscription: {}", subscriptionId);

        UserSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if (!subscription.isTrial()) {
            log.warn("Subscription {} is not a trial", subscriptionId);
            throw new IllegalStateException("Subscription is not a trial");
        }

        // Get Free plan
        PricingPlan freePlan = pricingPlanRepository.findActivePlanByType(PlanType.FREE)
                .orElseThrow(() -> new IllegalStateException("Free plan not found"));

        Long previousPlanId = subscription.getPlanId();

        // Update subscription to Free plan
        subscription.setPlanId(freePlan.getId());
        subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        subscription.setTrialStartDate(null);
        subscription.setTrialEndDate(null);
        subscription.setNotes("Downgraded from premium trial to free plan");

        subscription = subscriptionRepository.save(subscription);

        // Record history
        recordSubscriptionHistory(subscription, previousPlanId, freePlan.getId(), 
                                 "TRIAL_END", "Trial expired, downgraded to free plan", null);

        log.info("Trial downgraded to free plan for subscription: {}", subscriptionId);
        return subscription;
    }

    /**
     * Upgrade user to a paid plan
     */
    @Transactional
    public UserSubscription upgradePlan(Long userId, Long organizationId, 
                                       PlanType targetPlanType, BillingCycle billingCycle,
                                       PaymentRequest paymentRequest) {
        log.info("Upgrading user: {} to plan: {}", userId, targetPlanType);

        // Get current subscription
        Optional<UserSubscription> currentSubOpt = subscriptionRepository
                .findActiveSubscription(userId, organizationId);

        // Get target plan
        PricingPlan targetPlan = pricingPlanRepository.findActivePlanByType(targetPlanType)
                .orElseThrow(() -> new IllegalArgumentException("Target plan not found"));

        // Calculate amount
        BigDecimal amount = billingCycle == BillingCycle.YEARLY ? 
                           targetPlan.getPriceYearly() : targetPlan.getPriceMonthly();

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            // Process payment
            paymentRequest.setAmount(amount);
            paymentRequest.setCurrency("USD");
            PaymentResponse paymentResponse = paymentGatewayService.processPayment(paymentRequest);

            if (!paymentResponse.isSuccess()) {
                // Record failed transaction
                recordFailedTransaction(paymentRequest, paymentResponse);
                throw new IllegalStateException("Payment failed: " + paymentResponse.getMessage());
            }

            // Record successful transaction
            PaymentTransaction transaction = recordSuccessfulTransaction(paymentRequest, paymentResponse);

            // Create or update subscription
            UserSubscription subscription;
            Long previousPlanId = null;

            if (currentSubOpt.isPresent()) {
                subscription = currentSubOpt.get();
                previousPlanId = subscription.getPlanId();
                
                // Cancel trial if exists
                if (subscription.isTrial()) {
                    subscription.setTrialStartDate(null);
                    subscription.setTrialEndDate(null);
                }
            } else {
                subscription = new UserSubscription();
                subscription.setUserId(userId);
                subscription.setOrganizationId(organizationId);
                subscription.setSubscriptionStartDate(LocalDate.now());
            }

            subscription.setPlanId(targetPlan.getId());
            subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            subscription.setBillingCycle(billingCycle);
            subscription.setPaymentStatus(krs.erp.model.enums.PaymentStatus.PAID);
            subscription.setAmountPaid(amount);
            
            // Set next billing date
            LocalDate nextBilling = billingCycle == BillingCycle.YEARLY ?
                                   LocalDate.now().plusYears(1) : LocalDate.now().plusMonths(1);
            subscription.setNextBillingDate(nextBilling);
            subscription.setSubscriptionEndDate(nextBilling);

            subscription = subscriptionRepository.save(subscription);

            // Record history
            String changeType = previousPlanId == null ? "UPGRADE" : 
                              (targetPlan.getPriceMonthly().compareTo(BigDecimal.ZERO) > 0 ? "UPGRADE" : "DOWNGRADE");
            recordSubscriptionHistory(subscription, previousPlanId, targetPlan.getId(), 
                                     changeType, "Plan upgraded via payment", userId);

            log.info("Plan upgraded successfully for user: {}", userId);
            return subscription;
        }

        throw new IllegalStateException("Cannot upgrade to free plan");
    }

    /**
     * Get all available pricing plans
     */
    public List<PricingPlan> getAllActivePlans() {
        return pricingPlanRepository.findAllActivePlansOrderedByPrice();
    }

    /**
     * Get user's current subscription
     */
    public Optional<UserSubscription> getCurrentSubscription(Long userId, Long organizationId) {
        return subscriptionRepository.findActiveSubscription(userId, organizationId);
    }

    /**
     * Get subscription history for user
     */
    public List<SubscriptionHistory> getSubscriptionHistory(Long userId) {
        return historyRepository.findUserHistoryOrderedByDate(userId);
    }

    /**
     * Get payment history for user
     */
    public List<PaymentTransaction> getPaymentHistory(Long userId) {
        return transactionRepository.findUserTransactionsOrderedByDate(userId);
    }

    // Helper methods

    private void recordSubscriptionHistory(UserSubscription subscription, Long previousPlanId, 
                                          Long newPlanId, String changeType, String reason, Long changedBy) {
        SubscriptionHistory history = new SubscriptionHistory();
        history.setSubscriptionId(subscription.getId());
        history.setUserId(subscription.getUserId());
        history.setOrganizationId(subscription.getOrganizationId());
        history.setPreviousPlanId(previousPlanId);
        history.setNewPlanId(newPlanId);
        history.setChangeType(changeType);
        history.setChangeReason(reason);
        history.setEffectiveDate(LocalDate.now());
        history.setChangedBy(changedBy);
        
        historyRepository.save(history);
    }

    private PaymentTransaction recordSuccessfulTransaction(PaymentRequest request, PaymentResponse response) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setSubscriptionId(request.getSubscriptionId());
        transaction.setUserId(request.getUserId());
        transaction.setOrganizationId(request.getOrganizationId());
        transaction.setTransactionId(response.getTransactionId());
        transaction.setTransactionType("SUBSCRIPTION");
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionStatus("SUCCESS");
        transaction.setPaymentGateway("MOCK_GATEWAY");
        transaction.setGatewayResponse(response.getGatewayResponse());
        transaction.setProcessedAt(LocalDateTime.now());
        
        return transactionRepository.save(transaction);
    }

    private void recordFailedTransaction(PaymentRequest request, PaymentResponse response) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setSubscriptionId(request.getSubscriptionId());
        transaction.setUserId(request.getUserId());
        transaction.setOrganizationId(request.getOrganizationId());
        transaction.setTransactionId(response.getTransactionId());
        transaction.setTransactionType("SUBSCRIPTION");
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionStatus("FAILED");
        transaction.setPaymentGateway("MOCK_GATEWAY");
        transaction.setGatewayResponse(response.getGatewayResponse());
        transaction.setErrorMessage(response.getMessage());
        
        transactionRepository.save(transaction);
    }
}
