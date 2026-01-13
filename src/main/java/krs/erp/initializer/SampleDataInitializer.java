package krs.erp.initializer;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.Attendance;
import krs.erp.model.Permission;
import krs.erp.model.Role;
import krs.erp.model.Staff;
import krs.erp.model.User;
import krs.erp.model.calendar.Holiday;
import krs.erp.model.hr.Department;
import krs.erp.model.hr.Designation;
import krs.erp.model.hr.JobApplication;
import krs.erp.model.hr.JobPosting;
import krs.erp.model.hr.LeaveBalance;
import krs.erp.model.hr.LeaveRequest;
import krs.erp.model.hr.LeaveType;
import krs.erp.model.hr.PayrollRun;
import krs.erp.model.hr.Payslip;
import krs.erp.model.tpd.Competency;
import krs.erp.repository.AttendanceRepository;
import krs.erp.repository.OrganizationRepository;
import krs.erp.repository.PermissionRepository;
import krs.erp.repository.RoleRepository;
import krs.erp.repository.StaffRepository;
import krs.erp.repository.UserRepository;
import krs.erp.repository.calendar.HolidayRepository;
import krs.erp.repository.hr.DepartmentRepository;
import krs.erp.repository.hr.DesignationRepository;
import krs.erp.repository.hr.JobApplicationRepository;
import krs.erp.repository.hr.JobPostingRepository;
import krs.erp.repository.hr.LeaveBalanceRepository;
import krs.erp.repository.hr.LeaveRequestRepository;
import krs.erp.repository.hr.LeaveTypeRepository;
import krs.erp.repository.hr.PayrollRunRepository;
import krs.erp.repository.hr.PayslipRepository;
import krs.erp.repository.tpd.CompetencyRepository;

/**
 * Unified SampleDataInitializer that loads all sample data (Permissions, Roles,
 * Organizations, Staff, Users)
 * from their respective XML files in a single initializer component.
 * 
 * Execution Order:
 * @Order(0) - Highest priority among initializers to load base system data
 * first
 * 
 * This initializer replaces individual initializers:
 * - PermissionDataInitializer
 * - RoleDataInitializer
 * - OrganizationDataInitializer
 * - StaffDataInitializer
 * - UserDataInitializer
 * 
 * Data is loaded from:
 * - data/permission/sample-permissions.xml
 * - data/role/sample-roles.xml
 * - data/organisation/sample-organisations.xml
 * - data/staff/sample-staff.xml
 * - data/user/sample-users.xml
 */
