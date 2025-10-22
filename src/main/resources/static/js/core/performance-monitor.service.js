(function() {
    'use strict';

    /**
     * Performance Monitor Service
     * Tracks page load times and module loading performance
     */
    angular.module('erpApp').service('PerformanceMonitorService', function() {
        var loadTimes = {};
        var startTimes = {};

        // Record start time for a operation
        this.startTimer = function(operation) {
            startTimes[operation] = performance.now();
        };

        // Record end time and calculate duration
        this.endTimer = function(operation) {
            if (startTimes[operation]) {
                var duration = performance.now() - startTimes[operation];
                loadTimes[operation] = duration;
                return duration;
            }
            return 0;
        };

        // Get all recorded times
        this.getLoadTimes = function() {
            return angular.copy(loadTimes);
        };

        // Get performance summary
        this.getPerformanceSummary = function() {
            var total = 0;
            var operations = Object.keys(loadTimes);
            
            operations.forEach(function(op) {
                total += loadTimes[op];
            });

            return {
                totalTime: total,
                operationCount: operations.length,
                averageTime: total / operations.length,
                operations: loadTimes
            };
        };

        // Monitor page load performance
        this.monitorPageLoad = function() {
            var self = this;
            
            // Monitor DOM ready time
            if (document.readyState === 'loading') {
                self.startTimer('dom-ready');
                document.addEventListener('DOMContentLoaded', function() {
                    self.endTimer('dom-ready');
                });
            }

            // Monitor window load time (all resources)
            if (document.readyState !== 'complete') {
                self.startTimer('window-load');
                window.addEventListener('load', function() {
                    self.endTimer('window-load');
                });
            }
        };

        // Log navigation timing if available
        this.logNavigationTiming = function() {
            if (window.performance && window.performance.timing) {
                var timing = window.performance.timing;
                var loadEventEnd = timing.loadEventEnd;
                var navigationStart = timing.navigationStart;
            }
        };

        // Initialize monitoring
        this.init = function() {
            this.monitorPageLoad();
            
            // Log navigation timing after page load
            var self = this;
            setTimeout(function() {
                self.logNavigationTiming();
            }, 1000);
        };
    });
})();