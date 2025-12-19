package krs.erp.model.lms;

import krs.erp.model.BaseEntity;
import krs.erp.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_lms_virtual_attendance")
@Data
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "attendance_id"))
public class VirtualAttendanceRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private VirtualClassSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime joinTime;

    private LocalDateTime leaveTime;

    private long durationMinutes;

    private boolean attended = false;
}
