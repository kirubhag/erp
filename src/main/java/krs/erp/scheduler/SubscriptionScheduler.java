package krs.erp.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.entity.ErpSubscription;
import krs.erp.repository.ErpSubscriptionRepository;
import krs.erp.service.SubscriptionService;

@Component
public class SubscriptionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionScheduler.class);

    @Autowired
    private ErpSubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Value("${app.subscription.test-mode:false}")
    private boolean testMode;

    // Run every minute
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void processExpiredTrials() {
        if (!testMode) {
            // If not in test mode, we might want to run less frequently, but checking every
            // minute is fine if optimization isn't critical.
            // Or use a condition inside.
        }

        LocalDateTime now = LocalDateTime.now();

        // Find all subscriptions in TRIAL status that have expired
        // This is inefficient for large datasets, should add a custom query to repo:
        // findByStatusAndCurrentPeriodEndBefore
        // For now, assuming small scale or JPA filter.

        // Let's assume we add a method to repo or iterate all (bad practice but simple
        // start).
        // Better: use repo method.
        List<ErpSubscription> expiredTrials = subscriptionRepository.findByStatusAndCurrentPeriodEndBefore("TRIAL",
                now);

        for (ErpSubscription sub : expiredTrials) {
            try {
                // Skip Master account (Organization ID 1 usually)
                if (sub.getOrganization() != null && sub.getOrganization().getId() == 1L) {
                    continue;
                }

                logger.info("Trial expired for organization {}. Downgrading to Free.", sub.getOrganization().getId());
                subscriptionService.downgradeToFree(sub.getOrganization().getId());

            } catch (Exception e) {
                logger.error("Error processing expired trial for subscription {}", sub.getId(), e);
            }
        }
    }
}
