package krs.erp.service;

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
public class TenantProvisioningService {

    private static final Logger logger = LoggerFactory.getLogger(TenantProvisioningService.class);

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    /**
     * Provision tenant database with system user as creator (for backward compatibility)
     */
    public void provisionTenantDatabase(String dbName, Long tenantId, String tenantName) {
        provisionTenantDatabase(dbName, tenantId, tenantName, null);
    }

    /**
     * Provision tenant database with specific user as creator
     * @param dbName Database name
     * @param tenantId Tenant ID
     * @param tenantName Tenant name
     * @param createdByUserId The user ID who initiated the signup (can be null for system)
     */
    public void provisionTenantDatabase(String dbName, Long tenantId, String tenantName, Long createdByUserId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);

        // 1. Create Database
        jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS " + dbName);

        // 2. Initialize Schema only (metadata copy is done separately after user creation)
        initializeTenantSchemaOnly(dbName, tenantId, tenantName);
        
        // 3. Copy system data if userId is provided (for backward compatibility)
        if (createdByUserId != null) {
            copySystemDataWithUserId(dbName, createdByUserId);
        }
    }

    /**
     * Copy system data from master DB to tenant DB with a specific user as creator.
     * This should be called after the user is created in the tenant DB.
     * @param dbName Database name
     * @param createdByUserId The user ID who initiated the signup
     */
    public void copySystemDataWithUserId(String dbName, Long createdByUserId) {
        // Create a temporary DataSource for the tenant DB
        HikariDataSource tenantDataSource = new HikariDataSource();
        tenantDataSource.setJdbcUrl("jdbc:mysql://localhost:3307/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        tenantDataSource.setUsername("root");
        tenantDataSource.setPassword("");
        tenantDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        try {
            copySystemData(dbName, tenantDataSource, null, null, createdByUserId);
        } finally {
            tenantDataSource.close();
        }
    }

    private void initializeTenantSchemaOnly(String dbName, Long tenantId, String tenantName) {
        // Create a temporary DataSource for the new tenant DB
        HikariDataSource tenantDataSource = new HikariDataSource();
        tenantDataSource.setJdbcUrl("jdbc:mysql://localhost:3307/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        tenantDataSource.setUsername("root"); // In prod, use configured credentials
        tenantDataSource.setPassword(""); // In prod, use configured credentials
        tenantDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        try {
            Resource resource = new ClassPathResource("scripts/tenant_schema.sql");
            ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
            databasePopulator.execute(tenantDataSource);

            // Add tenant info to tenant DB
            JdbcTemplate tenantJdbc = new JdbcTemplate(tenantDataSource);
            try {
                logger.info("Adding tenant info to tenant DB erp_tenants table...");
                String sql = "INSERT IGNORE INTO erp_tenants (tenant_id, tenant_name, db_host, db_name, status, created_at) VALUES (?, ?, 'localhost', ?, 'Active', NOW())";
                tenantJdbc.update(sql, tenantId, tenantName, dbName);
                logger.info("Tenant info added to tenant DB.");
            } catch (Exception e) {
                logger.warn("Failed to add tenant info to tenant DB: {}", e.getMessage());
            }
        } finally {
            tenantDataSource.close();
        }
    }

    private void initializeTenantSchema(String dbName, Long tenantId, String tenantName, Long createdByUserId) {
        // Create a temporary DataSource for the new tenant DB
        HikariDataSource tenantDataSource = new HikariDataSource();
        tenantDataSource.setJdbcUrl("jdbc:mysql://localhost:3307/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        tenantDataSource.setUsername("root"); // In prod, use configured credentials
        tenantDataSource.setPassword(""); // In prod, use configured credentials
        tenantDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        try {
            Resource resource = new ClassPathResource("scripts/tenant_schema.sql");
            ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
            databasePopulator.execute(tenantDataSource);

            // 3. Copy System Data from Master DB
            copySystemData(dbName, tenantDataSource, tenantId, tenantName, createdByUserId);
        } finally {
            tenantDataSource.close();
        }
    }

    private void copySystemData(String dbName, HikariDataSource tenantDataSource, Long tenantId, String tenantName, Long createdByUserId) {
        JdbcTemplate masterJdbc = new JdbcTemplate(masterDataSource);
        JdbcTemplate tenantJdbc = new JdbcTemplate(tenantDataSource);
        
        // Use the provided userId or null for system-created records
        final Long creatorId = createdByUserId;

        try {
            // Check if system data already exists to prevent duplicates
            Integer existingCount = tenantJdbc.queryForObject(
                    "SELECT COUNT(*) FROM erp_entities", Integer.class);
            if (existingCount != null && existingCount > 0) {
                logger.info("System data already exists in tenant DB: {} (found {} entities). Skipping copy.", dbName,
                        existingCount);
                return;
            }

            // First check if master DB has data
            Integer masterCount = masterJdbc.queryForObject(
                    "SELECT COUNT(*) FROM erp_entities", Integer.class);
            if (masterCount == null || masterCount == 0) {
                logger.warn("Master DB has no ERP entities! Data initialization may not have run yet.");
            } else {
                logger.info("Master DB has {} entities to copy", masterCount);
            }

            logger.info("Starting system data copy for tenant: {}", dbName);

            // 1. Copy ERP Entities
            try {
                logger.info("Copying ERP Entities...");
                masterJdbc.query("SELECT * FROM erp_entities", rs -> {
                    String sql = "INSERT IGNORE INTO erp_entities (erp_entity_id, singular_name, plural_name, description, is_active, sequence, system_name, presence, icon, route, table_name, pkid, display_column, has_rel_table, created_date, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_entity_id"),
                            rs.getString("singular_name"),
                            rs.getString("plural_name"),
                            rs.getString("description"),
                            rs.getBoolean("is_active"),
                            rs.getInt("sequence"),
                            rs.getString("system_name"),
                            rs.getBoolean("presence"),
                            rs.getString("icon"),
                            rs.getString("route"),
                            rs.getString("table_name"),
                            rs.getString("pkid"),
                            rs.getString("display_column"),
                            rs.getBoolean("has_rel_table"),
                            creatorId);
                });
                logger.info("ERP Entities copied.");
            } catch (Exception e) {
                logger.error("Failed to copy ERP Entities: {}", e.getMessage(), e);
                throw e;
            }

            // 2. Copy ERP Entity Relations
            try {
                logger.info("Copying ERP Entity Relations...");
                masterJdbc.query("SELECT * FROM erp_entity_relations", rs -> {
                    String sql = "INSERT IGNORE INTO erp_entity_relations (relation_id, parent_entity_id, child_entity_id, relation_type, relation_name, foreign_key_column, is_mandatory, cascade_delete, display_order, is_active, created_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                    tenantJdbc.update(sql,
                            rs.getLong("relation_id"),
                            rs.getLong("parent_entity_id"),
                            rs.getLong("child_entity_id"),
                            rs.getString("relation_type"),
                            rs.getString("relation_name"),
                            rs.getString("foreign_key_column"),
                            rs.getBoolean("is_mandatory"),
                            rs.getBoolean("cascade_delete"),
                            rs.getInt("display_order"),
                            rs.getBoolean("is_active"));
                });
                logger.info("ERP Entity Relations copied.");
            } catch (Exception e) {
                logger.warn("Failed to copy ERP Entity Relations (may not exist): {}", e.getMessage());
                // Don't throw - this table may not exist or be empty
            }

            // 3. Copy ERP Sections
            try {
                logger.info("Copying ERP Sections...");
                masterJdbc.query("SELECT * FROM erp_sections", rs -> {
                    String sql = "INSERT IGNORE INTO erp_sections (erp_section_id, entity_type, erp_entity_id, section_name, section_label, layout_type, display_order, is_collapsible, is_collapsed_by_default, show_in_create, show_in_edit, show_in_detail, section_icon, section_color, css_class, description, help_text, created_time, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_section_id"),
                            rs.getString("entity_type"),
                            rs.getObject("erp_entity_id"),
                            rs.getString("section_name"),
                            rs.getString("section_label"),
                            rs.getString("layout_type"),
                            rs.getInt("display_order"),
                            rs.getBoolean("is_collapsible"),
                            rs.getBoolean("is_collapsed_by_default"),
                            rs.getBoolean("show_in_create"),
                            rs.getBoolean("show_in_edit"),
                            rs.getBoolean("show_in_detail"),
                            rs.getString("section_icon"),
                            rs.getString("section_color"),
                            rs.getString("css_class"),
                            rs.getString("description"),
                            rs.getString("help_text"),
                            creatorId);
                });
                logger.info("ERP Sections copied.");
            } catch (Exception e) {
                logger.error("Failed to copy ERP Sections: {}", e.getMessage(), e);
                throw e;
            }

            // 4. Copy ERP Fields
            try {
                logger.info("Copying ERP Fields...");
                masterJdbc.query("SELECT * FROM erp_fields", rs -> {
                    String sql = "INSERT IGNORE INTO erp_fields (erp_field_id, entity_type, erp_entity_id, field_name, field_label, field_type, ui_type, row_position, column_position, is_required, is_searchable, is_sortable, display_order, field_description, default_width, max_length, validation_pattern, picklist_options, decimal_places, is_unique, show_in_list, show_in_form, column_width, show_type, created_time, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_field_id"),
                            rs.getString("entity_type"),
                            rs.getObject("erp_entity_id"),
                            rs.getString("field_name"),
                            rs.getString("field_label"),
                            rs.getString("field_type"),
                            rs.getObject("ui_type"),
                            rs.getInt("row_position"),
                            rs.getInt("column_position"),
                            rs.getBoolean("is_required"),
                            rs.getBoolean("is_searchable"),
                            rs.getBoolean("is_sortable"),
                            rs.getInt("display_order"),
                            rs.getString("field_description"),
                            rs.getInt("default_width"),
                            rs.getInt("max_length"),
                            rs.getString("validation_pattern"),
                            rs.getString("picklist_options"),
                            rs.getInt("decimal_places"),
                            rs.getBoolean("is_unique"),
                            rs.getBoolean("show_in_list"),
                            rs.getBoolean("show_in_form"),
                            rs.getString("column_width"),
                            rs.getObject("show_type"),
                            creatorId);
                });
                logger.info("ERP Fields copied.");
            } catch (Exception e) {
                logger.error("Failed to copy ERP Fields: {}", e.getMessage(), e);
                throw e;
            }

            // 4b. Copy Section-Field Relationships
            try {
                logger.info("Copying Section-Field Relationships...");
                masterJdbc.query("SELECT * FROM erp_sections_field_rel", rs -> {
                    String sql = "INSERT IGNORE INTO erp_sections_field_rel (erp_section_id, erp_field_id, field_order, created_time, created_by) VALUES (?, ?, ?, NOW(), ?)";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_section_id"),
                            rs.getLong("erp_field_id"),
                            rs.getInt("field_order"),
                            creatorId);
                });
                logger.info("Section-Field Relationships copied.");
            } catch (Exception e) {
                logger.warn("Failed to copy Section-Field Relationships (may not exist): {}", e.getMessage());
                // Don't throw - this table may not have data yet
            }

            // 4c. Copy ERP Layouts
            try {
                logger.info("Copying ERP Layouts...");
                masterJdbc.query("SELECT * FROM erp_layout", rs -> {
                    String sql = "INSERT IGNORE INTO erp_layout (erp_layout_id, erp_entity_id, layout_name, layout_type, layout_columns, is_default, description, created_time, modified_time, is_active, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_layout_id"),
                            rs.getLong("erp_entity_id"),
                            rs.getString("layout_name"),
                            rs.getString("layout_type"),
                            rs.getInt("layout_columns"),
                            rs.getBoolean("is_default"),
                            rs.getString("description"),
                            rs.getBoolean("is_active"),
                            creatorId);
                });
                logger.info("ERP Layouts copied.");
            } catch (Exception e) {
                logger.warn("Failed to copy ERP Layouts (may not exist): {}", e.getMessage());
            }

            // 4d. Copy Layout-Section Relationships
            try {
                logger.info("Copying Layout-Section Relationships...");
                masterJdbc.query("SELECT * FROM erp_layout_section_rel", rs -> {
                    String sql = "INSERT IGNORE INTO erp_layout_section_rel (erp_layout_id, erp_section_id, section_order, created_time, modified_time) VALUES (?, ?, ?, NOW(), NOW())";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_layout_id"),
                            rs.getLong("erp_section_id"),
                            rs.getInt("section_order"));
                });
                logger.info("Layout-Section Relationships copied.");
            } catch (Exception e) {
                logger.warn("Failed to copy Layout-Section Relationships (may not exist): {}", e.getMessage());
            }

            // 4e. Copy Auto Numbers
            try {
                logger.info("Copying Auto Numbers...");
                masterJdbc.query("SELECT * FROM erp_auto_numbers", rs -> {
                    String sql = "INSERT IGNORE INTO erp_auto_numbers (erp_auto_number_id, erp_field_id, prefix, suffix, next_number, padding_length, description, version, created_time, modified_time, created_by, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, 0, NOW(), NOW(), ?, 1)";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_auto_number_id"),
                            rs.getLong("erp_field_id"),
                            rs.getString("prefix"),
                            rs.getString("suffix"),
                            rs.getLong("next_number"),
                            rs.getInt("padding_length"),
                            rs.getString("description"),
                            creatorId);
                });
                logger.info("Auto Numbers copied.");
            } catch (Exception e) {
                logger.error("Failed to copy Auto Numbers: {}", e.getMessage(), e);
                throw e;
            }

            // 5. Copy Roles (Only System Roles)
            try {
                logger.info("Copying Roles...");
                masterJdbc.query("SELECT * FROM erp_roles WHERE system_role = 1", rs -> {
                    String sql = "INSERT IGNORE INTO erp_roles (role_id, name, description, system_role, created_time, created_by) VALUES (?, ?, ?, ?, NOW(), ?)";
                    tenantJdbc.update(sql,
                            rs.getLong("role_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("system_role"),
                            creatorId);
                });
                logger.info("Roles copied.");
            } catch (Exception e) {
                logger.error("Failed to copy Roles: {}", e.getMessage(), e);
                throw e;
            }

            // 6. Copy Permissions (Only System Permissions)
            try {
                logger.info("Copying Permissions...");
                masterJdbc.query("SELECT * FROM erp_permissions WHERE system_permission = 1", rs -> {
                    String sql = "INSERT IGNORE INTO erp_permissions (permission_id, name, description, resource, action, system_permission, created_time, created_by) VALUES (?, ?, ?, ?, ?, ?, NOW(), ?)";
                    tenantJdbc.update(sql,
                            rs.getLong("permission_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("resource"),
                            rs.getString("action"),
                            rs.getBoolean("system_permission"),
                            creatorId);
                });
                logger.info("Permissions copied.");
            } catch (Exception e) {
                logger.error("Failed to copy Permissions: {}", e.getMessage(), e);
                throw e;
            }

            // 7. Copy Role Permissions
            // We need to be careful here to only copy permissions for roles that exist in
            // the tenant DB
            // Since we copied system roles and permissions with their IDs, we can copy the
            // relations directly
            try {
                logger.info("Copying Role Permissions...");
                masterJdbc.query("SELECT rp.* FROM erp_role_permissions rp " +
                        "JOIN erp_roles r ON rp.role_id = r.role_id " +
                        "JOIN erp_permissions p ON rp.permission_id = p.permission_id " +
                        "WHERE r.system_role = 1 AND p.system_permission = 1", rs -> {
                            String sql = "INSERT IGNORE INTO erp_role_permissions (role_id, permission_id) VALUES (?, ?)";
                            tenantJdbc.update(sql,
                                    rs.getLong("role_id"),
                                    rs.getLong("permission_id"));
                        });
                logger.info("Role Permissions copied.");
            } catch (Exception e) {
                logger.error("Failed to copy Role Permissions: {}", e.getMessage(), e);
                throw e;
            }

            // 8. Copy Custom Views (System custom views - public or default views without specific owner)
            try {
                logger.info("Copying Custom Views...");
                masterJdbc.query("SELECT * FROM erp_custom_views WHERE is_public = 1 OR is_default = 1 OR created_by IS NULL", rs -> {
                    String sql = "INSERT IGNORE INTO erp_custom_views (custom_view_id, view_name, description, entity_type, is_default, is_public, created_by, created_time, modified_time, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    tenantJdbc.update(sql,
                            rs.getLong("custom_view_id"),
                            rs.getString("view_name"),
                            rs.getString("description"),
                            rs.getString("entity_type"),
                            rs.getBoolean("is_default"),
                            rs.getBoolean("is_public"),
                            creatorId,
                            rs.getTimestamp("created_time"),
                            rs.getTimestamp("modified_time"),
                            rs.getInt("is_active"));
                });
                logger.info("Custom Views copied.");
            } catch (Exception e) {
                logger.error("Failed to copy Custom Views: {}", e.getMessage(), e);
                throw e;
            }

            // 9. Copy Custom View Fields
            try {
                logger.info("Copying Custom View Fields...");
                masterJdbc.query("SELECT cvf.* FROM erp_custom_view_fields cvf " +
                        "JOIN erp_custom_views cv ON cvf.custom_view_id = cv.custom_view_id " +
                        "WHERE cv.is_public = 1 OR cv.is_default = 1 OR cv.created_by IS NULL", rs -> {
                            String sql = "INSERT IGNORE INTO erp_custom_view_fields (custom_view_id, field_name) VALUES (?, ?)";
                            tenantJdbc.update(sql,
                                    rs.getLong("custom_view_id"),
                                    rs.getString("field_name"));
                        });
                logger.info("Custom View Fields copied.");
            } catch (Exception e) {
                logger.error("Failed to copy Custom View Fields: {}", e.getMessage(), e);
                throw e;
            }

            // 10. Copy Tab Groups
            try {
                logger.info("Copying Tab Groups...");
                masterJdbc.query("SELECT * FROM erp_tab_groups", rs -> {
                    String sql = "INSERT IGNORE INTO erp_tab_groups (erp_tab_group_id, name, code, icon, route_path, sequence, description, is_active, created_by, created_time, modified_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NULL)";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_tab_group_id"),
                            rs.getString("name"),
                            rs.getString("code"),
                            rs.getString("icon"),
                            rs.getString("route_path"),
                            rs.getInt("sequence"),
                            rs.getString("description"),
                            rs.getInt("is_active"),
                            creatorId);
                });
                logger.info("Tab Groups copied.");
            } catch (Exception e) {
                logger.error("Failed to copy Tab Groups: {}", e.getMessage(), e);
                throw e;
            }

            // 11. Copy Tab Group Entity Relations
            try {
                logger.info("Copying Tab Group Entity Relations...");
                masterJdbc.query("SELECT * FROM erp_tab_group_entity_rel", rs -> {
                    String sql = "INSERT IGNORE INTO erp_tab_group_entity_rel (erp_tab_group_entity_rel_id, tab_group_id, entity_id, sequence, is_active, created_by, created_time, modified_time) VALUES (?, ?, ?, ?, ?, ?, NOW(), NULL)";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_tab_group_entity_rel_id"),
                            rs.getLong("tab_group_id"),
                            rs.getLong("entity_id"),
                            rs.getInt("sequence"),
                            rs.getInt("is_active"),
                            creatorId);
                });
                logger.info("Tab Group Entity Relations copied.");
            } catch (Exception e) {
                logger.error("Failed to copy Tab Group Entity Relations: {}", e.getMessage(), e);
                throw e;
            }

            // 12. Copy ERP Plans (for subscription management in tenant context)
            try {
                logger.info("Copying ERP Plans...");
                masterJdbc.query("SELECT * FROM erp_plans", rs -> {
                    String sql = "INSERT IGNORE INTO erp_plans (id, name, type, amount, currency, razorpay_plan_id, description, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
                    tenantJdbc.update(sql,
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getBigDecimal("amount"),
                            rs.getString("currency"),
                            rs.getString("razorpay_plan_id"),
                            rs.getString("description"),
                            rs.getBoolean("is_active"));
                });
                logger.info("ERP Plans copied.");
            } catch (Exception e) {
                logger.error("Failed to copy ERP Plans: {}", e.getMessage(), e);
                // Don't throw - plans might not exist yet
            }

            // 13. Copy Field Mapping Templates (for import functionality)
            try {
                logger.info("Copying Field Mapping Templates...");
                masterJdbc.query("SELECT * FROM erp_field_mapping_templates", rs -> {
                    String sql = "INSERT IGNORE INTO erp_field_mapping_templates (entity_type, field_name, field_label, is_required, data_type, section, display_order, suggestions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    tenantJdbc.update(sql,
                            rs.getString("entity_type"),
                            rs.getString("field_name"),
                            rs.getString("field_label"),
                            rs.getBoolean("is_required"),
                            rs.getString("data_type"),
                            rs.getString("section"),
                            rs.getInt("display_order"),
                            rs.getString("suggestions"));
                });
                logger.info("Field Mapping Templates copied.");
            } catch (Exception e) {
                logger.warn("Failed to copy Field Mapping Templates: {}", e.getMessage());
                // Don't throw - templates can be seeded by initializer if missing
            }

            logger.info("System data copied successfully to tenant DB: {}", dbName);

        } catch (Exception e) {
            logger.error("Error copying system data to tenant DB: {}", e.getMessage(), e);
            // We might want to throw this to fail the registration if system data is
            // critical
            throw new RuntimeException("Failed to copy system data to tenant DB: " + e.getMessage(), e);
        }
    }

    public void deleteTenantDatabase(String dbName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        jdbcTemplate.execute("DROP DATABASE IF EXISTS " + dbName);
    }
}
