package krs.erp.controller.tpd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.tpd.Competency;
import krs.erp.model.tpd.CpdLedger;
import krs.erp.model.tpd.Evidence;
import krs.erp.model.tpd.ProfessionalPortfolio;
import krs.erp.model.tpd.SkillAssessment;
import krs.erp.model.tpd.TrainingAttendance;
import krs.erp.model.tpd.TrainingEvaluation;
import krs.erp.model.tpd.TrainingEvent;
import krs.erp.repository.tpd.CompetencyRepository;
import krs.erp.repository.tpd.CpdLedgerRepository;
import krs.erp.repository.tpd.EvidenceRepository;
import krs.erp.repository.tpd.ProfessionalPortfolioRepository;
import krs.erp.repository.tpd.SkillAssessmentRepository;
import krs.erp.repository.tpd.TrainingAttendanceRepository;
import krs.erp.repository.tpd.TrainingEvaluationRepository;
import krs.erp.repository.tpd.TrainingEventRepository;

/**
 * REST Controller for Training & Professional Development (TPD) entities
 * Provides CRUD endpoints for all TPD related data
 */
@RestController
@RequestMapping("/api/tpd")
@CrossOrigin(origins = "*")
public class TpdController {

    @Autowired
    private CompetencyRepository competencyRepository;

    @Autowired
    private SkillAssessmentRepository skillAssessmentRepository;

    @Autowired
    private TrainingEventRepository trainingEventRepository;

    @Autowired
    private TrainingAttendanceRepository trainingAttendanceRepository;

    @Autowired
    private CpdLedgerRepository cpdLedgerRepository;

    @Autowired
    private ProfessionalPortfolioRepository professionalPortfolioRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private TrainingEvaluationRepository trainingEvaluationRepository;

