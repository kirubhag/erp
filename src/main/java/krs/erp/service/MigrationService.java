package krs.erp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import com.zaxxer.hikari.HikariDataSource;

@Service
public class MigrationService {

    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    /**
     * Upgrade all tenant databases to the latest schema.
     * 
     * @return Summary of the upgrade process
     */
    public String upgradeAllTenants() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(java.util.Objects.requireNonNull(masterDataSource));

        // Get all tenant database names
        List<String> dbNames = jdbcTemplate.queryForList("SELECT db_name FROM erp_tenants", String.class);

        if (dbNames.isEmpty()) {
            return "No tenant databases found to upgrade.";
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder report = new StringBuilder();
        report.append("Starting upgrade for ").append(dbNames.size()).append(" tenants...\n");

        for (String dbName : dbNames) {
            try {
                upgradeTenantDatabase(dbName);
                report.append("✅ ").append(dbName).append(": Success\n");
                successCount++;
            } catch (Exception e) {
                logger.error("Failed to upgrade tenant database: {}", dbName, e);
                report.append("❌ ").append(dbName).append(": Failed - ").append(e.getMessage()).append("\n");
                failCount++;
            }
        }

        report.append("\nSummary: ").append(successCount).append(" successful, ").append(failCount).append(" failed.");
        return report.toString();
    }

    private void upgradeTenantDatabase(String dbName) {
        logger.info("Upgrading tenant database: {}", dbName);

        // Create a temporary DataSource for the tenant DB
        HikariDataSource tenantDataSource = new HikariDataSource();
        tenantDataSource.setJdbcUrl("jdbc:mysql://localhost:3307/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        tenantDataSource.setUsername("root"); // In prod, use configured credentials
        tenantDataSource.setPassword(""); // In prod, use configured credentials
        tenantDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        try {
            JdbcTemplate tenantJdbc = new JdbcTemplate(tenantDataSource);

            // 1. Rename Primary Keys
            renamePkColumns(tenantJdbc, dbName);

            // 2. Add missing columns
            addMissingColumns(tenantJdbc, dbName);

            // 3. Create missing tables using the simplified SQL script
            Resource resource = new ClassPathResource("scripts/migrations/migrate_tenant_db.sql");
            ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
            databasePopulator.execute(tenantDataSource);

            logger.info("Successfully upgraded tenant database: {}", dbName);
        } finally {
            tenantDataSource.close();
        }
    }

    private void renamePkColumns(JdbcTemplate tenantJdbc, String dbName) {
        Map<String, String> tablePkMap = new HashMap<>();
        tablePkMap.put("erp_addresses", "address_id");
        tablePkMap.put("erp_attendance", "attendance_id");
        tablePkMap.put("custom_views", "custom_view_id");
        tablePkMap.put("email_logs", "email_log_id");
        tablePkMap.put("email_templates", "email_template_id");
        tablePkMap.put("erp_entities", "erp_entity_id");
        tablePkMap.put("erp_entity_relation", "erp_entity_relation_id");
        tablePkMap.put("erp_fields", "erp_field_id");
        tablePkMap.put("erp_sections", "erp_section_id");
        tablePkMap.put("grades", "grade_id");
        tablePkMap.put("erp_health_records", "health_record_id");
        tablePkMap.put("iam_users", "user_id");
        tablePkMap.put("organizations", "organization_id");
        tablePkMap.put("parent_student_relations", "parent_student_relation_id");
        tablePkMap.put("erp_parents", "parent_id");
        tablePkMap.put("permissions", "permission_id");
        tablePkMap.put("roles", "role_id");
        tablePkMap.put("erp_staff", "staff_id");
        tablePkMap.put("erp_students", "student_id");
        tablePkMap.put("erp_subjects", "subject_id");
        tablePkMap.put("timetables", "timetable_id");

        for (Map.Entry<String, String> entry : tablePkMap.entrySet()) {
            String tableName = entry.getKey();
            String newPkName = entry.getValue();

            try {
                // Special handling for students table collision
                if (tableName.equals("erp_students") && newPkName.equals("student_id")) {
                    handleColumnCollision(tenantJdbc, dbName, "erp_students", "student_id", "student_id_old");
                }

                // Special handling for staff table collision
                if (tableName.equals("erp_staff") && newPkName.equals("staff_id")) {
                    handleColumnCollision(tenantJdbc, dbName, "erp_staff", "staff_id", "staff_id_old");
                }

                // Check if 'id' column exists
                String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = 'id'";
                Integer count = tenantJdbc.queryForObject(checkSql, Integer.class, dbName, tableName);

                if (count != null && count > 0) {
                    logger.info("Renaming 'id' to '{}' in table '{}'", newPkName, tableName);
                    // Disable FK checks to avoid issues during rename
                    tenantJdbc.execute("SET FOREIGN_KEY_CHECKS = 0");
                    tenantJdbc.execute("ALTER TABLE " + tableName + " CHANGE COLUMN id " + newPkName
                            + " BIGINT NOT NULL AUTO_INCREMENT");
                    tenantJdbc.execute("SET FOREIGN_KEY_CHECKS = 1");
                }
            } catch (Exception e) {
                logger.error("Failed to rename PK for table {}: {}", tableName, e.getMessage());
                // Continue with other tables
            }
        }
    }

    private void handleColumnCollision(JdbcTemplate tenantJdbc, String dbName, String tableName, String columnName,
            String newColumnName) {
        try {
            // Check if collision column exists AND is NOT the primary key (i.e. it's the
            // varchar one)
            String checkSql = "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?";
            List<String> types = tenantJdbc.queryForList(checkSql, String.class, dbName, tableName, columnName);

            if (!types.isEmpty()) {
                String dataType = types.get(0);
                // If it's varchar, it's the collision column. If it's bigint, it might be
                // already renamed PK.
                if ("varchar".equalsIgnoreCase(dataType)) {
                    logger.info("Found conflicting '{}' (VARCHAR) column in '{}' table. Renaming to '{}'.", columnName,
                            tableName, newColumnName);
                    tenantJdbc.execute("ALTER TABLE " + tableName + " CHANGE COLUMN " + columnName + " " + newColumnName
                            + " VARCHAR(20)");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to handle collision for {}.{}: {}", tableName, columnName, e.getMessage());
        }
    }

    private void addMissingColumns(JdbcTemplate tenantJdbc, String dbName) {
        try {
            // Check if show_type exists in erp_fields
            String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = 'erp_fields' AND COLUMN_NAME = 'show_type'";
            Integer count = tenantJdbc.queryForObject(checkSql, Integer.class, dbName);

            if (count != null && count == 0) {
                logger.info("Adding 'show_type' column to 'erp_fields'");
                tenantJdbc.execute("ALTER TABLE erp_fields ADD COLUMN show_type INT DEFAULT 0 AFTER column_width");
            }
        } catch (Exception e) {
            logger.error("Failed to add missing columns: {}", e.getMessage());
        }
    }
}
