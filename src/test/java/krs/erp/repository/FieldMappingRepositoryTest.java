package krs.erp.repository;

import java.util.List;

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

import krs.erp.entity.DuplicateAction;
import krs.erp.entity.FieldMapping;
import krs.erp.entity.ImportSession;
import krs.erp.entity.ImportStatus;
import krs.erp.entity.ImportType;

/**
 * Test class for FieldMappingRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FieldMappingRepositoryTest {

    @Autowired
    private FieldMappingRepository fieldMappingRepository;

    @Autowired
    private ImportSessionRepository importSessionRepository;

    private ImportSession testSession;
    private FieldMapping mapping1;
    private FieldMapping mapping2;
    private FieldMapping mapping3;

    @BeforeEach
    void setUp() {
        fieldMappingRepository.deleteAllInBatch();
        importSessionRepository.deleteAllInBatch();

        // Create test import session
        testSession = new ImportSession();
        testSession.setId("TEST-MAPPING-SESSION-001");
        testSession.setUserId(1L);
        testSession.setEntityType("students");
        testSession.setFileName("test_import.csv");
        testSession.setFileFormat("csv");
        testSession.setTotalRecords(100);
        testSession.setFileSize(10000L);
        testSession.setImportType(ImportType.ORGANIZATION);
        testSession.setDuplicateAction(DuplicateAction.SKIP);
        testSession.setStatus(ImportStatus.PENDING);
        testSession = importSessionRepository.save(testSession);

        // Create test field mappings
        mapping1 = new FieldMapping();
        mapping1.setImportSession(testSession);
        mapping1.setSourceColumn("First Name");
        mapping1.setSourceIndex(0);
        mapping1.setTargetField("firstName");
        mapping1.setTargetFieldLabel("First Name");
        mapping1.setIsRequired(true);
        mapping1.setDataType("string");
        mapping1 = fieldMappingRepository.save(mapping1);

        mapping2 = new FieldMapping();
        mapping2.setImportSession(testSession);
        mapping2.setSourceColumn("Email Address");
        mapping2.setSourceIndex(1);
        mapping2.setTargetField("email");
        mapping2.setTargetFieldLabel("Email");
        mapping2.setIsRequired(true);
        mapping2.setDataType("email");
        mapping2 = fieldMappingRepository.save(mapping2);

        mapping3 = new FieldMapping();
        mapping3.setImportSession(testSession);
        mapping3.setSourceColumn("Phone");
        mapping3.setSourceIndex(2);
        mapping3.setTargetField("phone");
        mapping3.setTargetFieldLabel("Phone Number");
        mapping3.setIsRequired(false);
        mapping3.setDataType("phone");
        mapping3 = fieldMappingRepository.save(mapping3);
    }

    @Test
    void testSaveFieldMapping() {
        FieldMapping newMapping = new FieldMapping();
        newMapping.setImportSession(testSession);
        newMapping.setSourceColumn("Date of Birth");
        newMapping.setSourceIndex(3);
        newMapping.setTargetField("dateOfBirth");
        newMapping.setTargetFieldLabel("Date of Birth");
        newMapping.setIsRequired(false);
        newMapping.setDataType("date");
        
        FieldMapping saved = fieldMappingRepository.save(newMapping);
        
        assertNotNull(saved.getId());
        assertEquals("Date of Birth", saved.getSourceColumn());
        assertEquals("dateOfBirth", saved.getTargetField());
    }

    @Test
    void testFindByImportSessionIdOrderBySourceIndex() {
        List<FieldMapping> mappings = fieldMappingRepository.findByImportSessionIdOrderBySourceIndex("TEST-MAPPING-SESSION-001");
        
        assertThat(mappings).hasSize(3);
        assertEquals(0, mappings.get(0).getSourceIndex());
        assertEquals(1, mappings.get(1).getSourceIndex());
        assertEquals(2, mappings.get(2).getSourceIndex());
    }

    @Test
    void testFindByImportSessionIdAndTargetField() {
        FieldMapping found = fieldMappingRepository.findByImportSessionIdAndTargetField("TEST-MAPPING-SESSION-001", "email");
        
        assertNotNull(found);
        assertEquals("Email Address", found.getSourceColumn());
        assertEquals("email", found.getDataType());
    }

    @Test
    void testUpdateFieldMapping() {
        mapping1.setTargetFieldLabel("Student First Name");
        mapping1.setDataType("text");
        
        FieldMapping updated = fieldMappingRepository.save(mapping1);
        
        assertEquals("Student First Name", updated.getTargetFieldLabel());
        assertEquals("text", updated.getDataType());
    }

    @Test
    void testDeleteFieldMapping() {
        fieldMappingRepository.delete(mapping1);
        
        List<FieldMapping> mappings = fieldMappingRepository.findByImportSessionIdOrderBySourceIndex("TEST-MAPPING-SESSION-001");
        assertThat(mappings).hasSize(2);
    }

    @Test
    void testFindAll() {
        List<FieldMapping> allMappings = fieldMappingRepository.findAll();
        
        assertThat(allMappings).hasSize(3);
    }

    @Test
    void testMappingAttributes() {
        assertTrue(mapping1.getIsRequired());
        assertFalse(mapping3.getIsRequired());
        assertEquals("string", mapping1.getDataType());
        assertEquals("phone", mapping3.getDataType());
    }
}
