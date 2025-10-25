angular.module('erpApp').controller('HealthController', ['$scope', 'HealthService', 'StudentService', '$filter', function($scope, HealthService, StudentService, $filter) {
    
    // Initialize scope variables
    $scope.healthRecords = [];
    $scope.students = [];
    $scope.loading = false;
    $scope.selectedStudent = '';
    $scope.selectedRecordType = '';
    $scope.severityFilter = '';
    $scope.showOnlyActive = true;
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.totalPages = 0;
    
    // Statistics
    $scope.criticalCount = 0;
    $scope.attentionCount = 0;
    $scope.expiringCount = 0;
    $scope.totalRecords = 0;
    
    // Load health records
    $scope.loadHealthRecords = function() {
        $scope.loading = true;
        
        HealthService.getAll($scope.currentPage, $scope.pageSize, $scope.selectedRecordType).then(function(response) {
            $scope.healthRecords = response.data.content || response.data;
            $scope.totalPages = response.data.totalPages || 1;
            $scope.totalRecords = response.data.totalElements || $scope.healthRecords.length;
            $scope.loading = false;
            
            // Apply filters
            $scope.filterRecords();
            
        }).catch(function(error) {
            console.error('Error loading health records:', error);
            $scope.healthRecords = [];
            $scope.loading = false;
        });
    };
    
    // Load students for dropdown
    $scope.loadStudents = function() {
        StudentService.getActive().then(function(response) {
            $scope.students = response.data;
        }).catch(function(error) {
            console.error('Error loading students:', error);
            $scope.students = [];
        });
    };
    
    // Load statistics
    $scope.loadStatistics = function() {
        // Load records requiring attention
        HealthService.getRecordsRequiringAttention().then(function(response) {
            $scope.attentionCount = response.data.length;
        }).catch(function(error) {
            $scope.attentionCount = 0;
        });
        
        // Load expiring records (next 30 days)
        var thirtyDaysFromNow = new Date();
        thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() + 30);
        HealthService.getExpiringRecords(thirtyDaysFromNow.toISOString().split('T')[0]).then(function(response) {
            $scope.expiringCount = response.data.length;
        }).catch(function(error) {
            $scope.expiringCount = 0;
        });
        
        // Count critical records (client-side for now)
        $scope.criticalCount = $scope.healthRecords.filter(function(record) {
            return record.severity === 'CRITICAL' && record.active;
        }).length;
    };
    
    // Filter records
    $scope.filterRecords = function() {
        var filteredRecords = $scope.healthRecords;
        
        // Filter by student
        if ($scope.selectedStudent) {
            filteredRecords = filteredRecords.filter(function(record) {
                return record.student && record.student.id == $scope.selectedStudent;
            });
        }
        
        // Filter by severity
        if ($scope.severityFilter) {
            filteredRecords = filteredRecords.filter(function(record) {
                return record.severity === $scope.severityFilter;
            });
        }
        
        // Filter by active status
        if ($scope.showOnlyActive) {
            filteredRecords = filteredRecords.filter(function(record) {
                return record.active;
            });
        }
        
        $scope.healthRecords = filteredRecords;
        $scope.loadStatistics();
    };
    
    // Filter by student
    $scope.filterByStudent = function() {
        if ($scope.selectedStudent) {
            $scope.loading = true;
            HealthService.getByStudentId($scope.selectedStudent).then(function(response) {
                $scope.healthRecords = response.data;
                $scope.filterRecords();
                $scope.loading = false;
            }).catch(function(error) {
                console.error('Error filtering by student:', error);
                $scope.loading = false;
            });
        } else {
            $scope.loadHealthRecords();
        }
    };
    
    // Filter by record type
    $scope.filterByType = function() {
        $scope.loadHealthRecords();
    };
    
    // Filter by severity
    $scope.filterBySeverity = function() {
        $scope.filterRecords();
    };
    
    // View record details
    $scope.viewRecord = function(record) {

    };
    
    // Edit record
    $scope.editRecord = function(record) {

    };
    
    // Delete record (soft delete)
    $scope.deleteRecord = function(record) {
        if (confirm('Are you sure you want to deactivate this health record?')) {
            HealthService.delete(record.id).then(function(response) {
                record.active = false;

                $scope.loadStatistics();
            }).catch(function(error) {
                console.error('Error deactivating health record:', error);
            });
        }
    };
    
    // Add new record
    $scope.openAddRecordModal = function() {

    };
    
    // Pagination
    $scope.goToPage = function(page) {
        if (page >= 0 && page < $scope.totalPages && page !== $scope.currentPage) {
            $scope.currentPage = page;
            $scope.loadHealthRecords();
        }
    };
    
    // Check if record is expiring soon
    $scope.isExpiringSoon = function(expiryDate) {
        if (!expiryDate) return false;
        var expiry = new Date(expiryDate);
        var thirtyDaysFromNow = new Date();
        thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() + 30);
        return expiry <= thirtyDaysFromNow && expiry >= new Date();
    };
    
    // Custom filter for record type display
    $scope.$parent.recordTypeFormat = function(recordType) {
        if (!recordType) return '';
        return recordType.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    };
    
    // Initialize
    $scope.loadStudents();
    $scope.loadHealthRecords();
}]);