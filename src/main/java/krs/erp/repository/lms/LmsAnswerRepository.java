package krs.erp.repository.lms;

import krs.erp.model.lms.LmsAnswer;
import krs.erp.model.lms.LmsQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsAnswerRepository extends JpaRepository<LmsAnswer, Long> {
    List<LmsAnswer> findByQuestion(LmsQuestion question);
}
