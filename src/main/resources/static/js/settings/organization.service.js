// Organization Service for managing organization-related API calls
angular.module('erpApp').factory('OrganizationService', ['$http', function($http) {
    var baseUrl = '/api/organizations';
    
    return {
        /**
         * Get all organizations with pagination and search
         */
        getAllOrganizations: function(page, size, search) {
            var params = {
                page: page || 0,
                size: size || 10
            };
            
            if (search) {
                params.search = search;
            }
            
            return $http.get(baseUrl, { params: params });
        },
        
        /**
         * Get organization by ID
         */
        getOrganizationById: function(id) {
            return $http.get(baseUrl + '/' + id);
        },
        
        /**
         * Get organization by code
         */
        getOrganizationByCode: function(code) {
            return $http.get(baseUrl + '/code/' + code);
        },
        
        /**
         * Get organizations by type
         */
        getOrganizationsByType: function(type) {
            return $http.get(baseUrl + '/type/' + type);
        },
        
        /**
         * Get all organization types
         */
        getOrganizationTypes: function() {
            return $http.get(baseUrl + '/types');
        },
        
        /**
         * Create new organization
         */
        createOrganization: function(organization) {
            return $http.post(baseUrl, organization);
        },
        
        /**
         * Update existing organization
         */
        updateOrganization: function(id, organization) {
            return $http.put(baseUrl + '/' + id, organization);
        },
        
        /**
         * Delete organization
         */
        deleteOrganization: function(id) {
            return $http.delete(baseUrl + '/' + id);
        },
        
        /**
         * Get organization count
         */
        getOrganizationCount: function() {
            return $http.get(baseUrl + '/count');
        },
        
        /**
         * Create default organization data structure
         */
        createDefaultOrganization: function() {
            return {
                id: null,
                name: '',
                type: 'School',
                code: '',
                contactInfo: {
                    email: '',
                    phoneNumber: '',
                    website: ''
                },
                address: {
                    line1: '',
                    line2: '',
                    city: '',
                    state: '',
                    postalCode: '',
                    country: ''
                },
                branding: {
                    logoUrl: '',
                    primaryColor: '#007bff',
                    motto: '',
                    description: ''
                },
                establishedYear: new Date().getFullYear(),
                isActive: true
            };
        }
    };
}]);