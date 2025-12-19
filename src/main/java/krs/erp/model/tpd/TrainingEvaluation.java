package krs.erp.model.tpd;

import krs.erp.model.BaseEntity;
import krs.erp.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_tpd_evaluations")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "evaluation_id"))
public class TrainingEvaluation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private TrainingEvent event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    private Integer reactionScore; // Kirkpatrick Level 1 (1-5)

    private Integer learningScore; // Kirkpatrick Level 2 (1-100 quiz result)

    @Column(columnDefinition = "TEXT")
    private String feedback;
}
