// Main AngularJS Application Configuration
angular.module('erpApp', ['ngRoute'])
.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider
        .when('/', {
            template: '<div ng-include="\'templates/dashboard.html\'"></div>',
            controller: 'MainController'
        })
        .when('/students', {
            template: '<div ng-include="\'templates/students.html\'"></div>',
            controller: 'StudentController'
        })
        .when('/attendance', {
            template: '<div ng-include="\'templates/attendance.html\'"></div>',
            controller: 'AttendanceController'
        })
        .when('/parents', {
            template: '<div ng-include="\'templates/parents.html\'"></div>',
            controller: 'ParentController'
        })
        .when('/health', {
            template: '<div ng-include="\'templates/health.html\'"></div>',
            controller: 'HealthController'
        })
        .otherwise({
            redirectTo: '/'
        });
}])
.run(['$rootScope', function($rootScope) {
    // Global application settings
    $rootScope.appName = 'Student Information System';
    $rootScope.version = '1.0.0';
    
    // Global utility functions
    $rootScope.formatDate = function(date) {
        if (!date) return '';
        try {
            if (typeof date === 'string') {
                // Handle ISO date strings like "2025-10-20"
                return new Date(date + 'T00:00:00').toLocaleDateString();
            }
            return new Date(date).toLocaleDateString();
        } catch (e) {
            return '';
        }
    };
    
    $rootScope.formatDateTime = function(date) {
        if (!date) return '';
        try {
            if (typeof date === 'string') {
                return new Date(date).toLocaleString();
            }
            return new Date(date).toLocaleString();
        } catch (e) {
            return '';
        }
    };
    
    $rootScope.getStatusBadge = function(status) {
        const statusMap = {
            'PRESENT': 'success',
            'ABSENT': 'danger',
            'LATE': 'warning',
            'EXCUSED': 'info',
            'ACTIVE': 'success',
            'INACTIVE': 'secondary',
            'PENDING': 'warning'
        };
        return statusMap[status] || 'secondary';
    };
}]);

// Global constants
angular.module('erpApp').constant('APP_CONFIG', {
    API_BASE_URL: '/api',
    DATE_FORMAT: 'yyyy-MM-dd',
    DATETIME_FORMAT: 'yyyy-MM-dd HH:mm:ss',
    PAGINATION: {
        DEFAULT_SIZE: 10,
        SIZE_OPTIONS: [5, 10, 20, 50]
    },
    GRADE_LEVELS: [
        'KINDERGARTEN', 'FIRST', 'SECOND', 'THIRD', 'FOURTH', 
        'FIFTH', 'SIXTH', 'SEVENTH', 'EIGHTH', 'NINTH', 
        'TENTH', 'ELEVENTH', 'TWELFTH'
    ],
    ATTENDANCE_STATUSES: ['PRESENT', 'ABSENT', 'LATE', 'EXCUSED'],
    USER_ROLES: ['ADMIN', 'TEACHER', 'STUDENT', 'PARENT', 'STAFF']
});

// Global filters
angular.module('erpApp').filter('capitalize', function() {
    return function(input) {
        if (!input) return '';
        return input.charAt(0).toUpperCase() + input.slice(1).toLowerCase();
    };
});

angular.module('erpApp').filter('gradeLevel', function() {
    return function(input) {
        const gradeMap = {
            'KINDERGARTEN': 'Kindergarten',
            'FIRST': '1st Grade',
            'SECOND': '2nd Grade',
            'THIRD': '3rd Grade',
            'FOURTH': '4th Grade',
            'FIFTH': '5th Grade',
            'SIXTH': '6th Grade',
            'SEVENTH': '7th Grade',
            'EIGHTH': '8th Grade',
            'NINTH': '9th Grade',
            'TENTH': '10th Grade',
            'ELEVENTH': '11th Grade',
            'TWELFTH': '12th Grade'
        };
        return gradeMap[input] || input;
    };
});

angular.module('erpApp').filter('phoneNumber', function() {
    return function(input) {
        if (!input) return '';
        const cleaned = input.replace(/\D/g, '');
        const match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match) {
            return '(' + match[1] + ') ' + match[2] + '-' + match[3];
        }
        return input;
    };
});

angular.module('erpApp').filter('range', function() {
    return function(input, start, end) {
        start = parseInt(start);
        end = parseInt(end);
        for (var i = start; i < end; i++) {
            input.push(i);
        }
        return input;
    };
});

// Custom date filter that handles string dates properly
angular.module('erpApp').filter('safeDate', function() {
    return function(input, format) {
        if (!input) return '';
        try {
            var date;
            if (typeof input === 'string') {
                // Handle ISO date strings
                date = new Date(input);
            } else {
                date = new Date(input);
            }
            
            if (isNaN(date.getTime())) {
                return input; // Return original if not a valid date
            }
            
            // Apply requested format
            if (format === 'short') {
                return date.toLocaleDateString();
            } else if (format === 'medium') {
                return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
            } else {
                return date.toLocaleDateString();
            }
        } catch (e) {
            return input;
        }
    };
});

// Global directives
angular.module('erpApp').directive('loadingSpinner', function() {
    return {
        restrict: 'E',
        template: `
            <div class="text-center" ng-show="loading">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <div class="mt-2 text-muted">{{message || 'Loading...'}}</div>
            </div>
        `,
        scope: {
            loading: '=',
            message: '@'
        }
    };
});

angular.module('erpApp').directive('confirmClick', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('click', function(e) {
                const message = attrs.confirmClick || 'Are you sure?';
                if (!confirm(message)) {
                    e.preventDefault();
                }
            });
        }
    };
});

angular.module('erpApp').directive('autoFocus', ['$timeout', function($timeout) {
    return {
        restrict: 'A',
        link: function(scope, element) {
            $timeout(function() {
                element[0].focus();
            });
        }
    };
}]);

// Date input directive to handle date formatting
angular.module('erpApp').directive('dateInput', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attrs, ngModel) {
            // Convert from model (Date or ISO string) to view (YYYY-MM-DD string)
            ngModel.$formatters.push(function(modelValue) {
                if (!modelValue) return '';
                if (typeof modelValue === 'string') {
                    return modelValue.split('T')[0]; // Handle ISO strings
                }
                if (modelValue instanceof Date) {
                    return modelValue.toISOString().split('T')[0];
                }
                return modelValue;
            });

            // Convert from view (YYYY-MM-DD string) to model
            ngModel.$parsers.push(function(viewValue) {
                return viewValue; // Keep as string for HTML5 date inputs
            });
        }
    };
});

// Error handling interceptor
angular.module('erpApp').factory('httpErrorInterceptor', ['$q', '$rootScope', function($q, $rootScope) {
    return {
        responseError: function(rejection) {
            if (rejection.status === 401) {
                $rootScope.$broadcast('auth:loginRequired');
            } else if (rejection.status === 403) {
                $rootScope.$broadcast('auth:forbidden');
            } else if (rejection.status >= 500) {
                $rootScope.$broadcast('app:serverError', rejection);
            }
            return $q.reject(rejection);
        }
    };
}]);

angular.module('erpApp').config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('httpErrorInterceptor');
}]);