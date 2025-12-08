package krs.erp.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.service.StorageService;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping("/records")
    public ResponseEntity<List<Map<String, Object>>> getRecordStorageStats() {
        Map<String, Long> counts = storageService.getRecordCounts();

        // Convert map to list of objects for frontend
        List<Map<String, Object>> result = counts.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("module", entry.getKey());
                    map.put("recordCount", entry.getValue());
                    return map;
                }).sorted((a, b) -> ((String) a.get("module")).compareTo((String) b.get("module")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
