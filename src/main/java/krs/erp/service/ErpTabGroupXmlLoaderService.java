package krs.erp.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.ErpEntity;
import krs.erp.model.ErpTabGroup;
import krs.erp.model.ErpTabGroupEntityMapping;
import krs.erp.repository.ErpEntityRepository; // Assuming this exists
import krs.erp.repository.ErpTabGroupEntityMappingRepository;
import krs.erp.repository.ErpTabGroupRepository;

@Service
public class ErpTabGroupXmlLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ErpTabGroupXmlLoaderService.class);
    private static final String XML_FILE_PATH = "data/erp-tab-groups.xml";

    @Autowired
    private ErpTabGroupRepository tabGroupRepository;

    @Autowired
    private ErpTabGroupEntityMappingRepository mappingRepository;

    @Autowired
    private ErpEntityRepository erpEntityRepository;

    @Transactional
    public void loadTabGroupsFromXml() {
        try {
            logger.info("Loading Tab Groups from XML: {}", XML_FILE_PATH);
            ClassPathResource resource = new ClassPathResource(XML_FILE_PATH);

            if (!resource.exists()) {
                logger.warn("Tab Groups XML file not found: {}", XML_FILE_PATH);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            try (InputStream inputStream = resource.getInputStream()) {
                Document document = builder.parse(inputStream);
                document.getDocumentElement().normalize();

                NodeList groupNodes = document.getElementsByTagName("tab_group");
                logger.info("Found {} tab groups in XML", groupNodes.getLength());

                for (int i = 0; i < groupNodes.getLength(); i++) {
                    Element groupElement = (Element) groupNodes.item(i);
                    processTabGroup(groupElement);
                }
            }

        } catch (Exception e) {
            logger.error("Error loading tab groups from XML: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load tab groups", e);
        }
    }

    private void processTabGroup(Element groupElement) {
        String code = getElementText(groupElement, "code");
        String name = getElementText(groupElement, "name");
        String icon = getElementText(groupElement, "icon");
        String description = getElementText(groupElement, "description");
        int sequence = Integer.parseInt(getElementText(groupElement, "sequence"));

        // UPSERT Tab Group
        Optional<ErpTabGroup> existingGroup = tabGroupRepository.findByCode(code);
        ErpTabGroup group;
        if (existingGroup.isPresent()) {
            group = existingGroup.get();
            // Update fields if needed, or skip if you want to preserve user changes
            group.setName(name); // Optional: assume XML is source of truth for name/icon
            group.setIcon(icon);
            group.setSequence(sequence);
            group.setDescription(description);
            logger.debug("Updating existing Tab Group: {}", code);
        } else {
            group = new ErpTabGroup();
            group.setCode(code);
            group.setName(name);
            group.setIcon(icon);
            group.setSequence(sequence);
            group.setDescription(description);
            group.setCreatedTime(LocalDateTime.now());
            group.setIsActive(1);
            logger.debug("Creating new Tab Group: {}", code);
        }

        group.setModifiedTime(LocalDateTime.now());
        group = tabGroupRepository.save(group);

        // Process Entity Mappings
        processEntityMappings(group, groupElement);
    }

    private void processEntityMappings(ErpTabGroup group, Element groupElement) {
        NodeList entitiesNode = groupElement.getElementsByTagName("entities");
        if (entitiesNode.getLength() > 0) {
            Element entitiesElement = (Element) entitiesNode.item(0);
            NodeList entityNodes = entitiesElement.getElementsByTagName("entity");

            // We want to sync mappings. Strategy:
            // 1. Get all entities for this group from XML.
            // 2. Map them to DB.
            // 3. (Optional) Remove mappings not in XML? Or just add missing ones.
            // For now, let's just add/update sequences.

            for (int i = 0; i < entityNodes.getLength(); i++) {
                String entityName = entityNodes.item(i).getTextContent();
                int sequence = i + 1;

                // Find ErpEntity by singular name (assuming that's what's in XML)
                Optional<ErpEntity> erpEntityOpt = erpEntityRepository.findBySingularName(entityName);
                if (erpEntityOpt.isPresent()) {
                    ErpEntity entity = erpEntityOpt.get();

                    // Check if mapping exists
                    Optional<ErpTabGroupEntityMapping> existingMapping = mappingRepository
                            .findByTabGroupIdAndEntityId(group.getId(), entity.getId());
                    ErpTabGroupEntityMapping mapping;

                    if (existingMapping.isPresent()) {
                        mapping = existingMapping.get();
                        if (mapping.getSequence() != sequence) {
                            mapping.setSequence(sequence);
                            mappingRepository.save(mapping);
                        }
                    } else {
                        mapping = new ErpTabGroupEntityMapping();
                        mapping.setTabGroupId(group.getId());
                        mapping.setEntityId(entity.getId());
                        mapping.setSequence(sequence);
                        mapping.setIsActive(1);
                        mapping.setCreatedTime(LocalDateTime.now());
                        mappingRepository.save(mapping);
                    }
                } else {
                    logger.warn("Entity '{}' defined in Tab Group '{}' not found in database. Skipping mapping.",
                            entityName, group.getCode());
                }
            }
        }
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }
}
