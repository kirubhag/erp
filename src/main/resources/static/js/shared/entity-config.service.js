// Entity Configuration Service - Manages configurations for different entity types
(function() {
    'use strict';

    angular.module('erpApp')
        .factory('EntityConfigService', EntityConfigService);

    EntityConfigService.$inject = [];

    function EntityConfigService() {

        var entityConfigs = {};

        // Define configurations for each entity type
        initializeConfigs();

        var service = {
            getConfig: getConfig,
            registerConfig: registerConfig,
            updateConfig: updateConfig
        };

        return service;

        /**
         * Initialize default configurations for all entities
         */
        function initializeConfigs() {
            // Student Configuration
            entityConfigs['STUDENT'] = {
                entityType: 'STUDENT',
                entityName: 'Student',
                entityDisplayName: 'Student',
                title: 'Students',
                subtitle: 'Manage student profiles, enrollment, and academic information',
                createButtonText: 'Add Student',
                emptyIcon: 'user-graduate',
                emptyMessage: 'Get started by adding your first student to the system',
                allowCreate: true,
                
                // URL configurations for CRUD operations
                createUrl: '/students/new',
                viewUrl: '/students/:id',
                editUrl: '/students/:id/edit',
                listUrl: '/students',
                
                features: {
                    customViews: true,
                    import: true,
                    export: true,
                    sourceBooters: false,
                    bulkActions: true
                },

                defaultColumns: [
                    {
                        field: 'name',
                        label: 'Student Name',
                        primary: true,
                        showAvatar: true,
                        secondary: 'studentId',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'studentId',
                        label: 'Student ID',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'email',
                        label: 'Email',
                        sortable: true,
                        type: 'email'
                    },
                    {
                        field: 'phone',
                        label: 'Phone',
                        sortable: false,
                        type: 'phone'
                    },
                    {
                        field: 'gradeLevel',
                        label: 'Grade',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'enrollmentStatus',
                        label: 'Status',
                        sortable: true,
                        type: 'status'
                    },
                    {
                        field: 'enrollmentDate',
                        label: 'Enrollment Date',
                        sortable: true,
                        type: 'date'
                    }
                ],

                filters: [
                    {
                        name: 'Grade Level',
                        field: 'gradeLevel',
                        expanded: true,
                        options: [
                            { value: 'KINDERGARTEN', label: 'Kindergarten', selected: false },
                            { value: 'FIRST_GRADE', label: '1st Grade', selected: false },
                            { value: 'SECOND_GRADE', label: '2nd Grade', selected: false },
                            { value: 'THIRD_GRADE', label: '3rd Grade', selected: false },
                            { value: 'FOURTH_GRADE', label: '4th Grade', selected: false },
                            { value: 'FIFTH_GRADE', label: '5th Grade', selected: false }
                        ]
                    },
                    {
                        name: 'Status',
                        field: 'enrollmentStatus',
                        expanded: true,
                        options: [
                            { value: 'ACTIVE', label: 'Active', selected: false },
                            { value: 'INACTIVE', label: 'Inactive', selected: false },
                            { value: 'PENDING', label: 'Pending', selected: false },
                            { value: 'GRADUATED', label: 'Graduated', selected: false }
                        ]
                    },
                    {
                        name: 'Gender',
                        field: 'gender',
                        expanded: false,
                        options: [
                            { value: 'MALE', label: 'Male', selected: false },
                            { value: 'FEMALE', label: 'Female', selected: false },
                            { value: 'OTHER', label: 'Other', selected: false }
                        ]
                    }
                ],

                rowActions: [
                    {
                        name: 'view',
                        icon: 'eye',
                        label: 'View Details',
                        visible: true
                    },
                    {
                        name: 'edit',
                        icon: 'edit',
                        label: 'Edit Student',
                        visible: true
                    },
                    {
                        name: 'delete',
                        icon: 'trash',
                        label: 'Delete Student',
                        visible: true
                    }
                ],

                bulkActions: [
                    {
                        name: 'updateStatus',
                        icon: 'edit',
                        label: 'Update Status'
                    },
                    {
                        name: 'export',
                        icon: 'download',
                        label: 'Export Selected'
                    },
                    {
                        name: 'delete',
                        icon: 'trash',
                        label: 'Delete Selected'
                    }
                ],

                gridConfig: {
                    primaryField: 'firstName',
                    secondaryField: 'studentId',
                    fields: [
                        { field: 'email', label: 'Email' },
                        { field: 'gradeLevel', label: 'Grade' },
                        { field: 'enrollmentStatus', label: 'Status' }
                    ]
                }
            };

            // Parent Configuration
            entityConfigs['PARENT'] = {
                entityType: 'PARENT',
                entityName: 'Parent',
                title: 'Parents & Guardians',
                subtitle: 'Manage parent and guardian information and relationships',
                createButtonText: 'Add Parent',
                emptyIcon: 'users',
                emptyMessage: 'Add parent and guardian information to connect with students',
                allowCreate: true,
                
                features: {
                    customViews: true,
                    import: true,
                    export: true,
                    sourceBooters: false,
                    bulkActions: true
                },

                defaultColumns: [
                    {
                        field: 'name',
                        label: 'Parent Name',
                        primary: true,
                        showAvatar: true,
                        secondary: 'relationshipType',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'email',
                        label: 'Email',
                        sortable: true,
                        type: 'email'
                    },
                    {
                        field: 'phone',
                        label: 'Phone',
                        sortable: false,
                        type: 'phone'
                    },
                    {
                        field: 'relationshipType',
                        label: 'Relationship',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'occupation',
                        label: 'Occupation',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'createdTime',
                        label: 'Added Date',
                        sortable: true,
                        type: 'date'
                    }
                ],

                filters: [
                    {
                        name: 'Relationship',
                        field: 'relationshipType',
                        expanded: true,
                        options: [
                            { value: 'FATHER', label: 'Father', selected: false },
                            { value: 'MOTHER', label: 'Mother', selected: false },
                            { value: 'GUARDIAN', label: 'Guardian', selected: false },
                            { value: 'GRANDPARENT', label: 'Grandparent', selected: false },
                            { value: 'OTHER', label: 'Other', selected: false }
                        ]
                    }
                ],

                rowActions: [
                    {
                        name: 'view',
                        icon: 'eye',
                        label: 'View Details',
                        visible: true
                    },
                    {
                        name: 'edit',
                        icon: 'edit',
                        label: 'Edit Parent',
                        visible: true
                    },
                    {
                        name: 'delete',
                        icon: 'trash',
                        label: 'Delete Parent',
                        visible: true
                    }
                ],

                bulkActions: [
                    {
                        name: 'export',
                        icon: 'download',
                        label: 'Export Selected'
                    },
                    {
                        name: 'delete',
                        icon: 'trash',
                        label: 'Delete Selected'
                    }
                ]
            };

            // Staff Configuration
            entityConfigs['STAFF'] = {
                entityType: 'STAFF',
                entityName: 'Staff',
                title: 'Staff Members',
                subtitle: 'Manage teaching and administrative staff information',
                createButtonText: 'Add Staff',
                emptyIcon: 'user-tie',
                emptyMessage: 'Add your first staff member to begin team management',
                allowCreate: true,
                
                features: {
                    customViews: true,
                    import: true,
                    export: true,
                    sourceBooters: false,
                    bulkActions: true
                },

                defaultColumns: [
                    {
                        field: 'name',
                        label: 'Staff Name',
                        primary: true,
                        showAvatar: true,
                        secondary: 'position',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'employeeId',
                        label: 'Employee ID',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'email',
                        label: 'Email',
                        sortable: true,
                        type: 'email'
                    },
                    {
                        field: 'phone',
                        label: 'Phone',
                        sortable: false,
                        type: 'phone'
                    },
                    {
                        field: 'department',
                        label: 'Department',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'position',
                        label: 'Position',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'hireDate',
                        label: 'Hire Date',
                        sortable: true,
                        type: 'date'
                    }
                ],

                filters: [
                    {
                        name: 'Department',
                        field: 'department',
                        expanded: true,
                        options: [
                            { value: 'ACADEMIC', label: 'Academic', selected: false },
                            { value: 'ADMINISTRATION', label: 'Administration', selected: false },
                            { value: 'SUPPORT', label: 'Support', selected: false },
                            { value: 'MAINTENANCE', label: 'Maintenance', selected: false }
                        ]
                    },
                    {
                        name: 'Employment Type',
                        field: 'employmentType',
                        expanded: true,
                        options: [
                            { value: 'FULL_TIME', label: 'Full Time', selected: false },
                            { value: 'PART_TIME', label: 'Part Time', selected: false },
                            { value: 'CONTRACT', label: 'Contract', selected: false }
                        ]
                    }
                ],

                rowActions: [
                    {
                        name: 'view',
                        icon: 'eye',
                        label: 'View Details',
                        visible: true
                    },
                    {
                        name: 'edit',
                        icon: 'edit',
                        label: 'Edit Staff',
                        visible: true
                    },
                    {
                        name: 'delete',
                        icon: 'trash',
                        label: 'Delete Staff',
                        visible: true
                    }
                ],

                bulkActions: [
                    {
                        name: 'updateDepartment',
                        icon: 'edit',
                        label: 'Update Department'
                    },
                    {
                        name: 'export',
                        icon: 'download',
                        label: 'Export Selected'
                    }
                ]
            };

            // Attendance Configuration
            entityConfigs['ATTENDANCE'] = {
                entityType: 'ATTENDANCE',
                entityName: 'Attendance',
                title: 'Attendance Records',
                subtitle: 'Track and manage student attendance records',
                createButtonText: 'Add Record',
                emptyIcon: 'calendar-check',
                emptyMessage: 'No attendance records found. Start tracking attendance.',
                allowCreate: true,
                
                features: {
                    customViews: true,
                    import: false,
                    export: true,
                    sourceBooters: false,
                    bulkActions: true
                },

                defaultColumns: [
                    {
                        field: 'student.name',
                        label: 'Student',
                        primary: true,
                        showAvatar: false,
                        secondary: 'student.studentId',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'date',
                        label: 'Date',
                        sortable: true,
                        type: 'date'
                    },
                    {
                        field: 'status',
                        label: 'Status',
                        sortable: true,
                        type: 'status'
                    },
                    {
                        field: 'checkInTime',
                        label: 'Check In',
                        sortable: true,
                        type: 'time'
                    },
                    {
                        field: 'checkOutTime',
                        label: 'Check Out',
                        sortable: true,
                        type: 'time'
                    },
                    {
                        field: 'notes',
                        label: 'Notes',
                        sortable: false,
                        type: 'text'
                    }
                ],

                filters: [
                    {
                        name: 'Attendance Status',
                        field: 'status',
                        expanded: true,
                        options: [
                            { value: 'PRESENT', label: 'Present', selected: false },
                            { value: 'ABSENT', label: 'Absent', selected: false },
                            { value: 'LATE', label: 'Late', selected: false },
                            { value: 'EXCUSED', label: 'Excused', selected: false }
                        ]
                    }
                ],

                rowActions: [
                    {
                        name: 'view',
                        icon: 'eye',
                        label: 'View Details',
                        visible: true
                    },
                    {
                        name: 'edit',
                        icon: 'edit',
                        label: 'Edit Record',
                        visible: true
                    }
                ],

                bulkActions: [
                    {
                        name: 'updateStatus',
                        icon: 'edit',
                        label: 'Update Status'
                    },
                    {
                        name: 'export',
                        icon: 'download',
                        label: 'Export Selected'
                    }
                ]
            };

            // Health Records Configuration
            entityConfigs['HEALTH'] = {
                entityType: 'HEALTH',
                entityName: 'Health Record',
                title: 'Health Records',
                subtitle: 'Manage student health information and medical records',
                createButtonText: 'Add Health Record',
                emptyIcon: 'heartbeat',
                emptyMessage: 'No health records found. Add medical information for students.',
                allowCreate: true,
                
                features: {
                    customViews: true,
                    import: false,
                    export: true,
                    sourceBooters: false,
                    bulkActions: false
                },

                defaultColumns: [
                    {
                        field: 'student.name',
                        label: 'Student',
                        primary: true,
                        showAvatar: false,
                        secondary: 'recordType',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'recordType',
                        label: 'Record Type',
                        sortable: true,
                        type: 'text'
                    },
                    {
                        field: 'recordDate',
                        label: 'Date',
                        sortable: true,
                        type: 'date'
                    },
                    {
                        field: 'healthStatus',
                        label: 'Status',
                        sortable: true,
                        type: 'status'
                    },
                    {
                        field: 'description',
                        label: 'Description',
                        sortable: false,
                        type: 'text'
                    }
                ],

                filters: [
                    {
                        name: 'Record Type',
                        field: 'recordType',
                        expanded: true,
                        options: [
                            { value: 'VACCINATION', label: 'Vaccination', selected: false },
                            { value: 'MEDICAL_EXAM', label: 'Medical Exam', selected: false },
                            { value: 'ALLERGY', label: 'Allergy', selected: false },
                            { value: 'MEDICATION', label: 'Medication', selected: false },
                            { value: 'INJURY', label: 'Injury', selected: false }
                        ]
                    }
                ],

                rowActions: [
                    {
                        name: 'view',
                        icon: 'eye',
                        label: 'View Details',
                        visible: true
                    },
                    {
                        name: 'edit',
                        icon: 'edit',
                        label: 'Edit Record',
                        visible: true
                    },
                    {
                        name: 'delete',
                        icon: 'trash',
                        label: 'Delete Record',
                        visible: true
                    }
                ]
            };
        }

        /**
         * Get configuration for an entity type
         */
        function getConfig(entityType) {
            if (!entityType) {
                return null;
            }
            
            var config = entityConfigs[entityType.toUpperCase()];
            
            if (!config) {
                return null;
            }
            
            // Return a copy to prevent accidental modifications
            return angular.copy(config);
        }

        /**
         * Register a new entity configuration
         */
        function registerConfig(entityType, config) {
            if (!entityType || !config) {
                return false;
            }
            
            entityConfigs[entityType.toUpperCase()] = config;
            return true;
        }

        /**
         * Update an existing entity configuration
         */
        function updateConfig(entityType, updates) {
            if (!entityType || !updates) {
                return false;
            }
            
            var existingConfig = entityConfigs[entityType.toUpperCase()];
            if (!existingConfig) {
                return false;
            }
            
            // Merge updates with existing config
            angular.extend(existingConfig, updates);
            return true;
        }
    }
})();