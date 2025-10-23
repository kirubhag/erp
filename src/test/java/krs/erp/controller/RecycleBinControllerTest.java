package krs.erp.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.enums.EntityType;
import krs.erp.model.RecycleBin;
import krs.erp.service.RecycleBinService;

/**
 * Unit tests for RecycleBinController
 */
@WebMvcTest(RecycleBinController.class)
class RecycleBinControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private RecycleBinService recycleBinService;
    
    private RecycleBin testRecycleBin;
    
    @BeforeEach
    void setUp() {
        testRecycleBin = new RecycleBin(1L, "Test Student", EntityType.STUDENT, "admin");
        testRecycleBin.setRecycleBinId(1L);
        testRecycleBin.setDeletionReason("Test deletion");
        testRecycleBin.setDeletedTime(LocalDateTime.now());
        testRecycleBin.setRelatedEntityCount(2);
    }
    
    @Test
    void testGetAllRecycleBinRecords() throws Exception {
        List<RecycleBin> records = Arrays.asList(testRecycleBin);
        when(recycleBinService.getAllRecycleBinRecords()).thenReturn(records);
        
        mockMvc.perform(get("/api/recycle-bin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entityName", is("Test Student")))
                .andExpect(jsonPath("$[0].entityType", is("STUDENT")));
        
        verify(recycleBinService, times(1)).getAllRecycleBinRecords();
    }
    
    @Test
    void testGetAllRecycleBinRecordsWithPagination() throws Exception {
        Page<RecycleBin> page = new PageImpl<>(Arrays.asList(testRecycleBin));
        when(recycleBinService.getAllRecycleBinRecords(any(PageRequest.class))).thenReturn(page);
        
        mockMvc.perform(get("/api/recycle-bin")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].entityName", is("Test Student")));
        
        verify(recycleBinService, times(1)).getAllRecycleBinRecords(any(PageRequest.class));
    }
    
    @Test
    void testGetRecycleBinRecordsByType() throws Exception {
        List<RecycleBin> records = Arrays.asList(testRecycleBin);
        when(recycleBinService.getRecycleBinRecordsByType(eq(EntityType.STUDENT))).thenReturn(records);
        
        mockMvc.perform(get("/api/recycle-bin/type/STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entityType", is("STUDENT")));
        
        verify(recycleBinService, times(1)).getRecycleBinRecordsByType(eq(EntityType.STUDENT));
    }
    
    @Test
    void testGetRecycleBinRecordById() throws Exception {
        when(recycleBinService.getRecycleBinRecord(1L)).thenReturn(Optional.of(testRecycleBin));
        
        mockMvc.perform(get("/api/recycle-bin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entityName", is("Test Student")))
                .andExpect(jsonPath("$.recycleBinId", is(1)));
        
        verify(recycleBinService, times(1)).getRecycleBinRecord(1L);
    }
    
    @Test
    void testGetRecycleBinRecordById_NotFound() throws Exception {
        when(recycleBinService.getRecycleBinRecord(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/recycle-bin/999"))
                .andExpect(status().isNotFound());
        
        verify(recycleBinService, times(1)).getRecycleBinRecord(999L);
    }
    
    @Test
    void testSearchRecycleBinRecords() throws Exception {
        List<RecycleBin> records = Arrays.asList(testRecycleBin);
        when(recycleBinService.searchRecycleBinRecords("Test")).thenReturn(records);
        
        mockMvc.perform(get("/api/recycle-bin/search")
                .param("searchTerm", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entityName", is("Test Student")));
        
        verify(recycleBinService, times(1)).searchRecycleBinRecords("Test");
    }
    
    @Test
    void testGetRecycleBinStatistics() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", 10L);
        stats.put("recordsByType", Map.of(EntityType.STUDENT, 5L, EntityType.PARENT, 5L));
        
        when(recycleBinService.getRecycleBinStatistics()).thenReturn(stats);
        
        mockMvc.perform(get("/api/recycle-bin/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords", is(10)));
        
        verify(recycleBinService, times(1)).getRecycleBinStatistics();
    }
    
    @Test
    void testGetRecycleBinCount() throws Exception {
        when(recycleBinService.getRecycleBinCount()).thenReturn(25L);
        
        mockMvc.perform(get("/api/recycle-bin/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(25)));
        
        verify(recycleBinService, times(1)).getRecycleBinCount();
    }
    
    @Test
    void testRestoreEntity() throws Exception {
        when(recycleBinService.restoreEntity(eq(1L), anyString())).thenReturn(true);
        
        Map<String, String> requestBody = Map.of("restoredBy", "admin");
        
        mockMvc.perform(post("/api/recycle-bin/1/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Entity restored successfully")))
                .andExpect(jsonPath("$.recycleBinId", is(1)));
        
        verify(recycleBinService, times(1)).restoreEntity(eq(1L), eq("admin"));
    }
    
    @Test
    void testRestoreEntity_NotFound() throws Exception {
        when(recycleBinService.restoreEntity(eq(999L), anyString())).thenReturn(false);
        
        Map<String, String> requestBody = Map.of("restoredBy", "admin");
        
        mockMvc.perform(post("/api/recycle-bin/999/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
        
        verify(recycleBinService, times(1)).restoreEntity(eq(999L), eq("admin"));
    }
    
    @Test
    void testRestoreEntityByIdAndType() throws Exception {
        when(recycleBinService.restoreEntityByIdAndType(eq(1L), eq(EntityType.STUDENT), anyString())).thenReturn(true);
        
        Map<String, String> requestBody = Map.of("restoredBy", "admin");
        
        mockMvc.perform(post("/api/recycle-bin/restore/entity/STUDENT/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Entity restored successfully")));
        
        verify(recycleBinService, times(1)).restoreEntityByIdAndType(eq(1L), eq(EntityType.STUDENT), eq("admin"));
    }
    
    @Test
    void testRestoreAllEntitiesByType() throws Exception {
        when(recycleBinService.restoreAllEntitiesByType(eq(EntityType.STUDENT), anyString())).thenReturn(5);
        
        Map<String, String> requestBody = Map.of("restoredBy", "admin");
        
        mockMvc.perform(post("/api/recycle-bin/restore/type/STUDENT")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Entities restored successfully")))
                .andExpect(jsonPath("$.restoredCount", is(5)));
        
        verify(recycleBinService, times(1)).restoreAllEntitiesByType(eq(EntityType.STUDENT), eq("admin"));
    }
    
    @Test
    void testRestoreAllEntities() throws Exception {
        when(recycleBinService.restoreAllEntities(anyString())).thenReturn(10);
        
        Map<String, String> requestBody = Map.of("restoredBy", "admin");
        
        mockMvc.perform(post("/api/recycle-bin/restore/all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("All entities restored successfully")))
                .andExpect(jsonPath("$.restoredCount", is(10)));
        
        verify(recycleBinService, times(1)).restoreAllEntities(eq("admin"));
    }
    
    @Test
    void testPermanentlyDeleteRecord() throws Exception {
        when(recycleBinService.permanentlyDelete(1L)).thenReturn(true);
        
        mockMvc.perform(delete("/api/recycle-bin/1/permanent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Record permanently deleted successfully")));
        
        verify(recycleBinService, times(1)).permanentlyDelete(1L);
    }
    
    @Test
    void testCleanupOldRecords() throws Exception {
        when(recycleBinService.permanentlyDeleteOldRecords(any(LocalDateTime.class))).thenReturn(3);
        
        mockMvc.perform(delete("/api/recycle-bin/cleanup")
                .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Old records cleaned up successfully")))
                .andExpect(jsonPath("$.deletedCount", is(3)));
        
        verify(recycleBinService, times(1)).permanentlyDeleteOldRecords(any(LocalDateTime.class));
    }
    
    @Test
    void testEmptyRecycleBin() throws Exception {
        when(recycleBinService.emptyRecycleBin()).thenReturn(20);
        
        mockMvc.perform(delete("/api/recycle-bin/empty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Recycle bin emptied successfully")))
                .andExpect(jsonPath("$.deletedCount", is(20)));
        
        verify(recycleBinService, times(1)).emptyRecycleBin();
    }
    
    @Test
    void testExistsInRecycleBin() throws Exception {
        when(recycleBinService.existsInRecycleBin(1L, EntityType.STUDENT)).thenReturn(true);
        
        mockMvc.perform(get("/api/recycle-bin/exists/STUDENT/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", is(true)))
                .andExpect(jsonPath("$.entityId", is(1)))
                .andExpect(jsonPath("$.entityType", is("STUDENT")));
        
        verify(recycleBinService, times(1)).existsInRecycleBin(1L, EntityType.STUDENT);
    }
    
    @Test
    void testGetEntityTypes() throws Exception {
        mockMvc.perform(get("/api/recycle-bin/entity-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].value", notNullValue()))
                .andExpect(jsonPath("$[0].displayName", notNullValue()));
    }
}
