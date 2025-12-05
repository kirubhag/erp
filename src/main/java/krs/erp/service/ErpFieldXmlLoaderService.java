package krs.erp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.enums.EntityType;
import krs.erp.model.ErpField;
import krs.erp.model.FieldType;
import krs.erp.repository.ErpFieldRepository;

/**
 * Service for loading ERP field definitions from XML configuration
 * Provides centralized field management through XML-based configuration
 */
@Service
public class ErpFieldXmlLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ErpFieldXmlLoaderService.class);

    @Autowired
    private ErpFieldRepository erpFieldRepository;

    private static final String XML_FILE_PATTERN = "classpath:data/**/*_fields.xml";

    /**
     * Load all entity fields from XML configuration files in subdirectories
     * Scans for all *_fields.xml files and loads them
     * Checks for existing fields and only creates new ones
     */
    public void loadFieldsFromXml() {
        try {
            logger.info("Loading ERP field definitions from XML pattern: {}", XML_FILE_PATTERN);

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(XML_FILE_PATTERN);

            if (resources.length == 0) {
                logger.error("No XML files found matching pattern: {}", XML_FILE_PATTERN);
                return;
            }

            logger.info("Found {} field definition XML files", resources.length);

            for (Resource resource : resources) {
                loadFieldsFromResource(resource);
            }

        } catch (Exception e) {
            logger.error("Error loading field definitions from XML", e);
        }
    }

    /**
     * Load fields from a single XML resource
     */
    private void loadFieldsFromResource(Resource resource) {
        try {
            logger.info("Loading fields from: {}", resource.getFilename());

            if (!resource.exists()) {
                logger.error("XML resource not found: {}", resource.getFilename());
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(resource.getInputStream());
            document.getDocumentElement().normalize();

            // Look for entityFields tag (actual XML structure)
            Element rootElement = document.getDocumentElement();
            String entityTypeStr = rootElement.getAttribute("type");

            if (entityTypeStr == null || entityTypeStr.isEmpty()) {
                logger.error("No entity type found in XML root element for: {}", resource.getFilename());
                return;
            }

            try {
                EntityType entityType = EntityType.valueOf(entityTypeStr);
                int fieldsLoaded = loadEntityFields(rootElement, entityType);
                logger.info("✓ Loaded {} fields for entity type: {} from {}", fieldsLoaded, entityType,
                        resource.getFilename());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid entity type in XML: {}", entityTypeStr);
            }

        } catch (Exception e) {
            logger.error("Error loading ERP fields from XML: {}", e.getMessage(), e);
        }
    }

    /**
     * Load fields for a specific entity type from XML element
     */
    private int loadEntityFields(Element entityElement, EntityType entityType) {
        NodeList fieldNodes = entityElement.getElementsByTagName("field");
        List<ErpField> fieldsToSave = new ArrayList<>();
        int existingFieldsCount = 0;

        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Element fieldElement = (Element) fieldNodes.item(i);

            String fieldName = getElementText(fieldElement, "fieldName");

            // Check if field already exists
            if (fieldExists(entityType, fieldName)) {
                existingFieldsCount++;
                logger.debug("Field already exists: {}.{}", entityType, fieldName);
                continue;
            }

            ErpField field = createFieldFromXml(fieldElement, entityType);
            if (field != null) {
                fieldsToSave.add(field);
            }
        }

        // Save new fields in batch
        if (!fieldsToSave.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            fieldsToSave.forEach(field -> {
                field.setCreatedTime(now);
                field.setModifiedTime(now);
                field.setIsActive(1);
            });

            erpFieldRepository.saveAll(fieldsToSave);
            logger.info("Saved {} new fields for {}, {} already existed",
                    fieldsToSave.size(), entityType, existingFieldsCount);
        } else {
            logger.info("All {} fields for {} already exist", existingFieldsCount, entityType);
        }

        return fieldsToSave.size();
    }

    /**
     * Create ErpField entity from XML field element
     */
    private ErpField createFieldFromXml(Element fieldElement, EntityType entityType) {
        try {
            String fieldName = getElementText(fieldElement, "fieldName");
            String fieldLabel = getElementText(fieldElement, "fieldLabel");
            String fieldTypeStr = getElementText(fieldElement, "fieldType");

            FieldType fieldType = FieldType.valueOf(fieldTypeStr);

            ErpField field = new ErpField(entityType, fieldName, fieldLabel, fieldType);

            // Set optional fields with defaults
            // Note: fieldCategory from XML is now handled by sections
            field.setDisplayOrder(getElementTextAsInt(fieldElement, "displayOrder", 0));
            field.setDefaultWidth(getElementTextAsInt(fieldElement, "defaultWidth", 100));
            field.setIsRequired(getElementTextAsBoolean(fieldElement, "isRequired", false));
            field.setIsSearchable(getElementTextAsBoolean(fieldElement, "isSearchable", false));
            field.setIsSortable(getElementTextAsBoolean(fieldElement, "isSortable", false));
            field.setFieldDescription(getElementText(fieldElement, "fieldDescription"));
            field.setShowType(getElementTextAsInt(fieldElement, "showType", 0));

            return field;

        } catch (Exception e) {
            logger.error("Error creating field from XML element: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if field already exists for entity type
     */
    private boolean fieldExists(EntityType entityType, String fieldName) {
        return erpFieldRepository.existsByEntityTypeAndFieldNameAndIsActiveTrue(entityType, fieldName);
    }

    /**
     * Utility method to get text content from XML element
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }

    /**
     * Utility method to get integer value from XML element
     */
    private Integer getElementTextAsInt(Element parent, String tagName, int defaultValue) {
        String text = getElementText(parent, tagName);
        if (text != null && !text.isEmpty()) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer value for {}: {}", tagName, text);
            }
        }
        return defaultValue;
    }

    /**
     * Utility method to get boolean value from XML element
     */
    private Boolean getElementTextAsBoolean(Element parent, String tagName, boolean defaultValue) {
        String text = getElementText(parent, tagName);
        if (text != null && !text.isEmpty()) {
            return Boolean.parseBoolean(text);
        }
        return defaultValue;
    }

    /**
     * Get total count of fields loaded from XML for all entities
     */
    public long getTotalFieldCount() {
        return erpFieldRepository.count();
    }

    /**
     * Get count of fields for specific entity type
     */
    public long getFieldCountForEntity(EntityType entityType) {
        return erpFieldRepository.countByEntityTypeAndIsActiveTrue(entityType);
    }
}