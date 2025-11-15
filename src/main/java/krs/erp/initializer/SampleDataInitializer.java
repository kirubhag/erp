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

import krs.erp.model.Organization;
import krs.erp.model.Permission;
import krs.erp.model.Role;
import krs.erp.model.Staff;
import krs.erp.model.User;
import krs.erp.repository.ImportHistoryRepository;
import krs.erp.repository.OrganizationRepository;
import krs.erp.repository.PermissionRepository;
import krs.erp.repository.RoleRepository;
import krs.erp.repository.StaffRepository;
import krs.erp.repository.UserRepository;

/**
 * Unified SampleDataInitializer that loads all sample data (Permissions, Roles, Organizations, Staff, Users)
 * from their respective XML files in a single initializer component.
 * 
 * Execution Order:
 * @Order(0) - Highest priority among initializers to load base system data first
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
@Component
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
    private ImportHistoryRepository importHistoryRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting unified sample data initialization...");

        try {
            logger.info("Loading sample data from organized XML files...");

            // Load data in proper order (dependencies first)
            loadPermissions();
            loadRoles();
            // DISABLED: Organization data should be populated via registration form, not auto-initialization
            // loadOrganizations();
            loadStaff();
            loadUsers();

            logSummary();

        } catch (Exception e) {
            logger.error("Error during sample data initialization", e);
        }
    }

    private boolean isDataAlreadyLoaded() {
        return permissionRepository.count() > 0 ||
               roleRepository.count() > 0 ||
               organizationRepository.count() > 0 ||
               staffRepository.count() > 0 ||
               userRepository.count() > 0;
    }

    private void loadPermissions() {
        logger.info("Loading Permissions from data/permission/sample-permissions.xml...");
        try {
            // DISABLED: if (permissionRepository.count() > 0) {
            //     logger.info("Permissions already exist. Skipping.");
            //     return;
            // }

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
            // DISABLED: if (roleRepository.count() > 0) {
            //     logger.info("Roles already exist. Skipping.");
            //     return;
            // }

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

    private void loadOrganizations() {
        logger.info("Loading Organizations from data/organisation/sample-organisations.xml...");
        try {
            // DISABLED: if (organizationRepository.count() > 0) {
            //     logger.info("Organizations already exist. Skipping.");
            //     return;
            // }

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/organisation/sample-organisations.xml");
            if (inputStream == null) {
                logger.warn("Organizations XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList organizationList = document.getElementsByTagName("organizations");
            int loadedCount = 0;

            for (int i = 0; i < organizationList.getLength(); i++) {
                Element element = (Element) organizationList.item(i);
                Organization organization = mapToOrganization(element);
                organizationRepository.save(organization);
                loadedCount++;
            }

            logger.info("✓ Loaded {} organizations", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading organizations", e);
        }
    }

    private void loadStaff() {
        logger.info("Loading Staff from data/staff/sample-staff.xml...");
        try {
            // DISABLED: if (staffRepository.count() > 0) {
            //     logger.info("Staff already exist. Skipping.");
            //     return;
            // }

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
            // DISABLED: if (userRepository.count() > 0) {
            //     logger.info("Users already exist. Skipping.");
            //     return;
            // }

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

    private Organization mapToOrganization(Element element) {
        Organization organization = new Organization();
        organization.setName(element.getAttribute("name"));
        organization.setType(element.getAttribute("type"));
        organization.setCode(element.getAttribute("code"));
        organization.setDescription(element.getAttribute("description"));
        organization.setEmail(element.getAttribute("email"));
        organization.setPhone(element.getAttribute("phone"));
        organization.setFax(element.getAttribute("fax"));
        organization.setWebsite(element.getAttribute("website"));
        organization.setStreetAddress(element.getAttribute("street_address"));
        organization.setCity(element.getAttribute("city"));
        organization.setState(element.getAttribute("state"));
        organization.setPostalCode(element.getAttribute("postal_code"));
        organization.setCountry(element.getAttribute("country"));
        organization.setRegistrationNumber(element.getAttribute("registration_number"));
        organization.setTaxId(element.getAttribute("tax_id"));

        String establishedYear = element.getAttribute("established_year");
        if (!establishedYear.isEmpty()) {
            try {
                organization.setEstablishedYear(Integer.parseInt(establishedYear));
            } catch (NumberFormatException e) {
                logger.warn("Invalid established_year format: {}", establishedYear);
            }
        }

        organization.markAsActive();
        return organization;
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
        user.setAccountNonExpired("0".equals(element.getAttribute("account_expired")) ? false : true);
        user.setCredentialsNonExpired("0".equals(element.getAttribute("credentials_expired")) ? false : true);
        user.setAccountNonLocked("0".equals(element.getAttribute("account_locked")) ? false : true);

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
        logger.info("========================================================");
        logger.info("✓ Sample data initialization completed successfully");
    }
}
