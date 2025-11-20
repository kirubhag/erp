package krs.erp.repository;

import java.util.Optional;

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

import krs.erp.model.UserSettings;

/**
 * Test class for UserSettingsRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserSettingsRepositoryTest {

    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private UserSettings settings1;
    private UserSettings settings2;
    private krs.erp.model.Organization organization;
    private krs.erp.model.User user1;
    private krs.erp.model.User user2;
    private krs.erp.model.User user3;
    private krs.erp.model.User user4;

    @BeforeEach
    void setUp() {
        userSettingsRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        organizationRepository.deleteAllInBatch();

        // Create test Organization
        organization = new krs.erp.model.Organization();
        organization.setName("Test Organization");
        organization.setCode("TEST_ORG");
        organization.setType("School");
        organization = organizationRepository.save(organization);

        // Create test Users
        user1 = new krs.erp.model.User();
        user1.setUsername("testuser1");
        user1.setEmail("user1@test.com");
        user1.setPasswordHash("password1");
        user1.setFirstName("Test");
        user1.setLastName("User1");
        user1.setEnabled(true);
        user1.setUserType(krs.erp.model.User.UserType.ADMIN);
        user1 = userRepository.save(user1);

        user2 = new krs.erp.model.User();
        user2.setUsername("testuser2");
        user2.setEmail("user2@test.com");
        user2.setPasswordHash("password2");
        user2.setFirstName("Test");
        user2.setLastName("User2");
        user2.setEnabled(true);
        user2.setUserType(krs.erp.model.User.UserType.ADMIN);
        user2 = userRepository.save(user2);

        user3 = new krs.erp.model.User();
        user3.setUsername("testuser3");
        user3.setEmail("user3@test.com");
        user3.setPasswordHash("password3");
        user3.setFirstName("Test");
        user3.setLastName("User3");
        user3.setEnabled(true);
        user3.setUserType(krs.erp.model.User.UserType.ADMIN);
        user3 = userRepository.save(user3);

        user4 = new krs.erp.model.User();
        user4.setUsername("testuser4");
        user4.setEmail("user4@test.com");
        user4.setPasswordHash("password4");
        user4.setFirstName("Test");
        user4.setLastName("User4");
        user4.setEnabled(true);
        user4.setUserType(krs.erp.model.User.UserType.ADMIN);
        user4 = userRepository.save(user4);

        // Create test UserSettings
        settings1 = new UserSettings();
        settings1.setUserId(user1.getId());
        settings1.setOrganizationId(organization.getId());
        settings1.setTheme("#0099cc");
        settings1.setDefaultListView("table");
        settings1.setRecordsPerPage(25);
        settings1.setListSidebarExpanded(true);
        settings1 = userSettingsRepository.save(settings1);

        settings2 = new UserSettings();
        settings2.setUserId(user2.getId());
        settings2.setOrganizationId(organization.getId());
        settings2.setTheme("#ff5733");
        settings2.setDefaultListView("card");
        settings2.setRecordsPerPage(50);
        settings2.setListSidebarExpanded(false);
        settings2 = userSettingsRepository.save(settings2);
    }

    @Test
    void testSaveUserSettings() {
        UserSettings newSettings = new UserSettings();
        newSettings.setUserId(user3.getId());
        newSettings.setOrganizationId(organization.getId());
        newSettings.setTheme("#00ff00");
        
        UserSettings saved = userSettingsRepository.save(newSettings);
        
        assertNotNull(saved.getId());
        assertEquals(user3.getId(), saved.getUserId());
        assertEquals("#00ff00", saved.getTheme());
    }

    @Test
    void testFindByUserIdAndOrganizationId() {
        Optional<UserSettings> found = userSettingsRepository.findByUserIdAndOrganizationId(user1.getId(), organization.getId());
        
        assertTrue(found.isPresent());
        assertEquals("#0099cc", found.get().getTheme());
        assertEquals("table", found.get().getDefaultListView());
    }

    @Test
    void testUpdateThemePreference() {
        userSettingsRepository.updateThemePreference(user1.getId(), organization.getId(), "#333333");
        userSettingsRepository.flush();
        entityManager.clear();
        
        Optional<UserSettings> updated = userSettingsRepository.findByUserIdAndOrganizationId(user1.getId(), organization.getId());
        assertTrue(updated.isPresent());
        assertEquals("#333333", updated.get().getTheme());
    }

    @Test
    void testUpdateListItemsPerPage() {
        userSettingsRepository.updateListItemsPerPage(user1.getId(), organization.getId(), 100);
        userSettingsRepository.flush();
        entityManager.clear();
        
        Optional<UserSettings> updated = userSettingsRepository.findByUserIdAndOrganizationId(user1.getId(), organization.getId());
        assertTrue(updated.isPresent());
        assertEquals(100, updated.get().getRecordsPerPage());
    }

    @Test
    void testUpdateListSidebarState() {
        userSettingsRepository.updateListSidebarState(user1.getId(), organization.getId(), false);
        userSettingsRepository.flush();
        entityManager.clear();
        
        Optional<UserSettings> updated = userSettingsRepository.findByUserIdAndOrganizationId(user1.getId(), organization.getId());
        assertTrue(updated.isPresent());
        assertFalse(updated.get().getListSidebarExpanded());
    }

    @Test
    void testUpdateDefaultListView() {
        userSettingsRepository.updateDefaultListView(user1.getId(), organization.getId(), "grid");
        userSettingsRepository.flush();
        entityManager.clear();
        
        Optional<UserSettings> updated = userSettingsRepository.findByUserIdAndOrganizationId(user1.getId(), organization.getId());
        assertTrue(updated.isPresent());
        assertEquals("grid", updated.get().getDefaultListView());
    }

    @Test
    void testUpdateUserSettings() {
        settings1.setTheme("#ffffff");
        settings1.setRecordsPerPage(75);
        
        UserSettings updated = userSettingsRepository.save(settings1);
        
        assertEquals("#ffffff", updated.getTheme());
        assertEquals(75, updated.getRecordsPerPage());
    }

    @Test
    void testDeleteUserSettings() {
        userSettingsRepository.delete(settings1);
        
        Optional<UserSettings> found = userSettingsRepository.findByUserIdAndOrganizationId(user1.getId(), organization.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testSettingsDefaults() {
        UserSettings newSettings = new UserSettings(user4.getId(), organization.getId());
        newSettings = userSettingsRepository.save(newSettings);
        
        assertNotNull(newSettings.getLastUpdated());
        assertTrue(newSettings.getIsActive());
    }
}
