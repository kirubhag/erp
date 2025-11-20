package krs.erp.service.academic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.academic.GradingScale;
import krs.erp.repository.academic.GradingScaleRepository;

/**
 * Service for managing Grading Scales
 */
@Service
@Transactional
public class GradingScaleService {
    
    @Autowired
    private GradingScaleRepository gradingScaleRepository;
    
    public List<GradingScale> getAllByOrganization(Long organizationId) {
        return gradingScaleRepository.findByOrganizationIdOrderByMinPercentageDesc(organizationId);
    }
    
    public Optional<GradingScale> getById(Long id) {
        return gradingScaleRepository.findById(id);
    }
    
    public GradingScale create(GradingScale gradingScale) {
        return gradingScaleRepository.save(gradingScale);
    }
    
    public GradingScale update(Long id, GradingScale gradingScale) {
        GradingScale existing = gradingScaleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Grading scale not found"));
        
        existing.setName(gradingScale.getName());
        existing.setLetterGrade(gradingScale.getLetterGrade());
        existing.setMinPercentage(gradingScale.getMinPercentage());
        existing.setMaxPercentage(gradingScale.getMaxPercentage());
        existing.setGradePoint(gradingScale.getGradePoint());
        
        return gradingScaleRepository.save(existing);
    }
    
    public void delete(Long id) {
        gradingScaleRepository.deleteById(id);
    }
}
