package krs.erp.service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import krs.erp.dto.StudentPromotionRequest;
import krs.erp.dto.StudentPromotionResponse;
import krs.erp.entity.StudentPromotionBatch;
import krs.erp.repository.StudentPromotionBatchRepository;

/**
 * Async service to process student promotions in background thread
 * This ensures UI remains responsive and progress can be tracked
 */
@Service
public class StudentPromotionSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(StudentPromotionSchedulerService.class);

    @Autowired
    private StudentPromotionService promotionService;

    @Autowired
    private StudentPromotionBatchRepository batchRepository;

    /**
     * Create batch and return ID immediately, then execute asynchronously
     * 
     * @param request Promotion request details
     * @param currentUserId User who initiated the promotion
     * @param httpRequest HTTP request for IP/user agent tracking
     * @return Batch ID for polling progress
     */
    @Transactional
    public Long createAndSchedulePromotion(
            StudentPromotionRequest request, 
            Long currentUserId, 
            HttpServletRequest httpRequest) {
        
        // Create batch record immediately
        Long batchId = promotionService.createPromotionBatch(request, currentUserId, httpRequest);
        
        // Schedule async execution
        executePromotionAsync(batchId, request, currentUserId, httpRequest);
        
        return batchId;
    }

    /**
     * Execute promotion batch asynchronously in a separate thread
     * Updates progress in database as students are processed
     * 
     * @param batchId Batch ID to process
     * @param request Promotion request details
     * @param currentUserId User who initiated the promotion
     * @param httpRequest HTTP request for IP/user agent tracking
     */
    @Async("promotionTaskExecutor")
    public void executePromotionAsync(
            Long batchId,
            StudentPromotionRequest request, 
            Long currentUserId, 
            HttpServletRequest httpRequest) {
        
        logger.info("Starting async promotion execution for batch: {}", batchId);
        
        try {
            // Execute promotion in background
            promotionService.createAndExecutePromotionBatch(request, currentUserId, httpRequest);
            
            logger.info("Async promotion completed successfully. Batch ID: {}", batchId);
            
        } catch (Exception e) {
            logger.error("Error during async promotion execution", e);
            
            // Mark batch as failed
            try {
                StudentPromotionBatch batch = batchRepository.findById(batchId).orElse(null);
                if (batch != null) {
                    batch.setStatus(StudentPromotionBatch.PromotionBatchStatus.FAILED);
                    batch.setCompletedAt(LocalDateTime.now());
                    batch.setCurrentPhase("Failed: " + e.getMessage());
                    batch.setProgressPercentage(0.0);
                    batchRepository.save(batch);
                }
            } catch (Exception ex) {
                logger.error("Error updating batch status to FAILED", ex);
            }
        }
    }

    /**
     * Get current progress of a promotion batch
     * 
     * @param batchId Batch ID to check progress
     * @return StudentPromotionResponse with current status and progress
     */
    public StudentPromotionResponse getPromotionProgress(Long batchId) {
        logger.debug("Fetching progress for batch ID: {}", batchId);
        return promotionService.getBatchDetails(batchId);
    }
}
