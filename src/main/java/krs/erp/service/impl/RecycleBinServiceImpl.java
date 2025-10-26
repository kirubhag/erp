package krs.erp.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.Attendance;
import krs.erp.model.HealthRecord;
import krs.erp.model.Parent;
import krs.erp.model.ParentStudentRelation;
import krs.erp.model.RecycleBin;
import krs.erp.model.Student;
import krs.erp.repository.AttendanceRepository;
import krs.erp.repository.HealthRecordRepository;
import krs.erp.repository.ParentRepository;
import krs.erp.repository.ParentStudentRelationRepository;
import krs.erp.repository.RecycleBinRepository;
import krs.erp.repository.StudentRepository;
import krs.erp.service.RecycleBinService;

/**
 * Implementation of RecycleBin service with complex parent-child relationship handling
 */
@Service
@Transactional
public class RecycleBinServiceImpl implements RecycleBinService {
    
    private static final Logger logger = LoggerFactory.getLogger(RecycleBinServiceImpl.class);
    
    @Autowired
    private RecycleBinRepository recycleBinRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ParentRepository parentRepository;
    
    @Autowired
    private ParentStudentRelationRepository parentStudentRelationRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    
    @Override
    public RecycleBin addToRecycleBin(Long entityId, String entityName, EntityType entityType, 
                                     String deletedBy, String deletionReason) {
        return addToRecycleBin(entityId, entityName, entityType, deletedBy, deletionReason, null);
    }
    
    @Override
    public RecycleBin addToRecycleBin(Long entityId, String entityName, EntityType entityType, 
                                     String deletedBy, String deletionReason, String entityData) {
        RecycleBin recycleBin = new RecycleBin(entityId, entityName, entityType, deletedBy, deletionReason);
        recycleBin.setEntityData(entityData);
        
        return recycleBinRepository.save(recycleBin);
    }
    
    @Override
    @Transactional
    public void softDeleteEntity(Long entityId, EntityType entityType, String deletedBy, String deletionReason) {
        logger.info("Soft deleting entity: {} with ID: {} by user: {}", entityType, entityId, deletedBy);
        
        switch (entityType) {
            case STUDENT -> softDeleteStudent(entityId, deletedBy, deletionReason);
            case PARENT -> softDeleteParent(entityId, deletedBy, deletionReason);
            case ATTENDANCE -> softDeleteAttendance(entityId, deletedBy, deletionReason);
            case HEALTH -> softDeleteHealthRecord(entityId, deletedBy, deletionReason);
            default -> {
                // For other entity types, perform simple soft delete
                softDeleteGenericEntity(entityId, entityType, deletedBy, deletionReason);
            }
        }
    }
    
    private void softDeleteStudent(Long studentId, String deletedBy, String deletionReason) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (!studentOpt.isPresent() || studentOpt.get().isDeleted()) {
            logger.warn("Student with ID {} not found or already deleted", studentId);
            return;
        }
        
        Student student = studentOpt.get();
        String studentName = student.getFirstName() + " " + student.getLastName();
        
        // Count related entities
        int relatedCount = 0;
        
        // Soft delete related attendance records
        List<Attendance> attendanceRecords = attendanceRepository.findByStudentIdAndIsActive(studentId, 1);
        for (Attendance attendance : attendanceRecords) {
            attendance.markAsDeleted();
            attendanceRepository.save(attendance);
            addToRecycleBin(attendance.getId(), "Attendance for " + studentName, 
                           EntityType.ATTENDANCE, deletedBy, "Deleted due to student deletion");
            relatedCount++;
        }
        
        // Soft delete related health records
        List<HealthRecord> healthRecords = healthRecordRepository.findByStudentIdAndIsActive(studentId, 1);
        for (HealthRecord healthRecord : healthRecords) {
            healthRecord.markAsDeleted();
            healthRecordRepository.save(healthRecord);
            addToRecycleBin(healthRecord.getId(), "Health Record for " + studentName, 
                           EntityType.HEALTH, deletedBy, "Deleted due to student deletion");
            relatedCount++;
        }
        
