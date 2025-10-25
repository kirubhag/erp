// Generic Custom View Service for Student Entity


// Register service with explicit module verification
try {
    var appModule = angular.module('erpApp');

    
    appModule.factory('StudentCustomViewService', ['$http', function($http) {

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
            return $http.get('/api/fields/' + entityType);
        },
        
        getAccessibleViewsList: function() {
            return $http.get(baseUrl + '?entityType=' + entityType + '&userId=1', {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        createCustomView: function(viewData) {
            viewData.entityType = entityType;
            return $http.post(baseUrl + '?userId=1', viewData, {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        updateCustomView: function(viewId, viewData) {
            viewData.entityType = entityType;
            return $http.put(baseUrl + '/' + viewId + '?userId=1', viewData, {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        deleteCustomView: function(viewId) {
            return $http.delete(baseUrl + '/' + viewId + '?userId=1', {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        setAsDefaultView: function(view) {
            // Create a copy of the view and set it as default
            var updatedView = angular.copy(view);
            updatedView.isDefault = true;
            updatedView.entityType = entityType;
            
            return $http.put(baseUrl + '/' + view.id + '?userId=1', updatedView, {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        getDefaultView: function() {
            return $http.get(baseUrl + '/default?entityType=' + entityType, {
                headers: {
                    'X-User': 'admin'
                }
            });
        },
        
        groupFieldsByCategory: function(fields) {
            var grouped = {};
            if (fields && fields.length) {
                fields.forEach(function(field) {
                    var category = field.fieldCategory || field.category || 'Other';
                    if (!grouped[category]) {
                        grouped[category] = [];
                    }
                    grouped[category].push(field);
                });
            }
            return grouped;
        }
    };
}]);


} catch (serviceError) {
    console.error('❌ Error registering StudentCustomViewService:', serviceError);
}

// Student Controller - Handles student management UI logic  


// Register controller with explicit module verification
try {
    // Ensure we have the correct module reference
    var appModule;
    try {
        appModule = angular.module('erpApp');

    } catch (moduleError) {
        console.error('❌ Could not find erpApp module:', moduleError);
        throw new Error('erpApp module not available');
    }
    
    // Override the placeholder controller with the real implementation


// Get the app module
var appModule = angular.module('erpApp');
    
    // Force replacement of existing controller instances
    appModule.controller('StudentController', ['$scope', '$http', 'StudentCustomViewService', function($scope, $http, StudentCustomViewService) {


        
        // Clear placeholder flag
        $scope.isPlaceholder = false;
        

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

        $scope.loadDefaultView();

    };
    
    // Load default view - removed custom view system
    
    // Load students
    $scope.loadStudents = function(page) {

        $scope.loading = true;
        page = page || 0;
        
        $http.get('/api/students?page=' + page + '&size=10&sort=id,asc')
            .then(function(response) {

                $scope.students = response.data.content || response.data;
                if (response.data.totalElements !== undefined) {
                    $scope.pagination.totalElements = response.data.totalElements;
                    $scope.pagination.totalPages = response.data.totalPages;
                    $scope.pagination.currentPage = response.data.number;
                }
                $scope.loading = false;

                
                // Force digest to update view
                if (!$scope.$$phase) {
                    $scope.$apply();
                }
            })
            .catch(function(error) {
                console.error('❌ Error loading students:', error);
                $scope.loading = false;
                // Force digest to update view
                if (!$scope.$$phase) {
                    $scope.$apply();
                }
            });
    };
    
    // Load custom views
    $scope.loadCustomViews = function() {
        StudentCustomViewService.getAccessibleViewsList()
            .then(function(response) {
                $scope.customViews = response.data;

            })
            .catch(function(error) {
                console.error('Failed to load custom views:', error);
                $scope.customViews = [];
            });
    };
    
    // Load default view
    $scope.loadDefaultView = function() {
        StudentCustomViewService.getDefaultView()
            .then(function(response) {
                $scope.currentCustomView = response.data;

            })
            .catch(function(error) {

                $scope.currentCustomView = null;
            });
    };
    
    // Load available fields
    $scope.loadAvailableFields = function() {
        StudentCustomViewService.getAvailableFields()
            .then(function(response) {
                $scope.availableFields = response.data;
                $scope.fieldCategories = $scope.groupFieldsByCategory(response.data);

            })
            .catch(function(error) {
                console.error('Failed to load fields:', error);
                $scope.availableFields = [];
                $scope.fieldCategories = {};
            });
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

    // Group fields by category
    $scope.groupFieldsByCategory = function(fields) {
        var grouped = {};
        if (fields && fields.length) {
            fields.forEach(function(field) {
                var category = field.fieldCategory || field.category || 'Other';
                if (!grouped[category]) {
                    grouped[category] = [];
                }
                grouped[category].push(field);
            });
        }
        return grouped;
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

        
        if (!$scope.students || $scope.students.length === 0) {

            return;
        }
        
        // Check if all students are already selected
        var allSelected = $scope.students.every(function(student) {
            return $scope.isStudentSelected(student);
        });
        
        if (allSelected) {
            // If all are selected, deselect all

            $scope.selectedStudents = [];
        } else {
            // Select all students

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
        // Load the default view (if any exists)
        $scope.loadDefaultView();
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
        delete duplicatedView.createdTime;
        delete duplicatedView.modifiedTime;
        
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
        
        StudentCustomViewService.setAsDefaultView(view)
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
        
    }]);
