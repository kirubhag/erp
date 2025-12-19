package krs.erp.model.hr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_performance_review_details")
@Data
@EqualsAndHashCode(callSuper = true)
public class PerformanceReviewDetail extends BaseEntity {

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "criteria_id", nullable = false)
    private Long criteriaId;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comments")
    private String comments;
}