        // Handle parent-student relationships
        List<ParentStudentRelation> parentRelations = parentStudentRelationRepository.findByStudentIdAndIsActive(studentId, 1);
        for (ParentStudentRelation relation : parentRelations) {
            relation.markAsDeleted();
            parentStudentRelationRepository.save(relation);
            
            // Check if parent has other active children
            Long parentId = relation.getParent().getId();
            long activeChildrenCount = parentStudentRelationRepository.countByParentIdAndIsActive(parentId, 1);
            
            // If parent has no other active children, soft delete parent too
            if (activeChildrenCount == 0) {
                Parent parent = relation.getParent();
                parent.markAsDeleted();
                parentRepository.save(parent);
                addToRecycleBin(parent.getId(), parent.getFirstName() + " " + parent.getLastName(), 
                               EntityType.PARENT, deletedBy, "Deleted due to no active children");
                relatedCount++;
            }
            relatedCount++;
        }
        
        // Soft delete the student
        student.markAsDeleted();
        studentRepository.save(student);
        
        // Add student to recycle bin
        RecycleBin recycleBin = addToRecycleBin(student.getId(), studentName, EntityType.STUDENT, 
                                               deletedBy, deletionReason);
        recycleBin.setRelatedEntityCount(relatedCount);
        recycleBinRepository.save(recycleBin);
        
