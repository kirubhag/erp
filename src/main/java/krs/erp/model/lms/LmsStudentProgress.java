package krs.erp.model.lms;

import krs.erp.model.BaseEntity;
import krs.erp.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_lms_student_progress")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "progress_id"))
public class LmsStudentProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    private Double completionPercentage = 0.0;

    private boolean completed = false;

    private LocalDateTime completionDate;

    private Integer timeSpentMinutes = 0;

    private LocalDateTime lastAccessed = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String notes;
}
