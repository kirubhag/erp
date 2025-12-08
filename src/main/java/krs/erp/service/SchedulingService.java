package krs.erp.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.ErpClass;
import krs.erp.model.Room;
import krs.erp.model.Staff;
import krs.erp.model.Subject;
import krs.erp.model.Timetable;
import krs.erp.model.Timetable.DayOfWeek;
import krs.erp.repository.ErpClassRepository;
import krs.erp.repository.RoomRepository;
import krs.erp.repository.StaffRepository;
import krs.erp.repository.SubjectRepository;
import krs.erp.repository.TimetableRepository;

/**
 * Service to handle automated schedule generation and validation
 */
@Service
public class SchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);

    private static final int PERIODS_PER_DAY = 8;
    private static final LocalTime SCHOOL_START_TIME = LocalTime.of(9, 0);
    private static final int PERIOD_DURATION_MINUTES = 60; // Assuming 1 hour per period

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private ErpClassRepository classRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Generate a complete schedule for the given academic year
     * 
     * @param academicYear The academic year to generate for
     * @return Number of timetable entries created
     */
    @Transactional
    public int generateSchedule(String academicYear) {
        logger.info("Starting schedule generation for Academic Year: {}", academicYear);

        // 1. Clean existing schedule for this year (Optional: could be a parameter to
        // keep existing)
        // For V1, we'll assume a fresh generation or we should implement a delete
        // method in repo
        List<Timetable> existing = timetableRepository.findByAcademicYear(academicYear);
        if (!existing.isEmpty()) {
            logger.info("Clearing {} existing entries for {}", existing.size(), academicYear);
            timetableRepository.deleteAll(existing);
        }

        List<ErpClass> classes = classRepository.findByAcademicYear(academicYear);
        if (classes.isEmpty()) {
            logger.warn("No classes found for academic year {}", academicYear);
            return 0;
        }

        int totalEntries = 0;
        Random random = new Random();

        for (ErpClass erpClass : classes) {
            logger.info("Scheduling for Class: {} ({})", erpClass.getClassName(), erpClass.getClassCode());

            // Resolve Room for the class (Home Room)
            Room homeRoom = resolveRoomForClass(erpClass);

            // Get Subjects for the class
            // If class has no specific subjects assigned, fetch generic subjects for the
            // grade level
            List<Subject> subjects = new ArrayList<>(erpClass.getSubjects());
            if (subjects.isEmpty()) {
                subjects = subjectRepository.findByGradeLevel(erpClass.getGradeLevel());
            }

            if (subjects.isEmpty()) {
                logger.warn("No subjects found for class/grade {}", erpClass.getClassCode());
                continue;
            }

            // For each subject, determine teacher and periods needed
            for (Subject subject : subjects) {
                int periodsNeeded = subject.getHoursPerWeek() != null ? subject.getHoursPerWeek() : 4; // Default 4
                Staff teacher = resolveTeacherForSubject(erpClass, subject);

                if (teacher == null) {
                    logger.warn("Could not resolve teacher for Subject: {} in Class: {}", subject.getSubjectName(),
                            erpClass.getClassCode());
                    continue; // Skip subject
                }

                // Try to assign slots
                int assigned = 0;
                int attempts = 0;
                while (assigned < periodsNeeded && attempts < 50) {
                    attempts++;

                    // Pick a random Day and Period
                    DayOfWeek day = DayOfWeek.values()[random.nextInt(5)]; // Mon-Fri
                    int periodNum = random.nextInt(PERIODS_PER_DAY) + 1; // 1 to 8

                    // Check availability
                    if (isSlotAvailable(erpClass, teacher, homeRoom, day, periodNum, academicYear)) {
                        createAndSaveTimetableEntry(erpClass, subject, teacher, homeRoom, day, periodNum, academicYear);
                        assigned++;
                    }
                }

                if (assigned < periodsNeeded) {
                    logger.warn("Could not fully schedule Subject: {} for Class: {} (Assigned: {}/{})",
                            subject.getSubjectName(), erpClass.getClassCode(), assigned, periodsNeeded);
                }
                totalEntries += assigned;
            }
        }

        logger.info("Schedule generation completed. Total entries created: {}", totalEntries);
        return totalEntries;
    }

    private Room resolveRoomForClass(ErpClass erpClass) {
        if (erpClass.getRoomNumber() != null) {
            Room room = roomRepository.findByRoomName(erpClass.getRoomNumber());
            if (room != null)
                return room;
        }
        // Fallback or explicit mapping logic
        return null; // Null room means "TBD" or no room assigned yet
    }

    private Staff resolveTeacherForSubject(ErpClass erpClass, Subject subject) {
        // 1. Look in class's assigned teachers for a match (by dept/subject?)
        // Since we don't have a direct link, let's try to match Department with Subject
        // Category
        if (erpClass.getTeachers() != null && !erpClass.getTeachers().isEmpty()) {
            for (Staff t : erpClass.getTeachers()) {
                if (matchesSubject(t, subject))
                    return t;
            }
        }

        // 2. Fallback: Class Teacher?
        if (erpClass.getClassTeacher() != null) {
            // Check if class teacher basically teaches everything (Primary) or matches
            // For simplicity in V1, fallback to Class Teacher if no specialist found
            return erpClass.getClassTeacher();
        }

        // 3. Fallback: Any teacher in the system with matching department?
        if (subject.getCategory() != null) {
            List<Staff> specialists = staffRepository.findByDepartment(subject.getCategory());
            if (!specialists.isEmpty())
                return specialists.get(0); // Pick first
        }

        return null;
    }

    private boolean matchesSubject(Staff teacher, Subject subject) {
        if (teacher.getDepartment() != null && subject.getCategory() != null) {
            return teacher.getDepartment().equalsIgnoreCase(subject.getCategory());
        }
        return false;
    }

    private boolean isSlotAvailable(ErpClass erpClass, Staff teacher, Room room, DayOfWeek day, int period,
            String year) {
        // 1. Check Class availability (Class cannot have 2 subjects at once)
        if (timetableRepository.existsByErpClassAndDayOfWeekAndPeriodNumberAndAcademicYear(erpClass, day, period,
                year)) {
            return false;
        }

        // 2. Check Teacher availability (Teacher cannot teach 2 classes at once)
        if (teacher != null && timetableRepository.existsByTeacherAndDayOfWeekAndPeriodNumberAndAcademicYear(teacher,
                day, period, year)) {
            return false;
        }

        // 3. Check Room availability (Room cannot host 2 classes). Only check if room
        // is assigned.
        if (room != null && timetableRepository.existsByRoomAndDayOfWeekAndPeriodNumberAndAcademicYear(room, day,
                period, year)) {
            return false;
        }

        return true;
    }

    private void createAndSaveTimetableEntry(ErpClass erpClass, Subject subject, Staff teacher, Room room,
            DayOfWeek day, int periodNum, String academicYear) {
        Timetable entry = new Timetable();
        entry.setErpClass(erpClass);
        entry.setSubject(subject);
        entry.setTeacher(teacher);
        entry.setRoom(room);
        entry.setDayOfWeek(day);
        entry.setPeriodNumber(periodNum);
        entry.setAcademicYear(academicYear);

        // Calculate times
        LocalTime startTime = SCHOOL_START_TIME.plusMinutes((periodNum - 1) * PERIOD_DURATION_MINUTES);
        LocalTime endTime = startTime.plusMinutes(PERIOD_DURATION_MINUTES);

        // Lunch break adjustment? Assuming continuous for V1, or maybe 12-1 is break.
        // If period 4 ends at 13:00, that's fine.

        entry.setStartTime(startTime);
        entry.setEndTime(endTime);
        entry.setDescription(subject.getSubjectName());
        entry.markAsActive();

        timetableRepository.save(entry);
    }
}
