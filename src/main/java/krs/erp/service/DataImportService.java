package krs.erp.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.Address;
import krs.erp.model.Attendance;
import krs.erp.model.Course;
import krs.erp.model.ErpClass;
import krs.erp.model.Exam;
import krs.erp.model.Grade;
import krs.erp.model.HealthRecord;
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
import krs.erp.repository.AddressRepository;
import krs.erp.repository.AttendanceRepository;
import krs.erp.repository.CourseRepository;
import krs.erp.repository.ErpClassRepository;
import krs.erp.repository.ExamRepository;
import krs.erp.repository.GradeRepository;
import krs.erp.repository.HealthRecordRepository;
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

@Service
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

            logger.info("Data import completed successfully");

        } catch (Exception e) {
            logger.error("Error importing data from XML", e);
            throw new RuntimeException("Failed to import data from XML", e);
        }
    }

    @Transactional
    public void importStudentDataFromXml(String xmlFilePath) {
        try {
            logger.debug("Starting student data import from XML file: {}", xmlFilePath);

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
                    if (student != null) {
                        students.put(studentId, student);
                    }
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
        } catch (Exception e) {
            // Ignore if fields don't exist or can't be set
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
}