package krs.erp.initializer;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.Student;
import krs.erp.repository.StudentRepository;

/**
 * Initializes student data from XML files.
 * Loads student records from grade_*.xml and kindergarten.xml files into the database.
 * 
 * Expected XML structure (attributes-based):
 * <students 
 *   id="101" 
 *   first_name="Aiden" 
 *   last_name="Anderson" 
 *   student_id="G1STU001" 
 *   email="aiden.anderson@student.school.edu" 
 *   date_of_birth="2018-01-15" 
 *   gender="MALE" 
 *   enrollment_date="2024-08-15" 
 *   grade_level="GRADE_1" 
 *   enrollment_status="ACTIVE" 
 *   emergency_contact_name="Jennifer Anderson" 
 *   emergency_contact_phone="+1234560101" 
 *   emergency_contact_relation="Mother" 
 * />
 */
@Component
@Order(5)
public class StudentDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StudentDataInitializer.class);

    @Autowired
    private StudentRepository studentRepository;

    // Map grade_level string to enum
    private static final Map<String, Student.GradeLevel> GRADE_LEVEL_MAP = new HashMap<>();
    
    static {
        GRADE_LEVEL_MAP.put("KINDERGARTEN", Student.GradeLevel.KINDERGARTEN);
        GRADE_LEVEL_MAP.put("GRADE_1", Student.GradeLevel.GRADE_1);
        GRADE_LEVEL_MAP.put("GRADE_2", Student.GradeLevel.GRADE_2);
        GRADE_LEVEL_MAP.put("GRADE_3", Student.GradeLevel.GRADE_3);
        GRADE_LEVEL_MAP.put("GRADE_4", Student.GradeLevel.GRADE_4);
        GRADE_LEVEL_MAP.put("GRADE_5", Student.GradeLevel.GRADE_5);
        GRADE_LEVEL_MAP.put("GRADE_6", Student.GradeLevel.GRADE_6);
        GRADE_LEVEL_MAP.put("GRADE_7", Student.GradeLevel.GRADE_7);
        GRADE_LEVEL_MAP.put("GRADE_8", Student.GradeLevel.GRADE_8);
        GRADE_LEVEL_MAP.put("GRADE_9", Student.GradeLevel.GRADE_9);
        GRADE_LEVEL_MAP.put("GRADE_10", Student.GradeLevel.GRADE_10);
        GRADE_LEVEL_MAP.put("GRADE_11", Student.GradeLevel.GRADE_11);
        GRADE_LEVEL_MAP.put("GRADE_12", Student.GradeLevel.GRADE_12);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting student data initialization from XML files...");
        
        // Only initialize if table is empty
        if (studentRepository.count() > 0) {
            logger.info("Student data already exists. Skipping initialization.");
            return;
        }
        
        int totalLoaded = 0;
        
        // Load students from kindergarten
        try {
            int loaded = loadStudentsFromFile("data/student/student_kindergarten.xml");
            totalLoaded += loaded;
            logger.info("Loaded {} students from student_kindergarten.xml", loaded);
        } catch (Exception e) {
            logger.error("Error loading students from student_kindergarten.xml: {}", e.getMessage(), e);
        }

        // Load students from all grade files
        for (int i = 1; i <= 12; i++) {
            String filename = String.format("data/student/student_grade_%d.xml", i);
            try {
                int loaded = loadStudentsFromFile(filename);
                totalLoaded += loaded;
                logger.info("Loaded {} students from {}", loaded, filename);
            } catch (Exception e) {
                logger.error("Error loading students from {}: {}", filename, e.getMessage(), e);
            }
        }

        logger.info("Student data initialization complete. Total students loaded: {}", totalLoaded);
    }

    /**
     * Load students from an XML file containing student records as attributes
     */
    private int loadStudentsFromFile(String filename) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            logger.warn("File not found: {}", filename);
            return 0;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);

        NodeList studentNodes = doc.getElementsByTagName("students");
        int count = 0;

        for (int i = 0; i < studentNodes.getLength(); i++) {
            Element studentElement = (Element) studentNodes.item(i);
            
            try {
                Student student = parseStudentFromElement(studentElement);
                if (student != null) {
                    // Check if student with this ID already exists
                    if (!studentRepository.findByStudentId(student.getStudentId()).isPresent()) {
                        studentRepository.save(student);
                        count++;
                    }
                }
            } catch (Exception e) {
                logger.error("Error parsing student element: {}", e.getMessage(), e);
            }
        }

        return count;
    }

    /**
     * Parse a single student element from XML
     */
    private Student parseStudentFromElement(Element element) throws Exception {
        Student student = new Student();
        
        // Required fields
        String firstName = getAttributeValue(element, "first_name");
        String lastName = getAttributeValue(element, "last_name");
        String studentId = getAttributeValue(element, "student_id");
        String dateOfBirthStr = getAttributeValue(element, "date_of_birth");
        String enrollmentDateStr = getAttributeValue(element, "enrollment_date");
        String gradeLevelStr = getAttributeValue(element, "grade_level");
        
        // Validate required fields
        if (firstName == null || lastName == null || studentId == null) {
            logger.warn("Missing required fields for student");
            return null;
        }
        
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setStudentId(studentId);
        
        // Date fields
        if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
            try {
                student.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
            } catch (Exception e) {
                logger.warn("Invalid date of birth format: {}", dateOfBirthStr);
            }
        }
        
        if (enrollmentDateStr != null && !enrollmentDateStr.isEmpty()) {
            try {
                student.setEnrollmentDate(LocalDate.parse(enrollmentDateStr));
            } catch (Exception e) {
                logger.warn("Invalid enrollment date format: {}", enrollmentDateStr);
            }
        }
        
        // Grade level
        if (gradeLevelStr != null) {
            Student.GradeLevel gradeLevel = GRADE_LEVEL_MAP.get(gradeLevelStr);
            if (gradeLevel != null) {
                student.setGradeLevel(gradeLevel);
            } else {
                logger.warn("Unknown grade level: {}", gradeLevelStr);
                return null;
            }
        }
        
        // Optional fields
        String email = getAttributeValue(element, "email");
        if (email != null && !email.isEmpty()) {
            student.setEmail(email);
        }
        
        String phone = getAttributeValue(element, "phone");
        if (phone != null && !phone.isEmpty()) {
            student.setPhone(phone);
        }
        
        String gender = getAttributeValue(element, "gender");
        if (gender != null) {
            try {
                student.setGender(Student.Gender.valueOf(gender));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid gender value: {}", gender);
            }
        }
        
        String enrollmentStatus = getAttributeValue(element, "enrollment_status");
        if (enrollmentStatus != null) {
            try {
                student.setEnrollmentStatus(Student.EnrollmentStatus.valueOf(enrollmentStatus));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid enrollment status: {}", enrollmentStatus);
            }
        }
        
        // Emergency contact information
        String emergencyContactName = getAttributeValue(element, "emergency_contact_name");
        if (emergencyContactName != null && !emergencyContactName.isEmpty()) {
            student.setEmergencyContactName(emergencyContactName);
        }
        
        String emergencyContactPhone = getAttributeValue(element, "emergency_contact_phone");
        if (emergencyContactPhone != null && !emergencyContactPhone.isEmpty()) {
            student.setEmergencyContactPhone(emergencyContactPhone);
        }
        
        String emergencyContactRelation = getAttributeValue(element, "emergency_contact_relation");
        if (emergencyContactRelation != null && !emergencyContactRelation.isEmpty()) {
            student.setEmergencyContactRelation(emergencyContactRelation);
        }
        
        // Set default values
        student.markAsActive();
        
        return student;
    }

    /**
     * Get attribute value from element, returning null if not present
     */
    private String getAttributeValue(Element element, String attributeName) {
        if (element.hasAttribute(attributeName)) {
            String value = element.getAttribute(attributeName).trim();
            return value.isEmpty() ? null : value;
        }
        return null;
    }
}
