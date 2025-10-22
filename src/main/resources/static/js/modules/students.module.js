(function() {
    'use strict';

    /**
     * Students Module Bundle
     * Contains all student-related functionality
     * Loaded lazily when accessing student pages
     */
    
    console.log('Loading students module...');

    // Module initialization can go here if needed
    // This file serves as an entry point for the student module
    // and can contain shared configuration or initialization code

    // You can define module-specific constants here
    angular.module('erpApp').constant('STUDENT_CONSTANTS', {
        STATUS: {
            ACTIVE: 'active',
            INACTIVE: 'inactive',
            GRADUATED: 'graduated'
        },
        GRADES: [
            { value: 'KG1', label: 'Kindergarten 1' },
            { value: 'KG2', label: 'Kindergarten 2' },
            { value: '1', label: 'Grade 1' },
            { value: '2', label: 'Grade 2' },
            { value: '3', label: 'Grade 3' },
            { value: '4', label: 'Grade 4' },
            { value: '5', label: 'Grade 5' },
            { value: '6', label: 'Grade 6' },
            { value: '7', label: 'Grade 7' },
            { value: '8', label: 'Grade 8' },
            { value: '9', label: 'Grade 9' },
            { value: '10', label: 'Grade 10' },
            { value: '11', label: 'Grade 11' },
            { value: '12', label: 'Grade 12' }
        ]
    });

    console.log('Students module loaded successfully');
})();