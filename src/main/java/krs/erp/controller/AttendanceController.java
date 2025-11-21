package krs.erp.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import krs.erp.enums.EntityType;
import krs.erp.model.Attendance;
import krs.erp.model.Student;
import krs.erp.repository.AttendanceRepository;
import krs.erp.service.RecycleBinService;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private RecycleBinService recycleBinService;
    
    // Get all attendance records (only active, isActive = 1)
    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        List<Attendance> attendanceList = attendanceRepository.findByIsActive(1);
        return ResponseEntity.ok(attendanceList);
    }
    
    // Get attendance by ID
    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long id) {
        Optional<Attendance> attendance = attendanceRepository.findById(id);
        return attendance.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
    
    // Create attendance record
    @PostMapping
    public ResponseEntity<Attendance> createAttendance(@Valid @RequestBody Attendance attendance) {
        try {
            Attendance savedAttendance = attendanceRepository.save(attendance);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAttendance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Update attendance record
    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable Long id, 
                                                      @Valid @RequestBody Attendance attendanceDetails) {
        Optional<Attendance> optionalAttendance = attendanceRepository.findById(id);
        
        if (optionalAttendance.isPresent()) {
            Attendance attendance = optionalAttendance.get();
            attendance.setAttendanceDate(attendanceDetails.getAttendanceDate());
            attendance.setCheckInTime(attendanceDetails.getCheckInTime());
            attendance.setCheckOutTime(attendanceDetails.getCheckOutTime());
            attendance.setStatus(attendanceDetails.getStatus());
            attendance.setRemarks(attendanceDetails.getRemarks());
            attendance.setExcused(attendanceDetails.getExcused());
            
            Attendance updatedAttendance = attendanceRepository.save(attendance);
            return ResponseEntity.ok(updatedAttendance);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    // Delete attendance record (soft delete with recycle bin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        Optional<Attendance> attendanceOpt = attendanceRepository.findById(id);
        if (attendanceOpt.isPresent()) {
            // Soft delete using recycle bin service
            recycleBinService.softDeleteEntity(id, EntityType.ATTENDANCE, "current-user", "User deleted attendance record");
            
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Get attendance by date
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Attendance>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Attendance> attendance = attendanceRepository.findByAttendanceDate(date);
        return ResponseEntity.ok(attendance);
    }
    
    // Get student attendance by date
    @GetMapping("/student/{studentId}/date/{date}")
    public ResponseEntity<List<Attendance>> getStudentAttendanceByDate(
            @PathVariable Long studentId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Attendance> attendance = attendanceRepository.findByStudentIdAndAttendanceDate(studentId, date);
        return ResponseEntity.ok(attendance);
    }
    
    // Get student attendance history
    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<List<Attendance>> getStudentAttendanceHistory(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Attendance> attendance = attendanceRepository.findStudentAttendanceHistory(studentId, startDate, endDate);
        return ResponseEntity.ok(attendance);
    }
    
    // Get staff attendance by date
    @GetMapping("/staff/{staffId}/date/{date}")
    public ResponseEntity<List<Attendance>> getStaffAttendanceByDate(
            @PathVariable Long staffId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Attendance> attendance = attendanceRepository.findByStaffIdAndAttendanceDate(staffId, date);
        return ResponseEntity.ok(attendance);
    }
    
    // Get attendance by date range
    @GetMapping("/range")
    public ResponseEntity<List<Attendance>> getAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Attendance> attendance = attendanceRepository.findByAttendanceDateBetween(startDate, endDate);
        return ResponseEntity.ok(attendance);
    }

    // Get attendance statistics for a specific date
    @GetMapping("/statistics/date/{date}")
    public ResponseEntity<Map<String, Object>> getAttendanceStatisticsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Long presentStudents = attendanceRepository.countPresentStudentsByDate(date);
        Long absentStudents = attendanceRepository.countAbsentStudentsByDate(date);
        Long lateStudents = attendanceRepository.countLateStudentsByDate(date);
        Long totalStudents = presentStudents + absentStudents + lateStudents;
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("date", date);
        statistics.put("presentStudents", presentStudents);
        statistics.put("absentStudents", absentStudents);
        statistics.put("lateStudents", lateStudents);
        statistics.put("totalStudents", totalStudents);
        statistics.put("attendanceRate", totalStudents > 0 ? (presentStudents.doubleValue() / totalStudents.doubleValue()) * 100 : 0);
        
        return ResponseEntity.ok(statistics);
    }

    // Get student attendance statistics
    @GetMapping("/student/{studentId}/statistics")
    public ResponseEntity<Map<String, Object>> getStudentAttendanceStatistics(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long presentDays = attendanceRepository.countStudentPresentDays(studentId, startDate, endDate);
        Long absentDays = attendanceRepository.countStudentAbsentDays(studentId, startDate, endDate);
        Long totalDays = presentDays + absentDays;
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("presentDays", presentDays);
        statistics.put("absentDays", absentDays);
        statistics.put("totalDays", totalDays);
        statistics.put("attendanceRate", totalDays > 0 ? (presentDays.doubleValue() / totalDays.doubleValue()) * 100 : 0);
        
        return ResponseEntity.ok(statistics);
    }
    
    // Get daily attendance summary
    @GetMapping("/summary/{date}")
    public ResponseEntity<Map<String, Object>> getDailyAttendanceSummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Long presentStudents = attendanceRepository.countPresentStudentsByDate(date);
        Long absentStudents = attendanceRepository.countAbsentStudentsByDate(date);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("date", date);
        summary.put("presentStudents", presentStudents);
        summary.put("absentStudents", absentStudents);
        summary.put("totalStudents", presentStudents + absentStudents);
        
        return ResponseEntity.ok(summary);
    }
    
    // Get attendance by grade level and date
    @GetMapping("/grade/{gradeLevel}/date/{date}")
    public ResponseEntity<List<Attendance>> getAttendanceByGradeLevelAndDate(
            @PathVariable String gradeLevel,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            Student.GradeLevel grade = Student.GradeLevel.valueOf(gradeLevel.toUpperCase());
            List<Attendance> attendance = attendanceRepository.findAttendanceByGradeLevelAndDate(grade, date);
            return ResponseEntity.ok(attendance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get late arrivals and early departures
    @GetMapping("/irregular")
    public ResponseEntity<List<Attendance>> getIrregularAttendance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Attendance> attendance = attendanceRepository.findLateOrEarlyDepartures(startDate, endDate);
        return ResponseEntity.ok(attendance);
    }
    
    // Mark student present
    @PostMapping("/student/{studentId}/present")
    public ResponseEntity<Attendance> markStudentPresent(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Attendance attendance = new Attendance();
        attendance.setAttendanceDate(date);
        attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
        attendance.setAttendanceType(Attendance.AttendanceType.STUDENT);
        // Note: You would need to set the student entity here
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAttendance);
    }
    
    // Mark student absent
    @PostMapping("/student/{studentId}/absent")
    public ResponseEntity<Attendance> markStudentAbsent(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String remarks) {
        
        Attendance attendance = new Attendance();
        attendance.setAttendanceDate(date);
        attendance.setStatus(Attendance.AttendanceStatus.ABSENT);
        attendance.setAttendanceType(Attendance.AttendanceType.STUDENT);
        attendance.setRemarks(remarks);
        // Note: You would need to set the student entity here
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAttendance);
    }
}