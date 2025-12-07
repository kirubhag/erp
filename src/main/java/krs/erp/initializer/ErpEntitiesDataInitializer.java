package krs.erp.initializer;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.ErpEntity;
import krs.erp.repository.ErpEntityRepository;

// @Component - Disabled for multi-tenant system
@Order(1)
public class ErpEntitiesDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ErpEntitiesDataInitializer.class);

    @Autowired
    private ErpEntityRepository erpEntityRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting ERP Entities data initialization...");
        
        // Check if data already exists
        long count = erpEntityRepository.count();
        if (count > 0) {
            logger.info("ERP Entities data already exists. Skipping initialization.");
            return;
        }

        try {
            List<ErpEntity> entities = loadEntitiesFromXml();
            
            int loaded = 0;
            int skipped = 0;
            
            for (ErpEntity entity : entities) {
                try {
                    // Check if entity with same system_name already exists
                    if (erpEntityRepository.findBySystemName(entity.getSystemName()).isPresent()) {
                        skipped++;
                        logger.debug("Entity with system_name '{}' already exists. Skipping.", entity.getSystemName());
                        continue;
                    }
                    
                    // Set audit fields
                    entity.setCreatedDate(LocalDateTime.now());
                    entity.setCreatedBy("system");
                    
                    erpEntityRepository.save(entity);
                    loaded++;
                    logger.debug("Loaded entity: {} - {}", entity.getSystemName(), entity.getPluralName());
                    
                } catch (Exception e) {
                    logger.error("Error loading entity: {}", entity.getSystemName(), e);
                    skipped++;
                }
            }
            
            logger.info("ERP Entities data initialization completed. Loaded: {}, Skipped: {}, Total: {}", 
                       loaded, skipped, entities.size());
            
        } catch (Exception e) {
            logger.error("Error during ERP Entities data initialization", e);
        }
    }

    private List<ErpEntity> loadEntitiesFromXml() throws Exception {
        List<ErpEntity> entities = new ArrayList<>();
        
        ClassPathResource resource = new ClassPathResource("data/erp-entities.xml");
        
        if (!resource.exists()) {
            logger.warn("ERP entities XML file not found at: data/erp-entities.xml");
            return entities;
        }
        
        try (InputStream inputStream = resource.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            doc.getDocumentElement().normalize();
            
            NodeList nodeList = doc.getElementsByTagName("entity");
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                
                ErpEntity entity = new ErpEntity();
                entity.setSingularName(getElementValue(element, "singular_name"));
                entity.setPluralName(getElementValue(element, "plural_name"));
                entity.setSystemName(getElementValue(element, "system_name"));
                entity.setDescription(getElementValue(element, "description"));
                entity.setIsActive(Boolean.parseBoolean(getElementValue(element, "is_active", "true")));
                entity.setSequence(Integer.parseInt(getElementValue(element, "sequence", "0")));
                entity.setPresence(Boolean.parseBoolean(getElementValue(element, "presence", "true")));
                entity.setIcon(getElementValue(element, "icon"));
                entity.setRoute(getElementValue(element, "route"));
                
                entities.add(entity);
            }
        }
        
        return entities;
    }

    private String getElementValue(Element parent, String tagName) {
        return getElementValue(parent, tagName, null);
    }

    private String getElementValue(Element parent, String tagName, String defaultValue) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            String value = nodeList.item(0).getTextContent();
            return value != null && !value.trim().isEmpty() ? value.trim() : defaultValue;
        }
        return defaultValue;
    }
}
