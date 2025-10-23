// Entity Data Service - Generic data service for all entities
(function() {
    'use strict';

    angular.module('erpApp')
        .factory('EntityDataService', EntityDataService);

    EntityDataService.$inject = ['$http', '$q'];

    function EntityDataService($http) {

        var service = {
            loadData: loadData,
            loadCustomViews: loadCustomViews,
            loadFieldsGrouped: loadFieldsGrouped,
            createItem: createItem,
            updateItem: updateItem,
            deleteItem: deleteItem,
            exportData: exportData,
            importData: importData,
            getFieldDefinitions: getFieldDefinitions
        };

        return service;

        /**
         * Load data for a specific entity type
         */
        function loadData(entityType, params) {
            
            var endpoint = getEndpointForEntity(entityType);
            if (!endpoint) {
                return $q.reject('Unknown entity type: ' + entityType);
            }

            // Build query parameters
            var queryParams = {};
            
            if (params.page !== undefined) {
                queryParams.page = params.page;
            }
            if (params.size !== undefined) {
                queryParams.size = params.size;
            }
            if (params.search) {
                queryParams.search = params.search;
            }
            if (params.sort && params.direction) {
                queryParams.sort = params.sort + ',' + params.direction;
            }
            
            // Add filter parameters
            if (params.filters) {
                Object.keys(params.filters).forEach(function(key) {
                    if (params.filters[key]) {
                        queryParams[key] = params.filters[key];
                    }
                });
            }

            return $http.get(endpoint, { params: queryParams })
                .then(function(response) {
                    return response;
                })
                .catch(function(error) {
                    throw error;
                });
        }

        /**
         * Load custom views for an entity type
         */
        function loadCustomViews(entityType) {
            var requestParams = {
                entityType: entityType,
                userId: 1 // Default user ID - this should be passed from the session or config
            };
            
            return $http.get('/api/custom-views', { params: requestParams })
                .then(function(response) {
                    return response;
                })
                .catch(function(error) {
                    // Return empty array instead of throwing error
                    return { data: [] };
                });
        }

        /**
         * Create a new item for the specified entity type
         */
        function createItem(entityType, itemData) {
            
            var endpoint = getEndpointForEntity(entityType);
            if (!endpoint) {
                return $q.reject('Unknown entity type: ' + entityType);
            }

            return $http.post(endpoint, itemData)
                .then(function(response) {
                    return response;
                })
                .catch(function(error) {
                    throw error;
                });
        }

        /**
         * Update an existing item
         */
        function updateItem(entityType, itemId, itemData) {
            
            var endpoint = getEndpointForEntity(entityType);
            if (!endpoint) {
                return $q.reject('Unknown entity type: ' + entityType);
            }

            return $http.put(endpoint + '/' + itemId, itemData)
                .then(function(response) {
                    return response;
                })
                .catch(function(error) {
                    throw error;
                });
        }

        /**
         * Delete an item
         */
        function deleteItem(entityType, itemId) {
            
            var endpoint = getEndpointForEntity(entityType);
            if (!endpoint) {
                return $q.reject('Unknown entity type: ' + entityType);
            }

            return $http.delete(endpoint + '/' + itemId)
                .then(function(response) {
                    return response;
                })
                .catch(function(error) {
                    throw error;
                });
        }

        /**
         * Export data in specified format
         */
                function exportData(entityType, format, data) {
            
            var endpoint = getEndpointForEntity(entityType) + '/export';
            
            return $http.post(endpoint, {
                format: format,
                data: data,
                filename: filename
            }, {
                responseType: 'blob'
            }).then(function(response) {
                // Create download link
                var blob = new Blob([response.data], { 
                    type: response.headers('Content-Type') 
                });
                var url = window.URL.createObjectURL(blob);
                
                // Create and click download link
                var link = document.createElement('a');
                link.href = url;
                link.download = filename || (entityType.toLowerCase() + '_export.' + format);
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                window.URL.revokeObjectURL(url);
                
                return response;
            }).catch(function(error) {
                throw error;
            });
        }

        /**
         * Import data from file
         */
        function importData(entityType, file) {
            
            var endpoint = getEndpointForEntity(entityType) + '/import';
            
            var formData = new FormData();
            formData.append('file', file);
            
            if (options) {
                Object.keys(options).forEach(function(key) {
                    formData.append(key, options[key]);
                });
            }

            return $http.post(endpoint, formData, {
                transformRequest: angular.identity,
                headers: { 'Content-Type': undefined }
            }).then(function(response) {
                return response;
            }).catch(function(error) {
                throw error;
            });
        }

        /**
         * Get field definitions for an entity type
         */
        function getFieldDefinitions(entityType) {
            return $http.get('/api/fields/' + entityType).then(function(response) {
                return response;
            }).catch(function(error) {
                return { data: [] };
            });
        }

        /**
         * Get API endpoint for entity type
         */
        function getEndpointForEntity(entityType) {
            var endpoints = {
                'STUDENT': '/api/students',
                'PARENT': '/api/parents',
                'STAFF': '/api/staff',
                'ATTENDANCE': '/api/attendance',
                'HEALTH': '/api/health-records',
                'USER': '/api/users',
                'ORGANIZATION': '/api/organizations',
                'PERMISSION': '/api/permissions',
                'ROLE': '/api/roles',
                'EMAIL_TEMPLATE': '/api/email-templates'
            };
            
            var endpoint = endpoints[entityType.toUpperCase()];
            
            if (!endpoint) {
                return null;
            }
            
            return endpoint;
        }

        /**
         * Load entity fields grouped by category
         */
        function loadFieldsGrouped(entityType) {
            if (!entityType) {
                return $q.reject('Entity type is required');
            }

            var requestParams = {
                method: 'GET',
                url: '/api/fields/' + entityType + '/grouped',
                headers: {
                    'Content-Type': 'application/json'
                }
            };

            return $http(requestParams);
        }

        /**
         * Get current user ID (placeholder - should be implemented based on auth system)
         */
        function getCurrentUserId() {
            // TODO: Implement based on your authentication system
            // For now, return a default value
            return 1;
        }
    }
})();