package krs.erp.initializer;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.Permission;
import krs.erp.repository.PermissionRepository;

/**
 * Initializes Permission data from sample-permissions.xml on application startup
 * DEPRECATED: Use SampleDataInitializer instead for consolidated data loading
 */
// @Component - Disabled for multi-tenant system
@Order(1)
public class PermissionDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PermissionDataInitializer.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Permission data initialization...");

        try {
            if (permissionRepository.count() > 0) {
                logger.info("Permission data already exists. Skipping initialization.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/sample-permissions.xml");
            if (inputStream == null) {
                logger.warn("sample-permissions.xml not found. Skipping Permission initialization.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList permissionList = document.getElementsByTagName("permissions");
            int totalPermissions = permissionList.getLength();
            int loadedPermissions = 0;

            logger.info("Found {} permissions in XML file", totalPermissions);

            for (int i = 0; i < totalPermissions; i++) {
                Element permissionElement = (Element) permissionList.item(i);

                try {
                    Permission permission = new Permission();
                    permission.setName(getTextContent(permissionElement, "name"));
                    permission.setDescription(getTextContent(permissionElement, "description"));
                    permission.setResource(getTextContent(permissionElement, "resource"));
                    permission.setAction(getTextContent(permissionElement, "action"));

                    String systemPermission = getTextContent(permissionElement, "system_permission");
                    permission.setSystemPermission("1".equals(systemPermission));

                    permission.markAsActive();
                    permissionRepository.save(permission);
                    loadedPermissions++;

                    if (loadedPermissions % 5 == 0) {
                        logger.debug("Loaded {} permissions...", loadedPermissions);
                    }

                } catch (Exception e) {
                    logger.error("Error processing permission at index {}: {}", i, e.getMessage());
                }
            }

            logger.info("Permission data initialization completed. Loaded: {}, Total: {}", 
                    loadedPermissions, totalPermissions);

        } catch (Exception e) {
            logger.error("Error initializing Permission data: {}", e.getMessage(), e);
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
