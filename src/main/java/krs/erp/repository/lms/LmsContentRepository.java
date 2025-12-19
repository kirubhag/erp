package krs.erp.repository.lms;

import krs.erp.model.lms.LmsContent;
import krs.erp.model.lms.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsContentRepository extends JpaRepository<LmsContent, Long> {
    List<LmsContent> findByLesson(Lesson lesson);
}
