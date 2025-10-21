/**
 * Test Service to verify dependency injection
 */
angular.module('erpApp').service('TestService', ['$http', function($http) {
    return {
        test: function() {
            return 'Test service works';
        }
    };
}]);