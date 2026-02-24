package krs.erp.initializer;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zaxxer.hikari.HikariDataSource;

import krs.erp.entity.FieldMappingTemplate;
import krs.erp.repository.FieldMappingTemplateRepository;

/**
 * Data initializer for Field Mapping Templates.
 * Seeds the database with field mapping templates for import functionality.
 * Seeds templates into both master DB and all tenant databases.
 */
@Component
@Order(5)
public class FieldMappingTemplateDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(FieldMappingTemplateDataInitializer.class);

    @Autowired
    private FieldMappingTemplateRepository fieldMappingTemplateRepository;

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting Field Mapping Templates data initialization...");

        try {
            // 1. Seed templates in master DB (for backward compatibility)
            int masterLoaded = seedTemplatesInCurrentContext();
            logger.info("Master DB: {} templates initialized.", masterLoaded);

            // 2. Seed templates in all tenant databases
            seedTemplatesInAllTenantDatabases();

            logger.info("Field Mapping Templates initialization completed.");

        } catch (Exception e) {
            logger.error("Error during Field Mapping Templates data initialization", e);
        }
    }

    /**
     * Seeds templates in the current database context (master DB when called from CommandLineRunner)
     */
    private int seedTemplatesInCurrentContext() {
        int totalLoaded = 0;
        
        // Initialize students templates if not exists
        long studentCount = fieldMappingTemplateRepository.findByEntityTypeOrderByDisplayOrder("students").size();
        if (studentCount == 0) {
            List<FieldMappingTemplate> studentTemplates = createStudentFieldMappingTemplates();
            for (FieldMappingTemplate template : studentTemplates) {
                fieldMappingTemplateRepository.save(template);
            }
            totalLoaded += studentTemplates.size();
            logger.info("Loaded {} templates for 'students' entity type.", studentTemplates.size());
        } else {
            logger.debug("Templates for 'students' entity type already exist ({} records).", studentCount);
        }
        
        // Initialize subjects templates if not exists
        long subjectCount = fieldMappingTemplateRepository.findByEntityTypeOrderByDisplayOrder("subjects").size();
        if (subjectCount == 0) {
            List<FieldMappingTemplate> subjectTemplates = createSubjectFieldMappingTemplates();
            for (FieldMappingTemplate template : subjectTemplates) {
                fieldMappingTemplateRepository.save(template);
            }
            totalLoaded += subjectTemplates.size();
            logger.info("Loaded {} templates for 'subjects' entity type.", subjectTemplates.size());
        } else {
            logger.debug("Templates for 'subjects' entity type already exist ({} records).", subjectCount);
        }

        return totalLoaded;
    }

    /**
     * Iterate over all tenant databases and seed templates into each one
     */
    private void seedTemplatesInAllTenantDatabases() {
        JdbcTemplate masterJdbc = new JdbcTemplate(masterDataSource);

        try {
            // Get all active tenant database names
            List<String> dbNames = masterJdbc.queryForList(
                "SELECT db_name FROM erp_tenants WHERE status = 'Active'", String.class);

            if (dbNames.isEmpty()) {
                logger.info("No tenant databases found to seed templates.");
                return;
            }

            logger.info("Seeding templates into {} tenant databases...", dbNames.size());
            int successCount = 0;
            int skipCount = 0;

            for (String dbName : dbNames) {
                try {
                    int seeded = seedTemplatesInTenantDatabase(dbName);
                    if (seeded > 0) {
                        successCount++;
                        logger.info("Seeded {} templates into tenant DB: {}", seeded, dbName);
                    } else {
                        skipCount++;
                        logger.debug("Tenant DB {} already has templates, skipped.", dbName);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to seed templates in tenant DB {}: {}", dbName, e.getMessage());
                }
            }

            logger.info("Template seeding complete. {} tenants updated, {} skipped (already had templates).", 
                successCount, skipCount);

        } catch (Exception e) {
            logger.error("Error querying tenant databases: {}", e.getMessage());
        }
    }

    /**
     * Seed templates into a specific tenant database using direct JDBC
     */
    private int seedTemplatesInTenantDatabase(String dbName) {
        HikariDataSource tenantDataSource = new HikariDataSource();
        tenantDataSource.setJdbcUrl("jdbc:mysql://localhost:3307/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        tenantDataSource.setUsername("root");
        tenantDataSource.setPassword("");
        tenantDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        tenantDataSource.setMaximumPoolSize(2);
        tenantDataSource.setMinimumIdle(1);

        int totalInserted = 0;

        try {
            JdbcTemplate tenantJdbc = new JdbcTemplate(tenantDataSource);

            // Check if table exists and has data
            try {
                Integer existingCount = tenantJdbc.queryForObject(
                    "SELECT COUNT(*) FROM erp_field_mapping_templates", Integer.class);
                if (existingCount != null && existingCount > 0) {
                    return 0; // Already has templates
                }
            } catch (Exception e) {
                logger.warn("Table erp_field_mapping_templates may not exist in {}: {}", dbName, e.getMessage());
                return 0;
            }

            // Insert students templates
            List<FieldMappingTemplate> studentTemplates = createStudentFieldMappingTemplates();
            for (FieldMappingTemplate t : studentTemplates) {
                tenantJdbc.update(
                    "INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    t.getEntityType(), t.getFieldName(), t.getFieldLabel(), t.getIsRequired(), 
                    t.getDataType(), t.getSection(), t.getDisplayOrder(), t.getSuggestions());
                totalInserted++;
            }

            // Insert subjects templates
            List<FieldMappingTemplate> subjectTemplates = createSubjectFieldMappingTemplates();
            for (FieldMappingTemplate t : subjectTemplates) {
                tenantJdbc.update(
                    "INSERT INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    t.getEntityType(), t.getFieldName(), t.getFieldLabel(), t.getIsRequired(), 
                    t.getDataType(), t.getSection(), t.getDisplayOrder(), t.getSuggestions());
                totalInserted++;
            }

        } finally {
            tenantDataSource.close();
        }

        return totalInserted;
    }

    /**
     * Create field mapping templates for the 'students' entity type
     */
    private List<FieldMappingTemplate> createStudentFieldMappingTemplates() {
        List<FieldMappingTemplate> templates = new ArrayList<>();
        int order = 0;

        // Basic Information Section
        templates.add(createTemplate("students", "firstName", "First Name", true, "string", "Basic Info", order++, "First Name,Given Name,Student First Name"));
        templates.add(createTemplate("students", "lastName", "Last Name", true, "string", "Basic Info", order++, "Last Name,Surname,Family Name,Student Last Name"));
        templates.add(createTemplate("students", "middleName", "Middle Name", false, "string", "Basic Info", order++, "Middle Name,Middle Initial"));
        templates.add(createTemplate("students", "email", "Email", false, "email", "Basic Info", order++, "Email,Email Address,E-mail,Student Email"));
        templates.add(createTemplate("students", "phone", "Phone", false, "phone", "Basic Info", order++, "Phone,Phone Number,Mobile,Contact Number,Cell"));
        templates.add(createTemplate("students", "dateOfBirth", "Date of Birth", false, "date", "Basic Info", order++, "Date of Birth,DOB,Birth Date,Birthday"));
        templates.add(createTemplate("students", "gender", "Gender", false, "enum", "Basic Info", order++, "Gender,Sex"));

        // Academic Information Section
        templates.add(createTemplate("students", "gradeLevel", "Grade Level", true, "enum", "Academic Info", order++, "Grade,Grade Level,Class,Year,Standard"));
        templates.add(createTemplate("students", "section", "Section", false, "string", "Academic Info", order++, "Section,Division,Class Section"));
        templates.add(createTemplate("students", "enrollmentDate", "Enrollment Date", false, "date", "Academic Info", order++, "Enrollment Date,Admission Date,Join Date,Start Date"));
        templates.add(createTemplate("students", "enrollmentStatus", "Enrollment Status", false, "enum", "Academic Info", order++, "Status,Enrollment Status,Current Status"));
        templates.add(createTemplate("students", "admissionNumber", "Admission Number", false, "string", "Academic Info", order++, "Admission Number,Admission No,Admission ID,Registration Number"));

        // Contact Information Section
        templates.add(createTemplate("students", "emergencyContactName", "Emergency Contact Name", false, "string", "Emergency Contact", order++, "Emergency Contact,Guardian Name,Parent Name,Emergency Name"));
        templates.add(createTemplate("students", "emergencyContactPhone", "Emergency Contact Phone", false, "phone", "Emergency Contact", order++, "Emergency Phone,Guardian Phone,Parent Phone,Emergency Number"));
        templates.add(createTemplate("students", "emergencyContactRelation", "Emergency Contact Relation", false, "string", "Emergency Contact", order++, "Relation,Relationship,Guardian Relation,Contact Relation"));

        // Additional Information Section
        templates.add(createTemplate("students", "nationality", "Nationality", false, "string", "Additional Info", order++, "Nationality,Country,Citizenship"));
        templates.add(createTemplate("students", "bloodGroup", "Blood Group", false, "string", "Additional Info", order++, "Blood Group,Blood Type"));

        return templates;
    }

    /**
     * Create field mapping templates for the 'subjects' entity type
     */
    private List<FieldMappingTemplate> createSubjectFieldMappingTemplates() {
        List<FieldMappingTemplate> templates = new ArrayList<>();
        int order = 0;

        // Basic Subject Information
        templates.add(createTemplate("subjects", "subjectCode", "Subject Code", true, "string", "Basic Info", order++, "Subject Code,Code,Subject ID,Course Code"));
        templates.add(createTemplate("subjects", "subjectName", "Subject Name", true, "string", "Basic Info", order++, "Subject Name,Name,Subject,Course Name,Title"));
        templates.add(createTemplate("subjects", "description", "Description", false, "string", "Basic Info", order++, "Description,Details,Subject Description,Course Description"));
        templates.add(createTemplate("subjects", "gradeLevel", "Grade Level", true, "string", "Basic Info", order++, "Grade Level,Grade,Class,Year,Standard,Level"));
        templates.add(createTemplate("subjects", "category", "Category", false, "string", "Basic Info", order++, "Category,Subject Category,Type,Department"));
        
        // Academic Details
        templates.add(createTemplate("subjects", "credits", "Credits", false, "integer", "Academic Details", order++, "Credits,Credit Hours,Units"));
        templates.add(createTemplate("subjects", "hoursPerWeek", "Hours Per Week", false, "integer", "Academic Details", order++, "Hours Per Week,Weekly Hours,Hours,Periods"));
        templates.add(createTemplate("subjects", "prerequisites", "Prerequisites", false, "string", "Academic Details", order++, "Prerequisites,Pre-requisites,Required Subjects"));
        templates.add(createTemplate("subjects", "difficultyLevel", "Difficulty Level", false, "string", "Academic Details", order++, "Difficulty Level,Difficulty,Level"));
        templates.add(createTemplate("subjects", "isMandatory", "Is Mandatory", false, "boolean", "Academic Details", order++, "Is Mandatory,Mandatory,Required,Compulsory"));

        return templates;
    }

    /**
     * Helper method to create a FieldMappingTemplate
     */
    private FieldMappingTemplate createTemplate(String entityType, String fieldName, String fieldLabel, 
                                                  boolean isRequired, String dataType, String section, 
                                                  int displayOrder, String suggestions) {
        FieldMappingTemplate template = new FieldMappingTemplate();
        template.setEntityType(entityType);
        template.setFieldName(fieldName);
        template.setFieldLabel(fieldLabel);
        template.setIsRequired(isRequired);
        template.setDataType(dataType);
        template.setSection(section);
        template.setDisplayOrder(displayOrder);
        template.setSuggestions(suggestions);
        return template;
    }
}
