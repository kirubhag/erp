angular.module('erpApp').controller('AttendanceController', ['$scope', 'AttendanceService', '$filter', function($scope, AttendanceService, $filter) {
    
    // Initialize scope variables
    $scope.attendanceRecords = [];
    $scope.attendanceStats = {};
    $scope.loading = false;
    $scope.selectedDate = new Date().toISOString().split('T')[0];
    $scope.selectedGrade = '';
    $scope.statusFilter = '';
    $scope.currentAttendance = {};
    $scope.showViewModal = false;
    $scope.showEditModal = false;
    $scope.showDeleteModal = false;
    
    // Load attendance data
    $scope.loadAttendance = function() {
        console.log('Loading attendance data...');
        $scope.loading = true;
        
        // Use the simple getAllAttendance to get all records
        AttendanceService.getAllAttendance().then(function(response) {
            console.log('Attendance API response:', response);
            $scope.attendanceRecords = response.data;
            $scope.loading = false;
            $scope.calculateStats();
            console.log('Loaded attendance records:', $scope.attendanceRecords.length);
        }).catch(function(error) {
            console.error('Error loading attendance records:', error);
            $scope.attendanceRecords = [];
            $scope.loading = false;
            alert('Error loading attendance records: ' + (error.data?.message || error.message || 'Unknown error'));
        });
    };
    
    // Calculate statistics from loaded data
    $scope.calculateStats = function() {
        var filteredRecords = $scope.getFilteredRecords();
        $scope.attendanceStats = {
            presentStudents: filteredRecords.filter(function(a) { return a.status === 'PRESENT'; }).length,
            absentStudents: filteredRecords.filter(function(a) { return a.status === 'ABSENT'; }).length,
            lateStudents: filteredRecords.filter(function(a) { return a.status === 'LATE'; }).length,
            excusedStudents: filteredRecords.filter(function(a) { return a.status === 'EXCUSED'; }).length,
            attendanceRate: filteredRecords.length > 0 ? Math.round((filteredRecords.filter(function(a) { return a.status === 'PRESENT'; }).length / filteredRecords.length) * 100) : 0
        };
    };
    
    // Get filtered records based on current filters
    $scope.getFilteredRecords = function() {
        return $scope.attendanceRecords.filter(function(attendance) {
            var dateMatch = !$scope.selectedDate || attendance.attendanceDate === $scope.selectedDate;
            var statusMatch = !$scope.statusFilter || attendance.status === $scope.statusFilter;
            return dateMatch && statusMatch;
        });
    };
    
    // Filter by grade level
    $scope.filterByGrade = function() {
        $scope.calculateStats();
    };
    
    // Filter by status
    $scope.filterByStatus = function() {
        $scope.calculateStats();
    };
    
    // View attendance record
    $scope.viewAttendance = function(attendance) {
        $scope.currentAttendance = angular.copy(attendance);
        $scope.showViewModal = true;
    };
    
    // Edit attendance record
    $scope.editAttendance = function(attendance) {
        $scope.currentAttendance = angular.copy(attendance);
        $scope.showEditModal = true;
    };
    
    // Save attendance changes
    $scope.saveAttendance = function() {
        if (!$scope.currentAttendance.id) {
            alert('Invalid attendance record');
            return;
        }
        
        AttendanceService.updateAttendance($scope.currentAttendance.id, $scope.currentAttendance)
            .then(function(response) {
                $scope.showEditModal = false;
                $scope.loadAttendance(); // Reload data
                alert('Attendance updated successfully');
            })
            .catch(function(error) {
                console.error('Error updating attendance:', error);
                alert('Error updating attendance: ' + (error.data?.message || error.message || 'Unknown error'));
            });
    };
    
    // Delete attendance record
    $scope.confirmDeleteAttendance = function(attendance) {
        $scope.currentAttendance = angular.copy(attendance);
        $scope.showDeleteModal = true;
    };
    
    $scope.deleteAttendance = function() {
        if (!$scope.currentAttendance.id) {
            alert('Invalid attendance record');
            return;
        }
        
        AttendanceService.deleteAttendance($scope.currentAttendance.id)
            .then(function(response) {
                $scope.showDeleteModal = false;
                $scope.loadAttendance(); // Reload data
                alert('Attendance deleted successfully');
            })
            .catch(function(error) {
                console.error('Error deleting attendance:', error);
                alert('Error deleting attendance: ' + (error.data?.message || error.message || 'Unknown error'));
            });
    };
    
    // Close modals
    $scope.closeModal = function() {
        $scope.showViewModal = false;
        $scope.showEditModal = false;
        $scope.showDeleteModal = false;
        $scope.currentAttendance = {};
    };
    
    // Mark attendance
    $scope.markAttendance = function() {
        // Implementation for marking attendance
        alert('Mark attendance functionality to be implemented');
    };
    
    // View student attendance history
    $scope.viewHistory = function(student) {
        // Implementation for viewing attendance history
        alert('View history functionality to be implemented');
    };
    
    // Export attendance data
    $scope.exportAttendance = function() {
        // Implementation for exporting attendance
        alert('Export attendance functionality to be implemented');
    };
    
    // Refresh data
    $scope.refreshData = function() {
        $scope.loadAttendance();
    };
    
    // Format date for display
    $scope.formatDate = function(dateString) {
        if (!dateString) return '';
        return new Date(dateString).toLocaleDateString();
    };
    
    // Format time for display
    $scope.formatTime = function(timeString) {
        if (!timeString) return '-';
        return timeString;
    };
    
    // Get status badge class
    $scope.getStatusClass = function(status) {
        switch(status) {
            case 'PRESENT': return 'bg-success';
            case 'ABSENT': return 'bg-danger';
            case 'LATE': return 'bg-warning';
            case 'EXCUSED': return 'bg-info';
            default: return 'bg-secondary';
        }
    };
    
    // Custom filter for grade level display
    $scope.$parent.gradeFormat = function(grade) {
        if (!grade) return '';
        return grade.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase());
    };
    
    // Initialize
    $scope.loadAttendance();
}]);