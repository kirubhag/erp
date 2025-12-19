package krs.erp.service.hr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import krs.erp.model.hr.JobApplication;
import krs.erp.model.hr.JobApplication.ApplicationStatus;
import krs.erp.model.hr.JobPosting;
import krs.erp.model.hr.JobPosting.JobStatus;
import krs.erp.repository.hr.JobApplicationRepository;
import krs.erp.repository.hr.JobPostingRepository;

@Service
@Transactional
public class RecruitmentService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    // Job Posting Methods
    @SuppressWarnings("null")
    public JobPosting createJobPosting(JobPosting jobPosting) {
        return jobPostingRepository.save(jobPosting);
    }

    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepository.findAll();
    }

    public List<JobPosting> getOpenJobPostings() {
        return jobPostingRepository.findByStatus(JobStatus.OPEN);
    }

    @SuppressWarnings("null")
    public JobPosting updateJobStatus(Long id, JobStatus status) {
        JobPosting job = jobPostingRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(status);
        return jobPostingRepository.save(job);
    }

    // Job Application Methods
    @SuppressWarnings("null")
    public JobApplication applyForJob(JobApplication application) {
        return jobApplicationRepository.save(application);
    }

    public List<JobApplication> getApplicationsForJob(Long jobPostingId) {
        return jobApplicationRepository.findByJobPostingId(jobPostingId);
    }

    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findAll();
    }

    @SuppressWarnings("null")
    public JobApplication updateApplicationStatus(Long id, ApplicationStatus status) {
        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(status);
        return jobApplicationRepository.save(app);
    }
}
