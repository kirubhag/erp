package krs.erp.repository.lms;

import krs.erp.model.lms.LmsModule;
import krs.erp.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsModuleRepository extends JpaRepository<LmsModule, Long> {
    List<LmsModule> findBySubjectOrderByOrderIndexAsc(Subject subject);
}
