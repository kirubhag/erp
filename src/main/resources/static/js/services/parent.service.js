// Parent Service - Handles parent-related API operations
angular.module('erpApp').service('ParentService', [
    'ApiService', '$q',
    function(ApiService, $q) {
        
        const self = this;
        const baseUrl = '/parents';
        
        // Get all parents with pagination
        self.getAllParents = function(page, size, sort) {
            return ApiService.getPage(baseUrl, page, size, sort);
        };
        
        // Search parents
        self.searchParents = function(searchTerm, page, size, sort) {
            return ApiService.search(baseUrl + '/search', searchTerm, page, size, sort);
        };
        
        // Get parent by ID
        self.getParentById = function(id) {
            if (!id) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            return ApiService.get(baseUrl + '/' + id);
        };
        
        // Create new parent
        self.createParent = function(parentData) {
            if (!parentData) {
                return $q.reject({ message: 'Parent data is required' });
            }
            
            const validationError = self.validateParentData(parentData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.post(baseUrl, parentData);
        };
        
        // Update parent
        self.updateParent = function(id, parentData) {
            if (!id) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            if (!parentData) {
                return $q.reject({ message: 'Parent data is required' });
            }
            
            const validationError = self.validateParentData(parentData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.put(baseUrl + '/' + id, parentData);
        };
        
        // Delete parent
        self.deleteParent = function(id) {
            if (!id) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            return ApiService.delete(baseUrl + '/' + id);
        };
        
        // Get parent's children (students)
        self.getParentChildren = function(parentId, page, size) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            return ApiService.getPage(baseUrl + '/' + parentId + '/children', page, size);
        };
        
        // Get students by parent
        self.getStudentsByParent = function(parentId) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            return ApiService.get(baseUrl + '/' + parentId + '/students');
        };
        
        // Get parents by student
        self.getParentsByStudent = function(studentId) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.get('/students/' + studentId + '/parents');
        };
        
        // Associate parent with student
        self.associateParentWithStudent = function(parentId, studentId, relationshipType) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!relationshipType) {
                return $q.reject({ message: 'Relationship type is required' });
            }
            
            const relationshipData = {
                parentId: parentId,
                studentId: studentId,
                relationshipType: relationshipType
            };
            
            return ApiService.post(baseUrl + '/relationships', relationshipData);
        };
        
        // Remove parent-student association
        self.removeParentStudentAssociation = function(parentId, studentId) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            
            return ApiService.delete(baseUrl + '/' + parentId + '/students/' + studentId);
        };
        
        // Update parent-student relationship
        self.updateParentStudentRelationship = function(parentId, studentId, relationshipType) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!relationshipType) {
                return $q.reject({ message: 'Relationship type is required' });
            }
            
            return ApiService.put(baseUrl + '/' + parentId + '/students/' + studentId, {
                relationshipType: relationshipType
            });
        };
        
        // Get parent count
        self.getParentCount = function() {
            return ApiService.get(baseUrl + '/count');
        };
        
        // Get parent portal access information
        self.getParentPortalAccess = function(parentId) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            return ApiService.get(baseUrl + '/' + parentId + '/portal-access');
        };
        
        // Enable/disable parent portal access
        self.updateParentPortalAccess = function(parentId, enabled) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            
            return ApiService.patch(baseUrl + '/' + parentId + '/portal-access', {
                enabled: enabled
            });
        };
        
        // Reset parent portal password
        self.resetParentPortalPassword = function(parentId) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            return ApiService.post(baseUrl + '/' + parentId + '/reset-password');
        };
        
        // Get parent communication preferences
        self.getParentCommunicationPreferences = function(parentId) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            return ApiService.get(baseUrl + '/' + parentId + '/communication-preferences');
        };
        
        // Update parent communication preferences
        self.updateParentCommunicationPreferences = function(parentId, preferences) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            if (!preferences) {
                return $q.reject({ message: 'Preferences are required' });
            }
            
            return ApiService.put(baseUrl + '/' + parentId + '/communication-preferences', preferences);
        };
        
        // Bulk operations
        self.bulkUpdatePortalAccess = function(parentIds, enabled) {
            if (!parentIds || !parentIds.length) {
                return $q.reject({ message: 'Parent IDs are required' });
            }
            
            return ApiService.post(baseUrl + '/bulk/portal-access', {
                parentIds: parentIds,
                enabled: enabled
            });
        };
        
        self.bulkDelete = function(parentIds) {
            if (!parentIds || !parentIds.length) {
                return $q.reject({ message: 'Parent IDs are required' });
            }
            
            return ApiService.post(baseUrl + '/bulk/delete', { parentIds: parentIds });
        };
        
        // Export operations
        self.exportToCsv = function(filters) {
            let url = baseUrl + '/export/csv';
            if (filters) {
                const queryString = ApiService.buildQueryString(filters);
                if (queryString) {
                    url += '?' + queryString;
                }
            }
            return ApiService.exportToCsv(url, 'parents_export.csv');
        };
        
        self.exportToPdf = function(filters) {
            let url = baseUrl + '/export/pdf';
            if (filters) {
                const queryString = ApiService.buildQueryString(filters);
                if (queryString) {
                    url += '?' + queryString;
                }
            }
            return ApiService.exportToPdf(url, 'parents_report.pdf');
        };
        
        // Validation functions
        self.validateParentData = function(parentData) {
            if (!parentData.firstName || parentData.firstName.trim() === '') {
                return 'First name is required';
            }
            
            if (!parentData.lastName || parentData.lastName.trim() === '') {
                return 'Last name is required';
            }
            
            if (!parentData.email || parentData.email.trim() === '') {
                return 'Email is required';
            }
            
            if (!self.isValidEmail(parentData.email)) {
                return 'Please enter a valid email address';
            }
            
            if (!parentData.phoneNumber || parentData.phoneNumber.trim() === '') {
                return 'Phone number is required';
            }
            
            if (!self.isValidPhoneNumber(parentData.phoneNumber)) {
                return 'Please enter a valid phone number';
            }
            
            return null; // No validation errors
        };
        
        // Utility validation functions
        self.isValidEmail = function(email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(email);
        };
        
        self.isValidPhoneNumber = function(phone) {
            const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
            return phoneRegex.test(phone.replace(/[\s\-\(\)\.]/g, ''));
        };
        
        // Helper functions for UI
        self.getRelationshipDisplayName = function(relationshipType) {
            const relationshipMap = {
                'FATHER': 'Father',
                'MOTHER': 'Mother',
                'GUARDIAN': 'Guardian',
                'STEP_FATHER': 'Step Father',
                'STEP_MOTHER': 'Step Mother',
                'GRANDFATHER': 'Grandfather',
                'GRANDMOTHER': 'Grandmother',
                'OTHER': 'Other'
            };
            return relationshipMap[relationshipType] || relationshipType;
        };
        
        self.getRelationshipOptions = function() {
            return [
                { value: 'FATHER', label: 'Father' },
                { value: 'MOTHER', label: 'Mother' },
                { value: 'GUARDIAN', label: 'Guardian' },
                { value: 'STEP_FATHER', label: 'Step Father' },
                { value: 'STEP_MOTHER', label: 'Step Mother' },
                { value: 'GRANDFATHER', label: 'Grandfather' },
                { value: 'GRANDMOTHER', label: 'Grandmother' },
                { value: 'OTHER', label: 'Other' }
            ];
        };
        
        // Format parent name
        self.formatParentName = function(parent) {
            if (!parent) return '';
            
            let name = '';
            if (parent.firstName) name += parent.firstName;
            if (parent.lastName) name += ' ' + parent.lastName;
            
            return name.trim();
        };
        
        // Create empty parent object with default values
        self.createEmptyParent = function() {
            return {
                firstName: '',
                lastName: '',
                email: '',
                phoneNumber: '',
                alternatePhoneNumber: '',
                address: {
                    street: '',
                    city: '',
                    state: '',
                    zipCode: '',
                    country: 'USA'
                },
                occupation: '',
                employer: '',
                portalAccessEnabled: true,
                communicationPreferences: {
                    emailNotifications: true,
                    smsNotifications: false,
                    attendanceAlerts: true,
                    gradeAlerts: true,
                    eventNotifications: true
                }
            };
        };
        
        // Create empty parent-student relationship
        self.createEmptyRelationship = function() {
            return {
                parentId: null,
                studentId: null,
                relationshipType: 'FATHER',
                isPrimaryContact: false,
                canPickup: true,
                emergencyContact: true
            };
        };
        
        // Get parent dashboard data
        self.getParentDashboardData = function(parentId) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            return ApiService.get(baseUrl + '/' + parentId + '/dashboard');
        };
        
        // Get child attendance summary for parent
        self.getChildAttendanceSummary = function(parentId, studentId, dateRange) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            
            const params = {};
            if (dateRange && dateRange.startDate && dateRange.endDate) {
                params.startDate = dateRange.startDate;
                params.endDate = dateRange.endDate;
            }
            
            return ApiService.get(baseUrl + '/' + parentId + '/students/' + studentId + '/attendance', params);
        };
        
        // Send notification to parent
        self.sendNotificationToParent = function(parentId, notification) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            if (!notification) {
                return $q.reject({ message: 'Notification data is required' });
            }
            
            return ApiService.post(baseUrl + '/' + parentId + '/notifications', notification);
        };
        
        // Get parent notifications
        self.getParentNotifications = function(parentId, page, size) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            return ApiService.getPage(baseUrl + '/' + parentId + '/notifications', page, size);
        };
        
        // Mark notification as read
        self.markNotificationAsRead = function(parentId, notificationId) {
            if (!parentId) {
                return $q.reject({ message: 'Parent ID is required' });
            }
            if (!notificationId) {
                return $q.reject({ message: 'Notification ID is required' });
            }
            
            return ApiService.patch(baseUrl + '/' + parentId + '/notifications/' + notificationId + '/read');
        };
        
        return self;
    }
]);