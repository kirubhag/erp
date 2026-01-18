package krs.erp.controller.lms;

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

import krs.erp.model.lms.Lesson;
import krs.erp.model.lms.LmsAnswer;
import krs.erp.model.lms.LmsBadge;
import krs.erp.model.lms.LmsContent;
import krs.erp.model.lms.LmsForum;
import krs.erp.model.lms.LmsForumPost;
import krs.erp.model.lms.LmsModule;
import krs.erp.model.lms.LmsPeerReview;
import krs.erp.model.lms.LmsPointLog;
import krs.erp.model.lms.LmsQuestion;
import krs.erp.model.lms.LmsQuestionBank;
import krs.erp.model.lms.LmsQuiz;
import krs.erp.model.lms.LmsRubric;
import krs.erp.model.lms.LmsStudentProgress;
import krs.erp.model.lms.LmsSubmission;
import krs.erp.model.lms.LmsTopic;
import krs.erp.model.lms.VirtualAttendanceRecord;
import krs.erp.model.lms.VirtualClassSession;
import krs.erp.repository.lms.LessonRepository;
import krs.erp.repository.lms.LmsAnswerRepository;
import krs.erp.repository.lms.LmsBadgeRepository;
import krs.erp.repository.lms.LmsContentRepository;
import krs.erp.repository.lms.LmsForumPostRepository;
import krs.erp.repository.lms.LmsForumRepository;
import krs.erp.repository.lms.LmsModuleRepository;
import krs.erp.repository.lms.LmsPeerReviewRepository;
import krs.erp.repository.lms.LmsPointLogRepository;
import krs.erp.repository.lms.LmsQuestionBankRepository;
import krs.erp.repository.lms.LmsQuestionRepository;
import krs.erp.repository.lms.LmsQuizRepository;
import krs.erp.repository.lms.LmsRubricRepository;
import krs.erp.repository.lms.LmsStudentProgressRepository;
import krs.erp.repository.lms.LmsSubmissionRepository;
import krs.erp.repository.lms.LmsTopicRepository;
import krs.erp.repository.lms.VirtualAttendanceRecordRepository;
import krs.erp.repository.lms.VirtualClassSessionRepository;

/**
 * REST Controller for Learning Management System (LMS) entities
 * Provides CRUD endpoints for all LMS related data
 */
@RestController
@RequestMapping("/api/lms")
@CrossOrigin(origins = "*")
public class LmsController {

    @Autowired
    private LmsModuleRepository lmsModuleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LmsTopicRepository lmsTopicRepository;

    @Autowired
    private LmsContentRepository lmsContentRepository;

    @Autowired
    private LmsQuizRepository lmsQuizRepository;

    @Autowired
    private LmsQuestionRepository lmsQuestionRepository;

    @Autowired
    private LmsAnswerRepository lmsAnswerRepository;

    @Autowired
    private LmsQuestionBankRepository lmsQuestionBankRepository;

    @Autowired
    private LmsSubmissionRepository lmsSubmissionRepository;

    @Autowired
    private LmsRubricRepository lmsRubricRepository;

    @Autowired
    private VirtualClassSessionRepository virtualClassSessionRepository;

    @Autowired
    private VirtualAttendanceRecordRepository virtualAttendanceRecordRepository;

    @Autowired
    private LmsStudentProgressRepository lmsStudentProgressRepository;

    @Autowired
    private LmsBadgeRepository lmsBadgeRepository;

    @Autowired
    private LmsPointLogRepository lmsPointLogRepository;

    @Autowired
    private LmsForumRepository lmsForumRepository;

    @Autowired
    private LmsForumPostRepository lmsForumPostRepository;

    @Autowired
    private LmsPeerReviewRepository lmsPeerReviewRepository;

