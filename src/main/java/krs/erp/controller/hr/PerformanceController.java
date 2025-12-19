package krs.erp.controller.hr;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import krs.erp.model.hr.PerformanceReview;
import krs.erp.model.hr.PerformanceReviewDetail;
import krs.erp.service.hr.PerformanceService;

@RestController
@RequestMapping("/api/hr/performance")
@CrossOrigin(origins = "*")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @GetMapping("/reviews/staff/{staffId}")
    public ResponseEntity<List<PerformanceReview>> getStaffReviews(@PathVariable Long staffId) {
        return ResponseEntity.ok(performanceService.getStaffReviews(staffId));
    }

    @GetMapping("/reviews/{reviewId}/details")
    public ResponseEntity<List<PerformanceReviewDetail>> getReviewDetails(@PathVariable Long reviewId) {
        return ResponseEntity.ok(performanceService.getReviewDetails(reviewId));
    }

    @PostMapping("/reviews/{reviewId}/submit")
    public ResponseEntity<PerformanceReview> submitReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(performanceService.submitReview(reviewId));
    }
}
