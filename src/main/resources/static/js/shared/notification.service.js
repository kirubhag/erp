// Notification Service - User feedback and alerts
(function() {
    'use strict';

    angular.module('erpApp')
        .factory('NotificationService', NotificationService);

    NotificationService.$inject = ['$timeout'];

    function NotificationService($timeout) {
        console.log('🔔 NotificationService initialized');

        var notifications = [];
        var nextId = 1;

        var service = {
            success: success,
            error: error,
            warning: warning,
            info: info,
            getNotifications: getNotifications,
            removeNotification: removeNotification,
            clearAll: clearAll
        };

        return service;

        /**
         * Show success notification
         */
        function success(message, title) {
            return addNotification('success', message, title);
        }

        /**
         * Show error notification
         */
        function error(message, title) {
            return addNotification('error', message, title || 'Error');
        }

        /**
         * Show warning notification
         */
        function warning(message, title) {
            return addNotification('warning', message, title || 'Warning');
        }

        /**
         * Show info notification
         */
        function info(message, title) {
            return addNotification('info', message, title || 'Information');
        }

        /**
         * Get all notifications
         */
        function getNotifications() {
            return notifications;
        }

        /**
         * Remove a specific notification
         */
        function removeNotification(id) {
            var index = notifications.findIndex(function(n) {
                return n.id === id;
            });
            
            if (index >= 0) {
                notifications.splice(index, 1);
            }
        }

        /**
         * Clear all notifications
         */
        function clearAll() {
            notifications.length = 0;
        }

        /**
         * Add a notification
         */
        function addNotification(type, message, title) {
            var notification = {
                id: nextId++,
                type: type,
                title: title,
                message: message,
                timestamp: new Date()
            };

            notifications.unshift(notification);

            // Auto-remove after delay (except errors)
            if (type !== 'error') {
                var delay = type === 'success' ? 3000 : 5000;
                $timeout(function() {
                    removeNotification(notification.id);
                }, delay);
            }

            console.log('🔔 Notification added:', notification);
            return notification;
        }
    }
})();