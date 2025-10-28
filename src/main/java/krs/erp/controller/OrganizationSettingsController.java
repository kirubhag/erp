package krs.erp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.Organization;
import krs.erp.service.OrganizationService;

/**
 * REST Controller for Organization Settings
 * Handles organization details, business hours, holidays, and currencies
 */
@RestController
@RequestMapping("/api/organization")
@CrossOrigin(origins = "*")
public class OrganizationSettingsController {
    
    @Autowired
    private OrganizationService organizationService;
    
    /**
     * Get organization details for settings page
     * Returns the first organization or default data
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getOrganizationDetails() {
        try {
            // Get first organization or create default
            List<Organization> orgs = organizationService.getAllOrganizations(null).getContent();
            
            Map<String, Object> details = new HashMap<>();
            
            if (orgs != null && !orgs.isEmpty()) {
                Organization org = orgs.get(0);
                details.put("name", org.getName());
                
                // Contact Info
                Map<String, String> contactInfo = new HashMap<>();
                contactInfo.put("email", org.getEmail() != null ? org.getEmail() : "");
                contactInfo.put("phone", org.getPhone() != null ? org.getPhone() : "");
                contactInfo.put("fax", org.getFax() != null ? org.getFax() : "");
                contactInfo.put("website", org.getWebsite() != null ? org.getWebsite() : "");
                details.put("contactInfo", contactInfo);
                
                // Address
                Map<String, String> address = new HashMap<>();
                address.put("street", org.getStreetAddress() != null ? org.getStreetAddress() : "");
                address.put("city", org.getCity() != null ? org.getCity() : "");
                address.put("state", org.getState() != null ? org.getState() : "");
                address.put("country", org.getCountry() != null ? org.getCountry() : "");
                address.put("zipCode", org.getPostalCode() != null ? org.getPostalCode() : "");
                details.put("address", address);
                
                // Branding
                Map<String, String> branding = new HashMap<>();
                branding.put("logoUrl", "");
                details.put("branding", branding);
                
                // Locale Info
                Map<String, String> localeInfo = new HashMap<>();
                localeInfo.put("language", "en");
                localeInfo.put("currencyLocale", "United States");
                localeInfo.put("timeZone", "(GMT 5:30) India Standard Time(Asia/Kolkata)");
                localeInfo.put("dateFormat", "MM/DD/YYYY");
                details.put("localeInfo", localeInfo);
                
                // Access URL
                details.put("accessUrl", "http://localhost:8081");
                
                // Fiscal Year
                Map<String, String> fiscalYear = new HashMap<>();
                fiscalYear.put("startDate", null);
                fiscalYear.put("endDate", null);
                details.put("fiscalYear", fiscalYear);
            } else {
                // Return default data
                details = getDefaultOrganizationDetails();
            }
            
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update organization details
     */
    @PutMapping("/details")
    public ResponseEntity<Map<String, Object>> updateOrganizationDetails(@RequestBody Map<String, Object> details) {
        try {
            // Get first organization or create new
            List<Organization> orgs = organizationService.getAllOrganizations(null).getContent();
            Organization org = (orgs != null && !orgs.isEmpty()) ? orgs.get(0) : new Organization();
            
            // Set default type for new organization
            if (org.getId() == null && org.getType() == null) {
                org.setType("Enterprise");
            }
            
            // Update basic info
            if (details.containsKey("name")) {
                org.setName((String) details.get("name"));
            }
            
            // Update contact info
            if (details.containsKey("contactInfo")) {
                @SuppressWarnings("unchecked")
                Map<String, String> contactInfo = (Map<String, String>) details.get("contactInfo");
                org.setEmail(contactInfo.get("email"));
                org.setPhone(contactInfo.get("phone"));
                org.setFax(contactInfo.get("fax"));
                org.setWebsite(contactInfo.get("website"));
            }
            
            // Update address
            if (details.containsKey("address")) {
                @SuppressWarnings("unchecked")
                Map<String, String> address = (Map<String, String>) details.get("address");
                org.setStreetAddress(address.get("street"));
                org.setCity(address.get("city"));
                org.setState(address.get("state"));
                org.setCountry(address.get("country"));
                org.setPostalCode(address.get("zipCode"));
            }
            
            // Save organization
            if (org.getId() == null) {
                organizationService.createOrganization(org);
            } else {
                organizationService.updateOrganization(org.getId(), org);
            }
            
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get business hours
     */
    @GetMapping("/business-hours")
    public ResponseEntity<List<Map<String, Object>>> getBusinessHours() {
        List<Map<String, Object>> businessHours = new ArrayList<>();
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] codes = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
        
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> daySchedule = new HashMap<>();
            daySchedule.put("name", days[i]);
            daySchedule.put("code", codes[i]);
            daySchedule.put("isWorkingDay", i < 5); // Mon-Fri are working days
            daySchedule.put("startTime", "09:00");
            daySchedule.put("endTime", "17:00");
            businessHours.add(daySchedule);
        }
        
        return ResponseEntity.ok(businessHours);
    }
    
