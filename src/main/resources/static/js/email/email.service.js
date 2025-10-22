// Email Service - Handles email template CRUD operations and email sending
angular.module('erpApp').factory('EmailService', ['$http', function($http) {
    var baseUrl = '/api/email-templates';
    var emailLogUrl = '/api/email-logs';
    
    return {
        // Template CRUD operations
        getAllTemplates: function(page, size, entityType) {
            var params = { page: page || 0, size: size || 10 };
            if (entityType) params.entityType = entityType;
            
            return $http.get(baseUrl, { params: params });
        },
        
        getTemplateById: function(id) {
            return $http.get(baseUrl + '/' + id);
        },
        
        createTemplate: function(template) {
            return $http.post(baseUrl, template);
        },
        
        updateTemplate: function(id, template) {
            return $http.put(baseUrl + '/' + id, template);
        },
        
        deleteTemplate: function(id) {
            return $http.delete(baseUrl + '/' + id);
        },
        
        getActiveTemplatesByEntityType: function(entityType) {
            return $http.get(baseUrl + '/entity-type/' + entityType);
        },
        
        getAllActiveTemplates: function() {
            return $http.get(baseUrl + '/active');
        },
        
        searchTemplates: function(searchTerm) {
            return $http.get(baseUrl + '/search', { params: { term: searchTerm } });
        },
        
        getPopularTemplates: function(minUsage) {
            return $http.get(baseUrl + '/popular', { params: { minUsage: minUsage || 5 } });
        },
        
        // Template processing and sending
        previewTemplate: function(id, entityId, customVariables) {
            var data = {};
            if (entityId) data.entityId = entityId;
            if (customVariables) data.customVariables = customVariables;
            
            return $http.post(baseUrl + '/' + id + '/preview', data);
        },
        
        sendEmailUsingTemplate: function(templateId, entityId, recipientEmail, recipientName, sentBy) {
            return $http.post(baseUrl + '/' + templateId + '/send', {
                entityId: entityId,
                recipientEmail: recipientEmail,
                recipientName: recipientName,
                sentBy: sentBy
            });
        },
        
        sendEmailUsingTemplateWithCustomVariables: function(templateId, customVariables, entityType, entityId, recipientEmail, recipientName, sentBy) {
            return $http.post(baseUrl + '/' + templateId + '/send-custom', {
                customVariables: customVariables,
                entityType: entityType,
                entityId: entityId,
                recipientEmail: recipientEmail,
                recipientName: recipientName,
                sentBy: sentBy
            });
        },
        
        // Email logs
        getAllEmailLogs: function(params) {
            // If called with individual parameters (legacy support) 
            if (typeof params === 'number' || typeof params === 'undefined' || arguments.length > 1) {
                var page = arguments[0] || 0;
                var size = arguments[1] || 10;
                var status = arguments[2];
                var entityType = arguments[3];
                
                var queryParams = { page: page, size: size };
                if (status) queryParams.status = status;
                if (entityType) queryParams.entityType = entityType;
                
                return $http.get(emailLogUrl, { params: queryParams });
            }
            
            // If called with params object (new way) - only when single object parameter
            if (typeof params === 'object' && params !== null) {
                return $http.get(emailLogUrl, { params: params });
            }
            
            // Default case
            return $http.get(emailLogUrl, { params: {} });
        },
        
        getEmailLogsForEntity: function(entityType, entityId) {
            return $http.get(emailLogUrl + '/entity/' + entityType + '/' + entityId);
        },
        
        getEmailLogsForEntityPaginated: function(entityType, entityId, page, size) {
            return $http.get(emailLogUrl + '/entity/' + entityType + '/' + entityId + '/paginated', {
                params: { page: page || 0, size: size || 10 }
            });
        },
        
        getEmailLogsForRecipient: function(email) {
            return $http.get(emailLogUrl + '/recipient/' + encodeURIComponent(email));
        },
        
        getFailedEmails: function() {
            return $http.get(emailLogUrl + '/failed');
        },
        
        getPendingEmails: function() {
            return $http.get(emailLogUrl + '/pending');
        },
        
        getRecentEmails: function() {
            return $http.get(emailLogUrl + '/recent');
        },
        
        searchEmailLogs: function(searchTerm) {
            return $http.get(emailLogUrl + '/search', { params: { term: searchTerm } });
        },
        
        retryFailedEmail: function(emailLogId) {
            return $http.post(emailLogUrl + '/' + emailLogId + '/retry');
        },
        
        // Alias for retryFailedEmail to match controller expectations
        retryEmail: function(emailLogId) {
            return $http.post(emailLogUrl + '/' + emailLogId + '/retry');
        },
        
        // Export email logs
        exportEmailLogs: function(params) {
            return $http.get(emailLogUrl + '/export', { 
                params: params,
                responseType: 'blob'
            });
        },
        
        // Get emails by entity (alias for getEmailLogsForEntityPaginated)
        getEmailsByEntity: function(entityType, entityId, options) {
            const params = {
                page: options?.page || 0,
                size: options?.size || 10
            };
            return $http.get(emailLogUrl + '/entity/' + entityType + '/' + entityId + '/paginated', {
                params: params
            });
        },
        
        sendDirectEmail: function(entityType, entityId, recipientEmail, recipientName, subject, body, sentBy) {
            return $http.post(emailLogUrl + '/send-direct', {
                entityType: entityType,
                entityId: entityId,
                recipientEmail: recipientEmail,
                recipientName: recipientName,
                subject: subject,
                body: body,
                sentBy: sentBy
            });
        },
        
        getEmailStatistics: function(daysSince) {
            var params = {};
            if (daysSince) params.daysSince = daysSince;
            
            return $http.get(emailLogUrl + '/statistics', { params: params });
        },
        
        getEmailCountForEntity: function(entityType, entityId) {
            return $http.get(emailLogUrl + '/count/' + entityType + '/' + entityId);
        },
        
        // Utility methods
        getEntityTypes: function() {
            return $http.get(baseUrl + '/entity-types');
        },
        
        getEmailStatuses: function() {
            return $http.get(emailLogUrl + '/statuses');
        },
        
        // Helper method to create default template data
        createDefaultTemplateData: function() {
            return {
                templateName: '',
                subject: '',
                body: '',
                description: '',
                entityType: 'STUDENT',
                isActive: true,
                createdBy: 'Admin' // This should be set from current user context
            };
        }
    };
}]);