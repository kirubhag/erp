// Generic Entity Edit Controller - v1.0
// Handles edit/create for any entity type (students, parents, health, etc.)
(function() {
    'use strict';

    angular.module('erpApp')
        .controller('EntityEditController', EntityEditController);

    EntityEditController.$inject = [
        '$scope', 
        '$routeParams',
        '$location',
        '$timeout',
        'EntityDataService',
        'EntityConfigService',
        'NotificationService'
    ];

    function EntityEditController($scope, $routeParams, $location, $timeout, EntityDataService, EntityConfigService, NotificationService) {
        
        // Initialize controller
        init();

        function init() {
            $scope.loading = true;
            $scope.saving = false;
            $scope.item = {};
            $scope.entity = $scope.item; // Alias for templates that use 'entity'
            $scope.originalItem = {};
            $scope.hasChanges = false;
            $scope.isNew = false;
            
            // Get entity type from route or config
            $scope.entityType = $routeParams.entityType || getEntityTypeFromPath();
            $scope.config = EntityConfigService.getConfig($scope.entityType);
            
            if (!$scope.config) {
                console.error('No configuration found for entity type:', $scope.entityType);
                NotificationService.error('Invalid entity type');
                $location.path('/');
                return;
            }
            
            // Grade levels (for student/academic entities)
            $scope.gradeLevels = [
                'KINDERGARTEN',
                'FIRST_GRADE', 
                'SECOND_GRADE',
                'THIRD_GRADE',
                'FOURTH_GRADE',
                'FIFTH_GRADE',
                'SIXTH_GRADE',
                'SEVENTH_GRADE',
                'EIGHTH_GRADE',
                'NINTH_GRADE',
                'TENTH_GRADE',
                'ELEVENTH_GRADE',
                'TWELFTH_GRADE'
            ];

            loadItem();
            setupWatchers();
            setupBeforeUnload();
        }

        /**
         * Get entity type from current path
         */
        function getEntityTypeFromPath() {
            var path = $location.path();
            var pathParts = path.split('/');
            
            // Expected path: /students/:id/edit
            if (pathParts.length >= 2) {
                var entityPlural = pathParts[1];
                
                var entityMap = {
                    'students': 'STUDENT',
                    'parents': 'PARENT',
                    'subjects': 'SUBJECT',
                    'timetables': 'TIMETABLE',
                    'health': 'HEALTH',
                    'staff': 'STAFF',
                    'users': 'USER',
                    'attendance': 'ATTENDANCE',
                    'grades': 'GRADE',
                    'assignments': 'ASSIGNMENT',
                    'exams': 'EXAM'
                };
                
                return entityMap[entityPlural] || entityPlural.toUpperCase().replace(/S$/, '');
            }
            
            return null;
        }

        /**
         * Load item data for editing
         */
        function loadItem() {
            var itemId = $routeParams.id;
            
            // Check if this is a new item creation
            if (itemId === 'new' || !itemId) {
                $scope.item = createEmptyItem();
                $scope.entity = $scope.item; // Keep alias in sync
                $scope.originalItem = angular.copy($scope.item);
                $scope.isNew = true;
                $scope.isEditMode = false; // Alias for templates
                $scope.loading = false;
                return;
            }

            EntityDataService.getItem($scope.entityType, itemId)
                .then(function(response) {
                    // Use $timeout to ensure proper digest cycle for date inputs
                    $timeout(function() {
                        $scope.item = angular.copy(response);
                        $scope.entity = $scope.item; // Keep alias in sync
                        $scope.originalItem = angular.copy(response);
                        $scope.isEditMode = true; // Alias for templates
                        
                        // Transform flat backend structure to nested frontend structure
                        if ($scope.entityType === 'STUDENT' || $scope.entityType === 'PARENT') {
                            $scope.item.address = {
                                street: $scope.item.addressLine1 || '',
                                city: $scope.item.city || '',
                                state: $scope.item.state || '',
                                zipCode: $scope.item.postalCode || ''
                            };
                            
                            $scope.item.emergencyContact = {
                                name: $scope.item.emergencyContactName || '',
                                phoneNumber: $scope.item.emergencyContactPhone || '',
                                relationship: $scope.item.emergencyContactRelation || ''
                            };
                        }
                        
                        // Convert dates to proper format for HTML5 date input fields (YYYY-MM-DD)
                        convertDatesToInputFormat($scope.item);
                        
                        console.log($scope.config.entityName + ' data loaded for editing:', $scope.item);
                        
                        $scope.loading = false;
                    });
                })
                .catch(function(error) {
                    console.error('Error loading ' + $scope.config.entityName + ':', error);
                    NotificationService.error('Failed to load ' + $scope.config.entityName.toLowerCase() + ' for editing');
                    $scope.loading = false;
                    $location.path('/' + $scope.entityType.toLowerCase() + 's');
                });
        }

        /**
         * Convert date fields to YYYY-MM-DD format for HTML5 inputs
         */
        function convertDatesToInputFormat(item) {
            var dateFields = ['dateOfBirth', 'enrollmentDate', 'hireDate', 'recordDate', 'date', 'examDate', 'assignedDate', 'dueDate'];
            
            dateFields.forEach(function(field) {
                if (item[field]) {
                    // Check if it's already in correct format (YYYY-MM-DD)
                    if (typeof item[field] === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(item[field])) {
                        // Already in correct format
                    } else {
                        item[field] = new Date(item[field]).toISOString().split('T')[0];
                    }
                }
            });
        }

        /**
         * Set up watchers to detect changes
         */
        function setupWatchers() {
            $scope.$watch('item', function(newVal, oldVal) {
                if (newVal && oldVal && !angular.equals(newVal, $scope.originalItem)) {
                    $scope.hasChanges = true;
                } else {
                    $scope.hasChanges = false;
                }
            }, true);
        }

        /**
         * Save item changes
         */
        $scope.saveItem = function() {
            // Check form validity - supports both 'itemForm' and entity-specific forms like 'timetableForm'
            var form = $scope.itemForm || $scope.timetableForm || $scope[Object.keys($scope).find(key => key.endsWith('Form'))];
            
            if ($scope.saving || (form && !form.$valid)) {
                return;
            }

            $scope.saving = true;

            // Prepare data for API
            var itemData = angular.copy($scope.item);
            
            // Flatten nested objects to match backend structure
            if ($scope.entityType === 'STUDENT' || $scope.entityType === 'PARENT') {
                if (itemData.address) {
                    itemData.addressLine1 = itemData.address.street;
                    itemData.city = itemData.address.city;
                    itemData.state = itemData.address.state;
                    itemData.postalCode = itemData.address.zipCode;
                    delete itemData.address;
                }
                
                if (itemData.emergencyContact) {
                    itemData.emergencyContactName = itemData.emergencyContact.name;
                    itemData.emergencyContactPhone = itemData.emergencyContact.phoneNumber;
                    itemData.emergencyContactRelation = itemData.emergencyContact.relationship;
                    delete itemData.emergencyContact;
                }
            }
            
            // Convert date strings to proper format
            convertDatesToISOFormat(itemData);

            var apiCall;
            if ($scope.isNew) {
                // Remove ID for new item creation
                delete itemData.id;
                apiCall = EntityDataService.createItem($scope.entityType, itemData);
            } else {
                apiCall = EntityDataService.updateItem($scope.entityType, itemData.id, itemData);
            }

            apiCall
                .then(function(response) {
                    var successMessage = $scope.isNew ? 
                        $scope.config.entityName + ' created successfully' : 
                        $scope.config.entityName + ' updated successfully';
                    NotificationService.success(successMessage);
                    $scope.originalItem = angular.copy($scope.item);
                    $scope.hasChanges = false;
                    
                    // Navigate back to detail page or list
                    if ($scope.isNew) {
                        $location.path('/' + $scope.entityType.toLowerCase() + 's/' + response.id);
                    } else {
                        $location.path('/' + $scope.entityType.toLowerCase() + 's/' + itemData.id);
                    }
                })
                .catch(function(error) {
                    console.error('Error saving ' + $scope.config.entityName + ':', error);
                    
                    // Handle validation errors from backend
                    if (error.status === 400 && error.data && error.data.errors) {
                        // Parse validation errors
                        var validationErrors = [];
                        error.data.errors.forEach(function(fieldError) {
                            var fieldName = fieldError.field;
                            var message = fieldError.defaultMessage || fieldError.message;
                            
                            validationErrors.push(fieldName + ': ' + message);
                            
                            // Set field-level error in form if possible
                            if ($scope.itemForm && $scope.itemForm[fieldName]) {
                                $scope.itemForm[fieldName].$setValidity('server', false);
                                $scope.itemForm[fieldName].$error.serverMessage = message;
                            }
                        });
                        
                        var errorMessage = 'Validation failed:\n' + validationErrors.join('\n');
                        NotificationService.error(errorMessage);
                    } else {
                        // Generic error handling
                        var errorMessage = 'Failed to save ' + $scope.config.entityName.toLowerCase();
                        if (error.data && error.data.message) {
                            errorMessage += ': ' + error.data.message;
                        } else if (error.data && typeof error.data === 'string') {
                            errorMessage += ': ' + error.data;
                        } else if (error.statusText) {
                            errorMessage += ': ' + error.statusText;
                        }
                        NotificationService.error(errorMessage);
                    }
                })
                .finally(function() {
                    $scope.saving = false;
                });
        };

        // Alias for templates that use 'saveEntity' instead of 'saveItem'
        $scope.saveEntity = $scope.saveItem;

        /**
         * Convert date fields to ISO format for API
         */
        function convertDatesToISOFormat(item) {
            var dateFields = ['dateOfBirth', 'enrollmentDate', 'hireDate', 'recordDate', 'date', 'examDate', 'assignedDate', 'dueDate'];
            
            dateFields.forEach(function(field) {
                if (item[field]) {
                    item[field] = new Date(item[field]).toISOString().split('T')[0];
                }
            });
        }

        /**
         * Navigation functions
         */
        $scope.goBack = function() {
            if ($scope.hasChanges) {
                var confirmMessage = 'You have unsaved changes. Are you sure you want to leave?';
                if (!confirm(confirmMessage)) {
                    return;
                }
            }
            
            if ($scope.isNew) {
                $location.path('/' + $scope.entityType.toLowerCase() + 's');
            } else {
                $location.path('/' + $scope.entityType.toLowerCase() + 's/' + $scope.item.id);
            }
        };

        $scope.cancelEdit = function() {
            $scope.goBack();
        };

        /**
         * Utility functions
         */
        $scope.getAvatarInitials = function(item) {
            if (!item) return '?';
            
            var firstName = item.firstName || item.name || '';
            var lastName = item.lastName || '';
            
            if (!firstName) return '?';
            
            var initials = firstName.charAt(0).toUpperCase();
            if (lastName) {
                initials += lastName.charAt(0).toUpperCase();
            }
            return initials;
        };

        $scope.getAvatarStyle = function(item) {
            // Generate a consistent color based on the name
            if (!item || (!item.firstName && !item.name)) {
                return { 'background': 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' };
            }
            
            var colors = [
                'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
                'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
                'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
                'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
                'linear-gradient(135deg, #ff8a80 0%, #ea6100 100%)'
            ];
            
            var name = (item.firstName || item.name || '') + (item.lastName || '');
            var nameHash = name.split('').reduce((a, b) => {
                a = ((a << 5) - a) + b.charCodeAt(0);
                return a & a;
            }, 0);
            
            var colorIndex = Math.abs(nameHash) % colors.length;
            return { 'background': colors[colorIndex] };
        };

        $scope.formatGradeName = function(grade) {
            if (!grade) return '';
            
            var gradeMap = {
                'KINDERGARTEN': 'Kindergarten',
                'FIRST_GRADE': '1st Grade',
                'SECOND_GRADE': '2nd Grade',
                'THIRD_GRADE': '3rd Grade',
                'FOURTH_GRADE': '4th Grade',
                'FIFTH_GRADE': '5th Grade',
                'SIXTH_GRADE': '6th Grade',
                'SEVENTH_GRADE': '7th Grade',
                'EIGHTH_GRADE': '8th Grade',
                'NINTH_GRADE': '9th Grade',
                'TENTH_GRADE': '10th Grade',
                'ELEVENTH_GRADE': '11th Grade',
                'TWELFTH_GRADE': '12th Grade'
            };
            
            return gradeMap[grade] || grade.replace(/_/g, ' ').toLowerCase()
                .replace(/\b\w/g, l => l.toUpperCase());
        };

        /**
         * Setup beforeunload handler to warn about unsaved changes
         */
        function setupBeforeUnload() {
            var beforeUnloadHandler = function(e) {
                if ($scope.hasChanges) {
                    var confirmationMessage = 'You have unsaved changes. Are you sure you want to leave?';
                    e.returnValue = confirmationMessage;
                    return confirmationMessage;
                }
            };
            
            window.addEventListener('beforeunload', beforeUnloadHandler);
            
            $scope.$on('$destroy', function() {
                window.removeEventListener('beforeunload', beforeUnloadHandler);
            });
        }

        /**
         * Create empty item object for new item creation
         */
        function createEmptyItem() {
            var item = {
                id: null,
                createdAt: null,
                updatedAt: null
            };
            
            // Entity-specific defaults
            if ($scope.entityType === 'STUDENT') {
                item = {
                    studentId: '',
                    firstName: '',
                    lastName: '',
                    middleName: '',
                    dateOfBirth: null,
                    gender: '',
                    gradeLevel: '',
                    enrollmentStatus: 'ENROLLED',
                    enrollmentDate: new Date().toISOString().split('T')[0],
                    email: '',
                    phoneNumber: '',
                    address: {
                        street: '',
                        city: '',
                        state: '',
                        zipCode: ''
                    },
                    emergencyContact: {
                        name: '',
                        phoneNumber: '',
                        relationship: ''
                    },
                    notes: ''
                };
            } else if ($scope.entityType === 'PARENT') {
                item = {
                    firstName: '',
                    lastName: '',
                    email: '',
                    phoneNumber: '',
                    relationship: '',
                    address: {
                        street: '',
                        city: '',
                        state: '',
                        zipCode: ''
                    },
                    emergencyContact: {
                        name: '',
                        phoneNumber: '',
                        relationship: ''
                    }
                };
            } else if ($scope.entityType === 'HEALTH') {
                item = {
                    studentId: null,
                    recordDate: new Date().toISOString().split('T')[0],
                    height: null,
                    weight: null,
                    bloodGroup: '',
                    allergies: '',
                    medicalConditions: '',
                    medications: '',
                    emergencyContact: '',
                    emergencyPhone: ''
                };
            } else if ($scope.entityType === 'TIMETABLE') {
                item = {
                    timetableCode: '',
                    className: '',
                    gradeLevel: '',
                    academicYear: '',
                    semester: '',
                    dayOfWeek: '',
                    startTime: '',
                    endTime: '',
                    periodNumber: null,
                    subjectName: '',
                    subjectCode: '',
                    teacherName: '',
                    teacherId: '',
                    isLabSession: false,
                    roomNumber: '',
                    building: '',
                    notes: ''
                };
            }
            
            return item;
        }
    }
})();
