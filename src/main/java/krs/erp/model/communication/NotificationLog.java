package krs.erp.model.communication;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_comm_logs")
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationLog extends BaseEntity {

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "recipient_type")
    private String recipientType; // STUDENT, STAFF, PARENT

    @Column(name = "channel")
    private String channel; // SMS, EMAIL, PUSH

    @Column(name = "subject")
    private String subject;

    @Column(name = "content", length = 2000)
    private String content;

    @Column(name = "status")
    private String status; // SENT, FAILED, PENDING

    @Column(name = "sent_at")
    private LocalDateTime sentAt = LocalDateTime.now();
}