    // === Competencies CRUD ===
    @GetMapping("/competencies")
    public ResponseEntity<Page<Competency>> getAllCompetencies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(competencyRepository.findAll(pageable));
    }

    @GetMapping("/competencies/{id}")
    public ResponseEntity<Competency> getCompetency(@PathVariable Long id) {
        return competencyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/competencies")
    public ResponseEntity<Competency> createCompetency(@RequestBody Competency competency) {
        return ResponseEntity.ok(competencyRepository.save(competency));
    }

    @PutMapping("/competencies/{id}")
    public ResponseEntity<Competency> updateCompetency(@PathVariable Long id, @RequestBody Competency competency) {
        if (!competencyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        competency.setId(id);
        return ResponseEntity.ok(competencyRepository.save(competency));
    }

    @DeleteMapping("/competencies/{id}")
    public ResponseEntity<Void> deleteCompetency(@PathVariable Long id) {
        if (!competencyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        competencyRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Skill Assessments CRUD ===
    @GetMapping("/skill-assessments")
    public ResponseEntity<Page<SkillAssessment>> getAllSkillAssessments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(skillAssessmentRepository.findAll(pageable));
    }

    @GetMapping("/skill-assessments/{id}")
    public ResponseEntity<SkillAssessment> getSkillAssessment(@PathVariable Long id) {
        return skillAssessmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/skill-assessments")
    public ResponseEntity<SkillAssessment> createSkillAssessment(@RequestBody SkillAssessment skillAssessment) {
        return ResponseEntity.ok(skillAssessmentRepository.save(skillAssessment));
    }

    @PutMapping("/skill-assessments/{id}")
    public ResponseEntity<SkillAssessment> updateSkillAssessment(@PathVariable Long id, @RequestBody SkillAssessment skillAssessment) {
        if (!skillAssessmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        skillAssessment.setId(id);
        return ResponseEntity.ok(skillAssessmentRepository.save(skillAssessment));
    }

    @DeleteMapping("/skill-assessments/{id}")
    public ResponseEntity<Void> deleteSkillAssessment(@PathVariable Long id) {
        if (!skillAssessmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        skillAssessmentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Training Events CRUD ===
    @GetMapping("/training-events")
    public ResponseEntity<Page<TrainingEvent>> getAllTrainingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(trainingEventRepository.findAll(pageable));
    }

    @GetMapping("/training-events/{id}")
    public ResponseEntity<TrainingEvent> getTrainingEvent(@PathVariable Long id) {
        return trainingEventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/training-events")
    public ResponseEntity<TrainingEvent> createTrainingEvent(@RequestBody TrainingEvent trainingEvent) {
        return ResponseEntity.ok(trainingEventRepository.save(trainingEvent));
    }

    @PutMapping("/training-events/{id}")
    public ResponseEntity<TrainingEvent> updateTrainingEvent(@PathVariable Long id, @RequestBody TrainingEvent trainingEvent) {
        if (!trainingEventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        trainingEvent.setId(id);
        return ResponseEntity.ok(trainingEventRepository.save(trainingEvent));
    }

    @DeleteMapping("/training-events/{id}")
    public ResponseEntity<Void> deleteTrainingEvent(@PathVariable Long id) {
        if (!trainingEventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        trainingEventRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Training Attendance CRUD ===
    @GetMapping("/training-attendance")
    public ResponseEntity<Page<TrainingAttendance>> getAllTrainingAttendance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(trainingAttendanceRepository.findAll(pageable));
    }

    @GetMapping("/training-attendance/{id}")
    public ResponseEntity<TrainingAttendance> getTrainingAttendance(@PathVariable Long id) {
        return trainingAttendanceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/training-attendance")
    public ResponseEntity<TrainingAttendance> createTrainingAttendance(@RequestBody TrainingAttendance trainingAttendance) {
        return ResponseEntity.ok(trainingAttendanceRepository.save(trainingAttendance));
    }

    @PutMapping("/training-attendance/{id}")
    public ResponseEntity<TrainingAttendance> updateTrainingAttendance(@PathVariable Long id, @RequestBody TrainingAttendance trainingAttendance) {
        if (!trainingAttendanceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        trainingAttendance.setId(id);
        return ResponseEntity.ok(trainingAttendanceRepository.save(trainingAttendance));
    }

    @DeleteMapping("/training-attendance/{id}")
    public ResponseEntity<Void> deleteTrainingAttendance(@PathVariable Long id) {
        if (!trainingAttendanceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        trainingAttendanceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === CPD Ledger CRUD ===
    @GetMapping("/cpd-ledger")
    public ResponseEntity<Page<CpdLedger>> getAllCpdLedger(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(cpdLedgerRepository.findAll(pageable));
    }

    @GetMapping("/cpd-ledger/{id}")
    public ResponseEntity<CpdLedger> getCpdLedger(@PathVariable Long id) {
        return cpdLedgerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cpd-ledger")
    public ResponseEntity<CpdLedger> createCpdLedger(@RequestBody CpdLedger cpdLedger) {
        return ResponseEntity.ok(cpdLedgerRepository.save(cpdLedger));
    }

    @PutMapping("/cpd-ledger/{id}")
    public ResponseEntity<CpdLedger> updateCpdLedger(@PathVariable Long id, @RequestBody CpdLedger cpdLedger) {
        if (!cpdLedgerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cpdLedger.setId(id);
        return ResponseEntity.ok(cpdLedgerRepository.save(cpdLedger));
    }

    @DeleteMapping("/cpd-ledger/{id}")
    public ResponseEntity<Void> deleteCpdLedger(@PathVariable Long id) {
        if (!cpdLedgerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        cpdLedgerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Professional Portfolios CRUD ===
    @GetMapping("/portfolios")
    public ResponseEntity<Page<ProfessionalPortfolio>> getAllPortfolios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(professionalPortfolioRepository.findAll(pageable));
    }

    @GetMapping("/portfolios/{id}")
    public ResponseEntity<ProfessionalPortfolio> getPortfolio(@PathVariable Long id) {
        return professionalPortfolioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/portfolios")
    public ResponseEntity<ProfessionalPortfolio> createPortfolio(@RequestBody ProfessionalPortfolio portfolio) {
        return ResponseEntity.ok(professionalPortfolioRepository.save(portfolio));
    }

    @PutMapping("/portfolios/{id}")
    public ResponseEntity<ProfessionalPortfolio> updatePortfolio(@PathVariable Long id, @RequestBody ProfessionalPortfolio portfolio) {
        if (!professionalPortfolioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        portfolio.setId(id);
        return ResponseEntity.ok(professionalPortfolioRepository.save(portfolio));
    }

    @DeleteMapping("/portfolios/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        if (!professionalPortfolioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        professionalPortfolioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Evidence CRUD ===
    @GetMapping("/evidence")
    public ResponseEntity<Page<Evidence>> getAllEvidence(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(evidenceRepository.findAll(pageable));
    }

    @GetMapping("/evidence/{id}")
    public ResponseEntity<Evidence> getEvidence(@PathVariable Long id) {
        return evidenceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/evidence")
    public ResponseEntity<Evidence> createEvidence(@RequestBody Evidence evidence) {
        return ResponseEntity.ok(evidenceRepository.save(evidence));
    }

    @PutMapping("/evidence/{id}")
    public ResponseEntity<Evidence> updateEvidence(@PathVariable Long id, @RequestBody Evidence evidence) {
        if (!evidenceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        evidence.setId(id);
        return ResponseEntity.ok(evidenceRepository.save(evidence));
    }

    @DeleteMapping("/evidence/{id}")
    public ResponseEntity<Void> deleteEvidence(@PathVariable Long id) {
        if (!evidenceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        evidenceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Training Evaluations CRUD ===
    @GetMapping("/evaluations")
    public ResponseEntity<Page<TrainingEvaluation>> getAllEvaluations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(trainingEvaluationRepository.findAll(pageable));
    }

    @GetMapping("/evaluations/{id}")
    public ResponseEntity<TrainingEvaluation> getEvaluation(@PathVariable Long id) {
        return trainingEvaluationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/evaluations")
    public ResponseEntity<TrainingEvaluation> createEvaluation(@RequestBody TrainingEvaluation evaluation) {
        return ResponseEntity.ok(trainingEvaluationRepository.save(evaluation));
    }

    @PutMapping("/evaluations/{id}")
    public ResponseEntity<TrainingEvaluation> updateEvaluation(@PathVariable Long id, @RequestBody TrainingEvaluation evaluation) {
        if (!trainingEvaluationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        evaluation.setId(id);
        return ResponseEntity.ok(trainingEvaluationRepository.save(evaluation));
    }

    @DeleteMapping("/evaluations/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        if (!trainingEvaluationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        trainingEvaluationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
