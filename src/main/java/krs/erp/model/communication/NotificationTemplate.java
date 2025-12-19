package krs.erp.model.communication;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_comm_templates")
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationTemplate extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "channel")
    private String channel; // SMS, EMAIL, PUSH

    @Column(name = "subject")
    private String subject;

    @Column(name = "content", length = 2000)
    private String content;

    @Column(name = "placeholders")
    private String placeholders; // e.g., name, date, amount
}
