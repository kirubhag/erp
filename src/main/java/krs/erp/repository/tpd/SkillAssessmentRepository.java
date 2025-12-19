package krs.erp.repository.tpd;

import krs.erp.model.tpd.SkillAssessment;
import krs.erp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillAssessmentRepository extends JpaRepository<SkillAssessment, Long> {
    List<SkillAssessment> findByStaff(User staff);
}
