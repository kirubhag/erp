package krs.erp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for dashboard.
 * Provides endpoints for dashboard statistics and widgets.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    /**
     * Get dashboard statistics
     * GET /api/dashboard/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // TODO: Replace with actual data from services
        stats.put("totalStudents", 378);
        stats.put("presentToday", 0);
        stats.put("absentToday", 0);
        stats.put("registeredParents", 0);
        stats.put("attendanceRate", 0.0);
        stats.put("totalAttendance", 0);
        
        return ResponseEntity.ok(stats);
    }
}
