package krs.erp.model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for BaseEntity audit functionality
 */
class BaseEntityTest {

    private TestEntity testEntity;

    // Create a test entity class that extends BaseEntity for testing
    static class TestEntity extends BaseEntity {
        private String name;

        public TestEntity() {
            super();
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @BeforeEach
    void setUp() {
        testEntity = new TestEntity();
    }

    @Test
    void testBaseEntityCreation() {
        assertNotNull(testEntity);
        assertEquals(1, testEntity.getIsActive()); // Default should be 1 (active)
    }

    @Test
    void testAuditFields() {
        // Test created_by field
        testEntity.setCreatedBy("admin");
        assertEquals("admin", testEntity.getCreatedBy());

        // Test modified_by field
        testEntity.setModifiedBy("user1");
        assertEquals("user1", testEntity.getModifiedBy());

        // Test owner_id field
        testEntity.setOwnerId(123L);
        assertEquals(123L, testEntity.getOwnerId());

        // Test is_active field
        testEntity.setIsActive(0);
        assertEquals(0, testEntity.getIsActive());
    }

    @Test
    void testTimestampFields() {
        LocalDateTime now = LocalDateTime.now();
        
        // Test created_time
        testEntity.setCreatedTime(now);
        assertEquals(now, testEntity.getCreatedTime());

        // Test modified_time
        LocalDateTime modifiedTime = now.plusMinutes(5);
        testEntity.setModifiedTime(modifiedTime);
        assertEquals(modifiedTime, testEntity.getModifiedTime());
    }

    @Test
    void testDefaultValues() {
        TestEntity entity = new TestEntity();
        
        // Test default is_active value
        assertEquals(1, entity.getIsActive());
        
        // Test that timestamps can be null initially
        assertNull(entity.getCreatedTime());
        assertNull(entity.getModifiedTime());
    }

    @Test
    void testIdField() {
        assertNull(testEntity.getId()); // Should be null for new entity
        
        testEntity.setId(42L);
        assertEquals(42L, testEntity.getId());
    }



    @Test
    void testAuditTrail() {
        // Simulate entity creation audit trail
        testEntity.setCreatedBy("system");
        testEntity.setCreatedTime(LocalDateTime.now());
        testEntity.setModifiedBy("system");
        testEntity.setModifiedTime(LocalDateTime.now());
        testEntity.setOwnerId(1L);
        testEntity.setIsActive(1);

        // Verify all audit fields are set
        assertNotNull(testEntity.getCreatedBy());
        assertNotNull(testEntity.getCreatedTime());
        assertNotNull(testEntity.getModifiedBy());
        assertNotNull(testEntity.getModifiedTime());
        assertNotNull(testEntity.getOwnerId());
        assertEquals(1, testEntity.getIsActive());
    }

    @Test
    void testEntityUpdate() {
        // Set initial values
        testEntity.setCreatedBy("admin");
        testEntity.setCreatedTime(LocalDateTime.now().minusHours(1));
        
        // Update values
        testEntity.setModifiedBy("user2");
        testEntity.setModifiedTime(LocalDateTime.now());
        
        assertEquals("admin", testEntity.getCreatedBy());
        assertEquals("user2", testEntity.getModifiedBy());
        assertTrue(testEntity.getModifiedTime().isAfter(testEntity.getCreatedTime()));
    }

    @Test
    void testSoftDelete() {
        // Entity should be active by default
        assertEquals(1, testEntity.getIsActive());
        
        // Simulate soft delete
        testEntity.setIsActive(0);
        testEntity.setModifiedBy("admin");
        testEntity.setModifiedTime(LocalDateTime.now());
        
        assertEquals(0, testEntity.getIsActive());
        assertEquals("admin", testEntity.getModifiedBy());
        assertNotNull(testEntity.getModifiedTime());
    }
}