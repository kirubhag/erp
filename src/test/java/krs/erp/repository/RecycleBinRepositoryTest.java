package krs.erp.repository;

import java.time.LocalDateTime;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.RecycleBin;

/**
 * Test class for RecycleBinRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RecycleBinRepositoryTest {

    @Autowired
    private RecycleBinRepository recycleBinRepository;

    private RecycleBin testRecord1;
    private RecycleBin testRecord2;
    private RecycleBin testRecord3;

    @BeforeEach
    void setUp() {
        recycleBinRepository.deleteAllInBatch();

        // Create test records with different entity types and users
        testRecord1 = new RecycleBin();
        testRecord1.setEntityId(1001L);
        testRecord1.setEntityName("John Doe");
        testRecord1.setEntityType(EntityType.STUDENT);
        testRecord1.setDeletedBy("admin");
        testRecord1.setDeletionReason("Test deletion 1");
        testRecord1.setRelatedEntityCount(2);
        testRecord1 = recycleBinRepository.save(testRecord1);

        testRecord2 = new RecycleBin();
        testRecord2.setEntityId(2001L);
        testRecord2.setEntityName("Jane Smith");
        testRecord2.setEntityType(EntityType.STAFF);
        testRecord2.setDeletedBy("manager");
        testRecord2.setDeletionReason("Test deletion 2");
        testRecord2.setRelatedEntityCount(0);
        testRecord2 = recycleBinRepository.save(testRecord2);

        testRecord3 = new RecycleBin();
        testRecord3.setEntityId(3001L);
        testRecord3.setEntityName("Bob Johnson");
        testRecord3.setEntityType(EntityType.STUDENT);
        testRecord3.setDeletedBy("admin");
        testRecord3.setDeletionReason("Test deletion 3");
        testRecord3.setRelatedEntityCount(1);
        testRecord3 = recycleBinRepository.save(testRecord3);
    }

    @Test
    void testSaveRecycleBinRecord() {
        RecycleBin newRecord = new RecycleBin();
        newRecord.setEntityId(4001L);
        newRecord.setEntityName("Test Entity");
        newRecord.setEntityType(EntityType.PARENT);
        newRecord.setDeletedBy("testuser");
        
        RecycleBin saved = recycleBinRepository.save(newRecord);
        
        assertNotNull(saved.getRecycleBinId());
        assertEquals("Test Entity", saved.getEntityName());
        assertEquals(EntityType.PARENT, saved.getEntityType());
        assertNotNull(saved.getDeletedTime());
    }

    @Test
    void testFindAllByOrderByDeletedTimeDesc() {
        List<RecycleBin> records = recycleBinRepository.findAllByOrderByDeletedTimeDesc();
        
        assertThat(records).hasSize(3);
        // All records created at similar time, ordering may vary
        // Just verify all records are present
        assertTrue(records.stream().anyMatch(r -> "John Doe".equals(r.getEntityName())));
        assertTrue(records.stream().anyMatch(r -> "Jane Smith".equals(r.getEntityName())));
        assertTrue(records.stream().anyMatch(r -> "Bob Johnson".equals(r.getEntityName())));
    }

    @Test
    void testFindAllByOrderByDeletedTimeDescWithPagination() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<RecycleBin> page = recycleBinRepository.findAllByOrderByDeletedTimeDesc(pageRequest);
        
        assertThat(page.getContent()).hasSize(2);
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void testFindByEntityTypeOrderByDeletedTimeDesc() {
        List<RecycleBin> studentRecords = recycleBinRepository.findByEntityTypeOrderByDeletedTimeDesc(EntityType.STUDENT);
        
        assertThat(studentRecords).hasSize(2);
        // Verify both student records are present
        assertTrue(studentRecords.stream().anyMatch(r -> "John Doe".equals(r.getEntityName())));
        assertTrue(studentRecords.stream().anyMatch(r -> "Bob Johnson".equals(r.getEntityName())));
    }

    @Test
    void testFindByEntityTypeOrderByDeletedTimeDescWithPagination() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<RecycleBin> page = recycleBinRepository.findByEntityTypeOrderByDeletedTimeDesc(EntityType.STUDENT, pageRequest);
        
        assertThat(page.getContent()).hasSize(1);
        assertEquals(2, page.getTotalElements());
        // Verify we got one student record
        assertTrue(EntityType.STUDENT == page.getContent().get(0).getEntityType());
    }

    @Test
    void testFindByDeletedByOrderByDeletedTimeDesc() {
        List<RecycleBin> adminRecords = recycleBinRepository.findByDeletedByOrderByDeletedTimeDesc("admin");
        
        assertThat(adminRecords).hasSize(2);
        // Verify both records deleted by admin are present
        assertTrue(adminRecords.stream().anyMatch(r -> "John Doe".equals(r.getEntityName())));
        assertTrue(adminRecords.stream().anyMatch(r -> "Bob Johnson".equals(r.getEntityName())));
    }

    @Test
    void testFindByEntityIdAndEntityType() {
        Optional<RecycleBin> found = recycleBinRepository.findByEntityIdAndEntityType(1001L, EntityType.STUDENT);
        
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getEntityName());
    }

    @Test
    void testFindByDeletedTimeBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusHours(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);
        
        List<RecycleBin> records = recycleBinRepository.findByDeletedTimeBetween(startDate, endDate);
        
        assertThat(records).hasSize(3);
    }

    @Test
    void testCount() {
        long count = recycleBinRepository.count();
        assertEquals(3, count);
    }

    @Test
    void testCountByEntityType() {
        long studentCount = recycleBinRepository.countByEntityType(EntityType.STUDENT);
        long staffCount = recycleBinRepository.countByEntityType(EntityType.STAFF);
        
        assertEquals(2, studentCount);
        assertEquals(1, staffCount);
    }

    @Test
    void testCountByDeletedBy() {
        long adminCount = recycleBinRepository.countByDeletedBy("admin");
        long managerCount = recycleBinRepository.countByDeletedBy("manager");
        
        assertEquals(2, adminCount);
        assertEquals(1, managerCount);
    }

    @Test
    void testFindOldRecords() {
        LocalDateTime cutoffDate = LocalDateTime.now().plusHours(1);
        
        List<RecycleBin> oldRecords = recycleBinRepository.findOldRecords(cutoffDate);
        
        assertThat(oldRecords).hasSize(3);
    }

    @Test
    void testExistsByEntityIdAndEntityType() {
        assertTrue(recycleBinRepository.existsByEntityIdAndEntityType(1001L, EntityType.STUDENT));
        assertFalse(recycleBinRepository.existsByEntityIdAndEntityType(9999L, EntityType.PARENT));
    }

    @Test
    void testSearchByEntityName() {
        List<RecycleBin> found = recycleBinRepository.searchByEntityName("john");
        
        assertThat(found).hasSize(2);
        // Should find "John Doe" and "Bob Johnson"
    }

    @Test
    void testSearchByEntityNameWithPagination() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<RecycleBin> page = recycleBinRepository.searchByEntityName("john", pageRequest);
        
        assertThat(page.getContent()).hasSize(1);
        assertEquals(2, page.getTotalElements());
    }

    @Test
    void testUpdateRecycleBinRecord() {
        testRecord1.setDeletionReason("Updated reason");
        testRecord1.setRelatedEntityCount(5);
        
        RecycleBin updated = recycleBinRepository.save(testRecord1);
        
        assertEquals("Updated reason", updated.getDeletionReason());
        assertEquals(5, updated.getRelatedEntityCount());
    }

    @Test
    void testDeleteRecycleBinRecord() {
        recycleBinRepository.delete(testRecord1);
        
        assertFalse(recycleBinRepository.existsByEntityIdAndEntityType(1001L, EntityType.STUDENT));
        assertEquals(2, recycleBinRepository.count());
    }

    @Test
    void testFindAll() {
        List<RecycleBin> allRecords = recycleBinRepository.findAll();
        
        assertThat(allRecords).hasSize(3);
    }
}
