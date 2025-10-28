// Settings Sidebar JavaScript
// Common functionality for all settings pages

// Settings Sidebar Controller - mixin for all settings pages
angular.module('erpApp').run(['$rootScope', '$location', function($rootScope, $location) {
  
  // Add isActivePage function to all scopes
  $rootScope.isActivePage = function(page) {
    var currentPath = $location.path();
    // Match exact page or any sub-path
    return currentPath.indexOf('/settings/' + page) === 0 || 
           currentPath === '/settings/' + page;
  };
  
  // Toggle section expansion
  $rootScope.toggleSection = function(section) {
    if (!$rootScope.expandedSections) {
      $rootScope.expandedSections = {};
    }
    $rootScope.expandedSections[section] = !$rootScope.expandedSections[section];
  };
  
  // Initialize all sections as expanded by default
  if (!$rootScope.expandedSections) {
    $rootScope.expandedSections = {
      general: true,
      users: true,
      customization: true,
      portal: true,
      data: true,
      developer: true
    };
  }
}]);
