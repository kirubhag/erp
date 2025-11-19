package krs.erp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for SettingsController
 * Tests web views and settings page functionality
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = { "ADMIN" })
class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Setup test data if needed
    }

    // ==================== SETTINGS PAGE VIEW TESTS ====================

    @Test
    void testSettingsPageLoads() throws Exception {
        // When & Then
        mockMvc.perform(get("/settings"))
            .andExpect(status().isOk());
    }

    @Test
    void testPersonalSettingsPageLoads() throws Exception {
        // When & Then
        mockMvc.perform(get("/settings/personal"))
            .andExpect(status().isOk());
    }

    @Test
    void testOrganizationSettingsPageLoads() throws Exception {
        // When & Then
        mockMvc.perform(get("/settings/organization"))
            .andExpect(status().isOk());
    }

    @Test
    void testSecuritySettingsPageLoads() throws Exception {
        // When & Then
        mockMvc.perform(get("/settings/security"))
            .andExpect(status().isOk());
    }

    @Test
    void testPreferencesPageLoads() throws Exception {
        // When & Then
        mockMvc.perform(get("/settings/preferences"))
            .andExpect(status().isOk());
    }

    // ==================== SETTINGS PAGE NOT FOUND TESTS ====================

    @Test
    void testInvalidSettingsPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/settings/invalid"))
            .andExpect(status().is4xxClientError());
    }
}
