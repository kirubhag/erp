package krs.erp.repository.lms;

import krs.erp.model.lms.LmsPeerReview;
import krs.erp.model.lms.LmsSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsPeerReviewRepository extends JpaRepository<LmsPeerReview, Long> {
    List<LmsPeerReview> findBySubmission(LmsSubmission submission);
}
