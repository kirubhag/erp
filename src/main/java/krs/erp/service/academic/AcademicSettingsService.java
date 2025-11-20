package krs.erp.service.academic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.academic.AcademicSettings;
import krs.erp.repository.academic.AcademicSettingsRepository;

/**
 * Service for managing Academic Settings
 */
@Service
@Transactional
public class AcademicSettingsService {
    
    @Autowired
    private AcademicSettingsRepository settingsRepository;
    
    public AcademicSettings getOrCreateByOrganization(Long organizationId) {
        return settingsRepository.findByOrganizationId(organizationId)
            .orElseGet(() -> {
                AcademicSettings settings = new AcademicSettings(organizationId);
                return settingsRepository.save(settings);
            });
    }
    
    public AcademicSettings updateAttendanceSettings(Long organizationId, AcademicSettings newSettings) {
        AcademicSettings existing = getOrCreateByOrganization(organizationId);
        
        existing.setEnableAttendanceTracking(newSettings.getEnableAttendanceTracking());
        existing.setAttendanceCalculationMethod(newSettings.getAttendanceCalculationMethod());
        existing.setMinimumAttendancePercentage(newSettings.getMinimumAttendancePercentage());
        existing.setAllowLateMarking(newSettings.getAllowLateMarking());
        existing.setLateMarkingCutoffMinutes(newSettings.getLateMarkingCutoffMinutes());
        existing.setEnableBiometricIntegration(newSettings.getEnableBiometricIntegration());
        
        return settingsRepository.save(existing);
    }
    
    public AcademicSettings updateExamSettings(Long organizationId, AcademicSettings newSettings) {
        AcademicSettings existing = getOrCreateByOrganization(organizationId);
        
        existing.setDefaultExamDuration(newSettings.getDefaultExamDuration());
        existing.setAllowMakeupExams(newSettings.getAllowMakeupExams());
        existing.setMakeupExamDeadlineDays(newSettings.getMakeupExamDeadlineDays());
        existing.setPassingPercentage(newSettings.getPassingPercentage());
        existing.setEnableGradeModeration(newSettings.getEnableGradeModeration());
        existing.setAutoCalculateGrades(newSettings.getAutoCalculateGrades());
        existing.setPublishResultsImmediately(newSettings.getPublishResultsImmediately());
        
        return settingsRepository.save(existing);
    }
    
    public AcademicSettings updatePromotionSettings(Long organizationId, AcademicSettings newSettings) {
        AcademicSettings existing = getOrCreateByOrganization(organizationId);
        
        existing.setAutoPromoteStudents(newSettings.getAutoPromoteStudents());
        existing.setMinimumAttendanceForPromotion(newSettings.getMinimumAttendanceForPromotion());
        existing.setMinimumGradeForPromotion(newSettings.getMinimumGradeForPromotion());
        existing.setAllowGraceMarks(newSettings.getAllowGraceMarks());
        existing.setGraceMarksLimit(newSettings.getGraceMarksLimit());
        existing.setRequireAllSubjectsPass(newSettings.getRequireAllSubjectsPass());
        existing.setAllowCompartmentExams(newSettings.getAllowCompartmentExams());
        
        return settingsRepository.save(existing);
    }
}
