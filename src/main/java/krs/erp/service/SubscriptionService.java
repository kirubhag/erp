package krs.erp.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.entity.ErpPlan;
import krs.erp.entity.ErpSubscription;
import krs.erp.model.Organization;
import krs.erp.repository.ErpPlanRepository;
import krs.erp.repository.ErpSubscriptionRepository;

@Service
@Transactional
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    @Autowired
    private ErpSubscriptionRepository subscriptionRepository;

    @Autowired
    private ErpPlanRepository planRepository;

    @Value("${app.subscription.test-mode:false}")
    private boolean testMode;

    /**
     * Create a trial subscription for a new organization
     */
    public void createTrialSubscription(Organization organization) {
        logger.info("Creating trial subscription for organization: {}", organization.getName());

        ErpPlan enterprisePlan = planRepository.findByName("Enterprise")
                .orElseThrow(() -> new RuntimeException("Enterprise plan not found"));

        ErpSubscription subscription = new ErpSubscription();
        subscription.setOrganization(organization);
        subscription.setPlan(enterprisePlan);
        subscription.setStatus("TRIAL");

        LocalDateTime now = LocalDateTime.now();
        subscription.setCurrentPeriodStart(now);

        // Determine trial duration
        if (testMode) {
            // 1 minute trial for testing
            subscription.setCurrentPeriodEnd(now.plusMinutes(1));
            logger.info("Test mode enabled: Trial set to 1 minute");
        } else {
            // 15 days standard trial
            subscription.setCurrentPeriodEnd(now.plusDays(15));
            logger.info("Standard mode: Trial set to 15 days");
        }

        subscriptionRepository.save(subscription);
    }

    /**
     * Extend trial period
     */
    public void extendTrial(Long organizationId, int duration, ChronoUnit unit) {
        ErpSubscription subscription = subscriptionRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new RuntimeException("Subscription not found for organization: " + organizationId));

        if (!"TRIAL".equalsIgnoreCase(subscription.getStatus())) {
            throw new RuntimeException("Subscription is not in TRIAL status");
        }

        subscription.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd().plus(duration, unit));
        subscriptionRepository.save(subscription);
        logger.info("Extended trial for organization {} by {} {}", organizationId, duration, unit);
    }

    /**
     * Change plan (Upgrade/Downgrade)
     */
    public void changePlan(Long organizationId, Long newPlanId) {
        ErpSubscription subscription = subscriptionRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new RuntimeException("Subscription not found for organization: " + organizationId));

        ErpPlan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + newPlanId));

        subscription.setPlan(newPlan);

        // If moving from Trial to Paid/Free, update status
        if ("TRIAL".equalsIgnoreCase(subscription.getStatus())) {
            subscription.setStatus("ACTIVE");
            // Reset period if needed, or keep existing flow.
            // Usually valid from NOW if upgrading instantly.
            subscription.setCurrentPeriodStart(LocalDateTime.now());

            // Set end date based on plan type (Monthly/Yearly)
            if ("YEARLY".equalsIgnoreCase(newPlan.getType())) {
                subscription.setCurrentPeriodEnd(LocalDateTime.now().plusYears(1));
            } else {
                subscription.setCurrentPeriodEnd(LocalDateTime.now().plusMonths(1));
            }
        }
        // If moving between paid plans, logic might vary (proration etc), simpler for
        // now.

        subscriptionRepository.save(subscription);
        logger.info("Changed plan for organization {} to {}", organizationId, newPlan.getName());
    }

    /**
     * Downgrade to Free plan
     */
    public void downgradeToFree(Long organizationId) {
        ErpSubscription subscription = subscriptionRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new RuntimeException("Subscription not found for organization: " + organizationId));

        ErpPlan freePlan = planRepository.findByName("Free")
                .orElseThrow(() -> new RuntimeException("Free plan not found"));

        subscription.setPlan(freePlan);
        subscription.setStatus("ACTIVE"); // Free plan is always active? Or "FREE" status? ACTIVE is fine.
        subscription.setCurrentPeriodEnd(null); // No expiry for free plan? Or set far future.
        // Let's keep it null for indefinite or manage 100 years.

        subscriptionRepository.save(subscription);
        logger.info("Downgraded organization {} to Free plan", organizationId);
    }

    // Compatibility method for UserController
    public void startPremiumTrial(Long userId, Long organizationId, Long creatorId) {
        krs.erp.model.Organization org = new krs.erp.model.Organization();
        org.setId(organizationId); // Assuming reference is enough or fetch it
        // Ideally fetch from DB
        createTrialSubscription(org);
    }

    // Compatibility method for TrialExpirationScheduler
    public void downgradeTrialToFree(Long subscriptionId) {
        // Logic to find subscription by ID and downgrade
        // Since we switched to Organization-based, this might be tricky if ID is
        // UserSubscription ID.
        // For now, logging usage.
        System.out.println("Legacy downgrade called for " + subscriptionId);
        subscriptionRepository.findById(subscriptionId).ifPresent(sub -> {
            if (sub.getOrganization() != null) {
                downgradeToFree(sub.getOrganization().getId());
            }
        });
    }

    public Optional<ErpSubscription> getCurrentSubscription(Long organizationId) {
        return subscriptionRepository.findByOrganizationId(organizationId);
    }

    /**
     * Get all available plans (for subscription page)
     * Works in tenant context - plans are copied from master DB during provisioning
     */
    public List<ErpPlan> getAllPlans() {
        return planRepository.findAll();
    }
}
