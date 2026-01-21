package krs.erp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.ErpAutoNumber;
import krs.erp.repository.ErpAutoNumberRepository;

/**
 * Service for generating unique auto-numbers for entity fields.
 * 
 * This service provides thread-safe, deadlock-free auto-number generation using:
 * 1. Application-level locking per entity-type/field combination
 * 2. Optimistic locking via @Version annotation in the entity
 * 3. Pessimistic database locking as a fallback
 * 4. Retry mechanism for handling optimistic lock failures
 * 
 * The design ensures:
 * - No deadlocks between concurrent transactions
 * - Unique number generation even under high concurrency
 * - Minimal database contention
 */
@Service
public class AutoNumberService {

    private static final Logger logger = LoggerFactory.getLogger(AutoNumberService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 50;

    @Autowired
    private ErpAutoNumberRepository autoNumberRepository;

    /**
     * Application-level locks per entity-type/field combination to prevent
     * multiple threads from hitting the database simultaneously for the same sequence.
     */
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    /**
     * Get or create a lock for a specific entity type and field name combination.
     */
    private ReentrantLock getLock(EntityType entityType, String fieldName) {
        String key = entityType.name() + ":" + fieldName;
        return locks.computeIfAbsent(key, k -> new ReentrantLock());
    }

    /**
     * Generate the next auto-number for a given entity type and field name.
     * This method is thread-safe and handles concurrent access gracefully.
     * 
     * @param entityType The entity type (e.g., STUDENT, STAFF)
     * @param fieldName  The field name (e.g., "autoNumber", "studentId")
     * @return The generated auto-number string, or null if not configured
     */
    public String generateNextNumber(EntityType entityType, String fieldName) {
        ReentrantLock lock = getLock(entityType, fieldName);
        lock.lock();
        try {
            return generateWithRetry(entityType, fieldName, MAX_RETRIES);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Generate auto-number with retry mechanism for optimistic lock failures.
     */
    private String generateWithRetry(EntityType entityType, String fieldName, int retriesLeft) {
        try {
            return doGenerateNextNumber(entityType, fieldName);
        } catch (ObjectOptimisticLockingFailureException e) {
            if (retriesLeft > 0) {
                logger.warn("Optimistic lock failure for {}.{}, retrying... ({} retries left)",
                        entityType, fieldName, retriesLeft);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while generating auto-number", ie);
                }
                return generateWithRetry(entityType, fieldName, retriesLeft - 1);
            } else {
                logger.error("Failed to generate auto-number after {} retries for {}.{}",
                        MAX_RETRIES, entityType, fieldName);
                throw new RuntimeException("Failed to generate auto-number due to concurrent access", e);
            }
        }
    }

    /**
     * Internal method to generate and persist the next auto-number.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    protected String doGenerateNextNumber(EntityType entityType, String fieldName) {
        Optional<ErpAutoNumber> autoNumberOpt = autoNumberRepository
                .findByEntityTypeAndFieldNameWithLock(entityType, fieldName);

        if (autoNumberOpt.isEmpty()) {
            logger.debug("No auto-number configuration found for {}.{}", entityType, fieldName);
            return null;
        }

        ErpAutoNumber autoNumber = autoNumberOpt.get();
        String generatedNumber = autoNumber.generateNextNumber();
        autoNumberRepository.save(autoNumber);

        logger.debug("Generated auto-number {} for {}.{}", generatedNumber, entityType, fieldName);
        return generatedNumber;
    }

    /**
     * Preview what the next auto-number will look like without generating it.
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @return The preview string, or null if not configured
     */
    @Transactional(readOnly = true)
    public String previewNextNumber(EntityType entityType, String fieldName) {
        return autoNumberRepository.findByEntityTypeAndFieldName(entityType, fieldName)
                .map(ErpAutoNumber::previewNextNumber)
                .orElse(null);
    }

    /**
     * Get the auto-number configuration for a given entity type and field name.
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @return Optional containing the configuration if found
     */
    @Transactional(readOnly = true)
    public Optional<ErpAutoNumber> getAutoNumberConfig(EntityType entityType, String fieldName) {
        return autoNumberRepository.findByEntityTypeAndFieldName(entityType, fieldName);
    }

    /**
     * Get all auto-number configurations for an entity type.
     * 
     * @param entityType The entity type
     * @return List of auto-number configurations
     */
    @Transactional(readOnly = true)
    public List<ErpAutoNumber> getAutoNumbersForEntity(EntityType entityType) {
        return autoNumberRepository.findByEntityType(entityType);
    }

    /**
     * Generate all auto-numbers for an entity.
     * This is useful when creating a new entity record.
     * 
     * @param entityType The entity type
     * @return Map of field name to generated auto-number value
     */
    public Map<String, String> generateAllAutoNumbersForEntity(EntityType entityType) {
        Map<String, String> generatedNumbers = new HashMap<>();
        List<ErpAutoNumber> configs = autoNumberRepository.findByEntityType(entityType);

        for (ErpAutoNumber config : configs) {
            String value = generateNextNumber(entityType, config.getFieldName());
            if (value != null) {
                generatedNumbers.put(config.getFieldName(), value);
            }
        }

        return generatedNumbers;
    }

    /**
     * Create a new auto-number configuration.
     * 
     * @param entityType    The entity type
     * @param fieldName     The field name
     * @param prefix        The prefix for the auto-number
     * @param suffix        The suffix for the auto-number
     * @param startNumber   The starting number
     * @param paddingLength The number of digits to pad with zeros
     * @return The created configuration
     */
    @Transactional
    public ErpAutoNumber createAutoNumberConfig(EntityType entityType, String fieldName,
            String prefix, String suffix, Long startNumber, Integer paddingLength) {
        
        if (autoNumberRepository.existsByEntityTypeAndFieldName(entityType, fieldName)) {
            throw new IllegalArgumentException(
                    "Auto-number configuration already exists for " + entityType + "." + fieldName);
        }

        ErpAutoNumber autoNumber = new ErpAutoNumber();
        autoNumber.setEntityType(entityType);
        autoNumber.setFieldName(fieldName);
        autoNumber.setPrefix(prefix);
        autoNumber.setSuffix(suffix);
        autoNumber.setNextNumber(startNumber != null ? startNumber : 1L);
        autoNumber.setPaddingLength(paddingLength != null ? paddingLength : 4);

        return autoNumberRepository.save(autoNumber);
    }

    /**
     * Update an existing auto-number configuration.
     * Note: This should be used carefully as changing prefix/suffix mid-stream
     * could result in inconsistent numbering.
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @param prefix     New prefix (null to keep existing)
     * @param suffix     New suffix (null to keep existing)
     * @return The updated configuration, or empty if not found
     */
    @Transactional
    public Optional<ErpAutoNumber> updateAutoNumberConfig(EntityType entityType, String fieldName,
            String prefix, String suffix) {
        
        return autoNumberRepository.findByEntityTypeAndFieldName(entityType, fieldName)
                .map(autoNumber -> {
                    if (prefix != null) {
                        autoNumber.setPrefix(prefix);
                    }
                    if (suffix != null) {
                        autoNumber.setSuffix(suffix);
                    }
                    return autoNumberRepository.save(autoNumber);
                });
    }

    /**
     * Reset the next number for a specific entity type and field.
     * WARNING: Use with extreme caution as this could cause duplicate numbers.
     * 
     * @param entityType The entity type
     * @param fieldName  The field name
     * @param newNumber  The new starting number
     * @return The updated configuration, or empty if not found
     */
    @Transactional
    public Optional<ErpAutoNumber> resetNextNumber(EntityType entityType, String fieldName, Long newNumber) {
        ReentrantLock lock = getLock(entityType, fieldName);
        lock.lock();
        try {
            return autoNumberRepository.findByEntityTypeAndFieldName(entityType, fieldName)
                    .map(autoNumber -> {
                        autoNumber.setNextNumber(newNumber);
                        return autoNumberRepository.save(autoNumber);
                    });
        } finally {
            lock.unlock();
        }
    }
}
