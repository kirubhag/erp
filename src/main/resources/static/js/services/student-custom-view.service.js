// Student Custom View Service - Separate File Implementation
angular.module('erpApp').service('StudentCustomViewService', ['$http', '$q', function($http, $q) {
    console.log('StudentCustomViewService loaded from separate file - v2.0');
    
    var self = this;
    var baseUrl = '/api/student-custom-views';
    
    // Helper method to create default view data
    self.createDefaultViewData = function() {
        return {
            viewName: '',
            description: '',
            selectedFields: [],
            isDefault: false,
            isPublic: false
        };
    };
    
    // Real HTTP methods
    self.getAvailableFields = function() {
        console.log('getAvailableFields called - making HTTP request');
        return $http.get(baseUrl + '/available-fields');
    };
    
    self.getAccessibleViewsList = function() {
        console.log('getAccessibleViewsList called - making HTTP request');
        return $http.get(baseUrl + '/list');
    };
    
    // Helper methods
    self.groupFieldsByCategory = function(fields) {
        console.log('groupFieldsByCategory called with', fields ? fields.length : 0, 'fields');
        var grouped = {};
        if (fields && fields.length) {
            fields.forEach(function(field) {
                if (!grouped[field.category]) {
                    grouped[field.category] = [];
                }
                grouped[field.category].push(field);
            });
        }
        return grouped;
    };
    
    // Stub methods to prevent errors
    self.getDefaultVisibleFields = function() {
        return [];
    };
    
    self.createCustomView = function(viewData) {
        console.log('createCustomView called - making HTTP request');
        return $http.post(baseUrl, viewData);
    };
    
    self.updateCustomView = function(viewId, viewData) {
        console.log('updateCustomView called - making HTTP request');
        return $http.put(baseUrl + '/' + viewId, viewData);
    };
    
    self.deleteCustomView = function(viewId) {
        console.log('deleteCustomView called - making HTTP request');
        return $http.delete(baseUrl + '/' + viewId);
    };
    
    self.setAsDefault = function(viewId) {
        console.log('setAsDefault called - making HTTP request');
        return $http.put(baseUrl + '/' + viewId + '/set-default');
    };
    
    self.duplicateCustomView = function(viewId, newName) {
        console.log('duplicateCustomView called - making HTTP request');
        return $http.post(baseUrl + '/' + viewId + '/duplicate', { newViewName: newName });
    };
    
    self.getFieldDisplayName = function(fieldName, availableFields) {
        return fieldName;
    };
    
    return self;
}]);