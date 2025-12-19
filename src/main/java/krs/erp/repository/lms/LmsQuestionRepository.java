package krs.erp.repository.lms;

import krs.erp.model.lms.LmsQuestion;
import krs.erp.model.lms.LmsQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsQuestionRepository extends JpaRepository<LmsQuestion, Long> {
    List<LmsQuestion> findByQuiz(LmsQuiz quiz);
}
