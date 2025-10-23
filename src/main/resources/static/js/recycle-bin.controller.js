angular.module('erpApp').controller('RecycleBinController', ['$scope', '$http', '$location', function($scope, $http, $location) {
    const API_BASE = '/api/recycle-bin';
    
    // Initialize scope variables
    $scope.recycleBinRecords = [];
    $scope.entityTypes = [];
    $scope.statistics = {
        totalRecords: 0,
        recordsByType: {}
    };
    $scope.loading = false;
    $scope.searchQuery = '';
    $scope.selectedEntityType = '';
    $scope.currentPage = 0;
    $scope.pageSize = 20;
    $scope.totalRecords = 0;
    $scope.totalPages = 0;
    $scope.selectedRecord = null;
    $scope.Math = Math;
    
    /**
     * Load recycle bin records with pagination
     */
    $scope.loadRecycleBinRecords = function() {
        $scope.loading = true;
        
        let url = API_BASE + '?page=' + $scope.currentPage + '&size=' + $scope.pageSize;
        
        if ($scope.selectedEntityType) {
            url = API_BASE + '/type/' + $scope.selectedEntityType + '?page=' + $scope.currentPage + '&size=' + $scope.pageSize;
        }
        
        $http.get(url)
            .then(function(response) {
                if (response.data.content) {
                    // Paginated response
                    $scope.recycleBinRecords = response.data.content;
                    $scope.totalRecords = response.data.totalElements;
                    $scope.totalPages = response.data.totalPages;
                } else {
                    // Non-paginated response
                    $scope.recycleBinRecords = response.data;
                    $scope.totalRecords = response.data.length;
                    $scope.totalPages = 1;
                }
                $scope.loading = false;
            })
            .catch(function(error) {
                console.error('Error loading recycle bin records:', error);
                $scope.showError('Failed to load recycle bin records');
                $scope.loading = false;
            });
    };
    
    /**
     * Load statistics
     */
    $scope.loadStatistics = function() {
        $http.get(API_BASE + '/statistics')
            .then(function(response) {
                $scope.statistics = response.data;
            })
            .catch(function(error) {
                console.error('Error loading statistics:', error);
            });
    };
    
    /**
     * Load entity types
     */
    $scope.loadEntityTypes = function() {
        $http.get(API_BASE + '/entity-types')
            .then(function(response) {
                $scope.entityTypes = response.data;
            })
            .catch(function(error) {
                console.error('Error loading entity types:', error);
            });
    };
    
    /**
     * Search recycle bin
     */
    $scope.searchRecycleBin = function() {
        if (!$scope.searchQuery || $scope.searchQuery.trim() === '') {
            $scope.loadRecycleBinRecords();
            return;
        }
        
        $scope.loading = true;
        
        $http.get(API_BASE + '/search?searchTerm=' + encodeURIComponent($scope.searchQuery) + 
                  '&page=' + $scope.currentPage + '&size=' + $scope.pageSize)
            .then(function(response) {
                if (response.data.content) {
                    $scope.recycleBinRecords = response.data.content;
                    $scope.totalRecords = response.data.totalElements;
                    $scope.totalPages = response.data.totalPages;
                } else {
                    $scope.recycleBinRecords = response.data;
                    $scope.totalRecords = response.data.length;
                    $scope.totalPages = 1;
                }
                $scope.loading = false;
            })
            .catch(function(error) {
                console.error('Error searching recycle bin:', error);
                $scope.showError('Failed to search recycle bin');
                $scope.loading = false;
            });
    };
    
    /**
     * Filter by entity type
     */
    $scope.filterByEntityType = function() {
        $scope.currentPage = 0;
        $scope.loadRecycleBinRecords();
    };
    
    /**
     * Restore a single record
     */
    $scope.restoreSingle = function(record) {
        if (!confirm('Are you sure you want to restore "' + record.entityName + '"?')) {
            return;
        }
        
        const requestBody = {
            restoredBy: 'current-user' // TODO: Replace with actual logged-in user
        };
        
        $http.post(API_BASE + '/' + record.recycleBinId + '/restore', requestBody)
            .then(function(response) {
                $scope.showSuccess('Record restored successfully');
                $scope.loadRecycleBinRecords();
                $scope.loadStatistics();
            })
            .catch(function(error) {
                console.error('Error restoring record:', error);
                $scope.showError('Failed to restore record');
            });
    };
    
    /**
     * Restore all records
     */
    $scope.restoreAll = function() {
        if (!confirm('Are you sure you want to restore ALL records in the recycle bin?')) {
            return;
        }
        
        const requestBody = {
            restoredBy: 'current-user' // TODO: Replace with actual logged-in user
        };
        
        $http.post(API_BASE + '/restore/all', requestBody)
            .then(function(response) {
                $scope.showSuccess('All records restored successfully. Count: ' + response.data.restoredCount);
                $scope.loadRecycleBinRecords();
                $scope.loadStatistics();
            })
            .catch(function(error) {
                console.error('Error restoring all records:', error);
                $scope.showError('Failed to restore all records');
            });
    };
    
    /**
     * Permanently delete a single record
     */
    $scope.permanentlyDelete = function(record) {
        if (!confirm('Are you sure you want to PERMANENTLY delete "' + record.entityName + '"? This action cannot be undone!')) {
            return;
        }
        
        $http.delete(API_BASE + '/' + record.recycleBinId + '/permanent')
            .then(function(response) {
                $scope.showSuccess('Record permanently deleted');
                $scope.loadRecycleBinRecords();
                $scope.loadStatistics();
            })
            .catch(function(error) {
                console.error('Error permanently deleting record:', error);
                $scope.showError('Failed to delete record');
            });
    };
    
    /**
     * Cleanup old records (30+ days)
     */
    $scope.cleanupOldRecords = function() {
        if (!confirm('Are you sure you want to permanently delete all records older than 30 days? This action cannot be undone!')) {
            return;
        }
        
        $http.delete(API_BASE + '/cleanup?days=30')
            .then(function(response) {
                $scope.showSuccess('Cleaned up ' + response.data.deletedCount + ' old records');
                $scope.loadRecycleBinRecords();
                $scope.loadStatistics();
            })
            .catch(function(error) {
                console.error('Error cleaning up old records:', error);
                $scope.showError('Failed to cleanup old records');
            });
    };
    
    /**
     * Empty entire recycle bin
     */
    $scope.emptyRecycleBin = function() {
        if (!confirm('Are you sure you want to EMPTY the entire recycle bin? This will permanently delete ALL records and cannot be undone!')) {
            return;
        }
        
        $http.delete(API_BASE + '/empty')
            .then(function(response) {
                $scope.showSuccess('Recycle bin emptied successfully. Deleted ' + response.data.deletedCount + ' records');
                $scope.loadRecycleBinRecords();
                $scope.loadStatistics();
            })
            .catch(function(error) {
                console.error('Error emptying recycle bin:', error);
                $scope.showError('Failed to empty recycle bin');
            });
    };
    
    /**
     * View record details
     */
    $scope.viewDetails = function(record) {
        $scope.selectedRecord = record;
        const modal = new bootstrap.Modal(document.getElementById('recordDetailsModal'));
        modal.show();
    };
    
    /**
     * Restore single from modal
     */
    $scope.restoreSingleFromModal = function() {
        if ($scope.selectedRecord) {
            const modal = bootstrap.Modal.getInstance(document.getElementById('recordDetailsModal'));
            modal.hide();
            $scope.restoreSingle($scope.selectedRecord);
        }
    };
    
    /**
     * Permanently delete from modal
     */
    $scope.permanentlyDeleteFromModal = function() {
        if ($scope.selectedRecord) {
            const modal = bootstrap.Modal.getInstance(document.getElementById('recordDetailsModal'));
            modal.hide();
            $scope.permanentlyDelete($scope.selectedRecord);
        }
    };
    
    /**
     * Get time ago string
     */
    $scope.getTimeAgo = function(dateString) {
        if (!dateString) return '';
        
        const date = new Date(dateString);
        const now = new Date();
        const seconds = Math.floor((now - date) / 1000);
        
        let interval = Math.floor(seconds / 31536000);
        if (interval >= 1) return interval + ' year' + (interval > 1 ? 's' : '') + ' ago';
        
        interval = Math.floor(seconds / 2592000);
        if (interval >= 1) return interval + ' month' + (interval > 1 ? 's' : '') + ' ago';
        
        interval = Math.floor(seconds / 86400);
        if (interval >= 1) return interval + ' day' + (interval > 1 ? 's' : '') + ' ago';
        
        interval = Math.floor(seconds / 3600);
        if (interval >= 1) return interval + ' hour' + (interval > 1 ? 's' : '') + ' ago';
        
        interval = Math.floor(seconds / 60);
        if (interval >= 1) return interval + ' minute' + (interval > 1 ? 's' : '') + ' ago';
        
        return Math.floor(seconds) + ' second' + (Math.floor(seconds) > 1 ? 's' : '') + ' ago';
    };
    
    /**
     * Pagination functions
     */
    $scope.goToPage = function(page) {
        if (page < 0 || page >= $scope.totalPages) return;
        $scope.currentPage = page;
        if ($scope.searchQuery && $scope.searchQuery.trim() !== '') {
            $scope.searchRecycleBin();
        } else {
            $scope.loadRecycleBinRecords();
        }
    };
    
    $scope.getPageNumbers = function() {
        const pages = [];
        const maxPagesToShow = 5;
        let startPage = Math.max(0, $scope.currentPage - Math.floor(maxPagesToShow / 2));
        let endPage = Math.min($scope.totalPages - 1, startPage + maxPagesToShow - 1);
        
        if (endPage - startPage < maxPagesToShow - 1) {
            startPage = Math.max(0, endPage - maxPagesToShow + 1);
        }
        
        for (let i = startPage; i <= endPage; i++) {
            pages.push(i);
        }
        
        return pages;
    };
    
    /**
     * Notification functions
     */
    $scope.showSuccess = function(message) {
        // You can integrate with a toast library like toastr or use Bootstrap toasts
        alert('Success: ' + message);
    };
    
    $scope.showError = function(message) {
        alert('Error: ' + message);
    };
    
    // Initialize
    $scope.loadRecycleBinRecords();
    $scope.loadStatistics();
    $scope.loadEntityTypes();
}]);
