package krs.erp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.Staff;

/**
 * Unit tests for StaffRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StaffRepositoryTest {
    
    @Autowired
    private StaffRepository staffRepository;
    
    private Staff testStaff1;
    private Staff testStaff2;
    private Staff testStaff3;
    
    @BeforeEach
    void setUp() {
        staffRepository.deleteAllInBatch();
        
        // Create test staff 1 - Active Teacher
        testStaff1 = new Staff();
        testStaff1.setStaffId("STF001");
        testStaff1.setFirstName("John");
        testStaff1.setLastName("Smith");
        testStaff1.setEmail("john.smith@test.com");
        testStaff1.setPhone("+11234567890");
        testStaff1.setDateOfBirth(LocalDate.of(1985, 5, 15));
        testStaff1.setGender(Staff.Gender.MALE);
        testStaff1.setHireDate(LocalDate.of(2020, 8, 1));
        testStaff1.setEmploymentStatus(Staff.EmploymentStatus.ACTIVE);
        testStaff1.setStaffType(Staff.StaffType.TEACHER);
        testStaff1.setDepartment("Mathematics");
        testStaff1.setPosition("Senior Teacher");
        testStaff1.setQualification("M.Sc Mathematics");
        testStaff1.setIsActive(1);
        testStaff1 = staffRepository.save(testStaff1);
        
        // Create test staff 2 - Active Administrator
        testStaff2 = new Staff();
        testStaff2.setStaffId("STF002");
        testStaff2.setFirstName("Jane");
        testStaff2.setLastName("Doe");
        testStaff2.setEmail("jane.doe@test.com");
        testStaff2.setPhone("+11234567891");
        testStaff2.setDateOfBirth(LocalDate.of(1990, 3, 20));
        testStaff2.setGender(Staff.Gender.FEMALE);
        testStaff2.setHireDate(LocalDate.of(2019, 1, 15));
        testStaff2.setEmploymentStatus(Staff.EmploymentStatus.ACTIVE);
        testStaff2.setStaffType(Staff.StaffType.ADMINISTRATOR);
        testStaff2.setDepartment("Administration");
        testStaff2.setPosition("HR Manager");
        testStaff2.setQualification("MBA HR");
        testStaff2.setIsActive(1);
        testStaff2 = staffRepository.save(testStaff2);
        
        // Create test staff 3 - Terminated Teacher
        testStaff3 = new Staff();
        testStaff3.setStaffId("STF003");
        testStaff3.setFirstName("Bob");
        testStaff3.setLastName("Johnson");
        testStaff3.setEmail("bob.johnson@test.com");
        testStaff3.setPhone("+11234567892");
        testStaff3.setDateOfBirth(LocalDate.of(1982, 7, 10));
        testStaff3.setGender(Staff.Gender.MALE);
        testStaff3.setHireDate(LocalDate.of(2015, 6, 1));
        testStaff3.setTerminationDate(LocalDate.of(2023, 12, 31));
        testStaff3.setEmploymentStatus(Staff.EmploymentStatus.TERMINATED);
        testStaff3.setStaffType(Staff.StaffType.TEACHER);
        testStaff3.setDepartment("Science");
        testStaff3.setPosition("Physics Teacher");
        testStaff3.setQualification("M.Sc Physics");
        testStaff3.setIsActive(0);
        testStaff3 = staffRepository.save(testStaff3);
    }
    
    @Test
    void testSaveStaff() {
        Staff newStaff = new Staff();
        newStaff.setStaffId("STF004");
        newStaff.setFirstName("Alice");
        newStaff.setLastName("Williams");
        newStaff.setEmail("alice.williams@test.com");
        newStaff.setDateOfBirth(LocalDate.of(1988, 9, 25));
        newStaff.setHireDate(LocalDate.of(2021, 1, 1));
        newStaff.setEmploymentStatus(Staff.EmploymentStatus.ACTIVE);
        newStaff.setStaffType(Staff.StaffType.TEACHER);
        newStaff.setIsActive(1);
        
        Staff saved = staffRepository.save(newStaff);
        
        assertNotNull(saved.getId());
        assertEquals("Alice", saved.getFirstName());
    }
    
    @Test
    void testFindByStaffId() {
        Optional<Staff> found = staffRepository.findByStaffId("STF001");
        
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Smith", found.get().getLastName());
    }
    
    @Test
    void testFindByStaffId_NotFound() {
        Optional<Staff> found = staffRepository.findByStaffId("STF999");
        assertFalse(found.isPresent());
    }
    
    @Test
    void testFindByEmail() {
        Optional<Staff> found = staffRepository.findByEmail("jane.doe@test.com");
        
        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
    }
    
    @Test
    void testFindByEmploymentStatus() {
        List<Staff> activeStaff = staffRepository.findByEmploymentStatus(Staff.EmploymentStatus.ACTIVE);
        
        assertThat(activeStaff).hasSize(2);
        assertThat(activeStaff).extracting(Staff::getEmploymentStatus)
                              .containsOnly(Staff.EmploymentStatus.ACTIVE);
    }
    
    @Test
    void testFindByStaffType() {
        List<Staff> teachers = staffRepository.findByStaffType(Staff.StaffType.TEACHER);
        
        assertThat(teachers).hasSizeGreaterThanOrEqualTo(2);
        assertThat(teachers).extracting(Staff::getStaffType)
                            .contains(Staff.StaffType.TEACHER);
    }
    
    @Test
    void testFindByDepartment() {
        List<Staff> mathStaff = staffRepository.findByDepartment("Mathematics");
        
        assertThat(mathStaff).hasSize(1);
        assertEquals("John", mathStaff.get(0).getFirstName());
    }
    
    @Test
    void testFindActiveStaff() {
        List<Staff> activeStaff = staffRepository.findActiveStaff();
        
        assertThat(activeStaff).hasSize(2);
        assertThat(activeStaff).extracting(Staff::getEmploymentStatus)
                              .containsOnly(Staff.EmploymentStatus.ACTIVE);
    }
    
    @Test
    void testFindByNameContaining() {
        List<Staff> found = staffRepository.findByNameContaining("John");
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(2); // John Smith and Bob Johnson
    }
    
    @Test
    void testFindByHireDateBetween() {
        LocalDate startDate = LocalDate.of(2019, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);
        
        List<Staff> found = staffRepository.findByHireDateBetween(startDate, endDate);
        
        assertThat(found).hasSize(2);
    }
    
    @Test
    void testFindActiveStaffByType() {
        List<Staff> activeTeachers = staffRepository.findActiveStaffByType(Staff.StaffType.TEACHER);
        
        assertThat(activeTeachers).hasSize(1);
        assertEquals("John", activeTeachers.get(0).getFirstName());
    }
    
    @Test
    void testFindActiveStaffByDepartment() {
        List<Staff> mathStaff = staffRepository.findActiveStaffByDepartment("Mathematics");
        
        assertThat(mathStaff).hasSize(1);
        assertEquals("STF001", mathStaff.get(0).getStaffId());
    }
    
    @Test
    void testCountActiveStaff() {
        Long count = staffRepository.countActiveStaff();
        
        assertEquals(2L, count);
    }
    
    @Test
    void testCountActiveStaffByType() {
        Long teacherCount = staffRepository.countActiveStaffByType(Staff.StaffType.TEACHER);
        Long adminCount = staffRepository.countActiveStaffByType(Staff.StaffType.ADMINISTRATOR);
        
        assertEquals(1L, teacherCount);
        assertEquals(1L, adminCount);
    }
    
    @Test
    void testFindAllDepartments() {
        List<String> departments = staffRepository.findAllDepartments();
        
        assertThat(departments).hasSizeGreaterThanOrEqualTo(2);
        assertThat(departments).contains("Mathematics", "Administration");
    }
    
    @Test
    void testExistsByStaffId() {
        assertTrue(staffRepository.existsByStaffId("STF001"));
        assertFalse(staffRepository.existsByStaffId("STF999"));
    }
    
    @Test
    void testExistsByEmail() {
        assertTrue(staffRepository.existsByEmail("john.smith@test.com"));
        assertFalse(staffRepository.existsByEmail("nonexistent@test.com"));
    }
    
    @Test
    void testUpdateStaff() {
        Staff staff = staffRepository.findByStaffId("STF001").orElseThrow();
        staff.setPosition("Head of Department");
        
        Staff updated = staffRepository.save(staff);
        
        assertEquals("Head of Department", updated.getPosition());
    }
    
    @Test
    void testDeleteStaff() {
        Long initialCount = staffRepository.count();
        
        staffRepository.delete(testStaff1);
        
        Long afterDeleteCount = staffRepository.count();
        assertEquals(initialCount - 1, afterDeleteCount);
    }
    
    @Test
    void testFindAll() {
        List<Staff> allStaff = staffRepository.findAll();
        
        assertThat(allStaff).hasSizeGreaterThanOrEqualTo(3);
    }
}
