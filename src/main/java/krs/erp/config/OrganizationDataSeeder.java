package krs.erp.config;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import krs.erp.model.Organization;
import krs.erp.repository.OrganizationRepository;

/**
 * Data seeder to create sample organization data for development and testing
 */
@Component
public class OrganizationDataSeeder implements CommandLineRunner {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public void run(String... args) throws Exception {
        // Temporarily disabled to test basic organization functionality
        // Only seed data if no organizations exist
        // if (organizationRepository.count() == 0) {
        //     seedSampleOrganizations();
        // }
        System.out.println("Organization data seeder temporarily disabled for testing");
    }

    private void seedSampleOrganizations() {
        List<Organization> organizations = Arrays.asList(
            createOrganization(
                "Springfield University",
                "University",
                "SPRU",
                "A prestigious public university offering undergraduate and graduate programs across multiple disciplines.",
                "info@springfield.edu",
                "+1 (555) 123-4567",
                "+1 (555) 123-4568",
                "https://www.springfield.edu",
                "123 University Avenue",
                "Springfield",
                "IL",
                "62701",
                "United States",
                "REG-SPRU-001",
                "36-1234567",
                1875,
                "AACSB, ABET",
                "YYYY-YYYY",
                "en",
                "USD",
                "US/Central",
                "https://www.springfield.edu/images/logo.png"
            ),
            
            createOrganization(
                "Riverside High School",
                "School",
                "RHS",
                "A comprehensive public high school serving grades 9-12 in the Riverside community.",
                "principal@riversidehs.edu",
                "+1 (555) 234-5678",
                "+1 (555) 234-5679",
                "https://www.riversidehs.edu",
                "456 Oak Street",
                "Riverside",
                "CA",
                "92501",
                "United States",
                "REG-RHS-002",
                "95-7654321",
                1952,
                "WASC",
                "YYYY-YYYY",
                "en",
                "USD",
                "US/Pacific",
                "https://www.riversidehs.edu/assets/logo.png"
            ),
            
            createOrganization(
                "Central Community College",
                "College",
                "CCC",
                "A two-year community college providing associate degrees, certificates, and continuing education programs.",
                "admissions@centralcc.edu",
                "+1 (555) 345-6789",
                "+1 (555) 345-6790",
                "https://www.centralcc.edu",
                "789 College Drive",
                "Central City",
                "TX",
                "75001",
                "United States",
                "REG-CCC-003",
                "74-9876543",
                1968,
                "SACSCOC",
                "Fall-Spring-Summer",
                "en",
                "USD",
                "US/Central",
                "https://www.centralcc.edu/brand/logo.png"
            ),
            
            createOrganization(
                "Harmony Elementary School",
                "School",
                "HES",
                "An elementary school serving kindergarten through 5th grade with a focus on arts integration.",
                "office@harmonyes.org",
                "+1 (555) 456-7890",
                null,
                "https://www.harmonyes.org",
                "321 Maple Lane",
                "Harmony",
                "NY",
                "14127",
                "United States",
                "REG-HES-004",
                "16-1357924",
                1987,
                "AdvancED",
                "2024-2025",
                "en",
                "USD",
                "US/Eastern",
                null
            ),
            
            createOrganization(
                "Tech Innovation Institute",
                "Institute",
                "TII",
                "A specialized institute focusing on technology education and professional development programs.",
                "contact@techinnovation.org",
                "+1 (555) 567-8901",
                "+1 (555) 567-8902",
                "https://www.techinnovation.org",
                "1010 Innovation Boulevard",
                "Silicon Valley",
                "CA",
                "94025",
                "United States",
                "REG-TII-005",
                "77-2468135",
                2001,
                "ACCET",
                "Quarter System",
                "en",
                "USD",
                "US/Pacific",
                "https://www.techinnovation.org/static/logo.svg"
            ),
            
            createOrganization(
                "Global Language Academy",
                "Academy",
                "GLA",
                "An international language academy offering intensive language courses and cultural immersion programs.",
                "info@globallang.edu",
                "+1 (555) 678-9012",
                null,
                "https://www.globallang.edu",
                "555 International Way",
                "Miami",
                "FL",
                "33101",
                "United States",
                "REG-GLA-006",
                "59-3691470",
                1995,
                "CEA",
                "Monthly Sessions",
                "en",
                "USD",
                "US/Eastern",
                "https://www.globallang.edu/images/brand/logo.png"
            )
        );

        organizationRepository.saveAll(organizations);
        System.out.println("Successfully seeded " + organizations.size() + " sample organizations");
    }

    private Organization createOrganization(String name, String type, String code, String description,
                                         String email, String phone, String fax, String website,
                                         String streetAddress, String city, String state, String postalCode,
                                         String country, String registrationNumber, String taxId,
                                         Integer establishedYear, String accreditation, String academicYearFormat,
                                         String defaultLanguage, String defaultCurrency, String timezone,
                                         String logoUrl) {
        Organization org = new Organization();
        org.setName(name);
        org.setType(type);
        org.setCode(code);
        org.setDescription(description);
        org.setEmail(email);
        org.setPhone(phone);
        org.setFax(fax);
        org.setWebsite(website);
        org.setStreetAddress(streetAddress);
        org.setCity(city);
        org.setState(state);
        org.setPostalCode(postalCode);
        org.setCountry(country);
        org.setRegistrationNumber(registrationNumber);
        org.setTaxId(taxId);
        org.setEstablishedYear(establishedYear);
        org.setAccreditation(accreditation);
        org.setAcademicYearFormat(academicYearFormat);
        org.setDefaultLanguage(defaultLanguage);
        org.setDefaultCurrency(defaultCurrency);
        org.setTimezone(timezone);
        org.setLogoUrl(logoUrl);
        
        // Set audit fields
        org.setCreatedTime(LocalDateTime.now());
        org.setModifiedTime(LocalDateTime.now());
        org.setIsActive(1);
        
        return org;
    }
}