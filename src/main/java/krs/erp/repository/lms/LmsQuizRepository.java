package krs.erp.repository.lms;

import krs.erp.model.lms.LmsQuiz;
import krs.erp.model.lms.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsQuizRepository extends JpaRepository<LmsQuiz, Long> {
    List<LmsQuiz> findByLesson(Lesson lesson);
}
