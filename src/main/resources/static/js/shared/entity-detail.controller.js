// Generic Entity Detail Controller - v1.0
// Handles detail view for any entity type (students, parents, health, etc.)
(function() {
    'use strict';

    angular.module('erpApp')
        .controller('EntityDetailController', EntityDetailController);

    EntityDetailController.$inject = [
        '$scope', 
        '$routeParams',
        '$location', 
        'EntityDataService',
        'EntityConfigService',
        'NotificationService'
    ];

    function EntityDetailController($scope, $routeParams, $location, EntityDataService, EntityConfigService, NotificationService) {
        
        // Initialize controller
        init();

        function init() {
            $scope.loading = true;
            $scope.item = {};
            $scope.activities = [];
            $scope.notes = [];
            $scope.attachments = [];
            $scope.courses = [];
            
            // Get entity type from route or config
            $scope.entityType = $routeParams.entityType || getEntityTypeFromPath();
            $scope.config = EntityConfigService.getConfig($scope.entityType);
            
            if (!$scope.config) {
                console.error('No configuration found for entity type:', $scope.entityType);
                NotificationService.error('Invalid entity type');
                $location.path('/');
                return;
            }
            
            loadItem();
            loadRelatedData();
        }

        /**
         * Get entity type from current path
         */
        function getEntityTypeFromPath() {
            var path = $location.path();
            var pathParts = path.split('/');
            
            // Expected path: /students/:id or /parents/:id
            if (pathParts.length >= 2) {
                var entityPlural = pathParts[1]; // e.g., 'students'
                
                // Convert plural to singular and uppercase
                var entityMap = {
                    'students': 'STUDENT',
                    'parents': 'PARENT',
                    'subjects': 'SUBJECT',
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
         * Load item data
         */
        function loadItem() {
            var itemId = $routeParams.id;
            
            if (!itemId) {
                NotificationService.error($scope.config.entityName + ' ID is required');
                $location.path('/' + $scope.entityType.toLowerCase() + 's');
                return;
            }

            EntityDataService.getItem($scope.entityType, itemId)
                .then(function(response) {
                    $scope.item = response;
                    
                    // Ensure nested objects exist for entities that have them
                    if ($scope.entityType === 'STUDENT' || $scope.entityType === 'PARENT') {
                        if (!$scope.item.address) {
                            $scope.item.address = {};
                        }
                        if (!$scope.item.emergencyContact) {
                            $scope.item.emergencyContact = {};
                        }
                    }
                    
                    $scope.loading = false;
                })
                .catch(function(error) {
                    console.error('Error loading ' + $scope.config.entityName + ':', error);
                    NotificationService.error('Failed to load ' + $scope.config.entityName.toLowerCase() + ' details');
                    $scope.loading = false;
                    $location.path('/' + $scope.entityType.toLowerCase() + 's');
                });
        }

        /**
         * Load related data (activities, notes, etc.)
         */
        function loadRelatedData() {
            // Mock data for now - can be replaced with actual API calls per entity
            $scope.activities = [
                {
                    id: 1,
                    title: $scope.config.entityName + ' Created',
                    description: $scope.config.entityName + ' was created in the system',
                    date: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
                    icon: 'plus-circle'
                },
                {
                    id: 2,
                    title: 'Information Updated',
                    description: 'Record information was updated',
                    date: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
                    icon: 'edit'
                }
            ];

            $scope.notes = [];
            $scope.attachments = [];
        }

        /**
         * Navigation functions
         */
        $scope.goBack = function() {
            $location.path('/' + $scope.entityType.toLowerCase() + 's');
        };

        $scope.editItem = function() {
            $location.path('/' + $scope.entityType.toLowerCase() + 's/' + $scope.item.id + '/edit');
        };

        /**
         * Action functions
         */
        $scope.duplicateItem = function() {
            NotificationService.info('Duplicate ' + $scope.config.entityName.toLowerCase() + ' functionality coming soon');
        };

        $scope.exportItem = function() {
            NotificationService.info('Export ' + $scope.config.entityName.toLowerCase() + ' functionality coming soon');
        };

        $scope.deleteItem = function() {
            $scope.itemToDelete = $scope.item;
            $scope.showDeleteModal = true;
        };

        $scope.confirmDelete = function() {
            console.log('Delete confirmed for ' + $scope.config.entityName + ':', $scope.itemToDelete);
            $scope.showDeleteModal = false;
            
            EntityDataService.deleteItem($scope.entityType, $scope.itemToDelete.id)
                .then(function() {
                    console.log($scope.config.entityName + ' deleted successfully');
                    NotificationService.success($scope.config.entityName + ' deleted successfully');
                    $location.path('/' + $scope.entityType.toLowerCase() + 's');
                })
                .catch(function(error) {
                    console.error('Error deleting ' + $scope.config.entityName + ':', error);
                    NotificationService.error('Failed to delete ' + $scope.config.entityName.toLowerCase());
                });
        };

        $scope.cancelDelete = function() {
            $scope.showDeleteModal = false;
            $scope.itemToDelete = null;
        };

        /**
         * Utility functions
         */
        $scope.getAvatarInitials = function(item) {
            if (!item) return '?';
            
            // Try different name fields
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
         * Get item display name for delete confirmation
         */
        $scope.getItemDisplayName = function(item) {
            if (!item) return 'this item';
            
            // Try different name combinations
            if (item.firstName && item.lastName) {
                return item.firstName + ' ' + item.lastName;
            }
            if (item.firstName) {
                return item.firstName;
            }
            if (item.name) {
                return item.name;
            }
            if (item.title) {
                return item.title;
            }
            if (item.id) {
                return $scope.config.entityName + ' #' + item.id;
            }
            
            return 'this ' + $scope.config.entityName.toLowerCase();
        };
    }
})();
