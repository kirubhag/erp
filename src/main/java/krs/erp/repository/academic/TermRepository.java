package krs.erp.repository.academic;

import krs.erp.model.academic.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Term entity
 */
@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    
    List<Term> findByOrganizationIdOrderByStartDateAsc(Long organizationId);
    
    List<Term> findByAcademicYearIdOrderByStartDateAsc(Long academicYearId);
}
