package krs.erp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Parent model
 */
class ParentTest {

    @Test
    void testParentCreation() {
        // Given & When
        Parent parent = new Parent();
        parent.setFirstName("John");
        parent.setLastName("Smith");
        parent.setEmail("john.smith@example.com");
        parent.setPhone("555-1234");

        // Then
        assertNotNull(parent);
        assertEquals("John", parent.getFirstName());
        assertEquals("Smith", parent.getLastName());
        assertEquals("john.smith@example.com", parent.getEmail());
        assertEquals("555-1234", parent.getPhone());
    }

    @Test
    void testParentWithConstructor() {
        // Given & When
        Parent parent = new Parent("Jane", "Doe", "jane.doe@example.com", "555-9876");

        // Then
        assertEquals("Jane", parent.getFirstName());
        assertEquals("Doe", parent.getLastName());
        assertEquals("jane.doe@example.com", parent.getEmail());
        assertEquals("555-9876", parent.getPhone());
    }

    @Test
    void testGenderEnum() {
        // Given & When & Then
        assertEquals("MALE", Parent.Gender.MALE.name());
        assertEquals("FEMALE", Parent.Gender.FEMALE.name());
        assertEquals("OTHER", Parent.Gender.OTHER.name());
        assertEquals("PREFER_NOT_TO_SAY", Parent.Gender.PREFER_NOT_TO_SAY.name());
    }

    @Test
    void testFullName() {
        // Given
        Parent parent = new Parent();
        parent.setFirstName("Michael");
        parent.setLastName("Johnson");

        // When
        String fullName = parent.getFullName();

        // Then
        assertEquals("Michael Johnson", fullName);
    }

    @Test
    void testFullNameWithMiddleName() {
        // Given
        Parent parent = new Parent();
        parent.setFirstName("Sarah");
        parent.setMiddleName("Anne");
        parent.setLastName("Smith");

        // When
        String fullName = parent.getFullName();

        // Then
        assertEquals("Sarah Anne Smith", fullName);
    }

    @Test
    void testContactInfo() {
        // Given
        Parent parent = new Parent();
        parent.setPhone("555-0123");
        parent.setAlternatePhone("555-9876");
        parent.setWorkPhone("555-4567");
        parent.setEmail("parent@example.com");

        // Then
        assertEquals("555-0123", parent.getPhone());
        assertEquals("555-9876", parent.getAlternatePhone());
        assertEquals("555-4567", parent.getWorkPhone());
        assertEquals("parent@example.com", parent.getEmail());
    }

    @Test
    void testEmploymentInfo() {
        // Given
        Parent parent = new Parent();
        parent.setOccupation("Software Engineer");
        parent.setWorkplace("Tech Corp");
        parent.setWorkPhone("555-1234");

        // Then
        assertEquals("Software Engineer", parent.getOccupation());
        assertEquals("Tech Corp", parent.getWorkplace());
        assertEquals("555-1234", parent.getWorkPhone());
    }

    @Test
    void testAddressInfo() {
        // Given
        Parent parent = new Parent();
        parent.setAddressLine1("123 Main St");
        parent.setAddressLine2("Apt 4B");
        parent.setCity("Springfield");
        parent.setState("IL");
        parent.setPostalCode("62701");
        parent.setCountry("USA");

        // Then
        assertEquals("123 Main St", parent.getAddressLine1());
        assertEquals("Apt 4B", parent.getAddressLine2());
        assertEquals("Springfield", parent.getCity());
        assertEquals("IL", parent.getState());
        assertEquals("62701", parent.getPostalCode());
        assertEquals("USA", parent.getCountry());
    }

    @Test
    void testFullAddress() {
        // Given
        Parent parent = new Parent();
        parent.setAddressLine1("123 Main St");
        parent.setAddressLine2("Apt 4B");
        parent.setCity("Springfield");
        parent.setState("IL");
        parent.setPostalCode("62701");
        parent.setCountry("USA");

        // When
        String fullAddress = parent.getFullAddress();

        // Then
        assertEquals("123 Main St, Apt 4B, Springfield, IL 62701, USA", fullAddress);
    }

    @Test
    void testBooleanFlags() {
        // Given
        Parent parent = new Parent();
        parent.setEmergencyContact(true);
        parent.setAuthorizedPickup(false);
        parent.setReceiveNotifications(true);

        // Then
        assertTrue(parent.getEmergencyContact());
        assertFalse(parent.getAuthorizedPickup());
        assertTrue(parent.getReceiveNotifications());
    }

    @Test
    void testGenderAssignment() {
        // Given
        Parent parent = new Parent();
        parent.setGender(Parent.Gender.FEMALE);

        // Then
        assertEquals(Parent.Gender.FEMALE, parent.getGender());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Parent parent = new Parent();

        // When
        parent.setId(1L);
        parent.setFirstName("Robert");
        parent.setLastName("Wilson");
        parent.setMiddleName("James");
        parent.setEmail("robert@example.com");
        parent.setPhone("555-7890");
        parent.setGender(Parent.Gender.MALE);
        parent.setOccupation("Teacher");

        // Then
        assertEquals(1L, parent.getId());
        assertEquals("Robert", parent.getFirstName());
        assertEquals("Wilson", parent.getLastName());
        assertEquals("James", parent.getMiddleName());
        assertEquals("robert@example.com", parent.getEmail());
        assertEquals("555-7890", parent.getPhone());
        assertEquals(Parent.Gender.MALE, parent.getGender());
        assertEquals("Teacher", parent.getOccupation());
    }
}