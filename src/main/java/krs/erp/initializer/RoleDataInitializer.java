package krs.erp.initializer;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.Role;
import krs.erp.repository.RoleRepository;

/**
 * Initializes Role data from sample-roles.xml on application startup
 * DEPRECATED: Use SampleDataInitializer instead for consolidated data loading
 */
// @Component  // Disabled - using SampleDataInitializer instead
// @Order(2)
public class RoleDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RoleDataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Role data initialization...");

        try {
            if (roleRepository.count() > 0) {
                logger.info("Role data already exists. Skipping initialization.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/sample-roles.xml");
            if (inputStream == null) {
                logger.warn("sample-roles.xml not found. Skipping Role initialization.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList roleList = document.getElementsByTagName("roles");
            int totalRoles = roleList.getLength();
            int loadedRoles = 0;

            logger.info("Found {} roles in XML file", totalRoles);

            for (int i = 0; i < totalRoles; i++) {
                Element roleElement = (Element) roleList.item(i);

                try {
                    Role role = new Role();
                    role.setName(getTextContent(roleElement, "name"));
                    role.setDescription(getTextContent(roleElement, "description"));

                    String systemRole = getTextContent(roleElement, "system_role");
                    role.setSystemRole("1".equals(systemRole));

                    role.markAsActive();
                    roleRepository.save(role);
                    loadedRoles++;

                    if (loadedRoles % 5 == 0) {
                        logger.debug("Loaded {} roles...", loadedRoles);
                    }

                } catch (Exception e) {
                    logger.error("Error processing role at index {}: {}", i, e.getMessage());
                }
            }

            logger.info("Role data initialization completed. Loaded: {}, Total: {}", 
                    loadedRoles, totalRoles);

        } catch (Exception e) {
            logger.error("Error initializing Role data: {}", e.getMessage(), e);
        }
    }

    private String getTextContent(Element element, String tagName) {
        try {
            return element.getAttribute(tagName);
        } catch (Exception e) {
            logger.debug("Could not extract attribute: {}", tagName);
        }
        return null;
    }
}
