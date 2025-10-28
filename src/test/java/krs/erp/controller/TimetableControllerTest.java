package krs.erp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.model.Timetable;
import krs.erp.model.Timetable.DayOfWeek;
import krs.erp.repository.TimetableRepository;

@WebMvcTest(TimetableController.class)
@WithMockUser
class TimetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimetableRepository timetableRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Timetable timetable1;
    private Timetable timetable2;

    @BeforeEach
    void setUp() {
        timetable1 = new Timetable();
        timetable1.setId(1L);
        timetable1.setTimetableCode("TT-G1-2024-MON-1");
        timetable1.setClassName("1-A");
        timetable1.setGradeLevel("Grade 1");
        timetable1.setAcademicYear("2024-2025");
        timetable1.setSemester("Fall 2024");
        timetable1.setDayOfWeek(DayOfWeek.MONDAY);
        timetable1.setStartTime(LocalTime.of(8, 0));
        timetable1.setEndTime(LocalTime.of(8, 50));
        timetable1.setSubjectName("Mathematics");
        timetable1.setSubjectCode("MATH-G1");
        timetable1.setTeacherName("Mrs. Anderson");
        timetable1.setTeacherId("T001");
        timetable1.setRoomNumber("101");
        timetable1.setBuilding("Elementary Wing");
        timetable1.setPeriodNumber(1);
        timetable1.setIsLabSession(false);
        timetable1.markAsActive();

        timetable2 = new Timetable();
        timetable2.setId(2L);
        timetable2.setTimetableCode("TT-G1-2024-TUE-2");
        timetable2.setClassName("1-A");
        timetable2.setGradeLevel("Grade 1");
        timetable2.setAcademicYear("2024-2025");
        timetable2.setSemester("Fall 2024");
        timetable2.setDayOfWeek(DayOfWeek.TUESDAY);
        timetable2.setStartTime(LocalTime.of(9, 0));
        timetable2.setEndTime(LocalTime.of(9, 50));
        timetable2.setSubjectName("Science");
        timetable2.setSubjectCode("SCI-G1");
        timetable2.setTeacherName("Mr. Williams");
        timetable2.setTeacherId("T002");
        timetable2.setRoomNumber("Lab-101");
        timetable2.setBuilding("Science Block");
        timetable2.setPeriodNumber(2);
        timetable2.setIsLabSession(true);
        timetable2.markAsActive();
    }

    @Test
    void testGetAllTimetables() throws Exception {
        when(timetableRepository.findAllActiveTimetables())
            .thenReturn(Arrays.asList(timetable1, timetable2));

        mockMvc.perform(get("/api/timetables")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void testGetTimetableById() throws Exception {
        when(timetableRepository.findById(1L))
            .thenReturn(Optional.of(timetable1));

        mockMvc.perform(get("/api/timetables/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timetableCode", is("TT-G1-2024-MON-1")))
                .andExpect(jsonPath("$.className", is("1-A")))
                .andExpect(jsonPath("$.subjectName", is("Mathematics")));
    }

    @Test
    void testGetTimetableByIdNotFound() throws Exception {
        when(timetableRepository.findById(999L))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/timetables/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTimetable() throws Exception {
        when(timetableRepository.existsByTimetableCode("TT-NEW-2024-MON-1"))
            .thenReturn(false);
        when(timetableRepository.findConflictingTimetables(any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(timetableRepository.save(any(Timetable.class)))
            .thenReturn(timetable1);

        Timetable newTimetable = new Timetable();
        newTimetable.setTimetableCode("TT-NEW-2024-MON-1");
        newTimetable.setClassName("1-A");
        newTimetable.setGradeLevel("Grade 1");
        newTimetable.setAcademicYear("2024-2025");
        newTimetable.setDayOfWeek(DayOfWeek.MONDAY);
        newTimetable.setStartTime(LocalTime.of(8, 0));
        newTimetable.setEndTime(LocalTime.of(8, 50));
        newTimetable.setSubjectName("Mathematics");
        newTimetable.setSubjectCode("MATH-G1");
        newTimetable.setTeacherName("Mrs. Anderson");
        newTimetable.setTeacherId("T001");
        newTimetable.setRoomNumber("101");

        mockMvc.perform(post("/api/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTimetable))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateTimetableWithDuplicateCode() throws Exception {
        when(timetableRepository.existsByTimetableCode("TT-G1-2024-MON-1"))
            .thenReturn(true);

        mockMvc.perform(post("/api/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timetable1))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateTimetableWithRoomConflict() throws Exception {
        when(timetableRepository.existsByTimetableCode(any()))
            .thenReturn(false);
        when(timetableRepository.findConflictingTimetables(any(), any(), any(), any()))
            .thenReturn(Arrays.asList(timetable1));

        mockMvc.perform(post("/api/timetables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timetable1))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateTimetable() throws Exception {
        when(timetableRepository.findById(1L))
            .thenReturn(Optional.of(timetable1));
        when(timetableRepository.existsByTimetableCode("TT-G1-2024-MON-1"))
            .thenReturn(false);
        when(timetableRepository.findConflictingTimetables(any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(timetableRepository.save(any(Timetable.class)))
            .thenReturn(timetable1);

        timetable1.setSubjectName("Advanced Mathematics");

        mockMvc.perform(put("/api/timetables/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timetable1))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteTimetable() throws Exception {
        when(timetableRepository.findById(1L))
            .thenReturn(Optional.of(timetable1));
        when(timetableRepository.save(any(Timetable.class)))
            .thenReturn(timetable1);

        mockMvc.perform(delete("/api/timetables/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetActiveTimetables() throws Exception {
        when(timetableRepository.findAllActiveTimetables())
            .thenReturn(Arrays.asList(timetable1, timetable2));

        mockMvc.perform(get("/api/timetables/active")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetTimetablesByGrade() throws Exception {
        when(timetableRepository.findByGradeLevelAndIsActive("Grade 1", 1))
            .thenReturn(Arrays.asList(timetable1, timetable2));

        mockMvc.perform(get("/api/timetables/grade/Grade 1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetTimetablesByClass() throws Exception {
        when(timetableRepository.findByClassNameAndIsActive("1-A", 1))
            .thenReturn(Arrays.asList(timetable1, timetable2));

        mockMvc.perform(get("/api/timetables/class/1-A")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetTimetablesByDay() throws Exception {
        when(timetableRepository.findByDayOfWeekAndIsActive(DayOfWeek.MONDAY, 1))
            .thenReturn(Arrays.asList(timetable1));

        mockMvc.perform(get("/api/timetables/day/MONDAY")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetTimetablesBySubject() throws Exception {
        when(timetableRepository.findBySubjectCodeAndIsActive("MATH-G1", 1))
            .thenReturn(Arrays.asList(timetable1));

        mockMvc.perform(get("/api/timetables/subject/MATH-G1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetTimetablesByTeacher() throws Exception {
        when(timetableRepository.findByTeacherIdAndIsActive("T001", 1))
            .thenReturn(Arrays.asList(timetable1));

        mockMvc.perform(get("/api/timetables/teacher/T001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetTimetablesByRoom() throws Exception {
        when(timetableRepository.findByRoomNumberAndIsActive("101", 1))
            .thenReturn(Arrays.asList(timetable1));

        mockMvc.perform(get("/api/timetables/room/101")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testCountTimetablesByGrade() throws Exception {
        when(timetableRepository.countByGradeLevelAndActive("Grade 1"))
            .thenReturn(2L);

        mockMvc.perform(get("/api/timetables/count/grade/Grade 1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(2)));
    }

    @Test
    void testGetClassSchedule() throws Exception {
        when(timetableRepository.findByClassAndAcademicPeriod(eq("1-A"), eq("2024-2025"), any()))
            .thenReturn(Arrays.asList(timetable1, timetable2));

        mockMvc.perform(get("/api/timetables/schedule")
                .param("className", "1-A")
                .param("academicYear", "2024-2025")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
