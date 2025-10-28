// Settings Sidebar Directive
// Reusable sidebar component for all settings pages

angular.module('erpApp').directive('settingsSidebar', function() {
  return {
    restrict: 'E',
    templateUrl: '/templates/settings/settings-sidebar.html',
    replace: true
  };
});
