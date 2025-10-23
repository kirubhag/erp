package krs.erp.scheduler;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import krs.erp.service.RecycleBinService;

/**
 * Scheduled task to automatically cleanup old records from recycle bin
 * Runs daily at 2:00 AM to permanently delete records older than 30 days
 */
@Component
public class RecycleBinScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(RecycleBinScheduler.class);
    private static final int RETENTION_DAYS = 30;
    
    @Autowired
    private RecycleBinService recycleBinService;
    
    /**
     * Scheduled task that runs every day at 2:00 AM
     * Permanently deletes records from recycle bin that are older than 30 days
     */
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2:00 AM every day
    public void cleanupOldRecycleBinRecords() {
        logger.info("Starting scheduled cleanup of old recycle bin records");
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(RETENTION_DAYS);
            logger.info("Cleaning up records older than: {}", cutoffDate);
            
            int deletedCount = recycleBinService.permanentlyDeleteOldRecords(cutoffDate);
            
            if (deletedCount > 0) {
                logger.info("Successfully cleaned up {} old recycle bin records", deletedCount);
            } else {
                logger.info("No old recycle bin records found to cleanup");
            }
        } catch (Exception e) {
            logger.error("Error during scheduled cleanup of recycle bin records", e);
        }
    }
    
    /**
     * Manual cleanup method that can be called with a custom retention period
     * 
     * @param retentionDays Number of days to retain records
     * @return Number of records deleted
     */
    public int cleanupWithCustomRetention(int retentionDays) {
        logger.info("Starting manual cleanup of recycle bin records older than {} days", retentionDays);
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            int deletedCount = recycleBinService.permanentlyDeleteOldRecords(cutoffDate);
            
            logger.info("Successfully cleaned up {} recycle bin records", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            logger.error("Error during manual cleanup of recycle bin records", e);
            throw e;
        }
    }
    
    /**
     * Get the number of days records are retained in recycle bin before permanent deletion
     * 
     * @return Retention period in days
     */
    public int getRetentionDays() {
        return RETENTION_DAYS;
    }
}
