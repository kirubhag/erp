package krs.erp.controller.hr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.hr.JobApplication;
import krs.erp.model.hr.JobApplication.ApplicationStatus;
import krs.erp.model.hr.JobPosting;
import krs.erp.model.hr.JobPosting.JobStatus;
import krs.erp.service.hr.RecruitmentService;

@RestController
@RequestMapping("/api/hr/recruitment")
public class RecruitmentController {

    @Autowired
    private RecruitmentService recruitmentService;

    // Job Posting Endpoints
    @PostMapping("/jobs")
    public ResponseEntity<JobPosting> createJob(@RequestBody JobPosting job) {
        return ResponseEntity.ok(recruitmentService.createJobPosting(job));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobPosting>> getAllJobs(@RequestParam(required = false) boolean onlyOpen) {
        if (onlyOpen) {
            return ResponseEntity.ok(recruitmentService.getOpenJobPostings());
        }
        return ResponseEntity.ok(recruitmentService.getAllJobPostings());
    }

    @PutMapping("/jobs/{id}/status")
    public ResponseEntity<JobPosting> updateJobStatus(@PathVariable Long id, @RequestParam JobStatus status) {
        return ResponseEntity.ok(recruitmentService.updateJobStatus(id, status));
    }

    // Application Endpoints
    @PostMapping("/applications")
    public ResponseEntity<JobApplication> apply(@RequestBody JobApplication application) {
        return ResponseEntity.ok(recruitmentService.applyForJob(application));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<JobApplication>> getAllApplications(@RequestParam(required = false) Long jobId) {
        if (jobId != null) {
            return ResponseEntity.ok(recruitmentService.getApplicationsForJob(jobId));
        }
        return ResponseEntity.ok(recruitmentService.getAllApplications());
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<JobApplication> updateApplicationStatus(@PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(recruitmentService.updateApplicationStatus(id, status));
    }
}
