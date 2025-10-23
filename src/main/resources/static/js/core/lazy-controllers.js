// Lazy Loading Controller Placeholders
// This file registers placeholder controllers that will be replaced when modules load

angular.module('erpApp')

// Placeholder AttendanceController - will be replaced by lazy-loaded one  
.controller('AttendanceController', ['$scope', function($scope) {
    console.log('📍 Placeholder AttendanceController - waiting for real controller to load...');
    $scope.loading = true;
    $scope.loadingMessage = 'Loading attendance module...';
}])

// Placeholder HealthController - will be replaced by lazy-loaded one
.controller('HealthController', ['$scope', function($scope) {
    console.log('📍 Placeholder HealthController - waiting for real controller to load...'); 
    $scope.loading = true;
    $scope.loadingMessage = 'Loading health module...';
}])

// Placeholder SettingsController - will be replaced by lazy-loaded one
.controller('SettingsController', ['$scope', function($scope) {
    console.log('📍 Placeholder SettingsController - waiting for real controller to load...');
    $scope.loading = true;
    $scope.loadingMessage = 'Loading settings module...';
}]);

console.log('✅ Lazy loading controller placeholders registered');