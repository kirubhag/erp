package krs.erp.config;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Initializes system-wide data in IAM_MasterDB that should be shared across all
 * tenants.
 * This includes:
 * - ERP Field definitions
 * - ERP Section definitions
 * - ERP Entity definitions
 * - ERP Entity Relations
 * - System Roles
 * - System Permissions
 * 
 * These configurations are loaded from XML files and stored in IAM_MasterDB,
 * not in tenant databases.
 */
@Component
@Order(1) // Run before tenant-specific initializers
public class MasterDbSystemDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MasterDbSystemDataInitializer.class);

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    private JdbcTemplate masterJdbcTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== Starting IAM_MasterDB System Data Initialization ===");

        // Initialize master JdbcTemplate
        masterJdbcTemplate = new JdbcTemplate(masterDataSource);

        // Ensure IAM_MasterDB exists
        ensureMasterDatabaseExists();

        // Load system data in order
        loadSystemPermissions();
        loadSystemRoles();
        loadRolePermissions();
        loadErpSections();
        loadErpFields();
        loadErpEntities();
        loadErpEntityRelations();

        logger.info("=== IAM_MasterDB System Data Initialization Completed ===");
    }

    /**
     * Ensure IAM_MasterDB database exists
     */
    private void ensureMasterDatabaseExists() {
        try {
            // Get connection URL and create a connection to MySQL server (without database)
            HikariDataSource ds = (HikariDataSource) masterDataSource;
            String jdbcUrl = ds.getJdbcUrl();

            // Check if we're already connected to IAM_MasterDB
            if (jdbcUrl.contains("/IAM_MasterDB")) {
                logger.info("Already connected to IAM_MasterDB");
                return;
            }

            // Create IAM_MasterDB if it doesn't exist
            String serverUrl = jdbcUrl.substring(0, jdbcUrl.lastIndexOf('/'));
            HikariDataSource serverDs = new HikariDataSource();
            serverDs.setJdbcUrl(serverUrl + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            serverDs.setUsername(ds.getUsername());
            serverDs.setPassword(ds.getPassword());
            serverDs.setDriverClassName(ds.getDriverClassName());

            try (Connection conn = serverDs.getConnection();
                    Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS IAM_MasterDB");
                logger.info("✓ IAM_MasterDB database ensured");
            } finally {
                serverDs.close();
            }

        } catch (Exception e) {
            logger.warn("Could not ensure IAM_MasterDB exists: {}", e.getMessage());
        }
    }

    /**
     * Load system permissions from sample-permissions.xml
     */
    private void loadSystemPermissions() {
        try {
            logger.info("Loading system permissions into IAM_MasterDB...");

            // Check if permissions already exist
            // Integer count = masterJdbcTemplate.queryForObject(
            // "SELECT COUNT(*) FROM IAM_MasterDB.permissions", Integer.class);

            // if (count != null && count > 0) {
            // logger.info("Permissions already exist in IAM_MasterDB ({}). Skipping.",
            // count);
            // return;
            // }

            // Load from XML
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:data/permission/sample-permissions.xml");

            if (!resource.exists()) {
                logger.warn("sample-permissions.xml not found");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(resource.getInputStream());

            NodeList permissionNodes = doc.getElementsByTagName("permissions");
            int loaded = 0;

            for (int i = 0; i < permissionNodes.getLength(); i++) {
                Element permElement = (Element) permissionNodes.item(i);

                String name = permElement.getAttribute("name");
                String description = permElement.getAttribute("description");
                String resource_name = permElement.getAttribute("resource");
                String action = permElement.getAttribute("action");
                Boolean systemPermission = "1".equals(permElement.getAttribute("system_permission"));
                String createdTime = permElement.getAttribute("created_time");

                masterJdbcTemplate.update(
                        "INSERT INTO IAM_MasterDB.permissions (name, description, resource, action, system_permission, created_time, modified_time) "
                                +
                                "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE modified_time = ?",
                        name, description, resource_name, action, systemPermission,
                        LocalDateTime.parse(createdTime, DATE_FORMATTER),
                        LocalDateTime.now(),
                        LocalDateTime.now());
                loaded++;
            }

            logger.info("✓ Loaded {} system permissions into IAM_MasterDB", loaded);

        } catch (Exception e) {
            logger.error("Error loading system permissions", e);
        }
    }

    /**
     * Load system roles from sample-roles.xml
     */
    private void loadSystemRoles() {
        try {
            logger.info("Loading system roles into IAM_MasterDB...");

            // Check if roles already exist
            // Integer count = masterJdbcTemplate.queryForObject(
            // "SELECT COUNT(*) FROM IAM_MasterDB.roles", Integer.class);

            // if (count != null && count > 0) {
            // logger.info("Roles already exist in IAM_MasterDB ({}). Skipping.", count);
            // return;
            // }

            // Load from XML
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:data/role/sample-roles.xml");

            if (!resource.exists()) {
                logger.warn("sample-roles.xml not found");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(resource.getInputStream());

            NodeList roleNodes = doc.getElementsByTagName("roles");
            int loaded = 0;

            for (int i = 0; i < roleNodes.getLength(); i++) {
                Element roleElement = (Element) roleNodes.item(i);

                String name = roleElement.getAttribute("name");
                String description = roleElement.getAttribute("description");
                Boolean systemRole = "1".equals(roleElement.getAttribute("system_role"));
                String createdTime = roleElement.getAttribute("created_time");

                masterJdbcTemplate.update(
                        "INSERT INTO IAM_MasterDB.roles (name, description, system_role, created_time, modified_time) "
                                +
                                "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE modified_time = ?",
                        name, description, systemRole,
                        LocalDateTime.parse(createdTime, DATE_FORMATTER),
                        LocalDateTime.now(),
                        LocalDateTime.now());
                loaded++;
            }

            logger.info("✓ Loaded {} system roles into IAM_MasterDB", loaded);

        } catch (Exception e) {
            logger.error("Error loading system roles", e);
        }
    }

    /**
     * Load role-permission mappings from sample-roles.xml
     */
    private void loadRolePermissions() {
        try {
            logger.info("Loading role-permission mappings into IAM_MasterDB...");

            // Check if mappings already exist
            // Integer count = masterJdbcTemplate.queryForObject(
            // "SELECT COUNT(*) FROM IAM_MasterDB.role_permissions", Integer.class);

            // if (count != null && count > 0) {
            // logger.info("Role-permission mappings already exist in IAM_MasterDB ({}).
            // Skipping.", count);
            // return;
            // }

            // Load from XML if it contains role_permissions nodes
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:data/role/sample-roles.xml");

            if (!resource.exists()) {
                logger.warn("sample-roles.xml not found for role-permission mappings");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(resource.getInputStream());

            NodeList mappingNodes = doc.getElementsByTagName("role_permissions");
            int loaded = 0;

            for (int i = 0; i < mappingNodes.getLength(); i++) {
                Element mappingElement = (Element) mappingNodes.item(i);

                String roleIdStr = mappingElement.getAttribute("role_id");
                String permissionIdStr = mappingElement.getAttribute("permission_id");

                if (roleIdStr != null && !roleIdStr.isEmpty() &&
                        permissionIdStr != null && !permissionIdStr.isEmpty()) {

                    masterJdbcTemplate.update(
                            "INSERT IGNORE INTO IAM_MasterDB.role_permissions (role_id, permission_id) VALUES (?, ?)",
                            Long.parseLong(roleIdStr), Long.parseLong(permissionIdStr));
                    loaded++;
                }
            }

            logger.info("✓ Loaded {} role-permission mappings into IAM_MasterDB", loaded);

        } catch (Exception e) {
            logger.error("Error loading role-permission mappings", e);
        }
    }

    /**
     * Load ERP sections from *_sections.xml files
     */
    private void loadErpSections() {
        try {
            logger.info("Loading ERP sections into IAM_MasterDB...");

            // Check if sections already exist
            // Integer count = masterJdbcTemplate.queryForObject(
            // "SELECT COUNT(*) FROM IAM_MasterDB.erp_sections", Integer.class);

            // if (count != null && count > 0) {
            // logger.info("ERP sections already exist in IAM_MasterDB ({}). Skipping.",
            // count);
            // return;
            // }

            // Load all *_sections.xml files
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:data/**/*_sections.xml");

            int totalLoaded = 0;

            for (Resource resource : resources) {
                logger.info("Loading sections from: {}", resource.getFilename());

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(resource.getInputStream());

                Element root = doc.getDocumentElement();
                String entityType = root.getAttribute("type");

                NodeList sectionNodes = root.getElementsByTagName("section");

                for (int i = 0; i < sectionNodes.getLength(); i++) {
                    Element sectionElement = (Element) sectionNodes.item(i);

                    String sectionName = getElementText(sectionElement, "sectionName");
                    String sectionLabel = getElementText(sectionElement, "sectionLabel");
                    String layoutType = getElementText(sectionElement, "layoutType", "TWO_COLUMN");
                    Integer displayOrder = getElementInt(sectionElement, "displayOrder", 0);
                    Boolean isCollapsible = getElementBoolean(sectionElement, "isCollapsible", false);
                    Boolean isCollapsedByDefault = getElementBoolean(sectionElement, "isCollapsedByDefault", false);
                    Boolean showInCreate = getElementBoolean(sectionElement, "showInCreate", true);
                    Boolean showInEdit = getElementBoolean(sectionElement, "showInEdit", true);
                    Boolean showInDetail = getElementBoolean(sectionElement, "showInDetail", true);
                    String description = getElementText(sectionElement, "description", null);

                    masterJdbcTemplate.update(
                            "INSERT INTO IAM_MasterDB.erp_sections " +
                                    "(entity_type, section_name, section_label, layout_type, display_order, " +
                                    "is_collapsible, is_collapsed_by_default, show_in_create, show_in_edit, show_in_detail, "
                                    +
                                    "description, created_time, modified_time, is_active) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE modified_time = ?",
                            entityType, sectionName, sectionLabel, layoutType, displayOrder,
                            isCollapsible, isCollapsedByDefault, showInCreate, showInEdit, showInDetail,
                            description, LocalDateTime.now(), LocalDateTime.now(), 1,
                            LocalDateTime.now());
                    totalLoaded++;
                }
            }

            logger.info("✓ Loaded {} ERP sections into IAM_MasterDB", totalLoaded);

        } catch (Exception e) {
            logger.error("Error loading ERP sections", e);
        }
    }

    /**
     * Load ERP fields from *_fields.xml files
     */
    private void loadErpFields() {
        try {
            logger.info("Loading ERP fields into IAM_MasterDB...");

            // Check if fields already exist
            // Integer count = masterJdbcTemplate.queryForObject(
            // "SELECT COUNT(*) FROM IAM_MasterDB.erp_fields", Integer.class);

            // if (count != null && count > 0) {
            // logger.info("ERP fields already exist in IAM_MasterDB ({}). Skipping.",
            // count);
            // return;
            // }

            // Load all *_fields.xml files
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:data/**/*_fields.xml");

            int totalLoaded = 0;

            for (Resource resource : resources) {
                logger.info("Loading fields from: {}", resource.getFilename());

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(resource.getInputStream());

                Element root = doc.getDocumentElement();
                String entityType = root.getAttribute("type");
                
                // Validate entity type
                if (entityType == null || entityType.trim().isEmpty()) {
                    logger.warn("Skipping file {} - no entity type specified", resource.getFilename());
                    continue;
                }
                
                logger.debug("Processing fields for entity type: {}", entityType);

                NodeList fieldNodes = root.getElementsByTagName("field");

                for (int i = 0; i < fieldNodes.getLength(); i++) {
                    try {
                        Element fieldElement = (Element) fieldNodes.item(i);

                        String fieldName = getElementText(fieldElement, "fieldName");
                        String fieldLabel = getElementText(fieldElement, "fieldLabel");
                        String fieldType = getElementText(fieldElement, "fieldType");
                        String sectionName = getElementText(fieldElement, "sectionName", null);
                        Integer displayOrder = getElementInt(fieldElement, "displayOrder", 0);
                        Boolean isRequired = getElementBoolean(fieldElement, "isRequired", false);
                        Boolean isSearchable = getElementBoolean(fieldElement, "isSearchable", true);
                        Boolean isSortable = getElementBoolean(fieldElement, "isSortable", true);
                        Boolean showInList = getElementBoolean(fieldElement, "showInList", true);
                        Boolean showInForm = getElementBoolean(fieldElement, "showInForm", true);
                        String description = getElementText(fieldElement, "description", null);
                        Integer maxLength = getElementInt(fieldElement, "maxLength", null);
                        String validationPattern = getElementText(fieldElement, "validationPattern", null);

                        // Get section_id from section_name
                        Long sectionId = null;
                        if (sectionName != null && !sectionName.isEmpty()) {
                            try {
                                sectionId = masterJdbcTemplate.queryForObject(
                                        "SELECT erp_section_id FROM IAM_MasterDB.erp_sections WHERE entity_type = ? AND section_name = ?",
                                        Long.class, entityType, sectionName);
                            } catch (Exception e) {
                                logger.warn(
                                        "Section not found: {} for entity: {}. Field: {} will be linked to null section.",
                                        sectionName, entityType, fieldName);
                            }
                        }

                        masterJdbcTemplate.update(
                                "INSERT INTO IAM_MasterDB.erp_fields " +
                                        "(entity_type, field_name, field_label, field_type, section_id, display_order, "
                                        +
                                        "is_required, is_searchable, is_sortable, show_in_list, show_in_form, " +
                                        "field_description, max_length, validation_pattern, created_time, modified_time, is_active) "
                                        +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                        "ON DUPLICATE KEY UPDATE modified_time = ?",
                                entityType, fieldName, fieldLabel, fieldType, sectionId, displayOrder,
                                isRequired, isSearchable, isSortable, showInList, showInForm,
                                description, maxLength, validationPattern, LocalDateTime.now(), LocalDateTime.now(), 1,
                                LocalDateTime.now());
                        totalLoaded++;
                    } catch (Exception e) {
                        String fieldName = "unknown";
                        try {
                            fieldName = getElementText((Element) fieldNodes.item(i), "fieldName");
                        } catch (Exception ex) {
                            // ignore
                        }
                        logger.error("Error loading field '{}' for entity type '{}' from {}: {}", 
                            fieldName, entityType, resource.getFilename(), e.getMessage());
                    }
                }
            }

            logger.info("✓ Loaded {} ERP fields into IAM_MasterDB", totalLoaded);

        } catch (Exception e) {
            logger.error("Error loading ERP fields", e);
        }
    }

    /**
     * Load ERP entities from erp-entities.xml
     */
    private void loadErpEntities() {
        try {
            logger.info("Loading ERP entities into IAM_MasterDB...");

            // Check if entities already exist
            // Integer count = masterJdbcTemplate.queryForObject(
            // "SELECT COUNT(*) FROM IAM_MasterDB.erp_entities", Integer.class);

            // if (count != null && count > 0) {
            // logger.info("ERP entities already exist in IAM_MasterDB ({}). Skipping.",
            // count);
            // return;
            // }

            // Load from XML
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:data/erp-entities.xml");

            if (!resource.exists()) {
                logger.warn("erp-entities.xml not found");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(resource.getInputStream());

            NodeList entityNodes = doc.getElementsByTagName("entity");
            int loaded = 0;

            for (int i = 0; i < entityNodes.getLength(); i++) {
                Element entityElement = (Element) entityNodes.item(i);

                String singularName = getElementText(entityElement, "singular_name");
                String pluralName = getElementText(entityElement, "plural_name");
                String systemName = getElementText(entityElement, "system_name");
                String description = getElementText(entityElement, "description", null);
                String tableName = getElementText(entityElement, "table_name", null);
                String pkid = getElementText(entityElement, "pkid", null);
                String displayColumn = getElementText(entityElement, "display_column", null);
                Boolean hasRelTable = getElementBoolean(entityElement, "has_rel_table", false);
                String icon = getElementText(entityElement, "icon", null);
                String route = getElementText(entityElement, "route", null);
                Integer sequence = getElementInt(entityElement, "sequence", 0);
                Boolean presence = getElementBoolean(entityElement, "presence", true);
                Boolean isActive = getElementBoolean(entityElement, "is_active", true);

                masterJdbcTemplate.update(
                        "INSERT INTO IAM_MasterDB.erp_entities " +
                                "(singular_name, plural_name, system_name, description, table_name, pkid, display_column, "
                                +
                                "has_rel_table, icon, route, sequence, presence, is_active, created_date, last_modified_date, created_by, last_modified_by) "
                                +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE last_modified_date = ?, last_modified_by = ?",
                        singularName, pluralName, systemName, description, tableName, pkid, displayColumn,
                        hasRelTable, icon, route, sequence, presence, isActive, LocalDateTime.now(),
                        LocalDateTime.now(), "SYSTEM", "SYSTEM",
                        LocalDateTime.now(), "SYSTEM");
                loaded++;
            }

            logger.info("✓ Loaded {} ERP entities into IAM_MasterDB", loaded);

        } catch (Exception e) {
            logger.error("Error loading ERP entities", e);
        }
    }

    /**
     * Load ERP entity relations from erp-entity-relations.xml
     */
    private void loadErpEntityRelations() {
        try {
            logger.info("Loading ERP entity relations into IAM_MasterDB...");

            // Check if relations already exist
            // Integer count = masterJdbcTemplate.queryForObject(
            // "SELECT COUNT(*) FROM IAM_MasterDB.erp_entity_relations", Integer.class);

            // if (count != null && count > 0) {
            // logger.info("ERP entity relations already exist in IAM_MasterDB ({}).
            // Skipping.", count);
            // return;
            // }

            // Load from XML
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:data/erp-entity-relations.xml");

            if (!resource.exists()) {
                logger.warn("erp-entity-relations.xml not found");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(resource.getInputStream());

            NodeList relationNodes = doc.getElementsByTagName("relation");
            int loaded = 0;

            for (int i = 0; i < relationNodes.getLength(); i++) {
                Element relationElement = (Element) relationNodes.item(i);

                String parentTableName = getElementText(relationElement, "p_table_name");
                String childTableName = getElementText(relationElement, "c_table_name");
                String foreignKeyColumn = getElementText(relationElement, "fk_column", null);
                String description = getElementText(relationElement, "description", null);
                Boolean isActive = getElementBoolean(relationElement, "is_active", true);

                // Get entity IDs from table names (which are used as system_name in
                // erp_entities)
                // Skip if either entity doesn't exist
                Long parentEntityId = null;
                Long childEntityId = null;

                try {
                    parentEntityId = masterJdbcTemplate.queryForObject(
                            "SELECT erp_entity_id FROM IAM_MasterDB.erp_entities WHERE system_name = ? OR table_name = ?",
                            Long.class, parentTableName, parentTableName);
                } catch (Exception e) {
                    logger.warn("Parent entity not found for table: {}. Skipping relation.", parentTableName);
                    continue;
                }

                try {
                    childEntityId = masterJdbcTemplate.queryForObject(
                            "SELECT erp_entity_id FROM IAM_MasterDB.erp_entities WHERE system_name = ? OR table_name = ?",
                            Long.class, childTableName, childTableName);
                } catch (Exception e) {
                    logger.warn("Child entity not found for table: {}. Skipping relation.", childTableName);
                    continue;
                }

                masterJdbcTemplate.update(
                        "INSERT INTO IAM_MasterDB.erp_entity_relations " +
                                "(parent_entity_id, child_entity_id, relation_type, relation_name, foreign_key_column, "
                                +
                                "is_mandatory, cascade_delete, display_order, is_active, created_time, modified_time) "
                                +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE modified_time = ?",
                        parentEntityId, childEntityId, "ONE_TO_MANY", description, foreignKeyColumn,
                        false, false, 0, isActive, LocalDateTime.now(), LocalDateTime.now(),
                        LocalDateTime.now());
                loaded++;
            }

            logger.info("✓ Loaded {} ERP entity relations into IAM_MasterDB", loaded);

        } catch (Exception e) {
            logger.error("Error loading ERP entity relations", e);
        }
    }

    // Helper methods for XML parsing

    private String getElementText(Element parent, String tagName) {
        return getElementText(parent, tagName, null);
    }

    private String getElementText(Element parent, String tagName, String defaultValue) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            String text = node.getTextContent();
            return (text != null && !text.trim().isEmpty()) ? text.trim() : defaultValue;
        }
        return defaultValue;
    }

    private Integer getElementInt(Element parent, String tagName, Integer defaultValue) {
        String text = getElementText(parent, tagName, null);
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
        String text = getElementText(parent, tagName, null);
        if (text != null && !text.isEmpty()) {
            return "true".equalsIgnoreCase(text) || "1".equals(text);
        }
        return defaultValue;
    }
}
