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

import krs.erp.model.Organization;
import krs.erp.repository.OrganizationRepository;

/**
 * Initializes Organization data from sample-organisations.xml on application startup
 * DEPRECATED: Use SampleDataInitializer instead for consolidated data loading
 */
// @Component  // Disabled - using SampleDataInitializer instead
// @Order(3)
public class OrganizationDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationDataInitializer.class);

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Organization data initialization...");

        try {
            if (organizationRepository.count() > 0) {
                logger.info("Organization data already exists. Skipping initialization.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/sample-organisations.xml");
            if (inputStream == null) {
                logger.warn("sample-organisations.xml not found. Skipping Organization initialization.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList organizationList = document.getElementsByTagName("organizations");
            int totalOrganizations = organizationList.getLength();
            int loadedOrganizations = 0;

            logger.info("Found {} organizations in XML file", totalOrganizations);

            for (int i = 0; i < totalOrganizations; i++) {
                Element orgElement = (Element) organizationList.item(i);

                try {
                    Organization organization = new Organization();
                    organization.setName(getTextContent(orgElement, "name"));
                    organization.setType(getTextContent(orgElement, "type"));
                    organization.setCode(getTextContent(orgElement, "code"));
                    organization.setDescription(getTextContent(orgElement, "description"));
                    organization.setEmail(getTextContent(orgElement, "email"));
                    organization.setPhone(getTextContent(orgElement, "phone"));
                    organization.setFax(getTextContent(orgElement, "fax"));
                    organization.setWebsite(getTextContent(orgElement, "website"));
                    organization.setStreetAddress(getTextContent(orgElement, "street_address"));
                    organization.setCity(getTextContent(orgElement, "city"));
                    organization.setState(getTextContent(orgElement, "state"));
                    organization.setPostalCode(getTextContent(orgElement, "postal_code"));
                    organization.setCountry(getTextContent(orgElement, "country"));
                    organization.setRegistrationNumber(getTextContent(orgElement, "registration_number"));
                    organization.setTaxId(getTextContent(orgElement, "tax_id"));

                    String establishedYear = getTextContent(orgElement, "established_year");
                    if (establishedYear != null && !establishedYear.isEmpty()) {
                        try {
                            organization.setEstablishedYear(Integer.parseInt(establishedYear));
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid established_year format: {}", establishedYear);
                        }
                    }

                    organization.markAsActive();
                    organizationRepository.save(organization);
                    loadedOrganizations++;

                    logger.debug("Loaded organization: {}", organization.getName());

                } catch (Exception e) {
                    logger.error("Error processing organization at index {}: {}", i, e.getMessage());
                }
            }

            logger.info("Organization data initialization completed. Loaded: {}, Total: {}", 
                    loadedOrganizations, totalOrganizations);

        } catch (Exception e) {
            logger.error("Error initializing Organization data: {}", e.getMessage(), e);
        }
    }

    private String getTextContent(Element element, String attributeName) {
        try {
            String value = element.getAttribute(attributeName);
            return value != null && !value.trim().isEmpty() ? value.trim() : null;
        } catch (Exception e) {
            logger.debug("Could not extract attribute: {}", attributeName);
        }
        return null;
    }
}
