package krs.erp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import krs.erp.model.ErpClass;
import krs.erp.model.Room;
import krs.erp.model.Staff;
import krs.erp.model.Subject;
import krs.erp.model.Timetable;
import krs.erp.repository.ErpClassRepository;
import krs.erp.repository.RoomRepository;
import krs.erp.repository.StaffRepository;
import krs.erp.repository.SubjectRepository;
import krs.erp.repository.TimetableRepository;

@ExtendWith(MockitoExtension.class)
public class SchedulingServiceTest {

    @Mock
    private TimetableRepository timetableRepository;

    @Mock
    private ErpClassRepository classRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private SchedulingService schedulingService;

    @Test
    public void testGenerateSchedule_BasicFlow() {
        // Setup Class
        ErpClass erpClass = new ErpClass();
        erpClass.setClassCode("10A");
        erpClass.setClassName("Class 10A");
        erpClass.setGradeLevel("Grade 10");
        erpClass.setRoomNumber("101");

        // Setup Room
        Room room = new Room();
        room.setRoomName("101");
        when(roomRepository.findByRoomName("101")).thenReturn(room);

        // Setup Subject
        Subject math = new Subject();
        math.setSubjectName("Math");
        math.setSubjectCode("MATH101");
        math.setHoursPerWeek(4);
        math.setCategory("Science");

        // Setup Teacher
        Staff teacher = new Staff();
        teacher.setFirstName("John");
        teacher.setDepartment("Science");

        // Link Subject to Class (Mocking behavior)
        erpClass.setSubjects(new HashSet<>(Collections.singletonList(math)));

        // Link Teacher to Class (Mocking behavior)
        erpClass.setTeachers(new HashSet<>(Collections.singletonList(teacher)));

        // Mock Repositories
        when(classRepository.findByAcademicYear("2024-2025")).thenReturn(Collections.singletonList(erpClass));
        when(timetableRepository.findByAcademicYear("2024-2025")).thenReturn(new ArrayList<>()); // Empty existing
        when(timetableRepository.existsByErpClassAndDayOfWeekAndPeriodNumberAndAcademicYear(any(), any(),
                any(Integer.class), anyString())).thenReturn(false);
        when(timetableRepository.existsByTeacherAndDayOfWeekAndPeriodNumberAndAcademicYear(any(), any(),
                any(Integer.class), anyString())).thenReturn(false);
        when(timetableRepository.existsByRoomAndDayOfWeekAndPeriodNumberAndAcademicYear(any(), any(),
                any(Integer.class), anyString())).thenReturn(false);

        // Execute
        int count = schedulingService.generateSchedule("2024-2025");

        // Verify
        assertTrue(count > 0, "Should generate entries");
        verify(timetableRepository, atLeastOnce()).save(any(Timetable.class));
    }

    @Test
    public void testGenerateSchedule_NoSubjects() {
        // Setup Class with NO subjects
        ErpClass erpClass = new ErpClass();
        erpClass.setClassCode("10B");
        erpClass.setGradeLevel("Grade 10");

        when(classRepository.findByAcademicYear("2024-2025")).thenReturn(Collections.singletonList(erpClass));
        when(subjectRepository.findByGradeLevel("Grade 10")).thenReturn(new ArrayList<>()); // No generic subjects
                                                                                            // either

        // Execute
        int count = schedulingService.generateSchedule("2024-2025");

        // Verify
        assertTrue(count == 0, "Should generate 0 entries");
        verify(timetableRepository, times(0)).save(any(Timetable.class));
    }
}
