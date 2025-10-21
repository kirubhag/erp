package krs.erp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import krs.erp.dto.StudentCustomViewDTO;
import krs.erp.dto.StudentFieldDTO;
import krs.erp.service.StudentCustomViewService;

/**
 * REST Controller for managing Student Custom Views
 */
@RestController
@RequestMapping("/api/student-custom-views")
@CrossOrigin(origins = "*")
public class StudentCustomViewController {
    
    @Autowired
    private StudentCustomViewService customViewService;
    
    /**
     * Get all available fields that can be used in custom views
     */
    @GetMapping("/available-fields")
    public ResponseEntity<List<StudentFieldDTO>> getAvailableFields() {
        List<StudentFieldDTO> fields = customViewService.getAvailableFields();
        return ResponseEntity.ok(fields);
    }
    
    /**
     * Get default visible fields
     */
    @GetMapping("/default-fields")
    public ResponseEntity<List<String>> getDefaultFields() {
        List<String> defaultFields = customViewService.getDefaultVisibleFields();
        return ResponseEntity.ok(defaultFields);
    }
    
    /**
     * Create a new custom view
     */
    @PostMapping
    public ResponseEntity<?> createCustomView(
            @Valid @RequestBody StudentCustomViewDTO viewDTO,
            @RequestHeader(value = "X-User", defaultValue = "system") String currentUser) {
        try {
            customViewService.validateCustomView(viewDTO);
            StudentCustomViewDTO createdView = customViewService.createCustomView(viewDTO, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdView);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create custom view: " + e.getMessage()));
        }
    }
    
    /**
     * Update an existing custom view
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomView(
            @PathVariable Long id,
            @Valid @RequestBody StudentCustomViewDTO viewDTO,
            @RequestHeader(value = "X-User", defaultValue = "system") String currentUser) {
        try {
            customViewService.validateCustomView(viewDTO);
            StudentCustomViewDTO updatedView = customViewService.updateCustomView(id, viewDTO, currentUser);
            return ResponseEntity.ok(updatedView);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to update custom view: " + e.getMessage()));
        }
    }
    
    /**
     * Delete a custom view
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomView(
            @PathVariable Long id,
            @RequestHeader(value = "X-User", defaultValue = "system") String currentUser) {
        try {
            customViewService.deleteCustomView(id, currentUser);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete custom view: " + e.getMessage()));
        }
    }
    
    /**
     * Get a custom view by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomView(@PathVariable Long id) {
        try {
            Optional<StudentCustomViewDTO> view = customViewService.getCustomView(id);
            return view.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve custom view: " + e.getMessage()));
        }
    }
    
    /**
     * Get all custom views accessible to the current user
     */
    @GetMapping
    public ResponseEntity<?> getAccessibleViews(
            @RequestHeader(value = "X-User", defaultValue = "system") String currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "viewName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<StudentCustomViewDTO> views = customViewService.getAccessibleViews(currentUser, pageable);
            return ResponseEntity.ok(views);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve custom views: " + e.getMessage()));
        }
    }
    
    /**
     * Get all custom views as a simple list (no pagination)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAccessibleViewsList(
            @RequestHeader(value = "X-User", defaultValue = "system") String currentUser) {
        try {
            List<StudentCustomViewDTO> views = customViewService.getAccessibleViews(currentUser);
            return ResponseEntity.ok(views);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve custom views: " + e.getMessage()));
        }
    }
    
    /**
     * Search custom views by criteria
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCustomViews(
            @RequestParam(required = false) String viewName,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "viewName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<StudentCustomViewDTO> views = customViewService.searchCustomViews(viewName, isPublic, username, pageable);
            return ResponseEntity.ok(views);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to search custom views: " + e.getMessage()));
        }
    }
    
    /**
     * Get the default custom view
     */
    @GetMapping("/default")
    public ResponseEntity<?> getDefaultView() {
        try {
            Optional<StudentCustomViewDTO> defaultView = customViewService.getDefaultView();
            return defaultView.map(ResponseEntity::ok)
                             .orElse(ResponseEntity.noContent().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve default view: " + e.getMessage()));
        }
    }
    
    /**
     * Set a custom view as default
     */
    @PutMapping("/{id}/set-default")
    public ResponseEntity<?> setAsDefault(
            @PathVariable Long id,
            @RequestHeader(value = "X-User", defaultValue = "system") String currentUser) {
        try {
            customViewService.setAsDefault(id, currentUser);
            return ResponseEntity.ok(new SuccessResponse("Custom view set as default successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to set as default: " + e.getMessage()));
        }
    }
    
    /**
     * Get custom views created by current user
     */
    @GetMapping("/my-views")
    public ResponseEntity<?> getMyViews(
            @RequestHeader(value = "X-User", defaultValue = "system") String currentUser) {
        try {
            List<StudentCustomViewDTO> views = customViewService.getViewsByUser(currentUser);
            return ResponseEntity.ok(views);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve user views: " + e.getMessage()));
        }
    }
    
    /**
     * Duplicate an existing custom view
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<?> duplicateCustomView(
            @PathVariable Long id,
            @RequestParam String newViewName,
            @RequestHeader(value = "X-User", defaultValue = "system") String currentUser) {
        try {
            StudentCustomViewDTO duplicatedView = customViewService.duplicateCustomView(id, newViewName, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(duplicatedView);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to duplicate custom view: " + e.getMessage()));
        }
    }
    
    /**
     * Get custom view statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics(
            @RequestHeader(value = "X-User", defaultValue = "system") String currentUser) {
        try {
            Object statistics = customViewService.getCustomViewStatistics(currentUser);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Validate custom view data
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateCustomView(@RequestBody StudentCustomViewDTO viewDTO) {
        try {
            customViewService.validateCustomView(viewDTO);
            return ResponseEntity.ok(new SuccessResponse("Custom view is valid"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to validate custom view: " + e.getMessage()));
        }
    }
    
    // Helper classes for API responses
    static class ErrorResponse {
        private String error;
        private long timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getError() { return error; }
        public long getTimestamp() { return timestamp; }
    }
    
    static class SuccessResponse {
        private String message;
        private long timestamp;
        
        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}