package krs.erp.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.dto.CustomViewDTO;
import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;
import krs.erp.service.CustomViewService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomViewController.class)
class CustomViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomViewService customViewService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomView customView;
    private CustomViewDTO customViewDTO;
    private List<String> selectedFields;

    @BeforeEach
    void setUp() {
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
        customView.setCreatedTime(LocalDateTime.now());
        customView.setModifiedBy("1");
        customView.setModifiedTime(LocalDateTime.now());

        customViewDTO = new CustomViewDTO();
        customViewDTO.setId(1L);
        customViewDTO.setViewName("Test View");
        customViewDTO.setDescription("Test Description");
        customViewDTO.setEntityType(EntityType.STUDENT);
        customViewDTO.setSelectedFields(selectedFields);
        customViewDTO.setIsDefault(false);
        customViewDTO.setIsPublic(true);
        customViewDTO.setCreatedBy("1");
        customViewDTO.setCreatedTime(LocalDateTime.now());
    }

    @Test
    void testGetViewsByEntityType() throws Exception {
        List<CustomView> views = Arrays.asList(customView);
        when(customViewService.getAccessibleViews(EntityType.STUDENT, 1L)).thenReturn(views);

        mockMvc.perform(get("/api/custom-views")
                .param("entityType", "STUDENT")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].viewName").value("Test View"))
                .andExpect(jsonPath("$[0].entityType").value("STUDENT"));

        verify(customViewService).getAccessibleViews(EntityType.STUDENT, 1L);
    }

    @Test
    void testGetViewsByEntityTypePublicOnly() throws Exception {
        List<CustomView> views = Arrays.asList(customView);
        when(customViewService.getPublicViews(EntityType.STUDENT)).thenReturn(views);

        mockMvc.perform(get("/api/custom-views")
                .param("entityType", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].viewName").value("Test View"));

        verify(customViewService).getPublicViews(EntityType.STUDENT);
    }

    @Test
    void testGetViewsByEntityTypeInvalidEntityType() throws Exception {
        mockMvc.perform(get("/api/custom-views")
                .param("entityType", "INVALID_TYPE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetViewById() throws Exception {
        when(customViewService.getViewById(1L)).thenReturn(Optional.of(customView));

        mockMvc.perform(get("/api/custom-views/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewName").value("Test View"))
                .andExpect(jsonPath("$.entityType").value("STUDENT"));

        verify(customViewService).getViewById(1L);
    }

    @Test
    void testGetViewByIdNotFound() throws Exception {
        when(customViewService.getViewById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/custom-views/999"))
                .andExpect(status().isNotFound());

        verify(customViewService).getViewById(999L);
    }

    @Test
    void testGetDefaultView() throws Exception {
        when(customViewService.getDefaultView(EntityType.STUDENT)).thenReturn(Optional.of(customView));

        mockMvc.perform(get("/api/custom-views/default")
                .param("entityType", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewName").value("Test View"));

        verify(customViewService).getDefaultView(EntityType.STUDENT);
    }

    @Test
    void testGetDefaultViewNotFound() throws Exception {
        when(customViewService.getDefaultView(EntityType.STUDENT)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/custom-views/default")
                .param("entityType", "STUDENT"))
                .andExpect(status().isNotFound());

        verify(customViewService).getDefaultView(EntityType.STUDENT);
    }

    @Test
    void testGetUserViews() throws Exception {
        List<CustomView> views = Arrays.asList(customView);
        when(customViewService.getUserViews(EntityType.STUDENT, 1L)).thenReturn(views);

        mockMvc.perform(get("/api/custom-views/user/1")
                .param("entityType", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].viewName").value("Test View"));

        verify(customViewService).getUserViews(EntityType.STUDENT, 1L);
    }

    @Test
    void testCreateView() throws Exception {
        when(customViewService.viewNameExists("New View", EntityType.STUDENT)).thenReturn(false);
        when(customViewService.createView(any(CustomView.class), eq(1L))).thenReturn(customView);

        CustomViewDTO newViewDTO = new CustomViewDTO();
        newViewDTO.setViewName("New View");
        newViewDTO.setDescription("New Description");
        newViewDTO.setEntityType(EntityType.STUDENT);
        newViewDTO.setSelectedFields(selectedFields);
        newViewDTO.setIsDefault(false);
        newViewDTO.setIsPublic(true);

        mockMvc.perform(post("/api/custom-views")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newViewDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.viewName").value("Test View"));

        verify(customViewService).viewNameExists("New View", EntityType.STUDENT);
        verify(customViewService).createView(any(CustomView.class), eq(1L));
    }

    @Test
    void testCreateViewNameConflict() throws Exception {
        when(customViewService.viewNameExists("Existing View", EntityType.STUDENT)).thenReturn(true);

        CustomViewDTO newViewDTO = new CustomViewDTO();
        newViewDTO.setViewName("Existing View");
        newViewDTO.setEntityType(EntityType.STUDENT);

        mockMvc.perform(post("/api/custom-views")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newViewDTO)))
                .andExpect(status().isConflict());

        verify(customViewService).viewNameExists("Existing View", EntityType.STUDENT);
        verify(customViewService, never()).createView(any(), any());
    }

    @Test
    void testUpdateView() throws Exception {
        when(customViewService.viewNameExists("Updated View", EntityType.STUDENT, 1L)).thenReturn(false);
        when(customViewService.updateView(eq(1L), any(CustomView.class), eq(1L))).thenReturn(customView);

        CustomViewDTO updatedViewDTO = new CustomViewDTO();
        updatedViewDTO.setViewName("Updated View");
        updatedViewDTO.setDescription("Updated Description");
        updatedViewDTO.setEntityType(EntityType.STUDENT);
        updatedViewDTO.setSelectedFields(selectedFields);

        mockMvc.perform(put("/api/custom-views/1")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedViewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewName").value("Test View"));

        verify(customViewService).viewNameExists("Updated View", EntityType.STUDENT, 1L);
        verify(customViewService).updateView(eq(1L), any(CustomView.class), eq(1L));
    }

    @Test
    void testUpdateViewNotFound() throws Exception {
        when(customViewService.viewNameExists("Updated View", EntityType.STUDENT, 999L)).thenReturn(false);
        when(customViewService.updateView(eq(999L), any(CustomView.class), eq(1L)))
            .thenThrow(new RuntimeException("Custom view not found"));

        CustomViewDTO updatedViewDTO = new CustomViewDTO();
        updatedViewDTO.setViewName("Updated View");
        updatedViewDTO.setEntityType(EntityType.STUDENT);

        mockMvc.perform(put("/api/custom-views/999")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedViewDTO)))
                .andExpect(status().isNotFound());

        verify(customViewService).updateView(eq(999L), any(CustomView.class), eq(1L));
    }

    @Test
    void testDeleteView() throws Exception {
        doNothing().when(customViewService).deleteView(1L, 1L);

        mockMvc.perform(delete("/api/custom-views/1")
                .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(customViewService).deleteView(1L, 1L);
    }

    @Test
    void testDeleteViewNotFound() throws Exception {
        doThrow(new RuntimeException("Custom view not found")).when(customViewService).deleteView(999L, 1L);

        mockMvc.perform(delete("/api/custom-views/999")
                .param("userId", "1"))
                .andExpect(status().isNotFound());

        verify(customViewService).deleteView(999L, 1L);
    }

    @Test
    void testCloneView() throws Exception {
        when(customViewService.cloneView(1L, "Cloned View", 1L)).thenReturn(customView);

        mockMvc.perform(post("/api/custom-views/1/clone")
                .param("newViewName", "Cloned View")
                .param("userId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.viewName").value("Test View"));

        verify(customViewService).cloneView(1L, "Cloned View", 1L);
    }

    @Test
    void testCloneViewError() throws Exception {
        when(customViewService.cloneView(1L, "Existing Name", 1L))
            .thenThrow(new RuntimeException("View name already exists"));

        mockMvc.perform(post("/api/custom-views/1/clone")
                .param("newViewName", "Existing Name")
                .param("userId", "1"))
                .andExpect(status().isBadRequest());

        verify(customViewService).cloneView(1L, "Existing Name", 1L);
    }

    @Test
    void testGetRecentViews() throws Exception {
        List<CustomView> views = Arrays.asList(customView);
        when(customViewService.getRecentViews(EntityType.STUDENT, 5)).thenReturn(views);

        mockMvc.perform(get("/api/custom-views/recent")
                .param("entityType", "STUDENT")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].viewName").value("Test View"));

        verify(customViewService).getRecentViews(EntityType.STUDENT, 5);
    }

    @Test
    void testGetRecentViewsDefaultLimit() throws Exception {
        List<CustomView> views = Arrays.asList(customView);
        when(customViewService.getRecentViews(EntityType.STUDENT, 5)).thenReturn(views);

        mockMvc.perform(get("/api/custom-views/recent")
                .param("entityType", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(customViewService).getRecentViews(EntityType.STUDENT, 5);
    }

    @Test
    void testGetViewCount() throws Exception {
        when(customViewService.getViewCount(EntityType.STUDENT)).thenReturn(10L);

        mockMvc.perform(get("/api/custom-views/count")
                .param("entityType", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        verify(customViewService).getViewCount(EntityType.STUDENT);
    }

    @Test
    void testGetEntityTypes() throws Exception {
        mockMvc.perform(get("/api/custom-views/entity-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(EntityType.values().length));
    }
}