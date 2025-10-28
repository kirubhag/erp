/**
 * Menu Service
 * Handles fetching and caching of dynamic menu items from the backend
 */
angular.module('erpApp').factory('MenuService', ['$http', '$q', 'NotificationService', function($http, $q, NotificationService) {
    var service = {
        menuItems: [],
        menuItemsLoaded: false
    };

    /**
     * Fetch all active menu items from the backend
     * @returns {Promise} Promise with menu items array
     */
    service.getMenuItems = function() {
        var deferred = $q.defer();
        
        // Return cached data if already loaded
        if (service.menuItemsLoaded) {
            deferred.resolve(service.menuItems);
            return deferred.promise;
        }

        $http.get('/api/erp-entities/menu-items')
            .then(function(response) {
                service.menuItems = response.data || [];
                service.menuItemsLoaded = true;
                deferred.resolve(service.menuItems);
            })
            .catch(function(error) {
                console.error('Error fetching menu items:', error);
                NotificationService.error('Failed to load menu items');
                deferred.reject(error);
            });

        return deferred.promise;
    };

    /**
     * Fetch menu items filtered by user roles
     * @param {Array} roleIds - Array of role IDs
     * @returns {Promise} Promise with filtered menu items array
     */
    service.getMenuItemsByRoles = function(roleIds) {
        var deferred = $q.defer();

        if (!roleIds || roleIds.length === 0) {
            // If no roles provided, return all menu items
            return service.getMenuItems();
        }

        $http.post('/api/erp-entities/menu-items/by-roles', roleIds)
            .then(function(response) {
                deferred.resolve(response.data || []);
            })
            .catch(function(error) {
                console.error('Error fetching menu items by roles:', error);
                NotificationService.error('Failed to load menu items');
                deferred.reject(error);
            });

        return deferred.promise;
    };

    /**
     * Clear cached menu items (useful after menu configuration changes)
     */
    service.clearCache = function() {
        service.menuItems = [];
        service.menuItemsLoaded = false;
    };

    /**
     * Refresh menu items from backend
     * @returns {Promise} Promise with updated menu items array
     */
    service.refresh = function() {
        service.clearCache();
        return service.getMenuItems();
    };

    return service;
}]);
