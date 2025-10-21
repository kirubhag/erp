package krs.erp.model;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import krs.erp.enums.EntityType;

class CustomViewTest {

    private CustomView customView;

    @BeforeEach
    void setUp() {
        customView = new CustomView();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(customView);
        assertNull(customView.getId());
        assertNull(customView.getViewName());
        assertNull(customView.getDescription());
        assertNull(customView.getEntityType());
        assertNotNull(customView.getSelectedFields());
        assertTrue(customView.getSelectedFields().isEmpty());
        assertEquals(false, customView.getIsDefault()); // Default value is false
        assertEquals(false, customView.getIsPublic());  // Default value is false
    }

    @Test
    void testParameterizedConstructor() {
        List<String> fields = Arrays.asList("name", "email", "phone");
        CustomView view = new CustomView("Test View", "Test Description", EntityType.STUDENT, fields);
        
        assertEquals("Test View", view.getViewName());
        assertEquals("Test Description", view.getDescription());
        assertEquals(EntityType.STUDENT, view.getEntityType());
        assertEquals(fields, view.getSelectedFields());
    }

    @Test
    void testParameterizedConstructorWithNullFields() {
        CustomView view = new CustomView("Test View", "Test Description", EntityType.STUDENT, null);
        
        assertEquals("Test View", view.getViewName());
        assertEquals("Test Description", view.getDescription());
        assertEquals(EntityType.STUDENT, view.getEntityType());
        assertNotNull(view.getSelectedFields());
        assertTrue(view.getSelectedFields().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        customView.setViewName("Student Basic Info");
        customView.setDescription("Basic student information view");
        customView.setEntityType(EntityType.STUDENT);
        customView.setIsDefault(true);
        customView.setIsPublic(false);

        assertEquals("Student Basic Info", customView.getViewName());
        assertEquals("Basic student information view", customView.getDescription());
        assertEquals(EntityType.STUDENT, customView.getEntityType());
        assertTrue(customView.getIsDefault());
        assertFalse(customView.getIsPublic());
    }

    @Test
    void testSelectedFieldsManagement() {
        List<String> fields = Arrays.asList("firstName", "lastName", "email");
        customView.setSelectedFields(fields);
        
        assertEquals(fields, customView.getSelectedFields());
        assertEquals(3, customView.getSelectedFields().size());
        assertTrue(customView.getSelectedFields().contains("firstName"));
        assertTrue(customView.getSelectedFields().contains("lastName"));
        assertTrue(customView.getSelectedFields().contains("email"));
    }

    @Test
    void testAddSelectedField() {
        customView.addField("newField");
        
        assertEquals(1, customView.getSelectedFields().size());
        assertTrue(customView.getSelectedFields().contains("newField"));
    }

    @Test
    void testAddSelectedFieldToExistingList() {
        customView.addField("field1");
        customView.addField("field2");
        
        assertEquals(2, customView.getSelectedFields().size());
        assertTrue(customView.getSelectedFields().contains("field1"));
        assertTrue(customView.getSelectedFields().contains("field2"));
    }

    @Test
    void testRemoveSelectedField() {
        customView.addField("field1");
        customView.addField("field2");
        customView.removeField("field1");
        
        assertEquals(1, customView.getSelectedFields().size());
        assertFalse(customView.getSelectedFields().contains("field1"));
        assertTrue(customView.getSelectedFields().contains("field2"));
    }

    @Test
    void testRemoveNonExistentField() {
        customView.addField("field1");
        customView.removeField("field2");
        
        assertEquals(1, customView.getSelectedFields().size());
        assertTrue(customView.getSelectedFields().contains("field1"));
    }

    @Test
    void testHasSelectedField() {
        customView.addField("testField");
        
        assertTrue(customView.hasField("testField"));
        assertFalse(customView.hasField("nonExistentField"));
    }

    @Test
    void testClearSelectedFields() {
        customView.addField("field1");
        customView.addField("field2");
        customView.getSelectedFields().clear();
        
        assertTrue(customView.getSelectedFields().isEmpty());
    }

    @Test
    void testGetSelectedFieldsCount() {
        assertEquals(0, customView.getFieldCount());
        
        customView.addField("field1");
        assertEquals(1, customView.getFieldCount());
        
        customView.addField("field2");
        assertEquals(2, customView.getFieldCount());
    }

    @Test
    void testEntityTypeValidation() {
        // Test all valid entity types
        for (EntityType entityType : EntityType.values()) {
            customView.setEntityType(entityType);
            assertEquals(entityType, customView.getEntityType());
        }
    }

    @Test
    void testBooleanFieldDefaults() {
        // Test that boolean fields can handle null values
        customView.setIsDefault(null);
        customView.setIsPublic(null);
        
        assertNull(customView.getIsDefault());
        assertNull(customView.getIsPublic());
    }

    @Test
    void testInheritedBaseEntityFields() {
        // Test that CustomView inherits from BaseEntity
        // createdAt and updatedAt are null for new entities (set by @CreationTimestamp/@UpdateTimestamp)
        assertNull(customView.getCreatedAt());
        assertNull(customView.getUpdatedAt());
        // ID should be null for new entities
        assertNull(customView.getId());
        // isActive should default to true from BaseEntity
        assertTrue(customView.getIsActive());
    }

    @Test
    void testToStringMethod() {
        customView.setViewName("Test View");
        customView.setEntityType(EntityType.STUDENT);
        
        String toString = customView.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test View"));
        assertTrue(toString.contains("STUDENT"));
    }

    @Test
    void testEqualsAndHashCode() {
        CustomView view1 = new CustomView();
        view1.setId(1L);
        view1.setViewName("Test View");
        view1.setEntityType(EntityType.STUDENT);

        CustomView view2 = new CustomView();
        view2.setId(1L);
        view2.setViewName("Test View");
        view2.setEntityType(EntityType.STUDENT);

        CustomView view3 = new CustomView();
        view3.setId(2L);
        view3.setViewName("Different View");
        view3.setEntityType(EntityType.PARENT);

        // Since BaseEntity doesn't override equals/hashCode, we test object identity
        assertEquals(view1, view1); // Same object
        assertNotEquals(view1, view2); // Different objects
        assertNotEquals(view1, view3);
        assertNotEquals(view1.hashCode(), view2.hashCode()); // Different objects have different hash codes
    }

    @Test
    void testFieldValidation() {
        // Test that required fields trigger validation
        customView.setViewName(""); // Should fail @NotBlank
        customView.setEntityType(null); // Should fail @NotNull
        
        // Note: Actual validation would be triggered by the validation framework
        // These tests just ensure the fields can be set to invalid values
        assertEquals("", customView.getViewName());
        assertNull(customView.getEntityType());
    }

    @Test
    void testSelectedFieldsListModification() {
        List<String> originalFields = Arrays.asList("field1", "field2");
        customView.setSelectedFields(originalFields);
        
        // Test that the list is copied, not referenced
        List<String> retrievedFields = customView.getSelectedFields();
        retrievedFields.add("field3");
        
        // The original list should not be modified
        assertEquals(2, originalFields.size());
        assertFalse(originalFields.contains("field3"));
    }
}