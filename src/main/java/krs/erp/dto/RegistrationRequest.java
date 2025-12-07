package krs.erp.dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String organizationName;
    private String adminEmail;
    private String adminPassword;
    private String adminFirstName;
    private String adminLastName;
    private String adminPhone;
}
