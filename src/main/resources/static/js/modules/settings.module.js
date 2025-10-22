// Settings Module Bundle
// This file contains all settings-related dependencies for lazy loading

// Load settings module scripts in the correct order
(function() {
    'use strict';
    
    // Settings module configuration
    angular.module('erpApp').config(['$provide', function($provide) {
        // Module-specific configurations can go here
    }]);

    // Module loading status tracker
    angular.module('erpApp').run(['$rootScope', function($rootScope) {
        $rootScope.settingsModuleLoaded = true;
    }]);
})();