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

import krs.erp.model.Permission;

/**
 * Unit tests for PermissionRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PermissionRepositoryTest {
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    private Permission permission1;
    private Permission permission2;
    private Permission permission3;
    
    @BeforeEach
    void setUp() {
        permissionRepository.deleteAllInBatch();
        
        // Create permission 1 - Student Read (System Permission)
        permission1 = new Permission();
        permission1.setName("STUDENT_READ");
        permission1.setDescription("Permission to read student records");
        permission1.setResource("STUDENT");
        permission1.setAction("READ");
        permission1.setSystemPermission(true);
        permission1.setIsActive(1);
        permission1.setCreatedTime(LocalDateTime.now());
        permission1 = permissionRepository.save(permission1);
        
        // Create permission 2 - Student Write (System Permission)
        permission2 = new Permission();
        permission2.setName("STUDENT_WRITE");
        permission2.setDescription("Permission to write student records");
        permission2.setResource("STUDENT");
        permission2.setAction("WRITE");
        permission2.setSystemPermission(true);
        permission2.setIsActive(1);
        permission2.setCreatedTime(LocalDateTime.now());
        permission2 = permissionRepository.save(permission2);
        
        // Create permission 3 - Grade Read (Custom Permission)
        permission3 = new Permission();
        permission3.setName("GRADE_READ");
        permission3.setDescription("Permission to read grade records");
        permission3.setResource("GRADE");
        permission3.setAction("READ");
        permission3.setSystemPermission(false);
        permission3.setIsActive(1);
        permission3.setCreatedTime(LocalDateTime.now());
        permission3 = permissionRepository.save(permission3);
    }
    
    @Test
    void testFindByName() {
        Optional<Permission> found = permissionRepository.findByName("STUDENT_READ");
        
        assertThat(found).isPresent();
        assertEquals("Permission to read student records", found.get().getDescription());
    }
    
    @Test
    void testFindByResource() {
        List<Permission> studentPermissions = permissionRepository.findByResource("STUDENT");
        
        assertThat(studentPermissions).hasSize(2);
        assertThat(studentPermissions).allMatch(p -> p.getResource().equals("STUDENT"));
    }
    
    @Test
    void testFindByAction() {
        List<Permission> readPermissions = permissionRepository.findByAction("READ");
        
        assertThat(readPermissions).hasSize(2);
        assertThat(readPermissions).allMatch(p -> p.getAction().equals("READ"));
    }
    
    @Test
    void testFindByResourceAndAction() {
        List<Permission> studentReadPermissions = permissionRepository.findByResourceAndAction("STUDENT", "READ");
        
        assertThat(studentReadPermissions).hasSize(1);
        assertEquals("STUDENT_READ", studentReadPermissions.get(0).getName());
    }
    
    @Test
    void testFindBySystemPermissionTrue() {
        List<Permission> systemPermissions = permissionRepository.findBySystemPermissionTrue();
        
        assertThat(systemPermissions).hasSize(2);
        assertThat(systemPermissions).allMatch(Permission::getSystemPermission);
    }
    
    @Test
    void testFindBySystemPermissionFalse() {
        List<Permission> customPermissions = permissionRepository.findBySystemPermissionFalse();
        
        assertThat(customPermissions).hasSize(1);
        assertEquals("GRADE_READ", customPermissions.get(0).getName());
    }
    
    @Test
    void testFindByNameContaining() {
        List<Permission> studentPerms = permissionRepository.findByNameContaining("student");
        
        assertThat(studentPerms).hasSize(2);
    }
    
    @Test
    void testExistsByName() {
        assertTrue(permissionRepository.existsByName("STUDENT_READ"));
        assertFalse(permissionRepository.existsByName("NON_EXISTENT_PERMISSION"));
    }
    
    @Test
    void testExistsByResourceAndAction() {
        assertTrue(permissionRepository.existsByResourceAndAction("STUDENT", "READ"));
        assertFalse(permissionRepository.existsByResourceAndAction("STUDENT", "DELETE"));
    }
    
    @Test
    void testCountRolesByPermissionId() {
        // Note: This test will return 0 since we haven't created role-permission mappings
        // But we're testing the query method exists and returns a value
        Long count = permissionRepository.countRolesByPermissionId(permission1.getId());
        assertNotNull(count);
        assertEquals(0L, count);
    }
    
    @Test
    void testSavePermission() {
        Permission newPermission = new Permission();
        newPermission.setName("ATTENDANCE_WRITE");
        newPermission.setDescription("Permission to write attendance records");
        newPermission.setResource("ATTENDANCE");
        newPermission.setAction("WRITE");
        newPermission.setSystemPermission(false);
        newPermission.setIsActive(1);
        newPermission.setCreatedTime(LocalDateTime.now());
        
        Permission saved = permissionRepository.save(newPermission);
        
        assertNotNull(saved.getId());
        assertEquals("ATTENDANCE_WRITE", saved.getName());
    }
    
    @Test
    void testUpdatePermission() {
        permission3.setDescription("Updated permission description");
        permission3.setModifiedTime(LocalDateTime.now());
        
        Permission updated = permissionRepository.save(permission3);
        
        assertEquals("Updated permission description", updated.getDescription());
    }
    
    @Test
    void testDeletePermission() {
        Long permissionId = permission3.getId();
        permissionRepository.delete(permission3);
        
        assertThat(permissionRepository.findById(permissionId)).isEmpty();
    }
    
    @Test
    void testFindAll() {
        List<Permission> allPermissions = permissionRepository.findAll();
        assertThat(allPermissions).hasSize(3);
    }
    
    @Test
    void testResourceDistribution() {
        List<Permission> allPermissions = permissionRepository.findAll();
        
        long studentCount = allPermissions.stream()
            .filter(p -> p.getResource().equals("STUDENT"))
            .count();
        
        long gradeCount = allPermissions.stream()
            .filter(p -> p.getResource().equals("GRADE"))
            .count();
        
        assertEquals(2L, studentCount);
        assertEquals(1L, gradeCount);
    }
    
    @Test
    void testActionDistribution() {
        List<Permission> allPermissions = permissionRepository.findAll();
        
        long readCount = allPermissions.stream()
            .filter(p -> p.getAction().equals("READ"))
            .count();
        
        long writeCount = allPermissions.stream()
            .filter(p -> p.getAction().equals("WRITE"))
            .count();
        
        assertEquals(2L, readCount);
        assertEquals(1L, writeCount);
    }
}
