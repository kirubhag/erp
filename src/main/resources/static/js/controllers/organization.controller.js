// Organization Controller for managing organization UI and interactions
angular.module('erpApp').controller('OrganizationController', ['$scope', 'OrganizationService', '$timeout', function($scope, OrganizationService, $timeout) {
    
    // Initialize scope variables
    $scope.organizations = [];
    $scope.currentOrganization = null;
    $scope.isEditing = false;
    $scope.isLoading = false;
    $scope.searchTerm = '';
    $scope.organizationTypes = [
        { value: 'School', label: 'School' },
        { value: 'College', label: 'College' },
        { value: 'University', label: 'University' },
        { value: 'Institute', label: 'Institute' },
        { value: 'Academy', label: 'Academy' },
        { value: 'Center', label: 'Center' },
        { value: 'Foundation', label: 'Foundation' },
        { value: 'Corporation', label: 'Corporation' },
        { value: 'Department', label: 'Department' }
    ];
    
    // Pagination
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.totalPages = 0;
    $scope.totalElements = 0;
    
    // Form validation flags
    $scope.formErrors = {};
    $scope.showSuccessMessage = false;
    $scope.successMessage = '';
    $scope.showErrorMessage = false;
    $scope.errorMessage = '';
    
    // Initialize organizationData for the form (if not already set by parent scope)
    $scope.currentYear = new Date().getFullYear();
    
    // Check if we're running inside a settings context (parent scope has loadOrganizationData function)
    $scope.isInSettingsContext = function() {
        return $scope.$parent && typeof $scope.$parent.loadOrganizationData === 'function';
    };
    
    // Ensure organizationData is always initialized
    $scope.initializeOrganizationData = function() {
        // If we're in settings context and parent already has organizationData, don't override it
        if ($scope.isInSettingsContext() && $scope.$parent.organizationData) {
            $scope.organizationData = $scope.$parent.organizationData;
        } else if (!$scope.organizationData) {
            $scope.organizationData = OrganizationService.createDefaultOrganization();
        }
        
        // Ensure nested objects exist
        if (!$scope.organizationData.contactInfo) {
            $scope.organizationData.contactInfo = {};
        }
        if (!$scope.organizationData.address) {
            $scope.organizationData.address = {};
        }
        if (!$scope.organizationData.branding) {
            $scope.organizationData.branding = {};
        }
    };
    
    /**
     * Initialize the controller
     */
    $scope.init = function() {
        try {
            $scope.initializeOrganizationData();
            
            // Only load organizations list if not in settings context
            if (!$scope.isInSettingsContext()) {
                $scope.loadOrganizations();
            }
            
            $scope.resetForm();
        } catch (error) {
            console.error('Error initializing organization controller:', error);
            // Ensure basic initialization even if something fails
            $scope.initializeOrganizationData();
        }
    };
    
    /**
     * Save organization (main form submit)
     */
    $scope.saveOrganization = function() {
        // Ensure organizationData is initialized
        $scope.initializeOrganizationData();
        
        if (!$scope.validateOrganizationForm()) {
            return;
        }
        
        $scope.isLoading = true;
        
        var promise;
        if ($scope.organizationData.id) {
            // Update existing organization
            promise = OrganizationService.updateOrganization($scope.organizationData.id, $scope.organizationData);
        } else {
            // Create new organization
            promise = OrganizationService.createOrganization($scope.organizationData);
        }
        
        promise.then(function(response) {
            $scope.showSuccess($scope.organizationData.id ? 'Organization updated successfully!' : 'Organization created successfully!');
            $scope.loadOrganizations();
            
            // Refresh organization data in parent scope (for view pages)
            if ($scope.isInSettingsContext()) {
                $scope.$parent.loadOrganizationData();
                // Update parent scope organizationData immediately
                $scope.$parent.organizationData = $scope.organizationData;
            }
            
            $scope.resetToDefaults();
            $scope.isLoading = false;
        })
        .catch(function(error) {
            console.error('Error saving organization:', error);
            if (error.data && error.data.message) {
                $scope.showError('Error: ' + error.data.message);
            } else {
                $scope.showError('Failed to save organization. Please check all required fields and try again.');
            }
            $scope.isLoading = false;
        });
    };
    
    /**
     * Reset form to defaults
     */
    $scope.resetToDefaults = function() {
        $scope.organizationData = OrganizationService.createDefaultOrganization();
        $scope.initializeOrganizationData();
        $scope.formErrors = {};
        $scope.clearMessages();
    };
    
    /**
     * Validate organization form
     */
    $scope.validateOrganizationForm = function() {
        $scope.formErrors = {};
        
        // Required field validations
        if (!$scope.organizationData.name || $scope.organizationData.name.trim() === '') {
            $scope.formErrors.name = 'Organization name is required';
        }
        
        if (!$scope.organizationData.type || $scope.organizationData.type.trim() === '') {
            $scope.formErrors.type = 'Organization type is required';
        }
        
        // Email validation
        if ($scope.organizationData.contactInfo && $scope.organizationData.contactInfo.email && $scope.organizationData.contactInfo.email.trim() !== '') {
            var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test($scope.organizationData.contactInfo.email)) {
                $scope.formErrors.email = 'Please enter a valid email address';
            }
        }
        
        // Website validation
        if ($scope.organizationData.contactInfo && $scope.organizationData.contactInfo.website && $scope.organizationData.contactInfo.website.trim() !== '') {
            var urlRegex = /^https?:\/\/.+/;
            if (!urlRegex.test($scope.organizationData.contactInfo.website)) {
                $scope.formErrors.website = 'Website must start with http:// or https://';
            }
        }
        
        // Year validation
        if ($scope.organizationData.establishedYear) {
            var currentYear = new Date().getFullYear();
            if ($scope.organizationData.establishedYear < 1000 || $scope.organizationData.establishedYear > currentYear) {
                $scope.formErrors.establishedYear = 'Please enter a valid year between 1000 and ' + currentYear;
            }
        }
        
        return Object.keys($scope.formErrors).length === 0;
    };
    
    /**
     * Load all organizations with pagination
     */
    $scope.loadOrganizations = function() {
        $scope.isLoading = true;
        
        OrganizationService.getAllOrganizations($scope.currentPage, $scope.pageSize, $scope.searchTerm)
            .then(function(response) {
                $scope.organizations = response.data.content || response.data;
                $scope.totalPages = response.data.totalPages || 0;
                $scope.totalElements = response.data.totalElements || $scope.organizations.length;
                $scope.isLoading = false;
            })
            .catch(function(error) {
                console.error('Error loading organizations:', error);
                $scope.showError('Failed to load organizations. Please try again.');
                $scope.isLoading = false;
            });
    };
    
    /**
     * Search organizations
     */
    $scope.searchOrganizations = function() {
        $scope.currentPage = 0; // Reset to first page
        $scope.loadOrganizations();
    };
    
    /**
     * Clear search
     */
    $scope.clearSearch = function() {
        $scope.searchTerm = '';
        $scope.searchOrganizations();
    };
    
    /**
     * Create new organization
     */
    $scope.createOrganization = function() {
        $scope.resetForm();
        $scope.organizationData = OrganizationService.createDefaultOrganization();
        $scope.isEditing = true;
    };
    
    /**
     * Edit organization
     */
    $scope.editOrganization = function(organization) {
        // Map the organization data to the expected structure
        $scope.organizationData = {
            id: organization.id,
            name: organization.name,
            type: organization.type,
            contactInfo: {
                email: organization.email,
                phoneNumber: organization.phone,
                website: organization.website
            },
            address: {
                line1: organization.streetAddress,
                line2: '',
                city: organization.city,
                state: organization.state,
                postalCode: organization.postalCode,
                country: organization.country
            },
            branding: {
                logoUrl: organization.logoUrl,
                primaryColor: '#007bff',
                motto: '',
                description: organization.description
            },
            establishedYear: organization.establishedYear
        };
        $scope.isEditing = true;
        $scope.clearMessages();
    };

    /**
     * Pagination functions
     */
    $scope.goToPage = function(page) {
        if (page >= 0 && page < $scope.totalPages) {
            $scope.currentPage = page;
            $scope.loadOrganizations();
        }
    };
    
    $scope.previousPage = function() {
        if ($scope.currentPage > 0) {
            $scope.goToPage($scope.currentPage - 1);
        }
    };
    
    $scope.nextPage = function() {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.goToPage($scope.currentPage + 1);
        }
    };
    
    /**
     * Get page numbers for pagination display
     */
    $scope.getPageNumbers = function() {
        var pages = [];
        var start = Math.max(0, $scope.currentPage - 2);
        var end = Math.min($scope.totalPages - 1, start + 4);
        
        for (var i = start; i <= end; i++) {
            pages.push(i);
        }
        return pages;
    };
    
    /**
     * Reset form
     */
    $scope.resetForm = function() {
        $scope.initializeOrganizationData();
        $scope.isEditing = false;
        $scope.formErrors = {};
        $scope.clearMessages();
    };
    
    /**
     * Cancel editing
     */
    $scope.cancelEdit = function() {
        $scope.isEditing = false;
        $scope.formErrors = {};
        $scope.clearMessages();
    };

    /**
     * Utility functions for messages
     */
    $scope.showSuccess = function(message) {
        $scope.successMessage = message;
        $scope.showSuccessMessage = true;
        $scope.showErrorMessage = false;
        $timeout(function() {
            $scope.clearMessages();
        }, 5000);
    };
    
    $scope.showError = function(message) {
        $scope.errorMessage = message;
        $scope.showErrorMessage = true;
        $scope.showSuccessMessage = false;
    };
    
    $scope.clearMessages = function() {
        $scope.showSuccessMessage = false;
        $scope.showErrorMessage = false;
        $scope.successMessage = '';
        $scope.errorMessage = '';
    };
    
    /**
     * Generate organization code from name
     */
    $scope.generateCodeFromName = function() {
        if ($scope.organizationData && $scope.organizationData.name) {
            var code = $scope.organizationData.name
                .toUpperCase()
                .replace(/[^A-Z0-9]/g, '')
                .substring(0, 10);
            $scope.organizationData.code = code;
        }
    };
    
    // Initialize controller
    $scope.init();
}]);