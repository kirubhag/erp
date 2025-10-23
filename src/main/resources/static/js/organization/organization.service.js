angular.module('erpApp').service('OrganizationService', ['$http', '$q', function($http, $q) {
    
    var baseUrl = '/api/organization';
    
    // Get organization details
    this.getOrganizationDetails = function() {
        return $http.get(baseUrl + '/details').then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error fetching organization details:', error);
            return $q.reject(error);
        });
    };
    
    // Update organization details
    this.updateOrganizationDetails = function(organizationData) {
        return $http.put(baseUrl + '/details', organizationData).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error updating organization details:', error);
            return $q.reject(error);
        });
    };
    
    // Upload organization logo
    this.uploadLogo = function(logoFile) {
        var formData = new FormData();
        formData.append('logo', logoFile);
        
        return $http.post(baseUrl + '/logo', formData, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error uploading logo:', error);
            return $q.reject(error);
        });
    };
    
    // Get business hours
    this.getBusinessHours = function() {
        return $http.get(baseUrl + '/business-hours').then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error fetching business hours:', error);
            return $q.reject(error);
        });
    };
    
    // Update business hours
    this.updateBusinessHours = function(businessHours) {
        return $http.put(baseUrl + '/business-hours', businessHours).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error updating business hours:', error);
            return $q.reject(error);
        });
    };
    
    // Get holidays
    this.getHolidays = function() {
        return $http.get(baseUrl + '/holidays').then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error fetching holidays:', error);
            return $q.reject(error);
        });
    };
    
    // Create holiday
    this.createHoliday = function(holiday) {
        return $http.post(baseUrl + '/holidays', holiday).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error creating holiday:', error);
            return $q.reject(error);
        });
    };
    
    // Update holiday
    this.updateHoliday = function(holidayId, holiday) {
        return $http.put(baseUrl + '/holidays/' + holidayId, holiday).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error updating holiday:', error);
            return $q.reject(error);
        });
    };
    
    // Delete holiday
    this.deleteHoliday = function(holidayId) {
        return $http.delete(baseUrl + '/holidays/' + holidayId).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error deleting holiday:', error);
            return $q.reject(error);
        });
    };
    
    // Get currencies
    this.getCurrencies = function() {
        return $http.get(baseUrl + '/currencies').then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error fetching currencies:', error);
            return $q.reject(error);
        });
    };
    
    // Create currency
    this.createCurrency = function(currency) {
        return $http.post(baseUrl + '/currencies', currency).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error creating currency:', error);
            return $q.reject(error);
        });
    };
    
    // Update currency
    this.updateCurrency = function(currencyId, currency) {
        return $http.put(baseUrl + '/currencies/' + currencyId, currency).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error updating currency:', error);
            return $q.reject(error);
        });
    };
    
    // Delete currency
    this.deleteCurrency = function(currencyId) {
        return $http.delete(baseUrl + '/currencies/' + currencyId).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error deleting currency:', error);
            return $q.reject(error);
        });
    };
    
    // Get fiscal year settings
    this.getFiscalYear = function() {
        return $http.get(baseUrl + '/fiscal-year').then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error fetching fiscal year:', error);
            return $q.reject(error);
        });
    };
    
    // Update fiscal year settings
    this.updateFiscalYear = function(fiscalYear) {
        return $http.put(baseUrl + '/fiscal-year', fiscalYear).then(function(response) {
            return response.data;
        }).catch(function(error) {
            console.error('Error updating fiscal year:', error);
            return $q.reject(error);
        });
    };
}]);