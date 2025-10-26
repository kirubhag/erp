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

        // Extend the generic EntityListController FIRST
        angular.extend(this, $controller('EntityListController', {$scope: $scope}));
        
        // Then override/enhance the configuration with student-specific setup
        initializeStudentSpecifics();

        // Log configuration status for debugging
        console.log('StudentEntityController initialized with config:', $scope.config);

        /**
         * Initialize student-specific functionality
         */
        function initializeStudentSpecifics() {
            console.log('initializeStudentSpecifics called, existing config:', $scope.config);
            
            // Ensure config exists - preserve existing or create new
            if (!$scope.config) {
                $scope.config = {};
            }
            
            // Set entity type (preserve other config properties)
            $scope.config.entityType = 'STUDENT';

            // Set up student-specific handlers
            setupStudentHandlers();
            
            console.log('initializeStudentSpecifics completed, final config:', $scope.config);

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


        }

        /**
         * Set up student-specific action handlers
         */
        function setupStudentHandlers() {
            console.log('setupStudentHandlers() called');
            console.log('Current $scope.config before setup:', $scope.config);
            
            // Ensure config exists
            if (!$scope.config) {
                console.log('Creating new config object');
                $scope.config = {};
            }
            
            // Ensure handlers object exists
            if (!$scope.config.handlers) {
                console.log('Creating new handlers object');
                $scope.config.handlers = {};
            }

            console.log('Setting up student handlers...', $scope.config.handlers);

            // Create action handler
            $scope.config.handlers.create = function() {
                console.log('Create handler called - starting navigation process');
                console.log('Config handlers:', $scope.config.handlers);
                try {
                    console.log('About to call showAddStudentForm()');
                    $scope.showAddStudentForm();
                    console.log('showAddStudentForm() call completed');
                } catch (error) {
                    console.error('Error in create handler:', error);
                }
            };

            // Export action handler
            $scope.config.handlers.export = function(format, data) {
                console.log('Export handler called');
                $scope.exportStudents(format, data);
            };

            // Edit configuration - set both editUrl and onEdit for compatibility
            $scope.config.editUrl = '/students/{id}/edit';
            $scope.config.onEdit = function(student) {
                console.log('onEdit handler called for student:', student.id);
                $scope.editStudent(student);
            };

            // View configuration - for row clicks
            $scope.config.onRowClick = function(student) {
                console.log('onRowClick handler called for student:', student.id);
                $scope.viewStudent(student);
            };
            
            // Add onView handler for the generic controller
            $scope.config.onView = function(student) {
                console.log('onView handler called for student:', student.id);
                $scope.viewStudent(student);
            };
            
            // Add onDelete handler for the generic controller
            $scope.config.onDelete = function(student) {
                console.log('onDelete handler called for student:', student.id);
                $scope.deleteStudent(student);
            };

            console.log('Student handlers configured:', $scope.config);

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

            // Ensure rowActions exist and update with student-specific handlers
            if (!$scope.config.rowActions) {
                $scope.config.rowActions = [
                    {
                        name: 'view',
                        icon: 'eye',
                        label: 'View Details',
                        visible: true,
                        handler: function(student) {
                            console.log('View row action handler called for student:', student.id);
                            $scope.viewStudent(student);
                        }
                    },
                    {
                        name: 'edit',
                        icon: 'edit',
                        label: 'Edit Student',
                        visible: true,
                        handler: function(student) {
                            console.log('Edit row action handler called for student:', student.id);
                            $scope.editStudent(student);
                        }
                    },
                    {
                        name: 'delete',
                        icon: 'trash',
                        label: 'Delete Student',
                        visible: true,
                        handler: function(student) {
                            console.log('Delete row action handler called for student:', student.id);
                            $scope.deleteStudent(student);
                        }
                    }
                ];
            } else {
                // Update existing row actions with handlers
                $scope.config.rowActions.forEach(function(action) {
                    if (action.name === 'view') {
                        action.handler = function(student) {
                            console.log('View row action handler called for student:', student.id);
                            $scope.viewStudent(student);
                        };
                    } else if (action.name === 'edit') {
                        action.handler = function(student) {
                            console.log('Edit row action handler called for student:', student.id);
                            $scope.editStudent(student);
                        };
                    } else if (action.name === 'delete') {
                        action.handler = function(student) {
                            console.log('Delete row action handler called for student:', student.id);
                            $scope.deleteStudent(student);
                        };
                    }
                });
            }
            
            console.log('setupStudentHandlers() completed successfully');
            console.log('Final handlers object:', $scope.config.handlers);
        }

        // Student-specific methods

        /**
         * Show add student form (navigate to create page)
         */
        $scope.showAddStudentForm = function() {
            console.log('showAddStudentForm called - navigating to /students/new');
            console.log('Current location before navigation:', $location.path());
            
            // Use $timeout to ensure navigation happens outside current digest cycle
            $timeout(function() {
                console.log('Setting location path to /students/new');
                $location.path('/students/new');
                console.log('Location path set, new location:', $location.path());
            });
        };

        /**
         * View student details
         */
        $scope.viewStudent = function(student) {
            console.log('viewStudent called for student:', student.id, 'navigating to /students/' + student.id);
            console.log('Current location before navigation:', $location.path());
            
            // Use $timeout to ensure navigation happens outside current digest cycle
            $timeout(function() {
                var path = '/students/' + student.id;
                console.log('Setting location path to:', path);
                $location.path(path);
                console.log('Location path set, new location:', $location.path());
            });
        };

        /**
         * Edit student
         */
        $scope.editStudent = function(student) {
            console.log('editStudent called for student:', student.id, 'navigating to /students/' + student.id + '/edit');
            console.log('Current location before navigation:', $location.path());
            
            // Use $timeout to ensure navigation happens outside current digest cycle
            $timeout(function() {
                var path = '/students/' + student.id + '/edit';
                console.log('Setting location path to:', path);
                $location.path(path);
                console.log('Location path set, new location:', $location.path());
            });
        };

        /**
         * Delete student
         */
        $scope.deleteStudent = function(student) {
            console.log('deleteStudent called with student:', student);
            $scope.studentToDelete = student;
            $scope.showDeleteModal = true;
            console.log('Modal state set - showDeleteModal:', $scope.showDeleteModal);
            console.log('Student to delete:', $scope.studentToDelete);
        };

        $scope.confirmDelete = function() {
            if (!$scope.studentToDelete) {
                console.error('No student to delete');
                return;
            }
            
            console.log('=== DELETE OPERATION START ===');
            console.log('Student to delete:', $scope.studentToDelete);
            console.log('Student ID:', $scope.studentToDelete.id);
            
            var studentId = $scope.studentToDelete.id;
            var studentName = $scope.studentToDelete.firstName + ' ' + $scope.studentToDelete.lastName;
            
            // Close modal first
            $scope.showDeleteModal = false;
            $scope.studentToDelete = null;
            
            EntityDataService.deleteItem('STUDENT', studentId)
                .then(function(response) {
                    console.log('DELETE API SUCCESS');
                    console.log('Response:', response);
                    console.log('Response status:', response.status);
                    
                    // Show success notification
                    NotificationService.success('Student ' + studentName + ' deleted successfully');
                    
                    // Use $timeout to ensure reload happens in next digest cycle
                    return $timeout(function() {
                        console.log('Reloading data after timeout...');
                        if ($scope.loadEntityData && typeof $scope.loadEntityData === 'function') {
                            console.log('Calling loadEntityData...');
                            $scope.loadEntityData();
                        } else {
                            console.error('loadEntityData not found!');
                        }
                    }, 200);
                })
                .catch(function(error) {
                    console.error('DELETE API ERROR');
                    console.error('Error:', error);
                    console.error('Error status:', error.status);
                    console.error('Error data:', error.data);
                    
                    var errorMsg = 'Failed to delete student';
                    if (error.data && error.data.message) {
                        errorMsg = error.data.message;
                    } else if (error.statusText) {
                        errorMsg = 'Failed to delete student: ' + error.statusText;
                    }
                    NotificationService.error(errorMsg);
                })
                .finally(function() {
                    console.log('=== DELETE OPERATION END ===');
                });
        };

        $scope.cancelDelete = function() {
            $scope.showDeleteModal = false;
            $scope.studentToDelete = null;
        };

        /**
         * Export students data
         */
        $scope.exportStudents = function(format, data) {

            
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

            NotificationService.info('Bulk update status functionality coming soon');
        };

        /**
         * Show bulk delete confirmation
         */
        $scope.showBulkDeleteConfirmation = function(selectedStudents) {

            if (confirm('Are you sure you want to delete ' + selectedStudents.length + ' selected students?')) {
                // Perform bulk delete
                NotificationService.success('Bulk delete functionality coming soon');
                $scope.clearSelection();
            }
        };

        /**
         * Initialize modal variables
         */
        function initializeModalVariables() {
            $scope.showStudentModal = false;
            $scope.studentForm = {};
            $scope.studentFormSubmitting = false;
            $scope.editingStudent = null;
        }

        /**
         * Create empty student object
         */
        function createEmptyStudent() {
            return {
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
        }

        /**
         * Close student modal
         */
        $scope.closeStudentModal = function() {
            $scope.showStudentModal = false;
            $scope.studentForm = {};
            $scope.editingStudent = null;
            $scope.studentFormSubmitting = false;
        };

        /**
         * Save student
         */
        $scope.saveStudent = function() {
            if ($scope.studentFormSubmitting) return;

            $scope.studentFormSubmitting = true;

            // Prepare data for API
            var studentData = angular.copy($scope.studentForm);
            
            // Convert date strings to proper format if needed
            if (studentData.dateOfBirth) {
                studentData.dateOfBirth = new Date(studentData.dateOfBirth).toISOString().split('T')[0];
            }
            if (studentData.enrollmentDate) {
                studentData.enrollmentDate = new Date(studentData.enrollmentDate).toISOString().split('T')[0];
            }

            var apiCall;
            if ($scope.editingStudent) {
                // Update existing student
                studentData.id = $scope.editingStudent.id;
                apiCall = EntityDataService.updateEntity('STUDENT', studentData.id, studentData);
            } else {
                // Create new student
                apiCall = EntityDataService.createEntity('STUDENT', studentData);
            }

            apiCall.then(function(response) {
                NotificationService.success($scope.editingStudent ? 'Student updated successfully' : 'Student created successfully');
                $scope.closeStudentModal();
                
                // Reload the entity list
                if ($scope.loadEntityData) {
                    $scope.loadEntityData();
                }
            }).catch(function(error) {
                console.error('Error saving student:', error);
                NotificationService.error('Error saving student: ' + (error.message || 'Unknown error'));
            }).finally(function() {
                $scope.studentFormSubmitting = false;
            });
        };

        /**
         * Format grade name for display
         */
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
            
            return gradeMap[grade] || grade.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
        };

        // Initialize modal variables
        initializeModalVariables();

        // Override the base entity functions to use our specific implementations  
        $scope.editItem = $scope.editStudent;
        $scope.deleteItem = function(student) {
            if (confirm('Are you sure you want to delete ' + $scope.formatStudentName(student) + '?')) {
                EntityDataService.deleteEntity('STUDENT', student.id).then(function() {
                    NotificationService.success('Student deleted successfully');
                    if ($scope.loadEntityData) {
                        $scope.loadEntityData();
                    }
                }).catch(function(error) {
                    console.error('Error deleting student:', error);
                    NotificationService.error('Error deleting student: ' + (error.message || 'Unknown error'));
                });
            }
        };

        // Cleanup
        $scope.$on('$destroy', function() {

        });


    }
})();