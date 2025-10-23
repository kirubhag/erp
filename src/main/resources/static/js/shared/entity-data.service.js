// Entity Data Service - Generic data service for all entities
(function() {
    'use strict';

    angular.module('erpApp')
        .factory('EntityDataService', EntityDataService);

    EntityDataService.$inject = ['$http', '$q'];

    function EntityDataService($http, $q) {
        console.log('🔌 EntityDataService initialized');

        var service = {
            loadData: loadData,
            loadCustomViews: loadCustomViews,
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
            console.log('📊 Loading data for entity type:', entityType, 'with params:', params);
            
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

            return $http.get(endpoint, {
                params: queryParams,
                timeout: 30000 // 30 second timeout
            }).then(function(response) {
                console.log('✅ Data loaded successfully for', entityType, ':', response.data);
                return response;
            }).catch(function(error) {
                console.error('❌ Error loading data for', entityType, ':', error);
                throw error;
            });
        }

        /**
         * Load custom views for an entity type
         */
        function loadCustomViews(entityType) {
            console.log('👁️ Loading custom views for entity type:', entityType);
            
            return $http.get('/api/custom-views', {
                params: {
                    entityType: entityType,
                    userId: getCurrentUserId()
                }
            }).then(function(response) {
                console.log('✅ Custom views loaded for', entityType, ':', response.data);
                return response;
            }).catch(function(error) {
                console.warn('⚠️ Could not load custom views for', entityType, ':', error);
                // Return empty array instead of failing
                return { data: [] };
            });
        }

        /**
         * Create a new item for the specified entity type
         */
        function createItem(entityType, itemData) {
            console.log('➕ Creating new item for entity type:', entityType, itemData);
            
            var endpoint = getEndpointForEntity(entityType);
            if (!endpoint) {
                return $q.reject('Unknown entity type: ' + entityType);
            }

            return $http.post(endpoint, itemData).then(function(response) {
                console.log('✅ Item created successfully for', entityType, ':', response.data);
                return response;
            }).catch(function(error) {
                console.error('❌ Error creating item for', entityType, ':', error);
                throw error;
            });
        }

        /**
         * Update an existing item
         */
        function updateItem(entityType, itemId, itemData) {
            console.log('📝 Updating item for entity type:', entityType, 'ID:', itemId, itemData);
            
            var endpoint = getEndpointForEntity(entityType);
            if (!endpoint) {
                return $q.reject('Unknown entity type: ' + entityType);
            }

            return $http.put(endpoint + '/' + itemId, itemData).then(function(response) {
                console.log('✅ Item updated successfully for', entityType, ':', response.data);
                return response;
            }).catch(function(error) {
                console.error('❌ Error updating item for', entityType, ':', error);
                throw error;
            });
        }

        /**
         * Delete an item
         */
        function deleteItem(entityType, itemId) {
            console.log('🗑️ Deleting item for entity type:', entityType, 'ID:', itemId);
            
            var endpoint = getEndpointForEntity(entityType);
            if (!endpoint) {
                return $q.reject('Unknown entity type: ' + entityType);
            }

            return $http.delete(endpoint + '/' + itemId).then(function(response) {
                console.log('✅ Item deleted successfully for', entityType);
                return response;
            }).catch(function(error) {
                console.error('❌ Error deleting item for', entityType, ':', error);
                throw error;
            });
        }

        /**
         * Export data in specified format
         */
        function exportData(entityType, format, data, filename) {
            console.log('📤 Exporting data for entity type:', entityType, 'Format:', format);
            
            var endpoint = getEndpointForEntity(entityType) + '/export';
            
            return $http.post(endpoint, {
                format: format,
                data: data,
                filename: filename
            }, {
                responseType: 'blob'
            }).then(function(response) {
                console.log('✅ Data exported successfully for', entityType);
                
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
                console.error('❌ Error exporting data for', entityType, ':', error);
                throw error;
            });
        }

        /**
         * Import data from file
         */
        function importData(entityType, file, options) {
            console.log('📥 Importing data for entity type:', entityType, 'File:', file.name);
            
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
                console.log('✅ Data imported successfully for', entityType, ':', response.data);
                return response;
            }).catch(function(error) {
                console.error('❌ Error importing data for', entityType, ':', error);
                throw error;
            });
        }

        /**
         * Get field definitions for an entity type
         */
        function getFieldDefinitions(entityType) {
            console.log('📋 Loading field definitions for entity type:', entityType);
            
            return $http.get('/api/fields/' + entityType).then(function(response) {
                console.log('✅ Field definitions loaded for', entityType, ':', response.data);
                return response;
            }).catch(function(error) {
                console.warn('⚠️ Could not load field definitions for', entityType, ':', error);
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
                console.error('❌ No endpoint defined for entity type:', entityType);
                return null;
            }
            
            return endpoint;
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