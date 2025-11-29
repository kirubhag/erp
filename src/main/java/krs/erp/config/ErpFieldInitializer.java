package krs.erp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import krs.erp.service.ErpFieldXmlLoaderService;
import krs.erp.service.ErpSectionXmlLoaderService;

/**
 * Configuration class to initialize ERP sections and field metadata on application startup
 * Loads section and field definitions from XML configuration files
 * Sections must be loaded before fields since fields reference sections
 */
@Component
public class ErpFieldInitializer implements CommandLineRunner {

    @Autowired
    private ErpSectionXmlLoaderService sectionXmlLoaderService;

    @Autowired
    private ErpFieldXmlLoaderService fieldXmlLoaderService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize sections first (fields depend on sections)
        System.out.println("Loading ERP sections from XML...");
        sectionXmlLoaderService.loadSectionsFromXml();
        
        // Then initialize fields from XML configuration
        System.out.println("Loading ERP fields from XML...");
        fieldXmlLoaderService.loadFieldsFromXml();
        
        long totalFields = fieldXmlLoaderService.getTotalFieldCount();
        System.out.println("✓ ERP metadata initialization completed. Total fields: " + totalFields);
    }
}