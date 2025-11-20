package krs.erp.repository;

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
 * Test class for ErpEntityRoleRelationRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ErpEntityRoleRelationRepositoryTest {

    @Autowired
    private ErpEntityRoleRelationRepository erpEntityRoleRelationRepository;

    @Autowired
    private ErpEntityRepository erpEntityRepository;

    @Autowired
    private RoleRepository roleRepository;

    private ErpEntity entity1;
    private ErpEntity entity2;
    private Role role1;
    private Role role2;
    private ErpEntityRoleRelation relation1;
    private ErpEntityRoleRelation relation2;

    @BeforeEach
    void setUp() {
        erpEntityRoleRelationRepository.deleteAllInBatch();
        erpEntityRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();

        // Create test entities
        entity1 = new ErpEntity();
        entity1.setSingularName("Student");
        entity1.setPluralName("Students");
        entity1.setIsActive(true);
        entity1 = erpEntityRepository.save(entity1);

        entity2 = new ErpEntity();
        entity2.setSingularName("Staff");
        entity2.setPluralName("Staff");
        entity2.setIsActive(true);
        entity2 = erpEntityRepository.save(entity2);

        // Create test roles
        role1 = new Role();
        role1.setName("ADMIN_ROLE");
        role1.setDescription("Admin Role");
        role1 = roleRepository.save(role1);

        role2 = new Role();
        role2.setName("USER_ROLE");
        role2.setDescription("User Role");
        role2 = roleRepository.save(role2);

        // Create test relations
        relation1 = new ErpEntityRoleRelation();
        relation1.setErpEntity(entity1);
        relation1.setRole(role1);
        relation1 = erpEntityRoleRelationRepository.save(relation1);

        relation2 = new ErpEntityRoleRelation();
        relation2.setErpEntity(entity1);
        relation2.setRole(role2);
        relation2 = erpEntityRoleRelationRepository.save(relation2);
    }

    @Test
    void testSaveErpEntityRoleRelation() {
        ErpEntityRoleRelation newRelation = new ErpEntityRoleRelation();
        newRelation.setErpEntity(entity2);
        newRelation.setRole(role1);
        
        ErpEntityRoleRelation saved = erpEntityRoleRelationRepository.save(newRelation);
        
        assertNotNull(saved.getId());
        assertEquals(entity2.getId(), saved.getErpEntity().getId());
        assertEquals(role1.getId(), saved.getRole().getId());
    }

    @Test
    void testFindByErpEntityId() {
        List<ErpEntityRoleRelation> relations = erpEntityRoleRelationRepository.findByErpEntityId(entity1.getId());
        
        assertThat(relations).hasSize(2);
    }

    @Test
    void testFindByRoleId() {
        List<ErpEntityRoleRelation> relations = erpEntityRoleRelationRepository.findByRoleId(role1.getId());
        
        assertThat(relations).hasSize(1);
        assertEquals(entity1.getId(), relations.get(0).getErpEntity().getId());
    }

    @Test
    void testFindByErpEntityIdAndRoleId() {
        Optional<ErpEntityRoleRelation> found = erpEntityRoleRelationRepository.findByErpEntityIdAndRoleId(
            entity1.getId(), role1.getId());
        
        assertTrue(found.isPresent());
        assertEquals(entity1.getId(), found.get().getErpEntity().getId());
        assertEquals(role1.getId(), found.get().getRole().getId());
    }

    @Test
    void testExistsByErpEntityIdAndRoleId() {
        assertTrue(erpEntityRoleRelationRepository.existsByErpEntityIdAndRoleId(entity1.getId(), role1.getId()));
        assertFalse(erpEntityRoleRelationRepository.existsByErpEntityIdAndRoleId(entity2.getId(), role1.getId()));
    }

    @Test
    void testCountByErpEntityId() {
        long count = erpEntityRoleRelationRepository.countByErpEntityId(entity1.getId());
        assertEquals(2L, count);
    }

    @Test
    void testCountByRoleId() {
        long count = erpEntityRoleRelationRepository.countByRoleId(role1.getId());
        assertEquals(1L, count);
    }

    @Test
    void testDeleteErpEntityRoleRelation() {
        erpEntityRoleRelationRepository.delete(relation1);
        
        List<ErpEntityRoleRelation> relations = erpEntityRoleRelationRepository.findByErpEntityId(entity1.getId());
        assertThat(relations).hasSize(1);
    }

    @Test
    void testFindAll() {
        List<ErpEntityRoleRelation> allRelations = erpEntityRoleRelationRepository.findAll();
        assertThat(allRelations).hasSize(2);
    }
}
