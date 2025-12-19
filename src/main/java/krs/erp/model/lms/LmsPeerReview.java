package krs.erp.model.lms;

import krs.erp.model.BaseEntity;
import krs.erp.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_lms_peer_reviews")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "review_id"))
public class LmsPeerReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private LmsSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    private Integer score; // 1-100 or 1-5

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private boolean isAnonymous = true;

    private LocalDateTime reviewedAt = LocalDateTime.now();
}
