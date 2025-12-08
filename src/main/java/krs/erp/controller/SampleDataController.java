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

                    // Map entity names to XML file paths and use appropriate import method
                    String xmlFilePath = getXmlFilePathForEntity(entityName);

                    if (xmlFilePath != null) {
                        // Use entity-specific import method based on entity type
                        if (isStudentEntity(entityName)) {
                            // Load all student files if "STUDENTS" is selected
                            if (entityName.equalsIgnoreCase("STUDENTS")) {
                                // Load kindergarten
                                dataImportService.importStudentDataFromXml("data/student/student_kindergarten.xml");
                                // Load all 12 grades
                                for (int i = 1; i <= 12; i++) {
                                    dataImportService
                                            .importStudentDataFromXml("data/student/student_grade_" + i + ".xml");
                                }
                            } else {
                                // Load specific grade file
                                dataImportService.importStudentDataFromXml(xmlFilePath);
                            }
                        } else if (isGradeEntity(entityName)) {
                            dataImportService.importGradesDataFromXml(xmlFilePath);
                        } else if (isStaffEntity(entityName)) {
                            dataImportService.importStaffDataFromXml(xmlFilePath);
                        } else if (isParentEntity(entityName)) {
                            dataImportService.importParentsDataFromXml(xmlFilePath);
                        } else if (isSubjectEntity(entityName)) {
                            dataImportService.importSubjectsDataFromXml(xmlFilePath);
                        } else if (isClassEntity(entityName)) {
                            dataImportService.importClassesDataFromXml(xmlFilePath);
                        } else {
                            // For other entities, use the generic import
                            dataImportService.importDataFromXml(xmlFilePath);
                        }
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
        entityToXmlMap.put("students", "data/student/student_grade_1.xml");
        entityToXmlMap.put("staff", "data/staff/sample-staff.xml");
        entityToXmlMap.put("parents", "data/parent/sample-parents.xml");
        entityToXmlMap.put("users", "data/user/sample-users.xml");

        // Academic data - Grade files
        entityToXmlMap.put("grades", "data/grade/grades.xml");
        entityToXmlMap.put("grade_1", "data/student/student_grade_1.xml");
        entityToXmlMap.put("grade_2", "data/student/student_grade_2.xml");
        entityToXmlMap.put("grade_3", "data/student/student_grade_3.xml");
        entityToXmlMap.put("grade_4", "data/student/student_grade_4.xml");
        entityToXmlMap.put("grade_5", "data/student/student_grade_5.xml");
        entityToXmlMap.put("grade_6", "data/student/student_grade_6.xml");
        entityToXmlMap.put("grade_7", "data/student/student_grade_7.xml");
        entityToXmlMap.put("grade_8", "data/student/student_grade_8.xml");
        entityToXmlMap.put("grade_9", "data/student/student_grade_9.xml");
        entityToXmlMap.put("grade_10", "data/student/student_grade_10.xml");
        entityToXmlMap.put("grade_11", "data/student/student_grade_11.xml");
        entityToXmlMap.put("grade_12", "data/student/student_grade_12.xml");
        entityToXmlMap.put("kindergarten", "data/student/student_kindergarten.xml");

        entityToXmlMap.put("subjects", "data/subject/subjects.xml");
        entityToXmlMap.put("classes", "data/class/classes.xml");
        entityToXmlMap.put("timetables", "data/timetable/timetables.xml");

        // Address data
        entityToXmlMap.put("addresses", "data/address/sample-addresses.xml");

        // Medical and Guardian data
        entityToXmlMap.put("medical", "data/student/sample_medical_data.xml");
        entityToXmlMap.put("guardians", "data/student/sample_guardian_data.xml");

        return entityToXmlMap.get(entityName.toLowerCase());
    }

    /**
     * Check if the entity name represents student data
     */
    private boolean isStudentEntity(String entityName) {
        String lowerName = entityName.toLowerCase();
        return lowerName.equals("students") ||
                lowerName.startsWith("grade_") ||
                lowerName.equals("kindergarten");
    }

    /**
     * Check if the entity name represents staff data
     */
    private boolean isStaffEntity(String entityName) {
        return entityName.equalsIgnoreCase("staff");
    }

    /**
     * Check if the entity name represents parent data
     */
    private boolean isParentEntity(String entityName) {
        return entityName.equalsIgnoreCase("parents");
    }

    /**
     * Check if the entity name represents subject data
     */
    private boolean isSubjectEntity(String entityName) {
        return entityName.equalsIgnoreCase("subjects");
    }

    /**
     * Check if the entity name represents grade data
     */
    private boolean isGradeEntity(String entityName) {
        return entityName.equalsIgnoreCase("grades");
    }

    /**
     * Check if the entity name represents class data
     */
    private boolean isClassEntity(String entityName) {
        return entityName.equalsIgnoreCase("classes");
    }
}
