package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.entity.FieldMappingTemplate;

/**
 * Test class for FieldMappingTemplateRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FieldMappingTemplateRepositoryTest {

    @Autowired
    private FieldMappingTemplateRepository fieldMappingTemplateRepository;

    private FieldMappingTemplate template1;
    private FieldMappingTemplate template2;
    private FieldMappingTemplate template3;

    @BeforeEach
    void setUp() {
        fieldMappingTemplateRepository.deleteAllInBatch();

        // Create test templates
        template1 = new FieldMappingTemplate();
        template1.setEntityType("students");
        template1.setFieldName("firstName");
        template1.setFieldLabel("First Name");
        template1.setIsRequired(true);
        template1.setDataType("string");
        template1.setSection("Basic Info");
        template1.setDisplayOrder(1);
        template1 = fieldMappingTemplateRepository.save(template1);

        template2 = new FieldMappingTemplate();
        template2.setEntityType("students");
        template2.setFieldName("email");
        template2.setFieldLabel("Email Address");
        template2.setIsRequired(true);
        template2.setDataType("email");
        template2.setSection("Contact Info");
        template2.setDisplayOrder(2);
        template2 = fieldMappingTemplateRepository.save(template2);

        template3 = new FieldMappingTemplate();
        template3.setEntityType("staff");
        template3.setFieldName("employeeId");
        template3.setFieldLabel("Employee ID");
        template3.setIsRequired(true);
        template3.setDataType("string");
        template3.setSection("Basic Info");
        template3.setDisplayOrder(1);
        template3 = fieldMappingTemplateRepository.save(template3);
    }

    @Test
    void testSaveFieldMappingTemplate() {
        FieldMappingTemplate newTemplate = new FieldMappingTemplate();
        newTemplate.setEntityType("students");
        newTemplate.setFieldName("phone");
        newTemplate.setFieldLabel("Phone Number");
        newTemplate.setIsRequired(false);
        newTemplate.setDataType("phone");
        newTemplate.setSection("Contact Info");
        newTemplate.setDisplayOrder(3);
        
        FieldMappingTemplate saved = fieldMappingTemplateRepository.save(newTemplate);
        
        assertNotNull(saved.getId());
        assertEquals("phone", saved.getFieldName());
        assertEquals("Phone Number", saved.getFieldLabel());
    }

    @Test
    void testFindByEntityTypeOrderByDisplayOrder() {
        List<FieldMappingTemplate> studentTemplates = fieldMappingTemplateRepository.findByEntityTypeOrderByDisplayOrder("students");
        
        assertThat(studentTemplates).hasSize(2);
        assertEquals(1, studentTemplates.get(0).getDisplayOrder());
        assertEquals(2, studentTemplates.get(1).getDisplayOrder());
    }

    @Test
    void testFindByEntityTypeAndSectionOrderByDisplayOrder() {
        List<FieldMappingTemplate> basicInfoTemplates = fieldMappingTemplateRepository.findByEntityTypeAndSectionOrderByDisplayOrder("students", "Basic Info");
        
        assertThat(basicInfoTemplates).hasSize(1);
        assertEquals("firstName", basicInfoTemplates.get(0).getFieldName());
    }

    @Test
    void testFindByEntityTypeAndFieldName() {
        Optional<FieldMappingTemplate> found = fieldMappingTemplateRepository.findByEntityTypeAndFieldName("students", "email");
        
        assertTrue(found.isPresent());
        assertEquals("Email Address", found.get().getFieldLabel());
        assertEquals("email", found.get().getDataType());
    }

    @Test
    void testUpdateFieldMappingTemplate() {
        template1.setFieldLabel("Student First Name");
        template1.setDisplayOrder(10);
        
        FieldMappingTemplate updated = fieldMappingTemplateRepository.save(template1);
        
        assertEquals("Student First Name", updated.getFieldLabel());
        assertEquals(10, updated.getDisplayOrder());
    }

    @Test
    void testDeleteFieldMappingTemplate() {
        fieldMappingTemplateRepository.delete(template1);
        
        List<FieldMappingTemplate> studentTemplates = fieldMappingTemplateRepository.findByEntityTypeOrderByDisplayOrder("students");
        assertThat(studentTemplates).hasSize(1);
    }

    @Test
    void testFindAll() {
        List<FieldMappingTemplate> allTemplates = fieldMappingTemplateRepository.findAll();
        
        assertThat(allTemplates).hasSize(3);
    }

    @Test
    void testTemplateAttributes() {
        assertTrue(template1.getIsRequired());
        assertEquals("string", template1.getDataType());
        assertEquals("Basic Info", template1.getSection());
        assertEquals(1, template1.getDisplayOrder());
    }
}
