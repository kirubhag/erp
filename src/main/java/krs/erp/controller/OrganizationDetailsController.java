package krs.erp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for organization details, settings, and configuration.
 * Handles organization-specific settings like business hours, holidays, currencies, etc.
 */
@RestController
@RequestMapping("/api/organization")
@CrossOrigin(origins = "*")
public class OrganizationDetailsController {

    /**
     * Get organization details
     * GET /api/organization/details
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getOrganizationDetails() {
        // Return mock data for now - this will be replaced with actual database data
        Map<String, Object> organizationData = new HashMap<>();
        organizationData.put("name", "Zylker");
        
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("email", "kirubhakaran.g+cllc@zohotest.com");
        contactInfo.put("phone", "09876543210");
        contactInfo.put("fax", "09876543210");
        contactInfo.put("website", "https://www.zylker.com");
        organizationData.put("contactInfo", contactInfo);
        
        Map<String, String> address = new HashMap<>();
        address.put("street", "123 Main Street");
        address.put("city", "Chennai");
        address.put("state", "Tamil Nadu");
        address.put("country", "India");
        address.put("zipCode", "600001");
        organizationData.put("address", address);
        
        Map<String, String> branding = new HashMap<>();
        branding.put("logoUrl", "");
        organizationData.put("branding", branding);
        
        organizationData.put("accessUrl", "https://recruitqa1.localzoho.com/recruit/org875438l6/");
        
        Map<String, String> localeInfo = new HashMap<>();
        localeInfo.put("language", "en");
        localeInfo.put("currencyLocale", "United States");
        localeInfo.put("timeZone", "(GMT 5:30) India Standard Time(Asia/Kolkata)");
        localeInfo.put("dateFormat", "MM/DD/YYYY");
        organizationData.put("localeInfo", localeInfo);
        
        Map<String, String> fiscalYear = new HashMap<>();
        fiscalYear.put("startDate", "2025-01-01");
        fiscalYear.put("endDate", "2025-12-31");
        organizationData.put("fiscalYear", fiscalYear);
        
        return ResponseEntity.ok(organizationData);
    }

    /**
     * Update organization details
     * PUT /api/organization/details
     */
    @PutMapping("/details")
    public ResponseEntity<Map<String, Object>> updateOrganizationDetails(@RequestBody Map<String, Object> organizationData) {
        // TODO: Implement actual database update
        return ResponseEntity.ok(organizationData);
    }

