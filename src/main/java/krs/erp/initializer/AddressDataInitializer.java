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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import krs.erp.model.Address;
import krs.erp.repository.AddressRepository;

/**
 * Initializes Address data from sample-addresses.xml on application startup
 */
// @Component  // DISABLED: Schema issues with address_id column
@Order(10)
public class AddressDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AddressDataInitializer.class);

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Address data initialization...");

        try {
            // Load the XML file from classpath
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/address/sample-addresses.xml");
            if (inputStream == null) {
                logger.warn("sample-addresses.xml not found in classpath. Skipping Address initialization.");
                return;
            }

            // Parse the XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Get all address nodes
            NodeList addressList = document.getElementsByTagName("addresses");
            int totalAddresses = addressList.getLength();
            int loadedAddresses = 0;
            int skippedAddresses = 0;

            logger.info("Found {} addresses in XML file", totalAddresses);

            // Process each address
            for (int i = 0; i < totalAddresses; i++) {
                Node addressNode = addressList.item(i);

                if (addressNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element addressElement = (Element) addressNode;

                    try {
                        // Extract address data
                        String entityType = addressElement.getAttribute("entity_type");
                        String entityIdStr = addressElement.getAttribute("entity_id");

                        // Check if this address already exists
                        if (!entityIdStr.isEmpty() && !entityType.isEmpty()) {
                            Long entityId = Long.parseLong(entityIdStr);
                            Address.EntityType enumEntityType = Address.EntityType.valueOf(entityType);
                            
                            // Check if address exists
                            boolean exists = addressRepository.findByEntityTypeAndEntityId(enumEntityType, entityId)
                                    .stream()
                                    .count() > 0;
                            
                            if (exists) {
                                logger.debug("Address already exists for entity type {} and ID {}. Skipping.",
                                        entityType, entityId);
                                skippedAddresses++;
                                continue;
                            }
                        }

                        // Create new Address entity
                        Address address = new Address();
                        
                        // Set entity type and ID
                        if (!entityType.isEmpty()) {
                            try {
                                address.setEntityType(Address.EntityType.valueOf(entityType));
                            } catch (IllegalArgumentException e) {
                                logger.warn("Invalid entity_type value: {}", entityType);
                                continue;
                            }
                        }
                        
                        if (!entityIdStr.isEmpty()) {
                            address.setEntityId(Long.parseLong(entityIdStr));
                        }

                        // Set address fields
                        String addressLine1 = addressElement.getAttribute("address_line1");
                        if (!addressLine1.isEmpty()) {
                            address.setAddressLine1(addressLine1);
                        }

                        String addressLine2 = addressElement.getAttribute("address_line2");
                        if (!addressLine2.isEmpty()) {
                            address.setAddressLine2(addressLine2);
                        }

                        String city = addressElement.getAttribute("city");
                        if (!city.isEmpty()) {
                            address.setCity(city);
                        }

                        String state = addressElement.getAttribute("state");
                        if (!state.isEmpty()) {
                            address.setState(state);
                        }

                        String postalCode = addressElement.getAttribute("postal_code");
                        if (!postalCode.isEmpty()) {
                            address.setPostalCode(postalCode);
                        }

                        String country = addressElement.getAttribute("country");
                        if (!country.isEmpty()) {
                            address.setCountry(country);
                        }

                        // Set address type
                        String addressType = addressElement.getAttribute("address_type");
                        if (!addressType.isEmpty()) {
                            try {
                                address.setAddressType(Address.AddressType.valueOf(addressType));
                            } catch (IllegalArgumentException e) {
                                logger.warn("Invalid address_type value: {}", addressType);
                                address.setAddressType(Address.AddressType.RESIDENTIAL);
                            }
                        } else {
                            address.setAddressType(Address.AddressType.RESIDENTIAL);
                        }

                        // Set primary flag
                        String isPrimary = addressElement.getAttribute("is_primary");
                        if (!isPrimary.isEmpty()) {
                            address.setIsPrimary("1".equals(isPrimary));
                        } else {
                            address.setIsPrimary(true);
                        }

                        // Mark as active
                        address.markAsActive();

                        // Save to database
                        addressRepository.save(address);
                        loadedAddresses++;

                        if (loadedAddresses % 50 == 0) {
                            logger.info("Loaded {} addresses...", loadedAddresses);
                        }

                    } catch (Exception e) {
                        logger.error("Error processing address at index {}: {}", i, e.getMessage(), e);
                    }
                }
            }

            logger.info("Address data initialization completed. Loaded: {}, Skipped: {}, Total: {}",
                    loadedAddresses, skippedAddresses, totalAddresses);

        } catch (Exception e) {
            logger.error("Error initializing Address data: {}", e.getMessage(), e);
        }
    }
}
