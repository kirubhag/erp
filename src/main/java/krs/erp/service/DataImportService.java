package krs.erp.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.enums.AccountType;
import krs.erp.model.Address;
import krs.erp.model.Attendance;
import krs.erp.model.Course;
import krs.erp.model.ErpClass;
import krs.erp.model.Exam;
import krs.erp.model.Grade;
import krs.erp.model.HealthRecord;
import krs.erp.model.Organization;
import krs.erp.model.Parent;
import krs.erp.model.ParentStudentRelation;
import krs.erp.model.Permission;
import krs.erp.model.Role;
import krs.erp.model.Room;
import krs.erp.model.Room.RoomType;
import krs.erp.model.Staff;
import krs.erp.model.Student;
import krs.erp.model.Subject;
import krs.erp.model.Timetable;
import krs.erp.model.Timetable.DayOfWeek;
import krs.erp.model.User;
import krs.erp.model.academic.AcademicYear;
import krs.erp.model.admission.StudentRegistration;
import krs.erp.model.calendar.CalendarDay;
import krs.erp.model.communication.Announcement;
import krs.erp.model.communication.Message;
import krs.erp.model.communication.SupportTicket;
import krs.erp.model.communication.TicketComment;
import krs.erp.config.CustomUserDetails;
import krs.erp.model.finance.AccountingPeriod;
import krs.erp.model.finance.BankStatement;
import krs.erp.model.finance.BankStatementLine;
import krs.erp.model.finance.Budget;
import krs.erp.model.finance.BudgetLine;
import krs.erp.model.finance.ChartOfAccount;
import krs.erp.model.finance.DisciplinaryIncident;
import krs.erp.model.finance.FineConfiguration;
import krs.erp.model.finance.FineWaiverRequest;
import krs.erp.model.finance.InvoiceItem;
import krs.erp.model.finance.JournalEntry;
import krs.erp.model.finance.JournalItem;
import krs.erp.model.finance.ScholarshipApplication;
import krs.erp.model.finance.ScholarshipCategory;
import krs.erp.model.finance.StudentFineLedger;
import krs.erp.model.hr.Department;
import krs.erp.model.hr.Designation;
import krs.erp.model.hr.Payslip;
import krs.erp.model.hr.PerformanceCriteria;
import krs.erp.model.hr.PerformanceCycle;
import krs.erp.model.hr.PerformanceReview;
import krs.erp.model.hr.PerformanceReviewDetail;
import krs.erp.model.inventory.Asset;
import krs.erp.model.inventory.Consumable;
import krs.erp.model.inventory.PurchaseOrder;
import krs.erp.model.inventory.Vendor;
import krs.erp.model.library.Author;
import krs.erp.model.library.LibraryPolicy;
import krs.erp.model.library.LibraryPurchaseRequest;
import krs.erp.model.library.LibraryResource;
import krs.erp.model.library.Publisher;
import krs.erp.model.library.ResourceItem;
import krs.erp.model.lms.Lesson;
import krs.erp.model.lms.LmsAnswer;
import krs.erp.model.lms.LmsBadge;
import krs.erp.model.lms.LmsContent;
import krs.erp.model.lms.LmsForum;
import krs.erp.model.lms.LmsForumPost;
import krs.erp.model.lms.LmsModule;
import krs.erp.model.lms.LmsPeerReview;
import krs.erp.model.lms.LmsPointLog;
import krs.erp.model.lms.LmsQuestion;
import krs.erp.model.lms.LmsQuestionBank;
import krs.erp.model.lms.LmsQuiz;
import krs.erp.model.lms.LmsRubric;
import krs.erp.model.lms.LmsStudentProgress;
import krs.erp.model.lms.LmsSubmission;
import krs.erp.model.lms.LmsTopic;
import krs.erp.model.lms.VirtualAttendanceRecord;
import krs.erp.model.lms.VirtualClassSession;
import krs.erp.model.maintenance.Facility;
import krs.erp.model.maintenance.FacilityBooking;
import krs.erp.model.maintenance.WorkOrder;
import krs.erp.model.reporting.MISReport;
import krs.erp.model.tpd.Competency;
import krs.erp.model.tpd.CpdLedger;
import krs.erp.model.tpd.ProfessionalPortfolio;
import krs.erp.model.tpd.SkillAssessment;
import krs.erp.model.tpd.TrainingEvent;
import krs.erp.repository.AddressRepository;
import krs.erp.repository.AttendanceRepository;
import krs.erp.repository.CourseRepository;
import krs.erp.repository.ErpClassRepository;
import krs.erp.repository.ExamRepository;
import krs.erp.repository.GradeRepository;
import krs.erp.repository.HealthRecordRepository;
import krs.erp.repository.OrganizationRepository;
import krs.erp.repository.ParentRepository;
import krs.erp.repository.ParentStudentRelationRepository;
import krs.erp.repository.PermissionRepository;
import krs.erp.repository.RoleRepository;
import krs.erp.repository.RoomRepository;
import krs.erp.repository.StaffRepository;
import krs.erp.repository.StudentRepository;
import krs.erp.repository.SubjectRepository;
import krs.erp.repository.TimetableRepository;
import krs.erp.repository.UserRepository;
import krs.erp.repository.academic.AcademicYearRepository;
import krs.erp.repository.admission.StudentRegistrationRepository;
import krs.erp.repository.calendar.CalendarDayRepository;
import krs.erp.repository.communication.AnnouncementRepository;
import krs.erp.repository.communication.MessageRepository;
import krs.erp.repository.communication.SupportTicketRepository;
import krs.erp.repository.communication.TicketCommentRepository;
import krs.erp.repository.finance.BankStatementRepository;
import krs.erp.repository.finance.BudgetLineRepository;
import krs.erp.repository.finance.BudgetRepository;
import krs.erp.repository.hr.DepartmentRepository;
import krs.erp.repository.hr.DesignationRepository;
import krs.erp.repository.hr.PayslipRepository;
import krs.erp.repository.library.AuthorRepository;
import krs.erp.repository.library.LibraryHoldRepository;
import krs.erp.repository.library.LibraryLoanRepository;
import krs.erp.repository.library.LibraryPORepository;
import krs.erp.repository.library.LibraryPolicyRepository;
import krs.erp.repository.library.LibraryPurchaseRequestRepository;
import krs.erp.repository.library.LibraryResourceRepository;
import krs.erp.repository.library.PublisherRepository;
import krs.erp.repository.library.ResourceItemRepository;
import krs.erp.repository.lms.LessonRepository;
import krs.erp.repository.lms.LmsAnswerRepository;
import krs.erp.repository.lms.LmsBadgeRepository;
import krs.erp.repository.lms.LmsContentRepository;
import krs.erp.repository.lms.LmsForumPostRepository;
import krs.erp.repository.lms.LmsForumRepository;
import krs.erp.repository.lms.LmsModuleRepository;
import krs.erp.repository.lms.LmsPeerReviewRepository;
import krs.erp.repository.lms.LmsPointLogRepository;
import krs.erp.repository.lms.LmsQuestionBankRepository;
import krs.erp.repository.lms.LmsQuestionRepository;
import krs.erp.repository.lms.LmsQuizRepository;
import krs.erp.repository.lms.LmsRubricRepository;
import krs.erp.repository.lms.LmsStudentProgressRepository;
import krs.erp.repository.lms.LmsSubmissionRepository;
import krs.erp.repository.lms.LmsTopicRepository;
import krs.erp.repository.lms.VirtualAttendanceRecordRepository;
import krs.erp.repository.lms.VirtualClassSessionRepository;
import krs.erp.repository.tpd.CompetencyRepository;
import krs.erp.repository.tpd.CpdLedgerRepository;
import krs.erp.repository.tpd.EvidenceRepository;
import krs.erp.repository.tpd.ProfessionalPortfolioRepository;
import krs.erp.repository.tpd.SkillAssessmentRepository;
import krs.erp.repository.tpd.TrainingAttendanceRepository;
import krs.erp.repository.tpd.TrainingEvaluationRepository;
import krs.erp.repository.tpd.TrainingEventRepository;

@Service
@SuppressWarnings({ "unused", "UnnecessaryLocalVariable" })
public class DataImportService {

