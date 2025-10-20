// Main Controller - Handles global application state and navigation
angular.module('studentApp').controller('MainController', [
    '$scope', '$rootScope', '$location', 'ApiService',
    function($scope, $rootScope, $location, ApiService) {
        
        // Initialize controller
        $scope.init = function() {
            $scope.activeTab = 'dashboard';
            $scope.loading = false;
            $scope.toasts = [];
            $scope.currentUser = {
                name: 'Administrator',
                role: 'ADMIN',
                email: 'admin@school.edu'
            };
            
            $scope.loadDashboardData();
        };
        
        // Navigation functions
        $scope.setActiveTab = function(tab) {
            $scope.activeTab = tab;
            $location.path('/' + (tab === 'dashboard' ? '' : tab));
        };
        
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
                // Clear user session and redirect to login
                $scope.currentUser = null;
                $location.path('/login');
                $scope.showToast('info', 'Logged Out', 'You have been successfully logged out.');
            }
        };
        
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
            $scope.loadDashboardData();
            $scope.showToast('info', 'Refreshed', 'Dashboard data has been refreshed.');
        };
        
        // Initialize controller when page loads
        $scope.init();
    }
]);