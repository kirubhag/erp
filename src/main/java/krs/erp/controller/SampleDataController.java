package krs.erp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.service.DataImportService;

/**
 * REST Controller for Sample Data operations
 * Provides endpoints to populate sample data from XML files
 */
@RestController
@RequestMapping("/api/v1/sample-data")
@CrossOrigin(origins = "*")
public class SampleDataController {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataController.class);

    @Autowired
    private DataImportService dataImportService;

    /**
     * Populate sample data for selected entities
     * 
     * @param request Map containing "entityNames" list
     * @return Summary of population results
     */
    @PostMapping("/populate")
    public ResponseEntity<Map<String, Object>> populateSampleData(@RequestBody Map<String, List<String>> request) {
        logger.info("Received sample data population request");
        
        List<String> entityNames = request.get("entityNames");
        if (entityNames == null || entityNames.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "No entities specified");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, Object> response = new HashMap<>();
        List<Long> importHistoryIds = new ArrayList<>();
        int successfulImports = 0;
        int failedImports = 0;
        int skippedImports = 0;
        List<String> errors = new ArrayList<>();

        try {
            for (String entityName : entityNames) {
                try {
                    logger.info("Importing sample data for entity: {}", entityName);
                    
                    // Map entity names to XML file paths
                    String xmlFilePath = getXmlFilePathForEntity(entityName);
                    
                    if (xmlFilePath != null) {
                        // Import data using existing DataImportService
                        dataImportService.importDataFromXml(xmlFilePath);
                        successfulImports++;
                        logger.info("Successfully imported sample data for: {}", entityName);
                    } else {
                        logger.warn("No XML file mapping found for entity: {}", entityName);
                        skippedImports++;
                        errors.add("No XML file found for: " + entityName);
                    }
                } catch (Exception e) {
                    logger.error("Failed to import sample data for entity: {}", entityName, e);
                    failedImports++;
                    errors.add(entityName + ": " + e.getMessage());
                }
            }

            response.put("totalEntities", entityNames.size());
            response.put("successfulImports", successfulImports);
            response.put("failedImports", failedImports);
            response.put("skippedImports", skippedImports);
            response.put("importHistoryIds", importHistoryIds);
            response.put("errors", errors);
            response.put("summary", String.format("Imported %d/%d entities successfully", 
                successfulImports, entityNames.size()));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error during sample data population", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to populate sample data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Map entity names to their XML file paths
     */
    private String getXmlFilePathForEntity(String entityName) {
        Map<String, String> entityToXmlMap = new HashMap<>();
        
        // Core entities
        entityToXmlMap.put("students", "data/sample-data.xml");
        entityToXmlMap.put("staff", "data/sample-data.xml");
        entityToXmlMap.put("parents", "data/sample-data.xml");
        entityToXmlMap.put("users", "data/sample-data.xml");
        
        // Academic data
        entityToXmlMap.put("grades", "data/grades.xml");
        entityToXmlMap.put("grade_1", "data/grade_1.xml");
        entityToXmlMap.put("grade_2", "data/grade_2.xml");
        entityToXmlMap.put("grade_3", "data/grade_3.xml");
        entityToXmlMap.put("grade_4", "data/grade_4.xml");
        entityToXmlMap.put("grade_5", "data/grade_5.xml");
        entityToXmlMap.put("grade_6", "data/grade_6.xml");
        entityToXmlMap.put("grade_7", "data/grade_7.xml");
        entityToXmlMap.put("grade_8", "data/grade_8.xml");
        entityToXmlMap.put("grade_9", "data/grade_9.xml");
        entityToXmlMap.put("grade_10", "data/grade_10.xml");
        entityToXmlMap.put("grade_11", "data/grade_11.xml");
        entityToXmlMap.put("grade_12", "data/grade_12.xml");
        entityToXmlMap.put("kindergarten", "data/kindergarten.xml");
        
        entityToXmlMap.put("subjects", "data/subjects.xml");
        entityToXmlMap.put("timetables", "data/timetables.xml");
        
        // Address data
        entityToXmlMap.put("addresses", "data/address/sample-addresses.xml");
        
        return entityToXmlMap.get(entityName.toLowerCase());
    }
}
