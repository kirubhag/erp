// Notification Directive - Displays notifications from NotificationService
(function() {
    'use strict';

    angular.module('erpApp')
        .directive('notificationDisplay', notificationDisplay);

    notificationDisplay.$inject = ['NotificationService'];

    function notificationDisplay(NotificationService) {
        return {
            restrict: 'E',
            template: `
                <div class="notification-container">
                    <div class="notification alert alert-dismissible fade show" 
                         ng-repeat="notification in notifications track by notification.id"
                         ng-class="{
                             'alert-success': notification.type === 'success',
                             'alert-danger': notification.type === 'error',
                             'alert-warning': notification.type === 'warning',
                             'alert-info': notification.type === 'info'
                         }"
                         role="alert">
                        <div class="d-flex align-items-center">
                            <i class="fas me-2" ng-class="{
                                'fa-check-circle': notification.type === 'success',
                                'fa-exclamation-circle': notification.type === 'error',
                                'fa-exclamation-triangle': notification.type === 'warning',
                                'fa-info-circle': notification.type === 'info'
                            }"></i>
                            <div class="flex-grow-1">
                                <strong ng-if="notification.title">{{notification.title}}: </strong>
                                <span>{{notification.message}}</span>
                            </div>
                            <button type="button" class="btn-close" ng-click="removeNotification(notification.id)"></button>
                        </div>
                    </div>
                </div>
            `,
            link: function(scope) {
                scope.notifications = NotificationService.getNotifications();
                
                scope.removeNotification = function(id) {
                    NotificationService.removeNotification(id);
                };
            }
        };
    }
})();
