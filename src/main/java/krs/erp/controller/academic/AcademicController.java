package krs.erp.controller.academic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.academic.AcademicSettings;
import krs.erp.model.academic.AcademicYear;
import krs.erp.model.academic.GradingScale;
import krs.erp.model.academic.Term;
import krs.erp.service.academic.AcademicSettingsService;
import krs.erp.service.academic.AcademicYearService;
import krs.erp.service.academic.GradingScaleService;
import krs.erp.service.academic.TermService;

/**
 * REST Controller for Academic Settings
 */
@RestController
@RequestMapping("/api/academic")
public class AcademicController {
    
    @Autowired
    private AcademicYearService academicYearService;
    
    @Autowired
    private TermService termService;
    
    @Autowired
    private GradingScaleService gradingScaleService;
    
    @Autowired
    private AcademicSettingsService academicSettingsService;
    
    // Hardcoded organization ID for now - should be from session/JWT
    private static final Long DEFAULT_ORG_ID = 1L;
    
    // Academic Year Endpoints
    @GetMapping("/years")
    public ResponseEntity<List<AcademicYear>> getAllYears() {
        return ResponseEntity.ok(academicYearService.getAllByOrganization(DEFAULT_ORG_ID));
    }
    
    @GetMapping("/years/{id}")
    public ResponseEntity<AcademicYear> getYearById(@PathVariable Long id) {
        return academicYearService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/years/active")
    public ResponseEntity<AcademicYear> getActiveYear() {
        return academicYearService.getActiveYear(DEFAULT_ORG_ID)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/years")
    public ResponseEntity<AcademicYear> createYear(@RequestBody AcademicYear academicYear) {
        academicYear.setOrganizationId(DEFAULT_ORG_ID);
        return ResponseEntity.ok(academicYearService.create(academicYear));
    }
    
    @PutMapping("/years/{id}")
    public ResponseEntity<AcademicYear> updateYear(@PathVariable Long id, @RequestBody AcademicYear academicYear) {
        try {
            return ResponseEntity.ok(academicYearService.update(id, academicYear));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/years/{id}/activate")
    public ResponseEntity<Void> activateYear(@PathVariable Long id) {
        try {
            academicYearService.activate(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/years/{id}")
    public ResponseEntity<Void> deleteYear(@PathVariable Long id) {
        academicYearService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    // Term Endpoints
    @GetMapping("/terms")
    public ResponseEntity<List<Term>> getAllTerms() {
        return ResponseEntity.ok(termService.getAllByOrganization(DEFAULT_ORG_ID));
    }
    
    @GetMapping("/terms/{id}")
    public ResponseEntity<Term> getTermById(@PathVariable Long id) {
        return termService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/terms/year/{academicYearId}")
    public ResponseEntity<List<Term>> getTermsByYear(@PathVariable Long academicYearId) {
        return ResponseEntity.ok(termService.getByAcademicYear(academicYearId));
    }
    
    @PostMapping("/terms")
    public ResponseEntity<Term> createTerm(@RequestBody Term term) {
        term.setOrganizationId(DEFAULT_ORG_ID);
        return ResponseEntity.ok(termService.create(term));
    }
    
    @PutMapping("/terms/{id}")
    public ResponseEntity<Term> updateTerm(@PathVariable Long id, @RequestBody Term term) {
        try {
            return ResponseEntity.ok(termService.update(id, term));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/terms/{id}")
    public ResponseEntity<Void> deleteTerm(@PathVariable Long id) {
        termService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    // Grading Scale Endpoints
    @GetMapping("/grading-scales")
    public ResponseEntity<List<GradingScale>> getAllGradingScales() {
        return ResponseEntity.ok(gradingScaleService.getAllByOrganization(DEFAULT_ORG_ID));
    }
    
    @GetMapping("/grading-scales/{id}")
    public ResponseEntity<GradingScale> getGradingScaleById(@PathVariable Long id) {
        return gradingScaleService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/grading-scales")
    public ResponseEntity<GradingScale> createGradingScale(@RequestBody GradingScale gradingScale) {
        gradingScale.setOrganizationId(DEFAULT_ORG_ID);
        return ResponseEntity.ok(gradingScaleService.create(gradingScale));
    }
    
    @PutMapping("/grading-scales/{id}")
    public ResponseEntity<GradingScale> updateGradingScale(@PathVariable Long id, @RequestBody GradingScale gradingScale) {
        try {
            return ResponseEntity.ok(gradingScaleService.update(id, gradingScale));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/grading-scales/{id}")
    public ResponseEntity<Void> deleteGradingScale(@PathVariable Long id) {
        gradingScaleService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    // Academic Settings Endpoints
    @GetMapping("/settings/attendance")
    public ResponseEntity<AcademicSettings> getAttendanceSettings() {
        return ResponseEntity.ok(academicSettingsService.getOrCreateByOrganization(DEFAULT_ORG_ID));
    }
    
    @PutMapping("/settings/attendance")
    public ResponseEntity<AcademicSettings> updateAttendanceSettings(@RequestBody AcademicSettings settings) {
        return ResponseEntity.ok(academicSettingsService.updateAttendanceSettings(DEFAULT_ORG_ID, settings));
    }
    
    @GetMapping("/settings/exam")
    public ResponseEntity<AcademicSettings> getExamSettings() {
        return ResponseEntity.ok(academicSettingsService.getOrCreateByOrganization(DEFAULT_ORG_ID));
    }
    
    @PutMapping("/settings/exam")
    public ResponseEntity<AcademicSettings> updateExamSettings(@RequestBody AcademicSettings settings) {
        return ResponseEntity.ok(academicSettingsService.updateExamSettings(DEFAULT_ORG_ID, settings));
    }
    
    @GetMapping("/settings/promotion")
    public ResponseEntity<AcademicSettings> getPromotionSettings() {
        return ResponseEntity.ok(academicSettingsService.getOrCreateByOrganization(DEFAULT_ORG_ID));
    }
    
    @PutMapping("/settings/promotion")
    public ResponseEntity<AcademicSettings> updatePromotionSettings(@RequestBody AcademicSettings settings) {
        return ResponseEntity.ok(academicSettingsService.updatePromotionSettings(DEFAULT_ORG_ID, settings));
    }
}
