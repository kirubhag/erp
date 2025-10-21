package krs.erp.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class EntityTypeTest {

    @Test
    void testAllEntityTypesExist() {
        EntityType[] entityTypes = EntityType.values();
        
        assertTrue(entityTypes.length >= 10, "Should have at least 10 entity types");
        
        // Test that all expected entity types exist
        assertNotNull(EntityType.valueOf("STUDENT"));
        assertNotNull(EntityType.valueOf("PARENT"));
        assertNotNull(EntityType.valueOf("ATTENDANCE"));
        assertNotNull(EntityType.valueOf("HEALTH"));
        assertNotNull(EntityType.valueOf("USER"));
        assertNotNull(EntityType.valueOf("TEACHER"));
        assertNotNull(EntityType.valueOf("COURSE"));
        assertNotNull(EntityType.valueOf("GRADE"));
        assertNotNull(EntityType.valueOf("ASSIGNMENT"));
        assertNotNull(EntityType.valueOf("EXAM"));
    }

    @Test
    void testEntityTypeToString() {
        assertEquals("STUDENT", EntityType.STUDENT.toString());
        assertEquals("PARENT", EntityType.PARENT.toString());
        assertEquals("ATTENDANCE", EntityType.ATTENDANCE.toString());
        assertEquals("HEALTH", EntityType.HEALTH.toString());
    }

    @Test
    void testEntityTypeOrdinal() {
        // Test that ordinals are consistent
        assertEquals(0, EntityType.STUDENT.ordinal());
        assertEquals(1, EntityType.PARENT.ordinal());
        assertEquals(2, EntityType.ATTENDANCE.ordinal());
    }

    @Test
    void testEntityTypeCompareTo() {
        assertTrue(EntityType.STUDENT.compareTo(EntityType.PARENT) < 0);
        assertTrue(EntityType.PARENT.compareTo(EntityType.STUDENT) > 0);
        assertEquals(0, EntityType.STUDENT.compareTo(EntityType.STUDENT));
    }

    @Test
    void testInvalidEntityType() {
        assertThrows(IllegalArgumentException.class, () -> {
            EntityType.valueOf("INVALID_TYPE");
        });
    }

    @Test
    void testEntityTypeEquality() {
        assertEquals(EntityType.STUDENT, EntityType.STUDENT);
        assertNotEquals(EntityType.STUDENT, EntityType.PARENT);
        assertEquals(EntityType.valueOf("STUDENT"), EntityType.STUDENT);
    }

    @Test
    void testEntityTypeInSwitch() {
        // Test that entity types can be used in switch statements
        EntityType type = EntityType.STUDENT;
        String result = switch (type) {
            case STUDENT -> "Student entity";
            case PARENT -> "Parent entity";
            case ATTENDANCE -> "Attendance entity";
            default -> "Other entity";
        };
        
        assertEquals("Student entity", result);
    }

    @Test
    void testEntityTypeEnumSet() {
        // Test that entity types work with EnumSet
        java.util.EnumSet<EntityType> entitySet = java.util.EnumSet.of(
            EntityType.STUDENT, 
            EntityType.PARENT, 
            EntityType.TEACHER
        );
        
        assertTrue(entitySet.contains(EntityType.STUDENT));
        assertTrue(entitySet.contains(EntityType.PARENT));
        assertTrue(entitySet.contains(EntityType.TEACHER));
        assertFalse(entitySet.contains(EntityType.ATTENDANCE));
        assertEquals(3, entitySet.size());
    }

    @Test
    void testEntityTypeEnumMap() {
        // Test that entity types work with EnumMap
        java.util.EnumMap<EntityType, String> entityMap = new java.util.EnumMap<>(EntityType.class);
        entityMap.put(EntityType.STUDENT, "Student Management");
        entityMap.put(EntityType.PARENT, "Parent Portal");
        
        assertEquals("Student Management", entityMap.get(EntityType.STUDENT));
        assertEquals("Parent Portal", entityMap.get(EntityType.PARENT));
        assertNull(entityMap.get(EntityType.ATTENDANCE));
    }

    @Test
    void testEntityTypeStream() {
        // Test that entity types work with streams
        long count = java.util.Arrays.stream(EntityType.values())
            .filter(type -> type.name().contains("E"))
            .count();
        
        assertTrue(count > 0, "Should have entity types containing 'E'");
    }
}