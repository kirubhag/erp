// CSRF Token Interceptor
angular.module('erpApp').factory('CsrfInterceptor', [function() {
    return {
        request: function(config) {
            // Get CSRF token from cookie
            var csrfToken = getCsrfToken();
            
            // Add CSRF token to headers for non-GET requests
            if (csrfToken && config.method !== 'GET') {
                config.headers['X-XSRF-TOKEN'] = csrfToken;
            }
            
            return config;
        }
    };
    
    function getCsrfToken() {
        // Try to get from cookie first
        var token = getCookie('XSRF-TOKEN');
        
        // If not in cookie, try to get from meta tag
        if (!token) {
            var meta = document.querySelector('meta[name="_csrf"]');
            if (meta) {
                token = meta.getAttribute('content');
            }
        }
        
        return token;
    }
    
    function getCookie(name) {
        var value = "; " + document.cookie;
        var parts = value.split("; " + name + "=");
        if (parts.length === 2) {
            return parts.pop().split(";").shift();
        }
        return null;
    }
}]);

// Configure the interceptor
angular.module('erpApp').config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('CsrfInterceptor');
}]);
