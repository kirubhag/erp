package krs.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.service.OrganizationService;

@RestController
@RequestMapping("/api/organization")
@CrossOrigin(origins = "*")
public class OrganizationDetailsController {
    
    @Autowired
    private OrganizationService organizationService;
    
    /**
     * Get all organizations with pagination
     */
}