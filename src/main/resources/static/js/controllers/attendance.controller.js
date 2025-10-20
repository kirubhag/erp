angular.module('erpApp').controller('AttendanceController', ['$scope', 'AttendanceService', '$filter', function($scope, AttendanceService, $filter) {
    
    // Initialize scope variables
    $scope.attendanceRecords = [];
    $scope.attendanceStats = {};
    $scope.loading = false;
    $scope.selectedDate = new Date().toISOString().split('T')[0];
    $scope.selectedGrade = '';
    $scope.statusFilter = '';
    
    // Load attendance data
    $scope.loadAttendance = function() {
        if (!$scope.selectedDate) return;
        
        $scope.loading = true;
        
        // Load attendance statistics for the selected date
        AttendanceService.getStatisticsByDate($scope.selectedDate).then(function(response) {
            $scope.attendanceStats = response.data;
        }).catch(function(error) {
            console.error('Error loading attendance statistics:', error);
            $scope.attendanceStats = {
                presentStudents: 0,
                absentStudents: 0,
                lateStudents: 0,
                attendanceRate: 0
            };
        });
        
        // Load attendance records for the selected date
        AttendanceService.getByDate($scope.selectedDate).then(function(response) {
            $scope.attendanceRecords = response.data;
            $scope.loading = false;
        }).catch(function(error) {
            console.error('Error loading attendance records:', error);
            $scope.attendanceRecords = [];
            $scope.loading = false;
        });
    };
    
    // Filter by grade level
    $scope.filterByGrade = function() {
        if ($scope.selectedGrade && $scope.selectedDate) {
            $scope.loading = true;
            AttendanceService.getByGradeLevelAndDate($scope.selectedGrade, $scope.selectedDate).then(function(response) {
                $scope.attendanceRecords = response.data;
                $scope.loading = false;
            }).catch(function(error) {
                console.error('Error filtering by grade:', error);
                $scope.attendanceRecords = [];
                $scope.loading = false;
            });
        } else {
            $scope.loadAttendance();
        }
    };
    
    // Filter by status
    $scope.filterByStatus = function() {
        // Client-side filtering for now
        $scope.loadAttendance();
    };
    
    // Mark attendance
    $scope.markAttendance = function() {
        // Implementation for marking attendance
        console.log('Mark attendance functionality to be implemented');
    };
    
    // Edit attendance record
    $scope.editAttendance = function(attendance) {
        // Implementation for editing attendance
        console.log('Edit attendance:', attendance);
    };
    
    // View student attendance history
    $scope.viewHistory = function(student) {
        // Implementation for viewing attendance history
        console.log('View history for student:', student);
    };
    
    // Export attendance data
    $scope.exportAttendance = function() {
        // Implementation for exporting attendance
        console.log('Export attendance functionality to be implemented');
    };
    
    // Refresh data
    $scope.refreshData = function() {
        $scope.loadAttendance();
    };
    
    // Custom filter for grade level display
    $scope.$parent.gradeFormat = function(grade) {
        if (!grade) return '';
        return grade.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase());
    };
    
    // Initialize
    $scope.loadAttendance();
}]);