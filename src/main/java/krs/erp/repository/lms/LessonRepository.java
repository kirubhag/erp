package krs.erp.repository.lms;

import krs.erp.model.lms.Lesson;
import krs.erp.model.lms.LmsModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModuleOrderByOrderIndexAsc(LmsModule module);
}
