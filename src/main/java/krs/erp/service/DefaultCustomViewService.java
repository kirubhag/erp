package krs.erp.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;
import krs.erp.repository.CustomViewRepository;

/**
 * Service responsible for creating default custom views for all entity types
 */
@Service
public class DefaultCustomViewService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCustomViewService.class);

    @Autowired
    private CustomViewRepository customViewRepository;

    @Transactional
    public void initializeDefaultViews() {
        logger.info("🔧 Starting default custom views initialization...");
        
        // Define default fields for each entity type
        Map<EntityType, List<String>> defaultFields = getDefaultFieldsMap();
        
        int createdCount = 0;
        int existingCount = 0;
        
        for (EntityType entityType : EntityType.values()) {
            // Check if default view already exists for this entity type
            if (customViewRepository.findByEntityTypeAndIsDefaultTrue(entityType).isPresent()) {
                logger.debug("✓ Default view already exists for {}", entityType.getDisplayName());
                existingCount++;
                continue;
            }
            
            // Create default view for this entity type
            CustomView defaultView = createDefaultView(entityType, defaultFields.get(entityType));
            customViewRepository.save(defaultView);
            
            logger.info("✓ Created default view for {}: {}", 
                entityType.getDisplayName(), defaultView.getViewName());
            createdCount++;
        }
        
        logger.info("🎉 Default views initialization completed!");
        logger.info("   - Created: {} new default views", createdCount);
        logger.info("   - Existing: {} default views", existingCount);
        logger.info("   - Total: {} entity types covered", EntityType.values().length);
    }

    private CustomView createDefaultView(EntityType entityType, List<String> fields) {
        CustomView view = new CustomView();
        view.setViewName("Default " + entityType.getDisplayName() + " View");
        view.setDescription("Default view showing essential " + entityType.getDisplayName().toLowerCase() + " information");
        view.setEntityType(entityType);
        view.setSelectedFields(fields);
        view.setIsDefault(true);
        view.setIsPublic(true);
        view.setCreatedByUser("system");
        return view;
    }

    private Map<EntityType, List<String>> getDefaultFieldsMap() {
        Map<EntityType, List<String>> defaultFields = new HashMap<>();
        
        // Student default fields
        defaultFields.put(EntityType.STUDENT, Arrays.asList(
            "id", "firstName", "lastName", "email", "phoneNumber", 
            "dateOfBirth", "grade", "enrollmentDate", "status"
        ));
        
        // Parent default fields
        defaultFields.put(EntityType.PARENT, Arrays.asList(
            "id", "firstName", "lastName", "email", "phoneNumber", 
            "relationship", "address", "emergencyContact"
        ));
        
        // Teacher default fields
        defaultFields.put(EntityType.TEACHER, Arrays.asList(
            "id", "firstName", "lastName", "email", "phoneNumber", 
            "subject", "department", "hireDate", "qualification"
        ));
        
        // User default fields
        defaultFields.put(EntityType.USER, Arrays.asList(
            "id", "username", "email", "firstName", "lastName", 
            "role", "isActive", "lastLogin", "createdTime"
        ));
        
        // Course default fields
        defaultFields.put(EntityType.COURSE, Arrays.asList(
            "id", "courseName", "courseCode", "description", 
            "credits", "department", "teacher", "semester"
        ));
        
        // Attendance default fields
        defaultFields.put(EntityType.ATTENDANCE, Arrays.asList(
            "id", "studentId", "studentName", "date", "status", 
            "courseId", "courseName", "remarks"
        ));
        
        // Grade default fields
        defaultFields.put(EntityType.GRADE, Arrays.asList(
            "id", "studentId", "studentName", "courseId", "courseName", 
            "gradeValue", "gradePoints", "semester", "academicYear"
        ));
        
        // Assignment default fields
        defaultFields.put(EntityType.ASSIGNMENT, Arrays.asList(
            "id", "title", "description", "courseId", "courseName", 
            "dueDate", "maxPoints", "status", "createdDate"
        ));
        
        // Exam default fields
        defaultFields.put(EntityType.EXAM, Arrays.asList(
            "id", "examName", "courseId", "courseName", "examDate", 
            "duration", "maxMarks", "examType", "venue"
        ));
        
        // Health default fields
        defaultFields.put(EntityType.HEALTH, Arrays.asList(
            "id", "studentId", "studentName", "recordDate", "height", 
            "weight", "bloodGroup", "allergies", "medications"
        ));
        
        return defaultFields;
    }

    /**
     * Reset and recreate all default views (useful for updates)
     */
    @Transactional
    public void resetDefaultViews() {
        logger.info("🔄 Resetting all default views...");
        
        // Delete existing default views
        List<CustomView> defaultViews = customViewRepository.findByIsDefaultTrue();
        customViewRepository.deleteAll(defaultViews);
        logger.info("🗑️ Deleted {} existing default views", defaultViews.size());
        
        // Recreate all default views
        initializeDefaultViews();
    }

    /**
     * Get all default views grouped by entity type
     */
    public Map<EntityType, CustomView> getAllDefaultViews() {
        Map<EntityType, CustomView> defaultViews = new HashMap<>();
        
        for (EntityType entityType : EntityType.values()) {
            customViewRepository.findByEntityTypeAndIsDefaultTrue(entityType)
                .ifPresent(view -> defaultViews.put(entityType, view));
        }
        
        return defaultViews;
    }
}