// Entity Field Layout Controller - Lightning Design System inspired field layout management
angular.module('erpApp').controller('EntityFieldLayoutController', [
    '$scope', '$routeParams', '$location',
    function($scope, $routeParams, $location) {
        
        // Initialize scope variables
        $scope.entityType = $routeParams.entityType;
        $scope.currentEntity = {};
        $scope.selectedField = null;
        $scope.showStandard = true;
        $scope.customFieldsLeft = 291;
        
        // Field types for sidebar
        $scope.basicFieldTypes = [
            { id: 'single-line', label: 'Single Line', icon: 'fas fa-text-width', uiType: 100 },
            { id: 'multi-line', label: 'Multi-Line', icon: 'fas fa-align-left', uiType: 101 },
            { id: 'email', label: 'Email', icon: 'fas fa-envelope', uiType: 108 },
            { id: 'phone', label: 'Phone', icon: 'fas fa-phone', uiType: 109 },
            { id: 'picklist', label: 'Pick List', icon: 'fas fa-list', uiType: 110 },
            { id: 'multi-select', label: 'Multi-Select', icon: 'fas fa-list-ul', uiType: 111 },
            { id: 'date', label: 'Date', icon: 'fas fa-calendar-alt', uiType: 105 },
            { id: 'datetime', label: 'Date/Time', icon: 'fas fa-clock', uiType: 106 },
            { id: 'checkbox', label: 'Checkbox', icon: 'fas fa-check-square', uiType: 112 },
            { id: 'number', label: 'Number', icon: 'fas fa-hashtag', uiType: 102 }
        ];
        
        $scope.advancedFieldTypes = [
            { id: 'currency', label: 'Currency', icon: 'fas fa-dollar-sign', uiType: 103 },
            { id: 'percent', label: 'Percent', icon: 'fas fa-percentage', uiType: 104 },
            { id: 'textarea', label: 'Text Area', icon: 'fas fa-file-alt', uiType: 113 },
            { id: 'file', label: 'File Upload', icon: 'fas fa-file-upload', uiType: 114 },
            { id: 'image', label: 'Image', icon: 'fas fa-image', uiType: 115 },
            { id: 'url', label: 'URL', icon: 'fas fa-link', uiType: 119 },
            { id: 'formula', label: 'Formula', icon: 'fas fa-calculator', uiType: 117 },
            { id: 'auto-number', label: 'Auto Number', icon: 'fas fa-code', uiType: 118 }
        ];
        
        $scope.relationshipFieldTypes = [
            { id: 'lookup', label: 'Lookup', icon: 'fas fa-user', uiType: 116 },
            { id: 'master-detail', label: 'Master-Detail', icon: 'fas fa-link', uiType: 120 },
            { id: 'roll-up', label: 'Roll-Up Summary', icon: 'fas fa-chart-line', uiType: 121 }
        ];
        
        // Field types dropdown options
        $scope.fieldTypes = [
            { value: 100, label: 'Single Line Text' },
            { value: 101, label: 'Multi Line Text' },
            { value: 102, label: 'Number' },
            { value: 103, label: 'Currency' },
            { value: 104, label: 'Percent' },
            { value: 105, label: 'Date' },
            { value: 106, label: 'Date/Time' },
            { value: 107, label: 'Time' },
            { value: 108, label: 'Email' },
            { value: 109, label: 'Phone' },
            { value: 110, label: 'Picklist' },
            { value: 111, label: 'Multi-Select Picklist' },
            { value: 112, label: 'Checkbox' },
            { value: 113, label: 'Text Area' },
            { value: 114, label: 'File Upload' },
            { value: 115, label: 'Image' },
            { value: 116, label: 'Lookup' },
            { value: 117, label: 'Formula' },
            { value: 118, label: 'Auto Number' },
            { value: 119, label: 'URL' }
        ];
        
        // Default layout sections based on entity type
        $scope.layoutSections = [
            {
                id: 'basic-info',
                title: 'Basic Info',
                isRequired: true,
                allowNewRows: true,
                fields: [
                    {
                        columns: [
                            {
                                fieldName: 'firstName',
                                fieldLabel: 'First Name',
                                displayLabel: 'First Name',
                                fieldType: 'Single Line',
                                uiType: 100,
                                isRequired: true,
                                isUnique: false
                            },
                            {
                                fieldName: 'lastName',
                                fieldLabel: 'Last Name', 
                                displayLabel: 'Last Name',
                                fieldType: 'Single Line',
                                uiType: 100,
                                isRequired: true,
                                isUnique: false
                            }
                        ]
                    },
                    {
                        columns: [
                            {
                                fieldName: 'email',
                                fieldLabel: 'Secondary Email',
                                displayLabel: 'Email',
                                fieldType: 'Email',
                                uiType: 108,
                                isRequired: false,
                                isUnique: true
                            },
                            {
                                fieldName: 'phone',
                                fieldLabel: 'Phone',
                                displayLabel: 'Phone',
                                fieldType: 'Phone',
                                uiType: 109,
                                isRequired: false,
                                isUnique: false
                            }
                        ]
                    },
                    {
                        columns: [
                            {
                                fieldName: 'website',
                                fieldLabel: 'Website',
                                displayLabel: 'Website',
                                fieldType: 'URL',
                                uiType: 119,
                                isRequired: false,
                                isUnique: false
                            },
                            null // Empty column
                        ]
                    }
                ]
            }
        ];
        
        // Initialize controller
        $scope.init = function() {
            $scope.loadEntityInfo();
            $scope.loadFieldLayout();
        };
        
        // Load entity information
        $scope.loadEntityInfo = function() {
            // Map entity types to display information
            var entityMap = {
                'students': {
                    displayName: 'Students',
                    icon: 'user-graduate',
                    description: 'Student information and academic records'
                },
                'staff': {
                    displayName: 'Staff',
                    icon: 'chalkboard-teacher',
                    description: 'Staff member profiles and employment details'
                },
                'parents': {
                    displayName: 'Parents',
                    icon: 'users',
                    description: 'Parent and guardian information'
                },
                'courses': {
                    displayName: 'Courses',
                    icon: 'book-open',
                    description: 'Course catalog and curriculum'
                },
                'attendance': {
                    displayName: 'Attendance',
                    icon: 'calendar-check',
                    description: 'Attendance tracking and records'
                }
            };
            
            $scope.currentEntity = entityMap[$scope.entityType] || {
                displayName: $scope.entityType,
                icon: 'database',
                description: 'Entity information'
            };
            
            // Load entity-specific layout sections
            $scope.loadEntitySpecificSections();
        };
        
        // Load entity-specific layout sections
        $scope.loadEntitySpecificSections = function() {
            if ($scope.entityType === 'students') {
                $scope.layoutSections = [
                    {
                        id: 'basic-info',
                        title: 'Basic Info',
                        isRequired: true,
                        allowNewRows: true,
                        fields: [
                            {
                                columns: [
                                    {
                                        fieldName: 'studentId',
                                        fieldLabel: 'Student ID',
                                        displayLabel: 'Student ID',
                                        fieldType: 'Auto-Number',
                                        uiType: 118,
                                        isRequired: true,
                                        isUnique: true
                                    },
                                    null
                                ]
                            },
                            {
                                columns: [
                                    {
                                        fieldName: 'firstName',
                                        fieldLabel: 'First Name',
                                        displayLabel: 'Mr.',
                                        fieldType: 'Single Line',
                                        uiType: 100,
                                        isRequired: true,
                                        isUnique: false
                                    },
                                    {
                                        fieldName: 'lastName',
                                        fieldLabel: 'Last Name',
                                        displayLabel: 'Last Name',
                                        fieldType: 'Single Line',
                                        uiType: 100,
                                        isRequired: true,
                                        isUnique: false
                                    }
                                ]
                            },
                            {
                                columns: [
                                    {
                                        fieldName: 'email',
                                        fieldLabel: 'Email',
                                        displayLabel: 'Email (Unique)',
                                        fieldType: 'Email',
                                        uiType: 108,
                                        isRequired: false,
                                        isUnique: true
                                    },
                                    {
                                        fieldName: 'phone',
                                        fieldLabel: 'Phone',
                                        displayLabel: 'Phone',
                                        fieldType: 'Phone',
                                        uiType: 109,
                                        isRequired: false,
                                        isUnique: false
                                    }
                                ]
                            },
                            {
                                columns: [
                                    {
                                        fieldName: 'website',
                                        fieldLabel: 'Website',
                                        displayLabel: 'Website',
                                        fieldType: 'URL',
                                        uiType: 119,
                                        isRequired: false,
                                        isUnique: false
                                    },
                                    null
                                ]
                            }
                        ]
                    },
                    {
                        id: 'address-info',
                        title: 'Address Information',
                        isRequired: false,
                        allowNewRows: true,
                        fields: [
                            {
                                columns: [
                                    {
                                        fieldName: 'street',
                                        fieldLabel: 'Street',
                                        displayLabel: 'Street',
                                        fieldType: 'Single Line',
                                        uiType: 100,
                                        isRequired: false,
                                        isUnique: false
                                    },
                                    {
                                        fieldName: 'postalCode',
                                        fieldLabel: 'Postal Code',
                                        displayLabel: 'Postal Code',
                                        fieldType: 'Single Line',
                                        uiType: 100,
                                        isRequired: false,
                                        isUnique: false
                                    }
                                ]
                            },
                            {
                                columns: [
                                    {
                                        fieldName: 'city',
                                        fieldLabel: 'City',
                                        displayLabel: 'City',
                                        fieldType: 'Single Line',
                                        uiType: 100,
                                        isRequired: false,
                                        isUnique: false
                                    },
                                    {
                                        fieldName: 'province',
                                        fieldLabel: 'Province',
                                        displayLabel: 'Province',
                                        fieldType: 'Single Line',
                                        uiType: 100,
                                        isRequired: false,
                                        isUnique: false
                                    }
                                ]
                            },
                            {
                                columns: [
                                    {
                                        fieldName: 'country',
                                        fieldLabel: 'Country',
                                        displayLabel: 'Country',
                                        fieldType: 'Single Line',
                                        uiType: 100,
                                        isRequired: false,
                                        isUnique: false
                                    },
                                    null
                                ]
                            }
                        ]
                    },
                    {
                        id: 'professional-details',
                        title: 'Professional Details',
                        isRequired: false,
                        allowNewRows: true,
                        fields: [
                            {
                                columns: [
                                    {
                                        fieldName: 'experienceYears',
                                        fieldLabel: 'Experience in Years',
                                        displayLabel: 'Experience in Years',
                                        fieldType: 'Decimal',
                                        uiType: 102,
                                        isRequired: false,
                                        isUnique: false
                                    },
                                    {
                                        fieldName: 'qualification',
                                        fieldLabel: 'Highest Qualification Obtained',
                                        displayLabel: 'Highest Qualification Obtained',
                                        fieldType: 'Option 1',
                                        uiType: 110,
                                        isRequired: false,
                                        isUnique: false
                                    }
                                ]
                            },
                            {
                                columns: [
                                    {
                                        fieldName: 'currentJobTitle',
                                        fieldLabel: 'Current Job Title',
                                        displayLabel: 'Current Job Title',
                                        fieldType: 'Option 1',
                                        uiType: 110,
                                        isRequired: false,
                                        isUnique: false
                                    },
                                    {
                                        fieldName: 'currentEmployer',
                                        fieldLabel: 'Current Employer',
                                        displayLabel: 'Current Employer',
                                        fieldType: 'Single Line',
                                        uiType: 100,
                                        isRequired: false,
                                        isUnique: false
                                    }
                                ]
                            },
                            {
                                columns: [
                                    {
                                        fieldName: 'expectedSalary',
                                        fieldLabel: 'Expected Salary',
                                        displayLabel: 'Expected Salary',
                                        fieldType: 'Currency',
                                        uiType: 103,
                                        isRequired: false,
                                        isUnique: false
                                    },
                                    {
                                        fieldName: 'currentSalary',
                                        fieldLabel: 'Current Salary',
                                        displayLabel: 'Current Salary',
                                        fieldType: 'Currency',
                                        uiType: 103,
                                        isRequired: false,
                                        isUnique: false
                                    }
                                ]
                            }
                        ]
                    }
                ];
            }
        };
        
        // Load field layout
        $scope.loadFieldLayout = function() {
            // In a real implementation, this would load from the backend
            console.log('Loading field layout for entity:', $scope.entityType);
        };
        
        // Add field to layout
        $scope.addFieldToLayout = function(fieldType) {
            $scope.selectedField = {
                fieldName: '',
                fieldLabel: '',
                displayLabel: '',
                fieldType: fieldType.label,
                uiType: fieldType.uiType,
                isRequired: false,
                isUnique: false,
                helpText: '',
                maxLength: fieldType.uiType === 100 ? 255 : null
            };
            $scope.showFieldModal();
        };
        
        // Edit existing field
        $scope.editField = function(field) {
            $scope.selectedField = angular.copy(field);
            $scope.showFieldModal();
        };
        
        // Show field configuration modal
        $scope.showFieldModal = function() {
            $('#fieldConfigModal').modal('show');
        };
        
        // Close field modal
        $scope.closeFieldModal = function() {
            $('#fieldConfigModal').modal('hide');
            $scope.selectedField = null;
        };
        
        // Save field configuration
        $scope.saveField = function() {
            if (!$scope.selectedField.fieldLabel || !$scope.selectedField.fieldName) {
                alert('Please fill in required fields');
                return;
            }
            
            // Generate field name from label if not provided
            if (!$scope.selectedField.fieldName) {
                $scope.selectedField.fieldName = $scope.selectedField.fieldLabel
                    .toLowerCase()
                    .replace(/[^a-z0-9]/g, '_')
                    .replace(/_{2,}/g, '_')
                    .replace(/^_|_$/g, '');
            }
            
            console.log('Saving field:', $scope.selectedField);
            
            // In a real implementation, save to backend
            alert('Field saved successfully');
            $scope.closeFieldModal();
        };
        
        // Add new section
        $scope.addNewSection = function() {
            var sectionName = prompt('Enter section name:');
            if (sectionName) {
                var newSection = {
                    id: 'section-' + Date.now(),
                    title: sectionName,
                    isRequired: false,
                    allowNewRows: true,
                    fields: []
                };
                $scope.layoutSections.push(newSection);
            }
        };
        
        // Edit section
        $scope.editSection = function(section) {
            var newName = prompt('Enter new section name:', section.title);
            if (newName && newName !== section.title) {
                section.title = newName;
            }
        };
        
        // Delete section
        $scope.deleteSection = function(section) {
            if (confirm('Are you sure you want to delete this section?')) {
                var index = $scope.layoutSections.indexOf(section);
                if (index > -1) {
                    $scope.layoutSections.splice(index, 1);
                }
            }
        };
        
        // Add row to section
        $scope.addRowToSection = function(section) {
            section.fields.push({
                columns: [null, null] // Two empty columns
            });
        };
        
        // Create new field
        $scope.createNewField = function() {
            $scope.selectedField = {
                fieldName: '',
                fieldLabel: '',
                displayLabel: '',
                fieldType: 'Single Line',
                uiType: 100,
                isRequired: false,
                isUnique: false,
                helpText: '',
                maxLength: 255
            };
            $scope.showFieldModal();
        };
        
        // Get field type name
        $scope.getFieldTypeName = function(uiType) {
            var fieldType = $scope.fieldTypes.find(function(type) {
                return type.value === uiType;
            });
            return fieldType ? fieldType.label : 'Unknown';
        };
        
        // Initialize controller
        $scope.init();
    }
]);