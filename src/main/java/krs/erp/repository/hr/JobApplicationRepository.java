package krs.erp.repository.hr;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.hr.JobApplication;
import krs.erp.model.hr.JobApplication.ApplicationStatus;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobPostingId(Long jobPostingId);

    List<JobApplication> findByStatus(ApplicationStatus status);
}
