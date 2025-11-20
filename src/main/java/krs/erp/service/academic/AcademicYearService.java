package krs.erp.service.academic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.academic.AcademicYear;
import krs.erp.repository.academic.AcademicYearRepository;

/**
 * Service for managing Academic Years
 */
@Service
@Transactional
public class AcademicYearService {
    
    @Autowired
    private AcademicYearRepository academicYearRepository;
    
    public List<AcademicYear> getAllByOrganization(Long organizationId) {
        return academicYearRepository.findByOrganizationIdOrderByStartDateDesc(organizationId);
    }
    
    public Optional<AcademicYear> getById(Long id) {
        return academicYearRepository.findById(id);
    }
    
    public Optional<AcademicYear> getActiveYear(Long organizationId) {
        return academicYearRepository.findByOrganizationIdAndIsActive(organizationId, true);
    }
    
    public AcademicYear create(AcademicYear academicYear) {
        // If setting as active, deactivate all others
        if (Boolean.TRUE.equals(academicYear.getIsActive())) {
            academicYearRepository.deactivateAllForOrganization(academicYear.getOrganizationId());
        }
        return academicYearRepository.save(academicYear);
    }
    
    public AcademicYear update(Long id, AcademicYear academicYear) {
        AcademicYear existing = academicYearRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Academic year not found"));
        
        existing.setName(academicYear.getName());
        existing.setStartDate(academicYear.getStartDate());
        existing.setEndDate(academicYear.getEndDate());
        
        // If setting as active, deactivate all others
        if (Boolean.TRUE.equals(academicYear.getIsActive()) && !Boolean.TRUE.equals(existing.getIsActive())) {
            academicYearRepository.deactivateAllForOrganization(existing.getOrganizationId());
            existing.setIsActive(true);
        } else if (Boolean.FALSE.equals(academicYear.getIsActive())) {
            existing.setIsActive(false);
        }
        
        return academicYearRepository.save(existing);
    }
    
    public void activate(Long id) {
        AcademicYear year = academicYearRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Academic year not found"));
        
        // Deactivate all others for this organization
        academicYearRepository.deactivateAllForOrganization(year.getOrganizationId());
        
        // Activate this one
        year.setIsActive(true);
        academicYearRepository.save(year);
    }
    
    public void delete(Long id) {
        academicYearRepository.deleteById(id);
    }
}
