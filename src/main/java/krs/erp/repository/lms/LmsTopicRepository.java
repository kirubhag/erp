package krs.erp.repository.lms;

import krs.erp.model.lms.LmsTopic;
import krs.erp.model.lms.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsTopicRepository extends JpaRepository<LmsTopic, Long> {
    List<LmsTopic> findByLessonOrderByOrderIndexAsc(Lesson lesson);
}
