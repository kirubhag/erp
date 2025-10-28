// Modules Controller - Handles module listing and management
angular.module('erpApp').controller('ModulesController', [
    '$scope', '$rootScope', '$location', 'ApiService',
    function($scope, $rootScope, $location, ApiService) {
        
        // Initialize controller
        $scope.init = function() {
            $scope.modules = [];
            $scope.filteredModules = [];
            $scope.loading = false;
            $scope.searchTerm = '';
            
            // Initialize sidebar sections
            $scope.expandedSections = {
                general: false,
                users: false,
                customization: true,  // Customization section is expanded by default
                portal: false,
                dataAdmin: false,
                developer: false
            };
            
            // Toggle sidebar sections
            $scope.toggleSection = function(section) {
                $scope.expandedSections[section] = !$scope.expandedSections[section];
            };
            
            // Module categories
            $scope.moduleCategories = [
                { value: 'academic', label: 'Academic Management' },
                { value: 'student', label: 'Student Services' },
                { value: 'administrative', label: 'Administrative' },
                { value: 'health', label: 'Health & Wellness' },
                { value: 'communication', label: 'Communication' },
                { value: 'reporting', label: 'Reports & Analytics' }
            ];
            
            // Set active tab based on current path without redirecting
            var currentPath = $location.path();
            if (currentPath.includes('/settings/modules')) {
                // If called from settings, set active tab to settings without redirect
                $rootScope.activeTab = 'settings';
            }
            
            // Load modules
            $scope.loadModules();
        };
        
        // Load module data
        $scope.loadModules = function() {
            $scope.loading = true;
            
            // Try to load from API if available
            if (typeof ApiService !== 'undefined') {
                ApiService.get('/module/menu-items').then(function(response) {
                    if (response.data && response.data.length > 0) {
                        // Convert API data to module format
                        $scope.modules = response.data.map(function(item) {
                            return {
                                id: item.id,
                                name: item.systemName || item.singularName.toLowerCase(),
                                displayName: item.pluralName || item.displayName,
                                description: item.description || 'Manage ' + item.pluralName.toLowerCase(),
                                icon: item.icon || 'fas fa-cube',
                                sequence: item.sequence,
                                isActive: item.isActive,
                                route: item.route,
                                sharedTo: 'All Profiles',
                                lastModified: item.lastModifiedDate ? new Date(item.lastModifiedDate) : new Date()
                            };
                        });
                        
                        // Sort by sequence
                        $scope.modules.sort(function(a, b) {
                            return a.sequence - b.sequence;
                        });
                        
                        $scope.filteredModules = angular.copy($scope.modules);
                        $scope.loading = false;
                    } else {
                        // Fallback to static modules
                        $scope.loadStaticModules();
                    }
                }).catch(function(error) {
                    console.error('Error loading modules from API:', error);
                    // Fallback to static modules
                    $scope.loadStaticModules();
                });
            } else {
                // ApiService not available, use static modules
                $scope.loadStaticModules();
            }
        };
        
        // Load static module data (fallback)
        $scope.loadStaticModules = function() {
            
            // Define system modules (non-settings entities)
            $scope.modules = [
                {
                    id: 'students',
                    name: 'students',
                    displayName: 'Students',
                    description: 'Manage student profiles, enrollment, and academic information',
                    icon: 'fas fa-user-graduate',
                    color: '#0d6efd',
                    category: 'student',
                    isActive: true,
                    recordCount: 156,
                    activeUsers: 12,
                    views: 1248,
                    lastModified: new Date('2024-10-20'),
                    sharedTo: 'All Profiles',
                    route: '/students'
                },
                {
                    id: 'staff',
                    name: 'staff',
                    displayName: 'Staffs',
                    description: 'Manage staff members, their roles, and employment details',
                    icon: 'fas fa-chalkboard-teacher',
                    color: '#198754',
                    category: 'administrative',
                    isActive: true,
                    recordCount: 45,
                    activeUsers: 8,
                    views: 892,
                    lastModified: new Date('2024-05-30'),
                    sharedTo: 'All Profiles',
                    route: '/staff'
                },
                {
                    id: 'courses',
                    name: 'courses',
                    displayName: 'Courses',
                    description: 'Course catalog, curriculum management, and academic planning',
                    icon: 'fas fa-book-open',
                    color: '#fd7e14',
                    category: 'academic',
                    isActive: true,
                    recordCount: 28,
                    activeUsers: 15,
                    views: 567,
                    lastModified: new Date('2024-10-23'),
                    sharedTo: 'All Profiles',
                    route: '/courses'
                },
                {
                    id: 'exams',
                    name: 'exams',
                    displayName: 'Exams',
                    description: 'Examination scheduling, management, and result processing',
                    icon: 'fas fa-clipboard-list',
                    color: '#dc3545',
                    category: 'academic',
                    isActive: true,
                    recordCount: 84,
                    activeUsers: 7,
                    views: 432,
                    lastModified: new Date('2024-10-22'),
                    sharedTo: '4 Profiles',
                    route: '/exams'
                },
                {
                    id: 'attendance',
                    name: 'attendance',
                    displayName: 'Attendance',
                    description: 'Track and manage daily attendance for students and staff',
                    icon: 'fas fa-calendar-check',
                    color: '#20c997',
                    category: 'administrative',
                    isActive: true,
                    recordCount: 3240,
                    activeUsers: 25,
                    views: 2156,
                    lastModified: new Date('2024-10-23'),
                    sharedTo: 'All Profiles',
                    route: '/attendance'
                },
                {
                    id: 'parents',
                    name: 'parents',
                    displayName: 'Parents',
                    description: 'Parent information, communication, and engagement tools',
                    icon: 'fas fa-users',
                    color: '#6f42c1',
                    category: 'communication',
                    isActive: true,
                    recordCount: 98,
                    activeUsers: 18,
                    views: 723,
                    lastModified: new Date('2024-10-21'),
                    sharedTo: 'All Profiles',
                    route: '/parents'
                },
                {
                    id: 'health',
                    name: 'health',
                    displayName: 'Health Records',
                    description: 'Student health information, medical records, and wellness tracking',
                    icon: 'fas fa-heartbeat',
                    color: '#e91e63',
                    category: 'health',
                    isActive: true,
                    recordCount: 124,
                    activeUsers: 5,
                    views: 445,
                    lastModified: new Date('2024-10-19'),
                    sharedTo: 'All Profiles',
                    route: '/health'
                },
                {
                    id: 'assignments',
                    name: 'assignments',
                    displayName: 'Assignments',
                    description: 'Assignment creation, distribution, and submission management',
                    icon: 'fas fa-tasks',
                    color: '#17a2b8',
                    category: 'academic',
                    isActive: false,
                    recordCount: 0,
                    activeUsers: 0,
                    views: 0,
                    lastModified: new Date('2024-09-15'),
                    sharedTo: 'No Access',
                    route: '/assignments'
                },
                {
                    id: 'grades',
                    name: 'grades',
                    displayName: 'Grades',
                    description: 'Grade book management, assessment tracking, and progress reports',
                    icon: 'fas fa-chart-line',
                    color: '#ffc107',
                    category: 'academic',
                    isActive: false,
                    recordCount: 0,
                    activeUsers: 0,
                    views: 0,
                    lastModified: new Date('2024-08-30'),
                    sharedTo: 'No Access',
                    route: '/grades'
                },
                {
                    id: 'library',
                    name: 'library',
                    displayName: 'Library',
                    description: 'Library catalog, book lending, and resource management',
                    icon: 'fas fa-book',
                    color: '#795548',
                    category: 'administrative',
                    isActive: false,
                    recordCount: 0,
                    activeUsers: 0,
                    views: 0,
                    lastModified: new Date('2024-07-20'),
                    sharedTo: 'No Access',
                    route: '/library'
                }
            ];
            
            // Apply initial filter
            $scope.filteredModules = angular.copy($scope.modules);
            $scope.loading = false;
        };
        
        // Toggle sidebar sections
        $scope.toggleSection = function(sectionName) {
            $scope.expandedSections[sectionName] = !$scope.expandedSections[sectionName];
        };
        
        // Filter modules based on search
        $scope.filterModules = function() {
            $scope.filteredModules = $scope.modules.filter(function(module) {
                if (!$scope.searchTerm) {
                    return true;
                }
                
                const searchLower = $scope.searchTerm.toLowerCase();
                return module.displayName.toLowerCase().includes(searchLower) ||
                       module.description.toLowerCase().includes(searchLower) ||
                       module.category.toLowerCase().includes(searchLower);
            });
        };
        
        // Clear all filters
        $scope.clearFilters = function() {
            $scope.searchTerm = '';
            $scope.selectedCategory = '';
            $scope.selectedStatus = '';
            $scope.filterModules();
        };
        
        // Navigate to module
        $scope.navigateToModule = function(module) {
            if (module.isActive && module.route) {
                $location.path(module.route);
            } else {
                alert('This module is not currently active or available.');
            }
        };
        
        // Navigate to entity field layout (now handled by direct HTML links)
        $scope.navigateToEntityFields = function(module) {
            var entityType = module.name;
            $location.path('/settings/modules/' + entityType + '/fields');
        };
        
        // View module details
        $scope.viewModuleDetails = function(module) {
            alert('Module Details:\n\nName: ' + module.displayName + 
                  '\nDescription: ' + module.description + 
                  '\nCategory: ' + module.category + 
                  '\nStatus: ' + (module.isActive ? 'Active' : 'Inactive') +
                  '\nRecords: ' + module.recordCount +
                  '\nLast Modified: ' + module.lastModified.toDateString());
        };
        
        // Edit module (placeholder)
        $scope.editModule = function(module) {
            alert('Edit functionality for "' + module.displayName + '" module is not yet implemented.');
        };
        
        // Toggle module status
        $scope.toggleModuleStatus = function(module) {
            const action = module.isActive ? 'deactivate' : 'activate';
            if (confirm('Are you sure you want to ' + action + ' the "' + module.displayName + '" module?')) {
                module.isActive = !module.isActive;
                
                // Update shared access based on status
                if (!module.isActive) {
                    module.sharedTo = 'No Access';
                    module.activeUsers = 0;
                } else {
                    module.sharedTo = 'All Profiles';
                }
                
                $scope.filterModules();
                alert('Module "' + module.displayName + '" has been ' + 
                      (module.isActive ? 'activated' : 'deactivated') + ' successfully.');
            }
        };
        
        // Create new module (placeholder)
        $scope.createNewModule = function() {
            alert('Custom module creation is not yet implemented.');
        };
        
        // Organize modules
        $scope.organizeModules = function() {
            $scope.organizingModules = angular.copy($scope.modules);
            // Store original order for comparison
            $scope.originalModulesOrder = angular.copy($scope.modules);
            
            // Open Bootstrap modal with proper configuration
            var modalElement = document.getElementById('organizeModulesModal');
            var modal = new bootstrap.Modal(modalElement, {
                backdrop: 'static',
                keyboard: false,
                focus: true
            });
            modal.show();
            
            // Initialize jQuery UI sortable after modal is shown
            setTimeout(function() {
                $('#sortable-modules').sortable({
                    handle: '.organize-item-handle',
                    axis: 'y',
                    cursor: 'move',
                    placeholder: 'sortable-placeholder',
                    items: '.organize-item:not(.organize-item-locked)', // Exclude locked items from sorting
                    start: function(event, ui) {
                        ui.placeholder.height(ui.item.height());
                    },
                    update: function(event, ui) {
                        // Update the organizingModules array based on new order
                        $scope.$apply(function() {
                            var newOrder = [];
                            $('#sortable-modules .organize-item').each(function() {
                                var moduleId = parseInt($(this).attr('data-module-id'));
                                var module = $scope.organizingModules.find(function(m) {
                                    return m.id === moduleId;
                                });
                                if (module) {
                                    newOrder.push(module);
                                }
                            });
                            $scope.organizingModules = newOrder;
                        });
                    }
                });
            }, 300);
        };
        
        // Close modal function
        $scope.closeModal = function() {
            var modalElement = document.getElementById('organizeModulesModal');
            var modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) {
                modal.hide();
            }
            // Destroy sortable when modal closes
            if ($('#sortable-modules').hasClass('ui-sortable')) {
                $('#sortable-modules').sortable('destroy');
            }
        };
        
        // Save module order
        $scope.saveModuleOrder = function() {
            // Check if order has changed
            var hasChanged = false;
            for (var i = 0; i < $scope.organizingModules.length; i++) {
                if ($scope.organizingModules[i].id !== $scope.originalModulesOrder[i].id) {
                    hasChanged = true;
                    break;
                }
            }
            
            // If no changes, just close modal
            if (!hasChanged) {
                $scope.closeModal();
                return;
            }
            
            $scope.loading = true;
            
            // Prepare data for API - ensure Dashboard is always at sequence 1
            var updates = [];
            var dashboardModule = null;
            var otherModules = [];
            
            // Separate Dashboard from other modules
            $scope.organizingModules.forEach(function(module) {
                if (module.name === 'Dashboard') {
                    dashboardModule = module;
                } else {
                    otherModules.push(module);
                }
            });
            
            // Dashboard always gets sequence 1
            if (dashboardModule) {
                updates.push({
                    id: dashboardModule.id,
                    sequence: 1
                });
            }
            
            // Other modules get sequences 2, 3, 4...
            otherModules.forEach(function(module, index) {
                updates.push({
                    id: module.id,
                    sequence: index + 2
                });
            });
            
            // Call API to update sequence
            ApiService.put('/module/update-sequence', updates).then(function(response) {
                // Update main list
                $scope.modules = angular.copy($scope.organizingModules);
                
                // Update sequence numbers
                $scope.modules.forEach(function(module, index) {
                    module.sequence = index + 1;
                });
                
                $scope.filteredModules = angular.copy($scope.modules);
                
                // Close modal
                var modalElement = document.getElementById('organizeModulesModal');
                var modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) {
                    modal.hide();
                }
                
                // Broadcast event to refresh menu items in navbar
                $rootScope.$broadcast('menuOrderUpdated');
            }).catch(function(error) {
                console.error('Error saving module order:', error);
                alert('Failed to save module order. Please try again.');
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        // Permission check (placeholder)
        $scope.hasPermission = function(permission) {
            // For now, return true for basic permissions
            return ['module:edit', 'module:create'].includes(permission);
        };
        
        // Statistics functions
        $scope.getActiveModulesCount = function() {
            return $scope.modules.filter(function(module) {
                return module.isActive;
            }).length;
        };
        
        $scope.getTotalRecords = function() {
            return $scope.modules.reduce(function(total, module) {
                return total + (module.recordCount || 0);
            }, 0);
        };
        
        $scope.getTotalUsers = function() {
            return $scope.modules.reduce(function(total, module) {
                return total + (module.activeUsers || 0);
            }, 0);
        };
        
        // Initialize controller
        $scope.init();
    }
]);