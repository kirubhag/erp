// Generic Entity List Controller - Reusable for all entities
(function() {
    'use strict';

    angular.module('erpApp')
        .controller('EntityListController', EntityListController);

    EntityListController.$inject = [
        '$scope', 
        '$location', 
        '$timeout', 
        '$q',
        'EntityConfigService',
        'EntityDataService',
        'NotificationService'
    ];

    function EntityListController($scope, $location, $timeout, $q, EntityConfigService, EntityDataService, NotificationService) {
        console.log('🎯 EntityListController initialized');

        // Initialize scope variables
        initializeScope();
        
        // Load entity configuration based on current route
        loadEntityConfig();
        
        // Initialize controller
        initialize();

        /**
         * Initialize scope variables and default values
         */
        function initializeScope() {
            // Core data
            $scope.loading = false;
            $scope.filteredData = [];
            $scope.selectedItems = [];
            
            // View state
            $scope.viewMode = 'table'; // 'table' or 'grid'
            $scope.showViewDropdown = false;
            $scope.showExportDropdown = false;
            
            // Search and filtering
            $scope.searchQuery = '';
            $scope.appliedFilters = {};
            
            // Pagination
            $scope.pagination = {
                currentPage: 0,
                size: 100,
                totalElements: 0,
                totalPages: 0
            };
            $scope.pageSizeOptions = [25, 50, 100, 200];
            
            // Sorting
            $scope.sortField = null;
            $scope.sortDirection = 'asc';
            
            // Views
            $scope.currentView = null;
            $scope.customViews = [];
            $scope.visibleColumns = [];
        }

        /**
         * Load entity configuration based on current route
         */
        function loadEntityConfig() {
            var path = $location.path();
            var entityType = extractEntityTypeFromPath(path);
            
            console.log('🔍 Loading config for entity type:', entityType);
            
            $scope.config = EntityConfigService.getConfig(entityType);
            
            if (!$scope.config) {
                console.error('❌ No configuration found for entity type:', entityType);
                return;
            }
            
            console.log('✅ Entity config loaded:', $scope.config);
        }

        /**
         * Extract entity type from URL path
         */
        function extractEntityTypeFromPath(path) {
            // Examples: /students -> STUDENT, /parents -> PARENT, /staff -> STAFF
            var segments = path.split('/').filter(function(segment) {
                return segment.length > 0;
            });
            
            if (segments.length === 0) return null;
            
            var entityPath = segments[0];
            
            // Convert plural forms to singular and uppercase
            var entityMap = {
                'students': 'STUDENT',
                'parents': 'PARENT',
                'staff': 'STAFF',
                'attendance': 'ATTENDANCE',
                'health': 'HEALTH',
                'users': 'USER',
                'organizations': 'ORGANIZATION',
                'permissions': 'PERMISSION',
                'roles': 'ROLE',
                'email-templates': 'EMAIL_TEMPLATE'
            };
            
            return entityMap[entityPath] || entityPath.toUpperCase();
        }

        /**
         * Initialize the controller
         */
        function initialize() {
            if (!$scope.config) return;
            
            // Load initial data
            loadEntityData();
            
            // Load custom views if enabled
            if ($scope.config.features.customViews) {
                loadCustomViews();
            }
            
            // Set up default visible columns
            setupDefaultColumns();
        }

        /**
         * Load entity data from server
         */
        function loadEntityData() {
            if (!$scope.config) return;
            
            $scope.loading = true;
            
            var params = {
                page: $scope.pagination.currentPage,
                size: $scope.pagination.size,
                search: $scope.searchQuery,
                sort: $scope.sortField,
                direction: $scope.sortDirection,
                filters: $scope.appliedFilters
            };
            
            EntityDataService.loadData($scope.config.entityType, params)
                .then(function(response) {
                    console.log('✅ Data loaded successfully:', response.data);
                    
                    $scope.filteredData = response.data.content || response.data.data || response.data || [];
                    
                    // Update pagination info
                    if (response.data.pageable !== undefined) {
                        $scope.pagination.totalElements = response.data.totalElements || 0;
                        $scope.pagination.totalPages = response.data.totalPages || 0;
                        $scope.pagination.currentPage = response.data.number || 0;
                    } else {
                        // Handle non-paginated response
                        $scope.pagination.totalElements = $scope.filteredData.length;
                        $scope.pagination.totalPages = 1;
                        $scope.pagination.currentPage = 0;
                    }
                    
                    console.log('📊 Pagination updated:', $scope.pagination);
                })
                .catch(function(error) {
                    console.error('❌ Error loading data:', error);
                    NotificationService.error('Failed to load ' + $scope.config.entityName + 's');
                    $scope.filteredData = [];
                })
                .finally(function() {
                    $scope.loading = false;
                });
        }

        /**
         * Load custom views for the entity
         */
        function loadCustomViews() {
            EntityDataService.loadCustomViews($scope.config.entityType)
                .then(function(response) {
                    $scope.customViews = response.data || [];
                    console.log('📋 Custom views loaded:', $scope.customViews);
                })
                .catch(function(error) {
                    console.warn('⚠️ Could not load custom views:', error);
                    $scope.customViews = [];
                });
        }

        /**
         * Setup default visible columns
         */
        function setupDefaultColumns() {
            if ($scope.currentView && $scope.currentView.columns) {
                $scope.visibleColumns = $scope.currentView.columns;
            } else {
                $scope.visibleColumns = $scope.config.defaultColumns || [];
            }
        }

        // Scope methods for template interaction

        /**
         * Toggle view dropdown visibility
         */
        $scope.toggleViewDropdown = function() {
            $scope.showViewDropdown = !$scope.showViewDropdown;
            $scope.showExportDropdown = false; // Close other dropdowns
        };

        /**
         * Toggle export dropdown visibility
         */
        $scope.toggleExportDropdown = function() {
            $scope.showExportDropdown = !$scope.showExportDropdown;
            $scope.showViewDropdown = false; // Close other dropdowns
        };

        /**
         * Select a custom view
         */
        $scope.selectView = function(view) {
            $scope.currentView = view;
            $scope.showViewDropdown = false;
            
            if (view) {
                console.log('🎯 Applying custom view:', view);
                // Apply view-specific columns and filters
                if (view.columns) {
                    $scope.visibleColumns = view.columns;
                }
                if (view.filters) {
                    $scope.appliedFilters = angular.copy(view.filters);
                    loadEntityData(); // Reload with new filters
                }
            } else {
                // Reset to default view
                setupDefaultColumns();
                $scope.appliedFilters = {};
                loadEntityData();
            }
        };

        /**
         * Perform search
         */
        $scope.performSearch = function() {
            console.log('🔍 Performing search:', $scope.searchQuery);
            $scope.pagination.currentPage = 0; // Reset to first page
            loadEntityData();
        };

        /**
         * Set view mode (table or grid)
         */
        $scope.setViewMode = function(mode) {
            $scope.viewMode = mode;
            console.log('👁️ View mode changed to:', mode);
        };

        /**
         * Check if any items are selected
         */
        $scope.hasSelectedItems = function() {
            return $scope.selectedItems.length > 0;
        };

        /**
         * Toggle select all items
         */
        $scope.toggleSelectAll = function() {
            if ($scope.isAllSelected()) {
                $scope.selectedItems = [];
            } else {
                $scope.selectedItems = angular.copy($scope.filteredData);
            }
        };

        /**
         * Check if all items are selected
         */
        $scope.isAllSelected = function() {
            return $scope.filteredData.length > 0 && 
                   $scope.selectedItems.length === $scope.filteredData.length;
        };

        /**
         * Check if an item is selected
         */
        $scope.isSelected = function(item) {
            return $scope.selectedItems.some(function(selected) {
                return selected.id === item.id;
            });
        };

        /**
         * Toggle item selection
         */
        $scope.toggleSelection = function(item, $event) {
            if ($event) {
                $event.stopPropagation();
            }
            
            var index = $scope.selectedItems.findIndex(function(selected) {
                return selected.id === item.id;
            });
            
            if (index >= 0) {
                $scope.selectedItems.splice(index, 1);
            } else {
                $scope.selectedItems.push(item);
            }
        };

        /**
         * Clear all selections
         */
        $scope.clearSelection = function() {
            $scope.selectedItems = [];
        };

        /**
         * Handle row click
         */
        $scope.handleRowClick = function(item, $event) {
            // Don't select if clicking on checkbox or action button
            if ($event.target.type === 'checkbox' || 
                $event.target.closest('.action-btn') || 
                $event.target.closest('.more-btn')) {
                return;
            }
            
            $scope.toggleSelection(item);
        };

        /**
         * Sort by field
         */
        $scope.sortBy = function(field) {
            if ($scope.sortField === field) {
                $scope.sortDirection = $scope.sortDirection === 'asc' ? 'desc' : 'asc';
            } else {
                $scope.sortField = field;
                $scope.sortDirection = 'asc';
            }
            
            console.log('🔄 Sorting by:', field, $scope.sortDirection);
            loadEntityData();
        };

        /**
         * Get field value from item using dot notation
         */
        $scope.getFieldValue = function(item, fieldPath) {
            if (!item || !fieldPath) return '';
            
            var value = fieldPath.split('.').reduce(function(obj, key) {
                return obj && obj[key];
            }, item);
            
            return value || '';
        };

        /**
         * Get avatar initials for an item
         */
        $scope.getAvatarInitials = function(item) {
            if (!item) return '';
            
            var name = '';
            if (item.firstName && item.lastName) {
                name = item.firstName.charAt(0) + item.lastName.charAt(0);
            } else if (item.name) {
                var parts = item.name.split(' ');
                name = parts[0].charAt(0) + (parts[1] ? parts[1].charAt(0) : '');
            } else if (item.title) {
                name = item.title.charAt(0);
            }
            
            return name.toUpperCase();
        };

        /**
         * Get avatar background color style
         */
        $scope.getAvatarStyle = function(item) {
            if (!item || !item.id) return {};
            
            var colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];
            var colorIndex = item.id % colors.length;
            
            return {
                'background-color': colors[colorIndex]
            };
        };

        /**
         * Get status badge CSS class
         */
        $scope.getStatusClass = function(item, field) {
            var status = $scope.getFieldValue(item, field);
            if (!status) return '';
            
            var statusLower = status.toString().toLowerCase();
            
            if (statusLower.includes('active') || statusLower.includes('approved')) {
                return 'status-badge active';
            } else if (statusLower.includes('inactive') || statusLower.includes('rejected')) {
                return 'status-badge inactive';
            } else if (statusLower.includes('pending') || statusLower.includes('waiting')) {
                return 'status-badge pending';
            } else if (statusLower.includes('new')) {
                return 'status-badge new';
            }
            
            return 'status-badge';
        };

        /**
         * Format date for display
         */
        $scope.formatDate = function(dateStr) {
            if (!dateStr) return '';
            
            var date = new Date(dateStr);
            if (isNaN(date.getTime())) return '';
            
            return date.toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });
        };

        /**
         * Execute an action on an item
         */
        $scope.executeAction = function(action, item, $event) {
            if ($event) {
                $event.stopPropagation();
            }
            
            console.log('🎬 Executing action:', action.name, 'on item:', item);
            
            if (action.handler) {
                action.handler(item, $scope);
            } else {
                // Default handlers based on action type
                switch (action.name) {
                    case 'view':
                        $scope.viewItem(item);
                        break;
                    case 'edit':
                        $scope.editItem(item);
                        break;
                    case 'delete':
                        $scope.deleteItem(item);
                        break;
                    default:
                        console.warn('⚠️ No handler for action:', action.name);
                }
            }
        };

        /**
         * Execute bulk action
         */
        $scope.executeBulkAction = function(action) {
            if ($scope.selectedItems.length === 0) {
                NotificationService.warning('Please select items first');
                return;
            }
            
            console.log('🎬 Executing bulk action:', action.name, 'on items:', $scope.selectedItems);
            
            if (action.handler) {
                action.handler($scope.selectedItems, $scope);
            } else {
                console.warn('⚠️ No handler for bulk action:', action.name);
            }
        };

        /**
         * Go to specific page
         */
        $scope.goToPage = function(page) {
            if (page >= 0 && page < $scope.pagination.totalPages) {
                $scope.pagination.currentPage = page;
                loadEntityData();
            }
        };

        /**
         * Change page size
         */
        $scope.changePageSize = function() {
            $scope.pagination.currentPage = 0; // Reset to first page
            loadEntityData();
        };

        /**
         * Get visible pages for pagination
         */
        $scope.getVisiblePages = function() {
            var total = $scope.pagination.totalPages;
            var current = $scope.pagination.currentPage;
            var delta = 2;
            
            var range = [];
            var start = Math.max(0, current - delta);
            var end = Math.min(total - 1, current + delta);
            
            for (var i = start; i <= end; i++) {
                range.push(i);
            }
            
            return range;
        };

        /**
         * Get pagination info text
         */
        $scope.getPaginationInfo = function() {
            var start = ($scope.pagination.currentPage * $scope.pagination.size) + 1;
            var end = Math.min(start + $scope.pagination.size - 1, $scope.pagination.totalElements);
            
            return start + ' to ' + end + ' of ' + $scope.pagination.totalElements;
        };

        /**
         * Open create dialog
         */
        $scope.openCreateDialog = function() {
            console.log('➕ Opening create dialog for:', $scope.config.entityName);
            // This will be handled by entity-specific controllers or services
            if ($scope.config.handlers && $scope.config.handlers.create) {
                $scope.config.handlers.create();
            }
        };

        /**
         * Export data in specified format
         */
        $scope.exportData = function(format) {
            console.log('📤 Exporting data in format:', format);
            $scope.showExportDropdown = false;
            
            if ($scope.config.handlers && $scope.config.handlers.export) {
                $scope.config.handlers.export(format, $scope.selectedItems.length > 0 ? $scope.selectedItems : $scope.filteredData);
            }
        };

        // Grid view specific methods
        
        /**
         * Get primary value for grid display
         */
        $scope.getGridPrimaryValue = function(item) {
            var primaryField = $scope.config.gridConfig ? $scope.config.gridConfig.primaryField : 'name';
            return $scope.getFieldValue(item, primaryField);
        };

        /**
         * Get secondary value for grid display
         */
        $scope.getGridSecondaryValue = function(item) {
            var secondaryField = $scope.config.gridConfig ? $scope.config.gridConfig.secondaryField : 'email';
            return $scope.getFieldValue(item, secondaryField);
        };

        /**
         * Get grid fields for display
         */
        $scope.getGridFields = function(item) {
            if (!$scope.config.gridConfig || !$scope.config.gridConfig.fields) {
                return [];
            }
            
            return $scope.config.gridConfig.fields.map(function(field) {
                return {
                    label: field.label,
                    value: $scope.getFieldValue(item, field.field)
                };
            });
        };

        // Cleanup
        $scope.$on('$destroy', function() {
            console.log('🧹 EntityListController destroyed');
        });

        console.log('✅ EntityListController setup complete');
    }
})();