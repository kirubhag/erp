// Theme Service - Manages theme and font preferences
(function() {
    'use strict';

    angular.module('erpApp')
        .factory('ThemeService', ThemeService);

    ThemeService.$inject = ['$window'];

    function ThemeService($window) {
        var currentTheme = 'light';
        var currentColor = 'default';
        var currentFont = 'inter';
        var observers = [];

        // Initialize theme from localStorage
        init();

        var service = {
            setTheme: setTheme,
            getTheme: getTheme,
            setColor: setColor,
            getColor: getColor,
            setFont: setFont,
            getFont: getFont,
            getAvailableThemes: getAvailableThemes,
            getAvailableColors: getAvailableColors,
            getAvailableFonts: getAvailableFonts,
            subscribe: subscribe,
            unsubscribe: unsubscribe
        };

        return service;

        /**
         * Initialize theme from localStorage or defaults
         */
        function init() {
            // Load saved preferences
            currentTheme = localStorage.getItem('app-theme') || 'light';
            currentColor = localStorage.getItem('app-color') || 'default';
            currentFont = localStorage.getItem('app-font') || 'inter';

            // Apply theme
            applyTheme();
        }

        /**
         * Apply theme to document
         */
        function applyTheme() {
            var body = document.body;
            
            // Add no-transition class to prevent flash
            body.classList.add('no-transition');

            // Set theme attribute
            if (currentTheme === 'dark') {
                body.setAttribute('data-theme', 'dark');
            } else {
                body.removeAttribute('data-theme');
            }

            // Set color attribute
            if (currentColor !== 'default') {
                body.setAttribute('data-theme', currentColor);
            }

            // Set font attribute
            body.setAttribute('data-font', currentFont);

            // Remove no-transition class after a short delay
            setTimeout(function() {
                body.classList.remove('no-transition');
            }, 50);

            // Notify observers
            notifyObservers();
        }

        /**
         * Set theme (light/dark)
         */
        function setTheme(theme) {
            if (!theme || (theme !== 'light' && theme !== 'dark')) {
                return false;
            }

            currentTheme = theme;
            localStorage.setItem('app-theme', theme);
            applyTheme();
            return true;
        }

        /**
         * Get current theme
         */
        function getTheme() {
            return currentTheme;
        }

        /**
         * Set color theme
         */
        function setColor(color) {
            var validColors = ['default', 'blue', 'green', 'purple', 'red', 'orange', 'pink', 'indigo', 'teal'];
            
            if (!color || validColors.indexOf(color) === -1) {
                return false;
            }

            currentColor = color;
            localStorage.setItem('app-color', color);
            applyTheme();
            return true;
        }

        /**
         * Get current color theme
         */
        function getColor() {
            return currentColor;
        }

        /**
         * Set font family
         */
        function setFont(font) {
            var validFonts = ['inter', 'poppins', 'roboto', 'system'];
            
            if (!font || validFonts.indexOf(font) === -1) {
                return false;
            }

            currentFont = font;
            localStorage.setItem('app-font', font);
            applyTheme();
            return true;
        }

        /**
         * Get current font
         */
        function getFont() {
            return currentFont;
        }

        /**
         * Get available themes
         */
        function getAvailableThemes() {
            return [
                { value: 'light', label: 'Light', icon: 'sun' },
                { value: 'dark', label: 'Dark', icon: 'moon' }
            ];
        }

        /**
         * Get available color themes
         */
        function getAvailableColors() {
            return [
                { value: 'default', label: 'Default Blue', color: '#3b82f6' },
                { value: 'blue', label: 'Sky Blue', color: '#0ea5e9' },
                { value: 'green', label: 'Emerald Green', color: '#10b981' },
                { value: 'purple', label: 'Purple', color: '#8b5cf6' },
                { value: 'red', label: 'Red', color: '#ef4444' },
                { value: 'orange', label: 'Orange', color: '#f97316' },
                { value: 'pink', label: 'Pink', color: '#ec4899' },
                { value: 'indigo', label: 'Indigo', color: '#6366f1' },
                { value: 'teal', label: 'Teal', color: '#14b8a6' }
            ];
        }

        /**
         * Get available fonts
         */
        function getAvailableFonts() {
            return [
                { value: 'inter', label: 'Inter (Modern)', preview: 'Inter' },
                { value: 'poppins', label: 'Poppins (Friendly)', preview: 'Poppins' },
                { value: 'roboto', label: 'Roboto (Classic)', preview: 'Roboto' },
                { value: 'system', label: 'System Default', preview: 'System' }
            ];
        }

        /**
         * Subscribe to theme changes
         */
        function subscribe(callback) {
            if (typeof callback === 'function') {
                observers.push(callback);
            }
        }

        /**
         * Unsubscribe from theme changes
         */
        function unsubscribe(callback) {
            var index = observers.indexOf(callback);
            if (index > -1) {
                observers.splice(index, 1);
            }
        }

        /**
         * Notify all observers of theme change
         */
        function notifyObservers() {
            var themeData = {
                theme: currentTheme,
                color: currentColor,
                font: currentFont
            };

            observers.forEach(function(callback) {
                try {
                    callback(themeData);
                } catch (e) {
                    console.error('Error in theme observer:', e);
                }
            });
        }
    }
})();
