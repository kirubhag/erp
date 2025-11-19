package krs.erp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import krs.erp.model.Organization;
import krs.erp.model.OrganizationSettings;

/**
 * Unit tests for OrganizationSettingsRepository
 * Tests all custom query methods for OrganizationSettings entity CRUD operations
 */
@DataJpaTest
@ActiveProfiles("test")
class OrganizationSettingsRepositoryTest {

    @Autowired
    private OrganizationSettingsRepository organizationSettingsRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization testOrganization;
    private OrganizationSettings testSettings;

    @BeforeEach
    void setUp() {
        // Clear repositories before each test
        organizationSettingsRepository.deleteAll();
        organizationRepository.deleteAll();

        // Create test organization
        testOrganization = new Organization();
        testOrganization.setName("Test Organization");
        testOrganization.setDescription("Test Org Description");
        testOrganization = organizationRepository.save(testOrganization);

        // Create test settings
        testSettings = new OrganizationSettings();
        testSettings.setOrganizationId(testOrganization.getId());
        testSettings.setMainTheme("blue");
        testSettings.setSecondaryTheme("light");
        testSettings.setMaxUsers(100);
        testSettings.setMaxStorage(1000);
        testSettings.setEnableNotifications(true);
        testSettings.setEnableReports(true);
        testSettings = organizationSettingsRepository.save(testSettings);
    }

    // ==================== BASIC CRUD TESTS ====================

    @Test
    void testSaveOrganizationSettings() {
        // Given
        OrganizationSettings newSettings = new OrganizationSettings();
        newSettings.setOrganizationId(testOrganization.getId());
        newSettings.setMainTheme("green");
        newSettings.setSecondaryTheme("dark");
        newSettings.setMaxUsers(200);
        newSettings.setMaxStorage(2000);
        newSettings.setEnableNotifications(true);
        newSettings.setEnableReports(false);

        // When
        OrganizationSettings savedSettings = organizationSettingsRepository.save(newSettings);

        // Then
        assertNotNull(savedSettings.getId());
        assertEquals(testOrganization.getId(), savedSettings.getOrganizationId());
        assertEquals("green", savedSettings.getMainTheme());
        assertEquals(200, savedSettings.getMaxUsers());
    }

    @Test
    void testFindByOrganizationId() {
        // When
        Optional<OrganizationSettings> foundSettings = organizationSettingsRepository
            .findByOrganizationId(testOrganization.getId());

        // Then
        assertTrue(foundSettings.isPresent());
        assertEquals(testSettings.getId(), foundSettings.get().getId());
        assertEquals("blue", foundSettings.get().getMainTheme());
        assertEquals(100, foundSettings.get().getMaxUsers());
    }

    @Test
    void testUpdateMainTheme() {
        // When
        organizationSettingsRepository.updateMainTheme(testOrganization.getId(), "red");

        // Then
        Optional<OrganizationSettings> updatedSettings = organizationSettingsRepository
            .findByOrganizationId(testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals("red", updatedSettings.get().getMainTheme());
    }

    @Test
    void testUpdateSecondaryTheme() {
        // When
        organizationSettingsRepository.updateSecondaryTheme(testOrganization.getId(), "dark");

        // Then
        Optional<OrganizationSettings> updatedSettings = organizationSettingsRepository
            .findByOrganizationId(testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals("dark", updatedSettings.get().getSecondaryTheme());
    }

    @Test
    void testUpdateMaxUsers() {
        // When
        organizationSettingsRepository.updateMaxUsers(testOrganization.getId(), 500);

        // Then
        Optional<OrganizationSettings> updatedSettings = organizationSettingsRepository
            .findByOrganizationId(testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals(500, updatedSettings.get().getMaxUsers());
    }

    @Test
    void testUpdateMaxStorage() {
        // When
        organizationSettingsRepository.updateMaxStorage(testOrganization.getId(), 5000);

        // Then
        Optional<OrganizationSettings> updatedSettings = organizationSettingsRepository
            .findByOrganizationId(testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals(5000, updatedSettings.get().getMaxStorage());
    }

    @Test
    void testUpdateNotificationsEnabled() {
        // When
        organizationSettingsRepository.updateNotificationsEnabled(testOrganization.getId(), false);

        // Then
        Optional<OrganizationSettings> updatedSettings = organizationSettingsRepository
            .findByOrganizationId(testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals(false, updatedSettings.get().isEnableNotifications());
    }

    @Test
    void testUpdateReportsEnabled() {
        // When
        organizationSettingsRepository.updateReportsEnabled(testOrganization.getId(), false);

        // Then
        Optional<OrganizationSettings> updatedSettings = organizationSettingsRepository
            .findByOrganizationId(testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals(false, updatedSettings.get().isEnableReports());
    }

    @Test
    void testUpdateOrganizationSettings() {
        // Given
        testSettings.setMainTheme("purple");
        testSettings.setSecondaryTheme("dark");
        testSettings.setMaxUsers(300);
        testSettings.setMaxStorage(3000);
        testSettings.setEnableNotifications(false);
        testSettings.setEnableReports(true);

        // When
        OrganizationSettings updatedSettings = organizationSettingsRepository.save(testSettings);

        // Then
        assertEquals("purple", updatedSettings.getMainTheme());
        assertEquals("dark", updatedSettings.getSecondaryTheme());
        assertEquals(300, updatedSettings.getMaxUsers());
        assertEquals(3000, updatedSettings.getMaxStorage());
        assertEquals(false, updatedSettings.isEnableNotifications());
        assertEquals(true, updatedSettings.isEnableReports());
    }

    @Test
    void testDeleteOrganizationSettings() {
        // Given
        Long settingsId = testSettings.getId();

        // When
        organizationSettingsRepository.deleteById(settingsId);

        // Then
        Optional<OrganizationSettings> deletedSettings = organizationSettingsRepository.findById(settingsId);
        assertTrue(deletedSettings.isEmpty());
    }
}
