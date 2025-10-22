(function() {
    'use strict';

    /**
     * Parents Module Bundle
     * Contains all parent-related functionality
     * Loaded lazily when accessing parent pages
     */
    
    console.log('Loading parents module...');

    // Module-specific constants for parents
    angular.module('erpApp').constant('PARENT_CONSTANTS', {
        RELATIONSHIP: {
            MOTHER: 'Mother',
            FATHER: 'Father',
            GUARDIAN: 'Guardian',
            GRANDMOTHER: 'Grandmother',
            GRANDFATHER: 'Grandfather',
            AUNT: 'Aunt',
            UNCLE: 'Uncle',
            OTHER: 'Other'
        },
        OCCUPATION_TYPES: [
            'Business',
            'Government Service',
            'Private Service',
            'Professional',
            'Agriculture',
            'Homemaker',
            'Retired',
            'Other'
        ],
        CONTACT_PREFERENCES: {
            EMAIL: 'email',
            SMS: 'sms',
            PHONE: 'phone',
            WHATSAPP: 'whatsapp'
        }
    });

    console.log('Parents module loaded successfully');
})();