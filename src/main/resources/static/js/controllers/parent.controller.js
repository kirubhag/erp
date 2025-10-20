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
    $scope.selectedParent = null;
    $scope.showViewModal = false;
    $scope.showEditModal = false;
    $scope.showAddModal = false;
    
    // Load parents data
    $scope.loadParents = function() {
        $scope.loading = true;
        
        // Use simple GET request to /api/parents
        ParentService.getAllParents($scope.currentPage, $scope.pageSize).then(function(response) {
            // Handle both paginated and simple array responses
            if (response.data && response.data.content) {
                $scope.parents = response.data.content;
                $scope.totalPages = response.data.totalPages || 1;
                $scope.totalElements = response.data.totalElements;
            } else if (Array.isArray(response.data)) {
                $scope.parents = response.data;
                $scope.totalPages = 1;
                $scope.totalElements = response.data.length;
            } else {
                $scope.parents = [];
            }
            
            $scope.loading = false;
            console.log('Loaded', $scope.parents.length, 'parents');
            
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
            ParentService.searchParents($scope.searchQuery, $scope.currentPage, $scope.pageSize).then(function(response) {
                $scope.parents = response.data.content || response.data;
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
        console.log('viewParent called with:', parent);
        $scope.loading = true;
        ParentService.getParentById(parent.id).then(function(response) {
            console.log('Parent details loaded:', response.data);
            $scope.selectedParent = response.data;
            $scope.showViewModal = true;
            $scope.loading = false;
        }).catch(function(error) {
            console.error('Error loading parent details:', error);
            alert('Error loading parent details: ' + (error.data?.message || 'Unknown error'));
            $scope.loading = false;
        });
    };
    
    // Edit parent
    $scope.editParent = function(parent) {
        console.log('editParent called with:', parent);
        $scope.loading = true;
        ParentService.getParentById(parent.id).then(function(response) {
            console.log('Parent data for edit loaded:', response.data);
            $scope.selectedParent = angular.copy(response.data);
            $scope.showEditModal = true;
            $scope.loading = false;
        }).catch(function(error) {
            console.error('Error loading parent for edit:', error);
            alert('Error loading parent data: ' + (error.data?.message || 'Unknown error'));
            $scope.loading = false;
        });
    };
    
    // Save edited parent
    $scope.saveParent = function() {
        if (!$scope.selectedParent || !$scope.selectedParent.id) return;
        
        console.log('saveParent called with:', $scope.selectedParent);
        $scope.loading = true;
        ParentService.updateParent($scope.selectedParent.id, $scope.selectedParent).then(function(response) {
            console.log('Parent updated successfully:', response.data);
            // Update the parent in the list
            const index = $scope.parents.findIndex(p => p.id === $scope.selectedParent.id);
            if (index !== -1) {
                $scope.parents[index] = response.data;
            }
            $scope.showEditModal = false;
            $scope.selectedParent = null;
            $scope.loading = false;
            alert('Parent updated successfully!');
        }).catch(function(error) {
            console.error('Error updating parent:', error);
            alert('Error updating parent: ' + (error.data?.message || 'Unknown error'));
            $scope.loading = false;
        });
    };
    
    // Delete parent (soft delete)
    $scope.deleteParent = function(parent) {
        console.log('deleteParent called with:', parent);
        if (confirm('Are you sure you want to deactivate this parent?')) {
            $scope.loading = true;
            ParentService.deleteParent(parent.id).then(function(response) {
                console.log('Parent deleted successfully:', response);
                parent.isActive = false;
                alert('Parent deactivated successfully!');
                $scope.loading = false;
            }).catch(function(error) {
                console.error('Error deactivating parent:', error);
                alert('Error deactivating parent: ' + (error.data?.message || 'Unknown error'));
                $scope.loading = false;
            });
        }
    };
    
    // Add new parent
    $scope.openAddParentModal = function() {
        $scope.selectedParent = ParentService.createEmptyParent();
        $scope.showAddModal = true;
    };
    
    // Create new parent
    $scope.createParent = function() {
        if (!$scope.selectedParent) return;
        
        $scope.loading = true;
        ParentService.createParent($scope.selectedParent).then(function(response) {
            $scope.parents.unshift(response.data);
            $scope.showAddModal = false;
            $scope.selectedParent = null;
            $scope.loading = false;
            alert('Parent created successfully!');
        }).catch(function(error) {
            console.error('Error creating parent:', error);
            alert('Error creating parent: ' + (error.data?.message || 'Unknown error'));
            $scope.loading = false;
        });
    };
    
    // Close modals
    $scope.closeModals = function() {
        $scope.showViewModal = false;
        $scope.showEditModal = false;
        $scope.showAddModal = false;
        $scope.selectedParent = null;
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