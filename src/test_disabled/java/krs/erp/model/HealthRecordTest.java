package krs.erp.model;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for HealthRecord model
 */
class HealthRecordTest {

    @Test
    void testHealthRecordCreation() {
        // Given & When
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setRecordDate(LocalDate.now());
        healthRecord.setRecordType(HealthRecord.RecordType.PHYSICAL_EXAM);
        healthRecord.setTitle("Annual Physical Exam");
        healthRecord.setDescription("Regular checkup - healthy");

        // Then
        assertNotNull(healthRecord);
        assertEquals(LocalDate.now(), healthRecord.getRecordDate());
        assertEquals(HealthRecord.RecordType.PHYSICAL_EXAM, healthRecord.getRecordType());
        assertEquals("Annual Physical Exam", healthRecord.getTitle());
        assertEquals("Regular checkup - healthy", healthRecord.getDescription());
    }

    @Test
    void testHealthRecordWithConstructor() {
        // Given
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");

        // When
        HealthRecord healthRecord = new HealthRecord(
            student,
            HealthRecord.RecordType.IMMUNIZATION,
            "COVID-19 vaccination"
        );

        // Then
        assertEquals(student, healthRecord.getStudent());
        assertEquals(HealthRecord.RecordType.IMMUNIZATION, healthRecord.getRecordType());
        assertEquals("COVID-19 vaccination", healthRecord.getTitle());
    }

    @Test
    void testRecordTypeEnum() {
        // Given & When & Then
        assertEquals("IMMUNIZATION", HealthRecord.RecordType.IMMUNIZATION.name());
        assertEquals("ALLERGY", HealthRecord.RecordType.ALLERGY.name());
        assertEquals("MEDICAL_CONDITION", HealthRecord.RecordType.MEDICAL_CONDITION.name());
        assertEquals("MEDICATION", HealthRecord.RecordType.MEDICATION.name());
        assertEquals("PHYSICAL_EXAM", HealthRecord.RecordType.PHYSICAL_EXAM.name());
        assertEquals("VISION_SCREENING", HealthRecord.RecordType.VISION_SCREENING.name());
    }

    @Test
    void testSeverityEnum() {
        // Given & When & Then
        assertEquals("LOW", HealthRecord.Severity.LOW.name());
        assertEquals("MODERATE", HealthRecord.Severity.MODERATE.name());
        assertEquals("HIGH", HealthRecord.Severity.HIGH.name());
        assertEquals("CRITICAL", HealthRecord.Severity.CRITICAL.name());
    }

    @Test
    void testMedicationFields() {
        // Given
        HealthRecord healthRecord = new HealthRecord();

        // When
        healthRecord.setMedication("Paracetamol");
        healthRecord.setDosage("500mg");
        healthRecord.setFrequency("Twice daily");
        healthRecord.setSpecialInstructions("Take with food");

        // Then
        assertEquals("Paracetamol", healthRecord.getMedication());
        assertEquals("500mg", healthRecord.getDosage());
        assertEquals("Twice daily", healthRecord.getFrequency());
        assertEquals("Take with food", healthRecord.getSpecialInstructions());
    }

    @Test
    void testActiveAndAttentionFlags() {
        // Given
        HealthRecord healthRecord = new HealthRecord();

        // When
        healthRecord.setActive(true);
        healthRecord.setRequiresAttention(false);

        // Then
        assertTrue(healthRecord.getActive());
        assertFalse(healthRecord.getRequiresAttention());
    }

    @Test
    void testExpiryMethods() {
        // Given
        HealthRecord healthRecord = new HealthRecord();
        LocalDate futureDate = LocalDate.now().plusDays(30);
        LocalDate pastDate = LocalDate.now().minusDays(30);

        // When & Then - Not expired
        healthRecord.setExpiryDate(futureDate);
        assertFalse(healthRecord.isExpired());
        assertTrue(healthRecord.isExpiringWithin(60));
        
        // When & Then - Expired
        healthRecord.setExpiryDate(pastDate);
        assertTrue(healthRecord.isExpired());
        assertFalse(healthRecord.isExpiringWithin(60));
    }

    @Test
    void testIsCritical() {
        // Given
        HealthRecord criticalRecord = new HealthRecord();
        HealthRecord highRecord = new HealthRecord();
        HealthRecord lowRecord = new HealthRecord();

        // When
        criticalRecord.setSeverity(HealthRecord.Severity.CRITICAL);
        highRecord.setSeverity(HealthRecord.Severity.HIGH);
        lowRecord.setSeverity(HealthRecord.Severity.LOW);

        // Then
        assertTrue(criticalRecord.isCritical());
        assertTrue(highRecord.isCritical());
        assertFalse(lowRecord.isCritical());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        HealthRecord healthRecord = new HealthRecord();
        LocalDate testDate = LocalDate.of(2025, 5, 15);

        // When
        healthRecord.setId(1L);
        healthRecord.setRecordDate(testDate);
        healthRecord.setRecordType(HealthRecord.RecordType.MEDICAL_CONDITION);
        healthRecord.setTitle("Common Cold");
        healthRecord.setDescription("Patient has symptoms of common cold");
        healthRecord.setProvider("Dr. Smith");
        healthRecord.setProviderContact("555-1234");
        healthRecord.setSeverity(HealthRecord.Severity.LOW);
        healthRecord.setExpiryDate(testDate.plusWeeks(2));
        healthRecord.setDocumentPath("/documents/health-record-1.pdf");

        // Then
        assertEquals(1L, healthRecord.getId());
        assertEquals(testDate, healthRecord.getRecordDate());
        assertEquals(HealthRecord.RecordType.MEDICAL_CONDITION, healthRecord.getRecordType());
        assertEquals("Common Cold", healthRecord.getTitle());
        assertEquals("Patient has symptoms of common cold", healthRecord.getDescription());
        assertEquals("Dr. Smith", healthRecord.getProvider());
        assertEquals("555-1234", healthRecord.getProviderContact());
        assertEquals(HealthRecord.Severity.LOW, healthRecord.getSeverity());
        assertEquals(testDate.plusWeeks(2), healthRecord.getExpiryDate());
        assertEquals("/documents/health-record-1.pdf", healthRecord.getDocumentPath());
    }

    @Test
    void testRecordTypeDescription() {
        // Given
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setRecordType(HealthRecord.RecordType.MEDICAL_CONDITION);

        // When
        String description = healthRecord.getRecordTypeDescription();

        // Then
        assertEquals("medical condition", description);
    }

    @Test
    void testToString() {
        // Given
        Student student = new Student();
        student.setFirstName("Jane");
        student.setLastName("Doe");
        
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setId(1L);
        healthRecord.setStudent(student);
        healthRecord.setRecordType(HealthRecord.RecordType.ALLERGY);
        healthRecord.setTitle("Peanut Allergy");
        healthRecord.setRecordDate(LocalDate.now());
        healthRecord.setActive(true);

        // When
        String result = healthRecord.toString();

        // Then
        assertTrue(result.contains("HealthRecord{"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("recordType=ALLERGY"));
        assertTrue(result.contains("title='Peanut Allergy'"));
    }
}