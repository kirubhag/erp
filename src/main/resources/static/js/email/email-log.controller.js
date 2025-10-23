angular.module('erpApp').controller('EmailLogController', ['$scope', 'EmailService', '$location', '$timeout', 
    function($scope, EmailService, $location, $timeout) {

    // Initialize variables
    $scope.emailLogs = [];
    $scope.selectedLog = null;
    $scope.loading = false;
    $scope.error = '';
    
    // Modal states
    $scope.showDetailsModal = false;
    
    // Statistics
    $scope.emailStats = {
        total: 0,
        sent: 0,
        delivered: 0,
        pending: 0,
        failed: 0,
        retrying: 0
    };
    
    // Pagination
    $scope.logPagination = {
        currentPage: 0,
        size: 20,
        totalElements: 0,
        totalPages: 0
    };
    
    // Filters
    $scope.logFilters = {
        searchTerm: '',
        status: '',
        entityType: '',
        dateFrom: '',
        dateTo: ''
    };
    
    // Configuration
    $scope.pageSizeOptions = [10, 20, 50, 100];
    
    $scope.emailStatuses = [
        { value: 'PENDING', displayName: 'Pending' },
        { value: 'SENT', displayName: 'Sent' },
        { value: 'DELIVERED', displayName: 'Delivered' },
        { value: 'FAILED', displayName: 'Failed' },
        { value: 'RETRYING', displayName: 'Retrying' }
    ];
    
    $scope.entityTypes = [
        { value: 'STUDENT', displayName: 'Student' },
        { value: 'TEACHER', displayName: 'Teacher' },
        { value: 'PARENT', displayName: 'Parent' },
        { value: 'ADMIN', displayName: 'Admin' },
        { value: 'COURSE', displayName: 'Course' },
        { value: 'GENERAL', displayName: 'General' }
    ];

    // Utility functions
    $scope.formatDateTime = function(dateString) {
        if (!dateString) return null;
        const date = new Date(dateString);
        return date.toLocaleString();
    };
    
    $scope.getEntityTypeDisplayName = function(entityType) {
        const type = $scope.entityTypes.find(t => t.value === entityType);
        return type ? type.displayName : entityType;
    };
    
    $scope.getStatusDisplayName = function(status) {
        const statusObj = $scope.emailStatuses.find(s => s.value === status);
        return statusObj ? statusObj.displayName : status;
    };
    
    $scope.getStatusBadgeClass = function(status) {
        const statusClasses = {
            'PENDING': 'bg-warning',
            'SENT': 'bg-primary',
            'DELIVERED': 'bg-success',
            'FAILED': 'bg-danger',
            'RETRYING': 'bg-info'
        };
        return statusClasses[status] || 'bg-secondary';
    };

    // Load email logs with pagination and filters
    $scope.loadLogs = function(page) {
        $scope.loading = true;
        $scope.error = '';
        
        if (page !== undefined) {
            $scope.logPagination.currentPage = page;
        }
        
        const params = {
            page: $scope.logPagination.currentPage,
            size: $scope.logPagination.size,
            search: $scope.logFilters.searchTerm || undefined,
            status: $scope.logFilters.status || undefined,
            entityType: $scope.logFilters.entityType || undefined,
            dateFrom: $scope.logFilters.dateFrom || undefined,
            dateTo: $scope.logFilters.dateTo || undefined
        };
        
        // Remove undefined parameters
        Object.keys(params).forEach(key => {
            if (params[key] === undefined) {
                delete params[key];
            }
        });
        
        EmailService.getAllEmailLogs(params)
            .then(function(response) {
                $scope.emailLogs = response.data.content || [];
                $scope.logPagination.totalElements = response.data.totalElements || 0;
                $scope.logPagination.totalPages = response.data.totalPages || 0;
                $scope.loading = false;
            })
            .catch(function(error) {
                $scope.error = 'Failed to load email logs';
                $scope.loading = false;
            });
    };
    
    // Load email statistics
    $scope.loadEmailStatistics = function() {
        EmailService.getEmailStatistics()
            .then(function(response) {
                $scope.emailStats = response.data || {};
            })
            .catch(function(error) {
                // Handle error silently
            });
    };

    // Search functionality
    $scope.searchLogs = function() {
        $scope.logPagination.currentPage = 0;
        $scope.loadLogs();
    };
    
    // Filter functions
    $scope.filterLogsByStatus = function() {
        $scope.logPagination.currentPage = 0;
        $scope.loadLogs();
    };
    
    $scope.filterLogsByEntityType = function() {
        $scope.logPagination.currentPage = 0;
        $scope.loadLogs();
    };
    
    $scope.filterLogsByDate = function() {
        $scope.logPagination.currentPage = 0;
        $scope.loadLogs();
    };
    
    $scope.clearFilters = function() {
        $scope.logFilters = {
            searchTerm: '',
            status: '',
            entityType: '',
            dateFrom: '',
            dateTo: ''
        };
        $scope.logPagination.currentPage = 0;
        $scope.loadLogs();
    };

    // Pagination functions
    $scope.goToLogPage = function(page) {
        if (page >= 0 && page < $scope.logPagination.totalPages) {
            $scope.loadLogs(page);
        }
    };
    
    $scope.changeLogPageSize = function() {
        $scope.logPagination.currentPage = 0;
        $scope.loadLogs();
    };

    // Modal functions
    $scope.viewEmailDetails = function(log) {
        $scope.selectedLog = angular.copy(log);
        $scope.showDetailsModal = true;
    };
    
    $scope.closeDetailsModal = function() {
        $scope.showDetailsModal = false;
        $scope.selectedLog = null;
    };

    // Action functions
    $scope.retryEmail = function(log) {
        if (!log || !log.id) {
            $scope.error = 'Invalid email log';
            return;
        }
        
        $scope.loading = true;
        EmailService.retryEmail(log.id)
            .then(function(response) {
                $scope.loading = false;
                $scope.showSuccess('Email retry initiated successfully');
                $scope.loadLogs(); // Refresh the list
                $scope.closeDetailsModal();
            })
            .catch(function(error) {
                $scope.error = 'Failed to retry email: ' + (error.data?.message || 'Unknown error');
                $scope.loading = false;
            });
    };
    
    $scope.viewEntity = function(log) {
        if (!log.entityType || !log.entityId) {
            $scope.error = 'Entity information not available';
            return;
        }
        
        // Navigate to entity detail page based on entity type
        let entityRoute = '';
        switch (log.entityType) {
            case 'STUDENT':
                entityRoute = '/students/' + log.entityId;
                break;
            case 'TEACHER':
                entityRoute = '/teachers/' + log.entityId;
                break;
            case 'PARENT':
                entityRoute = '/parents/' + log.entityId;
                break;
            case 'COURSE':
                entityRoute = '/courses/' + log.entityId;
                break;
            default:
                $scope.error = 'Unknown entity type: ' + log.entityType;
                return;
        }
        
        $location.path(entityRoute);
    };
    
    $scope.refreshLogs = function() {
        $scope.loadLogs();
        $scope.loadEmailStatistics();
        $scope.showSuccess('Email logs refreshed');
    };
    
    $scope.exportLogs = function() {
        $scope.loading = true;
        
        const params = {
            search: $scope.logFilters.searchTerm || undefined,
            status: $scope.logFilters.status || undefined,
            entityType: $scope.logFilters.entityType || undefined,
            dateFrom: $scope.logFilters.dateFrom || undefined,
            dateTo: $scope.logFilters.dateTo || undefined
        };
        
        // Remove undefined parameters
        Object.keys(params).forEach(key => {
            if (params[key] === undefined) {
                delete params[key];
            }
        });
        
        EmailService.exportEmailLogs(params)
            .then(function(response) {
                // Create and download file
                const blob = new Blob([response.data], { type: 'text/csv' });
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = 'email-logs-' + new Date().toISOString().split('T')[0] + '.csv';
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                window.URL.revokeObjectURL(url);
                
                $scope.loading = false;
                $scope.showSuccess('Email logs exported successfully');
            })
            .catch(function(error) {
                $scope.error = 'Failed to export email logs';
                $scope.loading = false;
            });
    };

    // Utility functions for success/error messages
    $scope.showSuccess = function(message) {
        $scope.successMessage = message;
        $scope.error = '';
        $timeout(function() {
            $scope.successMessage = '';
        }, 3000);
    };
    
    $scope.showError = function(message) {
        $scope.error = message;
        $scope.successMessage = '';
    };

    // Load data for specific entity (if entityType and entityId are provided)
    $scope.loadEntityEmailHistory = function(entityType, entityId) {
        if (!entityType || !entityId) {
            return;
        }
        
        $scope.loading = true;
        EmailService.getEmailsByEntity(entityType, entityId, {
            page: $scope.logPagination.currentPage,
            size: $scope.logPagination.size
        })
        .then(function(response) {
            $scope.emailLogs = response.data.content || [];
            $scope.logPagination.totalElements = response.data.totalElements || 0;
            $scope.logPagination.totalPages = response.data.totalPages || 0;
            $scope.loading = false;
        })
        .catch(function(error) {
            $scope.error = 'Failed to load email history';
            $scope.loading = false;
        });
    };

    // Helper for Angular's range filter (if not already available)
    if (!$scope.range) {
        $scope.range = function(n) {
            return Array.from({length: n}, (v, k) => k);
        };
    }

    // Watch for route parameters to determine if we should load specific entity emails
    $scope.$on('$routeChangeSuccess', function(event, current) {
        const params = current.params;
        if (params && params.entityType && params.entityId) {
            // Load emails for specific entity
            $scope.loadEntityEmailHistory(params.entityType, params.entityId);
        } else {
            // Load all emails
            $scope.loadLogs();
        }
        $scope.loadEmailStatistics();
    });

    // Initialize the controller
    $scope.init = function() {
        // Check if we have route parameters for entity-specific view
        const urlPath = $location.path();
        const entityMatch = urlPath.match(/\/(students|teachers|parents|courses)\/(\d+)\/emails/);
        
        if (entityMatch) {
            const entityTypeMap = {
                'students': 'STUDENT',
                'teachers': 'TEACHER',  
                'parents': 'PARENT',
                'courses': 'COURSE'
            };
            const entityType = entityTypeMap[entityMatch[1]];
            const entityId = parseInt(entityMatch[2]);
            
            if (entityType && entityId) {
                $scope.loadEntityEmailHistory(entityType, entityId);
            }
        } else {
            $scope.loadLogs();
        }
        
        $scope.loadEmailStatistics();
    };
    
    // Initialize when controller loads
    $scope.init();
}]);