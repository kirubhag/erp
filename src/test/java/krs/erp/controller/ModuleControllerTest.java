package krs.erp.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.dto.SequenceUpdateDTO;
import krs.erp.model.ErpEntity;
import krs.erp.service.ErpEntityService;

/**
 * Unit tests for ModuleController
 */
@WebMvcTest(ModuleController.class)
@ActiveProfiles("test")
class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ErpEntityService erpEntityService;

    @Autowired
    private ObjectMapper objectMapper;

    private ErpEntity dashboardEntity;
    private ErpEntity studentsEntity;
    private ErpEntity staffEntity;

    @BeforeEach
    void setUp() {
        dashboardEntity = createTestEntity(1L, "Dashboard", "Dashboard", 1);
        studentsEntity = createTestEntity(2L, "Student", "Students", 2);
        staffEntity = createTestEntity(3L, "Staff", "Staff", 3);
    }

    @Test
    @WithMockUser
    void testGetMenuItems() throws Exception {
        // Given
        List<ErpEntity> menuItems = Arrays.asList(dashboardEntity, studentsEntity, staffEntity);
        when(erpEntityService.getActiveMenuItems()).thenReturn(menuItems);

        // When & Then
        mockMvc.perform(get("/api/module/menu-items")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].singularName").value("Dashboard"))
                .andExpect(jsonPath("$[1].singularName").value("Student"))
                .andExpect(jsonPath("$[2].singularName").value("Staff"));

        verify(erpEntityService, times(1)).getActiveMenuItems();
    }

    @Test
    @WithMockUser
    void testUpdateSequence_Success() throws Exception {
        // Given
        List<SequenceUpdateDTO> updates = Arrays.asList(
            new SequenceUpdateDTO(1L, 1), // Dashboard at position 1
            new SequenceUpdateDTO(2L, 2), // Students at position 2
            new SequenceUpdateDTO(3L, 3)  // Staff at position 3
        );

        when(erpEntityService.getEntityById(1L)).thenReturn(Optional.of(dashboardEntity));
        when(erpEntityService.getEntityById(2L)).thenReturn(Optional.of(studentsEntity));
        when(erpEntityService.getEntityById(3L)).thenReturn(Optional.of(staffEntity));

        when(erpEntityService.updateEntity(eq(1L), any(ErpEntity.class))).thenReturn(dashboardEntity);
        when(erpEntityService.updateEntity(eq(2L), any(ErpEntity.class))).thenReturn(studentsEntity);
        when(erpEntityService.updateEntity(eq(3L), any(ErpEntity.class))).thenReturn(staffEntity);

        // When & Then
        mockMvc.perform(put("/api/module/update-sequence")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Successfully updated 3 menu items"))
                .andExpect(jsonPath("$.count").value(3));

        verify(erpEntityService, times(6)).getEntityById(any(Long.class)); // 2 calls per update (validation + update)
        verify(erpEntityService, times(3)).updateEntity(any(Long.class), any(ErpEntity.class));
    }

    @Test
    @WithMockUser
    void testUpdateSequence_DashboardNotAtSequence1_ReturnsBadRequest() throws Exception {
        // Given - Dashboard at position 2 (invalid)
        List<SequenceUpdateDTO> updates = Arrays.asList(
            new SequenceUpdateDTO(1L, 2), // Dashboard NOT at position 1
            new SequenceUpdateDTO(2L, 1)  // Students at position 1
        );

        when(erpEntityService.getEntityById(1L)).thenReturn(Optional.of(dashboardEntity));

        // When & Then
        mockMvc.perform(put("/api/module/update-sequence")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Dashboard must always be at sequence 1"));

        verify(erpEntityService, times(1)).getEntityById(1L);
        verify(erpEntityService, times(0)).updateEntity(any(Long.class), any(ErpEntity.class));
    }

    @Test
    @WithMockUser
    void testUpdateSequence_EntityNotFound_ReturnsError() throws Exception {
        // Given
        List<SequenceUpdateDTO> updates = Arrays.asList(
            new SequenceUpdateDTO(999L, 1) // Non-existent entity
        );

        when(erpEntityService.getEntityById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/module/update-sequence")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));

        verify(erpEntityService, times(1)).getEntityById(999L);
        verify(erpEntityService, times(0)).updateEntity(any(Long.class), any(ErpEntity.class));
    }

    @Test
    @WithMockUser
    void testRenameModule_Success() throws Exception {
        // Given
        Map<String, Object> renameData = new HashMap<>();
        renameData.put("id", 2L);
        renameData.put("pluralName", "Pupils");
        renameData.put("singularName", "Pupil");

        ErpEntity updatedEntity = createTestEntity(2L, "Pupil", "Pupils", 2);

        when(erpEntityService.getEntityById(2L)).thenReturn(Optional.of(studentsEntity));
        when(erpEntityService.updateEntity(eq(2L), any(ErpEntity.class))).thenReturn(updatedEntity);

        // When & Then
        mockMvc.perform(put("/api/module/rename")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(renameData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Module renamed successfully"))
                .andExpect(jsonPath("$.data").exists());

        verify(erpEntityService, times(1)).getEntityById(2L);
        verify(erpEntityService, times(1)).updateEntity(eq(2L), any(ErpEntity.class));
    }

    @Test
    @WithMockUser
    void testRenameModule_EmptyPluralName_ReturnsBadRequest() throws Exception {
        // Given
        Map<String, Object> renameData = new HashMap<>();
        renameData.put("id", 2L);
        renameData.put("pluralName", "");
        renameData.put("singularName", "Pupil");

        // When & Then
        mockMvc.perform(put("/api/module/rename")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(renameData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Plural name is required"));

        verify(erpEntityService, times(0)).getEntityById(any(Long.class));
        verify(erpEntityService, times(0)).updateEntity(any(Long.class), any(ErpEntity.class));
    }

    @Test
    @WithMockUser
    void testRenameModule_EmptySingularName_ReturnsBadRequest() throws Exception {
        // Given
        Map<String, Object> renameData = new HashMap<>();
        renameData.put("id", 2L);
        renameData.put("pluralName", "Pupils");
        renameData.put("singularName", "");

        // When & Then
        mockMvc.perform(put("/api/module/rename")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(renameData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Singular name is required"));

        verify(erpEntityService, times(0)).getEntityById(any(Long.class));
        verify(erpEntityService, times(0)).updateEntity(any(Long.class), any(ErpEntity.class));
    }

    @Test
    @WithMockUser
    void testRenameModule_EntityNotFound_ReturnsError() throws Exception {
        // Given
        Map<String, Object> renameData = new HashMap<>();
        renameData.put("id", 999L);
        renameData.put("pluralName", "Test");
        renameData.put("singularName", "Test");

        when(erpEntityService.getEntityById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/module/rename")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(renameData)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));

        verify(erpEntityService, times(1)).getEntityById(999L);
        verify(erpEntityService, times(0)).updateEntity(any(Long.class), any(ErpEntity.class));
    }

    // Helper method to create test entities
    private ErpEntity createTestEntity(Long id, String singularName, String pluralName, Integer sequence) {
        ErpEntity entity = new ErpEntity();
        entity.setId(id);
        entity.setSingularName(singularName);
        entity.setPluralName(pluralName);
        entity.setSequence(sequence);
        entity.setIsActive(true);
        entity.setIcon("fas fa-cube");
        entity.setRoute("#!/" + singularName.toLowerCase());
        return entity;
    }
}
