package krs.erp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import krs.erp.model.Organization;
import krs.erp.model.User;
import krs.erp.model.UserSettings;

/**
 * Unit tests for UserSettingsRepository
 * Tests all custom query methods for UserSettings entity CRUD operations
 */
@DataJpaTest
@ActiveProfiles("test")
class UserSettingsRepositoryTest {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private User testUser;
    private Organization testOrganization;
    private UserSettings testSettings;

    @BeforeEach
    void setUp() {
        // Clear repositories before each test
        userSettingsRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();

        // Create test organization
        testOrganization = new Organization();
        testOrganization.setName("Test Organization");
        testOrganization.setDescription("Test Org Description");
        testOrganization = organizationRepository.save(testOrganization);

        // Create test user
        testUser = new User();
        testUser.setUsername("test_user");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setUserType(User.UserType.ADMIN);
        testUser.setEnabled(true);
        testUser.setOrganizationId(testOrganization.getId());
        testUser = userRepository.save(testUser);

        // Create test settings
        testSettings = new UserSettings();
        testSettings.setUserId(testUser.getId());
        testSettings.setOrganizationId(testOrganization.getId());
        testSettings.setDefaultListView("table");
        testSettings.setRecordsPerPage(50);
        testSettings.setListSidebarExpanded(true);
        testSettings.setTheme("light");
        testSettings = userSettingsRepository.save(testSettings);
    }

    // ==================== BASIC CRUD TESTS ====================

    @Test
    void testSaveUserSettings() {
        // Given
        UserSettings newSettings = new UserSettings();
        newSettings.setUserId(testUser.getId());
        newSettings.setOrganizationId(testOrganization.getId());
        newSettings.setDefaultListView("grid");
        newSettings.setRecordsPerPage(100);
        newSettings.setListSidebarExpanded(false);

        // When
        UserSettings savedSettings = userSettingsRepository.save(newSettings);

        // Then
        assertNotNull(savedSettings.getId());
        assertEquals(testUser.getId(), savedSettings.getUserId());
        assertEquals(testOrganization.getId(), savedSettings.getOrganizationId());
        assertEquals("grid", savedSettings.getDefaultListView());
        assertEquals(100, savedSettings.getRecordsPerPage());
    }

    @Test
    void testFindByUserIdAndOrganizationId() {
        // When
        Optional<UserSettings> foundSettings = userSettingsRepository
            .findByUserIdAndOrganizationId(testUser.getId(), testOrganization.getId());

        // Then
        assertTrue(foundSettings.isPresent());
        assertEquals(testSettings.getId(), foundSettings.get().getId());
        assertEquals("table", foundSettings.get().getDefaultListView());
        assertEquals(50, foundSettings.get().getRecordsPerPage());
    }

    @Test
    void testUpdateDefaultListView() {
        // When
        userSettingsRepository.updateDefaultListView(
            testUser.getId(),
            testOrganization.getId(),
            "grid"
        );

        // Then
        Optional<UserSettings> updatedSettings = userSettingsRepository
            .findByUserIdAndOrganizationId(testUser.getId(), testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals("grid", updatedSettings.get().getDefaultListView());
    }

    @Test
    void testUpdateListItemsPerPage() {
        // When
        userSettingsRepository.updateListItemsPerPage(
            testUser.getId(),
            testOrganization.getId(),
            200
        );

        // Then
        Optional<UserSettings> updatedSettings = userSettingsRepository
            .findByUserIdAndOrganizationId(testUser.getId(), testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals(200, updatedSettings.get().getRecordsPerPage());
    }

    @Test
    void testUpdateListSidebarState() {
        // When
        userSettingsRepository.updateListSidebarState(
            testUser.getId(),
            testOrganization.getId(),
            false
        );

        // Then
        Optional<UserSettings> updatedSettings = userSettingsRepository
            .findByUserIdAndOrganizationId(testUser.getId(), testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals(false, updatedSettings.get().isListSidebarExpanded());
    }

    @Test
    void testUpdateThemePreference() {
        // When
        userSettingsRepository.updateThemePreference(
            testUser.getId(),
            testOrganization.getId(),
            "dark"
        );

        // Then
        Optional<UserSettings> updatedSettings = userSettingsRepository
            .findByUserIdAndOrganizationId(testUser.getId(), testOrganization.getId());
        assertTrue(updatedSettings.isPresent());
        assertEquals("dark", updatedSettings.get().getTheme());
    }

    @Test
    void testUpdateUserSettings() {
        // Given
        testSettings.setDefaultListView("grid");
        testSettings.setRecordsPerPage(100);
        testSettings.setListSidebarExpanded(false);
        testSettings.setTheme("dark");

        // When
        UserSettings updatedSettings = userSettingsRepository.save(testSettings);

        // Then
        assertEquals("grid", updatedSettings.getDefaultListView());
        assertEquals(100, updatedSettings.getRecordsPerPage());
        assertEquals(false, updatedSettings.isListSidebarExpanded());
        assertEquals("dark", updatedSettings.getTheme());
    }

    @Test
    void testDeleteUserSettings() {
        // Given
        Long settingsId = testSettings.getId();

        // When
        userSettingsRepository.deleteById(settingsId);

        // Then
        Optional<UserSettings> deletedSettings = userSettingsRepository.findById(settingsId);
        assertTrue(deletedSettings.isEmpty());
    }

    @Test
    void testLastUpdatedTimestamp() {
        // Given
        LocalDateTime beforeUpdate = LocalDateTime.now();
        testSettings.setTheme("dark");

        // When
        UserSettings updatedSettings = userSettingsRepository.save(testSettings);

        // Then
        assertNotNull(updatedSettings.getLastUpdated());
        assertTrue(updatedSettings.getLastUpdated().isAfter(beforeUpdate.minusSeconds(1)));
    }
}
