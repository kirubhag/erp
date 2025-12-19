package krs.erp.service.hr;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import krs.erp.model.hr.PerformanceReview;
import krs.erp.model.hr.PerformanceReviewDetail;
import krs.erp.repository.hr.PerformanceReviewRepository;
import krs.erp.repository.hr.PerformanceReviewDetailRepository;

@Service
public class PerformanceService {

    @Autowired
    private PerformanceReviewRepository reviewRepository;

    @Autowired
    private PerformanceReviewDetailRepository detailRepository;

    public List<PerformanceReview> getStaffReviews(Long staffId) {
        return reviewRepository.findByStaffId(staffId);
    }

    public List<PerformanceReviewDetail> getReviewDetails(Long reviewId) {
        return detailRepository.findByReviewId(reviewId);
    }

    @Transactional
    public PerformanceReview submitReview(Long reviewId) {
        PerformanceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        List<PerformanceReviewDetail> details = detailRepository.findByReviewId(reviewId);
        if (!details.isEmpty()) {
            double total = details.stream()
                    .filter(d -> d.getRating() != null)
                    .mapToInt(PerformanceReviewDetail::getRating)
                    .average()
                    .orElse(0.0);
            review.setOverallRating(total);
        }

        review.setStatus(PerformanceReview.ReviewStatus.SUBMITTED);
        return reviewRepository.save(review);
    }
}
