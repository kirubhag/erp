(function() {
    'use strict';

    /**
     * Attendance Module Bundle
     * Contains all attendance-related functionality
     * Loaded lazily when accessing attendance pages
     */
    
    console.log('Loading attendance module...');

    // Module-specific constants for attendance
    angular.module('erpApp').constant('ATTENDANCE_CONSTANTS', {
        STATUS: {
            PRESENT: 'Present',
            ABSENT: 'Absent',
            LATE: 'Late',
            EXCUSED: 'Excused',
            SICK: 'Sick',
            HOLIDAY: 'Holiday'
        },
        PERIODS: [
            { value: 1, label: 'Period 1' },
            { value: 2, label: 'Period 2' },
            { value: 3, label: 'Period 3' },
            { value: 4, label: 'Period 4' },
            { value: 5, label: 'Period 5' },
            { value: 6, label: 'Period 6' },
            { value: 7, label: 'Period 7' },
            { value: 8, label: 'Period 8' }
        ],
        REPORT_TYPES: {
            DAILY: 'daily',
            WEEKLY: 'weekly',
            MONTHLY: 'monthly',
            CUSTOM: 'custom'
        },
        NOTIFICATION_THRESHOLDS: {
            CONSECUTIVE_ABSENCES: 3,
            MONTHLY_ABSENCE_PERCENTAGE: 20
        }
    });

    console.log('Attendance module loaded successfully');
})();