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

import krs.erp.model.User;
import krs.erp.repository.UserRepository;

/**
 * Initializes user data from sample-users.xml on application startup
 * DEPRECATED: Use SampleDataInitializer instead for consolidated data loading
 */
// @Component  // Disabled - using SampleDataInitializer instead
// @Order(5)
public class UserDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserDataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting User data initialization...");

        try {
            if (userRepository.count() > 0) {
                logger.info("User data already exists. Skipping initialization.");
                return;
            }

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/sample-users.xml");
            if (inputStream == null) {
                logger.warn("sample-users.xml not found. Skipping User initialization.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList userList = document.getElementsByTagName("users");
            int totalUsers = userList.getLength();
            int loadedUsers = 0;

            logger.info("Found {} users in XML file", totalUsers);

            for (int i = 0; i < totalUsers; i++) {
                Element userElement = (Element) userList.item(i);

                try {
                    User user = new User();
                    user.setUsername(getTextContent(userElement, "username"));
                    user.setPasswordHash(getTextContent(userElement, "password_hash"));
                    user.setEmail(getTextContent(userElement, "email"));
                    user.setFirstName(getTextContent(userElement, "first_name"));
                    user.setLastName(getTextContent(userElement, "last_name"));
                    user.setPhone(getTextContent(userElement, "phone"));

                    String userType = getTextContent(userElement, "user_type");
                    if (userType != null) {
                        try {
                            user.setUserType(User.UserType.valueOf(userType));
                        } catch (IllegalArgumentException e) {
                            logger.warn("Invalid user_type value: {}", userType);
                        }
                    }

                    String enabled = getTextContent(userElement, "enabled");
                    user.setEnabled("1".equals(enabled));

                    String accountNonExpired = getTextContent(userElement, "account_non_expired");
                    user.setAccountNonExpired("1".equals(accountNonExpired));

                    String credentialsNonExpired = getTextContent(userElement, "credentials_non_expired");
                    user.setCredentialsNonExpired("1".equals(credentialsNonExpired));

                    String accountNonLocked = getTextContent(userElement, "account_non_locked");
                    user.setAccountNonLocked("1".equals(accountNonLocked));

                    user.markAsActive();
                    userRepository.save(user);
                    loadedUsers++;

                    logger.debug("Loaded user: {} ({})", user.getUsername(), user.getUserType());

                } catch (Exception e) {
                    logger.error("Error processing user at index {}: {}", i, e.getMessage());
                }
            }

            logger.info("User data initialization completed. Loaded: {}, Total: {}", 
                    loadedUsers, totalUsers);

        } catch (Exception e) {
            logger.error("Error initializing User data: {}", e.getMessage(), e);
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