// @Component - Disabled for multi-tenant system
@Order(0)
public class SampleDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataInitializer.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @Autowired
    private CompetencyRepository competencyRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting unified sample data initialization...");

        try {
            logger.info("Loading sample data from organized XML files...");

            // Load data in proper order (dependencies first)
            loadPermissions();
            loadRoles();
            // DISABLED: Organization data should be populated via registration form, not
            // auto-initialization
            // loadOrganizations();
            loadStaff();
            loadUsers();
            loadPayslips();
            loadCompetencies();
            
            // Load HR sample data
            loadDepartments();
            loadDesignations();
            loadPayrollRuns();
            loadLeaveTypes();
            loadHolidays();
            loadJobPostings();
            loadJobApplications();
            loadLeaveRequests();
            loadLeaveBalances();
            loadAttendanceRecords();

            logSummary();

        } catch (Exception e) {
            logger.error("Error during sample data initialization", e);
        }
    }

    private void loadPermissions() {
        logger.info("Loading Permissions from data/permission/sample-permissions.xml...");
        try {
            if (permissionRepository.count() > 0) {
                logger.info("Permissions already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/permission/sample-permissions.xml");
            if (inputStream == null) {
                logger.warn("Permissions XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList permissionList = document.getElementsByTagName("permissions");
            int loadedCount = 0;

            for (int i = 0; i < permissionList.getLength(); i++) {
                Element element = (Element) permissionList.item(i);
                Permission permission = mapToPermission(element);
                permissionRepository.save(permission);
                loadedCount++;
            }

            logger.info("✓ Loaded {} permissions", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading permissions", e);
        }
    }

    private void loadRoles() {
        logger.info("Loading Roles from data/role/sample-roles.xml...");
        try {
            if (roleRepository.count() > 0) {
                logger.info("Roles already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/role/sample-roles.xml");
            if (inputStream == null) {
                logger.warn("Roles XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList roleList = document.getElementsByTagName("roles");
            int loadedCount = 0;

            for (int i = 0; i < roleList.getLength(); i++) {
                Element element = (Element) roleList.item(i);
                Role role = mapToRole(element);
                roleRepository.save(role);
                loadedCount++;
            }

            logger.info("✓ Loaded {} roles", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading roles", e);
        }
    }

    private void loadStaff() {
        logger.info("Loading Staff from data/staff/sample-staff.xml...");
        try {
            if (staffRepository.count() > 0) {
                logger.info("Staff already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/staff/sample-staff.xml");
            if (inputStream == null) {
                logger.warn("Staff XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList staffList = document.getElementsByTagName("staff");
            int loadedCount = 0;

            for (int i = 0; i < staffList.getLength(); i++) {
                Element element = (Element) staffList.item(i);
                Staff staff = mapToStaff(element);
                staffRepository.save(staff);
                loadedCount++;
            }

            logger.info("✓ Loaded {} staff members", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading staff", e);
        }
    }

    private void loadUsers() {
        logger.info("Loading Users from data/user/sample-users.xml...");
        try {
            if (userRepository.count() > 0) {
                logger.info("Users already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/user/sample-users.xml");
            if (inputStream == null) {
                logger.warn("Users XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList userList = document.getElementsByTagName("users");
            int loadedCount = 0;

            for (int i = 0; i < userList.getLength(); i++) {
                Element element = (Element) userList.item(i);
                User user = mapToUser(element);
                userRepository.save(user);
                loadedCount++;
            }

            logger.info("✓ Loaded {} users", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading users", e);
        }
    }

    private Document parseXml(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();
        return document;
    }

    private Permission mapToPermission(Element element) {
        Permission permission = new Permission();
        permission.setName(element.getAttribute("name"));
        permission.setDescription(element.getAttribute("description"));
        permission.setResource(element.getAttribute("resource"));
        permission.setAction(element.getAttribute("action"));
        permission.setSystemPermission("1".equals(element.getAttribute("system_permission")));
        return permission;
    }

    private Role mapToRole(Element element) {
        Role role = new Role();
        role.setName(element.getAttribute("name"));
        role.setDescription(element.getAttribute("description"));
        role.setSystemRole("1".equals(element.getAttribute("system_role")));
        return role;
    }

    private Staff mapToStaff(Element element) {
        Staff staff = new Staff();
        staff.setFirstName(element.getAttribute("first_name"));
        staff.setLastName(element.getAttribute("last_name"));
        staff.setMiddleName(element.getAttribute("middle_name"));
        staff.setStaffId(element.getAttribute("staff_id"));
        staff.setEmail(element.getAttribute("email"));
        staff.setPhone(element.getAttribute("phone"));

        String dateOfBirth = element.getAttribute("date_of_birth");
        if (!dateOfBirth.isEmpty()) {
            try {
                staff.setDateOfBirth(LocalDate.parse(dateOfBirth, DATE_FORMATTER));
            } catch (Exception e) {
                logger.warn("Invalid date_of_birth format: {}", dateOfBirth);
            }
        }

        String gender = element.getAttribute("gender");
        if (!gender.isEmpty()) {
            try {
                staff.setGender(Staff.Gender.valueOf(gender));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid gender value: {}", gender);
            }
        }

        String hireDate = element.getAttribute("hire_date");
        if (!hireDate.isEmpty()) {
            try {
                staff.setHireDate(LocalDate.parse(hireDate, DATE_FORMATTER));
            } catch (Exception e) {
                logger.warn("Invalid hire_date format: {}", hireDate);
            }
        }

        String employmentStatus = element.getAttribute("employment_status");
        if (!employmentStatus.isEmpty()) {
            try {
                staff.setEmploymentStatus(Staff.EmploymentStatus.valueOf(employmentStatus));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid employment_status value: {}", employmentStatus);
            }
        }

        String staffType = element.getAttribute("staff_type");
        if (!staffType.isEmpty()) {
            try {
                staff.setStaffType(Staff.StaffType.valueOf(staffType));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid staff_type value: {}, defaulting to OTHER", staffType);
                staff.setStaffType(Staff.StaffType.OTHER);
            }
        } else {
            staff.setStaffType(Staff.StaffType.OTHER);
        }

        staff.setDepartment(element.getAttribute("department"));
        staff.setPosition(element.getAttribute("position"));
        staff.setQualification(element.getAttribute("qualification"));

        String experienceYears = element.getAttribute("experience_years");
        if (!experienceYears.isEmpty()) {
            try {
                staff.setExperienceYears(Integer.parseInt(experienceYears));
            } catch (NumberFormatException e) {
                logger.warn("Invalid experience_years format: {}", experienceYears);
            }
        }

        staff.markAsActive();
        return staff;
    }

    private User mapToUser(Element element) {
        User user = new User();
        user.setUsername(element.getAttribute("username"));
        user.setPasswordHash(element.getAttribute("password_hash"));
        user.setEmail(element.getAttribute("email"));
        user.setFirstName(element.getAttribute("first_name"));
        user.setLastName(element.getAttribute("last_name"));
        user.setPhone(element.getAttribute("phone"));

        String userType = element.getAttribute("user_type");
        if (!userType.isEmpty()) {
            try {
                user.setUserType(User.UserType.valueOf(userType));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid user_type value: {}", userType);
            }
        }

        user.setEnabled("1".equals(element.getAttribute("enabled")));
        user.setAccountNonExpired(!"0".equals(element.getAttribute("account_expired")));
        user.setCredentialsNonExpired(!"0".equals(element.getAttribute("credentials_expired")));
        user.setAccountNonLocked(!"0".equals(element.getAttribute("account_locked")));

        user.markAsActive();
        return user;
    }

    private void logSummary() {
        logger.info("========== SAMPLE DATA INITIALIZATION SUMMARY ==========");
        logger.info("Permissions: {}", permissionRepository.count());
        logger.info("Roles: {}", roleRepository.count());
        logger.info("Organizations: {}", organizationRepository.count());
        logger.info("Staff: {}", staffRepository.count());
        logger.info("Users: {}", userRepository.count());
        logger.info("Departments: {}", departmentRepository.count());
        logger.info("Designations: {}", designationRepository.count());
        logger.info("Payroll Runs: {}", payrollRunRepository.count());
        logger.info("Payslips: {}", payslipRepository.count());
        logger.info("Competencies: {}", competencyRepository.count());
        logger.info("Leave Types: {}", leaveTypeRepository.count());
        logger.info("Holidays: {}", holidayRepository.count());
        logger.info("Job Postings: {}", jobPostingRepository.count());
        logger.info("Job Applications: {}", jobApplicationRepository.count());
        logger.info("Leave Requests: {}", leaveRequestRepository.count());
        logger.info("Leave Balances: {}", leaveBalanceRepository.count());
        logger.info("Attendance Records: {}", attendanceRepository.count());
        logger.info("========================================================");
        logger.info("✓ Sample data initialization completed successfully");
    }

    private void loadPayslips() {
        logger.info("Loading Payslips from data/hr/sample-payslips.xml...");
        try {
            if (payslipRepository.count() > 0) {
                logger.info("Payslips already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-payslips.xml");
            if (inputStream == null) {
                logger.warn("Payslips XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList payslipList = document.getElementsByTagName("payslip");
            int loadedCount = 0;

            for (int i = 0; i < payslipList.getLength(); i++) {
                Element element = (Element) payslipList.item(i);
                Payslip payslip = mapToPayslip(element);
                payslipRepository.save(payslip);
                loadedCount++;
            }

            logger.info("✓ Loaded {} payslips", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading payslips", e);
        }
    }

    private void loadCompetencies() {
        logger.info("Loading Competencies from data/tpd/sample-competencies.xml...");
        try {
            if (competencyRepository.count() > 0) {
                logger.info("Competencies already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/tpd/sample-competencies.xml");
            if (inputStream == null) {
                logger.warn("Competencies XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList competencyList = document.getElementsByTagName("competency");
            int loadedCount = 0;

            for (int i = 0; i < competencyList.getLength(); i++) {
                Element element = (Element) competencyList.item(i);
                Competency competency = mapToCompetency(element);
                competencyRepository.save(competency);
                loadedCount++;
            }

            logger.info("✓ Loaded {} competencies", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading competencies", e);
        }
    }

    // ==================== HR SAMPLE DATA LOADERS ====================

    private void loadDepartments() {
        logger.info("Loading Departments from data/hr/sample-departments.xml...");
        try {
            if (departmentRepository.count() > 0) {
                logger.info("Departments already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-departments.xml");
            if (inputStream == null) {
                logger.warn("Departments XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList departmentList = document.getElementsByTagName("department");
            int loadedCount = 0;

            for (int i = 0; i < departmentList.getLength(); i++) {
                Element element = (Element) departmentList.item(i);
                Department department = mapToDepartment(element);
                departmentRepository.save(department);
                loadedCount++;
            }

            logger.info("✓ Loaded {} departments", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading departments", e);
        }
    }

    private void loadDesignations() {
        logger.info("Loading Designations from data/hr/sample-designations.xml...");
        try {
            if (designationRepository.count() > 0) {
                logger.info("Designations already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-designations.xml");
            if (inputStream == null) {
                logger.warn("Designations XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList designationList = document.getElementsByTagName("designation");
            int loadedCount = 0;

            for (int i = 0; i < designationList.getLength(); i++) {
                Element element = (Element) designationList.item(i);
                Designation designation = mapToDesignation(element);
                designationRepository.save(designation);
                loadedCount++;
            }

            logger.info("✓ Loaded {} designations", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading designations", e);
        }
    }

    private void loadPayrollRuns() {
        logger.info("Loading Payroll Runs from data/hr/sample-payroll-runs.xml...");
        try {
            if (payrollRunRepository.count() > 0) {
                logger.info("Payroll runs already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-payroll-runs.xml");
            if (inputStream == null) {
                logger.warn("Payroll runs XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList payrollRunList = document.getElementsByTagName("payroll_run");
            int loadedCount = 0;

            for (int i = 0; i < payrollRunList.getLength(); i++) {
                Element element = (Element) payrollRunList.item(i);
                PayrollRun payrollRun = mapToPayrollRun(element);
                payrollRunRepository.save(payrollRun);
                loadedCount++;
            }

            logger.info("✓ Loaded {} payroll runs", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading payroll runs", e);
        }
    }

    private void loadLeaveTypes() {
        logger.info("Loading Leave Types from data/hr/sample-leave-types.xml...");
        try {
            if (leaveTypeRepository.count() > 0) {
                logger.info("Leave types already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-leave-types.xml");
            if (inputStream == null) {
                logger.warn("Leave types XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList leaveTypeList = document.getElementsByTagName("leave_type");
            int loadedCount = 0;

            for (int i = 0; i < leaveTypeList.getLength(); i++) {
                Element element = (Element) leaveTypeList.item(i);
                LeaveType leaveType = mapToLeaveType(element);
                leaveTypeRepository.save(leaveType);
                loadedCount++;
            }

            logger.info("✓ Loaded {} leave types", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading leave types", e);
        }
    }

    private void loadHolidays() {
        logger.info("Loading Holidays from data/hr/sample-holidays.xml...");
        try {
            if (holidayRepository.count() > 0) {
                logger.info("Holidays already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-holidays.xml");
            if (inputStream == null) {
                logger.warn("Holidays XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList holidayList = document.getElementsByTagName("holiday");
            int loadedCount = 0;

            for (int i = 0; i < holidayList.getLength(); i++) {
                Element element = (Element) holidayList.item(i);
                Holiday holiday = mapToHoliday(element);
                holidayRepository.save(holiday);
                loadedCount++;
            }

            logger.info("✓ Loaded {} holidays", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading holidays", e);
        }
    }

    private void loadJobPostings() {
        logger.info("Loading Job Postings from data/hr/sample-job-postings.xml...");
        try {
            if (jobPostingRepository.count() > 0) {
                logger.info("Job postings already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-job-postings.xml");
            if (inputStream == null) {
                logger.warn("Job postings XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList jobPostingList = document.getElementsByTagName("job_posting");
            int loadedCount = 0;

            for (int i = 0; i < jobPostingList.getLength(); i++) {
                Element element = (Element) jobPostingList.item(i);
                JobPosting jobPosting = mapToJobPosting(element);
                jobPostingRepository.save(jobPosting);
                loadedCount++;
            }

            logger.info("✓ Loaded {} job postings", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading job postings", e);
        }
    }

    private void loadJobApplications() {
        logger.info("Loading Job Applications from data/hr/sample-job-applications.xml...");
        try {
            if (jobApplicationRepository.count() > 0) {
                logger.info("Job applications already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-job-applications.xml");
            if (inputStream == null) {
                logger.warn("Job applications XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList jobApplicationList = document.getElementsByTagName("job_application");
            int loadedCount = 0;

            for (int i = 0; i < jobApplicationList.getLength(); i++) {
                Element element = (Element) jobApplicationList.item(i);
                JobApplication jobApplication = mapToJobApplication(element);
                jobApplicationRepository.save(jobApplication);
                loadedCount++;
            }

            logger.info("✓ Loaded {} job applications", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading job applications", e);
        }
    }

    private void loadLeaveRequests() {
        logger.info("Loading Leave Requests from data/hr/sample-leave-requests.xml...");
        try {
            if (leaveRequestRepository.count() > 0) {
                logger.info("Leave requests already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-leave-requests.xml");
            if (inputStream == null) {
                logger.warn("Leave requests XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList leaveRequestList = document.getElementsByTagName("leave_request");
            int loadedCount = 0;

            for (int i = 0; i < leaveRequestList.getLength(); i++) {
                Element element = (Element) leaveRequestList.item(i);
                LeaveRequest leaveRequest = mapToLeaveRequest(element);
                leaveRequestRepository.save(leaveRequest);
                loadedCount++;
            }

            logger.info("✓ Loaded {} leave requests", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading leave requests", e);
        }
    }

    private void loadLeaveBalances() {
        logger.info("Loading Leave Balances from data/hr/sample-leave-balances.xml...");
        try {
            if (leaveBalanceRepository.count() > 0) {
                logger.info("Leave balances already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-leave-balances.xml");
            if (inputStream == null) {
                logger.warn("Leave balances XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList leaveBalanceList = document.getElementsByTagName("leave_balance");
            int loadedCount = 0;

            for (int i = 0; i < leaveBalanceList.getLength(); i++) {
                Element element = (Element) leaveBalanceList.item(i);
                LeaveBalance leaveBalance = mapToLeaveBalance(element);
                leaveBalanceRepository.save(leaveBalance);
                loadedCount++;
            }

            logger.info("✓ Loaded {} leave balances", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading leave balances", e);
        }
    }

    private void loadAttendanceRecords() {
        logger.info("Loading Attendance from data/hr/sample-attendance.xml...");
        try {
            if (attendanceRepository.count() > 0) {
                logger.info("Attendance records already exist. Skipping.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/hr/sample-attendance.xml");
            if (inputStream == null) {
                logger.warn("Attendance XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList attendanceList = document.getElementsByTagName("attendance");
            int loadedCount = 0;

            for (int i = 0; i < attendanceList.getLength(); i++) {
                Element element = (Element) attendanceList.item(i);
                Attendance attendance = mapToAttendance(element);
                attendanceRepository.save(attendance);
                loadedCount++;
            }

            logger.info("✓ Loaded {} attendance records", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading attendance records", e);
        }
    }

    // ==================== MAPPING METHODS ====================

    private Payslip mapToPayslip(Element element) {
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
        return payslip;
    }

    private Competency mapToCompetency(Element element) {
        Competency competency = new Competency();
        competency.setName(element.getAttribute("name"));
        competency.setDescription(element.getAttribute("description"));
        competency.setTargetRole(element.getAttribute("target_role"));
        competency.setRequiredLevel(Integer.parseInt(element.getAttribute("required_level")));
        return competency;
    }

    private Department mapToDepartment(Element element) {
        Department department = new Department();
        department.setName(getElementText(element, "name"));
        department.setCode(getElementText(element, "code"));
        department.setDescription(getElementText(element, "description"));
        department.setHeadOfDepartmentName(getElementText(element, "headOfDepartmentName"));
        department.markAsActive();
        return department;
    }

    private Designation mapToDesignation(Element element) {
        Designation designation = new Designation();
        designation.setTitle(getElementText(element, "title"));
        designation.setDescription(getElementText(element, "description"));
        String rank = getElementText(element, "rank");
        if (rank != null && !rank.isEmpty()) {
            designation.setRankLevel(Integer.parseInt(rank));
        }
        designation.markAsActive();
        return designation;
    }

    private PayrollRun mapToPayrollRun(Element element) {
        PayrollRun payrollRun = new PayrollRun();
        String month = getElementText(element, "month");
        if (month != null && !month.isEmpty()) {
            payrollRun.setMonth(Integer.parseInt(month));
        }
        String year = getElementText(element, "year");
        if (year != null && !year.isEmpty()) {
            payrollRun.setYear(Integer.parseInt(year));
        }
        String processedDateStr = getElementText(element, "processed_date");
        if (processedDateStr != null && !processedDateStr.isEmpty()) {
            payrollRun.setProcessedDate(LocalDate.parse(processedDateStr, DATE_FORMATTER));
        }
        String status = getElementText(element, "status");
        if (status != null && !status.isEmpty()) {
            try {
                payrollRun.setStatus(PayrollRun.RunStatus.valueOf(status));
            } catch (IllegalArgumentException e) {
                payrollRun.setStatus(PayrollRun.RunStatus.DRAFT);
            }
        }
        String totalPayout = getElementText(element, "total_payout");
        if (totalPayout != null && !totalPayout.isEmpty()) {
            payrollRun.setTotalPayout(Double.parseDouble(totalPayout));
        }
        payrollRun.markAsActive();
        return payrollRun;
    }

    private LeaveType mapToLeaveType(Element element) {
        LeaveType leaveType = new LeaveType();
        leaveType.setName(getElementText(element, "name"));
        leaveType.setCode(getElementText(element, "code"));
        String daysAllowed = getElementText(element, "days_allowed");
        if (daysAllowed != null && !daysAllowed.isEmpty()) {
            leaveType.setDaysAllowed(Integer.parseInt(daysAllowed));
        }
        String isCarryForward = getElementText(element, "is_carry_forward");
        leaveType.setIsCarryForward("true".equalsIgnoreCase(isCarryForward) || "1".equals(isCarryForward));
        leaveType.setDescription(getElementText(element, "description"));
        leaveType.markAsActive();
        return leaveType;
    }

    private Holiday mapToHoliday(Element element) {
        Holiday holiday = new Holiday();
        String holidayDateStr = getElementText(element, "holiday_date");
        if (holidayDateStr != null && !holidayDateStr.isEmpty()) {
            holiday.setHolidayDate(LocalDate.parse(holidayDateStr, DATE_FORMATTER));
        }
        holiday.setName(getElementText(element, "name"));
        holiday.setDescription(getElementText(element, "description"));
        holiday.setHolidayType(getElementText(element, "holiday_type"));
        String isOptional = getElementText(element, "is_optional");
        holiday.setIsOptional("true".equalsIgnoreCase(isOptional) || "1".equals(isOptional));
        holiday.setApplicableTo(getElementText(element, "applicable_to"));
        holiday.markAsActive();
        return holiday;
    }

    private JobPosting mapToJobPosting(Element element) {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(getElementText(element, "title"));
        jobPosting.setDepartment(getElementText(element, "department"));
        jobPosting.setDescription(getElementText(element, "description"));
        jobPosting.setRequirements(getElementText(element, "requirements"));
        
        String employmentType = getElementText(element, "employment_type");
        if (employmentType != null && !employmentType.isEmpty()) {
            try {
                jobPosting.setEmploymentType(JobPosting.EmploymentType.valueOf(employmentType));
            } catch (IllegalArgumentException e) {
                jobPosting.setEmploymentType(JobPosting.EmploymentType.FULL_TIME);
            }
        }
        
        String postedDateStr = getElementText(element, "posted_date");
        if (postedDateStr != null && !postedDateStr.isEmpty()) {
            jobPosting.setPostedDate(LocalDate.parse(postedDateStr, DATE_FORMATTER));
        }
        
        String closingDateStr = getElementText(element, "closing_date");
        if (closingDateStr != null && !closingDateStr.isEmpty()) {
            jobPosting.setClosingDate(LocalDate.parse(closingDateStr, DATE_FORMATTER));
        }
        
        String status = getElementText(element, "status");
        if (status != null && !status.isEmpty()) {
            try {
                jobPosting.setStatus(JobPosting.JobStatus.valueOf(status));
            } catch (IllegalArgumentException e) {
                jobPosting.setStatus(JobPosting.JobStatus.OPEN);
            }
        }
        
        jobPosting.markAsActive();
        return jobPosting;
    }

    private JobApplication mapToJobApplication(Element element) {
        JobApplication jobApplication = new JobApplication();
        
        String jobPostingId = getElementText(element, "job_posting_id");
        if (jobPostingId != null && !jobPostingId.isEmpty()) {
            jobApplication.setJobPostingId(Long.parseLong(jobPostingId));
        } else {
            // Default to 1 if not specified
            jobApplication.setJobPostingId(1L);
        }
        
        jobApplication.setCandidateName(getElementText(element, "candidate_name"));
        jobApplication.setCandidateEmail(getElementText(element, "candidate_email"));
        jobApplication.setCandidatePhone(getElementText(element, "candidate_phone"));
        jobApplication.setResumeUrl(getElementText(element, "resume_url"));
        
        String appliedDateStr = getElementText(element, "applied_date");
        if (appliedDateStr != null && !appliedDateStr.isEmpty()) {
            jobApplication.setAppliedDate(LocalDate.parse(appliedDateStr, DATE_FORMATTER));
        }
        
        String status = getElementText(element, "status");
        if (status != null && !status.isEmpty()) {
            try {
                jobApplication.setStatus(JobApplication.ApplicationStatus.valueOf(status));
            } catch (IllegalArgumentException e) {
                jobApplication.setStatus(JobApplication.ApplicationStatus.NEW);
            }
        }
        
        jobApplication.markAsActive();
        return jobApplication;
    }

    private LeaveRequest mapToLeaveRequest(Element element) {
        LeaveRequest leaveRequest = new LeaveRequest();
        
        String staffId = getElementText(element, "staff_id");
        leaveRequest.setStaffId(staffId != null && !staffId.isEmpty() ? Long.parseLong(staffId) : 1L);
        
        String leaveTypeId = getElementText(element, "leave_type_id");
        leaveRequest.setLeaveTypeId(leaveTypeId != null && !leaveTypeId.isEmpty() ? Long.parseLong(leaveTypeId) : 1L);
        
        String startDateStr = getElementText(element, "start_date");
        if (startDateStr != null && !startDateStr.isEmpty()) {
            leaveRequest.setStartDate(LocalDate.parse(startDateStr, DATE_FORMATTER));
        }
        
        String endDateStr = getElementText(element, "end_date");
        if (endDateStr != null && !endDateStr.isEmpty()) {
            leaveRequest.setEndDate(LocalDate.parse(endDateStr, DATE_FORMATTER));
        }
        
        leaveRequest.setReason(getElementText(element, "reason"));
        
        String status = getElementText(element, "status");
        if (status != null && !status.isEmpty()) {
            try {
                leaveRequest.setStatus(LeaveRequest.LeaveStatus.valueOf(status));
            } catch (IllegalArgumentException e) {
                leaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);
            }
        }
        
        leaveRequest.setRejectionReason(getElementText(element, "rejection_reason"));
        leaveRequest.markAsActive();
        return leaveRequest;
    }

    private LeaveBalance mapToLeaveBalance(Element element) {
        LeaveBalance leaveBalance = new LeaveBalance();
        
        String staffId = getElementText(element, "staff_id");
        leaveBalance.setStaffId(staffId != null && !staffId.isEmpty() ? Long.parseLong(staffId) : 1L);
        
        String leaveTypeId = getElementText(element, "leave_type_id");
        leaveBalance.setLeaveTypeId(leaveTypeId != null && !leaveTypeId.isEmpty() ? Long.parseLong(leaveTypeId) : 1L);
        
        leaveBalance.setAcademicYear(getElementText(element, "academic_year"));
        
        String totalDays = getElementText(element, "total_days");
        if (totalDays != null && !totalDays.isEmpty()) {
            leaveBalance.setTotalDays(Double.parseDouble(totalDays));
        }
        
        String consumedDays = getElementText(element, "consumed_days");
        if (consumedDays != null && !consumedDays.isEmpty()) {
            leaveBalance.setConsumedDays(Double.parseDouble(consumedDays));
        }
        
        String remainingDays = getElementText(element, "remaining_days");
        if (remainingDays != null && !remainingDays.isEmpty()) {
            leaveBalance.setRemainingDays(Double.parseDouble(remainingDays));
        }
        
        leaveBalance.markAsActive();
        return leaveBalance;
    }

    private Attendance mapToAttendance(Element element) {
        Attendance attendance = new Attendance();
        
        String attendanceDateStr = getElementText(element, "attendance_date");
        if (attendanceDateStr != null && !attendanceDateStr.isEmpty()) {
            attendance.setAttendanceDate(LocalDate.parse(attendanceDateStr, DATE_FORMATTER));
        }
        
        String checkInTimeStr = getElementText(element, "check_in_time");
        if (checkInTimeStr != null && !checkInTimeStr.isEmpty()) {
            attendance.setCheckInTime(LocalTime.parse(checkInTimeStr));
        }
        
        String checkOutTimeStr = getElementText(element, "check_out_time");
        if (checkOutTimeStr != null && !checkOutTimeStr.isEmpty()) {
            attendance.setCheckOutTime(LocalTime.parse(checkOutTimeStr));
        }
        
        String status = getElementText(element, "status");
        if (status != null && !status.isEmpty()) {
            try {
                attendance.setStatus(Attendance.AttendanceStatus.valueOf(status));
            } catch (IllegalArgumentException e) {
                attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
            }
        }
        
        String attendanceType = getElementText(element, "attendance_type");
        if (attendanceType != null && !attendanceType.isEmpty()) {
            try {
                attendance.setAttendanceType(Attendance.AttendanceType.valueOf(attendanceType));
            } catch (IllegalArgumentException e) {
                attendance.setAttendanceType(Attendance.AttendanceType.STAFF);
            }
        }
        
        attendance.setRemarks(getElementText(element, "remarks"));
        attendance.markAsActive();
        return attendance;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }
}
