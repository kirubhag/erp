angular.module('erpApp').controller('ParentController', ['$scope', 'ParentService', '$filter', function($scope, ParentService, $filter) {
    
    // Initialize scope variables
    $scope.parents = [];
    $scope.loading = false;
    $scope.searchQuery = '';
    $scope.filterStatus = '';
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.totalPages = 0;
    $scope.totalElements = 0;
    
    // Load parents data
    $scope.loadParents = function() {
        $scope.loading = true;
        
        ParentService.getAll($scope.currentPage, $scope.pageSize).then(function(response) {
            $scope.parents = response.data.content || response.data;
            $scope.totalPages = response.data.totalPages || 1;
            $scope.totalElements = response.data.totalElements || $scope.parents.length;
            $scope.loading = false;
            
            // Load student count for each parent
            $scope.parents.forEach(function(parent) {
                ParentService.getStudentCount(parent.id).then(function(countResponse) {
                    parent.studentCount = countResponse.data;
                }).catch(function(error) {
                    parent.studentCount = 0;
                });
            });
            
        }).catch(function(error) {
            console.error('Error loading parents:', error);
            $scope.parents = [];
            $scope.loading = false;
        });
    };
    
    // Search parents
    $scope.searchParents = function() {
        if ($scope.searchQuery.length >= 2) {
            $scope.loading = true;
            ParentService.search($scope.searchQuery).then(function(response) {
                $scope.parents = response.data;
                $scope.loading = false;
            }).catch(function(error) {
                console.error('Error searching parents:', error);
                $scope.parents = [];
                $scope.loading = false;
            });
        } else if ($scope.searchQuery.length === 0) {
            $scope.loadParents();
        }
    };
    
    // Filter parents by status
    $scope.filterParents = function() {
        if ($scope.filterStatus !== '') {
            $scope.parents = $scope.parents.filter(function(parent) {
                return parent.isActive.toString() === $scope.filterStatus;
            });
        } else {
            $scope.loadParents();
        }
    };
    
    // View parent details
    $scope.viewParent = function(parent) {
        // Implementation for viewing parent details
        console.log('View parent:', parent);
    };
    
    // Edit parent
    $scope.editParent = function(parent) {
        // Implementation for editing parent
        console.log('Edit parent:', parent);
    };
    
    // Delete parent (soft delete)
    $scope.deleteParent = function(parent) {
        if (confirm('Are you sure you want to deactivate this parent?')) {
            ParentService.delete(parent.id).then(function(response) {
                parent.isActive = false;
                console.log('Parent deactivated successfully');
            }).catch(function(error) {
                console.error('Error deactivating parent:', error);
            });
        }
    };
    
    // Add new parent
    $scope.openAddParentModal = function() {
        // Implementation for adding new parent
        console.log('Add parent functionality to be implemented');
    };
    
    // Pagination functions
    $scope.goToPage = function(page) {
        if (page >= 0 && page < $scope.totalPages && page !== $scope.currentPage) {
            $scope.currentPage = page;
            $scope.loadParents();
        }
    };
    
    // Initialize
    $scope.loadParents();
}]);