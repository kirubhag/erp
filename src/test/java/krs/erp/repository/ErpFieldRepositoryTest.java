package krs.erp.repository;

import java.util.List;

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
import krs.erp.model.ErpField;
import krs.erp.model.FieldType;

/**
 * Test class for ErpFieldRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ErpFieldRepositoryTest {

    @Autowired
    private ErpFieldRepository erpFieldRepository;

    private ErpField field1;
    private ErpField field2;
    private ErpField field3;

    @BeforeEach
    void setUp() {
        erpFieldRepository.deleteAllInBatch();

        // Create test ErpField entities
        field1 = new ErpField();
        field1.setEntityType(EntityType.STUDENT);
        field1.setFieldName("firstName");
        field1.setFieldLabel("First Name");
        field1.setFieldType(FieldType.STRING);
        field1.setIsRequired(true);
        field1.setIsSearchable(true);
        field1.setIsSortable(true);
        field1.setDisplayOrder(1);
        field1.setIsActive(1);
        field1 = erpFieldRepository.save(field1);

        field2 = new ErpField();
        field2.setEntityType(EntityType.STUDENT);
        field2.setFieldName("email");
        field2.setFieldLabel("Email Address");
        field2.setFieldType(FieldType.EMAIL);
        field2.setIsRequired(true);
        field2.setIsSearchable(true);
        field2.setIsSortable(true);
        field2.setDisplayOrder(2);
        field2.setIsActive(1);
        field2 = erpFieldRepository.save(field2);

        field3 = new ErpField();
        field3.setEntityType(EntityType.STAFF);
        field3.setFieldName("employeeId");
        field3.setFieldLabel("Employee ID");
        field3.setFieldType(FieldType.STRING);
        field3.setIsRequired(true);
        field3.setIsSearchable(true);
        field3.setIsSortable(false);
        field3.setDisplayOrder(1);
        field3.setIsActive(1);
        field3 = erpFieldRepository.save(field3);
    }

    @Test
    void testSaveErpField() {
        ErpField newField = new ErpField();
        newField.setEntityType(EntityType.STUDENT);
        newField.setFieldName("phone");
        newField.setFieldLabel("Phone Number");
        newField.setFieldType(FieldType.PHONE);
        newField.setIsActive(1);
        
        ErpField saved = erpFieldRepository.save(newField);
        
        assertNotNull(saved.getId());
        assertEquals("phone", saved.getFieldName());
        assertEquals(EntityType.STUDENT, saved.getEntityType());
    }

    @Test
    void testFindByEntityTypeAndIsActiveTrue() {
        List<ErpField> studentFields = erpFieldRepository.findByEntityTypeAndIsActiveTrue(EntityType.STUDENT);
        
        assertThat(studentFields).hasSize(2);
        assertEquals(1, studentFields.get(0).getDisplayOrder());
        assertEquals(2, studentFields.get(1).getDisplayOrder());
    }

    @Test
    void testFindByEntityTypeAndIsActiveTrueWithPagination() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<ErpField> page = erpFieldRepository.findByEntityTypeAndIsActiveTrue(EntityType.STUDENT, pageRequest);
        
        assertThat(page.getContent()).hasSize(1);
        assertEquals(2, page.getTotalElements());
    }

    // Test removed - field categories are deprecated in favor of sections

    @Test
    void testFindSearchableFieldsByEntityType() {
        List<ErpField> searchableFields = erpFieldRepository.findSearchableFieldsByEntityType(EntityType.STUDENT);
        
        assertThat(searchableFields).hasSize(2);
        assertTrue(searchableFields.stream().allMatch(ErpField::getIsSearchable));
    }

    @Test
    void testFindSortableFieldsByEntityType() {
        List<ErpField> sortableFields = erpFieldRepository.findSortableFieldsByEntityType(EntityType.STUDENT);
        
        assertThat(sortableFields).hasSize(2);
        assertTrue(sortableFields.stream().allMatch(ErpField::getIsSortable));
    }

    @Test
    void testFindByEntityTypeAndFieldNameAndIsActiveTrue() {
        ErpField found = erpFieldRepository.findByEntityTypeAndFieldNameAndIsActiveTrue(
            EntityType.STUDENT, "email");
        
        assertNotNull(found);
        assertEquals("Email Address", found.getFieldLabel());
        assertEquals(FieldType.EMAIL, found.getFieldType());
    }

    @Test
    void testExistsByEntityTypeAndFieldNameAndIsActiveTrue() {
        assertTrue(erpFieldRepository.existsByEntityTypeAndFieldNameAndIsActiveTrue(
            EntityType.STUDENT, "firstName"));
        assertFalse(erpFieldRepository.existsByEntityTypeAndFieldNameAndIsActiveTrue(
            EntityType.STUDENT, "nonexistent"));
    }

    // Test removed - field categories are deprecated in favor of sections

    @Test
    void testCountByEntityTypeAndIsActiveTrue() {
        Long count = erpFieldRepository.countByEntityTypeAndIsActiveTrue(EntityType.STUDENT);
        assertEquals(2L, count);
    }

    @Test
    void testUpdateErpField() {
        field1.setFieldLabel("Student First Name");
        field1.setDisplayOrder(10);
        
        ErpField updated = erpFieldRepository.save(field1);
        
        assertEquals("Student First Name", updated.getFieldLabel());
        assertEquals(10, updated.getDisplayOrder());
    }

    @Test
    void testDeleteErpField() {
        erpFieldRepository.delete(field1);
        
        List<ErpField> studentFields = erpFieldRepository.findByEntityTypeAndIsActiveTrue(EntityType.STUDENT);
        assertThat(studentFields).hasSize(1);
    }

    @Test
    void testFindAll() {
        List<ErpField> allFields = erpFieldRepository.findAll();
        assertThat(allFields).hasSize(3);
    }

    @Test
    void testFieldAttributes() {
        assertTrue(field1.getIsRequired());
        assertTrue(field1.getIsSearchable());
        assertTrue(field1.getIsSortable());
        assertFalse(field3.getIsSortable());
    }
}
