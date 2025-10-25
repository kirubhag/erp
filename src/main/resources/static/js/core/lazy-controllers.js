// Lazy Loading Controller Placeholders
// This file registers placeholder controllers that will be replaced when modules load

angular.module('erpApp')

// Placeholder AttendanceController - will be replaced by lazy-loaded one  
.controller('AttendanceController', ['$scope', function($scope) {

    $scope.loading = true;
    $scope.loadingMessage = 'Loading attendance module...';
}])

// Placeholder HealthController - will be replaced by lazy-loaded one
.controller('HealthController', ['$scope', function($scope) {

    $scope.loading = true;
    $scope.loadingMessage = 'Loading health module...';
}])

// Placeholder SettingsController - will be replaced by lazy-loaded one
.controller('SettingsController', ['$scope', function($scope) {

    $scope.loading = true;
    $scope.loadingMessage = 'Loading settings module...';
}])

// Placeholder EmailTemplateController - will be replaced by lazy-loaded one
.controller('EmailTemplateController', ['$scope', function($scope) {

    $scope.loading = true;
    $scope.loadingMessage = 'Loading email template module...';
}])

// Placeholder EmailLogController - will be replaced by lazy-loaded one
.controller('EmailLogController', ['$scope', function($scope) {

    $scope.loading = true;
    $scope.loadingMessage = 'Loading email log module...';
}]);

