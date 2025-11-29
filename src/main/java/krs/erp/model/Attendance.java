package krs.erp.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "attendance")
@AttributeOverride(name = "id", column = @Column(name = "attendance_id"))
public class Attendance extends BaseEntity {
    
    @NotNull(message = "Date is required")
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;
    
    @Column(name = "check_in_time")
    private LocalTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalTime checkOutTime;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", nullable = false)
    private AttendanceType attendanceType;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
    
    @Column(name = "excused")
    private Boolean excused = false;
    
    // Relationships - either student or staff
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    @JsonIgnore
    private Staff staff;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    @JsonIgnore
    private User recordedBy;
    
    // Enums
    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE, EARLY_DEPARTURE, EXCUSED
    }
    
    public enum AttendanceType {
        STUDENT, STAFF
    }
    
    // Constructors
    public Attendance() {}
    
    public Attendance(LocalDate attendanceDate, AttendanceStatus status, AttendanceType attendanceType) {
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.attendanceType = attendanceType;
    }
    
    // Getters and Setters
    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }
    
    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
    
    public LocalTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }
    
    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
    
    public AttendanceStatus getStatus() {
        return status;
    }
    
    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
    
    public AttendanceType getAttendanceType() {
        return attendanceType;
    }
    
    public void setAttendanceType(AttendanceType attendanceType) {
        this.attendanceType = attendanceType;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Boolean getExcused() {
        return excused;
    }
    
    public void setExcused(Boolean excused) {
        this.excused = excused;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Staff getStaff() {
        return staff;
    }
    
    public void setStaff(Staff staff) {
        this.staff = staff;
    }
    
    public User getRecordedBy() {
        return recordedBy;
    }
    
    public void setRecordedBy(User recordedBy) {
        this.recordedBy = recordedBy;
    }
    
    // Helper methods
    public String getAttendeeType() {
        return attendanceType.toString();
    }
    
    @JsonIgnore
    public String getAttendeeName() {
        if (student != null) {
            return student.getFullName();
        } else if (staff != null) {
            return staff.getFullName();
        }
        return "Unknown";
    }
    
    @JsonIgnore
    public String getAttendeeId() {
        if (student != null) {
            return student.getStudentId();
        } else if (staff != null) {
            return staff.getStaffId();
        }
        return "Unknown";
    }
}