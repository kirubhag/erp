package krs.erp.model.lms;

import krs.erp.model.BaseEntity;
import krs.erp.model.Subject;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_lms_question_bank")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "qbank_id"))
public class LmsQuestionBank extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @NotBlank(message = "Question text is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LmsQuestion.QuestionType type;

    private String difficulty; // Easy, Medium, Hard

    private String tags; // Comma separated tags
}
