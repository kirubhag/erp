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
        logger.info("Starting data initialization check...");
        
        try {
            // Check if data already exists in the database
            long existingUserCount = userRepository.count();
            long existingStudentCount = studentRepository.count();
            
            if (existingUserCount > 0 && existingStudentCount > 0) {
                logger.info("✓ Sample data already exists in database:");
                logger.info("  - Users: {}", existingUserCount);
                logger.info("  - Students: {}", existingStudentCount);
                logger.info("✓ Skipping data import - using existing data");
                return;
            }
            
            logger.info("No existing data found - importing sample data...");
            
            // Import base system data (permissions, roles, users, staff)
            logger.info("Loading base system data...");
            dataImportService.importDataFromXml("data/sample-data.xml");
            
            // Import all grade-level student data
            logger.info("Loading K-12 student data...");
            String[] gradeFiles = {
                "data/kindergarten.xml",
                "data/grade_1.xml",
                "data/grade_2.xml", 
                "data/grade_3.xml",
                "data/grade_4.xml",
                "data/grade_5.xml",
                "data/grade_6.xml",
                "data/grade_7.xml",
                "data/grade_8.xml",
                "data/grade_9.xml",
                "data/grade_10.xml",
                "data/grade_11.xml",
                "data/grade_12.xml"
            };
            
            int loadedFiles = 0;
            int totalStudentsLoaded = 0;
            
            for (String gradeFile : gradeFiles) {
                try {
                    long studentsBeforeImport = studentRepository.count();
                    dataImportService.importStudentDataFromXml(gradeFile);
                    long studentsAfterImport = studentRepository.count();
                    int studentsInFile = (int)(studentsAfterImport - studentsBeforeImport);
                    
                    logger.info("✓ Loaded {} students from {}", studentsInFile, gradeFile);
                    loadedFiles++;
                    totalStudentsLoaded += studentsInFile;
                } catch (Exception e) {
                    logger.warn("⚠ Failed to load {}: {}", gradeFile, e.getMessage());
                }
            }
            
            // Final verification
            long finalUserCount = userRepository.count();
            long finalStudentCount = studentRepository.count();
            
            logger.info("✓ Data import completed successfully:");
            logger.info("  - Grade files loaded: {}/{}", loadedFiles, gradeFiles.length);
            logger.info("  - Total users: {}", finalUserCount);
            logger.info("  - Total students: {} (including {} from grade files)", finalStudentCount, totalStudentsLoaded);
            
            if (finalUserCount > 0 && finalStudentCount > 0) {
                logger.info("✓ ERP system ready with comprehensive sample data");
                logger.info("✓ Default admin credentials: username='admin', password='password123'");
                logger.info("✓ Sample data includes {} students across K-12 grades", totalStudentsLoaded);
            } else {
                logger.warn("⚠ Data import may have failed - please check logs");
            }
            
        } catch (Exception e) {
            logger.error("Error during data initialization", e);
            logger.warn("Application will start with empty database");
        }
        
        logger.info("Data initialization completed");
    }
}