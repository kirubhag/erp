package krs.erp.repository.lms;

import krs.erp.model.lms.LmsRubric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LmsRubricRepository extends JpaRepository<LmsRubric, Long> {
}
