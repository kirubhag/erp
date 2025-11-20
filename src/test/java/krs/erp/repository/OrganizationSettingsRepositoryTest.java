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

import krs.erp.model.Organization;
import krs.erp.model.OrganizationSettings;

/**
 * Test class for OrganizationSettingsRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrganizationSettingsRepositoryTest {

    @Autowired
    private OrganizationSettingsRepository organizationSettingsRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization organization1;
    private Organization organization2;
    private OrganizationSettings settings1;
    private OrganizationSettings settings2;

    @BeforeEach
    void setUp() {
        organizationSettingsRepository.deleteAllInBatch();
        organizationRepository.deleteAllInBatch();

        // Create test Organizations
        organization1 = new Organization();
        organization1.setName("Test Organization 1");
        organization1.setCode("TEST_ORG_1");
        organization1.setType("School");
        organization1 = organizationRepository.save(organization1);

        organization2 = new Organization();
        organization2.setName("Test Organization 2");
        organization2.setCode("TEST_ORG_2");
        organization2.setType("University");
        organization2 = organizationRepository.save(organization2);

        // Create test OrganizationSettings
        settings1 = new OrganizationSettings();
        settings1.setOrganizationId(organization1.getId());
        settings1.setDefaultTheme("light");
        settings1.setDefaultLanguage("en");
        settings1.setDefaultTimezone("UTC");
        settings1.setDefaultDateFormat("yyyy-MM-dd");
        settings1.setDefaultTimeFormat("HH:mm:ss");
        settings1.setItemsPerPage(25);
        settings1.setDefaultListView("table");
        settings1.setIsActive(true);
        settings1.setCreatedBy("test-user");
        settings1 = organizationSettingsRepository.save(settings1);

        settings2 = new OrganizationSettings();
        settings2.setOrganizationId(organization2.getId());
        settings2.setDefaultTheme("dark");
        settings2.setDefaultLanguage("es");
        settings2.setDefaultTimezone("EST");
        settings2.setDefaultDateFormat("dd/MM/yyyy");
        settings2.setDefaultTimeFormat("hh:mm a");
        settings2.setItemsPerPage(50);
        settings2.setDefaultListView("card");
        settings2.setIsActive(false);
        settings2.setCreatedBy("test-user");
        settings2 = organizationSettingsRepository.save(settings2);
    }

    @Test
    void testSaveOrganizationSettings() {
        Organization newOrg = new Organization();
        newOrg.setName("New Test Org");
        newOrg.setCode("NEW_ORG");
        newOrg.setType("College");
        newOrg = organizationRepository.save(newOrg);

        OrganizationSettings newSettings = new OrganizationSettings();
        newSettings.setOrganizationId(newOrg.getId());
        newSettings.setDefaultTheme("light");
        newSettings.setCreatedBy("test-user");
        
        OrganizationSettings saved = organizationSettingsRepository.save(newSettings);
        
        assertNotNull(saved.getId());
        assertEquals("light", saved.getDefaultTheme());
    }

    @Test
    void testFindById() {
        Optional<OrganizationSettings> found = organizationSettingsRepository.findById(settings1.getId());
        
        assertTrue(found.isPresent());
        assertEquals("light", found.get().getDefaultTheme());
        assertEquals("en", found.get().getDefaultLanguage());
    }

    @Test
    void testFindAll() {
        var allSettings = organizationSettingsRepository.findAll();
        assertEquals(2, allSettings.size());
    }

    @Test
    void testUpdateOrganizationSettings() {
        settings1.setDefaultTheme("dark");
        settings1.setItemsPerPage(100);
        
        OrganizationSettings updated = organizationSettingsRepository.save(settings1);
        
        assertEquals("dark", updated.getDefaultTheme());
        assertEquals(100, updated.getItemsPerPage());
    }

    @Test
    void testDeleteOrganizationSettings() {
        Long id = settings1.getId();
        organizationSettingsRepository.delete(settings1);
        
        Optional<OrganizationSettings> found = organizationSettingsRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void testSettingsAttributes() {
        assertEquals("UTC", settings1.getDefaultTimezone());
        assertEquals("yyyy-MM-dd", settings1.getDefaultDateFormat());
        assertEquals("table", settings1.getDefaultListView());
        assertTrue(settings1.getIsActive());
        assertFalse(settings2.getIsActive());
    }
}
