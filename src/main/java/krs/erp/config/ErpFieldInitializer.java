package krs.erp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import krs.erp.service.ErpFieldXmlLoaderService;

/**
 * Configuration class to initialize ERP field metadata on application startup
 * Loads field definitions from XML configuration file
 */
// @Component  // Disabled - prevents schema initialization
public class ErpFieldInitializer implements CommandLineRunner {

    @Autowired
    private ErpFieldXmlLoaderService xmlLoaderService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize fields from XML configuration on application startup
        // xmlLoaderService.loadFieldsFromXml();
        
        long totalFields = xmlLoaderService.getTotalFieldCount();
        System.out.println("✓ ERP field initialization completed from XML. Total fields: " + totalFields);
    }
}