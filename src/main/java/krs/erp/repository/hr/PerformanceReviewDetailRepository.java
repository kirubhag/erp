package krs.erp.repository.hr;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.hr.PerformanceReviewDetail;

@Repository
public interface PerformanceReviewDetailRepository extends JpaRepository<PerformanceReviewDetail, Long> {
    List<PerformanceReviewDetail> findByReviewId(Long reviewId);
}
