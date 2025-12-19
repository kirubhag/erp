package krs.erp.model.hr;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_performance_reviews")
@Data
@EqualsAndHashCode(callSuper = true)
public class PerformanceReview extends BaseEntity {

    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @Column(name = "cycle_id", nullable = false)
    private Long cycleId;

    @Column(name = "reviewer_id")
    private Long reviewerId;

    @Column(name = "review_date")
    private LocalDate reviewDate;

    @Column(name = "overall_rating")
    private Double overallRating;

    @Column(name = "comments")
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReviewStatus status = ReviewStatus.DRAFT;

    public enum ReviewStatus {
        DRAFT, SUBMITTED, FINALIZED
    }
}
