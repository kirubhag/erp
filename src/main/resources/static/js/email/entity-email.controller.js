angular.module('erpApp').controller('EntityEmailController', ['$scope', 'EmailService', '$timeout', '$routeParams',
    function($scope, EmailService, $timeout, $routeParams) {

    // Initialize variables
    $scope.entityEmails = [];
    $scope.selectedEmail = null;
    $scope.availableTemplates = [];
    $scope.selectedTemplate = null;
    $scope.loading = false;
    $scope.loadingMore = false;
    $scope.sendingEmail = false;
    $scope.error = '';
    $scope.successMessage = '';
    $scope.emailCount = 0;
    
    // Modal states
    $scope.showSendModal = false;
    $scope.showDetailsModal = false;
    
    // Entity information (passed from parent controller)
    $scope.entityType = '';
    $scope.entityId = null;
    $scope.entityDisplayName = '';
    $scope.entityEmail = '';
    
    // Pagination
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.hasMoreEmails = false;
    
    // Email statistics for this entity
    $scope.emailStats = {
        sent: 0,
        delivered: 0,
        pending: 0,
        failed: 0
    };
    
    // Email form
    $scope.emailForm = {
        useTemplate: true,
        templateId: '',
        recipientEmail: '',
        subject: '',
        body: ''
    };
    
    // Email status configurations
    $scope.emailStatuses = [
        { value: 'PENDING', displayName: 'Pending' },
        { value: 'SENT', displayName: 'Sent' },
        { value: 'DELIVERED', displayName: 'Delivered' },
        { value: 'FAILED', displayName: 'Failed' },
        { value: 'RETRYING', displayName: 'Retrying' }
    ];

    // Utility functions
    $scope.formatDateTime = function(dateString) {
        if (!dateString) return null;
        const date = new Date(dateString);
        return date.toLocaleString();
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

    // Initialize entity email controller with entity information
    $scope.initEntityEmails = function(entityType, entityId, entityDisplayName, entityEmail) {
        $scope.entityType = entityType;
        $scope.entityId = entityId;
        $scope.entityDisplayName = entityDisplayName || entityType;
        $scope.entityEmail = entityEmail || '';
        
        // Set default recipient email
        $scope.emailForm.recipientEmail = entityEmail || '';
        
        $scope.loadEntityEmails();
        $scope.loadAvailableTemplates();
        $scope.loadEntityEmailStats();
    };

    // Load emails for the specific entity
    $scope.loadEntityEmails = function(append) {
        if (!$scope.entityType || !$scope.entityId) {
            return;
        }
        
        if (!append) {
            $scope.loading = true;
            $scope.currentPage = 0;
            $scope.entityEmails = [];
        } else {
            $scope.loadingMore = true;
        }
        
        const params = {
            page: $scope.currentPage,
            size: $scope.pageSize
        };
        
        EmailService.getEmailsByEntity($scope.entityType, $scope.entityId, params)
            .then(function(response) {
                const newEmails = response.data.content || [];
                if (append) {
                    $scope.entityEmails = $scope.entityEmails.concat(newEmails);
                } else {
                    $scope.entityEmails = newEmails;
                }
                
                $scope.emailCount = response.data.totalElements || 0;
                $scope.hasMoreEmails = $scope.entityEmails.length < $scope.emailCount;
                
                $scope.loading = false;
                $scope.loadingMore = false;
            })
            .catch(function(error) {
                console.error('Error loading entity emails:', error);
                $scope.error = 'Failed to load emails';
                $scope.loading = false;
                $scope.loadingMore = false;
            });
    };
    
    // Load more emails (pagination)
    $scope.loadMoreEmails = function() {
        $scope.currentPage++;
        $scope.loadEntityEmails(true);
    };
    
    // Load available email templates for this entity type
    $scope.loadAvailableTemplates = function() {
        if (!$scope.entityType) {
            return;
        }
        
        EmailService.getTemplatesByEntityType($scope.entityType)
            .then(function(response) {
                $scope.availableTemplates = response.data.filter(template => template.isActive);
            })
            .catch(function(error) {
                console.error('Error loading templates:', error);
            });
    };
    
    // Load email statistics for this entity
    $scope.loadEntityEmailStats = function() {
        if (!$scope.entityType || !$scope.entityId) {
            return;
        }
        
        EmailService.getEntityEmailStatistics($scope.entityType, $scope.entityId)
            .then(function(response) {
                $scope.emailStats = response.data || {};
            })
            .catch(function(error) {
                console.error('Error loading email statistics:', error);
            });
    };

    // Modal functions
    $scope.showSendEmailModal = function() {
        $scope.resetEmailForm();
        $scope.showSendModal = true;
    };
    
    $scope.closeSendEmailModal = function() {
        $scope.showSendModal = false;
        $scope.resetEmailForm();
    };
    
    $scope.viewEmailDetails = function(email) {
        $scope.selectedEmail = angular.copy(email);
        $scope.showDetailsModal = true;
    };
    
    $scope.closeDetailsModal = function() {
        $scope.showDetailsModal = false;
        $scope.selectedEmail = null;
    };
    
    // Reset email form
    $scope.resetEmailForm = function() {
        $scope.emailForm = {
            useTemplate: true,
            templateId: '',
            recipientEmail: $scope.entityEmail || '',
            subject: '',
            body: ''
        };
        $scope.selectedTemplate = null;
    };
    
    // Handle template selection
    $scope.onTemplateSelect = function() {
        if (!$scope.emailForm.templateId) {
            $scope.selectedTemplate = null;
            $scope.emailForm.subject = '';
            $scope.emailForm.body = '';
            return;
        }
        
        $scope.selectedTemplate = $scope.availableTemplates.find(t => t.id == $scope.emailForm.templateId);
        if ($scope.selectedTemplate) {
            // Preview the template with entity data
            EmailService.previewTemplate($scope.emailForm.templateId, $scope.entityType, $scope.entityId)
                .then(function(response) {
                    $scope.emailForm.subject = response.data.subject || $scope.selectedTemplate.subject;
                    $scope.emailForm.body = response.data.body || $scope.selectedTemplate.body;
                })
                .catch(function(error) {
                    // Fallback to template without variable replacement
                    $scope.emailForm.subject = $scope.selectedTemplate.subject;
                    $scope.emailForm.body = $scope.selectedTemplate.body;
                });
        }
    };

    // Send email
    $scope.sendEmail = function() {
        if (!$scope.emailForm.recipientEmail || !$scope.emailForm.subject || !$scope.emailForm.body) {
            $scope.error = 'Please fill in all required fields';
            return;
        }
        
        $scope.sendingEmail = true;
        $scope.error = '';
        
        const emailData = {
            recipientEmail: $scope.emailForm.recipientEmail,
            recipientName: $scope.entityDisplayName,
            subject: $scope.emailForm.subject,
            body: $scope.emailForm.body,
            entityType: $scope.entityType,
            entityId: $scope.entityId
        };
        
        let sendPromise;
        if ($scope.emailForm.useTemplate && $scope.emailForm.templateId) {
            // Send using template
            sendPromise = EmailService.sendEmailUsingTemplate(
                $scope.emailForm.templateId,
                $scope.entityType,
                $scope.entityId,
                $scope.emailForm.recipientEmail
            );
        } else {
            // Send direct email
            sendPromise = EmailService.sendDirectEmail(emailData);
        }
        
        sendPromise
            .then(function(response) {
                $scope.sendingEmail = false;
                $scope.closeSendEmailModal();
                $scope.showSuccess('Email sent successfully');
                $scope.refreshEmails();
            })
            .catch(function(error) {
                console.error('Error sending email:', error);
                $scope.error = 'Failed to send email: ' + (error.data?.message || 'Unknown error');
                $scope.sendingEmail = false;
            });
    };

    // Action functions
    $scope.retryEmail = function(email) {
        if (!email || !email.id) {
            $scope.error = 'Invalid email';
            return;
        }
        
        EmailService.retryEmail(email.id)
            .then(function(response) {
                $scope.showSuccess('Email retry initiated');
                $scope.refreshEmails();
                $scope.closeDetailsModal();
            })
            .catch(function(error) {
                console.error('Error retrying email:', error);
                $scope.error = 'Failed to retry email';
            });
    };
    
    $scope.refreshEmails = function() {
        $scope.loadEntityEmails();
        $scope.loadEntityEmailStats();
        $scope.showSuccess('Emails refreshed');
    };

    // Utility functions for messages
    $scope.showSuccess = function(message) {
        $scope.successMessage = message;
        $scope.error = '';
        $timeout(function() {
            $scope.successMessage = '';
        }, 3000);
    };

    // Watch for changes in entity information from parent scope
    $scope.$watch('$parent.student', function(newStudent) {
        if (newStudent && newStudent.id) {
            $scope.initEntityEmails('STUDENT', newStudent.id, newStudent.fullName, newStudent.email);
        }
    });
    
    $scope.$watch('$parent.teacher', function(newTeacher) {
        if (newTeacher && newTeacher.id) {
            $scope.initEntityEmails('TEACHER', newTeacher.id, newTeacher.fullName, newTeacher.email);
        }
    });
    
    $scope.$watch('$parent.parent', function(newParent) {
        if (newParent && newParent.id) {
            $scope.initEntityEmails('PARENT', newParent.id, newParent.fullName, newParent.email);
        }
    });
    
    $scope.$watch('$parent.course', function(newCourse) {
        if (newCourse && newCourse.id) {
            $scope.initEntityEmails('COURSE', newCourse.id, newCourse.courseName, '');
        }
    });

    // Initialize from route parameters if available
    $scope.initFromRoute = function() {
        const entityType = $routeParams.entityType;
        const entityId = $routeParams.entityId;
        
        if (entityType && entityId) {
            const typeMap = {
                'students': 'STUDENT',
                'teachers': 'TEACHER',
                'parents': 'PARENT', 
                'courses': 'COURSE'
            };
            
            const mappedType = typeMap[entityType];
            if (mappedType) {
                $scope.initEntityEmails(mappedType, parseInt(entityId), mappedType.toLowerCase(), '');
            }
        }
    };
    
    // Initialize on controller load
    $scope.initFromRoute();
}]);