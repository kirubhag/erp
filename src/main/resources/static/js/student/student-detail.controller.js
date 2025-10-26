// Student Detail Controller - Handles student detail view
(function() {
    'use strict';

    angular.module('erpApp')
        .controller('StudentDetailController', StudentDetailController);

    StudentDetailController.$inject = [
        '$scope', 
        '$routeParams',
        '$location', 
        'EntityDataService',
        'NotificationService'
    ];

    function StudentDetailController($scope, $routeParams, $location, EntityDataService, NotificationService) {
        
        // Initialize controller
        init();

        function init() {
            $scope.loading = true;
            $scope.student = {};
            $scope.studentActivities = [];
            $scope.studentNotes = [];
            $scope.studentAttachments = [];
            $scope.studentCourses = [];
            
            loadStudent();
            loadRelatedData();
        }

        /**
         * Load student data
         */
        function loadStudent() {
            var studentId = $routeParams.id;
            
            if (!studentId) {
                NotificationService.error('Student ID is required');
                $location.path('/students');
                return;
            }

            EntityDataService.getItem('STUDENT', studentId)
                .then(function(response) {
                    $scope.student = response;
                    
                    // Ensure nested objects exist
                    if (!$scope.student.address) {
                        $scope.student.address = {};
                    }
                    if (!$scope.student.emergencyContact) {
                        $scope.student.emergencyContact = {};
                    }
                    
                    $scope.loading = false;
                })
                .catch(function(error) {
                    console.error('Error loading student:', error);
                    NotificationService.error('Failed to load student details');
                    $scope.loading = false;
                    $location.path('/students');
                });
        }

        /**
         * Load related data (activities, notes, etc.)
         */
        function loadRelatedData() {
            // Mock data for now - replace with actual API calls
            $scope.studentActivities = [
                {
                    id: 1,
                    title: 'Student Created',
                    description: 'Student profile was created in the system',
                    date: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000), // 7 days ago
                    icon: 'user-plus'
                },
                {
                    id: 2,
                    title: 'Grade Updated',
                    description: 'Grade level was updated to current academic year',
                    date: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000), // 3 days ago
                    icon: 'edit'
                },
                {
                    id: 3,
                    title: 'Contact Information Updated',
                    description: 'Phone number and email address were updated',
                    date: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000), // 1 day ago
                    icon: 'phone'
                }
            ];

            $scope.studentNotes = [
                { id: 1, title: 'Academic Performance Note', date: new Date() },
                { id: 2, title: 'Behavioral Observation', date: new Date() }
            ];

            $scope.studentAttachments = [
                { id: 1, name: 'Birth Certificate', type: 'PDF', size: '245 KB' },
                { id: 2, name: 'Medical Records', type: 'PDF', size: '1.2 MB' },
                { id: 3, name: 'Previous School Report', type: 'PDF', size: '890 KB' },
                { id: 4, name: 'Photo ID', type: 'JPG', size: '156 KB' }
            ];

            $scope.studentCourses = [
                { id: 1, name: 'Mathematics', grade: 'A', teacher: 'Ms. Johnson' },
                { id: 2, name: 'English Literature', grade: 'B+', teacher: 'Mr. Smith' },
                { id: 3, name: 'Science', grade: 'A-', teacher: 'Dr. Davis' }
            ];
        }

        /**
         * Navigation functions
         */
        $scope.goBack = function() {
            $location.path('/students');
        };

        $scope.editStudent = function() {
            $location.path('/students/' + $scope.student.id + '/edit');
        };

        /**
         * Action functions
         */
        $scope.duplicateStudent = function() {
            NotificationService.info('Duplicate student functionality coming soon');
        };

        $scope.exportStudent = function() {
            NotificationService.info('Export student functionality coming soon');
        };

        $scope.deleteStudent = function() {
            $scope.studentToDelete = $scope.student;
            $scope.showDeleteModal = true;
        };

        $scope.confirmDelete = function() {
            console.log('Delete confirmed for student:', $scope.studentToDelete);
            $scope.showDeleteModal = false;
            
            EntityDataService.deleteItem('STUDENT', $scope.studentToDelete.id)
                .then(function() {
                    console.log('Student deleted successfully');
                    NotificationService.success('Student deleted successfully');
                    $location.path('/students');
                })
                .catch(function(error) {
                    console.error('Error deleting student:', error);
                    console.error('Error response:', error.data);
                    NotificationService.error('Failed to delete student');
                });
        };

        $scope.cancelDelete = function() {
            $scope.showDeleteModal = false;
            $scope.studentToDelete = null;
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
    }
})();