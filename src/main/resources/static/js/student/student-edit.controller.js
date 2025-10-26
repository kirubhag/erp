// Student Edit Controller - Handles student edit page
(function() {
    'use strict';

    angular.module('erpApp')
        .controller('StudentEditController', StudentEditController);

    StudentEditController.$inject = [
        '$scope', 
        '$routeParams',
        '$location',
        '$timeout',
        'EntityDataService',
        'NotificationService'
    ];

    function StudentEditController($scope, $routeParams, $location, $timeout, EntityDataService, NotificationService) {
        
        // Initialize controller
        init();

        function init() {
            $scope.loading = true;
            $scope.saving = false;
            $scope.student = {};
            $scope.originalStudent = {};
            $scope.hasChanges = false;
            $scope.isNewStudent = false;
            
            // Grade levels
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

            loadStudent();
            setupWatchers();
        }

        /**
         * Load student data for editing
         */
        function loadStudent() {
            var studentId = $routeParams.id;
            
            // Check if this is a new student creation
            if (studentId === 'new' || !studentId) {
                $scope.student = createEmptyStudent();
                $scope.originalStudent = angular.copy($scope.student);
                $scope.isNewStudent = true;
                $scope.loading = false;
                return;
            }

            EntityDataService.getItem('STUDENT', studentId)
                .then(function(response) {
                    // Use $timeout to ensure proper digest cycle for date inputs
                    $timeout(function() {
                        $scope.student = angular.copy(response);
                        $scope.originalStudent = angular.copy(response);
                        
                        // Transform flat backend structure to nested frontend structure
                        $scope.student.address = {
                            street: $scope.student.addressLine1 || '',
                            city: $scope.student.city || '',
                            state: $scope.student.state || '',
                            zipCode: $scope.student.postalCode || ''
                        };
                        
                        $scope.student.emergencyContact = {
                            name: $scope.student.emergencyContactName || '',
                            phoneNumber: $scope.student.emergencyContactPhone || '',
                            relationship: $scope.student.emergencyContactRelation || ''
                        };
                        
                        // Convert dates to proper format for HTML5 date input fields (YYYY-MM-DD)
                        if ($scope.student.dateOfBirth) {
                            // Check if it's already in correct format (YYYY-MM-DD)
                            if (typeof $scope.student.dateOfBirth === 'string' && /^\d{4}-\d{2}-\d{2}$/.test($scope.student.dateOfBirth)) {
                                // Already in correct format, no conversion needed
                            } else {
                                $scope.student.dateOfBirth = new Date($scope.student.dateOfBirth)
                                    .toISOString().split('T')[0];
                            }
                        }
                        if ($scope.student.enrollmentDate) {
                            // Check if it's already in correct format (YYYY-MM-DD)
                            if (typeof $scope.student.enrollmentDate === 'string' && /^\d{4}-\d{2}-\d{2}$/.test($scope.student.enrollmentDate)) {
                                // Already in correct format, no conversion needed
                            } else {
                                $scope.student.enrollmentDate = new Date($scope.student.enrollmentDate)
                                    .toISOString().split('T')[0];
                            }
                        }
                        
                        console.log('Student data loaded for editing:', $scope.student);
                        console.log('Date of Birth:', $scope.student.dateOfBirth);
                        console.log('Enrollment Date:', $scope.student.enrollmentDate);
                        
                        $scope.loading = false;
                    });
                })
                .catch(function(error) {
                    console.error('Error loading student:', error);
                    NotificationService.error('Failed to load student for editing');
                    $scope.loading = false;
                    $location.path('/students');
                });
        }

        /**
         * Set up watchers to detect changes
         */
        function setupWatchers() {
            $scope.$watch('student', function(newVal, oldVal) {
                if (newVal && oldVal && !angular.equals(newVal, $scope.originalStudent)) {
                    $scope.hasChanges = true;
                } else {
                    $scope.hasChanges = false;
                }
            }, true);
        }

        /**
         * Save student changes
         */
        $scope.saveStudent = function() {
            if ($scope.saving || !$scope.studentForm.$valid) {
                return;
            }

            $scope.saving = true;

            // Prepare data for API
            var studentData = angular.copy($scope.student);
            
            // Flatten nested address object to match backend structure
            if (studentData.address) {
                studentData.addressLine1 = studentData.address.street;
                studentData.city = studentData.address.city;
                studentData.state = studentData.address.state;
                studentData.postalCode = studentData.address.zipCode;
                delete studentData.address;
            }
            
            // Flatten nested emergency contact object to match backend structure
            if (studentData.emergencyContact) {
                studentData.emergencyContactName = studentData.emergencyContact.name;
                studentData.emergencyContactPhone = studentData.emergencyContact.phoneNumber;
                studentData.emergencyContactRelation = studentData.emergencyContact.relationship;
                delete studentData.emergencyContact;
            }
            
            // Convert date strings to proper format if needed
            if (studentData.dateOfBirth) {
                studentData.dateOfBirth = new Date(studentData.dateOfBirth).toISOString().split('T')[0];
            }
            if (studentData.enrollmentDate) {
                studentData.enrollmentDate = new Date(studentData.enrollmentDate).toISOString().split('T')[0];
            }

            var apiCall;
            if ($scope.isNewStudent) {
                // Remove ID for new student creation
                delete studentData.id;
                apiCall = EntityDataService.createItem('STUDENT', studentData);
            } else {
                apiCall = EntityDataService.updateItem('STUDENT', studentData.id, studentData);
            }

            apiCall
                .then(function(response) {
                    var successMessage = $scope.isNewStudent ? 'Student created successfully' : 'Student updated successfully';
                    NotificationService.success(successMessage);
                    $scope.originalStudent = angular.copy($scope.student);
                    $scope.hasChanges = false;
                    
                    // Navigate back to detail page or students list
                    if ($scope.isNewStudent) {
                        $location.path('/students/' + response.id);
                    } else {
                        $location.path('/students/' + studentData.id);
                    }
                })
                .catch(function(error) {
                    console.error('Error updating student:', error);
                    console.error('Error response data:', error.data);
                    var errorMessage = 'Failed to save student';
                    if (error.data && error.data.message) {
                        errorMessage += ': ' + error.data.message;
                    } else if (error.data && typeof error.data === 'string') {
                        errorMessage += ': ' + error.data;
                    }
                    NotificationService.error(errorMessage);
                })
                .finally(function() {
                    $scope.saving = false;
                });
        };

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
            
            if ($scope.isNewStudent) {
                $location.path('/students');
            } else {
                $location.path('/students/' + $scope.student.id);
            }
        };

        $scope.cancelEdit = function() {
            $scope.goBack();
        };

        /**
         * Utility functions
         */
        $scope.getAvatarInitials = function(student) {
            if (!student || !student.firstName) return '?';
            
            var initials = student.firstName.charAt(0).toUpperCase();
            if (student.lastName) {
                initials += student.lastName.charAt(0).toUpperCase();
            }
            return initials;
        };

        $scope.getAvatarStyle = function(student) {
            // Generate a consistent color based on the student's name
            if (!student || !student.firstName) {
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
            
            var nameHash = (student.firstName + (student.lastName || '')).split('').reduce((a, b) => {
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

        // Handle beforeunload to warn about unsaved changes
        window.addEventListener('beforeunload', function(e) {
            if ($scope.hasChanges) {
                e.preventDefault();
                e.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
                return e.returnValue;
            }
        });

        /**
         * Create empty student object for new student creation
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

        // Cleanup on scope destroy
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
})();