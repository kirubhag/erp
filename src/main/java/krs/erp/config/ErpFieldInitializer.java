package krs.erp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import krs.erp.service.ErpEntityRelationXmlLoaderService;
import krs.erp.service.ErpFieldXmlLoaderService;
import krs.erp.service.ErpSectionXmlLoaderService;

/**
 * Configuration class to initialize ERP sections, fields, and entity relationships on application startup
 * Loads section, field, and relationship definitions from XML configuration files
 * Sections must be loaded before fields since fields reference sections
 */
//@Component
public class ErpFieldInitializer implements CommandLineRunner {

    @Autowired
    private ErpSectionXmlLoaderService sectionXmlLoaderService;

    @Autowired
    private ErpFieldXmlLoaderService fieldXmlLoaderService;

    @Autowired
    private ErpEntityRelationXmlLoaderService relationXmlLoaderService;

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
        
        // Finally load entity relationships
        System.out.println("Loading ERP entity relationships from XML...");
        relationXmlLoaderService.loadRelationsFromXml();
        
        long totalRelations = relationXmlLoaderService.getTotalRelationCount();
        System.out.println("✓ Entity relationships initialized. Total relationships: " + totalRelations);
    }
}