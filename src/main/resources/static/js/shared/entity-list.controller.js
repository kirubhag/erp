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
        '$document',
        'EntityConfigService',
        'EntityDataService',
        'NotificationService'
    ];

    function EntityListController($scope, $location, $timeout, $q, $document, EntityConfigService, EntityDataService, NotificationService) {
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
            $scope.showFilterPanel = true; // Filter panel visible by default
            
            // Total count display
            $scope.showTotalCount = false;
            $scope.totalCountLoaded = false;
            
            // Search and filtering
            $scope.searchQuery = '';
            $scope.appliedFilters = {};
            
            // Field-based filtering
            $scope.groupedFields = {};
            $scope.fieldGroupExpanded = {};
            $scope.fieldFilters = {};
            $scope.fieldFilterValues = {};
            $scope.fieldSearchQuery = '';
            $scope.fieldFilter = {};
            
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
            
            // Store existing configurations if already set (e.g., by child controller)
            var existingHandlers = $scope.config && $scope.config.handlers ? $scope.config.handlers : null;
            var existingEditUrl = $scope.config && $scope.config.editUrl ? $scope.config.editUrl : null;
            var existingOnEdit = $scope.config && $scope.config.onEdit ? $scope.config.onEdit : null;
            var existingOnView = $scope.config && $scope.config.onView ? $scope.config.onView : null;
            var existingOnRowClick = $scope.config && $scope.config.onRowClick ? $scope.config.onRowClick : null;
            var existingOnDelete = $scope.config && $scope.config.onDelete ? $scope.config.onDelete : null;
            var existingRowActions = $scope.config && $scope.config.rowActions ? $scope.config.rowActions : null;
            var existingBulkActions = $scope.config && $scope.config.bulkActions ? $scope.config.bulkActions : null;
            
            $scope.config = EntityConfigService.getConfig(entityType);
            
            if (!$scope.config) {
                return;
            }
            
            // Restore configurations if they were already set
            if (existingHandlers) {
                console.log('loadEntityConfig: Preserving existing handlers:', existingHandlers);
                $scope.config.handlers = existingHandlers;
            }
            if (existingEditUrl) {
                $scope.config.editUrl = existingEditUrl;
            }
            if (existingOnEdit) {
                $scope.config.onEdit = existingOnEdit;
            }
            if (existingOnView) {
                $scope.config.onView = existingOnView;
            }
            if (existingOnRowClick) {
                $scope.config.onRowClick = existingOnRowClick;
            }
            if (existingOnDelete) {
                $scope.config.onDelete = existingOnDelete;
            }
            if (existingRowActions) {
                $scope.config.rowActions = existingRowActions;
            }
            if (existingBulkActions) {
                $scope.config.bulkActions = existingBulkActions;
            }
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
                'subjects': 'SUBJECT',
                'timetables': 'TIMETABLE',
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
            
            // Load entity fields for filtering
            loadEntityFields();
            
            // Set up default visible columns
            setupDefaultColumns();
            
            // Set up document click handler to close dropdowns
            setupDocumentClickHandler();
        }
        
        /**
         * Set up document click handler to close dropdowns
         */
        function setupDocumentClickHandler() {
            $document.on('click', function(event) {
                var target = event.target;
                
                // Close bulk actions menu if clicking outside
                if ($scope.showBulkActionsMenu && 
                    !angular.element(target).closest('.bulk-actions-dropdown').length) {
                    $scope.$apply(function() {
                        $scope.showBulkActionsMenu = false;
                    });
                }
                
                // Close view dropdown if clicking outside
                if ($scope.showViewDropdown && 
                    !angular.element(target).closest('.view-selector').length) {
                    $scope.$apply(function() {
                        $scope.showViewDropdown = false;
                    });
                }
            });
            
            // Clean up event listener on destroy
            $scope.$on('$destroy', function() {
                $document.off('click');
            });
        }

        /**
         * Load entity data from server
         */
        function loadEntityData() {
            console.log('loadEntityData called - config:', $scope.config);
            if (!$scope.config) {
                console.warn('loadEntityData: No config available');
                return;
            }
            
            $scope.loading = true;
            console.log('loadEntityData: Setting loading=true');
            
            var params = {
                page: $scope.pagination.currentPage,
                size: $scope.pagination.size,
                search: $scope.searchQuery,
                sort: $scope.sortField,
                direction: $scope.sortDirection,
                filters: $scope.appliedFilters
            };
            
            console.log('loadEntityData: Calling API with params:', params);
            
            EntityDataService.loadData($scope.config.entityType, params)
                .then(function(response) {
                    console.log('loadEntityData: API response received:', response);
                    
                    // Clear caches when new data is loaded
                    clearFieldValueCache();
                    clearGridFieldsCache();
                    avatarStyleCache = {};
                    moreActionsCache = {};
                    
                    // Get new data
                    var newData = response.data.content || response.data.data || response.data || [];
                    console.log('loadEntityData: New data count:', newData.length);
                    
                    // Replace the array completely (reassign reference)
                    $scope.filteredData = newData;
                    
                    console.log('loadEntityData: Updated filteredData, count:', $scope.filteredData.length);
                    
                    // Update pagination info
                    if (response.data.pageable !== undefined) {
                        $scope.pagination.totalElements = response.data.totalElements || 0;
                        $scope.pagination.totalPages = response.data.totalPages || 0;
                        $scope.pagination.currentPage = response.data.number || 0;
                        console.log('loadEntityData: Updated pagination - totalElements:', $scope.pagination.totalElements);
                    } else {
                        // Handle non-paginated response
                        $scope.pagination.totalElements = $scope.filteredData.length;
                        $scope.pagination.totalPages = 1;
                        $scope.pagination.currentPage = 0;
                    }
                })
                .catch(function(error) {
                    console.error('loadEntityData: API error:', error);
                    NotificationService.error('Failed to load ' + $scope.config.entityName + 's');
                    $scope.filteredData = [];
                })
                .finally(function() {
                    $scope.loading = false;
                    console.log('loadEntityData: Setting loading=false');
                    
                    // Force digest cycle to update UI
                    if (!$scope.$$phase && !$scope.$root.$$phase) {
                        try {
                            $scope.$apply();
                            console.log('loadEntityData: $apply() executed');
                        } catch (e) {
                            console.warn('loadEntityData: $apply() error (may be safe to ignore):', e);
                        }
                    }
                });
        }

        /**
         * Load total count (called when user clicks Total Records link)
         */
        $scope.loadTotalCount = function() {
            if ($scope.totalCountLoaded) {
                return; // Already loaded
            }
            
            // The count is already loaded from the pagination data
            // Just mark it as loaded so we don't reload unnecessarily
            $scope.totalCountLoaded = true;
        };

        /**
         * Toggle total count display
         */
        $scope.toggleTotalCount = function() {
            $scope.showTotalCount = !$scope.showTotalCount;
            if ($scope.showTotalCount && !$scope.totalCountLoaded) {
                $scope.loadTotalCount();
            }
        };

        /**
         * Toggle filter panel visibility
         */
        $scope.toggleFilterPanel = function() {
            $scope.showFilterPanel = !$scope.showFilterPanel;
        };

        /**
         * Load custom views for the entity
         */
        function loadCustomViews() {
            EntityDataService.loadCustomViews($scope.config.entityType)
                .then(function(response) {
                    $scope.customViews = response.data || [];
                })
                .catch(function(error) {
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

        // Debounced search function
        var searchTimeout;
        
        /**
         * Perform search
         */
        $scope.performSearch = function() {
            if (searchTimeout) {
                $timeout.cancel(searchTimeout);
            }
            
            searchTimeout = $timeout(function() {
                $scope.pagination.currentPage = 0; // Reset to first page
                loadEntityData();
            }, 300); // 300ms debounce
        };

        /**
         * Set view mode (table or grid)
         */
        $scope.setViewMode = function(mode) {
            $scope.viewMode = mode;
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
         * Toggle bulk actions dropdown menu
         */
        $scope.showBulkActionsMenu = false;
        $scope.toggleBulkActionsMenu = function() {
            $scope.showBulkActionsMenu = !$scope.showBulkActionsMenu;
        };

        /**
         * Execute bulk action and close menu
         */
        $scope.executeBulkAction = function(action) {
            $scope.showBulkActionsMenu = false;
            
            if (action.handler && typeof action.handler === 'function') {
                action.handler($scope.selectedItems);
            } else {
                // Default bulk action handling
                NotificationService.info('Bulk action: ' + action.label);
            }
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
            console.log('handleRowClick called for item:', item.id);
            console.log('Event target:', $event.target);
            
            // Don't handle if clicking on checkbox or action button
            if ($event.target.type === 'checkbox' || 
                $event.target.closest('.action-btn') || 
                $event.target.closest('.more-btn')) {
                console.log('Ignoring click on checkbox or action button');
                return;
            }
            
            console.log('Row click handler exists:', !!($scope.config && $scope.config.onRowClick));
            
            // If row click handler is configured, use it to navigate to detail page
            if ($scope.config && $scope.config.onRowClick && typeof $scope.config.onRowClick === 'function') {
                console.log('Calling onRowClick handler');
                $scope.config.onRowClick(item);
            } else if ($scope.config && $scope.config.viewUrl) {
                // Default behavior: navigate to detail view using viewUrl
                console.log('No row click handler, using viewUrl:', $scope.config.viewUrl);
                $scope.viewItem(item);
            } else {
                console.log('No row click handler or viewUrl, falling back to selection toggle');
                // Fallback to selection toggle
                $scope.toggleSelection(item);
            }
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
            
            loadEntityData();
        };

        // Cache for field values to prevent excessive function calls
        var fieldValueCache = {};
        var gridFieldsCache = {};
        
        /**
         * Get field value from item using dot notation
         */
        $scope.getFieldValue = function(item, fieldPath) {
            if (!item || !fieldPath) return '';
            
            var cacheKey = item.id + '.' + fieldPath;
            
            if (fieldValueCache[cacheKey] === undefined) {
                var value = fieldPath.split('.').reduce(function(obj, key) {
                    return obj && obj[key];
                }, item);
                
                fieldValueCache[cacheKey] = value || '';
            }
            
            return fieldValueCache[cacheKey];
        };
        
        /**
         * Clear field value cache when data changes
         */
        function clearFieldValueCache() {
            fieldValueCache = {};
        }

        /**
         * Clear grid fields cache when data changes
         */
        function clearGridFieldsCache() {
            gridFieldsCache = {};
        }

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

        // Cache for avatar styles to prevent infinite digest
        var avatarStyleCache = {};
        
        /**
         * Get cached avatar style to prevent digest cycles
         */
        $scope.getCachedAvatarStyle = function(item) {
            if (!item || !item.id) return {};
            
            if (!avatarStyleCache[item.id]) {
                var colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];
                var colorIndex = item.id % colors.length;
                avatarStyleCache[item.id] = {
                    'background-color': colors[colorIndex]
                };
            }
            
            return avatarStyleCache[item.id];
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
                        break;
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
            
            if (action.handler) {
                action.handler($scope.selectedItems, $scope);
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
         * Open create dialog for new entity
         */
        $scope.openCreateDialog = function() {
            if ($scope.config && $scope.config.createUrl) {
                // Use configured create URL
                $location.path($scope.config.createUrl);
            } else if ($scope.config && $scope.config.handlers && $scope.config.handlers.create) {
                // Fallback to handler if createUrl not configured
                $scope.config.handlers.create();
            } else {
                console.error('Create functionality not configured. Please set config.createUrl or config.handlers.create');
            }
        };        /**
         * Export data in specified format
         */
        $scope.exportData = function(format) {
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
            if (!$scope.config.gridConfig || !$scope.config.gridConfig.fields || !item) {
                return [];
            }
            
            var cacheKey = item.id;
            
            if (gridFieldsCache[cacheKey] === undefined) {
                gridFieldsCache[cacheKey] = $scope.config.gridConfig.fields.map(function(field) {
                    return {
                        label: field.label,
                        value: $scope.getFieldValue(item, field.field)
                    };
                });
            }
            
            return gridFieldsCache[cacheKey];
        };

        /**
         * Check if an action is visible for an item
         */
        $scope.isActionVisible = function(action, item) {
            if (!action.condition) return true;
            
            if (typeof action.condition === 'function') {
                return action.condition(item);
            }
            
            return true;
        };

        // Cache for more actions to prevent repeated calculations
        var moreActionsCache = {};
        
        /**
         * Get more actions for an item (actions beyond the first few)
         */
        $scope.getMoreActions = function(item) {
            if (!$scope.config.rowActions || !item) return [];
            
            var cacheKey = item.id + '_moreActions';
            
            if (!moreActionsCache[cacheKey]) {
                moreActionsCache[cacheKey] = $scope.config.rowActions.slice(2).filter(function(action) {
                    return $scope.isActionVisible(action, item);
                });
            }
            
            return moreActionsCache[cacheKey];
        };

        /**
         * Toggle more actions dropdown
         */
        $scope.toggleMoreActions = function(item) {
            // Close other dropdowns first
            $scope.filteredData.forEach(function(dataItem) {
                if (dataItem.id !== item.id) {
                    dataItem.showMoreActions = false;
                }
            });
            
            item.showMoreActions = !item.showMoreActions;
        };

        /**
         * Handle item click in grid view
         */
        $scope.handleItemClick = function(item, $event) {
            if ($event && ($event.target.type === 'checkbox' || 
                          $event.target.closest('.grid-action-btn'))) {
                return;
            }
            
            $scope.toggleSelection(item);
        };

        /**
         * Toggle filter group expansion
         */
        $scope.toggleFilterGroup = function(filterGroup) {
            // This will be handled by the filter configuration
        };

        /**
         * Apply filter changes
         */
        $scope.applyFilter = function() {
            // Build filters from selected options
            var newFilters = {};
            
            if ($scope.config.filters) {
                $scope.config.filters.forEach(function(filterGroup) {
                    if (filterGroup.options) {
                        var selectedOptions = filterGroup.options
                            .filter(function(option) { return option.selected; })
                            .map(function(option) { return option.value; });
                        
                        if (selectedOptions.length > 0) {
                            newFilters[filterGroup.field] = selectedOptions;
                        }
                    }
                });
            }
            
            $scope.appliedFilters = newFilters;
            $scope.pagination.currentPage = 0; // Reset to first page
            loadEntityData();
        };

        /**
         * Show import dialog
         */
        $scope.showImportDialog = function() {
            if ($scope.config.handlers && $scope.config.handlers.import) {
                $scope.config.handlers.import();
            }
        };

        /**
         * Toggle source boosters dropdown
         */
        $scope.toggleSourceBooters = function() {
            // Implementation for source boosters
        };

        /**
         * Open view manager dialog
         */
        $scope.openViewManager = function() {
            $scope.showViewDropdown = false;
            if ($scope.config.handlers && $scope.config.handlers.viewManager) {
                $scope.config.handlers.viewManager();
            }
        };

        // Field-based filtering functionality

        /**
         * Load entity fields for field-based filtering
         */
        function loadEntityFields() {
            if (!$scope.config || !$scope.config.entityType) return;
            
            // Skip loading for settings entities
            if (isSettingsEntity($scope.config.entityType)) {
                return;
            }
            
            $scope.loading = true;
            
            EntityDataService.loadFieldsGrouped($scope.config.entityType)
                .then(function(response) {
                    $scope.groupedFields = response.data || {};
                    
                    // Create flat list of all fields for the new UI
                    $scope.allFields = [];
                    Object.keys($scope.groupedFields).forEach(function(category) {
                        var fields = $scope.groupedFields[category] || [];
                        $scope.allFields = $scope.allFields.concat(fields);
                    });
                    
                    // Initialize field group expanded state (kept for backward compatibility)
                    Object.keys($scope.groupedFields).forEach(function(category) {
                        $scope.fieldGroupExpanded[category] = category === 'PERSONAL' || category === 'General';
                    });
                })
                .catch(function(error) {
                    $scope.groupedFields = {};
                    $scope.allFields = [];
                })
                .finally(function() {
                    $scope.loading = false;
                });
        }

        /**
         * Check if entity is a settings entity that should not have field-based filtering
         */
        function isSettingsEntity(entityType) {
            var settingsEntities = ['USER', 'ROLE', 'PERMISSION', 'ORGANIZATION', 'EMAIL_TEMPLATE'];
            return settingsEntities.indexOf(entityType) >= 0;
        }

        /**
         * Toggle field group expansion
         */
        $scope.toggleFieldGroup = function(category) {
            $scope.fieldGroupExpanded[category] = !$scope.fieldGroupExpanded[category];
        };

        /**
         * Filter fields based on search query
         */
        $scope.filterFields = function() {
            var query = $scope.fieldSearchQuery.toLowerCase();
            
            $scope.fieldFilter = function(field) {
                if (!query) return true;
                
                return field.fieldName.toLowerCase().indexOf(query) >= 0 ||
                       field.fieldLabel.toLowerCase().indexOf(query) >= 0 ||
                       (field.fieldDescription && field.fieldDescription.toLowerCase().indexOf(query) >= 0);
            };
        };

        /**
         * Apply field filter when field is selected/deselected
         */
        $scope.applyFieldFilter = function(field) {
            if (!$scope.fieldFilters[field.fieldName]) {
                // Field deselected, remove its filter values
                delete $scope.fieldFilterValues[field.fieldName];
                delete $scope.fieldFilterValues[field.fieldName + '_min'];
                delete $scope.fieldFilterValues[field.fieldName + '_max'];
                delete $scope.fieldFilterValues[field.fieldName + '_from'];
                delete $scope.fieldFilterValues[field.fieldName + '_to'];
                
                // Remove enum options
                Object.keys($scope.fieldFilterValues).forEach(function(key) {
                    if (key.startsWith(field.fieldName + '_')) {
                        delete $scope.fieldFilterValues[key];
                    }
                });
            }
            
            applyAllFieldFilters();
        };

        /**
         * Apply field value filter when field value changes
         */
        $scope.applyFieldValueFilter = function(field) {
            var searchTimeout;
            
            if (searchTimeout) {
                $timeout.cancel(searchTimeout);
            }
            
            searchTimeout = $timeout(function() {
                applyAllFieldFilters();
            }, 300); // 300ms debounce
        };

        /**
         * Apply all field filters
         */
        function applyAllFieldFilters() {
            var filters = {};
            
            // Build filters from selected fields and their values
            Object.keys($scope.fieldFilters).forEach(function(fieldName) {
                if ($scope.fieldFilters[fieldName]) {
                    var fieldValue = $scope.fieldFilterValues[fieldName];
                    
                    if (fieldValue !== undefined && fieldValue !== null && fieldValue !== '') {
                        filters[fieldName] = fieldValue;
                    }
                    
                    // Handle range filters
                    var minValue = $scope.fieldFilterValues[fieldName + '_min'];
                    var maxValue = $scope.fieldFilterValues[fieldName + '_max'];
                    
                    if (minValue !== undefined && minValue !== null && minValue !== '') {
                        filters[fieldName + '_min'] = minValue;
                    }
                    if (maxValue !== undefined && maxValue !== null && maxValue !== '') {
                        filters[fieldName + '_max'] = maxValue;
                    }
                    
                    // Handle date range filters
                    var fromValue = $scope.fieldFilterValues[fieldName + '_from'];
                    var toValue = $scope.fieldFilterValues[fieldName + '_to'];
                    
                    if (fromValue) {
                        filters[fieldName + '_from'] = fromValue;
                    }
                    if (toValue) {
                        filters[fieldName + '_to'] = toValue;
                    }
                    
                    // Handle enum filters
                    var enumValues = [];
                    Object.keys($scope.fieldFilterValues).forEach(function(key) {
                        if (key.startsWith(fieldName + '_') && $scope.fieldFilterValues[key]) {
                            var enumValue = key.replace(fieldName + '_', '');
                            if (enumValue !== 'min' && enumValue !== 'max' && enumValue !== 'from' && enumValue !== 'to') {
                                enumValues.push(enumValue);
                            }
                        }
                    });
                    
                    if (enumValues.length > 0) {
                        filters[fieldName] = enumValues;
                    }
                }
            });
            
            $scope.appliedFilters = filters;
            $scope.pagination.currentPage = 0; // Reset to first page
            loadEntityData();
        }

        /**
         * Get field options for enum/select fields
         */
        $scope.getFieldOptions = function(field) {
            // This would typically come from the field metadata
            // For now, return common options based on field name
            var fieldName = field.fieldName.toLowerCase();
            
            if (fieldName.includes('status') || fieldName.includes('enrollmentstatus')) {
                return [
                    { value: 'ACTIVE', label: 'Active' },
                    { value: 'INACTIVE', label: 'Inactive' },
                    { value: 'PENDING', label: 'Pending' },
                    { value: 'GRADUATED', label: 'Graduated' }
                ];
            }
            
            if (fieldName.includes('gender')) {
                return [
                    { value: 'MALE', label: 'Male' },
                    { value: 'FEMALE', label: 'Female' },
                    { value: 'OTHER', label: 'Other' }
                ];
            }
            
            if (fieldName.includes('grade') || fieldName.includes('gradelevel')) {
                return [
                    { value: 'KINDERGARTEN', label: 'Kindergarten' },
                    { value: 'FIRST_GRADE', label: '1st Grade' },
                    { value: 'SECOND_GRADE', label: '2nd Grade' },
                    { value: 'THIRD_GRADE', label: '3rd Grade' },
                    { value: 'FOURTH_GRADE', label: '4th Grade' },
                    { value: 'FIFTH_GRADE', label: '5th Grade' }
                ];
            }
            
            return [];
        };

        /**
         * Check if there are active filters
         */
        $scope.hasActiveFilters = function() {
            return Object.keys($scope.appliedFilters).length > 0;
        };

        /**
         * Clear all field filters
         */
        $scope.clearAllFilters = function() {
            $scope.fieldFilters = {};
            $scope.fieldFilterValues = {};
            $scope.appliedFilters = {};
            $scope.fieldSearchQuery = '';
            $scope.fieldFilter = {};
            $scope.pagination.currentPage = 0;
            loadEntityData();
        };

        /**
         * Edit an item - placeholder function that can be overridden by specific controllers
         */
        $scope.editItem = function(item) {
            if ($scope.config && $scope.config.editUrl) {
                // Use configured edit URL - handle both :id and {id} templates
                var editPath = $scope.config.editUrl.replace(':id', item.id).replace('{id}', item.id);
                $location.path(editPath);
            } else if ($scope.config && $scope.config.onEdit && typeof $scope.config.onEdit === 'function') {
                // Use configured edit callback
                $scope.config.onEdit(item);
            } else {
                console.warn('Edit functionality not configured. Please set config.editUrl or config.onEdit');
            }
        };

        /**
         * Delete an item - with Bootstrap modal confirmation
         */
        $scope.deleteItem = function(item) {
            console.log('deleteItem called with item:', item);
            $scope.itemToDelete = item;
            $scope.showDeleteModal = true;
        };

        /**
         * Confirm delete - called from modal
         */
        $scope.confirmDelete = function() {
            if (!$scope.itemToDelete) {
                console.error('No item to delete');
                return;
            }
            
            console.log('=== DELETE OPERATION START ===');
            console.log('Item to delete:', $scope.itemToDelete);
            console.log('Item ID:', $scope.itemToDelete.id);
            
            var itemId = $scope.itemToDelete.id;
            var itemName = getItemDisplayName($scope.itemToDelete);
            var entityType = $scope.config.entityType;
            
            // Close modal first
            $scope.showDeleteModal = false;
            var deletedItem = $scope.itemToDelete;
            $scope.itemToDelete = null;
            
            EntityDataService.deleteItem(entityType, itemId)
                .then(function(response) {
                    console.log('DELETE API SUCCESS');
                    console.log('Response:', response);
                    console.log('Response status:', response.status);
                    
                    // Show success notification with entity name
                    NotificationService.success($scope.config.entityDisplayName + ' "' + itemName + '" deleted successfully');
                    
                    // Use $timeout to ensure reload happens in next digest cycle
                    return $timeout(function() {
                        console.log('Reloading data after timeout...');
                        loadEntityData();
                    }, 200);
                })
                .catch(function(error) {
                    console.error('DELETE API ERROR');
                    console.error('Error:', error);
                    
                    var errorMsg = 'Failed to delete ' + $scope.config.entityDisplayName.toLowerCase();
                    if (error.data && error.data.message) {
                        errorMsg = error.data.message;
                    } else if (error.statusText) {
                        errorMsg += ': ' + error.statusText;
                    }
                    NotificationService.error(errorMsg);
                })
                .finally(function() {
                    console.log('=== DELETE OPERATION END ===');
                });
        };

        /**
         * Cancel delete - called from modal
         */
        $scope.cancelDelete = function() {
            $scope.showDeleteModal = false;
            $scope.itemToDelete = null;
        };

        /**
         * Get display name for an item (for delete confirmation)
         */
        function getItemDisplayName(item) {
            if (item.firstName && item.lastName) {
                return item.firstName + ' ' + item.lastName;
            } else if (item.name) {
                return item.name;
            } else if (item.title) {
                return item.title;
            } else {
                return 'item #' + item.id;
            }
        }

        /**
         * Delete an item - placeholder function that can be overridden by specific controllers
         */
        $scope.deleteItemOld = function(item) {
            if ($scope.config && $scope.config.onDelete && typeof $scope.config.onDelete === 'function') {
                // Call the delete handler directly (child controllers should handle modal display)
                $scope.config.onDelete(item);
            } else {
                console.warn('Delete functionality not configured. Please set config.onDelete');
            }
        };

        /**
         * View an item - placeholder function that can be overridden by specific controllers
         */
        $scope.viewItem = function(item) {
            if ($scope.config && $scope.config.viewUrl) {
                // Use configured view URL
                $location.path($scope.config.viewUrl.replace(':id', item.id));
            } else if ($scope.config && $scope.config.onView && typeof $scope.config.onView === 'function') {
                // Use configured view callback
                $scope.config.onView(item);
            } else {
                console.warn('View functionality not configured. Please set config.viewUrl or config.onView');
            }
        };

        /**
         * Reload entity data (public wrapper for private loadEntityData function)
         */
        $scope.loadEntityData = function() {
            loadEntityData();
        };

        // Cleanup
        $scope.$on('$destroy', function() {
            // Cleanup logic here
        });
    }
})();