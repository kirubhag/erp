package krs.erp.controller;

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

import krs.erp.model.admission.AdmissionApplication;
import krs.erp.model.admission.AdmissionApplication.ApplicationStatus;
import krs.erp.model.admission.AdmissionInquiry;
import krs.erp.model.admission.AdmissionInquiry.InquiryStatus;
import krs.erp.service.AdmissionService;

@RestController
@RequestMapping("/api/admission")
public class AdmissionController {

    @Autowired
    private AdmissionService admissionService;

    // Inquiry Endpoints
    @PostMapping("/inquiries")
    public ResponseEntity<AdmissionInquiry> createInquiry(@RequestBody AdmissionInquiry inquiry) {
        return ResponseEntity.ok(admissionService.createInquiry(inquiry));
    }

    @GetMapping("/inquiries")
    public ResponseEntity<List<AdmissionInquiry>> getAllInquiries() {
        return ResponseEntity.ok(admissionService.getAllInquiries());
    }

    @PutMapping("/inquiries/{id}/status")
    public ResponseEntity<AdmissionInquiry> updateInquiryStatus(
            @PathVariable Long id,
            @RequestParam InquiryStatus status) {
        return ResponseEntity.ok(admissionService.updateInquiryStatus(id, status));
    }

    // Application Endpoints
    @PostMapping("/applications")
    public ResponseEntity<AdmissionApplication> createApplication(@RequestBody AdmissionApplication application) {
        return ResponseEntity.ok(admissionService.createApplication(application));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<AdmissionApplication>> getAllApplications() {
        return ResponseEntity.ok(admissionService.getAllApplications());
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<AdmissionApplication> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(admissionService.updateApplicationStatus(id, status));
    }

    // Cycle Endpoints
    @PostMapping("/cycles")
    public ResponseEntity<krs.erp.model.admission.AdmissionCycle> createCycle(
            @RequestBody krs.erp.model.admission.AdmissionCycle cycle) {
        return ResponseEntity.ok(admissionService.createCycle(cycle));
    }

    @GetMapping("/cycles")
    public ResponseEntity<List<krs.erp.model.admission.AdmissionCycle>> getAllCycles() {
        return ResponseEntity.ok(admissionService.getAllCycles());
    }

    // Seat Allocation Endpoints
    @PostMapping("/allocations")
    public ResponseEntity<krs.erp.model.admission.AdmissionSeatAllocation> createAllocation(
            @RequestBody krs.erp.model.admission.AdmissionSeatAllocation allocation) {
        return ResponseEntity.ok(admissionService.createAllocation(allocation));
    }
}
