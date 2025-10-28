package krs.erp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import jakarta.validation.Valid;
import krs.erp.model.Timetable;
import krs.erp.model.Timetable.DayOfWeek;
import krs.erp.repository.TimetableRepository;

@RestController
@RequestMapping("/api/timetables")
@CrossOrigin(origins = "*")
public class TimetableController {

    @Autowired
    private TimetableRepository timetableRepository;

    // Get all timetables with pagination and filtering
    @GetMapping
    public ResponseEntity<Page<Timetable>> getAllTimetables(
            @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String dayOfWeek,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String subjectCode,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dayOfWeek,startTime") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.split(",")));
        List<Timetable> timetables;

        if (search != null && !search.isEmpty()) {
            timetables = timetableRepository.searchTimetables(search);
        } else if (gradeLevel != null && !gradeLevel.isEmpty() && dayOfWeek != null && !dayOfWeek.isEmpty()) {
            timetables = timetableRepository.findByGradeLevelAndDay(gradeLevel, DayOfWeek.valueOf(dayOfWeek));
        } else if (className != null && !className.isEmpty() && dayOfWeek != null && !dayOfWeek.isEmpty()) {
            timetables = timetableRepository.findByClassAndDay(className, DayOfWeek.valueOf(dayOfWeek));
        } else if (gradeLevel != null && !gradeLevel.isEmpty()) {
            timetables = timetableRepository.findByGradeLevelAndIsActive(gradeLevel, 1);
        } else if (className != null && !className.isEmpty()) {
            timetables = timetableRepository.findByClassNameAndIsActive(className, 1);
        } else if (dayOfWeek != null && !dayOfWeek.isEmpty()) {
            timetables = timetableRepository.findByDayOfWeekAndIsActive(DayOfWeek.valueOf(dayOfWeek), 1);
        } else if (academicYear != null && !academicYear.isEmpty()) {
            timetables = timetableRepository.findByAcademicYearAndIsActive(academicYear, 1);
        } else if (subjectCode != null && !subjectCode.isEmpty()) {
            timetables = timetableRepository.findBySubjectCodeAndIsActive(subjectCode, 1);
        } else if (teacherId != null && !teacherId.isEmpty()) {
            timetables = timetableRepository.findByTeacherIdAndIsActive(teacherId, 1);
        } else if (roomNumber != null && !roomNumber.isEmpty()) {
            timetables = timetableRepository.findByRoomNumberAndIsActive(roomNumber, 1);
        } else {
            timetables = timetableRepository.findAllActiveTimetables();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), timetables.size());

        Page<Timetable> timetablePage = new PageImpl<>(
                timetables.subList(start, end),
                pageable,
                timetables.size()
        );

