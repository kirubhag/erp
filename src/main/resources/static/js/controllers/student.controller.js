// Generic Custom View Service for Student Entity
angular.module('erpApp').factory('StudentCustomViewService', ['$http', function($http) {
    var baseUrl = '/api/custom-views';
    var entityType = 'STUDENT';
    
    return {
        createDefaultViewData: function() {
            return {
                viewName: '',
                description: '',
                selectedFields: [],
                isDefault: false,
                isPublic: false,
                entityType: entityType
            };
        },
        
        getAvailableFields: function() {
            return $http.get(baseUrl + '/available-fields/' + entityType);
        },
        
        getAccessibleViewsList: function() {
            return $http.get(baseUrl + '/entity/' + entityType, {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        createCustomView: function(viewData) {
            viewData.entityType = entityType;
            return $http.post(baseUrl, viewData, {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        updateCustomView: function(viewId, viewData) {
            viewData.entityType = entityType;
            return $http.put(baseUrl + '/' + viewId, viewData, {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        deleteCustomView: function(viewId) {
            return $http.delete(baseUrl + '/' + viewId, {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        setAsDefaultView: function(viewId) {
            return $http.put(baseUrl + '/' + viewId + '/set-default', {}, {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        getDefaultView: function() {
            return $http.get(baseUrl + '/entity/' + entityType + '/default', {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        groupFieldsByCategory: function(fields) {
            var grouped = {};
            if (fields && fields.length) {
                fields.forEach(function(field) {
                    if (!grouped[field.category]) {
                        grouped[field.category] = [];
                    }
                    grouped[field.category].push(field);
                });
            }
            return grouped;
        }
    };
}]);

// Student Controller - Handles student management UI logic
angular.module('erpApp').controller('StudentController', ['$scope', '$http', 'StudentCustomViewService', function($scope, $http, StudentCustomViewService) {
    // Basic properties
    $scope.students = [];
    $scope.currentStudent = {};
    $scope.isEditing = false;
    $scope.loading = false;
    
    // Pagination
    $scope.pagination = {
        currentPage: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0
    };
    
    // Custom View Configuration
    $scope.customViews = [];
    $scope.availableFields = [];
    $scope.currentCustomView = null;
    $scope.showCustomViewModal = false;
    $scope.customViewForm = StudentCustomViewService.createDefaultViewData();
    $scope.fieldCategories = {};
    
    // Initialize controller
    $scope.init = function() {
        $scope.loadStudents();
        $scope.loadCustomViews();
        $scope.loadAvailableFields();
    };
    
    // Load default view - removed custom view system
    
    // Load students
    $scope.loadStudents = function(page) {
        $scope.loading = true;
        page = page || 0;
        
        var params = {
            page: page,
            size: $scope.pagination.size,
            sort: 'id,asc'
        };
        
        $http.get('/api/students', { params: params })
            .then(function(response) {
                $scope.students = response.data.content || response.data;
                if (response.data.totalElements !== undefined) {
                    $scope.pagination.totalElements = response.data.totalElements;
                    $scope.pagination.totalPages = response.data.totalPages;
                    $scope.pagination.currentPage = response.data.number;
                }
                $scope.loading = false;
            })
            .catch(function(error) {
                console.error('Error loading students:', error);
                $scope.loading = false;
            });
    };
    
    // Load custom views - removed custom view system
    $scope.loadCustomViews = function() {
        // Custom view system has been removed
        $scope.customViews = [];
    };
    
    // Load available fields - removed custom view system
    $scope.loadAvailableFields = function() {
        // Custom view system has been removed
        $scope.availableFields = [];
        $scope.fieldCategories = {};
    };
    
    // Utility functions
    $scope.formatStudentName = function(student) {
        if (!student) return '';
        var name = student.firstName + ' ' + student.lastName;
        return name;
    };
    
    $scope.formatDate = function(dateString) {
        if (!dateString) return '';
        var date = new Date(dateString);
        return date.toLocaleDateString();
    };
    
    $scope.calculateAge = function(dateOfBirth) {
        if (!dateOfBirth) return 0;
        var today = new Date();
        var birthDate = new Date(dateOfBirth);
        var age = today.getFullYear() - birthDate.getFullYear();
        var monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    };
    
    $scope.getGradeDisplayName = function(gradeLevel) {
        var gradeMap = {
            'KINDERGARTEN': 'Kindergarten',
            'GRADE_1': 'Grade 1',
            'GRADE_2': 'Grade 2',
            'GRADE_3': 'Grade 3',
            'GRADE_4': 'Grade 4',
            'GRADE_5': 'Grade 5',
            'GRADE_6': 'Grade 6',
            'GRADE_7': 'Grade 7',
            'GRADE_8': 'Grade 8',
            'GRADE_9': 'Grade 9',
            'GRADE_10': 'Grade 10',
            'GRADE_11': 'Grade 11',
            'GRADE_12': 'Grade 12'
        };
        return gradeMap[gradeLevel] || gradeLevel;
    };
    
    $scope.getStatusDisplayName = function(status) {
        var statusMap = {
            'ACTIVE': 'Active',
            'INACTIVE': 'Inactive',
            'GRADUATED': 'Graduated',
            'TRANSFERRED': 'Transferred',
            'SUSPENDED': 'Suspended',
            'EXPELLED': 'Expelled'
        };
        return statusMap[status] || status;
    };
    
    $scope.getStatusBadgeClass = function(status) {
        var classMap = {
            'ACTIVE': 'bg-success',
            'INACTIVE': 'bg-secondary',
            'GRADUATED': 'bg-primary',
            'TRANSFERRED': 'bg-info',
            'SUSPENDED': 'bg-warning',
            'EXPELLED': 'bg-danger'
        };
        return classMap[status] || 'bg-secondary';
    };
    
    // Initialize options
    $scope.gradeLevels = ['KINDERGARTEN', 'GRADE_1', 'GRADE_2', 'GRADE_3', 'GRADE_4', 'GRADE_5',
                          'GRADE_6', 'GRADE_7', 'GRADE_8', 'GRADE_9', 'GRADE_10', 'GRADE_11', 'GRADE_12'];
    $scope.statusOptions = ['ACTIVE', 'INACTIVE', 'GRADUATED', 'TRANSFERRED', 'SUSPENDED', 'EXPELLED'];
    $scope.pageSizeOptions = [5, 10, 20, 50, 100];
    
    // Selection management
    $scope.selectedStudents = [];
    
    $scope.isStudentSelected = function(student) {
        return $scope.selectedStudents.indexOf(student) >= 0;
    };
    
    $scope.toggleStudentSelection = function(student) {
        var index = $scope.selectedStudents.indexOf(student);
        if (index >= 0) {
            $scope.selectedStudents.splice(index, 1);
        } else {
            $scope.selectedStudents.push(student);
        }
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
    
    // Custom View Management
    $scope.isFieldVisible = function(fieldName) {
        if (!$scope.currentCustomView) return true; // Default view shows all fields
        return $scope.currentCustomView.selectedFields.indexOf(fieldName) >= 0;
    };
    
    $scope.applyCustomView = function(view) {
        $scope.currentCustomView = view;
    };
    
    $scope.resetToDefaultView = function() {
        // If there's a current custom view that's set as default, we need to unset it
        if ($scope.currentCustomView && $scope.currentCustomView.isDefault) {
            // In a full implementation, you might want to call an API to unset the default
            // For now, we'll just clear the local state
        }
        
        $scope.currentCustomView = null;
    };
    
    // Modal management
    $scope.showStudentModal = false;
    $scope.showViewModal = false;
    $scope.showDeleteModal = false;
    $scope.showBulkActionModal = false;
    $scope.showCustomViewManagerModal = false;
    
    $scope.showAddStudentForm = function() {
        $scope.editingStudent = null;
        $scope.studentForm = {};
        $scope.showStudentModal = true;
    };
    
    $scope.editStudent = function(student) {
        $scope.editingStudent = student;
        $scope.studentForm = angular.copy(student);
        $scope.showStudentModal = true;
    };
    
    $scope.viewStudent = function(student) {
        $scope.currentStudent = student;
        $scope.showViewModal = true;
    };
    
    $scope.deleteStudent = function(student) {
        $scope.currentStudent = student;
        $scope.showDeleteModal = true;
    };
    
    $scope.closeStudentModal = function() {
        $scope.showStudentModal = false;
    };
    
    $scope.closeViewModal = function() {
        $scope.showViewModal = false;
    };
    
    $scope.closeDeleteModal = function() {
        $scope.showDeleteModal = false;
    };
    
    $scope.showCustomViewManager = function() {
        $scope.showCustomViewManagerModal = true;
    };
    
    $scope.closeCustomViewManager = function() {
        $scope.showCustomViewManagerModal = false;
    };
    
    // Custom View CRUD Operations
    $scope.showCreateCustomView = function() {
        $scope.editingCustomView = null;
        $scope.customViewForm = StudentCustomViewService.createDefaultViewData();
        $scope.showCustomViewModal = true;
    };
    
    $scope.showEditCustomView = function(view) {
        $scope.editingCustomView = view;
        $scope.customViewForm = angular.copy(view);
        $scope.showCustomViewModal = true;
    };
    
    $scope.closeCustomViewModal = function() {
        $scope.showCustomViewModal = false;
        $scope.editingCustomView = null;
        $scope.customViewForm = {};
    };
    
    $scope.saveCustomView = function() {
        if (!$scope.isValidCustomViewForm()) return;
        
        $scope.loading = true;
        var promise;
        
        if ($scope.editingCustomView) {
            promise = StudentCustomViewService.updateCustomView($scope.editingCustomView.id, $scope.customViewForm);
        } else {
            promise = StudentCustomViewService.createCustomView($scope.customViewForm);
        }
        
        promise.then(function(response) {
            $scope.loadCustomViews();
            $scope.closeCustomViewModal();
            $scope.loading = false;
        }).catch(function(error) {
            console.error('Error saving custom view:', error);
            $scope.loading = false;
        });
    };
    
    $scope.deleteCustomView = function(view) {
        if (confirm('Are you sure you want to delete the custom view "' + view.viewName + '"?')) {
            $scope.loading = true;
            StudentCustomViewService.deleteCustomView(view.id)
                .then(function() {
                    $scope.loadCustomViews();
                    $scope.loading = false;
                })
                .catch(function(error) {
                    console.error('Error deleting custom view:', error);
                    $scope.loading = false;
                });
        }
    };
    
    $scope.duplicateCustomView = function(view) {
        var newViewName = view.viewName + ' (Copy)';
        var duplicatedView = angular.copy(view);
        duplicatedView.viewName = newViewName;
        duplicatedView.isDefault = false;
        delete duplicatedView.id;
        delete duplicatedView.createdAt;
        delete duplicatedView.updatedAt;
        
        $scope.loading = true;
        StudentCustomViewService.createCustomView(duplicatedView)
            .then(function(response) {
                $scope.loadCustomViews();
                $scope.loading = false;
            })
            .catch(function(error) {
                console.error('Error duplicating custom view:', error);
                $scope.loading = false;
            });
    };
    
    $scope.setAsDefaultView = function(view) {
        $scope.loading = true;
        
        StudentCustomViewService.setAsDefaultView(view.id)
            .then(function(response) {
                
                // Update local state
                $scope.customViews.forEach(function(v) {
                    v.isDefault = false;
                });
                view.isDefault = true;
                $scope.currentCustomView = view;
                
                $scope.loading = false;
            })
            .catch(function(error) {
                console.error('Error setting default view:', error);
                $scope.loading = false;
            });
    };
    
    // Custom View Field Management
    $scope.isFieldSelected = function(fieldName) {
        return $scope.customViewForm.selectedFields && 
               $scope.customViewForm.selectedFields.indexOf(fieldName) >= 0;
    };
    
    $scope.toggleFieldSelection = function(fieldName) {
        if (!$scope.customViewForm.selectedFields) {
            $scope.customViewForm.selectedFields = [];
        }
        
        var index = $scope.customViewForm.selectedFields.indexOf(fieldName);
        if (index >= 0) {
            $scope.customViewForm.selectedFields.splice(index, 1);
        } else {
            $scope.customViewForm.selectedFields.push(fieldName);
        }
    };
    
    $scope.selectAllFields = function(category) {
        if (!$scope.fieldCategories[category]) return;
        
        if (!$scope.customViewForm.selectedFields) {
            $scope.customViewForm.selectedFields = [];
        }
        
        $scope.fieldCategories[category].forEach(function(field) {
            if ($scope.customViewForm.selectedFields.indexOf(field.fieldName) < 0) {
                $scope.customViewForm.selectedFields.push(field.fieldName);
            }
        });
    };
    
    $scope.deselectAllFields = function(category) {
        if (!$scope.fieldCategories[category] || !$scope.customViewForm.selectedFields) return;
        
        $scope.fieldCategories[category].forEach(function(field) {
            var index = $scope.customViewForm.selectedFields.indexOf(field.fieldName);
            if (index >= 0) {
                $scope.customViewForm.selectedFields.splice(index, 1);
            }
        });
    };
    
    $scope.isValidCustomViewForm = function() {
        return $scope.customViewForm.viewName && 
               $scope.customViewForm.selectedFields && 
               $scope.customViewForm.selectedFields.length > 0;
    };
    
    // Pagination
    $scope.goToPage = function(page) {
        if (page >= 0 && page < $scope.pagination.totalPages) {
            $scope.loadStudents(page);
        }
    };
    
    $scope.changePageSize = function() {
        $scope.loadStudents(0); // Reset to first page
    };
    
    // Test service functionality
    $scope.testService = function() {
        var testData = StudentCustomViewService.createDefaultViewData();
    };
    
    // Initialize controller when loaded
    $scope.init();
    
    // Test the service immediately
    $scope.testService();
}]);
