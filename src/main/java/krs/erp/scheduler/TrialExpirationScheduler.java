package krs.erp.scheduler;

import krs.erp.model.UserSubscription;
import krs.erp.repository.UserSubscriptionRepository;
import krs.erp.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled job to handle trial expirations
 * Runs daily at 1:00 AM to check and downgrade expired trials
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TrialExpirationScheduler {

    private final SubscriptionService subscriptionService;
    private final UserSubscriptionRepository subscriptionRepository;

    /**
     * Check and downgrade expired trials
     * Runs every day at 1:00 AM
     */
    @Scheduled(cron = "0 0 1 * * *") // 1:00 AM daily
    public void processExpiredTrials() {
        log.info("Starting trial expiration check...");

        try {
            LocalDate today = LocalDate.now();
            
            // Find all trials expiring today
            List<UserSubscription> expiringTrials = subscriptionRepository.findTrialsExpiringOnDate(today);
            
            log.info("Found {} trials expiring today", expiringTrials.size());

            int successCount = 0;
            int failureCount = 0;

            for (UserSubscription subscription : expiringTrials) {
                try {
                    log.info("Processing expired trial for subscription: {}, user: {}", 
                            subscription.getId(), subscription.getUserId());
                    
                    subscriptionService.downgradeTrialToFree(subscription.getId());
                    successCount++;
                    
                } catch (Exception e) {
                    log.error("Failed to downgrade trial for subscription: {}", 
                             subscription.getId(), e);
                    failureCount++;
                }
            }

            log.info("Trial expiration check completed. Success: {}, Failed: {}", 
                    successCount, failureCount);

        } catch (Exception e) {
            log.error("Error during trial expiration check", e);
        }
    }

    /**
     * Send trial expiration warnings
     * Runs every day at 9:00 AM to notify users whose trial expires in 3 days
     */
    @Scheduled(cron = "0 0 9 * * *") // 9:00 AM daily
    public void sendTrialExpirationWarnings() {
        log.info("Checking for upcoming trial expirations...");

        try {
            LocalDate threeDaysFromNow = LocalDate.now().plusDays(3);
            List<UserSubscription> upcomingExpirations = 
                subscriptionRepository.findTrialsExpiringOnDate(threeDaysFromNow);

            log.info("Found {} trials expiring in 3 days", upcomingExpirations.size());

            for (UserSubscription subscription : upcomingExpirations) {
                try {
                    // TODO: Send email notification to user
                    log.info("Trial expiring soon for user: {}, expires on: {}", 
                            subscription.getUserId(), subscription.getTrialEndDate());
                    
                    // In a real implementation, you would call an email service here
                    // emailService.sendTrialExpirationWarning(subscription);
                    
                } catch (Exception e) {
                    log.error("Failed to send warning for subscription: {}", 
                             subscription.getId(), e);
                }
            }

        } catch (Exception e) {
            log.error("Error sending trial expiration warnings", e);
        }
    }

    /**
     * Clean up very old expired trials (optional maintenance task)
     * Runs once a week on Sunday at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * SUN") // 2:00 AM every Sunday
    public void cleanupOldExpiredTrials() {
        log.info("Cleaning up old expired trials...");

        try {
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            List<UserSubscription> oldExpired = subscriptionRepository.findExpiredTrials(thirtyDaysAgo);

            log.info("Found {} old expired trials to review", oldExpired.size());

            // In a real implementation, you might archive these or send final notifications
            
        } catch (Exception e) {
            log.error("Error during old trial cleanup", e);
        }
    }
}
