package krs.erp.controller.api.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.dto.UserSettingsDTO;
import krs.erp.model.UserSettings;
import krs.erp.repository.UserSettingsRepository;
import krs.erp.service.UserSettingsService;

/**
 * Unit tests for UserSettingsController
 * Tests REST endpoints for UserSettings management
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = { "ADMIN" })
class UserSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSettingsService userSettingsService;

    @MockBean
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserSettings testSettings;
    private UserSettingsDTO testSettingsDTO;

    @BeforeEach
    void setUp() {
        // Create test settings
        testSettings = new UserSettings();
        testSettings.setId(1L);
        testSettings.setUserId(1L);
        testSettings.setOrganizationId(1L);
        testSettings.setDefaultListView("table");
        testSettings.setRecordsPerPage(50);
        testSettings.setListSidebarExpanded(true);
        testSettings.setTheme("light");

        // Create test DTO
        testSettingsDTO = new UserSettingsDTO();
        testSettingsDTO.setId(1L);
        testSettingsDTO.setUserId(1L);
        testSettingsDTO.setOrganizationId(1L);
        testSettingsDTO.setDefaultListView("table");
        testSettingsDTO.setRecordsPerPage(50);
        testSettingsDTO.setListSidebarExpanded(true);
        testSettingsDTO.setTheme("light");
    }

    // ==================== GET ENDPOINT TESTS ====================

    @Test
    void testGetUserSettings_Success() throws Exception {
        // Given
        when(userSettingsService.getUserSettings(1L, 1L))
            .thenReturn(testSettingsDTO);

        // When & Then
        mockMvc.perform(get("/api/user-settings/1/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.organizationId").value(1))
            .andExpect(jsonPath("$.defaultListView").value("table"))
            .andExpect(jsonPath("$.recordsPerPage").value(50))
            .andExpect(jsonPath("$.listSidebarExpanded").value(true))
            .andExpect(jsonPath("$.theme").value("light"));

        verify(userSettingsService, times(1)).getUserSettings(1L, 1L);
    }

    @Test
    void testGetUserSettings_NotFound() throws Exception {
        // Given
        when(userSettingsService.getUserSettings(999L, 999L))
            .thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/user-settings/999/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    // ==================== PUT ENDPOINT TESTS ====================

    @Test
    void testUpdateDefaultListView() throws Exception {
        // Given
        String requestBody = "{\"defaultListView\": \"grid\"}";

        // When & Then
        mockMvc.perform(put("/api/user-settings/1/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk());

        verify(userSettingsService).updateUserSettings(anyLong(), anyLong(), any());
    }

    @Test
    void testUpdateRecordsPerPage() throws Exception {
        // Given
        String requestBody = "{\"recordsPerPage\": 100}";

        // When & Then
        mockMvc.perform(put("/api/user-settings/1/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk());

        verify(userSettingsService).updateUserSettings(anyLong(), anyLong(), any());
    }

    @Test
    void testUpdateListSidebarExpanded() throws Exception {
        // Given
        String requestBody = "{\"listSidebarExpanded\": false}";

        // When & Then
        mockMvc.perform(put("/api/user-settings/1/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk());

        verify(userSettingsService).updateUserSettings(anyLong(), anyLong(), any());
    }

    @Test
    void testUpdateTheme() throws Exception {
        // Given
        String requestBody = "{\"theme\": \"dark\"}";

        // When & Then
        mockMvc.perform(put("/api/user-settings/1/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk());

        verify(userSettingsService).updateUserSettings(anyLong(), anyLong(), any());
    }

    @Test
    void testUpdateMultipleSettings() throws Exception {
        // Given
        String requestBody = "{\"defaultListView\": \"grid\", \"recordsPerPage\": 100, \"theme\": \"dark\"}";

        // When & Then
        mockMvc.perform(put("/api/user-settings/1/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk());

        verify(userSettingsService).updateUserSettings(anyLong(), anyLong(), any());
    }

    // ==================== VALIDATION TESTS ====================

    @Test
    void testGetUserSettings_InvalidUserId() throws Exception {
        // Given
        when(userSettingsService.getUserSettings(null, 1L))
            .thenReturn(null);

        // When & Then - Should handle gracefully
        mockMvc.perform(get("/api/user-settings/invalid/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateSettings_InvalidPayload() throws Exception {
        // Given
        String invalidBody = "{}";

        // When & Then
        mockMvc.perform(put("/api/user-settings/1/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidBody))
            .andExpect(status().isOk());
    }
}
