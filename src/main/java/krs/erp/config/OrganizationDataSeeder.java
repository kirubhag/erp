package krs.erp.config;

import org.springframework.boot.CommandLineRunner;

/**
 * Data seeder to create sample organization data for development and testing
 */
// @Component - Disabled to prevent data initialization before schema is ready
public class OrganizationDataSeeder implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Temporarily disabled to test basic organization functionality
        // Organization data should be populated via registration form, not auto-initialization
        System.out.println("Organization data seeder temporarily disabled for testing");
    }
}