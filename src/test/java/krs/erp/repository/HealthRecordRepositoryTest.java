package krs.erp.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.model.HealthRecord;
import krs.erp.model.Student;

/**
 * Unit tests for HealthRecordRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HealthRecordRepositoryTest {
    
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    private Student testStudent;
    private HealthRecord healthRecord1;
    private HealthRecord healthRecord2;
    private HealthRecord healthRecord3;
    
    @BeforeEach
    void setUp() {
        healthRecordRepository.deleteAllInBatch();
        studentRepository.deleteAllInBatch();
        
        // Create test student
        testStudent = new Student();
        testStudent.setStudentId("HLT-STU001");
        testStudent.setFirstName("Alice");
        testStudent.setLastName("Health");
        testStudent.setEmail("alice.health@test.com");
        testStudent.setDateOfBirth(LocalDate.of(2010, 3, 15));
        testStudent.setGradeLevel(Student.GradeLevel.GRADE_7);
        testStudent.setEnrollmentStatus(Student.EnrollmentStatus.ACTIVE);
        testStudent.setEnrollmentDate(LocalDate.now().minusYears(2));
        testStudent.setIsActive(1);
        testStudent.setCreatedTime(LocalDateTime.now());
        testStudent = studentRepository.save(testStudent);
        
        // Create health record 1 - Allergy (Active, High Severity)
        healthRecord1 = new HealthRecord();
        healthRecord1.setStudent(testStudent);
        healthRecord1.setRecordType(HealthRecord.RecordType.ALLERGY);
        healthRecord1.setTitle("Peanut Allergy");
        healthRecord1.setDescription("Severe peanut allergy");
        healthRecord1.setRecordDate(LocalDate.now().minusMonths(6));
        healthRecord1.setSeverity(HealthRecord.Severity.HIGH);
        healthRecord1.setActive(true);
        healthRecord1.setRequiresAttention(true);
        healthRecord1.setIsActive(1);
        healthRecord1.setCreatedTime(LocalDateTime.now());
        healthRecord1 = healthRecordRepository.save(healthRecord1);
        
        // Create health record 2 - Medication (Active, Moderate Severity, Expiring Soon)
        healthRecord2 = new HealthRecord();
        healthRecord2.setStudent(testStudent);
        healthRecord2.setRecordType(HealthRecord.RecordType.MEDICATION);
        healthRecord2.setTitle("Asthma Inhaler");
        healthRecord2.setDescription("Daily asthma medication");
        healthRecord2.setRecordDate(LocalDate.now().minusMonths(3));
        healthRecord2.setExpiryDate(LocalDate.now().plusDays(15));
        healthRecord2.setMedication("Albuterol");
        healthRecord2.setDosage("2 puffs");
        healthRecord2.setFrequency("As needed");
        healthRecord2.setSeverity(HealthRecord.Severity.MODERATE);
        healthRecord2.setActive(true);
        healthRecord2.setRequiresAttention(false);
        healthRecord2.setIsActive(1);
        healthRecord2.setCreatedTime(LocalDateTime.now());
        healthRecord2 = healthRecordRepository.save(healthRecord2);
        
        // Create health record 3 - Immunization (Inactive, Expired)
        healthRecord3 = new HealthRecord();
        healthRecord3.setStudent(testStudent);
        healthRecord3.setRecordType(HealthRecord.RecordType.IMMUNIZATION);
        healthRecord3.setTitle("Flu Shot 2023");
        healthRecord3.setDescription("Annual flu immunization");
        healthRecord3.setRecordDate(LocalDate.now().minusYears(1));
        healthRecord3.setExpiryDate(LocalDate.now().minusMonths(2));
        healthRecord3.setProvider("Dr. Smith");
        healthRecord3.setProviderContact("555-1234");
        healthRecord3.setSeverity(HealthRecord.Severity.LOW);
        healthRecord3.setActive(true);
        healthRecord3.setRequiresAttention(false);
        healthRecord3.setIsActive(1);
        healthRecord3.setCreatedTime(LocalDateTime.now());
        healthRecord3 = healthRecordRepository.save(healthRecord3);
    }
    
    @Test
    void testFindByStudentId() {
        List<HealthRecord> records = healthRecordRepository.findByStudentId(testStudent.getId());
        assertThat(records).hasSize(3);
    }
    
    @Test
    void testFindByStudentIdAndRecordType() {
        List<HealthRecord> allergies = healthRecordRepository.findByStudentIdAndRecordType(
            testStudent.getId(), HealthRecord.RecordType.ALLERGY);
        assertThat(allergies).hasSize(1);
        assertEquals("Peanut Allergy", allergies.get(0).getTitle());
        
        List<HealthRecord> medications = healthRecordRepository.findByStudentIdAndRecordType(
            testStudent.getId(), HealthRecord.RecordType.MEDICATION);
        assertThat(medications).hasSize(1);
        assertEquals("Asthma Inhaler", medications.get(0).getTitle());
    }
    
    @Test
    void testFindByStudentIdAndActiveTrue() {
        List<HealthRecord> activeRecords = healthRecordRepository.findByStudentIdAndActiveTrue(testStudent.getId());
        assertThat(activeRecords).hasSize(3);
        assertThat(activeRecords).allMatch(HealthRecord::getActive);
    }
    
    @Test
    void testFindByRecordType() {
        List<HealthRecord> immunizations = healthRecordRepository.findByRecordType(HealthRecord.RecordType.IMMUNIZATION);
        assertThat(immunizations).hasSize(1);
        assertEquals("Flu Shot 2023", immunizations.get(0).getTitle());
    }
    
    @Test
    void testFindActiveHealthRecordsByStudentAndType() {
        List<HealthRecord> activeAllergies = healthRecordRepository.findActiveHealthRecordsByStudentAndType(
            testStudent.getId(), HealthRecord.RecordType.ALLERGY);
        assertThat(activeAllergies).hasSize(1);
        assertEquals("Peanut Allergy", activeAllergies.get(0).getTitle());
    }
    
    @Test
    void testFindRecordsExpiringBetween() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        
        List<HealthRecord> expiringRecords = healthRecordRepository.findRecordsExpiringBetween(startDate, endDate);
        assertThat(expiringRecords).hasSize(1);
        assertEquals("Asthma Inhaler", expiringRecords.get(0).getTitle());
    }
    
    @Test
    void testFindExpiredRecords() {
        List<HealthRecord> expiredRecords = healthRecordRepository.findExpiredRecords();
        assertThat(expiredRecords).hasSize(1);
        assertEquals("Flu Shot 2023", expiredRecords.get(0).getTitle());
    }
    
    @Test
    void testFindRecordsRequiringAttention() {
        List<HealthRecord> attentionRecords = healthRecordRepository.findRecordsRequiringAttention();
        assertThat(attentionRecords).hasSize(1);
        assertEquals("Peanut Allergy", attentionRecords.get(0).getTitle());
    }
    
    @Test
    void testFindCriticalHealthRecords() {
        List<HealthRecord> criticalRecords = healthRecordRepository.findCriticalHealthRecords();
        assertThat(criticalRecords).hasSize(1);
        assertEquals(HealthRecord.Severity.HIGH, criticalRecords.get(0).getSeverity());
    }
    
    @Test
    void testFindActiveAllergiesByStudent() {
        List<HealthRecord> allergies = healthRecordRepository.findActiveAllergiesByStudent(testStudent.getId());
        assertThat(allergies).hasSize(1);
        assertEquals(HealthRecord.RecordType.ALLERGY, allergies.get(0).getRecordType());
    }
    
    @Test
    void testFindActiveMedicationsByStudent() {
        List<HealthRecord> medications = healthRecordRepository.findActiveMedicationsByStudent(testStudent.getId());
        assertThat(medications).hasSize(1);
        assertEquals("Albuterol", medications.get(0).getMedication());
    }
    
    @Test
    void testFindImmunizationsByStudent() {
        List<HealthRecord> immunizations = healthRecordRepository.findImmunizationsByStudent(testStudent.getId());
        assertThat(immunizations).hasSize(1);
        assertEquals(HealthRecord.RecordType.IMMUNIZATION, immunizations.get(0).getRecordType());
    }
    
    @Test
    void testCountActiveHealthRecordsByStudent() {
        Long count = healthRecordRepository.countActiveHealthRecordsByStudent(testStudent.getId());
        assertEquals(3L, count);
    }
    
    @Test
    void testFindByRecordDateBetween() {
        LocalDate startDate = LocalDate.now().minusYears(2);
        LocalDate endDate = LocalDate.now();
        
        List<HealthRecord> records = healthRecordRepository.findByRecordDateBetween(startDate, endDate);
        assertThat(records).hasSize(3);
    }
    
    @Test
    void testFindByRequiresAttentionTrueAndActiveTrue() {
        List<HealthRecord> attentionRecords = healthRecordRepository.findByRequiresAttentionTrueAndActiveTrue();
        assertThat(attentionRecords).hasSize(1);
        assertTrue(attentionRecords.get(0).getRequiresAttention());
    }
    
    @Test
    void testFindByExpiryDateBeforeAndActiveTrue() {
        List<HealthRecord> expiredRecords = healthRecordRepository.findByExpiryDateBeforeAndActiveTrue(LocalDate.now());
        assertThat(expiredRecords).hasSize(1);
        assertEquals("Flu Shot 2023", expiredRecords.get(0).getTitle());
    }
    
    @Test
    void testFindByStudentIdAndIsActive() {
        List<HealthRecord> activeRecords = healthRecordRepository.findByStudentIdAndIsActive(testStudent.getId(), 1);
        assertThat(activeRecords).hasSize(3);
    }
    
    @Test
    void testFindAllActive() {
        List<HealthRecord> allActive = healthRecordRepository.findAllActive();
        assertThat(allActive).hasSize(3);
    }
    
    @Test
    void testSaveHealthRecord() {
        HealthRecord newRecord = new HealthRecord();
        newRecord.setStudent(testStudent);
        newRecord.setRecordType(HealthRecord.RecordType.PHYSICAL_EXAM);
        newRecord.setTitle("Annual Physical");
        newRecord.setRecordDate(LocalDate.now());
        newRecord.setActive(true);
        newRecord.setIsActive(1);
        newRecord.setCreatedTime(LocalDateTime.now());
        
        HealthRecord saved = healthRecordRepository.save(newRecord);
        assertNotNull(saved.getId());
        assertEquals("Annual Physical", saved.getTitle());
    }
    
    @Test
    void testUpdateHealthRecord() {
        healthRecord1.setDescription("Updated: Severe peanut and tree nut allergy");
        healthRecord1.setModifiedTime(LocalDateTime.now());
        
        HealthRecord updated = healthRecordRepository.save(healthRecord1);
        assertTrue(updated.getDescription().contains("tree nut"));
    }
    
    @Test
    void testDeleteHealthRecord() {
        Long recordId = healthRecord1.getId();
        healthRecordRepository.delete(healthRecord1);
        
        assertThat(healthRecordRepository.findById(recordId)).isEmpty();
    }
}
