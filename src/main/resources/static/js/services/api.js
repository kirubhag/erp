// API Service - Base service for HTTP requests
angular.module('erpApp').service('ApiService', [
    '$http', '$q', 'APP_CONFIG',
    function($http, $q, APP_CONFIG) {
        
        const self = this;
        
        // Base configuration
        const config = {
            baseURL: APP_CONFIG.API_BASE_URL,
            timeout: 30000,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        };
        
        // Add authentication token if available
        self.setAuthToken = function(token) {
            if (token) {
                config.headers['Authorization'] = 'Bearer ' + token;
            } else {
                delete config.headers['Authorization'];
            }
        };
        
        // Generic HTTP methods
        self.get = function(url, params) {
            return $http({
                method: 'GET',
                url: config.baseURL + url,
                params: params,
                headers: config.headers,
                timeout: config.timeout
            }).then(
                function(response) {
                    return response;
                },
                function(error) {
                    return self.handleError(error);
                }
            );
        };
        
        self.post = function(url, data) {
            return $http({
                method: 'POST',
                url: config.baseURL + url,
                data: data,
                headers: config.headers,
                timeout: config.timeout
            }).then(
                function(response) {
                    return response;
                },
                function(error) {
                    return self.handleError(error);
                }
            );
        };
        
        self.put = function(url, data) {
            return $http({
                method: 'PUT',
                url: config.baseURL + url,
                data: data,
                headers: config.headers,
                timeout: config.timeout
            }).then(
                function(response) {
                    return response;
                },
                function(error) {
                    return self.handleError(error);
                }
            );
        };
        
        self.delete = function(url) {
            return $http({
                method: 'DELETE',
                url: config.baseURL + url,
                headers: config.headers,
                timeout: config.timeout
            }).then(
                function(response) {
                    return response;
                },
                function(error) {
                    return self.handleError(error);
                }
            );
        };
        
        self.patch = function(url, data) {
            return $http({
                method: 'PATCH',
                url: config.baseURL + url,
                data: data,
                headers: config.headers,
                timeout: config.timeout
            }).then(
                function(response) {
                    return response;
                },
                function(error) {
                    return self.handleError(error);
                }
            );
        };
        
        // File upload method
        self.upload = function(url, file, additionalData) {
            const formData = new FormData();
            formData.append('file', file);
            
            if (additionalData) {
                Object.keys(additionalData).forEach(function(key) {
                    formData.append(key, additionalData[key]);
                });
            }
            
            const uploadHeaders = angular.copy(config.headers);
            delete uploadHeaders['Content-Type']; // Let browser set Content-Type for FormData
            
            return $http({
                method: 'POST',
                url: config.baseURL + url,
                data: formData,
                headers: uploadHeaders,
                timeout: 0, // No timeout for file uploads
                transformRequest: angular.identity
            }).then(
                function(response) {
                    return response;
                },
                function(error) {
                    return self.handleError(error);
                }
            );
        };
        
        // Get paginated data
        self.getPage = function(url, page, size, sort) {
            var params = {
                page: page || 0,
                size: size || 10
            };
            
            if (sort) {
                params.sort = sort;
            }
            
            return $http.get(config.baseURL + url, { params: params })
                .then(function(response) {
                    return response.data;
                })
                .catch(function(error) {
                    console.error('API getPage error:', error);
                    throw error;
                });
        };
        
        // Search request with pagination
        self.search = function(url, searchTerm, page, size, sort) {
            const searchParams = {
                search: searchTerm,
                page: page || 0,
                size: size || APP_CONFIG.PAGINATION.DEFAULT_SIZE,
                sort: sort || 'id,asc'
            };
            
            return self.get(url, searchParams);
        };
        
        // Error handling
        self.handleError = function(error) {
            let errorMessage = 'An unexpected error occurred';
            let errorDetails = {};
            
            if (error.data) {
                if (error.data.message) {
                    errorMessage = error.data.message;
                } else if (error.data.error) {
                    errorMessage = error.data.error;
                }
                
                errorDetails = {
                    status: error.status,
                    statusText: error.statusText,
                    data: error.data,
                    timestamp: new Date().toISOString()
                };
            } else if (error.message) {
                errorMessage = error.message;
            }
            
            // Log error for debugging
            console.error('API Error:', errorDetails);
            
            // Create user-friendly error message
            const userError = self.createUserFriendlyError(error.status, errorMessage);
            
            return $q.reject({
                message: userError,
                details: errorDetails,
                originalError: error
            });
        };
        
        // Create user-friendly error messages
        self.createUserFriendlyError = function(status, message) {
            switch (status) {
                case 400:
                    return 'Invalid request. Please check your input and try again.';
                case 401:
                    return 'Authentication required. Please log in and try again.';
                case 403:
                    return 'You do not have permission to perform this action.';
                case 404:
                    return 'The requested resource was not found.';
                case 409:
                    return 'Conflict: ' + (message || 'The resource already exists or conflicts with existing data.');
                case 422:
                    return 'Validation error: ' + (message || 'Please check your input data.');
                case 429:
                    return 'Too many requests. Please wait and try again.';
                case 500:
                    return 'Server error. Please contact support if the problem persists.';
                case 503:
                    return 'Service temporarily unavailable. Please try again later.';
                case -1:
                    return 'Network error. Please check your connection and try again.';
                default:
                    return message || 'An unexpected error occurred. Please try again.';
            }
        };
        
        // Health check
        self.healthCheck = function() {
            return self.get('/actuator/health');
        };
        
        // Batch operations
        self.batchRequest = function(requests) {
            const promises = requests.map(function(request) {
                switch (request.method.toUpperCase()) {
                    case 'GET':
                        return self.get(request.url, request.params);
                    case 'POST':
                        return self.post(request.url, request.data);
                    case 'PUT':
                        return self.put(request.url, request.data);
                    case 'DELETE':
                        return self.delete(request.url);
                    case 'PATCH':
                        return self.patch(request.url, request.data);
                    default:
                        return $q.reject({ message: 'Unsupported HTTP method: ' + request.method });
                }
            });
            
            return $q.all(promises);
        };
        
        // Export data methods
        self.exportToCsv = function(url, filename) {
            return $http({
                method: 'GET',
                url: config.baseURL + url,
                headers: {
                    'Accept': 'text/csv',
                    'Authorization': config.headers.Authorization
                },
                responseType: 'blob'
            }).then(function(response) {
                // Create download link
                const blob = new Blob([response.data], { type: 'text/csv' });
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = filename || 'export.csv';
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                window.URL.revokeObjectURL(url);
                
                return response;
            });
        };
        
        self.exportToPdf = function(url, filename) {
            return $http({
                method: 'GET',
                url: config.baseURL + url,
                headers: {
                    'Accept': 'application/pdf',
                    'Authorization': config.headers.Authorization
                },
                responseType: 'blob'
            }).then(function(response) {
                // Create download link
                const blob = new Blob([response.data], { type: 'application/pdf' });
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = filename || 'export.pdf';
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                window.URL.revokeObjectURL(url);
                
                return response;
            });
        };
        
        // Utility functions
        self.buildQueryString = function(params) {
            return Object.keys(params)
                .filter(function(key) { return params[key] != null; })
                .map(function(key) {
                    return encodeURIComponent(key) + '=' + encodeURIComponent(params[key]);
                })
                .join('&');
        };
        
        self.isOnline = function() {
            return navigator.onLine;
        };
        
        return self;
    }
]);