        return ResponseEntity.ok(timetablePage);
    }

    // Get timetable by ID
    @GetMapping("/{id}")
    public ResponseEntity<Timetable> getTimetableById(@PathVariable Long id) {
        Optional<Timetable> timetable = timetableRepository.findById(id);
        return timetable.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new timetable
    @PostMapping
    public ResponseEntity<?> createTimetable(@Valid @RequestBody Timetable timetable) {
        // Check for existing timetable code
        if (timetableRepository.existsByTimetableCode(timetable.getTimetableCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Timetable with code " + timetable.getTimetableCode() + " already exists");
        }

        // Check for room conflicts
        List<Timetable> conflicts = timetableRepository.findConflictingTimetables(
                timetable.getRoomNumber(),
                timetable.getDayOfWeek(),
                timetable.getStartTime(),
                timetable.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Room " + timetable.getRoomNumber() + " is already scheduled on " +
                            timetable.getDayOfWeek() + " during this time");
        }

        timetable.markAsActive();
        Timetable savedTimetable = timetableRepository.save(timetable);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTimetable);
    }

    // Update timetable
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTimetable(@PathVariable Long id, @Valid @RequestBody Timetable timetableDetails) {
        Optional<Timetable> timetableOptional = timetableRepository.findById(id);

        if (!timetableOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Timetable timetable = timetableOptional.get();

        // Check for timetable code conflicts (excluding current record)
        if (!timetable.getTimetableCode().equals(timetableDetails.getTimetableCode()) &&
                timetableRepository.existsByTimetableCode(timetableDetails.getTimetableCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Timetable with code " + timetableDetails.getTimetableCode() + " already exists");
        }

        // Check for room conflicts (excluding current record)
        List<Timetable> conflicts = timetableRepository.findConflictingTimetables(
                timetableDetails.getRoomNumber(),
                timetableDetails.getDayOfWeek(),
                timetableDetails.getStartTime(),
                timetableDetails.getEndTime()
        );

        conflicts.removeIf(t -> t.getId().equals(id));

        if (!conflicts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Room " + timetableDetails.getRoomNumber() + " is already scheduled on " +
                            timetableDetails.getDayOfWeek() + " during this time");
        }

        timetable.setTimetableCode(timetableDetails.getTimetableCode());
        timetable.setClassName(timetableDetails.getClassName());
        timetable.setGradeLevel(timetableDetails.getGradeLevel());
        timetable.setAcademicYear(timetableDetails.getAcademicYear());
        timetable.setSemester(timetableDetails.getSemester());
        timetable.setDayOfWeek(timetableDetails.getDayOfWeek());
        timetable.setStartTime(timetableDetails.getStartTime());
        timetable.setEndTime(timetableDetails.getEndTime());
        timetable.setSubjectName(timetableDetails.getSubjectName());
        timetable.setSubjectCode(timetableDetails.getSubjectCode());
        timetable.setTeacherName(timetableDetails.getTeacherName());
        timetable.setTeacherId(timetableDetails.getTeacherId());
        timetable.setRoomNumber(timetableDetails.getRoomNumber());
        timetable.setBuilding(timetableDetails.getBuilding());
        timetable.setPeriodNumber(timetableDetails.getPeriodNumber());
        timetable.setNotes(timetableDetails.getNotes());
        timetable.setIsLabSession(timetableDetails.getIsLabSession());

        Timetable updatedTimetable = timetableRepository.save(timetable);
        return ResponseEntity.ok(updatedTimetable);
    }

    // Soft delete timetable
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTimetable(@PathVariable Long id) {
        Optional<Timetable> timetableOptional = timetableRepository.findById(id);

        if (!timetableOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Timetable timetable = timetableOptional.get();
        timetable.markAsDeleted();
        timetableRepository.save(timetable);

        return ResponseEntity.ok().body("Timetable deleted successfully");
    }

    // Get all active timetables
    @GetMapping("/active")
    public ResponseEntity<List<Timetable>> getActiveTimetables() {
        List<Timetable> timetables = timetableRepository.findAllActiveTimetables();
        return ResponseEntity.ok(timetables);
    }

    // Get timetables by grade level
    @GetMapping("/grade/{gradeLevel}")
    public ResponseEntity<List<Timetable>> getTimetablesByGrade(@PathVariable String gradeLevel) {
        List<Timetable> timetables = timetableRepository.findByGradeLevelAndIsActive(gradeLevel, 1);
        return ResponseEntity.ok(timetables);
    }

    // Get timetables by class name
    @GetMapping("/class/{className}")
    public ResponseEntity<List<Timetable>> getTimetablesByClass(@PathVariable String className) {
        List<Timetable> timetables = timetableRepository.findByClassNameAndIsActive(className, 1);
        return ResponseEntity.ok(timetables);
    }

    // Get timetables by day of week
    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<List<Timetable>> getTimetablesByDay(@PathVariable String dayOfWeek) {
        try {
            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek);
            List<Timetable> timetables = timetableRepository.findByDayOfWeekAndIsActive(day, 1);
            return ResponseEntity.ok(timetables);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get timetables by subject code
    @GetMapping("/subject/{subjectCode}")
    public ResponseEntity<List<Timetable>> getTimetablesBySubject(@PathVariable String subjectCode) {
        List<Timetable> timetables = timetableRepository.findBySubjectCodeAndIsActive(subjectCode, 1);
        return ResponseEntity.ok(timetables);
    }

    // Get timetables by teacher ID
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Timetable>> getTimetablesByTeacher(@PathVariable String teacherId) {
        List<Timetable> timetables = timetableRepository.findByTeacherIdAndIsActive(teacherId, 1);
        return ResponseEntity.ok(timetables);
    }

    // Get timetables by room number
    @GetMapping("/room/{roomNumber}")
    public ResponseEntity<List<Timetable>> getTimetablesByRoom(@PathVariable String roomNumber) {
        List<Timetable> timetables = timetableRepository.findByRoomNumberAndIsActive(roomNumber, 1);
        return ResponseEntity.ok(timetables);
    }

    // Count timetables by grade
    @GetMapping("/count/grade/{gradeLevel}")
    public ResponseEntity<Long> countTimetablesByGrade(@PathVariable String gradeLevel) {
        long count = timetableRepository.countByGradeLevelAndActive(gradeLevel);
        return ResponseEntity.ok(count);
    }

    // Get timetable for class and academic period
    @GetMapping("/schedule")
    public ResponseEntity<List<Timetable>> getClassSchedule(
            @RequestParam String className,
            @RequestParam String academicYear,
            @RequestParam(required = false) String semester) {

        List<Timetable> timetables = timetableRepository.findByClassAndAcademicPeriod(
                className,
                academicYear,
                semester
        );
        return ResponseEntity.ok(timetables);
    }
}
