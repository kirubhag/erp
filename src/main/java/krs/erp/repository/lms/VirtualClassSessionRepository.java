package krs.erp.repository.lms;

import krs.erp.model.lms.VirtualClassSession;
import krs.erp.model.lms.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VirtualClassSessionRepository extends JpaRepository<VirtualClassSession, Long> {
    List<VirtualClassSession> findByLesson(Lesson lesson);
}
