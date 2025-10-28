// Main Controller - Dashboard and navigation
angular.module('erpApp').controller('MainController', [
    '$scope', '$rootScope', '$location', 'ApiService', 'PerformanceMonitorService',
    function($scope, $rootScope, $location, ApiService, PerformanceMonitorService) {
        
        // Initialize controller
        $scope.init = function() {
            // Initialize performance monitoring
            PerformanceMonitorService.init();
            
            // Determine active tab based on current route
            var currentPath = $location.path();
            if (currentPath.indexOf('/students') === 0) {
                $scope.activeTab = 'students';
            } else if (currentPath.indexOf('/attendance') === 0) {
                $scope.activeTab = 'attendance';
            } else if (currentPath.indexOf('/parents') === 0) {
                $scope.activeTab = 'parents';
            } else if (currentPath.indexOf('/health') === 0) {
                $scope.activeTab = 'health';
            } else if (currentPath.indexOf('/settings') === 0) {
                $scope.activeTab = 'settings';
            } else {
                $scope.activeTab = 'dashboard';
            }
            
            $scope.loading = false;
            $scope.moduleLoading = false;
            $scope.toasts = [];
            $scope.currentUser = {
                name: 'Administrator',
                role: 'ADMIN',
                email: 'admin@school.edu'
            };
            
            // Initialize organization name
            $scope.organizationName = 'Student Information System';
            
            // Set current date/time for dashboard
            $scope.currentDate = new Date();
            $scope.currentDateTime = new Date();
            
            // Load organization details
            $scope.loadOrganizationDetails();
            
            $scope.loadDashboardData();
            
        };
        
        // Listen for route changes to keep activeTab in sync
        $scope.$on('$routeChangeStart', function(event, next, current) {
            if (!next) return;
            
            var nextPath = next.$$route ? next.$$route.originalPath : '';
            
            if (nextPath.indexOf('/students') === 0) {
                $scope.activeTab = 'students';
            } else if (nextPath.indexOf('/attendance') === 0) {
                $scope.activeTab = 'attendance';
            } else if (nextPath.indexOf('/parents') === 0) {
                $scope.activeTab = 'parents';
            } else if (nextPath.indexOf('/health') === 0) {
                $scope.activeTab = 'health';
            } else if (nextPath.indexOf('/settings') === 0) {
                $scope.activeTab = 'settings';
            } else {
                $scope.activeTab = 'dashboard';
            }
        });
        
        // Also listen for successful route changes
        $scope.$on('$routeChangeSuccess', function(event, current, previous) {
            // Route change handling if needed
        });
        
        // Navigation functions
        $scope.setActiveTab = function(tab) {
            $scope.activeTab = tab;
            $location.path('/' + (tab === 'dashboard' ? '' : tab));
        };
        
        // Expose setActiveTab to $rootScope for other controllers
        $rootScope.setActiveTab = $scope.setActiveTab;
        
        // Dashboard data
        $scope.dashboardStats = {
            totalStudents: 0,
            presentToday: 0,
            absentToday: 0,
            totalParents: 0
        };
        
        $scope.recentActivities = [];
        
        // Load dashboard data
        $scope.loadDashboardData = function() {
            $scope.loading = true;
            
            // Load student statistics
            ApiService.get('/students/count').then(function(response) {
                $scope.dashboardStats.totalStudents = response.data || 0;
            }).catch(function(error) {
                console.error('Error loading student count:', error);
            });
            
            // Load today's attendance statistics
            const today = new Date().toISOString().split('T')[0];
            ApiService.get('/attendance/statistics/date/' + today).then(function(response) {
                if (response.data) {
                    $scope.dashboardStats.presentToday = response.data.presentCount || 0;
                    $scope.dashboardStats.absentToday = response.data.absentCount || 0;
                }
            }).catch(function(error) {
                console.error('Error loading attendance statistics:', error);
            });
            
            // Load parent count
            ApiService.get('/parents/count').then(function(response) {
                $scope.dashboardStats.totalParents = response.data || 0;
            }).catch(function(error) {
                console.error('Error loading parent count:', error);
            });
            
            // Load recent activities
            $scope.loadRecentActivities();
            
            $scope.loading = false;
        };
        
        // Load organization details for navbar
        $scope.loadOrganizationDetails = function() {
            ApiService.get('/organization/details').then(function(response) {
                if (response.data && response.data.name) {
                    $scope.organizationName = response.data.name;
                }
            }).catch(function(error) {
                // Silently fail - organization endpoint may not exist yet
                // Keep default name 'Student Information System'
            });
        };
        
        // Load recent activities
        $scope.loadRecentActivities = function() {
            // This would typically come from an audit log or activity service
            $scope.recentActivities = [
                {
                    id: 1,
                    type: 'student_enrolled',
                    message: 'New student John Doe enrolled in 5th grade',
                    timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000), // 2 hours ago
                    icon: 'user-plus',
                    color: 'success'
                },
                {
                    id: 2,
                    type: 'attendance_marked',
                    message: 'Attendance marked for Grade 3 - Section A',
                    timestamp: new Date(Date.now() - 4 * 60 * 60 * 1000), // 4 hours ago
                    icon: 'calendar-check',
                    color: 'info'
                },
                {
                    id: 3,
                    type: 'parent_registered',
                    message: 'Parent Mary Smith registered for student portal',
                    timestamp: new Date(Date.now() - 6 * 60 * 60 * 1000), // 6 hours ago
                    icon: 'users',
                    color: 'primary'
                },
                {
                    id: 4,
                    type: 'health_record_updated',
                    message: 'Health record updated for student Jane Wilson',
                    timestamp: new Date(Date.now() - 8 * 60 * 60 * 1000), // 8 hours ago
                    icon: 'heartbeat',
                    color: 'warning'
                }
            ];
        };
        
        // Quick actions for dashboard
        $scope.quickActions = [
            {
                title: 'Add New Student',
                description: 'Register a new student in the system',
                icon: 'user-plus',
                action: function() { 
                    $scope.setActiveTab('students'); 
                    $rootScope.$broadcast('student:showAddForm');
                }
            },
            {
                title: 'Mark Attendance',
                description: 'Record daily attendance for classes',
                icon: 'calendar-check',
                action: function() { 
                    $scope.setActiveTab('attendance');
                    $rootScope.$broadcast('attendance:showMarkForm');
                }
            },
            {
                title: 'Parent Registration',
                description: 'Register new parent for portal access',
                icon: 'user-friends',
                action: function() { 
                    $scope.setActiveTab('parents');
                    $rootScope.$broadcast('parent:showAddForm');
                }
            },
            {
                title: 'Health Records',
                description: 'Manage student health information',
                icon: 'heartbeat',
                action: function() { 
                    $scope.setActiveTab('health');
                }
            }
        ];
        
        // Toast notification functions
        $scope.showToast = function(type, title, message) {
            const toast = {
                id: Date.now(),
                type: type,
                title: title,
                message: message,
                timestamp: new Date()
            };
            
            $scope.toasts.push(toast);
            
            // Auto-remove toast after 5 seconds
            setTimeout(function() {
                $scope.removeToast(toast.id);
                $scope.$apply();
            }, 5000);
        };
        
        $scope.removeToast = function(toastId) {
            $scope.toasts = $scope.toasts.filter(function(toast) {
                return toast.id !== toastId;
            });
        };
        
        $scope.getToastIcon = function(type) {
            const iconMap = {
                success: 'fa-check-circle',
                error: 'fa-exclamation-circle',
                warning: 'fa-exclamation-triangle',
                info: 'fa-info-circle'
            };
            return iconMap[type] || 'fa-info-circle';
        };
        
        // Logout function
        $scope.logout = function() {
            if (confirm('Are you sure you want to logout?')) {
                // Get CSRF token
                var csrfToken = getCookie('XSRF-TOKEN');
                
                // Create a form and submit it to logout endpoint
                var form = document.createElement('form');
                form.method = 'POST';
                form.action = '/logout';
                
                // Add CSRF token
                if (csrfToken) {
                    var csrfInput = document.createElement('input');
                    csrfInput.type = 'hidden';
                    csrfInput.name = '_csrf';
                    csrfInput.value = csrfToken;
                    form.appendChild(csrfInput);
                }
                
                document.body.appendChild(form);
                form.submit();
            }
        };
        
        // Helper function to get cookie
        function getCookie(name) {
            var value = "; " + document.cookie;
            var parts = value.split("; " + name + "=");
            if (parts.length === 2) {
                return parts.pop().split(";").shift();
            }
            return null;
        }
        
        // Global error handling
        $rootScope.$on('app:error', function(event, error) {
            $scope.showToast('error', 'Error', error.message || 'An unexpected error occurred.');
        });
        
        $rootScope.$on('app:success', function(event, message) {
            $scope.showToast('success', 'Success', message);
        });
        
        $rootScope.$on('app:warning', function(event, message) {
            $scope.showToast('warning', 'Warning', message);
        });
        
        $rootScope.$on('app:info', function(event, message) {
            $scope.showToast('info', 'Information', message);
        });
        
        // Utility functions
        $scope.getAttendanceRate = function() {
            const total = $scope.dashboardStats.presentToday + $scope.dashboardStats.absentToday;
            if (total === 0) return 0;
            return Math.round(($scope.dashboardStats.presentToday / total) * 100);
        };
        
        $scope.getAttendanceRateClass = function() {
            const rate = $scope.getAttendanceRate();
            if (rate >= 90) return 'text-success';
            if (rate >= 75) return 'text-warning';
            return 'text-danger';
        };
        
        $scope.formatTimeAgo = function(timestamp) {
            if (!timestamp) return '';
            
            const now = new Date();
            const diff = now - new Date(timestamp);
            const minutes = Math.floor(diff / 60000);
            const hours = Math.floor(minutes / 60);
            const days = Math.floor(hours / 24);
            
            if (days > 0) return days + ' day' + (days > 1 ? 's' : '') + ' ago';
            if (hours > 0) return hours + ' hour' + (hours > 1 ? 's' : '') + ' ago';
            if (minutes > 0) return minutes + ' minute' + (minutes > 1 ? 's' : '') + ' ago';
            return 'Just now';
        };
        
        // Refresh dashboard data
        $scope.refreshDashboard = function() {
            // Update current date/time
            $scope.currentDate = new Date();
            $scope.currentDateTime = new Date();
            
            $scope.loadDashboardData();
            $scope.showToast('info', 'Refreshed', 'Dashboard data has been refreshed.');
        };
        
        // Module extension mechanism - allows loaded modules to extend MainController
        $scope.extendController = function(extensionName, extensionFunctions) {

            angular.extend($scope, extensionFunctions);
        };

        // Settings functionality - will be available after settings module loads
        $scope.settingsReady = false;
        
        // Listen for settings module loaded event
        $scope.$on('settingsModuleLoaded', function() {
            $scope.settingsReady = true;

        });

        // Listen for route change events to show loading indicators
        $rootScope.$on('$routeChangeStart', function(event, next, current) {
            $scope.moduleLoading = true;
        });
        
        $rootScope.$on('$routeChangeSuccess', function(event, current, previous) {
            $scope.moduleLoading = false;
        });
        
        $rootScope.$on('$routeChangeError', function(event, current, previous, rejection) {
            $scope.moduleLoading = false;
            $scope.showToast('error', 'Loading Error', 'Failed to load page resources');
        });

        // Initialize controller when page loads
        $scope.init();
    }
]);