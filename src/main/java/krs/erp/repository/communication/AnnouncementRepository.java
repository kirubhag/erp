package krs.erp.repository.communication;

import krs.erp.model.communication.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTargetAudienceInAndExpiresAtAfterOrderByPublishedAtDesc(
            List<Announcement.AudienceType> audiences, LocalDateTime now);
}
