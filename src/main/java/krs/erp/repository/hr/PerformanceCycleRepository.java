package krs.erp.repository.hr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.hr.PerformanceCycle;

@Repository
public interface PerformanceCycleRepository extends JpaRepository<PerformanceCycle, Long> {
}
