package krs.erp.initializer;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

import krs.erp.model.ImportHistory;
import krs.erp.model.Parent;
import krs.erp.model.Student;
import krs.erp.model.Grade;
import krs.erp.model.Subject;
import krs.erp.model.Timetable;
import krs.erp.model.Address;
import krs.erp.model.ImportHistory.ImportStatus;
import krs.erp.model.ImportHistory.ImportType;
import krs.erp.repository.ImportHistoryRepository;
import krs.erp.repository.StudentRepository;
import krs.erp.repository.GradeRepository;
import krs.erp.repository.SubjectRepository;
import krs.erp.repository.TimetableRepository;
import krs.erp.repository.ParentRepository;
import krs.erp.repository.AddressRepository;

/**
 * InitializerWithHistory - Enhanced initializer that tracks data population in ImportHistory
 * Respects existing imports and only loads data if not already populated
 * @Order(12) - Executes after all other initializers
 */
@Component
@Order(12)
public class InitializerWithHistory implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitializerWithHistory.class);

    @Autowired
    private ImportHistoryRepository importHistoryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private AddressRepository addressRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting history-aware sample data initialization...");

        try {
            // Load data with history tracking
            loadStudents();
            loadGrades();
            loadSubjects();
            loadTimetables();
            loadParents();
            loadAddresses();

        } catch (Exception e) {
            logger.error("Error during history-aware data initialization", e);
        }
    }

    /**
     * Load students with history tracking
     */
    private void loadStudents() {
        String entityName = "STUDENTS";
        
        // Check if already imported
        if (importHistoryRepository.findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
                entityName, ImportStatus.SUCCESS).isPresent()) {
            logger.info("✓ {} already imported. Skipping...", entityName);
            return;
        }

        logger.info("Loading {} from data/student/sample-students.xml...", entityName);
        
        ImportHistory history = new ImportHistory(
                entityName, 
                ImportType.SAMPLE_DATA, 
                "SYSTEM", 
                "sample-students.xml"
        );
        
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/student/sample-students.xml");
            if (inputStream == null) {
                logger.warn("{} XML file not found. Skipping.", entityName);
                return;
            }

            Document document = parseXml(inputStream);
            NodeList studentList = document.getElementsByTagName("students");
            int loadedCount = 0;

            for (int i = 0; i < studentList.getLength(); i++) {
                Element element = (Element) studentList.item(i);
                Student student = mapToStudent(element);
                studentRepository.save(student);
                loadedCount++;
            }

            history.markAsCompleted(loadedCount);
            importHistoryRepository.save(history);
            logger.info("✓ Loaded {} {}", loadedCount, entityName);

        } catch (Exception e) {
            logger.error("Error loading {}", entityName, e);
            history.markAsFailed("Exception: " + e.getMessage());
            importHistoryRepository.save(history);
        }
    }

    /**
     * Load grades with history tracking
     */
    private void loadGrades() {
        String entityName = "GRADES";
        
        if (importHistoryRepository.findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
                entityName, ImportStatus.SUCCESS).isPresent()) {
            logger.info("✓ {} already imported. Skipping...", entityName);
            return;
        }

        logger.info("Loading {} from data/grade/sample-grades.xml...", entityName);
        
        ImportHistory history = new ImportHistory(
                entityName, 
                ImportType.SAMPLE_DATA, 
                "SYSTEM", 
                "sample-grades.xml"
        );
        
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/grade/sample-grades.xml");
            if (inputStream == null) {
                logger.warn("{} XML file not found. Skipping.", entityName);
                return;
            }

            Document document = parseXml(inputStream);
            NodeList gradeList = document.getElementsByTagName("grades");
            int loadedCount = 0;

            for (int i = 0; i < gradeList.getLength(); i++) {
                Element element = (Element) gradeList.item(i);
                Grade grade = mapToGrade(element);
                gradeRepository.save(grade);
                loadedCount++;
            }

            history.markAsCompleted(loadedCount);
            importHistoryRepository.save(history);
            logger.info("✓ Loaded {} {}", loadedCount, entityName);

        } catch (Exception e) {
            logger.error("Error loading {}", entityName, e);
            history.markAsFailed("Exception: " + e.getMessage());
            importHistoryRepository.save(history);
        }
    }

    /**
     * Load subjects with history tracking
     */
    private void loadSubjects() {
        String entityName = "SUBJECTS";
        
        if (importHistoryRepository.findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
                entityName, ImportStatus.SUCCESS).isPresent()) {
            logger.info("✓ {} already imported. Skipping...", entityName);
            return;
        }

        logger.info("Loading {} from data/subject/sample-subjects.xml...", entityName);
        
        ImportHistory history = new ImportHistory(
                entityName, 
                ImportType.SAMPLE_DATA, 
                "SYSTEM", 
                "sample-subjects.xml"
        );
        
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/subject/sample-subjects.xml");
            if (inputStream == null) {
                logger.warn("{} XML file not found. Skipping.", entityName);
                return;
            }

            Document document = parseXml(inputStream);
            NodeList subjectList = document.getElementsByTagName("subjects");
            int loadedCount = 0;

            for (int i = 0; i < subjectList.getLength(); i++) {
                Element element = (Element) subjectList.item(i);
                Subject subject = mapToSubject(element);
                subjectRepository.save(subject);
                loadedCount++;
            }

            history.markAsCompleted(loadedCount);
            importHistoryRepository.save(history);
            logger.info("✓ Loaded {} {}", loadedCount, entityName);

        } catch (Exception e) {
            logger.error("Error loading {}", entityName, e);
            history.markAsFailed("Exception: " + e.getMessage());
            importHistoryRepository.save(history);
        }
    }

    /**
     * Load timetables with history tracking
     */
    private void loadTimetables() {
        String entityName = "TIMETABLES";
        
        if (importHistoryRepository.findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
                entityName, ImportStatus.SUCCESS).isPresent()) {
            logger.info("✓ {} already imported. Skipping...", entityName);
            return;
        }

        logger.info("Loading {} from data/timetable/sample-timetables.xml...", entityName);
        
        ImportHistory history = new ImportHistory(
                entityName, 
                ImportType.SAMPLE_DATA, 
                "SYSTEM", 
                "sample-timetables.xml"
        );
        
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/timetable/sample-timetables.xml");
            if (inputStream == null) {
                logger.warn("{} XML file not found. Skipping.", entityName);
                return;
            }

            Document document = parseXml(inputStream);
            NodeList timetableList = document.getElementsByTagName("timetables");
            int loadedCount = 0;

            for (int i = 0; i < timetableList.getLength(); i++) {
                Element element = (Element) timetableList.item(i);
                Timetable timetable = mapToTimetable(element);
                timetableRepository.save(timetable);
                loadedCount++;
            }

            history.markAsCompleted(loadedCount);
            importHistoryRepository.save(history);
            logger.info("✓ Loaded {} {}", loadedCount, entityName);

        } catch (Exception e) {
            logger.error("Error loading {}", entityName, e);
            history.markAsFailed("Exception: " + e.getMessage());
            importHistoryRepository.save(history);
        }
    }

    /**
     * Load parents with history tracking
     */
    private void loadParents() {
        String entityName = "PARENTS";
        
        if (importHistoryRepository.findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
                entityName, ImportStatus.SUCCESS).isPresent()) {
            logger.info("✓ {} already imported. Skipping...", entityName);
            return;
        }

        logger.info("Loading {} from data/parent/sample-parents.xml...", entityName);
        
        ImportHistory history = new ImportHistory(
                entityName, 
                ImportType.SAMPLE_DATA, 
                "SYSTEM", 
                "sample-parents.xml"
        );
        
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/parent/sample-parents.xml");
            if (inputStream == null) {
                logger.warn("{} XML file not found. Skipping.", entityName);
                return;
            }

            Document document = parseXml(inputStream);
            NodeList parentList = document.getElementsByTagName("parents");
            int loadedCount = 0;

            for (int i = 0; i < parentList.getLength(); i++) {
                Element element = (Element) parentList.item(i);
                Parent parent = mapToParent(element);
                parentRepository.save(parent);
                loadedCount++;
            }

            history.markAsCompleted(loadedCount);
            importHistoryRepository.save(history);
            logger.info("✓ Loaded {} {}", loadedCount, entityName);

        } catch (Exception e) {
            logger.error("Error loading {}", entityName, e);
            history.markAsFailed("Exception: " + e.getMessage());
            importHistoryRepository.save(history);
        }
    }

    /**
     * Load addresses with history tracking
     */
    private void loadAddresses() {
        String entityName = "ADDRESSES";
        
        if (importHistoryRepository.findFirstByEntityNameAndImportStatusOrderByCreatedAtDesc(
                entityName, ImportStatus.SUCCESS).isPresent()) {
            logger.info("✓ {} already imported. Skipping...", entityName);
            return;
        }

        logger.info("Loading {} from data/address/sample-addresses.xml...", entityName);
        
        ImportHistory history = new ImportHistory(
                entityName, 
                ImportType.SAMPLE_DATA, 
                "SYSTEM", 
                "sample-addresses.xml"
        );
        
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/address/sample-addresses.xml");
            if (inputStream == null) {
                logger.warn("{} XML file not found. Skipping.", entityName);
                return;
            }

            Document document = parseXml(inputStream);
            NodeList addressList = document.getElementsByTagName("addresses");
            int loadedCount = 0;

            for (int i = 0; i < addressList.getLength(); i++) {
                Element element = (Element) addressList.item(i);
                Address address = mapToAddress(element);
                addressRepository.save(address);
                loadedCount++;
            }

            history.markAsCompleted(loadedCount);
            importHistoryRepository.save(history);
            logger.info("✓ Loaded {} {}", loadedCount, entityName);

        } catch (Exception e) {
            logger.error("Error loading {}", entityName, e);
            history.markAsFailed("Exception: " + e.getMessage());
            importHistoryRepository.save(history);
        }
    }

    private Document parseXml(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();
        return document;
    }

    private Student mapToStudent(Element element) {
        Student student = new Student();
        student.setFirstName(element.getAttribute("first_name"));
        student.setLastName(element.getAttribute("last_name"));
        student.setMiddleName(element.getAttribute("middle_name"));
        student.setStudentId(element.getAttribute("student_id"));
        student.setEmail(element.getAttribute("email"));
        student.setPhone(element.getAttribute("phone"));
        
        String dob = element.getAttribute("date_of_birth");
        if (!dob.isEmpty()) {
            try {
                student.setDateOfBirth(LocalDate.parse(dob, DATE_FORMATTER));
            } catch (Exception e) {
                logger.warn("Invalid date format for student: {}", dob);
            }
        }
        
        String enrollmentDate = element.getAttribute("enrollment_date");
        if (!enrollmentDate.isEmpty()) {
            try {
                student.setEnrollmentDate(LocalDate.parse(enrollmentDate, DATE_FORMATTER));
            } catch (Exception e) {
                logger.warn("Invalid enrollment date: {}", enrollmentDate);
            }
        }
        
        student.setGradeLevel(element.getAttribute("grade_level"));
        student.markAsActive();
        return student;
    }

    private Grade mapToGrade(Element element) {
        Grade grade = new Grade();
        grade.setStudentName(element.getAttribute("student_name"));
        grade.setGradeLevel(element.getAttribute("grade_level"));
        grade.setCourseCode(element.getAttribute("course_code"));
        grade.setCourseName(element.getAttribute("course_name"));
        grade.setExamType(element.getAttribute("exam_type"));
        grade.setMarksObtained(Double.parseDouble(element.getAttribute("marks_obtained")));
        grade.setTotalMarks(Double.parseDouble(element.getAttribute("total_marks")));
        grade.setLetterGrade(element.getAttribute("letter_grade"));
        grade.setSemester(element.getAttribute("semester"));
        grade.setAcademicYear(element.getAttribute("academic_year"));
        grade.markAsActive();
        return grade;
    }

    private Subject mapToSubject(Element element) {
        Subject subject = new Subject();
        subject.setSubjectCode(element.getAttribute("subject_code"));
        subject.setSubjectName(element.getAttribute("subject_name"));
        subject.setDescription(element.getAttribute("description"));
        subject.setGradeLevel(element.getAttribute("grade_level"));
        subject.setCategory(element.getAttribute("category"));
        subject.markAsActive();
        return subject;
    }

    private Timetable mapToTimetable(Element element) {
        Timetable timetable = new Timetable();
        timetable.setTimetableCode(element.getAttribute("timetable_code"));
        timetable.setClassName(element.getAttribute("class_name"));
        timetable.setGradeLevel(element.getAttribute("grade_level"));
        timetable.setAcademicYear(element.getAttribute("academic_year"));
        timetable.setDayOfWeek(element.getAttribute("day_of_week"));
        timetable.setSubjectName(element.getAttribute("subject_name"));
        timetable.setTeacherName(element.getAttribute("teacher_name"));
        timetable.markAsActive();
        return timetable;
    }

    private Parent mapToParent(Element element) {
        Parent parent = new Parent();
        parent.setFirstName(element.getAttribute("first_name"));
        parent.setLastName(element.getAttribute("last_name"));
        parent.setMiddleName(element.getAttribute("middle_name"));
        parent.setEmail(element.getAttribute("email"));
        parent.setPhone(element.getAttribute("phone"));
        parent.setAlternatePhone(element.getAttribute("alternate_phone"));
        
        String genderStr = element.getAttribute("gender");
        if (genderStr != null && !genderStr.isEmpty()) {
            try {
                parent.setGender(Parent.Gender.valueOf(genderStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                parent.setGender(Parent.Gender.OTHER);
            }
        }
        
        parent.setOccupation(element.getAttribute("occupation"));
        parent.setWorkplace(element.getAttribute("workplace"));
        parent.setWorkPhone(element.getAttribute("work_phone"));
        parent.markAsActive();
        return parent;
    }

    private Address mapToAddress(Element element) {
        Address address = new Address();
        address.setEntityType(element.getAttribute("entity_type"));
        address.setEntityId(Long.parseLong(element.getAttribute("entity_id")));
        address.setAddressLine1(element.getAttribute("address_line1"));
        address.setAddressLine2(element.getAttribute("address_line2"));
        address.setCity(element.getAttribute("city"));
        address.setState(element.getAttribute("state"));
        address.setPostalCode(element.getAttribute("postal_code"));
        address.setCountry(element.getAttribute("country"));
        address.markAsActive();
        return address;
    }
}
