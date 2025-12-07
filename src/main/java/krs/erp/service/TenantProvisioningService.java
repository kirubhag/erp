package krs.erp.service;

import javax.sql.DataSource;

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

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    public void provisionTenantDatabase(String dbName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);

        // 1. Create Database
        jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS " + dbName);

        // 2. Initialize Schema
        initializeTenantSchema(dbName);
    }

    private void initializeTenantSchema(String dbName) {
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
            copySystemData(dbName, tenantDataSource);
        } finally {
            tenantDataSource.close();
        }
    }

    private void copySystemData(String dbName, HikariDataSource tenantDataSource) {
        JdbcTemplate masterJdbc = new JdbcTemplate(masterDataSource);
        JdbcTemplate tenantJdbc = new JdbcTemplate(tenantDataSource);

        try {
            System.out.println("Starting system data copy for tenant: " + dbName);

            // 1. Copy ERP Entities
            try {
                System.out.println("Copying ERP Entities...");
                masterJdbc.query("SELECT * FROM erp_entities", rs -> {
                    String sql = "INSERT INTO erp_entities (erp_entity_id, singular_name, plural_name, description, is_active, sequence, system_name, presence, icon, route, table_name, pkid, display_column, has_rel_table, created_date, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 'SYSTEM')";
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
                            rs.getBoolean("has_rel_table"));
                });
                System.out.println("ERP Entities copied.");
            } catch (Exception e) {
                System.err.println("Failed to copy ERP Entities: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // 2. Copy ERP Sections
            try {
                System.out.println("Copying ERP Sections...");
                masterJdbc.query("SELECT * FROM erp_sections", rs -> {
                    String sql = "INSERT INTO erp_sections (erp_section_id, entity_type, section_name, section_label, layout_type, display_order, is_collapsible, is_collapsed_by_default, show_in_create, show_in_edit, show_in_detail, section_icon, section_color, css_class, description, help_text, created_time, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 'SYSTEM')";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_section_id"),
                            rs.getString("entity_type"),
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
                            rs.getString("help_text"));
                });
                System.out.println("ERP Sections copied.");
            } catch (Exception e) {
                System.err.println("Failed to copy ERP Sections: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // 3. Copy ERP Fields
            try {
                System.out.println("Copying ERP Fields...");
                masterJdbc.query("SELECT * FROM erp_fields", rs -> {
                    String sql = "INSERT INTO erp_fields (erp_field_id, entity_type, field_name, field_label, field_type, ui_type, section_id, row_position, column_position, is_required, is_searchable, is_sortable, display_order, field_description, default_width, max_length, validation_pattern, picklist_options, decimal_places, is_unique, show_in_list, show_in_form, column_width, created_time, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 'SYSTEM')";
                    tenantJdbc.update(sql,
                            rs.getLong("erp_field_id"),
                            rs.getString("entity_type"),
                            rs.getString("field_name"),
                            rs.getString("field_label"),
                            rs.getString("field_type"),
                            rs.getObject("ui_type"),
                            rs.getObject("section_id"),
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
                            rs.getString("column_width"));
                });
                System.out.println("ERP Fields copied.");
            } catch (Exception e) {
                System.err.println("Failed to copy ERP Fields: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // 4. Copy Roles (Only System Roles)
            try {
                System.out.println("Copying Roles...");
                masterJdbc.query("SELECT * FROM roles WHERE system_role = 1", rs -> {
                    String sql = "INSERT INTO roles (id, name, description, system_role, created_time, created_by) VALUES (?, ?, ?, ?, NOW(), 'SYSTEM')";
                    tenantJdbc.update(sql,
                            rs.getLong("role_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("system_role"));
                });
                System.out.println("Roles copied.");
            } catch (Exception e) {
                System.err.println("Failed to copy Roles: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // 5. Copy Permissions (Only System Permissions)
            try {
                System.out.println("Copying Permissions...");
                masterJdbc.query("SELECT * FROM permissions WHERE system_permission = 1", rs -> {
                    String sql = "INSERT INTO permissions (id, name, description, resource, action, system_permission, created_time, created_by) VALUES (?, ?, ?, ?, ?, ?, NOW(), 'SYSTEM')";
                    tenantJdbc.update(sql,
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("resource"),
                            rs.getString("action"),
                            rs.getBoolean("system_permission"));
                });
                System.out.println("Permissions copied.");
            } catch (Exception e) {
                System.err.println("Failed to copy Permissions: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // 6. Copy Role Permissions
            // We need to be careful here to only copy permissions for roles that exist in
            // the tenant DB
            // Since we copied system roles and permissions with their IDs, we can copy the
            // relations directly
            try {
                System.out.println("Copying Role Permissions...");
                masterJdbc.query("SELECT rp.* FROM role_permissions rp " +
                        "JOIN roles r ON rp.role_id = r.role_id " +
                        "JOIN permissions p ON rp.permission_id = p.id " +
                        "WHERE r.system_role = 1 AND p.system_permission = 1", rs -> {
                            String sql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
                            tenantJdbc.update(sql,
                                    rs.getLong("role_id"),
                                    rs.getLong("permission_id"));
                        });
                System.out.println("Role Permissions copied.");
            } catch (Exception e) {
                System.err.println("Failed to copy Role Permissions: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            System.out.println("System data copied successfully to tenant DB: " + dbName);

        } catch (Exception e) {
            System.err.println("Error copying system data to tenant DB: " + e.getMessage());
            e.printStackTrace();
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
