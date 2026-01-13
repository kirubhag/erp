package krs.erp.initializer;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.Parent;
import krs.erp.repository.ParentRepository;

/**
 * ParentDataInitializer loads sample parent data from XML files.
 * 
 * Execution Order:
 * @Order(11) - After all other initializers to ensure dependent entities exist
 * 
 * Data is loaded from: data/parent/sample-parents.xml
 */
// @Component - Disabled for multi-tenant system
@Order(9)
public class ParentDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ParentDataInitializer.class);

    @Autowired
    private ParentRepository parentRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Parent data initialization...");
        try {
            loadParents();
        } catch (Exception e) {
            logger.error("Error during parent data initialization", e);
        }
    }

    private void loadParents() {
        logger.info("Loading Parents from data/parent/sample-parents.xml...");
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/parent/sample-parents.xml");
            if (inputStream == null) {
                logger.warn("Parents XML file not found. Skipping.");
                return;
            }

            Document document = parseXml(inputStream);
            NodeList parentList = document.getElementsByTagName("parent");
            int loadedCount = 0;

            for (int i = 0; i < parentList.getLength(); i++) {
                Element element = (Element) parentList.item(i);
                Parent parent = mapToParent(element);
                parentRepository.save(parent);
                loadedCount++;
            }

            logger.info("✓ Loaded {} parents", loadedCount);

        } catch (Exception e) {
            logger.error("Error loading parents", e);
        }
    }

    private Parent mapToParent(Element element) {
        Parent parent = new Parent();
        
        parent.setFirstName(element.getAttribute("first_name"));
        parent.setLastName(element.getAttribute("last_name"));
        parent.setMiddleName(element.getAttribute("middle_name"));
        parent.setEmail(element.getAttribute("email"));
        parent.setPhone(element.getAttribute("phone"));
        parent.setAlternatePhone(element.getAttribute("alternate_phone"));
        
        // Parse gender string to enum
        String genderStr = element.getAttribute("gender");
        if (genderStr != null && !genderStr.isEmpty()) {
            try {
                parent.setGender(Parent.Gender.valueOf(genderStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                parent.setGender(Parent.Gender.OTHER);
            }
        }
        
        parent.setOccupation(element.getAttribute("occupation"));
        parent.setWorkplace(element.getAttribute("workplace"));
        parent.setWorkPhone(element.getAttribute("work_phone"));

        // Boolean fields
        parent.setEmergencyContact("1".equals(element.getAttribute("emergency_contact")));
        parent.setAuthorizedPickup("1".equals(element.getAttribute("authorized_pickup")));
        parent.setReceiveNotifications("1".equals(element.getAttribute("receive_notifications")));

        parent.markAsActive();
        return parent;
    }

    private Document parseXml(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
    }
}
