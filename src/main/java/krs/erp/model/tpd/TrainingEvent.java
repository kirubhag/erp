package krs.erp.model.tpd;

import krs.erp.model.BaseEntity;
import krs.erp.model.Room;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_tpd_training_events")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "event_id"))
public class TrainingEvent extends BaseEntity {

    @NotBlank(message = "Event title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type; // INTERNAL, EXTERNAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Room venue;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private String resourcePerson;

    private Double cost = 0.0;

    private Integer maxParticipants;

    public enum EventType {
        INTERNAL, EXTERNAL
    }
}
