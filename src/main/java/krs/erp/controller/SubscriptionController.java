package krs.erp.controller;

import krs.erp.model.*;
import krs.erp.model.enums.BillingCycle;
import krs.erp.model.enums.PlanType;
import krs.erp.service.SubscriptionService;
import krs.erp.service.payment.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Pricing Plans and Subscriptions
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * Get all available pricing plans
     */
    @GetMapping("/plans")
    public ResponseEntity<List<PricingPlan>> getAllPlans() {
        log.info("Fetching all pricing plans");
        List<PricingPlan> plans = subscriptionService.getAllActivePlans();
        return ResponseEntity.ok(plans);
    }

    /**
     * Get current user's subscription
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentSubscription(
            @RequestParam Long userId,
            @RequestParam Long organizationId) {
        
        log.info("Fetching current subscription for user: {}, org: {}", userId, organizationId);
        
        Optional<UserSubscription> subscription = 
            subscriptionService.getCurrentSubscription(userId, organizationId);

        Map<String, Object> response = new HashMap<>();
        
        if (subscription.isPresent()) {
            UserSubscription sub = subscription.get();
            response.put("hasSubscription", true);
            response.put("subscription", sub);
            response.put("plan", sub.getPlan());
            response.put("isTrial", sub.isTrial());
            response.put("daysUntilExpiry", sub.getDaysUntilTrialExpires());
        } else {
            response.put("hasSubscription", false);
            response.put("message", "No active subscription found");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Start premium trial for new user
     */
    @PostMapping("/trial/start")
    public ResponseEntity<Map<String, Object>> startTrial(
            @RequestParam Long userId,
            @RequestParam Long organizationId,
            @RequestParam(required = false) Long createdBy) {
        
        log.info("Starting trial for user: {}, org: {}", userId, organizationId);
        
        try {
            UserSubscription subscription = subscriptionService.startPremiumTrial(
                userId, organizationId, createdBy != null ? createdBy : userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "15-day premium trial started successfully");
            response.put("subscription", subscription);
            response.put("trialEndDate", subscription.getTrialEndDate());

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.error("Failed to start trial: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Upgrade to a paid plan
     */
    @PostMapping("/upgrade")
    public ResponseEntity<Map<String, Object>> upgradePlan(
            @RequestBody UpgradeRequest request) {
        
        log.info("Upgrading plan for user: {} to: {}", request.getUserId(), request.getTargetPlan());
        
        try {
            // Create payment request
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setUserId(request.getUserId());
            paymentRequest.setOrganizationId(request.getOrganizationId());
            paymentRequest.setPaymentMethod(request.getPaymentMethod());
            paymentRequest.setCardNumber(request.getCardNumber());
            paymentRequest.setCardHolderName(request.getCardHolderName());
            paymentRequest.setCardExpiry(request.getCardExpiry());
            paymentRequest.setCardCvv(request.getCardCvv());

            // Process upgrade
            UserSubscription subscription = subscriptionService.upgradePlan(
                request.getUserId(),
                request.getOrganizationId(),
                PlanType.valueOf(request.getTargetPlan().toUpperCase()),
                BillingCycle.valueOf(request.getBillingCycle().toUpperCase()),
                paymentRequest
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Plan upgraded successfully");
            response.put("subscription", subscription);
            response.put("nextBillingDate", subscription.getNextBillingDate());

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.error("Failed to upgrade plan: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get subscription history
     */
    @GetMapping("/history")
    public ResponseEntity<List<SubscriptionHistory>> getHistory(@RequestParam Long userId) {
        log.info("Fetching subscription history for user: {}", userId);
        List<SubscriptionHistory> history = subscriptionService.getSubscriptionHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get payment history
     */
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentTransaction>> getPayments(@RequestParam Long userId) {
        log.info("Fetching payment history for user: {}", userId);
        List<PaymentTransaction> payments = subscriptionService.getPaymentHistory(userId);
        return ResponseEntity.ok(payments);
    }

    /**
     * DTO for upgrade request
     */
    public static class UpgradeRequest {
        private Long userId;
        private Long organizationId;
        private String targetPlan; // FREE, BASIC, STANDARD, PREMIUM
        private String billingCycle; // MONTHLY, YEARLY
        private String paymentMethod;
        private String cardNumber;
        private String cardHolderName;
        private String cardExpiry;
        private String cardCvv;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getOrganizationId() { return organizationId; }
        public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
        
        public String getTargetPlan() { return targetPlan; }
        public void setTargetPlan(String targetPlan) { this.targetPlan = targetPlan; }
        
        public String getBillingCycle() { return billingCycle; }
        public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public String getCardNumber() { return cardNumber; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
        
        public String getCardHolderName() { return cardHolderName; }
        public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
        
        public String getCardExpiry() { return cardExpiry; }
        public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }
        
        public String getCardCvv() { return cardCvv; }
        public void setCardCvv(String cardCvv) { this.cardCvv = cardCvv; }
    }
}
