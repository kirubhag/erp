package krs.erp.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;
import krs.erp.repository.CustomViewRepository;

@ExtendWith(MockitoExtension.class)
class CustomViewServiceTest {

    @Mock
    private CustomViewRepository customViewRepository;

    @InjectMocks
    private CustomViewService customViewService;

    private CustomView customView;
    private List<String> selectedFields;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        selectedFields = Arrays.asList("name", "email", "phone");
        
        customView = new CustomView();
        customView.setId(1L);
        customView.setViewName("Test View");
        customView.setDescription("Test Description");
        customView.setEntityType(EntityType.STUDENT);
        customView.setSelectedFields(selectedFields);
        customView.setIsDefault(false);
        customView.setIsPublic(true);
        customView.setCreatedBy("1");
        customView.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetViewsByEntityType() {
        List<CustomView> expectedViews = Arrays.asList(customView);
        when(customViewRepository.findByEntityTypeOrderByViewNameAsc(EntityType.STUDENT))
            .thenReturn(expectedViews);

        List<CustomView> result = customViewService.getViewsByEntityType(EntityType.STUDENT);

        assertEquals(expectedViews, result);
        verify(customViewRepository).findByEntityTypeOrderByViewNameAsc(EntityType.STUDENT);
    }

    @Test
    void testGetPublicViews() {
        List<CustomView> expectedViews = Arrays.asList(customView);
        when(customViewRepository.findByEntityTypeAndIsPublicTrue(EntityType.STUDENT))
            .thenReturn(expectedViews);

        List<CustomView> result = customViewService.getPublicViews(EntityType.STUDENT);

        assertEquals(expectedViews, result);
        verify(customViewRepository).findByEntityTypeAndIsPublicTrue(EntityType.STUDENT);
    }

    @Test
    void testGetAccessibleViews() {
        List<CustomView> expectedViews = Arrays.asList(customView);
        when(customViewRepository.findAccessibleViews(EntityType.STUDENT, userId))
            .thenReturn(expectedViews);

        List<CustomView> result = customViewService.getAccessibleViews(EntityType.STUDENT, userId);

        assertEquals(expectedViews, result);
        verify(customViewRepository).findAccessibleViews(EntityType.STUDENT, userId);
    }

    @Test
    void testGetUserViews() {
        List<CustomView> expectedViews = Arrays.asList(customView);
        when(customViewRepository.findByEntityTypeAndCreatedBy(EntityType.STUDENT, userId))
            .thenReturn(expectedViews);

        List<CustomView> result = customViewService.getUserViews(EntityType.STUDENT, userId);

        assertEquals(expectedViews, result);
        verify(customViewRepository).findByEntityTypeAndCreatedBy(EntityType.STUDENT, userId);
    }

    @Test
    void testGetDefaultView() {
        when(customViewRepository.findByEntityTypeAndIsDefaultTrue(EntityType.STUDENT))
            .thenReturn(Optional.of(customView));

        Optional<CustomView> result = customViewService.getDefaultView(EntityType.STUDENT);

        assertTrue(result.isPresent());
        assertEquals(customView, result.get());
        verify(customViewRepository).findByEntityTypeAndIsDefaultTrue(EntityType.STUDENT);
    }

    @Test
    void testGetDefaultViewNotFound() {
        when(customViewRepository.findByEntityTypeAndIsDefaultTrue(EntityType.STUDENT))
            .thenReturn(Optional.empty());

        Optional<CustomView> result = customViewService.getDefaultView(EntityType.STUDENT);

        assertFalse(result.isPresent());
        verify(customViewRepository).findByEntityTypeAndIsDefaultTrue(EntityType.STUDENT);
    }

    @Test
    void testGetViewByName() {
        when(customViewRepository.findByViewNameAndEntityType("Test View", EntityType.STUDENT))
            .thenReturn(Optional.of(customView));

        Optional<CustomView> result = customViewService.getViewByName("Test View", EntityType.STUDENT);

        assertTrue(result.isPresent());
        assertEquals(customView, result.get());
        verify(customViewRepository).findByViewNameAndEntityType("Test View", EntityType.STUDENT);
    }

    @Test
    void testGetViewById() {
        when(customViewRepository.findById(1L)).thenReturn(Optional.of(customView));

        Optional<CustomView> result = customViewService.getViewById(1L);

        assertTrue(result.isPresent());
        assertEquals(customView, result.get());
        verify(customViewRepository).findById(1L);
    }

    @Test
    void testCreateView() {
        when(customViewRepository.save(any(CustomView.class))).thenReturn(customView);

        CustomView newView = new CustomView();
        newView.setViewName("New View");
        newView.setEntityType(EntityType.STUDENT);
        newView.setIsDefault(false);

        CustomView result = customViewService.createView(newView, userId);

        assertNotNull(result);
        assertEquals(userId.toString(), newView.getCreatedBy());
        assertEquals(userId.toString(), newView.getUpdatedBy());
        assertNotNull(newView.getCreatedAt());
        assertNotNull(newView.getUpdatedAt());
        verify(customViewRepository).save(newView);
    }

    @Test
    void testCreateViewAsDefault() {
        CustomView existingDefault = new CustomView();
        existingDefault.setIsDefault(true);
        existingDefault.setEntityType(EntityType.STUDENT);
        
        when(customViewRepository.findByEntityTypeAndIsDefaultTrue(EntityType.STUDENT))
            .thenReturn(Optional.of(existingDefault));
        when(customViewRepository.save(any(CustomView.class))).thenReturn(customView);

        CustomView newView = new CustomView();
        newView.setViewName("New Default View");
        newView.setEntityType(EntityType.STUDENT);
        newView.setIsDefault(true);

        CustomView result = customViewService.createView(newView, userId);

        assertNotNull(result);
        verify(customViewRepository).save(existingDefault); // Clear existing default
        verify(customViewRepository).save(newView); // Save new view
        assertFalse(existingDefault.getIsDefault()); // Existing default should be cleared
    }

