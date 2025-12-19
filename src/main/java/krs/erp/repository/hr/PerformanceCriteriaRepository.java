package krs.erp.repository.hr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.hr.PerformanceCriteria;

@Repository
public interface PerformanceCriteriaRepository extends JpaRepository<PerformanceCriteria, Long> {
}
