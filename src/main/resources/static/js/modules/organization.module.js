// Organization Module
angular.module('erpApp.organization', [])

.config(function() {
    console.log('🏢 Organization module loaded');
});

// Include the organization module in the main app
angular.module('erpApp').requires.push('erpApp.organization');