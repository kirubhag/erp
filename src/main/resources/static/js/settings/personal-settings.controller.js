// Personal Settings Controller
(function() {
    'use strict';

    angular.module('erpApp')
        .controller('PersonalSettingsController', PersonalSettingsController);

    PersonalSettingsController.$inject = ['$scope', 'ThemeService', 'NotificationService', '$http'];

    function PersonalSettingsController($scope, ThemeService, NotificationService, $http) {
        var vm = this;

        // User information
        vm.user = {
            name: 'IDC G',
            role: 'Administrator',
            title: 'Recruiter Admin at Zohotest',
            email: 'kirubhakaran.g+idc@zohotest.com',
            country: 'India'
        };

        // Locale settings
        vm.settings = {
            language: 'English (United States)',
            country: 'India',
            dateFormat: 'DD/MM/YYYY',
            timeFormat: '12 Hours',
            timezone: 'India Standard Time'
        };

        // Theme settings
        vm.currentTheme = ThemeService.getTheme();
        vm.currentColor = ThemeService.getColor();
        vm.currentFont = ThemeService.getFont();

        vm.availableColors = ThemeService.getAvailableColors();
        vm.availableFonts = ThemeService.getAvailableFonts();

        // Methods
        vm.editLocale = editLocale;
        vm.selectTheme = selectTheme;
        vm.selectColor = selectColor;
        vm.selectFont = selectFont;
        vm.saveSettings = saveSettings;

        // Load user data
        init();

        /**
         * Initialize controller
         */
        function init() {
            loadUserData();
            loadUserSettings();
        }

        /**
         * Load user data from API
         */
        function loadUserData() {
            // TODO: Replace with actual API call
            // $http.get('/api/users/current').then(function(response) {
            //     vm.user = response.data;
            // });
        }

        /**
         * Load user settings from API
         */
        function loadUserSettings() {
            // TODO: Replace with actual API call
            // $http.get('/api/users/current/settings').then(function(response) {
            //     vm.settings = response.data;
            // });
        }

        /**
         * Edit locale information
         */
        function editLocale() {
            // TODO: Implement locale editing modal
            NotificationService.info('Edit Locale', 'Locale editing feature coming soon');
        }

        /**
         * Select theme (light/dark)
         */
        function selectTheme(theme) {
            if (ThemeService.setTheme(theme)) {
                vm.currentTheme = theme;
                NotificationService.success('Theme Updated', 'Appearance changed to ' + theme + ' mode');
            }
        }

        /**
         * Select color theme
         */
        function selectColor(color) {
            if (ThemeService.setColor(color)) {
                vm.currentColor = color;
                var colorObj = vm.availableColors.find(function(c) { return c.value === color; });
                NotificationService.success('Color Theme Updated', 'Color theme changed to ' + colorObj.label);
            }
        }

        /**
         * Select font
         */
        function selectFont(font) {
            if (ThemeService.setFont(font)) {
                vm.currentFont = font;
                var fontObj = vm.availableFonts.find(function(f) { return f.value === font; });
                NotificationService.success('Font Updated', 'Font changed to ' + fontObj.label);
            }
        }

        /**
         * Save all settings
         */
        function saveSettings() {
            var settingsData = {
                locale: vm.settings,
                theme: {
                    mode: vm.currentTheme,
                    color: vm.currentColor,
                    font: vm.currentFont
                }
            };

            // TODO: Replace with actual API call
            // $http.put('/api/users/current/settings', settingsData).then(function(response) {
            //     NotificationService.success('Settings Saved', 'Your settings have been saved successfully');
            // });

            NotificationService.success('Settings Saved', 'Your settings have been saved successfully');
        }

        /**
         * Subscribe to theme changes
         */
        ThemeService.subscribe(function(themeData) {
            $scope.$apply(function() {
                vm.currentTheme = themeData.theme;
                vm.currentColor = themeData.color;
                vm.currentFont = themeData.font;
            });
        });
    }
})();
