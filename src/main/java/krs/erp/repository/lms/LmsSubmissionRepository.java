package krs.erp.repository.lms;

import krs.erp.model.lms.LmsSubmission;
import krs.erp.model.lms.LmsQuiz;
import krs.erp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsSubmissionRepository extends JpaRepository<LmsSubmission, Long> {
    List<LmsSubmission> findByQuizAndStudent(LmsQuiz quiz, User student);
}
