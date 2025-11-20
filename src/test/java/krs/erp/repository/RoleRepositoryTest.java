package krs.erp.repository;

import java.time.LocalDateTime;
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

import krs.erp.model.Role;

/**
 * Unit tests for RoleRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoleRepositoryTest {
    
    @Autowired
    private RoleRepository roleRepository;
    
    private Role role1;
    private Role role2;
    private Role role3;
    
    @BeforeEach
    void setUp() {
        roleRepository.deleteAllInBatch();
        
        // Create role 1 - Admin (System Role)
        role1 = new Role();
        role1.setName("ROLE_ADMIN");
        role1.setDescription("System Administrator Role");
        role1.setSystemRole(true);
        role1.setIsActive(1);
        role1.setCreatedTime(LocalDateTime.now());
        role1 = roleRepository.save(role1);
        
        // Create role 2 - Teacher (Custom Role)
        role2 = new Role();
        role2.setName("ROLE_TEACHER");
        role2.setDescription("Teacher Role");
        role2.setSystemRole(false);
        role2.setIsActive(1);
        role2.setCreatedTime(LocalDateTime.now());
        role2 = roleRepository.save(role2);
        
        // Create role 3 - Student (System Role)
        role3 = new Role();
        role3.setName("ROLE_STUDENT");
        role3.setDescription("Student Role");
        role3.setSystemRole(true);
        role3.setIsActive(1);
        role3.setCreatedTime(LocalDateTime.now());
        role3 = roleRepository.save(role3);
    }
    
    @Test
    void testFindByName() {
        Optional<Role> found = roleRepository.findByName("ROLE_ADMIN");
        
        assertThat(found).isPresent();
        assertEquals("System Administrator Role", found.get().getDescription());
    }
    
    @Test
    void testFindBySystemRoleTrue() {
        List<Role> systemRoles = roleRepository.findBySystemRoleTrue();
        
        assertThat(systemRoles).hasSize(2);
        assertThat(systemRoles).allMatch(Role::getSystemRole);
    }
    
    @Test
    void testFindBySystemRoleFalse() {
        List<Role> customRoles = roleRepository.findBySystemRoleFalse();
        
        assertThat(customRoles).hasSize(1);
        assertEquals("ROLE_TEACHER", customRoles.get(0).getName());
    }
    
    @Test
    void testFindByNameContaining() {
        List<Role> rolesWithTeacher = roleRepository.findByNameContaining("teacher");
        
        assertThat(rolesWithTeacher).hasSize(1);
        assertEquals("ROLE_TEACHER", rolesWithTeacher.get(0).getName());
    }
    
    @Test
    void testExistsByName() {
        assertTrue(roleRepository.existsByName("ROLE_ADMIN"));
        assertFalse(roleRepository.existsByName("ROLE_NON_EXISTENT"));
    }
    
    @Test
    void testCountUsersByRoleId() {
        // Note: This test will return 0 since we haven't created users
        // But we're testing the query method exists and returns a value
        Long count = roleRepository.countUsersByRoleId(role1.getId());
        assertNotNull(count);
        assertEquals(0L, count);
    }
    
    @Test
    void testSaveRole() {
        Role newRole = new Role();
        newRole.setName("ROLE_PARENT");
        newRole.setDescription("Parent Role");
        newRole.setSystemRole(false);
        newRole.setIsActive(1);
        newRole.setCreatedTime(LocalDateTime.now());
        
        Role saved = roleRepository.save(newRole);
        
        assertNotNull(saved.getId());
        assertEquals("ROLE_PARENT", saved.getName());
    }
    
    @Test
    void testUpdateRole() {
        role2.setDescription("Updated Teacher Role Description");
        role2.setModifiedTime(LocalDateTime.now());
        
        Role updated = roleRepository.save(role2);
        
        assertEquals("Updated Teacher Role Description", updated.getDescription());
    }
    
    @Test
    void testDeleteRole() {
        Long roleId = role2.getId();
        roleRepository.delete(role2);
        
        assertThat(roleRepository.findById(roleId)).isEmpty();
    }
    
    @Test
    void testFindAll() {
        List<Role> allRoles = roleRepository.findAll();
        assertThat(allRoles).hasSize(3);
    }
    
    @Test
    void testSystemRoleDistribution() {
        List<Role> allRoles = roleRepository.findAll();
        
        long systemCount = allRoles.stream().filter(Role::getSystemRole).count();
        long customCount = allRoles.stream().filter(r -> !r.getSystemRole()).count();
        
        assertEquals(2L, systemCount);
        assertEquals(1L, customCount);
    }
}
