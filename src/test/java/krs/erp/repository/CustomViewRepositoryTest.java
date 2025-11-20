package krs.erp.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;

/**
 * Unit tests for CustomViewRepository using MySQL test database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomViewRepositoryTest {
    
    @Autowired
    private CustomViewRepository customViewRepository;
    
    private CustomView testView1;
    private CustomView testView2;
    private CustomView testView3;
    
    @BeforeEach
    void setUp() {
        customViewRepository.deleteAllInBatch();
        
        // Create test view 1 - Public Student view
        testView1 = new CustomView();
        testView1.setViewName("Student Basic View");
        testView1.setDescription("Basic student information");
        testView1.setEntityType(EntityType.STUDENT);
        testView1.setSelectedFields(Arrays.asList("firstName", "lastName", "email", "gradeLevel"));
        testView1.setIsPublic(true);
        testView1.setIsDefault(false);
        testView1.setCreatedBy("user1");
        testView1.setIsActive(1);
        testView1 = customViewRepository.save(testView1);
        
        // Create test view 2 - Private Student view
        testView2 = new CustomView();
        testView2.setViewName("Student Detailed View");
        testView2.setDescription("Detailed student information");
        testView2.setEntityType(EntityType.STUDENT);
        testView2.setSelectedFields(Arrays.asList("firstName", "lastName", "email", "phone", "address"));
        testView2.setIsPublic(false);
        testView2.setIsDefault(true);
        testView2.setCreatedBy("user2");
        testView2.setIsActive(1);
        testView2 = customViewRepository.save(testView2);
        
        // Create test view 3 - Parent view
        testView3 = new CustomView();
        testView3.setViewName("Parent Basic View");
        testView3.setDescription("Basic parent information");
        testView3.setEntityType(EntityType.PARENT);
        testView3.setSelectedFields(Arrays.asList("firstName", "lastName", "email"));
        testView3.setIsPublic(true);
        testView3.setIsDefault(false);
        testView3.setCreatedBy("user1");
        testView3.setIsActive(1);
        testView3 = customViewRepository.save(testView3);
    }
    
    @Test
    void testSaveCustomView() {
        CustomView newView = new CustomView();
        newView.setViewName("New Test View");
        newView.setEntityType(EntityType.STAFF);
        newView.setSelectedFields(Arrays.asList("firstName", "lastName"));
        newView.setIsActive(1);
        
        CustomView saved = customViewRepository.save(newView);
        
        assertNotNull(saved.getId());
        assertEquals("New Test View", saved.getViewName());
    }
    
    @Test
    void testFindByEntityType() {
        List<CustomView> found = customViewRepository.findByEntityType(EntityType.STUDENT);
        
        assertThat(found).hasSize(2);
    }
    
    @Test
    void testFindByEntityTypeOrderByViewNameAsc() {
        List<CustomView> found = customViewRepository.findByEntityTypeOrderByViewNameAsc(EntityType.STUDENT);
        
        assertThat(found).hasSize(2);
        assertEquals("Student Basic View", found.get(0).getViewName());
    }
    
    @Test
    void testFindByEntityTypeAndIsPublicTrue() {
        List<CustomView> found = customViewRepository.findByEntityTypeAndIsPublicTrue(EntityType.STUDENT);
        
        assertThat(found).hasSize(1);
        assertTrue(found.get(0).getIsPublic());
    }
    
    @Test
    void testFindByEntityTypeAndIsDefaultTrue() {
        Optional<CustomView> found = customViewRepository.findByEntityTypeAndIsDefaultTrue(EntityType.STUDENT);
        
        assertTrue(found.isPresent());
        assertEquals("Student Detailed View", found.get().getViewName());
    }
    
    @Test
    void testFindByViewNameAndEntityType() {
        Optional<CustomView> found = customViewRepository.findByViewNameAndEntityType(
            "Student Basic View", EntityType.STUDENT);
        
        assertTrue(found.isPresent());
        assertEquals(EntityType.STUDENT, found.get().getEntityType());
    }
    
    @Test
    void testExistsByViewNameAndEntityType() {
        assertTrue(customViewRepository.existsByViewNameAndEntityType("Student Basic View", EntityType.STUDENT));
        assertFalse(customViewRepository.existsByViewNameAndEntityType("Nonexistent View", EntityType.STUDENT));
    }
    
    @Test
    void testFindAccessibleViews() {
        List<CustomView> found = customViewRepository.findAccessibleViews(EntityType.STUDENT, 1L);
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(1);
    }
    
    @Test
    void testCountByEntityType() {
        long count = customViewRepository.countByEntityType(EntityType.STUDENT);
        
        assertEquals(2L, count);
    }
    
    @Test
    void testFindRecentViews() {
        List<CustomView> found = customViewRepository.findRecentViews(EntityType.STUDENT, PageRequest.of(0, 10));
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(1);
    }
    
    @Test
    void testFindByIsDefaultTrue() {
        List<CustomView> found = customViewRepository.findByIsDefaultTrue();
        
        assertThat(found).hasSizeGreaterThanOrEqualTo(1);
        assertThat(found).allMatch(CustomView::getIsDefault);
    }
    
    @Test
    void testUpdateCustomView() {
        CustomView view = customViewRepository.findByViewNameAndEntityType(
            "Student Basic View", EntityType.STUDENT).orElseThrow();
        view.setDescription("Updated description");
        
        CustomView updated = customViewRepository.save(view);
        
        assertEquals("Updated description", updated.getDescription());
    }
    
    @Test
    void testDeleteCustomView() {
        Long initialCount = customViewRepository.count();
        
        customViewRepository.delete(testView1);
        
        Long afterDeleteCount = customViewRepository.count();
        assertEquals(initialCount - 1, afterDeleteCount);
    }
    
    @Test
    void testFindAll() {
        List<CustomView> allViews = customViewRepository.findAll();
        
        assertThat(allViews).hasSizeGreaterThanOrEqualTo(3);
    }
}
