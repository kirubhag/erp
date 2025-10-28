// Theme Settings Controller
(function() {
    'use strict';

    angular.module('erpApp')
        .controller('ThemeSettingsController', ThemeSettingsController);

    ThemeSettingsController.$inject = ['$scope', 'ThemeService', 'NotificationService'];

    function ThemeSettingsController($scope, ThemeService, NotificationService) {
        var vm = this;

        // Initialize
        vm.currentTheme = ThemeService.getTheme();
        vm.currentColor = ThemeService.getColor();
        vm.currentFont = ThemeService.getFont();

        vm.availableThemes = ThemeService.getAvailableThemes();
        vm.availableColors = ThemeService.getAvailableColors();
        vm.availableFonts = ThemeService.getAvailableFonts();

        // Methods
        vm.selectTheme = selectTheme;
        vm.selectColor = selectColor;
        vm.selectFont = selectFont;
        vm.resetToDefaults = resetToDefaults;

        /**
         * Select theme (light/dark)
         */
        function selectTheme(theme) {
            if (ThemeService.setTheme(theme)) {
                vm.currentTheme = theme;
                NotificationService.success('Theme Updated', 'Theme changed to ' + theme + ' mode');
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
         * Reset to default settings
         */
        function resetToDefaults() {
            ThemeService.setTheme('light');
            ThemeService.setColor('default');
            ThemeService.setFont('inter');
            
            vm.currentTheme = 'light';
            vm.currentColor = 'default';
            vm.currentFont = 'inter';
            
            NotificationService.success('Reset Complete', 'Theme settings reset to defaults');
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