        logger.info("Successfully soft deleted student {} with {} related entities", studentName, relatedCount);
    }
    
    private void softDeleteParent(Long parentId, String deletedBy, String deletionReason) {
        Optional<Parent> parentOpt = parentRepository.findById(parentId);
        if (!parentOpt.isPresent() || parentOpt.get().isDeleted()) {
            logger.warn("Parent with ID {} not found or already deleted", parentId);
            return;
        }
        
        Parent parent = parentOpt.get();
        String parentName = parent.getFirstName() + " " + parent.getLastName();
        
        // Get all active children relationships
        List<ParentStudentRelation> childRelations = parentStudentRelationRepository.findByParentIdAndIsActive(parentId, 1);
        
        int relatedCount = 0;
        
        // Soft delete all parent-student relations
        for (ParentStudentRelation relation : childRelations) {
            relation.markAsDeleted();
            parentStudentRelationRepository.save(relation);
            relatedCount++;
        }
        
        // Soft delete the parent
        parent.markAsDeleted();
        parentRepository.save(parent);
        
        // Add parent to recycle bin
        RecycleBin recycleBin = addToRecycleBin(parent.getId(), parentName, EntityType.PARENT, 
                                               deletedBy, deletionReason);
        recycleBin.setRelatedEntityCount(relatedCount);
        recycleBinRepository.save(recycleBin);
        
        logger.info("Successfully soft deleted parent {} with {} related entities", parentName, relatedCount);
    }
    
    private void softDeleteAttendance(Long attendanceId, String deletedBy, String deletionReason) {
        Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);
        if (!attendanceOpt.isPresent() || attendanceOpt.get().isDeleted()) {
            logger.warn("Attendance with ID {} not found or already deleted", attendanceId);
            return;
        }
        
        Attendance attendance = attendanceOpt.get();
        attendance.markAsDeleted();
        attendanceRepository.save(attendance);
        
        String attendanceName = "Attendance - " + attendance.getAttendanceDate();
        addToRecycleBin(attendance.getId(), attendanceName, EntityType.ATTENDANCE, deletedBy, deletionReason);
        
        logger.info("Successfully soft deleted attendance {}", attendanceName);
    }
    
    private void softDeleteHealthRecord(Long healthRecordId, String deletedBy, String deletionReason) {
        Optional<HealthRecord> healthRecordOpt = healthRecordRepository.findById(healthRecordId);
        if (!healthRecordOpt.isPresent() || healthRecordOpt.get().isDeleted()) {
            logger.warn("Health record with ID {} not found or already deleted", healthRecordId);
            return;
        }
        
        HealthRecord healthRecord = healthRecordOpt.get();
        healthRecord.markAsDeleted();
        healthRecordRepository.save(healthRecord);
        
        String healthRecordName = "Health Record - " + healthRecord.getRecordDate();
        addToRecycleBin(healthRecord.getId(), healthRecordName, EntityType.HEALTH, deletedBy, deletionReason);
        
        logger.info("Successfully soft deleted health record {}", healthRecordName);
    }
    
    private void softDeleteGenericEntity(Long entityId, EntityType entityType, String deletedBy, String deletionReason) {
        // For entities that extend BaseEntity but don't have complex relationships
        logger.info("Performing generic soft delete for entity type: {} with ID: {}", entityType, entityId);
        
        String entityName = entityType.getDisplayName() + " (ID: " + entityId + ")";
        addToRecycleBin(entityId, entityName, entityType, deletedBy, deletionReason);
        
        // Note: The actual entity soft delete should be handled by the caller
        // This method only adds the record to recycle bin
    }
    
    @Override
    @Transactional
    public boolean restoreEntity(Long recycleBinId, String restoredBy) {
        Optional<RecycleBin> recycleBinOpt = recycleBinRepository.findById(recycleBinId);
        if (!recycleBinOpt.isPresent()) {
            logger.warn("Recycle bin record with ID {} not found", recycleBinId);
            return false;
        }
        
        RecycleBin recycleBin = recycleBinOpt.get();
        return restoreEntityByIdAndType(recycleBin.getEntityId(), recycleBin.getEntityType(), restoredBy);
    }
    
    @Override
    @Transactional
    public boolean restoreEntityByIdAndType(Long entityId, EntityType entityType, String restoredBy) {
        logger.info("Restoring entity: {} with ID: {} by user: {}", entityType, entityId, restoredBy);
        
        boolean restored = switch (entityType) {
            case STUDENT -> restoreStudent(entityId, restoredBy);
            case PARENT -> restoreParent(entityId, restoredBy);
            case ATTENDANCE -> restoreAttendance(entityId, restoredBy);
            case HEALTH -> restoreHealthRecord(entityId, restoredBy);
            default -> restoreGenericEntity(entityId, entityType, restoredBy);
        };
        
        if (restored) {
            // Remove from recycle bin
            Optional<RecycleBin> recycleBinOpt = recycleBinRepository.findByEntityIdAndEntityType(entityId, entityType);
            if (recycleBinOpt.isPresent()) {
                recycleBinRepository.delete(recycleBinOpt.get());
                logger.info("Removed entity from recycle bin: {} ID: {}", entityType, entityId);
            }
        }
        
        return restored;
    }
    
    private boolean restoreStudent(Long studentId, String restoredBy) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (!studentOpt.isPresent()) {
            logger.warn("Student with ID {} not found", studentId);
            return false;
        }
        
        Student student = studentOpt.get();
        if (!student.isDeleted()) {
            logger.warn("Student with ID {} is not in deleted state", studentId);
            return false;
        }
        
        // Restore student
        student.markAsActive();
        student.setModifiedBy(restoredBy);
        studentRepository.save(student);
        
        // Restore related entities
        restoreRelatedEntitiesForStudent(studentId, restoredBy);
        
        logger.info("Successfully restored student with ID: {}", studentId);
        return true;
    }
    
    private void restoreRelatedEntitiesForStudent(Long studentId, String restoredBy) {
        // Restore attendance records
        List<Attendance> deletedAttendance = attendanceRepository.findByStudentIdAndIsActive(studentId, -1);
        for (Attendance attendance : deletedAttendance) {
            attendance.markAsActive();
            attendance.setModifiedBy(restoredBy);
            attendanceRepository.save(attendance);
            
            // Remove from recycle bin
            recycleBinRepository.findByEntityIdAndEntityType(attendance.getId(), EntityType.ATTENDANCE)
                .ifPresent(recycleBinRepository::delete);
        }
        
        // Restore health records
        List<HealthRecord> deletedHealthRecords = healthRecordRepository.findByStudentIdAndIsActive(studentId, -1);
        for (HealthRecord healthRecord : deletedHealthRecords) {
            healthRecord.markAsActive();
            healthRecord.setModifiedBy(restoredBy);
            healthRecordRepository.save(healthRecord);
            
            // Remove from recycle bin
            recycleBinRepository.findByEntityIdAndEntityType(healthRecord.getId(), EntityType.HEALTH)
                .ifPresent(recycleBinRepository::delete);
        }
        
        // Restore parent-student relationships and parents if needed
        List<ParentStudentRelation> deletedRelations = parentStudentRelationRepository.findByStudentIdAndIsActive(studentId, -1);
        for (ParentStudentRelation relation : deletedRelations) {
            // First restore parent if they are also deleted
            Parent parent = relation.getParent();
            if (parent.isDeleted()) {
                parent.markAsActive();
                parent.setModifiedBy(restoredBy);
                parentRepository.save(parent);
                
                // Remove parent from recycle bin
                recycleBinRepository.findByEntityIdAndEntityType(parent.getId(), EntityType.PARENT)
                    .ifPresent(recycleBinRepository::delete);
            }
            
            // Restore the relationship
            relation.markAsActive();
            relation.setModifiedBy(restoredBy);
            parentStudentRelationRepository.save(relation);
        }
    }
    
    private boolean restoreParent(Long parentId, String restoredBy) {
        Optional<Parent> parentOpt = parentRepository.findById(parentId);
        if (!parentOpt.isPresent()) {
            logger.warn("Parent with ID {} not found", parentId);
            return false;
        }
        
        Parent parent = parentOpt.get();
        if (!parent.isDeleted()) {
            logger.warn("Parent with ID {} is not in deleted state", parentId);
            return false;
        }
        
        // Restore parent
        parent.markAsActive();
        parent.setModifiedBy(restoredBy);
        parentRepository.save(parent);
        
        // Restore all child relationships
        List<ParentStudentRelation> deletedRelations = parentStudentRelationRepository.findByParentIdAndIsActive(parentId, -1);
        for (ParentStudentRelation relation : deletedRelations) {
            relation.markAsActive();
            relation.setModifiedBy(restoredBy);
            parentStudentRelationRepository.save(relation);
        }
        
        logger.info("Successfully restored parent with ID: {}", parentId);
        return true;
    }
    
    private boolean restoreAttendance(Long attendanceId, String restoredBy) {
        Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);
        if (!attendanceOpt.isPresent()) {
            logger.warn("Attendance with ID {} not found", attendanceId);
            return false;
        }
        
        Attendance attendance = attendanceOpt.get();
        if (!attendance.isDeleted()) {
            logger.warn("Attendance with ID {} is not in deleted state", attendanceId);
            return false;
        }
        
        attendance.markAsActive();
        attendance.setModifiedBy(restoredBy);
        attendanceRepository.save(attendance);
        
        logger.info("Successfully restored attendance with ID: {}", attendanceId);
        return true;
    }
    
    private boolean restoreHealthRecord(Long healthRecordId, String restoredBy) {
        Optional<HealthRecord> healthRecordOpt = healthRecordRepository.findById(healthRecordId);
        if (!healthRecordOpt.isPresent()) {
            logger.warn("Health record with ID {} not found", healthRecordId);
            return false;
        }
        
        HealthRecord healthRecord = healthRecordOpt.get();
        if (!healthRecord.isDeleted()) {
            logger.warn("Health record with ID {} is not in deleted state", healthRecordId);
            return false;
        }
        
        healthRecord.markAsActive();
        healthRecord.setModifiedBy(restoredBy);
        healthRecordRepository.save(healthRecord);
        
        logger.info("Successfully restored health record with ID: {}", healthRecordId);
        return true;
    }
    
    private boolean restoreGenericEntity(Long entityId, EntityType entityType, String restoredBy) {
        logger.info("Performing generic restore for entity type: {} with ID: {} by user: {}", entityType, entityId, restoredBy);
        // This method handles restoration for entities that don't have complex relationships
        // The actual entity restoration should be handled by specific service classes
        return true;
    }
    
    @Override
    public int restoreAllEntitiesByType(EntityType entityType, String restoredBy) {
        List<RecycleBin> records = recycleBinRepository.findByEntityTypeOrderByDeletedTimeDesc(entityType);
        int restoredCount = 0;
        
        for (RecycleBin record : records) {
            if (restoreEntityByIdAndType(record.getEntityId(), record.getEntityType(), restoredBy)) {
                restoredCount++;
            }
        }
        
        logger.info("Restored {} entities of type: {}", restoredCount, entityType);
        return restoredCount;
    }
    
    @Override
    public int restoreAllEntities(String restoredBy) {
        logger.info("🔄 Starting restore all entities operation by user: {}", restoredBy);
        List<RecycleBin> allRecords = recycleBinRepository.findAllByOrderByDeletedTimeDesc();
        logger.info("📋 Found {} records in recycle bin to restore", allRecords.size());
        int restoredCount = 0;
        int failedCount = 0;
        
        for (RecycleBin record : allRecords) {
            logger.info("🔄 Attempting to restore: {} (ID: {}, Type: {})", 
                       record.getEntityName(), record.getEntityId(), record.getEntityType());
            try {
                if (restoreEntityByIdAndType(record.getEntityId(), record.getEntityType(), restoredBy)) {
                    restoredCount++;
                    logger.info("✅ Successfully restored: {}", record.getEntityName());
                } else {
                    failedCount++;
                    logger.warn("❌ Failed to restore: {} (ID: {})", record.getEntityName(), record.getEntityId());
                }
            } catch (Exception e) {
                failedCount++;
                logger.error("❌ Exception while restoring: {} (ID: {})", record.getEntityName(), record.getEntityId(), e);
            }
        }
        
        logger.info("✨ Restore all completed - Restored: {}, Failed: {}, Total: {}", 
                   restoredCount, failedCount, allRecords.size());
        return restoredCount;
    }
    
    // Read-only methods implementation
    
    @Override
    @Transactional(readOnly = true)
    public List<RecycleBin> getAllRecycleBinRecords() {
        return recycleBinRepository.findAllByOrderByDeletedTimeDesc();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RecycleBin> getAllRecycleBinRecords(Pageable pageable) {
        return recycleBinRepository.findAllByOrderByDeletedTimeDesc(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RecycleBin> getRecycleBinRecordsByType(EntityType entityType) {
        return recycleBinRepository.findByEntityTypeOrderByDeletedTimeDesc(entityType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RecycleBin> getRecycleBinRecordsByType(EntityType entityType, Pageable pageable) {
        return recycleBinRepository.findByEntityTypeOrderByDeletedTimeDesc(entityType, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RecycleBin> getRecycleBinRecordsByDeletedBy(String deletedBy) {
        return recycleBinRepository.findByDeletedByOrderByDeletedTimeDesc(deletedBy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RecycleBin> getRecycleBinRecord(Long recycleBinId) {
        return recycleBinRepository.findById(recycleBinId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RecycleBin> searchRecycleBinRecords(String searchTerm) {
        return recycleBinRepository.searchByEntityName(searchTerm);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RecycleBin> searchRecycleBinRecords(String searchTerm, Pageable pageable) {
        return recycleBinRepository.searchByEntityName(searchTerm, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getRecycleBinStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", recycleBinRepository.count());
        stats.put("recordsByType", getRecycleBinStatsByEntityType());
        stats.put("oldestRecord", recycleBinRepository.findAllByOrderByDeletedTimeDesc().stream()
                .reduce((first, second) -> second).orElse(null));
        stats.put("newestRecord", recycleBinRepository.findAllByOrderByDeletedTimeDesc().stream()
                .findFirst().orElse(null));
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<EntityType, Long> getRecycleBinStatsByEntityType() {
        Map<EntityType, Long> stats = new HashMap<>();
        for (EntityType type : EntityType.values()) {
            long count = recycleBinRepository.countByEntityType(type);
            if (count > 0) {
                stats.put(type, count);
            }
        }
        return stats;
    }
    
    @Override
    @Transactional
    public boolean permanentlyDelete(Long recycleBinId) {
        Optional<RecycleBin> recycleBinOpt = recycleBinRepository.findById(recycleBinId);
        if (recycleBinOpt.isPresent()) {
            recycleBinRepository.delete(recycleBinOpt.get());
            logger.info("Permanently deleted recycle bin record with ID: {}", recycleBinId);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public int permanentlyDeleteOldRecords(LocalDateTime cutoffDate) {
        logger.info("🧹 Starting cleanup of old records before {}", cutoffDate);
        List<RecycleBin> oldRecords = recycleBinRepository.findOldRecords(cutoffDate);
        int deletedCount = oldRecords.size();
        logger.info("📊 Found {} old records to delete", deletedCount);
        
        recycleBinRepository.deleteByDeletedTimeBefore(cutoffDate);
        
        logger.info("✨ Cleanup completed - Permanently deleted {} old recycle bin records", deletedCount);
        return deletedCount;
    }
    
    @Override
    @Transactional
    public int emptyRecycleBin() {
        logger.info("🗑️ Starting to empty recycle bin...");
        long count = recycleBinRepository.count();
        logger.info("📊 Found {} records to delete", count);
        
        recycleBinRepository.deleteAll();
        
        long remainingCount = recycleBinRepository.count();
        logger.info("✨ Emptied recycle bin - Deleted: {}, Remaining: {}", count, remainingCount);
        return (int) count;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsInRecycleBin(Long entityId, EntityType entityType) {
        return recycleBinRepository.existsByEntityIdAndEntityType(entityId, entityType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRecycleBinCount() {
        return recycleBinRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRecycleBinCountByType(EntityType entityType) {
        return recycleBinRepository.countByEntityType(entityType);
    }
}