    private static final Logger logger = LoggerFactory.getLogger(DataImportService.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ParentStudentRelationRepository parentStudentRelationRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private ErpClassRepository classRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExamRepository examRepository;

    // Cache for entities to avoid repeated lookups

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private LibraryResourceRepository resourceRepository;

    @Autowired
    private ResourceItemRepository itemRepository;

    @Autowired
    private LibraryLoanRepository loanRepository;

    @Autowired
    private LibraryHoldRepository holdRepository;

    @Autowired
    private LibraryPolicyRepository policyRepository;

    @Autowired
    private LibraryPurchaseRequestRepository purchaseRequestRepository;

    @Autowired
    private LibraryPORepository poRepository;

    @Autowired
    private LmsModuleRepository lmsModuleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LmsTopicRepository lmsTopicRepository;

    @Autowired
    private LmsContentRepository lmsContentRepository;

    @Autowired
    private LmsQuizRepository lmsQuizRepository;

    @Autowired
    private LmsQuestionRepository lmsQuestionRepository;

    @Autowired
    private LmsAnswerRepository lmsAnswerRepository;

    @Autowired
    private LmsSubmissionRepository lmsSubmissionRepository;

    @Autowired
    private LmsQuestionBankRepository lmsQuestionBankRepository;

    @Autowired
    private LmsRubricRepository lmsRubricRepository;

    @Autowired
    private VirtualClassSessionRepository virtualClassSessionRepository;

    @Autowired
    private VirtualAttendanceRecordRepository virtualAttendanceRecordRepository;

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private CompetencyRepository competencyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private CalendarDayRepository calendarDayRepository;

    @Autowired
    private SkillAssessmentRepository skillAssessmentRepository;

    @Autowired
    private TrainingEventRepository trainingEventRepository;

    @Autowired
    private TrainingAttendanceRepository trainingAttendanceRepository;

    @Autowired
    private CpdLedgerRepository cpdLedgerRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private TrainingEvaluationRepository trainingEvaluationRepository;

    @Autowired
    private ProfessionalPortfolioRepository professionalPortfolioRepository;

    @Autowired
    private LmsStudentProgressRepository lmsStudentProgressRepository;

    @Autowired
    private LmsBadgeRepository lmsBadgeRepository;

    @Autowired
    private LmsPointLogRepository lmsPointLogRepository;

    @Autowired
    private LmsForumRepository lmsForumRepository;

    @Autowired
    private LmsForumPostRepository lmsForumPostRepository;

    @Autowired
    private LmsPeerReviewRepository lmsPeerReviewRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BudgetLineRepository budgetLineRepository;

    @Autowired
    private BankStatementRepository bankStatementRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    @Autowired
    private StudentRegistrationRepository studentRegistrationRepository;

    // Cache for loaded entities
    private final Map<Long, Permission> permissions = new HashMap<>();
    private final Map<Long, Role> roles = new HashMap<>();
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Staff> staffMembers = new HashMap<>();
    private final Map<Long, Parent> parents = new HashMap<>();
    private final Map<Long, Student> students = new HashMap<>();
    private final Map<Long, Address> addresses = new HashMap<>();
    private final Map<String, Subject> subjects = new HashMap<>();

    @Transactional
    public void importDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting data import from XML file: {}", xmlFilePath);

            // Clear caches
            clearCaches();

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import in dependency order
            importPermissions(document);
            importRoles(document);
            importRolePermissions(document);
            importUsers(document);
            importUserRoles(document);
            importAddresses(document); // Import addresses first before entities that reference them
            importStaff(document);
            importParents(document);
            importStudents(document);
            importParentStudentRelations(document);
            importSubjects(document);
            importAttendance(document);
            importCourses(document);
            importExams(document);
            importHealthRecords(document);
            importStudentRegistrations(document);

            logger.info("Data import completed successfully");

        } catch (Exception e) {
            logger.error("Error importing data from XML", e);
            throw new RuntimeException("Failed to import data from XML", e);
        }
    }

    @Transactional
    public void importAddressesDataFromXml(String xmlFilePath) {
        try {
            logger.debug("Starting address data import from XML file: {}", xmlFilePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import only addresses from the XML file
            importAddresses(document);

            logger.info("Address data import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing address data from XML: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to import address data from XML", e);
        }
    }

    @Transactional
    public void importStudentDataFromXml(String xmlFilePath) {
        try {
            logger.debug("Starting student data import from XML file: {}", xmlFilePath);

            // Load existing addresses from database into cache (students reference
            // addresses)
            loadAddressesIntoCache();

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import only students from this file
            importStudents(document);

            logger.debug("Student data import completed successfully for: {}", xmlFilePath);

        } catch (Exception e) {
            logger.error("Error importing student data from XML file: {}", xmlFilePath, e);
            throw new RuntimeException("Failed to import student data from XML: " + xmlFilePath, e);
        }
    }

    @Transactional
    public void importStaffDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting staff data import from XML file: {}", xmlFilePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import only staff from this file
            importStaff(document);

            logger.info("Staff data import completed successfully for: {}", xmlFilePath);

        } catch (Exception e) {
            logger.error("Error importing staff data from XML file: {}", xmlFilePath, e);
            throw new RuntimeException("Failed to import staff data from XML: " + xmlFilePath, e);
        }
    }

    @Transactional
    public void importParentsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting parents data import from XML file: {}", xmlFilePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import only parents from this file
            importParents(document);

            logger.info("Parents data import completed successfully for: {}", xmlFilePath);

        } catch (Exception e) {
            logger.error("Error importing parents data from XML file: {}", xmlFilePath, e);
            throw new RuntimeException("Failed to import parents data from XML: " + xmlFilePath, e);
        }
    }

    @Transactional
    public void importSubjectsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting subjects data import from XML file: {}", xmlFilePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import only subjects from this file
            importSubjects(document);

            logger.info("Subjects data import completed successfully for: {}", xmlFilePath);

        } catch (Exception e) {
            logger.error("Error importing subjects data from XML file: {}", xmlFilePath, e);
            throw new RuntimeException("Failed to import subjects data from XML: " + xmlFilePath, e);
        }
    }

    @Transactional
    public void importGradesDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting grades data import from XML file: {}", xmlFilePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import grades from this file
            importGrades(document);

            logger.info("Grades data import completed successfully for: {}", xmlFilePath);

        } catch (Exception e) {
            logger.error("Error importing grades data from XML file: {}", xmlFilePath, e);
            throw new RuntimeException("Failed to import grades data from XML: " + xmlFilePath, e);
        }
    }

    @Transactional
    public void importClassesDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting classes data import from XML file: {}", xmlFilePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import classes from this file
            importClasses(document);

            logger.info("Classes data import completed successfully for: {}", xmlFilePath);

        } catch (Exception e) {
            logger.error("Error importing classes data from XML file: {}", xmlFilePath, e);
            throw new RuntimeException("Failed to import classes data from XML: " + xmlFilePath, e);
        }
    }

    @Transactional
    public void importRoomsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting rooms data import from XML file: {}", xmlFilePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import rooms from this file
            importRooms(document);

            logger.info("Rooms data import completed successfully for: {}", xmlFilePath);

        } catch (Exception e) {
            logger.error("Error importing rooms data from XML file: {}", xmlFilePath, e);
            throw new RuntimeException("Failed to import rooms data from XML: " + xmlFilePath, e);
        }
    }

    @Transactional
    public void importTimetablesDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting timetables data import from XML file: {}", xmlFilePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import timetables from this file
            importTimetables(document);

            logger.info("Timetables data import completed successfully for: {}", xmlFilePath);

        } catch (Exception e) {
            logger.error("Error importing timetables data from XML file: {}", xmlFilePath, e);
            throw new RuntimeException("Failed to import timetables data from XML: " + xmlFilePath, e);
        }
    }

    private void importClasses(Document document) {
        NodeList classNodes = document.getElementsByTagName("class");
        int totalClasses = classNodes.getLength();
        int loadedClasses = 0;
        int skippedClasses = 0;

        logger.info("Found {} classes in XML file", totalClasses);

        for (int i = 0; i < totalClasses; i++) {
            Element classElement = (Element) classNodes.item(i);

            try {
                String classCode = getElementText(classElement, "classCode");
                String gradeLevel = getElementText(classElement, "gradeLevel");
                String section = getElementText(classElement, "section");
                String academicYear = getElementText(classElement, "academicYear");

                // Check if class already exists
                if (classRepository.existsByGradeLevelAndSectionAndAcademicYear(
                        gradeLevel, section, academicYear)) {
                    skippedClasses++;
                    continue;
                }

                ErpClass erpClass = new ErpClass();
                erpClass.setClassCode(classCode);
                erpClass.setClassName(getElementText(classElement, "className"));
                erpClass.setGradeLevel(gradeLevel);
                erpClass.setSection(section);
                erpClass.setAcademicYear(academicYear);

                String capacityStr = getElementText(classElement, "capacity");
                if (capacityStr != null && !capacityStr.isEmpty()) {
                    erpClass.setCapacity(Integer.parseInt(capacityStr));
                }

                erpClass.setRoomNumber(getElementText(classElement, "roomNumber"));
                erpClass.setDescription(getElementText(classElement, "description"));

                String isActiveStr = getElementText(classElement, "isActive");
                if ("true".equalsIgnoreCase(isActiveStr)) {
                    erpClass.markAsActive();
                } else {
                    erpClass.markAsDeleted();
                }

                classRepository.save(erpClass);
                loadedClasses++;

            } catch (Exception e) {
                logger.error("Error processing class at index {}: {}", i, e.getMessage());
            }
        }

        logger.info("Class import completed. Loaded: {}, Skipped: {}, Total: {}",
                loadedClasses, skippedClasses, totalClasses);
    }

    private void importGrades(Document document) {
        NodeList gradeNodes = document.getElementsByTagName("grade");
        int totalGrades = gradeNodes.getLength();
        int loadedGrades = 0;
        int skippedGrades = 0;

        logger.info("Found {} grades in XML file", totalGrades);

        for (int i = 0; i < totalGrades; i++) {
            Element gradeElement = (Element) gradeNodes.item(i);

            try {
                Long studentId = Long.parseLong(getElementText(gradeElement, "studentId"));
                String courseCode = getElementText(gradeElement, "courseCode");
                String examType = getElementText(gradeElement, "examType");
                String semester = getElementText(gradeElement, "semester");

                // Look up Student entity from cache or repository
                Student student = students.get(studentId);
                if (student == null) {
                    student = studentRepository.findById(studentId).orElse(null);
                }

                // Fallback: Try looking up by student code (string ID) if provided
                if (student == null) {
                    String studentCode = getElementText(gradeElement, "studentCode");
                    if (studentCode != null && !studentCode.isEmpty()) {
                        student = studentRepository.findByStudentId(studentCode).orElse(null);
                    }
                }

                if (student != null) { // Cache found student
                    students.put(studentId, student);
                }

                // Look up Subject entity from cache or repository
                Subject subject = subjects.get(courseCode);
                if (subject == null) {
                    subject = subjectRepository.findBySubjectCode(courseCode).orElse(null);
                    if (subject != null) {
                        subjects.put(courseCode, subject);
                    }
                }

                // Skip if student or subject not found
                if (student == null || subject == null) {
                    logger.warn("Skipping grade - student {} or subject {} not found", studentId, courseCode);
                    skippedGrades++;
                    continue;
                }

                // Check if grade already exists
                if (gradeRepository.existsByStudentAndSubjectAndExamTypeAndSemester(
                        student, subject, examType, semester)) {
                    skippedGrades++;
                    continue;
                }

                Grade grade = new Grade();
                grade.setStudent(student);
                grade.setSubject(subject);
                grade.setExamType(examType);

                String marksObtained = getElementText(gradeElement, "marksObtained");
                if (marksObtained != null && !marksObtained.isEmpty()) {
                    grade.setMarksObtained(new java.math.BigDecimal(marksObtained));
                }

                String totalMarks = getElementText(gradeElement, "totalMarks");
                if (totalMarks != null && !totalMarks.isEmpty()) {
                    grade.setTotalMarks(new java.math.BigDecimal(totalMarks));
                }

                String examDate = getElementText(gradeElement, "examDate");
                if (examDate != null && !examDate.isEmpty()) {
                    grade.setExamDate(LocalDate.parse(examDate));
                }

                grade.setSemester(semester);
                grade.setAcademicYear(getElementText(gradeElement, "academicYear"));
                grade.setRemarks(getElementText(gradeElement, "remarks"));

                // Look up Teacher (Staff) entity
                String teacherIdStr = getElementText(gradeElement, "teacherId");
                if (teacherIdStr != null && !teacherIdStr.isEmpty()) {
                    Long teacherId = Long.parseLong(teacherIdStr);
                    Staff teacher = staffMembers.get(teacherId);
                    if (teacher == null) {
                        teacher = staffRepository.findById(teacherId).orElse(null);
                        if (teacher != null) {
                            staffMembers.put(teacherId, teacher);
                        }
                    }
                    grade.setTeacher(teacher);
                }

                String organizationId = getElementText(gradeElement, "organizationId");
                if (organizationId != null && !organizationId.isEmpty()) {
                    grade.setOrganizationId(Long.parseLong(organizationId));
                }

                grade.markAsActive();
                gradeRepository.save(grade);
                loadedGrades++;

            } catch (Exception e) {
                logger.error("Error processing grade at index {}: {}", i, e.getMessage());
            }
        }

        logger.info("Grade import completed. Loaded: {}, Skipped: {}, Total: {}",
                loadedGrades, skippedGrades, totalGrades);
    }

    private void importRooms(Document document) {
        NodeList roomNodes = document.getElementsByTagName("room");
        logger.info("Importing {} rooms", roomNodes.getLength());

        for (int i = 0; i < roomNodes.getLength(); i++) {
            Element element = (Element) roomNodes.item(i);

            String roomName = getElementText(element, "roomName");
            if (roomRepository.findByRoomName(roomName) != null) {
                continue; // Skip existing
            }

            Room room = new Room();
            room.setRoomName(roomName);

            String capacityStr = getElementText(element, "capacity");
            if (capacityStr != null) {
                room.setCapacity(Integer.parseInt(capacityStr));
            }

            String roomTypeStr = getElementText(element, "roomType");
            if (roomTypeStr != null) {
                try {
                    room.setRoomType(RoomType.valueOf(roomTypeStr));
                } catch (Exception e) {
                    logger.warn("Invalid room type: {}", roomTypeStr);
                }
            }

            room.setBuilding(getElementText(element, "building"));
            room.setDescription(getElementText(element, "description"));

            String isActive = getElementText(element, "isActive");
            if ("true".equalsIgnoreCase(isActive)) {
                room.markAsActive();
            } else {
                room.markAsDeleted();
            }

            roomRepository.save(room);
        }
    }

    private void importTimetables(Document document) {
        NodeList timetableNodes = document.getElementsByTagName("timetable");
        logger.info("Importing {} timetables", timetableNodes.getLength());

        for (int i = 0; i < timetableNodes.getLength(); i++) {
            Element tElement = (Element) timetableNodes.item(i);

            try {
                String classCode = getElementText(tElement, "classCode");
                String subjectCode = getElementText(tElement, "subjectCode");
                String teacherId = getElementText(tElement, "teacherId");
                String roomName = getElementText(tElement, "roomName");

                // Validate required lookups
                ErpClass erpClass = classRepository.findByClassCodeAndIsActive(classCode, 1).orElse(null);
                Subject subject = subjectRepository.findBySubjectCode(subjectCode).orElse(null);
                Staff teacher = staffRepository.findByStaffId(teacherId).orElse(null);
                Room room = roomRepository.findByRoomName(roomName);

                if (erpClass == null || subject == null || teacher == null || room == null) {
                    logger.warn(
                            "Skipping timetable entry due to missing dependencies: Class={}, Subject={}, Teacher={}, Room={}",
                            classCode, subjectCode, teacherId, roomName);
                    continue;
                }

                Timetable timetable = new Timetable();
                timetable.setAcademicYear(getElementText(tElement, "academicYear"));

                String dayOfWeekStr = getElementText(tElement, "dayOfWeek");
                if (dayOfWeekStr != null) {
                    timetable.setDayOfWeek(DayOfWeek.valueOf(dayOfWeekStr));
                }

                String startTimeStr = getElementText(tElement, "startTime");
                if (startTimeStr != null) {
                    timetable.setStartTime(LocalTime.parse(startTimeStr));
                }

                String endTimeStr = getElementText(tElement, "endTime");
                if (endTimeStr != null) {
                    timetable.setEndTime(LocalTime.parse(endTimeStr));
                }

                timetable.setDescription(getElementText(tElement, "description"));
                timetable.setErpClass(erpClass);
                timetable.setSubject(subject);
                timetable.setTeacher(teacher);
                timetable.setRoom(room);

                String periodNumStr = getElementText(tElement, "periodNumber");
                if (periodNumStr != null && !periodNumStr.isEmpty()) {
                    timetable.setPeriodNumber(Integer.parseInt(periodNumStr));
                }

                timetable.markAsActive();
                timetableRepository.save(timetable);

            } catch (Exception e) {
                logger.error("Error importing timetable entry", e);
            }
        }
    }

    private void clearCaches() {
        permissions.clear();
        roles.clear();
        users.clear();
        staffMembers.clear();
        parents.clear();
        students.clear();
        addresses.clear();
        subjects.clear();
    }

    /**
     * Load existing addresses from database into cache.
     * This is needed when importing students in standalone mode (not via
     * importDataFromXml).
     */
    private void loadAddressesIntoCache() {
        if (addresses.isEmpty()) {
            List<Address> allAddresses = addressRepository.findAll();
            logger.info("Loading {} addresses into cache for student import", allAddresses.size());
            for (Address address : allAddresses) {
                if (address.getId() != null) {
                    addresses.put(address.getId(), address);
                }
            }
        }
    }

    @Transactional
    public void importPayslipsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting Payslip data import from XML file: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList payslipList = document.getElementsByTagName("payslip");
            logger.info("Found {} payslips in XML", payslipList.getLength());

            for (int i = 0; i < payslipList.getLength(); i++) {
                Element element = (Element) payslipList.item(i);
                Payslip payslip = new Payslip();
                payslip.setPayrollRunId(Long.parseLong(element.getAttribute("payroll_run_id")));
                payslip.setStaffId(Long.parseLong(element.getAttribute("staff_id")));
                payslip.setStaffName(element.getAttribute("staff_name"));
                payslip.setDepartment(element.getAttribute("department"));
                payslip.setBasicSalary(Double.parseDouble(element.getAttribute("basic_salary")));
                payslip.setHra(Double.parseDouble(element.getAttribute("hra")));
                payslip.setDa(Double.parseDouble(element.getAttribute("da")));
                payslip.setAllowances(Double.parseDouble(element.getAttribute("allowances")));
                payslip.setPfDeduction(Double.parseDouble(element.getAttribute("pf_deduction")));
                payslip.setTaxDeduction(Double.parseDouble(element.getAttribute("tax_deduction")));
                payslip.setOtherDeductions(Double.parseDouble(element.getAttribute("other_deductions")));
                payslip.setGrossSalary(Double.parseDouble(element.getAttribute("gross_salary")));
                payslip.setTotalDeductions(Double.parseDouble(element.getAttribute("total_deductions")));
                payslip.setNetSalary(Double.parseDouble(element.getAttribute("net_salary")));

                payslipRepository.save(payslip);
            }
            logger.info("Payslips loaded successfully");
        } catch (Exception e) {
            logger.error("Error importing payslips", e);
            throw new RuntimeException("Failed to import payslips", e);
        }
    }

    @Transactional
    public void importCompetenciesDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting Competency data import from XML file: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList competencyList = document.getElementsByTagName("competency");
            logger.info("Found {} competencies in XML", competencyList.getLength());

            for (int i = 0; i < competencyList.getLength(); i++) {
                Element element = (Element) competencyList.item(i);

                // Check if competency already exists by name
                String name = element.getAttribute("name");

                // Note: Assuming we want to skip or update if exists. Here we'll skip/add
                // simplistic check if repository supported naming check
                // For now, simpler map:
                Competency competency = new Competency();
                competency.setName(name);
                competency.setDescription(element.getAttribute("description"));
                competency.setTargetRole(element.getAttribute("target_role"));
                competency.setRequiredLevel(Integer.parseInt(element.getAttribute("required_level")));

                competencyRepository.save(competency);
            }
            logger.info("Imported {} competencies", competencyList.getLength());
        } catch (Exception e) {
            logger.error("Error importing competencies data", e);
            throw new RuntimeException("Error importing competencies data", e);
        }
    }

    @Transactional
    public void importDepartmentsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Importing departments from XML: {}", xmlFilePath);
            Document doc = parseXmlFile(xmlFilePath);
            NodeList nodeList = doc.getElementsByTagName("department");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                String name = getElementTextContent(element, "name");
                if (departmentRepository.findByName(name).isPresent()) {
                    continue;
                }

                Department department = new Department();
                department.setName(name);
                department.setCode(getElementTextContent(element, "code"));
                department.setDescription(getElementTextContent(element, "description"));
                department.setHeadOfDepartmentName(getElementTextContent(element, "headOfDepartmentName"));

                departmentRepository.save(department);
            }
            logger.info("Imported {} departments", nodeList.getLength());
        } catch (Exception e) {
            logger.error("Error importing departments data", e);
            throw new RuntimeException("Error importing departments data", e);
        }
    }

    @Transactional
    public void importDesignationsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Importing designations from XML: {}", xmlFilePath);
            Document doc = parseXmlFile(xmlFilePath);
            NodeList nodeList = doc.getElementsByTagName("designation");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                String title = getElementTextContent(element, "title");
                if (designationRepository.findByTitle(title).isPresent()) {
                    continue;
                }

                Designation designation = new Designation();
                designation.setTitle(title);
                designation.setDescription(getElementTextContent(element, "description"));

                String rankStr = getElementTextContent(element, "rank");
                if (rankStr != null && !rankStr.isEmpty()) {
                    designation.setRankLevel(Integer.parseInt(rankStr));
                }

                designationRepository.save(designation);
            }
            logger.info("Imported {} designations", nodeList.getLength());
        } catch (Exception e) {
            logger.error("Error importing designations data", e);
            throw new RuntimeException("Error importing designations data", e);
        }
    }

    private void importPermissions(Document document) {
        NodeList permissionNodes = document.getElementsByTagName("permissions");
        logger.info("Importing {} permissions", permissionNodes.getLength());

        for (int i = 0; i < permissionNodes.getLength(); i++) {
            Element element = (Element) permissionNodes.item(i);

            Permission permission = new Permission();
            permission.setName(element.getAttribute("name"));
            permission.setDescription(element.getAttribute("description"));
            permission.setResource(element.getAttribute("resource"));
            permission.setAction(element.getAttribute("action"));
            permission.setSystemPermission(Boolean.valueOf(element.getAttribute("system_permission")));

            setBaseEntityFields(permission, element);

            permission = permissionRepository.save(permission);
            permissions.put(Long.valueOf(element.getAttribute("id")), permission);
        }
    }

    private void importRoles(Document document) {
        NodeList roleNodes = document.getElementsByTagName("roles");
        logger.info("Importing {} roles", roleNodes.getLength());

        for (int i = 0; i < roleNodes.getLength(); i++) {
            Element element = (Element) roleNodes.item(i);

            Role role = new Role();
            role.setName(element.getAttribute("name"));
            role.setDescription(element.getAttribute("description"));
            role.setSystemRole(Boolean.valueOf(element.getAttribute("system_role")));

            setBaseEntityFields(role, element);

            role = roleRepository.save(role);
            roles.put(Long.valueOf(element.getAttribute("id")), role);
        }
    }

    private void importRolePermissions(Document document) {
        NodeList relationNodes = document.getElementsByTagName("role_permissions");
        logger.info("Importing {} role-permission relations", relationNodes.getLength());

        for (int i = 0; i < relationNodes.getLength(); i++) {
            Element element = (Element) relationNodes.item(i);

            Long roleId = Long.valueOf(element.getAttribute("role_id"));
            Long permissionId = Long.valueOf(element.getAttribute("permission_id"));

            Role role = roles.get(roleId);
            Permission permission = permissions.get(permissionId);

            if (role != null && permission != null) {
                role.addPermission(permission);
                roleRepository.save(role);
            }
        }
    }

    private void importUsers(Document document) {
        NodeList userNodes = document.getElementsByTagName("users");
        logger.info("Importing {} users", userNodes.getLength());

        for (int i = 0; i < userNodes.getLength(); i++) {
            Element element = (Element) userNodes.item(i);

            User user = new User();
            user.setUsername(element.getAttribute("username"));
            user.setPasswordHash(element.getAttribute("password_hash"));
            user.setEmail(element.getAttribute("email"));
            user.setFirstName(element.getAttribute("first_name"));
            user.setLastName(element.getAttribute("last_name"));
            user.setPhone(getAttributeOrNull(element, "phone"));
            user.setUserType(User.UserType.valueOf(element.getAttribute("user_type")));
            user.setEnabled(Boolean.valueOf(element.getAttribute("enabled")));
            user.setAccountNonExpired(Boolean.valueOf(element.getAttribute("account_non_expired")));
            user.setCredentialsNonExpired(Boolean.valueOf(element.getAttribute("credentials_non_expired")));
            user.setAccountNonLocked(Boolean.valueOf(element.getAttribute("account_non_locked")));

            setBaseEntityFields(user, element);

            user = userRepository.save(user);
            users.put(Long.valueOf(element.getAttribute("id")), user);
        }
    }

    private void importUserRoles(Document document) {
        NodeList relationNodes = document.getElementsByTagName("user_roles");
        logger.info("Importing {} user-role relations", relationNodes.getLength());

        for (int i = 0; i < relationNodes.getLength(); i++) {
            Element element = (Element) relationNodes.item(i);

            Long userId = Long.valueOf(element.getAttribute("user_id"));
            Long roleId = Long.valueOf(element.getAttribute("role_id"));

            User user = users.get(userId);
            Role role = roles.get(roleId);

            if (user != null && role != null) {
                user.addRole(role);
                userRepository.save(user);
            }
        }
    }

    private void importAddresses(Document document) {
        NodeList addressNodes = document.getElementsByTagName("addresses");
        logger.info("Importing {} addresses", addressNodes.getLength());

        for (int i = 0; i < addressNodes.getLength(); i++) {
            Element element = (Element) addressNodes.item(i);

            Address address = new Address();
            address.setAddressLine1(getAttributeOrNull(element, "address_line1"));
            address.setAddressLine2(getAttributeOrNull(element, "address_line2"));
            address.setCity(getAttributeOrNull(element, "city"));
            address.setState(getAttributeOrNull(element, "state"));
            address.setPostalCode(getAttributeOrNull(element, "postal_code"));
            address.setCountry(getAttributeOrNull(element, "country"));

            String entityType = getAttributeOrNull(element, "entity_type");
            if (entityType != null) {
                address.setEntityType(Address.EntityType.valueOf(entityType));
            }

            String entityId = getAttributeOrNull(element, "entity_id");
            if (entityId != null) {
                address.setEntityId(Long.parseLong(entityId));
            }

            String isPrimary = getAttributeOrNull(element, "is_primary");
            if (isPrimary != null) {
                address.setIsPrimary("1".equals(isPrimary) || "true".equalsIgnoreCase(isPrimary));
            }

            String addressType = getAttributeOrNull(element, "address_type");
            if (addressType != null) {
                address.setAddressType(Address.AddressType.valueOf(addressType));
            }

            setBaseEntityFields(address, element);

            address = addressRepository.save(address);
            addresses.put(Long.valueOf(element.getAttribute("id")), address);
        }
    }

    private void importStaff(Document document) {
        NodeList staffNodes = document.getElementsByTagName("staff");
        logger.info("Importing {} staff members", staffNodes.getLength());

        for (int i = 0; i < staffNodes.getLength(); i++) {
            Element element = (Element) staffNodes.item(i);

            Staff staff = new Staff();
            staff.setFirstName(element.getAttribute("first_name"));
            staff.setLastName(element.getAttribute("last_name"));
            staff.setMiddleName(getAttributeOrNull(element, "middle_name"));
            staff.setStaffId(element.getAttribute("staff_id"));
            staff.setEmail(getAttributeOrNull(element, "email"));
            staff.setPhone(getAttributeOrNull(element, "phone"));
            staff.setDateOfBirth(parseLocalDate(element.getAttribute("date_of_birth")));
            staff.setGender(parseGender(element.getAttribute("gender"), Staff.Gender.class));
            staff.setHireDate(parseLocalDate(element.getAttribute("hire_date")));

            String terminationDate = getAttributeOrNull(element, "termination_date");
            if (terminationDate != null) {
                staff.setTerminationDate(parseLocalDate(terminationDate));
            }

            staff.setEmploymentStatus(Staff.EmploymentStatus.valueOf(element.getAttribute("employment_status")));
            staff.setStaffType(Staff.StaffType.valueOf(element.getAttribute("staff_type")));
            staff.setDepartment(getAttributeOrNull(element, "department"));
            staff.setPosition(getAttributeOrNull(element, "position"));
            staff.setQualification(getAttributeOrNull(element, "qualification"));

            String experienceYears = getAttributeOrNull(element, "experience_years");
            if (experienceYears != null) {
                staff.setExperienceYears(Integer.valueOf(experienceYears));
            }

            String salary = getAttributeOrNull(element, "salary");
            if (salary != null) {
                staff.setSalary(Double.valueOf(salary));
            }

            // Address relationship
            String addressId = getAttributeOrNull(element, "address_id");
            if (addressId != null) {
                staff.setAddress(addresses.get(Long.valueOf(addressId)));
            }

            // Emergency contact
            staff.setEmergencyContactName(getAttributeOrNull(element, "emergency_contact_name"));
            staff.setEmergencyContactPhone(getAttributeOrNull(element, "emergency_contact_phone"));
            staff.setEmergencyContactRelation(getAttributeOrNull(element, "emergency_contact_relation"));

            // User relationship
            String userId = getAttributeOrNull(element, "user_id");
            if (userId != null) {
                staff.setUser(users.get(Long.valueOf(userId)));
            }

            setBaseEntityFields(staff, element);

            staff = staffRepository.save(staff);
            staffMembers.put(Long.valueOf(element.getAttribute("id")), staff);
        }
    }

    private void importParents(Document document) {
        NodeList parentNodes = document.getElementsByTagName("parent");
        logger.info("Importing {} parents", parentNodes.getLength());

        for (int i = 0; i < parentNodes.getLength(); i++) {
            Element element = (Element) parentNodes.item(i);

            Parent parent = new Parent();
            parent.setFirstName(element.getAttribute("first_name"));
            parent.setLastName(element.getAttribute("last_name"));
            parent.setMiddleName(getAttributeOrNull(element, "middle_name"));
            parent.setEmail(getAttributeOrNull(element, "email"));
            parent.setPhone(getAttributeOrNull(element, "phone"));
            parent.setAlternatePhone(getAttributeOrNull(element, "alternate_phone"));
            parent.setGender(parseGender(element.getAttribute("gender"), Parent.Gender.class));
            parent.setOccupation(getAttributeOrNull(element, "occupation"));
            parent.setWorkplace(getAttributeOrNull(element, "workplace"));
            parent.setWorkPhone(getAttributeOrNull(element, "work_phone"));

            // Address relationship
            String addressId = getAttributeOrNull(element, "address_id");
            if (addressId != null) {
                parent.setAddress(addresses.get(Long.valueOf(addressId)));
            }

            parent.setEmergencyContact(parseBooleanValue(element.getAttribute("emergency_contact")));
            parent.setAuthorizedPickup(parseBooleanValue(element.getAttribute("authorized_pickup")));
            parent.setReceiveNotifications(parseBooleanValue(element.getAttribute("receive_notifications")));

            // Set isActive field
            String isActive = getAttributeOrNull(element, "is_active");
            parent.setIsActive(isActive != null ? (parseBooleanValue(isActive) ? 1 : 0) : 1);

            // User relationship
            String userId = getAttributeOrNull(element, "user_id");
            if (userId != null) {
                parent.setUser(users.get(Long.valueOf(userId)));
            }

            setBaseEntityFields(parent, element);

            parent = parentRepository.save(parent);
            parents.put(Long.valueOf(element.getAttribute("id")), parent);
        }
    }

    private void importStudents(Document document) {
        NodeList studentNodes = document.getElementsByTagName("students");
        logger.info("Importing {} students", studentNodes.getLength());

        for (int i = 0; i < studentNodes.getLength(); i++) {
            Element element = (Element) studentNodes.item(i);

            Student student = new Student();
            student.setFirstName(element.getAttribute("first_name"));
            student.setLastName(element.getAttribute("last_name"));
            student.setMiddleName(getAttributeOrNull(element, "middle_name"));
            student.setStudentId(element.getAttribute("student_id"));
            student.setEmail(getAttributeOrNull(element, "email"));
            student.setPhone(getAttributeOrNull(element, "phone"));
            student.setDateOfBirth(parseLocalDate(element.getAttribute("date_of_birth")));
            student.setGender(parseGender(element.getAttribute("gender"), Student.Gender.class));
            student.setEnrollmentDate(parseLocalDate(element.getAttribute("enrollment_date")));
            student.setGradeLevel(Student.GradeLevel.valueOf(element.getAttribute("grade_level")));
            student.setEnrollmentStatus(Student.EnrollmentStatus.valueOf(element.getAttribute("enrollment_status")));

            // Address relationship
            String addressId = getAttributeOrNull(element, "address_id");
            if (addressId != null) {
                student.setAddress(addresses.get(Long.valueOf(addressId)));
            }

            // Emergency contact
            student.setEmergencyContactName(getAttributeOrNull(element, "emergency_contact_name"));
            student.setEmergencyContactPhone(getAttributeOrNull(element, "emergency_contact_phone"));
            student.setEmergencyContactRelation(getAttributeOrNull(element, "emergency_contact_relation"));

            // User relationship
            String userId = getAttributeOrNull(element, "user_id");
            if (userId != null) {
                student.setUser(users.get(Long.valueOf(userId)));
            }

            setBaseEntityFields(student, element);

            // Check if student with same email already exists
            if (student.getEmail() != null && studentRepository.findByEmail(student.getEmail()).isPresent()) {
                logger.warn("⚠ Skipping student {} {} - email {} already exists",
                        student.getFirstName(), student.getLastName(), student.getEmail());
                continue;
            }

            student = studentRepository.save(student);
            students.put(Long.valueOf(element.getAttribute("id")), student);
        }
    }

    private void importParentStudentRelations(Document document) {
        NodeList relationNodes = document.getElementsByTagName("parent_student_relations");
        logger.info("Importing {} parent-student relations", relationNodes.getLength());

        for (int i = 0; i < relationNodes.getLength(); i++) {
            Element element = (Element) relationNodes.item(i);

            ParentStudentRelation relation = new ParentStudentRelation();

            Long parentId = Long.valueOf(element.getAttribute("parent_id"));
            Long studentId = Long.valueOf(element.getAttribute("student_id"));

            relation.setParent(parents.get(parentId));
            relation.setStudent(students.get(studentId));
            relation.setRelationshipType(
                    ParentStudentRelation.RelationshipType.valueOf(element.getAttribute("relationship_type")));
            relation.setPrimaryContact(Boolean.valueOf(element.getAttribute("primary_contact")));
            relation.setCustodyRights(Boolean.valueOf(element.getAttribute("custody_rights")));
            relation.setEmergencyContact(Boolean.valueOf(element.getAttribute("emergency_contact")));
            relation.setAuthorizedPickup(Boolean.valueOf(element.getAttribute("authorized_pickup")));
            relation.setReceiveCommunications(Boolean.valueOf(element.getAttribute("receive_communications")));
            relation.setNotes(getAttributeOrNull(element, "notes"));

            setBaseEntityFields(relation, element);

            parentStudentRelationRepository.save(relation);
        }
    }

    private void importSubjects(Document document) {
        NodeList subjectNodes = document.getElementsByTagName("subject");
        logger.info("Importing {} subjects", subjectNodes.getLength());

        for (int i = 0; i < subjectNodes.getLength(); i++) {
            Element element = (Element) subjectNodes.item(i);

            Subject subject = new Subject();
            subject.setSubjectCode(getElementText(element, "subjectCode"));
            subject.setSubjectName(getElementText(element, "subjectName"));
            subject.setDescription(getElementText(element, "description"));
            subject.setGradeLevel(getElementText(element, "gradeLevel"));
            subject.setCategory(getElementText(element, "category"));
            subject.setCredits(getElementInt(element, "credits", 0));
            subject.setIsMandatory(getElementBoolean(element, "isMandatory", true));
            subject.setDifficultyLevel(getElementText(element, "difficultyLevel"));
            subject.setPrerequisites(getElementText(element, "prerequisites"));
            subject.setIsActive(getElementBoolean(element, "isActive", true) ? 1 : 0);

            setBaseEntityFields(subject, element);

            subject = subjectRepository.save(subject);
            subjects.put(subject.getSubjectCode(), subject);
        }
    }

    // Helper methods for element text content (used by subjects.xml style)
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }

    private Integer getElementInt(Element parent, String tagName, Integer defaultValue) {
        String text = getElementText(parent, tagName);
        if (text != null && !text.isEmpty()) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private Boolean getElementBoolean(Element parent, String tagName, Boolean defaultValue) {
        String text = getElementText(parent, tagName);
        if (text != null && !text.isEmpty()) {
            return "true".equalsIgnoreCase(text) || "1".equals(text);
        }
        return defaultValue;
    }

    private void importAttendance(Document document) {
        NodeList attendanceNodes = document.getElementsByTagName("attendance");
        logger.info("Importing {} attendance records", attendanceNodes.getLength());

        for (int i = 0; i < attendanceNodes.getLength(); i++) {
            Element element = (Element) attendanceNodes.item(i);

            Attendance attendance = new Attendance();
            attendance.setAttendanceDate(parseLocalDate(element.getAttribute("attendance_date")));

            String checkInTime = getAttributeOrNull(element, "check_in_time");
            if (checkInTime != null) {
                attendance.setCheckInTime(parseLocalTime(checkInTime));
            }

            String checkOutTime = getAttributeOrNull(element, "check_out_time");
            if (checkOutTime != null) {
                attendance.setCheckOutTime(parseLocalTime(checkOutTime));
            }

            attendance.setStatus(Attendance.AttendanceStatus.valueOf(element.getAttribute("status")));
            attendance.setAttendanceType(Attendance.AttendanceType.valueOf(element.getAttribute("attendance_type")));
            attendance.setRemarks(getAttributeOrNull(element, "remarks"));
            attendance.setExcused(Boolean.valueOf(element.getAttribute("excused")));

            // Set either student or staff
            String studentId = getAttributeOrNull(element, "student_id");
            String staffId = getAttributeOrNull(element, "staff_id");

            if (studentId != null) {
                attendance.setStudent(students.get(Long.valueOf(studentId)));
            }

            if (staffId != null) {
                attendance.setStaff(staffMembers.get(Long.valueOf(staffId)));
            }

            String recordedBy = getAttributeOrNull(element, "recorded_by");
            if (recordedBy != null) {
                attendance.setRecordedBy(users.get(Long.valueOf(recordedBy)));
            }

            setBaseEntityFields(attendance, element);

            attendanceRepository.save(attendance);
        }
    }

    private void importHealthRecords(Document document) {
        NodeList healthNodes = document.getElementsByTagName("health_records");
        logger.info("Importing {} health records", healthNodes.getLength());

        for (int i = 0; i < healthNodes.getLength(); i++) {
            Element element = (Element) healthNodes.item(i);

            HealthRecord healthRecord = new HealthRecord();

            Long studentId = Long.valueOf(element.getAttribute("student_id"));
            healthRecord.setStudent(students.get(studentId));
            healthRecord.setRecordType(HealthRecord.RecordType.valueOf(element.getAttribute("record_type")));
            healthRecord.setTitle(element.getAttribute("title"));
            healthRecord.setDescription(getAttributeOrNull(element, "description"));

            String recordDate = getAttributeOrNull(element, "record_date");
            if (recordDate != null) {
                healthRecord.setRecordDate(parseLocalDate(recordDate));
            }

            String expiryDate = getAttributeOrNull(element, "expiry_date");
            if (expiryDate != null) {
                healthRecord.setExpiryDate(parseLocalDate(expiryDate));
            }

            healthRecord.setProvider(getAttributeOrNull(element, "provider"));
            healthRecord.setProviderContact(getAttributeOrNull(element, "provider_contact"));

            String severity = getAttributeOrNull(element, "severity");
            if (severity != null) {
                healthRecord.setSeverity(HealthRecord.Severity.valueOf(severity));
            }

            healthRecord.setMedication(getAttributeOrNull(element, "medication"));
            healthRecord.setDosage(getAttributeOrNull(element, "dosage"));
            healthRecord.setFrequency(getAttributeOrNull(element, "frequency"));
            healthRecord.setSpecialInstructions(getAttributeOrNull(element, "special_instructions"));
            healthRecord.setActive(Boolean.valueOf(element.getAttribute("active")));
            healthRecord.setRequiresAttention(Boolean.valueOf(element.getAttribute("requires_attention")));
            healthRecord.setDocumentPath(getAttributeOrNull(element, "document_path"));

            String recordedBy = getAttributeOrNull(element, "recorded_by");
            if (recordedBy != null) {
                healthRecord.setRecordedBy(users.get(Long.valueOf(recordedBy)));
            }

            setBaseEntityFields(healthRecord, element);

            healthRecordRepository.save(healthRecord);
        }
    }

    private void importStudentRegistrations(Document document) {
        NodeList registrationNodes = document.getElementsByTagName("student_registration");
        logger.info("Importing {} student registrations", registrationNodes.getLength());

        for (int i = 0; i < registrationNodes.getLength(); i++) {
            Element element = (Element) registrationNodes.item(i);
            try {
                String studentIdStr = getElementText(element, "studentId");
                String academicYearIdStr = getElementText(element, "academicYearId");
                String classIdStr = getElementText(element, "classId");

                Student student = studentRepository.findById(Long.parseLong(studentIdStr)).orElse(null);
                AcademicYear academicYear = academicYearRepository.findById(Long.parseLong(academicYearIdStr))
                        .orElse(null);
                ErpClass erpClass = classRepository.findById(Long.parseLong(classIdStr)).orElse(null);

                if (student != null && academicYear != null && erpClass != null) {
                    StudentRegistration registration = new StudentRegistration();
                    registration.setStudent(student);
                    registration.setAcademicYear(academicYear);
                    registration.setErpClass(erpClass);

                    String regDateStr = getElementText(element, "registrationDate");
                    if (regDateStr != null && !regDateStr.isEmpty()) {
                        registration.setRegistrationDate(LocalDate.parse(regDateStr));
                    } else {
                        registration.setRegistrationDate(LocalDate.now());
                    }

                    String statusStr = getElementText(element, "status");
                    if (statusStr != null && !statusStr.isEmpty()) {
                        registration.setStatus(StudentRegistration.RegistrationStatus.valueOf(statusStr));
                    }

                    registration.setRemarks(getElementText(element, "remarks"));
                    registration.markAsActive();
                    studentRegistrationRepository.save(registration);
                }
            } catch (Exception e) {
                logger.error("Error importing student registration", e);
            }
        }
    }

    // Helper methods
    private String getAttributeOrNull(Element element, String attributeName) {
        String value = element.getAttribute(attributeName);
        return (value == null || value.trim().isEmpty()) ? null : value;
    }

    private LocalDate parseLocalDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private LocalTime parseLocalTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    private LocalDateTime parseLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private <T extends Enum<T>> T parseGender(String genderString, Class<T> enumClass) {
        if (genderString == null || genderString.trim().isEmpty()) {
            return null;
        }
        return Enum.valueOf(enumClass, genderString);
    }

    private Boolean parseBooleanValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    private void setBaseEntityFields(Object entity, Element element) {
        try {
            // Map old field names to new ones for backward compatibility
            String createdAt = getAttributeOrNull(element, "created_at");
            String updatedAt = getAttributeOrNull(element, "updated_at");
            String createdTime = getAttributeOrNull(element, "created_time");
            String modifiedTime = getAttributeOrNull(element, "modified_time");

            // Use created_time if available, otherwise fall back to created_at
            String timeToSet = createdTime != null ? createdTime : createdAt;
            if (timeToSet != null) {
                entity.getClass().getMethod("setCreatedTime", LocalDateTime.class)
                        .invoke(entity, parseLocalDateTime(timeToSet));
            }

            // Use modified_time if available, otherwise fall back to updated_at
            String modTimeToSet = modifiedTime != null ? modifiedTime : updatedAt;
            if (modTimeToSet != null) {
                entity.getClass().getMethod("setModifiedTime", LocalDateTime.class)
                        .invoke(entity, parseLocalDateTime(modTimeToSet));
            }
            
            // Set createdBy to current user ID from security context
            Long currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                try {
                    entity.getClass().getMethod("setCreatedBy", Long.class)
                            .invoke(entity, currentUserId);
                } catch (NoSuchMethodException e) {
                    // Entity doesn't have setCreatedBy method, ignore
                }
            }
        } catch (Exception e) {
            // Ignore if fields don't exist or can't be set
        }
    }
    
    /**
     * Get the current user ID from the security context.
     * @return The user ID or null if not authenticated
     */
    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                return userDetails.getUserId();
            }
        } catch (Exception e) {
            logger.debug("Could not get current user ID from security context: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Set the createdBy field on an entity using reflection.
     * This method is used for entities that are created directly without going through setBaseEntityFields.
     * @param entity The entity to set createdBy on
     */
    private void setCreatedByOnEntity(Object entity) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            try {
                entity.getClass().getMethod("setCreatedBy", Long.class).invoke(entity, currentUserId);
            } catch (NoSuchMethodException e) {
                // Entity doesn't have setCreatedBy method, ignore
            } catch (Exception e) {
                logger.debug("Could not set createdBy on entity: {}", e.getMessage());
            }
        }
    }

    public void importAttendanceDataFromXml(String filePath) {
        importDataFromXml(filePath);
    }

    public void importCoursesDataFromXml(String filePath) {
        try {
            logger.info("Starting course data import from XML file: {}", filePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", filePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            importCourses(document);

            logger.info("Course data import completed successfully for: {}", filePath);

        } catch (Exception e) {
            logger.error("Error importing course data from XML file: {}", filePath, e);
            throw new RuntimeException("Failed to import course data from XML: " + filePath, e);
        }
    }

    private void importCourses(Document document) {
        NodeList courseNodes = document.getElementsByTagName("courses");
        logger.info("Importing {} courses", courseNodes.getLength());

        for (int i = 0; i < courseNodes.getLength(); i++) {
            Element element = (Element) courseNodes.item(i);

            Course course = new Course();
            course.setCourseCode(getChildElementText(element, "course_code"));
            course.setCourseName(getChildElementText(element, "course_name"));
            course.setDescription(getChildElementText(element, "description"));

            String credits = getChildElementText(element, "credits");
            if (credits != null && !credits.isEmpty()) {
                course.setCredits(Integer.valueOf(credits));
            }

            course.setDepartment(getChildElementText(element, "department"));
            course.setIsActive(1);
            course.setCreatedTime(LocalDateTime.now());

            if (course.getCourseCode() != null && !course.getCourseCode().isEmpty()
                    && !courseRepository.existsByCourseCode(course.getCourseCode())) {
                courseRepository.save(course);
                logger.info("Imported course: {}", course.getCourseCode());
            } else {
                logger.warn("Skipping existing or invalid course: {}", course.getCourseCode());
            }
        }
    }

    public void importExamsDataFromXml(String filePath) {
        try {
            logger.info("Starting exam data import from XML file: {}", filePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", filePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            importExams(document);

            logger.info("Exam data import completed successfully for: {}", filePath);

        } catch (Exception e) {
            logger.error("Error importing exam data from XML file: {}", filePath, e);
            throw new RuntimeException("Failed to import exam data from XML: " + filePath, e);
        }
    }

    private void importExams(Document document) {
        NodeList examNodes = document.getElementsByTagName("exams");
        logger.info("Importing {} exams", examNodes.getLength());

        for (int i = 0; i < examNodes.getLength(); i++) {
            Element element = (Element) examNodes.item(i);

            Exam exam = new Exam();
            String examName = getChildElementText(element, "exam_name");
            if (examName == null || examName.isEmpty()) {
                logger.warn("Skipping exam with no name");
                continue;
            }

            exam.setExamName(examName);
            exam.setExamCode(getChildElementText(element, "exam_code"));
            exam.setExamType(getChildElementText(element, "exam_type"));
            exam.setAcademicYear(getChildElementText(element, "academic_year"));
            exam.setSemester(getChildElementText(element, "semester"));
            exam.setExamDate(parseLocalDate(getChildElementText(element, "exam_date")));
            exam.setInstructions(getChildElementText(element, "instructions"));

            String totalMarks = getChildElementText(element, "total_marks");
            if (totalMarks != null && !totalMarks.isEmpty()) {
                exam.setTotalMarks(new java.math.BigDecimal(totalMarks));
            }

            String passingMarks = getChildElementText(element, "passing_marks");
            if (passingMarks != null && !passingMarks.isEmpty()) {
                exam.setPassingMarks(new java.math.BigDecimal(passingMarks));
            }

            String durationMinutes = getChildElementText(element, "duration_minutes");
            if (durationMinutes != null && !durationMinutes.isEmpty()) {
                exam.setDurationMinutes(Integer.valueOf(durationMinutes));
            }

            exam.setIsActive(1);
            exam.setCreatedTime(LocalDateTime.now());

            // Check if exam already exists by name
            if (!examRepository.findByExamName(examName).isPresent()) {
                examRepository.save(exam);
                logger.info("Imported exam: {}", examName);
            } else {
                logger.warn("Skipping existing exam: {}", examName);
            }
        }
    }

    /**
     * Get text content of a child element
     */
    private String getChildElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            String text = nodeList.item(0).getTextContent();
            return (text != null && !text.trim().isEmpty()) ? text.trim() : null;
        }
        return null;
    }

    /**
     * Import organizations from XML file
     */
    @Transactional
    public void importOrganizationsDataFromXml(String filePath) {
        try {
            logger.info("Importing organizations from: {}", filePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                throw new RuntimeException("XML file not found: " + filePath);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList orgList = document.getElementsByTagName("organizations");
            for (int i = 0; i < orgList.getLength(); i++) {
                Element orgElement = (Element) orgList.item(i);

                String name = orgElement.getAttribute("name");
                if (name == null || name.isEmpty())
                    continue;

                // Check if organization already exists
                if (organizationRepository.findByName(name).isPresent()) {
                    logger.info("Skipping existing organization: {}", name);
                    continue;
                }

                Organization org = new Organization();
                org.setName(name);
                org.setType(orgElement.getAttribute("type"));
                org.setCode(orgElement.getAttribute("code"));
                org.setDescription(orgElement.getAttribute("description"));
                org.setEmail(orgElement.getAttribute("email"));
                org.setPhone(orgElement.getAttribute("phone"));
                org.setFax(orgElement.getAttribute("fax"));
                org.setWebsite(orgElement.getAttribute("website"));
                org.setStreetAddress(orgElement.getAttribute("street_address"));
                org.setCity(orgElement.getAttribute("city"));
                org.setState(orgElement.getAttribute("state"));
                org.setPostalCode(orgElement.getAttribute("postal_code"));
                org.setCountry(orgElement.getAttribute("country"));
                org.setRegistrationNumber(orgElement.getAttribute("registration_number"));
                org.setTaxId(orgElement.getAttribute("tax_id"));

                String establishedYear = orgElement.getAttribute("established_year");
                if (establishedYear != null && !establishedYear.isEmpty()) {
                    org.setEstablishedYear(Integer.parseInt(establishedYear));
                }

                org.setIsActive("1".equals(orgElement.getAttribute("is_active")) ? 1 : 0);
                org.setCreatedTime(LocalDateTime.now());

                organizationRepository.save(org);
                logger.info("Imported organization: {}", name);
            }
        } catch (Exception e) {
            logger.error("Error importing organizations from XML", e);
            throw new RuntimeException("Failed to import organizations: " + e.getMessage(), e);
        }
    }

    /**
     * Import academic years from XML file
     */
    @Transactional
    public void importAcademicYearsDataFromXml(String filePath) {
        try {
            logger.info("Importing academic years from: {}", filePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                throw new RuntimeException("XML file not found: " + filePath);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList yearList = document.getElementsByTagName("academicYear");
            for (int i = 0; i < yearList.getLength(); i++) {
                Element yearElement = (Element) yearList.item(i);

                String name = getChildElementText(yearElement, "name");
                if (name == null || name.isEmpty())
                    continue;

                // Check if academic year already exists
                if (academicYearRepository.findByName(name).isPresent()) {
                    logger.info("Skipping existing academic year: {}", name);
                    continue;
                }

                AcademicYear academicYear = new AcademicYear();
                academicYear.setName(name);

                String startDateStr = getChildElementText(yearElement, "startDate");
                if (startDateStr != null) {
                    academicYear.setStartDate(LocalDate.parse(startDateStr));
                }

                String endDateStr = getChildElementText(yearElement, "endDate");
                if (endDateStr != null) {
                    academicYear.setEndDate(LocalDate.parse(endDateStr));
                }

                String isActive = getChildElementText(yearElement, "isActive");
                academicYear.setIsActive("true".equalsIgnoreCase(isActive));

                academicYear.setCreatedAt(LocalDateTime.now());

                academicYearRepository.save(academicYear);
                logger.info("Imported academic year: {}", name);
            }
        } catch (Exception e) {
            logger.error("Error importing academic years from XML", e);
            throw new RuntimeException("Failed to import academic years: " + e.getMessage(), e);
        }
    }

    @Autowired
    private krs.erp.service.hr.PayrollService payrollService;

    @Autowired
    private krs.erp.repository.hr.StaffSalaryRepository staffSalaryRepository;

    @Transactional
    public void generatePayrollSampleData() {
        try {
            logger.info("Starting Payroll Sample Data Generation...");

            // 1. Create Salary Structures for all Active Staff
            List<Staff> activeStaff = staffRepository.findByEmploymentStatus(Staff.EmploymentStatus.ACTIVE);
            logger.info("Found {} active staff members", activeStaff.size());

            for (Staff staff : activeStaff) {
                if (staffSalaryRepository.findByStaffId(staff.getId()).isEmpty()) {
                    krs.erp.model.hr.StaffSalary salary = new krs.erp.model.hr.StaffSalary();
                    salary.setStaffId(staff.getId());

                    // Randomize base salary between 30k and 100k
                    double basic = 30000 + (Math.random() * 70000);
                    basic = Math.round(basic / 100) * 100; // Round to nearest 100

                    salary.setBasicSalary(basic);
                    salary.setHra(basic * 0.40); // 40% of Basic
                    salary.setDa(basic * 0.10); // 10% of Basic
                    salary.setSpecialAllowance(5000.0);

                    // Enable PF for 70% of staff
                    boolean enablePf = Math.random() > 0.3;
                    salary.setIsPfEnabled(enablePf);
                    if (enablePf) {
                        salary.setPfAccountNumber("PF" + (10000 + staff.getId()));
                    }

                    // Random Tax between 1000 and 5000
                    salary.setTaxDeduction(enablePf ? 1500.0 : 500.0);
                    salary.setPanNumber("ABCDE" + (1000 + staff.getId()) + "F");

                    setCreatedByOnEntity(salary);
                    staffSalaryRepository.save(salary);
                }
            }
            logger.info("Salary structures created/verified.");

            // 2. Generate Payroll Runs for last 3 months
            LocalDate today = LocalDate.now();
            for (int i = 2; i >= 0; i--) {
                LocalDate date = today.minusMonths(i);
                int month = date.getMonthValue();
                int year = date.getYear();

                try {
                    if (!payrollService.getAllRuns().stream()
                            .anyMatch(r -> r.getMonth() == month && r.getYear() == year)) {
                        logger.info("Generating Payroll for {}/{}", month, year);
                        krs.erp.model.hr.PayrollRun run = payrollService.initiatePayrollRun(month, year);
                        payrollService.executePayrollRun(run.getId());
                    }
                } catch (Exception e) {
                    logger.warn("Skipping payroll gen for {}/{}: {}", month, year, e.getMessage());
                }
            }

            logger.info("Payroll Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating payroll sample data", e);
            throw new RuntimeException("Failed to generate payroll data", e);
        }
    }

    @Autowired
    private krs.erp.repository.hr.LeaveTypeRepository leaveTypeRepository;
    @Autowired
    private krs.erp.repository.hr.LeaveBalanceRepository leaveBalanceRepository;

    @Transactional
    public void generateLeaveSampleData() {
        try {
            logger.info("Starting Leave Sample Data Generation...");

            // 1. Create Leave Types
            createLeaveType("Sick Leave", "SL", 10, false, "Medical leave for illness");
            createLeaveType("Casual Leave", "CL", 10, false, "For personal matters");
            createLeaveType("Privilege Leave", "PL", 15, true, "Earned leave based on service");

            List<krs.erp.model.hr.LeaveType> types = leaveTypeRepository.findAll();

            // 2. Create Balances for all staff
            List<Staff> staffList = staffRepository.findAll();
            String academicYear = "2025-2026";

            int balancesCreated = 0;
            for (Staff staff : staffList) {
                for (krs.erp.model.hr.LeaveType type : types) {
                    if (leaveBalanceRepository
                            .findByStaffIdAndLeaveTypeIdAndAcademicYear(staff.getId(), type.getId(), academicYear)
                            .isEmpty()) {
                        krs.erp.model.hr.LeaveBalance balance = new krs.erp.model.hr.LeaveBalance();
                        balance.setStaffId(staff.getId());
                        balance.setLeaveTypeId(type.getId());
                        balance.setAcademicYear(academicYear);
                        balance.setTotalDays((double) type.getDaysAllowed());

                        // Randomly consume some days for realism
                        double consumed = Math.random() > 0.5 ? Math.floor(Math.random() * 5) : 0.0;
                        balance.setConsumedDays(consumed);
                        balance.setRemainingDays(type.getDaysAllowed() - consumed);

                        leaveBalanceRepository.save(balance);
                        balancesCreated++;
                    }
                }
            }
            logger.info("Leave Sample Data Generation Completed. Balances created: {}", balancesCreated);

        } catch (Exception e) {
            logger.error("Error generating leave sample data", e);
            throw new RuntimeException("Failed to generate leave data", e);
        }
    }

    private void createLeaveType(String name, String code, int days, boolean carry, String desc) {
        if (leaveTypeRepository.findByCode(code).isEmpty()) {
            krs.erp.model.hr.LeaveType t = new krs.erp.model.hr.LeaveType();
            t.setName(name);
            t.setCode(code);
            t.setDaysAllowed(days);
            t.setIsCarryForward(carry);
            t.setDescription(desc);
            leaveTypeRepository.save(t);
        }
    }

    @Autowired
    private krs.erp.repository.hr.PerformanceCycleRepository performanceCycleRepository;
    @Autowired
    private krs.erp.repository.hr.PerformanceCriteriaRepository performanceCriteriaRepository;
    @Autowired
    private krs.erp.repository.hr.PerformanceReviewRepository performanceReviewRepository;
    @Autowired
    private krs.erp.repository.hr.PerformanceReviewDetailRepository performanceReviewDetailRepository;

    @Transactional
    public void generatePerformanceSampleData() {
        try {
            logger.info("Starting Performance Sample Data Generation...");

            // 1. Create Performance Cycles
            PerformanceCycle cycle = new PerformanceCycle();
            cycle.setName("Annual Review 2025");
            cycle.setStartDate(LocalDate.of(2025, 1, 1));
            cycle.setEndDate(LocalDate.of(2025, 12, 31));
            cycle.setStatus(PerformanceCycle.CycleStatus.ACTIVE);
            performanceCycleRepository.save(cycle);

            // 2. Create Criteria
            createCriteria("Productivity", "Employee's ability to produce work efficiently.");
            createCriteria("Quality of Work", "The accuracy and thoroughness of work produced.");
            createCriteria("Teamwork", "Cooperation and contribution to team goals.");
            createCriteria("Attendance", "Punctuality and presence at work.");

            List<PerformanceCriteria> criteriaList = performanceCriteriaRepository.findAll();
            List<Staff> staffList = staffRepository.findAll();

            // 3. Create Sample Reviews for first 5 staff members
            for (int i = 0; i < Math.min(5, staffList.size()); i++) {
                Staff staff = staffList.get(i);
                PerformanceReview review = new PerformanceReview();
                review.setStaffId(staff.getId());
                review.setCycleId(cycle.getId());
                review.setReviewerId(1L); // Admin or Manager
                review.setReviewDate(LocalDate.now());
                review.setStatus(PerformanceReview.ReviewStatus.SUBMITTED);
                review.setComments("Good overall performance during this period.");
                performanceReviewRepository.save(review);

                double ratingSum = 0;
                for (PerformanceCriteria criteria : criteriaList) {
                    PerformanceReviewDetail detail = new PerformanceReviewDetail();
                    detail.setReviewId(review.getId());
                    detail.setCriteriaId(criteria.getId());
                    int rating = 3 + (int) (Math.random() * 3); // Rating 3, 4, or 5
                    detail.setRating(rating);
                    detail.setComments("Consistent performance in " + criteria.getName());
                    performanceReviewDetailRepository.save(detail);
                    ratingSum += rating;
                }

                review.setOverallRating(ratingSum / criteriaList.size());
                performanceReviewRepository.save(review);
            }

            logger.info("Performance Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating performance sample data", e);
            throw new RuntimeException("Failed to generate performance data", e);
        }
    }

    @Autowired
    private krs.erp.repository.inventory.AssetRepository assetRepository;
    @Autowired
    private krs.erp.repository.inventory.ConsumableRepository consumableRepository;
    @Autowired
    private krs.erp.repository.inventory.VendorRepository vendorRepository;
    @Autowired
    private krs.erp.repository.inventory.PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public void generateInventorySampleData() {
        try {
            logger.info("Starting Inventory Sample Data Generation...");

            // 1. Create Vendors
            Vendor v1 = createVendor("Global Systems", "John Doe", "john@globalsystems.com", "9876543210", "GST12345");
            Vendor v2 = createVendor("Elite Furniture", "Jane Smith", "jane@elite.com", "9876543211", "GST54321");

            // 2. Create Assets
            createAsset("MacBook Air M2", "AST-001", "SN12345", "Electronics", LocalDate.now().minusMonths(6),
                    Asset.AssetStatus.ASSIGNED, "Lab 1", 1L);
            createAsset("Dell Latitude 7420", "AST-002", "SN54321", "Electronics", LocalDate.now().minusMonths(2),
                    Asset.AssetStatus.AVAILABLE, "Storage", null);
            createAsset("Ergonomic Chair", "AST-003", "SN98765", "Furniture", LocalDate.now().minusYears(1),
                    Asset.AssetStatus.ASSIGNED, "Office 101", 2L);

            // 3. Create Consumables
            createConsumable("A4 Paper", "CON-001", "Stationery", "Box", 5, 20);
            createConsumable("Blue Ink Pen", "CON-002", "Stationery", "Nos", 50, 200);
            createConsumable("Hand Sanitizer", "CON-003", "Cleaning", "Litre", 10, 50);

            // 4. Create sample Purchase Orders
            createPO("PO-2025-001", v1.getId(), LocalDate.now().minusDays(10), 150000.0,
                    PurchaseOrder.POStatus.RECEIVED);
            createPO("PO-2025-002", v2.getId(), LocalDate.now().minusDays(2), 25000.0, PurchaseOrder.POStatus.ORDERED);

            logger.info("Inventory Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating inventory sample data", e);
            throw new RuntimeException("Failed to generate inventory data", e);
        }
    }

    private Vendor createVendor(String name, String contact, String email, String phone, String gstin) {
        Vendor v = new Vendor();
        v.setName(name);
        v.setContactPerson(contact);
        v.setEmail(email);
        v.setPhone(phone);
        v.setTinGstin(gstin);
        return vendorRepository.save(v);
    }

    private void createAsset(String name, String tag, String sn, String type, LocalDate date, Asset.AssetStatus status,
            String loc, Long staffId) {
        Asset a = new Asset();
        a.setName(name);
        a.setAssetTag(tag);
        a.setSerialNumber(sn);
        a.setType(type);
        a.setPurchaseDate(date);
        a.setStatus(status);
        a.setLocation(loc);
        a.setAssignedStaffId(staffId);
        assetRepository.save(a);
    }

    private void createConsumable(String name, String code, String cat, String unit, Integer reorder, Integer stock) {
        Consumable c = new Consumable();
        c.setName(code); // Fixed: name should be set to name, code to code
        c.setName(name);
        c.setCode(code);
        c.setCategory(cat);
        c.setUnit(unit);
        c.setReorderLevel(reorder);
        c.setCurrentStock(stock);
        consumableRepository.save(c);
    }

    private void createPO(String poNum, Long vendorId, LocalDate date, Double amount, PurchaseOrder.POStatus status) {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(poNum);
        po.setVendorId(vendorId);
        po.setOrderDate(date);
        po.setTotalAmount(amount);
        po.setStatus(status);
        purchaseOrderRepository.save(po);
    }

    @Autowired
    private krs.erp.repository.maintenance.WorkOrderRepository workOrderRepository;
    @Autowired
    private krs.erp.repository.maintenance.FacilityRepository facilityRepository;
    @Autowired
    private krs.erp.repository.maintenance.FacilityBookingRepository facilityBookingRepository;

    @Transactional
    public void generateMaintenanceSampleData() {
        try {
            logger.info("Starting Maintenance Sample Data Generation...");

            // 1. Create Facilities
            Facility f1 = createFacility("Main Auditorium", "Auditorium", 500,
                    "Main building auditorium for large events.");
            Facility f2 = createFacility("Advanced Physics Lab", "Lab", 40,
                    "Lab with advanced equipment for research.");
            Facility f3 = createFacility("Conference Room A", "Meeting Room", 20, "Boardroom for faculty meetings.");

            // 2. Create sample Facilities Bookings
            createFacilityBooking(f1.getId(), 1L, LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
                    LocalDateTime.now().plusDays(2).withHour(13).withMinute(0), "Annual Science Fair");
            createFacilityBooking(f3.getId(), 2L, LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
                    LocalDateTime.now().plusDays(1).withHour(16).withMinute(0), "Departmental Meeting");

            // 3. Create Work Orders
            createWorkOrder("AC Repair - Room 202", "AC is not cooling, making noise.", "HIGH",
                    WorkOrder.WorkOrderStatus.ASSIGNED, 3L, null);
            createWorkOrder("Projector Bulb Replacement", "Projector in Lab 1 bulb is fused.", "MEDIUM",
                    WorkOrder.WorkOrderStatus.NEW, null, null);
            createWorkOrder("Water Leakage - Cafeteria", "Major leakage in the kitchen area.", "URGENT",
                    WorkOrder.WorkOrderStatus.IN_PROGRESS, 4L, null);

            logger.info("Maintenance Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating maintenance sample data", e);
            throw new RuntimeException("Failed to generate maintenance data", e);
        }
    }

    private Facility createFacility(String name, String type, Integer cap, String desc) {
        Facility f = new Facility();
        f.setName(name);
        f.setType(type);
        f.setCapacity(cap);
        f.setDescription(desc);
        return facilityRepository.save(f);
    }

    private void createFacilityBooking(Long facilityId, Long staffId, LocalDateTime start, LocalDateTime end,
            String purpose) {
        FacilityBooking b = new FacilityBooking();
        b.setFacilityId(facilityId);
        b.setBookedById(staffId);
        b.setStartTime(start);
        b.setEndTime(end);
        b.setPurpose(purpose);
        facilityBookingRepository.save(b);
    }

    private void createWorkOrder(String title, String desc, String priority, WorkOrder.WorkOrderStatus status,
            Long technicianId, Long assetId) {
        WorkOrder wo = new WorkOrder();
        wo.setTitle(title);
        wo.setDescription(desc);
        wo.setPriority(priority);
        wo.setStatus(status);
        wo.setAssignedTechnicianId(technicianId);
        wo.setAssetId(assetId);
        workOrderRepository.save(wo);
    }

    @Autowired
    private krs.erp.repository.reporting.MISReportRepository misReportRepository;

    @Transactional
    public void generateReportingSampleData() {
        try {
            logger.info("Starting Reporting Sample Data Generation...");

            createMISReport("Student Enrollment Summary 2025", "ADMISSION", "Overview of new student intakes by grade.",
                    "{\"total\": 378, \"new\": 45}");
            createMISReport("Staff Distribution by Dept", "HR",
                    "Staff count per department including teaching and admin.", "{\"Teaching\": 18, \"Admin\": 7}");
            createMISReport("Financial Quarterly Review", "FINANCE", "Revenue and Expenditure for Q1 2025.",
                    "{\"Revenue\": 1250000, \"Expense\": 850000}");
            createMISReport("Asset Inventory Audit", "INVENTORY", "Full audit report of institutional physical assets.",
                    "{\"IT\": 80, \"Furniture\": 70}");

            logger.info("Reporting Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating reporting sample data", e);
            throw new RuntimeException("Failed to generate reporting data", e);
        }
    }

    private void createMISReport(String name, String type, String desc, String data) {
        MISReport r = new MISReport();
        r.setName(name);
        r.setType(type);
        r.setDescription(desc);
        r.setLastRunDate(LocalDateTime.now());
        r.setReportData(data);
        misReportRepository.save(r);
    }

    @Autowired
    private krs.erp.repository.documents.DocumentRepository documentRepository;

    @Transactional
    public void generateDocumentSampleData() {
        try {
            logger.info("Starting Document Sample Data Generation...");

            createDocument("School Leave Policy 2025", "POLICY", "PDF", "docs/policies/leave_2025.pdf", null);
            createDocument("Admission Guidelines", "POLICY", "PDF", "docs/policies/admission.pdf", null);
            createDocument("Staff Recruitment Contract", "CONTRACT", "DOC", "docs/contracts/staff_template.docx", null);
            createDocument("Student ID Card - John Doe", "STUDENT", "IMG", "docs/students/id_1.jpg", 1L);
            createDocument("Salary Increment Circular", "CIRCULAR", "PDF", "docs/circulars/increment_q1.pdf", null);

            logger.info("Document Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating document sample data", e);
            throw new RuntimeException("Failed to generate document data", e);
        }
    }

    private void createDocument(String title, String category, String type, String url, Long ownerId) {
        krs.erp.model.documents.ErpDocument d = new krs.erp.model.documents.ErpDocument();
        d.setTitle(title);
        d.setCategory(category);
        d.setFileType(type);
        d.setFileUrl(url);
        d.setOwnerId(ownerId);
        d.setUploadDate(LocalDateTime.now());
        d.setStatus(krs.erp.model.documents.ErpDocument.DocumentStatus.ACTIVE);
        documentRepository.save(d);
    }

    @Autowired
    private krs.erp.repository.calendar.InstitutionEventRepository institutionEventRepository;

    @Transactional
    public void generateCalendarSampleData() {
        try {
            logger.info("Starting Event & Calendar Sample Data Generation...");

            // Holidays
            createCalendarDay(java.time.LocalDate.of(2025, 1, 1), "HOLIDAY", "New Year Day", true);
            createCalendarDay(java.time.LocalDate.of(2025, 1, 26), "HOLIDAY", "Republic Day", true);
            createCalendarDay(java.time.LocalDate.of(2025, 8, 15), "HOLIDAY", "Independence Day", true);
            createCalendarDay(java.time.LocalDate.of(2025, 10, 2), "HOLIDAY", "Gandhi Jayanti", true);

            // Events
            createInstitutionEvent("Annual Sports Meet", "School sports day with various competitions.",
                    java.time.LocalDateTime.of(2025, 2, 15, 9, 0), java.time.LocalDateTime.of(2025, 2, 15, 17, 0),
                    "Main Ground", "SPORTS");

            createInstitutionEvent("Science Exhibition", "Inter-school science project display.",
                    java.time.LocalDateTime.of(2025, 3, 10, 10, 0), java.time.LocalDateTime.of(2025, 3, 10, 16, 0),
                    "Auditorium", "ACADEMIC");

            createInstitutionEvent("Annual Day Celebration", "Cultural program and award ceremony.",
                    java.time.LocalDateTime.of(2025, 12, 20, 16, 0), java.time.LocalDateTime.of(2025, 12, 20, 21, 0),
                    "Auditorium", "CULTURAL");

            logger.info("Event & Calendar Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating calendar data", e);
            throw new RuntimeException("Failed to generate calendar data", e);
        }
    }

    private void createCalendarDay(java.time.LocalDate date, String type, String desc, boolean isHoliday) {
        CalendarDay d = new CalendarDay();
        d.setDate(date);
        d.setType(type);
        d.setDescription(desc);
        d.setHoliday(isHoliday);
        calendarDayRepository.save(d);
    }

    @Autowired
    private krs.erp.repository.communication.NotificationLogRepository logRepository;

    @Autowired
    private krs.erp.repository.communication.NotificationTemplateRepository templateRepository;

    @Transactional
    public void generateCommunicationSampleData() {
        generateNotificationSampleData();
        generateMessagingSampleData();
    }

    private void generateNotificationSampleData() {
        try {
            logger.info("Starting Communication & Alerts Sample Data Generation...");

            // Templates
            createTemplate("Admission Welcome", "EMAIL", "Welcome to our Institution!",
                    "Dear {{name}}, Welcome to the 2025 academic year. Your admission id is {{id}}.", "name,id");

            createTemplate("Fee Reminder", "SMS", "Fee Payment Due",
                    "Dear Parent, fee for {{month}} is due. Amount: {{amount}}. Please pay before {{date}}.",
                    "month,amount,date");

            // Logs
            createLog("john.doe@example.com", "STUDENT", "EMAIL", "Welcome to our Institution!",
                    "Dear John Doe, Welcome to the 2025 academic year. Your admission id is ADM001.", "SENT");

            createLog("+1234567890", "PARENT", "SMS", "Fee Payment Due",
                    "Dear Parent, fee for January is due. Amount: 5000. Please pay before 2025-01-15.", "SENT");

            logger.info("Communication & Alerts Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating communication data", e);
            throw new RuntimeException("Failed to generate communication data", e);
        }
    }

    private void createTemplate(String name, String channel, String subject, String content, String placeholders) {
        krs.erp.model.communication.NotificationTemplate t = new krs.erp.model.communication.NotificationTemplate();
        t.setName(name);
        t.setChannel(channel);
        t.setSubject(subject);
        t.setContent(content);
        t.setPlaceholders(placeholders);
        templateRepository.save(t);
    }

    private void createLog(String recipient, String type, String channel, String subject, String content,
            String status) {
        krs.erp.model.communication.NotificationLog l = new krs.erp.model.communication.NotificationLog();
        l.setRecipient(recipient);
        l.setRecipientType(type);
        l.setChannel(channel);
        l.setSubject(subject);
        l.setContent(content);
        l.setStatus(status);
        l.setSentAt(LocalDateTime.now());
        logRepository.save(l);
    }

    @Autowired

    private krs.erp.repository.alumni.AlumniRepository alumniRepository;

    @Autowired
    private krs.erp.repository.alumni.AlumniContributionRepository alumniContributionRepository;

    @Transactional
    public void generateAlumniSampleData() {
        try {
            logger.info("Starting Alumni Management Sample Data Generation...");

            // Alumni Profiles
            krs.erp.model.alumni.Alumni a1 = createAlumni("Jane Smith", 2018, "B.Sc Computer Science",
                    "jane@example.com", "Senior Dev", "Google");
            krs.erp.model.alumni.Alumni a2 = createAlumni("Robert Brown", 2020, "B.Tech Electronics",
                    "robert@example.com", "Product Manager", "Apple");

            // Contributions
            createContribution(a1.getId(), 10000.0, krs.erp.model.alumni.AlumniContribution.ContributionType.DONATION,
                    "Library fund donation");
            createContribution(a2.getId(), 0.0, krs.erp.model.alumni.AlumniContribution.ContributionType.MENTORSHIP,
                    "Career guidance for juniors");

            logger.info("Alumni Management Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating alumni data", e);
            throw new RuntimeException("Failed to generate alumni data", e);
        }
    }

    private krs.erp.model.alumni.Alumni createAlumni(String name, Integer year, String degree, String email, String job,
            String company) {
        krs.erp.model.alumni.Alumni a = new krs.erp.model.alumni.Alumni();
        a.setFullName(name);
        a.setGraduationYear(year);
        a.setDegree(degree);
        a.setEmail(email);
        a.setOccupation(job);
        a.setCompany(company);
        a.setStatus(krs.erp.model.alumni.Alumni.AlumniStatus.ACTIVE);
        return alumniRepository.save(a);
    }

    private void createContribution(Long alumniId, Double amount,
            krs.erp.model.alumni.AlumniContribution.ContributionType type, String desc) {
        krs.erp.model.alumni.AlumniContribution c = new krs.erp.model.alumni.AlumniContribution();
        c.setAlumniId(alumniId);
        c.setAmount(amount);
        c.setType(type);
        c.setDescription(desc);
        c.setContributionDate(LocalDate.now());
        alumniContributionRepository.save(c);
    }

    private void createInstitutionEvent(String title, String desc, java.time.LocalDateTime start,
            java.time.LocalDateTime end, String loc, String cat) {
        krs.erp.model.calendar.InstitutionEvent e = new krs.erp.model.calendar.InstitutionEvent();
        e.setTitle(title);
        e.setDescription(desc);
        e.setStartDate(start);
        e.setEndDate(end);
        e.setLocation(loc);
        e.setCategory(cat);
        e.setStatus(krs.erp.model.calendar.InstitutionEvent.EventStatus.SCHEDULED);
        institutionEventRepository.save(e);

        // Sync to calendar
        // Sync to calendar
        createCalendarDay(start.toLocalDate(), "EVENT", title, false);
    }

    @Autowired
    private krs.erp.repository.finance.FeeTypeRepository feeTypeRepository;
    @Autowired
    private krs.erp.repository.finance.FeeStructureRepository feeStructureRepository;
    @Autowired
    private krs.erp.repository.finance.FeeDiscountRuleRepository feeDiscountRuleRepository;
    @Autowired
    private krs.erp.repository.finance.FineCategoryRepository fineCategoryRepository;
    @Autowired
    private krs.erp.repository.finance.FineConfigurationRepository fineConfigurationRepository;
    @Autowired
    private krs.erp.repository.finance.FeePaymentRepository feePaymentRepository;
    @Autowired
    private krs.erp.repository.finance.StudentFineLedgerRepository fineLedgerRepository;
    @Autowired
    private krs.erp.repository.finance.DisciplinaryIncidentRepository disciplinaryIncidentRepository;
    @Autowired
    private krs.erp.repository.finance.FineWaiverRequestRepository fineWaiverRequestRepository;
    @Autowired
    private krs.erp.repository.finance.InvoiceRepository invoiceRepository;
    @Autowired
    private krs.erp.repository.finance.InvoiceItemRepository invoiceItemRepository;
    @Autowired
    private krs.erp.repository.finance.TransactionRepository transactionRepository;
    @Autowired
    private krs.erp.repository.finance.ScholarshipCategoryRepository scholarshipCategoryRepository;
    @Autowired
    private krs.erp.repository.finance.ScholarshipApplicationRepository scholarshipApplicationRepository;
    @Autowired
    private krs.erp.repository.finance.ScholarshipDisbursementRepository scholarshipDisbursementRepository;
    @Autowired
    private krs.erp.repository.finance.ChartOfAccountRepository chartOfAccountRepository;
    @Autowired
    private krs.erp.repository.finance.JournalEntryRepository journalEntryRepository;
    @Autowired
    private krs.erp.repository.finance.JournalItemRepository journalItemRepository;
    @Autowired
    private krs.erp.repository.finance.AccountingPeriodRepository accountingPeriodRepository;

    @Transactional
    public void generateFinanceSampleData() {
        try {
            logger.info("Starting Fee & Fine Management Sample Data Generation from XML...");

            // Import from XML files
            importFeeTypesDataFromXml("data/finance/sample-fee-types.xml");
            importFeeStructuresDataFromXml("data/finance/sample-fee-structures.xml");
            importDiscountRulesDataFromXml("data/finance/sample-discount-rules.xml");
            importFineCategoriesDataFromXml("data/finance/sample-fine-categories.xml");
            importInvoicesDataFromXml("data/finance/sample-invoices.xml");
            importTransactionsDataFromXml("data/finance/sample-transactions.xml");
            importFeePaymentsDataFromXml("data/finance/sample-fee-payments.xml");
            importChartOfAccountsDataFromXml("data/finance/sample-chart-of-accounts.xml");
            importJournalEntriesDataFromXml("data/finance/sample-journal-entries.xml");
            importBudgetsDataFromXml("data/finance/sample-budgets.xml");

            logger.info("Fee & Fine Management Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating finance data", e);
            throw new RuntimeException("Failed to generate finance data", e);
        }
    }

    private krs.erp.model.finance.FeeType createFeeType(String name, String code) {
        krs.erp.model.finance.FeeType t = new krs.erp.model.finance.FeeType();
        t.setName(name);
        t.setCode(code);
        return feeTypeRepository.save(t);
    }

    private void createFeeStructure(Long typeId, Double amount, LocalDate due) {
        krs.erp.model.finance.FeeStructure s = new krs.erp.model.finance.FeeStructure();
        s.setFeeTypeId(typeId);
        s.setAcademicYearId(1L); // Default
        s.setAmount(amount);
        s.setDueDate(due);
        feeStructureRepository.save(s);
    }

    private void createDiscountRule(String name, krs.erp.model.finance.FeeDiscountRule.DiscountType type, Double val,
            String cond) {
        krs.erp.model.finance.FeeDiscountRule r = new krs.erp.model.finance.FeeDiscountRule();
        r.setName(name);
        r.setType(type);
        r.setValue(val);
        r.setConditionType(cond);
        feeDiscountRuleRepository.save(r);
    }

    private krs.erp.model.finance.FineCategory createFineCategory(String name, String desc) {
        krs.erp.model.finance.FineCategory c = new krs.erp.model.finance.FineCategory();
        c.setName(name);
        c.setDescription(desc);
        return fineCategoryRepository.save(c);
    }

    private void createFineConfig(Long catId, krs.erp.model.finance.FineConfiguration.CalculationLogic logic, Double b,
            krs.erp.model.finance.FineConfiguration.FineFrequency freq, Integer grace) {
        krs.erp.model.finance.FineConfiguration c = new krs.erp.model.finance.FineConfiguration();
        c.setCategoryId(catId);
        c.setCalcLogic(logic);
        c.setBaseAmount(b);
        c.setFrequency(freq);
        c.setGracePeriodDays(grace);
        fineConfigurationRepository.save(c);
    }

    private void createSampleInvoices() {
        krs.erp.model.finance.Invoice inv = new krs.erp.model.finance.Invoice();
        inv.setInvoiceNumber("INV-2025-001");
        inv.setStudentId(1L);
        inv.setIssueDate(LocalDate.now());
        inv.setDueDate(LocalDate.now().plusDays(15));
        inv.setTotalAmount(5000.0);
        inv.setNetAmount(5000.0);
        inv.setStatus(krs.erp.model.finance.Invoice.InvoiceStatus.PARTIALLY_PAID);
        krs.erp.model.finance.Invoice savedInv = invoiceRepository.save(inv);

        krs.erp.model.finance.InvoiceItem item = new krs.erp.model.finance.InvoiceItem();
        item.setInvoiceId(savedInv.getId());
        item.setDescription("Late Fee - Previous Month");
        item.setAmount(5000.0);
        item.setItemType(krs.erp.model.finance.InvoiceItem.ItemType.FINE);
        invoiceItemRepository.save(item);

        krs.erp.model.finance.Transaction txn = new krs.erp.model.finance.Transaction();
        txn.setInvoiceId(savedInv.getId());
        txn.setAmountPaid(2000.0);
        txn.setPaymentDate(LocalDateTime.now());
        txn.setPaymentMode(krs.erp.model.finance.Transaction.PaymentMode.ONLINE);
        txn.setStatus(krs.erp.model.finance.Transaction.TransactionStatus.SUCCESS);
        transactionRepository.save(txn);
    }

    @Transactional
    public void generateScholarshipSampleData() {
        try {
            logger.info("Starting Scholarships Sample Data Generation...");

            // 1. Scholarship Categories
            ScholarshipCategory merit = new ScholarshipCategory();
            merit.setName("Academic Merit Scholarship");
            merit.setDescription("Awarded for students with GPA > 3.8");
            merit.setType(ScholarshipCategory.AidType.PERCENTAGE);
            merit.setPercentage(new java.math.BigDecimal("50.00"));
            merit.setMeritBased(true);
            merit.setNeedBased(false);
            scholarshipCategoryRepository.save(merit);

            ScholarshipCategory need = new ScholarshipCategory();
            need.setName("Institutional Need-Based Grant");
            need.setDescription("Financial aid for underprivileged backgrounds");
            need.setType(ScholarshipCategory.AidType.FIXED);
            need.setAmount(new java.math.BigDecimal("10000.00"));
            need.setMeritBased(false);
            need.setNeedBased(true);
            scholarshipCategoryRepository.save(need);

            // 2. Applications
            ScholarshipApplication app1 = new ScholarshipApplication();
            app1.setStudent(studentRepository.findById(1L).orElse(null));
            app1.setCategory(merit);
            app1.setAcademicYear(academicYearRepository.findById(1L).orElse(null));
            app1.setApplicationDate(LocalDate.now());
            app1.setStatus(ScholarshipApplication.ApplicationStatus.APPROVED);
            app1.setRemarks("GPA 3.9 verified");
            app1.setApprovedBy("Admin");
            app1.setApprovalDate(LocalDate.now());
            scholarshipApplicationRepository.save(app1);

            logger.info("Scholarships Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating scholarship data", e);
            throw new RuntimeException("Failed to generate scholarship data", e);
        }
    }

    @Transactional
    public void generateAccountingSampleData() {
        try {
            logger.info("Starting Accounting Sample Data Generation...");

            // 1. Accounting Period
            AccountingPeriod fy2024 = new AccountingPeriod();
            fy2024.setStartDate(LocalDate.of(2024, 4, 1));
            fy2024.setEndDate(LocalDate.of(2025, 3, 31));
            fy2024.setIsClosed(false);
            accountingPeriodRepository.save(fy2024);

            // 2. Chart of Accounts
            ChartOfAccount cash = new ChartOfAccount();
            cash.setName("Cash in Hand");
            cash.setType(AccountType.ASSET);
            chartOfAccountRepository.save(cash);

            ChartOfAccount bank = new ChartOfAccount();
            bank.setName("Main Bank Account");
            bank.setType(AccountType.ASSET);
            chartOfAccountRepository.save(bank);

            ChartOfAccount tuitionIncome = new ChartOfAccount();
            tuitionIncome.setName("Tuition Fee Income");
            tuitionIncome.setType(AccountType.REVENUE);
            chartOfAccountRepository.save(tuitionIncome);

            ChartOfAccount salaryExpense = new ChartOfAccount();
            salaryExpense.setName("Staff Salary Expense");
            salaryExpense.setType(AccountType.EXPENSE);
            chartOfAccountRepository.save(salaryExpense);

            // 3. Initial Journal Entry (Opening Balance)
            JournalEntry je = new JournalEntry();
            je.setEntryNumber("OP-001");
            je.setDescription("Opening Balances");
            je.setStatus(JournalEntry.EntryStatus.POSTED);

            JournalItem item1 = new JournalItem();
            item1.setJournalEntry(je);
            item1.setAccount(cash);
            item1.setDebit(new BigDecimal("50000.00"));
            item1.setCredit(BigDecimal.ZERO);
            item1.setLabel("Opening Cash");

            JournalItem item2 = new JournalItem();
            item2.setJournalEntry(je);
            item2.setAccount(bank);
            item2.setCredit(new BigDecimal("50000.00")); // Balanced for demo
            item2.setDebit(BigDecimal.ZERO);
            item2.setLabel("Initial Capital (Demo)");

            je.getItems().add(item2);
            journalEntryRepository.save(je);

            // 4. Budget
            Budget b = new Budget();
            b.setName("Administration Budget 2024");
            b.setAccountingPeriod(fy2024);
            b.setTotalAmount(new BigDecimal("1000000.00"));
            b.setStatus(Budget.BudgetStatus.APPROVED);

            BudgetLine bl1 = new BudgetLine();
            bl1.setBudget(b);
            bl1.setAccount(salaryExpense);
            bl1.setAllocatedAmount(new BigDecimal("800000.00"));
            bl1.setActualAmount(BigDecimal.ZERO);
            bl1.setDescription("Staff salaries");

            BudgetLine bl2 = new BudgetLine();
            bl2.setBudget(b);
            bl2.setAccount(tuitionIncome); // Just for demo
            bl2.setAllocatedAmount(new BigDecimal("50000.00"));
            bl2.setActualAmount(BigDecimal.ZERO);

            b.setLines(java.util.Arrays.asList(bl1, bl2));
            budgetRepository.save(b);

            // 5. Bank Statement
            BankStatement bs = new BankStatement();
            bs.setBankName("Global Bank");
            bs.setAccountNumber("6543210987");
            bs.setStatementDate(LocalDate.now());
            bs.setOpeningBalance(new BigDecimal("50000.00"));
            bs.setClosingBalance(new BigDecimal("45000.00"));

            BankStatementLine bsl1 = new BankStatementLine();
            bsl1.setBankStatement(bs);
            bsl1.setDate(LocalDate.now().minusDays(2));
            bsl1.setDescription("ATM Withdrawal");
            bsl1.setAmount(new BigDecimal("-5000.00"));
            bsl1.setReconciled(false);

            bs.setLines(java.util.Collections.singletonList(bsl1));
            bankStatementRepository.save(bs);

            logger.info("Accounting & Budgeting Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating accounting data", e);
            throw new RuntimeException("Failed to generate accounting data", e);
        }
    }

    private PerformanceCriteria createCriteria(String name, String desc) {
        PerformanceCriteria c = new PerformanceCriteria();
        c.setName(name);
        c.setDescription(desc);
        return performanceCriteriaRepository.save(c);
    }

    private void generateMessagingSampleData() {
        try {
            logger.info("Starting Communication & Engagement Sample Data Generation...");

            List<User> allUsers = userRepository.findAll();
            if (allUsers.size() < 2) {
                logger.warn("Not enough users to generate communication sample data");
                return;
            }

            User sender = allUsers.get(0);
            User recipient = allUsers.get(1);

            // 1. Messages
            Message msg = new Message();
            msg.setSender(sender);
            msg.setRecipient(recipient);
            msg.setSubject("Welcome to the ERP");
            msg.setBody("Hello! I hope you are enjoying the new Communication module.");
            msg.setSentAt(LocalDateTime.now().minusHours(2));
            msg.setRead(false);
            messageRepository.save(msg);

            // 2. Announcements
            Announcement ann = new Announcement();
            ann.setTitle("Annual Day Celebration");
            ann.setContent("The annual day celebration will be held on December 25th.");
            ann.setTargetAudience(Announcement.AudienceType.ALL);
            ann.setPublishedAt(LocalDateTime.now().minusDays(1));
            ann.setExpiresAt(LocalDateTime.now().plusDays(30));
            ann.setAuthor(sender);
            announcementRepository.save(ann);

            // 3. Support Tickets
            SupportTicket ticket = new SupportTicket();
            ticket.setTitle("Cannot access Fee Payment");
            ticket.setDescription("I am unable to see the fee payment button on my dashboard.");
            ticket.setCategory(SupportTicket.TicketCategory.FINANCE);
            ticket.setPriority(SupportTicket.TicketPriority.HIGH);
            ticket.setStatus(SupportTicket.TicketStatus.OPEN);
            ticket.setAuthor(recipient);
            ticket.setAssignedTo(sender);
            SupportTicket savedTicket = supportTicketRepository.save(ticket);

            // 4. Ticket Comment
            TicketComment comment = new TicketComment();
            comment.setTicket(savedTicket);
            comment.setAuthor(sender);
            comment.setComment("I am looking into this issue right now.");
            comment.setCreatedAt(LocalDateTime.now());
            ticketCommentRepository.save(comment);

            logger.info("Communication & Engagement Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating communication sample data", e);
            throw new RuntimeException("Failed to generate communication data", e);
        }
    }

    @Transactional
    public void generateLibrarySampleData() {
        try {
            logger.info("Starting Library Management Sample Data Generation...");

            List<User> allUsers = userRepository.findAll();
            if (allUsers.isEmpty()) {
                logger.warn("No users found to generate library sample data");
                return;
            }
            User admin = allUsers.get(0);
            User student = allUsers.size() > 1 ? allUsers.get(1) : admin;

            // 1. Authors & Publishers
            Author author = new Author();
            author.setName("Robert C. Martin");
            author.setBiography("Uncle Bob is a software engineer and author.");
            author = authorRepository.save(author);

            Publisher publisher = new Publisher();
            publisher.setName("Prentice Hall");
            publisher.setContactInfo("contact@prenticehall.com");
            publisher = publisherRepository.save(publisher);

            // 2. Resources & Items
            LibraryResource book = new LibraryResource();
            book.setTitle("Clean Code");
            book.setAuthor(author);
            book.setPublisher(publisher);
            book.setIsbnIssn("9780132350884");
            book.setFormat(LibraryResource.ResourceFormat.BOOK);
            book.setCategory("Software Engineering");
            book.setYear(2008);
            book = resourceRepository.save(book);

            ResourceItem item = new ResourceItem();
            item.setResource(book);
            item.setAccessionNumber("ACC-001");
            item.setBarcode("9780132350884-001");
            item.setLocation("Shelf A1");
            item.setStatus(ResourceItem.ItemStatus.AVAILABLE);
            itemRepository.save(item);

            // 3. Policies
            LibraryPolicy studentPolicy = new LibraryPolicy();
            studentPolicy.setRole(student.getRoles().iterator().next());
            studentPolicy.setMaxBooks(5);
            studentPolicy.setLoanPeriodDays(14);
            studentPolicy.setMaxRenewals(2);
            studentPolicy.setFinePerDay(new BigDecimal("5.00"));
            policyRepository.save(studentPolicy);

            // 4. Procurement
            LibraryPurchaseRequest request = new LibraryPurchaseRequest();
            request.setTitle("The Pragmatic Programmer");
            request.setAuthor("Andrew Hunt");
            request.setRequestedBy(student);
            request.setStatus(LibraryPurchaseRequest.RequestStatus.PENDING);
            request.setReason("Highly recommended for developers.");
            purchaseRequestRepository.save(request);

            logger.info("Library Management Sample Data Generation Completed.");

        } catch (Exception e) {
            logger.error("Error generating library sample data", e);
            throw new RuntimeException("Failed to generate library data", e);
        }
    }

    @Transactional
    public void generateLmsSampleData() {
        logger.info("Generating LMS sample data...");

        List<Subject> subjects = subjectRepository.findAll();
        if (subjects.isEmpty())
            return;

        Subject science = subjects.stream()
                .filter(s -> s.getSubjectName().toLowerCase().contains("science"))
                .findFirst().orElse(subjects.get(0));

        // 1. Create Module
        LmsModule introModule = new LmsModule();
        introModule.setSubject(science);
        introModule.setTitle("Introduction to General Science");
        introModule.setDescription("The fundamental concepts of scientific inquiry.");
        introModule.setOrderIndex(1);
        introModule.setPublished(true);
        introModule = lmsModuleRepository.save(introModule);

        // 2. Create Lesson
        Lesson lesson1 = new Lesson();
        lesson1.setModule(introModule);
        lesson1.setTitle("The Scientific Method");
        lesson1.setDescription("Steps of scientific investigation.");
        lesson1.setOrderIndex(1);
        lesson1.setPublished(true);
        lesson1 = lessonRepository.save(lesson1);

        // 3. Create Topic
        LmsTopic topic1 = new LmsTopic();
        topic1.setLesson(lesson1);
        topic1.setTitle("What is a Hypothesis?");
        topic1.setContent("A hypothesis is an educated guess...");
        topic1.setOrderIndex(1);
        lmsTopicRepository.save(topic1);

        // 4. Create Content
        LmsContent videoContent = new LmsContent();
        videoContent.setLesson(lesson1);
        videoContent.setTitle("Scientific Method Overview");
        videoContent.setType(LmsContent.ContentType.VIDEO);
        videoContent.setContentUrl("https://www.youtube.com/watch?v=qAJ8if4ZSrQ");
        lmsContentRepository.save(videoContent);

        // 5. Create Quiz
        LmsQuiz quiz = new LmsQuiz();
        quiz.setLesson(lesson1);
        quiz.setSubject(science);
        quiz.setTitle("Scientific Method Baseline");
        quiz.setDescription("Test your knowledge of the steps.");
        quiz.setTimeLimitMinutes(10);
        quiz.setPassingScore(70.0);
        quiz.setPublished(true);
        quiz = lmsQuizRepository.save(quiz);

        // 6. Create Questions & Answers
        LmsQuestion q1 = new LmsQuestion();
        q1.setQuiz(quiz);
        q1.setQuestionText("Identify the first step of the scientific method.");
        q1.setType(LmsQuestion.QuestionType.MULTIPLE_CHOICE);
        q1.setPoints(10.0);
        q1 = lmsQuestionRepository.save(q1);

        LmsAnswer a1 = new LmsAnswer();
        a1.setQuestion(q1);
        a1.setAnswerText("Observation");
        a1.setCorrect(true);
        lmsAnswerRepository.save(a1);

        LmsAnswer a2 = new LmsAnswer();
        a2.setQuestion(q1);
        a2.setAnswerText("Analysis");
        a2.setCorrect(false);
        lmsAnswerRepository.save(a2);

        // 7. Create Question Bank entries
        LmsQuestionBank qbEntry = new LmsQuestionBank();
        qbEntry.setSubject(science);
        qbEntry.setQuestionText("What is control in an experiment?");
        qbEntry.setType(LmsQuestion.QuestionType.MULTIPLE_CHOICE);
        qbEntry.setDifficulty("Medium");
        qbEntry.setTags("Science, Experiment");
        lmsQuestionBankRepository.save(qbEntry);

        // 8. Create Rubric
        LmsRubric rubric = new LmsRubric();
        rubric.setName("Experiment Report Rubric");
        rubric.setCriteria("Organization: 40%, Accuracy: 40%, Presentation: 20%");
        rubric.setMaxPoints(100.0);
        lmsRubricRepository.save(rubric);

        // 9. Virtual Sessions
        VirtualClassSession session = new VirtualClassSession();
        session.setLesson(lesson1);
        session.setTitle("Live Science Experiment - Volcanos");
        session.setProvider(VirtualClassSession.Provider.GOOGLE_MEET);
        session.setMeetingUrl("https://meet.google.com/abc-defg-hij");
        session.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        session.setEndTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0));
        session = virtualClassSessionRepository.save(session);

        // 10. Virtual Attendance
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            VirtualAttendanceRecord att = new VirtualAttendanceRecord();
            att.setSession(session);
            att.setUser(users.get(0));
            att.setJoinTime(session.getStartTime().plusMinutes(2));
            att.setLeaveTime(session.getEndTime().minusMinutes(5));
            att.setDurationMinutes(53);
            att.setAttended(true);
            virtualAttendanceRecordRepository.save(att);
        }

        // 11. Student Progress
        if (!users.isEmpty()) {
            LmsStudentProgress progress = new LmsStudentProgress();
            progress.setStudent(users.get(0));
            progress.setLesson(lesson1);
            progress.setCompletionPercentage(75.0);
            progress.setCompleted(false);
            progress.setTimeSpentMinutes(45);
            lmsStudentProgressRepository.save(progress);
        }

        // 12. Badges & Points
        LmsBadge badge = new LmsBadge();
        badge.setName("Science Explorer");
        badge.setDescription("Completed all lessons in the first module.");
        badge.setTier(LmsBadge.BadgeTier.SILVER);
        badge.setPointsRequired(100);
        badge = lmsBadgeRepository.save(badge);

        if (!users.isEmpty()) {
            LmsPointLog log = new LmsPointLog();
            log.setStudent(users.get(0));
            log.setPoints(50);
            log.setReason("Completed 'Introduction to Volcanos'");
            log.setBadgeAwarded(badge);
            lmsPointLogRepository.save(log);
        }

        // 13. Collaborative Learning (Forums & Peer Reviews)
        LmsForum forum = new LmsForum();
        forum.setCourse(introModule);
        forum.setTitle("General Discussion - Scientific Method");
        forum.setDescription("Discuss the first module here.");
        forum = lmsForumRepository.save(forum);

        if (!users.isEmpty()) {
            LmsForumPost post1 = new LmsForumPost();
            post1.setForum(forum);
            post1.setAuthor(users.get(0));
            post1.setContent("I find the step of 'Observation' fascinating!");
            post1.setLikes(5);
            post1 = lmsForumPostRepository.save(post1);

            LmsForumPost reply = new LmsForumPost();
            reply.setForum(forum);
            reply.setAuthor(users.size() > 1 ? users.get(1) : users.get(0));
            reply.setContent("Yes, it's the foundation of everything else.");
            reply.setParentPost(post1);
            lmsForumPostRepository.save(reply);
        }

        List<LmsSubmission> submissions = lmsSubmissionRepository.findAll();
        if (!submissions.isEmpty() && !users.isEmpty()) {
            LmsPeerReview review = new LmsPeerReview();
            review.setSubmission(submissions.get(0));
            review.setReviewer(users.size() > 1 ? users.get(1) : users.get(0));
            review.setScore(90);
            review.setFeedback("Very thorough analysis of the data.");
            review.setAnonymous(true);
            lmsPeerReviewRepository.save(review);
        }

        logger.info("LMS sample data generated successfully.");
    }

    @Transactional
    public void generateTpdSampleData() {
        logger.info("Generating TPD sample data...");

        List<User> staffList = userRepository.findAll();
        if (staffList.isEmpty())
            return;

        User staff = staffList.get(0);
        User manager = staffList.size() > 1 ? staffList.get(1) : staff;

        // 1. Create Competencies
        Competency techComp = new Competency();
        techComp.setName("Digital Classroom Mastery");
        techComp.setDescription("Ability to use LMS and interactive tools effectively.");
        techComp.setTargetRole("TEACHER");
        techComp.setRequiredLevel(4);
        techComp = competencyRepository.save(techComp);

        // 2. Skill Assessment
        SkillAssessment assessment = new SkillAssessment();
        assessment.setStaff(staff);
        assessment.setCompetency(techComp);
        assessment.setSelfRating(3);
        assessment.setManagerRating(4);
        assessment.setManager(manager);
        assessment.setFeedback("Good progress, needs more practice with H5P tools.");
        skillAssessmentRepository.save(assessment);

        // 3. Training Event
        TrainingEvent workshop = new TrainingEvent();
        workshop.setTitle("Summer Tech Intensive 2024");
        workshop.setType(TrainingEvent.EventType.INTERNAL);
        workshop.setStartDateTime(LocalDateTime.now().plusDays(5));
        workshop.setEndDateTime(LocalDateTime.now().plusDays(5).plusHours(4));
        workshop.setResourcePerson("Dr. Satish Kumar");
        workshop.setCost(500.0);
        workshop = trainingEventRepository.save(workshop);

        // 4. CPD Ledger
        CpdLedger ledger = new CpdLedger();
        ledger.setStaff(staff);
        ledger.setAcademicYear(2024);
        ledger.setCreditsEarned(12.5);
        ledger.setCreditsRequired(30.0);
        cpdLedgerRepository.save(ledger);

        // 5. Portfolio
        ProfessionalPortfolio portfolio = new ProfessionalPortfolio();
        portfolio.setStaff(staff);
        portfolio.setCurrentRole("Junior Teacher");
        portfolio.setTargetTrack("Senior Teacher");
        portfolio.setTotalCPDCredits(12.5);
        portfolio.setYearsOfService(3);
        portfolio.setPromotionReady(false);
        portfolio.setProfessionalSummary("Passionate educator focusing on STEM subjects.");
        professionalPortfolioRepository.save(portfolio);

        logger.info("TPD sample data generated successfully.");
    }

    @Transactional
    public void generateStudentRegistrationSampleData() {
        logger.info("Generating sample student registration data...");
        List<Student> activeStudents = studentRepository.findAll();
        List<AcademicYear> academicYears = academicYearRepository.findAll();
        List<ErpClass> classes = classRepository.findAll();

        if (activeStudents.isEmpty() || academicYears.isEmpty() || classes.isEmpty()) {
            logger.warn("Cannot generate registrations: Missing students, years, or classes.");
            return;
        }

        AcademicYear currentYear = academicYears.get(0);
        int count = 0;
        for (int i = 0; i < Math.min(activeStudents.size(), 20); i++) {
            Student student = activeStudents.get(i);
            ErpClass erpClass = classes.get(i % classes.size());

            StudentRegistration reg = new StudentRegistration();
            reg.setStudent(student);
            reg.setAcademicYear(currentYear);
            reg.setErpClass(erpClass);
            reg.setRegistrationDate(LocalDate.now().minusDays(i));
            reg.setStatus(i % 5 == 0 ? StudentRegistration.RegistrationStatus.PENDING
                    : StudentRegistration.RegistrationStatus.COMPLETED);
            reg.setRemarks("Auto-generated sample registration");
            reg.markAsActive();
            studentRegistrationRepository.save(reg);
            count++;
        }
        logger.info("✓ Generated {} sample student registrations", count);
    }

    // ==================== Finance XML Import Methods ====================

    @Transactional
    public void importFeeTypesDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting fee types import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList feeTypeList = document.getElementsByTagName("fee_type");
            for (int i = 0; i < feeTypeList.getLength(); i++) {
                Element feeTypeElement = (Element) feeTypeList.item(i);

                krs.erp.model.finance.FeeType feeType = new krs.erp.model.finance.FeeType();
                feeType.setName(getElementText(feeTypeElement, "name"));
                feeType.setCode(getElementText(feeTypeElement, "code"));
                feeType.setDescription(getElementText(feeTypeElement, "description"));

                feeTypeRepository.save(feeType);
            }
            logger.info("Fee types import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing fee types from XML", e);
            throw new RuntimeException("Failed to import fee types", e);
        }
    }

    @Transactional
    public void importFeeStructuresDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting fee structures import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList structureList = document.getElementsByTagName("fee_structure");
            for (int i = 0; i < structureList.getLength(); i++) {
                Element structureElement = (Element) structureList.item(i);

                krs.erp.model.finance.FeeStructure structure = new krs.erp.model.finance.FeeStructure();

                // Find fee type by code
                String feeTypeCode = getElementText(structureElement, "fee_type_code");
                krs.erp.model.finance.FeeType feeType = feeTypeRepository.findAll().stream()
                        .filter(ft -> ft.getCode().equals(feeTypeCode))
                        .findFirst()
                        .orElse(null);

                if (feeType != null) {
                    structure.setFeeTypeId(feeType.getId());
                }

                structure.setAcademicYearId(Long.parseLong(getElementText(structureElement, "academic_year_id")));
                structure.setAmount(Double.parseDouble(getElementText(structureElement, "amount")));
                structure.setDueDate(LocalDate.parse(getElementText(structureElement, "due_date")));

                feeStructureRepository.save(structure);
            }
            logger.info("Fee structures import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing fee structures from XML", e);
            throw new RuntimeException("Failed to import fee structures", e);
        }
    }

    @Transactional
    public void importDiscountRulesDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting discount rules import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList ruleList = document.getElementsByTagName("discount_rule");
            for (int i = 0; i < ruleList.getLength(); i++) {
                Element ruleElement = (Element) ruleList.item(i);

                krs.erp.model.finance.FeeDiscountRule rule = new krs.erp.model.finance.FeeDiscountRule();
                rule.setName(getElementText(ruleElement, "name"));
                rule.setType(krs.erp.model.finance.FeeDiscountRule.DiscountType
                        .valueOf(getElementText(ruleElement, "type")));
                rule.setValue(Double.parseDouble(getElementText(ruleElement, "value")));
                rule.setConditionType(getElementText(ruleElement, "condition_type"));
                // Note: description field not available in FeeDiscountRule model

                feeDiscountRuleRepository.save(rule);
            }
            logger.info("Discount rules import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing discount rules from XML", e);
            throw new RuntimeException("Failed to import discount rules", e);
        }
    }

    @Transactional
    public void importFineCategoriesDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting fine categories import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList categoryList = document.getElementsByTagName("fine_category");
            for (int i = 0; i < categoryList.getLength(); i++) {
                Element categoryElement = (Element) categoryList.item(i);

                krs.erp.model.finance.FineCategory category = new krs.erp.model.finance.FineCategory();
                category.setName(getElementText(categoryElement, "name"));
                category.setDescription(getElementText(categoryElement, "description"));

                fineCategoryRepository.save(category);
            }
            logger.info("Fine categories import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing fine categories from XML", e);
            throw new RuntimeException("Failed to import fine categories", e);
        }
    }

    @Transactional
    public void importInvoicesDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting invoices import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList invoiceList = document.getElementsByTagName("invoice");
            for (int i = 0; i < invoiceList.getLength(); i++) {
                Element invoiceElement = (Element) invoiceList.item(i);

                krs.erp.model.finance.Invoice invoice = new krs.erp.model.finance.Invoice();
                invoice.setInvoiceNumber(getElementText(invoiceElement, "invoice_number"));
                invoice.setStudentId(Long.parseLong(getElementText(invoiceElement, "student_id")));
                invoice.setIssueDate(LocalDate.parse(getElementText(invoiceElement, "issue_date")));
                invoice.setDueDate(LocalDate.parse(getElementText(invoiceElement, "due_date")));
                invoice.setTotalAmount(Double.parseDouble(getElementText(invoiceElement, "total_amount")));
                invoice.setNetAmount(Double.parseDouble(getElementText(invoiceElement, "net_amount")));
                invoice.setStatus(
                        krs.erp.model.finance.Invoice.InvoiceStatus.valueOf(getElementText(invoiceElement, "status")));

                krs.erp.model.finance.Invoice savedInvoice = invoiceRepository.save(invoice);

                // Import invoice items
                NodeList itemsList = invoiceElement.getElementsByTagName("item");
                for (int j = 0; j < itemsList.getLength(); j++) {
                    Element itemElement = (Element) itemsList.item(j);

                    krs.erp.model.finance.InvoiceItem item = new krs.erp.model.finance.InvoiceItem();
                    item.setInvoiceId(savedInvoice.getId());
                    item.setDescription(getElementText(itemElement, "description"));
                    item.setAmount(Double.parseDouble(getElementText(itemElement, "amount")));
                    item.setItemType(krs.erp.model.finance.InvoiceItem.ItemType
                            .valueOf(getElementText(itemElement, "item_type")));

                    invoiceItemRepository.save(item);
                }
            }
            logger.info("Invoices import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing invoices from XML", e);
            throw new RuntimeException("Failed to import invoices", e);
        }
    }

    @Transactional
    public void importTransactionsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting transactions import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList transactionList = document.getElementsByTagName("transaction");
            for (int i = 0; i < transactionList.getLength(); i++) {
                Element transactionElement = (Element) transactionList.item(i);

                krs.erp.model.finance.Transaction transaction = new krs.erp.model.finance.Transaction();
                transaction.setTransactionIdExt(getElementText(transactionElement, "transaction_id_ext"));

                // Find invoice by invoice number
                String invoiceNumber = getElementText(transactionElement, "invoice_number");
                krs.erp.model.finance.Invoice invoice = invoiceRepository.findAll().stream()
                        .filter(inv -> inv.getInvoiceNumber().equals(invoiceNumber))
                        .findFirst()
                        .orElse(null);

                if (invoice != null) {
                    transaction.setInvoiceId(invoice.getId());
                }

                transaction.setPaymentDate(LocalDateTime.parse(getElementText(transactionElement, "payment_date")));
                transaction.setAmountPaid(Double.parseDouble(getElementText(transactionElement, "amount_paid")));
                transaction.setPaymentMode(krs.erp.model.finance.Transaction.PaymentMode
                        .valueOf(getElementText(transactionElement, "payment_mode")));
                transaction.setReferenceNumber(getElementText(transactionElement, "reference_number"));
                transaction.setStatus(krs.erp.model.finance.Transaction.TransactionStatus
                        .valueOf(getElementText(transactionElement, "status")));

                transactionRepository.save(transaction);
            }
            logger.info("Transactions import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing transactions from XML", e);
            throw new RuntimeException("Failed to import transactions", e);
        }
    }

    @Transactional
    public void importFeePaymentsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting fee payments import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList paymentList = document.getElementsByTagName("fee_payment");
            for (int i = 0; i < paymentList.getLength(); i++) {
                Element paymentElement = (Element) paymentList.item(i);

                krs.erp.model.finance.FeePayment payment = new krs.erp.model.finance.FeePayment();
                payment.setStudentId(Long.parseLong(getElementText(paymentElement, "student_id")));
                payment.setFeeStructureId(Long.parseLong(getElementText(paymentElement, "fee_structure_id")));
                payment.setBaseAmount(Double.parseDouble(getElementText(paymentElement, "base_amount")));
                payment.setDiscountAmount(Double.parseDouble(getElementText(paymentElement, "discount_amount")));
                payment.setFineAmount(Double.parseDouble(getElementText(paymentElement, "fine_amount")));
                payment.setNetAmount(Double.parseDouble(getElementText(paymentElement, "net_amount")));
                payment.setPaymentDate(LocalDateTime.parse(getElementText(paymentElement, "payment_date")));
                payment.setPaymentMode(krs.erp.model.finance.FeePayment.PaymentMode
                        .valueOf(getElementText(paymentElement, "payment_mode")));
                payment.setReceiptNumber(getElementText(paymentElement, "receipt_number"));
                payment.setRemarks(getElementText(paymentElement, "remarks"));

                feePaymentRepository.save(payment);
            }
            logger.info("Fee payments import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing fee payments from XML", e);
            throw new RuntimeException("Failed to import fee payments", e);
        }
    }

    @Transactional
    public void importChartOfAccountsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting chart of accounts import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList accountList = document.getElementsByTagName("account");
            for (int i = 0; i < accountList.getLength(); i++) {
                Element accountElement = (Element) accountList.item(i);

                ChartOfAccount account = new ChartOfAccount();
                account.setName(getElementText(accountElement, "name"));
                account.setType(AccountType.valueOf(getElementText(accountElement, "type")));
                // Note: description field not available in ChartOfAccount model

                chartOfAccountRepository.save(account);
            }
            logger.info("Chart of accounts import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing chart of accounts from XML", e);
            throw new RuntimeException("Failed to import chart of accounts", e);
        }
    }

    @Transactional
    public void importJournalEntriesDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting journal entries import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList entryList = document.getElementsByTagName("journal_entry");
            for (int i = 0; i < entryList.getLength(); i++) {
                Element entryElement = (Element) entryList.item(i);

                JournalEntry entry = new JournalEntry();
                entry.setEntryNumber(getElementText(entryElement, "entry_number"));
                entry.setDescription(getElementText(entryElement, "description"));
                entry.setStatus(JournalEntry.EntryStatus.valueOf(getElementText(entryElement, "status")));

                JournalEntry savedEntry = journalEntryRepository.save(entry);

                // Import journal items
                NodeList itemsList = entryElement.getElementsByTagName("item");
                for (int j = 0; j < itemsList.getLength(); j++) {
                    Element itemElement = (Element) itemsList.item(j);

                    // Find account by name
                    String accountName = getElementText(itemElement, "account_name");
                    ChartOfAccount account = chartOfAccountRepository.findAll().stream()
                            .filter(acc -> acc.getName().equals(accountName))
                            .findFirst()
                            .orElse(null);

                    if (account != null) {
                        JournalItem item = new JournalItem();
                        item.setJournalEntry(savedEntry);
                        item.setAccount(account);
                        item.setLabel(getElementText(itemElement, "label"));
                        item.setDebit(new BigDecimal(getElementText(itemElement, "debit")));
                        item.setCredit(new BigDecimal(getElementText(itemElement, "credit")));

                        journalItemRepository.save(item);
                    }
                }
            }
            logger.info("Journal entries import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing journal entries from XML", e);
            throw new RuntimeException("Failed to import journal entries", e);
        }
    }

    @Transactional
    public void importBudgetsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting budgets import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // First ensure we have an accounting period
            LocalDate periodStart = LocalDate.of(2024, 4, 1);
            LocalDate periodEnd = LocalDate.of(2025, 3, 31);
            AccountingPeriod period = accountingPeriodRepository.findAll().stream()
                    .filter(p -> p.getStartDate().equals(periodStart) && p.getEndDate().equals(periodEnd))
                    .findFirst()
                    .orElseGet(() -> {
                        AccountingPeriod newPeriod = new AccountingPeriod();
                        newPeriod.setStartDate(periodStart);
                        newPeriod.setEndDate(periodEnd);
                        newPeriod.setIsClosed(false);
                        return accountingPeriodRepository.save(newPeriod);
                    });

            NodeList budgetList = document.getElementsByTagName("budget");
            for (int i = 0; i < budgetList.getLength(); i++) {
                Element budgetElement = (Element) budgetList.item(i);

                Budget budget = new Budget();
                budget.setName(getElementText(budgetElement, "name"));
                budget.setAccountingPeriod(period);
                budget.setTotalAmount(new BigDecimal(getElementText(budgetElement, "total_amount")));
                budget.setStatus(Budget.BudgetStatus.valueOf(getElementText(budgetElement, "status")));

                Budget savedBudget = budgetRepository.save(budget);

                // Import budget lines
                NodeList linesList = budgetElement.getElementsByTagName("budget_line");
                for (int j = 0; j < linesList.getLength(); j++) {
                    Element lineElement = (Element) linesList.item(j);

                    // Find account by name
                    String accountName = getElementText(lineElement, "account_name");
                    ChartOfAccount account = chartOfAccountRepository.findAll().stream()
                            .filter(acc -> acc.getName().equals(accountName))
                            .findFirst()
                            .orElse(null);

                    if (account != null) {
                        BudgetLine line = new BudgetLine();
                        line.setBudget(savedBudget);
                        line.setAccount(account);
                        line.setAllocatedAmount(new BigDecimal(getElementText(lineElement, "allocated_amount")));
                        line.setActualAmount(new BigDecimal(getElementText(lineElement, "actual_amount")));
                        line.setDescription(getElementText(lineElement, "description"));

                        budgetLineRepository.save(line);
                    }
                }
            }
            logger.info("Budgets import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing budgets from XML", e);
            throw new RuntimeException("Failed to import budgets", e);
        }
    }

    @Transactional
    public void importFineConfigurationsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting fine configurations import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList configList = document.getElementsByTagName("configuration");
            for (int i = 0; i < configList.getLength(); i++) {
                Element configElement = (Element) configList.item(i);

                FineConfiguration config = new FineConfiguration();
                config.setCategoryId(Long.parseLong(getElementText(configElement, "categoryId")));
                config.setCalcLogic(
                        FineConfiguration.CalculationLogic.valueOf(getElementText(configElement, "calcLogic")));
                config.setBaseAmount(Double.parseDouble(getElementText(configElement, "baseAmount")));
                config.setFrequency(
                        FineConfiguration.FineFrequency.valueOf(getElementText(configElement, "frequency")));
                config.setGracePeriodDays(Integer.parseInt(getElementText(configElement, "gracePeriodDays")));

                String isActiveStr = getElementText(configElement, "isActive");
                if (isActiveStr != null) {
                    config.setIsActive(Boolean.parseBoolean(isActiveStr) ? 1 : 0);
                }

                fineConfigurationRepository.save(config);
            }
            logger.info("Fine configurations import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing fine configurations from XML", e);
            throw new RuntimeException("Failed to import fine configurations", e);
        }
    }

    @Transactional
    public void importFineLedgerDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting fine ledger import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList entryList = document.getElementsByTagName("entry");
            for (int i = 0; i < entryList.getLength(); i++) {
                Element entryElement = (Element) entryList.item(i);

                StudentFineLedger entry = new StudentFineLedger();
                entry.setStudentId(Long.parseLong(getElementText(entryElement, "student_id")));
                entry.setFineConfigId(Long.parseLong(getElementText(entryElement, "fine_config_id")));
                entry.setBaseAmount(Double.parseDouble(getElementText(entryElement, "base_amount")));
                entry.setAccruedAmount(Double.parseDouble(getElementText(entryElement, "accrued_amount")));
                entry.setStatus(StudentFineLedger.FineStatus.valueOf(getElementText(entryElement, "status")));
                entry.setIssuedAt(LocalDateTime.parse(getElementText(entryElement, "issued_at") + "T00:00:00"));

                fineLedgerRepository.save(entry);
            }
            logger.info("Fine ledger import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing fine ledger from XML", e);
            throw new RuntimeException("Failed to import fine ledger", e);
        }
    }

    @Transactional
    public void importDisciplinaryIncidentsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting disciplinary incidents import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList incidentList = document.getElementsByTagName("incident");
            for (int i = 0; i < incidentList.getLength(); i++) {
                Element incidentElement = (Element) incidentList.item(i);

                DisciplinaryIncident incident = new DisciplinaryIncident();
                incident.setStudentId(Long.parseLong(getElementText(incidentElement, "student_id")));
                incident.setReportedById(Long.parseLong(getElementText(incidentElement, "reported_by_id")));
                incident.setDescription(getElementText(incidentElement, "description"));
                incident.setEvidenceUrl(getElementText(incidentElement, "evidence_url"));
                incident.setFineAmount(Double.parseDouble(getElementText(incidentElement, "fine_amount")));
                incident.setIncidentDate(
                        LocalDateTime.parse(getElementText(incidentElement, "incident_date") + "T00:00:00"));
                incident.setApprovalStatus(
                        DisciplinaryIncident.ApprovalStatus.valueOf(getElementText(incidentElement, "status")));

                disciplinaryIncidentRepository.save(incident);
            }
            logger.info("Disciplinary incidents import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing disciplinary incidents from XML", e);
            throw new RuntimeException("Failed to import disciplinary incidents", e);
        }
    }

    @Transactional
    public void importFineWaiverRequestsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting fine waiver requests import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList requestList = document.getElementsByTagName("request");
            for (int i = 0; i < requestList.getLength(); i++) {
                Element requestElement = (Element) requestList.item(i);

                FineWaiverRequest request = new FineWaiverRequest();
                request.setFineLedgerId(Long.parseLong(getElementText(requestElement, "fine_ledger_id")));
                request.setRequestedById(Long.parseLong(getElementText(requestElement, "requested_by_id")));
                request.setReason(getElementText(requestElement, "reason"));
                request.setStatus(FineWaiverRequest.WaiverStatus.valueOf(getElementText(requestElement, "status")));
                request.setRequestDate(
                        LocalDateTime.parse(getElementText(requestElement, "request_date") + "T00:00:00"));

                String approvedById = getElementText(requestElement, "approved_by_id");
                if (approvedById != null && !approvedById.isEmpty()) {
                    request.setApprovedById(Long.parseLong(approvedById));
                }

                String adjustmentAmount = getElementText(requestElement, "adjustment_amount");
                if (adjustmentAmount != null && !adjustmentAmount.isEmpty()) {
                    request.setAdjustmentAmount(Double.parseDouble(adjustmentAmount));
                }

                fineWaiverRequestRepository.save(request);
            }
            logger.info("Fine waiver requests import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing fine waiver requests from XML", e);
            throw new RuntimeException("Failed to import fine waiver requests", e);
        }
    }

    @Transactional
    public void importInvoiceItemsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting invoice items import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList itemList = document.getElementsByTagName("item");
            for (int i = 0; i < itemList.getLength(); i++) {
                Element itemElement = (Element) itemList.item(i);

                InvoiceItem item = new InvoiceItem();
                item.setInvoiceId(Long.parseLong(getElementText(itemElement, "invoice_id")));
                item.setDescription(getElementText(itemElement, "description"));
                item.setItemType(InvoiceItem.ItemType.valueOf(getElementText(itemElement, "item_type")));
                item.setAmount(Double.parseDouble(getElementText(itemElement, "amount")));

                invoiceItemRepository.save(item);
            }
            logger.info("Invoice items import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing invoice items from XML", e);
            throw new RuntimeException("Failed to import invoice items", e);
        }
    }

    @Transactional
    public void importAccountingPeriodsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting accounting periods import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList periodList = document.getElementsByTagName("period");
            for (int i = 0; i < periodList.getLength(); i++) {
                Element periodElement = (Element) periodList.item(i);

                AccountingPeriod period = new AccountingPeriod();
                period.setStartDate(LocalDate.parse(getElementText(periodElement, "start_date")));
                period.setEndDate(LocalDate.parse(getElementText(periodElement, "end_date")));
                period.setIsClosed(Boolean.parseBoolean(getElementText(periodElement, "is_closed")));

                accountingPeriodRepository.save(period);
            }
            logger.info("Accounting periods import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing accounting periods from XML", e);
            throw new RuntimeException("Failed to import accounting periods", e);
        }
    }

    @Transactional
    public void importBankStatementsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting bank statements import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList statementList = document.getElementsByTagName("statement");
            for (int i = 0; i < statementList.getLength(); i++) {
                Element statementElement = (Element) statementList.item(i);

                BankStatement statement = new BankStatement();
                statement.setAccountNumber(getElementText(statementElement, "accountNumber"));
                statement.setBankName(getElementText(statementElement, "bankName"));
                statement.setStatementDate(LocalDate.parse(getElementText(statementElement, "statementDate")));
                statement.setOpeningBalance(new BigDecimal(getElementText(statementElement, "openingBalance")));
                statement.setClosingBalance(new BigDecimal(getElementText(statementElement, "closingBalance")));

                bankStatementRepository.save(statement);
            }
            logger.info("Bank statements import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing bank statements from XML", e);
            throw new RuntimeException("Failed to import bank statements", e);
        }
    }

    @Transactional
    public void importScholarshipsDataFromXml(String xmlFilePath) {
        try {
            logger.info("Starting scholarships import from XML: {}", xmlFilePath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            if (inputStream == null) {
                logger.error("XML file not found: {}", xmlFilePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Import categories first
            NodeList categoryList = document.getElementsByTagName("category");
            for (int i = 0; i < categoryList.getLength(); i++) {
                Element categoryElement = (Element) categoryList.item(i);

                ScholarshipCategory category = new ScholarshipCategory();
                category.setName(getElementText(categoryElement, "name"));
                category.setDescription(getElementText(categoryElement, "description"));
                category.setType(ScholarshipCategory.AidType.valueOf(getElementText(categoryElement, "type")));

                String percentageStr = getElementText(categoryElement, "percentage");
                if (percentageStr != null && !percentageStr.isEmpty()) {
                    category.setPercentage(new BigDecimal(percentageStr));
                }

                String amountStr = getElementText(categoryElement, "amount");
                if (amountStr != null && !amountStr.isEmpty()) {
                    category.setAmount(new BigDecimal(amountStr));
                }

                category.setMeritBased(Boolean.parseBoolean(getElementText(categoryElement, "isMeritBased")));
                category.setNeedBased(Boolean.parseBoolean(getElementText(categoryElement, "isNeedBased")));

                scholarshipCategoryRepository.save(category);
            }

            // Import applications - skip for now as they require Student and AcademicYear
            // entities
            logger.info(
                    "Note: Scholarship applications require existing student and academic year records. Skipping application import.");

            logger.info("Scholarships import completed successfully");
        } catch (Exception e) {
            logger.error("Error importing scholarships from XML", e);
            throw new RuntimeException("Failed to import scholarships", e);
        }
    }

    private Document parseXmlFile(String xmlFilePath) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
        if (inputStream == null) {
            throw new java.io.FileNotFoundException("XML file not found: " + xmlFilePath);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();
        return document;
    }

    private String getElementTextContent(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}
