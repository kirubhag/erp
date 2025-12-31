package krs.erp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
     * Define the correct dependency order for loading sample data.
     * Entities are loaded in this order to satisfy foreign key dependencies:
     * 1. Addresses (required by students, staff, parents)
     * 2. Students (no other dependencies besides addresses)
     * 3. Staff (no dependencies besides addresses)
     * 4. Parents (no dependencies besides addresses)
     * 5. Subjects (no dependencies)
     * 6. Rooms (no dependencies)
     * 7. Classes (may depend on staff/rooms)
     * 8. Courses (may depend on subjects/staff)
     * 9. Timetables (depends on classes/subjects/staff)
     * 10. Attendance (depends on students/classes)
     * 11. Grades (depends on students and subjects)
     * 12. Exams (depends on courses/subjects)
     */
    private static final List<String> DEPENDENCY_ORDER = Arrays.asList(
            "addresses", // MUST be loaded first - referenced by students, staff, parents
            "STUDENTS", // Load all students
            "staff", // Staff data
            "parents", // Parent data
            "subjects", // Subjects must be loaded before grades
            "rooms", // Room data
            "classes", // Class data
            "courses", // Course data
            "timetables", // Timetable data
            "attendance", // Attendance data
            "grades", // Grades depend on students and subjects
            "exams", // Exam data
            "registrations", // Student registrations for new academic year
            "payroll", // Depends on Staff
            "leaves", // Depends on Staff
            "performance", // Depends on Staff
            "inventory", // Depends on Staff
            "maintenance", // Depends on Staff
            "reporting", // Reporting & Analytics / MIS
            "documents", // Document Management
            "finance", // Fee & Fine Management
            "scholarships", // Scholarships & Financial Aid
            "accounting", // General Ledger
            "library", // Library Management
            "lms", // Learning Management System
            "tpd" // Training & Professional Development
    );

    /**
     * Populate ALL sample data with correct dependency order.
     * This is the recommended endpoint for the "Add Sample Data" button.
     * 
     * @return Summary of population results
     */
    @PostMapping("/populate-all")
    public ResponseEntity<Map<String, Object>> populateAllSampleData() {
        logger.info("Received request to populate ALL sample data with correct dependency order");

        // Use all entities in dependency order
        List<String> allEntities = new ArrayList<>(DEPENDENCY_ORDER);

        Map<String, List<String>> request = new HashMap<>();
        request.put("entityNames", allEntities);

        return populateSampleData(request);
    }

    /**
     * Get the list of available sample data entities
     * 
     * @return List of available entity names
     */
    @GetMapping("/available-entities")
    public ResponseEntity<Map<String, Object>> getAvailableEntities() {
        Map<String, Object> response = new HashMap<>();

        List<Map<String, String>> entities = new ArrayList<>();

        // Add entity information with descriptions
        entities.add(createEntityInfo("addresses", "Sample Addresses (Required for students, staff, parents)"));
        entities.add(createEntityInfo("STUDENTS", "All Students (Kindergarten to Grade 12)"));
        entities.add(createEntityInfo("staff", "Staff Members"));
        entities.add(createEntityInfo("parents", "Parents/Guardians"));
        entities.add(createEntityInfo("subjects", "Subjects/Courses"));
        entities.add(createEntityInfo("classes", "Classes"));
        entities.add(createEntityInfo("rooms", "Rooms"));
        entities.add(createEntityInfo("courses", "Courses"));
        entities.add(createEntityInfo("timetables", "Timetables"));
        entities.add(createEntityInfo("attendance", "Attendance Records"));
        entities.add(createEntityInfo("grades", "Student Grades"));
        entities.add(createEntityInfo("exams", "Exams"));
        entities.add(createEntityInfo("payroll", "Payroll Data (Generated)"));
        entities.add(createEntityInfo("leaves", "Leave Management (Types & Balances)"));
        entities.add(createEntityInfo("performance", "Performance Evaluation (Cycles & Reviews)"));
        entities.add(createEntityInfo("inventory", "Inventory & Asset Management (Assets, Stock, Vendors)"));
        entities.add(createEntityInfo("maintenance", "Maintenance & Facility Management"));
        entities.add(createEntityInfo("reporting", "Reporting & Analytics / MIS"));
        entities.add(createEntityInfo("documents", "Document Management"));
        entities.add(createEntityInfo("calendar", "Event & Calendar Management"));
        entities.add(createEntityInfo("communication", "Communication & Alerts"));
        entities.add(createEntityInfo("alumni", "Alumni Management"));
        entities.add(createEntityInfo("finance", "Fee & Fine Management"));
        entities.add(createEntityInfo("scholarships", "Scholarships & Financial Aid"));
        entities.add(createEntityInfo("accounting", "General Ledger (Accounting)"));

        // Individual Finance Entities
        entities.add(createEntityInfo("fee_types", "Fee Types (Tuition, Transport, Library, etc.)"));
        entities.add(createEntityInfo("fee_structures", "Fee Structures (Grade-specific fee amounts)"));
        entities.add(createEntityInfo("fee_payments", "Fee Payments (Student payment records)"));
        entities.add(createEntityInfo("discount_rules", "Discount Rules (Sibling, Merit, Early Payment)"));
        entities.add(createEntityInfo("fine_categories", "Fine Categories (Late fees, Disciplinary)"));
        entities.add(createEntityInfo("invoices", "Invoices (Billing invoices with line items)"));
        entities.add(createEntityInfo("transactions", "Transactions (Payment transactions)"));
        entities.add(createEntityInfo("chart_of_accounts", "Chart of Accounts (Asset, Liability, Revenue, Expense)"));
        entities.add(createEntityInfo("journal_entries", "Journal Entries (Double-entry bookkeeping)"));
        entities.add(createEntityInfo("budgets", "Budgets (Department budgets with tracking)"));

        entities.add(createEntityInfo("library", "Library Management"));
        entities.add(createEntityInfo("lms", "Learning Management System (LMS)"));
        entities.add(createEntityInfo("tpd", "Staff Training & Growth (TPD)"));

        response.put("entities", entities);
        response.put("recommendedOrder", DEPENDENCY_ORDER);

        return ResponseEntity.ok(response);
    }

    private Map<String, String> createEntityInfo(String name, String description) {
        Map<String, String> info = new HashMap<>();
        info.put("name", name);
        info.put("description", description);
        return info;
    }

    /**
     * Populate sample data for selected entities.
     * Entities are automatically reordered to satisfy dependencies.
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

        // Reorder entities based on dependency order
        List<String> orderedEntities = reorderByDependencies(entityNames);
        logger.info("Entities reordered for dependencies: {}", orderedEntities);

        Map<String, Object> response = new HashMap<>();
        List<Long> importHistoryIds = new ArrayList<>();
        int successfulImports = 0;
        int failedImports = 0;
        int skippedImports = 0;
        List<String> errors = new ArrayList<>();

        try {
            for (String entityName : orderedEntities) {
                try {
                    logger.info("Importing sample data for entity: {}", entityName);

                    // Check for special entity handlers first (these don't use XML file mapping)
                    boolean handledBySpecialMethod = false;

                    // Finance entities with direct XML import methods
                    if (entityName.equalsIgnoreCase("fee_types")) {
                        dataImportService.importFeeTypesDataFromXml("data/finance/sample-fee-types.xml");
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("fee_structures")) {
                        dataImportService.importFeeStructuresDataFromXml("data/finance/sample-fee-structures.xml");
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("fee_payments")) {
                        dataImportService.importFeePaymentsDataFromXml("data/finance/sample-fee-payments.xml");
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("discount_rules")) {
                        dataImportService.importDiscountRulesDataFromXml("data/finance/sample-discount-rules.xml");
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("fine_categories")) {
                        dataImportService.importFineCategoriesDataFromXml("data/finance/sample-fine-categories.xml");
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("invoices")) {
                        dataImportService.importInvoicesDataFromXml("data/finance/sample-invoices.xml");
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("transactions")) {
                        dataImportService.importTransactionsDataFromXml("data/finance/sample-transactions.xml");
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("chart_of_accounts")) {
                        dataImportService.importChartOfAccountsDataFromXml("data/finance/sample-chart-of-accounts.xml");
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("journal_entries")) {
                        dataImportService.importJournalEntriesDataFromXml("data/finance/sample-journal-entries.xml");
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("budgets")) {
                        dataImportService.importBudgetsDataFromXml("data/finance/sample-budgets.xml");
                        handledBySpecialMethod = true;
                    }
                    // Other special entity handlers (programmatic generation)
                    else if (entityName.equalsIgnoreCase("registrations")) {
                        dataImportService.generateStudentRegistrationSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("payroll")) {
                        dataImportService.generatePayrollSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("leaves")) {
                        dataImportService.generateLeaveSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("performance")) {
                        dataImportService.generatePerformanceSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("inventory")) {
                        dataImportService.generateInventorySampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("maintenance")) {
                        dataImportService.generateMaintenanceSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("reporting")) {
                        dataImportService.generateReportingSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("documents")) {
                        dataImportService.generateDocumentSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("calendar")) {
                        dataImportService.generateCalendarSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("communication")) {
                        dataImportService.generateCommunicationSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("alumni")) {
                        dataImportService.generateAlumniSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("finance")) {
                        dataImportService.generateFinanceSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("scholarships")) {
                        dataImportService.generateScholarshipSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("accounting")) {
                        dataImportService.generateAccountingSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("library")) {
                        dataImportService.generateLibrarySampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("lms")) {
                        dataImportService.generateLmsSampleData();
                        handledBySpecialMethod = true;
                    } else if (entityName.equalsIgnoreCase("tpd")) {
                        dataImportService.generateTpdSampleData();
                        handledBySpecialMethod = true;
                    }

                    // If handled by special method, mark as successful and continue
                    if (handledBySpecialMethod) {
                        successfulImports++;
                        logger.info("Successfully imported sample data for: {}", entityName);
                        continue;
                    }

                    // Otherwise, try to find XML file mapping
                    String xmlFilePath = getXmlFilePathForEntity(entityName);

                    if (xmlFilePath != null) {
                        // Use entity-specific import method based on entity type
                        if (isOrganizationEntity(entityName)) {
                            dataImportService.importOrganizationsDataFromXml(xmlFilePath);
                        } else if (isAcademicYearEntity(entityName)) {
                            dataImportService.importAcademicYearsDataFromXml(xmlFilePath);
                        } else if (isStudentEntity(entityName)) {
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
                        } else if (isTimetableEntity(entityName)) {
                            dataImportService.importTimetablesDataFromXml(xmlFilePath);
                        } else if (isRoomEntity(entityName)) {
                            dataImportService.importRoomsDataFromXml(xmlFilePath);
                        } else if (isAttendanceEntity(entityName)) {
                            dataImportService.importAttendanceDataFromXml(xmlFilePath);
                        } else if (isCourseEntity(entityName)) {
                            dataImportService.importCoursesDataFromXml(xmlFilePath);
                        } else if (isExamEntity(entityName)) {
                            dataImportService.importExamsDataFromXml(xmlFilePath);
                        } else if (entityName.equalsIgnoreCase("registrations")) {
                            dataImportService.generateStudentRegistrationSampleData();
                        } else if (isAddressEntity(entityName)) {
                            dataImportService.importAddressesDataFromXml(xmlFilePath);
                        } else if (entityName.equalsIgnoreCase("payroll")) {
                            dataImportService.generatePayrollSampleData();
                        } else if (entityName.equalsIgnoreCase("leaves")) {
                            dataImportService.generateLeaveSampleData();
                        } else if (entityName.equalsIgnoreCase("performance")) {
                            dataImportService.generatePerformanceSampleData();
                        } else if (entityName.equalsIgnoreCase("inventory")) {
                            dataImportService.generateInventorySampleData();
                        } else if (entityName.equalsIgnoreCase("maintenance")) {
                            dataImportService.generateMaintenanceSampleData();
                        } else if (entityName.equalsIgnoreCase("reporting")) {
                            dataImportService.generateReportingSampleData();
                        } else if (entityName.equalsIgnoreCase("documents")) {
                            dataImportService.generateDocumentSampleData();
                        } else if (entityName.equalsIgnoreCase("calendar")) {
                            dataImportService.generateCalendarSampleData();
                        } else if (entityName.equalsIgnoreCase("communication")) {
                            dataImportService.generateCommunicationSampleData();
                        } else if (entityName.equalsIgnoreCase("alumni")) {
                            dataImportService.generateAlumniSampleData();
                        } else if (entityName.equalsIgnoreCase("finance")) {
                            dataImportService.generateFinanceSampleData();
                        } else if (entityName.equalsIgnoreCase("scholarships")) {
                            dataImportService.generateScholarshipSampleData();
                        } else if (entityName.equalsIgnoreCase("accounting")) {
                            dataImportService.generateAccountingSampleData();
                        } else if (entityName.equalsIgnoreCase("library")) {
                            dataImportService.generateLibrarySampleData();
                        } else if (entityName.equalsIgnoreCase("lms")) {
                            dataImportService.generateLmsSampleData();
                        } else if (entityName.equalsIgnoreCase("tpd")) {
                            dataImportService.generateTpdSampleData();
                        } else if (entityName.equalsIgnoreCase("fee_types")) {
                            dataImportService.importFeeTypesDataFromXml("data/finance/sample-fee-types.xml");
                        } else if (entityName.equalsIgnoreCase("fee_structures")) {
                            dataImportService.importFeeStructuresDataFromXml("data/finance/sample-fee-structures.xml");
                        } else if (entityName.equalsIgnoreCase("fee_payments")) {
                            dataImportService.importFeePaymentsDataFromXml("data/finance/sample-fee-payments.xml");
                        } else if (entityName.equalsIgnoreCase("discount_rules")) {
                            dataImportService.importDiscountRulesDataFromXml("data/finance/sample-discount-rules.xml");
                        } else if (entityName.equalsIgnoreCase("fine_categories")) {
                            dataImportService
                                    .importFineCategoriesDataFromXml("data/finance/sample-fine-categories.xml");
                        } else if (entityName.equalsIgnoreCase("invoices")) {
                            dataImportService.importInvoicesDataFromXml("data/finance/sample-invoices.xml");
                        } else if (entityName.equalsIgnoreCase("transactions")) {
                            dataImportService.importTransactionsDataFromXml("data/finance/sample-transactions.xml");
                        } else if (entityName.equalsIgnoreCase("chart_of_accounts")) {
                            dataImportService
                                    .importChartOfAccountsDataFromXml("data/finance/sample-chart-of-accounts.xml");
                        } else if (entityName.equalsIgnoreCase("journal_entries")) {
                            dataImportService
                                    .importJournalEntriesDataFromXml("data/finance/sample-journal-entries.xml");
                        } else if (entityName.equalsIgnoreCase("budgets")) {
                            dataImportService.importBudgetsDataFromXml("data/finance/sample-budgets.xml");
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

        // Organization and Academic data
        entityToXmlMap.put("organization", "data/organisation/sample-organisations.xml");
        entityToXmlMap.put("organisations", "data/organisation/sample-organisations.xml");
        entityToXmlMap.put("academic_year", "data/academic/academic-years.xml");
        entityToXmlMap.put("academic_years", "data/academic/academic-years.xml");

        // Core entities
        entityToXmlMap.put("students", "data/student/student_grade_1.xml");
        entityToXmlMap.put("student", "data/student/student_grade_1.xml");
        entityToXmlMap.put("staff", "data/staff/sample-staff.xml");
        entityToXmlMap.put("parents", "data/parent/sample-parents.xml");
        entityToXmlMap.put("parent", "data/parent/sample-parents.xml");
        entityToXmlMap.put("users", "data/user/sample-users.xml");

        // Academic data - Grade files
        entityToXmlMap.put("grades", "data/grade/grades.xml");
        entityToXmlMap.put("grade", "data/grade/grades.xml");
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
        entityToXmlMap.put("subject", "data/subject/subjects.xml");
        entityToXmlMap.put("classes", "data/class/classes.xml");
        entityToXmlMap.put("class", "data/class/classes.xml");
        entityToXmlMap.put("registrations", "data/admission/student_registration_fields.xml"); // Dummy mapping, using
                                                                                               // generate method
        entityToXmlMap.put("timetables", "data/timetable/timetables.xml");

        // Address data
        entityToXmlMap.put("addresses", "data/address/sample-addresses.xml");
        entityToXmlMap.put("address", "data/address/sample-addresses.xml");

        // Medical and Guardian data
        entityToXmlMap.put("medical", "data/student/sample_medical_data.xml");
        entityToXmlMap.put("guardians", "data/student/sample_guardian_data.xml");

        entityToXmlMap.put("rooms", "data/room/rooms.xml");
        entityToXmlMap.put("attendance", "data/attendance/attendance.xml");
        entityToXmlMap.put("courses", "data/course/courses.xml");
        entityToXmlMap.put("course", "data/course/courses.xml");
        entityToXmlMap.put("exams", "data/exam/exams.xml");

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
     * Check if the entity name represents organization data
     */
    private boolean isOrganizationEntity(String entityName) {
        String lowerName = entityName.toLowerCase();
        return lowerName.equals("organization") || lowerName.equals("organisations")
                || lowerName.equals("organizations");
    }

    /**
     * Check if the entity name represents academic year data
     */
    private boolean isAcademicYearEntity(String entityName) {
        String lowerName = entityName.toLowerCase();
        return lowerName.equals("academic_year") || lowerName.equals("academic_years")
                || lowerName.equals("academicyear");
    }

    /**
     * Check if the entity name represents address data
     */
    private boolean isAddressEntity(String entityName) {
        String lowerName = entityName.toLowerCase();
        return lowerName.equals("address") || lowerName.equals("addresses");
    }

    /**
     * Check if the entity name represents grade data
     */
    private boolean isGradeEntity(String entityName) {
        return entityName.equalsIgnoreCase("grades") || entityName.equalsIgnoreCase("grade");
    }

    /**
     * Check if the entity name represents class data
     */
    private boolean isClassEntity(String entityName) {
        return entityName.equalsIgnoreCase("classes") || entityName.equalsIgnoreCase("class");
    }

    /**
     * Check if the entity name represents timetable data
     */
    private boolean isTimetableEntity(String entityName) {
        return entityName.equalsIgnoreCase("timetables");
    }

    /**
     * Check if the entity name represents room data
     */
    private boolean isRoomEntity(String entityName) {
        return entityName.equalsIgnoreCase("rooms");
    }

    private boolean isAttendanceEntity(String entityName) {
        return entityName.equalsIgnoreCase("attendance");
    }

    private boolean isCourseEntity(String entityName) {
        return entityName.equalsIgnoreCase("courses") || entityName.equalsIgnoreCase("course");
    }

    private boolean isExamEntity(String entityName) {
        return entityName.equalsIgnoreCase("exams");
    }

    /**
     * Reorder the requested entities based on dependency order.
     * This ensures that entities with dependencies are loaded after their
     * dependencies.
     * For example, "grades" will always be loaded after "students" and "subjects".
     * 
     * @param requestedEntities The list of entities requested by the user
     * @return A new list with entities ordered by dependencies
     */
    private List<String> reorderByDependencies(List<String> requestedEntities) {
        // Use LinkedHashSet to maintain insertion order and avoid duplicates
        Set<String> orderedSet = new LinkedHashSet<>();

        // Convert requested entities to lowercase for comparison
        Set<String> requestedLower = new LinkedHashSet<>();
        for (String entity : requestedEntities) {
            requestedLower.add(entity.toLowerCase());
        }

        // Check if grades is requested - if so, ensure students and subjects are loaded
        // first
        boolean gradesRequested = requestedLower.contains("grades");

        // Add entities in dependency order, but only if they were requested
        // OR if they are required dependencies for requested entities
        for (String dependencyEntity : DEPENDENCY_ORDER) {
            String lowerDep = dependencyEntity.toLowerCase();

            // Check if this entity was directly requested
            boolean directlyRequested = false;
            for (String requested : requestedEntities) {
                if (requested.equalsIgnoreCase(dependencyEntity)) {
                    directlyRequested = true;
                    orderedSet.add(requested); // Use original case
                    break;
                }
            }

            // If grades is requested, auto-include STUDENTS and subjects as dependencies
            if (!directlyRequested && gradesRequested) {
                if (lowerDep.equals("students") || lowerDep.equals("subjects")) {
                    orderedSet.add(dependencyEntity);
                    logger.info("Auto-including {} as dependency for grades", dependencyEntity);
                }
            }
        }

        // Add any remaining entities that weren't in the dependency order
        // (e.g., individual grade files like grade_1, grade_2, etc.)
        for (String entity : requestedEntities) {
            if (!orderedSet.contains(entity)) {
                // Check if it's a student entity - add it early
                if (isStudentEntity(entity)) {
                    // Insert student entities at the beginning (after any STUDENTS entry)
                    List<String> tempList = new ArrayList<>(orderedSet);
                    int insertIndex = 0;
                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i).equalsIgnoreCase("STUDENTS")) {
                            insertIndex = i + 1;
                            break;
                        }
                    }
                    tempList.add(insertIndex, entity);
                    orderedSet.clear();
                    orderedSet.addAll(tempList);
                } else {
                    orderedSet.add(entity);
                }
            }
        }

        return new ArrayList<>(orderedSet);
    }
}
