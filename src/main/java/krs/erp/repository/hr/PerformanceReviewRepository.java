package krs.erp.repository.hr;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.hr.PerformanceReview;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByStaffId(Long staffId);

    List<PerformanceReview> findByCycleId(Long cycleId);
}
