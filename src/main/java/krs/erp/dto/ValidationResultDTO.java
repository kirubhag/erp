package krs.erp.dto;

import java.util.List;

/**
 * DTO for validation results
 */
public class ValidationResultDTO {
    
    private Boolean valid;
    private List<String> errors;
    
    // Constructors
    public ValidationResultDTO() {
    }
    
    public ValidationResultDTO(Boolean valid) {
        this.valid = valid;
    }
    
    public ValidationResultDTO(Boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }
    
    // Getters and Setters
    public Boolean getValid() {
        return valid;
    }
    
    public void setValid(Boolean valid) {
        this.valid = valid;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
