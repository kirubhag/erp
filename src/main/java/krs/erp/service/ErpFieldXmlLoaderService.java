package krs.erp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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

    private static final String XML_FILE_PATH = "data/erpfields.xml";

    /**
     * Load all entity fields from XML configuration
     * Checks for existing fields and only creates new ones
     */
    public void loadFieldsFromXml() {
        try {
            logger.info("Loading ERP field definitions from XML: {}", XML_FILE_PATH);
            
            ClassPathResource resource = new ClassPathResource(XML_FILE_PATH);
            if (!resource.exists()) {
                logger.error("XML file not found: {}", XML_FILE_PATH);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(resource.getInputStream());
            document.getDocumentElement().normalize();

            NodeList entityNodes = document.getElementsByTagName("entity");
            int totalFieldsLoaded = 0;

            for (int i = 0; i < entityNodes.getLength(); i++) {
                Element entityElement = (Element) entityNodes.item(i);
                String entityTypeStr = entityElement.getAttribute("type");
                
                try {
                    EntityType entityType = EntityType.valueOf(entityTypeStr);
                    int fieldsLoaded = loadEntityFields(entityElement, entityType);
                    totalFieldsLoaded += fieldsLoaded;
                    logger.info("Loaded {} fields for entity type: {}", fieldsLoaded, entityType);
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid entity type in XML: {}", entityTypeStr);
                }
            }

            logger.info("✓ ERP field XML loading completed. Total fields processed: {}", totalFieldsLoaded);

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
                field.setIsActive(true);
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
            field.setFieldCategory(getElementText(fieldElement, "fieldCategory"));
            field.setDisplayOrder(getElementTextAsInt(fieldElement, "displayOrder", 0));
            field.setDefaultWidth(getElementTextAsInt(fieldElement, "defaultWidth", 100));
            field.setIsRequired(getElementTextAsBoolean(fieldElement, "isRequired", false));
            field.setIsSearchable(getElementTextAsBoolean(fieldElement, "isSearchable", false));
            field.setIsSortable(getElementTextAsBoolean(fieldElement, "isSortable", false));
            field.setFieldDescription(getElementText(fieldElement, "fieldDescription"));
            
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