    // === LMS Modules CRUD ===
    @GetMapping("/modules")
    public ResponseEntity<Page<LmsModule>> getAllModules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsModuleRepository.findAll(pageable));
    }

    @GetMapping("/modules/{id}")
    public ResponseEntity<LmsModule> getModule(@PathVariable Long id) {
        return lmsModuleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/modules")
    public ResponseEntity<LmsModule> createModule(@RequestBody LmsModule module) {
        return ResponseEntity.ok(lmsModuleRepository.save(module));
    }

    @PutMapping("/modules/{id}")
    public ResponseEntity<LmsModule> updateModule(@PathVariable Long id, @RequestBody LmsModule module) {
        if (!lmsModuleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        module.setId(id);
        return ResponseEntity.ok(lmsModuleRepository.save(module));
    }

    @DeleteMapping("/modules/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        if (!lmsModuleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsModuleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Lessons CRUD ===
    @GetMapping("/lessons")
    public ResponseEntity<Page<Lesson>> getAllLessons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lessonRepository.findAll(pageable));
    }

    @GetMapping("/lessons/{id}")
    public ResponseEntity<Lesson> getLesson(@PathVariable Long id) {
        return lessonRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/lessons")
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        return ResponseEntity.ok(lessonRepository.save(lesson));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson lesson) {
        if (!lessonRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lesson.setId(id);
        return ResponseEntity.ok(lessonRepository.save(lesson));
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        if (!lessonRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lessonRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Topics CRUD ===
    @GetMapping("/topics")
    public ResponseEntity<Page<LmsTopic>> getAllTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsTopicRepository.findAll(pageable));
    }

    @GetMapping("/topics/{id}")
    public ResponseEntity<LmsTopic> getTopic(@PathVariable Long id) {
        return lmsTopicRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/topics")
    public ResponseEntity<LmsTopic> createTopic(@RequestBody LmsTopic topic) {
        return ResponseEntity.ok(lmsTopicRepository.save(topic));
    }

    @PutMapping("/topics/{id}")
    public ResponseEntity<LmsTopic> updateTopic(@PathVariable Long id, @RequestBody LmsTopic topic) {
        if (!lmsTopicRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        topic.setId(id);
        return ResponseEntity.ok(lmsTopicRepository.save(topic));
    }

    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        if (!lmsTopicRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsTopicRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Content CRUD ===
    @GetMapping("/content")
    public ResponseEntity<Page<LmsContent>> getAllContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsContentRepository.findAll(pageable));
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<LmsContent> getContent(@PathVariable Long id) {
        return lmsContentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/content")
    public ResponseEntity<LmsContent> createContent(@RequestBody LmsContent content) {
        return ResponseEntity.ok(lmsContentRepository.save(content));
    }

    @PutMapping("/content/{id}")
    public ResponseEntity<LmsContent> updateContent(@PathVariable Long id, @RequestBody LmsContent content) {
        if (!lmsContentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        content.setId(id);
        return ResponseEntity.ok(lmsContentRepository.save(content));
    }

    @DeleteMapping("/content/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        if (!lmsContentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsContentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Quizzes CRUD ===
    @GetMapping("/quizzes")
    public ResponseEntity<Page<LmsQuiz>> getAllQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsQuizRepository.findAll(pageable));
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<LmsQuiz> getQuiz(@PathVariable Long id) {
        return lmsQuizRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/quizzes")
    public ResponseEntity<LmsQuiz> createQuiz(@RequestBody LmsQuiz quiz) {
        return ResponseEntity.ok(lmsQuizRepository.save(quiz));
    }

    @PutMapping("/quizzes/{id}")
    public ResponseEntity<LmsQuiz> updateQuiz(@PathVariable Long id, @RequestBody LmsQuiz quiz) {
        if (!lmsQuizRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        quiz.setId(id);
        return ResponseEntity.ok(lmsQuizRepository.save(quiz));
    }

    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        if (!lmsQuizRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsQuizRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Questions CRUD ===
    @GetMapping("/questions")
    public ResponseEntity<Page<LmsQuestion>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsQuestionRepository.findAll(pageable));
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<LmsQuestion> getQuestion(@PathVariable Long id) {
        return lmsQuestionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/questions")
    public ResponseEntity<LmsQuestion> createQuestion(@RequestBody LmsQuestion question) {
        return ResponseEntity.ok(lmsQuestionRepository.save(question));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<LmsQuestion> updateQuestion(@PathVariable Long id, @RequestBody LmsQuestion question) {
        if (!lmsQuestionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        question.setId(id);
        return ResponseEntity.ok(lmsQuestionRepository.save(question));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        if (!lmsQuestionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsQuestionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Answers CRUD ===
    @GetMapping("/answers")
    public ResponseEntity<Page<LmsAnswer>> getAllAnswers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsAnswerRepository.findAll(pageable));
    }

    @GetMapping("/answers/{id}")
    public ResponseEntity<LmsAnswer> getAnswer(@PathVariable Long id) {
        return lmsAnswerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/answers")
    public ResponseEntity<LmsAnswer> createAnswer(@RequestBody LmsAnswer answer) {
        return ResponseEntity.ok(lmsAnswerRepository.save(answer));
    }

    @PutMapping("/answers/{id}")
    public ResponseEntity<LmsAnswer> updateAnswer(@PathVariable Long id, @RequestBody LmsAnswer answer) {
        if (!lmsAnswerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        answer.setId(id);
        return ResponseEntity.ok(lmsAnswerRepository.save(answer));
    }

    @DeleteMapping("/answers/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        if (!lmsAnswerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsAnswerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Question Banks CRUD ===
    @GetMapping("/question-banks")
    public ResponseEntity<Page<LmsQuestionBank>> getAllQuestionBanks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsQuestionBankRepository.findAll(pageable));
    }

    @GetMapping("/question-banks/{id}")
    public ResponseEntity<LmsQuestionBank> getQuestionBank(@PathVariable Long id) {
        return lmsQuestionBankRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/question-banks")
    public ResponseEntity<LmsQuestionBank> createQuestionBank(@RequestBody LmsQuestionBank questionBank) {
        return ResponseEntity.ok(lmsQuestionBankRepository.save(questionBank));
    }

    @PutMapping("/question-banks/{id}")
    public ResponseEntity<LmsQuestionBank> updateQuestionBank(@PathVariable Long id, @RequestBody LmsQuestionBank questionBank) {
        if (!lmsQuestionBankRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        questionBank.setId(id);
        return ResponseEntity.ok(lmsQuestionBankRepository.save(questionBank));
    }

    @DeleteMapping("/question-banks/{id}")
    public ResponseEntity<Void> deleteQuestionBank(@PathVariable Long id) {
        if (!lmsQuestionBankRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsQuestionBankRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Submissions CRUD ===
    @GetMapping("/submissions")
    public ResponseEntity<Page<LmsSubmission>> getAllSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsSubmissionRepository.findAll(pageable));
    }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<LmsSubmission> getSubmission(@PathVariable Long id) {
        return lmsSubmissionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/submissions")
    public ResponseEntity<LmsSubmission> createSubmission(@RequestBody LmsSubmission submission) {
        return ResponseEntity.ok(lmsSubmissionRepository.save(submission));
    }

    @PutMapping("/submissions/{id}")
    public ResponseEntity<LmsSubmission> updateSubmission(@PathVariable Long id, @RequestBody LmsSubmission submission) {
        if (!lmsSubmissionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        submission.setId(id);
        return ResponseEntity.ok(lmsSubmissionRepository.save(submission));
    }

    @DeleteMapping("/submissions/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        if (!lmsSubmissionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsSubmissionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Rubrics CRUD ===
    @GetMapping("/rubrics")
    public ResponseEntity<Page<LmsRubric>> getAllRubrics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsRubricRepository.findAll(pageable));
    }

    @GetMapping("/rubrics/{id}")
    public ResponseEntity<LmsRubric> getRubric(@PathVariable Long id) {
        return lmsRubricRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rubrics")
    public ResponseEntity<LmsRubric> createRubric(@RequestBody LmsRubric rubric) {
        return ResponseEntity.ok(lmsRubricRepository.save(rubric));
    }

    @PutMapping("/rubrics/{id}")
    public ResponseEntity<LmsRubric> updateRubric(@PathVariable Long id, @RequestBody LmsRubric rubric) {
        if (!lmsRubricRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rubric.setId(id);
        return ResponseEntity.ok(lmsRubricRepository.save(rubric));
    }

    @DeleteMapping("/rubrics/{id}")
    public ResponseEntity<Void> deleteRubric(@PathVariable Long id) {
        if (!lmsRubricRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsRubricRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Virtual Class Sessions CRUD ===
    @GetMapping("/virtual-sessions")
    public ResponseEntity<Page<VirtualClassSession>> getAllVirtualSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(virtualClassSessionRepository.findAll(pageable));
    }

    @GetMapping("/virtual-sessions/{id}")
    public ResponseEntity<VirtualClassSession> getVirtualSession(@PathVariable Long id) {
        return virtualClassSessionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/virtual-sessions")
    public ResponseEntity<VirtualClassSession> createVirtualSession(@RequestBody VirtualClassSession session) {
        return ResponseEntity.ok(virtualClassSessionRepository.save(session));
    }

    @PutMapping("/virtual-sessions/{id}")
    public ResponseEntity<VirtualClassSession> updateVirtualSession(@PathVariable Long id, @RequestBody VirtualClassSession session) {
        if (!virtualClassSessionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        session.setId(id);
        return ResponseEntity.ok(virtualClassSessionRepository.save(session));
    }

    @DeleteMapping("/virtual-sessions/{id}")
    public ResponseEntity<Void> deleteVirtualSession(@PathVariable Long id) {
        if (!virtualClassSessionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        virtualClassSessionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Virtual Attendance Records CRUD ===
    @GetMapping("/virtual-attendance")
    public ResponseEntity<Page<VirtualAttendanceRecord>> getAllVirtualAttendance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(virtualAttendanceRecordRepository.findAll(pageable));
    }

    @GetMapping("/virtual-attendance/{id}")
    public ResponseEntity<VirtualAttendanceRecord> getVirtualAttendance(@PathVariable Long id) {
        return virtualAttendanceRecordRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/virtual-attendance")
    public ResponseEntity<VirtualAttendanceRecord> createVirtualAttendance(@RequestBody VirtualAttendanceRecord attendance) {
        return ResponseEntity.ok(virtualAttendanceRecordRepository.save(attendance));
    }

    @PutMapping("/virtual-attendance/{id}")
    public ResponseEntity<VirtualAttendanceRecord> updateVirtualAttendance(@PathVariable Long id, @RequestBody VirtualAttendanceRecord attendance) {
        if (!virtualAttendanceRecordRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        attendance.setId(id);
        return ResponseEntity.ok(virtualAttendanceRecordRepository.save(attendance));
    }

    @DeleteMapping("/virtual-attendance/{id}")
    public ResponseEntity<Void> deleteVirtualAttendance(@PathVariable Long id) {
        if (!virtualAttendanceRecordRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        virtualAttendanceRecordRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Student Progress CRUD ===
    @GetMapping("/student-progress")
    public ResponseEntity<Page<LmsStudentProgress>> getAllStudentProgress(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsStudentProgressRepository.findAll(pageable));
    }

    @GetMapping("/student-progress/{id}")
    public ResponseEntity<LmsStudentProgress> getStudentProgress(@PathVariable Long id) {
        return lmsStudentProgressRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/student-progress")
    public ResponseEntity<LmsStudentProgress> createStudentProgress(@RequestBody LmsStudentProgress progress) {
        return ResponseEntity.ok(lmsStudentProgressRepository.save(progress));
    }

    @PutMapping("/student-progress/{id}")
    public ResponseEntity<LmsStudentProgress> updateStudentProgress(@PathVariable Long id, @RequestBody LmsStudentProgress progress) {
        if (!lmsStudentProgressRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        progress.setId(id);
        return ResponseEntity.ok(lmsStudentProgressRepository.save(progress));
    }

    @DeleteMapping("/student-progress/{id}")
    public ResponseEntity<Void> deleteStudentProgress(@PathVariable Long id) {
        if (!lmsStudentProgressRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsStudentProgressRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Badges CRUD ===
    @GetMapping("/badges")
    public ResponseEntity<Page<LmsBadge>> getAllBadges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsBadgeRepository.findAll(pageable));
    }

    @GetMapping("/badges/{id}")
    public ResponseEntity<LmsBadge> getBadge(@PathVariable Long id) {
        return lmsBadgeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/badges")
    public ResponseEntity<LmsBadge> createBadge(@RequestBody LmsBadge badge) {
        return ResponseEntity.ok(lmsBadgeRepository.save(badge));
    }

    @PutMapping("/badges/{id}")
    public ResponseEntity<LmsBadge> updateBadge(@PathVariable Long id, @RequestBody LmsBadge badge) {
        if (!lmsBadgeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        badge.setId(id);
        return ResponseEntity.ok(lmsBadgeRepository.save(badge));
    }

    @DeleteMapping("/badges/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        if (!lmsBadgeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsBadgeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Point Logs CRUD ===
    @GetMapping("/point-logs")
    public ResponseEntity<Page<LmsPointLog>> getAllPointLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsPointLogRepository.findAll(pageable));
    }

    @GetMapping("/point-logs/{id}")
    public ResponseEntity<LmsPointLog> getPointLog(@PathVariable Long id) {
        return lmsPointLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/point-logs")
    public ResponseEntity<LmsPointLog> createPointLog(@RequestBody LmsPointLog pointLog) {
        return ResponseEntity.ok(lmsPointLogRepository.save(pointLog));
    }

    @PutMapping("/point-logs/{id}")
    public ResponseEntity<LmsPointLog> updatePointLog(@PathVariable Long id, @RequestBody LmsPointLog pointLog) {
        if (!lmsPointLogRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pointLog.setId(id);
        return ResponseEntity.ok(lmsPointLogRepository.save(pointLog));
    }

    @DeleteMapping("/point-logs/{id}")
    public ResponseEntity<Void> deletePointLog(@PathVariable Long id) {
        if (!lmsPointLogRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsPointLogRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Forums CRUD ===
    @GetMapping("/forums")
    public ResponseEntity<Page<LmsForum>> getAllForums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsForumRepository.findAll(pageable));
    }

    @GetMapping("/forums/{id}")
    public ResponseEntity<LmsForum> getForum(@PathVariable Long id) {
        return lmsForumRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/forums")
    public ResponseEntity<LmsForum> createForum(@RequestBody LmsForum forum) {
        return ResponseEntity.ok(lmsForumRepository.save(forum));
    }

    @PutMapping("/forums/{id}")
    public ResponseEntity<LmsForum> updateForum(@PathVariable Long id, @RequestBody LmsForum forum) {
        if (!lmsForumRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        forum.setId(id);
        return ResponseEntity.ok(lmsForumRepository.save(forum));
    }

    @DeleteMapping("/forums/{id}")
    public ResponseEntity<Void> deleteForum(@PathVariable Long id) {
        if (!lmsForumRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsForumRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Forum Posts CRUD ===
    @GetMapping("/forum-posts")
    public ResponseEntity<Page<LmsForumPost>> getAllForumPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsForumPostRepository.findAll(pageable));
    }

    @GetMapping("/forum-posts/{id}")
    public ResponseEntity<LmsForumPost> getForumPost(@PathVariable Long id) {
        return lmsForumPostRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/forum-posts")
    public ResponseEntity<LmsForumPost> createForumPost(@RequestBody LmsForumPost forumPost) {
        return ResponseEntity.ok(lmsForumPostRepository.save(forumPost));
    }

    @PutMapping("/forum-posts/{id}")
    public ResponseEntity<LmsForumPost> updateForumPost(@PathVariable Long id, @RequestBody LmsForumPost forumPost) {
        if (!lmsForumPostRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        forumPost.setId(id);
        return ResponseEntity.ok(lmsForumPostRepository.save(forumPost));
    }

    @DeleteMapping("/forum-posts/{id}")
    public ResponseEntity<Void> deleteForumPost(@PathVariable Long id) {
        if (!lmsForumPostRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsForumPostRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Peer Reviews CRUD ===
    @GetMapping("/peer-reviews")
    public ResponseEntity<Page<LmsPeerReview>> getAllPeerReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lmsPeerReviewRepository.findAll(pageable));
    }

    @GetMapping("/peer-reviews/{id}")
    public ResponseEntity<LmsPeerReview> getPeerReview(@PathVariable Long id) {
        return lmsPeerReviewRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/peer-reviews")
    public ResponseEntity<LmsPeerReview> createPeerReview(@RequestBody LmsPeerReview peerReview) {
        return ResponseEntity.ok(lmsPeerReviewRepository.save(peerReview));
    }

    @PutMapping("/peer-reviews/{id}")
    public ResponseEntity<LmsPeerReview> updatePeerReview(@PathVariable Long id, @RequestBody LmsPeerReview peerReview) {
        if (!lmsPeerReviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        peerReview.setId(id);
        return ResponseEntity.ok(lmsPeerReviewRepository.save(peerReview));
    }

    @DeleteMapping("/peer-reviews/{id}")
    public ResponseEntity<Void> deletePeerReview(@PathVariable Long id) {
        if (!lmsPeerReviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lmsPeerReviewRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
