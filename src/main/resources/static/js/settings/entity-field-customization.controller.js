angular.module('erpApp').controller('EntityFieldCustomizationController', ['$scope', '$routeParams', '$location', '$http', 'ApiService', 'NotificationService', 'UIFieldTypeService',
    function($scope, $routeParams, $location, $http, ApiService, NotificationService, UIFieldTypeService) {
        
        // Initialize scope variables
        $scope.entityType = $routeParams.entityType;
        $scope.currentEntity = {};
        $scope.allFields = [];
        $scope.coreFields = [];
        $scope.customFields = [];
        $scope.selectedField = null;
        $scope.fieldSearchTerm = '';
        
    // Load UI types from API with fallback
    $http.get('/api/fields/ui-types').then(function(response) {
        $scope.uiTypes = response.data;
        console.log('Loaded UI types:', $scope.uiTypes);
    }).catch(function(error) {
        console.warn('Could not load UI types from API, using fallback', error);
        // Keep the existing fallback UI types
    });

    // Lightning UI state management
    $scope.activeTab = 'fields';
    $scope.fieldFilter = 'all';
    $scope.searchTerm = '';

    $scope.setActiveTab = function(tab) {
        $scope.activeTab = tab;
    };

    $scope.setFieldFilter = function(filter) {
        $scope.fieldFilter = filter;
    };

    // Get icon for UI field type
    $scope.getFieldTypeIcon = function(uiType) {
        var iconMap = {
            100: 'fas fa-text-width',      // Single Line Text
            101: 'fas fa-align-left',      // Multi Line Text
            102: 'fas fa-hashtag',         // Number
            103: 'fas fa-dollar-sign',     // Currency
            104: 'fas fa-percentage',      // Percent
            105: 'fas fa-calendar-alt',    // Date
            106: 'fas fa-clock',           // DateTime
            107: 'fas fa-stopwatch',       // Time
            108: 'fas fa-envelope',        // Email
            109: 'fas fa-phone',           // Phone
            110: 'fas fa-list',            // Picklist
            111: 'fas fa-list-ul',         // Multi-select Picklist
            112: 'fas fa-check-square',    // Checkbox
            113: 'fas fa-file-alt',        // Text Area
            114: 'fas fa-file-upload',     // File Upload
            115: 'fas fa-image',           // Image
            116: 'fas fa-user',            // Lookup
            117: 'fas fa-calculator',      // Formula
            118: 'fas fa-code',            // Auto Number
            119: 'fas fa-link'             // URL
        };
        return iconMap[uiType] || 'fas fa-question';
    };

    // Get UI type label
    $scope.getUITypeLabel = function(uiType) {
        var uiTypeObj = $scope.uiTypes.find(function(type) { return type.id === uiType; });
        return uiTypeObj ? uiTypeObj.label : 'Unknown';
    };

    // Filter fields based on search and filter criteria
    $scope.getFilteredFields = function() {
        if (!$scope.fields) return [];
        
        var filtered = $scope.fields.filter(function(field) {
            // Apply search filter
            var matchesSearch = !$scope.searchTerm || 
                field.label.toLowerCase().indexOf($scope.searchTerm.toLowerCase()) > -1 ||
                field.fieldName.toLowerCase().indexOf($scope.searchTerm.toLowerCase()) > -1;
            
            // Apply type filter
            var matchesType = true;
            if ($scope.fieldFilter === 'custom') {
                matchesType = !field.isSystemField;
            } else if ($scope.fieldFilter === 'system') {
                matchesType = field.isSystemField;
            } else if ($scope.fieldFilter === 'required') {
                matchesType = field.isRequired;
            }
            
            return matchesSearch && matchesType;
        });
        
        return filtered;
    };

    // Group fields by type
    $scope.getFieldsByType = function(type) {
        var filtered = $scope.getFilteredFields();
        if (type === 'custom') {
            return filtered.filter(function(field) { return !field.isSystemField; });
        } else if (type === 'system') {
            return filtered.filter(function(field) { return field.isSystemField; });
        }
        return filtered;
    };

    $scope.editField = function(field) {
        $scope.currentField = angular.copy(field);
        $scope.isEditing = true;
        showFieldModal();
    };

    $scope.addNewField = function() {
        $scope.currentField = {
            entityType: $scope.entityType,
            uiType: 100, // Default to Single Line Text
            isActive: true,
            isRequired: false,
            isSystemField: false,
            showInList: true,
            showInForm: true,
            columnWidth: 150
        };
        $scope.isEditing = false;
        showFieldModal();
    };

    function showFieldModal() {
        var modal = document.getElementById('fieldEditModal');
        modal.style.display = 'flex';
        modal.classList.add('show');
    }

    $scope.closeFieldModal = function() {
        var modal = document.getElementById('fieldEditModal');
        modal.style.display = 'none';
        modal.classList.remove('show');
        $scope.currentField = {};
        $scope.isEditing = false;
    };

    $scope.saveField = function() {
        if (!$scope.currentField.label || !$scope.currentField.fieldName) {
            alert('Please fill in required fields');
            return;
        }

        // Validate field configuration using API
        $http.post('/api/fields/validate', $scope.currentField).then(function(response) {
            if (response.data.valid) {
                // Proceed with saving
                var url = $scope.isEditing ? '/api/fields/' + $scope.currentField.id : '/api/fields';
                var method = $scope.isEditing ? 'PUT' : 'POST';

                $http({
                    method: method,
                    url: url,
                    data: $scope.currentField
                }).then(function(response) {
                    loadFields();
                    $scope.closeFieldModal();
                }).catch(function(error) {
                    alert('Error saving field: ' + (error.data && error.data.message ? error.data.message : 'Unknown error'));
                });
            } else {
                alert('Field validation failed: ' + response.data.message);
            }
        }).catch(function(error) {
            // If validation API fails, proceed with basic save
            console.warn('Field validation API failed, proceeding with save', error);
            
            var url = $scope.isEditing ? '/api/fields/' + $scope.currentField.id : '/api/fields';
            var method = $scope.isEditing ? 'PUT' : 'POST';

            $http({
                method: method,
                url: url,
                data: $scope.currentField
            }).then(function(response) {
                loadFields();
                $scope.closeFieldModal();
            }).catch(function(error) {
                alert('Error saving field: ' + (error.data && error.data.message ? error.data.message : 'Unknown error'));
            });
        });
    };

    $scope.deleteField = function(field) {
        if (field.isSystemField) {
            alert('System fields cannot be deleted');
            return;
        }

        if (confirm('Are you sure you want to delete the field "' + field.label + '"? This action cannot be undone.')) {
            $http.delete('/api/fields/' + field.id).then(function(response) {
                loadFields();
            }).catch(function(error) {
                alert('Error deleting field: ' + (error.data && error.data.message ? error.data.message : 'Unknown error'));
            });
        }
    };        // Fallback UI types if API fails
        $scope.setDefaultUITypes = function() {
            $scope.uiTypes = {
                100: { name: 'Single Line Text', dataType: 'VARCHAR', category: 'Basic Input', htmlInputType: 'text', maxLength: 255 },
                101: { name: 'Multi Line Text', dataType: 'TEXT', category: 'Basic Input', htmlInputType: 'textarea' },
                102: { name: 'Email', dataType: 'VARCHAR', category: 'Contact Information', htmlInputType: 'email', maxLength: 255 },
                103: { name: 'Phone Number', dataType: 'VARCHAR', category: 'Contact Information', htmlInputType: 'tel', maxLength: 20 },
                104: { name: 'Pick List', dataType: 'VARCHAR', category: 'Selection Types', htmlInputType: 'select', maxLength: 255, hasOptions: true },
                105: { name: 'Multi-Select Picklist', dataType: 'JSON', category: 'Selection Types', htmlInputType: 'multi-select', hasOptions: true, isMultiSelect: true },
                106: { name: 'Date', dataType: 'DATE', category: 'Date & Time', htmlInputType: 'date', isDateTime: true },
                107: { name: 'Date/Time', dataType: 'DATETIME', category: 'Date & Time', htmlInputType: 'datetime-local', isDateTime: true },
                108: { name: 'Number', dataType: 'INT', category: 'Basic Input', htmlInputType: 'number', isNumeric: true },
                109: { name: 'Auto Number', dataType: 'INT', category: 'Basic Input', htmlInputType: 'number', isNumeric: true },
                110: { name: 'Currency', dataType: 'DECIMAL', category: 'Basic Input', htmlInputType: 'number', isNumeric: true },
                111: { name: 'Decimal', dataType: 'DECIMAL', category: 'Basic Input', htmlInputType: 'number', isNumeric: true },
                112: { name: 'Percent', dataType: 'DECIMAL', category: 'Basic Input', htmlInputType: 'number', isNumeric: true },
                113: { name: 'Long Integer', dataType: 'BIGINT', category: 'Basic Input', htmlInputType: 'number', isNumeric: true },
                114: { name: 'Checkbox', dataType: 'BOOLEAN', category: 'Selection Types', htmlInputType: 'checkbox' },
                115: { name: 'Lookup', dataType: 'BIGINT', category: 'Relationship', htmlInputType: 'select' },
                116: { name: 'Radio Button', dataType: 'VARCHAR', category: 'Selection Types', htmlInputType: 'radio', maxLength: 255, hasOptions: true },
                117: { name: 'File Upload', dataType: 'VARCHAR', category: 'File & Media', htmlInputType: 'file', maxLength: 500 },
                118: { name: 'Image Upload', dataType: 'VARCHAR', category: 'File & Media', htmlInputType: 'file', maxLength: 500 },
                119: { name: 'URL', dataType: 'VARCHAR', category: 'Contact Information', htmlInputType: 'url', maxLength: 500 }
            };
        };
        
        // Entity configurations
        $scope.entityConfigs = {
            'STUDENT': {
                name: 'STUDENT',
                displayName: 'Students',
                description: 'Manage student information and records',
                icon: 'user-graduate'
            },
            'PARENT': {
                name: 'PARENT',
                displayName: 'Parents',
                description: 'Parent and guardian information',
                icon: 'users'
            },
            'STAFF': {
                name: 'STAFF',
                displayName: 'Staff',
                description: 'Staff and faculty management',
                icon: 'chalkboard-teacher'
            },
            'ATTENDANCE': {
                name: 'ATTENDANCE',
                displayName: 'Attendance',
                description: 'Attendance tracking and reports',
                icon: 'calendar-check'
            },
            'GRADE': {
                name: 'GRADE',
                displayName: 'Grades',
                description: 'Student grades and assessments',
                icon: 'graduation-cap'
            },
            'ASSIGNMENT': {
                name: 'ASSIGNMENT',
                displayName: 'Assignments',
                description: 'Assignment and homework management',
                icon: 'tasks'
            },
            'EXAM': {
                name: 'EXAM',
                displayName: 'Exams',
                description: 'Exam scheduling and management',
                icon: 'clipboard-list'
            },
            'HEALTH': {
                name: 'HEALTH',
                displayName: 'Health Records',
                description: 'Student health and medical records',
                icon: 'heartbeat'
            }
        };
        
        // Initialize controller
        $scope.init = function() {
            $scope.currentEntity = $scope.entityConfigs[$scope.entityType] || {
                name: $scope.entityType,
                displayName: $scope.entityType,
                description: 'Entity field customization',
                icon: 'cog'
            };
            
            $scope.loadUIFieldTypes();
            $scope.loadEntityFields();
        };
        
        // Load entity fields
        $scope.loadEntityFields = function() {
            ApiService.get('/api/fields/' + $scope.entityType + '/all').then(function(response) {
                $scope.allFields = response.data || [];
                $scope.categorizeFields();
            }).catch(function(error) {
                console.error('Error loading fields:', error);
                NotificationService.error('Failed to load entity fields');
                $scope.allFields = [];
                $scope.categorizeFields();
            });
        };
        
        // Categorize fields into core and custom
        $scope.categorizeFields = function() {
            $scope.coreFields = $scope.allFields.filter(function(field) {
                return field.isSystem || field.isCore;
            });
            
            $scope.customFields = $scope.allFields.filter(function(field) {
                return !field.isSystem && !field.isCore;
            });
            
            // Sort fields by display order
            $scope.coreFields.sort(function(a, b) {
                return (a.displayOrder || 0) - (b.displayOrder || 0);
            });
            
            $scope.customFields.sort(function(a, b) {
                return (a.displayOrder || 0) - (b.displayOrder || 0);
            });
        };
        
        // Select field for editing
        $scope.selectField = function(field) {
            // Save any pending changes first
            if ($scope.selectedField && $scope.selectedField.isModified && !$scope.selectedField.isSaved) {
                var confirmSave = confirm('You have unsaved changes. Do you want to save them before switching fields?');
                if (confirmSave) {
                    $scope.saveField();
                }
            }
            
            // Clone field to avoid direct modification
            $scope.selectedField = angular.copy(field);
            $scope.originalField = angular.copy(field);
            
            // Initialize picklist options if needed
            if ($scope.isPicklistType($scope.selectedField.uiType) && !$scope.selectedField.picklistOptions) {
                $scope.selectedField.picklistOptions = [];
            }
        };
        
        // Get UI type name
        $scope.getUITypeName = function(uiType) {
            return $scope.uiTypes[uiType] ? $scope.uiTypes[uiType].name : 'Unknown Type';
        };
        
        // Check if UI type configuration should be shown
        $scope.showUITypeConfig = function() {
            return $scope.selectedField && $scope.selectedField.uiType;
        };
        
        // Check if field type is text-based
        $scope.isTextType = function(uiType) {
            return [100, 101, 102, 107, 108, 109].includes(parseInt(uiType));
        };
        
        // Check if field type is picklist-based
        $scope.isPicklistType = function(uiType) {
            return [113, 114, 115].includes(parseInt(uiType));
        };
        
        // Check if field type is number-based
        $scope.isNumberType = function(uiType) {
            return [103, 104, 105, 106].includes(parseInt(uiType));
        };
        
        // Handle UI type change
        $scope.onUITypeChange = function() {
            if ($scope.selectedField.uiType) {
                var uiTypeConfig = $scope.uiTypes[$scope.selectedField.uiType];
                if (uiTypeConfig) {
                    // Auto-set data type based on UI type
                    $scope.selectedField.dataType = uiTypeConfig.dataType;
                    
                    // Initialize type-specific configurations
                    if ($scope.isPicklistType($scope.selectedField.uiType)) {
                        if (!$scope.selectedField.picklistOptions) {
                            $scope.selectedField.picklistOptions = [];
                        }
                    }
                    
                    // Set defaults for number types
                    if ($scope.isNumberType($scope.selectedField.uiType)) {
                        if ($scope.selectedField.uiType === 104 || $scope.selectedField.uiType === 105) {
                            $scope.selectedField.decimalPlaces = $scope.selectedField.decimalPlaces || 2;
                        }
                    }
                }
                
                $scope.markFieldAsModified();
            }
        };
        
        // Add new field
        $scope.addNewField = function() {
            var newField = {
                id: null,
                fieldName: '',
                displayName: '',
                description: '',
                entityType: $scope.entityType,
                uiType: null,
                dataType: 'VARCHAR',
                isRequired: false,
                isUnique: false,
                isSystem: false,
                isCore: false,
                showInList: true,
                showInForm: true,
                isSearchable: true,
                displayOrder: $scope.getNextDisplayOrder(),
                columnWidth: 'medium',
                picklistOptions: [],
                isModified: true,
                isNew: true
            };
            
            $scope.selectedField = newField;
            $scope.originalField = {};
        };
        
        // Get next display order
        $scope.getNextDisplayOrder = function() {
            var maxOrder = 0;
            $scope.allFields.forEach(function(field) {
                if (field.displayOrder > maxOrder) {
                    maxOrder = field.displayOrder;
                }
            });
            return maxOrder + 10;
        };
        
        // Add picklist option
        $scope.addPicklistOption = function() {
            if (!$scope.selectedField.picklistOptions) {
                $scope.selectedField.picklistOptions = [];
            }
            $scope.selectedField.picklistOptions.push({
                label: '',
                value: ''
            });
            $scope.markFieldAsModified();
        };
        
        // Remove picklist option
        $scope.removePicklistOption = function(index) {
            if ($scope.selectedField.picklistOptions) {
                $scope.selectedField.picklistOptions.splice(index, 1);
                $scope.markFieldAsModified();
            }
        };
        
        // Mark field as modified
        $scope.markFieldAsModified = function() {
            if ($scope.selectedField) {
                $scope.selectedField.isModified = true;
            }
        };
        
        // Watch for field changes
        $scope.$watch('selectedField', function(newVal, oldVal) {
            if (newVal && oldVal && newVal !== oldVal) {
                $scope.markFieldAsModified();
            }
        }, true);
        
        // Save field
        $scope.saveField = function() {
            if (!$scope.selectedField) return;
            
            // Validate required fields
            if (!$scope.selectedField.fieldName || !$scope.selectedField.displayName || !$scope.selectedField.uiType) {
                NotificationService.error('Please fill in all required fields');
                return;
            }
            
            // Prepare field data for save
            var fieldData = angular.copy($scope.selectedField);
            delete fieldData.isModified;
            delete fieldData.isNew;
            delete fieldData.isSaved;
            
            var savePromise;
            if ($scope.selectedField.id) {
                // Update existing field
                savePromise = ApiService.put('/api/fields/' + $scope.selectedField.id, fieldData);
            } else {
                // Create new field
                savePromise = ApiService.post('/api/fields', fieldData);
            }
            
            savePromise.then(function(response) {
                NotificationService.success('Field saved successfully');
                $scope.selectedField.isModified = false;
                $scope.selectedField.isSaved = true;
                
                // Update field in lists
                if (!$scope.selectedField.id) {
                    $scope.selectedField.id = response.data.id;
                    if ($scope.selectedField.isSystem || $scope.selectedField.isCore) {
                        $scope.coreFields.push(angular.copy($scope.selectedField));
                    } else {
                        $scope.customFields.push(angular.copy($scope.selectedField));
                    }
                } else {
                    $scope.updateFieldInLists($scope.selectedField);
                }
                
                $scope.loadEntityFields(); // Refresh the field list
            }).catch(function(error) {
                console.error('Error saving field:', error);
                NotificationService.error('Failed to save field: ' + (error.data?.message || 'Unknown error'));
            });
        };
        
        // Update field in lists
        $scope.updateFieldInLists = function(updatedField) {
            var updateInArray = function(array) {
                var index = array.findIndex(function(f) { return f.id === updatedField.id; });
                if (index !== -1) {
                    array[index] = angular.copy(updatedField);
                    return true;
                }
                return false;
            };
            
            if (!updateInArray($scope.coreFields)) {
                updateInArray($scope.customFields);
            }
        };
        
        // Reset field changes
        $scope.resetField = function() {
            if ($scope.originalField && Object.keys($scope.originalField).length > 0) {
                $scope.selectedField = angular.copy($scope.originalField);
            } else {
                $scope.selectedField = null;
            }
        };
        
        // Delete field
        $scope.deleteField = function(field, event) {
            if (event) {
                event.stopPropagation();
            }
            
            if (field.isSystem) {
                NotificationService.error('Cannot delete system fields');
                return;
            }
            
            var confirmDelete = confirm('Are you sure you want to delete the field "' + field.displayName + '"? This action cannot be undone.');
            if (!confirmDelete) return;
            
            if (field.id) {
                ApiService.delete('/api/fields/' + field.id).then(function() {
                    NotificationService.success('Field deleted successfully');
                    
                    // Remove from lists
                    $scope.coreFields = $scope.coreFields.filter(function(f) { return f.id !== field.id; });
                    $scope.customFields = $scope.customFields.filter(function(f) { return f.id !== field.id; });
                    
                    // Clear selection if deleted field was selected
                    if ($scope.selectedField && $scope.selectedField.id === field.id) {
                        $scope.selectedField = null;
                    }
                    
                    $scope.loadEntityFields(); // Refresh the field list
                }).catch(function(error) {
                    console.error('Error deleting field:', error);
                    NotificationService.error('Failed to delete field: ' + (error.data?.message || 'Unknown error'));
                });
            } else {
                // Remove from custom fields if it's a new field
                $scope.customFields = $scope.customFields.filter(function(f) { return f !== field; });
                if ($scope.selectedField === field) {
                    $scope.selectedField = null;
                }
            }
        };
        
        // Save all changes
        $scope.saveAllChanges = function() {
            var modifiedFields = $scope.allFields.filter(function(field) {
                return field.isModified && !field.isSaved;
            });
            
            if (modifiedFields.length === 0) {
                NotificationService.info('No changes to save');
                return;
            }
            
            var savePromises = modifiedFields.map(function(field) {
                var fieldData = angular.copy(field);
                delete fieldData.isModified;
                delete fieldData.isSaved;
                
                if (field.id) {
                    return ApiService.put('/api/fields/' + field.id, fieldData);
                } else {
                    return ApiService.post('/api/fields', fieldData);
                }
            });
            
            Promise.all(savePromises).then(function() {
                NotificationService.success('All changes saved successfully');
                $scope.loadEntityFields();
            }).catch(function(error) {
                console.error('Error saving changes:', error);
                NotificationService.error('Failed to save all changes');
            });
        };
        
        // Initialize controller
        $scope.init();
    }
]);