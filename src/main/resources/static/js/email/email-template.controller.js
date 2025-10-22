// Email Template Controller - Manages email templates and email logs
angular.module('erpApp').controller('EmailTemplateController', ['$scope', '$http', 'EmailService', function($scope, $http, EmailService) {
    
    // Initialize scope variables
    $scope.templates = [];
    $scope.emailLogs = [];
    $scope.currentTemplate = {};
    $scope.isEditing = false;
    $scope.loading = false;
    
    // Pagination
    $scope.templatePagination = {
        currentPage: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0
    };
    
    $scope.emailLogPagination = {
        currentPage: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0
    };
    
    // Filter and search
    $scope.templateFilters = {
        entityType: '',
        searchTerm: '',
        showActiveOnly: true
    };
    
    $scope.emailLogFilters = {
        status: '',
        entityType: '',
        searchTerm: ''
    };
    
    // Options
    $scope.entityTypes = [];
    $scope.emailStatuses = [];
    $scope.pageSizeOptions = [5, 10, 20, 50];
    
    // Modal controls
    $scope.showTemplateModal = false;
    $scope.showDeleteModal = false;
    $scope.showPreviewModal = false;
    $scope.showSendEmailModal = false;
    
    // Preview and send email data
    $scope.previewData = {
        subject: '',
        body: '',
        entityId: '',
        customVariables: {}
    };
    
    $scope.sendEmailData = {
        templateId: null,
        entityId: '',
        recipientEmail: '',
        recipientName: '',
        sentBy: 'Admin'
    };
    
    // Statistics
    $scope.emailStatistics = {
        totalEmails: 0,
        sentEmails: 0,
        deliveredEmails: 0,
        failedEmails: 0
    };
    
    // Initialize controller
    $scope.init = function() {
        $scope.loadEntityTypes();
        $scope.loadEmailStatuses();
        $scope.loadTemplates();
        $scope.loadEmailStatistics();
    };
    
    // Load entity types
    $scope.loadEntityTypes = function() {
        EmailService.getEntityTypes().then(function(response) {
            $scope.entityTypes = response.data;
        }).catch(function(error) {
            console.error('Error loading entity types:', error);
        });
    };
    
    // Load email statuses
    $scope.loadEmailStatuses = function() {
        EmailService.getEmailStatuses().then(function(response) {
            $scope.emailStatuses = response.data;
        }).catch(function(error) {
            console.error('Error loading email statuses:', error);
        });
    };
    
    // Load templates
    // Load email templates
    $scope.loadTemplates = function() {
        $scope.loading = true;
        EmailService.getAllTemplates($scope.templatePagination.currentPage, $scope.templatePagination.size)
            .then(function(response) {
                $scope.templates = response.data.content || [];
                $scope.templatePagination.totalElements = response.data.totalElements || 0;
                $scope.templatePagination.totalPages = response.data.totalPages || 0;
                $scope.loading = false;
            })
            .catch(function(error) {
                console.error('Error loading templates:', error);
                $scope.error = 'Failed to load email templates';
                $scope.loading = false;
            });
    };    // Load email logs
    $scope.loadEmailLogs = function(page) {
        $scope.loading = true;
        page = page || 0;
        
        EmailService.getAllEmailLogs(page, $scope.emailLogPagination.size, $scope.emailLogFilters.status, $scope.emailLogFilters.entityType)
            .then(function(response) {
                $scope.emailLogs = response.data.content;
                $scope.emailLogPagination.currentPage = response.data.number;
                $scope.emailLogPagination.totalElements = response.data.totalElements;
                $scope.emailLogPagination.totalPages = response.data.totalPages;
                $scope.loading = false;
            })
            .catch(function(error) {
                console.error('Error loading email logs:', error);
                $scope.loading = false;
            });
    };
    
    // Load email statistics
    $scope.loadEmailStatistics = function() {
        EmailService.getEmailStatistics(30).then(function(response) {
            $scope.emailStatistics = response.data;
        }).catch(function(error) {
            console.error('Error loading email statistics:', error);
        });
    };
    
    // Template CRUD operations
    $scope.showCreateTemplateForm = function() {
        $scope.currentTemplate = EmailService.createDefaultTemplateData();
        $scope.isEditing = false;
        $scope.showTemplateModal = true;
    };
    
    $scope.editTemplate = function(template) {
        $scope.currentTemplate = angular.copy(template);
        $scope.isEditing = true;
        $scope.showTemplateModal = true;
    };
    
    $scope.saveTemplate = function() {
        if (!$scope.currentTemplate.templateName || !$scope.currentTemplate.subject || !$scope.currentTemplate.body) {
            alert('Please fill in all required fields');
            return;
        }
        
        $scope.loading = true;
        
        var savePromise = $scope.isEditing ? 
            EmailService.updateTemplate($scope.currentTemplate.id, $scope.currentTemplate) :
            EmailService.createTemplate($scope.currentTemplate);
        
        savePromise.then(function(response) {
            $scope.closeTemplateModal();
            $scope.loadTemplates();
            $scope.loading = false;
            alert($scope.isEditing ? 'Template updated successfully!' : 'Template created successfully!');
        }).catch(function(error) {
            $scope.loading = false;
            alert('Error saving template: ' + (error.data && error.data.message ? error.data.message : 'Unknown error'));
        });
    };
    
    $scope.deleteTemplate = function(template) {
        $scope.templateToDelete = template;
        $scope.showDeleteModal = true;
    };
    
    $scope.confirmDelete = function() {
        $scope.loading = true;
        
        EmailService.deleteTemplate($scope.templateToDelete.id).then(function(response) {
            $scope.closeDeleteModal();
            $scope.loadTemplates();
            $scope.loading = false;
            alert('Template deleted successfully!');
        }).catch(function(error) {
            $scope.loading = false;
            alert('Error deleting template: ' + (error.data && error.data.message ? error.data.message : 'Unknown error'));
        });
    };
    
    // Template preview
    $scope.previewTemplate = function(template) {
        $scope.currentTemplate = template;
        $scope.previewData = {
            subject: '',
            body: '',
            entityId: '',
            customVariables: {}
        };
        $scope.showPreviewModal = true;
    };
    
    $scope.generatePreview = function() {
        if (!$scope.previewData.entityId && Object.keys($scope.previewData.customVariables).length === 0) {
            alert('Please provide either Entity ID or Custom Variables for preview');
            return;
        }
        
        $scope.loading = true;
        
        EmailService.previewTemplate($scope.currentTemplate.id, $scope.previewData.entityId, $scope.previewData.customVariables)
            .then(function(response) {
                $scope.previewData.subject = response.data.subject;
                $scope.previewData.body = response.data.body;
                $scope.loading = false;
            })
            .catch(function(error) {
                $scope.loading = false;
                alert('Error generating preview: ' + (error.data && error.data.message ? error.data.message : 'Unknown error'));
            });
    };
    
    // Send email using template
    $scope.sendEmailUsingTemplate = function(template) {
        $scope.sendEmailData.templateId = template.id;
        $scope.currentTemplate = template;
        $scope.showSendEmailModal = true;
    };
    
    $scope.sendEmail = function() {
        if (!$scope.sendEmailData.recipientEmail || !$scope.sendEmailData.entityId) {
            alert('Please provide recipient email and entity ID');
            return;
        }
        
        $scope.loading = true;
        
        EmailService.sendEmailUsingTemplate(
            $scope.sendEmailData.templateId,
            $scope.sendEmailData.entityId,
            $scope.sendEmailData.recipientEmail,
            $scope.sendEmailData.recipientName,
            $scope.sendEmailData.sentBy
        ).then(function(response) {
            $scope.closeSendEmailModal();
            $scope.loadEmailLogs(); // Refresh email logs
            $scope.loading = false;
            
            if (response.data.success) {
                alert('Email sent successfully!');
            } else {
                alert('Failed to send email: ' + response.data.message);
            }
        }).catch(function(error) {
            $scope.loading = false;
            alert('Error sending email: ' + (error.data && error.data.message ? error.data.message : 'Unknown error'));
        });
    };
    
    // Email log operations
    $scope.retryFailedEmail = function(emailLog) {
        if (confirm('Are you sure you want to retry sending this email?')) {
            $scope.loading = true;
            
            EmailService.retryFailedEmail(emailLog.id).then(function(response) {
                $scope.loadEmailLogs();
                $scope.loading = false;
                
                if (response.data.success) {
                    alert('Email retry initiated successfully!');
                } else {
                    alert('Failed to retry email: ' + response.data.message);
                }
            }).catch(function(error) {
                $scope.loading = false;
                alert('Error retrying email: ' + (error.data && error.data.message ? error.data.message : 'Unknown error'));
            });
        }
    };
    
    // Search and filter functions
    $scope.searchTemplates = function() {
        if ($scope.templateFilters.searchTerm.length >= 3 || $scope.templateFilters.searchTerm.length === 0) {
            if ($scope.templateFilters.searchTerm) {
                EmailService.searchTemplates($scope.templateFilters.searchTerm).then(function(response) {
                    $scope.templates = response.data;
                });
            } else {
                $scope.loadTemplates();
            }
        }
    };
    
    $scope.searchEmailLogs = function() {
        if ($scope.emailLogFilters.searchTerm.length >= 3 || $scope.emailLogFilters.searchTerm.length === 0) {
            if ($scope.emailLogFilters.searchTerm) {
                EmailService.searchEmailLogs($scope.emailLogFilters.searchTerm).then(function(response) {
                    $scope.emailLogs = response.data;
                });
            } else {
                $scope.loadEmailLogs();
            }
        }
    };
    
    $scope.filterTemplatesByEntityType = function() {
        $scope.loadTemplates();
    };
    
    $scope.filterEmailLogsByStatus = function() {
        $scope.loadEmailLogs();
    };
    
    $scope.filterEmailLogsByEntityType = function() {
        $scope.loadEmailLogs();
    };
    
    // Pagination functions
    $scope.goToTemplatePage = function(page) {
        $scope.loadTemplates(page);
    };
    
    $scope.goToEmailLogPage = function(page) {
        $scope.loadEmailLogs(page);
    };
    
    $scope.changeTemplatePageSize = function() {
        $scope.templatePagination.currentPage = 0;
        $scope.loadTemplates();
    };
    
    $scope.changeEmailLogPageSize = function() {
        $scope.emailLogPagination.currentPage = 0;
        $scope.loadEmailLogs();
    };
    
    // Modal management
    $scope.closeTemplateModal = function() {
        $scope.showTemplateModal = false;
        $scope.currentTemplate = {};
        $scope.isEditing = false;
    };
    
    $scope.closeDeleteModal = function() {
        $scope.showDeleteModal = false;
        $scope.templateToDelete = null;
    };
    
    $scope.closePreviewModal = function() {
        $scope.showPreviewModal = false;
        $scope.previewData = {
            subject: '',
            body: '',
            entityId: '',
            customVariables: {}
        };
    };
    
    $scope.closeSendEmailModal = function() {
        $scope.showSendEmailModal = false;
        $scope.sendEmailData = {
            templateId: null,
            entityId: '',
            recipientEmail: '',
            recipientName: '',
            sentBy: 'Admin'
        };
    };
    
    // Utility functions
    $scope.getEntityTypeDisplayName = function(entityType) {
        var type = $scope.entityTypes.find(function(t) { return t.value === entityType; });
        return type ? type.displayName : entityType;
    };
    
    $scope.getEmailStatusDisplayName = function(status) {
        var statusObj = $scope.emailStatuses.find(function(s) { return s.value === status; });
        return statusObj ? statusObj.displayName : status;
    };
    
    $scope.getStatusBadgeClass = function(status) {
        switch(status) {
            case 'SENT': return 'badge bg-success';
            case 'DELIVERED': return 'badge bg-primary';
            case 'OPENED': return 'badge bg-info';
            case 'FAILED': return 'badge bg-danger';
            case 'PENDING': return 'badge bg-warning';
            case 'BOUNCED': return 'badge bg-dark';
            default: return 'badge bg-secondary';
        }
    };
    
    $scope.formatDateTime = function(dateTimeString) {
        if (!dateTimeString) return 'N/A';
        return new Date(dateTimeString).toLocaleString();
    };
    
    // Initialize the controller
    $scope.init();
}]);