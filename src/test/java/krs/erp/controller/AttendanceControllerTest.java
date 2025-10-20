package krs.erp.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.model.Attendance;
import krs.erp.repository.AttendanceRepository;

/**
 * Unit tests for AttendanceController
 */
@WebMvcTest(AttendanceController.class)
@ActiveProfiles("test")
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testGetAttendanceByDate() throws Exception {
        // Given
        LocalDate testDate = LocalDate.of(2025, 10, 20);
        List<Attendance> attendanceList = Arrays.asList(
            createTestAttendance(1L, testDate, Attendance.AttendanceStatus.PRESENT),
            createTestAttendance(2L, testDate, Attendance.AttendanceStatus.ABSENT)
        );
        when(attendanceRepository.findByAttendanceDate(testDate)).thenReturn(attendanceList);

        // When & Then
        mockMvc.perform(get("/api/attendance/date/2025-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(attendanceRepository).findByAttendanceDate(testDate);
    }

    @Test
    @WithMockUser
    void testGetAttendanceStatistics() throws Exception {
        // Given
        LocalDate testDate = LocalDate.of(2025, 10, 20);
        when(attendanceRepository.countPresentStudentsByDate(testDate)).thenReturn(25L);
        when(attendanceRepository.countAbsentStudentsByDate(testDate)).thenReturn(5L);
        when(attendanceRepository.countLateStudentsByDate(testDate)).thenReturn(3L);

        // When & Then
        mockMvc.perform(get("/api/attendance/statistics/date/2025-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2025-10-20"))
                .andExpect(jsonPath("$.presentStudents").value(25))
                .andExpect(jsonPath("$.absentStudents").value(5))
                .andExpect(jsonPath("$.lateStudents").value(3));

        verify(attendanceRepository).countPresentStudentsByDate(testDate);
        verify(attendanceRepository).countAbsentStudentsByDate(testDate);
        verify(attendanceRepository).countLateStudentsByDate(testDate);
    }

    @Test
    @WithMockUser
    void testCreateAttendance() throws Exception {
        // Given
        Attendance newAttendance = createTestAttendance(null, LocalDate.now(), Attendance.AttendanceStatus.PRESENT);
        Attendance savedAttendance = createTestAttendance(1L, LocalDate.now(), Attendance.AttendanceStatus.PRESENT);
        
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(savedAttendance);

        // When & Then
        mockMvc.perform(post("/api/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAttendance))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PRESENT"));

        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    @WithMockUser
    void testGetAttendanceById() throws Exception {
        // Given
        Attendance attendance = createTestAttendance(1L, LocalDate.now(), Attendance.AttendanceStatus.PRESENT);
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));

        // When & Then
        mockMvc.perform(get("/api/attendance/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PRESENT"));

        verify(attendanceRepository).findById(1L);
    }

    @Test
    @WithMockUser
    void testGetAttendanceByIdNotFound() throws Exception {
        // Given
        when(attendanceRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/attendance/999"))
                .andExpect(status().isNotFound());

        verify(attendanceRepository).findById(999L);
    }

    @Test
    @WithMockUser
    void testUpdateAttendance() throws Exception {
        // Given
        Attendance existingAttendance = createTestAttendance(1L, LocalDate.now(), Attendance.AttendanceStatus.PRESENT);
        Attendance updatedAttendance = createTestAttendance(1L, LocalDate.now(), Attendance.AttendanceStatus.LATE);
        
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existingAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(updatedAttendance);

        // When & Then
        mockMvc.perform(put("/api/attendance/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAttendance))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LATE"));

        verify(attendanceRepository).findById(1L);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    @WithMockUser
    void testDeleteAttendance() throws Exception {
        // Given
        Attendance attendance = createTestAttendance(1L, LocalDate.now(), Attendance.AttendanceStatus.PRESENT);
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));

        // When & Then
        mockMvc.perform(delete("/api/attendance/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(attendanceRepository).findById(1L);
        verify(attendanceRepository).delete(attendance);
    }

    @Test
    @WithMockUser
    void testGetAttendanceByDateRange() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2025, 10, 1);
        LocalDate endDate = LocalDate.of(2025, 10, 31);
        List<Attendance> attendanceList = Arrays.asList(
            createTestAttendance(1L, LocalDate.of(2025, 10, 15), Attendance.AttendanceStatus.PRESENT)
        );
        when(attendanceRepository.findByAttendanceDateBetween(startDate, endDate)).thenReturn(attendanceList);

        // When & Then
        mockMvc.perform(get("/api/attendance/range")
                .param("startDate", "2025-10-01")
                .param("endDate", "2025-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(attendanceRepository).findByAttendanceDateBetween(startDate, endDate);
    }

    private Attendance createTestAttendance(Long id, LocalDate date, Attendance.AttendanceStatus status) {
        Attendance attendance = new Attendance();
        attendance.setId(id);
        attendance.setAttendanceDate(date);
        attendance.setStatus(status);
        attendance.setAttendanceType(Attendance.AttendanceType.STUDENT);
        attendance.setCheckInTime(LocalTime.of(8, 30));
        attendance.setRemarks("Test attendance record");
        attendance.setExcused(false);
        return attendance;
    }
}