    /**
     * Update business hours
     */
    @PostMapping("/business-hours")
    public ResponseEntity<List<Map<String, Object>>> updateBusinessHours(@RequestBody List<Map<String, Object>> businessHours) {
        // In a real application, save to database
        return ResponseEntity.ok(businessHours);
    }
    
    /**
     * Get holidays
     */
    @GetMapping("/holidays")
    public ResponseEntity<List<Map<String, Object>>> getHolidays() {
        List<Map<String, Object>> holidays = new ArrayList<>();
        
        // Return empty list for now - can be populated from database
        // Example:
        // Map<String, Object> holiday = new HashMap<>();
        // holiday.put("date", "2025-01-01");
        // holiday.put("name", "New Year's Day");
        // holiday.put("description", "New Year Holiday");
        // holidays.add(holiday);
        
        return ResponseEntity.ok(holidays);
    }
    
    /**
     * Update holidays
     */
    @PostMapping("/holidays")
    public ResponseEntity<List<Map<String, Object>>> updateHolidays(@RequestBody List<Map<String, Object>> holidays) {
        // In a real application, save to database
        return ResponseEntity.ok(holidays);
    }
    
    /**
     * Get currencies
     */
    @GetMapping("/currencies")
    public ResponseEntity<List<Map<String, Object>>> getCurrencies() {
        List<Map<String, Object>> currencies = new ArrayList<>();
        
        // Default currency
        Map<String, Object> usd = new HashMap<>();
        usd.put("code", "USD");
        usd.put("name", "US Dollar");
        usd.put("symbol", "$");
        usd.put("exchangeRate", 1.0);
        usd.put("isDefault", true);
        currencies.add(usd);
        
        return ResponseEntity.ok(currencies);
    }
    
    /**
     * Update currencies
     */
    @PostMapping("/currencies")
    public ResponseEntity<List<Map<String, Object>>> updateCurrencies(@RequestBody List<Map<String, Object>> currencies) {
        // In a real application, save to database
        return ResponseEntity.ok(currencies);
    }
    
    /**
     * Get default organization details
     */
    private Map<String, Object> getDefaultOrganizationDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("name", "Zylker");
        
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("email", "contact@zylker.com");
        contactInfo.put("phone", "09876543210");
        contactInfo.put("fax", "09876543210");
        contactInfo.put("website", "https://www.zylker.com");
        details.put("contactInfo", contactInfo);
        
        Map<String, String> address = new HashMap<>();
        address.put("street", "");
        address.put("city", "");
        address.put("state", "");
        address.put("country", "");
        address.put("zipCode", "");
        details.put("address", address);
        
        Map<String, String> branding = new HashMap<>();
        branding.put("logoUrl", "");
        details.put("branding", branding);
        
        Map<String, String> localeInfo = new HashMap<>();
        localeInfo.put("language", "en");
        localeInfo.put("currencyLocale", "United States");
        localeInfo.put("timeZone", "(GMT 5:30) India Standard Time(Asia/Kolkata)");
        localeInfo.put("dateFormat", "MM/DD/YYYY");
        details.put("localeInfo", localeInfo);
        
        details.put("accessUrl", "http://localhost:8081");
        
        Map<String, String> fiscalYear = new HashMap<>();
        fiscalYear.put("startDate", null);
        fiscalYear.put("endDate", null);
        details.put("fiscalYear", fiscalYear);
        
        return details;
    }
}
