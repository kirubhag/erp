package krs.erp.repository.finance;

import krs.erp.model.finance.ScholarshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScholarshipApplicationRepository extends JpaRepository<ScholarshipApplication, Long> {
    List<ScholarshipApplication> findByStudentId(Long studentId);

    List<ScholarshipApplication> findByStatus(ScholarshipApplication.ApplicationStatus status);
}
