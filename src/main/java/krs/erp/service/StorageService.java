package krs.erp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Get record counts for various entities
     */
    public Map<String, Long> getRecordCounts() {
        Map<String, Long> counts = new HashMap<>();

        // Core Entities
        count(counts, "Students", "students");
        count(counts, "Staff", "staff");
        count(counts, "Parents", "parents");
        count(counts, "Users", "iam_users");
        count(counts, "Organizations", "organizations");

        // Academic Entities
        count(counts, "Subjects", "subjects");
        count(counts, "Timetables", "timetables");
        count(counts, "Attendance Records", "attendance");
        count(counts, "Health Records", "health_records");

        // System Entities
        count(counts, "Roles", "roles");
        count(counts, "Attachments", "erp_attachments");

        return counts;
    }

    private void count(Map<String, Long> map, String label, String tableName) {
        try {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Long.class);
            map.put(label, count != null ? count : 0L);
        } catch (Exception e) {
            // Table might not exist or other error, allow 0
            map.put(label, 0L);
        }
    }
}
