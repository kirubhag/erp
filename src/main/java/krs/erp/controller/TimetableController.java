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
import krs.erp.service.SchedulingService;

@RestController
@RequestMapping("/api/timetables")
public class TimetableController {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private SchedulingService schedulingService;

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

        DayOfWeek dayOfWeekEnum = null;
        if (dayOfWeek != null && !dayOfWeek.isEmpty()) {
            try {
                dayOfWeekEnum = DayOfWeek.valueOf(dayOfWeek);
            } catch (IllegalArgumentException e) {
                // Return empty list or bad request? For list filtering, ignoring invalid enum
                // is safer or returning empty.
                // Let's return empty page if invalid enum is passed to avoid crash
                return ResponseEntity.ok(new PageImpl<>(List.of(), pageable, 0));
            }
        }

        if (search != null && !search.isEmpty()) {
            timetables = timetableRepository.searchTimetables(search);
        } else if (gradeLevel != null && !gradeLevel.isEmpty() && dayOfWeekEnum != null) {
            timetables = timetableRepository.findByGradeLevelAndDay(gradeLevel, dayOfWeekEnum);
        } else if (className != null && !className.isEmpty() && dayOfWeekEnum != null) {
            timetables = timetableRepository.findByClassAndDay(className, dayOfWeekEnum);
        } else if (gradeLevel != null && !gradeLevel.isEmpty()) {
            timetables = timetableRepository.findByGradeLevelAndIsActive(gradeLevel, 1);
        } else if (className != null && !className.isEmpty()) {
            timetables = timetableRepository.findByClassNameAndIsActive(className, 1);
        } else if (dayOfWeekEnum != null) {
            timetables = timetableRepository.findByDayOfWeekAndIsActive(dayOfWeekEnum, 1);
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

        // Handle case where start index is out of bounds
        if (start > timetables.size()) {
            return ResponseEntity.ok(new PageImpl<>(List.of(), pageable, timetables.size()));
        }

        Page<Timetable> timetablePage = new PageImpl<>(
                timetables.subList(start, end),
                pageable,
                timetables.size());

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

        // Check for room type constraints could govern basic collision detection
        // Assuming the entity relationships (Room, Class, etc.) are populated by
        // Jackson
        // from the JSON request (e.g., "room": {"id": 1}).

        String roomName = (timetable.getRoom() != null) ? timetable.getRoom().getRoomName() : null;

        // Check for room conflicts
        if (roomName != null) {
            List<Timetable> conflicts = timetableRepository.findConflictingTimetables(
                    roomName,
                    timetable.getDayOfWeek(),
                    timetable.getStartTime(),
                    timetable.getEndTime());

            if (!conflicts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Room " + roomName + " is already scheduled on " +
                                timetable.getDayOfWeek() + " during this time");
            }
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
        String roomName = (timetableDetails.getRoom() != null) ? timetableDetails.getRoom().getRoomName() : null;

        // Check for room conflicts (excluding current record)
        if (roomName != null) {
            List<Timetable> conflicts = timetableRepository.findConflictingTimetables(
                    roomName,
                    timetableDetails.getDayOfWeek(),
                    timetableDetails.getStartTime(),
                    timetableDetails.getEndTime());

            conflicts.removeIf(t -> t.getId().equals(id));

            if (!conflicts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Room " + roomName + " is already scheduled on " +
                                timetableDetails.getDayOfWeek() + " during this time");
            }
        }

        timetable.setErpClass(timetableDetails.getErpClass());
        // timetable.setGradeLevel(timetableDetails.getGradeLevel()); // Keeping field
        // if it represents a cached/denormalized value or removing?
        // Wait, Timetable model no longer has gradeLevel field? It was removed in
        // refactor.
        // Need to check Timetable.java again. The refactor REPLACED the String fields
        // with Relationships.
        // So I must NOT set gradeLevel directly.

        timetable.setAcademicYear(timetableDetails.getAcademicYear());
        // timetable.setSemester(timetableDetails.getSemester()); // Removed/Missing in
        // new model?
        timetable.setDayOfWeek(timetableDetails.getDayOfWeek());
        timetable.setStartTime(timetableDetails.getStartTime());
        timetable.setEndTime(timetableDetails.getEndTime());
        timetable.setSubject(timetableDetails.getSubject());
        timetable.setTeacher(timetableDetails.getTeacher());
        timetable.setRoom(timetableDetails.getRoom());
        timetable.setPeriodNumber(timetableDetails.getPeriodNumber());
        timetable.setDescription(timetableDetails.getDescription());

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

    // Generate Timetables
    @PostMapping("/generate")
    public ResponseEntity<?> generateTimetable(@RequestParam String academicYear) {
        try {
            int count = schedulingService.generateSchedule(academicYear);
            return ResponseEntity
                    .ok("Successfully generated " + count + " timetable entries for academic year " + academicYear);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate schedule: " + e.getMessage());
        }
    }

    // Get timetable by class and academic year
    @GetMapping("/schedule")
    public ResponseEntity<List<Timetable>> getClassSchedule(
            @RequestParam String className,
            @RequestParam String academicYear) {

        List<Timetable> timetables = timetableRepository.findByClassAndAcademicPeriod(
                className,
                academicYear);
        return ResponseEntity.ok(timetables);
    }
}
