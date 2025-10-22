// Settings Service - For system settings management
angular.module('erpApp').factory('SettingsService', ['$http', function($http) {
    var baseUrl = '/api/settings';
    
    return {
        /**
         * Get all system settings
         */
        getSettings: function() {
            return $http.get(baseUrl);
        },
        
        /**
         * Update system settings
         */
        updateSettings: function(settings) {
            return $http.put(baseUrl, settings);
        },
        
        /**
         * Get setting by key
         */
        getSetting: function(key) {
            return $http.get(baseUrl + '/' + key);
        },
        
        /**
         * Update specific setting
         */
        updateSetting: function(key, value) {
            return $http.put(baseUrl + '/' + key, { value: value });
        },
        
        /**
         * Get default settings
         */
        getDefaultSettings: function() {
            return {
                language: 'en',
                theme: 'light',
                timezone: 'UTC',
                dateFormat: 'YYYY-MM-DD',
                currency: 'USD',
                notifications: {
                    email: true,
                    sms: false
                },
                customSetting: ''
            };
        },
        
        /**
         * Available options for dropdowns
         */
        getAvailableLanguages: function() {
            return [
                { value: 'en', label: 'English' },
                { value: 'es', label: 'Spanish' },
                { value: 'fr', label: 'French' },
                { value: 'de', label: 'German' },
                { value: 'it', label: 'Italian' },
                { value: 'pt', label: 'Portuguese' }
            ];
        },
        
        getAvailableThemes: function() {
            return [
                { value: 'light', label: 'Light' },
                { value: 'dark', label: 'Dark' },
                { value: 'auto', label: 'Auto (System)' }
            ];
        },
        
        getAvailableTimezones: function() {
            return [
                { value: 'UTC', label: 'UTC' },
                { value: 'America/New_York', label: 'Eastern Time (ET)' },
                { value: 'America/Chicago', label: 'Central Time (CT)' },
                { value: 'America/Denver', label: 'Mountain Time (MT)' },
                { value: 'America/Los_Angeles', label: 'Pacific Time (PT)' },
                { value: 'Europe/London', label: 'London' },
                { value: 'Europe/Paris', label: 'Paris' },
                { value: 'Asia/Tokyo', label: 'Tokyo' },
                { value: 'Asia/Shanghai', label: 'Shanghai' },
                { value: 'Australia/Sydney', label: 'Sydney' }
            ];
        },
        
        getAvailableCurrencies: function() {
            return [
                { value: 'USD', label: 'US Dollar (USD)' },
                { value: 'EUR', label: 'Euro (EUR)' },
                { value: 'GBP', label: 'British Pound (GBP)' },
                { value: 'JPY', label: 'Japanese Yen (JPY)' },
                { value: 'CAD', label: 'Canadian Dollar (CAD)' },
                { value: 'AUD', label: 'Australian Dollar (AUD)' },
                { value: 'CHF', label: 'Swiss Franc (CHF)' },
                { value: 'CNY', label: 'Chinese Yuan (CNY)' },
                { value: 'INR', label: 'Indian Rupee (INR)' }
            ];
        }
    };
}]);