    @Test
    void testUpdateView() {
        when(customViewRepository.findById(1L)).thenReturn(Optional.of(customView));
        when(customViewRepository.save(any(CustomView.class))).thenReturn(customView);

        CustomView updatedView = new CustomView();
        updatedView.setViewName("Updated View");
        updatedView.setDescription("Updated Description");
        updatedView.setSelectedFields(Arrays.asList("newField1", "newField2"));
        updatedView.setIsDefault(false);
        updatedView.setIsPublic(false);

        CustomView result = customViewService.updateView(1L, updatedView, userId);

        assertNotNull(result);
        assertEquals("Updated View", customView.getViewName());
        assertEquals("Updated Description", customView.getDescription());
        assertEquals(userId.toString(), customView.getUpdatedBy());
        assertNotNull(customView.getUpdatedAt());
        verify(customViewRepository).findById(1L);
        verify(customViewRepository).save(customView);
    }

    @Test
    void testUpdateViewNotFound() {
        when(customViewRepository.findById(999L)).thenReturn(Optional.empty());

        CustomView updatedView = new CustomView();
        updatedView.setViewName("Updated View");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customViewService.updateView(999L, updatedView, userId);
        });

        assertEquals("Custom view not found with id: 999", exception.getMessage());
        verify(customViewRepository).findById(999L);
        verify(customViewRepository, never()).save(any());
    }

    @Test
    void testDeleteView() {
        when(customViewRepository.findById(1L)).thenReturn(Optional.of(customView));

        assertDoesNotThrow(() -> {
            customViewService.deleteView(1L, userId);
        });

        verify(customViewRepository).findById(1L);
        verify(customViewRepository).deleteById(1L);
    }

    @Test
    void testDeleteViewNotFound() {
        when(customViewRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customViewService.deleteView(999L, userId);
        });

        assertEquals("Custom view not found with id: 999", exception.getMessage());
        verify(customViewRepository).findById(999L);
        verify(customViewRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteViewUnauthorized() {
        customView.setCreatedBy("2"); // Different user
        when(customViewRepository.findById(1L)).thenReturn(Optional.of(customView));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customViewService.deleteView(1L, userId);
        });

        assertEquals("User not authorized to delete this view", exception.getMessage());
        verify(customViewRepository).findById(1L);
        verify(customViewRepository, never()).deleteById(any());
    }

    @Test
    void testViewNameExists() {
        when(customViewRepository.existsByViewNameAndEntityType("Test View", EntityType.STUDENT))
            .thenReturn(true);

        boolean result = customViewService.viewNameExists("Test View", EntityType.STUDENT);

        assertTrue(result);
        verify(customViewRepository).existsByViewNameAndEntityType("Test View", EntityType.STUDENT);
    }

    @Test
    void testViewNameExistsWithExclusion() {
        when(customViewRepository.findByViewNameAndEntityType("Test View", EntityType.STUDENT))
            .thenReturn(Optional.of(customView));

        boolean result = customViewService.viewNameExists("Test View", EntityType.STUDENT, 2L);

        assertTrue(result); // Should return true since the found view has ID 1, not 2
        verify(customViewRepository).findByViewNameAndEntityType("Test View", EntityType.STUDENT);
    }

    @Test
    void testGetRecentViews() {
        List<CustomView> expectedViews = Arrays.asList(customView);
        when(customViewRepository.findRecentViews(eq(EntityType.STUDENT), any(PageRequest.class)))
            .thenReturn(expectedViews);

        List<CustomView> result = customViewService.getRecentViews(EntityType.STUDENT, 5);

        assertEquals(expectedViews, result);
        verify(customViewRepository).findRecentViews(eq(EntityType.STUDENT), any(PageRequest.class));
    }

    @Test
    void testGetViewCount() {
        when(customViewRepository.countByEntityType(EntityType.STUDENT)).thenReturn(5L);

        long result = customViewService.getViewCount(EntityType.STUDENT);

        assertEquals(5L, result);
        verify(customViewRepository).countByEntityType(EntityType.STUDENT);
    }

    @Test
    void testCloneView() {
        when(customViewRepository.findById(1L)).thenReturn(Optional.of(customView));
        when(customViewRepository.existsByViewNameAndEntityType("Cloned View", EntityType.STUDENT))
            .thenReturn(false);
        when(customViewRepository.save(any(CustomView.class))).thenReturn(customView);

        CustomView result = customViewService.cloneView(1L, "Cloned View", userId);

        assertNotNull(result);
        verify(customViewRepository).findById(1L);
        verify(customViewRepository).existsByViewNameAndEntityType("Cloned View", EntityType.STUDENT);
        verify(customViewRepository).save(any(CustomView.class));
    }

    @Test
    void testCloneViewNotFound() {
        when(customViewRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customViewService.cloneView(999L, "Cloned View", userId);
        });

        assertEquals("Custom view not found with id: 999", exception.getMessage());
        verify(customViewRepository).findById(999L);
    }

    @Test
    void testCloneViewNameAlreadyExists() {
        when(customViewRepository.findById(1L)).thenReturn(Optional.of(customView));
        when(customViewRepository.existsByViewNameAndEntityType("Existing View", EntityType.STUDENT))
            .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customViewService.cloneView(1L, "Existing View", userId);
        });

        assertEquals("View name already exists: Existing View", exception.getMessage());
        verify(customViewRepository).findById(1L);
        verify(customViewRepository).existsByViewNameAndEntityType("Existing View", EntityType.STUDENT);
    }
}