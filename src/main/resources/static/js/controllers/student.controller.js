// Student Controller - Handles student management UI logic
angular.module('studentApp').controller('StudentController', [
    '$scope', '$rootScope', 'StudentService', 'APP_CONFIG',
    function($scope, $rootScope, StudentService, APP_CONFIG) {
        
        // Initialize controller
        $scope.init = function() {
            $scope.students = [];
            $scope.currentStudent = null;
            $scope.editingStudent = false;
            $scope.loading = false;
            $scope.searchTerm = '';
            $scope.selectedGrade = '';
            $scope.selectedStatus = '';
            $scope.selectedStudents = [];
            
            // Pagination
            $scope.pagination = {
                currentPage: 0,
                size: APP_CONFIG.PAGINATION.DEFAULT_SIZE,
                totalElements: 0,
                totalPages: 0
            };
            
            // Form data
            $scope.studentForm = StudentService.createEmptyStudent();
            $scope.showStudentModal = false;
            $scope.showDeleteModal = false;
            $scope.showBulkActionModal = false;
            
            // Configuration
            $scope.gradeLevels = APP_CONFIG.GRADE_LEVELS;
            $scope.statusOptions = ['ACTIVE', 'INACTIVE', 'GRADUATED', 'TRANSFERRED', 'SUSPENDED'];
            $scope.pageSizeOptions = APP_CONFIG.PAGINATION.SIZE_OPTIONS;
            
            // Load initial data
            $scope.loadStudents();
        };
        
        // Load students with pagination and filters
        $scope.loadStudents = function(page) {
            $scope.loading = true;
            page = page || 0;
            
            let loadPromise;
            
            if ($scope.searchTerm && $scope.searchTerm.trim() !== '') {
                loadPromise = StudentService.searchStudents(
                    $scope.searchTerm.trim(),
                    page,
                    $scope.pagination.size,
                    'lastName,asc'
                );
            } else {
                loadPromise = StudentService.getAllStudents(
                    page,
                    $scope.pagination.size,
                    'lastName,asc'
                );
            }
            
            loadPromise.then(function(response) {
                $scope.students = response.data.content || [];
                $scope.pagination.currentPage = response.data.number || 0;
                $scope.pagination.totalElements = response.data.totalElements || 0;
                $scope.pagination.totalPages = response.data.totalPages || 0;
                $scope.selectedStudents = [];
            }).catch(function(error) {
                $rootScope.$broadcast('app:error', error);
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        // Search functionality
        $scope.searchStudents = function() {
            $scope.pagination.currentPage = 0;
            $scope.loadStudents();
        };
        
        $scope.clearSearch = function() {
            $scope.searchTerm = '';
            $scope.selectedGrade = '';
            $scope.selectedStatus = '';
            $scope.searchStudents();
        };
        
        // Filter by grade level
        $scope.filterByGrade = function() {
            if ($scope.selectedGrade) {
                $scope.loading = true;
                StudentService.getStudentsByGrade(
                    $scope.selectedGrade,
                    0,
                    $scope.pagination.size
                ).then(function(response) {
                    $scope.students = response.data.content || [];
                    $scope.pagination.currentPage = 0;
                    $scope.pagination.totalElements = response.data.totalElements || 0;
                    $scope.pagination.totalPages = response.data.totalPages || 0;
                }).catch(function(error) {
                    $rootScope.$broadcast('app:error', error);
                }).finally(function() {
                    $scope.loading = false;
                });
            } else {
                $scope.loadStudents();
            }
        };
        
        // Filter by status
        $scope.filterByStatus = function() {
            if ($scope.selectedStatus) {
                $scope.loading = true;
                StudentService.getStudentsByStatus(
                    $scope.selectedStatus,
                    0,
                    $scope.pagination.size
                ).then(function(response) {
                    $scope.students = response.data.content || [];
                    $scope.pagination.currentPage = 0;
                    $scope.pagination.totalElements = response.data.totalElements || 0;
                    $scope.pagination.totalPages = response.data.totalPages || 0;
                }).catch(function(error) {
                    $rootScope.$broadcast('app:error', error);
                }).finally(function() {
                    $scope.loading = false;
                });
            } else {
                $scope.loadStudents();
            }
        };
        
        // Pagination functions
        $scope.goToPage = function(page) {
            if (page >= 0 && page < $scope.pagination.totalPages) {
                $scope.loadStudents(page);
            }
        };
        
        $scope.changePageSize = function() {
            $scope.pagination.currentPage = 0;
            $scope.loadStudents();
        };
        
        // Student CRUD operations
        $scope.showAddStudentForm = function() {
            $scope.studentForm = StudentService.createEmptyStudent();
            $scope.editingStudent = false;
            $scope.currentStudent = null;
            $scope.showStudentModal = true;
        };
        
        $scope.editStudent = function(student) {
            $scope.studentForm = angular.copy(student);
            $scope.editingStudent = true;
            $scope.currentStudent = student;
            $scope.showStudentModal = true;
        };
        
        $scope.viewStudent = function(student) {
            $scope.currentStudent = student;
            // This would typically open a detailed view modal or navigate to a detail page
            $rootScope.$broadcast('app:info', 'Viewing details for ' + StudentService.formatStudentName(student));
        };
        
        $scope.saveStudent = function() {
            if (!$scope.isValidStudentForm()) {
                return;
            }
            
            $scope.loading = true;
            
            let savePromise;
            if ($scope.editingStudent) {
                savePromise = StudentService.updateStudent($scope.currentStudent.id, $scope.studentForm);
            } else {
                savePromise = StudentService.createStudent($scope.studentForm);
            }
            
            savePromise.then(function(response) {
                const message = $scope.editingStudent ? 'Student updated successfully' : 'Student created successfully';
                $rootScope.$broadcast('app:success', message);
                $scope.closeStudentModal();
                $scope.loadStudents($scope.pagination.currentPage);
            }).catch(function(error) {
                $rootScope.$broadcast('app:error', error);
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        $scope.deleteStudent = function(student) {
            $scope.currentStudent = student;
            $scope.showDeleteModal = true;
        };
        
        $scope.confirmDelete = function() {
            if (!$scope.currentStudent) return;
            
            $scope.loading = true;
            StudentService.deleteStudent($scope.currentStudent.id).then(function() {
                $rootScope.$broadcast('app:success', 'Student deleted successfully');
                $scope.closeDeleteModal();
                $scope.loadStudents($scope.pagination.currentPage);
            }).catch(function(error) {
                $rootScope.$broadcast('app:error', error);
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        // Modal control functions
        $scope.closeStudentModal = function() {
            $scope.showStudentModal = false;
            $scope.studentForm = StudentService.createEmptyStudent();
            $scope.editingStudent = false;
            $scope.currentStudent = null;
        };
        
        $scope.closeDeleteModal = function() {
            $scope.showDeleteModal = false;
            $scope.currentStudent = null;
        };
        
        $scope.closeBulkActionModal = function() {
            $scope.showBulkActionModal = false;
            $scope.selectedStudents = [];
        };
        
        // Form validation
        $scope.isValidStudentForm = function() {
            return $scope.studentForm.firstName && 
                   $scope.studentForm.lastName && 
                   $scope.studentForm.email && 
                   $scope.studentForm.gradeLevel &&
                   $scope.studentForm.dateOfBirth;
        };
        
        // Selection functions
        $scope.toggleStudentSelection = function(student) {
            const index = $scope.selectedStudents.findIndex(s => s.id === student.id);
            if (index > -1) {
                $scope.selectedStudents.splice(index, 1);
            } else {
                $scope.selectedStudents.push(student);
            }
        };
        
        $scope.isStudentSelected = function(student) {
            return $scope.selectedStudents.some(s => s.id === student.id);
        };
        
        $scope.selectAllStudents = function() {
            if ($scope.selectedStudents.length === $scope.students.length) {
                $scope.selectedStudents = [];
            } else {
                $scope.selectedStudents = angular.copy($scope.students);
            }
        };
        
        $scope.hasSelectedStudents = function() {
            return $scope.selectedStudents.length > 0;
        };
        
        // Bulk operations
        $scope.showBulkActions = function() {
            if (!$scope.hasSelectedStudents()) {
                $rootScope.$broadcast('app:warning', 'Please select students first');
                return;
            }
            $scope.showBulkActionModal = true;
        };
        
        $scope.bulkUpdateStatus = function(status) {
            if (!$scope.hasSelectedStudents()) return;
            
            const studentIds = $scope.selectedStudents.map(s => s.id);
            $scope.loading = true;
            
            StudentService.bulkUpdateStatus(studentIds, status).then(function() {
                $rootScope.$broadcast('app:success', 'Status updated for ' + studentIds.length + ' students');
                $scope.closeBulkActionModal();
                $scope.loadStudents($scope.pagination.currentPage);
            }).catch(function(error) {
                $rootScope.$broadcast('app:error', error);
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        $scope.bulkDeleteStudents = function() {
            if (!$scope.hasSelectedStudents()) return;
            if (!confirm('Are you sure you want to delete ' + $scope.selectedStudents.length + ' students? This action cannot be undone.')) {
                return;
            }
            
            const studentIds = $scope.selectedStudents.map(s => s.id);
            $scope.loading = true;
            
            StudentService.bulkDelete(studentIds).then(function() {
                $rootScope.$broadcast('app:success', studentIds.length + ' students deleted successfully');
                $scope.closeBulkActionModal();
                $scope.loadStudents($scope.pagination.currentPage);
            }).catch(function(error) {
                $rootScope.$broadcast('app:error', error);
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        // Export functions
        $scope.exportStudents = function(format) {
            const filters = {
                searchTerm: $scope.searchTerm,
                gradeLevel: $scope.selectedGrade,
                status: $scope.selectedStatus
            };
            
            $scope.loading = true;
            let exportPromise;
            
            if (format === 'csv') {
                exportPromise = StudentService.exportToCsv(filters);
            } else if (format === 'pdf') {
                exportPromise = StudentService.exportToPdf(filters);
            }
            
            if (exportPromise) {
                exportPromise.then(function() {
                    $rootScope.$broadcast('app:success', 'Export completed successfully');
                }).catch(function(error) {
                    $rootScope.$broadcast('app:error', error);
                }).finally(function() {
                    $scope.loading = false;
                });
            }
        };
        
        // Utility functions for templates
        $scope.getGradeDisplayName = function(gradeLevel) {
            return StudentService.getGradeLevelDisplayName(gradeLevel);
        };
        
        $scope.getStatusDisplayName = function(status) {
            return StudentService.getStatusDisplayName(status);
        };
        
        $scope.getStatusBadgeClass = function(status) {
            return StudentService.getStatusBadgeClass(status);
        };
        
        $scope.formatStudentName = function(student) {
            return StudentService.formatStudentName(student);
        };
        
        $scope.calculateAge = function(dateOfBirth) {
            return StudentService.calculateAge(dateOfBirth);
        };
        
        // Event listeners
        $rootScope.$on('student:showAddForm', function() {
            $scope.showAddStudentForm();
        });
        
        $rootScope.$on('student:refresh', function() {
            $scope.loadStudents($scope.pagination.currentPage);
        });
        
        // Initialize controller
        $scope.init();
    }
]);