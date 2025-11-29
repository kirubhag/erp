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

import krs.erp.model.ErpEntityRelation;
import krs.erp.repository.ErpEntityRelationRepository;

/**
 * Service for loading ERP entity relationship definitions from XML configuration
 * Provides centralized relationship management through XML-based configuration
 */
@Service
public class ErpEntityRelationXmlLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ErpEntityRelationXmlLoaderService.class);

    @Autowired
    private ErpEntityRelationRepository relationRepository;

    private static final String XML_FILE_PATH = "data/erp-entity-relations.xml";

    /**
     * Load all entity relationships from XML configuration file
     * Checks for existing relationships and only creates new ones
     */
    public void loadRelationsFromXml() {
        try {
            logger.info("Loading ERP entity relationships from XML: {}", XML_FILE_PATH);
            
            ClassPathResource resource = new ClassPathResource(XML_FILE_PATH);
            
            if (!resource.exists()) {
                logger.warn("Entity relations XML file not found: {}", XML_FILE_PATH);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(resource.getInputStream());
            document.getDocumentElement().normalize();

            NodeList relationNodes = document.getElementsByTagName("relation");
            List<ErpEntityRelation> relationsToSave = new ArrayList<>();
            int existingRelationsCount = 0;

            for (int i = 0; i < relationNodes.getLength(); i++) {
                Element relationElement = (Element) relationNodes.item(i);
                
                String parentTableName = getElementText(relationElement, "p_table_name");
                String childTableName = getElementText(relationElement, "c_table_name");
                String foreignKeyColumn = getElementText(relationElement, "fk_column");
                
                // Check if relationship already exists
                if (relationExists(parentTableName, childTableName, foreignKeyColumn)) {
                    existingRelationsCount++;
                    logger.debug("Relationship already exists: {} -> {} (FK: {})", 
                               parentTableName, childTableName, foreignKeyColumn);
                    continue;
                }

                ErpEntityRelation relation = createRelationFromXml(relationElement);
                if (relation != null) {
                    relationsToSave.add(relation);
                }
            }

            // Save new relationships in batch
            if (!relationsToSave.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                relationsToSave.forEach(relation -> {
                    relation.setCreatedTime(now);
                    relation.setModifiedTime(now);
                    relation.setIsActive(1);
                });
                
                relationRepository.saveAll(relationsToSave);
                logger.info("✓ Saved {} new entity relationships, {} already existed", 
                           relationsToSave.size(), existingRelationsCount);
            } else {
                logger.info("All {} entity relationships already exist", existingRelationsCount);
            }

        } catch (Exception e) {
            logger.error("Error loading entity relationships from XML: {}", e.getMessage(), e);
        }
    }

    /**
     * Create ErpEntityRelation entity from XML relation element
     */
    private ErpEntityRelation createRelationFromXml(Element relationElement) {
        try {
            ErpEntityRelation relation = new ErpEntityRelation();
            
            // Parent table metadata
            relation.setParentTableName(getElementText(relationElement, "p_table_name"));
            relation.setParentPkid(getElementText(relationElement, "p_pkid"));
            relation.setParentDisplayColumn(getElementText(relationElement, "p_display_column"));
            
            // Child table metadata
            relation.setChildTableName(getElementText(relationElement, "c_table_name"));
            relation.setChildPkid(getElementText(relationElement, "c_pkid"));
            relation.setChildDisplayColumn(getElementText(relationElement, "c_display_column"));
            
            // Foreign key and description
            relation.setForeignKeyColumn(getElementText(relationElement, "fk_column"));
            relation.setDescription(getElementText(relationElement, "description"));
            
            return relation;
            
        } catch (Exception e) {
            logger.error("Error creating relation from XML element: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if relationship already exists
     */
    private boolean relationExists(String parentTableName, String childTableName, String foreignKeyColumn) {
        return relationRepository.relationshipExists(parentTableName, childTableName);
    }

    /**
     * Utility method to get text content from XML element
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            String text = nodeList.item(0).getTextContent().trim();
            return text.isEmpty() ? null : text;
        }
        return null;
    }

    /**
     * Get total count of relationships loaded
     */
    public long getTotalRelationCount() {
        return relationRepository.count();
    }
}
