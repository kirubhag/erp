package krs.erp.initializer;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import krs.erp.model.Grade;
import krs.erp.repository.GradeRepository;

/**
 * Initializes Grade data from grades.xml on application startup
 */
// @Component  // Disabled - schema mismatch issues
@Order(7)
public class GradeDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GradeDataInitializer.class);

    @Autowired
    private GradeRepository gradeRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Grade data initialization...");

        try {
            // Load the XML file from classpath
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/grade/grades.xml");
            if (inputStream == null) {
                logger.warn("grades.xml not found in classpath. Skipping Grade initialization.");
                return;
            }

            // Parse the XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Get all grade nodes
            NodeList gradeList = document.getElementsByTagName("grade");
            int totalGrades = gradeList.getLength();
            int loadedGrades = 0;
            int skippedGrades = 0;

            Set<String> processedKeys = new HashSet<>();

            logger.info("Found {} grades in XML file", totalGrades);

            // Process each grade
            for (int i = 0; i < totalGrades; i++) {
                Node gradeNode = gradeList.item(i);

                if (gradeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element gradeElement = (Element) gradeNode;

                    try {
                        // Extract grade data
                        Long studentId = Long.parseLong(getTextContent(gradeElement, "studentId"));
                        String courseCode = getTextContent(gradeElement, "courseCode");
                        String examType = getTextContent(gradeElement, "examType");
                        String semester = getTextContent(gradeElement, "semester");

                        // Create unique key for duplicate checking
                        String uniqueKey = studentId + "_" + courseCode + "_" + examType + "_" + semester;

                        // Check if this grade already exists (either in DB or already processed)
                        if (processedKeys.contains(uniqueKey) ||
                            gradeRepository.existsByStudentIdAndCourseCodeAndExamTypeAndSemester(
                                    studentId, courseCode, examType, semester)) {
                            logger.debug("Grade already exists: Student {}, Course {}, Exam {}, Semester {}. Skipping.",
                                    studentId, courseCode, examType, semester);
                            skippedGrades++;
                            continue;
                        }

                        // Create new Grade entity
                        Grade grade = new Grade();
                        grade.setStudentId(studentId);
                        grade.setStudentName(getTextContent(gradeElement, "studentName"));
                        grade.setGradeLevel(getTextContent(gradeElement, "gradeLevel"));
                        grade.setCourseCode(courseCode);
                        grade.setCourseName(getTextContent(gradeElement, "courseName"));
                        grade.setExamType(examType);

                        // Parse marks (BigDecimal)
                        String marksObtained = getTextContent(gradeElement, "marksObtained");
                        String totalMarks = getTextContent(gradeElement, "totalMarks");
                        if (marksObtained != null && !marksObtained.isEmpty()) {
                            grade.setMarksObtained(new BigDecimal(marksObtained));
                        }
                        if (totalMarks != null && !totalMarks.isEmpty()) {
                            grade.setTotalMarks(new BigDecimal(totalMarks));
                        }

                        // Parse exam date
                        String examDate = getTextContent(gradeElement, "examDate");
                        if (examDate != null && !examDate.isEmpty()) {
                            grade.setExamDate(LocalDate.parse(examDate));
                        }

                        grade.setSemester(semester);
                        grade.setAcademicYear(getTextContent(gradeElement, "academicYear"));
                        grade.setRemarks(getTextContent(gradeElement, "remarks"));
                        grade.setTeacherId(getTextContent(gradeElement, "teacherId"));
                        grade.setTeacherName(getTextContent(gradeElement, "teacherName"));

                        // Parse organization ID
                        String organizationId = getTextContent(gradeElement, "organizationId");
                        if (organizationId != null && !organizationId.isEmpty()) {
                            grade.setOrganizationId(Long.parseLong(organizationId));
                        }

                        // Mark as active
                        grade.markAsActive();

                        // Save to database
                        gradeRepository.save(grade);
                        processedKeys.add(uniqueKey);
                        loadedGrades++;

                        if (loadedGrades % 10 == 0) {
                            logger.info("Loaded {} grades...", loadedGrades);
                        }

                    } catch (Exception e) {
                        logger.error("Error processing grade at index {}: {}", i, e.getMessage(), e);
                    }
                }
            }

            logger.info("Grade data initialization completed. Loaded: {}, Skipped: {}, Total: {}",
                    loadedGrades, skippedGrades, totalGrades);

        } catch (Exception e) {
            logger.error("Error initializing Grade data: {}", e.getMessage(), e);
        }
    }

    /**
     * Helper method to safely extract text content from XML element
     */
    private String getTextContent(Element element, String tagName) {
        try {
            NodeList nodeList = element.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node != null && node.getFirstChild() != null) {
                    return node.getFirstChild().getNodeValue();
                }
            }
        } catch (Exception e) {
            logger.debug("Could not extract text content for tag: {}", tagName);
        }
        return null;
    }
}
