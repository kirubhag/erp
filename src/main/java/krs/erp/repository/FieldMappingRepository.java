package krs.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.entity.FieldMapping;

/**
 * Repository for FieldMapping entities
 */
@Repository
public interface FieldMappingRepository extends JpaRepository<FieldMapping, Long> {
    
    /**
     * Find field mappings by session ID
     */
    List<FieldMapping> findByImportSessionIdOrderBySourceIndex(String sessionId);
    
    /**
     * Find field mapping by session and target field
     */
    FieldMapping findByImportSessionIdAndTargetField(String sessionId, String targetField);
    
    /**
     * Delete mappings for a session
     */
    void deleteByImportSessionId(String sessionId);
}