    /**
     * Get business hours
     * GET /api/organization/business-hours
     */
    @GetMapping("/business-hours")
    public ResponseEntity<List<Map<String, Object>>> getBusinessHours() {
        List<Map<String, Object>> businessHours = new ArrayList<>();
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] codes = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> day = new HashMap<>();
            day.put("code", codes[i]);
            day.put("name", days[i]);
            day.put("isWorkingDay", i < 5); // Monday to Friday are working days
            day.put("startTime", "09:00");
            day.put("endTime", "17:00");
            businessHours.add(day);
        }
        
        return ResponseEntity.ok(businessHours);
    }

    /**
     * Update business hours
     * PUT /api/organization/business-hours
     */
    @PutMapping("/business-hours")
    public ResponseEntity<List<Map<String, Object>>> updateBusinessHours(@RequestBody List<Map<String, Object>> businessHours) {
        // TODO: Implement actual database update
        return ResponseEntity.ok(businessHours);
    }

    /**
     * Get holidays
     * GET /api/organization/holidays
     */
    @GetMapping("/holidays")
    public ResponseEntity<List<Map<String, Object>>> getHolidays() {
        List<Map<String, Object>> holidays = new ArrayList<>();
        
        // Sample holidays
        Map<String, Object> holiday1 = new HashMap<>();
        holiday1.put("id", 1L);
        holiday1.put("date", "2025-01-26");
        holiday1.put("name", "Republic Day");
        holiday1.put("description", "Republic Day of India");
        holidays.add(holiday1);
        
        Map<String, Object> holiday2 = new HashMap<>();
        holiday2.put("id", 2L);
        holiday2.put("date", "2025-08-15");
        holiday2.put("name", "Independence Day");
        holiday2.put("description", "Independence Day of India");
        holidays.add(holiday2);
        
        Map<String, Object> holiday3 = new HashMap<>();
        holiday3.put("id", 3L);
        holiday3.put("date", "2025-10-02");
        holiday3.put("name", "Gandhi Jayanti");
        holiday3.put("description", "Birthday of Mahatma Gandhi");
        holidays.add(holiday3);
        
        return ResponseEntity.ok(holidays);
    }

    /**
     * Create holiday
     * POST /api/organization/holidays
     */
    @PostMapping("/holidays")
    public ResponseEntity<Map<String, Object>> createHoliday(@RequestBody Map<String, Object> holiday) {
        // TODO: Implement actual database insert
        holiday.put("id", System.currentTimeMillis()); // Generate temporary ID
        return ResponseEntity.ok(holiday);
    }

    /**
     * Update holiday
     * PUT /api/organization/holidays/{id}
     */
    @PutMapping("/holidays/{id}")
    public ResponseEntity<Map<String, Object>> updateHoliday(@PathVariable Long id, @RequestBody Map<String, Object> holiday) {
        // TODO: Implement actual database update
        holiday.put("id", id);
        return ResponseEntity.ok(holiday);
    }

    /**
     * Delete holiday
     * DELETE /api/organization/holidays/{id}
     */
    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        // TODO: Implement actual database delete
        return ResponseEntity.noContent().build();
    }

    /**
     * Get currencies
     * GET /api/organization/currencies
     */
    @GetMapping("/currencies")
    public ResponseEntity<List<Map<String, Object>>> getCurrencies() {
        List<Map<String, Object>> currencies = new ArrayList<>();
        
        Map<String, Object> usd = new HashMap<>();
        usd.put("id", 1L);
        usd.put("code", "USD");
        usd.put("name", "US Dollar");
        usd.put("symbol", "$");
        usd.put("exchangeRate", 1.00);
        usd.put("isDefault", true);
        currencies.add(usd);
        
        Map<String, Object> eur = new HashMap<>();
        eur.put("id", 2L);
        eur.put("code", "EUR");
        eur.put("name", "Euro");
        eur.put("symbol", "€");
        eur.put("exchangeRate", 0.85);
        eur.put("isDefault", false);
        currencies.add(eur);
        
        Map<String, Object> gbp = new HashMap<>();
        gbp.put("id", 3L);
        gbp.put("code", "GBP");
        gbp.put("name", "British Pound");
        gbp.put("symbol", "£");
        gbp.put("exchangeRate", 0.73);
        gbp.put("isDefault", false);
        currencies.add(gbp);
        
        Map<String, Object> inr = new HashMap<>();
        inr.put("id", 4L);
        inr.put("code", "INR");
        inr.put("name", "Indian Rupee");
        inr.put("symbol", "₹");
        inr.put("exchangeRate", 83.00);
        inr.put("isDefault", false);
        currencies.add(inr);
        
        return ResponseEntity.ok(currencies);
    }

    /**
     * Create currency
     * POST /api/organization/currencies
     */
    @PostMapping("/currencies")
    public ResponseEntity<Map<String, Object>> createCurrency(@RequestBody Map<String, Object> currency) {
        // TODO: Implement actual database insert
        currency.put("id", System.currentTimeMillis()); // Generate temporary ID
        return ResponseEntity.ok(currency);
    }

    /**
     * Update currency
     * PUT /api/organization/currencies/{id}
     */
    @PutMapping("/currencies/{id}")
    public ResponseEntity<Map<String, Object>> updateCurrency(@PathVariable Long id, @RequestBody Map<String, Object> currency) {
        // TODO: Implement actual database update
        currency.put("id", id);
        return ResponseEntity.ok(currency);
    }

    /**
     * Delete currency
     * DELETE /api/organization/currencies/{id}
     */
    @DeleteMapping("/currencies/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        // TODO: Implement actual database delete
        return ResponseEntity.noContent().build();
    }

    /**
     * Get fiscal year settings
     * GET /api/organization/fiscal-year
     */
    @GetMapping("/fiscal-year")
    public ResponseEntity<Map<String, String>> getFiscalYear() {
        Map<String, String> fiscalYear = new HashMap<>();
        fiscalYear.put("startDate", "2025-01-01");
        fiscalYear.put("endDate", "2025-12-31");
        return ResponseEntity.ok(fiscalYear);
    }

    /**
     * Update fiscal year settings
     * PUT /api/organization/fiscal-year
     */
    @PutMapping("/fiscal-year")
    public ResponseEntity<Map<String, String>> updateFiscalYear(@RequestBody Map<String, String> fiscalYear) {
        // TODO: Implement actual database update
        return ResponseEntity.ok(fiscalYear);
    }

    /**
     * Upload organization logo
     * POST /api/organization/logo
     */
    @PostMapping("/logo")
    public ResponseEntity<Map<String, String>> uploadLogo(@RequestParam("logo") String logoFile) {
        // TODO: Implement actual file upload and storage
        Map<String, String> response = new HashMap<>();
        response.put("logoUrl", "/uploads/logo.png");
        return ResponseEntity.ok(response);
    }
}