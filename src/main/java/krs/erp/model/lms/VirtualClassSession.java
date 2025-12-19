package krs.erp.model.lms;

import krs.erp.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_lms_virtual_sessions")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "session_id"))
public class VirtualClassSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @NotBlank(message = "Session title is required")
    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider; // ZOOM, GOOGLE_MEET, MS_TEAMS

    @Column(nullable = false)
    private String meetingUrl;

    private String meetingId;

    private String meetingPassword;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private String recordingUrl;

    private boolean recurring = false;

    public enum Provider {
        ZOOM, GOOGLE_MEET, MS_TEAMS
    }
}
