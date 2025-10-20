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
.run(['$rootScope', '$locale', function($rootScope, $locale) {
    // Global application settings
    $rootScope.appName = 'Student Information System';
    $rootScope.version = '1.0.0';
    
    // Override AngularJS date formatting to prevent ngModel:datefmt errors
    if ($locale && $locale.DATETIME_FORMATS) {
        // Set consistent date formats
        $locale.DATETIME_FORMATS.shortDate = 'yyyy-MM-dd';
        $locale.DATETIME_FORMATS.mediumDate = 'MMM d, y';
        $locale.DATETIME_FORMATS.longDate = 'MMMM d, y';
    }
    
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
        'KINDERGARTEN', 'GRADE_1', 'GRADE_2', 'GRADE_3', 'GRADE_4', 
        'GRADE_5', 'GRADE_6', 'GRADE_7', 'GRADE_8', 'GRADE_9', 
        'GRADE_10', 'GRADE_11', 'GRADE_12'
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
            'GRADE_1': '1st Grade',
            'GRADE_2': '2nd Grade',
            'GRADE_3': '3rd Grade',
            'GRADE_4': '4th Grade',
            'GRADE_5': '5th Grade',
            'GRADE_6': '6th Grade',
            'GRADE_7': '7th Grade',
            'GRADE_8': '8th Grade',
            'GRADE_9': '9th Grade',
            'GRADE_10': '10th Grade',
            'GRADE_11': '11th Grade',
            'GRADE_12': '12th Grade'
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

// Override AngularJS date filter to prevent date formatting errors
angular.module('erpApp').filter('date', function() {
    return function(input, format) {
        if (!input) return '';
        
        try {
            let date;
            
            // Handle string dates
            if (typeof input === 'string') {
                // Handle YYYY-MM-DD format (LocalDate from backend)
                if (/^\d{4}-\d{2}-\d{2}$/.test(input)) {
                    date = new Date(input + 'T00:00:00');
                } else {
                    date = new Date(input);
                }
            } else if (input instanceof Date) {
                date = input;
            } else {
                return input;
            }
            
            // Validate date
            if (isNaN(date.getTime())) {
                return input;
            }
            
            // Default format
            if (!format || format === 'shortDate' || format === 'yyyy-MM-dd') {
                return date.toLocaleDateString();
            }
            
            // Medium date format
            if (format === 'mediumDate') {
                return date.toLocaleDateString('en-US', { 
                    year: 'numeric', 
                    month: 'short', 
                    day: 'numeric' 
                });
            }
            
            // Long date format
            if (format === 'longDate') {
                return date.toLocaleDateString('en-US', { 
                    weekday: 'long',
                    year: 'numeric', 
                    month: 'long', 
                    day: 'numeric' 
                });
            }
            
            // Default fallback
            return date.toLocaleDateString();
            
        } catch (e) {
            console.warn('Date filter error:', e);
            return input;
        }
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
            // Completely override the date validator to prevent ngModel:datefmt errors
            delete ngModel.$validators.date;
            
            // Set element type to date for proper HTML5 date input behavior
            element.attr('type', 'date');
            
            // Clear existing formatters and parsers to start fresh
            ngModel.$formatters.length = 0;
            ngModel.$parsers.length = 0;
            
            // Convert from model (Date or ISO string) to view (YYYY-MM-DD string)
            ngModel.$formatters.push(function(modelValue) {
                if (!modelValue) return '';
                
                try {
                    // Handle string dates
                    if (typeof modelValue === 'string') {
                        // Extract date part from ISO string or validate existing format
                        const dateStr = modelValue.includes('T') ? modelValue.split('T')[0] : modelValue;
                        if (/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) {
                            return dateStr;
                        }
                    }
                    
                    // Handle Date objects
                    if (modelValue instanceof Date && !isNaN(modelValue.getTime())) {
                        const year = modelValue.getFullYear();
                        const month = String(modelValue.getMonth() + 1).padStart(2, '0');
                        const day = String(modelValue.getDate()).padStart(2, '0');
                        return `${year}-${month}-${day}`;
                    }
                } catch (e) {
                    console.warn('Date formatting error:', e);
                }
                
                return '';
            });

            // Convert from view (YYYY-MM-DD string) to model
            ngModel.$parsers.push(function(viewValue) {
                if (!viewValue) return null;
                
                // Validate and return YYYY-MM-DD format for backend compatibility
                if (/^\d{4}-\d{2}-\d{2}$/.test(viewValue)) {
                    return viewValue;
                }
                
                return null;
            });
            
            // Add custom validation
            ngModel.$validators.customDate = function(modelValue, viewValue) {
                const value = modelValue || viewValue;
                if (!value) return true; // Empty is valid
                
                // Check if it's a valid date format
                if (typeof value === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(value)) {
                    const date = new Date(value + 'T00:00:00');
                    return !isNaN(date.getTime());
                }
                
                return false;
            };
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

// Global exception handler to suppress AngularJS date format errors
angular.module('erpApp').config(['$provide', function($provide) {
    $provide.decorator('$exceptionHandler', ['$delegate', function($delegate) {
        return function(exception, cause) {
            // Suppress ngModel:datefmt errors completely
            if (exception && exception.message && 
                (exception.message.includes('ngModel:datefmt') || 
                 exception.message.includes('datefmt'))) {
                console.warn('AngularJS date format error suppressed - using custom date handling');
                return; // Completely suppress this error
            }
            
            // Let other errors through to the default handler
            $delegate(exception, cause);
        };
    }]);
}]);

angular.module('erpApp').config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('httpErrorInterceptor');
}]);