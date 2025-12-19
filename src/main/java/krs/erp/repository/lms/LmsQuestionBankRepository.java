package krs.erp.repository.lms;

import krs.erp.model.lms.LmsQuestionBank;
import krs.erp.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsQuestionBankRepository extends JpaRepository<LmsQuestionBank, Long> {
    List<LmsQuestionBank> findBySubject(Subject subject);
}
