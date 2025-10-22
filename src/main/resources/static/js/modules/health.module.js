(function() {
    'use strict';

    /**
     * Health Module Bundle
     * Contains all health-related functionality
     * Loaded lazily when accessing health pages
     */
    
    console.log('Loading health module...');

    // Module-specific constants for health
    angular.module('erpApp').constant('HEALTH_CONSTANTS', {
        RECORD_TYPES: {
            VACCINATION: 'vaccination',
            MEDICAL_CHECKUP: 'medical_checkup',
            ILLNESS: 'illness',
            INJURY: 'injury',
            ALLERGY: 'allergy',
            MEDICATION: 'medication',
            PHYSICAL_EXAM: 'physical_exam'
        },
        VACCINATION_TYPES: [
            'BCG',
            'Hepatitis B',
            'DPT',
            'Polio',
            'MMR',
            'Varicella',
            'Meningococcal',
            'HPV',
            'Influenza',
            'COVID-19'
        ],
        SEVERITY_LEVELS: {
            LOW: 'low',
            MEDIUM: 'medium',
            HIGH: 'high',
            CRITICAL: 'critical'
        },
        ALLERGY_TYPES: [
            'Food Allergy',
            'Drug Allergy',
            'Environmental Allergy',
            'Skin Allergy',
            'Respiratory Allergy'
        ],
        BODY_SYSTEMS: [
            'Cardiovascular',
            'Respiratory',
            'Digestive',
            'Nervous',
            'Musculoskeletal',
            'Endocrine',
            'Immune',
            'Reproductive',
            'Urinary'
        ]
    });

    console.log('Health module loaded successfully');
})();