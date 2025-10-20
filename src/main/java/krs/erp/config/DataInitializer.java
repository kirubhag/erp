package krs.erp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import krs.erp.repository.StudentRepository;
import krs.erp.repository.UserRepository;
import krs.erp.service.DataImportService;

@Component
@Profile("!test") // Don't run in test profile
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private DataImportService dataImportService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting XML data initialization...");
        
        try {
            // Import sample data from XML
            dataImportService.importDataFromXml("data/sample-data.xml");
            logger.info("✓ XML data imported successfully");
            
            // Verify the data was loaded
            long userCount = userRepository.count();
            long studentCount = studentRepository.count();
            
            logger.info("Sample data summary:");
            logger.info("- Users: {}", userCount);
            logger.info("- Students: {}", studentCount);
            
            if (userCount > 0 && studentCount > 0) {
                logger.info("✓ Sample data is available for testing");
                logger.info("✓ You can now use the application with pre-loaded data");
                logger.info("✓ Default admin credentials: username='admin', password='password123'");
            } else {
                logger.warn("⚠ No sample data was loaded - please check XML file");
            }
            
        } catch (Exception e) {
            logger.error("Error importing XML data", e);
            logger.warn("Application will start with empty database");
        }
        
        logger.info("Data initialization completed");
    }
}