package krs.erp.initializer;

import java.io.InputStream;
import java.time.LocalDate;

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

import krs.erp.model.Staff;
import krs.erp.repository.StaffRepository;

/**
 * Initializes staff data from XML files on application startup.
 * DEPRECATED: Use SampleDataInitializer instead for consolidated data loading
 */
// @Component - Disabled for multi-tenant system
@Order(4)
public class StaffDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StaffDataInitializer.class);

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Staff data initialization...");

        try {
            if (staffRepository.count() > 0) {
                logger.info("Staff data already exists. Skipping initialization.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/sample-staff.xml");
            if (inputStream == null) {
                logger.warn("sample-staff.xml not found. Skipping Staff initialization.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList staffList = document.getElementsByTagName("staff");
            int totalStaff = staffList.getLength();
            int loadedStaff = 0;

            logger.info("Found {} staff members in XML file", totalStaff);

            for (int i = 0; i < totalStaff; i++) {
                Element staffElement = (Element) staffList.item(i);

                try {
                    Staff staff = new Staff();
                    staff.setFirstName(getTextContent(staffElement, "first_name"));
                    staff.setLastName(getTextContent(staffElement, "last_name"));
                    staff.setMiddleName(getTextContent(staffElement, "middle_name"));
                    staff.setStaffId(getTextContent(staffElement, "staff_id"));
                    staff.setEmail(getTextContent(staffElement, "email"));
                    staff.setPhone(getTextContent(staffElement, "phone"));

                    String dateOfBirth = getTextContent(staffElement, "date_of_birth");
                    if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                        try {
                            staff.setDateOfBirth(LocalDate.parse(dateOfBirth));
                        } catch (Exception e) {
                            logger.warn("Invalid date_of_birth format: {}", dateOfBirth);
                        }
                    }

                    String gender = getTextContent(staffElement, "gender");
                    if (gender != null) {
                        try {
                            staff.setGender(Staff.Gender.valueOf(gender));
                        } catch (IllegalArgumentException e) {
                            logger.warn("Invalid gender value: {}", gender);
                        }
                    }

                    String hireDate = getTextContent(staffElement, "hire_date");
                    if (hireDate != null && !hireDate.isEmpty()) {
                        try {
                            staff.setHireDate(LocalDate.parse(hireDate));
                        } catch (Exception e) {
                            logger.warn("Invalid hire_date format: {}", hireDate);
                        }
                    }

                    String employmentStatus = getTextContent(staffElement, "employment_status");
                    if (employmentStatus != null) {
                        try {
                            staff.setEmploymentStatus(Staff.EmploymentStatus.valueOf(employmentStatus));
                        } catch (IllegalArgumentException e) {
                            logger.warn("Invalid employment_status value: {}", employmentStatus);
                        }
                    }

                    String staffType = getTextContent(staffElement, "staff_type");
                    if (staffType != null) {
                        try {
                            staff.setStaffType(Staff.StaffType.valueOf(staffType));
                        } catch (IllegalArgumentException e) {
                            logger.warn("Invalid staff_type value: {}", staffType);
                        }
                    }

                    staff.setDepartment(getTextContent(staffElement, "department"));
                    staff.setPosition(getTextContent(staffElement, "position"));
                    staff.setQualification(getTextContent(staffElement, "qualification"));

                    String experienceYears = getTextContent(staffElement, "experience_years");
                    if (experienceYears != null && !experienceYears.isEmpty()) {
                        try {
                            staff.setExperienceYears(Integer.parseInt(experienceYears));
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid experience_years format: {}", experienceYears);
                        }
                    }

                    staff.markAsActive();
                    staffRepository.save(staff);
                    loadedStaff++;

                    logger.debug("Loaded staff member: {} {}", staff.getFirstName(), staff.getLastName());

                } catch (Exception e) {
                    logger.error("Error processing staff at index {}: {}", i, e.getMessage());
                }
            }

            logger.info("Staff data initialization completed. Loaded: {}, Total: {}", 
                    loadedStaff, totalStaff);

        } catch (Exception e) {
            logger.error("Error initializing Staff data: {}", e.getMessage(), e);
        }
    }

    private String getTextContent(Element element, String attributeName) {
        try {
            String value = element.getAttribute(attributeName);
            return value != null && !value.trim().isEmpty() ? value.trim() : null;
        } catch (Exception e) {
            logger.debug("Could not extract attribute: {}", attributeName);
        }
        return null;
    }
}
