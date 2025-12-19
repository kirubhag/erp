package krs.erp.model.communication;

import jakarta.persistence.*;
import krs.erp.model.BaseEntity;
import krs.erp.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "erp_announcements")
@EqualsAndHashCode(callSuper = true)
public class Announcement extends BaseEntity {

    public enum AudienceType {
        ALL,
        STUDENTS,
        STAFF,
        PARENTS
    }

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience", nullable = false)
    private AudienceType targetAudience = AudienceType.ALL;

    @Column(name = "published_at")
    private LocalDateTime publishedAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
}
