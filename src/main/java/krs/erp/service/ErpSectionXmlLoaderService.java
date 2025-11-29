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
import krs.erp.enums.SectionLayoutType;
import krs.erp.model.ErpSection;
import krs.erp.repository.ErpSectionRepository;

/**
 * Service for loading ERP section definitions from XML configuration
 * Sections must be loaded before fields since fields reference sections
 */
@Service
public class ErpSectionXmlLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ErpSectionXmlLoaderService.class);

    @Autowired
    private ErpSectionRepository erpSectionRepository;

    private static final String XML_FILE_PATTERN = "classpath:data/**/*_sections.xml";

    /**
     * Load all entity sections from XML configuration files
     * Must be called before loading fields
     */
    public void loadSectionsFromXml() {
        try {
            logger.info("Loading ERP section definitions from XML pattern: {}", XML_FILE_PATTERN);
            
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(XML_FILE_PATTERN);
            
            if (resources.length == 0) {
                logger.warn("No section XML files found matching pattern: {}", XML_FILE_PATTERN);
                return;
            }
            
            logger.info("Found {} section definition XML files", resources.length);
            
            for (Resource resource : resources) {
                loadSectionsFromResource(resource);
            }
            
        } catch (Exception e) {
            logger.error("Error loading section definitions from XML", e);
        }
    }
    
    /**
     * Load sections from a single XML resource
     */
    private void loadSectionsFromResource(Resource resource) {
        try {
            logger.info("Loading sections from: {}", resource.getFilename());
            
            if (!resource.exists()) {
                logger.error("XML resource not found: {}", resource.getFilename());
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(resource.getInputStream());
            document.getDocumentElement().normalize();

            Element rootElement = document.getDocumentElement();
            String entityTypeStr = rootElement.getAttribute("type");
            
            if (entityTypeStr == null || entityTypeStr.isEmpty()) {
                logger.error("No entity type found in XML root element for: {}", resource.getFilename());
                return;
            }
            
            try {
                EntityType entityType = EntityType.valueOf(entityTypeStr);
                int sectionsLoaded = loadEntitySections(rootElement, entityType);
                logger.info("✓ Loaded {} sections for entity type: {} from {}", sectionsLoaded, entityType, resource.getFilename());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid entity type in XML: {}", entityTypeStr);
            }

        } catch (Exception e) {
            logger.error("Error loading ERP sections from XML: {}", e.getMessage(), e);
        }
    }

    /**
     * Load sections for a specific entity type from XML element
     */
    private int loadEntitySections(Element entityElement, EntityType entityType) {
        NodeList sectionNodes = entityElement.getElementsByTagName("section");
        List<ErpSection> sectionsToSave = new ArrayList<>();
        int existingSectionsCount = 0;

        for (int i = 0; i < sectionNodes.getLength(); i++) {
            Element sectionElement = (Element) sectionNodes.item(i);
            
            String sectionName = getElementText(sectionElement, "sectionName");
            
            // Check if section already exists
            if (sectionExists(entityType, sectionName)) {
                existingSectionsCount++;
                logger.debug("Section already exists: {}.{}", entityType, sectionName);
                continue;
            }

            ErpSection section = createSectionFromXml(sectionElement, entityType);
            if (section != null) {
                sectionsToSave.add(section);
            }
        }

        // Save new sections in batch
        if (!sectionsToSave.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            sectionsToSave.forEach(section -> {
                section.setCreatedTime(now);
                section.setModifiedTime(now);
                section.setIsActive(1);
            });
            
            erpSectionRepository.saveAll(sectionsToSave);
            logger.info("Saved {} new sections for {}, {} already existed", 
                       sectionsToSave.size(), entityType, existingSectionsCount);
        } else {
            logger.info("All {} sections for {} already exist", existingSectionsCount, entityType);
        }

        return sectionsToSave.size();
    }

    /**
     * Create ErpSection entity from XML section element
     */
    private ErpSection createSectionFromXml(Element sectionElement, EntityType entityType) {
        try {
            String sectionName = getElementText(sectionElement, "sectionName");
            String sectionLabel = getElementText(sectionElement, "sectionLabel");
            
            ErpSection section = new ErpSection(entityType, sectionName, sectionLabel);
            
            // Set optional fields with defaults
            String layoutTypeStr = getElementText(sectionElement, "layoutType");
            if (layoutTypeStr != null && !layoutTypeStr.isEmpty()) {
                try {
                    section.setLayoutType(SectionLayoutType.valueOf(layoutTypeStr));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid layout type: {}, using default", layoutTypeStr);
                }
            }
            
            section.setDisplayOrder(getElementTextAsInt(sectionElement, "displayOrder", 0));
            section.setIsCollapsible(getElementTextAsBoolean(sectionElement, "isCollapsible", false));
            section.setIsCollapsedByDefault(getElementTextAsBoolean(sectionElement, "isCollapsedByDefault", false));
            section.setShowInCreate(getElementTextAsBoolean(sectionElement, "showInCreate", true));
            section.setShowInEdit(getElementTextAsBoolean(sectionElement, "showInEdit", true));
            section.setShowInDetail(getElementTextAsBoolean(sectionElement, "showInDetail", true));
            section.setSectionIcon(getElementText(sectionElement, "sectionIcon"));
            section.setSectionColor(getElementText(sectionElement, "sectionColor"));
            section.setDescription(getElementText(sectionElement, "description"));
            
            return section;
            
        } catch (Exception e) {
            logger.error("Error creating section from XML element: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if section already exists for entity type
     */
    private boolean sectionExists(EntityType entityType, String sectionName) {
        return erpSectionRepository.existsByEntityTypeAndSectionNameAndIsActiveTrue(entityType, sectionName);
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
}
