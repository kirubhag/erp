package krs.erp.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

import krs.erp.model.ErpEntity;
import krs.erp.model.ErpEntityRoleRelation;
import krs.erp.model.Role;

/**
 * Test class for ErpEntityRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ErpEntityRepositoryTest {

    @Autowired
    private ErpEntityRepository erpEntityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ErpEntityRoleRelationRepository erpEntityRoleRelationRepository;

    private ErpEntity entity1;
    private ErpEntity entity2;
    private ErpEntity entity3;
    private Role role1;

    @BeforeEach
    void setUp() {
        erpEntityRoleRelationRepository.deleteAllInBatch();
        erpEntityRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();

        // Create test role
        role1 = new Role();
        role1.setName("TEST_ROLE_ENTITY");
        role1.setDescription("Test Role for Entity");
        role1 = roleRepository.save(role1);

        // Create test ErpEntity entities
        entity1 = new ErpEntity();
        entity1.setSingularName("Student");
        entity1.setPluralName("Students");
        entity1.setDescription("Student management");
        entity1.setIsActive(true);
        entity1.setPresence(true);
        entity1.setSequence(1);
        entity1.setRoute("/students");
        entity1.setIcon("student-icon");
        entity1 = erpEntityRepository.save(entity1);

        entity2 = new ErpEntity();
        entity2.setSingularName("Staff");
        entity2.setPluralName("Staff");
        entity2.setDescription("Staff management");
        entity2.setIsActive(true);
        entity2.setPresence(true);
        entity2.setSequence(2);
        entity2.setRoute("/staff");
        entity2.setIcon("staff-icon");
        entity2 = erpEntityRepository.save(entity2);

        entity3 = new ErpEntity();
        entity3.setSingularName("Grade");
        entity3.setPluralName("Grades");
        entity3.setDescription("Grade management");
        entity3.setIsActive(false);
        entity3.setPresence(false);
        entity3.setSequence(3);
        entity3 = erpEntityRepository.save(entity3);

        // Create entity-role relation
        ErpEntityRoleRelation relation = new ErpEntityRoleRelation();
        relation.setErpEntity(entity1);
        relation.setRole(role1);
        erpEntityRoleRelationRepository.save(relation);
    }

    @Test
    void testSaveErpEntity() {
        ErpEntity newEntity = new ErpEntity();
        newEntity.setSingularName("Parent");
        newEntity.setPluralName("Parents");
        newEntity.setDescription("Parent management");
        newEntity.setIsActive(true);
        
        ErpEntity saved = erpEntityRepository.save(newEntity);
        
        assertNotNull(saved.getId());
        assertEquals("Parent", saved.getSingularName());
        assertEquals("Parents", saved.getPluralName());
    }

    @Test
    void testFindBySingularName() {
        Optional<ErpEntity> found = erpEntityRepository.findBySingularName("Student");
        
        assertTrue(found.isPresent());
        assertEquals("Students", found.get().getPluralName());
    }

    @Test
    void testFindByIsActiveTrue() {
        List<ErpEntity> activeEntities = erpEntityRepository.findByIsActiveTrue();
        
        assertThat(activeEntities).hasSize(2);
        assertTrue(activeEntities.stream().allMatch(ErpEntity::getIsActive));
    }

    @Test
    void testFindActiveEntitiesByRoleId() {
        List<ErpEntity> entities = erpEntityRepository.findActiveEntitiesByRoleId(role1.getId());
        
        assertThat(entities).hasSize(1);
        assertEquals("Student", entities.get(0).getSingularName());
    }

    @Test
    void testFindActiveEntitiesByRoleIds() {
        List<ErpEntity> entities = erpEntityRepository.findActiveEntitiesByRoleIds(Arrays.asList(role1.getId()));
        
        assertThat(entities).hasSize(1);
        assertEquals("Student", entities.get(0).getSingularName());
    }

    @Test
    void testIsEntityAccessibleByRole() {
        assertTrue(erpEntityRepository.isEntityAccessibleByRole(entity1.getId(), role1.getId()));
        assertFalse(erpEntityRepository.isEntityAccessibleByRole(entity2.getId(), role1.getId()));
    }

    @Test
    void testFindActiveMenuItems() {
        List<ErpEntity> menuItems = erpEntityRepository.findActiveMenuItems();
        
        assertThat(menuItems).hasSize(2);
        assertEquals(1, menuItems.get(0).getSequence());
        assertEquals(2, menuItems.get(1).getSequence());
    }

    @Test
    void testFindActiveMenuItemsByRoleIds() {
        List<ErpEntity> menuItems = erpEntityRepository.findActiveMenuItemsByRoleIds(Arrays.asList(role1.getId()));
        
        assertThat(menuItems).hasSize(1);
        assertEquals("Student", menuItems.get(0).getSingularName());
    }

    @Test
    void testUpdateErpEntity() {
        entity1.setDescription("Updated student management");
        entity1.setSequence(10);
        
        ErpEntity updated = erpEntityRepository.save(entity1);
        
        assertEquals("Updated student management", updated.getDescription());
        assertEquals(10, updated.getSequence());
    }

    @Test
    void testDeleteErpEntity() {
        erpEntityRepository.delete(entity3);
        
        Optional<ErpEntity> found = erpEntityRepository.findBySingularName("Grade");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        List<ErpEntity> allEntities = erpEntityRepository.findAll();
        assertThat(allEntities).hasSize(3);
    }

    @Test
    void testEntityAttributes() {
        assertTrue(entity1.getIsActive());
        assertTrue(entity1.getPresence());
        assertFalse(entity3.getIsActive());
        assertEquals("/students", entity1.getRoute());
        assertEquals("student-icon", entity1.getIcon());
    }
}
