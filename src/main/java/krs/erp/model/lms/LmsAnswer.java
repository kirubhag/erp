package krs.erp.model.lms;

import krs.erp.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_lms_answers")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "answer_id"))
public class LmsAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private LmsQuestion question;

    @NotBlank(message = "Answer text is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String answerText;

    private boolean correct = false;
}
