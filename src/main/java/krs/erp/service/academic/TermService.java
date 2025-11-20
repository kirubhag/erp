package krs.erp.service.academic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.academic.Term;
import krs.erp.repository.academic.TermRepository;

/**
 * Service for managing Terms/Semesters
 */
@Service
@Transactional
public class TermService {
    
    @Autowired
    private TermRepository termRepository;
    
    public List<Term> getAllByOrganization(Long organizationId) {
        return termRepository.findByOrganizationIdOrderByStartDateAsc(organizationId);
    }
    
    public List<Term> getByAcademicYear(Long academicYearId) {
        return termRepository.findByAcademicYearIdOrderByStartDateAsc(academicYearId);
    }
    
    public Optional<Term> getById(Long id) {
        return termRepository.findById(id);
    }
    
    public Term create(Term term) {
        return termRepository.save(term);
    }
    
    public Term update(Long id, Term term) {
        Term existing = termRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Term not found"));
        
        existing.setName(term.getName());
        existing.setStartDate(term.getStartDate());
        existing.setEndDate(term.getEndDate());
        existing.setAcademicYearId(term.getAcademicYearId());
        
        return termRepository.save(existing);
    }
    
    public void delete(Long id) {
        termRepository.deleteById(id);
    }
}
