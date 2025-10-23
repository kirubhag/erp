// Student Entity Controller - Extends generic EntityListController for student-specific functionality
(function() {
    'use strict';

    angular.module('erpApp')
        .controller('StudentEntityController', StudentEntityController);

    StudentEntityController.$inject = [
        '$scope', 
        '$controller',
        '$location', 
        '$timeout',
        'EntityDataService',
        'NotificationService'
    ];

    function StudentEntityController($scope, $controller, $location, $timeout, EntityDataService, NotificationService) {
        console.log('🎓 StudentEntityController initialized');

        // Extend the generic EntityListController
        angular.extend(this, $controller('EntityListController', {$scope: $scope}));

        // Student-specific initialization
        initializeStudentSpecifics();

        /**
         * Initialize student-specific functionality
         */
        function initializeStudentSpecifics() {
            // Override entity type to ensure correct configuration
            if ($scope.config) {
                $scope.config.entityType = 'STUDENT';
            }

            // Set up student-specific handlers
            setupStudentHandlers();

            // Initialize student-specific data
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

            $scope.statusOptions = [
                'ACTIVE',
                'INACTIVE', 
                'PENDING',
                'GRADUATED',
                'TRANSFERRED',
                'WITHDRAWN'
            ];

            console.log('✅ Student-specific initialization complete');
        }

        /**
         * Set up student-specific action handlers
         */
        function setupStudentHandlers() {
            if (!$scope.config || !$scope.config.handlers) {
                if ($scope.config) {
                    $scope.config.handlers = {};
                }
            }

            // Create action handler
            $scope.config.handlers.create = function() {
                console.log('➕ Opening student creation dialog');
                $scope.showAddStudentForm();
            };

            // Export action handler
            $scope.config.handlers.export = function(format, data) {
                console.log('📤 Exporting student data in format:', format);
                $scope.exportStudents(format, data);
            };

            // Set up bulk actions
            if (!$scope.config.bulkActions) {
                $scope.config.bulkActions = [
                    {
                        name: 'updateStatus',
                        label: 'Update Status',
                        icon: 'edit',
                        handler: function(selectedStudents) {
                            $scope.showBulkUpdateStatus(selectedStudents);
                        }
                    },
                    {
                        name: 'delete',
                        label: 'Delete Selected',
                        icon: 'trash',
                        handler: function(selectedStudents) {
                            $scope.showBulkDeleteConfirmation(selectedStudents);
                        }
                    }
                ];
            }

            // Update configuration with student-specific row actions
            if ($scope.config && $scope.config.rowActions) {
                $scope.config.rowActions.forEach(function(action) {
                    if (action.name === 'view') {
                        action.handler = function(student) {
                            $scope.viewStudent(student);
                        };
                    } else if (action.name === 'edit') {
                        action.handler = function(student) {
                            $scope.editStudent(student);
                        };
                    } else if (action.name === 'delete') {
                        action.handler = function(student) {
                            $scope.deleteStudent(student);
                        };
                    }
                });
            }
        }

        // Student-specific methods

        /**
         * Show add student form
         */
        $scope.showAddStudentForm = function() {
            console.log('📝 Showing add student form');
            // TODO: Implement student form modal or navigate to form page
            NotificationService.info('Add Student form will open here');
        };

        /**
         * View student details
         */
        $scope.viewStudent = function(student) {
            console.log('👁️ Viewing student:', student);
            // TODO: Implement student detail view
            NotificationService.info('Student details view for: ' + $scope.formatStudentName(student));
        };

        /**
         * Edit student
         */
        $scope.editStudent = function(student) {
            console.log('✏️ Editing student:', student);
            // TODO: Implement student edit form
            NotificationService.info('Edit form for: ' + $scope.formatStudentName(student));
        };

        /**
         * Delete student
         */
        $scope.deleteStudent = function(student) {
            console.log('🗑️ Deleting student:', student);
            
            if (confirm('Are you sure you want to delete ' + $scope.formatStudentName(student) + '?')) {
                EntityDataService.deleteItem('STUDENT', student.id)
                    .then(function() {
                        NotificationService.success('Student deleted successfully');
                        $scope.loadEntityData(); // Reload data
                    })
                    .catch(function(error) {
                        NotificationService.error('Failed to delete student: ' + error.message);
                    });
            }
        };

        /**
         * Export students data
         */
        $scope.exportStudents = function(format, data) {
            console.log('📊 Exporting students in format:', format);
            
            var exportData = data || $scope.filteredData;
            var filename = 'students_export_' + new Date().toISOString().split('T')[0] + '.' + format;

            EntityDataService.exportData('STUDENT', format, exportData, filename)
                .then(function() {
                    NotificationService.success('Student data exported successfully');
                })
                .catch(function(error) {
                    NotificationService.error('Failed to export student data: ' + error.message);
                });
        };

        // Student-specific utility methods

        /**
         * Format student name for display
         */
        $scope.formatStudentName = function(student) {
            if (!student) return '';
            
            var name = '';
            if (student.firstName) name += student.firstName;
            if (student.middleName) name += ' ' + student.middleName;
            if (student.lastName) name += ' ' + student.lastName;
            
            return name.trim() || student.name || 'Unknown Student';
        };

        /**
         * Get grade display name
         */
        $scope.getGradeDisplayName = function(gradeLevel) {
            if (!gradeLevel) return '';
            
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
            
            return gradeMap[gradeLevel] || gradeLevel;
        };

        /**
         * Get status display name
         */
        $scope.getStatusDisplayName = function(status) {
            if (!status) return '';
            
            var statusMap = {
                'ACTIVE': 'Active',
                'INACTIVE': 'Inactive',
                'PENDING': 'Pending',
                'GRADUATED': 'Graduated',
                'TRANSFERRED': 'Transferred',
                'WITHDRAWN': 'Withdrawn'
            };
            
            return statusMap[status] || status;
        };

        /**
         * Calculate age from date of birth
         */
        $scope.calculateAge = function(dateOfBirth) {
            if (!dateOfBirth) return '';
            
            var birth = new Date(dateOfBirth);
            var today = new Date();
            var age = today.getFullYear() - birth.getFullYear();
            var monthDiff = today.getMonth() - birth.getMonth();
            
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
                age--;
            }
            
            return age;
        };

        /**
         * Override getFieldValue to handle student-specific field formatting
         */
        var originalGetFieldValue = $scope.getFieldValue;
        $scope.getFieldValue = function(item, fieldPath) {
            // Handle special student fields
            if (fieldPath === 'name') {
                return $scope.formatStudentName(item);
            } else if (fieldPath === 'gradeLevel') {
                return $scope.getGradeDisplayName(item.gradeLevel);
            } else if (fieldPath === 'enrollmentStatus' || fieldPath === 'status') {
                return $scope.getStatusDisplayName(item.enrollmentStatus || item.status);
            } else if (fieldPath === 'age') {
                return $scope.calculateAge(item.dateOfBirth) + ' years';
            }
            
            // Fall back to original implementation
            return originalGetFieldValue(item, fieldPath);
        };

        /**
         * Override getAvatarInitials for students
         */
        $scope.getAvatarInitials = function(student) {
            if (!student) return '';
            
            if (student.firstName && student.lastName) {
                return (student.firstName.charAt(0) + student.lastName.charAt(0)).toUpperCase();
            } else if (student.name) {
                var parts = student.name.split(' ');
                return (parts[0].charAt(0) + (parts[1] ? parts[1].charAt(0) : '')).toUpperCase();
            } else if (student.email) {
                return student.email.charAt(0).toUpperCase();
            }
            
            return 'S';
        };

        /**
         * Get grid primary value (for grid view)
         */
        $scope.getGridPrimaryValue = function(student) {
            return $scope.formatStudentName(student);
        };

        /**
         * Get grid secondary value (for grid view)
         */
        $scope.getGridSecondaryValue = function(student) {
            return student.studentId || student.email || '';
        };

        /**
         * Get grid fields for student (for grid view)
         */
        $scope.getGridFields = function(student) {
            return [
                {
                    label: 'Grade',
                    value: $scope.getGradeDisplayName(student.gradeLevel)
                },
                {
                    label: 'Status', 
                    value: $scope.getStatusDisplayName(student.enrollmentStatus)
                },
                {
                    label: 'Email',
                    value: student.email || ''
                }
            ];
        };

        /**
         * Show bulk update status modal
         */
        $scope.showBulkUpdateStatus = function(selectedStudents) {
            console.log('📝 Bulk updating status for', selectedStudents.length, 'students');
            NotificationService.info('Bulk update status functionality coming soon');
        };

        /**
         * Show bulk delete confirmation
         */
        $scope.showBulkDeleteConfirmation = function(selectedStudents) {
            console.log('🗑️ Bulk delete confirmation for', selectedStudents.length, 'students');
            if (confirm('Are you sure you want to delete ' + selectedStudents.length + ' selected students?')) {
                // Perform bulk delete
                NotificationService.success('Bulk delete functionality coming soon');
                $scope.clearSelection();
            }
        };

        // Cleanup
        $scope.$on('$destroy', function() {
            console.log('🧹 StudentEntityController destroyed');
        });

        console.log('✅ StudentEntityController setup complete');
    }
})();